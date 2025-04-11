package com.rideconnect.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;

    // Map to store user sessions: userId -> session
    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = extractUserId(session);
        if (userId != null) {
            userSessions.put(userId, session);
            log.info("WebSocket connection established for user: {}", userId);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Handle incoming messages if needed
        // For this app, most communication is server-initiated
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = extractUserId(session);
        if (userId != null) {
            userSessions.remove(userId);
            log.info("WebSocket connection closed for user: {}", userId);
        }
    }

    /**
     * Send location update to customer
     */
    public void sendLocationUpdateToCustomer(String customerId, Object data) {
        sendMessageToUser(customerId, "location_update", data);
    }

    /**
     * Send trip request to driver
     */
    public void sendTripRequestToDriver(String driverId, Object data) {
        sendMessageToUser(driverId, "trip_request", data);
    }

    /**
     * Send message to specific user
     */
    public void sendMessageToUser(String userId, String type, Object data) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                WebSocketMessage message = new WebSocketMessage(type, data);
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
            } catch (IOException e) {
                log.error("Error sending WebSocket message to user: {}", userId, e);
            }
        }
    }

    /**
     * Extract userId from session attributes or query parameters
     */
    private String extractUserId(WebSocketSession session) {
        // Extract from session attributes first
        if (session.getAttributes().containsKey("userId")) {
            return (String) session.getAttributes().get("userId");
        }

        // Extract from query parameters
        String query = session.getUri().getQuery();
        if (query != null && query.contains("userId=")) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("userId=")) {
                    return param.substring(7);
                }
            }
        }

        return null;
    }

    /**
     * WebSocket message wrapper
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    private static class WebSocketMessage {
        private String type;
        private Object data;
    }
}
