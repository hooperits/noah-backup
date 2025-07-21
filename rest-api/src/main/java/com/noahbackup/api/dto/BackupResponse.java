package com.noahbackup.api.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for backup responses.
 */
public class BackupResponse {
    
    private boolean success;
    private String jobType;
    private int successCount;
    private int failureCount;
    private String message;
    private LocalDateTime timestamp;
    private String bucketUsed;
    private Long durationMs;
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final BackupResponse response = new BackupResponse();
        
        public Builder success(boolean success) { response.success = success; return this; }
        public Builder jobType(String jobType) { response.jobType = jobType; return this; }
        public Builder successCount(int successCount) { response.successCount = successCount; return this; }
        public Builder failureCount(int failureCount) { response.failureCount = failureCount; return this; }
        public Builder message(String message) { response.message = message; return this; }
        public Builder timestamp(LocalDateTime timestamp) { response.timestamp = timestamp; return this; }
        public Builder bucketUsed(String bucketUsed) { response.bucketUsed = bucketUsed; return this; }
        public Builder durationMs(Long durationMs) { response.durationMs = durationMs; return this; }
        
        public BackupResponse build() { return response; }
    }
    
    // Getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }
    
    public int getSuccessCount() { return successCount; }
    public void setSuccessCount(int successCount) { this.successCount = successCount; }
    
    public int getFailureCount() { return failureCount; }
    public void setFailureCount(int failureCount) { this.failureCount = failureCount; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getBucketUsed() { return bucketUsed; }
    public void setBucketUsed(String bucketUsed) { this.bucketUsed = bucketUsed; }
    
    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }
}