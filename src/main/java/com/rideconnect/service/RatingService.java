package com.rideconnect.service;

import com.rideconnect.dto.request.rating.CreateRatingRequest;
import com.rideconnect.dto.response.rating.RatingResponse;
import com.rideconnect.security.CustomUserDetails;

import java.util.UUID;

public interface RatingService {

    /**
     * Create a rating for a trip
     *
     * @param userDetails authenticated user details (customer or driver)
     * @param tripId trip ID
     * @param request rating details
     * @return created rating details
     */
    RatingResponse createRating(CustomUserDetails userDetails, UUID tripId, CreateRatingRequest request);

    /**
     * Get rating for a trip
     *
     * @param userDetails authenticated user details (customer or driver)
     * @param tripId trip ID
     * @return rating details
     */
    RatingResponse getTripRating(CustomUserDetails userDetails, UUID tripId);
}