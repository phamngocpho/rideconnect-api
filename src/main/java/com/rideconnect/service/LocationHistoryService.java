package com.rideconnect.service;

import com.rideconnect.entity.LocationHistory;
import com.rideconnect.entity.User;
import org.postgis.Point;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface LocationHistoryService {

    LocationHistory save(LocationHistory locationHistory);

    LocationHistory findById(UUID locationId);

    List<LocationHistory> findAll();

    List<LocationHistory> findByUser(User user);

    List<LocationHistory> findByUserOrderByRecordedAtDesc(User user);

    List<LocationHistory> findByTripId(UUID tripId);

    List<LocationHistory> findByUserAndRecordedAtBetween(User user, ZonedDateTime start, ZonedDateTime end);

    List<LocationHistory> findByTripIdOrderByRecordedAtAsc(UUID tripId);

    void recordUserLocation(User user, Point location, Float heading, Float speed, UUID tripId);

    void delete(UUID locationId);
}
