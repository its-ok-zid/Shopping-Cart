package com.cts.user.controller;

import com.cts.common.api.ApiResponse;
import com.cts.security.SecurityPrincipal;
import com.cts.user.api.UpdateProfileRequest;
import com.cts.user.api.UserResponse;
import com.cts.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(
            @AuthenticationPrincipal SecurityPrincipal principal) {
        Long userId = Long.parseLong(principal.userId());
        return ResponseEntity.ok(ApiResponse.ok("Profile fetched successfully", 
                userService.getCurrentUserProfile(userId)));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal SecurityPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request) {
        Long userId = Long.parseLong(principal.userId());
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", 
                userService.updateProfile(userId, request)));
    }
}