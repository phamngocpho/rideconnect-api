package com.rideconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.postgis.Point;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "location_history")
public class LocationHistory {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "location_id", updatable = false, nullable = false)
    private UUID locationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "location", columnDefinition = "geography(Point)")
    private Point location;

    @Column(name = "recorded_at", nullable = false)
    private ZonedDateTime recordedAt;

    @Column(name = "heading")
    private Float heading;

    @Column(name = "speed")
    private Float speed;

    @Column(name = "trip_id")
    private UUID tripId;

    @PrePersist
    protected void onCreate() {
        this.recordedAt = ZonedDateTime.now();
    }
}
