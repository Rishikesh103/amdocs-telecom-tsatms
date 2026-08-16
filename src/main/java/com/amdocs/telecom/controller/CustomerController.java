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
 * Cyberpunk NOC & Telecom Assurance Edition
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
                    trackTicket(scanner);
                    break;
                case "5":
                    viewTicketHistory(scanner);
                    break;
                case "6":
                    viewNotifications(currentUser.getUsername());
                    break;
                case "7":
                    submitFeedback(customerId, scanner);
                    break;
                case "8":
                    ConsoleUI.printSuccess("Session terminated. Signed out safely.");
                    return false;
                default:
                    ConsoleUI.printError("Invalid option selected. Please try again.");
            }
        } catch (Exception e) {
            ConsoleUI.printError("Error processing customer request: " + e.getMessage());
        }
        return true;
    }

    private void viewMyServices(int customerId) throws Exception {
        ConsoleUI.printHeader("MY SUBSCRIBED TELECOM SERVICES", "Active Network Products & Provisioning");
        List<TelecomService> services = customerService.getServicesForCustomer(customerId);
        if (services.isEmpty()) {
            ConsoleUI.printWarning("No active telecom subscriptions registered to your account.");
            return;
        }
        
        System.out.printf("  %s%-6s %-18s %-28s %-20s %-12s%s\n",
                ConsoleUI.BRIGHT_CYAN + ConsoleUI.BOLD, "ID", "SERVICE CODE", "PRODUCT NAME", "NETWORK TYPE", "STATUS", ConsoleUI.RESET);
        ConsoleUI.printDivider();
        for (TelecomService s : services) {
            String statusStr = s.getServiceStatus() != null ? s.getServiceStatus().name() : "INACTIVE";
            String statusBadge = "ACTIVE".equalsIgnoreCase(statusStr) ? 
                    ConsoleUI.BRIGHT_GREEN + "● ACTIVE" + ConsoleUI.RESET : 
                    ConsoleUI.RED + "○ " + statusStr + ConsoleUI.RESET;
            System.out.printf("  %-6d %s%-18s%s %-28s %-20s %s\n",
                    s.getServiceId(), ConsoleUI.BRIGHT_YELLOW, s.getServiceCode(), ConsoleUI.RESET,
                    s.getServiceName(), s.getServiceType(), statusBadge);
        }
    }

    private void raiseTroubleTicket(int customerId, Scanner scanner) throws Exception {
        ConsoleUI.printHeader("INCIDENT DISPATCH WIZARD", "Report an outage or service degradation");
        List<TelecomService> services = customerService.getServicesForCustomer(customerId);
        if (services.isEmpty()) {
            ConsoleUI.printError("Cannot raise incident: No active services registered for customer.");
            return;
        }

        ConsoleUI.printSection("STEP 1: SELECT IMPACTED SERVICE");
        for (int i = 0; i < services.size(); i++) {
            TelecomService s = services.get(i);
            System.out.printf("  %s[%d]%s %s (%s) [%s]\n",
                    ConsoleUI.BRIGHT_CYAN + ConsoleUI.BOLD, (i + 1), ConsoleUI.RESET,
                    s.getServiceName(), s.getServiceCode(), s.getServiceType());
        }
        ConsoleUI.printPrompt("Select Service (1-" + services.size() + ")");
        int sChoice = Integer.parseInt(scanner.nextLine().trim());
        if (sChoice < 1 || sChoice > services.size()) {
            ConsoleUI.printError("Invalid service index selected.");
            return;
        }
        TelecomService selectedService = services.get(sChoice - 1);

        ConsoleUI.printSection("STEP 2: INCIDENT CLASSIFICATION");
        System.out.println("  1. NETWORK_OUTAGE (Total loss of signal/service)");
        System.out.println("  2. SLOW_DATA (Speed degradation)");
        System.out.println("  3. CALL_DROP (Frequent voice disconnections)");
        System.out.println("  4. BROADBAND (Fiber / Wi-Fi issues)");
        System.out.println("  5. OTHER (General technical assistance)");
        ConsoleUI.printPrompt("Enter Category or Choice (1-5)");
        String category = scanner.nextLine().trim().toUpperCase();
        if (category.equals("1")) category = "NETWORK_OUTAGE";
        else if (category.equals("2")) category = "SLOW_DATA";
        else if (category.equals("3")) category = "CALL_DROP";
        else if (category.equals("4")) category = "BROADBAND";
        else if (category.equals("5")) category = "OTHER";

        ConsoleUI.printSection("STEP 3: INCIDENT DETAILS");
        ConsoleUI.printPrompt("Detailed Description of the Issue");
        String description = scanner.nextLine().trim();

        ConsoleUI.printSection("STEP 4: IMPACT PRIORITY");
        System.out.println("  1. " + ConsoleUI.getPriorityBadge("LOW") + " (Minor inconvenience)");
        System.out.println("  2. " + ConsoleUI.getPriorityBadge("MEDIUM") + " (Standard degradation)");
        System.out.println("  3. " + ConsoleUI.getPriorityBadge("HIGH") + " (Business affecting)");
        System.out.println("  4. " + ConsoleUI.getPriorityBadge("CRITICAL") + " (Complete outage / Emergency)");
        ConsoleUI.printPrompt("Select Priority (1-4)");
        String pChoice = scanner.nextLine().trim();
        Priority priority;
        switch (pChoice) {
            case "1": priority = Priority.LOW; break;
            case "3": priority = Priority.HIGH; break;
            case "4": priority = Priority.CRITICAL; break;
            default: priority = Priority.MEDIUM;
        }

        TroubleTicket ticket = ticketService.createTicket(customerId, selectedService.getServiceId(), category, description, priority, priority.name());
        
        ConsoleUI.printSuccess("Trouble Ticket Created & Dispatched to NOC Queue!");
        ConsoleUI.printCard("TICKET CONFIRMATION", Arrays.asList(
                "Ticket Number : " + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + ticket.getTicketNumber() + ConsoleUI.RESET,
                "Category      : " + ticket.getCategory(),
                "Priority      : " + ConsoleUI.getPriorityBadge(ticket.getPriority().name()),
                "Initial Status: " + ConsoleUI.getStatusBadge(ticket.getStatus().name()),
                "SLA Deadline  : " + (ticket.getSlaDeadline() != null ? ticket.getSlaDeadline().format(DATE_FMT) : "N/A")
        ), ConsoleUI.BRIGHT_GREEN);
    }

    private void viewMyTickets(int customerId) throws Exception {
        ConsoleUI.printHeader("MY ACTIVE TROUBLE TICKETS", "Real-Time Tracking & Resolution Status");
        List<TroubleTicket> tickets = ticketService.getTicketsByCustomerId(customerId);
        if (tickets.isEmpty()) {
            ConsoleUI.printInfo("No trouble tickets found for your account.");
            return;
        }
        System.out.printf("  %-16s %-16s %-16s %-20s %-16s\n",
                "TICKET NUMBER", "CATEGORY", "PRIORITY", "STATUS", "CREATED DATE");
        ConsoleUI.printDivider();
        for (TroubleTicket t : tickets) {
            String createdStr = t.getCreatedDate() != null ? t.getCreatedDate().format(DATE_FMT) : "N/A";
            System.out.printf("  %s%-16s%s %-16s %-16s %-20s %s%-16s%s\n",
                    ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD, t.getTicketNumber(), ConsoleUI.RESET,
                    t.getCategory(),
                    ConsoleUI.getPriorityBadge(t.getPriority().name()),
                    ConsoleUI.getStatusBadge(t.getStatus().name()),
                    ConsoleUI.DIM, createdStr, ConsoleUI.RESET);
        }
    }

    private void trackTicket(Scanner scanner) throws Exception {
        ConsoleUI.printPrompt("Enter Ticket Number (e.g. TT-2026-004521)");
        String tktNum = scanner.nextLine().trim();
        TroubleTicket ticket = ticketService.getTicketByNumber(tktNum);
        if (ticket == null) {
            ConsoleUI.printError("Ticket not found with reference: " + tktNum);
            return;
        }
        ConsoleUI.printCard("TELEMETRY: TICKET #" + ticket.getTicketNumber(), Arrays.asList(
                "Reference ID  : " + ticket.getTicketId(),
                "Category      : " + ticket.getCategory(),
                "Priority      : " + ConsoleUI.getPriorityBadge(ticket.getPriority().name()),
                "Current Status: " + ConsoleUI.getStatusBadge(ticket.getStatus().name()),
                "Description   : " + ticket.getDescription(),
                "Created At    : " + (ticket.getCreatedDate() != null ? ticket.getCreatedDate().format(DATE_FMT) : "N/A"),
                "SLA Target    : " + (ticket.getSlaDeadline() != null ? ticket.getSlaDeadline().format(DATE_FMT) : "N/A"),
                "Root Cause    : " + (ticket.getRootCause() != null ? ticket.getRootCause() : "Under Investigation"),
                "Resolution    : " + (ticket.getResolutionText() != null ? ConsoleUI.BRIGHT_GREEN + ticket.getResolutionText() + ConsoleUI.RESET : "Pending Technical Resolution")
        ), ConsoleUI.BRIGHT_CYAN);
    }

    private void viewTicketHistory(Scanner scanner) throws Exception {
        ConsoleUI.printPrompt("Enter Ticket Number to inspect audit history");
        String tktNum = scanner.nextLine().trim();
        TroubleTicket ticket = ticketService.getTicketByNumber(tktNum);
        if (ticket == null) {
            ConsoleUI.printError("Ticket not found: " + tktNum);
            return;
        }

        ConsoleUI.printHeader("AUDIT LOG FOR TICKET: " + ticket.getTicketNumber(), "Immutable Lifecycle Transition History");
        List<TicketStatusHistory> historyList = ticketService.getTicketHistory(ticket.getTicketId());
        if (historyList == null || historyList.isEmpty()) {
            ConsoleUI.printInfo("No state transitions recorded yet for this ticket.");
            return;
        }

        System.out.printf("  %-18s %-16s %-16s %-15s %-30s\n",
                "TIMESTAMP", "PREVIOUS STATE", "NEW STATE", "OPERATOR", "ACTION / REMARKS");
        ConsoleUI.printDivider();
        for (TicketStatusHistory h : historyList) {
            String timeStr = h.getChangedDate() != null ? h.getChangedDate().format(DATE_FMT) : "N/A";
            System.out.printf("  %s%-18s%s %-16s %-16s %s%-15s%s %-30s\n",
                    ConsoleUI.DIM, timeStr, ConsoleUI.RESET,
                    h.getOldStatus() != null ? h.getOldStatus() : "NONE",
                    ConsoleUI.BRIGHT_GREEN + h.getNewStatus() + ConsoleUI.RESET,
                    ConsoleUI.BRIGHT_CYAN, h.getChangedBy(), ConsoleUI.RESET,
                    h.getRemarks() != null ? h.getRemarks() : "-");
        }
    }

    private void viewNotifications(String username) throws Exception {
        ConsoleUI.printHeader("PUSH NOTIFICATIONS & ALERTS", "Direct NOC Bulletins");
        List<Notification> notifs = customerService.getNotificationsForUser(username);
        if (notifs.isEmpty()) {
            ConsoleUI.printInfo("Your notification inbox is clean. No active alerts.");
            return;
        }
        for (Notification n : notifs) {
            String time = n.getCreatedDate() != null ? n.getCreatedDate().format(DATE_FMT) : "N/A";
            String typeStr = n.getNotificationType() != null ? n.getNotificationType().name() : "INFO";
            String typeBadge = typeStr.contains("SLA") ? 
                    ConsoleUI.BRIGHT_RED + "⚠ " + typeStr + ConsoleUI.RESET : 
                    ConsoleUI.BRIGHT_CYAN + "ℹ " + typeStr + ConsoleUI.RESET;
            System.out.printf("  [%s] %s: %s\n", time, typeBadge, n.getMessage());
        }
    }

    private void submitFeedback(int customerId, Scanner scanner) throws Exception {
        ConsoleUI.printHeader("CUSTOMER SATISFACTION FEEDBACK", "Rate your recent resolution experience");
        ConsoleUI.printPrompt("Enter Ticket Number or ID (e.g. TT-2026-004521)");
        String input = scanner.nextLine().trim();
        int tktId;
        try {
            tktId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            TroubleTicket t = ticketService.getTicketByNumber(input);
            if (t == null) {
                ConsoleUI.printError("Ticket not found with reference: " + input);
                return;
            }
            tktId = t.getTicketId();
        }
        
        ConsoleUI.printPrompt("Rating (1-5 Stars)");
        int rating = Integer.parseInt(scanner.nextLine().trim());
        if (rating < 1) rating = 1;
        if (rating > 5) rating = 5;
        
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < rating; i++) stars.append("⭐");
        System.out.println("  Selected Rating: " + stars.toString() + " (" + rating + "/5)");

        ConsoleUI.printPrompt("Feedback Remarks & Comments");
        String comments = scanner.nextLine().trim();

        customerService.submitFeedback(tktId, customerId, rating, comments);
        ConsoleUI.printSuccess("Thank you! Your satisfaction feedback has been recorded in the quality audit system.");
    }
}
