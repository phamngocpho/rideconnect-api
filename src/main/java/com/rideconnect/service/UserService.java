package com.rideconnect.service;
import com.rideconnect.dto.request.user.UserCreateRequest;
import com.rideconnect.dto.request.user.UserUpdateRequest;
import com.rideconnect.dto.response.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    UserResponse createUser(UserCreateRequest request);
    UserResponse getUserById(UUID userId);
    UserResponse getUserByEmail(String email);
    UserResponse getUserByPhoneNumber(String phoneNumber);
    Page<UserResponse> getAllUsers(Pageable pageable);
    UserResponse updateUser(UUID userId, UserUpdateRequest request);
    void deleteUser(UUID userId);
    UserResponse changeUserStatus(UUID userId, String status);
}
