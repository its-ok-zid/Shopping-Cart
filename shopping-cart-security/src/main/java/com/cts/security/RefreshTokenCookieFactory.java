package com.cts.security;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

/** Produces the only authentication cookie used by this API: the HttpOnly refresh token. */
@Component
public class RefreshTokenCookieFactory {
    private final SecurityProperties properties;

    public RefreshTokenCookieFactory(SecurityProperties properties) {
        this.properties = properties;
    }

    public ResponseCookie create(String token) {
        return ResponseCookie.from(properties.getRefreshCookieName(), token)
                .httpOnly(true)
                .secure(properties.isSecureCookies())
                .sameSite("Strict")
                .path("/api/auth")
                .maxAge(properties.getRefreshTokenTtl())
                .build();
    }

    public ResponseCookie clear() {
        return ResponseCookie.from(properties.getRefreshCookieName(), "")
                .httpOnly(true)
                .secure(properties.isSecureCookies())
                .sameSite("Strict")
                .path("/api/auth")
                .maxAge(0)
                .build();
    }
}
