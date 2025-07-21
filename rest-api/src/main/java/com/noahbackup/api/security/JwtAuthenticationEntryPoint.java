package com.noahbackup.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noahbackup.auth.SecureCodingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Authentication Entry Point for handling authentication failures.
 * 
 * This component is called when a user tries to access a secured resource
 * without proper authentication. It returns a JSON response with appropriate
 * HTTP status codes instead of redirecting to a login page.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException authException) throws IOException {
        
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        SecureCodingUtils.safeLog(logger, "WARN", "Unauthorized access attempt to {} {}", method, requestURI);
        
        // Set response status and content type
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Create error response
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", 401);
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", "Authentication required to access this resource");
        errorResponse.put("path", requestURI);
        
        // Add additional context based on the request
        if (requestURI.startsWith("/api/v1/backup")) {
            errorResponse.put("hint", "Please login via /api/v1/auth/login to get a valid JWT token");
        } else if (requestURI.startsWith("/api/v1/config")) {
            errorResponse.put("hint", "Configuration endpoints require authentication");
        } else {
            errorResponse.put("hint", "Include 'Authorization: Bearer <token>' header with valid JWT token");
        }
        
        // Security headers
        response.setHeader("WWW-Authenticate", "Bearer");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        
        // Write JSON response
        try {
            String jsonResponse = objectMapper.writeValueAsString(errorResponse);
            response.getWriter().write(jsonResponse);
        } catch (Exception e) {
            SecureCodingUtils.safeLog(logger, "ERROR", "Failed to write authentication error response");
            response.getWriter().write("{\"error\":\"Authentication required\"}");
        }
    }
}