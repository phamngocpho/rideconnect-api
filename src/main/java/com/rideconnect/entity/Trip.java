package com.rideconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.postgis.LineString;
import org.postgis.Point;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "trip_id", updatable = false, nullable = false)
    private UUID tripId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @Column(name = "pickup_location", columnDefinition = "geography(Point)")
    private Point pickupLocation;

    @Column(name = "dropoff_location", columnDefinition = "geography(Point)")
    private Point dropoffLocation;

    @Column(name = "pickup_address")
    private String pickupAddress;

    @Column(name = "dropoff_address")
    private String dropoffAddress;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "vehicle_type", nullable = false)
    private String vehicleType;

    @Column(name = "estimated_distance")
    private Float estimatedDistance;

    @Column(name = "estimated_duration")
    private Integer estimatedDuration;

    @Column(name = "estimated_fare")
    private BigDecimal estimatedFare;

    @Column(name = "actual_distance")
    private Float actualDistance;

    @Column(name = "actual_duration")
    private Integer actualDuration;

    @Column(name = "actual_fare")
    private BigDecimal actualFare;

    @Column(name = "route", columnDefinition = "geography(LineString)")
    private LineString route;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    // Thêm thuộc tính acceptedAt
    @Column(name = "accepted_at")
    private ZonedDateTime acceptedAt;

    @Column(name = "started_at")
    private ZonedDateTime startedAt;

    @Column(name = "completed_at")
    private ZonedDateTime completedAt;

    @Column(name = "cancelled_at")
    private ZonedDateTime cancelledAt;

    @Column(name = "cancelled_by")
    private UUID cancelledBy;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    // Thêm quan hệ với bảng ratings
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Rating> ratings;

    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now();
    }
}
