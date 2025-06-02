package com.rideconnect.controller.admin;

import com.rideconnect.entity.Rating;
import com.rideconnect.entity.Trip;
import com.rideconnect.entity.User;
import com.rideconnect.service.RatingService;
import com.rideconnect.service.TripService;
import com.rideconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/ratings")
public class RatingAdminController {

    private final RatingService ratingService;
    private final UserService userService;
    private final TripService tripService;

    @Autowired
    public RatingAdminController(RatingService ratingService, UserService userService, TripService tripService) {
        this.ratingService = ratingService;
        this.userService = userService;
        this.tripService = tripService;
    }

    @GetMapping
    public ResponseEntity<List<Rating>> getAllRatings() {
        List<Rating> ratings = ratingService.findAll();
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rating> getRatingById(@PathVariable UUID id) {
        Rating rating = ratingService.findById(id);
        return ResponseEntity.ok(rating);
    }

    @GetMapping("/rated-user/{userId}")
    public ResponseEntity<List<Rating>> getRatingsByRatedUser(@PathVariable UUID userId) {
        User user = userService.findById(userId);
        List<Rating> ratings = ratingService.findByRatedUser(user);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/rater/{userId}")
    public ResponseEntity<List<Rating>> getRatingsByRater(@PathVariable UUID userId) {
        User user = userService.findById(userId);
        List<Rating> ratings = ratingService.findByRater(user);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<Rating>> getRatingsByTrip(@PathVariable UUID tripId) {
        Trip trip = tripService.findById(tripId);
        List<Rating> ratings = ratingService.findByTrip(trip);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/trip/{tripId}/specific")
    public ResponseEntity<Rating> getSpecificRating(
            @PathVariable UUID tripId,
            @RequestParam UUID raterId,
            @RequestParam UUID ratedUserId) {

        Trip trip = tripService.findById(tripId);
        User rater = userService.findById(raterId);
        User ratedUser = userService.findById(ratedUserId);

        Optional<Rating> rating = ratingService.findByTripAndRaterAndRatedUser(trip, rater, ratedUser);
        return rating.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/average/{userId}")
    public ResponseEntity<Double> getAverageRatingForUser(@PathVariable UUID userId) {
        User user = userService.findById(userId);
        Double averageRating = ratingService.getAverageRatingForUser(user);
        return ResponseEntity.ok(averageRating);
    }

    @GetMapping("/count/{userId}")
    public ResponseEntity<Long> getRatingCountForUser(@PathVariable UUID userId) {
        User user = userService.findById(userId);
        Long count = ratingService.countRatingsByRatedUser(user);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/create")
    public ResponseEntity<Rating> createRating(
            @RequestParam UUID tripId,
            @RequestParam UUID raterId,
            @RequestParam UUID ratedUserId,
            @RequestParam Integer ratingValue,
            @RequestParam(required = false) String comment) {

        Trip trip = tripService.findById(tripId);
        User rater = userService.findById(raterId);
        User ratedUser = userService.findById(ratedUserId);

        Rating rating = ratingService.createRating(trip, rater, ratedUser, ratingValue, comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(rating);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable UUID id) {
        ratingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
