package com.cts.security;

import com.cts.dto.LoginRequestDTO;
import com.cts.dto.LoginResponseDTO;
import com.cts.dto.SignUpRequestDTO;
import com.cts.dto.SignUpResponseDTO;
import com.cts.model.User;
import com.cts.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDTO login(@Valid LoginRequestDTO loginRequest) {

    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            )
    );

    String username = authentication.getName();

    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

    String token = jwtUtil.generateAccessToken(user);

    return new LoginResponseDTO(user.getId(), token);
}


    public SignUpResponseDTO signUp(SignUpRequestDTO signUpRequest) {
        User user = userRepository.findByUsername(signUpRequest.getUsername()).orElse(null);

        if (user != null) throw new RuntimeException("Username already exists");

        user = User.builder().
                username(signUpRequest.getUsername()).
                password(passwordEncoder.encode(signUpRequest.getPassword())).
                email(signUpRequest.getEmail()).
                build();

        userRepository.save(user);

        return new SignUpResponseDTO(user.getId(), user.getUsername());
    }
}
