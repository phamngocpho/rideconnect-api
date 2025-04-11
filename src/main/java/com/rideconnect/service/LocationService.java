package com.rideconnect.service;

import com.rideconnect.dto.request.location.LocationUpdateRequest;
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
     * @param latitude user's latitude
     * @param longitude user's longitude
     * @param radius search radius in meters
     * @param vehicleType optional vehicle type filter
     * @return list of nearby drivers
     */
    NearbyDriversResponse findNearbyDrivers(String userId, double latitude, double longitude, double radius, String vehicleType);
}
