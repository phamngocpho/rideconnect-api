package com.rideconnect.service;

import com.rideconnect.dto.request.driver.DriverCreateRequest;
import com.rideconnect.dto.request.driver.DriverUpdateRequest;
import com.rideconnect.dto.response.driver.DriverResponse;
import com.rideconnect.entity.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface DriverService {
    Driver findById(UUID driverId);
    DriverResponse createDriver(UUID userId, DriverCreateRequest request);
    DriverResponse getDriverById(UUID driverId);
    Page<DriverResponse> getAllDrivers(Pageable pageable);
    List<DriverResponse> getDriversByStatus(String status);
    List<DriverResponse> getDriversByVerificationStatus(Boolean verified);
    DriverResponse updateDriver(UUID driverId, DriverUpdateRequest request);
    DriverResponse updateDriverStatus(UUID driverId, String status);
    DriverResponse verifyDriverDocuments(UUID driverId, Boolean verified);
    DriverResponse updateDriverRating(UUID driverId, BigDecimal rating);
    DriverResponse incrementDriverTrips(UUID driverId);
    void deleteDriver(UUID driverId);
}
