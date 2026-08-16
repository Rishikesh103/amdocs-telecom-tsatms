package com.amdocs.telecom.dto;

import com.amdocs.telecom.model.AvailabilityStatus;

public class EngineerWorkloadDTO {
    private Integer engineerId;
    private String employeeCode;
    private String engineerName;
    private String specialization;
    private String region;
    private AvailabilityStatus availability;
    private int activeTicketCount;
    private int criticalTicketCount;

    public EngineerWorkloadDTO() {}

    public Integer getEngineerId() { return engineerId; }
    public void setEngineerId(Integer engineerId) { this.engineerId = engineerId; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public String getEngineerName() { return engineerName; }
    public void setEngineerName(String engineerName) { this.engineerName = engineerName; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public AvailabilityStatus getAvailability() { return availability; }
    public void setAvailability(AvailabilityStatus availability) { this.availability = availability; }

    public int getActiveTicketCount() { return activeTicketCount; }
    public void setActiveTicketCount(int activeTicketCount) { this.activeTicketCount = activeTicketCount; }

    public int getCriticalTicketCount() { return criticalTicketCount; }
    public void setCriticalTicketCount(int criticalTicketCount) { this.criticalTicketCount = criticalTicketCount; }
}
