package com.rideconnect.controller;

import com.rideconnect.dto.request.driver.RegisterDriverRequest;
import com.rideconnect.dto.request.driver.UpdateDriverStatusRequest;
import com.rideconnect.dto.response.driver.DriverDashboardResponse;
import com.rideconnect.dto.response.driver.DriverProfileResponse;
import com.rideconnect.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping("/register")
    public ResponseEntity<DriverProfileResponse> registerAsDriver(
            Authentication authentication,
            @Valid @RequestBody RegisterDriverRequest request) {
        String userId = authentication.getName();
        DriverProfileResponse response = driverService.registerAsDriver(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<DriverProfileResponse> getDriverProfile(Authentication authentication) {
        String userId = authentication.getName();
        DriverProfileResponse response = driverService.getDriverProfile(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/status")
    public ResponseEntity<Void> updateDriverStatus(
            Authentication authentication,
            @Valid @RequestBody UpdateDriverStatusRequest request) {
        String userId = authentication.getName();
        driverService.updateDriverStatus(userId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DriverDashboardResponse> getDriverDashboard(Authentication authentication) {
        String userId = authentication.getName();
        DriverDashboardResponse response = driverService.getDriverDashboard(userId);
        return ResponseEntity.ok(response);
    }
}
