package com.rideconnect.repository;

import com.rideconnect.entity.Customer;
import com.rideconnect.entity.Driver;
import com.rideconnect.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TripRepository extends JpaRepository<Trip, UUID> {

    List<Trip> findByCustomer(Customer customer);

    List<Trip> findByDriver(Driver driver);

    List<Trip> findByStatus(String status);

    List<Trip> findByCustomerOrderByCreatedAtDesc(Customer customer);

    List<Trip> findByDriverOrderByCreatedAtDesc(Driver driver);

    List<Trip> findByCreatedAtBetween(ZonedDateTime start, ZonedDateTime end);

    @Query("SELECT t FROM Trip t WHERE t.status = 'completed' AND t.driver = :driver")
    List<Trip> findCompletedTripsByDriver(@Param("driver") Driver driver);

    @Query("SELECT t FROM Trip t WHERE t.status = 'completed' AND t.customer = :customer")
    List<Trip> findCompletedTripsByCustomer(@Param("customer") Customer customer);

    @Query("SELECT COUNT(t) FROM Trip t WHERE t.driver = :driver AND t.status = 'completed'")
    Long countCompletedTripsByDriver(@Param("driver") Driver driver);

    @Query("SELECT COUNT(t) FROM Trip t WHERE t.customer = :customer AND t.status = 'completed'")
    Long countCompletedTripsByCustomer(@Param("customer") Customer customer);

    @Query(value = "SELECT * FROM trip t WHERE ST_DWithin(t.pickup_location, ST_MakePoint(:longitude, :latitude)::geography, :radiusInMeters) AND t.status = 'pending'", nativeQuery = true)
    List<Trip> findPendingTripsNearby(@Param("longitude") double longitude, @Param("latitude") double latitude, @Param("radiusInMeters") double radiusInMeters);
}