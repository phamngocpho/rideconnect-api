package com.rideconnect.controller;

import com.rideconnect.dto.request.payment.CreatePaymentRequest;
import com.rideconnect.dto.request.payment.SavePaymentMethodRequest;
import com.rideconnect.dto.response.payment.PaymentDetailsResponse;
import com.rideconnect.dto.response.payment.PaymentMethodsResponse;
import com.rideconnect.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentDetailsResponse> createPayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreatePaymentRequest request) {
        String userId = userDetails.getUsername();
        PaymentDetailsResponse response = paymentService.createPayment(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDetailsResponse> getPaymentDetails(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID paymentId) {
        String userId = userDetails.getUsername();
        PaymentDetailsResponse response = paymentService.getPaymentDetails(userId, paymentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/methods")
    public ResponseEntity<PaymentMethodsResponse> getPaymentMethods(
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        PaymentMethodsResponse response = paymentService.getPaymentMethods(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/methods")
    public ResponseEntity<Void> savePaymentMethod(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SavePaymentMethodRequest request) {
        String userId = userDetails.getUsername();
        paymentService.savePaymentMethod(userId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/methods/{methodId}")
    public ResponseEntity<Void> deletePaymentMethod(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID methodId) {
        String userId = userDetails.getUsername();
        paymentService.deletePaymentMethod(userId, methodId);
        return ResponseEntity.ok().build();
    }
}

