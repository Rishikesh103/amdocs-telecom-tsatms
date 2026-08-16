package com.amdocs.telecom.model;

/**
 * Enum representing user roles in the system.
 */
public enum UserRole {
    CUSTOMER("Customer"),
    SERVICE_DESK_ADMIN("Service Desk Administrator"),
    NETWORK_ENGINEER("Network Engineer"),
    NETWORK_MANAGER("Network Manager");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static UserRole fromString(String value) {
        for (UserRole role : UserRole.values()) {
            if (role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown user role: " + value);
    }
}
