package com.amdocs.telecom.model;

import java.time.LocalDateTime;

/**
 * Entity representing a system notification for users about ticket events.
 */
public class Notification {
    private int notificationId;
    private String recipientId;
    private Integer ticketId;
    private String message;
    private NotificationType notificationType;
    private LocalDateTime createdDate;
    private int readStatus; // 0 = unread, 1 = read

    // ========== Constructors ==========
    public Notification() {
    }

    public Notification(String recipientId, Integer ticketId, String message,
                        NotificationType notificationType) {
        this.recipientId = recipientId;
        this.ticketId = ticketId;
        this.message = message;
        this.notificationType = notificationType;
        this.createdDate = LocalDateTime.now();
        this.readStatus = 0;
    }

    // ========== Getters & Setters ==========
    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public Integer getTicketId() {
        return ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public int getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(int readStatus) {
        this.readStatus = readStatus;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", recipientId='" + recipientId + '\'' +
                ", notificationType=" + notificationType +
                ", createdDate=" + createdDate +
                ", readStatus=" + readStatus +
                '}';
    }
}
