package com.cts.user.api;

import com.cts.user.domain.Role;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record RoleUpdateRequest(@NotEmpty Set<Role> roles) { }
