package com.amdocs.telecom.controller;

import com.amdocs.telecom.dto.TicketSummaryDTO;
import com.amdocs.telecom.model.*;
import com.amdocs.telecom.service.*;
import com.amdocs.telecom.service.impl.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class ServiceDeskController {

    private final TroubleTicketService ticketService;
    private final EngineerService engineerService;
    private final ReportService reportService;
    private final SLAMonitorService slaMonitorService;

    public ServiceDeskController() {
        this.ticketService = new TroubleTicketServiceImpl();
        this.engineerService = new EngineerServiceImpl();
        this.reportService = new ReportServiceImpl();
        this.slaMonitorService = new SLAMonitorServiceImpl();
    }

    public boolean handleMenuChoice(String choice, UserAccount currentUser, Scanner scanner) {
        try {
            switch (choice) {
                case "1":
                    viewOpenTickets();
                    break;
                case "2":
                    assignEngineer(scanner);
                    break;
                case "3":
                    reassignTicket(scanner);
                    break;
                case "4":
                    escalateTicket(currentUser.getUsername(), scanner);
                    break;
                case "5":
                    updatePriority(currentUser.getUsername(), scanner);
                    break;
                case "6":
                    monitorSLA();
                    break;
                case "7":
                    closeTicket(currentUser.getUsername(), scanner);
                    break;
                case "8":
                    generateReports(scanner);
                    break;
                case "9":
                    System.out.println("✓ Logged out successfully.");
                    return false;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } catch (Exception e) {
            System.err.println("Error processing Service Desk request: " + e.getMessage());
        }
        return true;
    }

    private void viewOpenTickets() throws Exception {
        System.out.println("\n================================ OPEN TROUBLE TICKETS ================================");
        List<TicketSummaryDTO> list = ticketService.getAllOpenTickets();
        if (list.isEmpty()) {
            System.out.println("No open tickets.");
            return;
        }
        System.out.printf("%-5s %-12s %-20s %-10s %-12s %-12s %-18s\n", "ID", "TICKET #", "CUSTOMER", "PRIORITY", "STATUS", "SLA STATUS", "ASSIGNED TO");
        System.out.println("--------------------------------------------------------------------------------------");
        for (TicketSummaryDTO dto : list) {
            System.out.printf("%-5d %-12s %-20s %-10s %-12s %-12s %-18s\n",
                    dto.getTicketId(), dto.getTicketNumber(),
                    dto.getCustomerName() != null ? dto.getCustomerName() : "N/A",
                    dto.getPriority(), dto.getStatus(), dto.getSlaStatus(),
                    dto.getAssignedEngineerName() != null ? dto.getAssignedEngineerName() : "Unassigned");
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

    private void assignEngineer(Scanner scanner) throws Exception {
        System.out.print("\nEnter Ticket Number or ID to assign engineer: ");
        int tktId = resolveTicketId(scanner.nextLine().trim());
        System.out.println("Assignment Mode:");
        System.out.println("1. Automatic Recommendation Engine (Java 8 Stream API)");
        System.out.println("2. Manual Engineer Selection");
        System.out.print("Choice: ");
        String mode = scanner.nextLine().trim();

        if ("1".equals(mode)) {
            NetworkEngineer assigned = ticketService.assignEngineerAuto(tktId);
            System.out.println("\n✓ Auto-Assignment Successful!");
            System.out.println("Assigned Engineer: " + assigned.getEngineerName() + " (" + assigned.getSpecialization() + ")");
        } else {
            List<NetworkEngineer> engineers = engineerService.getAllEngineers();
            System.out.println("\nAvailable Engineers:");
            for (NetworkEngineer e : engineers) {
                System.out.printf("ID: %d | %s | Spec: %s | Active Tkts: %d | Availability: %s\n",
                        e.getEngineerId(), e.getEngineerName(), e.getSpecialization(), e.getActiveTicketCount(), e.getAvailability());
            }
            System.out.print("Enter Engineer ID: ");
            int engId = Integer.parseInt(scanner.nextLine().trim());
            ticketService.assignEngineerManual(tktId, engId, "SERVICE_DESK_ADMIN");
            System.out.println("\n✓ Engineer manually assigned successfully.");
        }
    }

    private void reassignTicket(Scanner scanner) throws Exception {
        assignEngineer(scanner);
    }

    private void escalateTicket(String adminName, Scanner scanner) throws Exception {
        System.out.print("\nEnter Ticket Number or ID to escalate: ");
        int tktId = resolveTicketId(scanner.nextLine().trim());
        System.out.println("Escalation Levels:");
        System.out.println("1. TEAM_LEAD");
        System.out.println("2. NETWORK_MANAGER");
        System.out.println("3. OPERATIONS_MANAGER");
        System.out.print("Select Level (1-3): ");
        String lvlChoice = scanner.nextLine().trim();
        EscalationLevel level = EscalationLevel.TEAM_LEAD;
        if ("2".equals(lvlChoice)) level = EscalationLevel.NETWORK_MANAGER;
        else if ("3".equals(lvlChoice)) level = EscalationLevel.OPERATIONS_MANAGER;

        System.out.print("Enter Reason for Escalation: ");
        String reason = scanner.nextLine().trim();

        ticketService.escalateTicket(tktId, level, reason, adminName);
        System.out.println("✓ Ticket escalated successfully to " + level);
    }

    private void updatePriority(String adminName, Scanner scanner) throws Exception {
        System.out.print("\nEnter Ticket Number or ID: ");
        int tktId = resolveTicketId(scanner.nextLine().trim());
        System.out.print("Select New Priority (1. LOW, 2. MEDIUM, 3. HIGH, 4. CRITICAL): ");
        String pChoice = scanner.nextLine().trim();
        Priority p = Priority.MEDIUM;
        if ("1".equals(pChoice)) p = Priority.LOW;
        else if ("3".equals(pChoice)) p = Priority.HIGH;
        else if ("4".equals(pChoice)) p = Priority.CRITICAL;

        ticketService.updateTicketPriority(tktId, p, adminName, "Priority modified by admin");
        System.out.println("✓ Priority updated to " + p + " and SLA deadline recalculated.");
    }

    private void monitorSLA() throws Exception {
        System.out.println("\n========== REAL-TIME SLA MONITORING ==========");
        int count = slaMonitorService.checkAndProcessSLAs();
        System.out.println("✓ SLA Audit completed. Alerts triggered: " + count);
    }

    private void closeTicket(String adminName, Scanner scanner) throws Exception {
        System.out.print("\nEnter Ticket Number or ID to close: ");
        int tktId = resolveTicketId(scanner.nextLine().trim());
        System.out.print("Enter Closure Remarks: ");
        String remarks = scanner.nextLine().trim();
        ticketService.closeTicket(tktId, remarks, adminName);
        System.out.println("✓ Ticket closed successfully.");
    }

    private void generateReports(Scanner scanner) throws Exception {
        System.out.println("\n========== GENERATE REPORTS ==========");
        System.out.println("1. SLA Compliance Report");
        System.out.println("2. Engineer Performance Report");
        System.out.println("3. Incident Analysis Report");
        System.out.print("Choice: ");
        String rChoice = scanner.nextLine().trim();

        System.out.print("Format (CONSOLE / TXT / CSV): ");
        String format = scanner.nextLine().trim().toUpperCase();

        String report = "";
        switch (rChoice) {
            case "1":
                report = reportService.generateSlaComplianceReport(format);
                break;
            case "2":
                report = reportService.generateEngineerPerformanceReport(format);
                break;
            case "3":
                report = reportService.generateIncidentAnalysisReport(format);
                break;
            default:
                System.out.println("Invalid report choice.");
                return;
        }

        System.out.println("\n" + report);
    }
}
