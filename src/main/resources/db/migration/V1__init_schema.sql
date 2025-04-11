-- noinspection SqlResolve
CREATE TABLE users (
                       user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       phone_number VARCHAR(15) UNIQUE NOT NULL,
                       email VARCHAR(255) UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       full_name VARCHAR(100) NOT NULL,
                       avatar_url TEXT,
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                       status VARCHAR(20) DEFAULT 'active'
);


CREATE TABLE customers (
                           customer_id UUID PRIMARY KEY REFERENCES users(user_id),
                           default_payment_method VARCHAR(50),
                           rating DECIMAL(3,2) DEFAULT 5.0,
                           total_trips INTEGER DEFAULT 0
);


CREATE TABLE drivers (
                         driver_id UUID PRIMARY KEY REFERENCES users(user_id),
                         license_number VARCHAR(50) UNIQUE NOT NULL,
                         vehicle_type VARCHAR(20) NOT NULL, -- motorcycle, car_4, car_7
                         vehicle_brand VARCHAR(50),
                         vehicle_model VARCHAR(50),
                         vehicle_plate VARCHAR(20) UNIQUE NOT NULL,
                         rating DECIMAL(3,2) DEFAULT 5.0,
                         total_trips INTEGER DEFAULT 0,
                         current_status VARCHAR(20) DEFAULT 'offline', -- offline, online, on_trip
                         documents_verified BOOLEAN DEFAULT false
);


-- Kích hoạt PostGIS extension
CREATE EXTENSION IF NOT EXISTS postgis;


CREATE TABLE location_history (
                                  id BIGSERIAL PRIMARY KEY,
                                  user_id UUID NOT NULL REFERENCES users(user_id),
                                  location GEOGRAPHY(POINT) NOT NULL,
                                  timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                  accuracy FLOAT,
                                  speed FLOAT,
                                  bearing FLOAT
);


CREATE TABLE driver_locations (
                                  driver_id UUID PRIMARY KEY REFERENCES drivers(driver_id),
                                  current_location GEOGRAPHY(POINT) NOT NULL,
                                  last_updated TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                  is_available BOOLEAN DEFAULT true
);


CREATE INDEX idx_driver_locations_geom ON driver_locations USING GIST (current_location);
CREATE INDEX idx_location_history_geom ON location_history USING GIST (location);

-- noinspection SqlResolve
CREATE TABLE trips (
                       trip_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       customer_id UUID NOT NULL REFERENCES customers(customer_id),
                       driver_id UUID NOT NULL REFERENCES drivers(driver_id),
                       pickup_location GEOGRAPHY(POINT) NOT NULL,
                       dropoff_location GEOGRAPHY(POINT) NOT NULL,
                       pickup_address TEXT NOT NULL,
                       dropoff_address TEXT NOT NULL,
                       status VARCHAR(20) NOT NULL DEFAULT 'pending', -- pending, accepted, started, completed, cancelled
                       vehicle_type VARCHAR(20) NOT NULL,
                       estimated_distance FLOAT NOT NULL, -- in meters
                       estimated_duration INTEGER NOT NULL, -- in seconds
                       estimated_fare DECIMAL(10,2) NOT NULL,
                       actual_fare DECIMAL(10,2),
                       route GEOGRAPHY(LINESTRING), -- lưu tuyến đường thực tế (đã đổi tên từ route_geometry)
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                       accepted_at TIMESTAMP WITH TIME ZONE, -- thời điểm tài xế chấp nhận chuyến đi
                       started_at TIMESTAMP WITH TIME ZONE,
                       completed_at TIMESTAMP WITH TIME ZONE,
                       cancelled_at TIMESTAMP WITH TIME ZONE,
                       cancelled_by UUID REFERENCES users(user_id), -- người hủy chuyến (khách hoặc tài xế)
                       cancellation_reason TEXT,
                       actual_distance FLOAT, -- khoảng cách thực tế đã di chuyển (meters)
                       actual_duration INTEGER -- thời gian thực tế đã di chuyển (seconds)
);

-- noinspection SqlResolve
CREATE TABLE ratings (
                         rating_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         trip_id UUID NOT NULL REFERENCES trips(trip_id),
                         rater_id UUID NOT NULL REFERENCES users(user_id),
                         rated_user_id UUID NOT NULL REFERENCES users(user_id),
                         rating_value INTEGER NOT NULL CHECK (rating_value BETWEEN 1 AND 5),
                         comment TEXT,
                         created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- noinspection SqlResolve
CREATE TABLE payments (
                          payment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          trip_id UUID NOT NULL REFERENCES trips(trip_id),
                          amount DECIMAL(10,2) NOT NULL,
                          payment_method VARCHAR(50) NOT NULL, -- cash, bank_card, e_wallet
                          status VARCHAR(20) NOT NULL DEFAULT 'pending', -- pending, completed, failed
                          transaction_id VARCHAR(100),
                          created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                          completed_at TIMESTAMP WITH TIME ZONE
);

-- noinspection SqlResolve
CREATE TABLE payment_methods (
                                 method_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 user_id UUID NOT NULL REFERENCES users(user_id),
                                 method_type VARCHAR(50) NOT NULL, -- bank_card, e_wallet
                                 provider VARCHAR(50) NOT NULL, -- momo, zalopay, visa, mastercard
                                 account_number VARCHAR(255),
                                 is_default BOOLEAN DEFAULT false,
                                 created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- noinspection SqlResolve
CREATE TABLE notifications (
                               notification_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               user_id UUID NOT NULL REFERENCES users(user_id),
                               title VARCHAR(255) NOT NULL,
                               content TEXT NOT NULL,
                               type VARCHAR(50) NOT NULL, -- trip_request, system, promotion
                               read BOOLEAN DEFAULT false,
                               created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- noinspection SqlResolve
CREATE TABLE messages (
                          message_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          trip_id UUID NOT NULL REFERENCES trips(trip_id),
                          sender_id UUID NOT NULL REFERENCES users(user_id),
                          receiver_id UUID NOT NULL REFERENCES users(user_id),
                          content TEXT NOT NULL,
                          read BOOLEAN DEFAULT false,
                          created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- noinspection SqlResolve
CREATE OR REPLACE FUNCTION find_nearest_drivers(
    pickup_point GEOGRAPHY,
    max_distance_meters FLOAT,
    required_vehicle_type VARCHAR
) RETURNS TABLE (
                    driver_id UUID,
                    distance FLOAT,
                    current_location GEOGRAPHY
                ) AS $$
BEGIN
    RETURN QUERY
        SELECT
            dl.driver_id,
            ST_Distance(dl.current_location, pickup_point) as distance,
            dl.current_location
        FROM driver_locations dl
                 JOIN drivers d ON d.driver_id = dl.driver_id
        WHERE d.current_status = 'online'
          AND d.vehicle_type = required_vehicle_type
          AND dl.is_available = true
          AND ST_DWithin(dl.current_location, pickup_point, max_distance_meters)
        ORDER BY dl.current_location <-> pickup_point
        LIMIT 10;
END;
$$ LANGUAGE plpgsql;


-- Chỉ mục cho bảng trips
CREATE INDEX idx_trips_customer_id ON trips(customer_id);
CREATE INDEX idx_trips_driver_id ON trips(driver_id);
CREATE INDEX idx_trips_status ON trips(status);

-- Chỉ mục cho bảng payments
CREATE INDEX idx_payments_trip_id ON payments(trip_id);

-- Chỉ mục cho bảng notifications
CREATE INDEX idx_notifications_user_id ON notifications(user_id);

-- Chỉ mục cho bảng messages
CREATE INDEX idx_messages_trip_id ON messages(trip_id);

-- Chỉ mục cho bảng ratings
CREATE INDEX idx_ratings_trip_id ON ratings(trip_id);
CREATE INDEX idx_ratings_rater_id ON ratings(rater_id);
CREATE INDEX idx_ratings_rated_user_id ON ratings(rated_user_id);
