package com.noahbackup.appsec.audit;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Standardized audit event structure for security logging.
 * 
 * Provides a comprehensive data model for all security-related events
 * with standardized fields for SIEM integration and compliance reporting.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditEvent {
    
    private String eventId;
    private String correlationId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime timestamp;
    
    private AuditEventType eventType;
    private String subType;
    private AuditOutcome outcome;
    private AuditSeverity severity;
    
    // User context
    private String userId;
    private String username;
    private String userRole;
    private String sessionId;
    
    // Request context
    private String ipAddress;
    private String userAgent;
    private String requestId;
    private String sourceSystem;
    
    // Event details
    private String message;
    private String description;
    private Map<String, Object> details;
    
    // Compliance fields
    private String complianceCategory;
    private String regulatoryRequirement;
    
    private AuditEvent(Builder builder) {
        this.eventId = builder.eventId != null ? builder.eventId : UUID.randomUUID().toString();
        this.correlationId = builder.correlationId;
        this.timestamp = builder.timestamp != null ? builder.timestamp : LocalDateTime.now();
        this.eventType = builder.eventType;
        this.subType = builder.subType;
        this.outcome = builder.outcome;
        this.severity = builder.severity != null ? builder.severity : AuditSeverity.MEDIUM;
        this.userId = builder.userId;
        this.username = builder.username;
        this.userRole = builder.userRole;
        this.sessionId = builder.sessionId;
        this.ipAddress = builder.ipAddress;
        this.userAgent = builder.userAgent;
        this.requestId = builder.requestId;
        this.sourceSystem = builder.sourceSystem != null ? builder.sourceSystem : "noah-backup";
        this.message = builder.message;
        this.description = builder.description;
        this.details = builder.details;
        this.complianceCategory = builder.complianceCategory;
        this.regulatoryRequirement = builder.regulatoryRequirement;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String eventId;
        private String correlationId;
        private LocalDateTime timestamp;
        private AuditEventType eventType;
        private String subType;
        private AuditOutcome outcome;
        private AuditSeverity severity;
        private String userId;
        private String username;
        private String userRole;
        private String sessionId;
        private String ipAddress;
        private String userAgent;
        private String requestId;
        private String sourceSystem;
        private String message;
        private String description;
        private Map<String, Object> details;
        private String complianceCategory;
        private String regulatoryRequirement;
        
        public Builder eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }
        
        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }
        
        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        public Builder eventType(AuditEventType eventType) {
            this.eventType = eventType;
            return this;
        }
        
        public Builder subType(String subType) {
            this.subType = subType;
            return this;
        }
        
        public Builder outcome(AuditOutcome outcome) {
            this.outcome = outcome;
            return this;
        }
        
        public Builder severity(AuditSeverity severity) {
            this.severity = severity;
            return this;
        }
        
        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }
        
        public Builder username(String username) {
            this.username = username;
            return this;
        }
        
        public Builder userRole(String userRole) {
            this.userRole = userRole;
            return this;
        }
        
        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }
        
        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }
        
        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }
        
        public Builder requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }
        
        public Builder sourceSystem(String sourceSystem) {
            this.sourceSystem = sourceSystem;
            return this;
        }
        
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public Builder details(Map<String, Object> details) {
            this.details = details;
            return this;
        }
        
        public Builder complianceCategory(String complianceCategory) {
            this.complianceCategory = complianceCategory;
            return this;
        }
        
        public Builder regulatoryRequirement(String regulatoryRequirement) {
            this.regulatoryRequirement = regulatoryRequirement;
            return this;
        }
        
        public AuditEvent build() {
            return new AuditEvent(this);
        }
    }
    
    // Getters
    public String getEventId() { return eventId; }
    public String getCorrelationId() { return correlationId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public AuditEventType getEventType() { return eventType; }
    public String getSubType() { return subType; }
    public AuditOutcome getOutcome() { return outcome; }
    public AuditSeverity getSeverity() { return severity; }
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getUserRole() { return userRole; }
    public String getSessionId() { return sessionId; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public String getRequestId() { return requestId; }
    public String getSourceSystem() { return sourceSystem; }
    public String getMessage() { return message; }
    public String getDescription() { return description; }
    public Map<String, Object> getDetails() { return details; }
    public String getComplianceCategory() { return complianceCategory; }
    public String getRegulatoryRequirement() { return regulatoryRequirement; }
    
    @Override
    public String toString() {
        return String.format("AuditEvent{id='%s', type=%s, outcome=%s, user='%s', message='%s'}", 
                eventId, eventType, outcome, userId != null ? userId : "anonymous", message);
    }
}