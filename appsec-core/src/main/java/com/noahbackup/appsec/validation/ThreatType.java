package com.noahbackup.appsec.validation;

/**
 * Enumeration of security threat types that can be detected during input validation.
 * 
 * Covers major attack vectors from OWASP Top 10 and other common security threats.
 */
public enum ThreatType {
    
    // Injection Attacks
    SQL_INJECTION("SQL Injection", "Attempt to inject malicious SQL code", ThreatSeverity.HIGH),
    NOSQL_INJECTION("NoSQL Injection", "Attempt to inject malicious NoSQL queries", ThreatSeverity.HIGH),
    COMMAND_INJECTION("Command Injection", "Attempt to inject system commands", ThreatSeverity.CRITICAL),
    LDAP_INJECTION("LDAP Injection", "Attempt to inject LDAP queries", ThreatSeverity.HIGH),
    
    // Cross-Site Scripting
    XSS("Cross-Site Scripting", "Attempt to inject malicious scripts", ThreatSeverity.HIGH),
    STORED_XSS("Stored XSS", "Persistent cross-site scripting attack", ThreatSeverity.CRITICAL),
    REFLECTED_XSS("Reflected XSS", "Reflected cross-site scripting attack", ThreatSeverity.HIGH),
    DOM_XSS("DOM-based XSS", "DOM-based cross-site scripting attack", ThreatSeverity.HIGH),
    
    // Path and File Attacks
    PATH_TRAVERSAL("Path Traversal", "Attempt to access files outside allowed directories", ThreatSeverity.HIGH),
    LOCAL_FILE_INCLUSION("Local File Inclusion", "Attempt to include local files", ThreatSeverity.HIGH),
    REMOTE_FILE_INCLUSION("Remote File Inclusion", "Attempt to include remote files", ThreatSeverity.CRITICAL),
    DANGEROUS_FILE("Dangerous File Upload", "Upload of potentially malicious file", ThreatSeverity.HIGH),
    
    // XML Attacks
    XML_ATTACK("XML Attack", "Malicious XML content detected", ThreatSeverity.HIGH),
    XXE("XML External Entity", "XML External Entity injection attempt", ThreatSeverity.CRITICAL),
    XML_BOMB("XML Bomb", "XML bomb denial-of-service attempt", ThreatSeverity.HIGH),
    
    // Authentication and Authorization
    WEAK_PASSWORD("Weak Password", "Password does not meet security requirements", ThreatSeverity.MEDIUM),
    BRUTE_FORCE("Brute Force Attack", "Multiple failed authentication attempts", ThreatSeverity.HIGH),
    SESSION_HIJACKING("Session Hijacking", "Suspicious session activity", ThreatSeverity.CRITICAL),
    PRIVILEGE_ESCALATION("Privilege Escalation", "Attempt to gain elevated privileges", ThreatSeverity.CRITICAL),
    
    // Data Validation
    INVALID_FORMAT("Invalid Format", "Input does not match expected format", ThreatSeverity.LOW),
    MALFORMED_DATA("Malformed Data", "Data structure is corrupted or malformed", ThreatSeverity.MEDIUM),
    ENCODING_ATTACK("Encoding Attack", "Malicious character encoding detected", ThreatSeverity.HIGH),
    
    // Denial of Service
    DOS_ATTACK("Denial of Service", "Potential denial-of-service attack", ThreatSeverity.HIGH),
    RESOURCE_EXHAUSTION("Resource Exhaustion", "Attempt to exhaust system resources", ThreatSeverity.HIGH),
    ALGORITHMIC_COMPLEXITY("Algorithmic Complexity", "Input designed to cause high CPU usage", ThreatSeverity.MEDIUM),
    
    // Business Logic
    BUSINESS_LOGIC_BYPASS("Business Logic Bypass", "Attempt to bypass business rules", ThreatSeverity.HIGH),
    RATE_LIMIT_EXCEEDED("Rate Limit Exceeded", "Request rate limit exceeded", ThreatSeverity.MEDIUM),
    SUSPICIOUS_PATTERN("Suspicious Pattern", "Suspicious behavior pattern detected", ThreatSeverity.MEDIUM),
    
    // Data Exfiltration
    DATA_EXFILTRATION("Data Exfiltration", "Potential data theft attempt", ThreatSeverity.CRITICAL),
    SENSITIVE_DATA_EXPOSURE("Sensitive Data Exposure", "Sensitive information detected in input", ThreatSeverity.HIGH),
    
    // Other Threats
    UNKNOWN_THREAT("Unknown Threat", "Unclassified security threat", ThreatSeverity.MEDIUM),
    POLICY_VIOLATION("Policy Violation", "Input violates security policy", ThreatSeverity.LOW);
    
    private final String displayName;
    private final String description;
    private final ThreatSeverity defaultSeverity;
    
    ThreatType(String displayName, String description, ThreatSeverity defaultSeverity) {
        this.displayName = displayName;
        this.description = description;
        this.defaultSeverity = defaultSeverity;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public ThreatSeverity getDefaultSeverity() {
        return defaultSeverity;
    }
    
    /**
     * Check if this threat type is injection-related.
     */
    public boolean isInjectionThreat() {
        return this == SQL_INJECTION || this == NOSQL_INJECTION || 
               this == COMMAND_INJECTION || this == LDAP_INJECTION;
    }
    
    /**
     * Check if this threat type is XSS-related.
     */
    public boolean isXSSThreat() {
        return this == XSS || this == STORED_XSS || 
               this == REFLECTED_XSS || this == DOM_XSS;
    }
    
    /**
     * Check if this threat type is file-related.
     */
    public boolean isFileThreat() {
        return this == PATH_TRAVERSAL || this == LOCAL_FILE_INCLUSION || 
               this == REMOTE_FILE_INCLUSION || this == DANGEROUS_FILE;
    }
    
    /**
     * Check if this threat type requires immediate action.
     */
    public boolean requiresImmediateAction() {
        return defaultSeverity == ThreatSeverity.CRITICAL || 
               defaultSeverity == ThreatSeverity.HIGH;
    }
    
    /**
     * Get threat category for grouping and reporting.
     */
    public ThreatCategory getCategory() {
        if (isInjectionThreat()) return ThreatCategory.INJECTION;
        if (isXSSThreat()) return ThreatCategory.XSS;
        if (isFileThreat()) return ThreatCategory.FILE_SECURITY;
        if (this == XML_ATTACK || this == XXE || this == XML_BOMB) return ThreatCategory.XML;
        if (this == WEAK_PASSWORD || this == BRUTE_FORCE || this == SESSION_HIJACKING || this == PRIVILEGE_ESCALATION) return ThreatCategory.AUTHENTICATION;
        if (this == DOS_ATTACK || this == RESOURCE_EXHAUSTION || this == ALGORITHMIC_COMPLEXITY) return ThreatCategory.DOS;
        if (this == DATA_EXFILTRATION || this == SENSITIVE_DATA_EXPOSURE) return ThreatCategory.DATA_PROTECTION;
        return ThreatCategory.OTHER;
    }
    
    public enum ThreatCategory {
        INJECTION, XSS, FILE_SECURITY, XML, AUTHENTICATION, DOS, DATA_PROTECTION, OTHER
    }
}