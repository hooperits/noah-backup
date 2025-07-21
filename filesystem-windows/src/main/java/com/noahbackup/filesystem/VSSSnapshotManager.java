package com.noahbackup.filesystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * Manages Windows Volume Shadow Copy Service (VSS) operations for backing up locked files.
 * This class provides methods to create VSS snapshots and copy files safely from them.
 */
public class VSSSnapshotManager {
    
    private static final Logger logger = LoggerFactory.getLogger(VSSSnapshotManager.class);
    private static final String POWERSHELL_SCRIPT = "vss-backup.ps1";
    private static final int TIMEOUT_MINUTES = 30;
    
    /**
     * Creates a VSS snapshot and copies files from the specified source to destination.
     * This method handles locked files like .pst, .mdb, and other files in use.
     * 
     * @param sourcePath The source directory or file to backup
     * @param destinationPath The destination directory for the backup
     * @return VSSBackupResult containing operation status and details
     * @throws VSSException if the backup operation fails
     */
    public VSSBackupResult createSnapshot(String sourcePath, String destinationPath) throws VSSException {
        logger.info("Starting VSS backup: {} -> {}", sourcePath, destinationPath);
        
        validateParameters(sourcePath, destinationPath);
        
        try {
            // Get the PowerShell script from resources
            String scriptPath = extractPowerShellScript();
            
            // Build PowerShell command
            ProcessBuilder processBuilder = new ProcessBuilder(
                "powershell.exe",
                "-ExecutionPolicy", "Bypass",
                "-File", scriptPath,
                "-SourcePath", sourcePath,
                "-DestinationPath", destinationPath,
                "-LogFile", getLogFilePath(destinationPath)
            );
            
            processBuilder.redirectErrorStream(true);
            
            // Start the process
            logger.debug("Executing PowerShell script: {}", String.join(" ", processBuilder.command()));
            Process process = processBuilder.start();
            
            // Capture output
            String output = captureProcessOutput(process);
            
            // Wait for completion
            boolean finished = process.waitFor(TIMEOUT_MINUTES, TimeUnit.MINUTES);
            
            if (!finished) {
                process.destroyForcibly();
                throw new VSSException("VSS backup operation timed out after " + TIMEOUT_MINUTES + " minutes");
            }
            
            int exitCode = process.exitValue();
            
            if (exitCode == 0) {
                logger.info("VSS backup completed successfully");
                return new VSSBackupResult(true, output, exitCode);
            } else {
                logger.error("VSS backup failed with exit code: {}", exitCode);
                throw new VSSException("VSS backup failed with exit code: " + exitCode + "\nOutput: " + output);
            }
            
        } catch (VSSException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error during VSS backup operation", e);
            throw new VSSException("VSS backup operation failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Copies files from VSS snapshot - convenience method for single file operations.
     */
    public VSSBackupResult copyFromSnapshot(String sourceFile, String destinationFile) throws VSSException {
        return createSnapshot(sourceFile, destinationFile);
    }
    
    /**
     * Validates input parameters for the backup operation.
     */
    private void validateParameters(String sourcePath, String destinationPath) throws VSSException {
        if (sourcePath == null || sourcePath.trim().isEmpty()) {
            throw new VSSException("Source path cannot be null or empty");
        }
        
        if (destinationPath == null || destinationPath.trim().isEmpty()) {
            throw new VSSException("Destination path cannot be null or empty");
        }
        
        Path source = Paths.get(sourcePath);
        if (!Files.exists(source)) {
            throw new VSSException("Source path does not exist: " + sourcePath);
        }
        
        // Validate that we're on Windows
        String osName = System.getProperty("os.name").toLowerCase();
        if (!osName.contains("windows")) {
            throw new VSSException("VSS operations are only supported on Windows operating systems");
        }
    }
    
    /**
     * Extracts the PowerShell script from resources to a temporary location.
     */
    private String extractPowerShellScript() throws IOException {
        InputStream scriptStream = getClass().getClassLoader().getResourceAsStream(POWERSHELL_SCRIPT);
        if (scriptStream == null) {
            throw new IOException("PowerShell script not found in resources: " + POWERSHELL_SCRIPT);
        }
        
        // Create temporary file
        Path tempScript = Files.createTempFile("vss-backup", ".ps1");
        Files.copy(scriptStream, tempScript, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        
        // Make sure the temp file is deleted on JVM exit
        tempScript.toFile().deleteOnExit();
        
        logger.debug("Extracted PowerShell script to: {}", tempScript.toString());
        return tempScript.toString();
    }
    
    /**
     * Captures the output from the PowerShell process.
     */
    private String captureProcessOutput(Process process) throws IOException {
        StringBuilder output = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
                logger.debug("PS: {}", line);
            }
        }
        
        return output.toString();
    }
    
    /**
     * Generates a log file path for the PowerShell script.
     */
    private String getLogFilePath(String destinationPath) {
        Path destPath = Paths.get(destinationPath);
        Path logFile = destPath.getParent().resolve("vss-backup.log");
        return logFile.toString();
    }
    
    /**
     * Result object for VSS backup operations.
     */
    public static class VSSBackupResult {
        private final boolean success;
        private final String output;
        private final int exitCode;
        
        public VSSBackupResult(boolean success, String output, int exitCode) {
            this.success = success;
            this.output = output;
            this.exitCode = exitCode;
        }
        
        public boolean isSuccess() { return success; }
        public String getOutput() { return output; }
        public int getExitCode() { return exitCode; }
        
        @Override
        public String toString() {
            return String.format("VSSBackupResult{success=%s, exitCode=%d, output='%s'}", 
                               success, exitCode, output);
        }
    }
    
    /**
     * Custom exception for VSS operations.
     */
    public static class VSSException extends Exception {
        public VSSException(String message) {
            super(message);
        }
        
        public VSSException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}