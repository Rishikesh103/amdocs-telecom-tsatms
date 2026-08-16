package com.amdocs.telecom.model;

import java.time.LocalDateTime;

/**
 * Entity for customer feedback on resolved tickets.
 */
public class Feedback {
    private int feedbackId;
    private int ticketId;
    private int customerId;
    private int rating; // 1-5 scale
    private String comments;
    private LocalDateTime createdDate;

    // ========== Constructors ==========
    public Feedback() {
    }

    public Feedback(int ticketId, int customerId, int rating, String comments) {
        this.ticketId = ticketId;
        this.customerId = customerId;
        this.rating = rating;
        this.comments = comments;
        this.createdDate = LocalDateTime.now();
    }

    // ========== Getters & Setters ==========
    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "feedbackId=" + feedbackId +
                ", ticketId=" + ticketId +
                ", customerId=" + customerId +
                ", rating=" + rating +
                ", createdDate=" + createdDate +
                '}';
    }
}
