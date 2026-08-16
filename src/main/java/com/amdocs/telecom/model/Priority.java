package com.amdocs.telecom.model;

/**
 * Enum representing ticket priority levels.
 */
public enum Priority {
    LOW("Low", 4),
    MEDIUM("Medium", 3),
    HIGH("High", 2),
    CRITICAL("Critical", 1);

    private final String description;
    private final int level;

    Priority(String description, int level) {
        this.description = description;
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public int getLevel() {
        return level;
    }

    public static Priority fromString(String value) {
        for (Priority p : Priority.values()) {
            if (p.name().equalsIgnoreCase(value)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Unknown priority: " + value);
    }
}
