package com.rideconnect.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private UUID userId;
    private String token;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String avatarUrl;
    private String userType;
}
