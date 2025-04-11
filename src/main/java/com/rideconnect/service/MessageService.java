package com.rideconnect.service;

import com.rideconnect.dto.request.message.SendMessageRequest;
import com.rideconnect.dto.response.message.ConversationResponse;
import com.rideconnect.dto.response.message.MessageResponse;

import java.util.UUID;

public interface MessageService {

    /**
     * Send a message
     *
     * @param userId user ID (sender)
     * @param request message details
     * @return sent message details
     */
    MessageResponse sendMessage(String userId, SendMessageRequest request);

    /**
     * Get conversation for a trip
     *
     * @param userId user ID (customer or driver)
     * @param tripId trip ID
     * @return conversation with messages
     */
    ConversationResponse getTripConversation(String userId, UUID tripId);
}
