package com.amdocs.telecom.model;

/**
 * Enum representing service types.
 */
public enum ServiceType {
    MOBILE("Mobile"),
    BROADBAND("Broadband"),
    ENTERPRISE_CONNECTIVITY("Enterprise Connectivity"),
    VPN("VPN"),
    CLOUD_CONNECTIVITY("Cloud Connectivity");

    private final String description;

    ServiceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static ServiceType fromString(String value) {
        for (ServiceType type : ServiceType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown service type: " + value);
    }
}
