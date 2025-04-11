package com.rideconnect.controller;

import com.rideconnect.dto.request.message.SendMessageRequest;
import com.rideconnect.dto.response.message.ConversationResponse;
import com.rideconnect.dto.response.message.MessageResponse;
import com.rideconnect.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SendMessageRequest request) {
        String userId = userDetails.getUsername();
        MessageResponse response = messageService.sendMessage(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/trips/{tripId}")
    public ResponseEntity<ConversationResponse> getTripConversation(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID tripId) {
        String userId = userDetails.getUsername();
        ConversationResponse response = messageService.getTripConversation(userId, tripId);
        return ResponseEntity.ok(response);
    }
}
