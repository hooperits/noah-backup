package com.noahbackup.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.noahbackup.auth.SecureCodingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing backup operation reports and summaries.
 * 
 * Responsibilities:
 * - Save backup summaries to date-organized files
 * - Retrieve historical backup reports
 * - Manage log file rotation and cleanup
 * - Provide backup statistics and analytics
 * 
 * File Organization:
 * - /logs/reports/YYYY-MM-DD/backup-summary-{timestamp}.json
 * - /logs/reports/YYYY-MM-DD/daily-summary.json (aggregated)
 */
@Service
public class BackupReportService {
    
    private static final Logger logger = LoggerFactory.getLogger(BackupReportService.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss");
    
    @Value("${noah.backup.logs.directory:logs}")
    private String logsDirectory;
    
    @Value("${noah.backup.reports.retention.days:90}")
    private int retentionDays;
    
    private ObjectMapper objectMapper;
    private Path reportsPath;
    
    @PostConstruct
    public void initialize() {
        // Configure Jackson for LocalDateTime support
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        
        // Create reports directory structure
        try {
            reportsPath = Paths.get(logsDirectory, "reports");
            Files.createDirectories(reportsPath);
            
            SecureCodingUtils.safeLog(logger, "INFO", "BackupReportService initialized. Reports directory: {}", 
                                    reportsPath.toAbsolutePath());
        } catch (IOException e) {
            SecureCodingUtils.safeLog(logger, "ERROR", "Failed to create reports directory: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize BackupReportService", e);
        }
    }
    
    /**
     * Save a backup report summary to the appropriate date-organized directory.
     */
    public void saveBackupReport(BackupReportSummary summary) {
        if (summary == null || summary.getStartTime() == null) {
            SecureCodingUtils.safeLog(logger, "WARN", "Cannot save null or incomplete backup report");
            return;
        }
        
        try {
            // Create date-specific directory
            LocalDate reportDate = summary.getStartTime().toLocalDate();
            Path datePath = reportsPath.resolve(reportDate.format(DATE_FORMAT));
            Files.createDirectories(datePath);
            
            // Generate filename with timestamp
            String timestamp = summary.getStartTime().format(TIMESTAMP_FORMAT);
            String filename = String.format("backup-summary-%s.json", timestamp);
            Path reportFile = datePath.resolve(filename);
            
            // Save individual report
            objectMapper.writeValue(reportFile.toFile(), summary);
            
            SecureCodingUtils.safeLog(logger, "INFO", "Backup report saved: {} (Operation: {}, Success: {}, Files: {})", 
                                    filename, summary.getOperationId(), summary.isSuccess(), summary.getTotalFilesProcessed());
            
            // Update daily summary
            updateDailySummary(datePath, reportDate);
            
        } catch (IOException e) {
            SecureCodingUtils.safeLog(logger, "ERROR", "Failed to save backup report: {}", e.getMessage());
            throw new RuntimeException("Failed to save backup report", e);
        }
    }
    
    /**
     * Update the daily summary file with aggregated statistics.
     */
    private void updateDailySummary(Path datePath, LocalDate date) {
        try {
            List<BackupReportSummary> dailyReports = loadDailyReports(date);
            
            if (dailyReports.isEmpty()) {
                return;
            }
            
            // Calculate daily statistics
            Map<String, Object> dailySummary = new HashMap<>();
            dailySummary.put("date", date.format(DATE_FORMAT));
            dailySummary.put("timestamp", LocalDateTime.now());
            dailySummary.put("totalOperations", dailyReports.size());
            
            int successfulOps = (int) dailyReports.stream().filter(BackupReportSummary::isSuccess).count();
            dailySummary.put("successfulOperations", successfulOps);
            dailySummary.put("failedOperations", dailyReports.size() - successfulOps);
            dailySummary.put("successRate", dailyReports.size() > 0 ? (double) successfulOps / dailyReports.size() * 100 : 0);
            
            int totalFiles = dailyReports.stream().mapToInt(BackupReportSummary::getTotalFilesProcessed).sum();
            int successfulFiles = dailyReports.stream().mapToInt(BackupReportSummary::getSuccessfulFiles).sum();
            long totalBytes = dailyReports.stream().mapToLong(BackupReportSummary::getTotalBytesProcessed).sum();
            
            dailySummary.put("totalFilesProcessed", totalFiles);
            dailySummary.put("successfulFiles", successfulFiles);
            dailySummary.put("failedFiles", totalFiles - successfulFiles);
            dailySummary.put("totalBytesProcessed", totalBytes);
            
            long totalDuration = dailyReports.stream().mapToLong(BackupReportSummary::getDurationMillis).sum();
            dailySummary.put("totalDurationMillis", totalDuration);
            dailySummary.put("averageDurationMillis", dailyReports.size() > 0 ? totalDuration / dailyReports.size() : 0);
            
            // Save daily summary
            Path summaryFile = datePath.resolve("daily-summary.json");
            objectMapper.writeValue(summaryFile.toFile(), dailySummary);
            
        } catch (IOException e) {
            SecureCodingUtils.safeLog(logger, "ERROR", "Failed to update daily summary for {}: {}", date, e.getMessage());
        }
    }
    
    /**
     * Load all backup reports for a specific date.
     */
    public List<BackupReportSummary> loadDailyReports(LocalDate date) {
        List<BackupReportSummary> reports = new ArrayList<>();
        
        try {
            Path datePath = reportsPath.resolve(date.format(DATE_FORMAT));
            if (!Files.exists(datePath)) {
                return reports;
            }
            
            Files.list(datePath)
                .filter(path -> path.toString().endsWith(".json") && !path.toString().endsWith("daily-summary.json"))
                .forEach(reportFile -> {
                    try {
                        BackupReportSummary summary = objectMapper.readValue(reportFile.toFile(), BackupReportSummary.class);
                        reports.add(summary);
                    } catch (IOException e) {
                        SecureCodingUtils.safeLog(logger, "WARN", "Failed to read report file {}: {}", 
                                                reportFile.getFileName(), e.getMessage());
                    }
                });
            
            // Sort by start time
            reports.sort((a, b) -> a.getStartTime().compareTo(b.getStartTime()));
            
        } catch (IOException e) {
            SecureCodingUtils.safeLog(logger, "ERROR", "Failed to load daily reports for {}: {}", date, e.getMessage());
        }
        
        return reports;
    }
    
    /**
     * Load daily summary for a specific date.
     */
    public Optional<Map<String, Object>> loadDailySummary(LocalDate date) {
        try {
            Path summaryFile = reportsPath.resolve(date.format(DATE_FORMAT)).resolve("daily-summary.json");
            if (!Files.exists(summaryFile)) {
                return Optional.empty();
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Object> summary = objectMapper.readValue(summaryFile.toFile(), Map.class);
            return Optional.of(summary);
            
        } catch (IOException e) {
            SecureCodingUtils.safeLog(logger, "ERROR", "Failed to load daily summary for {}: {}", date, e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Get backup reports for a date range.
     */
    public List<BackupReportSummary> getReportsInRange(LocalDate startDate, LocalDate endDate) {
        List<BackupReportSummary> allReports = new ArrayList<>();
        
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            allReports.addAll(loadDailyReports(current));
            current = current.plusDays(1);
        }
        
        return allReports;
    }
    
    /**
     * Get recent backup reports (last N days).
     */
    public List<BackupReportSummary> getRecentReports(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        return getReportsInRange(startDate, endDate);
    }
    
    /**
     * Get overall backup statistics for a time period.
     */
    public Map<String, Object> getBackupStatistics(LocalDate startDate, LocalDate endDate) {
        List<BackupReportSummary> reports = getReportsInRange(startDate, endDate);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("period", String.format("%s to %s", startDate.format(DATE_FORMAT), endDate.format(DATE_FORMAT)));
        stats.put("totalOperations", reports.size());
        
        if (reports.isEmpty()) {
            stats.put("successRate", 0.0);
            stats.put("totalFiles", 0);
            stats.put("totalBytes", 0L);
            return stats;
        }
        
        int successfulOps = (int) reports.stream().filter(BackupReportSummary::isSuccess).count();
        stats.put("successfulOperations", successfulOps);
        stats.put("failedOperations", reports.size() - successfulOps);
        stats.put("successRate", (double) successfulOps / reports.size() * 100);
        
        int totalFiles = reports.stream().mapToInt(BackupReportSummary::getTotalFilesProcessed).sum();
        int successfulFiles = reports.stream().mapToInt(BackupReportSummary::getSuccessfulFiles).sum();
        long totalBytes = reports.stream().mapToLong(BackupReportSummary::getTotalBytesProcessed).sum();
        
        stats.put("totalFiles", totalFiles);
        stats.put("successfulFiles", successfulFiles);
        stats.put("fileSuccessRate", totalFiles > 0 ? (double) successfulFiles / totalFiles * 100 : 0);
        stats.put("totalBytes", totalBytes);
        
        OptionalDouble avgDuration = reports.stream().mapToLong(BackupReportSummary::getDurationMillis).average();
        stats.put("averageDurationMillis", avgDuration.orElse(0));
        
        return stats;
    }
    
    /**
     * Clean up old report files based on retention policy.
     */
    public int cleanupOldReports() {
        int deletedDirectories = 0;
        LocalDate cutoffDate = LocalDate.now().minusDays(retentionDays);
        
        try {
            if (!Files.exists(reportsPath)) {
                return 0;
            }
            
            Files.list(reportsPath)
                .filter(Files::isDirectory)
                .forEach(dateDir -> {
                    try {
                        LocalDate dirDate = LocalDate.parse(dateDir.getFileName().toString(), DATE_FORMAT);
                        if (dirDate.isBefore(cutoffDate)) {
                            deleteDirectory(dateDir);
                            SecureCodingUtils.safeLog(logger, "INFO", "Deleted old report directory: {}", dateDir.getFileName());
                        }
                    } catch (Exception e) {
                        SecureCodingUtils.safeLog(logger, "WARN", "Failed to process report directory {}: {}", 
                                                dateDir.getFileName(), e.getMessage());
                    }
                });
            
        } catch (IOException e) {
            SecureCodingUtils.safeLog(logger, "ERROR", "Failed to cleanup old reports: {}", e.getMessage());
        }
        
        return deletedDirectories;
    }
    
    private void deleteDirectory(Path directory) throws IOException {
        Files.walk(directory)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
    }
    
    /**
     * Get available report dates.
     */
    public List<LocalDate> getAvailableReportDates() {
        List<LocalDate> dates = new ArrayList<>();
        
        try {
            if (!Files.exists(reportsPath)) {
                return dates;
            }
            
            dates = Files.list(reportsPath)
                .filter(Files::isDirectory)
                .map(path -> {
                    try {
                        return LocalDate.parse(path.getFileName().toString(), DATE_FORMAT);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
                
        } catch (IOException e) {
            SecureCodingUtils.safeLog(logger, "ERROR", "Failed to get available report dates: {}", e.getMessage());
        }
        
        return dates;
    }
}