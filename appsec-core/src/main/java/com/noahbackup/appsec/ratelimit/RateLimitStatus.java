package com.noahbackup.appsec.ratelimit;

/**
 * Status information about current rate limits for monitoring and debugging.
 * Provides visibility into current usage and limits for a client.
 */
public class RateLimitStatus {
    
    private boolean enabled;
    private boolean blocked;
    private boolean whitelisted;
    private int ipRequestsThisMinute;
    private int ipRequestsThisHour;
    private int ipRequestsToday;
    private int userRequestsThisMinute;
    private int maxRequestsPerMinute;
    private int maxRequestsPerHour;
    private int maxRequestsPerDay;
    private long blockExpiresAt;
    
    public RateLimitStatus() {
        // Default constructor
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isBlocked() {
        return blocked;
    }
    
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
    
    public boolean isWhitelisted() {
        return whitelisted;
    }
    
    public void setWhitelisted(boolean whitelisted) {
        this.whitelisted = whitelisted;
    }
    
    public int getIpRequestsThisMinute() {
        return ipRequestsThisMinute;
    }
    
    public void setIpRequestsThisMinute(int ipRequestsThisMinute) {
        this.ipRequestsThisMinute = ipRequestsThisMinute;
    }
    
    public int getIpRequestsThisHour() {
        return ipRequestsThisHour;
    }
    
    public void setIpRequestsThisHour(int ipRequestsThisHour) {
        this.ipRequestsThisHour = ipRequestsThisHour;
    }
    
    public int getIpRequestsToday() {
        return ipRequestsToday;
    }
    
    public void setIpRequestsToday(int ipRequestsToday) {
        this.ipRequestsToday = ipRequestsToday;
    }
    
    public int getUserRequestsThisMinute() {
        return userRequestsThisMinute;
    }
    
    public void setUserRequestsThisMinute(int userRequestsThisMinute) {
        this.userRequestsThisMinute = userRequestsThisMinute;
    }
    
    public int getMaxRequestsPerMinute() {
        return maxRequestsPerMinute;
    }
    
    public void setMaxRequestsPerMinute(int maxRequestsPerMinute) {
        this.maxRequestsPerMinute = maxRequestsPerMinute;
    }
    
    public int getMaxRequestsPerHour() {
        return maxRequestsPerHour;
    }
    
    public void setMaxRequestsPerHour(int maxRequestsPerHour) {
        this.maxRequestsPerHour = maxRequestsPerHour;
    }
    
    public int getMaxRequestsPerDay() {
        return maxRequestsPerDay;
    }
    
    public void setMaxRequestsPerDay(int maxRequestsPerDay) {
        this.maxRequestsPerDay = maxRequestsPerDay;
    }
    
    public long getBlockExpiresAt() {
        return blockExpiresAt;
    }
    
    public void setBlockExpiresAt(long blockExpiresAt) {
        this.blockExpiresAt = blockExpiresAt;
    }
    
    /**
     * Check if the client is currently within rate limits.
     */
    public boolean isWithinLimits() {
        if (!enabled || whitelisted) {
            return true;
        }
        
        if (blocked) {
            return false;
        }
        
        return ipRequestsThisMinute < maxRequestsPerMinute &&
               ipRequestsThisHour < maxRequestsPerHour &&
               ipRequestsToday < maxRequestsPerDay;
    }
    
    /**
     * Get the most restrictive limit that applies.
     */
    public String getMostRestrictiveLimit() {
        if (blocked) {
            return "BLOCKED";
        }
        
        if (!enabled || whitelisted) {
            return "NONE";
        }
        
        if (ipRequestsThisMinute >= maxRequestsPerMinute) {
            return "MINUTE_LIMIT";
        }
        
        if (ipRequestsThisHour >= maxRequestsPerHour) {
            return "HOUR_LIMIT";
        }
        
        if (ipRequestsToday >= maxRequestsPerDay) {
            return "DAY_LIMIT";
        }
        
        return "NONE";
    }
    
    @Override
    public String toString() {
        return "RateLimitStatus{" +
                "enabled=" + enabled +
                ", blocked=" + blocked +
                ", whitelisted=" + whitelisted +
                ", ipRequestsThisMinute=" + ipRequestsThisMinute +
                ", ipRequestsThisHour=" + ipRequestsThisHour +
                ", ipRequestsToday=" + ipRequestsToday +
                ", userRequestsThisMinute=" + userRequestsThisMinute +
                ", maxRequestsPerMinute=" + maxRequestsPerMinute +
                ", maxRequestsPerHour=" + maxRequestsPerHour +
                ", maxRequestsPerDay=" + maxRequestsPerDay +
                ", blockExpiresAt=" + blockExpiresAt +
                '}';
    }
}