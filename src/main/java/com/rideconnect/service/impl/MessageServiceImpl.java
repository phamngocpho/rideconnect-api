package com.rideconnect.service.impl;

import com.rideconnect.entity.Message;
import com.rideconnect.entity.Trip;
import com.rideconnect.entity.User;
import com.rideconnect.repository.MessageRepository;
import com.rideconnect.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    @Transactional
    public Message save(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public Message findById(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + messageId));
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public List<Message> findByTrip(Trip trip) {
        return messageRepository.findByTrip(trip);
    }

    @Override
    public List<Message> findBySender(User sender) {
        return messageRepository.findBySender(sender);
    }

    @Override
    public List<Message> findByRecipient(User recipient) {
        return messageRepository.findByRecipient(recipient);
    }

    @Override
    public List<Message> findByRecipientAndIsRead(User recipient, Boolean isRead) {
        return messageRepository.findByRecipientAndIsRead(recipient, isRead);
    }

    @Override
    public List<Message> findByTripOrderByCreatedAtAsc(Trip trip) {
        return messageRepository.findByTripOrderByCreatedAtAsc(trip);
    }

    @Override
    public List<Message> findBySenderAndRecipient(User sender, User recipient) {
        return messageRepository.findBySenderAndRecipient(sender, recipient);
    }

    @Override
    public long countUnreadMessages(User recipient) {
        return messageRepository.countByRecipientAndIsRead(recipient, false);
    }

    @Override
    @Transactional
    public Message sendMessage(User sender, User recipient, Trip trip, String content) {
        Message message = Message.builder()
                .sender(sender)
                .recipient(recipient)
                .trip(trip)
                .content(content)
                .isRead(false)
                .build();
        return messageRepository.save(message);
    }

    @Override
    @Transactional
    public void markAsRead(UUID messageId) {
        Message message = findById(messageId);
        message.setIsRead(true);
        message.setReadAt(ZonedDateTime.now());
        messageRepository.save(message);
    }

    @Override
    @Transactional
    public void markAllAsRead(User recipient) {
        List<Message> unreadMessages = messageRepository.findByRecipientAndIsRead(recipient, false);
        for (Message message : unreadMessages) {
            message.setIsRead(true);
            message.setReadAt(ZonedDateTime.now());
        }
        messageRepository.saveAll(unreadMessages);
    }

    @Override
    @Transactional
    public void delete(UUID messageId) {
        messageRepository.deleteById(messageId);
    }
}
