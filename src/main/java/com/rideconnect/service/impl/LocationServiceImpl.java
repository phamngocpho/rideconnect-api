package com.rideconnect.service.impl;

import com.rideconnect.dto.request.location.LocationUpdateRequest;
import com.rideconnect.dto.response.location.NearbyDriversResponse;
import com.rideconnect.entity.DriverLocation;
import com.rideconnect.entity.LocationHistory;
import com.rideconnect.entity.User;
import com.rideconnect.exception.ResourceNotFoundException;
import com.rideconnect.repository.DriverLocationRepository;
import com.rideconnect.repository.DriverRepository;
import com.rideconnect.repository.LocationHistoryRepository;
import com.rideconnect.repository.UserRepository;
import com.rideconnect.service.LocationService;
import com.rideconnect.util.LocationUtils;
import lombok.RequiredArgsConstructor;
import org.postgis.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public void updateLocation(String userId, LocationUpdateRequest request) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Create a location point
        Point location = locationUtils.createPoint(request.getLatitude(), request.getLongitude());

        // Save to location history
        LocationHistory locationHistory = LocationHistory.builder()
                .user(user)
                .location(location)
                .heading(request.getHeading())
                .speed(request.getSpeed())
                .build();
        locationHistoryRepository.save(locationHistory);

        // If a user is a driver, update the current location
        driverRepository.findById(UUID.fromString(userId)).ifPresent(driver -> {
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
    public NearbyDriversResponse findNearbyDrivers(String userId, double latitude, double longitude, double radius, String vehicleType) {
        // Tìm tài xế gần đó
        List<Object[]> driversWithDistance = driverLocationRepository.findAvailableDriversWithinRadius(
                latitude, longitude, radius, vehicleType);

        List<NearbyDriversResponse.DriverLocation> driverLocations = new ArrayList<>();

        for (Object[] row : driversWithDistance) {
            UUID driverId = (UUID) row[0];
            double driverLongitude = ((Number) row[1]).doubleValue();
            double driverLatitude = ((Number) row[2]).doubleValue();
            Float heading = row[3] != null ? ((Number) row[3]).floatValue() : null;
            String vType = (String) row[4];
            String vPlate = (String) row[5];
            double distance = ((Number) row[6]).doubleValue();

            // Tính thời gian ước tính đến nơi (giả sử tốc độ trung bình 30 km/h)
            int estimatedArrivalTime = (int) (distance / 30.0 * 3.6); // chuyển đổi từ giây sang phút

            driverLocations.add(NearbyDriversResponse.DriverLocation.builder()
                    .driverId(driverId)
                    .latitude(driverLatitude)
                    .longitude(driverLongitude)
                    .heading(heading)
                    .vehicleType(vType)
                    .vehiclePlate(vPlate)
                    .distance(distance)
                    .estimatedArrivalTime(estimatedArrivalTime)
                    .build());
        }

        return NearbyDriversResponse.builder()
                .drivers(driverLocations)
                .build();
    }
}
