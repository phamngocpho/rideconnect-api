package com.rideconnect.service;

import com.rideconnect.dto.request.driver.RegisterDriverRequest;
import com.rideconnect.dto.request.driver.UpdateDriverStatusRequest;
import com.rideconnect.dto.response.driver.DriverDashboardResponse;
import com.rideconnect.dto.response.driver.DriverProfileResponse;
import com.rideconnect.security.CustomUserDetails;

public interface DriverService {
    DriverProfileResponse registerAsDriver(CustomUserDetails userDetails, RegisterDriverRequest request);

    DriverProfileResponse getDriverProfile(CustomUserDetails userDetails);

    void updateDriverStatus(CustomUserDetails userDetails, UpdateDriverStatusRequest request);

    DriverDashboardResponse getDriverDashboard(CustomUserDetails userDetails);
}
