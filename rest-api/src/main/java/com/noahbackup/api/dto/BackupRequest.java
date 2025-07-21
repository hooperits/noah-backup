package com.noahbackup.api.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Data Transfer Object for backup requests.
 */
public class BackupRequest {
    
    @Size(max = 100, message = "Cannot specify more than 100 backup paths")
    private List<String> paths;
    
    @Pattern(regexp = "^[a-z0-9][a-z0-9\\-]*[a-z0-9]$", message = "Invalid S3 bucket name format")
    @Size(min = 3, max = 63, message = "S3 bucket name must be between 3 and 63 characters")
    private String bucketOverride;
    
    private boolean forceBackup = false;
    private Integer timeoutMinutes;
    
    // Constructors
    public BackupRequest() {}
    
    public BackupRequest(List<String> paths, String bucketOverride) {
        this.paths = paths;
        this.bucketOverride = bucketOverride;
    }
    
    // Getters and setters
    public List<String> getPaths() { return paths; }
    public void setPaths(List<String> paths) { this.paths = paths; }
    
    public String getBucketOverride() { return bucketOverride; }
    public void setBucketOverride(String bucketOverride) { this.bucketOverride = bucketOverride; }
    
    public boolean isForceBackup() { return forceBackup; }
    public void setForceBackup(boolean forceBackup) { this.forceBackup = forceBackup; }
    
    public Integer getTimeoutMinutes() { return timeoutMinutes; }
    public void setTimeoutMinutes(Integer timeoutMinutes) { this.timeoutMinutes = timeoutMinutes; }
    
    @Override
    public String toString() {
        return String.format("BackupRequest{paths=%s, bucket='%s', force=%s}", 
                           paths != null ? paths.size() + " paths" : "default", bucketOverride, forceBackup);
    }
}