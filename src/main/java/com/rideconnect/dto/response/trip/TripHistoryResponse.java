package com.rideconnect.dto.response.trip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripHistoryResponse {

    private List<TripSummary> trips;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TripSummary {
        private UUID tripId;
        private String pickupAddress;
        private String dropoffAddress;
        private String status;
        private ZonedDateTime createdAt;
        private ZonedDateTime completedAt;
        private BigDecimal fare;
        private String vehicleType;
        private Integer rating;
    }
}
