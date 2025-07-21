package com.noahbackup.appsec.validation;

/**
 * Enumeration of threat severity levels for security risk assessment.
 * 
 * Used to classify and prioritize security threats based on their potential impact.
 */
public enum ThreatSeverity implements Comparable<ThreatSeverity> {
    
    /**
     * Low severity - Minor security concerns that should be addressed but don't pose immediate risk.
     * Examples: Format validation failures, policy violations
     */
    LOW("Low", 1, "Minor security concern", "No immediate action required"),
    
    /**
     * Medium severity - Moderate security risks that should be addressed promptly.
     * Examples: Weak passwords, suspicious patterns, encoding issues
     */
    MEDIUM("Medium", 2, "Moderate security risk", "Should be addressed promptly"),
    
    /**
     * High severity - Serious security threats that require immediate attention.
     * Examples: SQL injection, XSS, path traversal, file inclusion
     */
    HIGH("High", 3, "Serious security threat", "Requires immediate attention"),
    
    /**
     * Critical severity - Critical security threats that pose immediate danger.
     * Examples: Command injection, XXE, remote file inclusion, data exfiltration
     */
    CRITICAL("Critical", 4, "Critical security threat", "Immediate action required");
    
    private final String displayName;
    private final int level;
    private final String description;
    private final String actionRequired;
    
    ThreatSeverity(String displayName, int level, String description, String actionRequired) {
        this.displayName = displayName;
        this.level = level;
        this.description = description;
        this.actionRequired = actionRequired;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getLevel() {
        return level;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getActionRequired() {
        return actionRequired;
    }
    
    /**
     * Check if this severity level requires immediate blocking of the request.
     */
    public boolean requiresBlocking() {
        return this == HIGH || this == CRITICAL;
    }
    
    /**
     * Check if this severity level requires audit logging.
     */
    public boolean requiresAuditLogging() {
        return this == MEDIUM || this == HIGH || this == CRITICAL;
    }
    
    /**
     * Check if this severity level requires security team notification.
     */
    public boolean requiresSecurityNotification() {
        return this == HIGH || this == CRITICAL;
    }
    
    /**
     * Get the color code for UI representation.
     */
    public String getColorCode() {
        switch (this) {
            case LOW:
                return "#28a745";    // Green
            case MEDIUM:
                return "#ffc107";    // Yellow
            case HIGH:
                return "#fd7e14";    // Orange
            case CRITICAL:
                return "#dc3545";    // Red
            default:
                return "#6c757d";    // Gray
        }
    }
    
    /**
     * Get the emoji representation for notifications.
     */
    public String getEmoji() {
        switch (this) {
            case LOW:
                return "🔵";
            case MEDIUM:
                return "🟡";
            case HIGH:
                return "🟠";
            case CRITICAL:
                return "🔴";
            default:
                return "⚪";
        }
    }
    
    /**
     * Get response status code for HTTP responses.
     */
    public int getHttpStatusCode() {
        switch (this) {
            case LOW:
                return 400;  // Bad Request
            case MEDIUM:
                return 400;  // Bad Request
            case HIGH:
                return 403;  // Forbidden
            case CRITICAL:
                return 403;  // Forbidden
            default:
                return 400;
        }
    }
    
    /**
     * Get the maximum time (in milliseconds) to investigate this threat.
     */
    public long getMaxInvestigationTime() {
        switch (this) {
            case LOW:
                return 7 * 24 * 60 * 60 * 1000L;    // 7 days
            case MEDIUM:
                return 24 * 60 * 60 * 1000L;        // 24 hours
            case HIGH:
                return 4 * 60 * 60 * 1000L;         // 4 hours
            case CRITICAL:
                return 30 * 60 * 1000L;             // 30 minutes
            default:
                return 24 * 60 * 60 * 1000L;
        }
    }
    
    /**
     * Check if this severity is higher than another.
     */
    public boolean isHigherThan(ThreatSeverity other) {
        return this.compareTo(other) > 0;
    }
    
    /**
     * Check if this severity is lower than another.
     */
    public boolean isLowerThan(ThreatSeverity other) {
        return this.compareTo(other) < 0;
    }
    
    /**
     * Get severity from string representation.
     */
    public static ThreatSeverity fromString(String severity) {
        if (severity == null) {
            return LOW;
        }
        
        try {
            return valueOf(severity.toUpperCase());
        } catch (IllegalArgumentException e) {
            return LOW;
        }
    }
    
    /**
     * Get severity from numeric level.
     */
    public static ThreatSeverity fromLevel(int level) {
        switch (level) {
            case 1:
                return LOW;
            case 2:
                return MEDIUM;
            case 3:
                return HIGH;
            case 4:
                return CRITICAL;
            default:
                return level > 4 ? CRITICAL : LOW;
        }
    }
}