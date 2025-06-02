package com.rideconnect.service;

import com.rideconnect.entity.*;
import org.postgis.Point;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Facade service để tích hợp các service lại với nhau và cung cấp các phương thức nghiệp vụ cao cấp
 */
public interface RideConnectFacadeService {

    /**
     * Tạo một chuyến đi mới và thông báo cho các tài xế gần đó
     */
    Trip requestTrip(UUID customerId, Point pickupLocation, Point dropoffLocation,
                     String pickupAddress, String dropoffAddress, String vehicleType);

    /**
     * Tài xế chấp nhận một chuyến đi
     */
    Trip acceptTrip(UUID driverId, UUID tripId);

    /**
     * Tài xế bắt đầu chuyến đi
     */
    Trip startTrip(UUID tripId);

    /**
     * Hoàn thành chuyến đi, xử lý thanh toán và đánh giá
     */
    Trip completeTrip(UUID tripId, Float actualDistance, Integer actualDuration);

    /**
     * Hủy chuyến đi
     */
    Trip cancelTrip(UUID tripId, UUID cancelledBy, String cancellationReason);

    /**
     * Cập nhật vị trí của tài xế
     */
    void updateDriverLocation(UUID driverId, Point location, Float heading, Boolean isAvailable);

    /**
     * Tìm các tài xế có sẵn gần vị trí cụ thể
     */
    List<DriverLocation> findAvailableDriversNearby(Point location, double radiusInMeters);

    /**
     * Gửi tin nhắn giữa khách hàng và tài xế
     */
    Message sendTripMessage(UUID senderId, UUID recipientId, UUID tripId, String content);

    /**
     * Đánh giá sau chuyến đi
     */
    void rateTrip(UUID tripId, UUID raterId, UUID ratedUserId, Integer ratingValue, String comment);

    /**
     * Xử lý thanh toán cho chuyến đi
     */
    Payment processPayment(UUID tripId, String paymentMethod);
}
