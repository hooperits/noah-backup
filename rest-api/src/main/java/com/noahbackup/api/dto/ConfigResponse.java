package com.noahbackup.api.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for configuration responses.
 */
public class ConfigResponse {
    
    private LocalDateTime timestamp;
    private boolean enabled;
    private boolean weeklyEnabled;
    private List<String> backupPaths;
    private String bucket;
    private int timeoutMinutes;
    private String dailySchedule;
    private String weeklySchedule;
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final ConfigResponse response = new ConfigResponse();
        
        public Builder timestamp(LocalDateTime timestamp) { response.timestamp = timestamp; return this; }
        public Builder enabled(boolean enabled) { response.enabled = enabled; return this; }
        public Builder weeklyEnabled(boolean weeklyEnabled) { response.weeklyEnabled = weeklyEnabled; return this; }
        public Builder backupPaths(List<String> backupPaths) { response.backupPaths = backupPaths; return this; }
        public Builder bucket(String bucket) { response.bucket = bucket; return this; }
        public Builder timeoutMinutes(int timeoutMinutes) { response.timeoutMinutes = timeoutMinutes; return this; }
        public Builder dailySchedule(String dailySchedule) { response.dailySchedule = dailySchedule; return this; }
        public Builder weeklySchedule(String weeklySchedule) { response.weeklySchedule = weeklySchedule; return this; }
        
        public ConfigResponse build() { return response; }
    }
    
    // Getters and setters
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public boolean isWeeklyEnabled() { return weeklyEnabled; }
    public void setWeeklyEnabled(boolean weeklyEnabled) { this.weeklyEnabled = weeklyEnabled; }
    
    public List<String> getBackupPaths() { return backupPaths; }
    public void setBackupPaths(List<String> backupPaths) { this.backupPaths = backupPaths; }
    
    public String getBucket() { return bucket; }
    public void setBucket(String bucket) { this.bucket = bucket; }
    
    public int getTimeoutMinutes() { return timeoutMinutes; }
    public void setTimeoutMinutes(int timeoutMinutes) { this.timeoutMinutes = timeoutMinutes; }
    
    public String getDailySchedule() { return dailySchedule; }
    public void setDailySchedule(String dailySchedule) { this.dailySchedule = dailySchedule; }
    
    public String getWeeklySchedule() { return weeklySchedule; }
    public void setWeeklySchedule(String weeklySchedule) { this.weeklySchedule = weeklySchedule; }
}