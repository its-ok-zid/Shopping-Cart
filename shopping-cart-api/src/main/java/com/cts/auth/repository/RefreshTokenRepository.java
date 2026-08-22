package com.cts.auth.repository;

import com.cts.auth.domain.RefreshToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select token from RefreshToken token join fetch token.user where token.tokenIdHash = :hash")
    Optional<RefreshToken> findByTokenIdHashForUpdate(@Param("hash") String hash);
    List<RefreshToken> findByFamilyIdAndRevokedAtIsNull(UUID familyId);
}
