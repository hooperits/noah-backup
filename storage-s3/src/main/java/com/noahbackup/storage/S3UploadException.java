package com.noahbackup.storage;

/**
 * Custom exception for S3 upload operations.
 */
public class S3UploadException extends Exception {
    
    public S3UploadException(String message) {
        super(message);
    }
    
    public S3UploadException(String message, Throwable cause) {
        super(message, cause);
    }
}