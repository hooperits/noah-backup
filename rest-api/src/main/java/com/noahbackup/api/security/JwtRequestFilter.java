package com.noahbackup.api.security;

import com.noahbackup.auth.SecureCodingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT Request Filter for processing JWT tokens in HTTP requests.
 * 
 * This filter:
 * - Extracts JWT tokens from Authorization headers
 * - Validates tokens using JwtUtil
 * - Sets up Spring Security authentication context
 * - Handles token expiration and validation errors gracefully
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);
    
    private final JwtUtil jwtUtil;
    
    @Autowired
    public JwtRequestFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain chain) throws ServletException, IOException {
        
        final String requestTokenHeader = request.getHeader("Authorization");
        final String requestURI = request.getRequestURI();
        
        String username = null;
        String jwtToken = null;
        
        // Extract JWT token from Authorization header
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = jwtUtil.extractTokenFromHeader(requestTokenHeader);
            
            if (jwtToken != null) {
                try {
                    username = jwtUtil.extractUsername(jwtToken);
                } catch (Exception e) {
                    SecureCodingUtils.safeLog(logger, "WARN", "Unable to extract username from JWT token for request to {}", requestURI);
                }
            }
        }
        
        // Validate token and set up authentication context
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                if (jwtUtil.validateToken(jwtToken)) {
                    // Extract roles from token
                    String[] roles = jwtUtil.extractRoles(jwtToken);
                    
                    // Convert roles to Spring Security authorities
                    List<SimpleGrantedAuthority> authorities = Arrays.stream(roles)
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList());
                    
                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    SecureCodingUtils.safeLog(logger, "DEBUG", "JWT authentication successful for user: {} accessing {}", username, requestURI);
                } else {
                    SecureCodingUtils.safeLog(logger, "WARN", "JWT token validation failed for request to {}", requestURI);
                }
            } catch (Exception e) {
                SecureCodingUtils.safeLog(logger, "WARN", "JWT token processing failed for request to {}", requestURI);
            }
        }
        
        chain.doFilter(request, response);
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        
        // Skip JWT processing for public endpoints
        return path.startsWith("/api/v1/auth/login") ||
               path.startsWith("/api/v1/auth/status") ||
               path.startsWith("/actuator/health") ||
               path.startsWith("/actuator/info") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs");
    }
}