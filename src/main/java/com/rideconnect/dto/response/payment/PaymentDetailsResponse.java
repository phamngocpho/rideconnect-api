package com.rideconnect.dto.response.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetailsResponse {

    private UUID paymentId;
    private UUID tripId;
    private String paymentMethod;
    private BigDecimal amount;
    private String status;
    private String transactionId;
    private ZonedDateTime createdAt;
    private ZonedDateTime processedAt;
}
