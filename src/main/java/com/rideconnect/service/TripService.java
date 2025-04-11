package com.rideconnect.service;

import com.rideconnect.dto.request.trip.CreateTripRequest;
import com.rideconnect.dto.request.trip.UpdateTripStatusRequest;
import com.rideconnect.dto.response.trip.TripDetailsResponse;
import com.rideconnect.dto.response.trip.TripHistoryResponse;

import java.util.UUID;

public interface TripService {

    /**
     * Create a new trip request
     *
     * @param userId user ID (customer)
     * @param request trip details
     * @return created trip details
     */
    TripDetailsResponse createTrip(String userId, CreateTripRequest request);

    /**
     * Get trip details
     *
     * @param userId user ID (customer or driver)
     * @param tripId trip ID
     * @return trip details
     */
    TripDetailsResponse getTripDetails(String userId, UUID tripId);

    /**
     * Update trip status
     *
     * @param userId user ID (customer or driver)
     * @param tripId trip ID
     * @param request status update details
     * @return updated trip details
     */
    TripDetailsResponse updateTripStatus(String userId, UUID tripId, UpdateTripStatusRequest request);

    /**
     * Get trip history for a user
     *
     * @param userId user ID (customer or driver)
     * @return list of past trips
     */
    TripHistoryResponse getTripHistory(String userId);
}
