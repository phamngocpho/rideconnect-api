package com.rideconnect.service;

import com.rideconnect.dto.response.notification.NotificationsResponse;
import com.rideconnect.security.CustomUserDetails;

import java.util.UUID;

public interface NotificationService {

    /**
     * Get user's notifications
     *
     * @param userDetails authenticated user details
     * @return list of notifications
     */
    NotificationsResponse getNotifications(CustomUserDetails userDetails);

    /**
     * Mark a notification as read
     *
     * @param userDetails authenticated user details
     * @param notificationId notification ID
     */
    void markNotificationAsRead(CustomUserDetails userDetails, UUID notificationId);

    /**
     * Mark all notifications as read
     *
     * @param userDetails authenticated user details
     */
    void markAllNotificationsAsRead(CustomUserDetails userDetails);
}