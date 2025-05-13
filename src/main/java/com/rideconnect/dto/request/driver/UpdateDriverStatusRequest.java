package com.rideconnect.dto.request.driver;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDriverStatusRequest {

    @NotNull(message = "Available status is required")
    private String isAvailable;
}
