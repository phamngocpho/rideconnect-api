package com.rideconnect.dto.response.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyDriversResponse {

    private List<DriverLocation> drivers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DriverLocation {
        private UUID driverId;
        private double latitude;
        private double longitude;
        private Float heading;
        private String vehicleType;
        private String vehiclePlate;
        private double distance;
        private int estimatedArrivalTime;
    }
}
