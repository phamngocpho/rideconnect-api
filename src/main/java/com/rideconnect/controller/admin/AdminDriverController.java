package com.rideconnect.controller.admin;

import com.rideconnect.dto.request.driver.DriverCreateRequest;
import com.rideconnect.dto.request.driver.DriverUpdateRequest;
import com.rideconnect.dto.response.driver.DriverResponse;
import com.rideconnect.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/drivers")
@RequiredArgsConstructor
public class AdminDriverController {

    private final DriverService driverService;

    @PostMapping("/{userId}")
    public ResponseEntity<DriverResponse> createDriver(
            @PathVariable UUID userId,
            @Valid @RequestBody DriverCreateRequest request) {
        return new ResponseEntity<>(driverService.createDriver(userId, request), HttpStatus.CREATED);
    }

    @GetMapping("/{driverId}")
    public ResponseEntity<DriverResponse> getDriverById(@PathVariable UUID driverId) {
        return ResponseEntity.ok(driverService.getDriverById(driverId));
    }

    @GetMapping
    public ResponseEntity<Page<DriverResponse>> getAllDrivers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rating") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(driverService.getAllDrivers(pageable));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<DriverResponse>> getDriversByStatus(@PathVariable String status) {
        return ResponseEntity.ok(driverService.getDriversByStatus(status));
    }

    @GetMapping("/verification")
    public ResponseEntity<List<DriverResponse>> getDriversByVerificationStatus(@RequestParam Boolean verified) {
        return ResponseEntity.ok(driverService.getDriversByVerificationStatus(verified));
    }

    @PutMapping("/{driverId}")
    public ResponseEntity<DriverResponse> updateDriver(
            @PathVariable UUID driverId,
            @Valid @RequestBody DriverUpdateRequest request) {
        return ResponseEntity.ok(driverService.updateDriver(driverId, request));
    }

    @PatchMapping("/{driverId}/status")
    public ResponseEntity<DriverResponse> updateDriverStatus(
            @PathVariable UUID driverId,
            @RequestParam String status) {
        return ResponseEntity.ok(driverService.updateDriverStatus(driverId, status));
    }

    @PatchMapping("/{driverId}/verification")
    public ResponseEntity<DriverResponse> verifyDriverDocuments(
            @PathVariable UUID driverId,
            @RequestParam Boolean verified) {
        return ResponseEntity.ok(driverService.verifyDriverDocuments(driverId, verified));
    }

    @PatchMapping("/{driverId}/rating")
    public ResponseEntity<DriverResponse> updateDriverRating(
            @PathVariable UUID driverId,
            @RequestParam BigDecimal rating) {
        return ResponseEntity.ok(driverService.updateDriverRating(driverId, rating));
    }

    @PatchMapping("/{driverId}/increment-trips")
    public ResponseEntity<DriverResponse> incrementDriverTrips(@PathVariable UUID driverId) {
        return ResponseEntity.ok(driverService.incrementDriverTrips(driverId));
    }

    @DeleteMapping("/{driverId}")
    public ResponseEntity<Void> deleteDriver(@PathVariable UUID driverId) {
        driverService.deleteDriver(driverId);
        return ResponseEntity.noContent().build();
    }
}
