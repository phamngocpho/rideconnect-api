package com.rideconnect.service.impl;

import com.rideconnect.dto.request.driver.DriverCreateRequest;
import com.rideconnect.dto.request.driver.DriverUpdateRequest;
import com.rideconnect.dto.response.driver.DriverResponse;
import com.rideconnect.dto.response.user.UserResponse;
import com.rideconnect.entity.Driver;
import com.rideconnect.entity.User;
import com.rideconnect.repository.DriverRepository;
import com.rideconnect.repository.UserRepository;
import com.rideconnect.service.DriverService;
import com.rideconnect.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    @Transactional
    public DriverResponse createDriver(UUID userId, DriverCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        Driver driver = Driver.builder()
                .driverId(userId)
                .user(user)
                .licenseNumber(request.getLicenseNumber())
                .vehicleType(request.getVehicleType())
                .vehicleBrand(request.getVehicleBrand())
                .vehicleModel(request.getVehicleModel())
                .vehiclePlate(request.getVehiclePlate())
                .documentsVerified(request.getDocumentsVerified())
                .profileCompleted(false)
                .build();

        Driver savedDriver = driverRepository.save(driver);
        return mapToDriverResponse(savedDriver);
    }

    @Override
    public DriverResponse getDriverById(UUID driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài xế với ID: " + driverId));
        return mapToDriverResponse(driver);
    }

    @Override
    public Page<DriverResponse> getAllDrivers(Pageable pageable) {
        return driverRepository.findAll(pageable)
                .map(this::mapToDriverResponse);
    }

    @Override
    public List<DriverResponse> getDriversByStatus(String status) {
        return driverRepository.findByCurrentStatus(status)
                .stream()
                .map(this::mapToDriverResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DriverResponse> getDriversByVerificationStatus(Boolean verified) {
        return driverRepository.findByDocumentsVerified(verified)
                .stream()
                .map(this::mapToDriverResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DriverResponse updateDriver(UUID driverId, DriverUpdateRequest request) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài xế với ID: " + driverId));

        if (request.getLicenseNumber() != null) {
            driver.setLicenseNumber(request.getLicenseNumber());
        }

        if (request.getVehicleType() != null) {
            driver.setVehicleType(request.getVehicleType());
        }

        if (request.getVehicleBrand() != null) {
            driver.setVehicleBrand(request.getVehicleBrand());
        }

        if (request.getVehicleModel() != null) {
            driver.setVehicleModel(request.getVehicleModel());
        }

        if (request.getVehiclePlate() != null) {
            driver.setVehiclePlate(request.getVehiclePlate());
        }

        if (request.getCurrentStatus() != null) {
            driver.setCurrentStatus(request.getCurrentStatus());
        }

        if (request.getDocumentsVerified() != null) {
            driver.setDocumentsVerified(request.getDocumentsVerified());
        }

        if (request.getProfileCompleted() != null) {
            driver.setProfileCompleted(request.getProfileCompleted());
        }

        Driver updatedDriver = driverRepository.save(driver);
        return mapToDriverResponse(updatedDriver);
    }

    @Override
    @Transactional
    public DriverResponse updateDriverStatus(UUID driverId, String status) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài xế với ID: " + driverId));

        driver.setCurrentStatus(status);
        Driver updatedDriver = driverRepository.save(driver);
        return mapToDriverResponse(updatedDriver);
    }

    @Override
    @Transactional
    public DriverResponse verifyDriverDocuments(UUID driverId, Boolean verified) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài xế với ID: " + driverId));

        driver.setDocumentsVerified(verified);
        Driver updatedDriver = driverRepository.save(driver);
        return mapToDriverResponse(updatedDriver);
    }

    @Override
    @Transactional
    public DriverResponse updateDriverRating(UUID driverId, BigDecimal rating) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài xế với ID: " + driverId));

        driver.setRating(rating);
        Driver updatedDriver = driverRepository.save(driver);
        return mapToDriverResponse(updatedDriver);
    }

    @Override
    @Transactional
    public DriverResponse incrementDriverTrips(UUID driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài xế với ID: " + driverId));

        driver.setTotalTrips(driver.getTotalTrips() + 1);
        Driver updatedDriver = driverRepository.save(driver);
        return mapToDriverResponse(updatedDriver);
    }

    @Override
    @Transactional
    public void deleteDriver(UUID driverId) {
        if (!driverRepository.existsById(driverId)) {
            throw new EntityNotFoundException("Không tìm thấy tài xế với ID: " + driverId);
        }
        driverRepository.deleteById(driverId);
    }

    private DriverResponse mapToDriverResponse(Driver driver) {
        UserResponse userResponse = userService.getUserById(driver.getDriverId());

        return DriverResponse.builder()
                .driverId(driver.getDriverId())
                .user(userResponse)
                .licenseNumber(driver.getLicenseNumber())
                .vehicleType(driver.getVehicleType())
                .vehicleBrand(driver.getVehicleBrand())
                .vehicleModel(driver.getVehicleModel())
                .vehiclePlate(driver.getVehiclePlate())
                .rating(driver.getRating())
                .totalTrips(driver.getTotalTrips())
                .currentStatus(driver.getCurrentStatus())
                .documentsVerified(driver.getDocumentsVerified())
                .profileCompleted(driver.getProfileCompleted())
                .build();
    }
}
