package com.rideconnect.dto.response.auth;

import com.rideconnect.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private UUID userId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private User.UserRole role;
    private String message;
    private boolean success;
}
