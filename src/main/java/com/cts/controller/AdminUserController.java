package com.cts.controller;

import com.cts.exception.ApiResponse;
import com.cts.exception.ItemNotFoundException;
import com.cts.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserRepository userRepository;

    public AdminUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @PathVariable Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ItemNotFoundException("User not found with id: " + userId);
        }

        userRepository.deleteById(userId);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("User deleted successfully")
                        .data(null)
                        .build()
        );
    }
}
