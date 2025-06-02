package com.rideconnect.service.impl;

import com.rideconnect.entity.*;
import com.rideconnect.repository.*;
import com.rideconnect.service.*;
import org.postgis.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class RideConnectFacadeServiceImpl implements RideConnectFacadeService {

    private final TripService tripService;
    private final CustomerRepository customerRepository;
    private final DriverRepository driverRepository;
    private final DriverLocationService driverLocationService;
    private final MessageService messageService;
    private final NotificationService notificationService;
    private final PaymentService paymentService;
    private final RatingService ratingService;
    private final LocationHistoryService locationHistoryService;
    private final UserRepository userRepository;

    @Autowired
    public RideConnectFacadeServiceImpl(
            TripService tripService,
            CustomerRepository customerRepository,
            DriverRepository driverRepository,
            DriverLocationService driverLocationService,
            MessageService messageService,
            NotificationService notificationService,
            PaymentService paymentService,
            RatingService ratingService,
            LocationHistoryService locationHistoryService,
            UserRepository userRepository) {
        this.tripService = tripService;
        this.customerRepository = customerRepository;
        this.driverRepository = driverRepository;
        this.driverLocationService = driverLocationService;
        this.messageService = messageService;
        this.notificationService = notificationService;
        this.paymentService = paymentService;
        this.ratingService = ratingService;
        this.locationHistoryService = locationHistoryService;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Trip requestTrip(UUID customerId, Point pickupLocation, Point dropoffLocation,
                            String pickupAddress, String dropoffAddress, String vehicleType) {
        // Tìm khách hàng
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));

        // Tạo chuyến đi
        Trip trip = tripService.createTrip(customer, pickupLocation, dropoffLocation,
                pickupAddress, dropoffAddress, vehicleType);

        // Tìm các tài xế gần đó
        List<DriverLocation> nearbyDrivers = driverLocationService.findAvailableDriversNearby(
                pickupLocation.getX(), pickupLocation.getY(), 5000); // 5km

        // Gửi thông báo cho các tài xế gần đó
        for (DriverLocation driverLocation : nearbyDrivers) {
            Driver driver = driverLocation.getDriver();
            notificationService.createSystemNotification(
                    driver.getUser(),
                    "New Trip Request",
                    "New trip request from " + pickupAddress + " to " + dropoffAddress,
                    "trip_request"
            );
        }

        // Ghi lại vị trí của khách hàng
        locationHistoryService.recordUserLocation(customer.getUser(), pickupLocation, null, null, trip.getTripId());

        return trip;
    }

    @Override
    @Transactional
    public Trip acceptTrip(UUID driverId, UUID tripId) {
        // Tìm tài xế
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));

        // Chấp nhận chuyến đi
        Trip trip = tripService.assignDriver(tripId, driver);

        // Thông báo cho khách hàng
        notificationService.createSystemNotification(
                trip.getCustomer().getUser(),
                "Trip Accepted",
                "Your trip has been accepted by a driver",
                "trip_accepted"
        );

        // Cập nhật trạng thái của tài xế là không có sẵn
        driverLocationService.updateDriverAvailability(driverId, false);

        return trip;
    }

    @Override
    @Transactional
    public Trip startTrip(UUID tripId) {
        // Bắt đầu chuyến đi
        Trip trip = tripService.startTrip(tripId);

        // Thông báo cho khách hàng
        notificationService.createSystemNotification(
                trip.getCustomer().getUser(),
                "Trip Started",
                "Your trip has started",
                "trip_started"
        );

        return trip;
    }

    @Override
    @Transactional
    public Trip completeTrip(UUID tripId, Float actualDistance, Integer actualDuration) {
        // Tìm chuyến đi
        Trip trip = tripService.findById(tripId);

        // Tính toán giá tiền thực tế
        BigDecimal actualFare = calculateActualFare(actualDistance, trip.getVehicleType());

        // Hoàn thành chuyến đi
        trip = tripService.completeTrip(tripId, actualDistance, actualDuration, actualFare);

        // Xử lý thanh toán
        paymentService.createPayment(trip, "default", actualFare);

        // Thông báo cho khách hàng
        notificationService.createSystemNotification(
                trip.getCustomer().getUser(),
                "Trip Completed",
                "Your trip has been completed. Total fare: " + actualFare,
                "trip_completed"
        );

        // Thông báo cho tài xế
        notificationService.createSystemNotification(
                trip.getDriver().getUser(),
                "Trip Completed",
                "You have completed a trip. Earnings: " + actualFare,
                "trip_completed"
        );

        // Cập nhật trạng thái của tài xế là có sẵn
        driverLocationService.updateDriverAvailability(trip.getDriver().getDriverId(), true);

        return trip;
    }

    private BigDecimal calculateActualFare(Float distance, String vehicleType) {
        // Logic tính giá tiền dựa trên khoảng cách và loại xe
        BigDecimal baseRate;
        switch (vehicleType) {
            case "standard":
                baseRate = new BigDecimal("10000"); // 10,000 VND
                break;
            case "premium":
                baseRate = new BigDecimal("15000"); // 15,000 VND
                break;
            default:
                baseRate = new BigDecimal("8000"); // 8,000 VND
        }

        return baseRate.multiply(new BigDecimal(distance));
    }

    @Override
    @Transactional
    public Trip cancelTrip(UUID tripId, UUID cancelledBy, String cancellationReason) {
        // Hủy chuyến đi
        Trip trip = tripService.cancelTrip(tripId, cancelledBy, cancellationReason);

        // Thông báo cho khách hàng nếu tài xế hủy
        if (trip.getDriver() != null && trip.getDriver().getDriverId().equals(cancelledBy)) {
            notificationService.createSystemNotification(
                    trip.getCustomer().getUser(),
                    "Trip Cancelled",
                    "Your trip has been cancelled by the driver: " + cancellationReason,
                    "trip_cancelled"
            );

            // Cập nhật trạng thái của tài xế là có sẵn
            driverLocationService.updateDriverAvailability(trip.getDriver().getDriverId(), true);
        }

        // Thông báo cho tài xế nếu khách hàng hủy
        if (trip.getCustomer().getCustomerId().equals(cancelledBy) && trip.getDriver() != null) {
            notificationService.createSystemNotification(
                    trip.getDriver().getUser(),
                    "Trip Cancelled",
                    "The trip has been cancelled by the customer: " + cancellationReason,
                    "trip_cancelled"
            );

            // Cập nhật trạng thái của tài xế là có sẵn
            driverLocationService.updateDriverAvailability(trip.getDriver().getDriverId(), true);
        }

        return trip;
    }

    @Override
    @Transactional
    public void updateDriverLocation(UUID driverId, Point location, Float heading, Boolean isAvailable) {
        // Cập nhật vị trí của tài xế
        driverLocationService.updateDriverLocation(driverId, location, heading);

        // Cập nhật trạng thái có sẵn nếu được cung cấp
        if (isAvailable != null) {
            driverLocationService.updateDriverAvailability(driverId, isAvailable);
        }

        // Tìm tài xế
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));

        // Ghi lại vị trí vào lịch sử
        locationHistoryService.recordUserLocation(driver.getUser(), location, heading, null, null);
    }

    @Override
    public List<DriverLocation> findAvailableDriversNearby(Point location, double radiusInMeters) {
        return driverLocationService.findAvailableDriversNearby(location.getX(), location.getY(), radiusInMeters);
    }

    @Override
    @Transactional
    public Message sendTripMessage(UUID senderId, UUID recipientId, UUID tripId, String content) {
        // Tìm người gửi
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found with id: " + senderId));

        // Tìm người nhận
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found with id: " + recipientId));

        // Tìm chuyến đi
        Trip trip = tripService.findById(tripId);

        // Gửi tin nhắn
        Message message = messageService.sendMessage(sender, recipient, trip, content);

        // Thông báo cho người nhận
        notificationService.createSystemNotification(
                recipient,
                "New Message",
                "You have a new message from " + sender.getFullName(),
                "new_message"
        );

        return message;
    }

    @Override
    @Transactional
    public void rateTrip(UUID tripId, UUID raterId, UUID ratedUserId, Integer ratingValue, String comment) {
        // Tìm chuyến đi
        Trip trip = tripService.findById(tripId);

        // Tìm người đánh giá
        User rater = userRepository.findById(raterId)
                .orElseThrow(() -> new RuntimeException("Rater not found with id: " + raterId));

        // Tìm người được đánh giá
        User ratedUser = userRepository.findById(ratedUserId)
                .orElseThrow(() -> new RuntimeException("Rated user not found with id: " + ratedUserId));

        // Tạo đánh giá
        ratingService.createRating(trip, rater, ratedUser, ratingValue, comment);

        // Thông báo cho người được đánh giá
        notificationService.createSystemNotification(
                ratedUser,
                "New Rating",
                "You have received a " + ratingValue + " star rating",
                "new_rating"
        );
    }

    @Override
    @Transactional
    public Payment processPayment(UUID tripId, String paymentMethod) {
        // Tìm chuyến đi
        Trip trip = tripService.findById(tripId);

        // Tạo thanh toán
        Payment payment = paymentService.createPayment(trip, paymentMethod, trip.getActualFare());

        // Xử lý thanh toán (giả định)
        String transactionId = "TX-" + UUID.randomUUID().toString();
        payment = paymentService.processPayment(payment.getPaymentId(), transactionId);

        // Thông báo cho khách hàng
        notificationService.createSystemNotification(
                trip.getCustomer().getUser(),
                "Payment Processed",
                "Your payment of " + trip.getActualFare() + " has been processed",
                "payment_processed"
        );

        // Thông báo cho tài xế
        notificationService.createSystemNotification(
                trip.getDriver().getUser(),
                "Payment Received",
                "You have received a payment of " + trip.getActualFare(),
                "payment_received"
        );

        return payment;
    }
}
