package com.cts.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader; // <-- NEW IMPORT
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/** Creates and verifies signed HMAC JWTs. The configured secret must be Base64-encoded and at least 256 bits. */
public final class JwtTokenService {

    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String ROLES_CLAIM = "roles";
    private static final String USERNAME_CLAIM = "username";
    private static final String FAMILY_ID_CLAIM = "family_id";

    private final SecurityProperties properties;
    private final JwtEncoder encoder;
    private final JwtDecoder decoder;

    public JwtTokenService(SecurityProperties properties) {
        this.properties = properties;
        SecretKey signingKey = signingKey(properties.getJwtSecret());

        this.encoder = new NimbusJwtEncoder(new ImmutableSecret<SecurityContext>(signingKey));

        NimbusJwtDecoder configuredDecoder = NimbusJwtDecoder.withSecretKey(signingKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        configuredDecoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(properties.getIssuer()));
        this.decoder = configuredDecoder;
    }

    public IssuedToken issueAccessToken(SecurityPrincipal principal) {
        return issue(principal, TokenType.ACCESS, null, properties.getAccessTokenTtl());
    }

    public IssuedToken issueRefreshToken(SecurityPrincipal principal, UUID familyId) {
        return issue(principal, TokenType.REFRESH, familyId == null ? UUID.randomUUID() : familyId,
                properties.getRefreshTokenTtl());
    }

    public ParsedToken parse(String rawToken, TokenType expectedType) {
        Jwt jwt = decoder.decode(rawToken);

        String type = jwt.getClaimAsString(TOKEN_TYPE_CLAIM);
        if (!expectedType.name().equals(type)) {
            throw new JwtException("Unexpected token type");
        }

        String subject = jwt.getSubject();
        String username = jwt.getClaimAsString(USERNAME_CLAIM);
        String tokenId = jwt.getId();

        if (subject == null || username == null || tokenId == null || jwt.getExpiresAt() == null) {
            throw new JwtException("Required token claims are missing");
        }

        List<String> rolesClaim = jwt.getClaimAsStringList(ROLES_CLAIM);

        UUID familyId = null;
        if (expectedType == TokenType.REFRESH) {
            String familyClaim = jwt.getClaimAsString(FAMILY_ID_CLAIM);
            if (familyClaim == null) {
                throw new JwtException("Refresh token family is missing");
            }
            familyId = UUID.fromString(familyClaim);
        }

        return new ParsedToken(subject, username, rolesClaim == null ? Set.of() : Set.copyOf(rolesClaim),
                expectedType, UUID.fromString(tokenId), familyId, jwt.getExpiresAt());
    }

    private IssuedToken issue(SecurityPrincipal principal, TokenType tokenType, UUID familyId, java.time.Duration ttl) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(ttl);
        UUID tokenId = UUID.randomUUID();

        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer(properties.getIssuer())
                .subject(principal.userId())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .id(tokenId.toString())
                .claim(TOKEN_TYPE_CLAIM, tokenType.name())
                .claim(USERNAME_CLAIM, principal.username())
                .claim(ROLES_CLAIM, principal.roles().stream().sorted().toList());

        if (familyId != null) {
            claims.claim(FAMILY_ID_CLAIM, familyId.toString());
        }

        // --- THE FIX ---
        // We explicitly tell Spring Security to use HS256 for the JWT Header
        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();

        // We pass BOTH the header and the claims into the encoder
        String encoded = encoder.encode(JwtEncoderParameters.from(jwsHeader, claims.build())).getTokenValue();

        return new IssuedToken(encoded, tokenId, familyId, expiresAt);
    }

    private static SecretKey signingKey(String base64Secret) {
        try {
            byte[] key = Base64.getDecoder().decode(base64Secret);
            if (key.length < 32) {
                throw new IllegalArgumentException("JWT secret must decode to at least 32 bytes");
            }
            return new SecretKeySpec(key, "HmacSHA256");
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("app.security.jwt-secret must be a Base64-encoded 256-bit secret", exception);
        }
    }
}