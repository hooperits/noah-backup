package com.noahbackup.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Noah Backup REST API Application
 * 
 * Provides secure HTTP API endpoints for:
 * - Manual backup operations
 * - System status monitoring  
 * - Configuration management
 * - Schedule updates
 * - Health checks and metrics
 * 
 * Security Features:
 * - JWT-based authentication
 * - Role-based access control
 * - Rate limiting and request validation
 * - Audit logging
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {
    "com.noahbackup.api",
    "com.noahbackup.scheduler",
    "com.noahbackup.auth",
    "com.noahbackup.storage",
    "com.noahbackup.filesystem"
})
public class NoahBackupApiApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(NoahBackupApiApplication.class);
    
    public static void main(String[] args) {
        logger.info("Starting Noah Backup REST API...");
        
        // Print API banner
        printApiBanner();
        
        SpringApplication.run(NoahBackupApiApplication.class, args);
        
        logger.info("Noah Backup REST API started successfully!");
        logger.info("API Documentation: http://localhost:8080/swagger-ui.html (when enabled)");
        logger.info("Health Check: http://localhost:8080/actuator/health");
        logger.info("Authentication required for all backup endpoints");
    }
    
    private static void printApiBanner() {
        System.out.println("""
            ╔═══════════════════════════════════════════════════════════════╗
            ║                    NOAH BACKUP REST API                      ║
            ║                                                               ║
            ║  🔐 Secure HTTP API for Enterprise Backup Operations         ║
            ║                                                               ║
            ║  Features:                                                    ║
            ║  • JWT Authentication & Authorization                         ║
            ║  • Real-time backup status monitoring                        ║
            ║  • Schedule management & configuration                        ║
            ║  • Health checks & system metrics                            ║
            ║  • Audit logging & security controls                         ║
            ║                                                               ║
            ║  Endpoints:                                                   ║
            ║  POST /api/v1/backup/start    - Trigger manual backup        ║
            ║  GET  /api/v1/backup/status   - Get backup status            ║
            ║  GET  /api/v1/config          - View configuration           ║
            ║  POST /api/v1/schedule/update - Update backup schedule       ║
            ║  POST /api/v1/auth/login      - Authenticate & get JWT       ║
            ║                                                               ║
            ╚═══════════════════════════════════════════════════════════════╝
            """);
    }
}