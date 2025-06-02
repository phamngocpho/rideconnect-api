package com.rideconnect.controller.admin;

import com.rideconnect.entity.Message;
import com.rideconnect.entity.Trip;
import com.rideconnect.entity.User;
import com.rideconnect.service.MessageService;
import com.rideconnect.service.TripService;
import com.rideconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/messages")
public class MessageAdminController {

    private final MessageService messageService;
    private final UserService userService;
    private final TripService tripService;

    @Autowired
    public MessageAdminController(MessageService messageService, UserService userService, TripService tripService) {
        this.messageService = messageService;
        this.userService = userService;
        this.tripService = tripService;
    }

    @GetMapping
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageService.findAll();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable UUID id) {
        Message message = messageService.findById(id);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<Message>> getMessagesByTrip(@PathVariable UUID tripId) {
        Trip trip = tripService.findById(tripId);
        List<Message> messages = messageService.findByTrip(trip);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/trip/{tripId}/chronological")
    public ResponseEntity<List<Message>> getChronologicalMessagesByTrip(@PathVariable UUID tripId) {
        Trip trip = tripService.findById(tripId);
        List<Message> messages = messageService.findByTripOrderByCreatedAtAsc(trip);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/sender/{senderId}")
    public ResponseEntity<List<Message>> getMessagesBySender(@PathVariable UUID senderId) {
        User sender = userService.findById(senderId);
        List<Message> messages = messageService.findBySender(sender);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/recipient/{recipientId}")
    public ResponseEntity<List<Message>> getMessagesByRecipient(@PathVariable UUID recipientId) {
        User recipient = userService.findById(recipientId);
        List<Message> messages = messageService.findByRecipient(recipient);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/conversation")
    public ResponseEntity<List<Message>> getConversation(
            @RequestParam UUID senderId,
            @RequestParam UUID recipientId) {

        User sender = userService.findById(senderId);
        User recipient = userService.findById(recipientId);
        List<Message> messages = messageService.findBySenderAndRecipient(sender, recipient);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/unread/count/{recipientId}")
    public ResponseEntity<Long> getUnreadMessageCount(@PathVariable UUID recipientId) {
        User recipient = userService.findById(recipientId);
        long count = messageService.countUnreadMessages(recipient);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(
            @RequestParam UUID senderId,
            @RequestParam UUID recipientId,
            @RequestParam(required = false) UUID tripId,
            @RequestParam String content) {

        User sender = userService.findById(senderId);
        User recipient = userService.findById(recipientId);
        Trip trip = tripId != null ? tripService.findById(tripId) : null;

        Message message = messageService.sendMessage(sender, recipient, trip, content);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @PutMapping("/{id}/mark-read")
    public ResponseEntity<Void> markMessageAsRead(@PathVariable UUID id) {
        messageService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/mark-all-read/{recipientId}")
    public ResponseEntity<Void> markAllMessagesAsRead(@PathVariable UUID recipientId) {
        User recipient = userService.findById(recipientId);
        messageService.markAllAsRead(recipient);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID id) {
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
