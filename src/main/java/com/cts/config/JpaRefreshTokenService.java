package com.cts.config;

import com.cts.model.User;
import com.cts.repository.RefreshTokenRepository;
import com.cts.repository.UserRepository;
import com.zidtech.common.security.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JpaRefreshTokenService implements RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final UserRepository userRepo;

    @Override
    public com.zidtech.common.security.model.RefreshToken create(String username) {

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔁 REFRESH TOKEN ROTATION (invalidate previous tokens)
        repo.deleteByUserUsername(username);

        com.cts.model.RefreshToken entity = new com.cts.model.RefreshToken();
        entity.setToken(UUID.randomUUID().toString());
        entity.setUser(user);
        entity.setExpiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60));

        repo.save(entity);

        return com.zidtech.common.security.model.RefreshToken.builder()
                .token(entity.getToken())
                .username(username)
                .expiry(entity.getExpiryDate())
                .build();
    }

    @Override
    public Optional<com.zidtech.common.security.model.RefreshToken> find(String token) {
        return repo.findByToken(token)
                .map(entity ->
                        com.zidtech.common.security.model.RefreshToken.builder()
                                .token(entity.getToken())
                                .username(entity.getUser().getUsername())
                                .expiry(entity.getExpiryDate())
                                .build()
                );
    }

    @Override
    public void invalidate(String token) {
        repo.deleteByToken(token);
    }

    @Override
    public void invalidateAll(String username) {
        repo.deleteByUserUsername(username);
    }
}
