package com.noahbackup.api.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for login responses.
 */
public class LoginResponse {
    
    private boolean success;
    private String token;
    private String refreshToken;
    private String username;
    private String[] roles;
    private long expiresIn;
    private String message;
    private LocalDateTime timestamp;
    
    // Builder pattern for easier construction
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final LoginResponse response = new LoginResponse();
        
        public Builder success(boolean success) { response.success = success; return this; }
        public Builder token(String token) { response.token = token; return this; }
        public Builder refreshToken(String refreshToken) { response.refreshToken = refreshToken; return this; }
        public Builder username(String username) { response.username = username; return this; }
        public Builder roles(String[] roles) { response.roles = roles; return this; }
        public Builder expiresIn(long expiresIn) { response.expiresIn = expiresIn; return this; }
        public Builder message(String message) { response.message = message; return this; }
        public Builder timestamp(LocalDateTime timestamp) { response.timestamp = timestamp; return this; }
        
        public LoginResponse build() { return response; }
    }
    
    // Getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String[] getRoles() { return roles; }
    public void setRoles(String[] roles) { this.roles = roles; }
    
    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}