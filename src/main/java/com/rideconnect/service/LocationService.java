package com.rideconnect.service;

import com.rideconnect.dto.request.location.LocationUpdateRequest;
import com.rideconnect.dto.request.location.NearbyDriversRequest;
import com.rideconnect.dto.response.location.NearbyDriversResponse;
import com.rideconnect.security.CustomUserDetails;

public interface LocationService {

    /**
     * Update user's current location
     *
     * @param userDetails authenticated user details from security context
     * @param request location update details
     */
    void updateLocation(CustomUserDetails userDetails, LocationUpdateRequest request);

    /**
     * Find nearby available drivers
     *
     * @param userDetails authenticated user details from security context
     * @param request Nearby drivers request object
     * @return list of nearby drivers
     */
    NearbyDriversResponse findNearbyDrivers(CustomUserDetails userDetails, NearbyDriversRequest request);
}