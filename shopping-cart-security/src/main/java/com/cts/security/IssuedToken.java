package com.cts.security;

import java.time.Instant;
import java.util.UUID;

public record IssuedToken(String value, UUID tokenId, UUID familyId, Instant expiresAt) {
}
