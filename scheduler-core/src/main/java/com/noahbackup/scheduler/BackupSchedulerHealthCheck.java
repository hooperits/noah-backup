package com.noahbackup.scheduler;

import com.noahbackup.scheduler.config.BackupScheduleProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Health check for the backup scheduler service.
 * Provides status information for monitoring and alerting.
 */
@Component
public class BackupSchedulerHealthCheck implements HealthIndicator {
    
    private static final Logger logger = LoggerFactory.getLogger(BackupSchedulerHealthCheck.class);
    
    private final BackupScheduleProperties scheduleProperties;
    private final BackupJobScheduler backupJobScheduler;
    
    @Autowired
    public BackupSchedulerHealthCheck(BackupScheduleProperties scheduleProperties, 
                                    BackupJobScheduler backupJobScheduler) {
        this.scheduleProperties = scheduleProperties;
        this.backupJobScheduler = backupJobScheduler;
    }
    
    @Override
    public Health health() {
        try {
            Health.Builder builder = new Health.Builder();
            
            // Check if scheduling is enabled
            if (!scheduleProperties.isEnabled()) {
                return builder.down()
                       .withDetail("status", "Backup scheduling is disabled")
                       .withDetail("enabled", false)
                       .build();
            }
            
            // Check if backup is currently running
            boolean isRunning = backupJobScheduler.isBackupRunning();
            
            // Check configuration
            boolean hasValidConfig = !scheduleProperties.getPaths().isEmpty() && 
                                   scheduleProperties.getBucket() != null && 
                                   !scheduleProperties.getBucket().trim().isEmpty();
            
            if (hasValidConfig) {
                builder.up()
                       .withDetail("status", isRunning ? "Backup job running" : "Ready for scheduled backups")
                       .withDetail("enabled", true)
                       .withDetail("running", isRunning)
                       .withDetail("backup_paths", scheduleProperties.getPaths().size())
                       .withDetail("bucket", scheduleProperties.getBucket())
                       .withDetail("weekly_enabled", scheduleProperties.isWeeklyEnabled())
                       .withDetail("timeout_minutes", scheduleProperties.getTimeoutMinutes());
            } else {
                builder.down()
                       .withDetail("status", "Invalid configuration")
                       .withDetail("error", "Missing backup paths or bucket configuration")
                       .withDetail("enabled", true)
                       .withDetail("running", isRunning);
            }
            
            return builder.build();
            
        } catch (Exception e) {
            logger.error("Health check failed", e);
            return Health.down()
                   .withDetail("status", "Health check error")
                   .withDetail("error", e.getMessage())
                   .build();
        }
    }
}