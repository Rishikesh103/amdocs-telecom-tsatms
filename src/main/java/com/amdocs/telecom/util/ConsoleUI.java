package com.amdocs.telecom.util;

import java.util.List;

/**
 * Enterprise Professional CLI Design System
 * Clean, minimalistic terminal interface inspired by modern corporate developer tools (GitHub CLI / Stripe CLI / Claude CLI).
 */
public class ConsoleUI {

    // ANSI Colors - Subtle & Professional
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String DIM = "\u001B[2m";

    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    public static final String BRIGHT_RED = "\u001B[91m";
    public static final String BRIGHT_GREEN = "\u001B[92m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_CYAN = "\u001B[96m";
    public static final String BRIGHT_WHITE = "\u001B[97m";

    public static String repeatStr(String str, int count) {
        if (count <= 0) return "";
        StringBuilder sb = new StringBuilder(str.length() * count);
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    public static void printMainBanner() {
        System.out.println();
        System.out.println(BRIGHT_CYAN + BOLD + "================================================================================" + RESET);
        System.out.println(BRIGHT_WHITE + BOLD + "  Amdocs TSATMS | Telecom Service Assurance & Incident Management System" + RESET);
        System.out.println(DIM + "  Enterprise Automated NOC & Trouble Ticketing Platform | v1.0.0" + RESET);
        System.out.println(BRIGHT_CYAN + BOLD + "================================================================================" + RESET);
        System.out.println();
    }

    public static void printHeader(String title, String subtitle) {
        System.out.println();
        System.out.println(CYAN + BOLD + "--- [ " + title.toUpperCase() + " ] " + repeatStr("-", Math.max(2, 74 - title.length() - 8)) + RESET);
        if (subtitle != null && !subtitle.isEmpty()) {
            System.out.println(DIM + "    " + subtitle + RESET);
        }
        System.out.println();
    }

    public static void printSection(String title) {
        System.out.println();
        System.out.println(BRIGHT_WHITE + BOLD + title + ":" + RESET);
        System.out.println(DIM + repeatStr("-", 45) + RESET);
    }

    public static void printMenuOption(String key, String label) {
        System.out.printf("  %s[%s]%s %s\n", BRIGHT_CYAN + BOLD, key, RESET, label);
    }

    public static void printPrompt(String label) {
        System.out.print("\n" + BRIGHT_WHITE + label + ": " + RESET);
    }

    // Professional Status Badges
    public static String getPriorityBadge(String priority) {
        if (priority == null) return "N/A";
        switch (priority.toUpperCase()) {
            case "CRITICAL":
                return BRIGHT_RED + BOLD + "CRITICAL" + RESET;
            case "HIGH":
                return RED + "HIGH" + RESET;
            case "MEDIUM":
                return YELLOW + "MEDIUM" + RESET;
            case "LOW":
                return GREEN + "LOW" + RESET;
            default:
                return priority;
        }
    }

    public static String getStatusBadge(String status) {
        if (status == null) return "N/A";
        switch (status.toUpperCase()) {
            case "OPEN":
                return CYAN + "OPEN" + RESET;
            case "ASSIGNED":
                return BLUE + "ASSIGNED" + RESET;
            case "IN_PROGRESS":
                return YELLOW + BOLD + "IN_PROGRESS" + RESET;
            case "PENDING_CUSTOMER":
                return DIM + "PENDING_CUST" + RESET;
            case "ESCALATED":
                return BRIGHT_RED + BOLD + "ESCALATED" + RESET;
            case "RESOLVED":
                return GREEN + BOLD + "RESOLVED" + RESET;
            case "CLOSED":
                return DIM + "CLOSED" + RESET;
            case "CANCELLED":
                return DIM + RED + "CANCELLED" + RESET;
            default:
                return status;
        }
    }

    public static String getSLABadge(String slaStatus) {
        if (slaStatus == null) return "N/A";
        switch (slaStatus.toUpperCase()) {
            case "COMPLIANT":
            case "ON_TRACK":
                return GREEN + "ON_TRACK" + RESET;
            case "AT_RISK":
            case "SLA_WARNING":
                return BRIGHT_YELLOW + BOLD + "AT_RISK" + RESET;
            case "BREACHED":
            case "SLA_BREACH":
                return BRIGHT_RED + BOLD + "BREACHED" + RESET;
            default:
                return slaStatus;
        }
    }

    public static void printCard(String title, List<String> lines) {
        int maxLen = title.length();
        for (String l : lines) {
            String stripped = stripAnsi(l);
            if (stripped.length() > maxLen) maxLen = stripped.length();
        }
        maxLen = Math.max(maxLen + 4, 50);

        System.out.println(CYAN + "+-- [ " + BOLD + title + RESET + CYAN + " ] " + repeatStr("-", Math.max(0, maxLen - title.length() - 8)) + "+" + RESET);
        for (String l : lines) {
            int visualLen = stripAnsi(l).length();
            int pad = Math.max(0, maxLen - visualLen - 2);
            System.out.println(CYAN + "| " + RESET + l + repeatStr(" ", pad) + CYAN + " |" + RESET);
        }
        System.out.println(CYAN + "+" + repeatStr("-", maxLen) + "+" + RESET);
    }

    public static void printMetric(String label, String value) {
        System.out.printf("  * %-30s : %s%s%s\n", label, BOLD + BRIGHT_WHITE, value, RESET);
    }

    public static void printProgressBar(String label, int current, int total) {
        int barLength = 20;
        double percent = total > 0 ? ((double) current / total) : 0;
        int filled = (int) (percent * barLength);
        
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < barLength; i++) {
            if (i < filled) bar.append("#");
            else bar.append("-");
        }
        
        System.out.printf("  * %-20s : [%s] %3.0f%% (%d of %d)\n",
                label, bar.toString(), percent * 100, current, total);
    }

    public static void printSuccess(String msg) {
        System.out.println(GREEN + "[SUCCESS] " + RESET + msg);
    }

    public static void printError(String msg) {
        System.out.println(RED + "[ERROR] " + RESET + msg);
    }

    public static void printWarning(String msg) {
        System.out.println(YELLOW + "[WARNING] " + RESET + msg);
    }

    public static void printInfo(String msg) {
        System.out.println(CYAN + "[INFO] " + RESET + msg);
    }

    public static void printDivider() {
        System.out.println(DIM + "--------------------------------------------------------------------------------" + RESET);
    }

    private static String stripAnsi(String text) {
        if (text == null) return "";
        return text.replaceAll("\u001B\\[[;\\d]*m", "");
    }
}
