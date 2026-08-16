package com.amdocs.telecom.model;

/**
 * Enum representing customer types in the system.
 */
public enum CustomerType {
    CONSUMER("Consumer"),
    SME("Small/Medium Enterprise"),
    ENTERPRISE("Enterprise");

    private final String description;

    CustomerType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static CustomerType fromString(String value) {
        for (CustomerType type : CustomerType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown customer type: " + value);
    }
}
