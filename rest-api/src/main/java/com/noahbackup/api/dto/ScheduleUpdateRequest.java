package com.noahbackup.api.dto;

import jakarta.validation.constraints.Pattern;

/**
 * Data Transfer Object for schedule update requests.
 */
public class ScheduleUpdateRequest {
    
    @Pattern(regexp = "^[0-9*,/\\-\\s]+$", message = "Invalid cron expression format")
    private String dailySchedule;
    
    @Pattern(regexp = "^[0-9*,/\\-\\s]+$", message = "Invalid cron expression format")
    private String weeklySchedule;
    
    private Boolean enabled;
    private Boolean weeklyEnabled;
    private Integer timeoutMinutes;
    
    // Constructors
    public ScheduleUpdateRequest() {}
    
    public ScheduleUpdateRequest(String dailySchedule, String weeklySchedule) {
        this.dailySchedule = dailySchedule;
        this.weeklySchedule = weeklySchedule;
    }
    
    // Getters and setters
    public String getDailySchedule() { return dailySchedule; }
    public void setDailySchedule(String dailySchedule) { this.dailySchedule = dailySchedule; }
    
    public String getWeeklySchedule() { return weeklySchedule; }
    public void setWeeklySchedule(String weeklySchedule) { this.weeklySchedule = weeklySchedule; }
    
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    
    public Boolean getWeeklyEnabled() { return weeklyEnabled; }
    public void setWeeklyEnabled(Boolean weeklyEnabled) { this.weeklyEnabled = weeklyEnabled; }
    
    public Integer getTimeoutMinutes() { return timeoutMinutes; }
    public void setTimeoutMinutes(Integer timeoutMinutes) { this.timeoutMinutes = timeoutMinutes; }
    
    @Override
    public String toString() {
        return String.format("ScheduleUpdateRequest{daily='%s', weekly='%s', enabled=%s}", 
                           dailySchedule, weeklySchedule, enabled);
    }
}