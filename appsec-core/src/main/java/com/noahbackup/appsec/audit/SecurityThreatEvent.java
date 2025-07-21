package com.noahbackup.appsec.audit;

import com.noahbackup.appsec.validation.ThreatType;
import com.noahbackup.appsec.validation.ThreatSeverity;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Security threat event data for audit logging.
 */
public class SecurityThreatEvent {
    
    private final ThreatType threatType;
    private final ThreatSeverity severity;
    private final String description;
    private final String userId;
    private final String sessionId;
    private final String ipAddress;
    private final String userAgent;
    private final String inputValue;
    private final String fieldName;
    private final Map<String, Object> details;
    private final LocalDateTime timestamp;
    
    public SecurityThreatEvent(ThreatType threatType, ThreatSeverity severity, String description,
                              String userId, String sessionId, String ipAddress, String userAgent,
                              String inputValue, String fieldName, Map<String, Object> details) {
        this.threatType = threatType;
        this.severity = severity;
        this.description = description;
        this.userId = userId;
        this.sessionId = sessionId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.inputValue = inputValue;
        this.fieldName = fieldName;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters
    public ThreatType getThreatType() { return threatType; }
    public ThreatSeverity getSeverity() { return severity; }
    public String getDescription() { return description; }
    public String getUserId() { return userId; }
    public String getSessionId() { return sessionId; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public String getInputValue() { return inputValue; }
    public String getFieldName() { return fieldName; }
    public Map<String, Object> getDetails() { return details; }
    public LocalDateTime getTimestamp() { return timestamp; }
}

/**
 * Administrative action types for audit logging.
 */
enum AdminAction {
    USER_CREATE("Create User"),
    USER_DELETE("Delete User"), 
    USER_MODIFY("Modify User"),
    ROLE_ASSIGN("Assign Role"),
    ROLE_REVOKE("Revoke Role"),
    PERMISSION_GRANT("Grant Permission"),
    PERMISSION_REVOKE("Revoke Permission"),
    CONFIG_CHANGE("Configuration Change"),
    SYSTEM_MAINTENANCE("System Maintenance"),
    BACKUP_RESTORE("Backup Restore"),
    LOG_ACCESS("Log Access"),
    AUDIT_REVIEW("Audit Review");
    
    private final String displayName;
    
    AdminAction(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

/**
 * Data access types for audit logging.
 */
enum DataAccessType {
    READ("Read"), 
    WRITE("Write"), 
    UPDATE("Update"), 
    DELETE("Delete"),
    EXPORT("Export"), 
    IMPORT("Import"),
    COPY("Copy"), 
    MOVE("Move");
    
    private final String displayName;
    
    DataAccessType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

/**
 * Backup audit event for operation tracking.
 */
class BackupAuditEvent {
    
    public enum OperationType {
        BACKUP_START, BACKUP_COMPLETE, BACKUP_FAILED,
        RESTORE_START, RESTORE_COMPLETE, RESTORE_FAILED,
        SCHEDULE_CHANGE, CONFIGURATION_UPDATE
    }
    
    private final OperationType operationType;
    private final boolean success;
    private final String description;
    private final Map<String, Object> details;
    
    public BackupAuditEvent(OperationType operationType, boolean success, 
                           String description, Map<String, Object> details) {
        this.operationType = operationType;
        this.success = success;
        this.description = description;
        this.details = details;
    }
    
    public OperationType getOperationType() { return operationType; }
    public boolean isSuccess() { return success; }
    public String getDescription() { return description; }
    public Map<String, Object> getDetails() { return details; }
}