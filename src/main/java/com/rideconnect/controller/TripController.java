package com.rideconnect.controller;

import com.rideconnect.dto.request.trip.CreateTripRequest;
import com.rideconnect.dto.request.trip.UpdateTripStatusRequest;
import com.rideconnect.dto.response.trip.TripDetailsResponse;
import com.rideconnect.dto.response.trip.TripHistoryResponse;
import com.rideconnect.security.CustomUserDetails;
import com.rideconnect.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @PostMapping
    public ResponseEntity<TripDetailsResponse> createTrip(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateTripRequest request) {

        TripDetailsResponse response = tripService.createTrip(userDetails, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripDetailsResponse> getTripDetails(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID tripId) {

        TripDetailsResponse response = tripService.getTripDetails(userDetails, tripId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{tripId}/status")
    public ResponseEntity<TripDetailsResponse> updateTripStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID tripId,
            @Valid @RequestBody UpdateTripStatusRequest request) {

        TripDetailsResponse response = tripService.updateTripStatus(userDetails, tripId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<TripHistoryResponse> getTripHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        TripHistoryResponse response = tripService.getTripHistory(userDetails);
        return ResponseEntity.ok(response);
    }
}