package com.cts.auth.api;

import java.time.Instant;
import java.util.Set;

public record AuthResponse(String accessToken, Instant accessTokenExpiresAt, UserResponse user) {
    public record UserResponse(Long id, String username, String email, String displayName, Set<String> roles) { }
}
