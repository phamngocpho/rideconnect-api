package com.rideconnect.service;

import com.rideconnect.dto.request.user.UpdateProfileRequest;
import com.rideconnect.dto.response.user.ProfileResponse;

public interface UserService {

    /**
     * Get user profile
     *
     * @param userId user ID
     * @return user profile details
     */
    ProfileResponse getProfile(String userId);

    /**
     * Update user profile
     *
     * @param userId user ID
     * @param request profile update details
     * @return updated profile
     */
    ProfileResponse updateProfile(String userId, UpdateProfileRequest request);

    /**
     * Upload profile picture
     *
     * @param userId user ID
     * @param imageData image data
     * @return updated profile with new image URL
     */
    ProfileResponse uploadProfilePicture(String userId, byte[] imageData);
}
