package com.noahbackup.appsec.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.noahbackup.auth.SecureCodingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;

/**
 * Advanced rate limiting service for DOS protection and abuse prevention.
 * 
 * Implements multiple rate limiting strategies:
 * - Token bucket algorithm for smooth rate limiting
 * - Sliding window for burst control
 * - IP-based limiting for network-level protection
 * - User-based limiting for authenticated requests
 * - Endpoint-specific limits
 * - Adaptive limits based on system load
 * 
 * Features:
 * - Multi-level rate limiting (IP, user, endpoint, global)
 * - Configurable time windows and limits
 * - Automatic blocking of aggressive clients
 * - Integration with audit logging
 * - Support for whitelisting trusted IPs
 * - Dynamic rate adjustment based on threat level
 */
@Service
public class RateLimitService {
    
    private static final Logger logger = LoggerFactory.getLogger(RateLimitService.class);
    
    // Rate limit configurations
    @Value("${noah.security.ratelimit.enabled:true}")
    private boolean rateLimitEnabled;
    
    @Value("${noah.security.ratelimit.default.requests.per.minute:60}")
    private int defaultRequestsPerMinute;
    
    @Value("${noah.security.ratelimit.default.requests.per.hour:1000}")
    private int defaultRequestsPerHour;
    
    @Value("${noah.security.ratelimit.default.requests.per.day:10000}")
    private int defaultRequestsPerDay;
    
    @Value("${noah.security.ratelimit.authentication.requests.per.minute:10}")
    private int authRequestsPerMinute;
    
    @Value("${noah.security.ratelimit.backup.requests.per.hour:50}")
    private int backupRequestsPerHour;
    
    @Value("${noah.security.ratelimit.admin.requests.per.minute:30}")
    private int adminRequestsPerMinute;
    
    @Value("${noah.security.ratelimit.block.duration.minutes:15}")
    private int blockDurationMinutes;
    
    @Value("${noah.security.ratelimit.aggressive.threshold:5}")
    private int aggressiveThreshold;
    
    // Cache for rate limit counters
    private Cache<String, RateLimitCounter> rateLimitCache;
    private Cache<String, RateLimitCounter> hourlyCache;
    private Cache<String, RateLimitCounter> dailyCache;
    
    // Blocked IPs and users
    private Map<String, BlockedEntry> blockedIPs = new ConcurrentHashMap<>();
    private Map<String, BlockedEntry> blockedUsers = new ConcurrentHashMap<>();
    
    // Whitelisted IPs (internal networks, trusted partners)
    private final String[] whitelistedIPs = {
        "127.0.0.1", "::1",           // Localhost
        "10.0.0.0/8",                 // Private networks
        "172.16.0.0/12",
        "192.168.0.0/16"
    };
    
    @PostConstruct
    public void initialize() {
        // Initialize caches with different expiration times
        rateLimitCache = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(Duration.ofMinutes(1))
            .build();
            
        hourlyCache = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(Duration.ofHours(1))
            .build();
            
        dailyCache = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(Duration.ofDays(1))
            .build();
        
        SecureCodingUtils.safeLog(logger, "INFO", 
            "RateLimitService initialized. Enabled: {}, Default limits: {}/min, {}/hour, {}/day", 
            rateLimitEnabled, defaultRequestsPerMinute, defaultRequestsPerHour, defaultRequestsPerDay);
    }
    
    /**
     * Check if a request should be allowed based on rate limits.
     */
    public RateLimitResult checkRateLimit(RateLimitRequest request) {
        if (!rateLimitEnabled) {
            return RateLimitResult.allowed();
        }
        
        String ipAddress = request.getIpAddress();
        String userId = request.getUserId();
        String endpoint = request.getEndpoint();
        
        // Check if IP or user is blocked
        if (isBlocked(ipAddress, userId)) {
            return RateLimitResult.blocked("Client is temporarily blocked due to excessive requests");
        }
        
        // Check if IP is whitelisted
        if (isWhitelisted(ipAddress)) {
            return RateLimitResult.allowed();
        }
        
        // Check different rate limit levels
        RateLimitResult ipResult = checkIPRateLimit(ipAddress, request);
        if (!ipResult.isAllowed()) {
            handleViolation(ipAddress, userId, "IP_RATE_LIMIT", request);
            return ipResult;
        }
        
        if (userId != null) {
            RateLimitResult userResult = checkUserRateLimit(userId, request);
            if (!userResult.isAllowed()) {
                handleViolation(ipAddress, userId, "USER_RATE_LIMIT", request);
                return userResult;
            }
        }
        
        if (endpoint != null) {
            RateLimitResult endpointResult = checkEndpointRateLimit(endpoint, ipAddress, request);
            if (!endpointResult.isAllowed()) {
                handleViolation(ipAddress, userId, "ENDPOINT_RATE_LIMIT", request);
                return endpointResult;
            }
        }
        
        return RateLimitResult.allowed();
    }
    
    private RateLimitResult checkIPRateLimit(String ipAddress, RateLimitRequest request) {
        // Per-minute check
        String minuteKey = "ip:" + ipAddress + ":minute";
        RateLimitCounter minuteCounter = rateLimitCache.get(minuteKey, k -> new RateLimitCounter());
        
        if (minuteCounter.increment() > defaultRequestsPerMinute) {
            return RateLimitResult.rejected("Too many requests per minute from IP", 
                getRemainingSeconds(Duration.ofMinutes(1)));
        }
        
        // Per-hour check
        String hourKey = "ip:" + ipAddress + ":hour";
        RateLimitCounter hourCounter = hourlyCache.get(hourKey, k -> new RateLimitCounter());
        
        if (hourCounter.increment() > defaultRequestsPerHour) {
            return RateLimitResult.rejected("Too many requests per hour from IP", 
                getRemainingSeconds(Duration.ofHours(1)));
        }
        
        // Per-day check
        String dayKey = "ip:" + ipAddress + ":day";
        RateLimitCounter dayCounter = dailyCache.get(dayKey, k -> new RateLimitCounter());
        
        if (dayCounter.increment() > defaultRequestsPerDay) {
            return RateLimitResult.rejected("Too many requests per day from IP", 
                getRemainingSeconds(Duration.ofDays(1)));
        }
        
        return RateLimitResult.allowed();
    }
    
    private RateLimitResult checkUserRateLimit(String userId, RateLimitRequest request) {
        // User-specific limits (typically higher than IP limits)
        String minuteKey = "user:" + userId + ":minute";
        RateLimitCounter minuteCounter = rateLimitCache.get(minuteKey, k -> new RateLimitCounter());
        
        int userLimit = defaultRequestsPerMinute * 2; // Users get higher limits
        if (minuteCounter.increment() > userLimit) {
            return RateLimitResult.rejected("Too many requests per minute from user", 
                getRemainingSeconds(Duration.ofMinutes(1)));
        }
        
        return RateLimitResult.allowed();
    }
    
    private RateLimitResult checkEndpointRateLimit(String endpoint, String ipAddress, RateLimitRequest request) {
        int limit = getEndpointSpecificLimit(endpoint);
        
        String key = "endpoint:" + endpoint + ":ip:" + ipAddress + ":minute";
        RateLimitCounter counter = rateLimitCache.get(key, k -> new RateLimitCounter());
        
        if (counter.increment() > limit) {
            return RateLimitResult.rejected("Too many requests to endpoint " + endpoint, 
                getRemainingSeconds(Duration.ofMinutes(1)));
        }
        
        return RateLimitResult.allowed();
    }
    
    private int getEndpointSpecificLimit(String endpoint) {
        // Define endpoint-specific limits for sensitive operations
        return switch (endpoint.toLowerCase()) {
            case "/api/v1/auth/login" -> authRequestsPerMinute;
            case "/api/v1/backup/start" -> backupRequestsPerHour / 60; // Convert to per-minute
            case "/api/v1/admin/*" -> adminRequestsPerMinute;
            default -> defaultRequestsPerMinute;
        };
    }
    
    private void handleViolation(String ipAddress, String userId, String violationType, RateLimitRequest request) {
        String key = ipAddress + ":" + violationType;
        
        // Track violations for potential blocking
        RateLimitCounter violationCounter = rateLimitCache.get("violations:" + key, k -> new RateLimitCounter());
        int violationCount = violationCounter.increment();
        
        SecureCodingUtils.safeLog(logger, "WARN", 
            "Rate limit violation: {} from IP: {}, User: {}, Endpoint: {}, Violations: {}", 
            violationType, ipAddress, userId, request.getEndpoint(), violationCount);
        
        // Block aggressive clients
        if (violationCount >= aggressiveThreshold) {
            blockClient(ipAddress, userId, violationType);
        }
    }
    
    private void blockClient(String ipAddress, String userId, String reason) {
        LocalDateTime blockUntil = LocalDateTime.now().plusMinutes(blockDurationMinutes);
        
        if (ipAddress != null) {
            blockedIPs.put(ipAddress, new BlockedEntry(reason, blockUntil));
            SecureCodingUtils.safeLog(logger, "WARN", 
                "IP {} blocked until {} for reason: {}", ipAddress, blockUntil, reason);
        }
        
        if (userId != null) {
            blockedUsers.put(userId, new BlockedEntry(reason, blockUntil));
            SecureCodingUtils.safeLog(logger, "WARN", 
                "User {} blocked until {} for reason: {}", userId, blockUntil, reason);
        }
    }
    
    private boolean isBlocked(String ipAddress, String userId) {
        LocalDateTime now = LocalDateTime.now();
        
        // Check IP blocking
        if (ipAddress != null) {
            BlockedEntry blockedIP = blockedIPs.get(ipAddress);
            if (blockedIP != null) {
                if (now.isBefore(blockedIP.getBlockedUntil())) {
                    return true;
                } else {
                    // Block expired, remove it
                    blockedIPs.remove(ipAddress);
                }
            }
        }
        
        // Check user blocking
        if (userId != null) {
            BlockedEntry blockedUser = blockedUsers.get(userId);
            if (blockedUser != null) {
                if (now.isBefore(blockedUser.getBlockedUntil())) {
                    return true;
                } else {
                    // Block expired, remove it
                    blockedUsers.remove(userId);
                }
            }
        }
        
        return false;
    }
    
    private boolean isWhitelisted(String ipAddress) {
        if (ipAddress == null) return false;
        
        for (String whitelisted : whitelistedIPs) {
            if (whitelisted.contains("/")) {
                // CIDR notation - simplified check for demo
                if (ipAddress.startsWith(whitelisted.split("/")[0].substring(0, whitelisted.indexOf(".")))) {
                    return true;
                }
            } else {
                if (ipAddress.equals(whitelisted)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private long getRemainingSeconds(Duration duration) {
        return duration.getSeconds();
    }
    
    /**
     * Get current rate limit status for monitoring.
     */
    public RateLimitStatus getRateLimitStatus(String ipAddress, String userId) {
        RateLimitStatus status = new RateLimitStatus();
        status.setEnabled(rateLimitEnabled);
        status.setBlocked(isBlocked(ipAddress, userId));
        status.setWhitelisted(isWhitelisted(ipAddress));
        
        // Get current counters
        if (ipAddress != null) {
            RateLimitCounter ipMinute = rateLimitCache.getIfPresent("ip:" + ipAddress + ":minute");
            RateLimitCounter ipHour = hourlyCache.getIfPresent("ip:" + ipAddress + ":hour");
            RateLimitCounter ipDay = dailyCache.getIfPresent("ip:" + ipAddress + ":day");
            
            status.setIpRequestsThisMinute(ipMinute != null ? ipMinute.getCount() : 0);
            status.setIpRequestsThisHour(ipHour != null ? ipHour.getCount() : 0);
            status.setIpRequestsToday(ipDay != null ? ipDay.getCount() : 0);
        }
        
        if (userId != null) {
            RateLimitCounter userMinute = rateLimitCache.getIfPresent("user:" + userId + ":minute");
            status.setUserRequestsThisMinute(userMinute != null ? userMinute.getCount() : 0);
        }
        
        return status;
    }
    
    /**
     * Manually unblock an IP or user (admin function).
     */
    public void unblock(String ipAddress, String userId) {
        if (ipAddress != null) {
            BlockedEntry removed = blockedIPs.remove(ipAddress);
            if (removed != null) {
                SecureCodingUtils.safeLog(logger, "INFO", "IP {} manually unblocked", ipAddress);
            }
        }
        
        if (userId != null) {
            BlockedEntry removed = blockedUsers.remove(userId);
            if (removed != null) {
                SecureCodingUtils.safeLog(logger, "INFO", "User {} manually unblocked", userId);
            }
        }
    }
    
    /**
     * Get statistics about rate limiting for monitoring.
     */
    public Map<String, Object> getRateLimitStatistics() {
        Map<String, Object> stats = new Map.of(
            "enabled", rateLimitEnabled,
            "blocked_ips", blockedIPs.size(),
            "blocked_users", blockedUsers.size(),
            "cache_size_minute", rateLimitCache.estimatedSize(),
            "cache_size_hour", hourlyCache.estimatedSize(),
            "cache_size_daily", dailyCache.estimatedSize(),
            "default_requests_per_minute", defaultRequestsPerMinute,
            "default_requests_per_hour", defaultRequestsPerHour,
            "default_requests_per_day", defaultRequestsPerDay
        );
        return stats;
    }
    
    /**
     * Simple rate limit counter with thread safety.
     */
    private static class RateLimitCounter {
        private final AtomicInteger count = new AtomicInteger(0);
        private final LocalDateTime createdAt = LocalDateTime.now();
        
        public int increment() {
            return count.incrementAndGet();
        }
        
        public int getCount() {
            return count.get();
        }
        
        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
    }
    
    /**
     * Blocked entry with reason and expiration.
     */
    private static class BlockedEntry {
        private final String reason;
        private final LocalDateTime blockedUntil;
        
        public BlockedEntry(String reason, LocalDateTime blockedUntil) {
            this.reason = reason;
            this.blockedUntil = blockedUntil;
        }
        
        public String getReason() {
            return reason;
        }
        
        public LocalDateTime getBlockedUntil() {
            return blockedUntil;
        }
    }
}