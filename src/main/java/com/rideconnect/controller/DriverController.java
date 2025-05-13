package com.rideconnect.controller;

import com.rideconnect.dto.request.driver.RegisterDriverRequest;
import com.rideconnect.dto.request.driver.UpdateDriverStatusRequest;
import com.rideconnect.dto.response.driver.DriverDashboardResponse;
import com.rideconnect.dto.response.driver.DriverProfileResponse;
import com.rideconnect.security.CustomUserDetails;
import com.rideconnect.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping("/register")
    public ResponseEntity<DriverProfileResponse> registerAsDriver(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody RegisterDriverRequest request) {
        DriverProfileResponse response = driverService.registerAsDriver(userDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<DriverProfileResponse> getDriverProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        DriverProfileResponse response = driverService.getDriverProfile(userDetails);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/status")
    public ResponseEntity<Void> updateDriverStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateDriverStatusRequest request) {
        driverService.updateDriverStatus(userDetails, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DriverDashboardResponse> getDriverDashboard(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        DriverDashboardResponse response = driverService.getDriverDashboard(userDetails);
        return ResponseEntity.ok(response);
    }
}