package com.hue.mel.service;

import com.hue.mel.model.RateLimitBucket;
import com.hue.mel.repository.RateLimitRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Optional;

@Service
public class RateLimitService {

    private final RateLimitRepository repository;
    private final double maxTokens;
    private final double refillRatePerSecond;

    // Spring automatically injects the config values we saved inside application.yml
    public RateLimitService(
            RateLimitRepository repository,
            @Value("${gateway.limits.max-tokens}") double maxTokens,
            @Value("${gateway.limits.refill-rate-per-second}") double refillRatePerSecond) {
        this.repository = repository;
        this.maxTokens = maxTokens;
        this.refillRatePerSecond = refillRatePerSecond;
    }

    /**
     * The core check logic. Returns true if request is allowed, false if rate limit exceeded.
     */
    public synchronized boolean isAllowed(String clientId) {
        Instant now = Instant.now();
        
        // 1. Fetch existing bucket record from Atlas, or create a brand new full bucket if missing
        Optional<RateLimitBucket> existingBucket = repository.findById(clientId);
        RateLimitBucket bucket = existingBucket.orElse(new RateLimitBucket(clientId, maxTokens, now));

        // 2. Compute Lazy Refill Math (Only if time has passed)
        if (existingBucket.isPresent()) {
            long elapsedTimeInSeconds = now.getEpochSecond() - bucket.getLastRefilled().getEpochSecond();
            
            if (elapsedTimeInSeconds > 0) {
                double tokensToAdd = elapsedTimeInSeconds * refillRatePerSecond;
                double newBalance = Math.min(maxTokens, bucket.getTokens() + tokensToAdd);
                bucket.setTokens(newBalance);
                bucket.setLastRefilled(now); // Slide the calculation point forward
            }
        }

        // 3. Evaluate Request Consumption
        if (bucket.getTokens() >= 1.0) {
            bucket.setTokens(bucket.getTokens() - 1.0); // Spend 1 token credit
            repository.save(bucket); // Persist updated numbers back to MongoDB Atlas
            return true; // ALLOW REQUEST
        }

        // Save current empty state to keep the dynamic TTL index refreshed
        repository.save(bucket);
        return false; // DENY REQUEST (HTTP 429)
    }
}
