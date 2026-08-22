package com.cts.auth.service;

import com.cts.auth.api.AuthResponse;
import com.cts.auth.api.LoginRequest;
import com.cts.auth.api.RegisterRequest;
import com.cts.auth.domain.RefreshToken;
import com.cts.auth.repository.RefreshTokenRepository;
import com.cts.common.error.ApiException;
import com.cts.security.IssuedToken;
import com.cts.security.JwtTokenService;
import com.cts.security.ParsedToken;
import com.cts.security.RefreshTokenCookieFactory;
import com.cts.security.SecurityPrincipal;
import com.cts.security.TokenHasher;
import com.cts.security.TokenType;
import com.cts.user.domain.AppUser;
import com.cts.user.repository.UserRepository;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenCookieFactory cookieFactory;

    public AuthServiceImpl(UserRepository userRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenService jwtTokenService,
                           RefreshTokenCookieFactory cookieFactory) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.cookieFactory = cookieFactory;
    }

    @Override
    @Transactional
    public AuthResponse.UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsernameIgnoreCase(request.username())) {
            throw ApiException.conflict("USERNAME_TAKEN", "Username is already in use");
        }
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw ApiException.conflict("EMAIL_TAKEN", "Email is already in use");
        }

        AppUser user = new AppUser(
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password()),
                request.displayName()
        );
        
        userRepository.save(user);
        return mapToUserResponse(user);
    }

    @Override
    @Transactional
    public LoginResult login(LoginRequest request) {
        AppUser user = userRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(request.identifier(), request.identifier())
                .orElseThrow(() -> ApiException.badRequest("BAD_CREDENTIALS", "Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw ApiException.badRequest("BAD_CREDENTIALS", "Invalid credentials");
        }
        
        if (!user.isEnabled()) {
            throw ApiException.forbidden("User account is disabled");
        }

        return generateTokens(user, null);
    }

    @Override
    @Transactional
    public LoginResult refreshToken(String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            throw ApiException.forbidden("Refresh token is missing");
        }

        ParsedToken parsedToken;
        try {
            parsedToken = jwtTokenService.parse(rawRefreshToken, TokenType.REFRESH);
        } catch (Exception e) {
            throw ApiException.forbidden("Invalid refresh token");
        }

        // We hash the token ID to look it up, avoiding storing raw tokens in the DB
        String tokenIdHash = TokenHasher.sha256(parsedToken.tokenId().toString());
        
        RefreshToken storedToken = refreshTokenRepository.findByTokenIdHashForUpdate(tokenIdHash)
                .orElseThrow(() -> ApiException.forbidden("Refresh token not found"));

        AppUser user = storedToken.getUser();

        if (!storedToken.isActiveAt(Instant.now())) {
            // Token reuse detected! Someone tried to use an old token. Revoke the entire token family.
            if (storedToken.getRevokedAt() != null) {
                refreshTokenRepository.findByFamilyIdAndRevokedAtIsNull(storedToken.getFamilyId())
                        .forEach(RefreshToken::revoke);
            }
            throw ApiException.forbidden("Refresh token expired or revoked");
        }

        if (!user.isEnabled()) {
            throw ApiException.forbidden("User account is disabled");
        }

        // Token Rotation: Revoke the token just used, and issue a brand new pair
        storedToken.revoke();

        return generateTokens(user, storedToken.getFamilyId());
    }

    @Override
    @Transactional
    public ResponseCookie logout(String rawRefreshToken) {
        if (rawRefreshToken != null && !rawRefreshToken.isBlank()) {
            try {
                ParsedToken parsedToken = jwtTokenService.parse(rawRefreshToken, TokenType.REFRESH);
                String tokenIdHash = TokenHasher.sha256(parsedToken.tokenId().toString());
                refreshTokenRepository.findByTokenIdHashForUpdate(tokenIdHash)
                        .ifPresent(RefreshToken::revoke);
            } catch (Exception ignored) {
                // Ignore parse errors on logout, just clear the cookie
            }
        }
        return cookieFactory.clear();
    }

    private LoginResult generateTokens(AppUser user, UUID familyId) {
        Set<String> roleStrings = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
        
        SecurityPrincipal principal = new SecurityPrincipal(user.getId().toString(), user.getUsername(), roleStrings);

        IssuedToken accessToken = jwtTokenService.issueAccessToken(principal);
        IssuedToken refreshToken = jwtTokenService.issueRefreshToken(principal, familyId);

        // Store only the hashed signature of the refresh token
        RefreshToken tokenEntity = new RefreshToken(
                user,
                TokenHasher.sha256(refreshToken.tokenId().toString()),
                refreshToken.familyId(),
                refreshToken.expiresAt()
        );
        refreshTokenRepository.save(tokenEntity);

        AuthResponse response = new AuthResponse(
                accessToken.value(),
                accessToken.expiresAt(),
                mapToUserResponse(user)
        );

        // Package the raw refresh token into the secure HttpOnly cookie
        ResponseCookie cookie = cookieFactory.create(refreshToken.value());

        return new LoginResult(response, cookie);
    }

    private AuthResponse.UserResponse mapToUserResponse(AppUser user) {
        Set<String> roleStrings = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
        return new AuthResponse.UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName(),
                roleStrings
        );
    }
}