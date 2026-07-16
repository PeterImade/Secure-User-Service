package com.example.user_service.refresh;

import com.example.user_service.exceptions.RefreshTokenCompromisedException;
import com.example.user_service.exceptions.RefreshTokenExpiredException;
import com.example.user_service.exceptions.RefreshTokenNotFoundException;
import com.example.user_service.users.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenRevocationService refreshTokenRevocationService;

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);

    @Value("${refresh-expiration}")
    private Long refreshExpiration;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, RefreshTokenRevocationService refreshTokenRevocationService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenRevocationService = refreshTokenRevocationService;
    }

    @Transactional
    public RefreshToken createRefreshToken(User user)
    {
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().plusMillis(refreshExpiration))
                .used(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }


    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    @Transactional
    public RefreshToken rotateRefreshTokenWithTheftDetection(String oldToken) {
        // 1. Find the token
        RefreshToken existingToken = refreshTokenRepository.findByToken(oldToken)
                .orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token not found"));

        // 2. Check expiry
        if (existingToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(existingToken);
            throw new RefreshTokenExpiredException("Refresh token expired");
        }

        // 3. THEFT DETECTION: if already used → compromise
        if (existingToken.isUsed()) {
            refreshTokenRevocationService.revokeAllTokensForUser(existingToken.getUser());
            throw new RefreshTokenCompromisedException("Token compromised — all sessions revoked");
        }

        // 4. Mark as used BEFORE rotating
        existingToken.setUsed(true);
        refreshTokenRepository.save(existingToken);

        // 5. Create a new token
        RefreshToken newToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(existingToken.getUser())
                .expiryDate(Instant.now().plusMillis(refreshExpiration))
                .used(false) // new token starts fresh
                .build();

        return refreshTokenRepository.save(newToken);
    }
}
