package com.noahbackup.scheduler.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties for backup scheduling.
 * Reads configuration from application.yml under the 'noah.backup' prefix.
 */
@Component
@ConfigurationProperties(prefix = "noah.backup")
public class BackupScheduleProperties {
    
    /**
     * Whether backup scheduling is enabled globally.
     */
    private boolean enabled = true;
    
    /**
     * Whether weekly backups are enabled (in addition to daily).
     */
    private boolean weeklyEnabled = true;
    
    /**
     * List of file/directory paths to backup.
     */
    private List<String> paths = new ArrayList<>();
    
    /**
     * S3 bucket name for backups.
     */
    private String bucket = "noah-backup-default";
    
    /**
     * Timeout in minutes for backup operations.
     */
    private int timeoutMinutes = 60;
    
    /**
     * Schedule configuration for different backup types.
     */
    private Schedule schedule = new Schedule();
    
    // Getters and setters
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public boolean isWeeklyEnabled() { return weeklyEnabled; }
    public void setWeeklyEnabled(boolean weeklyEnabled) { this.weeklyEnabled = weeklyEnabled; }
    
    public List<String> getPaths() { return paths; }
    public void setPaths(List<String> paths) { this.paths = paths; }
    
    public String getBucket() { return bucket; }
    public void setBucket(String bucket) { this.bucket = bucket; }
    
    public int getTimeoutMinutes() { return timeoutMinutes; }
    public void setTimeoutMinutes(int timeoutMinutes) { this.timeoutMinutes = timeoutMinutes; }
    
    public Schedule getSchedule() { return schedule; }
    public void setSchedule(Schedule schedule) { this.schedule = schedule; }
    
    /**
     * Nested class for schedule configuration.
     */
    public static class Schedule {
        
        /**
         * Cron expression for daily backups.
         * Default: 2:00 AM daily
         */
        private String daily = "0 0 2 * * ?";
        
        /**
         * Cron expression for weekly backups.
         * Default: 1:00 AM every Sunday
         */
        private String weekly = "0 0 1 * * SUN";
        
        public String getDaily() { return daily; }
        public void setDaily(String daily) { this.daily = daily; }
        
        public String getWeekly() { return weekly; }
        public void setWeekly(String weekly) { this.weekly = weekly; }
    }
    
    /**
     * Validates the configuration.
     */
    public void validate() throws IllegalArgumentException {
        if (paths.isEmpty()) {
            throw new IllegalArgumentException("At least one backup path must be configured");
        }
        
        if (bucket == null || bucket.trim().isEmpty()) {
            throw new IllegalArgumentException("Backup bucket must be configured");
        }
        
        if (timeoutMinutes <= 0) {
            throw new IllegalArgumentException("Timeout minutes must be positive");
        }
    }
    
    @Override
    public String toString() {
        return String.format("BackupScheduleProperties{enabled=%s, weeklyEnabled=%s, paths=%s, bucket='%s', timeout=%d min}", 
                           enabled, weeklyEnabled, paths, bucket, timeoutMinutes);
    }
}