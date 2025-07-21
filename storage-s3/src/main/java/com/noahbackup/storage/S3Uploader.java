package com.noahbackup.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * S3-compatible storage uploader supporting AWS S3, MinIO, and Lightsail Object Storage.
 * Handles single file uploads, directory uploads, and multipart uploads for large files.
 */
public class S3Uploader {
    
    private static final Logger logger = LoggerFactory.getLogger(S3Uploader.class);
    private static final long MULTIPART_THRESHOLD = 100L * 1024 * 1024; // 100 MB
    private static final long PART_SIZE = 10L * 1024 * 1024; // 10 MB parts
    
    private final S3Client s3Client;
    private final String defaultBucket;
    
    /**
     * Creates S3Uploader with configuration.
     */
    public S3Uploader(S3Config config) {
        this.defaultBucket = config.getDefaultBucket();
        this.s3Client = createS3Client(config);
        logger.info("S3Uploader initialized for bucket: {} at endpoint: {}", 
                   defaultBucket, config.getEndpoint());
    }
    
    /**
     * Uploads a single file to the specified bucket.
     * 
     * @param file The file to upload
     * @param bucketName The target bucket name
     * @return S3UploadResult containing upload details
     * @throws S3UploadException if upload fails
     */
    public S3UploadResult uploadFile(File file, String bucketName) throws S3UploadException {
        return uploadFile(file, bucketName, null);
    }
    
    /**
     * Uploads a single file to the default bucket.
     */
    public S3UploadResult uploadFile(File file) throws S3UploadException {
        return uploadFile(file, defaultBucket, null);
    }
    
    /**
     * Uploads a file with a custom S3 key (path).
     */
    public S3UploadResult uploadFile(File file, String bucketName, String customKey) throws S3UploadException {
        validateFile(file);
        
        String s3Key = customKey != null ? customKey : generateS3Key(file.getName());
        long fileSize = file.length();
        
        logger.info("Uploading file: {} ({} bytes) to s3://{}/{}", 
                   file.getName(), fileSize, bucketName, s3Key);
        
        try {
            if (fileSize > MULTIPART_THRESHOLD) {
                return uploadMultipart(file, bucketName, s3Key);
            } else {
                return uploadSingle(file, bucketName, s3Key);
            }
        } catch (Exception e) {
            logger.error("Failed to upload file: {}", file.getName(), e);
            throw new S3UploadException("Upload failed for file: " + file.getName(), e);
        }
    }
    
    /**
     * Uploads an entire directory recursively to S3.
     * 
     * @param directory The directory to upload
     * @param bucketName The target bucket name
     * @return S3UploadResult with summary of all uploads
     * @throws S3UploadException if upload fails
     */
    public S3UploadResult uploadDirectory(File directory, String bucketName) throws S3UploadException {
        return uploadDirectory(directory, bucketName, null);
    }
    
    /**
     * Uploads directory to default bucket.
     */
    public S3UploadResult uploadDirectory(File directory) throws S3UploadException {
        return uploadDirectory(directory, defaultBucket, null);
    }
    
    /**
     * Uploads directory with custom S3 prefix.
     */
    public S3UploadResult uploadDirectory(File directory, String bucketName, String s3Prefix) throws S3UploadException {
        if (!directory.isDirectory()) {
            throw new S3UploadException("Path is not a directory: " + directory.getPath());
        }
        
        String prefix = s3Prefix != null ? s3Prefix : generateDirectoryPrefix(directory.getName());
        logger.info("Uploading directory: {} to s3://{}/{}", directory.getName(), bucketName, prefix);
        
        List<S3UploadResult> results = new ArrayList<>();
        long totalSize = 0;
        int fileCount = 0;
        
        try (Stream<Path> paths = Files.walk(directory.toPath())) {
            for (Path path : paths.filter(Files::isRegularFile).toList()) {
                File file = path.toFile();
                String relativePath = directory.toPath().relativize(path).toString().replace("\\", "/");
                String s3Key = prefix + relativePath;
                
                S3UploadResult result = uploadFile(file, bucketName, s3Key);
                results.add(result);
                totalSize += result.getFileSize();
                fileCount++;
            }
        } catch (IOException e) {
            throw new S3UploadException("Failed to traverse directory: " + directory.getPath(), e);
        }
        
        logger.info("Directory upload completed: {} files, {} bytes total", fileCount, totalSize);
        return new S3UploadResult(true, bucketName, prefix, totalSize, fileCount, "Directory upload completed", results);
    }
    
    /**
     * Single file upload for files under the multipart threshold.
     */
    private S3UploadResult uploadSingle(File file, String bucketName, String s3Key) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(detectContentType(file))
                    .build();
            
            PutObjectResponse response = s3Client.putObject(request, RequestBody.fromFile(file));
            
            logger.debug("Single upload completed: {} -> s3://{}/{}", file.getName(), bucketName, s3Key);
            return new S3UploadResult(true, bucketName, s3Key, file.length(), 1, 
                                    "Upload completed successfully", response.eTag());
            
        } catch (Exception e) {
            throw new RuntimeException("Single upload failed", e);
        }
    }
    
    /**
     * Multipart upload for large files (>100 MB).
     */
    private S3UploadResult uploadMultipart(File file, String bucketName, String s3Key) {
        logger.info("Starting multipart upload for large file: {} ({} MB)", 
                   file.getName(), file.length() / (1024 * 1024));
        
        try {
            // Initiate multipart upload
            CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(detectContentType(file))
                    .build();
            
            CreateMultipartUploadResponse createResponse = s3Client.createMultipartUpload(createRequest);
            String uploadId = createResponse.uploadId();
            
            // Upload parts
            List<CompletedPart> completedParts = new ArrayList<>();
            long fileSize = file.length();
            long position = 0;
            int partNumber = 1;
            
            while (position < fileSize) {
                long currentPartSize = Math.min(PART_SIZE, fileSize - position);
                
                UploadPartRequest partRequest = UploadPartRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key)
                        .uploadId(uploadId)
                        .partNumber(partNumber)
                        .build();
                
                // Create RequestBody from file with specific range
                try (var fileInputStream = new FileInputStream(file)) {
                    fileInputStream.skip(position);
                    byte[] buffer = new byte[(int) currentPartSize];
                    int bytesRead = fileInputStream.read(buffer, 0, (int) currentPartSize);
                    
                    // Create array with exact size for this part
                    byte[] partData = new byte[bytesRead];
                    System.arraycopy(buffer, 0, partData, 0, bytesRead);
                    RequestBody partBody = RequestBody.fromBytes(partData);
                    UploadPartResponse partResponse = s3Client.uploadPart(partRequest, partBody);
                    
                    completedParts.add(CompletedPart.builder()
                            .partNumber(partNumber)
                            .eTag(partResponse.eTag())
                            .build());
                    
                } catch (IOException e) {
                    throw new RuntimeException("Failed to read file part", e);
                }
                
                position += currentPartSize;
                partNumber++;
                
                logger.debug("Uploaded part {}: {} bytes at position {}", 
                           partNumber - 1, currentPartSize, position - currentPartSize);
            }
            
            // Complete multipart upload
            CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .uploadId(uploadId)
                    .multipartUpload(CompletedMultipartUpload.builder()
                            .parts(completedParts)
                            .build())
                    .build();
            
            CompleteMultipartUploadResponse completeResponse = s3Client.completeMultipartUpload(completeRequest);
            
            logger.info("Multipart upload completed: {} parts uploaded", completedParts.size());
            return new S3UploadResult(true, bucketName, s3Key, fileSize, 1,
                                    "Multipart upload completed successfully", completeResponse.eTag());
            
        } catch (Exception e) {
            logger.error("Multipart upload failed for file: {}", file.getName(), e);
            throw new RuntimeException("Multipart upload failed", e);
        }
    }
    
    /**
     * Creates S3 client based on configuration.
     */
    private S3Client createS3Client(S3Config config) {
        AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
            AwsBasicCredentials.create(config.getAccessKey(), config.getSecretKey())
        );
        
        var clientBuilder = S3Client.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.of(config.getRegion()));
        
        // Configure custom endpoint for MinIO/Lightsail
        if (config.getEndpoint() != null && !config.getEndpoint().isEmpty()) {
            clientBuilder.endpointOverride(URI.create(config.getEndpoint()));
            
            // Force path style for MinIO compatibility
            if (config.getEndpoint().contains("localhost") || config.getEndpoint().contains("127.0.0.1")) {
                clientBuilder.forcePathStyle(true);
            }
        }
        
        return clientBuilder.build();
    }
    
    /**
     * Validates file before upload.
     */
    private void validateFile(File file) throws S3UploadException {
        if (file == null) {
            throw new S3UploadException("File cannot be null");
        }
        if (!file.exists()) {
            throw new S3UploadException("File does not exist: " + file.getPath());
        }
        if (!file.isFile()) {
            throw new S3UploadException("Path is not a file: " + file.getPath());
        }
        if (!file.canRead()) {
            throw new S3UploadException("Cannot read file: " + file.getPath());
        }
    }
    
    /**
     * Generates S3 key with timestamp for uniqueness.
     */
    private String generateS3Key(String fileName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HHmmss"));
        return String.format("backups/%s/%s", timestamp, fileName);
    }
    
    /**
     * Generates S3 prefix for directory uploads.
     */
    private String generateDirectoryPrefix(String directoryName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HHmmss"));
        return String.format("backups/%s/%s/", timestamp, directoryName);
    }
    
    /**
     * Detects content type for S3 object.
     */
    private String detectContentType(File file) {
        try {
            String contentType = Files.probeContentType(file.toPath());
            return contentType != null ? contentType : "application/octet-stream";
        } catch (IOException e) {
            return "application/octet-stream";
        }
    }
    
    /**
     * Closes the S3 client.
     */
    public void close() {
        if (s3Client != null) {
            s3Client.close();
            logger.debug("S3Client closed");
        }
    }
}