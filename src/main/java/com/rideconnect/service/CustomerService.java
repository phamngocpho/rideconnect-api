package com.rideconnect.service;

import com.rideconnect.dto.request.customer.CustomerCreateRequest;
import com.rideconnect.dto.response.customer.CustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface CustomerService {
    CustomerResponse createCustomer(UUID userId, CustomerCreateRequest request);
    CustomerResponse getCustomerById(UUID customerId);
    Page<CustomerResponse> getAllCustomers(Pageable pageable);
    CustomerResponse updateCustomerPaymentMethod(UUID customerId, String paymentMethod);
    CustomerResponse updateCustomerRating(UUID customerId, BigDecimal rating);
    CustomerResponse incrementCustomerTrips(UUID customerId);
    void deleteCustomer(UUID customerId);
}
