package com.noahbackup.api.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for status responses.
 */
public class StatusResponse {
    
    private LocalDateTime timestamp;
    private boolean enabled;
    private boolean running;
    private boolean weeklyEnabled;
    private int backupPathsCount;
    private String bucket;
    private int timeoutMinutes;
    private String dailySchedule;
    private String weeklySchedule;
    private String systemStatus;
    private String lastBackupTime;
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final StatusResponse response = new StatusResponse();
        
        public Builder timestamp(LocalDateTime timestamp) { response.timestamp = timestamp; return this; }
        public Builder enabled(boolean enabled) { response.enabled = enabled; return this; }
        public Builder running(boolean running) { response.running = running; return this; }
        public Builder weeklyEnabled(boolean weeklyEnabled) { response.weeklyEnabled = weeklyEnabled; return this; }
        public Builder backupPathsCount(int backupPathsCount) { response.backupPathsCount = backupPathsCount; return this; }
        public Builder bucket(String bucket) { response.bucket = bucket; return this; }
        public Builder timeoutMinutes(int timeoutMinutes) { response.timeoutMinutes = timeoutMinutes; return this; }
        public Builder dailySchedule(String dailySchedule) { response.dailySchedule = dailySchedule; return this; }
        public Builder weeklySchedule(String weeklySchedule) { response.weeklySchedule = weeklySchedule; return this; }
        public Builder systemStatus(String systemStatus) { response.systemStatus = systemStatus; return this; }
        public Builder lastBackupTime(String lastBackupTime) { response.lastBackupTime = lastBackupTime; return this; }
        
        public StatusResponse build() { return response; }
    }
    
    // Getters and setters
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public boolean isRunning() { return running; }
    public void setRunning(boolean running) { this.running = running; }
    
    public boolean isWeeklyEnabled() { return weeklyEnabled; }
    public void setWeeklyEnabled(boolean weeklyEnabled) { this.weeklyEnabled = weeklyEnabled; }
    
    public int getBackupPathsCount() { return backupPathsCount; }
    public void setBackupPathsCount(int backupPathsCount) { this.backupPathsCount = backupPathsCount; }
    
    public String getBucket() { return bucket; }
    public void setBucket(String bucket) { this.bucket = bucket; }
    
    public int getTimeoutMinutes() { return timeoutMinutes; }
    public void setTimeoutMinutes(int timeoutMinutes) { this.timeoutMinutes = timeoutMinutes; }
    
    public String getDailySchedule() { return dailySchedule; }
    public void setDailySchedule(String dailySchedule) { this.dailySchedule = dailySchedule; }
    
    public String getWeeklySchedule() { return weeklySchedule; }
    public void setWeeklySchedule(String weeklySchedule) { this.weeklySchedule = weeklySchedule; }
    
    public String getSystemStatus() { return systemStatus; }
    public void setSystemStatus(String systemStatus) { this.systemStatus = systemStatus; }
    
    public String getLastBackupTime() { return lastBackupTime; }
    public void setLastBackupTime(String lastBackupTime) { this.lastBackupTime = lastBackupTime; }
}