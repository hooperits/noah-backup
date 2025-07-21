package com.noahbackup.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Loads S3 configuration from multiple sources: environment variables, .env file, and application.yml.
 * Priority: Environment Variables > .env file > application.yml > defaults
 */
public class S3ConfigLoader {
    
    private static final Logger logger = LoggerFactory.getLogger(S3ConfigLoader.class);
    
    // Environment variable names
    private static final String AWS_ACCESS_KEY_ID = "AWS_ACCESS_KEY_ID";
    private static final String AWS_SECRET_ACCESS_KEY = "AWS_SECRET_ACCESS_KEY";
    private static final String AWS_REGION = "AWS_REGION";
    private static final String AWS_ENDPOINT_URL = "AWS_ENDPOINT_URL";
    private static final String AWS_S3_BUCKET = "AWS_S3_BUCKET";
    
    // .env file property names
    private static final String ENV_ACCESS_KEY = "S3_ACCESS_KEY";
    private static final String ENV_SECRET_KEY = "S3_SECRET_KEY";
    private static final String ENV_REGION = "S3_REGION";
    private static final String ENV_ENDPOINT = "S3_ENDPOINT";
    private static final String ENV_BUCKET = "S3_BUCKET";
    
    /**
     * Loads S3 configuration from all available sources.
     * 
     * @return Configured S3Config object
     * @throws IllegalArgumentException if required configuration is missing
     */
    public static S3Config loadConfig() throws IllegalArgumentException {
        logger.info("Loading S3 configuration from multiple sources");
        
        S3Config config = new S3Config();
        
        // Load from .env file first (lowest priority)
        loadFromEnvFile(config);
        
        // Override with environment variables (highest priority)
        loadFromEnvironmentVariables(config);
        
        // Set defaults for missing values
        setDefaults(config);
        
        // Validate configuration
        config.validate();
        
        logger.info("S3 configuration loaded successfully: {}", config);
        return config;
    }
    
    /**
     * Loads configuration from environment variables.
     */
    private static void loadFromEnvironmentVariables(S3Config config) {
        String accessKey = System.getenv(AWS_ACCESS_KEY_ID);
        String secretKey = System.getenv(AWS_SECRET_ACCESS_KEY);
        String region = System.getenv(AWS_REGION);
        String endpoint = System.getenv(AWS_ENDPOINT_URL);
        String bucket = System.getenv(AWS_S3_BUCKET);
        
        if (accessKey != null) {
            config.setAccessKey(accessKey);
            logger.debug("Access key loaded from environment variable");
        }
        if (secretKey != null) {
            config.setSecretKey(secretKey);
            logger.debug("Secret key loaded from environment variable");
        }
        if (region != null) {
            config.setRegion(region);
            logger.debug("Region loaded from environment variable: {}", region);
        }
        if (endpoint != null) {
            config.setEndpoint(endpoint);
            logger.debug("Endpoint loaded from environment variable: {}", endpoint);
        }
        if (bucket != null) {
            config.setDefaultBucket(bucket);
            logger.debug("Bucket loaded from environment variable: {}", bucket);
        }
    }
    
    /**
     * Loads configuration from .env file.
     */
    private static void loadFromEnvFile(S3Config config) {
        Path envFile = Paths.get(".env");
        if (!Files.exists(envFile)) {
            logger.debug("No .env file found, skipping");
            return;
        }
        
        Properties envProps = new Properties();
        try (InputStream input = Files.newInputStream(envFile)) {
            envProps.load(input);
            logger.debug("Loaded .env file with {} properties", envProps.size());
            
            String accessKey = envProps.getProperty(ENV_ACCESS_KEY);
            String secretKey = envProps.getProperty(ENV_SECRET_KEY);
            String region = envProps.getProperty(ENV_REGION);
            String endpoint = envProps.getProperty(ENV_ENDPOINT);
            String bucket = envProps.getProperty(ENV_BUCKET);
            
            if (accessKey != null) config.setAccessKey(accessKey);
            if (secretKey != null) config.setSecretKey(secretKey);
            if (region != null) config.setRegion(region);
            if (endpoint != null) config.setEndpoint(endpoint);
            if (bucket != null) config.setDefaultBucket(bucket);
            
        } catch (IOException e) {
            logger.warn("Failed to load .env file: {}", e.getMessage());
        }
    }
    
    /**
     * Sets default values for missing configuration.
     */
    private static void setDefaults(S3Config config) {
        if (config.getRegion() == null || config.getRegion().isEmpty()) {
            config.setRegion("us-east-1");
            logger.debug("Using default region: us-east-1");
        }
        
        if (config.getDefaultBucket() == null || config.getDefaultBucket().isEmpty()) {
            config.setDefaultBucket("noah-backup-default");
            logger.debug("Using default bucket: noah-backup-default");
        }
    }
    
    /**
     * Creates a sample .env file for user reference.
     */
    public static void createSampleEnvFile() throws IOException {
        Path envExampleFile = Paths.get(".env.example");
        
        String sampleContent = """
                # Noah Backup S3 Configuration
                # Copy this file to .env and fill in your credentials
                
                # AWS/MinIO Access Credentials
                S3_ACCESS_KEY=your-access-key-here
                S3_SECRET_KEY=your-secret-key-here
                
                # S3 Configuration
                S3_REGION=us-east-1
                S3_BUCKET=noah-backup-bucket
                
                # Optional: Custom endpoint for MinIO/Lightsail Object Storage
                # S3_ENDPOINT=https://s3.amazonaws.com
                # S3_ENDPOINT=http://localhost:9000  # MinIO local
                # S3_ENDPOINT=https://storage.us-east-1.amazonaws.com  # Lightsail
                
                # Examples for different providers:
                # AWS S3: Leave S3_ENDPOINT empty or comment out
                # MinIO: S3_ENDPOINT=http://your-minio-server:9000
                # Lightsail: S3_ENDPOINT=https://storage.region.amazonaws.com
                """;
        
        Files.write(envExampleFile, sampleContent.getBytes());
        logger.info("Created sample .env.example file");
    }
}