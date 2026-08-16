package com.amdocs.telecom.model;

/**
 * Enum representing notification type.
 */
public enum NotificationType {
    TICKET_CREATION("Ticket Creation"),
    ENGINEER_ASSIGNMENT("Engineer Assignment"),
    SLA_WARNING("SLA Warning"),
    SLA_BREACH("SLA Breach"),
    TICKET_RESOLUTION("Ticket Resolution"),
    TICKET_CLOSURE("Ticket Closure"),
    ESCALATION("Escalation"),
    TICKET_ASSIGNMENT("Ticket Assignment");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static NotificationType fromString(String value) {
        for (NotificationType type : NotificationType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown notification type: " + value);
    }
}
