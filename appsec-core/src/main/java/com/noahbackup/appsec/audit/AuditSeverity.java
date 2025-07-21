package com.noahbackup.appsec.audit;

/**
 * Enumeration of audit event severity levels.
 * 
 * Classifies the importance and urgency of security audit events
 * for proper prioritization and response.
 */
public enum AuditSeverity implements Comparable<AuditSeverity> {
    
    /**
     * Low severity - Informational events with minimal security impact.
     */
    LOW("Low", 1, "Informational", "No immediate action required"),
    
    /**
     * Medium severity - Events that should be monitored but don't require immediate action.
     */
    MEDIUM("Medium", 2, "Noteworthy", "Monitor and review periodically"),
    
    /**
     * High severity - Events that require prompt attention and investigation.
     */
    HIGH("High", 3, "Important", "Requires prompt attention"),
    
    /**
     * Critical severity - Events that require immediate investigation and response.
     */
    CRITICAL("Critical", 4, "Urgent", "Immediate response required");
    
    private final String displayName;
    private final int level;
    private final String category;
    private final String responseRequired;
    
    AuditSeverity(String displayName, int level, String category, String responseRequired) {
        this.displayName = displayName;
        this.level = level;
        this.category = category;
        this.responseRequired = responseRequired;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getLevel() {
        return level;
    }
    
    public String getCategory() {
        return category;
    }
    
    public String getResponseRequired() {
        return responseRequired;
    }
    
    /**
     * Get maximum response time in milliseconds.
     */
    public long getMaxResponseTime() {
        return switch (this) {
            case LOW -> 7 * 24 * 60 * 60 * 1000L;    // 7 days
            case MEDIUM -> 24 * 60 * 60 * 1000L;      // 24 hours  
            case HIGH -> 4 * 60 * 60 * 1000L;        // 4 hours
            case CRITICAL -> 30 * 60 * 1000L;        // 30 minutes
        };
    }
    
    /**
     * Check if this severity requires immediate notification.
     */
    public boolean requiresImmediateNotification() {
        return this == HIGH || this == CRITICAL;
    }
    
    /**
     * Check if this severity requires escalation to security team.
     */
    public boolean requiresSecurityEscalation() {
        return this == CRITICAL;
    }
    
    /**
     * Get color code for UI representation.
     */
    public String getColorCode() {
        return switch (this) {
            case LOW -> "#28a745";      // Green
            case MEDIUM -> "#ffc107";   // Yellow
            case HIGH -> "#fd7e14";     // Orange
            case CRITICAL -> "#dc3545"; // Red
        };
    }
    
    /**
     * Get emoji representation.
     */
    public String getEmoji() {
        return switch (this) {
            case LOW -> "🔵";
            case MEDIUM -> "🟡";
            case HIGH -> "🟠";
            case CRITICAL -> "🔴";
        };
    }
    
    public boolean isHigherThan(AuditSeverity other) {
        return this.compareTo(other) > 0;
    }
    
    public boolean isLowerThan(AuditSeverity other) {
        return this.compareTo(other) < 0;
    }
}