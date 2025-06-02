package com.rideconnect.service;

import com.rideconnect.entity.Rating;
import com.rideconnect.entity.Trip;
import com.rideconnect.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RatingService {

    Rating save(Rating rating);

    Rating findById(UUID ratingId);

    List<Rating> findAll();

    List<Rating> findByRatedUser(User ratedUser);

    List<Rating> findByRater(User rater);

    List<Rating> findByTrip(Trip trip);

    Optional<Rating> findByTripAndRaterAndRatedUser(Trip trip, User rater, User ratedUser);

    Double getAverageRatingForUser(User user);

    Long countRatingsByRatedUser(User user);

    Rating createRating(Trip trip, User rater, User ratedUser, Integer ratingValue, String comment);

    void delete(UUID ratingId);
}
