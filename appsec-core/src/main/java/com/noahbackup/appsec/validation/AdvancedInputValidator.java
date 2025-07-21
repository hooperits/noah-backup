package com.noahbackup.appsec.validation;

import com.noahbackup.auth.SecureCodingUtils;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

/**
 * Advanced input validation framework for Noah Backup security hardening.
 * 
 * Provides comprehensive validation against:
 * - SQL injection attacks
 * - XSS (Cross-Site Scripting) attacks
 * - Path traversal attacks
 * - Command injection attacks
 * - LDAP injection attacks
 * - XML/XXE attacks
 * - NoSQL injection attacks
 * 
 * Features:
 * - OWASP-compliant validation patterns
 * - Context-aware sanitization
 * - Customizable security policies
 * - Detailed attack detection logging
 */
@Component
public class AdvancedInputValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(AdvancedInputValidator.class);
    
    // SQL Injection Detection Patterns
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i)(union|select|insert|update|delete|drop|create|alter|exec|execute|sp_|xp_|--|;|'|\"" +
        "|\\bor\\b|\\band\\b|\\bwhere\\b|\\bhaving\\b|\\bgroup\\s+by\\b|\\border\\s+by\\b" +
        "|\\bfrom\\b|\\binto\\b|\\bvalues\\b|\\bset\\b|\\bjoin\\b|\\binner\\b|\\bouter\\b" +
        "|\\bleft\\b|\\bright\\b|\\bfull\\b|\\bcross\\b|\\bunion\\s+all\\b)"
    );
    
    // XSS Detection Patterns
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "(?i)(<script|</script|javascript:|vbscript:|onload|onerror|onclick|onmouseover" +
        "|onkeypress|onkeydown|onkeyup|onfocus|onblur|onchange|onsubmit|onreset" +
        "|onselect|onunload|onbeforeunload|alert\\(|confirm\\(|prompt\\(" +
        "|document\\.|window\\.|eval\\(|settimeout|setinterval|<iframe|<object|<embed" +
        "|<link|<meta|<style|expression\\(|url\\(|@import|<svg|<math)"
    );
    
    // Command Injection Detection Patterns
    private static final Pattern COMMAND_INJECTION_PATTERN = Pattern.compile(
        "(?i)(;|\\||&&|\\$\\(|`|\\${|\\breturn\\b|\\beval\\b|\\bexec\\b|\\bsystem\\b" +
        "|\\bpassthru\\b|\\bshell_exec\\b|\\bpopen\\b|\\bproc_open\\b|\\bfile_get_contents\\b" +
        "|\\bfopen\\b|\\bfread\\b|\\bfwrite\\b|\\binclude\\b|\\brequire\\b|\\bimport\\b" +
        "|\\breturn\\b|\\bcat\\b|\\bls\\b|\\bps\\b|\\bid\\b|\\buname\\b|\\bwhoami\\b" +
        "|\\bnetstat\\b|\\bping\\b|\\bnslookup\\b|\\bdig\\b|\\bwget\\b|\\bcurl\\b)"
    );
    
    // Path Traversal Detection Patterns
    private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile(
        "(?i)(\\.\\.[\\\\/]|[\\\\/]\\.\\.[\\\\/]|%2e%2e[\\\\/]|%2e%2e%2f|%2e%2e%5c" +
        "|\\.\\.%2f|\\.\\.%5c|%252e%252e|%c0%ae%c0%ae|%c1%9c|%c1%8s|%c1%1c|%c1%af" +
        "|\\\\\\.\\.\\\\|/\\.\\./)|(^|[^a-zA-Z0-9])(\\.{2,}[/\\\\]+)+"
    );
    
    // LDAP Injection Detection Patterns
    private static final Pattern LDAP_INJECTION_PATTERN = Pattern.compile(
        "(?i)(\\*|\\(|\\)|\\\\|\\||&|!|=|~|>|<|\\+|\\-|,|;|\"|\\'|%|_" +
        "|\\x00|\\x01|\\x02|\\x03|\\x04|\\x05|\\x06|\\x07|\\x08|\\x09|\\x0a" +
        "|\\x0b|\\x0c|\\x0d|\\x0e|\\x0f|\\x10|\\x11|\\x12|\\x13|\\x14|\\x15" +
        "|\\x16|\\x17|\\x18|\\x19|\\x1a|\\x1b|\\x1c|\\x1d|\\x1e|\\x1f)"
    );
    
    // XML/XXE Detection Patterns
    private static final Pattern XML_ATTACK_PATTERN = Pattern.compile(
        "(?i)(<!DOCTYPE|<!ENTITY|SYSTEM|PUBLIC|file://|http://|https://|ftp://" +
        "|jar://|netdoc://|gopher://|&[a-zA-Z0-9]+;|&#[0-9]+;|&#x[a-fA-F0-9]+;)"
    );
    
    // NoSQL Injection Detection Patterns
    private static final Pattern NOSQL_INJECTION_PATTERN = Pattern.compile(
        "(?i)(\\$where|\\$ne|\\$in|\\$nin|\\$not|\\$or|\\$and|\\$nor|\\$exists" +
        "|\\$type|\\$mod|\\$regex|\\$text|\\$search|\\$near|\\$geoWithin" +
        "|\\$geoIntersects|\\$elemMatch|\\$size|\\$all|\\$slice|javascript:|sleep\\()"
    );
    
    // Dangerous file extensions
    private static final Set<String> DANGEROUS_FILE_EXTENSIONS = new HashSet<>(Arrays.asList(
        "exe", "bat", "cmd", "com", "pif", "scr", "vbs", "js", "jar", "app", "deb", "pkg",
        "dmg", "rpm", "msi", "run", "bin", "sh", "ps1", "psm1", "psd1", "ps1xml",
        "csh", "fish", "zsh", "bash", "jsp", "php", "asp", "aspx", "war", "ear"
    ));
    
    /**
     * Validates input against all known attack vectors.
     */
    public ValidationResult validateInput(String input, ValidationContext context) {
        if (input == null) {
            return ValidationResult.success();
        }
        
        ValidationResult result = new ValidationResult();
        String cleanInput = input.trim();
        
        // Check each attack vector
        checkSQLInjection(cleanInput, result, context);
        checkXSS(cleanInput, result, context);
        checkCommandInjection(cleanInput, result, context);
        checkPathTraversal(cleanInput, result, context);
        checkLDAPInjection(cleanInput, result, context);
        checkXMLAttacks(cleanInput, result, context);
        checkNoSQLInjection(cleanInput, result, context);
        
        // Additional context-specific validations
        if (context != null) {
            checkContextSpecific(cleanInput, result, context);
        }
        
        // Log security events if threats detected
        if (result.hasThreat()) {
            SecureCodingUtils.safeLog(logger, "WARN", 
                "Security threat detected in input validation: {} threats found in context: {}", 
                result.getThreatTypes().size(), context != null ? context.getType() : "unknown");
        }
        
        return result;
    }
    
    private void checkSQLInjection(String input, ValidationResult result, ValidationContext context) {
        if (SQL_INJECTION_PATTERN.matcher(input).find()) {
            result.addThreat(ThreatType.SQL_INJECTION, "Potential SQL injection detected");
            SecureCodingUtils.safeLog(logger, "WARN", "SQL injection pattern detected in input");
        }
    }
    
    private void checkXSS(String input, ValidationResult result, ValidationContext context) {
        if (XSS_PATTERN.matcher(input).find()) {
            result.addThreat(ThreatType.XSS, "Potential XSS attack detected");
            SecureCodingUtils.safeLog(logger, "WARN", "XSS pattern detected in input");
        }
    }
    
    private void checkCommandInjection(String input, ValidationResult result, ValidationContext context) {
        if (COMMAND_INJECTION_PATTERN.matcher(input).find()) {
            result.addThreat(ThreatType.COMMAND_INJECTION, "Potential command injection detected");
            SecureCodingUtils.safeLog(logger, "WARN", "Command injection pattern detected in input");
        }
    }
    
    private void checkPathTraversal(String input, ValidationResult result, ValidationContext context) {
        if (PATH_TRAVERSAL_PATTERN.matcher(input).find()) {
            result.addThreat(ThreatType.PATH_TRAVERSAL, "Potential path traversal detected");
            SecureCodingUtils.safeLog(logger, "WARN", "Path traversal pattern detected in input");
        }
    }
    
    private void checkLDAPInjection(String input, ValidationResult result, ValidationContext context) {
        if (LDAP_INJECTION_PATTERN.matcher(input).find()) {
            result.addThreat(ThreatType.LDAP_INJECTION, "Potential LDAP injection detected");
            SecureCodingUtils.safeLog(logger, "WARN", "LDAP injection pattern detected in input");
        }
    }
    
    private void checkXMLAttacks(String input, ValidationResult result, ValidationContext context) {
        if (XML_ATTACK_PATTERN.matcher(input).find()) {
            result.addThreat(ThreatType.XML_ATTACK, "Potential XML/XXE attack detected");
            SecureCodingUtils.safeLog(logger, "WARN", "XML attack pattern detected in input");
        }
    }
    
    private void checkNoSQLInjection(String input, ValidationResult result, ValidationContext context) {
        if (NOSQL_INJECTION_PATTERN.matcher(input).find()) {
            result.addThreat(ThreatType.NOSQL_INJECTION, "Potential NoSQL injection detected");
            SecureCodingUtils.safeLog(logger, "WARN", "NoSQL injection pattern detected in input");
        }
    }
    
    private void checkContextSpecific(String input, ValidationResult result, ValidationContext context) {
        switch (context.getType()) {
            case EMAIL:
                validateEmail(input, result);
                break;
            case URL:
                validateURL(input, result);
                break;
            case FILENAME:
                validateFilename(input, result);
                break;
            case USERNAME:
                validateUsername(input, result);
                break;
            case PASSWORD:
                validatePassword(input, result, context);
                break;
            case BACKUP_PATH:
                validateBackupPath(input, result);
                break;
            case S3_BUCKET_NAME:
                validateS3BucketName(input, result);
                break;
        }
    }
    
    private void validateEmail(String input, ValidationResult result) {
        if (!isValidEmail(input)) {
            result.addThreat(ThreatType.INVALID_FORMAT, "Invalid email format");
        }
    }
    
    private void validateURL(String input, ValidationResult result) {
        try {
            new URL(input);
            // Check for suspicious schemes
            if (input.toLowerCase().startsWith("javascript:") || 
                input.toLowerCase().startsWith("data:") ||
                input.toLowerCase().startsWith("vbscript:")) {
                result.addThreat(ThreatType.XSS, "Suspicious URL scheme");
            }
        } catch (MalformedURLException e) {
            result.addThreat(ThreatType.INVALID_FORMAT, "Invalid URL format");
        }
    }
    
    private void validateFilename(String input, ValidationResult result) {
        // Check for dangerous file extensions
        String extension = getFileExtension(input);
        if (DANGEROUS_FILE_EXTENSIONS.contains(extension.toLowerCase())) {
            result.addThreat(ThreatType.DANGEROUS_FILE, "Potentially dangerous file extension");
        }
        
        // Check for invalid filename characters
        if (input.matches(".*[<>:\"/\\\\|?*].*")) {
            result.addThreat(ThreatType.INVALID_FORMAT, "Invalid filename characters");
        }
    }
    
    private void validateUsername(String input, ValidationResult result) {
        // Username should be alphanumeric with limited special characters
        if (!input.matches("^[a-zA-Z0-9._-]{3,50}$")) {
            result.addThreat(ThreatType.INVALID_FORMAT, "Invalid username format");
        }
    }
    
    private void validatePassword(String input, ValidationResult result, ValidationContext context) {
        // Password strength validation
        if (input.length() < 8) {
            result.addThreat(ThreatType.WEAK_PASSWORD, "Password too short");
        }
        
        if (!input.matches(".*[A-Z].*")) {
            result.addThreat(ThreatType.WEAK_PASSWORD, "Password missing uppercase letter");
        }
        
        if (!input.matches(".*[a-z].*")) {
            result.addThreat(ThreatType.WEAK_PASSWORD, "Password missing lowercase letter");
        }
        
        if (!input.matches(".*[0-9].*")) {
            result.addThreat(ThreatType.WEAK_PASSWORD, "Password missing number");
        }
        
        if (!input.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\|,.<>/?].*")) {
            result.addThreat(ThreatType.WEAK_PASSWORD, "Password missing special character");
        }
    }
    
    private void validateBackupPath(String input, ValidationResult result) {
        // Ensure path doesn't contain suspicious patterns
        if (!input.matches("^[a-zA-Z]:\\\\[^<>:\"/|?*]*$") && !input.matches("^/[^<>:\"|?*]*$")) {
            result.addThreat(ThreatType.INVALID_FORMAT, "Invalid backup path format");
        }
    }
    
    private void validateS3BucketName(String input, ValidationResult result) {
        // S3 bucket name validation according to AWS rules
        if (!input.matches("^[a-z0-9.-]{3,63}$") || 
            input.startsWith(".") || input.endsWith(".") ||
            input.contains("..") || input.matches(".*\\d+\\.\\d+\\.\\d+\\.\\d+.*")) {
            result.addThreat(ThreatType.INVALID_FORMAT, "Invalid S3 bucket name format");
        }
    }
    
    /**
     * Sanitizes input by encoding potentially dangerous characters.
     */
    public String sanitizeInput(String input, SanitizationContext context) {
        if (input == null) {
            return null;
        }
        
        switch (context) {
            case HTML:
                return Encode.forHtml(input);
            case HTML_ATTRIBUTE:
                return Encode.forHtmlAttribute(input);
            case JAVASCRIPT:
                return Encode.forJavaScript(input);
            case CSS:
                return Encode.forCssString(input);
            case URL:
                return Encode.forUriComponent(input);
            case XML:
                return Encode.forXml(input);
            case XML_ATTRIBUTE:
                return Encode.forXmlAttribute(input);
            case SQL:
                // Use parameterized queries instead of sanitization
                return input.replaceAll("'", "''");
            default:
                return Encode.forHtml(input);
        }
    }
    
    private boolean isValidEmail(String email) {
        return email != null && 
               email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$") &&
               email.length() <= 254;
    }
    
    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot >= 0 ? filename.substring(lastDot + 1) : "";
    }
    
    public enum SanitizationContext {
        HTML, HTML_ATTRIBUTE, JAVASCRIPT, CSS, URL, XML, XML_ATTRIBUTE, SQL
    }
}