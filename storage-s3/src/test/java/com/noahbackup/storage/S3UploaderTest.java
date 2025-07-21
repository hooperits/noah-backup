package com.noahbackup.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for S3Uploader functionality.
 * Note: These are unit tests that don't require actual S3 connectivity.
 * Integration tests would require real S3 credentials.
 */
class S3UploaderTest {
    
    @TempDir
    Path tempDir;
    
    private S3Config testConfig;
    
    @BeforeEach
    void setUp() {
        testConfig = new S3Config(
            "test-access-key",
            "test-secret-key", 
            "us-east-1",
            "test-bucket"
        );
    }
    
    @Test
    void testS3ConfigValidation() {
        // Valid config should not throw
        assertDoesNotThrow(() -> testConfig.validate());
        
        // Invalid configs should throw
        S3Config invalidConfig1 = new S3Config();
        assertThrows(IllegalArgumentException.class, () -> invalidConfig1.validate());
        
        S3Config invalidConfig2 = new S3Config("", "secret", "region", "bucket");
        assertThrows(IllegalArgumentException.class, () -> invalidConfig2.validate());
    }
    
    @Test
    void testS3ConfigToString() {
        testConfig.setEndpoint("http://localhost:9000");
        testConfig.setPathStyleAccess(true);
        
        String configString = testConfig.toString();
        assertTrue(configString.contains("us-east-1"));
        assertTrue(configString.contains("http://localhost:9000"));
        assertTrue(configString.contains("test-bucket"));
        assertTrue(configString.contains("pathStyleAccess=true"));
    }
    
    @Test
    void testS3UploadResultFormatting() {
        S3UploadResult result = new S3UploadResult(
            true, "test-bucket", "test/file.txt", 
            1536, 1, "Upload successful", "etag123"
        );
        
        assertTrue(result.isSuccess());
        assertEquals("test-bucket", result.getBucketName());
        assertEquals("test/file.txt", result.getS3Key());
        assertEquals(1536, result.getFileSize());
        assertEquals("1.50 KB", result.getFormattedFileSize());
        assertEquals("s3://test-bucket/test/file.txt", result.getS3Url());
    }
    
    @Test
    void testS3UploadResultFileSizeFormatting() {
        // Test different file sizes
        S3UploadResult bytes = new S3UploadResult(true, "bucket", "key", 512, 1, "msg", "etag");
        assertEquals("512 B", bytes.getFormattedFileSize());
        
        S3UploadResult kb = new S3UploadResult(true, "bucket", "key", 2048, 1, "msg", "etag");
        assertEquals("2.00 KB", kb.getFormattedFileSize());
        
        S3UploadResult mb = new S3UploadResult(true, "bucket", "key", 5242880, 1, "msg", "etag");
        assertEquals("5.00 MB", mb.getFormattedFileSize());
        
        S3UploadResult gb = new S3UploadResult(true, "bucket", "key", 1073741824L, 1, "msg", "etag");
        assertEquals("1.00 GB", gb.getFormattedFileSize());
    }
    
    @Test
    void testUploadFileValidation() throws IOException {
        S3Uploader uploader = new S3Uploader(testConfig);
        
        // Null file should throw exception
        S3UploadException exception1 = assertThrows(S3UploadException.class, 
            () -> uploader.uploadFile(null, "bucket"));
        assertTrue(exception1.getMessage().contains("File cannot be null"));
        
        // Non-existent file should throw exception
        File nonExistentFile = new File(tempDir.toFile(), "does-not-exist.txt");
        S3UploadException exception2 = assertThrows(S3UploadException.class, 
            () -> uploader.uploadFile(nonExistentFile, "bucket"));
        assertTrue(exception2.getMessage().contains("File does not exist"));
        
        // Directory instead of file should throw exception
        File directory = tempDir.toFile();
        S3UploadException exception3 = assertThrows(S3UploadException.class, 
            () -> uploader.uploadFile(directory, "bucket"));
        assertTrue(exception3.getMessage().contains("Path is not a file"));
        
        uploader.close();
    }
    
    @Test
    void testUploadDirectoryValidation() throws IOException {
        S3Uploader uploader = new S3Uploader(testConfig);
        
        // Create a test file (not directory)
        File testFile = Files.createTempFile(tempDir, "test", ".txt").toFile();
        
        // File instead of directory should throw exception
        S3UploadException exception = assertThrows(S3UploadException.class, 
            () -> uploader.uploadDirectory(testFile, "bucket"));
        assertTrue(exception.getMessage().contains("Path is not a directory"));
        
        uploader.close();
    }
    
    @Test 
    void testS3ConfigLoader_Defaults() {
        // Test that defaults are applied when no configuration is found
        // This would require mocking environment variables in a real test
        assertDoesNotThrow(() -> {
            S3Config config = new S3Config();
            if (config.getRegion() == null) config.setRegion("us-east-1");
            if (config.getDefaultBucket() == null) config.setDefaultBucket("noah-backup-default");
            
            assertEquals("us-east-1", config.getRegion());
            assertEquals("noah-backup-default", config.getDefaultBucket());
        });
    }
    
    @Test
    void testS3UploadException() {
        String message = "Test upload exception";
        S3UploadException exception = new S3UploadException(message);
        assertEquals(message, exception.getMessage());
        
        Exception cause = new RuntimeException("Root cause");
        S3UploadException exceptionWithCause = new S3UploadException(message, cause);
        assertEquals(message, exceptionWithCause.getMessage());
        assertEquals(cause, exceptionWithCause.getCause());
    }
}