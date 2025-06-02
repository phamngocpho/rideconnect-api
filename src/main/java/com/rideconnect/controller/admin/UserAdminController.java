package com.rideconnect.controller.admin;

import com.rideconnect.dto.request.user.UserCreateRequest;
import com.rideconnect.dto.request.user.UserUpdateRequest;
import com.rideconnect.dto.response.user.UserResponse;
import com.rideconnect.entity.User;
import com.rideconnect.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        return new ResponseEntity<>(userService.createUser(request), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<UserResponse> getUserByPhoneNumber(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(userService.getUserByPhoneNumber(phoneNumber));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            // Tạo Pageable
            Sort sort = sortDir.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            // Lấy Page<User> thay vì Page<UserResponse>
            Page<User> userPage = userService.getAllUsers(pageable);

            // Tạo response thủ công để tránh infinite recursion
            Map<String, Object> response = new HashMap<>();

            // Convert User entities thành simple objects
            List<Map<String, Object>> users = userPage.getContent().stream()
                    .map(user -> {
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("id", user.getUserId());
                        userMap.put("fullName", user.getFullName());
                        userMap.put("email", user.getEmail());
                        userMap.put("phoneNumber", user.getPhoneNumber());
                        userMap.put("status", user.getStatus().name()); // Convert enum to String
                        userMap.put("role", user.getRole().name());     // Convert enum to String
                        userMap.put("avatarUrl", user.getAvatarUrl());
                        userMap.put("createdAt", user.getCreatedAt());
                        userMap.put("updatedAt", user.getUpdatedAt());
                        return userMap;
                    })
                    .collect(Collectors.toList());

            // Thông tin pagination
            response.put("content", users);
            response.put("totalElements", userPage.getTotalElements());
            response.put("totalPages", userPage.getTotalPages());
            response.put("currentPage", userPage.getNumber());
            response.put("size", userPage.getSize());
            response.put("hasNext", userPage.hasNext());
            response.put("hasPrevious", userPage.hasPrevious());
            response.put("first", userPage.isFirst());
            response.put("last", userPage.isLast());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Log error và return error response
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to load users");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(userId, request));
    }

    @PatchMapping("/{userId}/status")
    public ResponseEntity<UserResponse> changeUserStatus(
            @PathVariable UUID userId,
            @RequestParam User.UserStatus status) {
        return ResponseEntity.ok(userService.changeUserStatus(userId, status));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
