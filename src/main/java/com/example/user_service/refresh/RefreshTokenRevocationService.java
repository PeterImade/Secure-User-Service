package com.example.user_service.refresh;

import com.example.user_service.users.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenRevocationService {
    private final RefreshTokenRepository refreshTokenRepository;
    private static final Logger log = LoggerFactory.getLogger(RefreshTokenRevocationService.class);

    public RefreshTokenRevocationService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW) // means that this method doesn't join an existing transaction; instead, it opens a new transaction separately and commits or rolls back  independently
    public void revokeAllTokensForUser(User user) {
        refreshTokenRepository.deleteByUser(user);
        log.warn("All tokens revoked for user: {}", user.getEmail());
    }
}