package com.amdocs.telecom.model;

import java.time.LocalDateTime;

/**
 * Entity representing SLA Configuration for each priority level.
 * Stores response and resolution SLAs for tickets.
 */
public class SLAConfiguration {
    private int slaConfigId;
    private Priority priority;
    private int responseSlaMinutes;
    private int resolutionSlaHours;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    // ========== Constructors ==========
    public SLAConfiguration() {
    }

    public SLAConfiguration(Priority priority, int responseSlaMinutes, int resolutionSlaHours) {
        this.priority = priority;
        this.responseSlaMinutes = responseSlaMinutes;
        this.resolutionSlaHours = resolutionSlaHours;
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    // ========== Getters & Setters ==========
    public int getSlaConfigId() {
        return slaConfigId;
    }

    public void setSlaConfigId(int slaConfigId) {
        this.slaConfigId = slaConfigId;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public int getResponseSlaMinutes() {
        return responseSlaMinutes;
    }

    public void setResponseSlaMinutes(int responseSlaMinutes) {
        this.responseSlaMinutes = responseSlaMinutes;
    }

    public int getResolutionSlaHours() {
        return resolutionSlaHours;
    }

    public void setResolutionSlaHours(int resolutionSlaHours) {
        this.resolutionSlaHours = resolutionSlaHours;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Override
    public String toString() {
        return "SLAConfiguration{" +
                "slaConfigId=" + slaConfigId +
                ", priority=" + priority +
                ", responseSlaMinutes=" + responseSlaMinutes +
                ", resolutionSlaHours=" + resolutionSlaHours +
                '}';
    }
}
