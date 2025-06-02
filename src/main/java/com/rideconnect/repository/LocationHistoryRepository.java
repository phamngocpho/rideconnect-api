package com.rideconnect.repository;

import com.rideconnect.entity.LocationHistory;
import com.rideconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface LocationHistoryRepository extends JpaRepository<LocationHistory, UUID> {

    List<LocationHistory> findByUser(User user);

    List<LocationHistory> findByUserOrderByRecordedAtDesc(User user);

    List<LocationHistory> findByTripId(UUID tripId);

    List<LocationHistory> findByUserAndRecordedAtBetween(User user, ZonedDateTime start, ZonedDateTime end);

    List<LocationHistory> findByTripIdOrderByRecordedAtAsc(UUID tripId);
}
