package com.company.orderservice.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitConfig {

    @Bean
    public Bucket rateLimiter() {
        // Bucket capacity is 60 and allow 1 token refill per sec
        Bandwidth limit = Bandwidth.builder()
                .capacity(60)
                .refillGreedy(1, Duration.ofSeconds(1))
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
