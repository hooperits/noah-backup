package com.noahbackup.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Secure secrets management for Noah Backup.
 * Handles loading, validation, and secure access to sensitive configuration data.
 * 
 * Security Features:
 * - Environment variable priority over file-based config
 * - In-memory encryption of loaded secrets
 * - Secure cleanup of sensitive data
 * - Validation against common security misconfigurations
 */
public class SecretsManager {
    
    private static final Logger logger = LoggerFactory.getLogger(SecretsManager.class);
    
    // Environment variable names for secrets
    private static final String AWS_ACCESS_KEY_ID = "AWS_ACCESS_KEY_ID";
    private static final String AWS_SECRET_ACCESS_KEY = "AWS_SECRET_ACCESS_KEY";
    private static final String NOAH_MASTER_KEY = "NOAH_MASTER_KEY";
    private static final String NOAH_BACKUP_PASSWORD = "NOAH_BACKUP_PASSWORD";
    
    // .env file property names
    private static final String ENV_S3_ACCESS_KEY = "S3_ACCESS_KEY";
    private static final String ENV_S3_SECRET_KEY = "S3_SECRET_KEY";
    private static final String ENV_MASTER_KEY = "MASTER_KEY";
    private static final String ENV_BACKUP_PASSWORD = "BACKUP_PASSWORD";
    
    // Singleton instance
    private static SecretsManager instance;
    private static final Object lock = new Object();
    
    // Encrypted secrets storage
    private final Map<String, EncryptedSecret> secrets;
    private final AESEncryption encryption;
    
    // Private constructor for singleton pattern
    private SecretsManager() throws SecurityException {
        this.secrets = new ConcurrentHashMap<>();
        this.encryption = new AESEncryption();
        
        try {
            loadSecrets();
            validateSecrets();
            logger.info("SecretsManager initialized successfully with {} secrets", secrets.size());
        } catch (Exception e) {
            logger.error("Failed to initialize SecretsManager", e);
            throw new SecurityException("Secrets initialization failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Gets the singleton instance of SecretsManager.
     */
    public static SecretsManager getInstance() throws SecurityException {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new SecretsManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Retrieves a secret value securely.
     * 
     * @param key The secret key to retrieve
     * @return The decrypted secret value, or null if not found
     */
    public String getSecret(String key) {
        if (key == null || key.trim().isEmpty()) {
            logger.warn("Attempt to retrieve secret with null/empty key");
            return null;
        }
        
        EncryptedSecret encrypted = secrets.get(key.toUpperCase());
        if (encrypted == null) {
            logger.debug("Secret not found: {}", key);
            return null;
        }
        
        try {
            return encryption.decrypt(encrypted.encryptedValue, encrypted.iv);
        } catch (Exception e) {
            logger.error("Failed to decrypt secret: {}", key, e);
            return null;
        }
    }
    
    /**
     * Checks if a secret exists without retrieving its value.
     */
    public boolean hasSecret(String key) {
        return key != null && secrets.containsKey(key.toUpperCase());
    }
    
    /**
     * Gets all available secret keys (for debugging/validation).
     */
    public Set<String> getSecretKeys() {
        return new HashSet<>(secrets.keySet());
    }
    
    /**
     * Loads secrets from environment variables and .env files.
     */
    private void loadSecrets() throws Exception {
        logger.debug("Loading secrets from environment and configuration files");
        
        // Load from environment variables first (highest priority)
        loadFromEnvironment();
        
        // Load from .env file (lower priority)
        loadFromEnvFile();
        
        // Load from system properties (lowest priority)
        loadFromSystemProperties();
        
        logger.info("Loaded {} secrets total", secrets.size());
    }
    
    /**
     * Loads secrets from environment variables.
     */
    private void loadFromEnvironment() throws Exception {
        Map<String, String> envSecrets = new HashMap<>();
        
        // Standard AWS credentials
        addIfPresent(envSecrets, "AWS_ACCESS_KEY_ID", System.getenv(AWS_ACCESS_KEY_ID));
        addIfPresent(envSecrets, "AWS_SECRET_ACCESS_KEY", System.getenv(AWS_SECRET_ACCESS_KEY));
        
        // Noah-specific secrets
        addIfPresent(envSecrets, "MASTER_KEY", System.getenv(NOAH_MASTER_KEY));
        addIfPresent(envSecrets, "BACKUP_PASSWORD", System.getenv(NOAH_BACKUP_PASSWORD));
        
        // Store encrypted
        for (Map.Entry<String, String> entry : envSecrets.entrySet()) {
            storeSecret(entry.getKey(), entry.getValue());
        }
        
        if (!envSecrets.isEmpty()) {
            logger.debug("Loaded {} secrets from environment variables", envSecrets.size());
        }
    }
    
    /**
     * Loads secrets from .env file.
     */
    private void loadFromEnvFile() throws Exception {
        Path envFile = Paths.get(".env");
        if (!Files.exists(envFile)) {
            logger.debug("No .env file found");
            return;
        }
        
        Properties props = new Properties();
        try (InputStream input = Files.newInputStream(envFile)) {
            props.load(input);
        }
        
        Map<String, String> fileSecrets = new HashMap<>();
        
        // Map .env properties to secret keys
        addIfPresent(fileSecrets, "AWS_ACCESS_KEY_ID", props.getProperty(ENV_S3_ACCESS_KEY));
        addIfPresent(fileSecrets, "AWS_SECRET_ACCESS_KEY", props.getProperty(ENV_S3_SECRET_KEY));
        addIfPresent(fileSecrets, "MASTER_KEY", props.getProperty(ENV_MASTER_KEY));
        addIfPresent(fileSecrets, "BACKUP_PASSWORD", props.getProperty(ENV_BACKUP_PASSWORD));
        
        // Store only if not already loaded from environment
        for (Map.Entry<String, String> entry : fileSecrets.entrySet()) {
            if (!hasSecret(entry.getKey())) {
                storeSecret(entry.getKey(), entry.getValue());
            }
        }
        
        if (!fileSecrets.isEmpty()) {
            logger.debug("Loaded {} secrets from .env file", fileSecrets.size());
        }
    }
    
    /**
     * Loads secrets from system properties (fallback).
     */
    private void loadFromSystemProperties() throws Exception {
        Properties sysProps = System.getProperties();
        Map<String, String> propSecrets = new HashMap<>();
        
        // Check for system property overrides
        addIfPresent(propSecrets, "AWS_ACCESS_KEY_ID", sysProps.getProperty("aws.accessKeyId"));
        addIfPresent(propSecrets, "AWS_SECRET_ACCESS_KEY", sysProps.getProperty("aws.secretKey"));
        
        // Store only if not already loaded
        for (Map.Entry<String, String> entry : propSecrets.entrySet()) {
            if (!hasSecret(entry.getKey())) {
                storeSecret(entry.getKey(), entry.getValue());
            }
        }
        
        if (!propSecrets.isEmpty()) {
            logger.debug("Loaded {} secrets from system properties", propSecrets.size());
        }
    }
    
    /**
     * Stores a secret in encrypted form.
     */
    private void storeSecret(String key, String value) throws Exception {
        if (value == null || value.trim().isEmpty()) {
            return;
        }
        
        byte[] iv = encryption.generateIV();
        String encrypted = encryption.encrypt(value, iv);
        secrets.put(key.toUpperCase(), new EncryptedSecret(encrypted, iv));
        
        // Clear the plaintext value from memory
        if (value.length() > 0) {
            char[] chars = value.toCharArray();
            Arrays.fill(chars, '\0');
        }
    }
    
    /**
     * Adds a key-value pair if the value is not null/empty.
     */
    private void addIfPresent(Map<String, String> map, String key, String value) {
        if (value != null && !value.trim().isEmpty()) {
            map.put(key, value.trim());
        }
    }
    
    /**
     * Validates loaded secrets for security issues.
     */
    private void validateSecrets() throws SecurityException {
        List<String> issues = new ArrayList<>();
        
        // Check for required secrets
        if (!hasSecret("AWS_ACCESS_KEY_ID")) {
            issues.add("Missing AWS_ACCESS_KEY_ID - S3 operations will fail");
        }
        
        if (!hasSecret("AWS_SECRET_ACCESS_KEY")) {
            issues.add("Missing AWS_SECRET_ACCESS_KEY - S3 operations will fail");
        }
        
        // Validate secret strength
        validateSecretStrength("AWS_ACCESS_KEY_ID", issues);
        validateSecretStrength("AWS_SECRET_ACCESS_KEY", issues);
        validateSecretStrength("MASTER_KEY", issues);
        
        // Check for common insecure values
        checkForInsecureValues(issues);
        
        if (!issues.isEmpty()) {
            logger.warn("Security validation found {} issues:", issues.size());
            issues.forEach(issue -> logger.warn("  - {}", issue));
            
            // Only fail on critical issues
            if (issues.stream().anyMatch(issue -> issue.contains("Missing AWS"))) {
                throw new SecurityException("Critical security validation failed: missing required credentials");
            }
        } else {
            logger.info("All secrets passed security validation");
        }
    }
    
    /**
     * Validates the strength of a secret value.
     */
    private void validateSecretStrength(String key, List<String> issues) {
        String value = getSecret(key);
        if (value == null) {
            return;
        }
        
        if (value.length() < 8) {
            issues.add(String.format("Secret '%s' is too short (< 8 characters)", key));
        }
        
        if (value.matches("^[a-zA-Z]*$")) {
            issues.add(String.format("Secret '%s' contains only letters - consider adding numbers/symbols", key));
        }
        
        if (value.toLowerCase().contains("password") || value.toLowerCase().contains("secret")) {
            issues.add(String.format("Secret '%s' contains obvious keywords", key));
        }
    }
    
    /**
     * Checks for common insecure secret values.
     */
    private void checkForInsecureValues(List<String> issues) {
        String[] commonInsecureValues = {
            "password", "123456", "admin", "root", "test", "demo", 
            "your-access-key-here", "your-secret-key-here"
        };
        
        for (String secretKey : secrets.keySet()) {
            String value = getSecret(secretKey);
            if (value != null) {
                String lowerValue = value.toLowerCase();
                for (String insecure : commonInsecureValues) {
                    if (lowerValue.contains(insecure)) {
                        issues.add(String.format("Secret '%s' contains insecure value pattern", secretKey));
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Securely clears all secrets from memory.
     */
    public void clearSecrets() {
        logger.info("Clearing all secrets from memory");
        secrets.clear();
        encryption.clearKeys();
    }
    
    /**
     * Data class for encrypted secret storage.
     */
    private static class EncryptedSecret {
        final String encryptedValue;
        final byte[] iv;
        
        EncryptedSecret(String encryptedValue, byte[] iv) {
            this.encryptedValue = encryptedValue;
            this.iv = iv.clone();
        }
    }
    
    /**
     * Cleanup on JVM shutdown.
     */
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (instance != null) {
                instance.clearSecrets();
            }
        }));
    }
}