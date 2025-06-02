package com.rideconnect.repository;

import com.rideconnect.entity.Driver;
import com.rideconnect.entity.DriverLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DriverLocationRepository extends JpaRepository<DriverLocation, UUID> {

    List<DriverLocation> findByIsAvailable(Boolean isAvailable);

    @Query(value = "SELECT * FROM driver_location dl WHERE ST_DWithin(dl.current_location, ST_MakePoint(:longitude, :latitude)::geography, :radiusInMeters) AND dl.is_available = true", nativeQuery = true)
    List<DriverLocation> findAvailableDriversNearby(@Param("longitude") double longitude, @Param("latitude") double latitude, @Param("radiusInMeters") double radiusInMeters);

    DriverLocation findByDriver(Driver driver);
}
