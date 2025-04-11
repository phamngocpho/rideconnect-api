package com.rideconnect.repository;

import com.rideconnect.entity.LocationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface LocationHistoryRepository extends JpaRepository<LocationHistory, UUID> {

    List<LocationHistory> findByUserUserIdOrderByRecordedAtDesc(UUID userId);

    @Query("SELECT lh FROM LocationHistory lh WHERE lh.user.userId = :userId AND lh.recordedAt BETWEEN :startTime AND :endTime ORDER BY lh.recordedAt")
    List<LocationHistory> findLocationHistoryBetween(UUID userId, ZonedDateTime startTime, ZonedDateTime endTime);

    @Query("SELECT lh FROM LocationHistory lh WHERE lh.tripId = :tripId ORDER BY lh.recordedAt")
    List<LocationHistory> findByTripIdOrderByRecordedAt(UUID tripId);
}
