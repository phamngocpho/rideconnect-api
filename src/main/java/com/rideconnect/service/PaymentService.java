package com.rideconnect.service;

import com.rideconnect.dto.request.payment.CreatePaymentRequest;
import com.rideconnect.dto.request.payment.SavePaymentMethodRequest;
import com.rideconnect.dto.response.payment.PaymentDetailsResponse;
import com.rideconnect.dto.response.payment.PaymentMethodsResponse;

import java.util.UUID;

public interface PaymentService {

    /**
     * Create a new payment
     *
     * @param userId user ID (customer)
     * @param request payment details
     * @return created payment details
     */
    PaymentDetailsResponse createPayment(String userId, CreatePaymentRequest request);

    /**
     * Get payment details
     *
     * @param userId user ID (customer or driver)
     * @param paymentId payment ID
     * @return payment details
     */
    PaymentDetailsResponse getPaymentDetails(String userId, UUID paymentId);

    /**
     * Get user's payment methods
     *
     * @param userId user ID
     * @return list of payment methods
     */
    PaymentMethodsResponse getPaymentMethods(String userId);

    /**
     * Save a new payment method
     *
     * @param userId user ID
     * @param request payment method details
     */
    void savePaymentMethod(String userId, SavePaymentMethodRequest request);

    /**
     * Delete a payment method
     *
     * @param userId user ID
     * @param methodId payment method ID
     */
    void deletePaymentMethod(String userId, UUID methodId);
}
