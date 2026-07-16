package com.example.user_service.jobs;
import com.example.user_service.refresh.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class RefreshTokenCleanupJob {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenCleanupJob.class);
    private final RefreshTokenRepository refreshTokenRepository;

    // Keep refresh tokens until they expire, then delete.
    public RefreshTokenCleanupJob(RefreshTokenRepository refreshTokenRepository)
    {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Value("${refresh-token-retention-days}")
    private int retentionDays;
    /**
     * Runs daily at midnight (00:00:00).
     * Deletes all refresh tokens that expired more than 7 days ago.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        Instant cutoff = Instant.now().minus(retentionDays, ChronoUnit.DAYS);
        log.info("Starting cleanup of refresh tokens expired before: {}", cutoff);

        refreshTokenRepository.deleteByExpiryDateBefore(cutoff);

        log.info("Cleanup completed. Deleted expired refresh tokens.");
    }
}