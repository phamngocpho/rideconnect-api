package com.rideconnect.controller.admin;

import com.rideconnect.entity.*;
import com.rideconnect.service.*;
import org.postgis.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/facade")
public class RideConnectFacadeAdminController {

    private final RideConnectFacadeService facadeService;
    private final UserService userService;
    private final TripService tripService;

    @Autowired
    public RideConnectFacadeAdminController(
            RideConnectFacadeService facadeService,
            UserService userService,
            TripService tripService) {
        this.facadeService = facadeService;
        this.userService = userService;
        this.tripService = tripService;
    }

    @PostMapping("/request-trip")
    public ResponseEntity<Trip> requestTrip(
            @RequestParam UUID customerId,
            @RequestParam double pickupLongitude,
            @RequestParam double pickupLatitude,
            @RequestParam double dropoffLongitude,
            @RequestParam double dropoffLatitude,
            @RequestParam String pickupAddress,
            @RequestParam String dropoffAddress,
            @RequestParam String vehicleType) {

        Point pickupLocation = new Point(pickupLongitude, pickupLatitude);
        Point dropoffLocation = new Point(dropoffLongitude, dropoffLatitude);

        Trip trip = facadeService.requestTrip(customerId, pickupLocation, dropoffLocation,
                pickupAddress, dropoffAddress, vehicleType);
        return ResponseEntity.status(HttpStatus.CREATED).body(trip);
    }

    @PutMapping("/accept-trip")
    public ResponseEntity<Trip> acceptTrip(
            @RequestParam UUID driverId,
            @RequestParam UUID tripId) {

        Trip trip = facadeService.acceptTrip(driverId, tripId);
        return ResponseEntity.ok(trip);
    }

    @PutMapping("/start-trip/{tripId}")
    public ResponseEntity<Trip> startTrip(@PathVariable UUID tripId) {
        Trip trip = facadeService.startTrip(tripId);
        return ResponseEntity.ok(trip);
    }

    @PutMapping("/complete-trip")
    public ResponseEntity<Trip> completeTrip(
            @RequestParam UUID tripId,
            @RequestParam Float actualDistance,
            @RequestParam Integer actualDuration) {

        Trip trip = facadeService.completeTrip(tripId, actualDistance, actualDuration);
        return ResponseEntity.ok(trip);
    }

    @PutMapping("/cancel-trip")
    public ResponseEntity<Trip> cancelTrip(
            @RequestParam UUID tripId,
            @RequestParam UUID cancelledBy,
            @RequestParam String cancellationReason) {

        Trip trip = facadeService.cancelTrip(tripId, cancelledBy, cancellationReason);
        return ResponseEntity.ok(trip);
    }

    @PutMapping("/update-driver-location")
    public ResponseEntity<Void> updateDriverLocation(
            @RequestParam UUID driverId,
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam(required = false) Float heading,
            @RequestParam(required = false) Boolean isAvailable) {

        Point location = new Point(longitude, latitude);
        facadeService.updateDriverLocation(driverId, location, heading, isAvailable);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/available-drivers-nearby")
    public ResponseEntity<List<DriverLocation>> findAvailableDriversNearby(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam double radiusInMeters) {

        Point location = new Point(longitude, latitude);
        List<DriverLocation> drivers = facadeService.findAvailableDriversNearby(location, radiusInMeters);
        return ResponseEntity.ok(drivers);
    }

    @PostMapping("/send-message")
    public ResponseEntity<Message> sendTripMessage(
            @RequestParam UUID senderId,
            @RequestParam UUID recipientId,
            @RequestParam UUID tripId,
            @RequestParam String content) {

        Message message = facadeService.sendTripMessage(senderId, recipientId, tripId, content);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @PostMapping("/rate-trip")
    public ResponseEntity<Void> rateTrip(
            @RequestParam UUID tripId,
            @RequestParam UUID raterId,
            @RequestParam UUID ratedUserId,
            @RequestParam Integer ratingValue,
            @RequestParam(required = false) String comment) {

        facadeService.rateTrip(tripId, raterId, ratedUserId, ratingValue, comment);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/process-payment")
    public ResponseEntity<Payment> processPayment(
            @RequestParam UUID tripId,
            @RequestParam String paymentMethod) {

        Payment payment = facadeService.processPayment(tripId, paymentMethod);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @PostMapping("/simulate-trip")
    public ResponseEntity<Trip> simulateCompleteTrip(
            @RequestParam UUID customerId,
            @RequestParam UUID driverId,
            @RequestParam double pickupLongitude,
            @RequestParam double pickupLatitude,
            @RequestParam double dropoffLongitude,
            @RequestParam double dropoffLatitude,
            @RequestParam String pickupAddress,
            @RequestParam String dropoffAddress,
            @RequestParam String vehicleType,
            @RequestParam Float distance,
            @RequestParam Integer duration) {

        // 1. Tạo chuyến đi
        Point pickupLocation = new Point(pickupLongitude, pickupLatitude);
        Point dropoffLocation = new Point(dropoffLongitude, dropoffLatitude);

        Trip trip = facadeService.requestTrip(customerId, pickupLocation, dropoffLocation,
                pickupAddress, dropoffAddress, vehicleType);

        // 2. Tài xế chấp nhận chuyến đi
        trip = facadeService.acceptTrip(driverId, trip.getTripId());

        // 3. Bắt đầu chuyến đi
        trip = facadeService.startTrip(trip.getTripId());

        // 4. Hoàn thành chuyến đi
        trip = facadeService.completeTrip(trip.getTripId(), distance, duration);

        // 5. Xử lý thanh toán
        facadeService.processPayment(trip.getTripId(), "card");

        return ResponseEntity.ok(trip);
    }
}
