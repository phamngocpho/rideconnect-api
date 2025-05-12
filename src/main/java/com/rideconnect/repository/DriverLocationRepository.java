package com.rideconnect.repository;

import com.rideconnect.entity.DriverLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriverLocationRepository extends JpaRepository<DriverLocation, UUID> {

    Optional<DriverLocation> findByDriverDriverId(UUID driverId);

    @Query(value = """
                SELECT 
                    nd.driver_id,
                    nd.distance,
                    ST_Y(nd.current_location::geometry) as latitude,
                    ST_X(nd.current_location::geometry) as longitude,
                    dl.heading,  -- lấy heading từ bảng driver_locations
                    d.vehicle_type,
                    d.vehicle_plate
                FROM find_nearest_drivers(
                    ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
                    :radius,
                    :vehicleType
                ) nd
                JOIN driver_locations dl ON dl.driver_id = nd.driver_id  -- join để lấy heading
                JOIN drivers d ON d.driver_id = nd.driver_id
            """, nativeQuery = true)
    List<Object[]> findAvailableDriversWithinRadius(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radius") double radius,
            @Param("vehicleType") String vehicleType
    );
}
