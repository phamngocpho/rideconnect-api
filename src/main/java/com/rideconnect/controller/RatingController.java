package com.rideconnect.controller;

import com.rideconnect.dto.request.rating.CreateRatingRequest;
import com.rideconnect.dto.response.rating.RatingResponse;
import com.rideconnect.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/trips/{tripId}")
    public ResponseEntity<RatingResponse> rateTrip(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID tripId,
            @Valid @RequestBody CreateRatingRequest request) {
        String userId = userDetails.getUsername();
        RatingResponse response = ratingService.createRating(userId, tripId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/trips/{tripId}")
    public ResponseEntity<RatingResponse> getTripRating(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID tripId) {
        String userId = userDetails.getUsername();
        RatingResponse response = ratingService.getTripRating(userId, tripId);
        return ResponseEntity.ok(response);
    }
}
