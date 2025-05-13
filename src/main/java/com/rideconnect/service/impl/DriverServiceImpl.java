package com.rideconnect.service.impl;

import com.rideconnect.dto.request.driver.RegisterDriverRequest;
import com.rideconnect.dto.request.driver.UpdateDriverStatusRequest;
import com.rideconnect.dto.response.driver.DriverDashboardResponse;
import com.rideconnect.dto.response.driver.DriverProfileResponse;
import com.rideconnect.entity.Driver;
import com.rideconnect.exception.DriverNotFoundException;
import com.rideconnect.repository.DriverRepository;
import com.rideconnect.repository.UserRepository;
import com.rideconnect.service.DriverService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;

    @Override
    public DriverProfileResponse registerAsDriver(String userId, RegisterDriverRequest request) {
        // Triển khai sau
        return null;
    }

    @Override
    public DriverProfileResponse getDriverProfile(String userId) {
        // Triển khai sau
        return null;
    }

    @Override
    @Transactional
    public void updateDriverStatus(String userId, UpdateDriverStatusRequest request) {
        log.info("Updating driver status for userId: {}, isAvailable: {}", userId, request.getIsAvailable());

        UUID driverId = UUID.fromString(userId);
        Optional<Driver> driverOptional = driverRepository.findByUserUserId(driverId);

        if (driverOptional.isEmpty()) {
            log.error("Driver not found with userId: {}", userId);
            throw new DriverNotFoundException("Driver not found with userId: " + userId);
        }

        Driver driver = driverOptional.get();
        String newStatus = request.getIsAvailable() ? "online" : "offline";

        int updated = driverRepository.updateDriverStatus(driver.getDriverId(), newStatus);

        if (updated == 0) {
            log.error("Failed to update driver status for driverId: {}", driver.getDriverId());
            throw new RuntimeException("Failed to update driver status");
        }

        log.info("Driver status updated successfully to: {}", newStatus);
    }

    @Override
    public DriverDashboardResponse getDriverDashboard(String userId) {
        // Triển khai sau
        return null;
    }
}
