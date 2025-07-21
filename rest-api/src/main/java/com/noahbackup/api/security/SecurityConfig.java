package com.noahbackup.api.security;

import com.noahbackup.auth.SecureCodingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security configuration for Noah Backup REST API.
 * 
 * Security Features:
 * - JWT-based stateless authentication
 * - Role-based access control (RBAC)
 * - CORS configuration for web clients
 * - Method-level security annotations
 * - Secure password encoding
 * - Rate limiting (future enhancement)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtRequestFilter jwtRequestFilter;
    
    @Autowired
    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                         JwtRequestFilter jwtRequestFilter) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtRequestFilter = jwtRequestFilter;
    }
    
    /**
     * Configure HTTP security, endpoints, and JWT filter.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for stateless API
            .csrf(csrf -> csrf.disable())
            
            // Configure CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Configure session management (stateless)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configure authentication entry point
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            
            // Configure endpoint authorization
            .authorizeHttpRequests(authz -> authz
                // Public endpoints (no authentication required)
                .requestMatchers("/api/v1/auth/login").permitAll()
                .requestMatchers("/api/v1/auth/status").permitAll()
                
                // Actuator endpoints (health checks)
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/info").permitAll()
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                
                // API documentation (if enabled)
                .requestMatchers("/swagger-ui/**").hasRole("ADMIN")
                .requestMatchers("/v3/api-docs/**").hasRole("ADMIN")
                
                // Backup operations - require authentication
                .requestMatchers("/api/v1/backup/**").authenticated()
                .requestMatchers("/api/v1/config/**").authenticated()
                .requestMatchers("/api/v1/auth/me").authenticated()
                .requestMatchers("/api/v1/auth/refresh").authenticated()
                .requestMatchers("/api/v1/auth/logout").authenticated()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            );
        
        // Add JWT filter before username/password authentication filter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        
        SecureCodingUtils.safeLog(logger, "INFO", "Spring Security configuration applied successfully");
        
        return http.build();
    }
    
    /**
     * Configure CORS for cross-origin requests.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow specific origins (configure based on your frontend)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",  // React development server
            "http://localhost:8081",  // Alternative frontend port
            "http://127.0.0.1:3000",
            "http://127.0.0.1:8081"
        ));
        
        // Allow specific methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));
        
        // Allow specific headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Requested-With", "Accept"
        ));
        
        // Allow credentials
        configuration.setAllowCredentials(true);
        
        // Cache preflight requests
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        
        return source;
    }
    
    /**
     * Password encoder for secure password hashing.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Strength 12 for good security vs performance
    }
    
    /**
     * Authentication manager bean.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    /**
     * In-memory user details service for demonstration.
     * In production, this should be replaced with a database-backed service.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // Create demo users with different roles
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("admin123!"))
            .roles("ADMIN", "BACKUP_ADMIN", "BACKUP_USER", "BACKUP_VIEWER")
            .build();
        
        UserDetails backupUser = User.builder()
            .username("backup")
            .password(passwordEncoder().encode("backup123!"))
            .roles("BACKUP_USER", "BACKUP_VIEWER")
            .build();
        
        UserDetails viewer = User.builder()
            .username("viewer")
            .password(passwordEncoder().encode("viewer123!"))
            .roles("BACKUP_VIEWER")
            .build();
        
        SecureCodingUtils.safeLog(logger, "INFO", "Configured in-memory users: admin, backup, viewer");
        
        return new InMemoryUserDetailsManager(admin, backupUser, viewer);
    }
}