package com.rideconnect.dto.response.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDashboardResponse {

    private long totalTrips;
    private long completedTrips;
    private long cancelledTrips;
    private BigDecimal totalSpent;
    private List<TripSummary> recentTrips;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TripSummary {
        private String date;
        private String pickupAddress;
        private String dropoffAddress;
        private BigDecimal fare;
        private String status;
    }
}
