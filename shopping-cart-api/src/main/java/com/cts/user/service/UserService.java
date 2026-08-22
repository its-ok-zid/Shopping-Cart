package com.cts.user.service;

import com.cts.user.api.RoleUpdateRequest;
import com.cts.user.api.UpdateProfileRequest;
import com.cts.user.api.UserResponse;

public interface UserService {
    UserResponse getCurrentUserProfile(Long userId);
    UserResponse updateProfile(Long userId, UpdateProfileRequest request);
    UserResponse updateUserRoles(Long userId, RoleUpdateRequest request);
}