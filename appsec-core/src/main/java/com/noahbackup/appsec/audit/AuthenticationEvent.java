package com.noahbackup.appsec.audit;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Authentication event data for audit logging.
 */
public class AuthenticationEvent {
    
    public enum Type {
        LOGIN_SUCCESS, LOGIN_FAILURE, LOGOUT, PASSWORD_CHANGE, 
        TOKEN_REFRESH, SESSION_TIMEOUT, ACCOUNT_LOCKED
    }
    
    private final Type type;
    private final String userId;
    private final String username;
    private final String sessionId;
    private final String ipAddress;
    private final String userAgent;
    private final boolean success;
    private final String message;
    private final Map<String, Object> details;
    private final LocalDateTime timestamp;
    
    public AuthenticationEvent(Type type, String userId, String username, 
                             String sessionId, String ipAddress, String userAgent,
                             boolean success, String message, Map<String, Object> details) {
        this.type = type;
        this.userId = userId;
        this.username = username;
        this.sessionId = sessionId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.success = success;
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters
    public Type getType() { return type; }
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getSessionId() { return sessionId; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Map<String, Object> getDetails() { return details; }
    public LocalDateTime getTimestamp() { return timestamp; }
}