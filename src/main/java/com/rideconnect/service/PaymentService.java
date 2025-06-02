package com.rideconnect.service;

import com.rideconnect.entity.Payment;
import com.rideconnect.entity.Trip;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentService {

    Payment save(Payment payment);

    Payment findById(UUID paymentId);

    List<Payment> findAll();

    Optional<Payment> findByTrip(Trip trip);

    List<Payment> findByStatus(String status);

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByPaymentMethod(String paymentMethod);

    Payment createPayment(Trip trip, String paymentMethod, BigDecimal amount);

    Payment processPayment(UUID paymentId, String transactionId);

    Payment updatePaymentStatus(UUID paymentId, String status);

    void delete(UUID paymentId);
}
