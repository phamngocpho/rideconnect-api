package com.rideconnect.controller.admin;

import com.rideconnect.entity.LocationHistory;
import com.rideconnect.entity.User;
import com.rideconnect.service.LocationHistoryService;
import com.rideconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/location-history")
public class LocationHistoryAdminController {

    private final LocationHistoryService locationHistoryService;
    private final UserService userService;

    @Autowired
    public LocationHistoryAdminController(LocationHistoryService locationHistoryService, UserService userService) {
        this.locationHistoryService = locationHistoryService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<LocationHistory>> getAllLocationHistory() {
        List<LocationHistory> history = locationHistoryService.findAll();
        return ResponseEntity.ok(history);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationHistory> getLocationHistoryById(@PathVariable UUID id) {
        LocationHistory history = locationHistoryService.findById(id);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LocationHistory>> getLocationHistoryByUser(@PathVariable UUID userId) {
        User user = userService.findById(userId);
        List<LocationHistory> history = locationHistoryService.findByUser(user);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<List<LocationHistory>> getRecentLocationHistoryByUser(@PathVariable UUID userId) {
        User user = userService.findById(userId);
        List<LocationHistory> history = locationHistoryService.findByUserOrderByRecordedAtDesc(user);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<LocationHistory>> getLocationHistoryByTrip(@PathVariable UUID tripId) {
        List<LocationHistory> history = locationHistoryService.findByTripId(tripId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/trip/{tripId}/chronological")
    public ResponseEntity<List<LocationHistory>> getChronologicalLocationHistoryByTrip(@PathVariable UUID tripId) {
        List<LocationHistory> history = locationHistoryService.findByTripIdOrderByRecordedAtAsc(tripId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/user/{userId}/timeframe")
    public ResponseEntity<List<LocationHistory>> getLocationHistoryByUserAndTimeframe(
            @PathVariable UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime end) {

        User user = userService.findById(userId);
        List<LocationHistory> history = locationHistoryService.findByUserAndRecordedAtBetween(user, start, end);
        return ResponseEntity.ok(history);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocationHistory(@PathVariable UUID id) {
        locationHistoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
