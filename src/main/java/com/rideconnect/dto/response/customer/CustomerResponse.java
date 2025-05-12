package com.rideconnect.dto.response.customer;

import com.rideconnect.dto.response.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private UUID customerId;
    private UserResponse user;
    private String defaultPaymentMethod;
    private BigDecimal rating;
    private Integer totalTrips;
}

