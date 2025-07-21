package com.noahbackup.appsec.audit;

/**
 * Enumeration of audit event types for comprehensive security logging.
 * 
 * Categorizes security-relevant events for proper audit trail management
 * and compliance with security frameworks (ISO 27001, SOC 2, PCI DSS).
 */
public enum AuditEventType {
    
    // Authentication Events
    AUTHENTICATION("Authentication", "User authentication events", true),
    
    // Authorization Events  
    AUTHORIZATION("Authorization", "Access control and permission events", true),
    
    // Security Threats
    SECURITY_THREAT("Security Threat", "Security attacks and threat detection", true),
    
    // Configuration Management
    CONFIGURATION_CHANGE("Configuration Change", "System and security configuration changes", true),
    
    // Administrative Actions
    ADMINISTRATIVE_ACTION("Administrative Action", "Admin and privileged user actions", true),
    
    // Data Access and Handling
    DATA_ACCESS("Data Access", "Sensitive data access and manipulation", true),
    DATA_EXPORT("Data Export", "Data export and download operations", true),
    DATA_IMPORT("Data Import", "Data import and upload operations", false),
    
    // Backup Operations
    BACKUP_OPERATION("Backup Operation", "Backup-related operations and events", false),
    BACKUP_ACCESS("Backup Access", "Access to backup files and data", true),
    
    // System Events
    SYSTEM_START("System Start", "System startup and initialization", false),
    SYSTEM_SHUTDOWN("System Shutdown", "System shutdown and termination", false),
    SERVICE_START("Service Start", "Service startup events", false),
    SERVICE_STOP("Service Stop", "Service shutdown events", false),
    
    // Network and Communication
    NETWORK_CONNECTION("Network Connection", "Network connection events", false),
    API_ACCESS("API Access", "API endpoint access events", false),
    EXTERNAL_INTEGRATION("External Integration", "External system integration events", false),
    
    // Error and Exception Events
    SYSTEM_ERROR("System Error", "System errors and exceptions", false),
    SECURITY_ERROR("Security Error", "Security-related errors", true),
    
    // Compliance and Audit
    COMPLIANCE_EVENT("Compliance Event", "Regulatory compliance events", true),
    AUDIT_LOG_ACCESS("Audit Log Access", "Access to audit logs", true),
    
    // Performance and Monitoring
    PERFORMANCE_ALERT("Performance Alert", "Performance-related alerts", false),
    RESOURCE_USAGE("Resource Usage", "System resource usage events", false),
    
    // Maintenance and Operations
    MAINTENANCE_START("Maintenance Start", "System maintenance start", false),
    MAINTENANCE_END("Maintenance End", "System maintenance completion", false),
    
    // Generic catch-all
    OTHER("Other", "Other security-relevant events", false);
    
    private final String displayName;
    private final String description;
    private final boolean highPriority;
    
    AuditEventType(String displayName, String description, boolean highPriority) {
        this.displayName = displayName;
        this.description = description;
        this.highPriority = highPriority;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isHighPriority() {
        return highPriority;
    }
    
    /**
     * Check if this event type requires real-time alerting.
     */
    public boolean requiresRealTimeAlert() {
        return this == SECURITY_THREAT || 
               this == SECURITY_ERROR ||
               this == DATA_EXPORT ||
               this == ADMINISTRATIVE_ACTION;
    }
    
    /**
     * Check if this event type requires long-term retention.
     */
    public boolean requiresLongTermRetention() {
        return this == AUTHENTICATION ||
               this == AUTHORIZATION ||
               this == SECURITY_THREAT ||
               this == CONFIGURATION_CHANGE ||
               this == ADMINISTRATIVE_ACTION ||
               this == DATA_ACCESS ||
               this == DATA_EXPORT ||
               this == COMPLIANCE_EVENT ||
               this == AUDIT_LOG_ACCESS;
    }
    
    /**
     * Get recommended retention period in days.
     */
    public int getRecommendedRetentionDays() {
        if (requiresLongTermRetention()) {
            return switch (this) {
                case AUTHENTICATION, AUTHORIZATION -> 365;  // 1 year
                case SECURITY_THREAT, ADMINISTRATIVE_ACTION -> 2555;  // 7 years
                case CONFIGURATION_CHANGE, DATA_ACCESS -> 1095;  // 3 years
                case DATA_EXPORT, COMPLIANCE_EVENT -> 2555;  // 7 years
                case AUDIT_LOG_ACCESS -> 2555;  // 7 years
                default -> 365;
            };
        }
        return 90;  // 3 months for other events
    }
    
    /**
     * Get compliance categories this event type supports.
     */
    public String[] getComplianceCategories() {
        return switch (this) {
            case AUTHENTICATION, AUTHORIZATION -> new String[]{"ISO27001", "SOC2", "PCI-DSS"};
            case SECURITY_THREAT, SECURITY_ERROR -> new String[]{"ISO27001", "SOC2", "NIST"};
            case DATA_ACCESS, DATA_EXPORT -> new String[]{"GDPR", "CCPA", "SOC2", "PCI-DSS"};
            case CONFIGURATION_CHANGE -> new String[]{"ISO27001", "SOC2", "NIST"};
            case ADMINISTRATIVE_ACTION -> new String[]{"ISO27001", "SOC2", "PCI-DSS"};
            case BACKUP_ACCESS -> new String[]{"ISO27001", "SOC2"};
            case COMPLIANCE_EVENT -> new String[]{"ALL"};
            default -> new String[]{"GENERAL"};
        };
    }
    
    /**
     * Check if event type is related to user activity.
     */
    public boolean isUserActivity() {
        return this == AUTHENTICATION ||
               this == AUTHORIZATION ||
               this == DATA_ACCESS ||
               this == DATA_EXPORT ||
               this == API_ACCESS ||
               this == BACKUP_ACCESS;
    }
    
    /**
     * Check if event type is related to system activity.
     */
    public boolean isSystemActivity() {
        return this == SYSTEM_START ||
               this == SYSTEM_SHUTDOWN ||
               this == SERVICE_START ||
               this == SERVICE_STOP ||
               this == MAINTENANCE_START ||
               this == MAINTENANCE_END ||
               this == SYSTEM_ERROR;
    }
    
    /**
     * Check if event type is related to security.
     */
    public boolean isSecurityEvent() {
        return this == SECURITY_THREAT ||
               this == SECURITY_ERROR ||
               this == AUTHENTICATION ||
               this == AUTHORIZATION ||
               this == CONFIGURATION_CHANGE ||
               this == ADMINISTRATIVE_ACTION;
    }
}