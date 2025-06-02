package com.rideconnect.service;

import com.rideconnect.entity.Message;
import com.rideconnect.entity.Trip;
import com.rideconnect.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message save(Message message);

    Message findById(UUID messageId);

    List<Message> findAll();

    List<Message> findByTrip(Trip trip);

    List<Message> findBySender(User sender);

    List<Message> findByRecipient(User recipient);

    List<Message> findByRecipientAndIsRead(User recipient, Boolean isRead);

    List<Message> findByTripOrderByCreatedAtAsc(Trip trip);

    List<Message> findBySenderAndRecipient(User sender, User recipient);

    long countUnreadMessages(User recipient);

    Message sendMessage(User sender, User recipient, Trip trip, String content);

    void markAsRead(UUID messageId);

    void markAllAsRead(User recipient);

    void delete(UUID messageId);
}
