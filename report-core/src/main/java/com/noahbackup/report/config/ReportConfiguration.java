package com.noahbackup.report.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for the Noah Backup reporting system.
 * 
 * Centralizes all report-related configuration including:
 * - Log file locations and retention policies
 * - Notification settings and channel configuration
 * - Report generation schedules
 * - Integration endpoints
 */
@Configuration
@ConfigurationProperties(prefix = "noah.backup.reports")
public class ReportConfiguration {
    
    private String directory = "logs";
    private int retentionDays = 90;
    private boolean dailyEnabled = true;
    private boolean weeklyEnabled = true;
    private boolean cleanupEnabled = true;
    
    // Getters and setters
    public String getDirectory() {
        return directory;
    }
    
    public void setDirectory(String directory) {
        this.directory = directory;
    }
    
    public int getRetentionDays() {
        return retentionDays;
    }
    
    public void setRetentionDays(int retentionDays) {
        this.retentionDays = retentionDays;
    }
    
    public boolean isDailyEnabled() {
        return dailyEnabled;
    }
    
    public void setDailyEnabled(boolean dailyEnabled) {
        this.dailyEnabled = dailyEnabled;
    }
    
    public boolean isWeeklyEnabled() {
        return weeklyEnabled;
    }
    
    public void setWeeklyEnabled(boolean weeklyEnabled) {
        this.weeklyEnabled = weeklyEnabled;
    }
    
    public boolean isCleanupEnabled() {
        return cleanupEnabled;
    }
    
    public void setCleanupEnabled(boolean cleanupEnabled) {
        this.cleanupEnabled = cleanupEnabled;
    }
}