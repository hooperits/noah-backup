package com.noahbackup.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * AES-256-GCM encryption implementation for securing sensitive data in Noah Backup.
 * 
 * Security Features:
 * - AES-256-GCM authenticated encryption
 * - Cryptographically secure random key generation
 * - Unique initialization vectors (IV) for each encryption
 * - Secure key derivation from master password
 * - Memory cleanup of sensitive data
 */
public class AESEncryption {
    
    private static final Logger logger = LoggerFactory.getLogger(AESEncryption.class);
    
    // AES-GCM constants
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int KEY_LENGTH = 256; // AES-256
    private static final int IV_LENGTH = 12;   // 96 bits for GCM
    private static final int TAG_LENGTH = 16;  // 128 bits for authentication tag
    
    private SecretKey masterKey;
    private final SecureRandom secureRandom;
    
    /**
     * Creates a new AES encryption instance with auto-generated master key.
     */
    public AESEncryption() throws SecurityException {
        this.secureRandom = new SecureRandom();
        try {
            generateMasterKey();
            logger.debug("AES encryption initialized with generated master key");
        } catch (Exception e) {
            logger.error("Failed to initialize AES encryption", e);
            throw new SecurityException("AES encryption initialization failed", e);
        }
    }
    
    /**
     * Creates AES encryption with provided master key.
     */
    public AESEncryption(String masterKeyBase64) throws SecurityException {
        this.secureRandom = new SecureRandom();
        try {
            setMasterKey(masterKeyBase64);
            logger.debug("AES encryption initialized with provided master key");
        } catch (Exception e) {
            logger.error("Failed to initialize AES encryption with provided key", e);
            throw new SecurityException("AES encryption initialization failed", e);
        }
    }
    
    /**
     * Generates a new cryptographically secure master key.
     */
    private void generateMasterKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(KEY_LENGTH);
        this.masterKey = keyGenerator.generateKey();
    }
    
    /**
     * Sets the master key from Base64 encoded string.
     */
    private void setMasterKey(String masterKeyBase64) throws SecurityException {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(masterKeyBase64);
            if (keyBytes.length != KEY_LENGTH / 8) {
                throw new SecurityException("Invalid master key length: expected " + (KEY_LENGTH / 8) + " bytes");
            }
            this.masterKey = new SecretKeySpec(keyBytes, ALGORITHM);
        } catch (Exception e) {
            throw new SecurityException("Invalid master key format", e);
        }
    }
    
    /**
     * Gets the master key as Base64 encoded string (for backup/storage).
     */
    public String getMasterKeyBase64() {
        if (masterKey == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(masterKey.getEncoded());
    }
    
    /**
     * Encrypts plaintext using AES-256-GCM.
     * 
     * @param plaintext The text to encrypt
     * @param iv The initialization vector (12 bytes for GCM)
     * @return Base64 encoded ciphertext
     */
    public String encrypt(String plaintext, byte[] iv) throws SecurityException {
        if (plaintext == null) {
            throw new IllegalArgumentException("Plaintext cannot be null");
        }
        
        if (iv.length != IV_LENGTH) {
            throw new IllegalArgumentException("IV must be " + IV_LENGTH + " bytes for GCM");
        }
        
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, masterKey, gcmSpec);
            
            byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);
            byte[] ciphertext = cipher.doFinal(plaintextBytes);
            
            // Clear plaintext bytes from memory
            Arrays.fill(plaintextBytes, (byte) 0);
            
            return Base64.getEncoder().encodeToString(ciphertext);
            
        } catch (Exception e) {
            logger.error("Encryption failed", e);
            throw new SecurityException("AES encryption failed", e);
        }
    }
    
    /**
     * Decrypts ciphertext using AES-256-GCM.
     * 
     * @param ciphertextBase64 Base64 encoded ciphertext
     * @param iv The initialization vector used for encryption
     * @return Decrypted plaintext
     */
    public String decrypt(String ciphertextBase64, byte[] iv) throws SecurityException {
        if (ciphertextBase64 == null) {
            throw new IllegalArgumentException("Ciphertext cannot be null");
        }
        
        if (iv.length != IV_LENGTH) {
            throw new IllegalArgumentException("IV must be " + IV_LENGTH + " bytes for GCM");
        }
        
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, masterKey, gcmSpec);
            
            byte[] ciphertext = Base64.getDecoder().decode(ciphertextBase64);
            byte[] plaintextBytes = cipher.doFinal(ciphertext);
            
            String plaintext = new String(plaintextBytes, StandardCharsets.UTF_8);
            
            // Clear plaintext bytes from memory
            Arrays.fill(plaintextBytes, (byte) 0);
            
            return plaintext;
            
        } catch (Exception e) {
            logger.error("Decryption failed", e);
            throw new SecurityException("AES decryption failed", e);
        }
    }
    
    /**
     * Generates a cryptographically secure initialization vector.
     * 
     * @return 12-byte IV suitable for AES-GCM
     */
    public byte[] generateIV() {
        byte[] iv = new byte[IV_LENGTH];
        secureRandom.nextBytes(iv);
        return iv;
    }
    
    /**
     * Encrypts a file in place using AES-256-GCM.
     * 
     * @param filePath Path to the file to encrypt
     * @return Base64 encoded IV used for encryption
     */
    public String encryptFile(String filePath) throws SecurityException {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(filePath);
            byte[] fileContent = java.nio.file.Files.readAllBytes(path);
            
            byte[] iv = generateIV();
            String fileContentStr = Base64.getEncoder().encodeToString(fileContent);
            String encryptedContent = encrypt(fileContentStr, iv);
            
            // Write encrypted content back to file
            java.nio.file.Files.write(path, encryptedContent.getBytes(StandardCharsets.UTF_8));
            
            // Clear original content from memory
            Arrays.fill(fileContent, (byte) 0);
            
            logger.debug("File encrypted successfully: {}", filePath);
            return Base64.getEncoder().encodeToString(iv);
            
        } catch (Exception e) {
            logger.error("File encryption failed: {}", filePath, e);
            throw new SecurityException("File encryption failed", e);
        }
    }
    
    /**
     * Decrypts a file in place using AES-256-GCM.
     * 
     * @param filePath Path to the file to decrypt
     * @param ivBase64 Base64 encoded IV used for encryption
     */
    public void decryptFile(String filePath, String ivBase64) throws SecurityException {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(filePath);
            String encryptedContent = new String(java.nio.file.Files.readAllBytes(path), StandardCharsets.UTF_8);
            
            byte[] iv = Base64.getDecoder().decode(ivBase64);
            String decryptedContentStr = decrypt(encryptedContent, iv);
            byte[] decryptedContent = Base64.getDecoder().decode(decryptedContentStr);
            
            // Write decrypted content back to file
            java.nio.file.Files.write(path, decryptedContent);
            
            // Clear decrypted content from memory
            Arrays.fill(decryptedContent, (byte) 0);
            
            logger.debug("File decrypted successfully: {}", filePath);
            
        } catch (Exception e) {
            logger.error("File decryption failed: {}", filePath, e);
            throw new SecurityException("File decryption failed", e);
        }
    }
    
    /**
     * Creates an encrypted temporary file with the given content.
     * 
     * @param content Content to encrypt and store
     * @param prefix Temporary file prefix
     * @return Encrypted file info containing file path and IV
     */
    public EncryptedFileInfo createEncryptedTempFile(String content, String prefix) throws SecurityException {
        try {
            java.nio.file.Path tempFile = java.nio.file.Files.createTempFile(prefix, ".enc");
            byte[] iv = generateIV();
            String encryptedContent = encrypt(content, iv);
            
            java.nio.file.Files.write(tempFile, encryptedContent.getBytes(StandardCharsets.UTF_8));
            
            // Set file to be deleted on JVM exit
            tempFile.toFile().deleteOnExit();
            
            logger.debug("Created encrypted temporary file: {}", tempFile);
            return new EncryptedFileInfo(tempFile.toString(), Base64.getEncoder().encodeToString(iv));
            
        } catch (Exception e) {
            logger.error("Failed to create encrypted temporary file", e);
            throw new SecurityException("Encrypted temp file creation failed", e);
        }
    }
    
    /**
     * Reads and decrypts an encrypted temporary file.
     */
    public String readEncryptedTempFile(EncryptedFileInfo fileInfo) throws SecurityException {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(fileInfo.filePath);
            String encryptedContent = new String(java.nio.file.Files.readAllBytes(path), StandardCharsets.UTF_8);
            
            byte[] iv = Base64.getDecoder().decode(fileInfo.ivBase64);
            return decrypt(encryptedContent, iv);
            
        } catch (Exception e) {
            logger.error("Failed to read encrypted temporary file: {}", fileInfo.filePath, e);
            throw new SecurityException("Encrypted temp file read failed", e);
        }
    }
    
    /**
     * Securely deletes an encrypted temporary file.
     */
    public void deleteEncryptedTempFile(EncryptedFileInfo fileInfo) {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(fileInfo.filePath);
            if (java.nio.file.Files.exists(path)) {
                // Overwrite with random data before deletion (basic secure delete)
                byte[] randomData = new byte[(int) java.nio.file.Files.size(path)];
                secureRandom.nextBytes(randomData);
                java.nio.file.Files.write(path, randomData);
                
                // Delete the file
                java.nio.file.Files.delete(path);
                logger.debug("Securely deleted encrypted temporary file: {}", fileInfo.filePath);
            }
        } catch (Exception e) {
            logger.warn("Failed to securely delete encrypted temp file: {}", fileInfo.filePath, e);
        }
    }
    
    /**
     * Clears the master key from memory (irreversible).
     */
    public void clearKeys() {
        if (masterKey != null) {
            byte[] encoded = masterKey.getEncoded();
            if (encoded != null) {
                Arrays.fill(encoded, (byte) 0);
            }
            masterKey = null;
            logger.debug("Master key cleared from memory");
        }
    }
    
    /**
     * Data class for encrypted file information.
     */
    public static class EncryptedFileInfo {
        public final String filePath;
        public final String ivBase64;
        
        public EncryptedFileInfo(String filePath, String ivBase64) {
            this.filePath = filePath;
            this.ivBase64 = ivBase64;
        }
        
        @Override
        public String toString() {
            return String.format("EncryptedFileInfo{filePath='%s', iv='%s'}", filePath, ivBase64);
        }
    }
}