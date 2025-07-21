package com.noahbackup.api.controller;

import com.noahbackup.api.dto.ConfigResponse;
import com.noahbackup.api.dto.ScheduleUpdateRequest;
import com.noahbackup.auth.SecureCodingUtils;
import com.noahbackup.scheduler.config.BackupScheduleProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * REST API controller for configuration management.
 * 
 * Provides secure endpoints for:
 * - Viewing current configuration
 * - Updating backup schedules
 * - Managing backup paths
 * 
 * Security:
 * - JWT authentication required
 * - Admin-only for configuration changes
 * - Sensitive data masking in responses
 */
@RestController
@RequestMapping("/api/v1/config")
@Validated
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8081"}, maxAge = 3600)
public class ConfigController {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);
    
    private final BackupScheduleProperties scheduleProperties;
    
    @Autowired
    public ConfigController(BackupScheduleProperties scheduleProperties) {
        this.scheduleProperties = scheduleProperties;
    }
    
    /**
     * GET /api/v1/config
     * Returns the current backup configuration (sanitized for security).
     */
    @GetMapping
    @PreAuthorize("hasRole('BACKUP_ADMIN') or hasRole('BACKUP_USER') or hasRole('BACKUP_VIEWER')")
    public ResponseEntity<ConfigResponse> getConfiguration() {
        SecureCodingUtils.safeLog(logger, "DEBUG", "Configuration requested via API");
        
        try {
            ConfigResponse config = ConfigResponse.builder()
                .timestamp(LocalDateTime.now())
                .enabled(scheduleProperties.isEnabled())
                .weeklyEnabled(scheduleProperties.isWeeklyEnabled())
                .backupPaths(scheduleProperties.getPaths())
                .bucket(scheduleProperties.getBucket())
                .timeoutMinutes(scheduleProperties.getTimeoutMinutes())
                .dailySchedule(scheduleProperties.getSchedule().getDaily())
                .weeklySchedule(scheduleProperties.getSchedule().getWeekly())
                .build();
            
            return ResponseEntity.ok(config);
            
        } catch (Exception e) {
            String safeMessage = SecureCodingUtils.createSafeExceptionMessage("Configuration retrieval", e);
            SecureCodingUtils.safeLog(logger, "ERROR", safeMessage);
            
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * GET /api/v1/config/system
     * Returns system-level configuration and capabilities.
     */
    @GetMapping("/system")
    @PreAuthorize("hasRole('BACKUP_ADMIN')")
    public ResponseEntity<Map<String, Object>> getSystemConfiguration() {
        SecureCodingUtils.safeLog(logger, "DEBUG", "System configuration requested via API");
        
        try {
            Map<String, Object> systemConfig = new HashMap<>();
            systemConfig.put("timestamp", LocalDateTime.now());
            
            // System information (safe to expose)
            systemConfig.put("java_version", System.getProperty("java.version"));
            systemConfig.put("os_name", System.getProperty("os.name"));
            systemConfig.put("app_version", "1.0.0");
            
            // Feature capabilities
            Map<String, Boolean> features = new HashMap<>();
            features.put("vss_support", System.getProperty("os.name").toLowerCase().contains("windows"));
            features.put("encryption_support", true);
            features.put("s3_support", true);
            features.put("scheduling_support", true);
            features.put("api_support", true);
            systemConfig.put("features", features);
            
            // Configuration limits
            Map<String, Integer> limits = new HashMap<>();
            limits.put("max_backup_paths", 100);
            limits.put("max_concurrent_backups", 1);
            limits.put("max_file_size_gb", 50);
            limits.put("api_rate_limit_per_hour", 1000);
            systemConfig.put("limits", limits);
            
            return ResponseEntity.ok(systemConfig);
            
        } catch (Exception e) {
            String safeMessage = SecureCodingUtils.createSafeExceptionMessage("System configuration", e);
            SecureCodingUtils.safeLog(logger, "ERROR", safeMessage);
            
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * POST /api/v1/config/schedule
     * Updates the backup schedule configuration.
     */
    @PostMapping("/schedule")
    @PreAuthorize("hasRole('BACKUP_ADMIN')")
    public ResponseEntity<Map<String, Object>> updateSchedule(@Valid @RequestBody ScheduleUpdateRequest request) {
        SecureCodingUtils.safeLog(logger, "INFO", "Schedule update requested via API");
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        
        try {
            // Validate cron expressions (basic validation)
            if (request.getDailySchedule() != null) {
                validateCronExpression(request.getDailySchedule());
            }
            if (request.getWeeklySchedule() != null) {
                validateCronExpression(request.getWeeklySchedule());
            }
            
            // Note: In a real implementation, this would update the configuration
            // For now, we simulate the update
            boolean scheduleUpdated = false; // Placeholder
            
            if (scheduleUpdated) {
                response.put("success", true);
                response.put("message", "Schedule updated successfully");
                
                SecureCodingUtils.safeLog(logger, "INFO", "Backup schedule updated successfully");
            } else {
                response.put("success", false);
                response.put("message", "Schedule update not implemented - configuration is read-only");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "Invalid schedule format: " + e.getMessage());
            
            SecureCodingUtils.safeLog(logger, "WARN", "Invalid schedule update request: {}", e.getMessage());
            return ResponseEntity.status(400).body(response);
            
        } catch (Exception e) {
            String safeMessage = SecureCodingUtils.createSafeExceptionMessage("Schedule update", e);
            SecureCodingUtils.safeLog(logger, "ERROR", safeMessage);
            
            response.put("success", false);
            response.put("message", "Schedule update failed");
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * GET /api/v1/config/validate
     * Validates the current configuration for issues.
     */
    @GetMapping("/validate")
    @PreAuthorize("hasRole('BACKUP_ADMIN') or hasRole('BACKUP_USER')")
    public ResponseEntity<Map<String, Object>> validateConfiguration() {
        SecureCodingUtils.safeLog(logger, "DEBUG", "Configuration validation requested via API");
        
        Map<String, Object> validation = new HashMap<>();
        validation.put("timestamp", LocalDateTime.now());
        
        try {
            boolean isValid = true;
            Map<String, String> checks = new HashMap<>();
            
            // Check backup paths
            if (scheduleProperties.getPaths().isEmpty()) {
                checks.put("backup_paths", "WARNING: No backup paths configured");
                isValid = false;
            } else {
                checks.put("backup_paths", "OK: " + scheduleProperties.getPaths().size() + " paths configured");
            }
            
            // Check bucket configuration
            if (scheduleProperties.getBucket() == null || scheduleProperties.getBucket().trim().isEmpty()) {
                checks.put("s3_bucket", "ERROR: No S3 bucket configured");
                isValid = false;
            } else {
                checks.put("s3_bucket", "OK: Bucket configured");
            }
            
            // Check schedule configuration
            if (!scheduleProperties.isEnabled()) {
                checks.put("scheduling", "WARNING: Backup scheduling is disabled");
            } else {
                checks.put("scheduling", "OK: Scheduling enabled");
            }
            
            // Check timeout configuration
            if (scheduleProperties.getTimeoutMinutes() <= 0) {
                checks.put("timeout", "WARNING: Invalid timeout configuration");
            } else {
                checks.put("timeout", "OK: Timeout configured");
            }
            
            validation.put("valid", isValid);
            validation.put("checks", checks);
            
            return ResponseEntity.ok(validation);
            
        } catch (Exception e) {
            String safeMessage = SecureCodingUtils.createSafeExceptionMessage("Configuration validation", e);
            SecureCodingUtils.safeLog(logger, "ERROR", safeMessage);
            
            validation.put("valid", false);
            validation.put("error", "Validation failed");
            
            return ResponseEntity.status(500).body(validation);
        }
    }
    
    /**
     * Basic cron expression validation.
     */
    private void validateCronExpression(String cronExpression) throws IllegalArgumentException {
        if (cronExpression == null || cronExpression.trim().isEmpty()) {
            throw new IllegalArgumentException("Cron expression cannot be empty");
        }
        
        String[] parts = cronExpression.trim().split("\\s+");
        if (parts.length != 6) {
            throw new IllegalArgumentException("Cron expression must have 6 fields (second minute hour day month dayOfWeek)");
        }
        
        // Additional validation could be added here
        // For production, use a proper cron expression parser
    }
}