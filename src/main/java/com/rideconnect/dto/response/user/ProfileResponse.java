package com.rideconnect.dto.response.user;

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
public class ProfileResponse {

    private UUID userId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String avatarUrl;
    private boolean isDriver;
    private boolean isCustomer;
    private Double averageRating;
    private ZonedDateTime createdAt;
}
