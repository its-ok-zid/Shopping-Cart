package com.cts.serviceImpl;

import com.cts.dto.LoginRequestDTO;
import com.cts.dto.LoginResponseDTO;
import com.cts.dto.SignUpRequestDTO;
import com.cts.dto.SignUpResponseDTO;
import com.cts.model.Role;
import com.cts.model.User;
import com.cts.repository.UserRepository;
import com.cts.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // ⛔ NO TOKEN LOGIC HERE
        return new LoginResponseDTO(user.getId(), null, null);
    }

    @Override
    public SignUpResponseDTO signUp(SignUpRequestDTO request) {

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(Role.ROLE_USER));

        User saved = userRepository.save(user);
        return new SignUpResponseDTO(saved.getId(), saved.getUsername());
    }
}
