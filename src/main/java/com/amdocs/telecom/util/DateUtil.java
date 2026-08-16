package com.amdocs.telecom.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for date and time operations.
 */
public class DateUtil {
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Formats a LocalDateTime to string.
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }
        return dateTime.format(FORMATTER);
    }

    /**
     * Formats a LocalDateTime to date-only string.
     */
    public static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }
        return dateTime.format(DATE_ONLY_FORMATTER);
    }

    /**
     * Calculates the difference in minutes between two LocalDateTime objects.
     */
    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.MINUTES.between(start, end);
    }

    /**
     * Calculates the difference in hours between two LocalDateTime objects.
     */
    public static long hoursBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.HOURS.between(start, end);
    }

    /**
     * Calculates the difference in days between two LocalDateTime objects.
     */
    public static long daysBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * Adds minutes to a LocalDateTime.
     */
    public static LocalDateTime addMinutes(LocalDateTime dateTime, long minutes) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.plus(minutes, ChronoUnit.MINUTES);
    }

    /**
     * Adds hours to a LocalDateTime.
     */
    public static LocalDateTime addHours(LocalDateTime dateTime, long hours) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.plus(hours, ChronoUnit.HOURS);
    }

    /**
     * Checks if the given deadline has been exceeded.
     */
    public static boolean isDeadlineExceeded(LocalDateTime deadline) {
        if (deadline == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(deadline);
    }

    /**
     * Checks if deadline is within the next 30 minutes (at risk).
     */
    public static boolean isAtRisk(LocalDateTime deadline) {
        if (deadline == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyMinutesFromNow = now.plus(30, ChronoUnit.MINUTES);
        return deadline.isAfter(now) && deadline.isBefore(thirtyMinutesFromNow);
    }

    /**
     * Gets remaining minutes until deadline.
     */
    public static long getRemainingMinutes(LocalDateTime deadline) {
        if (deadline == null) {
            return 0;
        }
        long minutes = minutesBetween(LocalDateTime.now(), deadline);
        return minutes < 0 ? 0 : minutes;
    }
}
