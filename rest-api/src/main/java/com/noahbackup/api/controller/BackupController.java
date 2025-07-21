package com.noahbackup.api.controller;

import com.noahbackup.api.dto.BackupRequest;
import com.noahbackup.api.dto.BackupResponse;
import com.noahbackup.api.dto.StatusResponse;
import com.noahbackup.auth.SecureCodingUtils;
import com.noahbackup.integration.VSSToS3BackupService;
import com.noahbackup.scheduler.BackupJobScheduler;
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
 * REST API controller for backup operations.
 * 
 * Provides secure endpoints for:
 * - Manual backup triggering
 * - Backup status monitoring
 * - System health checks
 * 
 * Security:
 * - JWT authentication required
 * - Role-based access control
 * - Input validation and sanitization
 * - Audit logging
 */
@RestController
@RequestMapping("/api/v1/backup")
@Validated
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8081"}, maxAge = 3600)
public class BackupController {
    
    private static final Logger logger = LoggerFactory.getLogger(BackupController.class);
    
    private final BackupJobScheduler backupJobScheduler;
    private final BackupScheduleProperties scheduleProperties;
    
    @Autowired
    public BackupController(BackupJobScheduler backupJobScheduler,
                           BackupScheduleProperties scheduleProperties) {
        this.backupJobScheduler = backupJobScheduler;
        this.scheduleProperties = scheduleProperties;
    }
    
    /**
     * POST /api/v1/backup/start
     * Triggers a manual backup operation.
     * 
     * @param request Backup request parameters (optional paths, bucket override)
     * @return BackupResponse with operation status and details
     */
    @PostMapping("/start")
    @PreAuthorize("hasRole('BACKUP_ADMIN') or hasRole('BACKUP_USER')")
    public ResponseEntity<BackupResponse> startBackup(@Valid @RequestBody(required = false) BackupRequest request) {
        SecureCodingUtils.safeLog(logger, "INFO", "Manual backup requested via API by user");
        
        try {
            // Check if backup is already running
            if (backupJobScheduler.isBackupRunning()) {
                BackupResponse response = BackupResponse.builder()
                    .success(false)
                    .message("Backup operation is already in progress")
                    .timestamp(LocalDateTime.now())
                    .build();
                
                return ResponseEntity.status(409).body(response);
            }
            
            // Execute backup
            BackupJobScheduler.BackupJobResult result = backupJobScheduler.executeManualBackup();
            
            BackupResponse response = BackupResponse.builder()
                .success(result.isSuccess())
                .jobType(result.getJobType())
                .successCount(result.getSuccessCount())
                .failureCount(result.getFailureCount())
                .message(result.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
            
            if (result.isSuccess()) {
                SecureCodingUtils.safeLog(logger, "INFO", "Manual backup completed successfully: {} files processed", 
                                        result.getSuccessCount());
                return ResponseEntity.ok(response);
            } else {
                SecureCodingUtils.safeLog(logger, "WARN", "Manual backup completed with failures: {} failed", 
                                        result.getFailureCount());
                return ResponseEntity.status(500).body(response);
            }
            
        } catch (Exception e) {
            String safeMessage = SecureCodingUtils.createSafeExceptionMessage("Manual backup", e);
            SecureCodingUtils.safeLog(logger, "ERROR", safeMessage);
            
            BackupResponse response = BackupResponse.builder()
                .success(false)
                .message("Manual backup failed: " + e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * GET /api/v1/backup/status
     * Returns the current status of the backup system.
     */
    @GetMapping("/status")
    @PreAuthorize("hasRole('BACKUP_ADMIN') or hasRole('BACKUP_USER') or hasRole('BACKUP_VIEWER')")
    public ResponseEntity<StatusResponse> getBackupStatus() {
        SecureCodingUtils.safeLog(logger, "DEBUG", "Backup status requested via API");
        
        try {
            StatusResponse status = StatusResponse.builder()
                .timestamp(LocalDateTime.now())
                .enabled(scheduleProperties.isEnabled())
                .running(backupJobScheduler.isBackupRunning())
                .weeklyEnabled(scheduleProperties.isWeeklyEnabled())
                .backupPathsCount(scheduleProperties.getPaths().size())
                .bucket(scheduleProperties.getBucket())
                .timeoutMinutes(scheduleProperties.getTimeoutMinutes())
                .dailySchedule(scheduleProperties.getSchedule().getDaily())
                .weeklySchedule(scheduleProperties.getSchedule().getWeekly())
                .build();
            
            return ResponseEntity.ok(status);
            
        } catch (Exception e) {
            String safeMessage = SecureCodingUtils.createSafeExceptionMessage("Status check", e);
            SecureCodingUtils.safeLog(logger, "ERROR", safeMessage);
            
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * POST /api/v1/backup/stop
     * Attempts to gracefully stop a running backup operation.
     */
    @PostMapping("/stop")
    @PreAuthorize("hasRole('BACKUP_ADMIN')")
    public ResponseEntity<Map<String, Object>> stopBackup() {
        SecureCodingUtils.safeLog(logger, "INFO", "Backup stop requested via API");
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        
        if (!backupJobScheduler.isBackupRunning()) {
            response.put("success", false);
            response.put("message", "No backup operation is currently running");
            return ResponseEntity.status(400).body(response);
        }
        
        // Note: Current implementation doesn't support graceful stop
        // This would require additional implementation in BackupJobScheduler
        response.put("success", false);
        response.put("message", "Graceful backup stop not yet implemented - backup will complete normally");
        
        return ResponseEntity.status(501).body(response);
    }
    
    /**
     * GET /api/v1/backup/history
     * Returns recent backup operation history.
     */
    @GetMapping("/history")
    @PreAuthorize("hasRole('BACKUP_ADMIN') or hasRole('BACKUP_USER') or hasRole('BACKUP_VIEWER')")
    public ResponseEntity<Map<String, Object>> getBackupHistory(
            @RequestParam(defaultValue = "10") int limit) {
        
        SecureCodingUtils.safeLog(logger, "DEBUG", "Backup history requested (limit: {})", limit);
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Backup history tracking not yet implemented");
        response.put("limit", Math.min(limit, 100)); // Cap at 100 for security
        
        // Placeholder - would integrate with report-core module
        response.put("history", java.util.Collections.emptyList());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/v1/backup/health
     * Detailed health check for backup system components.
     */
    @GetMapping("/health")
    @PreAuthorize("hasRole('BACKUP_ADMIN') or hasRole('BACKUP_USER') or hasRole('BACKUP_VIEWER')")
    public ResponseEntity<Map<String, Object>> getBackupHealth() {
        SecureCodingUtils.safeLog(logger, "DEBUG", "Backup health check requested");
        
        Map<String, Object> health = new HashMap<>();
        health.put("timestamp", LocalDateTime.now());
        
        try {
            // System validation
            boolean systemHealthy = true;
            Map<String, String> components = new HashMap<>();
            
            // Check scheduler
            components.put("scheduler", scheduleProperties.isEnabled() ? "UP" : "DISABLED");
            
            // Check configuration
            boolean hasValidConfig = !scheduleProperties.getPaths().isEmpty() && 
                                   scheduleProperties.getBucket() != null;
            components.put("configuration", hasValidConfig ? "UP" : "INVALID");
            
            // Check if backup is stuck
            boolean backupRunning = backupJobScheduler.isBackupRunning();
            components.put("backup_operation", backupRunning ? "RUNNING" : "READY");
            
            health.put("status", systemHealthy ? "UP" : "DOWN");
            health.put("components", components);
            health.put("backup_paths", scheduleProperties.getPaths().size());
            health.put("enabled", scheduleProperties.isEnabled());
            
            return ResponseEntity.ok(health);
            
        } catch (Exception e) {
            String safeMessage = SecureCodingUtils.createSafeExceptionMessage("Health check", e);
            SecureCodingUtils.safeLog(logger, "ERROR", safeMessage);
            
            health.put("status", "DOWN");
            health.put("error", "Health check failed");
            
            return ResponseEntity.status(503).body(health);
        }
    }
}