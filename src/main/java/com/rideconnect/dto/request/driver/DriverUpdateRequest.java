package com.rideconnect.dto.request.driver;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverUpdateRequest {

    private String licenseNumber;

    private String vehicleType;

    private String vehicleBrand;

    private String vehicleModel;

    @Pattern(regexp = "^[0-9A-Z]{5,10}$", message = "Biển số xe không hợp lệ")
    private String vehiclePlate;

    private String currentStatus;

    private Boolean documentsVerified;

    private Boolean profileCompleted;
}

