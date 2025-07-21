package com.noahbackup.appsec.ratelimit;

/**
 * Represents a request for rate limit checking.
 * Contains all necessary information to evaluate rate limits.
 */
public class RateLimitRequest {
    
    private final String ipAddress;
    private final String userId;
    private final String endpoint;
    private final String userAgent;
    private final String method;
    private final long timestamp;
    
    private RateLimitRequest(Builder builder) {
        this.ipAddress = builder.ipAddress;
        this.userId = builder.userId;
        this.endpoint = builder.endpoint;
        this.userAgent = builder.userAgent;
        this.method = builder.method;
        this.timestamp = builder.timestamp;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getEndpoint() {
        return endpoint;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public String getMethod() {
        return method;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String ipAddress;
        private String userId;
        private String endpoint;
        private String userAgent;
        private String method;
        private long timestamp = System.currentTimeMillis();
        
        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }
        
        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }
        
        public Builder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }
        
        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }
        
        public Builder method(String method) {
            this.method = method;
            return this;
        }
        
        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        public RateLimitRequest build() {
            return new RateLimitRequest(this);
        }
    }
    
    @Override
    public String toString() {
        return "RateLimitRequest{" +
                "ipAddress='" + ipAddress + '\'' +
                ", userId='" + userId + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", method='" + method + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}