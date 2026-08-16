package com.amdocs.telecom.model;

/**
 * Enum representing login status/result.
 */
public enum LoginStatus {
    SUCCESS("Success"),
    FAILED("Failed"),
    LOCKED("Locked");

    private final String description;

    LoginStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static LoginStatus fromString(String value) {
        for (LoginStatus status : LoginStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown login status: " + value);
    }
}
