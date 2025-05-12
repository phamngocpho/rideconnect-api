package com.rideconnect.controller;

import com.rideconnect.dto.request.message.SendMessageRequest;
import com.rideconnect.dto.response.message.ConversationResponse;
import com.rideconnect.dto.response.message.MessageResponse;
import com.rideconnect.security.CustomUserDetails;
import com.rideconnect.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody SendMessageRequest request) {
        MessageResponse response = messageService.sendMessage(userDetails, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/trips/{tripId}")
    public ResponseEntity<ConversationResponse> getTripConversation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID tripId) {
        ConversationResponse response = messageService.getTripConversation(userDetails, tripId);
        return ResponseEntity.ok(response);
    }
}