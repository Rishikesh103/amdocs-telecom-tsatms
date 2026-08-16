package com.amdocs.telecom.model;

/**
 * Enum representing incident/problem categories.
 */
public enum IncidentCategory {
    NETWORK_OUTAGE("Network Outage"),
    CALL_DROP("Call Drop"),
    SLOW_DATA("Slow Data"),
    NO_CONNECTIVITY("No Connectivity"),
    SIM_ISSUE("SIM Issue"),
    BILLING("Billing"),
    BROADBAND("Broadband"),
    ROAMING("Roaming"),
    ENTERPRISE_LINK("Enterprise Link"),
    OTHER("Other");

    private final String description;

    IncidentCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static IncidentCategory fromString(String value) {
        for (IncidentCategory cat : IncidentCategory.values()) {
            if (cat.name().equalsIgnoreCase(value)) {
                return cat;
            }
        }
        throw new IllegalArgumentException("Unknown incident category: " + value);
    }
}
