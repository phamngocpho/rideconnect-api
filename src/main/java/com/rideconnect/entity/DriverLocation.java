package com.rideconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.postgis.Point;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "driver_locations")
public class DriverLocation {

    @Id
    @Column(name = "driver_id")
    private UUID driverId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", referencedColumnName = "driver_id")
    @MapsId
    private Driver driver;

    @Column(name = "current_location", columnDefinition = "geography(Point)")
    private Point currentLocation;

    @Column(name = "last_updated")
    private ZonedDateTime lastUpdated;

    @Column(name = "is_available")
    private Boolean isAvailable;

    @Column(name = "heading")
    private Float heading;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = ZonedDateTime.now();
        if (this.isAvailable == null) {
            this.isAvailable = true;
        }
    }
}
