package com.rideconnect.dto.response.driver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverProfileResponse {

    private UUID driverId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String avatarUrl;
    private String vehicleType;
    private String vehicleModel;
    private String vehicleColor;
    private String vehiclePlate;
    private String licenseNumber;
    private LocalDate licenseExpiry;
    private Boolean isVerified;
    private Boolean isActive;
    private Boolean isAvailable;
    private Double averageRating;
    private ZonedDateTime createdAt;
}
