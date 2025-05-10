package com.rideconnect.controller;

import com.rideconnect.dto.request.auth.RegisterRequest;
import com.rideconnect.dto.response.auth.RegisterResponse;
import com.rideconnect.entity.User;
import com.rideconnect.repository.UserRepository;
import com.rideconnect.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    private final UserRepository userRepository;
    private final AuthService authService;

    // Endpoints under /test-api
    @GetMapping("/test-api/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @GetMapping("/test-api/user-count")
    public Map<String, Object> getUserCount() {
        Map<String, Object> response = new HashMap<>();
        try {
            long count = userRepository.count();
            response.put("count", count);
            response.put("success", true);
        } catch (Exception e) {
            log.error("Error getting user count", e);
            response.put("error", e.getMessage());
            response.put("success", false);
        }
        return response;
    }
    
    @PostMapping("/test-api/register")
    public RegisterResponse testRegister(@RequestBody RegisterRequest request) {
        System.out.println("TEST REGISTER CALLED");
        System.out.println("Request: " + request);
        
        // Set a default role if not provided
        if (request.getRole() == null || request.getRole().isEmpty()) {
            request.setRole("ROLE_CUSTOMER");
        }
        
        try {
            RegisterResponse response = authService.register(request);
            System.out.println("Registration response: " + response);
            return response;
        } catch (Exception e) {
            System.out.println("Registration exception: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Endpoints under /test
    @GetMapping("/test/db-check")
    public Map<String, Object> checkDatabaseConnection() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            System.out.println("DB-CHECK ENDPOINT ACCESSED");
            long userCount = userRepository.countUsers();
            System.out.println("DATABASE CONNECTION SUCCESSFUL. USER COUNT: " + userCount);
            log.info("Database connection test successful. User count: {}", userCount);
            
            result.put("status", "success");
            result.put("message", "Database connection established");
            result.put("userCount", userCount);
            
            // Add additional database info
            result.put("test_result", "If you can see this, the test endpoint is working correctly");
            
        } catch (Exception e) {
            System.out.println("DATABASE CONNECTION ERROR: " + e.getMessage());
            e.printStackTrace();
            log.error("Database connection test failed", e);
            
            result.put("status", "error");
            result.put("message", "Database connection failed: " + e.getMessage());
            result.put("errorType", e.getClass().getName());
            result.put("stackTrace", e.getStackTrace());
        }
        
        return result;
    }
}
