package com.rideconnect.repository;

import com.rideconnect.entity.Notification;
import com.rideconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByUser(User user);

    List<Notification> findByUserAndIsRead(User user, Boolean isRead);

    long countByUserAndIsRead(User user, Boolean isRead);

    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    List<Notification> findByType(String type);
}
