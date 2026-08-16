package com.amdocs.telecom.controller;

import com.amdocs.telecom.model.*;
import com.amdocs.telecom.service.*;
import com.amdocs.telecom.service.impl.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class CustomerController {

    private final CustomerService customerService;
    private final TroubleTicketService ticketService;

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
                    System.out.println("✓ Logged out successfully.");
                    return false;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } catch (Exception e) {
            System.err.println("Error processing customer request: " + e.getMessage());
        }
        return true;
    }

    private void viewMyServices(int customerId) throws Exception {
        System.out.println("\n========== MY TELECOM SERVICES ==========");
        List<TelecomService> services = customerService.getServicesForCustomer(customerId);
        if (services.isEmpty()) {
            System.out.println("No subscribed services found.");
            return;
        }
        System.out.printf("%-10s %-15s %-25s %-20s %-12s\n", "ID", "SERVICE CODE", "SERVICE NAME", "TYPE", "STATUS");
        System.out.println("----------------------------------------------------------------------------------");
        for (TelecomService s : services) {
            System.out.printf("%-10d %-15s %-25s %-20s %-12s\n",
                    s.getServiceId(), s.getServiceCode(), s.getServiceName(), s.getServiceType(), s.getServiceStatus());
        }
    }

    private void raiseTroubleTicket(int customerId, Scanner scanner) throws Exception {
        System.out.println("\n========== RAISE TROUBLE TICKET ==========");
        List<TelecomService> services = customerService.getServicesForCustomer(customerId);
        if (services.isEmpty()) {
            System.out.println("Cannot raise ticket: No active services registered for customer.");
            return;
        }

        System.out.println("Select Subscribed Service:");
        for (int i = 0; i < services.size(); i++) {
            TelecomService s = services.get(i);
            System.out.println((i + 1) + ". " + s.getServiceName() + " (" + s.getServiceCode() + ")");
        }
        System.out.print("Enter choice (1-" + services.size() + "): ");
        int sChoice = Integer.parseInt(scanner.nextLine().trim());
        if (sChoice < 1 || sChoice > services.size()) {
            System.out.println("Invalid service selection.");
            return;
        }
        TelecomService selectedService = services.get(sChoice - 1);

        System.out.print("Enter Incident Category (NETWORK_OUTAGE / SLOW_DATA / CALL_DROP / BROADBAND / OTHER): ");
        String category = scanner.nextLine().trim();

        System.out.print("Enter Description of the Issue: ");
        String description = scanner.nextLine().trim();

        System.out.print("Select Priority (1. LOW, 2. MEDIUM, 3. HIGH, 4. CRITICAL): ");
        String pChoice = scanner.nextLine().trim();
        Priority priority;
        switch (pChoice) {
            case "1": priority = Priority.LOW; break;
            case "2": priority = Priority.MEDIUM; break;
            case "3": priority = Priority.HIGH; break;
            case "4": priority = Priority.CRITICAL; break;
            default: priority = Priority.MEDIUM;
        }

        TroubleTicket ticket = ticketService.createTicket(customerId, selectedService.getServiceId(), category, description, priority, priority.name());
        System.out.println("\n✓ Trouble Ticket Raised Successfully!");
        System.out.println("Ticket Number: " + ticket.getTicketNumber());
        System.out.println("SLA Target Deadline: " + (ticket.getSlaDeadline() != null ? ticket.getSlaDeadline().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A"));
    }

    private void viewMyTickets(int customerId) throws Exception {
        System.out.println("\n========== MY TROUBLE TICKETS ==========");
        List<TroubleTicket> tickets = ticketService.getTicketsByCustomerId(customerId);
        if (tickets.isEmpty()) {
            System.out.println("No trouble tickets found.");
            return;
        }
        System.out.printf("%-15s %-15s %-12s %-15s %-20s\n", "TICKET #", "CATEGORY", "PRIORITY", "STATUS", "CREATED DATE");
        System.out.println("----------------------------------------------------------------------------------");
        for (TroubleTicket t : tickets) {
            String createdStr = t.getCreatedDate() != null ? t.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A";
            System.out.printf("%-15s %-15s %-12s %-15s %-20s\n",
                    t.getTicketNumber(), t.getCategory(), t.getPriority(), t.getStatus(), createdStr);
        }
    }

    private void trackTicket(Scanner scanner) throws Exception {
        System.out.print("\nEnter Ticket Number (e.g. TKT100245): ");
        String tktNum = scanner.nextLine().trim();
        TroubleTicket ticket = ticketService.getTicketByNumber(tktNum);
        if (ticket == null) {
            System.out.println("❌ Ticket not found.");
            return;
        }
        System.out.println("\n========== TICKET DETAILS ==========");
        System.out.println("Ticket Number : " + ticket.getTicketNumber());
        System.out.println("Category      : " + ticket.getCategory());
        System.out.println("Priority      : " + ticket.getPriority());
        System.out.println("Status        : " + ticket.getStatus());
        System.out.println("Description   : " + ticket.getDescription());
        System.out.println("Created Date  : " + ticket.getCreatedDate());
        System.out.println("SLA Deadline  : " + ticket.getSlaDeadline());
        System.out.println("Resolution    : " + (ticket.getResolutionText() != null ? ticket.getResolutionText() : "Pending resolution"));
    }

    private void viewTicketHistory(Scanner scanner) throws Exception {
        trackTicket(scanner);
    }

    private void viewNotifications(String username) throws Exception {
        System.out.println("\n========== MY NOTIFICATIONS ==========");
        List<Notification> notifs = customerService.getNotificationsForUser(username);
        if (notifs.isEmpty()) {
            System.out.println("No notifications found.");
            return;
        }
        for (Notification n : notifs) {
            System.out.println("[" + n.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "] "
                    + n.getNotificationType() + ": " + n.getMessage());
        }
    }

    private void submitFeedback(int customerId, Scanner scanner) throws Exception {
        System.out.print("\nEnter Ticket Number or ID (e.g. TKT511874 or 1): ");
        String input = scanner.nextLine().trim();
        int tktId;
        try {
            tktId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            TroubleTicket t = ticketService.getTicketByNumber(input);
            if (t == null) {
                System.out.println("❌ Ticket not found with number: " + input);
                return;
            }
            tktId = t.getTicketId();
        }
        System.out.print("Enter Rating (1-5): ");
        int rating = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Enter Comments: ");
        String comments = scanner.nextLine().trim();

        customerService.submitFeedback(tktId, customerId, rating, comments);
        System.out.println("✓ Thank you! Your feedback has been recorded.");
    }
}
