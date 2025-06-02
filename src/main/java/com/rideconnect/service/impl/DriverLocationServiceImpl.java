package com.rideconnect.service.impl;

import com.rideconnect.entity.Driver;
import com.rideconnect.entity.DriverLocation;
import com.rideconnect.repository.DriverLocationRepository;
import com.rideconnect.repository.DriverRepository;
import com.rideconnect.service.DriverLocationService;
import org.postgis.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DriverLocationServiceImpl implements DriverLocationService {

    private final DriverLocationRepository driverLocationRepository;
    private final DriverRepository driverRepository;

    @Autowired
    public DriverLocationServiceImpl(DriverLocationRepository driverLocationRepository, DriverRepository driverRepository) {
        this.driverLocationRepository = driverLocationRepository;
        this.driverRepository = driverRepository;
    }

    @Override
    @Transactional
    public DriverLocation save(DriverLocation driverLocation) {
        return driverLocationRepository.save(driverLocation);
    }

    @Override
    public DriverLocation findById(UUID driverId) {
        return driverLocationRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver location not found with id: " + driverId));
    }

    @Override
    public DriverLocation findByDriver(Driver driver) {
        return driverLocationRepository.findByDriver(driver);
    }

    @Override
    public List<DriverLocation> findAll() {
        return driverLocationRepository.findAll();
    }

    @Override
    public List<DriverLocation> findByIsAvailable(Boolean isAvailable) {
        return driverLocationRepository.findByIsAvailable(isAvailable);
    }

    @Override
    public List<DriverLocation> findAvailableDriversNearby(double longitude, double latitude, double radiusInMeters) {
        return driverLocationRepository.findAvailableDriversNearby(longitude, latitude, radiusInMeters);
    }

    @Override
    @Transactional
    public void updateDriverLocation(UUID driverId, Point newLocation, Float heading) {
        DriverLocation driverLocation = findById(driverId);
        driverLocation.setCurrentLocation(newLocation);
        driverLocation.setHeading(heading);
        driverLocationRepository.save(driverLocation);
    }

    @Override
    @Transactional
    public void updateDriverAvailability(UUID driverId, Boolean isAvailable) {
        DriverLocation driverLocation = findById(driverId);
        driverLocation.setIsAvailable(isAvailable);
        driverLocationRepository.save(driverLocation);
    }

    @Override
    @Transactional
    public void delete(UUID driverId) {
        driverLocationRepository.deleteById(driverId);
    }
}
