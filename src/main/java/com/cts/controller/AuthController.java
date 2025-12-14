package com.cts.controller;

import com.cts.dto.LoginRequestDTO;
import com.cts.dto.LoginResponseDTO;
import com.cts.dto.SignUpRequestDTO;
import com.cts.dto.SignUpResponseDTO;
import com.cts.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDTO> signup(@RequestBody SignUpRequestDTO signUpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signUp(signUpRequest));
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponseDTO> signin(@RequestBody LoginRequestDTO loginRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(loginRequest));
    }

}
