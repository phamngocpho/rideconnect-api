package com.rideconnect.dto.response.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationsResponse {

    private List<NotificationDto> notifications;
    private int unreadCount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationDto {
        private UUID notificationId;
        private String type;
        private String title;
        private String message;
        private UUID relatedId;
        private Boolean isRead;
        private ZonedDateTime createdAt;
    }
}
