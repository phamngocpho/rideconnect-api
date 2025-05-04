package com.rideconnect.service.impl;

import com.rideconnect.dto.response.notification.NotificationsResponse;
import com.rideconnect.entity.Notification;
import com.rideconnect.exception.ResourceNotFoundException;
import com.rideconnect.repository.NotificationRepository;
import com.rideconnect.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional(readOnly = true)
    public NotificationsResponse getNotifications(String userId) {
        List<Notification> notifications = notificationRepository.findByUserUserIdOrderByCreatedAtDesc(UUID.fromString(userId));

        List<NotificationsResponse.NotificationDto> notificationDTOs = notifications.stream()
                .map(this::mapNotificationToDto)
                .collect(Collectors.toList());

        int unreadCount = (int) notifications.stream()
                .filter(notification -> !notification.getIsRead())
                .count();

        return NotificationsResponse.builder()
                .notifications(notificationDTOs)
                .unreadCount(unreadCount)
                .build();
    }

    @Override
    @Transactional
    public void markNotificationAsRead(String userId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId.toString()));

        // Check if notification belongs to user
        if (!notification.getUser().getUserId().equals(UUID.fromString(userId))) {
            throw new ResourceNotFoundException("Notification", "id", notificationId.toString());
        }

        notification.setIsRead(true);
        notification.setReadAt(ZonedDateTime.now());
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllNotificationsAsRead(String userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserUserIdAndIsReadFalse(UUID.fromString(userId));

        ZonedDateTime now = ZonedDateTime.now();
        unreadNotifications.forEach(notification -> {
            notification.setIsRead(true);
            notification.setReadAt(now);
        });

        notificationRepository.saveAll(unreadNotifications);
    }

    private NotificationsResponse.NotificationDto mapNotificationToDto(Notification notification) {
        return NotificationsResponse.NotificationDto.builder()
                .notificationId(notification.getNotificationId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .relatedId(notification.getRelatedId())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
