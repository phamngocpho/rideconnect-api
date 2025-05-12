package com.rideconnect.repository;

import com.rideconnect.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID> {
    Optional<Driver> findByLicenseNumber(String licenseNumber);
    Optional<Driver> findByVehiclePlate(String vehiclePlate);
    List<Driver> findByCurrentStatus(String status);
    List<Driver> findByDocumentsVerified(Boolean verified);
}