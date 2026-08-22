package com.cts.security;

import java.util.Set;

/** Application-neutral authenticated identity carried by a verified access token. */
public record SecurityPrincipal(String userId, String username, Set<String> roles) {
    public SecurityPrincipal {
        roles = Set.copyOf(roles);
    }
}
