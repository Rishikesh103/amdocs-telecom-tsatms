package com.amdocs.telecom.controller;

import com.amdocs.telecom.dto.TicketSummaryDTO;
import com.amdocs.telecom.model.*;
import com.amdocs.telecom.service.*;
import com.amdocs.telecom.service.impl.*;
import com.amdocs.telecom.util.ConsoleUI;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Service Desk Operations Console Controller
 * Professional Enterprise Design
 */
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
                    ConsoleUI.printSuccess("Logged out successfully.");
                    return false;
                default:
                    ConsoleUI.printError("Invalid option. Please try again.");
            }
        } catch (Exception e) {
            ConsoleUI.printError("Service Desk Error: " + e.getMessage());
        }
        return true;
    }

    private void viewOpenTickets() throws Exception {
        ConsoleUI.printHeader("Active Incident Queue", "Open trouble tickets and SLA status");
        List<TicketSummaryDTO> list = ticketService.getAllOpenTickets();
        if (list.isEmpty()) {
            ConsoleUI.printSuccess("Incident queue clear. No open tickets.");
            return;
        }
        
        System.out.printf("  %-5s %-16s %-20s %-12s %-16s %-14s %-18s\n",
                "ID", "TICKET NUMBER", "CUSTOMER", "PRIORITY", "STATUS", "SLA STATUS", "ASSIGNED TO");
        ConsoleUI.printDivider();
        for (TicketSummaryDTO dto : list) {
            String engName = dto.getAssignedEngineerName() != null ? dto.getAssignedEngineerName() : "Unassigned";
            String priorityStr = dto.getPriority() != null ? dto.getPriority().name() : null;
            String statusStr = dto.getStatus() != null ? dto.getStatus().name() : null;
            String slaStr = dto.getSlaStatus() != null ? dto.getSlaStatus().name() : null;

            System.out.printf("  %-5d %-16s %-20s %-12s %-16s %-14s %-18s\n",
                    dto.getTicketId(),
                    dto.getTicketNumber(),
                    dto.getCustomerName() != null ? dto.getCustomerName() : "N/A",
                    ConsoleUI.getPriorityBadge(priorityStr),
                    ConsoleUI.getStatusBadge(statusStr),
                    ConsoleUI.getSLABadge(slaStr),
                    engName);
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

    private void assignEngineer(Scanner scanner) throws Exception {
        ConsoleUI.printHeader("Engineer Assignment", "Dispatch ticket to network engineer");
        ConsoleUI.printPrompt("Enter Ticket Number or ID (e.g. TT-2026-004525)");
        int tktId = resolveTicketId(scanner.nextLine().trim());
        
        System.out.println("  [1] Automatic Recommendation (Stream API: Least Workload + Experience)");
        System.out.println("  [2] Manual Selection");
        ConsoleUI.printPrompt("Select mode (1 or 2)");
        String mode = scanner.nextLine().trim();

        if ("1".equals(mode)) {
            NetworkEngineer assigned = ticketService.assignEngineerAuto(tktId);
            ConsoleUI.printSuccess("Auto-assignment completed.");
            ConsoleUI.printCard("Assignment Details", Arrays.asList(
                    "Engineer Name   : " + assigned.getEngineerName(),
                    "Specialization  : " + assigned.getSpecialization(),
                    "Region          : " + assigned.getRegion(),
                    "Experience      : " + assigned.getExperienceYears() + " Years",
                    "Active Tickets  : " + (assigned.getActiveTicketCount() + 1)
            ));
        } else {
            List<NetworkEngineer> engineers = engineerService.getAllEngineers();
            ConsoleUI.printSection("Available Engineers");
            System.out.printf("  %-4s %-20s %-22s %-12s %-12s\n", "ID", "NAME", "SPECIALIZATION", "ACTIVE TKT", "STATUS");
            ConsoleUI.printDivider();
            for (NetworkEngineer e : engineers) {
                System.out.printf("  %-4d %-20s %-22s %-12d %-12s\n",
                        e.getEngineerId(), e.getEngineerName(), e.getSpecialization(), e.getActiveTicketCount(), e.getAvailability());
            }
            ConsoleUI.printPrompt("Enter Engineer ID");
            int engId = Integer.parseInt(scanner.nextLine().trim());
            ticketService.assignEngineerManual(tktId, engId, "SERVICE_DESK_ADMIN");
            ConsoleUI.printSuccess("Engineer manually assigned.");
        }
    }

    private void reassignTicket(Scanner scanner) throws Exception {
        assignEngineer(scanner);
    }

    private void escalateTicket(String adminName, Scanner scanner) throws Exception {
        ConsoleUI.printHeader("Escalate Ticket", "Elevate incident priority level");
        ConsoleUI.printPrompt("Enter Ticket Number or ID");
        int tktId = resolveTicketId(scanner.nextLine().trim());
        
        System.out.println("  [1] TEAM_LEAD");
        System.out.println("  [2] NETWORK_MANAGER");
        System.out.println("  [3] OPERATIONS_MANAGER");
        ConsoleUI.printPrompt("Select escalation level (1-3)");
        String lvlChoice = scanner.nextLine().trim();
        EscalationLevel level = EscalationLevel.TEAM_LEAD;
        if ("2".equals(lvlChoice)) level = EscalationLevel.NETWORK_MANAGER;
        else if ("3".equals(lvlChoice)) level = EscalationLevel.OPERATIONS_MANAGER;

        ConsoleUI.printPrompt("Enter reason for escalation");
        String reason = scanner.nextLine().trim();

        ticketService.escalateTicket(tktId, level, reason, adminName);
        ConsoleUI.printSuccess("Ticket escalated to " + level + ".");
    }

    private void updatePriority(String adminName, Scanner scanner) throws Exception {
        ConsoleUI.printHeader("Update Ticket Priority", "Recalculate SLA deadline");
        ConsoleUI.printPrompt("Enter Ticket Number or ID");
        int tktId = resolveTicketId(scanner.nextLine().trim());
        
        System.out.println("  [1] LOW");
        System.out.println("  [2] MEDIUM");
        System.out.println("  [3] HIGH");
        System.out.println("  [4] CRITICAL");
        ConsoleUI.printPrompt("Select new priority (1-4)");
        String pChoice = scanner.nextLine().trim();
        Priority p = Priority.MEDIUM;
        if ("1".equals(pChoice)) p = Priority.LOW;
        else if ("3".equals(pChoice)) p = Priority.HIGH;
        else if ("4".equals(pChoice)) p = Priority.CRITICAL;

        ticketService.updateTicketPriority(tktId, p, adminName, "Priority updated by Service Desk");
        ConsoleUI.printSuccess("Priority updated to " + p + ". SLA deadline recalculated.");
    }

    private void monitorSLA() throws Exception {
        ConsoleUI.printHeader("Real-Time SLA Audit", "Audit active tickets against SLA thresholds");
        int count = slaMonitorService.checkAndProcessSLAs();
        ConsoleUI.printSuccess("SLA audit cycle finished. Alerts generated: " + count);
    }

    private void closeTicket(String adminName, Scanner scanner) throws Exception {
        ConsoleUI.printHeader("Close Ticket", "Finalize and close resolved incident");
        ConsoleUI.printPrompt("Enter Ticket Number or ID");
        int tktId = resolveTicketId(scanner.nextLine().trim());
        ConsoleUI.printPrompt("Enter closure remarks");
        String remarks = scanner.nextLine().trim();
        ticketService.closeTicket(tktId, remarks, adminName);
        ConsoleUI.printSuccess("Ticket #" + tktId + " closed successfully.");
    }

    private void generateReports(Scanner scanner) throws Exception {
        ConsoleUI.printHeader("Generate Operational Reports", "Export analytics and compliance data");
        System.out.println("  [1] SLA Compliance Report");
        System.out.println("  [2] Engineer Performance Report");
        System.out.println("  [3] Incident Analysis Report");
        ConsoleUI.printPrompt("Select report (1-3)");
        String rChoice = scanner.nextLine().trim();

        ConsoleUI.printPrompt("Format (CONSOLE / TXT / CSV)");
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
                ConsoleUI.printError("Invalid report selection.");
                return;
        }

        ConsoleUI.printSuccess("Report generated successfully.");
        System.out.println("\n" + report);
    }
}
