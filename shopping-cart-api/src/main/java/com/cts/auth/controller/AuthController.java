package com.cts.auth.api;

import com.cts.auth.service.AuthService;
import com.cts.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse.UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse.UserResponse userResponse = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("User registered successfully", userResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthService.LoginResult result = authService.login(request);
        
        return ResponseEntity.ok()
                // Safely inject the HttpOnly cookie into the response header
                .header(HttpHeaders.SET_COOKIE, result.cookie().toString()) 
                .body(ApiResponse.ok("Login successful", result.response()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @CookieValue(name = "shopping_refresh", required = false) String refreshToken) {
        
        AuthService.LoginResult result = authService.refreshToken(refreshToken);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, result.cookie().toString())
                .body(ApiResponse.ok("Token refreshed successfully", result.response()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = "shopping_refresh", required = false) String refreshToken) {
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authService.logout(refreshToken).toString())
                .body(ApiResponse.ok("Logged out successfully", null));
    }
}