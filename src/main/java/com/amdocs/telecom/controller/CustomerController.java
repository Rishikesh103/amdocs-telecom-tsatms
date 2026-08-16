package com.amdocs.telecom.controller;

import com.amdocs.telecom.model.*;
import com.amdocs.telecom.service.*;
import com.amdocs.telecom.service.impl.*;
import com.amdocs.telecom.util.ConsoleUI;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Customer Self-Service Portal Controller
 * Professional Enterprise Design with Interactive Ticket Selection
 */
public class CustomerController {

    private final CustomerService customerService;
    private final TroubleTicketService ticketService;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public CustomerController() {
        this.customerService = new CustomerServiceImpl();
        this.ticketService = new TroubleTicketServiceImpl();
    }

    public boolean handleMenuChoice(String choice, UserAccount currentUser, Scanner scanner) {
        try {
            Customer customer = customerService.getCustomerByUserId(currentUser.getUserId());
            int customerId = (customer != null) ? customer.getCustomerId() : (currentUser.getLinkedId() != null ? currentUser.getLinkedId() : 1);

            switch (choice) {
                case "1":
                    viewMyServices(customerId);
                    break;
                case "2":
                    raiseTroubleTicket(customerId, scanner);
                    break;
                case "3":
                    viewMyTickets(customerId);
                    break;
                case "4":
                    trackTicket(customerId, scanner);
                    break;
                case "5":
                    viewTicketHistory(customerId, scanner);
                    break;
                case "6":
                    viewNotifications(currentUser.getUsername());
                    break;
                case "7":
                    submitFeedback(customerId, scanner);
                    break;
                case "8":
                case "0":
                    ConsoleUI.printSuccess("Logged out successfully. Returned to main gateway.");
                    return false;
                default:
                    ConsoleUI.printError("Invalid option. Please select 1-8 (or 0 to Sign Out).");
            }
        } catch (Exception e) {
            ConsoleUI.printError("Error processing request: " + e.getMessage());
        }
        return true;
    }

    private void viewMyServices(int customerId) throws Exception {
        ConsoleUI.printHeader("Subscribed Telecom Services", "Registered products for customer #" + customerId);
        List<TelecomService> services = customerService.getServicesForCustomer(customerId);
        if (services.isEmpty()) {
            ConsoleUI.printInfo("No subscribed services found.");
            return;
        }
        
        System.out.printf("  %-6s %-18s %-28s %-22s %-10s\n", "ID", "SERVICE CODE", "SERVICE NAME", "TYPE", "STATUS");
        ConsoleUI.printDivider();
        for (TelecomService s : services) {
            String statusStr = s.getServiceStatus() != null ? s.getServiceStatus().name() : "INACTIVE";
            System.out.printf("  %-6d %-18s %-28s %-22s %-10s\n",
                    s.getServiceId(), s.getServiceCode(), s.getServiceName(), s.getServiceType(), statusStr);
        }
    }

    private void raiseTroubleTicket(int customerId, Scanner scanner) throws Exception {
        ConsoleUI.printHeader("Raise Trouble Ticket", "Submit an incident or service degradation report");
        List<TelecomService> services = customerService.getServicesForCustomer(customerId);
        if (services.isEmpty()) {
            ConsoleUI.printError("Cannot raise ticket: No active services registered.");
            return;
        }

        ConsoleUI.printSection("Step 1: Select Subscribed Service");
        for (int i = 0; i < services.size(); i++) {
            TelecomService s = services.get(i);
            System.out.printf("  [%d] %s (%s) - %s\n",
                    (i + 1), s.getServiceName(), s.getServiceCode(), s.getServiceType());
        }
        System.out.println("  [0] Cancel and return to menu");
        
        ConsoleUI.printPrompt("Enter choice (1-" + services.size() + " or 0 to Cancel)");
        String rawChoice = scanner.nextLine().trim();
        if ("0".equals(rawChoice) || "back".equalsIgnoreCase(rawChoice) || "exit".equalsIgnoreCase(rawChoice)) {
            ConsoleUI.printInfo("Action canceled. Returning to Customer Portal.");
            return;
        }

        int sChoice;
        try {
            sChoice = Integer.parseInt(rawChoice);
        } catch (NumberFormatException e) {
            ConsoleUI.printError("Invalid input. Ticket creation canceled.");
            return;
        }

        if (sChoice < 1 || sChoice > services.size()) {
            ConsoleUI.printError("Invalid service selection. Ticket creation canceled.");
            return;
        }
        TelecomService selectedService = services.get(sChoice - 1);

        ConsoleUI.printSection("Step 2: Incident Category");
        System.out.println("  [1] NETWORK_OUTAGE (Complete service disruption)");
        System.out.println("  [2] SLOW_DATA (Speed degradation)");
        System.out.println("  [3] CALL_DROP (Voice call disconnection)");
        System.out.println("  [4] BROADBAND (Fiber / Connectivity issues)");
        System.out.println("  [5] OTHER (General inquiry)");
        System.out.println("  [0] Cancel");
        ConsoleUI.printPrompt("Select category (1-5 or 0 to Cancel)");
        String category = scanner.nextLine().trim().toUpperCase();
        if ("0".equals(category) || "BACK".equalsIgnoreCase(category)) {
            ConsoleUI.printInfo("Action canceled.");
            return;
        }
        if (category.equals("1")) category = "NETWORK_OUTAGE";
        else if (category.equals("2")) category = "SLOW_DATA";
        else if (category.equals("3")) category = "CALL_DROP";
        else if (category.equals("4")) category = "BROADBAND";
        else if (category.equals("5")) category = "OTHER";

        ConsoleUI.printSection("Step 3: Issue Description");
        ConsoleUI.printPrompt("Enter description (or 0 to Cancel)");
        String description = scanner.nextLine().trim();
        if ("0".equals(description) || "back".equalsIgnoreCase(description)) {
            ConsoleUI.printInfo("Action canceled.");
            return;
        }

        ConsoleUI.printSection("Step 4: Incident Priority");
        System.out.println("  [1] LOW (Minor non-critical request)");
        System.out.println("  [2] MEDIUM (Standard service issue)");
        System.out.println("  [3] HIGH (Significant service disruption)");
        System.out.println("  [4] CRITICAL (Complete outage / Emergency)");
        System.out.println("  [0] Cancel");
        ConsoleUI.printPrompt("Select priority (1-4 or 0 to Cancel)");
        String pChoice = scanner.nextLine().trim();
        if ("0".equals(pChoice) || "back".equalsIgnoreCase(pChoice)) {
            ConsoleUI.printInfo("Action canceled.");
            return;
        }

        Priority priority;
        switch (pChoice) {
            case "1": priority = Priority.LOW; break;
            case "3": priority = Priority.HIGH; break;
            case "4": priority = Priority.CRITICAL; break;
            default: priority = Priority.MEDIUM;
        }

        TroubleTicket ticket = ticketService.createTicket(customerId, selectedService.getServiceId(), category, description, priority, priority.name());
        
        ConsoleUI.printSuccess("Trouble Ticket created successfully.");
        ConsoleUI.printCard("Ticket Confirmation", Arrays.asList(
                "Ticket Number : " + ticket.getTicketNumber(),
                "Category      : " + ticket.getCategory(),
                "Priority      : " + ConsoleUI.getPriorityBadge(ticket.getPriority().name()),
                "Status        : " + ConsoleUI.getStatusBadge(ticket.getStatus().name()),
                "SLA Deadline  : " + (ticket.getSlaDeadline() != null ? ticket.getSlaDeadline().format(DATE_FMT) : "N/A")
        ));
    }

    private void viewMyTickets(int customerId) throws Exception {
        ConsoleUI.printHeader("My Trouble Tickets", "Incident status and history");
        List<TroubleTicket> tickets = ticketService.getTicketsByCustomerId(customerId);
        if (tickets.isEmpty()) {
            ConsoleUI.printInfo("No trouble tickets found for your account.");
            return;
        }
        System.out.printf("  %-16s %-18s %-12s %-16s %-16s\n",
                "TICKET NUMBER", "CATEGORY", "PRIORITY", "STATUS", "CREATED DATE");
        ConsoleUI.printDivider();
        for (TroubleTicket t : tickets) {
            String createdStr = t.getCreatedDate() != null ? t.getCreatedDate().format(DATE_FMT) : "N/A";
            System.out.printf("  %-16s %-18s %-12s %-16s %-16s\n",
                    t.getTicketNumber(),
                    t.getCategory(),
                    ConsoleUI.getPriorityBadge(t.getPriority().name()),
                    ConsoleUI.getStatusBadge(t.getStatus().name()),
                    createdStr);
        }
    }

    private TroubleTicket selectTicketFromList(int customerId, Scanner scanner, String headerTitle) throws Exception {
        List<TroubleTicket> tickets = ticketService.getTicketsByCustomerId(customerId);
        if (tickets.isEmpty()) {
            ConsoleUI.printInfo("No trouble tickets found for your account.");
            return null;
        }

        ConsoleUI.printHeader(headerTitle, "Select from your registered tickets or enter a ticket number");
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

        // Check if numeric choice
        try {
            int idx = Integer.parseInt(input);
            if (idx >= 1 && idx <= tickets.size()) {
                return tickets.get(idx - 1);
            }
        } catch (NumberFormatException ignored) {}

        // Fallback: search by ticket number or ID
        TroubleTicket t = ticketService.getTicketByNumber(input);
        if (t != null) return t;

        try {
            int id = Integer.parseInt(input);
            t = ticketService.getTicketById(id);
            if (t != null) return t;
        } catch (NumberFormatException ignored) {}

        ConsoleUI.printError("Ticket not found: " + input);
        return null;
    }

    private void trackTicket(int customerId, Scanner scanner) throws Exception {
        TroubleTicket ticket = selectTicketFromList(customerId, scanner, "Track Trouble Ticket Status");
        if (ticket == null) return;

        ConsoleUI.printCard("Ticket Details: " + ticket.getTicketNumber(), Arrays.asList(
                "Ticket ID    : " + ticket.getTicketId(),
                "Category     : " + ticket.getCategory(),
                "Priority     : " + ConsoleUI.getPriorityBadge(ticket.getPriority().name()),
                "Status       : " + ConsoleUI.getStatusBadge(ticket.getStatus().name()),
                "Description  : " + ticket.getDescription(),
                "Created Date : " + (ticket.getCreatedDate() != null ? ticket.getCreatedDate().format(DATE_FMT) : "N/A"),
                "SLA Deadline : " + (ticket.getSlaDeadline() != null ? ticket.getSlaDeadline().format(DATE_FMT) : "N/A"),
                "Root Cause   : " + (ticket.getRootCause() != null ? ticket.getRootCause() : "In progress"),
                "Resolution   : " + (ticket.getResolutionText() != null ? ticket.getResolutionText() : "Pending resolution")
        ));
    }

    private void viewTicketHistory(int customerId, Scanner scanner) throws Exception {
        TroubleTicket ticket = selectTicketFromList(customerId, scanner, "View Ticket Audit History");
        if (ticket == null) return;

        ConsoleUI.printHeader("Audit Trail: " + ticket.getTicketNumber(), "Lifecycle State Transitions");
        List<TicketStatusHistory> historyList = ticketService.getTicketHistory(ticket.getTicketId());
        if (historyList == null || historyList.isEmpty()) {
            ConsoleUI.printInfo("No state transitions recorded for this ticket.");
            return;
        }

        System.out.printf("  %-18s %-16s %-16s %-16s %-30s\n",
                "TIMESTAMP", "OLD STATUS", "NEW STATUS", "CHANGED BY", "REMARKS");
        ConsoleUI.printDivider();
        for (TicketStatusHistory h : historyList) {
            String timeStr = h.getChangedDate() != null ? h.getChangedDate().format(DATE_FMT) : "N/A";
            System.out.printf("  %-18s %-16s %-16s %-16s %-30s\n",
                    timeStr,
                    h.getOldStatus() != null ? h.getOldStatus() : "NONE",
                    h.getNewStatus(),
                    h.getChangedBy(),
                    h.getRemarks() != null ? h.getRemarks() : "-");
        }
    }

    private void viewNotifications(String username) throws Exception {
        ConsoleUI.printHeader("Notifications", "System alerts and bulletins");
        List<Notification> notifs = customerService.getNotificationsForUser(username);
        if (notifs.isEmpty()) {
            ConsoleUI.printInfo("No notifications found.");
            return;
        }
        for (Notification n : notifs) {
            String time = n.getCreatedDate() != null ? n.getCreatedDate().format(DATE_FMT) : "N/A";
            String typeStr = n.getNotificationType() != null ? n.getNotificationType().name() : "INFO";
            System.out.printf("  [%s] %s: %s\n", time, typeStr, n.getMessage());
        }
    }

    private void submitFeedback(int customerId, Scanner scanner) throws Exception {
        TroubleTicket ticket = selectTicketFromList(customerId, scanner, "Submit Customer Feedback");
        if (ticket == null) return;
        
        ConsoleUI.printPrompt("Enter Rating (1 to 5, or 0 to Cancel)");
        String rawRating = scanner.nextLine().trim();
        if ("0".equals(rawRating) || "back".equalsIgnoreCase(rawRating)) {
            ConsoleUI.printInfo("Feedback submission canceled.");
            return;
        }

        int rating;
        try {
            rating = Integer.parseInt(rawRating);
        } catch (NumberFormatException e) {
            rating = 5;
        }
        if (rating < 1) rating = 1;
        if (rating > 5) rating = 5;

        ConsoleUI.printPrompt("Enter Comments");
        String comments = scanner.nextLine().trim();

        customerService.submitFeedback(ticket.getTicketId(), customerId, rating, comments);
        ConsoleUI.printSuccess("Feedback recorded for Ticket #" + ticket.getTicketNumber() + ". Thank you.");
    }
}
