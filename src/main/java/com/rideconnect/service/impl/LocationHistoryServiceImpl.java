package com.rideconnect.service.impl;

import com.rideconnect.entity.LocationHistory;
import com.rideconnect.entity.User;
import com.rideconnect.repository.LocationHistoryRepository;
import com.rideconnect.service.LocationHistoryService;
import org.postgis.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LocationHistoryServiceImpl implements LocationHistoryService {

    private final LocationHistoryRepository locationHistoryRepository;

    @Autowired
    public LocationHistoryServiceImpl(LocationHistoryRepository locationHistoryRepository) {
        this.locationHistoryRepository = locationHistoryRepository;
    }

    @Override
    @Transactional
    public LocationHistory save(LocationHistory locationHistory) {
        return locationHistoryRepository.save(locationHistory);
    }

    @Override
    public LocationHistory findById(UUID locationId) {
        return locationHistoryRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location history not found with id: " + locationId));
    }

    @Override
    public List<LocationHistory> findAll() {
        return locationHistoryRepository.findAll();
    }

    @Override
    public List<LocationHistory> findByUser(User user) {
        return locationHistoryRepository.findByUser(user);
    }

    @Override
    public List<LocationHistory> findByUserOrderByRecordedAtDesc(User user) {
        return locationHistoryRepository.findByUserOrderByRecordedAtDesc(user);
    }

    @Override
    public List<LocationHistory> findByTripId(UUID tripId) {
        return locationHistoryRepository.findByTripId(tripId);
    }

    @Override
    public List<LocationHistory> findByUserAndRecordedAtBetween(User user, ZonedDateTime start, ZonedDateTime end) {
        return locationHistoryRepository.findByUserAndRecordedAtBetween(user, start, end);
    }

    @Override
    public List<LocationHistory> findByTripIdOrderByRecordedAtAsc(UUID tripId) {
        return locationHistoryRepository.findByTripIdOrderByRecordedAtAsc(tripId);
    }

    @Override
    @Transactional
    public void recordUserLocation(User user, Point location, Float heading, Float speed, UUID tripId) {
        LocationHistory locationHistory = LocationHistory.builder()
                .user(user)
                .location(location)
                .heading(heading)
                .speed(speed)
                .tripId(tripId)
                .build();
        locationHistoryRepository.save(locationHistory);
    }

    @Override
    @Transactional
    public void delete(UUID locationId) {
        locationHistoryRepository.deleteById(locationId);
    }
}
