package com.cts.user.api;

import java.util.Set;

public record UserResponse(Long id, String username, String email, String displayName, Set<String> roles) { }
