package com.cts.controller;

import com.cts.dto.LoginRequestDTO;
import com.cts.dto.LoginResponseDTO;
import com.cts.dto.SignUpRequestDTO;
import com.cts.dto.SignUpResponseDTO;
import com.cts.exception.ApiResponse;
import com.cts.model.User;
import com.cts.security.AuthService;
import com.cts.security.JwtUtil;
import com.cts.security.RefreshToken;
import com.cts.security.RefreshTokenRepository;
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
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDTO> signup(@RequestBody SignUpRequestDTO signUpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signUp(signUpRequest));
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponseDTO> signin(
            @RequestBody LoginRequestDTO loginRequest,
            HttpServletResponse response) {

        LoginResponseDTO loginResponse = authService.login(loginRequest);

        Cookie accessCookie = new Cookie("ACCESS_TOKEN", loginResponse.getJwt());
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(false);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(3600);

        response.addCookie(accessCookie);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication auth, HttpServletResponse response) {

        if (auth != null && auth.getPrincipal() instanceof User user) {
            refreshTokenRepository.deleteByUser(user);
        }

        Cookie cookie = new Cookie("ACCESS_TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Logged out successfully")
                        .build()
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @CookieValue("REFRESH_TOKEN") String refreshToken,
            HttpServletResponse response) {

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (token.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        User user = token.getUser();
        String newAccessToken = jwtUtil.generateAccessToken(user);

        Cookie accessCookie = new Cookie("ACCESS_TOKEN", newAccessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(3600);

        response.addCookie(accessCookie);

        return ResponseEntity.ok().build();
    }


}
