package com.rideconnect.service;

import com.rideconnect.dto.request.driver.RegisterDriverRequest;
import com.rideconnect.dto.request.driver.UpdateDriverStatusRequest;
import com.rideconnect.dto.response.driver.DriverDashboardResponse;
import com.rideconnect.dto.response.driver.DriverProfileResponse;

public interface DriverService {

    /**
     * Register as a driver
     *
     * @param userId user ID
     * @param request driver registration details
     * @return driver profile
     */
    DriverProfileResponse registerAsDriver(String userId, RegisterDriverRequest request);

    /**
     * Get driver profile
     *
     * @param userId user ID
     * @return driver profile details
     */
    DriverProfileResponse getDriverProfile(String userId);

    /**
     * Update driver status (available/unavailable)
     *
     * @param userId user ID
     * @param request status update details
     */
    void updateDriverStatus(String userId, UpdateDriverStatusRequest request);

    /**
     * Get driver dashboard with stats
     *
     * @param userId user ID
     * @return dashboard data
     */
    DriverDashboardResponse getDriverDashboard(String userId);
}
