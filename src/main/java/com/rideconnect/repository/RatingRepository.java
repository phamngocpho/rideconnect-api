package com.rideconnect.repository;

import com.rideconnect.entity.Rating;
import com.rideconnect.entity.Trip;
import com.rideconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {

    List<Rating> findByRatedUser(User ratedUser);

    List<Rating> findByRater(User rater);

    List<Rating> findByTrip(Trip trip);

    Optional<Rating> findByTripAndRaterAndRatedUser(Trip trip, User rater, User ratedUser);

    @Query("SELECT AVG(r.ratingValue) FROM Rating r WHERE r.ratedUser = :user")
    Double getAverageRatingForUser(@Param("user") User user);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.ratedUser = :user")
    Long countRatingsByRatedUser(@Param("user") User user);
}
