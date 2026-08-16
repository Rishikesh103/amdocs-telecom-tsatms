package com.amdocs.telecom.model;

import java.time.LocalDateTime;

/**
 * Entity representing a Network Engineer who resolves tickets.
 * Tracks specialization, region, experience, availability, and current workload.
 */
public class NetworkEngineer {
    private int engineerId;
    private String employeeCode;
    private String engineerName;
    private String specialization;
    private String region;
    private int experienceYears;
    private AvailabilityStatus availability;
    private int activeTicketCount;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    // ========== Constructors ==========
    public NetworkEngineer() {
    }

    public NetworkEngineer(String employeeCode, String engineerName, String specialization,
                           String region, int experienceYears) {
        this.employeeCode = employeeCode;
        this.engineerName = engineerName;
        this.specialization = specialization;
        this.region = region;
        this.experienceYears = experienceYears;
        this.availability = AvailabilityStatus.AVAILABLE;
        this.activeTicketCount = 0;
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    // ========== Getters & Setters ==========
    public int getEngineerId() {
        return engineerId;
    }

    public void setEngineerId(int engineerId) {
        this.engineerId = engineerId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getEngineerName() {
        return engineerName;
    }

    public void setEngineerName(String engineerName) {
        this.engineerName = engineerName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public AvailabilityStatus getAvailability() {
        return availability;
    }

    public void setAvailability(AvailabilityStatus availability) {
        this.availability = availability;
    }

    public int getActiveTicketCount() {
        return activeTicketCount;
    }

    public void setActiveTicketCount(int activeTicketCount) {
        this.activeTicketCount = activeTicketCount;
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
        return "NetworkEngineer{" +
                "engineerId=" + engineerId +
                ", employeeCode='" + employeeCode + '\'' +
                ", engineerName='" + engineerName + '\'' +
                ", specialization='" + specialization + '\'' +
                ", region='" + region + '\'' +
                ", experienceYears=" + experienceYears +
                ", availability=" + availability +
                ", activeTicketCount=" + activeTicketCount +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                '}';
    }
}
