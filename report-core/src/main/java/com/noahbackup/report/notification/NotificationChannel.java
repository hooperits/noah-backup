package com.noahbackup.report.notification;

/**
 * Enumeration of supported notification channels.
 * 
 * Each channel represents a different method of delivering notifications
 * about backup operations, system status, and alerts.
 */
public enum NotificationChannel {
    
    /**
     * Email notifications via SMTP.
     * Supports HTML and plain text formatting.
     */
    EMAIL("email", "Email (SMTP)", true),
    
    /**
     * Slack notifications via webhook or API.
     * Supports rich formatting and attachments.
     */
    SLACK("slack", "Slack", false),
    
    /**
     * Microsoft Teams notifications via webhook.
     * Supports adaptive cards and rich messaging.
     */
    TEAMS("teams", "Microsoft Teams", false),
    
    /**
     * Discord notifications via webhook.
     * Supports embeds and mentions.
     */
    DISCORD("discord", "Discord", false),
    
    /**
     * Generic webhook notifications.
     * Sends JSON payload to any HTTP endpoint.
     */
    WEBHOOK("webhook", "Generic Webhook", false),
    
    /**
     * Local log file notifications.
     * Always available as a fallback.
     */
    LOG("log", "Log File", true);
    
    private final String code;
    private final String displayName;
    private final boolean implemented;
    
    NotificationChannel(String code, String displayName, boolean implemented) {
        this.code = code;
        this.displayName = displayName;
        this.implemented = implemented;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isImplemented() {
        return implemented;
    }
    
    /**
     * Get channel by code string.
     */
    public static NotificationChannel fromCode(String code) {
        for (NotificationChannel channel : values()) {
            if (channel.code.equalsIgnoreCase(code)) {
                return channel;
            }
        }
        throw new IllegalArgumentException("Unknown notification channel: " + code);
    }
    
    /**
     * Get all implemented channels.
     */
    public static NotificationChannel[] getImplementedChannels() {
        return java.util.Arrays.stream(values())
            .filter(NotificationChannel::isImplemented)
            .toArray(NotificationChannel[]::new);
    }
}