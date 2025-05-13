package com.rideconnect.service.impl;

import com.rideconnect.dto.request.driver.RegisterDriverRequest;
import com.rideconnect.dto.request.driver.UpdateDriverStatusRequest;
import com.rideconnect.dto.response.driver.DriverDashboardResponse;
import com.rideconnect.dto.response.driver.DriverProfileResponse;
import com.rideconnect.entity.Driver;
import com.rideconnect.exception.DriverNotFoundException;
import com.rideconnect.repository.DriverRepository;
import com.rideconnect.repository.UserRepository;
import com.rideconnect.security.CustomUserDetails;
import com.rideconnect.service.DriverService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;

    @Override
    public DriverProfileResponse registerAsDriver(CustomUserDetails userDetails, RegisterDriverRequest request) {
        UUID userId = userDetails.getUserId();
        log.info("Registering user {} as driver", userId);

        // Triển khai sau
        return null;
    }

    @Override
    public DriverProfileResponse getDriverProfile(CustomUserDetails userDetails) {
        UUID userId = userDetails.getUserId();
        log.info("Getting driver profile for user {}", userId);

        // Triển khai sau
        return null;
    }

    @Override
    @Transactional
    public void updateDriverStatus(CustomUserDetails userDetails, UpdateDriverStatusRequest request) {
        UUID userId = userDetails.getUserId();
        log.info("Updating driver status for userId: {}, status: {}", userId, request.getIsAvailable());

        Optional<Driver> driverOptional = driverRepository.findByUserUserId(userId);

        if (driverOptional.isEmpty()) {
            log.error("Driver not found with userId: {}", userId);
            throw new DriverNotFoundException("Driver not found with userId: " + userId);
        }

        Driver driver = driverOptional.get();
        String newStatus = request.getIsAvailable();

        // Kiểm tra xem status có hợp lệ không
        if (!Arrays.asList("online", "offline", "busy").contains(newStatus)) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }

        int updated = driverRepository.updateDriverStatus(driver.getDriverId(), newStatus);

        if (updated == 0) {
            log.error("Failed to update driver status for driverId: {}", driver.getDriverId());
            throw new RuntimeException("Failed to update driver status");
        }

        log.info("Driver status updated successfully to: {}", newStatus);
    }

    @Override
    public DriverDashboardResponse getDriverDashboard(CustomUserDetails userDetails) {
        UUID userId = userDetails.getUserId();
        log.info("Getting driver dashboard for user {}", userId);

        // Triển khai sau
        return null;
    }
}