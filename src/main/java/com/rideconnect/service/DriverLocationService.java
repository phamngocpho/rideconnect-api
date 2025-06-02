package com.rideconnect.service;

import com.rideconnect.entity.Driver;
import com.rideconnect.entity.DriverLocation;
import org.postgis.Point;

import java.util.List;
import java.util.UUID;

public interface DriverLocationService {

    DriverLocation save(DriverLocation driverLocation);

    DriverLocation findById(UUID driverId);

    DriverLocation findByDriver(Driver driver);

    List<DriverLocation> findAll();

    List<DriverLocation> findByIsAvailable(Boolean isAvailable);

    List<DriverLocation> findAvailableDriversNearby(double longitude, double latitude, double radiusInMeters);

    void updateDriverLocation(UUID driverId, Point newLocation, Float heading);

    void updateDriverAvailability(UUID driverId, Boolean isAvailable);

    void delete(UUID driverId);
}
