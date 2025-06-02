package com.rideconnect.dto.response.user;

import com.fasterxml.jackson.annotation.JsonValue;
import com.rideconnect.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID userId;
    private String phoneNumber;
    private String email;
    private String fullName;
    private String avatarUrl;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    @JsonValue
    private User.UserStatus status;
    private User.UserRole role;
}

