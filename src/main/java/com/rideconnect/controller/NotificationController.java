package com.rideconnect.controller;

import com.rideconnect.dto.response.notification.NotificationsResponse;
import com.rideconnect.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<NotificationsResponse> getNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        NotificationsResponse response = notificationService.getNotifications(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID notificationId) {
        String userId = userDetails.getUsername();
        notificationService.markNotificationAsRead(userId, notificationId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllNotificationsAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        notificationService.markAllNotificationsAsRead(userId);
        return ResponseEntity.ok().build();
    }
}
