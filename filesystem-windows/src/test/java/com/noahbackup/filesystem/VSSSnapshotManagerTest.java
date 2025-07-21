package com.noahbackup.filesystem;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for VSSSnapshotManager.
 * Note: Full VSS tests require Administrator privileges and Windows OS.
 */
class VSSSnapshotManagerTest {
    
    private VSSSnapshotManager vssManager;
    private Path tempDir;
    
    @BeforeEach
    void setUp() throws IOException {
        vssManager = new VSSSnapshotManager();
        tempDir = Files.createTempDirectory("vss-test");
    }
    
    @Test
    void testValidateParameters_NullSource() {
        VSSSnapshotManager.VSSException exception = assertThrows(
            VSSSnapshotManager.VSSException.class,
            () -> vssManager.createSnapshot(null, tempDir.toString())
        );
        assertTrue(exception.getMessage().contains("Source path cannot be null"));
    }
    
    @Test
    void testValidateParameters_EmptySource() {
        VSSSnapshotManager.VSSException exception = assertThrows(
            VSSSnapshotManager.VSSException.class,
            () -> vssManager.createSnapshot("", tempDir.toString())
        );
        assertTrue(exception.getMessage().contains("Source path cannot be null"));
    }
    
    @Test
    void testValidateParameters_NullDestination() {
        VSSSnapshotManager.VSSException exception = assertThrows(
            VSSSnapshotManager.VSSException.class,
            () -> vssManager.createSnapshot(tempDir.toString(), null)
        );
        assertTrue(exception.getMessage().contains("Destination path cannot be null"));
    }
    
    @Test
    void testValidateParameters_NonExistentSource() {
        String nonExistentPath = Paths.get(tempDir.toString(), "does-not-exist").toString();
        VSSSnapshotManager.VSSException exception = assertThrows(
            VSSSnapshotManager.VSSException.class,
            () -> vssManager.createSnapshot(nonExistentPath, tempDir.toString())
        );
        assertTrue(exception.getMessage().contains("Source path does not exist"));
    }
    
    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testWindowsOSDetection() throws IOException {
        // Create a test file
        Path testFile = Files.createTempFile(tempDir, "test", ".txt");
        Files.write(testFile, "test content".getBytes());
        
        // This should not throw an OS-related exception on Windows
        // (though it might fail due to permissions)
        assertDoesNotThrow(() -> {
            try {
                vssManager.createSnapshot(testFile.toString(), tempDir.toString());
            } catch (VSSSnapshotManager.VSSException e) {
                // Only re-throw if it's an OS validation error
                if (e.getMessage().contains("only supported on Windows")) {
                    throw e;
                }
                // Other errors (like permission issues) are expected in test environment
            }
        });
    }
    
    @Test
    void testVSSBackupResult() {
        VSSSnapshotManager.VSSBackupResult result = new VSSSnapshotManager.VSSBackupResult(
            true, "Success output", 0
        );
        
        assertTrue(result.isSuccess());
        assertEquals("Success output", result.getOutput());
        assertEquals(0, result.getExitCode());
        
        String resultString = result.toString();
        assertTrue(resultString.contains("success=true"));
        assertTrue(resultString.contains("exitCode=0"));
        assertTrue(resultString.contains("Success output"));
    }
    
    @Test
    void testVSSException() {
        String message = "Test exception message";
        VSSSnapshotManager.VSSException exception = new VSSSnapshotManager.VSSException(message);
        assertEquals(message, exception.getMessage());
        
        Exception cause = new RuntimeException("Cause");
        VSSSnapshotManager.VSSException exceptionWithCause = new VSSSnapshotManager.VSSException(message, cause);
        assertEquals(message, exceptionWithCause.getMessage());
        assertEquals(cause, exceptionWithCause.getCause());
    }
}