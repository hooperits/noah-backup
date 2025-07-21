package com.noahbackup.api.security;

import com.noahbackup.auth.SecureCodingUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT (JSON Web Token) utility class for Noah Backup API authentication.
 * 
 * Provides secure token generation, validation, and parsing for API authentication.
 * Uses HMAC-SHA256 signing with configurable secret key and expiration times.
 * 
 * Security Features:
 * - Cryptographically secure token signing
 * - Configurable token expiration
 * - Safe error handling and logging
 * - Role-based claims in tokens
 */
@Component
public class JwtUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    
    // Default values - should be overridden by configuration
    private static final String DEFAULT_SECRET = "NoahBackupDefaultSecretKeyForJWTSigning2024!";
    private static final long DEFAULT_EXPIRATION = 24 * 60 * 60 * 1000; // 24 hours
    
    private final SecretKey secretKey;
    private final long jwtExpiration;
    
    public JwtUtil(@Value("${noah.api.jwt.secret:" + DEFAULT_SECRET + "}") String secret,
                   @Value("${noah.api.jwt.expiration:" + DEFAULT_EXPIRATION + "}") long expiration) {
        
        // Ensure secret is long enough for HMAC-SHA256
        if (secret.length() < 32) {
            SecureCodingUtils.safeLog(logger, "WARN", "JWT secret is shorter than recommended (32+ characters)");
            secret = secret + DEFAULT_SECRET; // Extend if too short
        }
        
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpiration = expiration;
        
        SecureCodingUtils.safeLog(logger, "INFO", "JWT utility initialized with {}ms expiration", expiration);
    }
    
    /**
     * Generates a JWT token for the given username and roles.
     * 
     * @param username The username to include in the token
     * @param roles User roles for authorization
     * @return JWT token string
     */
    public String generateToken(String username, String[] roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        claims.put("iat", new Date(System.currentTimeMillis()));
        
        return createToken(claims, username);
    }
    
    /**
     * Generates a token with additional custom claims.
     */
    public String generateTokenWithClaims(String username, String[] roles, Map<String, Object> additionalClaims) {
        Map<String, Object> claims = new HashMap<>(additionalClaims);
        claims.put("roles", roles);
        claims.put("iat", new Date(System.currentTimeMillis()));
        
        return createToken(claims, username);
    }
    
    /**
     * Creates a JWT token with the specified claims and subject.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        try {
            Date now = new Date(System.currentTimeMillis());
            Date expiration = new Date(System.currentTimeMillis() + jwtExpiration);
            
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(now)
                    .setExpiration(expiration)
                    .setIssuer("noah-backup-api")
                    .signWith(secretKey, SignatureAlgorithm.HS256)
                    .compact();
                    
        } catch (Exception e) {
            String safeMessage = SecureCodingUtils.createSafeExceptionMessage("JWT token creation", e);
            SecureCodingUtils.safeLog(logger, "ERROR", safeMessage);
            throw new RuntimeException("Failed to create JWT token", e);
        }
    }
    
    /**
     * Extracts the username from a JWT token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Extracts the expiration date from a JWT token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Extracts roles from a JWT token.
     */
    @SuppressWarnings("unchecked")
    public String[] extractRoles(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Object rolesObj = claims.get("roles");
            
            if (rolesObj instanceof String[]) {
                return (String[]) rolesObj;
            } else if (rolesObj instanceof java.util.List) {
                java.util.List<String> rolesList = (java.util.List<String>) rolesObj;
                return rolesList.toArray(new String[0]);
            }
            
            return new String[0];
        } catch (Exception e) {
            SecureCodingUtils.safeLog(logger, "WARN", "Failed to extract roles from token");
            return new String[0];
        }
    }
    
    /**
     * Extracts a specific claim from a JWT token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Extracts all claims from a JWT token.
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
                    
        } catch (ExpiredJwtException e) {
            SecureCodingUtils.safeLog(logger, "DEBUG", "JWT token has expired");
            throw e;
        } catch (UnsupportedJwtException e) {
            SecureCodingUtils.safeLog(logger, "WARN", "Unsupported JWT token format");
            throw e;
        } catch (MalformedJwtException e) {
            SecureCodingUtils.safeLog(logger, "WARN", "Malformed JWT token");
            throw e;
        } catch (SignatureException e) {
            SecureCodingUtils.safeLog(logger, "WARN", "Invalid JWT token signature");
            throw e;
        } catch (IllegalArgumentException e) {
            SecureCodingUtils.safeLog(logger, "WARN", "Invalid JWT token");
            throw e;
        }
    }
    
    /**
     * Checks if a JWT token has expired.
     */
    public Boolean isTokenExpired(String token) {
        try {
            final Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            SecureCodingUtils.safeLog(logger, "DEBUG", "Token expiration check failed - treating as expired");
            return true;
        }
    }
    
    /**
     * Validates a JWT token against user details.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            boolean usernameValid = username.equals(userDetails.getUsername());
            boolean tokenNotExpired = !isTokenExpired(token);
            
            if (usernameValid && tokenNotExpired) {
                SecureCodingUtils.safeLog(logger, "DEBUG", "JWT token validated successfully for user");
                return true;
            } else {
                SecureCodingUtils.safeLog(logger, "DEBUG", "JWT token validation failed - username: {}, expired: {}", 
                                        usernameValid, !tokenNotExpired);
                return false;
            }
            
        } catch (Exception e) {
            SecureCodingUtils.safeLog(logger, "WARN", "JWT token validation failed");
            return false;
        }
    }
    
    /**
     * Validates a JWT token without user details (basic validation).
     */
    public Boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            boolean notExpired = !isTokenExpired(token);
            boolean hasSubject = claims.getSubject() != null && !claims.getSubject().trim().isEmpty();
            
            return notExpired && hasSubject;
            
        } catch (Exception e) {
            SecureCodingUtils.safeLog(logger, "DEBUG", "Basic JWT token validation failed");
            return false;
        }
    }
    
    /**
     * Extracts the token from Authorization header.
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
    
    /**
     * Gets remaining time until token expires (in milliseconds).
     */
    public long getRemainingTime(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Creates a refresh token with longer expiration.
     */
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        claims.put("iat", new Date(System.currentTimeMillis()));
        
        try {
            Date now = new Date(System.currentTimeMillis());
            Date expiration = new Date(System.currentTimeMillis() + (jwtExpiration * 7)); // 7x longer
            
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(username)
                    .setIssuedAt(now)
                    .setExpiration(expiration)
                    .setIssuer("noah-backup-api")
                    .signWith(secretKey, SignatureAlgorithm.HS256)
                    .compact();
                    
        } catch (Exception e) {
            String safeMessage = SecureCodingUtils.createSafeExceptionMessage("Refresh token creation", e);
            SecureCodingUtils.safeLog(logger, "ERROR", safeMessage);
            throw new RuntimeException("Failed to create refresh token", e);
        }
    }
}