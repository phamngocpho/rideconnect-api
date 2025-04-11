package com.rideconnect.dto.response.customer;

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
public class CustomerProfileResponse {

    private UUID customerId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String avatarUrl;
    private AddressDto homeAddress;
    private AddressDto workAddress;
    private Double averageRating;
    private ZonedDateTime createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressDto {
        private String address;
        private Double latitude;
        private Double longitude;
    }
}
