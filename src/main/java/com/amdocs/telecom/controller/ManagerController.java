package com.amdocs.telecom.controller;

import com.amdocs.telecom.dto.DashboardMetricsDTO;
import com.amdocs.telecom.dto.EngineerWorkloadDTO;
import com.amdocs.telecom.model.*;
import com.amdocs.telecom.service.*;
import com.amdocs.telecom.service.impl.*;
import com.amdocs.telecom.util.ConsoleUI;

import java.util.List;
import java.util.Scanner;

/**
 * Executive Network Manager Dashboard Controller
 * Cyberpunk NOC & Telemetry Command Edition
 */
public class ManagerController {

    private final ManagerService managerService;
    private final ReportService reportService;
    private final TroubleTicketService ticketService;

    public ManagerController() {
        this.managerService = new ManagerServiceImpl();
        this.reportService = new ReportServiceImpl();
        this.ticketService = new TroubleTicketServiceImpl();
    }

    public boolean handleMenuChoice(String choice, UserAccount currentUser, Scanner scanner) {
        try {
            switch (choice) {
                case "1":
                    viewDashboardMetrics();
                    break;
                case "2":
                    viewEngineerPerformance();
                    break;
                case "3":
                    generateSLAReport(scanner);
                    break;
                case "4":
                    generateIncidentReport(scanner);
                    break;
                case "5":
                    manageEscalations(currentUser.getUsername(), scanner);
                    break;
                case "6":
                    ConsoleUI.printSuccess("Executive Session Concluded.");
                    return false;
                default:
                    ConsoleUI.printError("Invalid managerial option.");
            }
        } catch (Exception e) {
            ConsoleUI.printError("Managerial Telemetry Failure: " + e.getMessage());
        }
        return true;
    }

    private void viewDashboardMetrics() throws Exception {
        DashboardMetricsDTO m = managerService.getDashboardMetrics();
        ConsoleUI.printHeader("EXECUTIVE NETWORK NOC TELEMETRY", "Real-Time Enterprise Health Indicators");
        
        System.out.println("  " + ConsoleUI.BRIGHT_CYAN + ConsoleUI.BOLD + "INCIDENT METRICS OVERVIEW:" + ConsoleUI.RESET);
        ConsoleUI.printMetricCard("Active Incidents", String.valueOf(m.getTotalOpenTickets()) + " Tickets", ConsoleUI.BRIGHT_CYAN, "📋");
        ConsoleUI.printMetricCard("Critical Outages", String.valueOf(m.getCriticalIncidents()) + " Critical", ConsoleUI.BRIGHT_RED, "⚡");
        ConsoleUI.printMetricCard("SLA At Risk (<30m)", String.valueOf(m.getSlaAtRisk()) + " At Risk", ConsoleUI.BRIGHT_YELLOW, "▲");
        ConsoleUI.printMetricCard("SLA Breached", String.valueOf(m.getSlaBreached()) + " Penalized", ConsoleUI.RED, "✖");
        
        System.out.println("\n  " + ConsoleUI.BRIGHT_GREEN + ConsoleUI.BOLD + "FLEET RESOURCE CAPACITY:" + ConsoleUI.RESET);
        ConsoleUI.printProgressBar("Staff Available", m.getAvailableEngineers(), m.getTotalEngineers(), ConsoleUI.BRIGHT_GREEN);
    }

    private void viewEngineerPerformance() throws Exception {
        ConsoleUI.printHeader("ENGINEER FLEET WORKLOAD & SPECIALIZATION MATRIX", "Diagnostic Fleet Utilization");
        List<EngineerWorkloadDTO> list = managerService.getEngineerPerformance();
        
        System.out.printf("  %-10s %-20s %-24s %-12s %-14s %-14s\n",
                "EMP CODE", "ENGINEER NAME", "SPECIALIZATION", "ACTIVE TKT", "CRITICAL TKT", "FLEET STATUS");
        ConsoleUI.printDivider();
        for (EngineerWorkloadDTO dto : list) {
            String availStr = dto.getAvailability() != null ? dto.getAvailability().name() : "AVAILABLE";
            String status = "AVAILABLE".equalsIgnoreCase(availStr) ?
                    ConsoleUI.BRIGHT_GREEN + "● AVAILABLE" + ConsoleUI.RESET : 
                    ConsoleUI.BRIGHT_YELLOW + "▲ BUSY" + ConsoleUI.RESET;
            
            String critTag = dto.getCriticalTicketCount() > 0 ? 
                    ConsoleUI.BRIGHT_RED + ConsoleUI.BOLD + dto.getCriticalTicketCount() + " ⚡" + ConsoleUI.RESET : 
                    ConsoleUI.DIM + "0" + ConsoleUI.RESET;
            
            System.out.printf("  %s%-10s%s %-20s %-24s %-12d %-14s %s\n",
                    ConsoleUI.BRIGHT_CYAN, dto.getEmployeeCode(), ConsoleUI.RESET,
                    dto.getEngineerName(), dto.getSpecialization(),
                    dto.getActiveTicketCount(), critTag, status);
        }
    }

    private void generateSLAReport(Scanner scanner) throws Exception {
        ConsoleUI.printHeader("SLA COMPLIANCE AUDIT EXPORT", "Generate official telecom assurance reports");
        ConsoleUI.printPrompt("Select Format (CONSOLE / TXT / CSV)");
        String fmt = scanner.nextLine().trim().toUpperCase();
        String report = reportService.generateSlaComplianceReport(fmt);
        ConsoleUI.printSuccess("SLA report generated successfully!");
        System.out.println("\n" + report);
    }

    private void generateIncidentReport(Scanner scanner) throws Exception {
        ConsoleUI.printHeader("INCIDENT ROOT-CAUSE ANALYSIS EXPORT", "Historical failure categorization");
        ConsoleUI.printPrompt("Select Format (CONSOLE / TXT / CSV)");
        String fmt = scanner.nextLine().trim().toUpperCase();
        String report = reportService.generateIncidentAnalysisReport(fmt);
        ConsoleUI.printSuccess("Incident report generated successfully!");
        System.out.println("\n" + report);
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

    private void manageEscalations(String managerName, Scanner scanner) throws Exception {
        ConsoleUI.printHeader("ESCALATED INCIDENTS COMMAND QUEUE", "Direct Manager Intervention Queue");
        List<TroubleTicket> escalations = managerService.getEscalationQueue();
        if (escalations.isEmpty()) {
            ConsoleUI.printSuccess("Zero escalated incidents in queue. Normal operations.");
            return;
        }
        
        System.out.printf("  %-5s %-16s %-18s %-16s %-18s\n", "ID", "TICKET NUMBER", "CATEGORY", "PRIORITY", "CURRENT STATE");
        ConsoleUI.printDivider();
        for (TroubleTicket t : escalations) {
            System.out.printf("  %-5d %s%-16s%s %-18s %-16s %s\n",
                    t.getTicketId(),
                    ConsoleUI.BRIGHT_YELLOW, t.getTicketNumber(), ConsoleUI.RESET,
                    t.getCategory(),
                    ConsoleUI.getPriorityBadge(t.getPriority().name()),
                    ConsoleUI.getStatusBadge(t.getStatus().name()));
        }

        ConsoleUI.printPrompt("Enter Ticket Number or ID to override dispatch");
        int tktId = resolveTicketId(scanner.nextLine().trim());
        
        ConsoleUI.printPrompt("Enter Target Engineer ID for direct intervention");
        int engId = Integer.parseInt(scanner.nextLine().trim());

        ticketService.assignEngineerManual(tktId, engId, managerName);
        ConsoleUI.printSuccess("Managerial dispatch override executed. Ticket assigned to Engineer #" + engId);
    }
}
