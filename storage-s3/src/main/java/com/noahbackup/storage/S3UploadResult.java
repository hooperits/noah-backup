package com.noahbackup.storage;

import java.util.List;

/**
 * Result object containing details about S3 upload operations.
 */
public class S3UploadResult {
    
    private final boolean success;
    private final String bucketName;
    private final String s3Key;
    private final long fileSize;
    private final int fileCount;
    private final String message;
    private final String eTag;
    private final List<S3UploadResult> childResults;
    
    // Constructor for single file uploads
    public S3UploadResult(boolean success, String bucketName, String s3Key, long fileSize, int fileCount, String message, String eTag) {
        this.success = success;
        this.bucketName = bucketName;
        this.s3Key = s3Key;
        this.fileSize = fileSize;
        this.fileCount = fileCount;
        this.message = message;
        this.eTag = eTag;
        this.childResults = null;
    }
    
    // Constructor for directory uploads with child results
    public S3UploadResult(boolean success, String bucketName, String s3Key, long totalSize, int fileCount, String message, List<S3UploadResult> childResults) {
        this.success = success;
        this.bucketName = bucketName;
        this.s3Key = s3Key;
        this.fileSize = totalSize;
        this.fileCount = fileCount;
        this.message = message;
        this.eTag = null;
        this.childResults = childResults;
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getBucketName() { return bucketName; }
    public String getS3Key() { return s3Key; }
    public long getFileSize() { return fileSize; }
    public int getFileCount() { return fileCount; }
    public String getMessage() { return message; }
    public String getETag() { return eTag; }
    public List<S3UploadResult> getChildResults() { return childResults; }
    
    /**
     * Returns formatted file size in human-readable format.
     */
    public String getFormattedFileSize() {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.2f KB", fileSize / 1024.0);
        } else if (fileSize < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", fileSize / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", fileSize / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    /**
     * Returns the S3 URL for the uploaded object.
     */
    public String getS3Url() {
        return String.format("s3://%s/%s", bucketName, s3Key);
    }
    
    @Override
    public String toString() {
        if (childResults != null) {
            return String.format("S3UploadResult{success=%s, bucket='%s', prefix='%s', files=%d, totalSize='%s', message='%s'}", 
                               success, bucketName, s3Key, fileCount, getFormattedFileSize(), message);
        } else {
            return String.format("S3UploadResult{success=%s, bucket='%s', key='%s', size='%s', eTag='%s', message='%s'}", 
                               success, bucketName, s3Key, getFormattedFileSize(), eTag, message);
        }
    }
}