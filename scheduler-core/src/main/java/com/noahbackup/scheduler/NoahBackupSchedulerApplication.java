package com.noahbackup.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Spring Boot application for Noah Backup Scheduler.
 * Enables scheduled backup operations using Spring's @Scheduled annotation.
 */
@SpringBootApplication
@EnableScheduling
public class NoahBackupSchedulerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(NoahBackupSchedulerApplication.class, args);
    }
}