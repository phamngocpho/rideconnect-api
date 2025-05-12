package com.rideconnect.controller;

import com.rideconnect.dto.request.location.LocationUpdateRequest;
import com.rideconnect.dto.request.location.NearbyDriversRequest;
import com.rideconnect.dto.response.location.NearbyDriversResponse;
import com.rideconnect.security.CustomUserDetails;
import com.rideconnect.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping("/update")
    public ResponseEntity<Void> updateLocation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody LocationUpdateRequest request) {
        locationService.updateLocation(userDetails, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/nearby-drivers")
    public ResponseEntity<NearbyDriversResponse> getNearbyDrivers(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody NearbyDriversRequest request) {
        NearbyDriversResponse response = locationService.findNearbyDrivers(userDetails, request);
        return ResponseEntity.ok(response);
    }
}
