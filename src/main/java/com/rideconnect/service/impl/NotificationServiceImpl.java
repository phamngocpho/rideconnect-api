package com.rideconnect.service.impl;

import com.rideconnect.entity.Notification;
import com.rideconnect.entity.User;
import com.rideconnect.repository.NotificationRepository;
import com.rideconnect.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public Notification findById(UUID notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));
    }

    @Override
    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    @Override
    public List<Notification> findByUser(User user) {
        return notificationRepository.findByUser(user);
    }

    @Override
    public List<Notification> findByUserAndIsRead(User user, Boolean isRead) {
        return notificationRepository.findByUserAndIsRead(user, isRead);
    }

    @Override
    public List<Notification> findByUserOrderByCreatedAtDesc(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public long countUnreadNotifications(User user) {
        return notificationRepository.countByUserAndIsRead(user, false);
    }

    @Override
    @Transactional
    public void markAsRead(UUID notificationId) {
        Notification notification = findById(notificationId);
        notification.setIsRead(true);
        notification.setReadAt(ZonedDateTime.now());
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(User user) {
        List<Notification> unreadNotifications = notificationRepository.findByUserAndIsRead(user, false);
        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
            notification.setReadAt(ZonedDateTime.now());
        }
        notificationRepository.saveAll(unreadNotifications);
    }

    @Override
    @Transactional
    public void delete(UUID notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    @Override
    @Transactional
    public void createSystemNotification(User user, String title, String message, String type) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }
}
