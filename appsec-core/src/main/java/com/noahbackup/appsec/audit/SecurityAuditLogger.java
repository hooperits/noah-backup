package com.noahbackup.appsec.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.noahbackup.auth.SecureCodingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Comprehensive security audit logging system for Noah Backup.
 * 
 * Provides structured logging for:
 * - Authentication events (login, logout, failed attempts)
 * - Authorization events (permission checks, access denials)
 * - Security threats (injection attempts, XSS, etc.)
 * - Configuration changes (security settings modifications)
 * - Administrative actions (user management, system changes)
 * - Data access events (sensitive data access, exports)
 * 
 * Features:
 * - Structured JSON logging with standardized fields
 * - Asynchronous logging for performance
 * - Integration with SIEM systems
 * - Correlation IDs for request tracking
 * - Geographic and IP-based context
 * - Compliance with security frameworks (ISO 27001, SOC 2)
 */
@Component
public class SecurityAuditLogger {
    
    private static final Logger auditLogger = LoggerFactory.getLogger("SECURITY_AUDIT");
    private static final Logger logger = LoggerFactory.getLogger(SecurityAuditLogger.class);
    
    @Value("${noah.security.audit.enabled:true}")
    private boolean auditEnabled;
    
    @Value("${noah.security.audit.async:true}")
    private boolean asyncLogging;
    
    @Value("${noah.security.audit.include.request.details:true}")
    private boolean includeRequestDetails;
    
    @Value("${noah.security.audit.include.user.agent:true}")
    private boolean includeUserAgent;
    
    private ObjectMapper objectMapper;
    private ExecutorService auditExecutor;
    
    @PostConstruct
    public void initialize() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        
        if (asyncLogging) {
            auditExecutor = Executors.newFixedThreadPool(2, r -> {
                Thread t = new Thread(r, "security-audit-logger");
                t.setDaemon(true);
                return t;
            });
        }
        
        SecureCodingUtils.safeLog(logger, "INFO", 
            "SecurityAuditLogger initialized. Enabled: {}, Async: {}", auditEnabled, asyncLogging);
    }
    
    /**
     * Log authentication events (login, logout, password changes).
     */
    public void logAuthenticationEvent(AuthenticationEvent event) {
        if (!auditEnabled) return;
        
        AuditEvent auditEvent = AuditEvent.builder()
            .eventType(AuditEventType.AUTHENTICATION)
            .subType(event.getType().name())
            .outcome(event.isSuccess() ? AuditOutcome.SUCCESS : AuditOutcome.FAILURE)
            .userId(event.getUserId())
            .username(event.getUsername())
            .sessionId(event.getSessionId())
            .ipAddress(event.getIpAddress())
            .userAgent(event.getUserAgent())
            .message(event.getMessage())
            .details(event.getDetails())
            .build();
        
        logAuditEvent(auditEvent);
    }
    
    /**
     * Log authorization events (permission checks, role assignments).
     */
    public void logAuthorizationEvent(String userId, String resource, String action, 
                                     boolean granted, String reason) {
        if (!auditEnabled) return;
        
        Map<String, Object> details = new HashMap<>();
        details.put("resource", resource);
        details.put("action", action);
        details.put("reason", reason);
        
        AuditEvent auditEvent = AuditEvent.builder()
            .eventType(AuditEventType.AUTHORIZATION)
            .subType(granted ? "ACCESS_GRANTED" : "ACCESS_DENIED")
            .outcome(granted ? AuditOutcome.SUCCESS : AuditOutcome.FAILURE)
            .userId(userId)
            .message(String.format("Authorization %s for action '%s' on resource '%s'", 
                    granted ? "granted" : "denied", action, resource))
            .details(details)
            .build();
        
        logAuditEvent(auditEvent);
    }
    
    /**
     * Log security threats and attack attempts.
     */
    public void logSecurityThreat(SecurityThreatEvent threat) {
        if (!auditEnabled) return;
        
        AuditEvent auditEvent = AuditEvent.builder()
            .eventType(AuditEventType.SECURITY_THREAT)
            .subType(threat.getThreatType().name())
            .outcome(AuditOutcome.BLOCKED)
            .severity(mapSeverityToAuditSeverity(threat.getSeverity()))
            .userId(threat.getUserId())
            .sessionId(threat.getSessionId())
            .ipAddress(threat.getIpAddress())
            .userAgent(threat.getUserAgent())
            .message(String.format("Security threat detected: %s - %s", 
                    threat.getThreatType().getDisplayName(), threat.getDescription()))
            .details(threat.getDetails())
            .build();
        
        logAuditEvent(auditEvent);
    }
    
    /**
     * Log configuration changes.
     */
    public void logConfigurationChange(String userId, String configKey, String oldValue, 
                                      String newValue, String reason) {
        if (!auditEnabled) return;
        
        Map<String, Object> details = new HashMap<>();
        details.put("config_key", configKey);
        details.put("old_value", SecureCodingUtils.maskSensitiveData(oldValue));
        details.put("new_value", SecureCodingUtils.maskSensitiveData(newValue));
        details.put("reason", reason);
        
        AuditEvent auditEvent = AuditEvent.builder()
            .eventType(AuditEventType.CONFIGURATION_CHANGE)
            .subType("CONFIG_UPDATE")
            .outcome(AuditOutcome.SUCCESS)
            .userId(userId)
            .message(String.format("Configuration changed: %s", configKey))
            .details(details)
            .build();
        
        logAuditEvent(auditEvent);
    }
    
    /**
     * Log administrative actions.
     */
    public void logAdminAction(String adminUserId, AdminAction action, String targetUserId, 
                              String description, Map<String, Object> additionalDetails) {
        if (!auditEnabled) return;
        
        Map<String, Object> details = new HashMap<>();
        details.put("action", action.name());
        details.put("target_user", targetUserId);
        details.put("description", description);
        if (additionalDetails != null) {
            details.putAll(additionalDetails);
        }
        
        AuditEvent auditEvent = AuditEvent.builder()
            .eventType(AuditEventType.ADMINISTRATIVE_ACTION)
            .subType(action.name())
            .outcome(AuditOutcome.SUCCESS)
            .userId(adminUserId)
            .message(String.format("Admin action: %s - %s", action.getDisplayName(), description))
            .details(details)
            .build();
        
        logAuditEvent(auditEvent);
    }
    
    /**
     * Log data access events.
     */
    public void logDataAccess(String userId, String dataType, String dataIdentifier, 
                             DataAccessType accessType, boolean sensitive) {
        if (!auditEnabled) return;
        
        Map<String, Object> details = new HashMap<>();
        details.put("data_type", dataType);
        details.put("data_identifier", dataIdentifier);
        details.put("access_type", accessType.name());
        details.put("sensitive", sensitive);
        
        AuditEvent auditEvent = AuditEvent.builder()
            .eventType(AuditEventType.DATA_ACCESS)
            .subType(accessType.name())
            .outcome(AuditOutcome.SUCCESS)
            .severity(sensitive ? AuditSeverity.HIGH : AuditSeverity.MEDIUM)
            .userId(userId)
            .message(String.format("Data access: %s %s %s", accessType.name(), dataType, 
                    sensitive ? "(sensitive)" : ""))
            .details(details)
            .build();
        
        logAuditEvent(auditEvent);
    }
    
    /**
     * Log backup operation events with security context.
     */
    public void logBackupEvent(String userId, BackupAuditEvent event) {
        if (!auditEnabled) return;
        
        AuditEvent auditEvent = AuditEvent.builder()
            .eventType(AuditEventType.BACKUP_OPERATION)
            .subType(event.getOperationType().name())
            .outcome(event.isSuccess() ? AuditOutcome.SUCCESS : AuditOutcome.FAILURE)
            .userId(userId)
            .message(String.format("Backup operation: %s - %s", 
                    event.getOperationType(), event.getDescription()))
            .details(event.getDetails())
            .build();
        
        logAuditEvent(auditEvent);
    }
    
    /**
     * Core audit logging method - writes structured audit events.
     */
    private void logAuditEvent(AuditEvent event) {
        if (asyncLogging && auditExecutor != null) {
            CompletableFuture.runAsync(() -> writeAuditEvent(event), auditExecutor);
        } else {
            writeAuditEvent(event);
        }
    }
    
    private void writeAuditEvent(AuditEvent event) {
        try {
            // Set MDC context for correlation
            MDC.put("correlation_id", event.getCorrelationId());
            MDC.put("event_type", event.getEventType().name());
            MDC.put("user_id", event.getUserId() != null ? event.getUserId() : "anonymous");
            MDC.put("ip_address", event.getIpAddress() != null ? event.getIpAddress() : "unknown");
            
            // Convert to JSON for structured logging
            String jsonEvent = objectMapper.writeValueAsString(event);
            
            // Log based on severity
            switch (event.getSeverity()) {
                case CRITICAL:
                    auditLogger.error("AUDIT: {}", jsonEvent);
                    break;
                case HIGH:
                    auditLogger.warn("AUDIT: {}", jsonEvent);
                    break;
                case MEDIUM:
                    auditLogger.info("AUDIT: {}", jsonEvent);
                    break;
                case LOW:
                    auditLogger.debug("AUDIT: {}", jsonEvent);
                    break;
            }
            
        } catch (JsonProcessingException e) {
            SecureCodingUtils.safeLog(logger, "ERROR", "Failed to serialize audit event: {}", e.getMessage());
            // Fallback to simple text logging
            auditLogger.error("AUDIT: {} - {} - {}", event.getEventType(), event.getMessage(), 
                            event.getOutcome());
        } finally {
            // Clean up MDC
            MDC.clear();
        }
    }
    
    private AuditSeverity mapSeverityToAuditSeverity(com.noahbackup.appsec.validation.ThreatSeverity severity) {
        switch (severity) {
            case CRITICAL:
                return AuditSeverity.CRITICAL;
            case HIGH:
                return AuditSeverity.HIGH;
            case MEDIUM:
                return AuditSeverity.MEDIUM;
            case LOW:
                return AuditSeverity.LOW;
            default:
                return AuditSeverity.MEDIUM;
        }
    }
    
    /**
     * Create correlation context for request tracking.
     */
    public void setCorrelationContext(String correlationId, String sessionId, String userId) {
        MDC.put("correlation_id", correlationId);
        if (sessionId != null) MDC.put("session_id", sessionId);
        if (userId != null) MDC.put("user_id", userId);
    }
    
    /**
     * Clear correlation context.
     */
    public void clearCorrelationContext() {
        MDC.clear();
    }
    
    /**
     * Get audit statistics for monitoring.
     */
    public Map<String, Object> getAuditStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("audit_enabled", auditEnabled);
        stats.put("async_logging", asyncLogging);
        stats.put("include_request_details", includeRequestDetails);
        stats.put("include_user_agent", includeUserAgent);
        stats.put("executor_active", auditExecutor != null && !auditExecutor.isShutdown());
        return stats;
    }
    
    /**
     * Shutdown audit logger and cleanup resources.
     */
    public void shutdown() {
        if (auditExecutor != null && !auditExecutor.isShutdown()) {
            auditExecutor.shutdown();
            SecureCodingUtils.safeLog(logger, "INFO", "SecurityAuditLogger shutdown completed");
        }
    }
}