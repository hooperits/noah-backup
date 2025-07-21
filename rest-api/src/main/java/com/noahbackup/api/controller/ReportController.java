package com.noahbackup.api.controller;

import com.noahbackup.auth.SecureCodingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * REST API controller for backup reporting and analytics.
 * 
 * Provides secure endpoints for:
 * - Backup operation reports and history
 * - Daily/weekly/monthly statistics
 * - Notification system status
 * - Report export functionality
 * 
 * Security:
 * - JWT authentication required
 * - Role-based access control
 * - Input validation and sanitization
 * - Audit logging for all report access
 * 
 * Integration Note:
 * This controller provides the API framework for report-core integration.
 * Full integration with BackupReportService will be implemented when
 * report-core is connected to the main application.
 */
@RestController
@RequestMapping("/api/v1/reports")
@Validated
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8081"}, maxAge = 3600)
public class ReportController {
    
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Note: These will be injected once report-core is integrated
    // @Autowired
    // private BackupReportService backupReportService;
    // @Autowired 
    // private NotifierService notifierService;
    
    /**
     * GET /api/v1/reports/daily/{date}
     * Get backup reports for a specific date.
     */
    @GetMapping("/daily/{date}")
    @PreAuthorize("hasRole('BACKUP_ADMIN') or hasRole('BACKUP_USER') or hasRole('BACKUP_VIEWER')")
    public ResponseEntity<Map<String, Object>> getDailyReport(@PathVariable String date) {
        SecureCodingUtils.safeLog(logger, "INFO", "Daily report requested for date: {}", date);
        
        try {
            // Validate date format
            LocalDate reportDate = LocalDate.parse(date, DATE_FORMAT);
            
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now());
            response.put("date", date);
            response.put("message", "Daily report functionality will be available when report-core is integrated");
            
            // Future integration point:
            // List<BackupReportSummary> reports = backupReportService.loadDailyReports(reportDate);
            // Optional<Map<String, Object>> summary = backupReportService.loadDailySummary(reportDate);
            // response.put("reports", reports);
            // response.put("summary", summary.orElse(null));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            String safeMessage = SecureCodingUtils.createSafeExceptionMessage("Daily report retrieval", e);
            SecureCodingUtils.safeLog(logger, "ERROR", safeMessage);
            
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * GET /api/v1/reports/history
     * Get recent backup operation history.
     */
    @GetMapping("/history")
    @PreAuthorize("hasRole('BACKUP_ADMIN') or hasRole('BACKUP_USER') or hasRole('BACKUP_VIEWER')")
    public ResponseEntity<Map<String, Object>> getBackupHistory(
            @RequestParam(defaultValue = "7") @Min(1) @Max(90) int days) {
        
        SecureCodingUtils.safeLog(logger, "INFO", "Backup history requested for last {} days", days);
        
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now());
            response.put("days", days);
            response.put("message", "Backup history functionality will be available when report-core is integrated");
            
            // Future integration point:
            // List<BackupReportSummary> reports = backupReportService.getRecentReports(days);
            // response.put("reports", reports);
            // response.put("total_operations", reports.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            String safeMessage = SecureCodingUtils.createSafeExceptionMessage("Backup history retrieval", e);
            SecureCodingUtils.safeLog(logger, "ERROR", safeMessage);
            
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * GET /api/v1/reports/statistics
     * Get backup statistics for a date range.
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('BACKUP_ADMIN') or hasRole('BACKUP_USER') or hasRole('BACKUP_VIEWER')")
    public ResponseEntity<Map<String, Object>> getBackupStatistics(
            @RequestParam(defaultValue = "30") @Min(1) @Max(365) int days) {
        
        SecureCodingUtils.safeLog(logger, "INFO", "Backup statistics requested for last {} days", days);
        
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(days - 1);
            
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now());
            response.put("period_start", startDate.format(DATE_FORMAT));
            response.put("period_end", endDate.format(DATE_FORMAT));
            response.put("days", days);
            response.put("message", "Statistics functionality will be available when report-core is integrated");
            
            // Future integration point:
            // Map<String, Object> stats = backupReportService.getBackupStatistics(startDate, endDate);
            // response.putAll(stats);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            String safeMessage = SecureCodingUtils.createSafeExceptionMessage("Statistics retrieval", e);
            SecureCodingUtils.safeLog(logger, "ERROR", safeMessage);
            
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * GET /api/v1/reports/available-dates
     * Get list of dates for which reports are available.
     */
    @GetMapping("/available-dates")
    @PreAuthorize("hasRole('BACKUP_ADMIN') or hasRole('BACKUP_USER') or hasRole('BACKUP_VIEWER')")
    public ResponseEntity<Map<String, Object>> getAvailableReportDates() {
        SecureCodingUtils.safeLog(logger, "DEBUG", "Available report dates requested");
        
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now());
            response.put("message", "Available dates functionality will be available when report-core is integrated");
            
            // Future integration point:
            // List<LocalDate> dates = backupReportService.getAvailableReportDates();
            // response.put("dates", dates.stream().map(d -> d.format(DATE_FORMAT)).collect(Collectors.toList()));
            // response.put("count", dates.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            String safeMessage = SecureCodingUtils.createSafeExceptionMessage("Available dates retrieval", e);
            SecureCodingUtils.safeLog(logger, "ERROR", safeMessage);
            
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * GET /api/v1/reports/notifications/status
     * Get notification system status.
     */
    @GetMapping("/notifications/status")
    @PreAuthorize("hasRole('BACKUP_ADMIN')")
    public ResponseEntity<Map<String, Object>> getNotificationStatus() {
        SecureCodingUtils.safeLog(logger, "DEBUG", "Notification status requested");
        
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now());
            response.put("message", "Notification status will be available when report-core is integrated");
            
            // Future integration point:
            // Map<String, Object> status = notifierService.getNotificationStatus();
            // response.putAll(status);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            String safeMessage = SecureCodingUtils.createSafeExceptionMessage("Notification status retrieval", e);
            SecureCodingUtils.safeLog(logger, "ERROR", safeMessage);
            
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * POST /api/v1/reports/notifications/test
     * Send a test notification to all configured channels.
     */
    @PostMapping("/notifications/test")
    @PreAuthorize("hasRole('BACKUP_ADMIN')")
    public ResponseEntity<Map<String, Object>> sendTestNotification() {
        SecureCodingUtils.safeLog(logger, "INFO", "Test notification requested");
        
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now());
            response.put("success", true);
            response.put("message", "Test notification functionality will be available when report-core is integrated");
            
            // Future integration point:
            // notifierService.sendTestNotification();
            // response.put("sent", true);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            String safeMessage = SecureCodingUtils.createSafeExceptionMessage("Test notification", e);
            SecureCodingUtils.safeLog(logger, "ERROR", safeMessage);
            
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now());
            response.put("success", false);
            response.put("message", "Test notification failed");
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * POST /api/v1/reports/cleanup
     * Trigger cleanup of old report files.
     */
    @PostMapping("/cleanup")
    @PreAuthorize("hasRole('BACKUP_ADMIN')")
    public ResponseEntity<Map<String, Object>> cleanupOldReports() {
        SecureCodingUtils.safeLog(logger, "INFO", "Report cleanup requested");
        
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now());
            response.put("message", "Report cleanup functionality will be available when report-core is integrated");
            
            // Future integration point:
            // int deletedDirectories = backupReportService.cleanupOldReports();
            // response.put("deleted_directories", deletedDirectories);
            // response.put("success", true);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            String safeMessage = SecureCodingUtils.createSafeExceptionMessage("Report cleanup", e);
            SecureCodingUtils.safeLog(logger, "ERROR", safeMessage);
            
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now());
            response.put("success", false);
            response.put("message", "Report cleanup failed");
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * GET /api/v1/reports/health
     * Health check for reporting system.
     */
    @GetMapping("/health")
    @PreAuthorize("hasRole('BACKUP_ADMIN') or hasRole('BACKUP_USER') or hasRole('BACKUP_VIEWER')")
    public ResponseEntity<Map<String, Object>> getReportingHealth() {
        SecureCodingUtils.safeLog(logger, "DEBUG", "Reporting health check requested");
        
        try {
            Map<String, Object> health = new HashMap<>();
            health.put("timestamp", LocalDateTime.now());
            health.put("status", "UP");
            health.put("message", "Reporting health check will be enhanced when report-core is integrated");
            
            // Current basic health info
            health.put("reports_api", "UP");
            health.put("configuration", "UP");
            
            // Future integration point:
            // health.put("reports_directory", Files.exists(Paths.get("logs/reports")) ? "UP" : "DOWN");
            // health.put("notification_service", notifierService != null ? "UP" : "DOWN");
            // health.put("report_service", backupReportService != null ? "UP" : "DOWN");
            
            return ResponseEntity.ok(health);
            
        } catch (Exception e) {
            String safeMessage = SecureCodingUtils.createSafeExceptionMessage("Reporting health check", e);
            SecureCodingUtils.safeLog(logger, "ERROR", safeMessage);
            
            Map<String, Object> health = new HashMap<>();
            health.put("timestamp", LocalDateTime.now());
            health.put("status", "DOWN");
            health.put("error", "Health check failed");
            
            return ResponseEntity.status(503).body(health);
        }
    }
}