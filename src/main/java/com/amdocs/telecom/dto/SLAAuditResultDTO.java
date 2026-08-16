package com.amdocs.telecom.dto;

import com.amdocs.telecom.model.Priority;
import com.amdocs.telecom.model.TicketStatus;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for SLA Audit Results
 */
public class SLAAuditResultDTO {
    private int ticketId;
    private String ticketNumber;
    private String category;
    private Priority priority;
    private TicketStatus status;
    private LocalDateTime slaDeadline;
    private long remainingMinutes;
    private String slaHealth; // ON_TRACK, AT_RISK, BREACHED
    private String actionTaken;

    public SLAAuditResultDTO() {}

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public LocalDateTime getSlaDeadline() {
        return slaDeadline;
    }

    public void setSlaDeadline(LocalDateTime slaDeadline) {
        this.slaDeadline = slaDeadline;
    }

    public long getRemainingMinutes() {
        return remainingMinutes;
    }

    public void setRemainingMinutes(long remainingMinutes) {
        this.remainingMinutes = remainingMinutes;
    }

    public String getSlaHealth() {
        return slaHealth;
    }

    public void setSlaHealth(String slaHealth) {
        this.slaHealth = slaHealth;
    }

    public String getActionTaken() {
        return actionTaken;
    }

    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }
}
