package com.rideconnect.service;

import com.rideconnect.dto.request.customer.SaveAddressRequest;
import com.rideconnect.dto.response.customer.CustomerDashboardResponse;
import com.rideconnect.dto.response.customer.CustomerProfileResponse;

import java.util.UUID;

public interface CustomerService {

    /**
     * Get customer profile
     *
     * @param userId user ID
     * @return customer profile details
     */
    CustomerProfileResponse getCustomerProfile(String userId);

    /**
     * Save address (home or work)
     *
     * @param userId user ID
     * @param request address details
     * @return updated customer profile
     */
    CustomerProfileResponse saveAddress(String userId, SaveAddressRequest request);

    /**
     * Get customer dashboard with stats
     *
     * @param userId user ID
     * @return dashboard data
     */
    CustomerDashboardResponse getCustomerDashboard(String userId);
}
