package com.amdocs.telecom.model;

/**
 * Enum representing resolution codes.
 */
public enum ResolutionCode {
    HARDWARE_FAILURE("Hardware Failure"),
    CONFIGURATION_ERROR("Configuration Error"),
    NETWORK_CONGESTION("Network Congestion"),
    SOFTWARE_FAILURE("Software Failure"),
    FIBER_CUT("Fiber Cut"),
    POWER_FAILURE("Power Failure"),
    CUSTOMER_DEVICE("Customer Device"),
    UNKNOWN("Unknown");

    private final String description;

    ResolutionCode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static ResolutionCode fromString(String value) {
        for (ResolutionCode code : ResolutionCode.values()) {
            if (code.name().equalsIgnoreCase(value)) {
                return code;
            }
        }
        throw new IllegalArgumentException("Unknown resolution code: " + value);
    }
}
