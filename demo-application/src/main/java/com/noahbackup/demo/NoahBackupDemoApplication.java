package com.noahbackup.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Noah Backup Demo Application
 * 
 * This application demonstrates the complete Noah Backup workflow including:
 * - VSS snapshot creation and file copying
 * - S3-compatible storage uploads  
 * - Automated scheduling
 * - Web API for manual control
 * - Health monitoring
 * 
 * Usage:
 * 1. Set up your .env file with S3 credentials
 * 2. Configure backup paths in application.yml
 * 3. Run: java -jar noah-backup-demo.jar
 * 4. Access: http://localhost:8080/actuator/health
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {
    "com.noahbackup.demo",
    "com.noahbackup.scheduler", 
    "com.noahbackup.storage",
    "com.noahbackup.filesystem"
})
public class NoahBackupDemoApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(NoahBackupDemoApplication.class);
    
    public static void main(String[] args) {
        logger.info("Starting Noah Backup Demo Application...");
        
        // Print startup banner
        printStartupBanner();
        
        SpringApplication.run(NoahBackupDemoApplication.class, args);
        
        logger.info("Noah Backup Demo Application started successfully!");
        logger.info("Access the application at: http://localhost:8080");
        logger.info("Health check: http://localhost:8080/actuator/health");
        logger.info("Scheduled tasks: http://localhost:8080/actuator/scheduledtasks");
    }
    
    private static void printStartupBanner() {
        System.out.println("""
            ╔═══════════════════════════════════════════════════════════════╗
            ║                                                               ║
            ║  ███╗   ██╗ ██████╗  █████╗ ██╗  ██╗    ██████╗  █████╗ ██╗   ║
            ║  ████╗  ██║██╔═══██╗██╔══██╗██║  ██║    ██╔══██╗██╔══██╗██║   ║
            ║  ██╔██╗ ██║██║   ██║███████║███████║    ██████╔╝███████║██║   ║
            ║  ██║╚██╗██║██║   ██║██╔══██║██╔══██║    ██╔══██╗██╔══██║██║   ║
            ║  ██║ ╚████║╚██████╔╝██║  ██║██║  ██║    ██████╔╝██║  ██║██║   ║
            ║  ╚═╝  ╚═══╝ ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝    ╚═════╝ ╚═╝  ╚═╝╚═╝   ║
            ║                                                               ║
            ║              BACKUP - Enterprise Backup Solution             ║
            ║                                                               ║
            ║  Features:                                                    ║
            ║  • VSS Integration for locked files (Windows)                 ║
            ║  • S3-compatible storage (AWS S3, MinIO, Lightsail)          ║
            ║  • Automated scheduling with cron expressions                ║
            ║  • RESTful API for remote management                         ║
            ║  • Built-in monitoring and health checks                     ║
            ║                                                               ║
            ╚═══════════════════════════════════════════════════════════════╝
            """);
    }
}