package com.cts.user.controller;

import com.cts.common.api.ApiResponse;
import com.cts.user.api.RoleUpdateRequest;
import com.cts.user.api.UserResponse;
import com.cts.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')") // CRITICAL: Only Admins can access this controller!
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<ApiResponse<UserResponse>> updateRoles(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequest request) {
        
        UserResponse response = userService.updateUserRoles(id, request);
        return ResponseEntity.ok(ApiResponse.ok("User roles updated successfully", response));
    }
}