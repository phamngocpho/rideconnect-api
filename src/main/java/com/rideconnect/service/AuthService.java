package com.rideconnect.service;

import com.rideconnect.dto.request.auth.LoginRequest;
import com.rideconnect.dto.request.auth.RegisterRequest;
import com.rideconnect.dto.response.auth.LoginResponse;
import com.rideconnect.dto.response.auth.RegisterResponse;

public interface AuthService {

    /**
     * Register a new user
     *
     * @param request registration details
     * @return registration response with user details and token
     */
    RegisterResponse register(RegisterRequest request);

    /**
     * Login an existing user
     *
     * @param request login credentials
     * @return login response with user details and token
     */
    LoginResponse login(LoginRequest request);

    void logout(String token);
}
