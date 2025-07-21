package com.noahbackup.appsec.validation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Result object for input validation operations.
 * 
 * Contains information about:
 * - Validation success/failure status
 * - Detected threat types and descriptions
 * - Validation timestamp
 * - Severity levels for threats
 */
public class ValidationResult {
    
    private boolean valid = true;
    private List<ThreatDetail> threats = new ArrayList<>();
    private LocalDateTime validationTime = LocalDateTime.now();
    
    public static ValidationResult success() {
        return new ValidationResult();
    }
    
    public static ValidationResult failure(ThreatType threatType, String description) {
        ValidationResult result = new ValidationResult();
        result.addThreat(threatType, description);
        return result;
    }
    
    public void addThreat(ThreatType threatType, String description) {
        addThreat(threatType, description, ThreatSeverity.MEDIUM);
    }
    
    public void addThreat(ThreatType threatType, String description, ThreatSeverity severity) {
        this.valid = false;
        this.threats.add(new ThreatDetail(threatType, description, severity));
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public boolean hasThreat() {
        return !threats.isEmpty();
    }
    
    public List<ThreatDetail> getThreats() {
        return new ArrayList<>(threats);
    }
    
    public Set<ThreatType> getThreatTypes() {
        Set<ThreatType> types = new HashSet<>();
        for (ThreatDetail threat : threats) {
            types.add(threat.getType());
        }
        return types;
    }
    
    public boolean hasThreatType(ThreatType threatType) {
        return threats.stream().anyMatch(t -> t.getType() == threatType);
    }
    
    public ThreatSeverity getHighestSeverity() {
        return threats.stream()
            .map(ThreatDetail::getSeverity)
            .max(ThreatSeverity::compareTo)
            .orElse(ThreatSeverity.LOW);
    }
    
    public int getThreatCount() {
        return threats.size();
    }
    
    public LocalDateTime getValidationTime() {
        return validationTime;
    }
    
    public String getThreatsDescription() {
        if (threats.isEmpty()) {
            return "No threats detected";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < threats.size(); i++) {
            if (i > 0) sb.append("; ");
            ThreatDetail threat = threats.get(i);
            sb.append(threat.getType().name()).append(": ").append(threat.getDescription());
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return String.format("ValidationResult{valid=%s, threats=%d, highestSeverity=%s}", 
            valid, threats.size(), hasThreat() ? getHighestSeverity() : "NONE");
    }
    
    /**
     * Inner class representing a specific threat detected during validation.
     */
    public static class ThreatDetail {
        private final ThreatType type;
        private final String description;
        private final ThreatSeverity severity;
        private final LocalDateTime detectionTime;
        
        public ThreatDetail(ThreatType type, String description, ThreatSeverity severity) {
            this.type = type;
            this.description = description;
            this.severity = severity;
            this.detectionTime = LocalDateTime.now();
        }
        
        public ThreatType getType() {
            return type;
        }
        
        public String getDescription() {
            return description;
        }
        
        public ThreatSeverity getSeverity() {
            return severity;
        }
        
        public LocalDateTime getDetectionTime() {
            return detectionTime;
        }
        
        @Override
        public String toString() {
            return String.format("%s [%s]: %s", type, severity, description);
        }
    }
}