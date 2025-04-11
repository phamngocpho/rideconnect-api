package com.rideconnect.service;

import com.rideconnect.dto.response.notification.NotificationsResponse;

import java.util.UUID;

public interface NotificationService {

    /**
     * Get user's notifications
     *
     * @param userId user ID
     * @return list of notifications
     */
    NotificationsResponse getNotifications(String userId);

    /**
     * Mark a notification as read
     *
     * @param userId user ID
     * @param notificationId notification ID
     */
    void markNotificationAsRead(String userId, UUID notificationId);

    /**
     * Mark all notifications as read
     *
     * @param userId user ID
     */
    void markAllNotificationsAsRead(String userId);
}
