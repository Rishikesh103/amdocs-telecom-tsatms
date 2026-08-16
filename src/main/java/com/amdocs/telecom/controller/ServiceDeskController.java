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
 * Cyberpunk NOC & Incident Dispatch Edition
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
                    ConsoleUI.printSuccess("Operator logged out. Console secured.");
                    return false;
                default:
                    ConsoleUI.printError("Invalid operations command.");
            }
        } catch (Exception e) {
            ConsoleUI.printError("Service Desk Exception: " + e.getMessage());
        }
        return true;
    }

    private void viewOpenTickets() throws Exception {
        ConsoleUI.printHeader("NOC INCIDENT QUEUE (ACTIVE TICKETS)", "Real-Time Telemetry & SLA Tracking");
        List<TicketSummaryDTO> list = ticketService.getAllOpenTickets();
        if (list.isEmpty()) {
            ConsoleUI.printSuccess("All incident queues clear! 0 open tickets.");
            return;
        }
        
        System.out.printf("  %-5s %-16s %-20s %-16s %-18s %-15s %-18s\n",
                "ID", "TICKET NUMBER", "CUSTOMER / ENTITY", "PRIORITY", "STATUS", "SLA HEALTH", "ENGINEER");
        ConsoleUI.printDivider();
        for (TicketSummaryDTO dto : list) {
            String engName = dto.getAssignedEngineerName() != null ? 
                    ConsoleUI.BRIGHT_WHITE + dto.getAssignedEngineerName() + ConsoleUI.RESET : 
                    ConsoleUI.DIM + "⚡ UNASSIGNED" + ConsoleUI.RESET;
            
            String priorityStr = dto.getPriority() != null ? dto.getPriority().name() : null;
            String statusStr = dto.getStatus() != null ? dto.getStatus().name() : null;
            String slaStr = dto.getSlaStatus() != null ? dto.getSlaStatus().name() : null;

            System.out.printf("  %-5d %s%-16s%s %-20s %-16s %-18s %-15s %s\n",
                    dto.getTicketId(),
                    ConsoleUI.BRIGHT_YELLOW, dto.getTicketNumber(), ConsoleUI.RESET,
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
            throw new Exception("Ticket not found with reference: " + input);
        }
    }

    private void assignEngineer(Scanner scanner) throws Exception {
        ConsoleUI.printHeader("ENGINEER DISPATCH & ROUTING ENGINE", "Assign tickets based on workload & skills");
        ConsoleUI.printPrompt("Enter Ticket Number or ID (e.g. TT-2026-004525)");
        int tktId = resolveTicketId(scanner.nextLine().trim());
        
        System.out.println("\n  Routing Strategy:");
        System.out.println("  1. " + ConsoleUI.BRIGHT_CYAN + ConsoleUI.BOLD + "🤖 Intelligent Auto-Assignment" + ConsoleUI.RESET + " (Java 8 Stream: Least Workload + Highest Skill)");
        System.out.println("  2. " + ConsoleUI.BRIGHT_WHITE + "👤 Manual Dispatch" + ConsoleUI.RESET + " (Select from Active Engineers)");
        ConsoleUI.printPrompt("Select Strategy (1 or 2)");
        String mode = scanner.nextLine().trim();

        if ("1".equals(mode)) {
            System.out.println(ConsoleUI.DIM + "  ⚡ Scanning network engineer fleet availability..." + ConsoleUI.RESET);
            NetworkEngineer assigned = ticketService.assignEngineerAuto(tktId);
            ConsoleUI.printSuccess("Auto-Assignment Optimization Complete!");
            ConsoleUI.printCard("DISPATCH CONFIRMATION", Arrays.asList(
                    "Assigned Engineer : " + ConsoleUI.BRIGHT_GREEN + assigned.getEngineerName() + ConsoleUI.RESET,
                    "Specialization    : " + assigned.getSpecialization(),
                    "Region / Zone     : " + assigned.getRegion(),
                    "Experience        : " + assigned.getExperienceYears() + " Years",
                    "Active Workload   : " + (assigned.getActiveTicketCount() + 1) + " tickets"
            ), ConsoleUI.BRIGHT_GREEN);
        } else {
            List<NetworkEngineer> engineers = engineerService.getAllEngineers();
            ConsoleUI.printSection("AVAILABLE NETWORK ENGINEERS");
            System.out.printf("  %-4s %-20s %-22s %-12s %-12s\n", "ID", "ENGINEER NAME", "SPECIALIZATION", "ACTIVE TKT", "STATUS");
            ConsoleUI.printDivider();
            for (NetworkEngineer e : engineers) {
                String avail = "AVAILABLE".equalsIgnoreCase(e.getAvailability().name()) ?
                        ConsoleUI.BRIGHT_GREEN + "● AVAILABLE" + ConsoleUI.RESET : 
                        ConsoleUI.BRIGHT_YELLOW + "▲ BUSY" + ConsoleUI.RESET;
                System.out.printf("  %-4d %-20s %-22s %-12d %s\n",
                        e.getEngineerId(), e.getEngineerName(), e.getSpecialization(), e.getActiveTicketCount(), avail);
            }
            ConsoleUI.printPrompt("Enter Engineer ID to Assign");
            int engId = Integer.parseInt(scanner.nextLine().trim());
            ticketService.assignEngineerManual(tktId, engId, "SERVICE_DESK_ADMIN");
            ConsoleUI.printSuccess("Engineer manual assignment registered.");
        }
    }

    private void reassignTicket(Scanner scanner) throws Exception {
        assignEngineer(scanner);
    }

    private void escalateTicket(String adminName, Scanner scanner) throws Exception {
        ConsoleUI.printHeader("INCIDENT ESCALATION PROTOCOL", "Hierarchical Severity Level Elevation");
        ConsoleUI.printPrompt("Enter Ticket Number or ID to escalate");
        int tktId = resolveTicketId(scanner.nextLine().trim());
        
        System.out.println("  1. " + ConsoleUI.BRIGHT_YELLOW + "Level 1: TEAM_LEAD" + ConsoleUI.RESET);
        System.out.println("  2. " + ConsoleUI.BRIGHT_RED + "Level 2: NETWORK_MANAGER" + ConsoleUI.RESET);
        System.out.println("  3. " + ConsoleUI.BG_RED + ConsoleUI.BRIGHT_WHITE + " Level 3: OPERATIONS_MANAGER " + ConsoleUI.RESET);
        ConsoleUI.printPrompt("Select Escalation Level (1-3)");
        String lvlChoice = scanner.nextLine().trim();
        EscalationLevel level = EscalationLevel.TEAM_LEAD;
        if ("2".equals(lvlChoice)) level = EscalationLevel.NETWORK_MANAGER;
        else if ("3".equals(lvlChoice)) level = EscalationLevel.OPERATIONS_MANAGER;

        ConsoleUI.printPrompt("Reason for Escalation");
        String reason = scanner.nextLine().trim();

        ticketService.escalateTicket(tktId, level, reason, adminName);
        ConsoleUI.printSuccess("Ticket escalated to " + level + " with high-priority status.");
    }

    private void updatePriority(String adminName, Scanner scanner) throws Exception {
        ConsoleUI.printHeader("DYNAMIC PRIORITY & SLA OVERRIDE", "Recalculates SLA deadline automatically");
        ConsoleUI.printPrompt("Enter Ticket Number or ID");
        int tktId = resolveTicketId(scanner.nextLine().trim());
        
        System.out.println("  1. " + ConsoleUI.getPriorityBadge("LOW"));
        System.out.println("  2. " + ConsoleUI.getPriorityBadge("MEDIUM"));
        System.out.println("  3. " + ConsoleUI.getPriorityBadge("HIGH"));
        System.out.println("  4. " + ConsoleUI.getPriorityBadge("CRITICAL"));
        ConsoleUI.printPrompt("Select New Priority (1-4)");
        String pChoice = scanner.nextLine().trim();
        Priority p = Priority.MEDIUM;
        if ("1".equals(pChoice)) p = Priority.LOW;
        else if ("3".equals(pChoice)) p = Priority.HIGH;
        else if ("4".equals(pChoice)) p = Priority.CRITICAL;

        ticketService.updateTicketPriority(tktId, p, adminName, "Priority updated by Service Desk Operator");
        ConsoleUI.printSuccess("Priority reclassified to " + p + ". SLA target timer recalculated.");
    }

    private void monitorSLA() throws Exception {
        ConsoleUI.printHeader("REAL-TIME SLA ENGINE AUDIT", "Scanning open tickets against threshold matrices");
        int count = slaMonitorService.checkAndProcessSLAs();
        ConsoleUI.printSuccess("Real-time SLA audit cycle complete. Critical alerts triggered: " + count);
    }

    private void closeTicket(String adminName, Scanner scanner) throws Exception {
        ConsoleUI.printHeader("TICKET CLOSURE & SIGN-OFF", "Final quality assurance verification");
        ConsoleUI.printPrompt("Enter Ticket Number or ID to close");
        int tktId = resolveTicketId(scanner.nextLine().trim());
        ConsoleUI.printPrompt("Enter Closure Notes / Resolution Verification");
        String remarks = scanner.nextLine().trim();
        ticketService.closeTicket(tktId, remarks, adminName);
        ConsoleUI.printSuccess("Ticket #" + tktId + " marked as CLOSED in permanent records.");
    }

    private void generateReports(Scanner scanner) throws Exception {
        ConsoleUI.printHeader("ANALYTICS & AUDIT REPORTING ENGINE", "Export compliance, workload, and root causes");
        System.out.println("  1. " + ConsoleUI.BRIGHT_CYAN + "SLA Compliance & Breach Analysis Report" + ConsoleUI.RESET);
        System.out.println("  2. " + ConsoleUI.BRIGHT_YELLOW + "Network Engineer Workload & Performance Report" + ConsoleUI.RESET);
        System.out.println("  3. " + ConsoleUI.BRIGHT_MAGENTA + "Incident Root-Cause & Categorization Report" + ConsoleUI.RESET);
        ConsoleUI.printPrompt("Select Report Type (1-3)");
        String rChoice = scanner.nextLine().trim();

        ConsoleUI.printPrompt("Export Format (CONSOLE / TXT / CSV)");
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
                ConsoleUI.printError("Invalid report type specified.");
                return;
        }

        ConsoleUI.printSuccess("Report generated successfully!");
        System.out.println("\n" + report);
    }
}
