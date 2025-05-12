package com.rideconnect.controller.admin;

import com.rideconnect.dto.request.customer.CustomerCreateRequest;
import com.rideconnect.dto.response.customer.CustomerResponse;
import com.rideconnect.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/customers")
@RequiredArgsConstructor
public class AdminCustomerController {

    private final CustomerService customerService;

    @PostMapping("/{userId}")
    public ResponseEntity<CustomerResponse> createCustomer(
            @PathVariable UUID userId,
            @Valid @RequestBody CustomerCreateRequest request) {
        return new ResponseEntity<>(customerService.createCustomer(userId, request), HttpStatus.CREATED);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable UUID customerId) {
        return ResponseEntity.ok(customerService.getCustomerById(customerId));
    }

    @GetMapping
    public ResponseEntity<Page<CustomerResponse>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rating") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(customerService.getAllCustomers(pageable));
    }

    @PatchMapping("/{customerId}/payment-method")
    public ResponseEntity<CustomerResponse> updateCustomerPaymentMethod(
            @PathVariable UUID customerId,
            @RequestParam String paymentMethod) {
        return ResponseEntity.ok(customerService.updateCustomerPaymentMethod(customerId, paymentMethod));
    }

    @PatchMapping("/{customerId}/rating")
    public ResponseEntity<CustomerResponse> updateCustomerRating(
            @PathVariable UUID customerId,
            @RequestParam BigDecimal rating) {
        return ResponseEntity.ok(customerService.updateCustomerRating(customerId, rating));
    }

    @PatchMapping("/{customerId}/increment-trips")
    public ResponseEntity<CustomerResponse> incrementCustomerTrips(@PathVariable UUID customerId) {
        return ResponseEntity.ok(customerService.incrementCustomerTrips(customerId));
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}
