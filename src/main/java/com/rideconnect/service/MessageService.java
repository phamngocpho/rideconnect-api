package com.rideconnect.service;

import com.rideconnect.dto.request.message.SendMessageRequest;
import com.rideconnect.dto.response.message.ConversationResponse;
import com.rideconnect.dto.response.message.MessageResponse;
import com.rideconnect.security.CustomUserDetails;

import java.util.UUID;

public interface MessageService {

    /**
     * Send a message
     *
     * @param userDetails authenticated user details (sender)
     * @param request message details
     * @return sent message details
     */
    MessageResponse sendMessage(CustomUserDetails userDetails, SendMessageRequest request);

    /**
     * Get conversation for a trip
     *
     * @param userDetails authenticated user details (customer or driver)
     * @param tripId trip ID
     * @return conversation with messages
     */
    ConversationResponse getTripConversation(CustomUserDetails userDetails, UUID tripId);
}