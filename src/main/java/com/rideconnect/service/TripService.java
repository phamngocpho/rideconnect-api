package com.rideconnect.service;

import com.rideconnect.dto.request.trip.CreateTripRequest;
import com.rideconnect.dto.request.trip.UpdateTripStatusRequest;
import com.rideconnect.dto.response.trip.TripDetailsResponse;
import com.rideconnect.dto.response.trip.TripHistoryResponse;
import com.rideconnect.security.CustomUserDetails;

import java.util.UUID;

public interface TripService {

    /**
     * Create a new trip request
     *
     * @param userDetails authenticated user details
     * @param request trip details
     * @return created trip details
     */
    TripDetailsResponse createTrip(CustomUserDetails userDetails, CreateTripRequest request);

    /**
     * Get trip details
     *
     * @param userDetails authenticated user details
     * @param tripId trip ID
     * @return trip details
     */
    TripDetailsResponse getTripDetails(CustomUserDetails userDetails, UUID tripId);

    /**
     * Update trip status
     *
     * @param userDetails authenticated user details
     * @param tripId trip ID
     * @param request status update details
     * @return updated trip details
     */
    TripDetailsResponse updateTripStatus(CustomUserDetails userDetails, UUID tripId, UpdateTripStatusRequest request);

    /**
     * Get trip history for a user
     *
     * @param userDetails authenticated user details
     * @return list of past trips
     */
    TripHistoryResponse getTripHistory(CustomUserDetails userDetails);
}