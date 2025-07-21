package com.noahbackup.scheduler;

import com.noahbackup.integration.VSSToS3BackupService;
import com.noahbackup.scheduler.config.BackupScheduleProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Scheduled backup job service that automatically executes backups based on configured schedules.
 * Uses Spring's @Scheduled annotations to trigger backup operations.
 */
@Service
public class BackupJobScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(BackupJobScheduler.class);
    
    private final BackupScheduleProperties scheduleProperties;
    private final VSSToS3BackupService backupService;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    
    @Autowired
    public BackupJobScheduler(BackupScheduleProperties scheduleProperties) {
        this.scheduleProperties = scheduleProperties;
        try {
            this.backupService = new VSSToS3BackupService();
            logger.info("BackupJobScheduler initialized with {} backup paths configured", 
                       scheduleProperties.getPaths().size());
        } catch (Exception e) {
            logger.warn("Failed to initialize backup service, some operations may not work: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize backup service", e);
        }
    }
    
    // Constructor for testing with custom backup service
    public BackupJobScheduler(BackupScheduleProperties scheduleProperties, VSSToS3BackupService backupService) {
        this.scheduleProperties = scheduleProperties;
        this.backupService = backupService;
        logger.info("BackupJobScheduler initialized with custom backup service");
    }
    
    /**
     * Daily backup job - executes every day at configured time.
     * Default: 2:00 AM daily
     */
    @Scheduled(cron = "${noah.backup.schedule.daily:0 0 2 * * ?}")
    public void executeDailyBackup() {
        if (!scheduleProperties.isEnabled()) {
            logger.debug("Backup scheduling is disabled, skipping daily backup");
            return;
        }
        
        if (!isRunning.compareAndSet(false, true)) {
            logger.warn("Backup job is already running, skipping this execution");
            return;
        }
        
        try {
            logger.info("Starting daily backup job at {}", 
                       LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            executeBackupJob("Daily Backup");
            
        } catch (Exception e) {
            logger.error("Daily backup job failed", e);
        } finally {
            isRunning.set(false);
        }
    }
    
    /**
     * Weekly backup job - executes every Sunday at configured time.
     * Default: 1:00 AM every Sunday
     */
    @Scheduled(cron = "${noah.backup.schedule.weekly:0 0 1 * * SUN}")
    public void executeWeeklyBackup() {
        if (!scheduleProperties.isEnabled() || !scheduleProperties.isWeeklyEnabled()) {
            logger.debug("Weekly backup is disabled, skipping");
            return;
        }
        
        if (!isRunning.compareAndSet(false, true)) {
            logger.warn("Backup job is already running, skipping weekly backup");
            return;
        }
        
        try {
            logger.info("Starting weekly backup job at {}", 
                       LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            executeBackupJob("Weekly Backup");
            
        } catch (Exception e) {
            logger.error("Weekly backup job failed", e);
        } finally {
            isRunning.set(false);
        }
    }
    
    /**
     * Manual backup trigger - can be called programmatically.
     */
    public BackupJobResult executeManualBackup() {
        if (!isRunning.compareAndSet(false, true)) {
            String message = "Backup job is already running, cannot start manual backup";
            logger.warn(message);
            return new BackupJobResult(false, "Manual Backup", 0, 0, message);
        }
        
        try {
            logger.info("Starting manual backup job at {}", 
                       LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            return executeBackupJob("Manual Backup");
            
        } catch (Exception e) {
            logger.error("Manual backup job failed", e);
            return new BackupJobResult(false, "Manual Backup", 0, 0, 
                                     "Manual backup failed: " + e.getMessage());
        } finally {
            isRunning.set(false);
        }
    }
    
    /**
     * Core backup execution logic used by all scheduled jobs.
     */
    private BackupJobResult executeBackupJob(String jobType) {
        long startTime = System.currentTimeMillis();
        int successCount = 0;
        int failureCount = 0;
        
        List<String> backupPaths = scheduleProperties.getPaths();
        String bucketName = scheduleProperties.getBucket();
        
        logger.info("Executing {} for {} paths to bucket: {}", jobType, backupPaths.size(), bucketName);
        
        for (String path : backupPaths) {
            try {
                logger.info("Backing up path: {}", path);
                
                VSSToS3BackupService.BackupResult result = backupService.performBackup(path, bucketName);
                
                if (result.isSuccess()) {
                    successCount++;
                    logger.info("Successfully backed up: {} -> {}", 
                               path, result.getS3Result().getS3Url());
                } else {
                    failureCount++;
                    logger.error("Failed to backup path: {} - {}", path, result.getMessage());
                }
                
            } catch (Exception e) {
                failureCount++;
                logger.error("Error backing up path: {}", path, e);
            }
        }
        
        long duration = System.currentTimeMillis() - startTime;
        
        String message = String.format("%s completed: %d succeeded, %d failed in %d ms", 
                                      jobType, successCount, failureCount, duration);
        
        logger.info(message);
        
        return new BackupJobResult(failureCount == 0, jobType, successCount, failureCount, message);
    }
    
    /**
     * Health check method to verify if backup jobs are running properly.
     */
    public boolean isBackupRunning() {
        return isRunning.get();
    }
    
    /**
     * Result object for backup job execution.
     */
    public static class BackupJobResult {
        private final boolean success;
        private final String jobType;
        private final int successCount;
        private final int failureCount;
        private final String message;
        
        public BackupJobResult(boolean success, String jobType, int successCount, int failureCount, String message) {
            this.success = success;
            this.jobType = jobType;
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.message = message;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getJobType() { return jobType; }
        public int getSuccessCount() { return successCount; }
        public int getFailureCount() { return failureCount; }
        public String getMessage() { return message; }
        
        @Override
        public String toString() {
            return String.format("BackupJobResult{success=%s, jobType='%s', success=%d, failures=%d, message='%s'}", 
                               success, jobType, successCount, failureCount, message);
        }
    }
}