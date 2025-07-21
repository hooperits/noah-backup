package com.noahbackup.report.notification;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents a notification message to be sent through various channels.
 * 
 * Contains all information needed to format and deliver notifications
 * across different platforms (email, Slack, Teams, etc.).
 */
public class NotificationMessage {
    
    /**
     * Notification severity levels.
     */
    public enum Level {
        INFO("info", "✅"),
        SUCCESS("success", "🎉"),
        WARNING("warning", "⚠️"),
        ERROR("error", "❌"),
        CRITICAL("critical", "🚨");
        
        private final String code;
        private final String emoji;
        
        Level(String code, String emoji) {
            this.code = code;
            this.emoji = emoji;
        }
        
        public String getCode() { return code; }
        public String getEmoji() { return emoji; }
    }
    
    private String id;
    private Level level;
    private String title;
    private String message;
    private String summary;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;
    private NotificationChannel[] targetChannels;
    
    // Default constructor
    public NotificationMessage() {
        this.timestamp = LocalDateTime.now();
        this.id = java.util.UUID.randomUUID().toString();
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private NotificationMessage message = new NotificationMessage();
        
        public Builder level(Level level) {
            message.level = level;
            return this;
        }
        
        public Builder title(String title) {
            message.title = title;
            return this;
        }
        
        public Builder message(String messageText) {
            message.message = messageText;
            return this;
        }
        
        public Builder summary(String summary) {
            message.summary = summary;
            return this;
        }
        
        public Builder metadata(Map<String, Object> metadata) {
            message.metadata = metadata;
            return this;
        }
        
        public Builder targetChannels(NotificationChannel... channels) {
            message.targetChannels = channels;
            return this;
        }
        
        public Builder addMetadata(String key, Object value) {
            if (message.metadata == null) {
                message.metadata = new java.util.HashMap<>();
            }
            message.metadata.put(key, value);
            return this;
        }
        
        public NotificationMessage build() {
            // Set defaults
            if (message.level == null) {
                message.level = Level.INFO;
            }
            if (message.targetChannels == null) {
                message.targetChannels = new NotificationChannel[]{NotificationChannel.LOG};
            }
            if (message.summary == null && message.message != null) {
                // Generate summary from first line or first 100 characters
                String[] lines = message.message.split("\n");
                message.summary = lines[0].length() > 100 ? lines[0].substring(0, 97) + "..." : lines[0];
            }
            
            return message;
        }
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public Level getLevel() { return level; }
    public void setLevel(Level level) { this.level = level; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public NotificationChannel[] getTargetChannels() { return targetChannels; }
    public void setTargetChannels(NotificationChannel[] targetChannels) { this.targetChannels = targetChannels; }
    
    // Utility methods
    public String getFormattedTitle() {
        if (title == null) return "";
        return level.getEmoji() + " " + title;
    }
    
    public boolean shouldNotifyChannel(NotificationChannel channel) {
        if (targetChannels == null) return false;
        for (NotificationChannel target : targetChannels) {
            if (target == channel) return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return String.format("NotificationMessage{id='%s', level=%s, title='%s', channels=%d}",
                id, level, title, targetChannels != null ? targetChannels.length : 0);
    }
}