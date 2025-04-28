package com.rideconnect.service.impl;

import com.rideconnect.dto.request.auth.LoginRequest;
import com.rideconnect.dto.request.auth.RegisterRequest;
import com.rideconnect.dto.response.auth.LoginResponse;
import com.rideconnect.dto.response.auth.RegisterResponse;
import com.rideconnect.entity.Customer;
import com.rideconnect.entity.User;
import com.rideconnect.exception.BadRequestException;
import com.rideconnect.exception.UnauthorizedException;
import com.rideconnect.repository.CustomerRepository;
import com.rideconnect.repository.DriverRepository;
import com.rideconnect.repository.UserRepository;
import com.rideconnect.security.JwtTokenProvider;
import com.rideconnect.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // Check if a phone number already exists
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BadRequestException("Phone number already registered");
        }

        // Check if email already exists (if provided)
        if (request.getEmail() != null && !request.getEmail().isEmpty() &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        // Create a user entity
        User user = User.builder()
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .build();

        // Save user
        User savedUser = userRepository.save(user);

        // Create a customer entity
        Customer customer = Customer.builder()
                .customerId(savedUser.getUserId())
                .user(savedUser)
                .build();

        // Save customer
        customerRepository.save(customer);

        // Generate token
        String token = jwtTokenProvider.generateToken(savedUser.getUserId().toString());

        // Return response
        return RegisterResponse.builder()
                .userId(savedUser.getUserId())
                .token(token)
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getPhoneNumber(),
                            request.getPassword()
                    )
            );

            // Set authentication to a security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user from a repository
            User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                    .orElseThrow(() -> new UnauthorizedException("Invalid phone number or password"));

            // Xác định userType
            String userType = determineUserType(user.getUserId());

            // Generate token
            String token = jwtTokenProvider.generateToken(user.getUserId().toString());

            // Return response
            return LoginResponse.builder()
                    .userId(user.getUserId())
                    .token(token)
                    .fullName(user.getFullName())
                    .phoneNumber(user.getPhoneNumber())
                    .email(user.getEmail())
                    .avatarUrl(user.getAvatarUrl())
                    .userType(userType)  // Thêm userType vào response
                    .build();
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid phone number or password");
        }
    }

    // Phương thức này để xác định userType
    private String determineUserType(UUID userId) {
        if (driverRepository.existsById(userId)) {
            return "DRIVER";
        }

        customerRepository.existsById(userId);

        return "CUSTOMER";
    }


    @Override
    public void logout(String token) {
        // Thay thế phương thức invalidateToken bằng một phương thức khác hoặc thêm phương thức này vào JwtTokenProvider
        // Ví dụ:
        // jwtTokenProvider.addToBlacklist(token);
        // HOẶC
        // Không làm gì cả nếu sử dụng JWT stateless
    }
}
