package com.rideconnect.controller;

import com.rideconnect.dto.request.payment.CreatePaymentRequest;
import com.rideconnect.dto.request.payment.SavePaymentMethodRequest;
import com.rideconnect.dto.response.payment.PaymentDetailsResponse;
import com.rideconnect.dto.response.payment.PaymentMethodsResponse;
import com.rideconnect.security.CustomUserDetails;
import com.rideconnect.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentDetailsResponse> createPayment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreatePaymentRequest request) {
        PaymentDetailsResponse response = paymentService.createPayment(userDetails, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDetailsResponse> getPaymentDetails(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID paymentId) {
        PaymentDetailsResponse response = paymentService.getPaymentDetails(userDetails, paymentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/methods")
    public ResponseEntity<PaymentMethodsResponse> getPaymentMethods(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        PaymentMethodsResponse response = paymentService.getPaymentMethods(userDetails);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/methods")
    public ResponseEntity<Void> savePaymentMethod(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody SavePaymentMethodRequest request) {
        paymentService.savePaymentMethod(userDetails, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/methods/{methodId}")
    public ResponseEntity<Void> deletePaymentMethod(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID methodId) {
        paymentService.deletePaymentMethod(userDetails, methodId);
        return ResponseEntity.ok().build();
    }
}