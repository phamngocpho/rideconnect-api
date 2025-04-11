package com.rideconnect.repository;

import com.rideconnect.entity.DriverLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriverLocationRepository extends JpaRepository<DriverLocation, UUID> {

    Optional<DriverLocation> findByDriverDriverId(UUID driverId);

    @Query("SELECT dl FROM DriverLocation dl WHERE dl.isAvailable = true")
    List<DriverLocation> findAllAvailableDrivers();

    @Query(value = "SELECT * FROM find_nearest_drivers(ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography, :radius, :vehicleType)",
            nativeQuery = true)
    List<Object[]> findAvailableDriversWithinRadius(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radius") double radius,
            @Param("vehicleType") String vehicleType);
}
