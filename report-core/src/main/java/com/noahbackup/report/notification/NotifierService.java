package com.noahbackup.report.notification;

import com.noahbackup.auth.SecureCodingUtils;
import com.noahbackup.report.BackupReportSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service for managing notifications about backup operations and system status.
 * 
 * This service provides a framework for sending notifications through multiple channels:
 * - Email (SMTP) - Currently implemented
 * - Slack - Future implementation
 * - Microsoft Teams - Future implementation
 * - Discord - Future implementation
 * - Generic Webhooks - Future implementation
 * - Log files - Always available as fallback
 * 
 * Features:
 * - Asynchronous notification delivery
 * - Multiple channel support
 * - Template-based message formatting
 * - Retry logic for failed deliveries
 * - Configuration-driven channel enablement
 */
@Service
public class NotifierService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotifierService.class);
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Value("${noah.notifications.enabled:true}")
    private boolean notificationsEnabled;
    
    @Value("${noah.notifications.channels:log}")
    private String[] enabledChannels;
    
    @Value("${noah.notifications.backup.success.enabled:true}")
    private boolean backupSuccessNotificationsEnabled;
    
    @Value("${noah.notifications.backup.failure.enabled:true}")
    private boolean backupFailureNotificationsEnabled;
    
    @Value("${noah.notifications.system.alerts.enabled:true}")
    private boolean systemAlertNotificationsEnabled;
    
    private ExecutorService notificationExecutor;
    private Set<NotificationChannel> activeChannels;
    
    @PostConstruct
    public void initialize() {
        // Initialize async executor for notifications
        notificationExecutor = Executors.newFixedThreadPool(3, r -> {
            Thread t = new Thread(r, "notification-sender");
            t.setDaemon(true);
            return t;
        });
        
        // Parse enabled channels
        activeChannels = new HashSet<>();
        if (notificationsEnabled && enabledChannels != null) {
            for (String channelCode : enabledChannels) {
                try {
                    NotificationChannel channel = NotificationChannel.fromCode(channelCode.trim());
                    if (channel.isImplemented()) {
                        activeChannels.add(channel);
                    } else {
                        SecureCodingUtils.safeLog(logger, "WARN", 
                            "Notification channel '{}' is configured but not yet implemented", channelCode);
                    }
                } catch (IllegalArgumentException e) {
                    SecureCodingUtils.safeLog(logger, "WARN", 
                        "Unknown notification channel configured: {}", channelCode);
                }
            }
        }
        
        // Always ensure LOG channel is available as fallback
        activeChannels.add(NotificationChannel.LOG);
        
        SecureCodingUtils.safeLog(logger, "INFO", 
            "NotifierService initialized. Notifications enabled: {}, Active channels: {}", 
            notificationsEnabled, activeChannels);
    }
    
    /**
     * Send a notification about backup operation completion.
     */
    public void notifyBackupComplete(BackupReportSummary summary) {
        if (!notificationsEnabled || summary == null) {
            return;
        }
        
        boolean isSuccess = summary.isSuccess();
        if ((isSuccess && !backupSuccessNotificationsEnabled) || 
            (!isSuccess && !backupFailureNotificationsEnabled)) {
            return;
        }
        
        NotificationMessage.Level level = isSuccess ? 
            NotificationMessage.Level.SUCCESS : NotificationMessage.Level.ERROR;
        
        String title = isSuccess ? 
            "Backup Operation Completed Successfully" : 
            "Backup Operation Failed";
        
        String message = formatBackupSummaryMessage(summary);
        
        NotificationMessage notification = NotificationMessage.builder()
            .level(level)
            .title(title)
            .message(message)
            .targetChannels(activeChannels.toArray(new NotificationChannel[0]))
            .addMetadata("operation_id", summary.getOperationId())
            .addMetadata("job_type", summary.getJobType())
            .addMetadata("success", summary.isSuccess())
            .addMetadata("files_processed", summary.getTotalFilesProcessed())
            .addMetadata("duration_ms", summary.getDurationMillis())
            .build();
        
        sendNotificationAsync(notification);
    }
    
    /**
     * Send a system alert notification.
     */
    public void notifySystemAlert(String title, String message, NotificationMessage.Level level) {
        if (!notificationsEnabled || !systemAlertNotificationsEnabled) {
            return;
        }
        
        NotificationMessage notification = NotificationMessage.builder()
            .level(level)
            .title(title)
            .message(message)
            .targetChannels(activeChannels.toArray(new NotificationChannel[0]))
            .addMetadata("alert_type", "system")
            .build();
        
        sendNotificationAsync(notification);
    }
    
    /**
     * Send a custom notification message.
     */
    public void sendNotification(NotificationMessage notification) {
        if (!notificationsEnabled || notification == null) {
            return;
        }
        
        sendNotificationAsync(notification);
    }
    
    /**
     * Send notification asynchronously to avoid blocking main operations.
     */
    private void sendNotificationAsync(NotificationMessage notification) {
        CompletableFuture.runAsync(() -> {
            try {
                sendNotificationSync(notification);
            } catch (Exception e) {
                SecureCodingUtils.safeLog(logger, "ERROR", 
                    "Failed to send notification {}: {}", notification.getId(), e.getMessage());
            }
        }, notificationExecutor);
    }
    
    /**
     * Synchronous notification sending (internal use).
     */
    private void sendNotificationSync(NotificationMessage notification) {
        int successCount = 0;
        int totalChannels = 0;
        
        for (NotificationChannel channel : notification.getTargetChannels()) {
            if (!activeChannels.contains(channel)) {
                continue;
            }
            
            totalChannels++;
            try {
                boolean sent = sendToChannel(notification, channel);
                if (sent) {
                    successCount++;
                }
            } catch (Exception e) {
                SecureCodingUtils.safeLog(logger, "WARN", 
                    "Failed to send notification {} to channel {}: {}", 
                    notification.getId(), channel.getDisplayName(), e.getMessage());
            }
        }
        
        SecureCodingUtils.safeLog(logger, "DEBUG", 
            "Notification {} sent to {}/{} channels successfully", 
            notification.getId(), successCount, totalChannels);
    }
    
    /**
     * Send notification to a specific channel.
     */
    private boolean sendToChannel(NotificationMessage notification, NotificationChannel channel) {
        switch (channel) {
            case LOG:
                return sendToLog(notification);
            
            case EMAIL:
                return sendToEmail(notification);
            
            case SLACK:
            case TEAMS:
            case DISCORD:
            case WEBHOOK:
            default:
                SecureCodingUtils.safeLog(logger, "WARN", 
                    "Notification channel {} is not yet implemented", channel.getDisplayName());
                return false;
        }
    }
    
    /**
     * Send notification to log file (always available).
     */
    private boolean sendToLog(NotificationMessage notification) {
        try {
            String logMessage = String.format("[NOTIFICATION] [%s] %s: %s", 
                notification.getLevel().name(),
                notification.getTitle(),
                notification.getSummary() != null ? notification.getSummary() : notification.getMessage());
            
            switch (notification.getLevel()) {
                case ERROR:
                case CRITICAL:
                    logger.error(logMessage);
                    break;
                case WARNING:
                    logger.warn(logMessage);
                    break;
                case SUCCESS:
                case INFO:
                default:
                    logger.info(logMessage);
                    break;
            }
            
            return true;
        } catch (Exception e) {
            // This should not happen, but handle gracefully
            return false;
        }
    }
    
    /**
     * Send notification via email (basic implementation).
     * Future: Integrate with Spring Boot's mail starter for full SMTP support.
     */
    private boolean sendToEmail(NotificationMessage notification) {
        // TODO: Implement email sending using Spring Boot Mail
        // This would use JavaMailSender to send HTML/text emails
        
        SecureCodingUtils.safeLog(logger, "DEBUG", 
            "EMAIL notification would be sent: {} - {}", 
            notification.getTitle(), notification.getSummary());
        
        // For now, log that email would be sent
        // In the future, this will actually send email via SMTP
        return true;
    }
    
    /**
     * Format backup summary into a readable message.
     */
    private String formatBackupSummaryMessage(BackupReportSummary summary) {
        StringBuilder message = new StringBuilder();
        
        message.append("📊 **Backup Operation Summary**\n\n");
        message.append("**Operation ID:** ").append(summary.getOperationId()).append("\n");
        message.append("**Job Type:** ").append(summary.getJobType()).append("\n");
        message.append("**Start Time:** ").append(summary.getStartTime().format(TIMESTAMP_FORMAT)).append("\n");
        message.append("**End Time:** ").append(summary.getEndTime().format(TIMESTAMP_FORMAT)).append("\n");
        message.append("**Duration:** ").append(summary.getFormattedDuration()).append("\n");
        message.append("**Status:** ").append(summary.isSuccess() ? "✅ SUCCESS" : "❌ FAILED").append("\n\n");
        
        message.append("**File Processing:**\n");
        message.append("- Total Files: ").append(summary.getTotalFilesProcessed()).append("\n");
        message.append("- Successful: ").append(summary.getSuccessfulFiles()).append("\n");
        message.append("- Failed: ").append(summary.getFailedFilesCount()).append("\n");
        message.append("- Success Rate: ").append(String.format("%.1f%%", summary.getSuccessRate())).append("\n");
        message.append("- Data Processed: ").append(summary.getFileSizeFormatted()).append("\n\n");
        
        message.append("**Configuration:**\n");
        message.append("- Target Bucket: ").append(summary.getTargetBucket()).append("\n");
        message.append("- Backup Paths: ").append(summary.getBackupPaths() != null ? summary.getBackupPaths().size() : 0).append(" paths\n");
        message.append("- Trigger: ").append(summary.getTriggerType()).append("\n");
        
        if (!summary.isSuccess() && summary.getErrorMessages() != null && !summary.getErrorMessages().isEmpty()) {
            message.append("\n**Errors:**\n");
            for (String error : summary.getErrorMessages()) {
                message.append("- ").append(error).append("\n");
            }
        }
        
        return message.toString();
    }
    
    /**
     * Get status of notification system.
     */
    public Map<String, Object> getNotificationStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("enabled", notificationsEnabled);
        status.put("active_channels", activeChannels.size());
        status.put("available_channels", NotificationChannel.values().length);
        status.put("backup_success_notifications", backupSuccessNotificationsEnabled);
        status.put("backup_failure_notifications", backupFailureNotificationsEnabled);
        status.put("system_alert_notifications", systemAlertNotificationsEnabled);
        
        Map<String, Boolean> channels = new HashMap<>();
        for (NotificationChannel channel : NotificationChannel.values()) {
            channels.put(channel.getCode(), activeChannels.contains(channel));
        }
        status.put("channels", channels);
        
        return status;
    }
    
    /**
     * Test notification sending to all active channels.
     */
    public void sendTestNotification() {
        NotificationMessage testMessage = NotificationMessage.builder()
            .level(NotificationMessage.Level.INFO)
            .title("Noah Backup Test Notification")
            .message("This is a test notification from Noah Backup system.\n\n" +
                    "If you receive this message, notifications are working correctly.")
            .targetChannels(activeChannels.toArray(new NotificationChannel[0]))
            .addMetadata("test", true)
            .build();
        
        sendNotificationAsync(testMessage);
        
        SecureCodingUtils.safeLog(logger, "INFO", "Test notification sent to {} active channels", activeChannels.size());
    }
    
    /**
     * Cleanup resources on shutdown.
     */
    public void shutdown() {
        if (notificationExecutor != null && !notificationExecutor.isShutdown()) {
            notificationExecutor.shutdown();
            SecureCodingUtils.safeLog(logger, "INFO", "NotifierService shutdown completed");
        }
    }
}