package com.rideconnect.dto.response.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {

    private UUID messageId;
    private UUID tripId;
    private UUID senderId;
    private String senderName;
    private UUID recipientId;
    private String content;
    private Boolean isRead;
    private ZonedDateTime createdAt;
}
