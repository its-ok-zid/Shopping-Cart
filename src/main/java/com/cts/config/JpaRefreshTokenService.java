package com.cts.config;

import com.cts.model.User;
import com.cts.repository.RefreshTokenRepository;
import com.cts.repository.UserRepository;
import com.zidtech.common.security.model.RefreshToken;
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
    public RefreshToken issue(String username) {

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔁 invalidate existing tokens (safety)
        repo.deleteByUserUsername(username);

        return saveNewToken(user);
    }

    @Override
    public Optional<RefreshToken> validate(String token) {
        return repo.findByToken(token)
                .map(entity -> RefreshToken.builder()
                        .token(entity.getToken())
                        .username(entity.getUser().getUsername())
                        .expiry(entity.getExpiryDate())
                        .build());
    }


    @Override
    public void invalidateAll(String username) {
        repo.deleteByUserUsername(username);
    }

    @Override
    public RefreshToken rotate(String oldToken) {

        var existing = repo.findByToken(oldToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        User user = existing.getUser();

        // 🔁 HARD ROTATION
        repo.delete(existing);

        return saveNewToken(user);
    }


    // 🔒 internal helper
    private RefreshToken saveNewToken(User user) {

        com.cts.model.RefreshToken entity = new com.cts.model.RefreshToken();
        entity.setToken(UUID.randomUUID().toString());
        entity.setUser(user);
        entity.setExpiryDate(
                Instant.now().plusSeconds(7 * 24 * 60 * 60)
        );

        repo.save(entity);

        return RefreshToken.builder()
                .token(entity.getToken())
                .username(user.getUsername())
                .expiry(entity.getExpiryDate())
                .build();
    }
}
