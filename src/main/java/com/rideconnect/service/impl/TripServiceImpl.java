package com.rideconnect.service.impl;

import com.rideconnect.entity.Customer;
import com.rideconnect.entity.Driver;
import com.rideconnect.entity.Trip;
import com.rideconnect.repository.TripRepository;
import com.rideconnect.service.TripService;
import org.postgis.LineString;
import org.postgis.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;

    @Autowired
    public TripServiceImpl(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Override
    @Transactional
    public Trip save(Trip trip) {
        return tripRepository.save(trip);
    }

    @Override
    public Trip findById(UUID tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found with id: " + tripId));
    }

    @Override
    public List<Trip> findAll() {
        return tripRepository.findAll();
    }

    @Override
    public List<Trip> findByCustomer(Customer customer) {
        return tripRepository.findByCustomer(customer);
    }

    @Override
    public List<Trip> findByDriver(Driver driver) {
        return tripRepository.findByDriver(driver);
    }

    @Override
    public List<Trip> findByStatus(String status) {
        return tripRepository.findByStatus(status);
    }

    @Override
    public List<Trip> findByCustomerOrderByCreatedAtDesc(Customer customer) {
        return tripRepository.findByCustomerOrderByCreatedAtDesc(customer);
    }

    @Override
    public List<Trip> findByDriverOrderByCreatedAtDesc(Driver driver) {
        return tripRepository.findByDriverOrderByCreatedAtDesc(driver);
    }

    @Override
    public List<Trip> findByCreatedAtBetween(ZonedDateTime start, ZonedDateTime end) {
        return tripRepository.findByCreatedAtBetween(start, end);
    }

    @Override
    public List<Trip> findCompletedTripsByDriver(Driver driver) {
        return tripRepository.findCompletedTripsByDriver(driver);
    }

    @Override
    public List<Trip> findCompletedTripsByCustomer(Customer customer) {
        return tripRepository.findCompletedTripsByCustomer(customer);
    }

    @Override
    public Long countCompletedTripsByDriver(Driver driver) {
        return tripRepository.countCompletedTripsByDriver(driver);
    }

    @Override
    public Long countCompletedTripsByCustomer(Customer customer) {
        return tripRepository.countCompletedTripsByCustomer(customer);
    }

    @Override
    public List<Trip> findPendingTripsNearby(double longitude, double latitude, double radiusInMeters) {
        return tripRepository.findPendingTripsNearby(longitude, latitude, radiusInMeters);
    }

    @Override
    @Transactional
    public Trip createTrip(Customer customer, Point pickupLocation, Point dropoffLocation,
                           String pickupAddress, String dropoffAddress, String vehicleType) {
        Trip trip = Trip.builder()
                .customer(customer)
                .pickupLocation(pickupLocation)
                .dropoffLocation(dropoffLocation)
                .pickupAddress(pickupAddress)
                .dropoffAddress(dropoffAddress)
                .vehicleType(vehicleType)
                .status("pending")
                .build();

        // Tính toán ước tính khoảng cách, thời gian và giá tiền
        // Đây là logic giả định, cần thay thế bằng logic thực tế
        float estimatedDistance = calculateDistance(pickupLocation, dropoffLocation);
        int estimatedDuration = calculateDuration(estimatedDistance);
        BigDecimal estimatedFare = calculateFare(estimatedDistance, vehicleType);

        trip.setEstimatedDistance(estimatedDistance);
        trip.setEstimatedDuration(estimatedDuration);
        trip.setEstimatedFare(estimatedFare);

        return tripRepository.save(trip);
    }

    private float calculateDistance(Point pickup, Point dropoff) {
        // Logic tính khoảng cách giữa hai điểm
        // Đây là một ví dụ đơn giản, cần thay thế bằng công thức thực tế
        return 10.0f; // Giả sử 10km
    }

    private int calculateDuration(float distance) {
        // Logic tính thời gian dựa trên khoảng cách
        // Giả sử tốc độ trung bình 30km/h
        return (int) (distance / 30.0 * 60); // Đổi sang phút
    }

    private BigDecimal calculateFare(float distance, String vehicleType) {
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
    public Trip assignDriver(UUID tripId, Driver driver) {
        Trip trip = findById(tripId);

        if (!"pending".equals(trip.getStatus())) {
            throw new IllegalStateException("Trip is not in pending status");
        }

        trip.setDriver(driver);
        trip.setStatus("accepted");
        trip.setAcceptedAt(ZonedDateTime.now());

        return tripRepository.save(trip);
    }

    @Override
    @Transactional
    public Trip startTrip(UUID tripId) {
        Trip trip = findById(tripId);

        if (!"accepted".equals(trip.getStatus())) {
            throw new IllegalStateException("Trip is not in accepted status");
        }

        trip.setStatus("in_progress");
        trip.setStartedAt(ZonedDateTime.now());

        return tripRepository.save(trip);
    }

    @Override
    @Transactional
    public Trip completeTrip(UUID tripId, Float actualDistance, Integer actualDuration, BigDecimal actualFare) {
        Trip trip = findById(tripId);

        if (!"in_progress".equals(trip.getStatus())) {
            throw new IllegalStateException("Trip is not in progress");
        }

        trip.setStatus("completed");
        trip.setCompletedAt(ZonedDateTime.now());
        trip.setActualDistance(actualDistance);
        trip.setActualDuration(actualDuration);
        trip.setActualFare(actualFare);

        return tripRepository.save(trip);
    }

    @Override
    @Transactional
    public Trip cancelTrip(UUID tripId, UUID cancelledBy, String cancellationReason) {
        Trip trip = findById(tripId);

        if ("completed".equals(trip.getStatus()) || "cancelled".equals(trip.getStatus())) {
            throw new IllegalStateException("Trip cannot be cancelled in its current state");
        }

        trip.setStatus("cancelled");
        trip.setCancelledAt(ZonedDateTime.now());
        trip.setCancelledBy(cancelledBy);
        trip.setCancellationReason(cancellationReason);

        return tripRepository.save(trip);
    }

    @Override
    @Transactional
    public Trip updateTripRoute(UUID tripId, LineString route) {
        Trip trip = findById(tripId);
        trip.setRoute(route);
        return tripRepository.save(trip);
    }

    @Override
    @Transactional
    public void delete(UUID tripId) {
        tripRepository.deleteById(tripId);
    }
}
