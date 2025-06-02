package com.rideconnect.controller.admin;

import com.rideconnect.entity.Payment;
import com.rideconnect.entity.Trip;
import com.rideconnect.service.PaymentService;
import com.rideconnect.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/payments")
public class PaymentAdminController {

    private final PaymentService paymentService;
    private final TripService tripService;

    @Autowired
    public PaymentAdminController(PaymentService paymentService, TripService tripService) {
        this.paymentService = paymentService;
        this.tripService = tripService;
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.findAll();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable UUID id) {
        Payment payment = paymentService.findById(id);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<Payment> getPaymentByTrip(@PathVariable UUID tripId) {
        Trip trip = tripService.findById(tripId);
        Optional<Payment> payment = paymentService.findByTrip(trip);
        return payment.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable String status) {
        List<Payment> payments = paymentService.findByStatus(status);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<Payment> getPaymentByTransactionId(@PathVariable String transactionId) {
        Optional<Payment> payment = paymentService.findByTransactionId(transactionId);
        return payment.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/method/{paymentMethod}")
    public ResponseEntity<List<Payment>> getPaymentsByMethod(@PathVariable String paymentMethod) {
        List<Payment> payments = paymentService.findByPaymentMethod(paymentMethod);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/create")
    public ResponseEntity<Payment> createPayment(
            @RequestParam UUID tripId,
            @RequestParam String paymentMethod,
            @RequestParam BigDecimal amount) {

        Trip trip = tripService.findById(tripId);
        Payment payment = paymentService.createPayment(trip, paymentMethod, amount);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @PutMapping("/{id}/process")
    public ResponseEntity<Payment> processPayment(
            @PathVariable UUID id,
            @RequestParam String transactionId) {

        Payment payment = paymentService.processPayment(id, transactionId);
        return ResponseEntity.ok(payment);
    }

    @PutMapping("/{id}/update-status")
    public ResponseEntity<Payment> updatePaymentStatus(
            @PathVariable UUID id,
            @RequestParam String status) {

        Payment payment = paymentService.updatePaymentStatus(id, status);
        return ResponseEntity.ok(payment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable UUID id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
