package com.noahbackup.appsec.validation;

import java.util.HashMap;
import java.util.Map;

/**
 * Context information for input validation operations.
 * 
 * Provides context-specific validation rules and settings based on:
 * - Input type (email, URL, filename, etc.)
 * - Source of input (API, form, file upload, etc.)
 * - User context (authenticated user, role, permissions)
 * - Request context (IP address, user agent, session info)
 */
public class ValidationContext {
    
    /**
     * Types of input that require specific validation rules.
     */
    public enum InputType {
        EMAIL("Email address validation"),
        URL("URL validation with scheme checking"),
        FILENAME("Filename validation with extension checking"),
        USERNAME("Username format validation"),
        PASSWORD("Password strength validation"),
        BACKUP_PATH("Backup path validation"),
        S3_BUCKET_NAME("S3 bucket name validation"),
        CRON_EXPRESSION("Cron expression validation"),
        JSON_DATA("JSON data validation"),
        XML_DATA("XML data validation"),
        SQL_QUERY("SQL query validation"),
        SEARCH_QUERY("Search query validation"),
        FREE_TEXT("Free text with XSS protection"),
        NUMERIC("Numeric value validation"),
        DATE_TIME("Date/time format validation"),
        IP_ADDRESS("IP address validation"),
        PHONE_NUMBER("Phone number validation"),
        API_KEY("API key format validation"),
        JWT_TOKEN("JWT token validation"),
        GENERIC("Generic input validation");
        
        private final String description;
        
        InputType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Sources where input originates from.
     */
    public enum InputSource {
        WEB_FORM("Web form submission"),
        REST_API("REST API endpoint"),
        FILE_UPLOAD("File upload"),
        COMMAND_LINE("Command line argument"),
        CONFIGURATION("Configuration file"),
        DATABASE("Database query result"),
        EXTERNAL_API("External API response"),
        USER_INPUT("Direct user input"),
        SYSTEM_GENERATED("System generated value");
        
        private final String description;
        
        InputSource(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    private final InputType type;
    private final InputSource source;
    private final Map<String, Object> attributes;
    private final String userId;
    private final String userRole;
    private final String sessionId;
    private final String ipAddress;
    private final String userAgent;
    private final boolean strictValidation;
    private final int maxLength;
    private final String fieldName;
    
    private ValidationContext(Builder builder) {
        this.type = builder.type;
        this.source = builder.source;
        this.attributes = new HashMap<>(builder.attributes);
        this.userId = builder.userId;
        this.userRole = builder.userRole;
        this.sessionId = builder.sessionId;
        this.ipAddress = builder.ipAddress;
        this.userAgent = builder.userAgent;
        this.strictValidation = builder.strictValidation;
        this.maxLength = builder.maxLength;
        this.fieldName = builder.fieldName;
    }
    
    public static Builder builder(InputType type) {
        return new Builder(type);
    }
    
    public static class Builder {
        private final InputType type;
        private InputSource source = InputSource.USER_INPUT;
        private Map<String, Object> attributes = new HashMap<>();
        private String userId;
        private String userRole;
        private String sessionId;
        private String ipAddress;
        private String userAgent;
        private boolean strictValidation = false;
        private int maxLength = 1000;
        private String fieldName;
        
        private Builder(InputType type) {
            this.type = type;
        }
        
        public Builder source(InputSource source) {
            this.source = source;
            return this;
        }
        
        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }
        
        public Builder userRole(String userRole) {
            this.userRole = userRole;
            return this;
        }
        
        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }
        
        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }
        
        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }
        
        public Builder strictValidation(boolean strict) {
            this.strictValidation = strict;
            return this;
        }
        
        public Builder maxLength(int maxLength) {
            this.maxLength = maxLength;
            return this;
        }
        
        public Builder fieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }
        
        public Builder attribute(String key, Object value) {
            this.attributes.put(key, value);
            return this;
        }
        
        public ValidationContext build() {
            return new ValidationContext(this);
        }
    }
    
    // Getters
    public InputType getType() {
        return type;
    }
    
    public InputSource getSource() {
        return source;
    }
    
    public Map<String, Object> getAttributes() {
        return new HashMap<>(attributes);
    }
    
    public Object getAttribute(String key) {
        return attributes.get(key);
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getUserRole() {
        return userRole;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public boolean isStrictValidation() {
        return strictValidation;
    }
    
    public int getMaxLength() {
        return maxLength;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    // Utility methods
    public boolean hasUserContext() {
        return userId != null && !userId.trim().isEmpty();
    }
    
    public boolean hasSessionContext() {
        return sessionId != null && !sessionId.trim().isEmpty();
    }
    
    public boolean hasNetworkContext() {
        return ipAddress != null && !ipAddress.trim().isEmpty();
    }
    
    public boolean isHighRiskSource() {
        return source == InputSource.EXTERNAL_API || 
               source == InputSource.FILE_UPLOAD ||
               source == InputSource.WEB_FORM;
    }
    
    public boolean requiresEnhancedValidation() {
        return strictValidation || isHighRiskSource() || 
               type == InputType.PASSWORD || type == InputType.API_KEY;
    }
    
    public boolean isAdminUser() {
        return "ADMIN".equalsIgnoreCase(userRole) || 
               "BACKUP_ADMIN".equalsIgnoreCase(userRole);
    }
    
    /**
     * Create a simple validation context for basic validation needs.
     */
    public static ValidationContext simple(InputType type) {
        return builder(type).build();
    }
    
    /**
     * Create a web validation context with user and session information.
     */
    public static ValidationContext web(InputType type, String userId, String sessionId, String ipAddress) {
        return builder(type)
            .source(InputSource.WEB_FORM)
            .userId(userId)
            .sessionId(sessionId)
            .ipAddress(ipAddress)
            .build();
    }
    
    /**
     * Create an API validation context with authentication information.
     */
    public static ValidationContext api(InputType type, String userId, String userRole, String ipAddress) {
        return builder(type)
            .source(InputSource.REST_API)
            .userId(userId)
            .userRole(userRole)
            .ipAddress(ipAddress)
            .strictValidation(true)
            .build();
    }
    
    @Override
    public String toString() {
        return String.format("ValidationContext{type=%s, source=%s, user=%s, strict=%s}", 
            type, source, userId != null ? userId : "anonymous", strictValidation);
    }
}