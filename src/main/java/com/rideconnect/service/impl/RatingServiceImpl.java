package com.rideconnect.service.impl;

import com.rideconnect.dto.request.rating.CreateRatingRequest;
import com.rideconnect.dto.response.rating.RatingResponse;
import com.rideconnect.entity.Trip;
import com.rideconnect.entity.Rating;
import com.rideconnect.entity.User;
import com.rideconnect.exception.BadRequestException;
import com.rideconnect.exception.ResourceNotFoundException;
import com.rideconnect.repository.TripRatingRepository;
import com.rideconnect.repository.TripRepository;
import com.rideconnect.repository.UserRepository;
import com.rideconnect.security.CustomUserDetails;
import com.rideconnect.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final TripRepository tripRepository;
    private final TripRatingRepository tripRatingRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public RatingResponse createRating(CustomUserDetails userDetails, UUID tripId, CreateRatingRequest request) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId.toString()));

        // Check if a trip is completed
        if (!("completed").equals(trip.getStatus())) {
            throw new BadRequestException("Cannot rate a trip that is not completed");
        }

        // Check if the user is either customer or driver of this trip
        UUID userUuid = userDetails.getUserId();
        boolean isDriver = trip.getDriver().getDriverId().equals(userUuid);
        boolean isCustomer = trip.getCustomer().getCustomerId().equals(userUuid);

        if (!isDriver && !isCustomer) {
            throw new BadRequestException("You are not authorized to rate this trip");
        }

        // Check if a user has already rated this trip
        if (tripRatingRepository.existsByTripTripIdAndRaterUserId(tripId, userUuid)) {
            throw new BadRequestException("You have already rated this trip");
        }

        // Get the user being rated
        User rater = userRepository.findById(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userUuid.toString()));

        User rated;
        if (isDriver) {
            rated = trip.getCustomer().getUser();
        } else {
            rated = trip.getDriver().getUser();
        }

        // Kiểm tra rating không null
        if (request.getRating() == null) {
            throw new BadRequestException("Rating score cannot be null");
        }

        // Create rating
        Rating rating = Rating.builder()
                .trip(trip)
                .rater(rater)
                .ratedUser(rated)
                .ratingValue(request.getRating())
                .comment(request.getComment())
                .build();

        Rating savedRating = tripRatingRepository.save(rating);

        return mapRatingToRatingResponse(savedRating);
    }

    @Override
    @Transactional(readOnly = true)
    public RatingResponse getTripRating(CustomUserDetails userDetails, UUID tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId.toString()));

        // Check if the user is either customer or driver of this trip
        UUID userUuid = userDetails.getUserId();
        boolean isDriver = trip.getDriver().getDriverId().equals(userUuid);
        boolean isCustomer = trip.getCustomer().getCustomerId().equals(userUuid);

        if (!isDriver && !isCustomer) {
            throw new BadRequestException("You are not authorized to view this rating");
        }

        // Get a rating given by this user
        Rating rating = tripRatingRepository.findByTripTripIdAndRaterUserId(tripId, userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Rating", "tripId and userId",
                        tripId + " and " + userUuid));
        return mapRatingToRatingResponse(rating);
    }

    private RatingResponse mapRatingToRatingResponse(Rating rating) {
        return RatingResponse.builder()
                .ratingId(rating.getRatingId())
                .tripId(rating.getTrip().getTripId())
                .raterId(rating.getRater().getUserId())
                .ratedId(rating.getRatedUser().getUserId())
                .rating(rating.getRatingValue())
                .comment(rating.getComment())
                .createdAt(rating.getCreatedAt())
                .build();
    }
}