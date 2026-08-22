package com.cts.auth.domain;

import com.cts.user.domain.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_token")
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;
    @Column(name = "token_id_hash", nullable = false, unique = true, length = 64)
    private String tokenIdHash;
    @Column(name = "family_id", nullable = false)
    private UUID familyId;
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
    @Column(name = "revoked_at")
    private Instant revokedAt;
    @Version private long version;

    protected RefreshToken() { }
    public RefreshToken(AppUser user, String tokenIdHash, UUID familyId, Instant expiresAt) {
        this.user = user; this.tokenIdHash = tokenIdHash; this.familyId = familyId; this.expiresAt = expiresAt;
    }
    public boolean isActiveAt(Instant now) { return revokedAt == null && expiresAt.isAfter(now); }
    public void revoke() { if (revokedAt == null) revokedAt = Instant.now(); }
    public Long getId() { return id; }
    public AppUser getUser() { return user; }
    public String getTokenIdHash() { return tokenIdHash; }
    public UUID getFamilyId() { return familyId; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getRevokedAt() { return revokedAt; }
}
