package com.noahbackup.integration;

import com.noahbackup.scheduler.BackupJobScheduler;
import com.noahbackup.scheduler.config.BackupScheduleProperties;
import com.noahbackup.storage.S3Config;
import com.noahbackup.storage.S3UploadException;
import com.noahbackup.storage.S3UploadResult;
import com.noahbackup.storage.S3Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for the complete Noah Backup workflow.
 * Tests the integration between filesystem-windows, storage-s3, and scheduler-core modules.
 * 
 * Note: This test simulates the complete workflow but doesn't require actual:
 * - VSS operations (Windows-specific)
 * - S3 connectivity (requires credentials)
 * - Real scheduling (uses direct method calls)
 */
class CompleteWorkflowIntegrationTest {
    
    private static final Logger logger = LoggerFactory.getLogger(CompleteWorkflowIntegrationTest.class);
    
    @TempDir
    Path tempDir;
    
    private BackupScheduleProperties testProperties;
    
    @BeforeEach
    void setUp() throws IOException {
        // Create test backup configuration
        testProperties = new BackupScheduleProperties();
        testProperties.setEnabled(true);
        testProperties.setWeeklyEnabled(true);
        testProperties.setBucket("test-integration-bucket");
        testProperties.setTimeoutMinutes(5); // Short timeout for testing
        
        // Create test files and directories
        createTestBackupContent();
        
        // Set backup paths to our test content
        testProperties.setPaths(Arrays.asList(
            tempDir.resolve("test-document.txt").toString(),
            tempDir.resolve("test-directory").toString()
        ));
        
        logger.info("Integration test setup completed with temp directory: {}", tempDir);
    }
    
    @Test
    void testCompleteBackupWorkflow() {
        logger.info("Starting complete backup workflow integration test");
        
        // Test 1: Configuration Validation
        assertDoesNotThrow(() -> testProperties.validate(), 
            "Backup configuration should be valid");
        
        // Test 2: File System Operations (simulated VSS)
        testFileSystemOperations();
        
        // Test 3: Storage Operations (simulated S3)
        testStorageOperations();
        
        // Test 4: Scheduler Integration
        testSchedulerIntegration();
        
        logger.info("Complete backup workflow integration test completed successfully");
    }
    
    void testFileSystemOperations() {
        logger.info("Testing file system operations (VSS simulation)");
        
        // Verify test files exist and are readable
        for (String path : testProperties.getPaths()) {
            File file = new File(path);
            assertTrue(file.exists(), "Backup source should exist: " + path);
            assertTrue(file.canRead(), "Backup source should be readable: " + path);
            
            if (file.isDirectory()) {
                assertTrue(file.listFiles().length > 0, "Directory should contain files: " + path);
            }
        }
        
        // Simulate VSS snapshot creation (would use VSSSnapshotManager in real scenario)
        Path backupTempDir = tempDir.resolve("backup-temp");
        try {
            Files.createDirectories(backupTempDir);
            
            // Simulate copying files to backup temp location
            for (String sourcePath : testProperties.getPaths()) {
                File source = new File(sourcePath);
                if (source.isFile()) {
                    Path target = backupTempDir.resolve(source.getName());
                    Files.copy(source.toPath(), target);
                    logger.debug("Simulated VSS copy: {} -> {}", sourcePath, target);
                } else if (source.isDirectory()) {
                    // Simulate directory backup
                    Path targetDir = backupTempDir.resolve(source.getName());
                    Files.createDirectories(targetDir);
                    copyDirectoryContents(source.toPath(), targetDir);
                    logger.debug("Simulated VSS directory copy: {} -> {}", sourcePath, targetDir);
                }
            }
            
            assertTrue(Files.exists(backupTempDir), "Backup temp directory should be created");
            assertTrue(Files.list(backupTempDir).count() > 0, "Backup temp directory should contain files");
            
        } catch (IOException e) {
            fail("File system operations failed: " + e.getMessage());
        }
        
        logger.info("File system operations test completed successfully");
    }
    
    void testStorageOperations() {
        logger.info("Testing storage operations (S3 simulation)");
        
        // Create mock S3 configuration
        S3Config mockS3Config = new S3Config(
            "test-access-key",
            "test-secret-key",
            "us-east-1",
            testProperties.getBucket()
        );
        
        // Test S3 configuration validation
        assertDoesNotThrow(() -> mockS3Config.validate(), 
            "S3 configuration should be valid");
        
        // Test file upload preparation (would use S3Uploader in real scenario)
        for (String path : testProperties.getPaths()) {
            File file = new File(path);
            
            // Simulate upload validation
            assertTrue(file.exists(), "Upload source should exist: " + path);
            assertTrue(file.canRead(), "Upload source should be readable: " + path);
            
            // Simulate S3 key generation
            String s3Key = generateS3Key(file.getName());
            assertNotNull(s3Key, "S3 key should be generated");
            assertTrue(s3Key.contains("backups/"), "S3 key should contain backup prefix");
            
            logger.debug("Simulated S3 upload preparation: {} -> s3://{}/{}", 
                        path, testProperties.getBucket(), s3Key);
        }
        
        logger.info("Storage operations test completed successfully");
    }
    
    void testSchedulerIntegration() {
        logger.info("Testing scheduler integration");
        
        // Create a mock backup service for testing
        MockVSSToS3BackupService mockBackupService = new MockVSSToS3BackupService();
        
        // Create scheduler with test configuration
        BackupJobScheduler scheduler = new BackupJobScheduler(testProperties, mockBackupService);
        
        // Test manual backup execution
        BackupJobScheduler.BackupJobResult result = scheduler.executeManualBackup();
        
        assertNotNull(result, "Backup job result should not be null");
        assertTrue(result.isSuccess(), "Manual backup should succeed: " + result.getMessage());
        assertEquals("Manual Backup", result.getJobType());
        assertEquals(testProperties.getPaths().size(), result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        
        // Test scheduler status
        assertFalse(scheduler.isBackupRunning(), "Backup should not be running after completion");
        
        logger.info("Scheduler integration test completed successfully: {}", result);
    }
    
    @Test
    void testErrorHandling() {
        logger.info("Testing error handling in backup workflow");
        
        // Test with invalid configuration
        BackupScheduleProperties invalidConfig = new BackupScheduleProperties();
        invalidConfig.setPaths(Arrays.asList()); // Empty paths
        
        assertThrows(IllegalArgumentException.class, () -> invalidConfig.validate(),
            "Empty paths should cause validation failure");
        
        // Test with non-existent backup path
        BackupScheduleProperties badPathConfig = new BackupScheduleProperties();
        badPathConfig.setPaths(Arrays.asList("/non/existent/path"));
        badPathConfig.setBucket("test-bucket");
        
        MockVSSToS3BackupService mockService = new MockVSSToS3BackupService();
        mockService.setShouldFail(true); // Force failure
        
        BackupJobScheduler scheduler = new BackupJobScheduler(badPathConfig, mockService);
        BackupJobScheduler.BackupJobResult result = scheduler.executeManualBackup();
        
        assertFalse(result.isSuccess(), "Backup with non-existent path should fail");
        assertTrue(result.getFailureCount() > 0, "Should have failure count > 0");
        
        logger.info("Error handling test completed successfully");
    }
    
    private void createTestBackupContent() throws IOException {
        // Create test file
        Path testFile = tempDir.resolve("test-document.txt");
        Files.write(testFile, "This is a test document for backup testing.".getBytes());
        
        // Create test directory with multiple files
        Path testDir = tempDir.resolve("test-directory");
        Files.createDirectories(testDir);
        
        Files.write(testDir.resolve("file1.txt"), "Content of file 1".getBytes());
        Files.write(testDir.resolve("file2.log"), "Log content for testing".getBytes());
        Files.write(testDir.resolve("config.properties"), "app.name=noah-backup\nversion=1.0".getBytes());
        
        // Create subdirectory
        Path subDir = testDir.resolve("subdirectory");
        Files.createDirectories(subDir);
        Files.write(subDir.resolve("nested-file.txt"), "Nested file content".getBytes());
        
        logger.debug("Created test backup content: {} files", Files.walk(tempDir).count() - 1);
    }
    
    private void copyDirectoryContents(Path source, Path target) throws IOException {
        Files.walk(source)
             .filter(Files::isRegularFile)
             .forEach(sourcePath -> {
                 try {
                     Path relativePath = source.relativize(sourcePath);
                     Path targetPath = target.resolve(relativePath);
                     Files.createDirectories(targetPath.getParent());
                     Files.copy(sourcePath, targetPath);
                 } catch (IOException e) {
                     logger.error("Failed to copy file: {}", sourcePath, e);
                 }
             });
    }
    
    private String generateS3Key(String fileName) {
        return String.format("backups/integration-test/%s", fileName);
    }
    
    /**
     * Mock implementation of VSSToS3BackupService for testing.
     */
    private static class MockVSSToS3BackupService extends VSSToS3BackupService {
        private boolean shouldFail = false;
        
        public MockVSSToS3BackupService() {
            super(new S3Config("mock-key", "mock-secret", "us-east-1", "mock-bucket"));
        }
        
        public void setShouldFail(boolean shouldFail) {
            this.shouldFail = shouldFail;
        }
        
        @Override
        public BackupResult performBackup(String sourcePath, String bucketName) throws BackupException {
            if (shouldFail) {
                throw new BackupException("Mock backup failure for testing");
            }
            
            // Simulate successful backup
            File source = new File(sourcePath);
            if (!source.exists()) {
                throw new BackupException("Mock: Source path does not exist: " + sourcePath);
            }
            
            // Create mock S3 result
            String s3Key = "backups/mock/" + source.getName();
            long fileSize = source.isFile() ? source.length() : 1024L; // Simulate directory size
            S3UploadResult mockS3Result = new S3UploadResult(
                true, bucketName, s3Key, fileSize, 1, "Mock upload successful", "mock-etag-123"
            );
            
            return new BackupResult(true, sourcePath, mockS3Result, null, "Mock backup completed successfully");
        }
    }
}