package com.amdocs.telecom.dto;

import com.amdocs.telecom.model.Priority;
import com.amdocs.telecom.model.SLAStatus;
import com.amdocs.telecom.model.TicketStatus;

import java.time.LocalDateTime;

public class TicketSummaryDTO {
    private Integer ticketId;
    private String ticketNumber;
    private String customerName;
    private String serviceName;
    private String category;
    private Priority priority;
    private TicketStatus status;
    private SLAStatus slaStatus;
    private String assignedEngineerName;
    private LocalDateTime createdDate;
    private LocalDateTime slaDeadline;

    public TicketSummaryDTO() {}

    public Integer getTicketId() { return ticketId; }
    public void setTicketId(Integer ticketId) { this.ticketId = ticketId; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }

    public SLAStatus getSlaStatus() { return slaStatus; }
    public void setSlaStatus(SLAStatus slaStatus) { this.slaStatus = slaStatus; }

    public String getAssignedEngineerName() { return assignedEngineerName; }
    public void setAssignedEngineerName(String assignedEngineerName) { this.assignedEngineerName = assignedEngineerName; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getSlaDeadline() { return slaDeadline; }
    public void setSlaDeadline(LocalDateTime slaDeadline) { this.slaDeadline = slaDeadline; }
}
