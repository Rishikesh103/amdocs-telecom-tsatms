package com.amdocs.telecom.model;

import java.time.LocalDateTime;

/**
 * Entity tracking all status changes of a ticket for audit trail and history.
 */
public class TicketStatusHistory {
    private int historyId;
    private int ticketId;
    private String oldStatus;
    private String newStatus;
    private String changedBy;
    private LocalDateTime changedDate;
    private String remarks;

    // ========== Constructors ==========
    public TicketStatusHistory() {
    }

    public TicketStatusHistory(int ticketId, String oldStatus, String newStatus,
                               String changedBy, String remarks) {
        this.ticketId = ticketId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.remarks = remarks;
        this.changedDate = LocalDateTime.now();
    }

    // ========== Getters & Setters ==========
    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public LocalDateTime getChangedDate() {
        return changedDate;
    }

    public void setChangedDate(LocalDateTime changedDate) {
        this.changedDate = changedDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "TicketStatusHistory{" +
                "historyId=" + historyId +
                ", ticketId=" + ticketId +
                ", oldStatus='" + oldStatus + '\'' +
                ", newStatus='" + newStatus + '\'' +
                ", changedBy='" + changedBy + '\'' +
                ", changedDate=" + changedDate +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
