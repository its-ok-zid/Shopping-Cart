package com.cts.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {

    @NotBlank
    private String jwtSecret;
    @NotBlank
    private String issuer = "shopping-cart-api";
    @NotNull
    private Duration accessTokenTtl = Duration.ofMinutes(15);
    @NotNull
    private Duration refreshTokenTtl = Duration.ofDays(14);
    @NotBlank
    private String refreshCookieName = "shopping_refresh";
    private boolean secureCookies = true;

    public String getJwtSecret() { return jwtSecret; }
    public void setJwtSecret(String jwtSecret) { this.jwtSecret = jwtSecret; }
    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }
    public Duration getAccessTokenTtl() { return accessTokenTtl; }
    public void setAccessTokenTtl(Duration accessTokenTtl) { this.accessTokenTtl = accessTokenTtl; }
    public Duration getRefreshTokenTtl() { return refreshTokenTtl; }
    public void setRefreshTokenTtl(Duration refreshTokenTtl) { this.refreshTokenTtl = refreshTokenTtl; }
    public String getRefreshCookieName() { return refreshCookieName; }
    public void setRefreshCookieName(String refreshCookieName) { this.refreshCookieName = refreshCookieName; }
    public boolean isSecureCookies() { return secureCookies; }
    public void setSecureCookies(boolean secureCookies) { this.secureCookies = secureCookies; }
}
