package com.amdocs.telecom.controller;

import com.amdocs.telecom.model.NetworkEngineer;
import com.amdocs.telecom.model.Priority;
import com.amdocs.telecom.model.ResolutionCode;
import com.amdocs.telecom.model.SLAStatus;
import com.amdocs.telecom.model.TicketStatus;
import com.amdocs.telecom.model.TroubleTicket;
import com.amdocs.telecom.model.UserAccount;
import com.amdocs.telecom.service.EngineerService;
import com.amdocs.telecom.service.TroubleTicketService;
import com.amdocs.telecom.service.impl.EngineerServiceImpl;
import com.amdocs.telecom.service.impl.TroubleTicketServiceImpl;
import com.amdocs.telecom.util.ConsoleUI;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Network Engineer Workbench Controller
 * Professional Enterprise Design with Interactive Ticket Selection
 */
public class EngineerController {

    private final EngineerService engineerService;
    private final TroubleTicketService ticketService;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public EngineerController() {
        this.engineerService = new EngineerServiceImpl();
        this.ticketService = new TroubleTicketServiceImpl();
    }

    public boolean handleMenuChoice(String choice, UserAccount currentUser, Scanner scanner) {
        try {
            NetworkEngineer engineer = engineerService.getEngineerByUserId(currentUser.getUserId());
            int engineerId = (engineer != null) ? engineer.getEngineerId() : (currentUser.getLinkedId() != null ? currentUser.getLinkedId() : 1);

            switch (choice) {
                case "1":
                    viewAssignedTickets(engineerId);
                    break;
                case "2":
                    updateTicketStatus(engineerId, currentUser.getUsername(), scanner);
                    break;
                case "3":
                    addResolution(engineerId, scanner);
                    break;
                case "4":
                    viewTicketDetails(engineerId, scanner);
                    break;
                case "5":
                    checkSLAStatus(engineerId);
                    break;
                case "6":
                case "0":
                    ConsoleUI.printSuccess("Logged out successfully. Returned to main gateway.");
                    return false;
                default:
                    ConsoleUI.printError("Invalid option. Please select 1-6 (or 0 to Sign Out).");
            }
        } catch (Exception e) {
            ConsoleUI.printError("Engineer Operation Failed: " + e.getMessage());
        }
        return true;
    }

    private void viewAssignedTickets(int engineerId) throws Exception {
        ConsoleUI.printHeader("Assigned Trouble Tickets", "Active incidents in your queue");
        List<TroubleTicket> tickets = engineerService.getAssignedTickets(engineerId);
        if (tickets.isEmpty()) {
            ConsoleUI.printSuccess("No assigned tickets. Your queue is clear.");
            return;
        }
        
        System.out.printf("  %-5s %-16s %-16s %-12s %-16s %-16s\n",
                "ID", "TICKET NUMBER", "CATEGORY", "PRIORITY", "STATUS", "SLA DEADLINE");
        ConsoleUI.printDivider();
        for (TroubleTicket t : tickets) {
            String deadline = t.getSlaDeadline() != null ? t.getSlaDeadline().format(DATE_FMT) : "N/A";
            System.out.printf("  %-5d %-16s %-16s %-12s %-16s %-16s\n",
                    t.getTicketId(),
                    t.getTicketNumber(),
                    t.getCategory(),
                    ConsoleUI.getPriorityBadge(t.getPriority().name()),
                    ConsoleUI.getStatusBadge(t.getStatus().name()),
                    deadline);
        }
    }

    private TroubleTicket selectAssignedTicket(int engineerId, Scanner scanner, String headerTitle) throws Exception {
        List<TroubleTicket> tickets = engineerService.getAssignedTickets(engineerId);
        if (tickets.isEmpty()) {
            ConsoleUI.printInfo("No active assigned tickets found in your queue.");
            return null;
        }

        ConsoleUI.printHeader(headerTitle, "Select a ticket from your assigned queue or enter ticket number/ID");
        for (int i = 0; i < tickets.size(); i++) {
            TroubleTicket t = tickets.get(i);
            System.out.printf("  [%d] %-15s | %-16s | %-8s | %s\n",
                    (i + 1),
                    t.getTicketNumber(),
                    t.getCategory(),
                    ConsoleUI.getPriorityBadge(t.getPriority().name()),
                    ConsoleUI.getStatusBadge(t.getStatus().name()));
        }
        System.out.println("  [0] Cancel / Go Back");

        ConsoleUI.printPrompt("Select option (1-" + tickets.size() + ", Ticket #, or 0 to Cancel)");
        String input = scanner.nextLine().trim();
        if ("0".equals(input) || "back".equalsIgnoreCase(input) || input.isEmpty()) {
            ConsoleUI.printInfo("Action canceled.");
            return null;
        }

        try {
            int idx = Integer.parseInt(input);
            if (idx >= 1 && idx <= tickets.size()) {
                return tickets.get(idx - 1);
            }
        } catch (NumberFormatException ignored) {}

        for (TroubleTicket t : tickets) {
            if (t.getTicketNumber().equalsIgnoreCase(input)) return t;
        }

        try {
            int id = Integer.parseInt(input);
            for (TroubleTicket t : tickets) {
                if (t.getTicketId() == id) return t;
            }
        } catch (NumberFormatException ignored) {}

        // Global fallback search
        TroubleTicket t = ticketService.getTicketByNumber(input);
        if (t != null) return t;

        ConsoleUI.printError("Ticket not found: " + input);
        return null;
    }

    private void updateTicketStatus(int engineerId, String engineerName, Scanner scanner) throws Exception {
        TroubleTicket ticket = selectAssignedTicket(engineerId, scanner, "Update Ticket Status");
        if (ticket == null) return;
        int tktId = ticket.getTicketId();
        
        ConsoleUI.printSection("Select Workflow State Transition");
        System.out.println("  [1] IN_PROGRESS      (Investigation & active repairs underway)");
        System.out.println("  [2] PENDING_CUSTOMER (Awaiting customer input/confirmation)");
        System.out.println("  [3] RESOLVED         (Fix applied, pending Service Desk closure)");
        System.out.println("  [0] Cancel");
        ConsoleUI.printPrompt("Select status (1-3 or 0 to Cancel)");
        String sChoice = scanner.nextLine().trim();
        if ("0".equals(sChoice) || "back".equalsIgnoreCase(sChoice)) {
            ConsoleUI.printInfo("Action canceled.");
            return;
        }
        TicketStatus newStatus = TicketStatus.IN_PROGRESS;
        if ("2".equals(sChoice)) newStatus = TicketStatus.PENDING_CUSTOMER;
        else if ("3".equals(sChoice)) newStatus = TicketStatus.RESOLVED;

        ConsoleUI.printPrompt("Enter work log / diagnostic remarks");
        String remarks = scanner.nextLine().trim();

        ticketService.updateTicketStatus(tktId, newStatus, engineerName, remarks);
        ConsoleUI.printSuccess("Status updated to " + newStatus + " for Ticket " + ticket.getTicketNumber() + ".");
    }

    private void addResolution(int engineerId, Scanner scanner) throws Exception {
        TroubleTicket ticket = selectAssignedTicket(engineerId, scanner, "Submit Resolution & Root Cause Analysis (RCA)");
        if (ticket == null) return;
        int tktId = ticket.getTicketId();
        
        ConsoleUI.printSection("Root Cause & Resolution Submission");
        ConsoleUI.printPrompt("Enter Root Cause Analysis (RCA explanation)");
        String rootCause = scanner.nextLine().trim();
        if ("0".equals(rootCause) || "back".equalsIgnoreCase(rootCause)) {
            ConsoleUI.printInfo("Action canceled.");
            return;
        }
        
        ConsoleUI.printPrompt("Enter Corrective Action / Resolution Description");
        String resText = scanner.nextLine().trim();
        if ("0".equals(resText) || "back".equalsIgnoreCase(resText)) {
            ConsoleUI.printInfo("Action canceled.");
            return;
        }

        ConsoleUI.printSection("Select Resolution Classification Code (Case Study Section 10)");
        System.out.println("  [1] HARDWARE_FAILURE     (Router / Card / Switch replacement)");
        System.out.println("  [2] CONFIGURATION_ERROR  (BGP / VLAN / Routing fix)");
        System.out.println("  [3] NETWORK_CONGESTION   (Bandwidth throttled / QoS tuning)");
        System.out.println("  [4] SOFTWARE_FAILURE     (Firmware bug / Service restart)");
        System.out.println("  [5] FIBER_CUT            (Physical cable spliced / repaired)");
        System.out.println("  [6] POWER_FAILURE        (UPS / Generator power restored)");
        System.out.println("  [0] Cancel");
        ConsoleUI.printPrompt("Select code (1-6 or 0 to Cancel)");
        String rChoice = scanner.nextLine().trim();
        if ("0".equals(rChoice) || "back".equalsIgnoreCase(rChoice)) {
            ConsoleUI.printInfo("Action canceled.");
            return;
        }
        ResolutionCode code = ResolutionCode.HARDWARE_FAILURE;
        switch (rChoice) {
            case "2": code = ResolutionCode.CONFIGURATION_ERROR; break;
            case "3": code = ResolutionCode.NETWORK_CONGESTION; break;
            case "4": code = ResolutionCode.SOFTWARE_FAILURE; break;
            case "5": code = ResolutionCode.FIBER_CUT; break;
            case "6": code = ResolutionCode.POWER_FAILURE; break;
        }

        ticketService.addResolution(tktId, resText, rootCause, code, engineerId);
        ConsoleUI.printSuccess("Resolution submitted for Ticket " + ticket.getTicketNumber() + ".");
        ConsoleUI.printCard("Resolution Confirmation", Arrays.asList(
                "Ticket Number : " + ticket.getTicketNumber(),
                "Status        : " + ConsoleUI.getStatusBadge("RESOLVED"),
                "RCA Code      : " + code.name(),
                "Root Cause    : " + rootCause,
                "Action Taken  : " + resText,
                "Workload      : Active queue count decremented"
        ));
    }

    private void viewTicketDetails(int engineerId, Scanner scanner) throws Exception {
        TroubleTicket t = selectAssignedTicket(engineerId, scanner, "View Ticket Technical Details");
        if (t == null) return;

        ConsoleUI.printCard("Ticket Details: " + t.getTicketNumber(), Arrays.asList(
                "Ticket ID    : " + t.getTicketId(),
                "Category     : " + t.getCategory(),
                "Priority     : " + ConsoleUI.getPriorityBadge(t.getPriority().name()),
                "Status       : " + ConsoleUI.getStatusBadge(t.getStatus().name()),
                "Description  : " + t.getDescription(),
                "Created Date : " + (t.getCreatedDate() != null ? t.getCreatedDate().format(DATE_FMT) : "N/A"),
                "SLA Deadline : " + (t.getSlaDeadline() != null ? t.getSlaDeadline().format(DATE_FMT) : "N/A"),
                "Root Cause   : " + (t.getRootCause() != null ? t.getRootCause() : "In progress / Diagnostic stage"),
                "Resolution   : " + (t.getResolutionText() != null ? t.getResolutionText() : "Pending resolution")
        ));
    }

    private void checkSLAStatus(int engineerId) throws Exception {
        ConsoleUI.printHeader("SLA Compliance Status & Deadlines", "Live countdown targets for tickets assigned to you");
        List<TroubleTicket> tickets = engineerService.getAssignedTickets(engineerId);
        if (tickets.isEmpty()) {
            ConsoleUI.printSuccess("No active tickets. All SLA targets compliant.");
            return;
        }
        
        System.out.printf("  %-16s %-16s %-10s %-16s %-16s %-12s\n",
                "TICKET NUMBER", "CATEGORY", "PRIORITY", "SLA DEADLINE", "REMAINING TIME", "SLA HEALTH");
        ConsoleUI.printDivider();
        
        LocalDateTime now = LocalDateTime.now();
        int urgentCount = 0;
        int breachedCount = 0;

        for (TroubleTicket t : tickets) {
            String timeStr = t.getSlaDeadline() != null ? t.getSlaDeadline().format(DATE_FMT) : "N/A";
            String remTime = "N/A";
            String risk;

            if (t.getSlaDeadline() != null) {
                long diffMinutes = java.time.Duration.between(now, t.getSlaDeadline()).toMinutes();
                if (diffMinutes < 0) {
                    remTime = ConsoleUI.RED + (diffMinutes * -1) + "m OVERDUE" + ConsoleUI.RESET;
                    risk = ConsoleUI.BRIGHT_RED + "BREACHED" + ConsoleUI.RESET;
                    breachedCount++;
                } else if (diffMinutes <= 30) {
                    remTime = ConsoleUI.YELLOW + diffMinutes + "m (Expires Soon)" + ConsoleUI.RESET;
                    risk = ConsoleUI.BRIGHT_YELLOW + "AT_RISK (<30m)" + ConsoleUI.RESET;
                    urgentCount++;
                } else {
                    long hrs = diffMinutes / 60;
                    long mins = diffMinutes % 60;
                    remTime = (hrs > 0 ? hrs + "h " : "") + mins + "m";
                    risk = ConsoleUI.GREEN + "ON_TRACK" + ConsoleUI.RESET;
                }
            } else {
                risk = ConsoleUI.GREEN + "ON_TRACK" + ConsoleUI.RESET;
            }
            
            System.out.printf("  %-16s %-16s %-10s %-16s %-16s %-12s\n",
                    t.getTicketNumber(),
                    t.getCategory(),
                    ConsoleUI.getPriorityBadge(t.getPriority().name()),
                    timeStr,
                    remTime,
                    risk);
        }

        ConsoleUI.printDivider();
        if (breachedCount > 0 || urgentCount > 0) {
            ConsoleUI.printWarning("You have " + (breachedCount + urgentCount) + " ticket(s) requiring immediate attention to prevent SLA penalties.");
        } else {
            ConsoleUI.printSuccess("All assigned tickets are compliant and on schedule.");
        }
    }
}
