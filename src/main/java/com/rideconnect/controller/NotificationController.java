package com.rideconnect.controller;

import com.rideconnect.dto.response.notification.NotificationsResponse;
import com.rideconnect.security.CustomUserDetails;
import com.rideconnect.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<NotificationsResponse> getNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        NotificationsResponse response = notificationService.getNotifications(userDetails);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID notificationId) {
        notificationService.markNotificationAsRead(userDetails, notificationId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllNotificationsAsRead(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationService.markAllNotificationsAsRead(userDetails);
        return ResponseEntity.ok().build();
    }
}