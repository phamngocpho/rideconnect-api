package com.rideconnect.service.impl;

import com.rideconnect.dto.request.auth.LoginRequest;
import com.rideconnect.dto.request.auth.RegisterRequest;
import com.rideconnect.dto.response.auth.LoginResponse;
import com.rideconnect.dto.response.auth.RegisterResponse;
import com.rideconnect.entity.Customer;
import com.rideconnect.entity.Driver;
import com.rideconnect.entity.User;
import com.rideconnect.repository.CustomerRepository;
import com.rideconnect.repository.DriverRepository;
import com.rideconnect.repository.UserRepository;
import com.rideconnect.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegisterResponse register(RegisterRequest request) {
        System.out.println("===== STARTING REGISTRATION PROCESS =====");
        System.out.println("Email: " + request.getEmail());
        System.out.println("Phone: " + request.getPhoneNumber());
        System.out.println("Role: " + request.getRole());

        log.info("Starting user registration process for: {}", request.getEmail());

        // Kiểm tra số điện thoại đã tồn tại chưa
        log.debug("Checking if phone number already exists: {}", request.getPhoneNumber());
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            log.warn("Phone number already exists: {}", request.getPhoneNumber());
            return RegisterResponse.builder()
                    .success(false)
                    .message("Số điện thoại đã được sử dụng")
                    .build();
        }

        // Kiểm tra email đã tồn tại chưa
        log.debug("Checking if email already exists: {}", request.getEmail());
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email already exists: {}", request.getEmail());
            return RegisterResponse.builder()
                    .success(false)
                    .message("Email đã được sử dụng")
                    .build();
        }

        try {
            // Tạo user mới
            User.UserRole role = User.UserRole.valueOf(request.getRole());
            log.info("Creating new user with role: {}", role);

            User user = User.builder()
                    .fullName(request.getFullName())
                    .phoneNumber(request.getPhoneNumber())
                    .email(request.getEmail())
                    .passwordHash(passwordEncoder.encode(request.getPassword()))
                    .role(role)
                    .status(User.UserStatus.ACTIVE)
                    .build();

            log.debug("Saving user to database: {}", user);
            System.out.println("Attempting to save user to database...");
            User savedUser = userRepository.save(user);
            System.out.println("USER SAVED SUCCESSFULLY: " + savedUser.getUserId());
            log.info("User saved successfully with ID: {}", savedUser.getUserId());

            // Tạo customer/driver dựa vào role
            if ("ROLE_CUSTOMER".equals(role)) {
                log.info("Creating customer profile for user ID: {}", savedUser.getUserId());
                Customer customer = Customer.builder()
                        .user(savedUser)
                        .build();
                Customer savedCustomer = customerRepository.save(customer);
                log.info("Customer profile created with ID: {}", savedCustomer.getCustomerId());
            } else if ("ROLE_DRIVER".equals(role)) {
                log.info("Creating driver profile for user ID: {}", savedUser.getUserId());

                // Tạo giá trị tạm thời duy nhất cho licenseNumber và vehiclePlate
                String tempLicenseNumber = "PENDING_" + UUID.randomUUID().toString();
                String tempVehiclePlate = "PENDING_" + UUID.randomUUID().toString();

                Driver driver = Driver.builder()
                        .user(savedUser)
                        .licenseNumber(tempLicenseNumber) // Giá trị duy nhất
                        .vehicleType("Cần cập nhật")
                        .vehiclePlate(tempVehiclePlate) // Giá trị duy nhất
                        .profileCompleted(false) // Đánh dấu hồ sơ chưa hoàn thành
                        .build();
                Driver savedDriver = driverRepository.save(driver);
                log.info("Driver profile created with ID: {}", savedDriver.getDriverId());
            } else {
                log.warn("Unknown role provided: {}", role);
            }

            log.info("Registration completed successfully for user ID: {}", savedUser.getUserId());
            return RegisterResponse.builder()
                    .userId(savedUser.getUserId())
                    .fullName(savedUser.getFullName())
                    .phoneNumber(savedUser.getPhoneNumber())
                    .email(savedUser.getEmail())
                    .role(savedUser.getRole())
                    .success(true)
                    .message("Đăng ký thành công")
                    .build();
        } catch (Exception e) {
            log.error("Error during user registration", e);
            throw e;
        }
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getPhoneNumber(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        return LoginResponse.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .role(user.getRole())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    @Override
    public void logout(String token) {
        // Không cần thực hiện gì trong web interface
        // Session được quản lý bởi Spring Security
    }
}