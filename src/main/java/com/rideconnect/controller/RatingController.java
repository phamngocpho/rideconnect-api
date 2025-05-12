package com.rideconnect.controller;

import com.rideconnect.dto.request.rating.CreateRatingRequest;
import com.rideconnect.dto.response.rating.RatingResponse;
import com.rideconnect.security.CustomUserDetails;
import com.rideconnect.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/trips/{tripId}")
    public ResponseEntity<RatingResponse> rateTrip(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID tripId,
            @Valid @RequestBody CreateRatingRequest request) {
        RatingResponse response = ratingService.createRating(userDetails, tripId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/trips/{tripId}")
    public ResponseEntity<RatingResponse> getTripRating(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID tripId) {
        RatingResponse response = ratingService.getTripRating(userDetails, tripId);
        return ResponseEntity.ok(response);
    }
}