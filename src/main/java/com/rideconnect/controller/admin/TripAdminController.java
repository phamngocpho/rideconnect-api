package com.rideconnect.controller.admin;

import com.rideconnect.entity.Customer;
import com.rideconnect.entity.Driver;
import com.rideconnect.entity.Trip;
import com.rideconnect.service.CustomerService;
import com.rideconnect.service.DriverService;
import com.rideconnect.service.TripService;
import org.postgis.LineString;
import org.postgis.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/trips")
public class TripAdminController {

    private final TripService tripService;
    private final CustomerService customerService;
    private final DriverService driverService;

    @Autowired
    public TripAdminController(TripService tripService, CustomerService customerService, DriverService driverService) {
        this.tripService = tripService;
        this.customerService = customerService;
        this.driverService = driverService;
    }

    @GetMapping
    public ResponseEntity<List<Trip>> getAllTrips() {
        List<Trip> trips = tripService.findAll();
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripById(@PathVariable UUID id) {
        Trip trip = tripService.findById(id);
        return ResponseEntity.ok(trip);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Trip>> getTripsByCustomer(@PathVariable UUID customerId) {
        Customer customer = customerService.findById(customerId);
        List<Trip> trips = tripService.findByCustomer(customer);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<Trip>> getTripsByDriver(@PathVariable UUID driverId) {
        Driver driver = driverService.findById(driverId);
        List<Trip> trips = tripService.findByDriver(driver);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Trip>> getTripsByStatus(@PathVariable String status) {
        List<Trip> trips = tripService.findByStatus(status);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/customer/{customerId}/recent")
    public ResponseEntity<List<Trip>> getRecentTripsByCustomer(@PathVariable UUID customerId) {
        Customer customer = customerService.findById(customerId);
        List<Trip> trips = tripService.findByCustomerOrderByCreatedAtDesc(customer);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/driver/{driverId}/recent")
    public ResponseEntity<List<Trip>> getRecentTripsByDriver(@PathVariable UUID driverId) {
        Driver driver = driverService.findById(driverId);
        List<Trip> trips = tripService.findByDriverOrderByCreatedAtDesc(driver);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/timeframe")
    public ResponseEntity<List<Trip>> getTripsByTimeframe(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime end) {

        List<Trip> trips = tripService.findByCreatedAtBetween(start, end);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/driver/{driverId}/completed")
    public ResponseEntity<List<Trip>> getCompletedTripsByDriver(@PathVariable UUID driverId) {
        Driver driver = driverService.findById(driverId);
        List<Trip> trips = tripService.findCompletedTripsByDriver(driver);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/customer/{customerId}/completed")
    public ResponseEntity<List<Trip>> getCompletedTripsByCustomer(@PathVariable UUID customerId) {
        Customer customer = customerService.findById(customerId);
        List<Trip> trips = tripService.findCompletedTripsByCustomer(customer);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/driver/{driverId}/completed/count")
    public ResponseEntity<Long> getCompletedTripsCountByDriver(@PathVariable UUID driverId) {
        Driver driver = driverService.findById(driverId);
        Long count = tripService.countCompletedTripsByDriver(driver);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/customer/{customerId}/completed/count")
    public ResponseEntity<Long> getCompletedTripsCountByCustomer(@PathVariable UUID customerId) {
        Customer customer = customerService.findById(customerId);
        Long count = tripService.countCompletedTripsByCustomer(customer);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<Trip>> getPendingTripsNearby(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam double radiusInMeters) {

        List<Trip> trips = tripService.findPendingTripsNearby(longitude, latitude, radiusInMeters);
        return ResponseEntity.ok(trips);
    }

    @PostMapping("/create")
    public ResponseEntity<Trip> createTrip(
            @RequestParam UUID customerId,
            @RequestParam double pickupLongitude,
            @RequestParam double pickupLatitude,
            @RequestParam double dropoffLongitude,
            @RequestParam double dropoffLatitude,
            @RequestParam String pickupAddress,
            @RequestParam String dropoffAddress,
            @RequestParam String vehicleType) {

        Customer customer = customerService.findById(customerId);
        Point pickupLocation = new Point(pickupLongitude, pickupLatitude);
        Point dropoffLocation = new Point(dropoffLongitude, dropoffLatitude);

        Trip trip = tripService.createTrip(customer, pickupLocation, dropoffLocation,
                pickupAddress, dropoffAddress, vehicleType);
        return ResponseEntity.status(HttpStatus.CREATED).body(trip);
    }

    @PutMapping("/{id}/assign-driver")
    public ResponseEntity<Trip> assignDriver(
            @PathVariable UUID id,
            @RequestParam UUID driverId) {

        Driver driver = driverService.findById(driverId);
        Trip trip = tripService.assignDriver(id, driver);
        return ResponseEntity.ok(trip);
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<Trip> startTrip(@PathVariable UUID id) {
        Trip trip = tripService.startTrip(id);
        return ResponseEntity.ok(trip);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Trip> completeTrip(
            @PathVariable UUID id,
            @RequestParam Float actualDistance,
            @RequestParam Integer actualDuration,
            @RequestParam BigDecimal actualFare) {

        Trip trip = tripService.completeTrip(id, actualDistance, actualDuration, actualFare);
        return ResponseEntity.ok(trip);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Trip> cancelTrip(
            @PathVariable UUID id,
            @RequestParam UUID cancelledBy,
            @RequestParam String cancellationReason) {

        Trip trip = tripService.cancelTrip(id, cancelledBy, cancellationReason);
        return ResponseEntity.ok(trip);
    }

    @PutMapping("/{id}/update-route")
    public ResponseEntity<Trip> updateTripRoute(
            @PathVariable UUID id,
            @RequestBody String routeWKT) {

        // Chuyển đổi chuỗi WKT thành đối tượng LineString
        LineString route = null;
        try {
            // Đây là một ví dụ đơn giản, trong thực tế cần sử dụng thư viện WKT Parser
            route = new LineString(routeWKT);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        Trip trip = tripService.updateTripRoute(id, route);
        return ResponseEntity.ok(trip);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable UUID id) {
        tripService.delete(id);
        return ResponseEntity.noContent().build();
    }
}