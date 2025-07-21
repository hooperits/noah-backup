package com.noahbackup.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data transfer object for backup operation summaries.
 * 
 * Contains all information needed to track and report on backup operations:
 * - Timing information (start/end)
 * - File processing statistics 
 * - Error tracking and details
 * - Operation metadata
 */
public class BackupReportSummary {
    
    private String operationId;
    private String jobType;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime endTime;
    
    private long durationMillis;
    private boolean success;
    
    // File statistics
    private int totalFilesProcessed;
    private int successfulFiles;
    private int failedFiles;
    private long totalBytesProcessed;
    
    // Configuration at time of backup
    private String targetBucket;
    private List<String> backupPaths;
    
    // Error tracking
    private List<String> errorMessages;
    private List<String> failedFiles;
    
    // Additional metadata
    private String triggerType; // "SCHEDULED", "MANUAL", "API"
    private String triggerUser; // User who initiated (if manual/API)
    
    // Default constructor for Jackson
    public BackupReportSummary() {}
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private BackupReportSummary summary = new BackupReportSummary();
        
        public Builder operationId(String operationId) {
            summary.operationId = operationId;
            return this;
        }
        
        public Builder jobType(String jobType) {
            summary.jobType = jobType;
            return this;
        }
        
        public Builder startTime(LocalDateTime startTime) {
            summary.startTime = startTime;
            return this;
        }
        
        public Builder endTime(LocalDateTime endTime) {
            summary.endTime = endTime;
            if (summary.startTime != null) {
                summary.durationMillis = java.time.Duration.between(summary.startTime, endTime).toMillis();
            }
            return this;
        }
        
        public Builder success(boolean success) {
            summary.success = success;
            return this;
        }
        
        public Builder totalFilesProcessed(int totalFilesProcessed) {
            summary.totalFilesProcessed = totalFilesProcessed;
            return this;
        }
        
        public Builder successfulFiles(int successfulFiles) {
            summary.successfulFiles = successfulFiles;
            return this;
        }
        
        public Builder failedFiles(int failedFiles) {
            summary.failedFiles = failedFiles;
            return this;
        }
        
        public Builder totalBytesProcessed(long totalBytesProcessed) {
            summary.totalBytesProcessed = totalBytesProcessed;
            return this;
        }
        
        public Builder targetBucket(String targetBucket) {
            summary.targetBucket = targetBucket;
            return this;
        }
        
        public Builder backupPaths(List<String> backupPaths) {
            summary.backupPaths = backupPaths;
            return this;
        }
        
        public Builder errorMessages(List<String> errorMessages) {
            summary.errorMessages = errorMessages;
            return this;
        }
        
        public Builder failedFilesList(List<String> failedFiles) {
            summary.failedFiles = failedFiles;
            return this;
        }
        
        public Builder triggerType(String triggerType) {
            summary.triggerType = triggerType;
            return this;
        }
        
        public Builder triggerUser(String triggerUser) {
            summary.triggerUser = triggerUser;
            return this;
        }
        
        public BackupReportSummary build() {
            // Generate operation ID if not provided
            if (summary.operationId == null) {
                summary.operationId = java.util.UUID.randomUUID().toString();
            }
            return summary;
        }
    }
    
    // Getters and setters
    public String getOperationId() { return operationId; }
    public void setOperationId(String operationId) { this.operationId = operationId; }
    
    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public long getDurationMillis() { return durationMillis; }
    public void setDurationMillis(long durationMillis) { this.durationMillis = durationMillis; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public int getTotalFilesProcessed() { return totalFilesProcessed; }
    public void setTotalFilesProcessed(int totalFilesProcessed) { this.totalFilesProcessed = totalFilesProcessed; }
    
    public int getSuccessfulFiles() { return successfulFiles; }
    public void setSuccessfulFiles(int successfulFiles) { this.successfulFiles = successfulFiles; }
    
    public int getFailedFiles() { return failedFiles; }
    public void setFailedFiles(int failedFiles) { this.failedFiles = failedFiles; }
    
    public long getTotalBytesProcessed() { return totalBytesProcessed; }
    public void setTotalBytesProcessed(long totalBytesProcessed) { this.totalBytesProcessed = totalBytesProcessed; }
    
    public String getTargetBucket() { return targetBucket; }
    public void setTargetBucket(String targetBucket) { this.targetBucket = targetBucket; }
    
    public List<String> getBackupPaths() { return backupPaths; }
    public void setBackupPaths(List<String> backupPaths) { this.backupPaths = backupPaths; }
    
    public List<String> getErrorMessages() { return errorMessages; }
    public void setErrorMessages(List<String> errorMessages) { this.errorMessages = errorMessages; }
    
    public List<String> getFailedFilesList() { return failedFiles; }
    public void setFailedFilesList(List<String> failedFiles) { this.failedFiles = failedFiles; }
    
    public String getTriggerType() { return triggerType; }
    public void setTriggerType(String triggerType) { this.triggerType = triggerType; }
    
    public String getTriggerUser() { return triggerUser; }
    public void setTriggerUser(String triggerUser) { this.triggerUser = triggerUser; }
    
    // Utility methods
    public String getFormattedDuration() {
        long seconds = durationMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
        } else {
            return String.format("%02d:%02d", minutes, seconds % 60);
        }
    }
    
    public double getSuccessRate() {
        if (totalFilesProcessed == 0) return 0.0;
        return (double) successfulFiles / totalFilesProcessed * 100.0;
    }
    
    public String getFileSizeFormatted() {
        return formatBytes(totalBytesProcessed);
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    @Override
    public String toString() {
        return String.format("BackupReportSummary{id='%s', jobType='%s', success=%s, files=%d/%d, duration=%s}",
                operationId, jobType, success, successfulFiles, totalFilesProcessed, getFormattedDuration());
    }
}