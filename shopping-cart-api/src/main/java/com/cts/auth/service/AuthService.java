package com.cts.auth.service;

import com.cts.auth.api.AuthResponse;
import com.cts.auth.api.LoginRequest;
import com.cts.auth.api.RegisterRequest;
import org.springframework.http.ResponseCookie;

public interface AuthService {
    
    AuthResponse.UserResponse register(RegisterRequest request);
    
    LoginResult login(LoginRequest request);
    
    LoginResult refreshToken(String refreshToken);
    
    ResponseCookie logout(String refreshToken);

    // A handy record to return both the JSON body and the HttpOnly Cookie to the controller
    record LoginResult(AuthResponse response, ResponseCookie cookie) {}
}