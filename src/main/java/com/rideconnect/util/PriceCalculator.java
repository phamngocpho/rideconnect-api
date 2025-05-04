package com.rideconnect.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PriceCalculator {

    @Value("${rideconnect.pricing.base-fare}")
    private BigDecimal baseFare;

    @Value("${rideconnect.pricing.per-km}")
    private BigDecimal perKm;

    @Value("${rideconnect.pricing.per-minute}")
    private BigDecimal perMinute;

    @Value("${rideconnect.pricing.minimum-fare}")
    private BigDecimal minimumFare;

    private static final BigDecimal MOTORCYCLE_MULTIPLIER = new BigDecimal("0.8");
    private static final BigDecimal CAR_MULTIPLIER = new BigDecimal("1.0");
    private static final BigDecimal SUV_MULTIPLIER = new BigDecimal("1.2");
    private static final BigDecimal PREMIUM_MULTIPLIER = new BigDecimal("1.5");

    /**
     * Calculate the estimated fare for a trip
     *
     * @param distanceInKm distance in kilometers
     * @param durationInMinutes duration in minutes
     * @param vehicleType type of vehicle (motorcycle, car, suv, premium)
     * @return estimated fare
     */
    public BigDecimal calculateFare(float distanceInKm, int durationInMinutes, String vehicleType) {
        BigDecimal distanceCost = perKm.multiply(BigDecimal.valueOf(distanceInKm));
        BigDecimal timeCost = perMinute.multiply(BigDecimal.valueOf(durationInMinutes));

        BigDecimal fare = baseFare.add(distanceCost).add(timeCost);

        // Apply vehicle type multiplier
        BigDecimal multiplier = getVehicleMultiplier(vehicleType);
        fare = fare.multiply(multiplier);

        // Ensure minimum fare
        if (fare.compareTo(minimumFare) < 0) {
            fare = minimumFare;
        }

        return fare.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getVehicleMultiplier(String vehicleType) {
        if (vehicleType == null) {
            return CAR_MULTIPLIER;
        }

        return switch (vehicleType.toLowerCase()) {
            case "motorcycle" -> MOTORCYCLE_MULTIPLIER;
            case "suv" -> SUV_MULTIPLIER;
            case "premium" -> PREMIUM_MULTIPLIER;
            default -> CAR_MULTIPLIER;
        };
    }
}
