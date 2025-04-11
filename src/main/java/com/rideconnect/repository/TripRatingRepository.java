package com.rideconnect.repository;

import com.rideconnect.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TripRatingRepository extends JpaRepository<Rating, UUID> {

    List<Rating> findByTripTripId(UUID tripId);

    List<Rating> findByRatedUserUserId(UUID userId);

    Optional<Rating> findByTripTripIdAndRaterUserId(UUID tripId, UUID raterId);

    boolean existsByTripTripIdAndRaterUserId(UUID tripId, UUID raterId);
}
