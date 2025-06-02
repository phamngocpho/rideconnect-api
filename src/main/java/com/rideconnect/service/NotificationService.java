package com.rideconnect.service;

import com.rideconnect.entity.Notification;
import com.rideconnect.entity.User;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    Notification save(Notification notification);

    Notification findById(UUID notificationId);

    List<Notification> findAll();

    List<Notification> findByUser(User user);

    List<Notification> findByUserAndIsRead(User user, Boolean isRead);

    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    long countUnreadNotifications(User user);

    void markAsRead(UUID notificationId);

    void markAllAsRead(User user);

    void delete(UUID notificationId);

    void createSystemNotification(User user, String title, String message, String type);
}
