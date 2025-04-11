package com.rideconnect.repository;

import com.rideconnect.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID> {
    List<Driver> findByVehicleType(String vehicleType);

    @Query("SELECT d FROM Driver d WHERE d.currentStatus = 'online'")
    List<Driver> findAllAvailableDrivers();

    boolean existsByLicenseNumber(String licenseNumber);
    boolean existsByVehiclePlate(String vehiclePlate);
}
