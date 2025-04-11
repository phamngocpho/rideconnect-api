package com.rideconnect.dto.response.rating;

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
public class RatingResponse {

    private UUID ratingId;
    private UUID tripId;
    private UUID raterId;
    private UUID ratedId;
    private Integer rating;
    private String comment;
    private ZonedDateTime createdAt;
}
