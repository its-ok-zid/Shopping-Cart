package com.cts.user.service;

import com.cts.user.api.RoleUpdateRequest;
import com.cts.user.api.UserResponse;

public interface UserService {
    UserResponse updateUserRoles(Long userId, RoleUpdateRequest request);
}