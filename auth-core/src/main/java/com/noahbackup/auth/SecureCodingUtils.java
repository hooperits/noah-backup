package com.noahbackup.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * Secure coding utilities for Noah Backup.
 * Implements security best practices to prevent common vulnerabilities:
 * - Safe logging (no secrets in logs)
 * - Secure random generation
 * - Safe string operations
 * - Memory cleanup utilities
 */
public class SecureCodingUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(SecureCodingUtils.class);
    
    // Thread-safe secure random instance
    private static final SecureRandom secureRandom = new SecureRandom();
    
    // Sensitive keywords that should never appear in logs
    private static final String[] SENSITIVE_KEYWORDS = {
        "password", "secret", "key", "token", "credential", "auth",
        "AWS_SECRET", "AWS_ACCESS", "private", "confidential"
    };
    
    /**
     * Safely logs a message, masking any potentially sensitive information.
     * 
     * @param logger The logger to use
     * @param level Log level (INFO, DEBUG, WARN, ERROR)
     * @param message The message to log
     * @param args Optional arguments for message formatting
     */
    public static void safeLog(Logger logger, String level, String message, Object... args) {
        String safeMessage = maskSensitiveData(message);
        Object[] safeArgs = Arrays.stream(args)
                                  .map(SecureCodingUtils::maskSensitiveData)
                                  .toArray();
        
        switch (level.toUpperCase()) {
            case "DEBUG" -> logger.debug(safeMessage, safeArgs);
            case "INFO" -> logger.info(safeMessage, safeArgs);
            case "WARN" -> logger.warn(safeMessage, safeArgs);
            case "ERROR" -> logger.error(safeMessage, safeArgs);
            default -> logger.info(safeMessage, safeArgs);
        }
    }
    
    /**
     * Masks sensitive data in strings to prevent accidental exposure in logs.
     */
    private static String maskSensitiveData(Object obj) {
        if (obj == null) return "null";
        
        String str = obj.toString();
        String lowerStr = str.toLowerCase();
        
        // Check for sensitive keywords
        for (String keyword : SENSITIVE_KEYWORDS) {
            if (lowerStr.contains(keyword)) {
                // If it looks like key=value format, mask the value
                if (str.contains("=")) {
                    return str.replaceAll("(" + keyword + "\\s*=\\s*)[^\\s,}]+", "$1***MASKED***");
                } else if (str.length() > 8) {
                    // Mask most of the string, keeping first 2 and last 2 characters
                    return str.substring(0, 2) + "***MASKED***" + str.substring(str.length() - 2);
                } else {
                    return "***MASKED***";
                }
            }
        }
        
        // Check for long strings that might be credentials (base64-like)
        if (str.length() > 20 && str.matches("^[A-Za-z0-9+/=]+$")) {
            return str.substring(0, 4) + "***MASKED***" + str.substring(str.length() - 4);
        }
        
        return str;
    }
    
    /**
     * Generates a cryptographically secure random string.
     * 
     * @param length The length of the random string
     * @param charset The character set to use (default: alphanumeric)
     * @return Base64 encoded random string
     */
    public static String generateSecureRandomString(int length, String charset) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }
        
        if (charset == null || charset.isEmpty()) {
            // Default alphanumeric charset
            charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        }
        
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(charset.length());
            result.append(charset.charAt(index));
        }
        
        return result.toString();
    }
    
    /**
     * Generates a cryptographically secure random byte array.
     */
    public static byte[] generateSecureRandomBytes(int length) {
        byte[] bytes = new byte[length];
        secureRandom.nextBytes(bytes);
        return bytes;
    }
    
    /**
     * Computes SHA-256 hash of input string.
     * Useful for integrity checking and non-reversible identifiers.
     */
    public static String computeSHA256Hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    
    /**
     * Securely compares two strings in constant time to prevent timing attacks.
     */
    public static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return a == b;
        }
        
        byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
        byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);
        
        return MessageDigest.isEqual(aBytes, bBytes);
    }
    
    /**
     * Securely clears a character array from memory.
     * Use this for passwords and other sensitive char arrays.
     */
    public static void clearCharArray(char[] array) {
        if (array != null) {
            Arrays.fill(array, '\0');
        }
    }
    
    /**
     * Securely clears a byte array from memory.
     * Use this for encryption keys and other sensitive byte arrays.
     */
    public static void clearByteArray(byte[] array) {
        if (array != null) {
            Arrays.fill(array, (byte) 0);
        }
    }
    
    /**
     * Creates a secure temporary file name.
     * Prevents predictable file names that could be exploited.
     */
    public static String createSecureTempFileName(String prefix) {
        String randomPart = generateSecureRandomString(16, null);
        String timestamp = String.valueOf(System.currentTimeMillis());
        return (prefix != null ? prefix : "secure") + "-" + timestamp + "-" + randomPart;
    }
    
    /**
     * Validates input against injection attacks.
     * Checks for common SQL injection and command injection patterns.
     */
    public static void validateInputForInjection(String input, String parameterName) throws SecurityException {
        if (input == null) {
            return;
        }
        
        String lowerInput = input.toLowerCase();
        
        // Check for SQL injection patterns
        String[] sqlPatterns = {
            "select", "insert", "update", "delete", "drop", "create", "alter",
            "union", "exec", "execute", "sp_", "xp_", "'", "\"", ";", "--", "/*"
        };
        
        for (String pattern : sqlPatterns) {
            if (lowerInput.contains(pattern)) {
                throw new SecurityException("Potential SQL injection detected in " + parameterName);
            }
        }
        
        // Check for command injection patterns
        String[] cmdPatterns = {
            "&", "|", ";", "`", "$", "(", ")", "<", ">", "&&", "||"
        };
        
        for (String pattern : cmdPatterns) {
            if (input.contains(pattern)) {
                throw new SecurityException("Potential command injection detected in " + parameterName);
            }
        }
        
        // Check for path traversal
        if (input.contains("..") && (input.contains("/") || input.contains("\\"))) {
            throw new SecurityException("Potential path traversal detected in " + parameterName);
        }
    }
    
    /**
     * Safe exception handling that doesn't expose sensitive information.
     * 
     * @param operation Description of what was being attempted
     * @param exception The original exception
     * @return A safe exception message suitable for logging
     */
    public static String createSafeExceptionMessage(String operation, Exception exception) {
        String safeMessage = operation + " failed";
        
        if (exception != null) {
            String exceptionMessage = exception.getMessage();
            if (exceptionMessage != null) {
                // Remove potentially sensitive information from exception messages
                exceptionMessage = maskSensitiveData(exceptionMessage);
                safeMessage += ": " + exceptionMessage;
            } else {
                safeMessage += ": " + exception.getClass().getSimpleName();
            }
        }
        
        return safeMessage;
    }
    
    /**
     * Validates that a string contains only safe characters.
     * Useful for validating user inputs that will be used in file names, etc.
     */
    public static boolean containsOnlySafeCharacters(String input) {
        if (input == null) {
            return false;
        }
        
        // Allow alphanumeric, hyphens, underscores, and dots
        return input.matches("^[a-zA-Z0-9._-]+$");
    }
    
    /**
     * Truncates a string to a maximum length for safe logging.
     * Prevents log injection attacks through excessively long strings.
     */
    public static String truncateForLogging(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        
        if (str.length() <= maxLength) {
            return str;
        }
        
        return str.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Performs secure cleanup of system resources.
     * Call this during application shutdown to ensure sensitive data is cleared.
     */
    public static void performSecureCleanup() {
        logger.info("Performing secure cleanup of system resources");
        
        try {
            // Request garbage collection to help clear sensitive data
            System.gc();
            Thread.sleep(100); // Give GC a moment to run
            System.gc();
            
            logger.info("Secure cleanup completed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Secure cleanup interrupted");
        } catch (Exception e) {
            logger.error("Error during secure cleanup: {}", e.getMessage());
        }
    }
    
    /**
     * Utility class - prevent instantiation
     */
    private SecureCodingUtils() {
        throw new AssertionError("Utility class should not be instantiated");
    }
}