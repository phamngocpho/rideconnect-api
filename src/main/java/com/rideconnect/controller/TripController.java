package com.rideconnect.controller;

import com.rideconnect.dto.request.trip.CreateTripRequest;
import com.rideconnect.dto.request.trip.UpdateTripStatusRequest;
import com.rideconnect.dto.response.trip.TripDetailsResponse;
import com.rideconnect.dto.response.trip.TripHistoryResponse;
import com.rideconnect.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @PostMapping
    public ResponseEntity<TripDetailsResponse> createTrip(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateTripRequest request) {
        String userId = userDetails.getUsername();
        TripDetailsResponse response = tripService.createTrip(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripDetailsResponse> getTripDetails(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID tripId) {
        String userId = userDetails.getUsername();
        TripDetailsResponse response = tripService.getTripDetails(userId, tripId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{tripId}/status")
    public ResponseEntity<TripDetailsResponse> updateTripStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID tripId,
            @Valid @RequestBody UpdateTripStatusRequest request) {
        String userId = userDetails.getUsername();
        TripDetailsResponse response = tripService.updateTripStatus(userId, tripId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<TripHistoryResponse> getTripHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        TripHistoryResponse response = tripService.getTripHistory(userId);
        return ResponseEntity.ok(response);
    }
}
