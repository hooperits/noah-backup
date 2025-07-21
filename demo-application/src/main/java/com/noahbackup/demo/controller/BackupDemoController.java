package com.noahbackup.demo.controller;

import com.noahbackup.integration.VSSToS3BackupService;
import com.noahbackup.scheduler.BackupJobScheduler;
import com.noahbackup.scheduler.config.BackupScheduleProperties;
import com.noahbackup.storage.S3ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * REST API controller for Noah Backup demo operations.
 * Provides endpoints to manually trigger backups, check status, and view configuration.
 */
@RestController
@RequestMapping("/api/backup")
public class BackupDemoController {
    
    private static final Logger logger = LoggerFactory.getLogger(BackupDemoController.class);
    
    private final BackupJobScheduler backupJobScheduler;
    private final BackupScheduleProperties scheduleProperties;
    
    @Autowired
    public BackupDemoController(BackupJobScheduler backupJobScheduler, 
                               BackupScheduleProperties scheduleProperties) {
        this.backupJobScheduler = backupJobScheduler;
        this.scheduleProperties = scheduleProperties;
    }
    
    /**
     * GET /api/backup/status
     * Returns the current status of the backup system.
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getBackupStatus() {
        logger.debug("Backup status requested");
        
        Map<String, Object> status = new HashMap<>();
        status.put("timestamp", LocalDateTime.now());
        status.put("enabled", scheduleProperties.isEnabled());
        status.put("running", backupJobScheduler.isBackupRunning());
        status.put("weekly_enabled", scheduleProperties.isWeeklyEnabled());
        status.put("backup_paths_count", scheduleProperties.getPaths().size());
        status.put("bucket", scheduleProperties.getBucket());
        status.put("timeout_minutes", scheduleProperties.getTimeoutMinutes());
        status.put("schedule", scheduleProperties.getSchedule());
        
        return ResponseEntity.ok(status);
    }
    
    /**
     * POST /api/backup/start
     * Manually triggers a backup job.
     */
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startManualBackup() {
        logger.info("Manual backup requested via API");
        
        Map<String, Object> response = new HashMap<>();
        
        if (backupJobScheduler.isBackupRunning()) {
            response.put("success", false);
            response.put("message", "Backup job is already running");
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(409).body(response);
        }
        
        try {
            BackupJobScheduler.BackupJobResult result = backupJobScheduler.executeManualBackup();
            
            response.put("success", result.isSuccess());
            response.put("job_type", result.getJobType());
            response.put("success_count", result.getSuccessCount());
            response.put("failure_count", result.getFailureCount());
            response.put("message", result.getMessage());
            response.put("timestamp", LocalDateTime.now());
            
            if (result.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.internalServerError().body(response);
            }
            
        } catch (Exception e) {
            logger.error("Manual backup failed", e);
            response.put("success", false);
            response.put("message", "Manual backup failed: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * GET /api/backup/config
     * Returns the current backup configuration (sanitized).
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getBackupConfig() {
        logger.debug("Backup configuration requested");
        
        Map<String, Object> config = new HashMap<>();
        config.put("enabled", scheduleProperties.isEnabled());
        config.put("weekly_enabled", scheduleProperties.isWeeklyEnabled());
        config.put("backup_paths", scheduleProperties.getPaths());
        config.put("bucket", scheduleProperties.getBucket());
        config.put("timeout_minutes", scheduleProperties.getTimeoutMinutes());
        config.put("daily_schedule", scheduleProperties.getSchedule().getDaily());
        config.put("weekly_schedule", scheduleProperties.getSchedule().getWeekly());
        
        return ResponseEntity.ok(config);
    }
    
    /**
     * GET /api/backup/demo
     * Returns demo information and usage instructions.
     */
    @GetMapping("/demo")
    public ResponseEntity<Map<String, Object>> getDemoInfo() {
        Map<String, Object> demo = new HashMap<>();
        demo.put("name", "Noah Backup Demo Application");
        demo.put("version", "1.0.0");
        demo.put("description", "Enterprise backup solution with VSS support and S3-compatible storage");
        
        Map<String, String> features = new HashMap<>();
        features.put("vss_integration", "Volume Shadow Copy Service for locked files");
        features.put("s3_storage", "S3-compatible storage (AWS S3, MinIO, Lightsail)");
        features.put("automated_scheduling", "Cron-based backup scheduling");
        features.put("rest_api", "RESTful API for remote management");
        features.put("health_monitoring", "Built-in health checks and monitoring");
        demo.put("features", features);
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("status", "GET /api/backup/status");
        endpoints.put("start_backup", "POST /api/backup/start");
        endpoints.put("config", "GET /api/backup/config");
        endpoints.put("health", "GET /actuator/health");
        endpoints.put("scheduled_tasks", "GET /actuator/scheduledtasks");
        demo.put("api_endpoints", endpoints);
        
        Map<String, String> setup = new HashMap<>();
        setup.put("step1", "Copy .env.example to .env and configure S3 credentials");
        setup.put("step2", "Edit application.yml to set backup paths");
        setup.put("step3", "Run: java -jar noah-backup-demo.jar");
        setup.put("step4", "Test: curl http://localhost:8080/api/backup/status");
        demo.put("setup_instructions", setup);
        
        return ResponseEntity.ok(demo);
    }
    
    /**
     * POST /api/backup/test
     * Runs a test backup workflow (simulation mode).
     */
    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> runTestBackup(@RequestParam(defaultValue = "false") boolean simulation) {
        logger.info("Test backup requested (simulation={})", simulation);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (simulation) {
                // Run simulation mode (no actual VSS or S3 operations)
                response.put("success", true);
                response.put("mode", "simulation");
                response.put("message", "Test backup simulation completed successfully");
                response.put("simulated_operations", Map.of(
                    "vss_snapshot", "Created mock VSS snapshot",
                    "file_copy", "Simulated file copy operations",
                    "s3_upload", "Simulated S3 upload operations",
                    "cleanup", "Simulated cleanup operations"
                ));
            } else {
                // Run actual test backup
                BackupJobScheduler.BackupJobResult result = backupJobScheduler.executeManualBackup();
                response.put("success", result.isSuccess());
                response.put("mode", "actual");
                response.put("message", result.getMessage());
                response.put("success_count", result.getSuccessCount());
                response.put("failure_count", result.getFailureCount());
            }
            
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Test backup failed", e);
            response.put("success", false);
            response.put("message", "Test backup failed: " + e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}