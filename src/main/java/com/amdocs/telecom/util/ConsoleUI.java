package com.amdocs.telecom.util;

import java.util.List;

/**
 * High-Tech Cyberpunk & Telecom NOC Terminal UI Framework (Java 8 Compatible)
 * Provides rich ANSI color palettes, box frames, status badges, progress bars, and formatted tables.
 */
public class ConsoleUI {

    // =========================================================================
    // ANSI COLOR & STYLE CODES
    // =========================================================================
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String DIM = "\u001B[2m";
    public static final String ITALIC = "\u001B[3m";
    public static final String UNDERLINE = "\u001B[4m";

    // Foreground Colors
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    // Bright/Neon Colors
    public static final String BRIGHT_RED = "\u001B[91m";
    public static final String BRIGHT_GREEN = "\u001B[92m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_BLUE = "\u001B[94m";
    public static final String BRIGHT_MAGENTA = "\u001B[95m";
    public static final String BRIGHT_CYAN = "\u001B[96m";
    public static final String BRIGHT_WHITE = "\u001B[97m";

    // Background Colors
    public static final String BG_BLACK = "\u001B[40m";
    public static final String BG_RED = "\u001B[41m";
    public static final String BG_GREEN = "\u001B[42m";
    public static final String BG_YELLOW = "\u001B[43m";
    public static final String BG_BLUE = "\u001B[44m";
    public static final String BG_MAGENTA = "\u001B[45m";
    public static final String BG_CYAN = "\u001B[46m";
    public static final String BG_DARK_GRAY = "\u001B[100m";

    // =========================================================================
    // JAVA 8 COMPATIBLE STRING REPEAT HELPER
    // =========================================================================
    public static String repeatStr(String str, int count) {
        if (count <= 0) return "";
        StringBuilder sb = new StringBuilder(str.length() * count);
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    // =========================================================================
    // BANNER & HEADER METHODS
    // =========================================================================
    public static void printMainBanner() {
        System.out.println(BRIGHT_CYAN + BOLD);
        System.out.println("  ████████╗███████╗ █████╗ ████████╗███╗   ███╗███████╗");
        System.out.println("  ╚══██╔══╝██╔════╝██╔══██╗╚══██╔══╝████╗ ████║██╔════╝");
        System.out.println("     ██║   ███████╗███████║   ██║   ██╔████╔██║███████╗");
        System.out.println("     ██║   ╚════██║██╔══██║   ██║   ██║╚██╔╝██║╚════██║");
        System.out.println("     ██║   ███████║██║  ██║   ██║   ██║ ╚═╝ ██║███████║");
        System.out.println("     ╚═╝   ╚══════╝╚═╝  ╚═╝   ╚═╝   ╚═╝     ╚═╝╚══════╝" + RESET);
        System.out.println(BRIGHT_MAGENTA + "  ┌─────────────────────────────────────────────────────────────┐" + RESET);
        System.out.println(BRIGHT_MAGENTA + "  │ " + BRIGHT_WHITE + BOLD + "   TELECOM SERVICE ASSURANCE & INCIDENT COMMAND CENTER   " + BRIGHT_MAGENTA + "│" + RESET);
        System.out.println(BRIGHT_MAGENTA + "  │ " + CYAN + "     Next-Gen Automated NOC & Trouble Ticketing v2.5     " + BRIGHT_MAGENTA + "│" + RESET);
        System.out.println(BRIGHT_MAGENTA + "  └─────────────────────────────────────────────────────────────┘" + RESET);
        System.out.println();
    }

    public static void printHeader(String title, String subtitle) {
        int width = 70;
        String line = repeatStr("═", width);
        System.out.println("\n" + BRIGHT_CYAN + "╔" + line + "╗" + RESET);
        
        String centeredTitle = centerText(title.toUpperCase(), width);
        System.out.println(BRIGHT_CYAN + "║" + BRIGHT_WHITE + BOLD + centeredTitle + BRIGHT_CYAN + "║" + RESET);
        
        if (subtitle != null && !subtitle.isEmpty()) {
            String centeredSub = centerText(subtitle, width);
            System.out.println(BRIGHT_CYAN + "║" + DIM + CYAN + centeredSub + BRIGHT_CYAN + "║" + RESET);
        }
        System.out.println(BRIGHT_CYAN + "╚" + line + "╝" + RESET);
    }

    public static void printSection(String title) {
        System.out.println("\n" + BRIGHT_YELLOW + BOLD + "▶ " + title + RESET);
        System.out.println(DIM + repeatStr("─", 50) + RESET);
    }

    public static void printMenuOption(String key, String label, String icon) {
        System.out.printf("  %s[%s]%s %s %s%-35s%s\n", 
                BRIGHT_CYAN + BOLD, key, RESET, 
                icon != null ? icon : "•",
                BRIGHT_WHITE, label, RESET);
    }

    public static void printPrompt(String label) {
        System.out.print("\n" + BRIGHT_GREEN + BOLD + " ➜ " + BRIGHT_WHITE + label + ": " + BRIGHT_YELLOW);
    }

    // =========================================================================
    // STATUS BADGES
    // =========================================================================
    public static String getPriorityBadge(String priority) {
        if (priority == null) return "[ N/A ]";
        switch (priority.toUpperCase()) {
            case "CRITICAL":
                return BG_RED + BRIGHT_WHITE + BOLD + " ⚡ CRITICAL " + RESET;
            case "HIGH":
                return BRIGHT_RED + BOLD + "[▲ HIGH]" + RESET;
            case "MEDIUM":
                return BRIGHT_YELLOW + BOLD + "[● MEDIUM]" + RESET;
            case "LOW":
                return BRIGHT_GREEN + BOLD + "[▼ LOW]" + RESET;
            default:
                return "[" + priority + "]";
        }
    }

    public static String getStatusBadge(String status) {
        if (status == null) return "[ N/A ]";
        switch (status.toUpperCase()) {
            case "OPEN":
                return BRIGHT_CYAN + BOLD + "[ ✦ OPEN ]" + RESET;
            case "ASSIGNED":
                return BRIGHT_BLUE + BOLD + "[ ➔ ASSIGNED ]" + RESET;
            case "IN_PROGRESS":
                return BRIGHT_YELLOW + BOLD + "[ ⚙ IN PROGRESS ]" + RESET;
            case "PENDING_CUSTOMER":
                return BRIGHT_MAGENTA + BOLD + "[ ⌛ PENDING CUST ]" + RESET;
            case "ESCALATED":
                return BG_RED + BRIGHT_WHITE + BOLD + " 🚨 ESCALATED " + RESET;
            case "RESOLVED":
                return BRIGHT_GREEN + BOLD + "[ ✔ RESOLVED ]" + RESET;
            case "CLOSED":
                return DIM + WHITE + "[ 🔒 CLOSED ]" + RESET;
            case "CANCELLED":
                return RED + "[ ✖ CANCELLED ]" + RESET;
            default:
                return "[" + status + "]";
        }
    }

    public static String getSLABadge(String slaStatus) {
        if (slaStatus == null) return "[ N/A ]";
        switch (slaStatus.toUpperCase()) {
            case "COMPLIANT":
            case "ON_TRACK":
                return BRIGHT_GREEN + "● ON TRACK" + RESET;
            case "AT_RISK":
            case "SLA_WARNING":
                return BRIGHT_YELLOW + BOLD + "▲ AT RISK" + RESET;
            case "BREACHED":
            case "SLA_BREACH":
                return BRIGHT_RED + BOLD + "✖ BREACHED" + RESET;
            default:
                return slaStatus;
        }
    }

    // =========================================================================
    // CARDS & BOXES
    // =========================================================================
    public static void printCard(String title, List<String> lines, String color) {
        int maxLen = title.length();
        for (String l : lines) {
            if (l.length() > maxLen) maxLen = l.length();
        }
        maxLen = Math.max(maxLen + 4, 45);

        System.out.println(color + "╭─ " + BOLD + title + " " + repeatStr("─", Math.max(0, maxLen - title.length() - 3)) + "╮" + RESET);
        for (String l : lines) {
            System.out.printf(color + "│ " + RESET + "%-" + (maxLen - 2) + "s " + color + "│\n" + RESET, l);
        }
        System.out.println(color + "╰" + repeatStr("─", maxLen) + "╯" + RESET);
    }

    public static void printMetricCard(String label, String value, String color, String icon) {
        System.out.printf("  %s┌─────────────────────────────────────┐%s\n", color, RESET);
        System.out.printf("  %s│ %s %-25s %s │%s\n", color, icon, label, color, RESET);
        System.out.printf("  %s│ %s%s %-32s%s%s│%s\n", color, color, BOLD, value, RESET, color, RESET);
        System.out.printf("  %s└─────────────────────────────────────┘%s\n", color, RESET);
    }

    public static void printProgressBar(String label, int current, int total, String color) {
        int barLength = 25;
        double percent = total > 0 ? ((double) current / total) : 0;
        int filled = (int) (percent * barLength);
        
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < barLength; i++) {
            if (i < filled) bar.append("█");
            else bar.append("░");
        }
        
        System.out.printf("  %-15s %s[%s]%s %3.0f%% (%d/%d)\n",
                label, color, bar.toString(), RESET, percent * 100, current, total);
    }

    // =========================================================================
    // NOTIFICATIONS & MESSAGES
    // =========================================================================
    public static void printSuccess(String msg) {
        System.out.println("\n" + BRIGHT_GREEN + BOLD + " ✔ [SUCCESS] " + RESET + BRIGHT_WHITE + msg + RESET);
    }

    public static void printError(String msg) {
        System.out.println("\n" + BRIGHT_RED + BOLD + " ✖ [ERROR] " + RESET + RED + msg + RESET);
    }

    public static void printWarning(String msg) {
        System.out.println("\n" + BRIGHT_YELLOW + BOLD + " ⚠ [ALERT] " + RESET + YELLOW + msg + RESET);
    }

    public static void printInfo(String msg) {
        System.out.println(BRIGHT_CYAN + " ℹ [INFO] " + RESET + WHITE + msg + RESET);
    }

    public static void printDivider() {
        System.out.println(DIM + "──────────────────────────────────────────────────────────────────────────────────" + RESET);
    }

    private static String centerText(String text, int width) {
        if (text.length() >= width) return text.substring(0, width);
        int left = (width - text.length()) / 2;
        int right = width - text.length() - left;
        return repeatStr(" ", left) + text + repeatStr(" ", right);
    }
}
