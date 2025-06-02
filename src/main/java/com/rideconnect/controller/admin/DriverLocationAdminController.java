package com.rideconnect.controller.admin;

import com.rideconnect.entity.Driver;
import com.rideconnect.entity.DriverLocation;
import com.rideconnect.service.DriverLocationService;
import com.rideconnect.service.DriverService;
import org.postgis.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/driver-locations")
public class DriverLocationAdminController {

    private final DriverLocationService driverLocationService;
    private final DriverService driverService;

    @Autowired
    public DriverLocationAdminController(DriverLocationService driverLocationService, DriverService driverService) {
        this.driverLocationService = driverLocationService;
        this.driverService = driverService;
    }

    @GetMapping
    public ResponseEntity<List<DriverLocation>> getAllDriverLocations() {
        List<DriverLocation> locations = driverLocationService.findAll();
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverLocation> getDriverLocationById(@PathVariable UUID id) {
        DriverLocation location = driverLocationService.findById(id);
        return ResponseEntity.ok(location);
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<DriverLocation> getDriverLocationByDriver(@PathVariable UUID driverId) {
        Driver driver = driverService.findById(driverId);
        DriverLocation location = driverLocationService.findByDriver(driver);
        return ResponseEntity.ok(location);
    }

    @GetMapping("/available")
    public ResponseEntity<List<DriverLocation>> getAvailableDriverLocations() {
        List<DriverLocation> locations = driverLocationService.findByIsAvailable(true);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<DriverLocation>> getAvailableDriversNearby(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam double radiusInMeters) {

        List<DriverLocation> locations = driverLocationService.findAvailableDriversNearby(
                longitude, latitude, radiusInMeters);
        return ResponseEntity.ok(locations);
    }

    @PutMapping("/{driverId}/update-location")
    public ResponseEntity<Void> updateDriverLocation(
            @PathVariable UUID driverId,
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam(required = false) Float heading) {

        Point location = new Point(longitude, latitude);
        driverLocationService.updateDriverLocation(driverId, location, heading);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{driverId}/update-availability")
    public ResponseEntity<Void> updateDriverAvailability(
            @PathVariable UUID driverId,
            @RequestParam boolean isAvailable) {

        driverLocationService.updateDriverAvailability(driverId, isAvailable);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriverLocation(@PathVariable UUID id) {
        driverLocationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
