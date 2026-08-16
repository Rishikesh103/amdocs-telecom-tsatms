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
 * Cyberpunk NOC & Diagnostic Terminal Edition
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
                    ConsoleUI.printSuccess("Workbench session terminated. Status saved.");
                    return false;
                default:
                    ConsoleUI.printError("Invalid engineer command.");
            }
        } catch (Exception e) {
            ConsoleUI.printError("Engineer Operation Failed: " + e.getMessage());
        }
        return true;
    }

    private void viewAssignedTickets(int engineerId) throws Exception {
        ConsoleUI.printHeader("MY ACTIVE INCIDENT QUEUE", "Tickets assigned for diagnostic & restoration");
        List<TroubleTicket> tickets = engineerService.getAssignedTickets(engineerId);
        if (tickets.isEmpty()) {
            ConsoleUI.printSuccess("All assigned tickets resolved! Your active queue is empty.");
            return;
        }
        
        System.out.printf("  %-5s %-16s %-16s %-16s %-18s %-18s\n",
                "ID", "TICKET #", "CATEGORY", "PRIORITY", "STATUS", "SLA DEADLINE");
        ConsoleUI.printDivider();
        for (TroubleTicket t : tickets) {
            String deadline = t.getSlaDeadline() != null ? t.getSlaDeadline().format(DATE_FMT) : "N/A";
            System.out.printf("  %-5d %s%-16s%s %-16s %-16s %-18s %s%-18s%s\n",
                    t.getTicketId(),
                    ConsoleUI.BRIGHT_YELLOW, t.getTicketNumber(), ConsoleUI.RESET,
                    t.getCategory(),
                    ConsoleUI.getPriorityBadge(t.getPriority().name()),
                    ConsoleUI.getStatusBadge(t.getStatus().name()),
                    ConsoleUI.DIM, deadline, ConsoleUI.RESET);
        }
    }

    private int resolveTicketId(String input) throws Exception {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            TroubleTicket t = ticketService.getTicketByNumber(input);
            if (t != null) return t.getTicketId();
            throw new Exception("Ticket not found with reference: " + input);
        }
    }

    private void updateTicketStatus(String engineerName, Scanner scanner) throws Exception {
        ConsoleUI.printHeader("LIFECYCLE STATUS TRANSITION", "Update ticket state in real-time NOC telemetry");
        ConsoleUI.printPrompt("Enter Ticket Number or ID");
        int tktId = resolveTicketId(scanner.nextLine().trim());
        
        System.out.println("  1. " + ConsoleUI.getStatusBadge("IN_PROGRESS") + " (Engineer currently working)");
        System.out.println("  2. " + ConsoleUI.getStatusBadge("PENDING_CUSTOMER") + " (Waiting for customer response/test)");
        System.out.println("  3. " + ConsoleUI.getStatusBadge("RESOLVED") + " (Fix applied, ready for sign-off)");
        ConsoleUI.printPrompt("Select Target Status (1-3)");
        String sChoice = scanner.nextLine().trim();
        TicketStatus newStatus = TicketStatus.IN_PROGRESS;
        if ("2".equals(sChoice)) newStatus = TicketStatus.PENDING_CUSTOMER;
        else if ("3".equals(sChoice)) newStatus = TicketStatus.RESOLVED;

        ConsoleUI.printPrompt("Enter Operational Notes / Log");
        String remarks = scanner.nextLine().trim();

        ticketService.updateTicketStatus(tktId, newStatus, engineerName, remarks);
        ConsoleUI.printSuccess("Ticket state transitioned to " + newStatus + " and audit log recorded.");
    }

    private void addResolution(int engineerId, Scanner scanner) throws Exception {
        ConsoleUI.printHeader("INCIDENT RESOLUTION SUBMISSION", "Submit root cause analysis & restoration report");
        ConsoleUI.printPrompt("Enter Ticket Number or ID to resolve");
        int tktId = resolveTicketId(scanner.nextLine().trim());
        
        ConsoleUI.printPrompt("Root Cause Analysis (RCA Summary)");
        String rootCause = scanner.nextLine().trim();
        
        ConsoleUI.printPrompt("Detailed Resolution Steps / Fix Applied");
        String resText = scanner.nextLine().trim();

        System.out.println("\n  Classification of Resolution Code:");
        System.out.println("  1. HARDWARE_FAILURE (Replaced faulty card/chassis/router)");
        System.out.println("  2. CONFIGURATION_ERROR (Repaired routing/BGP/VLAN misconfig)");
        System.out.println("  3. NETWORK_CONGESTION (Traffic diverted / QoS tuned)");
        System.out.println("  4. SOFTWARE_FAILURE (Patched daemon / memory leak restart)");
        System.out.println("  5. FIBER_CUT (Physical splice restoration completed)");
        System.out.println("  6. POWER_FAILURE (UPS / Generator power supply restored)");
        ConsoleUI.printPrompt("Select Code (1-6)");
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
        ConsoleUI.printSuccess("Resolution registered! Incident marked RESOLVED and engineer workload decremented.");
    }

    private void viewTicketDetails(Scanner scanner) throws Exception {
        ConsoleUI.printPrompt("Enter Ticket Number (e.g. TT-2026-004521)");
        String num = scanner.nextLine().trim();
        TroubleTicket t = ticketService.getTicketByNumber(num);
        if (t == null) {
            ConsoleUI.printError("Ticket not found: " + num);
            return;
        }
        ConsoleUI.printCard("DEEP TELEMETRY: " + t.getTicketNumber(), Arrays.asList(
                "Ticket ID    : " + t.getTicketId(),
                "Incident Cat : " + t.getCategory(),
                "Description  : " + t.getDescription(),
                "Priority     : " + ConsoleUI.getPriorityBadge(t.getPriority().name()),
                "Status       : " + ConsoleUI.getStatusBadge(t.getStatus().name()),
                "SLA Target   : " + (t.getSlaDeadline() != null ? t.getSlaDeadline().format(DATE_FMT) : "N/A"),
                "Root Cause   : " + (t.getRootCause() != null ? t.getRootCause() : "Under Active Analysis"),
                "Resolution   : " + (t.getResolutionText() != null ? ConsoleUI.BRIGHT_GREEN + t.getResolutionText() + ConsoleUI.RESET : "Not resolved yet")
        ), ConsoleUI.BRIGHT_CYAN);
    }

    private void checkSLAStatus(int engineerId) throws Exception {
        ConsoleUI.printHeader("SLA DEADLINE HEALTH & RISK MATRIX", "Time remaining before SLA breach penalty");
        List<TroubleTicket> tickets = engineerService.getAssignedTickets(engineerId);
        if (tickets.isEmpty()) {
            ConsoleUI.printSuccess("Zero active tickets. No SLA risk.");
            return;
        }
        
        System.out.printf("  %-16s %-16s %-18s %-16s\n", "TICKET #", "PRIORITY", "SLA DEADLINE", "RISK ASSESSMENT");
        ConsoleUI.printDivider();
        for (TroubleTicket t : tickets) {
            String risk;
            if (t.getSlaDeadline() != null && t.getSlaDeadline().isBefore(LocalDateTime.now())) {
                risk = ConsoleUI.BRIGHT_RED + "✖ BREACHED" + ConsoleUI.RESET;
            } else if (t.getSlaDeadline() != null && t.getSlaDeadline().minusMinutes(30).isBefore(LocalDateTime.now())) {
                risk = ConsoleUI.BRIGHT_YELLOW + "▲ AT RISK (<30m)" + ConsoleUI.RESET;
            } else {
                risk = ConsoleUI.BRIGHT_GREEN + "● ON TRACK" + ConsoleUI.RESET;
            }
            
            System.out.printf("  %s%-16s%s %-16s %-18s %s\n",
                    ConsoleUI.BRIGHT_YELLOW, t.getTicketNumber(), ConsoleUI.RESET,
                    ConsoleUI.getPriorityBadge(t.getPriority().name()),
                    t.getSlaDeadline() != null ? t.getSlaDeadline().format(DATE_FMT) : "N/A",
                    risk);
        }
    }
}
