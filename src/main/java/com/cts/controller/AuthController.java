package com.cts.controller;

import com.cts.dto.LoginRequestDTO;
import com.cts.dto.LoginResponseDTO;
import com.cts.dto.SignUpRequestDTO;
import com.cts.dto.SignUpResponseDTO;
import com.cts.exception.ApiResponse;
import com.cts.exception.UnauthorizedException;
import com.cts.service.AuthService;
import com.zidtech.common.security.service.RefreshTokenService;
import com.zidtech.common.security.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDTO> signup(
            @RequestBody SignUpRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.signUp(request));
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponseDTO> signin(
            @RequestBody LoginRequestDTO request,
            HttpServletResponse response) {

        LoginResponseDTO login = authService.login(request);

        Cookie accessCookie = new Cookie("ACCESS_TOKEN", login.getAccessToken());
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(3600);

        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", login.getRefreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/api/auth/refresh");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(login);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(
            @CookieValue("REFRESH_TOKEN") String refreshToken,
            HttpServletResponse response) {

        var stored = refreshTokenService.find(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (stored.getExpiry().isBefore(Instant.now())) {
            refreshTokenService.invalidate(refreshToken);
            throw new UnauthorizedException("Refresh token expired");
        }

        // 🔁 ROTATION
        var newRefresh = refreshTokenService.create(stored.getUsername());
        var pair = jwtUtil.generateTokenPair(
                stored.getUsername(),
                newRefresh.getToken()
        );

        Cookie accessCookie = new Cookie("ACCESS_TOKEN", pair.getAccessToken());
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(3600);

        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", newRefresh.getToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/api/auth/refresh");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(
                new LoginResponseDTO(null, pair.getAccessToken(), newRefresh.getToken())
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication auth, HttpServletResponse response) {

        if (auth != null) {
            refreshTokenService.invalidateAll(auth.getName());
        }

        Cookie accessCookie = new Cookie("ACCESS_TOKEN", null);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);

        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/api/auth/refresh");
        refreshCookie.setMaxAge(0);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Logged out successfully")
                        .build()
        );
    }
}
