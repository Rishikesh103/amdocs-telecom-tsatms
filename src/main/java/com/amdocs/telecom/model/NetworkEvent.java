package com.amdocs.telecom.model;

import java.time.LocalDateTime;

/**
 * Entity representing network events from monitoring systems that can trigger tickets.
 */
public class NetworkEvent {
    private int eventId;
    private String eventNumber;
    private String networkNode;
    private String eventType;
    private Severity severity;
    private LocalDateTime eventTime;
    private Integer ticketCreatedId;
    private int processed; // 0 = not processed, 1 = processed
    private LocalDateTime createdDate;

    // ========== Constructors ==========
    public NetworkEvent() {
    }

    public NetworkEvent(String eventNumber, String networkNode, String eventType,
                        Severity severity, LocalDateTime eventTime) {
        this.eventNumber = eventNumber;
        this.networkNode = networkNode;
        this.eventType = eventType;
        this.severity = severity;
        this.eventTime = eventTime;
        this.processed = 0;
        this.createdDate = LocalDateTime.now();
    }

    // ========== Getters & Setters ==========
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getEventNumber() {
        return eventNumber;
    }

    public void setEventNumber(String eventNumber) {
        this.eventNumber = eventNumber;
    }

    public String getNetworkNode() {
        return networkNode;
    }

    public void setNetworkNode(String networkNode) {
        this.networkNode = networkNode;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public Integer getTicketCreatedId() {
        return ticketCreatedId;
    }

    public void setTicketCreatedId(Integer ticketCreatedId) {
        this.ticketCreatedId = ticketCreatedId;
    }

    public int getProcessed() {
        return processed;
    }

    public void setProcessed(int processed) {
        this.processed = processed;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "NetworkEvent{" +
                "eventId=" + eventId +
                ", eventNumber='" + eventNumber + '\'' +
                ", networkNode='" + networkNode + '\'' +
                ", eventType='" + eventType + '\'' +
                ", severity=" + severity +
                ", eventTime=" + eventTime +
                ", processed=" + processed +
                '}';
    }
}
