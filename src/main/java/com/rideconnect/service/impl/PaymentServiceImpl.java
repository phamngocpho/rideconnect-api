package com.rideconnect.service.impl;

import com.rideconnect.entity.Payment;
import com.rideconnect.entity.Trip;
import com.rideconnect.repository.PaymentRepository;
import com.rideconnect.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public Payment findById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
    }

    @Override
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    @Override
    public Optional<Payment> findByTrip(Trip trip) {
        return paymentRepository.findByTrip(trip);
    }

    @Override
    public List<Payment> findByStatus(String status) {
        return paymentRepository.findByStatus(status);
    }

    @Override
    public Optional<Payment> findByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId);
    }

    @Override
    public List<Payment> findByPaymentMethod(String paymentMethod) {
        return paymentRepository.findByPaymentMethod(paymentMethod);
    }

    @Override
    @Transactional
    public Payment createPayment(Trip trip, String paymentMethod, BigDecimal amount) {
        Payment payment = Payment.builder()
                .trip(trip)
                .paymentMethod(paymentMethod)
                .amount(amount)
                .status("pending")
                .build();
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public Payment processPayment(UUID paymentId, String transactionId) {
        Payment payment = findById(paymentId);
        payment.setTransactionId(transactionId);
        payment.setStatus("completed");
        payment.setProcessedAt(ZonedDateTime.now());
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public Payment updatePaymentStatus(UUID paymentId, String status) {
        Payment payment = findById(paymentId);
        payment.setStatus(status);
        if ("completed".equals(status)) {
            payment.setProcessedAt(ZonedDateTime.now());
        }
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public void delete(UUID paymentId) {
        paymentRepository.deleteById(paymentId);
    }
}
