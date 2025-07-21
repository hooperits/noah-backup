package com.noahbackup.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Security validation utilities for Noah Backup.
 * Implements security best practices and OWASP guidelines for:
 * - Input validation and sanitization
 * - Path traversal prevention
 * - File extension validation
 * - Configuration security checks
 */
public class SecurityValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityValidator.class);
    
    // File extension allowlists
    private static final Set<String> ALLOWED_BACKUP_EXTENSIONS = Set.of(
        ".txt", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".pdf",
        ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".tiff", ".svg",
        ".mp3", ".mp4", ".avi", ".mov", ".wav", ".flac",
        ".zip", ".rar", ".7z", ".tar", ".gz", ".bz2",
        ".json", ".xml", ".csv", ".log", ".config", ".properties",
        ".pst", ".ost", ".mdb", ".accdb", ".sql", ".db", ".sqlite"
    );
    
    private static final Set<String> DANGEROUS_EXTENSIONS = Set.of(
        ".exe", ".bat", ".cmd", ".com", ".scr", ".pif", ".vbs", ".js",
        ".jar", ".war", ".ear", ".class", ".dll", ".so", ".dylib"
    );
    
    // Path validation patterns
    private static final Pattern VALID_PATH_PATTERN = Pattern.compile("^[a-zA-Z]:[\\\\]([\\w\\s\\-\\.]+[\\\\]?)*$");
    private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile("\\.\\.[\\\\/]");
    
    // Input validation patterns
    private static final Pattern BUCKET_NAME_PATTERN = Pattern.compile("^[a-z0-9][a-z0-9\\-]*[a-z0-9]$");
    private static final Pattern S3_KEY_PATTERN = Pattern.compile("^[\\w\\-\\./]+$");
    
    // Security configuration
    private static final int MAX_PATH_LENGTH = 260; // Windows MAX_PATH
    private static final int MAX_FILENAME_LENGTH = 255;
    private static final long MAX_FILE_SIZE = 50L * 1024 * 1024 * 1024; // 50 GB
    
    /**
     * Validates and sanitizes a file path for backup operations.
     * 
     * @param path The file path to validate
     * @return Sanitized path if valid
     * @throws SecurityException if path is invalid or potentially dangerous
     */
    public static String validateAndSanitizePath(String path) throws SecurityException {
        if (path == null || path.trim().isEmpty()) {
            throw new SecurityException("Path cannot be null or empty");
        }
        
        String sanitizedPath = path.trim();
        
        // Check path length
        if (sanitizedPath.length() > MAX_PATH_LENGTH) {
            throw new SecurityException("Path too long: maximum " + MAX_PATH_LENGTH + " characters allowed");
        }
        
        // Check for path traversal attempts
        if (PATH_TRAVERSAL_PATTERN.matcher(sanitizedPath).find()) {
            throw new SecurityException("Path traversal detected in path: " + path);
        }
        
        // Normalize path separators (Windows)
        sanitizedPath = sanitizedPath.replace("/", "\\");
        
        // Validate Windows path format
        if (!VALID_PATH_PATTERN.matcher(sanitizedPath).matches()) {
            throw new SecurityException("Invalid path format: " + path);
        }
        
        // Check for null bytes (security vulnerability)
        if (sanitizedPath.contains("\0")) {
            throw new SecurityException("Null byte detected in path");
        }
        
        // Validate file extension
        validateFileExtension(sanitizedPath);
        
        logger.debug("Path validated and sanitized: {}", sanitizedPath);
        return sanitizedPath;
    }
    
    /**
     * Validates file extension against security policies.
     */
    private static void validateFileExtension(String path) throws SecurityException {
        Path pathObj = Paths.get(path);
        String filename = pathObj.getFileName().toString().toLowerCase();
        
        // Check filename length
        if (filename.length() > MAX_FILENAME_LENGTH) {
            throw new SecurityException("Filename too long: maximum " + MAX_FILENAME_LENGTH + " characters allowed");
        }
        
        // Extract extension
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0 && lastDot < filename.length() - 1) {
            String extension = filename.substring(lastDot);
            
            // Check for dangerous extensions
            if (DANGEROUS_EXTENSIONS.contains(extension)) {
                throw new SecurityException("Dangerous file extension detected: " + extension);
            }
            
            // Log warning for unknown extensions
            if (!ALLOWED_BACKUP_EXTENSIONS.contains(extension)) {
                logger.warn("Unknown file extension for backup: {} (file: {})", extension, filename);
            }
        }
    }
    
    /**
     * Validates S3 bucket name according to AWS naming rules.
     */
    public static String validateS3BucketName(String bucketName) throws SecurityException {
        if (bucketName == null || bucketName.trim().isEmpty()) {
            throw new SecurityException("S3 bucket name cannot be null or empty");
        }
        
        String sanitized = bucketName.trim().toLowerCase();
        
        // Check length constraints
        if (sanitized.length() < 3 || sanitized.length() > 63) {
            throw new SecurityException("S3 bucket name must be between 3 and 63 characters");
        }
        
        // Validate format
        if (!BUCKET_NAME_PATTERN.matcher(sanitized).matches()) {
            throw new SecurityException("Invalid S3 bucket name format: " + bucketName);
        }
        
        // Additional AWS restrictions
        if (sanitized.startsWith("xn--") || sanitized.endsWith("-s3alias")) {
            throw new SecurityException("S3 bucket name uses reserved prefix/suffix");
        }
        
        logger.debug("S3 bucket name validated: {}", sanitized);
        return sanitized;
    }
    
    /**
     * Validates and sanitizes S3 object key.
     */
    public static String validateS3Key(String key) throws SecurityException {
        if (key == null || key.trim().isEmpty()) {
            throw new SecurityException("S3 key cannot be null or empty");
        }
        
        String sanitized = key.trim();
        
        // Check length (S3 max is 1024 bytes)
        if (sanitized.getBytes().length > 1024) {
            throw new SecurityException("S3 key too long: maximum 1024 bytes allowed");
        }
        
        // Check for invalid characters
        if (!S3_KEY_PATTERN.matcher(sanitized).matches()) {
            throw new SecurityException("S3 key contains invalid characters: " + key);
        }
        
        // Prevent leading/trailing slashes that could cause issues
        if (sanitized.startsWith("/") || sanitized.endsWith("/")) {
            sanitized = sanitized.replaceAll("^/+", "").replaceAll("/+$", "");
        }
        
        // Normalize multiple slashes
        sanitized = sanitized.replaceAll("/+", "/");
        
        logger.debug("S3 key validated and sanitized: {}", sanitized);
        return sanitized;
    }
    
    /**
     * Validates file size against security limits.
     */
    public static void validateFileSize(File file) throws SecurityException {
        if (file == null || !file.exists()) {
            throw new SecurityException("File does not exist for size validation");
        }
        
        long size = file.length();
        if (size > MAX_FILE_SIZE) {
            throw new SecurityException("File too large: " + formatFileSize(size) + 
                                      " exceeds maximum " + formatFileSize(MAX_FILE_SIZE));
        }
        
        if (size == 0) {
            logger.warn("Empty file detected: {}", file.getPath());
        }
    }
    
    /**
     * Validates directory for backup operations.
     */
    public static ValidationResult validateDirectory(String directoryPath) {
        List<String> issues = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        try {
            String sanitizedPath = validateAndSanitizePath(directoryPath);
            File directory = new File(sanitizedPath);
            
            if (!directory.exists()) {
                issues.add("Directory does not exist: " + directoryPath);
                return new ValidationResult(false, issues, warnings);
            }
            
            if (!directory.isDirectory()) {
                issues.add("Path is not a directory: " + directoryPath);
                return new ValidationResult(false, issues, warnings);
            }
            
            if (!directory.canRead()) {
                issues.add("Directory is not readable: " + directoryPath);
                return new ValidationResult(false, issues, warnings);
            }
            
            // Check directory contents
            File[] files = directory.listFiles();
            if (files == null || files.length == 0) {
                warnings.add("Directory is empty: " + directoryPath);
            } else {
                int fileCount = 0;
                long totalSize = 0;
                
                for (File file : files) {
                    if (file.isFile()) {
                        fileCount++;
                        totalSize += file.length();
                        
                        // Validate individual files
                        try {
                            validateFileSize(file);
                        } catch (SecurityException e) {
                            warnings.add("Large file detected: " + file.getName() + " - " + e.getMessage());
                        }
                    }
                }
                
                if (fileCount > 10000) {
                    warnings.add("Large number of files detected: " + fileCount + " files");
                }
                
                if (totalSize > 100L * 1024 * 1024 * 1024) { // 100 GB
                    warnings.add("Large directory size detected: " + formatFileSize(totalSize));
                }
            }
            
        } catch (SecurityException e) {
            issues.add("Security validation failed: " + e.getMessage());
            return new ValidationResult(false, issues, warnings);
        }
        
        return new ValidationResult(true, issues, warnings);
    }
    
    /**
     * Validates system configuration for security compliance.
     */
    public static ValidationResult validateSystemConfiguration() {
        List<String> issues = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        // Check Java security properties
        String tmpDir = System.getProperty("java.io.tmpdir");
        if (tmpDir == null || tmpDir.isEmpty()) {
            warnings.add("Temporary directory not configured");
        }
        
        // Check file permissions on temp directory
        File tempDir = new File(tmpDir);
        if (!tempDir.canWrite()) {
            issues.add("Cannot write to temporary directory: " + tmpDir);
        }
        
        // Check for debug flags
        String debugFlag = System.getProperty("noah.backup.debug");
        if ("true".equals(debugFlag)) {
            warnings.add("Debug mode is enabled - not recommended for production");
        }
        
        // Check encryption capabilities
        try {
            new AESEncryption();
            logger.debug("AES encryption available");
        } catch (Exception e) {
            issues.add("AES encryption not available: " + e.getMessage());
        }
        
        return new ValidationResult(issues.isEmpty(), issues, warnings);
    }
    
    /**
     * Formats file size in human-readable format.
     */
    private static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    /**
     * Result class for validation operations.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> issues;
        private final List<String> warnings;
        
        public ValidationResult(boolean valid, List<String> issues, List<String> warnings) {
            this.valid = valid;
            this.issues = new ArrayList<>(issues);
            this.warnings = new ArrayList<>(warnings);
        }
        
        public boolean isValid() { return valid; }
        public List<String> getIssues() { return new ArrayList<>(issues); }
        public List<String> getWarnings() { return new ArrayList<>(warnings); }
        public boolean hasWarnings() { return !warnings.isEmpty(); }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ValidationResult{valid=").append(valid);
            if (!issues.isEmpty()) {
                sb.append(", issues=").append(issues);
            }
            if (!warnings.isEmpty()) {
                sb.append(", warnings=").append(warnings);
            }
            sb.append("}");
            return sb.toString();
        }
    }
}