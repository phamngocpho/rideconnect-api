package com.rideconnect.service;

import com.rideconnect.entity.Customer;
import com.rideconnect.entity.Driver;
import com.rideconnect.entity.Trip;
import org.postgis.LineString;
import org.postgis.Point;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface TripService {

    Trip save(Trip trip);

    Trip findById(UUID tripId);

    List<Trip> findAll();

    List<Trip> findByCustomer(Customer customer);

    List<Trip> findByDriver(Driver driver);

    List<Trip> findByStatus(String status);

    List<Trip> findByCustomerOrderByCreatedAtDesc(Customer customer);

    List<Trip> findByDriverOrderByCreatedAtDesc(Driver driver);

    List<Trip> findByCreatedAtBetween(ZonedDateTime start, ZonedDateTime end);

    List<Trip> findCompletedTripsByDriver(Driver driver);

    List<Trip> findCompletedTripsByCustomer(Customer customer);

    Long countCompletedTripsByDriver(Driver driver);

    Long countCompletedTripsByCustomer(Customer customer);

    List<Trip> findPendingTripsNearby(double longitude, double latitude, double radiusInMeters);

    Trip createTrip(Customer customer, Point pickupLocation, Point dropoffLocation,
                    String pickupAddress, String dropoffAddress, String vehicleType);

    Trip assignDriver(UUID tripId, Driver driver);

    Trip startTrip(UUID tripId);

    Trip completeTrip(UUID tripId, Float actualDistance, Integer actualDuration, BigDecimal actualFare);

    Trip cancelTrip(UUID tripId, UUID cancelledBy, String cancellationReason);

    Trip updateTripRoute(UUID tripId, LineString route);

    void delete(UUID tripId);
}
