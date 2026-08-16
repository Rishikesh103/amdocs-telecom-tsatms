package com.amdocs.telecom.controller;

import com.amdocs.telecom.model.*;
import com.amdocs.telecom.service.*;
import com.amdocs.telecom.service.impl.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class EngineerController {

    private final EngineerService engineerService;
    private final TroubleTicketService ticketService;

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
                    System.out.println("✓ Logged out successfully.");
                    return false;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } catch (Exception e) {
            System.err.println("Error processing Engineer request: " + e.getMessage());
        }
        return true;
    }

    private void viewAssignedTickets(int engineerId) throws Exception {
        System.out.println("\n========== MY ASSIGNED TROUBLE TICKETS ==========");
        List<TroubleTicket> tickets = engineerService.getAssignedTickets(engineerId);
        if (tickets.isEmpty()) {
            System.out.println("No assigned tickets found.");
            return;
        }
        System.out.printf("%-5s %-15s %-15s %-12s %-15s %-20s\n", "ID", "TICKET #", "CATEGORY", "PRIORITY", "STATUS", "SLA DEADLINE");
        System.out.println("----------------------------------------------------------------------------------");
        for (TroubleTicket t : tickets) {
            String deadline = t.getSlaDeadline() != null ? t.getSlaDeadline().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A";
            System.out.printf("%-5d %-15s %-15s %-12s %-15s %-20s\n",
                    t.getTicketId(), t.getTicketNumber(), t.getCategory(), t.getPriority(), t.getStatus(), deadline);
        }
    }

    private int resolveTicketId(String input) throws Exception {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            TroubleTicket t = ticketService.getTicketByNumber(input);
            if (t != null) return t.getTicketId();
            throw new Exception("Ticket not found with number: " + input);
        }
    }

    private void updateTicketStatus(String engineerName, Scanner scanner) throws Exception {
        System.out.print("\nEnter Ticket Number or ID: ");
        int tktId = resolveTicketId(scanner.nextLine().trim());
        System.out.println("Select New Status:");
        System.out.println("1. IN_PROGRESS");
        System.out.println("2. PENDING_CUSTOMER");
        System.out.println("3. RESOLVED");
        System.out.print("Choice: ");
        String sChoice = scanner.nextLine().trim();
        TicketStatus newStatus = TicketStatus.IN_PROGRESS;
        if ("2".equals(sChoice)) newStatus = TicketStatus.PENDING_CUSTOMER;
        else if ("3".equals(sChoice)) newStatus = TicketStatus.RESOLVED;

        System.out.print("Enter Remarks: ");
        String remarks = scanner.nextLine().trim();

        ticketService.updateTicketStatus(tktId, newStatus, engineerName, remarks);
        System.out.println("✓ Status updated successfully to " + newStatus);
    }

    private void addResolution(int engineerId, Scanner scanner) throws Exception {
        System.out.print("\nEnter Ticket Number or ID: ");
        int tktId = resolveTicketId(scanner.nextLine().trim());
        System.out.print("Enter Root Cause Analysis: ");
        String rootCause = scanner.nextLine().trim();
        System.out.print("Enter Resolution Description: ");
        String resText = scanner.nextLine().trim();

        System.out.println("Select Resolution Code:");
        System.out.println("1. HARDWARE_FAILURE");
        System.out.println("2. CONFIGURATION_ERROR");
        System.out.println("3. NETWORK_CONGESTION");
        System.out.println("4. SOFTWARE_FAILURE");
        System.out.println("5. FIBER_CUT");
        System.out.println("6. POWER_FAILURE");
        System.out.print("Choice: ");
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
        System.out.println("✓ Ticket resolved successfully! Active workload decremented.");
    }

    private void viewTicketDetails(Scanner scanner) throws Exception {
        System.out.print("\nEnter Ticket Number (e.g. TKT100245): ");
        String num = scanner.nextLine().trim();
        TroubleTicket t = ticketService.getTicketByNumber(num);
        if (t == null) {
            System.out.println("❌ Ticket not found.");
            return;
        }
        System.out.println("\n========== TICKET DETAILS ==========");
        System.out.println("Ticket ID    : " + t.getTicketId());
        System.out.println("Ticket #     : " + t.getTicketNumber());
        System.out.println("Category     : " + t.getCategory());
        System.out.println("Description  : " + t.getDescription());
        System.out.println("Priority     : " + t.getPriority());
        System.out.println("Status       : " + t.getStatus());
        System.out.println("SLA Deadline : " + t.getSlaDeadline());
        System.out.println("Root Cause   : " + t.getRootCause());
        System.out.println("Resolution   : " + t.getResolutionText());
    }

    private void checkSLAStatus(int engineerId) throws Exception {
        System.out.println("\n========== SLA DEADLINE STATUS ==========");
        List<TroubleTicket> tickets = engineerService.getAssignedTickets(engineerId);
        if (tickets.isEmpty()) {
            System.out.println("No assigned tickets to check.");
            return;
        }
        for (TroubleTicket t : tickets) {
            System.out.println("Ticket: " + t.getTicketNumber() + " | Priority: " + t.getPriority() + " | Deadline: " + t.getSlaDeadline());
        }
    }
}
