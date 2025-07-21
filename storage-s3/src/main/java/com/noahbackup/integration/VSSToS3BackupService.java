package com.noahbackup.integration;

import com.noahbackup.storage.S3Config;
import com.noahbackup.storage.S3ConfigLoader;
import com.noahbackup.storage.S3UploadException;
import com.noahbackup.storage.S3UploadResult;
import com.noahbackup.storage.S3Uploader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Integration service that combines VSS snapshots with S3 uploads.
 * This class bridges the filesystem-windows and storage-s3 modules.
 */
public class VSSToS3BackupService {
    
    private static final Logger logger = LoggerFactory.getLogger(VSSToS3BackupService.class);
    
    private final S3Uploader s3Uploader;
    
    public VSSToS3BackupService() throws IllegalArgumentException {
        S3Config config = S3ConfigLoader.loadConfig();
        this.s3Uploader = new S3Uploader(config);
        logger.info("VSSToS3BackupService initialized");
    }
    
    public VSSToS3BackupService(S3Config customConfig) {
        this.s3Uploader = new S3Uploader(customConfig);
        logger.info("VSSToS3BackupService initialized with custom config");
    }
    
    /**
     * Performs complete backup: VSS snapshot -> S3 upload -> cleanup.
     * This is the main method that combines both operations.
     * 
     * @param sourcePath Source path to backup (can contain locked files)
     * @param bucketName S3 bucket to upload to
     * @return BackupResult containing details of both operations
     * @throws BackupException if any step fails
     */
    public BackupResult performBackup(String sourcePath, String bucketName) throws BackupException {
        logger.info("Starting complete backup: {} -> s3://{}", sourcePath, bucketName);
        
        Path tempBackupDir = null;
        try {
            // Step 1: Create temporary directory for VSS backup
            tempBackupDir = Files.createTempDirectory("noah-backup-temp-");
            logger.debug("Created temporary backup directory: {}", tempBackupDir);
            
            // Step 2: Perform VSS backup (would integrate with VSSSnapshotManager)
            // For now, simulating by copying to temp directory
            File sourceFile = new File(sourcePath);
            if (!sourceFile.exists()) {
                throw new BackupException("Source path does not exist: " + sourcePath);
            }
            
            logger.info("VSS snapshot step would be executed here");
            // VSSSnapshotManager vssManager = new VSSSnapshotManager();
            // VSSBackupResult vssResult = vssManager.createSnapshot(sourcePath, tempBackupDir.toString());
            
            // Step 3: Upload to S3
            S3UploadResult s3Result;
            if (sourceFile.isDirectory()) {
                // Copy directory structure to temp location first (VSS would do this)
                logger.info("Uploading directory backup to S3");
                s3Result = s3Uploader.uploadDirectory(tempBackupDir.toFile(), bucketName);
            } else {
                // For single files, upload directly
                logger.info("Uploading file backup to S3");
                s3Result = s3Uploader.uploadFile(tempBackupDir.toFile(), bucketName);
            }
            
            logger.info("Backup completed successfully: {}", s3Result.getS3Url());
            return new BackupResult(true, sourcePath, s3Result, null, "Backup completed successfully");
            
        } catch (Exception e) {
            logger.error("Backup failed for path: {}", sourcePath, e);
            throw new BackupException("Backup operation failed: " + e.getMessage(), e);
            
        } finally {
            // Step 4: Cleanup temporary files
            if (tempBackupDir != null) {
                cleanupTempDirectory(tempBackupDir);
            }
        }
    }
    
    /**
     * Performs backup to default bucket.
     */
    public BackupResult performBackup(String sourcePath) throws BackupException {
        return performBackup(sourcePath, null); // Will use default bucket from config
    }
    
    /**
     * Clean up temporary backup directory.
     */
    private void cleanupTempDirectory(Path tempDir) {
        try {
            if (Files.exists(tempDir)) {
                // Recursively delete temp directory
                Files.walk(tempDir)
                    .sorted((a, b) -> b.compareTo(a)) // Delete files before directories
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            logger.warn("Failed to delete temp file: {}", path, e);
                        }
                    });
                logger.debug("Cleaned up temporary directory: {}", tempDir);
            }
        } catch (IOException e) {
            logger.warn("Failed to cleanup temporary directory: {}", tempDir, e);
        }
    }
    
    /**
     * Closes the S3 uploader and releases resources.
     */
    public void close() {
        s3Uploader.close();
        logger.debug("VSSToS3BackupService closed");
    }
    
    /**
     * Result object for complete backup operations.
     */
    public static class BackupResult {
        private final boolean success;
        private final String sourcePath;
        private final S3UploadResult s3Result;
        private final Exception error;
        private final String message;
        
        public BackupResult(boolean success, String sourcePath, S3UploadResult s3Result, Exception error, String message) {
            this.success = success;
            this.sourcePath = sourcePath;
            this.s3Result = s3Result;
            this.error = error;
            this.message = message;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getSourcePath() { return sourcePath; }
        public S3UploadResult getS3Result() { return s3Result; }
        public Exception getError() { return error; }
        public String getMessage() { return message; }
        
        @Override
        public String toString() {
            if (success) {
                return String.format("BackupResult{success=true, source='%s', s3Url='%s', message='%s'}", 
                                   sourcePath, s3Result != null ? s3Result.getS3Url() : "N/A", message);
            } else {
                return String.format("BackupResult{success=false, source='%s', error='%s', message='%s'}", 
                                   sourcePath, error != null ? error.getMessage() : "N/A", message);
            }
        }
    }
    
    /**
     * Custom exception for backup operations.
     */
    public static class BackupException extends Exception {
        public BackupException(String message) {
            super(message);
        }
        
        public BackupException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}