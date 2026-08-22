package com.cts.security;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record ParsedToken(
        String subject,
        String username,
        Set<String> roles,
        TokenType type,
        UUID tokenId,
        UUID familyId,
        Instant expiresAt
) {
    public ParsedToken {
        roles = Set.copyOf(roles);
    }
}
