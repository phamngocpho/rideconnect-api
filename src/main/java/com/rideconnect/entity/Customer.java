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
@Table(name = "customers")
public class Customer {

    @Id
    @Column(name = "customer_id")
    private UUID customerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "user_id")
    @MapsId
    private User user;

    @Column(name = "default_payment_method")
    private String defaultPaymentMethod;

    @Column(name = "rating", precision = 3, scale = 2)
    private BigDecimal rating;

    @Column(name = "total_trips")
    private Integer totalTrips;

    // Thêm trường version để hỗ trợ Optimistic Locking
    @Version
    @Column(name = "version")
    private Long version;

    @PrePersist
    protected void onCreate() {
        if (this.rating == null) {
            this.rating = new BigDecimal("5.0");
        }
        if (this.totalTrips == null) {
            this.totalTrips = 0;
        }
    }
}
