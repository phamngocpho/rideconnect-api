package com.rideconnect.service;

import com.rideconnect.dto.request.payment.CreatePaymentRequest;
import com.rideconnect.dto.request.payment.SavePaymentMethodRequest;
import com.rideconnect.dto.response.payment.PaymentDetailsResponse;
import com.rideconnect.dto.response.payment.PaymentMethodsResponse;
import com.rideconnect.security.CustomUserDetails;

import java.util.UUID;

public interface PaymentService {

    /**
     * Create a new payment
     *
     * @param userDetails authenticated user details (customer)
     * @param request payment details
     * @return created payment details
     */
    PaymentDetailsResponse createPayment(CustomUserDetails userDetails, CreatePaymentRequest request);

    /**
     * Get payment details
     *
     * @param userDetails authenticated user details (customer or driver)
     * @param paymentId payment ID
     * @return payment details
     */
    PaymentDetailsResponse getPaymentDetails(CustomUserDetails userDetails, UUID paymentId);

    /**
     * Get user's payment methods
     *
     * @param userDetails authenticated user details
     * @return list of payment methods
     */
    PaymentMethodsResponse getPaymentMethods(CustomUserDetails userDetails);

    /**
     * Save a new payment method
     *
     * @param userDetails authenticated user details
     * @param request payment method details
     */
    void savePaymentMethod(CustomUserDetails userDetails, SavePaymentMethodRequest request);

    /**
     * Delete a payment method
     *
     * @param userDetails authenticated user details
     * @param methodId payment method ID
     */
    void deletePaymentMethod(CustomUserDetails userDetails, UUID methodId);
}