package com.rideconnect.controller;

import com.rideconnect.dto.request.location.LocationUpdateRequest;
import com.rideconnect.dto.response.location.NearbyDriversResponse;
import com.rideconnect.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping("/update")
    public ResponseEntity<Void> updateLocation(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody LocationUpdateRequest request) {
        String userId = userDetails.getUsername();
        locationService.updateLocation(userId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/nearby-drivers")
    public ResponseEntity<NearbyDriversResponse> getNearbyDrivers(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "5000") double radius,
            @RequestParam String vehicleType) {
        String userId = userDetails.getUsername();
        NearbyDriversResponse response = locationService.findNearbyDrivers(userId, latitude, longitude, radius, vehicleType);
        return ResponseEntity.ok(response);
    }
}
