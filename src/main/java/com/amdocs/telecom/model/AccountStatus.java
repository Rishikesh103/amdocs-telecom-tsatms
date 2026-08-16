package com.amdocs.telecom.model;

/**
 * Enum representing account status.
 */
public enum AccountStatus {
    ACTIVE("Active"),
    LOCKED("Locked"),
    INACTIVE("Inactive");

    private final String description;

    AccountStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static AccountStatus fromString(String value) {
        for (AccountStatus status : AccountStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown account status: " + value);
    }
}
