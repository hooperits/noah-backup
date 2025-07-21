package com.noahbackup.appsec.audit;

/**
 * Enumeration of possible audit event outcomes.
 * 
 * Standardizes the result classification of security events
 * for consistent audit logging and reporting.
 */
public enum AuditOutcome {
    
    /**
     * Operation completed successfully.
     */
    SUCCESS("Success", "Operation completed successfully", false),
    
    /**
     * Operation failed due to an error.
     */
    FAILURE("Failure", "Operation failed", true),
    
    /**
     * Operation was blocked by security controls.
     */
    BLOCKED("Blocked", "Operation blocked by security controls", true),
    
    /**
     * Operation was denied due to insufficient permissions.
     */
    DENIED("Denied", "Operation denied - insufficient permissions", true),
    
    /**
     * Operation timed out.
     */
    TIMEOUT("Timeout", "Operation timed out", true),
    
    /**
     * Operation was cancelled by user or system.
     */
    CANCELLED("Cancelled", "Operation was cancelled", false),
    
    /**
     * Operation is still in progress.
     */
    IN_PROGRESS("In Progress", "Operation is in progress", false),
    
    /**
     * Operation completed but with warnings.
     */
    SUCCESS_WITH_WARNINGS("Success with Warnings", "Operation completed with warnings", false),
    
    /**
     * Operation failed but was automatically retried.
     */
    RETRY("Retry", "Operation failed and will be retried", false),
    
    /**
     * Operation was skipped due to conditions not being met.
     */
    SKIPPED("Skipped", "Operation was skipped", false),
    
    /**
     * Operation outcome is unknown or could not be determined.
     */
    UNKNOWN("Unknown", "Operation outcome unknown", true);
    
    private final String displayName;
    private final String description;
    private final boolean requiresInvestigation;
    
    AuditOutcome(String displayName, String description, boolean requiresInvestigation) {
        this.displayName = displayName;
        this.description = description;
        this.requiresInvestigation = requiresInvestigation;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean requiresInvestigation() {
        return requiresInvestigation;
    }
    
    /**
     * Check if this outcome indicates a successful operation.
     */
    public boolean isSuccess() {
        return this == SUCCESS || this == SUCCESS_WITH_WARNINGS;
    }
    
    /**
     * Check if this outcome indicates a failure.
     */
    public boolean isFailure() {
        return this == FAILURE || this == BLOCKED || this == DENIED || 
               this == TIMEOUT || this == UNKNOWN;
    }
    
    /**
     * Check if this outcome requires immediate attention.
     */
    public boolean requiresImmediateAttention() {
        return this == BLOCKED || this == DENIED || this == UNKNOWN;
    }
    
    /**
     * Get the severity level associated with this outcome.
     */
    public AuditSeverity getImpliedSeverity() {
        return switch (this) {
            case SUCCESS -> AuditSeverity.LOW;
            case SUCCESS_WITH_WARNINGS, SKIPPED, CANCELLED -> AuditSeverity.LOW;
            case IN_PROGRESS, RETRY -> AuditSeverity.LOW;
            case TIMEOUT, FAILURE -> AuditSeverity.MEDIUM;
            case DENIED -> AuditSeverity.HIGH;
            case BLOCKED -> AuditSeverity.HIGH;
            case UNKNOWN -> AuditSeverity.CRITICAL;
        };
    }
    
    /**
     * Get color code for UI representation.
     */
    public String getColorCode() {
        return switch (this) {
            case SUCCESS -> "#28a745";           // Green
            case SUCCESS_WITH_WARNINGS -> "#ffc107";  // Yellow
            case FAILURE, TIMEOUT -> "#fd7e14";      // Orange
            case BLOCKED, DENIED -> "#dc3545";       // Red
            case CANCELLED, SKIPPED -> "#6c757d";    // Gray
            case IN_PROGRESS -> "#17a2b8";          // Blue
            case RETRY -> "#ffc107";                // Yellow
            case UNKNOWN -> "#dc3545";              // Red
        };
    }
    
    /**
     * Get emoji representation for notifications.
     */
    public String getEmoji() {
        return switch (this) {
            case SUCCESS -> "✅";
            case SUCCESS_WITH_WARNINGS -> "⚠️";
            case FAILURE, TIMEOUT -> "❌";
            case BLOCKED, DENIED -> "🚫";
            case CANCELLED -> "🚪";
            case SKIPPED -> "⏭️";
            case IN_PROGRESS -> "⏳";
            case RETRY -> "🔄";
            case UNKNOWN -> "❓";
        };
    }
    
    /**
     * Check if this outcome should trigger security alerts.
     */
    public boolean shouldTriggerSecurityAlert() {
        return this == BLOCKED || this == DENIED || this == UNKNOWN;
    }
    
    /**
     * Get recommended follow-up action.
     */
    public String getRecommendedAction() {
        return switch (this) {
            case SUCCESS -> "No action required";
            case SUCCESS_WITH_WARNINGS -> "Review warnings";
            case FAILURE -> "Investigate failure cause";
            case BLOCKED -> "Security review required";
            case DENIED -> "Verify permissions";
            case TIMEOUT -> "Check system performance";
            case CANCELLED -> "Verify if cancellation was intentional";
            case SKIPPED -> "Verify skip conditions";
            case IN_PROGRESS -> "Monitor progress";
            case RETRY -> "Monitor retry attempts";
            case UNKNOWN -> "Immediate investigation required";
        };
    }
}