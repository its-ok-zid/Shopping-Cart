package com.cts.config;

import com.cts.model.User;
import com.cts.repository.UserRepository;
import com.zidtech.common.security.service.SecurityUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ShoppingCartUserProvider implements SecurityUserProvider {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
