package com.noahbackup.api.controller;

import com.noahbackup.api.dto.LoginRequest;
import com.noahbackup.api.dto.LoginResponse;
import com.noahbackup.api.security.JwtUtil;
import com.noahbackup.auth.SecureCodingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication controller for JWT-based API access.
 * 
 * Provides secure endpoints for:
 * - User login and JWT token generation
 * - Token refresh
 * - Authentication status checks
 * 
 * Security Features:
 * - Secure credential validation
 * - Rate limiting (future enhancement)
 * - Audit logging
 * - Safe error messages
 */
@RestController
@RequestMapping("/api/v1/auth")
@Validated
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8081"}, maxAge = 3600)
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    
    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }
    
    /**
     * POST /api/v1/auth/login
     * Authenticates user and returns JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        SecureCodingUtils.safeLog(logger, "INFO", "Login attempt for user: {}", loginRequest.getUsername());
        
        try {
            // Validate credentials
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // Extract roles from user details
            String[] roles = userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toArray(String[]::new);
            
            // Generate JWT token
            String token = jwtUtil.generateToken(userDetails.getUsername(), roles);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());
            
            LoginResponse response = LoginResponse.builder()
                .success(true)
                .token(token)
                .refreshToken(refreshToken)
                .username(userDetails.getUsername())
                .roles(roles)
                .expiresIn(jwtUtil.getRemainingTime(token))
                .timestamp(LocalDateTime.now())
                .build();
            
            SecureCodingUtils.safeLog(logger, "INFO", "Login successful for user: {}", loginRequest.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            SecureCodingUtils.safeLog(logger, "WARN", "Failed login attempt for user: {}", loginRequest.getUsername());
            
            LoginResponse response = LoginResponse.builder()
                .success(false)
                .message("Invalid credentials")
                .timestamp(LocalDateTime.now())
                .build();
            
            return ResponseEntity.status(401).body(response);
            
        } catch (Exception e) {
            String safeMessage = SecureCodingUtils.createSafeExceptionMessage("Authentication", e);
            SecureCodingUtils.safeLog(logger, "ERROR", safeMessage);
            
            LoginResponse response = LoginResponse.builder()
                .success(false)
                .message("Authentication failed")
                .timestamp(LocalDateTime.now())
                .build();
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * POST /api/v1/auth/refresh
     * Refreshes an expired or soon-to-expire JWT token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestHeader("Authorization") String authHeader) {
        SecureCodingUtils.safeLog(logger, "DEBUG", "Token refresh requested");
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        
        try {
            String refreshToken = jwtUtil.extractTokenFromHeader(authHeader);
            if (refreshToken == null) {
                response.put("success", false);
                response.put("message", "Missing or invalid refresh token");
                return ResponseEntity.status(400).body(response);
            }
            
            // Validate refresh token
            if (!jwtUtil.validateToken(refreshToken)) {
                response.put("success", false);
                response.put("message", "Invalid or expired refresh token");
                return ResponseEntity.status(401).body(response);
            }
            
            String username = jwtUtil.extractUsername(refreshToken);
            String[] roles = jwtUtil.extractRoles(refreshToken);
            
            // Generate new tokens
            String newToken = jwtUtil.generateToken(username, roles);
            String newRefreshToken = jwtUtil.generateRefreshToken(username);
            
            response.put("success", true);
            response.put("token", newToken);
            response.put("refreshToken", newRefreshToken);
            response.put("expiresIn", jwtUtil.getRemainingTime(newToken));
            
            SecureCodingUtils.safeLog(logger, "INFO", "Token refreshed successfully for user: {}", username);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            String safeMessage = SecureCodingUtils.createSafeExceptionMessage("Token refresh", e);
            SecureCodingUtils.safeLog(logger, "ERROR", safeMessage);
            
            response.put("success", false);
            response.put("message", "Token refresh failed");
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * GET /api/v1/auth/me
     * Returns current user information from JWT token.
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null || !jwtUtil.validateToken(token)) {
                response.put("authenticated", false);
                response.put("message", "Invalid or missing token");
                return ResponseEntity.status(401).body(response);
            }
            
            String username = jwtUtil.extractUsername(token);
            String[] roles = jwtUtil.extractRoles(token);
            long expiresIn = jwtUtil.getRemainingTime(token);
            
            response.put("authenticated", true);
            response.put("username", username);
            response.put("roles", roles);
            response.put("expiresIn", expiresIn);
            response.put("tokenExpiring", expiresIn < 300000); // Less than 5 minutes
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            String safeMessage = SecureCodingUtils.createSafeExceptionMessage("User info retrieval", e);
            SecureCodingUtils.safeLog(logger, "ERROR", safeMessage);
            
            response.put("authenticated", false);
            response.put("message", "Failed to retrieve user information");
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * POST /api/v1/auth/logout
     * Logs out the user (client-side token removal).
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("success", true);
        response.put("message", "Logout successful - please remove token from client");
        
        // Note: JWT tokens are stateless, so server-side logout is typically
        // handled by token blacklisting (not implemented in this basic version)
        
        SecureCodingUtils.safeLog(logger, "DEBUG", "User logout requested");
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/v1/auth/status
     * Returns authentication system status.
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getAuthStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("timestamp", LocalDateTime.now());
        status.put("authenticationEnabled", true);
        status.put("jwtEnabled", true);
        status.put("refreshTokenEnabled", true);
        
        // System status
        status.put("status", "UP");
        
        return ResponseEntity.ok(status);
    }
}