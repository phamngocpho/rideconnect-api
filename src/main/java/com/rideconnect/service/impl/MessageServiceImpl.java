package com.rideconnect.service.impl;

import com.rideconnect.dto.request.message.SendMessageRequest;
import com.rideconnect.dto.response.message.ConversationResponse;
import com.rideconnect.dto.response.message.MessageResponse;
import com.rideconnect.entity.Message;
import com.rideconnect.entity.Trip;
import com.rideconnect.entity.User;
import com.rideconnect.exception.BadRequestException;
import com.rideconnect.exception.ResourceNotFoundException;
import com.rideconnect.repository.MessageRepository;
import com.rideconnect.repository.TripRepository;
import com.rideconnect.repository.UserRepository;
import com.rideconnect.security.CustomUserDetails;
import com.rideconnect.service.MessageService;
import com.rideconnect.websocket.WebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final WebSocketHandler webSocketHandler;

    @Override
    @Transactional
    public MessageResponse sendMessage(CustomUserDetails userDetails, SendMessageRequest request) {
        UUID userId = userDetails.getUserId();

        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", request.getTripId().toString()));

        // Check if the user is either customer or driver of this trip
        boolean isDriver = trip.getDriver().getDriverId().equals(userId);
        boolean isCustomer = trip.getCustomer().getCustomerId().equals(userId);

        if (!isDriver && !isCustomer) {
            throw new BadRequestException("You are not authorized to send messages for this trip");
        }

        // Check if the recipient is the other party of the trip
        UUID recipientUuid = request.getRecipientId();
        if ((isDriver && !trip.getCustomer().getCustomerId().equals(recipientUuid)) ||
                (isCustomer && !trip.getDriver().getDriverId().equals(recipientUuid))) {
            throw new BadRequestException("Invalid recipient for this trip");
        }

        // Get sender and recipient users
        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));

        User recipient = userRepository.findById(recipientUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", recipientUuid.toString()));

        // Create a message
        Message message = Message.builder()
                .trip(trip)
                .sender(sender)
                .recipient(recipient)
                .content(request.getContent())
                .isRead(false)
                .build();

        Message savedMessage = messageRepository.save(message);

        // Convert to response
        MessageResponse response = mapMessageToMessageResponse(savedMessage);

        // Send messages via WebSocket
        if (isDriver) {
            webSocketHandler.sendLocationUpdateToCustomer(trip.getCustomer().getCustomerId().toString(), response);
        } else {
            webSocketHandler.sendTripRequestToDriver(trip.getDriver().getDriverId().toString(), response);
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationResponse getTripConversation(CustomUserDetails userDetails, UUID tripId) {
        UUID userId = userDetails.getUserId();

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId.toString()));

        // Check if the user is either customer or driver of this trip
        if (!trip.getCustomer().getCustomerId().equals(userId) &&
                !trip.getDriver().getDriverId().equals(userId)) {
            throw new BadRequestException("You are not authorized to view messages for this trip");
        }

        // Get all messages for this trip
        List<Message> messages = messageRepository.findByTripTripIdOrderByCreatedAt(tripId);

        // Mark messages as read if the user is the recipient
        List<Message> unreadMessages = messages.stream()
                .filter(message -> message.getRecipient().getUserId().equals(userId) && !message.getIsRead())
                .collect(Collectors.toList());

        if (!unreadMessages.isEmpty()) {
            unreadMessages.forEach(message -> message.setIsRead(true));
            messageRepository.saveAll(unreadMessages);
        }

        // Convert to response
        List<ConversationResponse.MessageDto> messageDTOs = messages.stream()
                .map(this::mapMessageToMessageDto)
                .collect(Collectors.toList());

        return ConversationResponse.builder()
                .tripId(tripId)
                .messages(messageDTOs)
                .build();
    }

    private MessageResponse mapMessageToMessageResponse(Message message) {
        return MessageResponse.builder()
                .messageId(message.getMessageId())
                .tripId(message.getTrip().getTripId())
                .senderId(message.getSender().getUserId())
                .senderName(message.getSender().getFullName())
                .recipientId(message.getRecipient().getUserId())
                .content(message.getContent())
                .isRead(message.getIsRead())
                .createdAt(message.getCreatedAt())
                .build();
    }

    private ConversationResponse.MessageDto mapMessageToMessageDto(Message message) {
        return ConversationResponse.MessageDto.builder()
                .messageId(message.getMessageId())
                .senderId(message.getSender().getUserId())
                .senderName(message.getSender().getFullName())
                .content(message.getContent())
                .isRead(message.getIsRead())
                .createdAt(message.getCreatedAt())
                .build();
    }
}