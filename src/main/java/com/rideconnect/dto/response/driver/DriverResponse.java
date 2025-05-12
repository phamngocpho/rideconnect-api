package com.rideconnect.dto.response.driver;

import com.rideconnect.dto.response.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponse {
    private UUID driverId;
    private UserResponse user;
    private String licenseNumber;
    private String vehicleType;
    private String vehicleBrand;
    private String vehicleModel;
    private String vehiclePlate;
    private BigDecimal rating;
    private Integer totalTrips;
    private String currentStatus;
    private Boolean documentsVerified;
    private Boolean profileCompleted;
}
