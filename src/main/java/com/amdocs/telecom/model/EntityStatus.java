package com.amdocs.telecom.model;

/**
 * Enum representing customer/service entity status.
 */
public enum EntityStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    SUSPENDED("Suspended");

    private final String description;

    EntityStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static EntityStatus fromString(String value) {
        for (EntityStatus status : EntityStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown entity status: " + value);
    }
}
