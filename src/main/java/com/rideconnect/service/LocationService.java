package com.rideconnect.service;

import com.rideconnect.dto.request.location.LocationUpdateRequest;
import com.rideconnect.dto.request.location.NearbyDriversRequest; // Import DTO
import com.rideconnect.dto.response.location.NearbyDriversResponse;

public interface LocationService {

    /**
     * Update user's current location
     *
     * @param userId user ID
     * @param request location update details
     */
    void updateLocation(String userId, LocationUpdateRequest request);

    /**
     * Find nearby available drivers
     *
     * @param userId user ID
     * @param request Nearby drivers request object
     * @return list of nearby drivers
     */
    NearbyDriversResponse findNearbyDrivers(String userId, NearbyDriversRequest request); // Updated signature
}