package com.myproject.insider.service;

import com.myproject.insider.config.RateLimitProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IpRateLimitService {

    private final RateLimitProperties properties;
    private final StringRedisTemplate redisTemplate;

    public RateLimitDecision evaluate(String ip) {
        if (!properties.isEnabled()) {
            return RateLimitDecision.allow();
        }
        String normalizedIp = normalizeIp(ip);
        if (isBlacklisted(normalizedIp)) {
            return RateLimitDecision.blockedByBlacklist(normalizedIp);
        }
        try {
            String key = properties.getCounterKeyPrefix() + normalizedIp;
            Long count = redisTemplate.opsForValue().increment(key);
            if (count == null) {
                return RateLimitDecision.allow();
            }
            if (count == 1L) {
                redisTemplate.expire(key, properties.getWindow());
            }
            if (count > properties.getMaxRequests()) {
                Long ttlSeconds = redisTemplate.getExpire(key);
                long retryAfter = (ttlSeconds != null && ttlSeconds > 0) ? ttlSeconds : properties.getWindow().toSeconds();
                return RateLimitDecision.rateLimited(normalizedIp, retryAfter);
            }
            return RateLimitDecision.allow();
        } catch (Exception ex) {
            log.error("Rate limit Redis check failed for ip={}", normalizedIp, ex);
            return RateLimitDecision.allow();
        }
    }

    private boolean isBlacklisted(String ip) {
        Set<String> bootstrap = properties.getBlacklistBootstrapIps().stream()
                .map(this::normalizeIp)
                .collect(Collectors.toSet());
        if (bootstrap.contains(ip)) {
            return true;
        }
        Boolean inRedis = redisTemplate.opsForSet().isMember(properties.getBlacklistRedisKey(), ip);
        return Boolean.TRUE.equals(inRedis);
    }

    private String normalizeIp(String ip) {
        if (ip == null || ip.isBlank()) {
            return "unknown";
        }
        return ip.trim().toLowerCase(Locale.ROOT);
    }

    public record RateLimitDecision(boolean allowed, boolean blacklisted, String ip, long retryAfterSeconds) {
        public static RateLimitDecision allow() {
            return new RateLimitDecision(true, false, "", 0);
        }

        public static RateLimitDecision blockedByBlacklist(String ip) {
            return new RateLimitDecision(false, true, ip, 0);
        }

        public static RateLimitDecision rateLimited(String ip, long retryAfterSeconds) {
            return new RateLimitDecision(false, false, ip, retryAfterSeconds);
        }
    }
}
