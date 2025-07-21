package com.noahbackup.storage;

/**
 * Configuration class for S3-compatible storage settings.
 * Supports AWS S3, MinIO, Lightsail Object Storage, and other S3-compatible providers.
 */
public class S3Config {
    
    private String accessKey;
    private String secretKey;
    private String region;
    private String endpoint;
    private String defaultBucket;
    private boolean pathStyleAccess;
    
    public S3Config() {}
    
    public S3Config(String accessKey, String secretKey, String region, String defaultBucket) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.defaultBucket = defaultBucket;
    }
    
    public S3Config(String accessKey, String secretKey, String region, String endpoint, String defaultBucket) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.endpoint = endpoint;
        this.defaultBucket = defaultBucket;
    }
    
    // Getters and setters
    public String getAccessKey() { return accessKey; }
    public void setAccessKey(String accessKey) { this.accessKey = accessKey; }
    
    public String getSecretKey() { return secretKey; }
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
    
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    
    public String getDefaultBucket() { return defaultBucket; }
    public void setDefaultBucket(String defaultBucket) { this.defaultBucket = defaultBucket; }
    
    public boolean isPathStyleAccess() { return pathStyleAccess; }
    public void setPathStyleAccess(boolean pathStyleAccess) { this.pathStyleAccess = pathStyleAccess; }
    
    /**
     * Validates the configuration.
     */
    public void validate() throws IllegalArgumentException {
        if (accessKey == null || accessKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Access key cannot be null or empty");
        }
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Secret key cannot be null or empty");
        }
        if (region == null || region.trim().isEmpty()) {
            throw new IllegalArgumentException("Region cannot be null or empty");
        }
        if (defaultBucket == null || defaultBucket.trim().isEmpty()) {
            throw new IllegalArgumentException("Default bucket cannot be null or empty");
        }
    }
    
    @Override
    public String toString() {
        return String.format("S3Config{region='%s', endpoint='%s', defaultBucket='%s', pathStyleAccess=%s}", 
                           region, endpoint, defaultBucket, pathStyleAccess);
    }
}