package com.rideconnect.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.rideconnect.converter.UserStatusConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    @Column(name = "phone_number", unique = true, nullable = false, length = 15)
    private String phoneNumber;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @Convert(converter = UserStatusConverter.class)
    @Column(name = "status", length = 20)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    // Enum cho Status
    public enum UserStatus {
        @JsonValue
        ACTIVE("active"),
        INACTIVE("inactive"),
        SUSPENDED("suspended");

        private final String value;

        UserStatus(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        @JsonCreator
        public static UserStatus fromValue(String value) {
            for (UserStatus status : UserStatus.values()) {
                if (status.value.equalsIgnoreCase(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown status: " + value);
        }
    }

    // Enum cho Role
    public enum UserRole {
        @JsonValue
        ROLE_ADMIN("ROLE_ADMIN"),
        ROLE_DRIVER("ROLE_DRIVER"),
        ROLE_CUSTOMER("ROLE_CUSTOMER");

        private final String value;

        UserRole(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public String getRoleName() {
            return this.value; // Đã có ROLE_ prefix
        }

        @JsonCreator
        public static UserRole fromValue(String value) {
            for (UserRole role : UserRole.values()) {
                if (role.value.equals(value)) {
                    return role;
                }
            }
            throw new IllegalArgumentException("Unknown role: " + value);
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now();
        this.updatedAt = ZonedDateTime.now();
        if (this.status == null) {
            this.status = UserStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = ZonedDateTime.now();
    }

    // Getter cho password để Spring Security sử dụng
    public String getPassword() {
        return this.passwordHash;
    }

    // Setter cho password
    public void setPassword(String password) {
        this.passwordHash = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
