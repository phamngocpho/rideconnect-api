package com.rideconnect.dto.response.trip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripDetailsResponse {

    private UUID tripId;
    private UUID customerId;
    private String customerName;
    private String customerPhone;
    private UUID driverId;
    private String driverName;
    private String driverPhone;
    private String vehicleType;
    private String vehiclePlate;
    private Double pickupLatitude;
    private Double pickupLongitude;
    private String pickupAddress;
    private Double dropoffLatitude;
    private Double dropoffLongitude;
    private String dropoffAddress;
    private String status;
    private Float estimatedDistance;
    private Integer estimatedDuration;
    private BigDecimal estimatedFare;
    private BigDecimal actualFare;
    private ZonedDateTime createdAt;
    private ZonedDateTime startedAt;
    private ZonedDateTime completedAt;
    private ZonedDateTime cancelledAt;
    private String cancellationReason;
    private DriverLocationDto driverLocation;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DriverLocationDto {
        private Double latitude;
        private Double longitude;
        private Float heading;
        private ZonedDateTime lastUpdated;
    }
}
