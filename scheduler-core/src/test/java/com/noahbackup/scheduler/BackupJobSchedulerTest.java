package com.noahbackup.scheduler;

import com.noahbackup.scheduler.config.BackupScheduleProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for BackupJobScheduler functionality.
 * Note: These are unit tests that don't require actual backup execution.
 */
class BackupJobSchedulerTest {
    
    private BackupScheduleProperties testProperties;
    
    @BeforeEach
    void setUp() {
        testProperties = new BackupScheduleProperties();
        testProperties.setEnabled(true);
        testProperties.setBucket("test-bucket");
        testProperties.setPaths(Arrays.asList("C:\\test\\path1", "C:\\test\\path2"));
    }
    
    @Test
    void testBackupSchedulePropertiesValidation() {
        // Valid config should not throw
        assertDoesNotThrow(() -> testProperties.validate());
        
        // Empty paths should throw
        BackupScheduleProperties invalidProps1 = new BackupScheduleProperties();
        invalidProps1.setBucket("bucket");
        assertThrows(IllegalArgumentException.class, () -> invalidProps1.validate());
        
        // Empty bucket should throw
        BackupScheduleProperties invalidProps2 = new BackupScheduleProperties();
        invalidProps2.setPaths(Arrays.asList("path"));
        invalidProps2.setBucket("");
        assertThrows(IllegalArgumentException.class, () -> invalidProps2.validate());
        
        // Invalid timeout should throw
        BackupScheduleProperties invalidProps3 = new BackupScheduleProperties();
        invalidProps3.setPaths(Arrays.asList("path"));
        invalidProps3.setBucket("bucket");
        invalidProps3.setTimeoutMinutes(-1);
        assertThrows(IllegalArgumentException.class, () -> invalidProps3.validate());
    }
    
    @Test
    void testBackupSchedulePropertiesToString() {
        String propsString = testProperties.toString();
        assertTrue(propsString.contains("enabled=true"));
        assertTrue(propsString.contains("test-bucket"));
        assertTrue(propsString.contains("[C:\\test\\path1, C:\\test\\path2]"));
    }
    
    @Test
    void testScheduleConfiguration() {
        BackupScheduleProperties.Schedule schedule = testProperties.getSchedule();
        assertNotNull(schedule);
        assertEquals("0 0 2 * * ?", schedule.getDaily()); // Default daily schedule
        assertEquals("0 0 1 * * SUN", schedule.getWeekly()); // Default weekly schedule
        
        // Test custom schedule
        schedule.setDaily("0 0 3 * * ?");
        schedule.setWeekly("0 0 2 * * MON");
        assertEquals("0 0 3 * * ?", schedule.getDaily());
        assertEquals("0 0 2 * * MON", schedule.getWeekly());
    }
    
    @Test
    void testBackupJobResult() {
        BackupJobScheduler.BackupJobResult result = new BackupJobScheduler.BackupJobResult(
            true, "Test Job", 3, 1, "Test completed: 3 succeeded, 1 failed"
        );
        
        assertTrue(result.isSuccess());
        assertEquals("Test Job", result.getJobType());
        assertEquals(3, result.getSuccessCount());
        assertEquals(1, result.getFailureCount());
        assertEquals("Test completed: 3 succeeded, 1 failed", result.getMessage());
        
        String resultString = result.toString();
        assertTrue(resultString.contains("success=true"));
        assertTrue(resultString.contains("Test Job"));
        assertTrue(resultString.contains("success=3"));
        assertTrue(resultString.contains("failures=1"));
    }
    
    @Test
    void testDefaultConfiguration() {
        BackupScheduleProperties defaultProps = new BackupScheduleProperties();
        
        assertTrue(defaultProps.isEnabled());
        assertTrue(defaultProps.isWeeklyEnabled());
        assertEquals("noah-backup-default", defaultProps.getBucket());
        assertEquals(60, defaultProps.getTimeoutMinutes());
        assertTrue(defaultProps.getPaths().isEmpty());
        
        BackupScheduleProperties.Schedule schedule = defaultProps.getSchedule();
        assertEquals("0 0 2 * * ?", schedule.getDaily());
        assertEquals("0 0 1 * * SUN", schedule.getWeekly());
    }
}