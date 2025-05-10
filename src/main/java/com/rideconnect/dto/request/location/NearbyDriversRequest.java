package com.rideconnect.dto.request.location;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NearbyDriversRequest {

    @NotNull(message = "Latitude cannot be null")
    private Double latitude;

    @NotNull(message = "Longitude cannot be null")
    private Double longitude;

    private Double radiusInKm; // Có thể null, có giá trị mặc định ở backend

    @NotNull(message = "Vehicle type cannot be null")
    private String vehicleType;
}