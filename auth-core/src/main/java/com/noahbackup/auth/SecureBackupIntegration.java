package com.noahbackup.auth;

import com.noahbackup.storage.S3Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Integration class that connects the auth-core security features
 * with the existing Noah Backup modules.
 * 
 * This class demonstrates how to use the security components together:
 * - SecretsManager for credential handling
 * - AESEncryption for sensitive data protection
 * - SecurityValidator for input validation
 * - SecureCodingUtils for safe operations
 */
public class SecureBackupIntegration {
    
    private static final Logger logger = LoggerFactory.getLogger(SecureBackupIntegration.class);
    
    private final SecretsManager secretsManager;
    private final AESEncryption encryption;
    
    public SecureBackupIntegration() throws SecurityException {
        this.secretsManager = SecretsManager.getInstance();
        this.encryption = new AESEncryption();
        
        // Validate system security configuration on startup
        SecurityValidator.ValidationResult sysValidation = SecurityValidator.validateSystemConfiguration();
        if (!sysValidation.isValid()) {
            throw new SecurityException("System security validation failed: " + sysValidation.getIssues());
        }
        
        if (sysValidation.hasWarnings()) {
            sysValidation.getWarnings().forEach(warning -> 
                logger.warn("Security warning: {}", warning)
            );
        }
        
        logger.info("SecureBackupIntegration initialized successfully");
    }
    
    /**
     * Creates a secure S3 configuration using the secrets manager.
     * 
     * @return S3Config with credentials loaded from secure sources
     * @throws SecurityException if required credentials are missing
     */
    public S3Config createSecureS3Config() throws SecurityException {
        String accessKey = secretsManager.getSecret("AWS_ACCESS_KEY_ID");
        String secretKey = secretsManager.getSecret("AWS_SECRET_ACCESS_KEY");
        
        if (accessKey == null || secretKey == null) {
            throw new SecurityException("Missing required S3 credentials");
        }
        
        // Get optional configuration
        String region = secretsManager.getSecret("AWS_REGION");
        if (region == null) region = "us-east-1";
        
        String bucket = secretsManager.getSecret("S3_BUCKET");
        if (bucket == null) bucket = "noah-backup-default";
        
        String endpoint = secretsManager.getSecret("S3_ENDPOINT");
        
        // Validate S3 configuration
        bucket = SecurityValidator.validateS3BucketName(bucket);
        
        S3Config config = new S3Config(accessKey, secretKey, region, bucket);
        if (endpoint != null && !endpoint.trim().isEmpty()) {
            config.setEndpoint(endpoint.trim());
        }
        
        SecureCodingUtils.safeLog(logger, "INFO", "Created secure S3 configuration for bucket: {}", bucket);
        return config;
    }
    
    /**
     * Validates and secures a backup path before processing.
     * 
     * @param backupPath The path to validate and secure
     * @return SecurePathInfo containing validated path and security metadata
     * @throws SecurityException if path is invalid or insecure
     */
    public SecurePathInfo secureBackupPath(String backupPath) throws SecurityException {
        // Input validation
        SecureCodingUtils.validateInputForInjection(backupPath, "backupPath");
        
        // Path validation and sanitization
        String sanitizedPath = SecurityValidator.validateAndSanitizePath(backupPath);
        
        // Directory validation
        SecurityValidator.ValidationResult dirValidation = SecurityValidator.validateDirectory(sanitizedPath);
        
        SecurePathInfo pathInfo = new SecurePathInfo(
            sanitizedPath, 
            dirValidation.isValid(),
            dirValidation.getIssues(),
            dirValidation.getWarnings()
        );
        
        if (!dirValidation.isValid()) {
            throw new SecurityException("Path validation failed: " + dirValidation.getIssues());
        }
        
        if (dirValidation.hasWarnings()) {
            dirValidation.getWarnings().forEach(warning ->
                logger.warn("Path security warning for {}: {}", sanitizedPath, warning)
            );
        }
        
        SecureCodingUtils.safeLog(logger, "DEBUG", "Secured backup path: {}", sanitizedPath);
        return pathInfo;
    }
    
    /**
     * Creates a secure temporary file for backup operations.
     * The file content is automatically encrypted.
     * 
     * @param content Content to store securely
     * @param prefix File prefix for identification
     * @return Encrypted file information
     */
    public AESEncryption.EncryptedFileInfo createSecureTempFile(String content, String prefix) {
        String safePrefix = SecureCodingUtils.createSecureTempFileName(prefix);
        AESEncryption.EncryptedFileInfo fileInfo = encryption.createEncryptedTempFile(content, safePrefix);
        
        SecureCodingUtils.safeLog(logger, "DEBUG", "Created secure temporary file: {}", fileInfo.filePath);
        return fileInfo;
    }
    
    /**
     * Securely processes backup credentials for logging.
     * Masks sensitive information to prevent credential exposure.
     * 
     * @param message Log message
     * @param args Message arguments that may contain sensitive data
     */
    public void secureLog(String level, String message, Object... args) {
        SecureCodingUtils.safeLog(logger, level, message, args);
    }
    
    /**
     * Validates S3 bucket and key for security compliance.
     * 
     * @param bucketName S3 bucket name to validate
     * @param objectKey S3 object key to validate
     * @return SecureS3Info with validated parameters
     * @throws SecurityException if validation fails
     */
    public SecureS3Info validateS3Parameters(String bucketName, String objectKey) throws SecurityException {
        String validatedBucket = SecurityValidator.validateS3BucketName(bucketName);
        String validatedKey = SecurityValidator.validateS3Key(objectKey);
        
        SecureS3Info s3Info = new SecureS3Info(validatedBucket, validatedKey);
        
        SecureCodingUtils.safeLog(logger, "DEBUG", "Validated S3 parameters: bucket={}, key={}", 
                                 validatedBucket, validatedKey);
        return s3Info;
    }
    
    /**
     * Performs secure cleanup of resources.
     * Call this when shutting down or after sensitive operations.
     */
    public void performSecureCleanup() {
        logger.info("Performing secure cleanup of authentication resources");
        
        try {
            encryption.clearKeys();
            secretsManager.clearSecrets();
            SecureCodingUtils.performSecureCleanup();
            
            logger.info("Secure cleanup completed successfully");
        } catch (Exception e) {
            String safeMessage = SecureCodingUtils.createSafeExceptionMessage("Secure cleanup", e);
            logger.error(safeMessage);
        }
    }
    
    /**
     * Data class for secure path information.
     */
    public static class SecurePathInfo {
        public final String sanitizedPath;
        public final boolean isValid;
        public final java.util.List<String> issues;
        public final java.util.List<String> warnings;
        
        public SecurePathInfo(String sanitizedPath, boolean isValid, 
                             java.util.List<String> issues, java.util.List<String> warnings) {
            this.sanitizedPath = sanitizedPath;
            this.isValid = isValid;
            this.issues = new java.util.ArrayList<>(issues);
            this.warnings = new java.util.ArrayList<>(warnings);
        }
        
        @Override
        public String toString() {
            return String.format("SecurePathInfo{path='%s', valid=%s, issues=%d, warnings=%d}", 
                               sanitizedPath, isValid, issues.size(), warnings.size());
        }
    }
    
    /**
     * Data class for secure S3 information.
     */
    public static class SecureS3Info {
        public final String validatedBucket;
        public final String validatedKey;
        
        public SecureS3Info(String validatedBucket, String validatedKey) {
            this.validatedBucket = validatedBucket;
            this.validatedKey = validatedKey;
        }
        
        public String getS3Url() {
            return String.format("s3://%s/%s", validatedBucket, validatedKey);
        }
        
        @Override
        public String toString() {
            return String.format("SecureS3Info{bucket='%s', key='%s'}", validatedBucket, validatedKey);
        }
    }
}