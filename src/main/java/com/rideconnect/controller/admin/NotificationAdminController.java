package com.rideconnect.controller.admin;

import com.rideconnect.entity.Notification;
import com.rideconnect.entity.User;
import com.rideconnect.service.NotificationService;
import com.rideconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/notifications")
public class NotificationAdminController {

    private final NotificationService notificationService;
    private final UserService userService;

    @Autowired
    public NotificationAdminController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationService.findAll();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable UUID id) {
        Notification notification = notificationService.findById(id);
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUser(@PathVariable UUID userId) {
        User user = userService.findById(userId);
        List<Notification> notifications = notificationService.findByUser(user);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread/count/{userId}")
    public ResponseEntity<Long> getUnreadNotificationCount(@PathVariable UUID userId) {
        User user = userService.findById(userId);
        long count = notificationService.countUnreadNotifications(user);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/send")
    public ResponseEntity<Notification> sendSystemNotification(
            @RequestParam UUID userId,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam String type) {

        User user = userService.findById(userId);
        notificationService.createSystemNotification(user, title, message, type);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/send/bulk")
    public ResponseEntity<Void> sendBulkSystemNotifications(
            @RequestParam List<UUID> userIds,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam String type) {

        for (UUID userId : userIds) {
            User user = userService.findById(userId);
            notificationService.createSystemNotification(user, title, message, type);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}/mark-read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable UUID id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/mark-all-read/{userId}")
    public ResponseEntity<Void> markAllNotificationsAsRead(@PathVariable UUID userId) {
        User user = userService.findById(userId);
        notificationService.markAllAsRead(user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID id) {
        notificationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
