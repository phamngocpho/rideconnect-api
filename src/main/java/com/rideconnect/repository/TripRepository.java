package com.rideconnect.repository;

import com.rideconnect.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TripRepository extends JpaRepository<Trip, UUID> {

    @Query("SELECT t FROM Trip t WHERE t.customer.customerId = :userId OR t.driver.driverId = :userId ORDER BY t.createdAt DESC")
    List<Trip> findTripsByUserIdOrderByCreatedAtDesc(@Param("userId") UUID userId);

    List<Trip> findByCustomerCustomerIdOrderByCreatedAtDesc(UUID customerId);

    List<Trip> findByDriverDriverIdOrderByCreatedAtDesc(UUID driverId);

    @Query("SELECT t FROM Trip t WHERE t.status = :status AND (t.customer.customerId = :userId OR t.driver.driverId = :userId)")
    List<Trip> findByStatusAndUserId(@Param("status") String status, @Param("userId") UUID userId);

    @Query("SELECT COUNT(t) FROM Trip t WHERE t.driver.driverId = :driverId AND t.status = 'completed'")
    Long countCompletedTripsByDriver(@Param("driverId") UUID driverId);

    @Query("SELECT AVG(r.ratingValue) FROM Rating r WHERE r.ratedUser.userId = :userId")
    Double getAverageRatingForUser(@Param("userId") UUID userId);
}
