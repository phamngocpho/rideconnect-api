package com.rideconnect.service.impl;

import com.rideconnect.dto.request.customer.CustomerCreateRequest;
import com.rideconnect.dto.response.customer.CustomerResponse;
import com.rideconnect.dto.response.user.UserResponse;
import com.rideconnect.entity.Customer;
import com.rideconnect.entity.User;
import com.rideconnect.repository.CustomerRepository;
import com.rideconnect.repository.UserRepository;
import com.rideconnect.service.CustomerService;
import com.rideconnect.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    @Transactional
    public CustomerResponse createCustomer(UUID userId, CustomerCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        Customer customer = Customer.builder()
                .customerId(userId)
                .user(user)
                .defaultPaymentMethod(request.getDefaultPaymentMethod())
                .build();

        Customer savedCustomer = customerRepository.save(customer);
        return mapToCustomerResponse(savedCustomer);
    }

    @Override
    public CustomerResponse getCustomerById(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khách hàng với ID: " + customerId));
        return mapToCustomerResponse(customer);
    }

    @Override
    public Page<CustomerResponse> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(this::mapToCustomerResponse);
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomerPaymentMethod(UUID customerId, String paymentMethod) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khách hàng với ID: " + customerId));

        customer.setDefaultPaymentMethod(paymentMethod);
        Customer updatedCustomer = customerRepository.save(customer);
        return mapToCustomerResponse(updatedCustomer);
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomerRating(UUID customerId, BigDecimal rating) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khách hàng với ID: " + customerId));

        customer.setRating(rating);
        Customer updatedCustomer = customerRepository.save(customer);
        return mapToCustomerResponse(updatedCustomer);
    }

    @Override
    @Transactional
    public CustomerResponse incrementCustomerTrips(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khách hàng với ID: " + customerId));

        customer.setTotalTrips(customer.getTotalTrips() + 1);
        Customer updatedCustomer = customerRepository.save(customer);
        return mapToCustomerResponse(updatedCustomer);
    }

    @Override
    @Transactional
    public void deleteCustomer(UUID customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new EntityNotFoundException("Không tìm thấy khách hàng với ID: " + customerId);
        }
        customerRepository.deleteById(customerId);
    }

    private CustomerResponse mapToCustomerResponse(Customer customer) {
        UserResponse userResponse = userService.getUserById(customer.getCustomerId());

        return CustomerResponse.builder()
                .customerId(customer.getCustomerId())
                .user(userResponse)
                .defaultPaymentMethod(customer.getDefaultPaymentMethod())
                .rating(customer.getRating())
                .totalTrips(customer.getTotalTrips())
                .build();
    }
}
