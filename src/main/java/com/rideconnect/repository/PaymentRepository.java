package com.rideconnect.repository;

import com.rideconnect.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByTripTripId(UUID tripId);

    boolean existsByTripTripId(UUID tripId);

    List<Payment> findByTripCustomerCustomerId(UUID customerId);

    List<Payment> findByTripDriverDriverId(UUID driverId);
}
