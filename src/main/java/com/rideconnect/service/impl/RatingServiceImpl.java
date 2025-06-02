package com.rideconnect.service.impl;

import com.rideconnect.entity.Rating;
import com.rideconnect.entity.Trip;
import com.rideconnect.entity.User;
import com.rideconnect.repository.RatingRepository;
import com.rideconnect.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;

    @Autowired
    public RatingServiceImpl(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    @Override
    @Transactional
    public Rating save(Rating rating) {
        return ratingRepository.save(rating);
    }

    @Override
    public Rating findById(UUID ratingId) {
        return ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RuntimeException("Rating not found with id: " + ratingId));
    }

    @Override
    public List<Rating> findAll() {
        return ratingRepository.findAll();
    }

    @Override
    public List<Rating> findByRatedUser(User ratedUser) {
        return ratingRepository.findByRatedUser(ratedUser);
    }

    @Override
    public List<Rating> findByRater(User rater) {
        return ratingRepository.findByRater(rater);
    }

    @Override
    public List<Rating> findByTrip(Trip trip) {
        return ratingRepository.findByTrip(trip);
    }

    @Override
    public Optional<Rating> findByTripAndRaterAndRatedUser(Trip trip, User rater, User ratedUser) {
        return ratingRepository.findByTripAndRaterAndRatedUser(trip, rater, ratedUser);
    }

    @Override
    public Double getAverageRatingForUser(User user) {
        return ratingRepository.getAverageRatingForUser(user);
    }

    @Override
    public Long countRatingsByRatedUser(User user) {
        return ratingRepository.countRatingsByRatedUser(user);
    }

    @Override
    @Transactional
    public Rating createRating(Trip trip, User rater, User ratedUser, Integer ratingValue, String comment) {
        // Kiểm tra xem đã có đánh giá chưa
        Optional<Rating> existingRating = ratingRepository.findByTripAndRaterAndRatedUser(trip, rater, ratedUser);
        if (existingRating.isPresent()) {
            throw new RuntimeException("Rating already exists for this trip, rater and rated user");
        }

        // Kiểm tra giá trị đánh giá hợp lệ
        if (ratingValue < 1 || ratingValue > 5) {
            throw new IllegalArgumentException("Rating value must be between 1 and 5");
        }

        Rating rating = Rating.builder()
                .trip(trip)
                .rater(rater)
                .ratedUser(ratedUser)
                .ratingValue(ratingValue)
                .comment(comment)
                .build();
        return ratingRepository.save(rating);
    }

    @Override
    @Transactional
    public void delete(UUID ratingId) {
        ratingRepository.deleteById(ratingId);
    }
}


