package com.rideconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "persistent_logins", indexes = {
        @Index(name = "ix_persistent_logins_username", columnList = "username")
})
public class PersistentLogin {

    @Column(name = "username", nullable = false, length = 64)
    private String username;

    @Id
    @Column(name = "series", length = 64)
    private String series;

    @Column(name = "token", nullable = false, length = 64)
    private String token;

    @Column(name = "last_used", nullable = false)
    private LocalDateTime lastUsed;
}
