package com.rideconnect.controller;

import com.rideconnect.dto.request.location.LocationUpdateRequest;
import com.rideconnect.dto.request.location.NearbyDriversRequest;
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
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false, defaultValue = "5.0") Double radiusInKm,
            @RequestParam String vehicleType) {

        String userId = userDetails.getUsername();

        // Create request object from parameters
        NearbyDriversRequest request = new NearbyDriversRequest();
        request.setLatitude(latitude);
        request.setLongitude(longitude);
        request.setRadiusInKm(radiusInKm);
        request.setVehicleType(vehicleType);

        NearbyDriversResponse response = locationService.findNearbyDrivers(userId, request);
        return ResponseEntity.ok(response);
    }
}