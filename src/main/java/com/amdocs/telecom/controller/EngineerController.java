package com.amdocs.telecom.controller;

import com.amdocs.telecom.model.*;
import com.amdocs.telecom.service.*;
import com.amdocs.telecom.service.impl.*;
import com.amdocs.telecom.util.ConsoleUI;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Network Engineer Workbench Controller
 * Professional Enterprise Design
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
                    updateTicketStatus(currentUser.getUsername(), scanner);
                    break;
                case "3":
                    addResolution(engineerId, scanner);
                    break;
                case "4":
                    viewTicketDetails(scanner);
                    break;
                case "5":
                    checkSLAStatus(engineerId);
                    break;
                case "6":
                    ConsoleUI.printSuccess("Logged out successfully.");
                    return false;
                default:
                    ConsoleUI.printError("Invalid option. Please try again.");
            }
        } catch (Exception e) {
            ConsoleUI.printError("Engineer Operation Failed: " + e.getMessage());
        }
        return true;
    }

    private void viewAssignedTickets(int engineerId) throws Exception {
        ConsoleUI.printHeader("Assigned Trouble Tickets", "Active incidents assigned to engineer #" + engineerId);
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

    private int resolveTicketId(String input) throws Exception {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            TroubleTicket t = ticketService.getTicketByNumber(input);
            if (t != null) return t.getTicketId();
            throw new Exception("Ticket not found: " + input);
        }
    }

    private void updateTicketStatus(String engineerName, Scanner scanner) throws Exception {
        ConsoleUI.printHeader("Update Ticket Status", "Transition ticket workflow status");
        ConsoleUI.printPrompt("Enter Ticket Number or ID");
        int tktId = resolveTicketId(scanner.nextLine().trim());
        
        System.out.println("  [1] IN_PROGRESS (Investigation / Repair underway)");
        System.out.println("  [2] PENDING_CUSTOMER (Awaiting customer input/confirmation)");
        System.out.println("  [3] RESOLVED (Fix applied, pending closure)");
        ConsoleUI.printPrompt("Select status (1-3)");
        String sChoice = scanner.nextLine().trim();
        TicketStatus newStatus = TicketStatus.IN_PROGRESS;
        if ("2".equals(sChoice)) newStatus = TicketStatus.PENDING_CUSTOMER;
        else if ("3".equals(sChoice)) newStatus = TicketStatus.RESOLVED;

        ConsoleUI.printPrompt("Enter remarks");
        String remarks = scanner.nextLine().trim();

        ticketService.updateTicketStatus(tktId, newStatus, engineerName, remarks);
        ConsoleUI.printSuccess("Status updated to " + newStatus + ".");
    }

    private void addResolution(int engineerId, Scanner scanner) throws Exception {
        ConsoleUI.printHeader("Submit Resolution & Root Cause", "Record resolution details for incident");
        ConsoleUI.printPrompt("Enter Ticket Number or ID");
        int tktId = resolveTicketId(scanner.nextLine().trim());
        
        ConsoleUI.printPrompt("Enter Root Cause Analysis (RCA)");
        String rootCause = scanner.nextLine().trim();
        
        ConsoleUI.printPrompt("Enter Resolution Description");
        String resText = scanner.nextLine().trim();

        System.out.println("\nResolution Classification Code:");
        System.out.println("  [1] HARDWARE_FAILURE");
        System.out.println("  [2] CONFIGURATION_ERROR");
        System.out.println("  [3] NETWORK_CONGESTION");
        System.out.println("  [4] SOFTWARE_FAILURE");
        System.out.println("  [5] FIBER_CUT");
        System.out.println("  [6] POWER_FAILURE");
        ConsoleUI.printPrompt("Select code (1-6)");
        String rChoice = scanner.nextLine().trim();
        ResolutionCode code = ResolutionCode.HARDWARE_FAILURE;
        switch (rChoice) {
            case "2": code = ResolutionCode.CONFIGURATION_ERROR; break;
            case "3": code = ResolutionCode.NETWORK_CONGESTION; break;
            case "4": code = ResolutionCode.SOFTWARE_FAILURE; break;
            case "5": code = ResolutionCode.FIBER_CUT; break;
            case "6": code = ResolutionCode.POWER_FAILURE; break;
        }

        ticketService.addResolution(tktId, resText, rootCause, code, engineerId);
        ConsoleUI.printSuccess("Resolution submitted. Ticket marked RESOLVED and engineer workload updated.");
    }

    private void viewTicketDetails(Scanner scanner) throws Exception {
        ConsoleUI.printPrompt("Enter Ticket Number (e.g. TT-2026-004521)");
        String num = scanner.nextLine().trim();
        TroubleTicket t = ticketService.getTicketByNumber(num);
        if (t == null) {
            ConsoleUI.printError("Ticket not found: " + num);
            return;
        }
        ConsoleUI.printCard("Ticket Details: " + t.getTicketNumber(), Arrays.asList(
                "Ticket ID    : " + t.getTicketId(),
                "Category     : " + t.getCategory(),
                "Description  : " + t.getDescription(),
                "Priority     : " + ConsoleUI.getPriorityBadge(t.getPriority().name()),
                "Status       : " + ConsoleUI.getStatusBadge(t.getStatus().name()),
                "SLA Deadline : " + (t.getSlaDeadline() != null ? t.getSlaDeadline().format(DATE_FMT) : "N/A"),
                "Root Cause   : " + (t.getRootCause() != null ? t.getRootCause() : "In progress"),
                "Resolution   : " + (t.getResolutionText() != null ? t.getResolutionText() : "Pending resolution")
        ));
    }

    private void checkSLAStatus(int engineerId) throws Exception {
        ConsoleUI.printHeader("SLA Compliance Status", "Deadlines for assigned tickets");
        List<TroubleTicket> tickets = engineerService.getAssignedTickets(engineerId);
        if (tickets.isEmpty()) {
            ConsoleUI.printSuccess("No active tickets. All SLA targets compliant.");
            return;
        }
        
        System.out.printf("  %-16s %-12s %-18s %-16s\n", "TICKET NUMBER", "PRIORITY", "SLA DEADLINE", "SLA HEALTH");
        ConsoleUI.printDivider();
        for (TroubleTicket t : tickets) {
            String risk;
            if (t.getSlaDeadline() != null && t.getSlaDeadline().isBefore(LocalDateTime.now())) {
                risk = ConsoleUI.BRIGHT_RED + "BREACHED" + ConsoleUI.RESET;
            } else if (t.getSlaDeadline() != null && t.getSlaDeadline().minusMinutes(30).isBefore(LocalDateTime.now())) {
                risk = ConsoleUI.BRIGHT_YELLOW + "AT_RISK (<30m)" + ConsoleUI.RESET;
            } else {
                risk = ConsoleUI.GREEN + "ON_TRACK" + ConsoleUI.RESET;
            }
            
            System.out.printf("  %-16s %-12s %-18s %-16s\n",
                    t.getTicketNumber(),
                    ConsoleUI.getPriorityBadge(t.getPriority().name()),
                    t.getSlaDeadline() != null ? t.getSlaDeadline().format(DATE_FMT) : "N/A",
                    risk);
        }
    }
}
