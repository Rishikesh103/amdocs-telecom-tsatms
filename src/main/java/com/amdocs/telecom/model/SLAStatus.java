package com.amdocs.telecom.model;

/**
 * Enum representing SLA status.
 */
public enum SLAStatus {
    WITHIN_SLA("Within SLA"),
    AT_RISK("At Risk"),
    BREACHED("Breached");

    private final String description;

    SLAStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static SLAStatus fromString(String value) {
        for (SLAStatus status : SLAStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown SLA status: " + value);
    }
}
