package com.rideconnect.repository;

import com.rideconnect.entity.Payment;
import com.rideconnect.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByTrip(Trip trip);

    List<Payment> findByStatus(String status);

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByPaymentMethod(String paymentMethod);
}
