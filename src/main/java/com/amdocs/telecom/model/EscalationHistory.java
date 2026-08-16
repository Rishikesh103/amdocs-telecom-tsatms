package com.amdocs.telecom.model;

import java.time.LocalDateTime;

/**
 * Entity tracking escalation history for tickets.
 */
public class EscalationHistory {
    private int escalationId;
    private int ticketId;
    private String fromLevel;
    private String toLevel;
    private String reason;
    private LocalDateTime escalationDate;
    private String escalatedBy;

    // ========== Constructors ==========
    public EscalationHistory() {
    }

    public EscalationHistory(int ticketId, String fromLevel, String toLevel,
                             String reason, String escalatedBy) {
        this.ticketId = ticketId;
        this.fromLevel = fromLevel;
        this.toLevel = toLevel;
        this.reason = reason;
        this.escalatedBy = escalatedBy;
        this.escalationDate = LocalDateTime.now();
    }

    // ========== Getters & Setters ==========
    public int getEscalationId() {
        return escalationId;
    }

    public void setEscalationId(int escalationId) {
        this.escalationId = escalationId;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public String getFromLevel() {
        return fromLevel;
    }

    public void setFromLevel(String fromLevel) {
        this.fromLevel = fromLevel;
    }

    public String getToLevel() {
        return toLevel;
    }

    public void setToLevel(String toLevel) {
        this.toLevel = toLevel;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getEscalationDate() {
        return escalationDate;
    }

    public void setEscalationDate(LocalDateTime escalationDate) {
        this.escalationDate = escalationDate;
    }

    public String getEscalatedBy() {
        return escalatedBy;
    }

    public void setEscalatedBy(String escalatedBy) {
        this.escalatedBy = escalatedBy;
    }

    @Override
    public String toString() {
        return "EscalationHistory{" +
                "escalationId=" + escalationId +
                ", ticketId=" + ticketId +
                ", fromLevel='" + fromLevel + '\'' +
                ", toLevel='" + toLevel + '\'' +
                ", escalationDate=" + escalationDate +
                '}';
    }
}
