package com.rideconnect.service.impl;

import com.rideconnect.dto.request.location.LocationUpdateRequest;
import com.rideconnect.dto.request.location.NearbyDriversRequest;
import com.rideconnect.dto.response.location.NearbyDriversResponse;
import com.rideconnect.entity.DriverLocation;
import com.rideconnect.entity.LocationHistory;
import com.rideconnect.entity.User;
import com.rideconnect.exception.ResourceNotFoundException;
import com.rideconnect.repository.DriverLocationRepository;
import com.rideconnect.repository.DriverRepository;
import com.rideconnect.repository.LocationHistoryRepository;
import com.rideconnect.repository.UserRepository;
import com.rideconnect.security.CustomUserDetails;
import com.rideconnect.service.LocationService;
import com.rideconnect.util.LocationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgis.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final DriverLocationRepository driverLocationRepository;
    private final LocationHistoryRepository locationHistoryRepository;
    private final LocationUtils locationUtils;

    @Override
    @Transactional
    public void updateLocation(CustomUserDetails userDetails, LocationUpdateRequest request) {
        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getUserId().toString()));

        Point location = locationUtils.createPoint(request.getLatitude(), request.getLongitude());

        LocationHistory locationHistory = LocationHistory.builder()
                .user(user)
                .location(location)
                .heading(request.getHeading())
                .speed(request.getSpeed())
                .build();
        locationHistoryRepository.save(locationHistory);

        driverRepository.findById(userDetails.getUserId()).ifPresent(driver -> {
            DriverLocation driverLocation = driverLocationRepository.findById(driver.getDriverId())
                    .orElse(new DriverLocation());

            driverLocation.setDriver(driver);
            driverLocation.setCurrentLocation(location);
            driverLocation.setLastUpdated(ZonedDateTime.now());

            if (request.getIsAvailable() != null) {
                driverLocation.setIsAvailable(request.getIsAvailable());
            }

            driverLocationRepository.save(driverLocation);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public NearbyDriversResponse findNearbyDrivers(CustomUserDetails userDetails, NearbyDriversRequest request) {
        double radiusInMeters = request.getRadiusInKm() != null ? request.getRadiusInKm() * 1000 : 5000;

        List<Object[]> driversWithDistance = driverLocationRepository.findAvailableDriversWithinRadius(
                request.getLatitude(),
                request.getLongitude(),
                radiusInMeters,
                request.getVehicleType()
        );

        List<NearbyDriversResponse.DriverLocation> driverLocations = new ArrayList<>();

        for (Object[] row : driversWithDistance) {
            try {
                UUID driverId = (UUID) row[0];
                double distance = ((Number) row[1]).doubleValue();
                double latitude = ((Number) row[2]).doubleValue();
                double longitude = ((Number) row[3]).doubleValue();
                Float heading = row[4] != null ? ((Number) row[4]).floatValue() : null;
                String vehicleType = (String) row[5];
                String vehiclePlate = (String) row[6];

                NearbyDriversResponse.DriverLocation driverLocation = NearbyDriversResponse.DriverLocation.builder()
                        .driverId(driverId)
                        .latitude(latitude)
                        .longitude(longitude)
                        .heading(heading)
                        .vehicleType(vehicleType)
                        .vehiclePlate(vehiclePlate)
                        .distance(distance)
                        .estimatedArrivalTime(calculateEstimatedArrivalTime(distance))
                        .build();

                driverLocations.add(driverLocation);
            } catch (Exception e) {
                log.error("Error processing driver location row: {}", e.getMessage());
            }
        }

        return NearbyDriversResponse.builder()
                .drivers(driverLocations)
                .build();
    }

    private int calculateEstimatedArrivalTime(double distanceInMeters) {
        // Giả sử tốc độ trung bình là 30 km/h
        double speedInMetersPerSecond = 30 * 1000 / 3600.0;
        return (int) Math.ceil(distanceInMeters / speedInMetersPerSecond / 60); // Chuyển đổi thành phút
    }
}