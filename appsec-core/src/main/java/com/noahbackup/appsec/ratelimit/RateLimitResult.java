package com.noahbackup.appsec.ratelimit;

/**
 * Result of a rate limit check operation.
 * Indicates whether the request should be allowed, rejected, or blocked.
 */
public class RateLimitResult {
    
    private final boolean allowed;
    private final String reason;
    private final long retryAfterSeconds;
    private final RateLimitStatus status;
    
    private RateLimitResult(boolean allowed, String reason, long retryAfterSeconds, RateLimitStatus status) {
        this.allowed = allowed;
        this.reason = reason;
        this.retryAfterSeconds = retryAfterSeconds;
        this.status = status;
    }
    
    /**
     * Create a result indicating the request is allowed.
     */
    public static RateLimitResult allowed() {
        return new RateLimitResult(true, null, 0, RateLimitStatus.ALLOWED);
    }
    
    /**
     * Create a result indicating the request is rejected due to rate limits.
     */
    public static RateLimitResult rejected(String reason, long retryAfterSeconds) {
        return new RateLimitResult(false, reason, retryAfterSeconds, RateLimitStatus.RATE_LIMITED);
    }
    
    /**
     * Create a result indicating the request is blocked due to abuse.
     */
    public static RateLimitResult blocked(String reason) {
        return new RateLimitResult(false, reason, -1, RateLimitStatus.BLOCKED);
    }
    
    public boolean isAllowed() {
        return allowed;
    }
    
    public String getReason() {
        return reason;
    }
    
    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
    
    public RateLimitStatus getStatus() {
        return status;
    }
    
    /**
     * Status codes for rate limit results.
     */
    public enum RateLimitStatus {
        ALLOWED,        // Request is allowed
        RATE_LIMITED,   // Request exceeds rate limits
        BLOCKED         // Client is blocked due to abuse
    }
    
    @Override
    public String toString() {
        return "RateLimitResult{" +
                "allowed=" + allowed +
                ", reason='" + reason + '\'' +
                ", retryAfterSeconds=" + retryAfterSeconds +
                ", status=" + status +
                '}';
    }
}