package com.rideconnect.dto.request.payment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavePaymentMethodRequest {

    @NotBlank(message = "Payment method type is required")
    private String type;

    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;
    private Boolean setAsDefault;

    // For e-wallets
    private String walletId;
}
