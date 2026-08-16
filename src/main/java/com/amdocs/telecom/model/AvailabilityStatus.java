package com.amdocs.telecom.model;

/**
 * Enum representing engineer availability status.
 */
public enum AvailabilityStatus {
    AVAILABLE("Available"),
    BUSY("Busy"),
    ON_LEAVE("On Leave"),
    OFFLINE("Offline");

    private final String description;

    AvailabilityStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static AvailabilityStatus fromString(String value) {
        for (AvailabilityStatus status : AvailabilityStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown availability status: " + value);
    }
}
