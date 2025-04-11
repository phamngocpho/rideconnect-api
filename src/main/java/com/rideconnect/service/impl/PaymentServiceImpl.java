package com.rideconnect.service.impl;

import com.rideconnect.dto.request.payment.CreatePaymentRequest;
import com.rideconnect.dto.request.payment.SavePaymentMethodRequest;
import com.rideconnect.dto.response.payment.PaymentDetailsResponse;
import com.rideconnect.dto.response.payment.PaymentMethodsResponse;
import com.rideconnect.entity.Payment;
import com.rideconnect.entity.PaymentMethod;
import com.rideconnect.entity.Trip;
import com.rideconnect.entity.User;
import com.rideconnect.exception.BadRequestException;
import com.rideconnect.exception.ResourceNotFoundException;
import com.rideconnect.repository.PaymentMethodRepository;
import com.rideconnect.repository.PaymentRepository;
import com.rideconnect.repository.TripRepository;
import com.rideconnect.repository.UserRepository;
import com.rideconnect.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PaymentDetailsResponse createPayment(String userId, CreatePaymentRequest request) {
        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", request.getTripId().toString()));

        // Check if user is the customer of this trip
        if (!trip.getCustomer().getCustomerId().equals(UUID.fromString(userId))) {
            throw new BadRequestException("You are not authorized to make payment for this trip");
        }

        // Check if trip is completed
        if (!"completed".equals(trip.getStatus())) {
            throw new BadRequestException("Cannot make payment for a trip that is not completed");
        }

        // Check if payment already exists
        if (paymentRepository.existsByTripTripId(request.getTripId())) {
            throw new BadRequestException("Payment already exists for this trip");
        }

        // Create payment
        Payment payment = Payment.builder()
                .trip(trip)
                .paymentMethod(request.getPaymentMethod())
                .amount(request.getAmount())
                .status("processing")
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Process payment (in a real app, this would integrate with a payment gateway)
        // For demo purposes, we'll just mark it as successful
        savedPayment.setStatus("completed");
        savedPayment.setProcessedAt(ZonedDateTime.now());
        savedPayment.setTransactionId(UUID.randomUUID().toString());

        // Update trip with actual fare
        trip.setActualFare(request.getAmount());
        tripRepository.save(trip);

        Payment finalPayment = paymentRepository.save(savedPayment);

        return mapPaymentToPaymentDetailsResponse(finalPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDetailsResponse getPaymentDetails(String userId, UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId.toString()));

        // Check if user is the customer of this trip
        if (!payment.getTrip().getCustomer().getCustomerId().equals(UUID.fromString(userId)) &&
                !payment.getTrip().getDriver().getDriverId().equals(UUID.fromString(userId))) {
            throw new BadRequestException("You are not authorized to view this payment");
        }

        return mapPaymentToPaymentDetailsResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentMethodsResponse getPaymentMethods(String userId) {
        List<PaymentMethod> paymentMethods = paymentMethodRepository.findByUserUserIdOrderByIsDefaultDesc(UUID.fromString(userId));

        List<PaymentMethodsResponse.PaymentMethodDto> paymentMethodDtos = paymentMethods.stream()
                .map(this::mapPaymentMethodToDto)
                .collect(Collectors.toList());

        String defaultMethod = paymentMethods.stream()
                .filter(PaymentMethod::getIsDefault)
                .map(PaymentMethod::getType)
                .findFirst()
                .orElse(null);

        return PaymentMethodsResponse.builder()
                .paymentMethods(paymentMethodDtos)
                .defaultPaymentMethod(defaultMethod)
                .build();
    }

    @Override
    @Transactional
    public void savePaymentMethod(String userId, SavePaymentMethodRequest request) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        PaymentMethod paymentMethod = PaymentMethod.builder()
                .user(user)
                .type(request.getType())
                .cardNumber(request.getCardNumber())
                .cardHolderName(request.getCardHolderName())
                .expiryDate(request.getExpiryDate())
                .walletId(request.getWalletId())
                .isDefault(request.getSetAsDefault() != null && request.getSetAsDefault())
                .build();

        // If this method is set as default, unset any existing default
        if (Boolean.TRUE.equals(request.getSetAsDefault())) {
            paymentMethodRepository.resetDefaultPaymentMethods(user.getUserId());
        }

        paymentMethodRepository.save(paymentMethod);
    }

    @Override
    @Transactional
    public void deletePaymentMethod(String userId, UUID methodId) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(methodId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method", "id", methodId.toString()));

        // Check if user is the owner of this payment method
        if (!paymentMethod.getUser().getUserId().equals(UUID.fromString(userId))) {
            throw new BadRequestException("You are not authorized to delete this payment method");
        }

        paymentMethodRepository.delete(paymentMethod);
    }

    private PaymentDetailsResponse mapPaymentToPaymentDetailsResponse(Payment payment) {
        return PaymentDetailsResponse.builder()
                .paymentId(payment.getPaymentId())
                .tripId(payment.getTrip().getTripId())
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .processedAt(payment.getProcessedAt())
                .build();
    }

    private PaymentMethodsResponse.PaymentMethodDto mapPaymentMethodToDto(PaymentMethod paymentMethod) {
        return PaymentMethodsResponse.PaymentMethodDto.builder()
                .id(paymentMethod.getMethodId())
                .type(paymentMethod.getType())
                .cardNumber(paymentMethod.getCardNumber())
                .cardHolderName(paymentMethod.getCardHolderName())
                .expiryDate(paymentMethod.getExpiryDate())
                .isDefault(paymentMethod.getIsDefault())
                .walletId(paymentMethod.getWalletId())
                .walletName(paymentMethod.getWalletName())
                .build();
    }
}
