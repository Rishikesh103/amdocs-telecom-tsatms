package com.amdocs.telecom.model;

/**
 * Enum representing escalation levels.
 */
public enum EscalationLevel {
    ENGINEER("Engineer", 1),
    TEAM_LEAD("Team Lead", 2),
    NETWORK_MANAGER("Network Manager", 3),
    OPERATIONS_MANAGER("Operations Manager", 4);

    private final String description;
    private final int level;

    EscalationLevel(String description, int level) {
        this.description = description;
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public int getLevel() {
        return level;
    }

    public static EscalationLevel fromString(String value) {
        for (EscalationLevel level : EscalationLevel.values()) {
            if (level.name().equalsIgnoreCase(value)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Unknown escalation level: " + value);
    }
}
