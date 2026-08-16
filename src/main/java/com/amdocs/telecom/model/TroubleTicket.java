package com.amdocs.telecom.model;

import java.time.LocalDateTime;

/**
 * Entity representing a Trouble Ticket - the core business entity for incident/fault management.
 * Contains all ticket lifecycle information, SLA tracking, and resolution details.
 */
public class TroubleTicket {
    private int ticketId;
    private String ticketNumber;
    private int customerId;
    private int serviceId;
    private String category; // IncidentCategory as string
    private String description;
    private Priority priority;
    private String severity;
    private TicketStatus status;
    private Integer assignedEngineerId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private LocalDateTime slaDeadline;
    private LocalDateTime resolutionDate;
    private String rootCause;
    private String resolutionText;
    private String resolutionCode;

    // ========== Constructors ==========
    public TroubleTicket() {
    }

    public TroubleTicket(String ticketNumber, int customerId, int serviceId,
                         String category, String description, Priority priority,
                         String severity) {
        this.ticketNumber = ticketNumber;
        this.customerId = customerId;
        this.serviceId = serviceId;
        this.category = category;
        this.description = description;
        this.priority = priority;
        this.severity = severity;
        this.status = TicketStatus.OPEN;
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    // ========== Getters & Setters ==========
    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public Integer getAssignedEngineerId() {
        return assignedEngineerId;
    }

    public void setAssignedEngineerId(Integer assignedEngineerId) {
        this.assignedEngineerId = assignedEngineerId;
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

    public LocalDateTime getSlaDeadline() {
        return slaDeadline;
    }

    public void setSlaDeadline(LocalDateTime slaDeadline) {
        this.slaDeadline = slaDeadline;
    }

    public LocalDateTime getResolutionDate() {
        return resolutionDate;
    }

    public void setResolutionDate(LocalDateTime resolutionDate) {
        this.resolutionDate = resolutionDate;
    }

    public String getRootCause() {
        return rootCause;
    }

    public void setRootCause(String rootCause) {
        this.rootCause = rootCause;
    }

    public String getResolutionText() {
        return resolutionText;
    }

    public void setResolutionText(String resolutionText) {
        this.resolutionText = resolutionText;
    }

    public String getResolutionCode() {
        return resolutionCode;
    }

    public void setResolutionCode(String resolutionCode) {
        this.resolutionCode = resolutionCode;
    }

    @Override
    public String toString() {
        return "TroubleTicket{" +
                "ticketId=" + ticketId +
                ", ticketNumber='" + ticketNumber + '\'' +
                ", customerId=" + customerId +
                ", serviceId=" + serviceId +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", priority=" + priority +
                ", severity='" + severity + '\'' +
                ", status=" + status +
                ", assignedEngineerId=" + assignedEngineerId +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                ", slaDeadline=" + slaDeadline +
                ", resolutionDate=" + resolutionDate +
                '}';
    }
}
