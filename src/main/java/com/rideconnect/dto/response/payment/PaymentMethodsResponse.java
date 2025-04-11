package com.rideconnect.dto.response.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodsResponse {

    private List<PaymentMethodDto> paymentMethods;
    private String defaultPaymentMethod;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentMethodDto {
        private UUID id;
        private String type;
        private String cardNumber;
        private String cardHolderName;
        private String expiryDate;
        private Boolean isDefault;
        private String walletId;
        private String walletName;
    }
}
