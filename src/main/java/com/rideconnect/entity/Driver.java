package com.rideconnect.entity;

import jakarta.persistence.*;
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
@Entity
@Table(name = "drivers")
public class Driver {

    @Id
    @Column(name = "driver_id")
    private UUID driverId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", referencedColumnName = "user_id")
    @MapsId
    private User user;

    @Column(name = "license_number", unique = true, nullable = false)
    private String licenseNumber;

    @Column(name = "vehicle_type", nullable = false)
    private String vehicleType;

    @Column(name = "vehicle_brand")
    private String vehicleBrand;

    @Column(name = "vehicle_model")
    private String vehicleModel;

    @Column(name = "vehicle_plate", unique = true, nullable = false)
    private String vehiclePlate;

    @Column(name = "rating", precision = 3, scale = 2)
    private BigDecimal rating;

    @Column(name = "total_trips")
    private Integer totalTrips;

    @Column(name = "current_status")
    private String currentStatus;

    @Column(name = "documents_verified")
    private Boolean documentsVerified;

    @PrePersist
    protected void onCreate() {
        if (this.rating == null) {
            this.rating = new BigDecimal("5.0");
        }
        if (this.totalTrips == null) {
            this.totalTrips = 0;
        }
        if (this.currentStatus == null) {
            this.currentStatus = "offline";
        }
        if (this.documentsVerified == null) {
            this.documentsVerified = false;
        }
    }
}
