package com.rideconnect.dto.response.driver;

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
public class DriverDashboardResponse {

    private long totalTrips;
    private long completedTrips;
    private long cancelledTrips;
    private BigDecimal totalEarnings;
    private Double averageRating;
    private List<EarningsSummary> recentEarnings;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EarningsSummary {
        private String date;
        private BigDecimal amount;
        private int tripCount;
    }
}
