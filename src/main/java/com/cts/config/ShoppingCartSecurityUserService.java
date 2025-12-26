package com.cts.config;

import com.cts.model.User;
import com.cts.repository.UserRepository;
import com.zidtech.common.security.model.SecurityUser;
import com.zidtech.common.security.service.SecurityUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartSecurityUserService implements SecurityUserService {

    private final UserRepository userRepository;

    @Override
    public SecurityUser loadByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return SecurityUser.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(
                        user.getRoles().stream()
                                .map(r -> new SimpleGrantedAuthority(r.name()))
                                .toList()
                )
                .build();
    }
}
