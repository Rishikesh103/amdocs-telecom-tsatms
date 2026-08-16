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
 * Professional Enterprise Design
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
                    ConsoleUI.printSuccess("Logged out successfully.");
                    return false;
                default:
                    ConsoleUI.printError("Invalid option. Please try again.");
            }
        } catch (Exception e) {
            ConsoleUI.printError("Managerial Telemetry Error: " + e.getMessage());
        }
        return true;
    }

    private void viewDashboardMetrics() throws Exception {
        DashboardMetricsDTO m = managerService.getDashboardMetrics();
        ConsoleUI.printHeader("Operational Health & KPI Summary", "Real-Time Network Operations Overview");
        
        ConsoleUI.printSection("Incident Metrics");
        ConsoleUI.printMetric("Total Open Tickets", String.valueOf(m.getTotalOpenTickets()));
        ConsoleUI.printMetric("Critical Incidents", String.valueOf(m.getCriticalIncidents()));
        ConsoleUI.printMetric("SLA At Risk (<30m)", String.valueOf(m.getSlaAtRisk()));
        ConsoleUI.printMetric("SLA Breached", String.valueOf(m.getSlaBreached()));
        
        ConsoleUI.printSection("Resource Utilization");
        ConsoleUI.printProgressBar("Engineer Staffing", m.getAvailableEngineers(), m.getTotalEngineers());
    }

    private void viewEngineerPerformance() throws Exception {
        ConsoleUI.printHeader("Engineer Fleet Workload & Performance", "Resource Allocation Matrix");
        List<EngineerWorkloadDTO> list = managerService.getEngineerPerformance();
        
        System.out.printf("  %-10s %-20s %-24s %-12s %-14s %-12s\n",
                "EMP CODE", "ENGINEER NAME", "SPECIALIZATION", "ACTIVE TKT", "CRITICAL TKT", "STATUS");
        ConsoleUI.printDivider();
        for (EngineerWorkloadDTO dto : list) {
            String availStr = dto.getAvailability() != null ? dto.getAvailability().name() : "AVAILABLE";
            String status = "AVAILABLE".equalsIgnoreCase(availStr) ?
                    ConsoleUI.GREEN + "AVAILABLE" + ConsoleUI.RESET : 
                    ConsoleUI.YELLOW + "BUSY" + ConsoleUI.RESET;
            
            System.out.printf("  %-10s %-20s %-24s %-12d %-14d %s\n",
                    dto.getEmployeeCode(),
                    dto.getEngineerName(),
                    dto.getSpecialization(),
                    dto.getActiveTicketCount(),
                    dto.getCriticalTicketCount(),
                    status);
        }
    }

    private void generateSLAReport(Scanner scanner) throws Exception {
        ConsoleUI.printHeader("SLA Compliance Report", "Audit resolution time compliance");
        ConsoleUI.printPrompt("Select Format (CONSOLE / TXT / CSV)");
        String fmt = scanner.nextLine().trim().toUpperCase();
        String report = reportService.generateSlaComplianceReport(fmt);
        ConsoleUI.printSuccess("Report generated successfully.");
        System.out.println("\n" + report);
    }

    private void generateIncidentReport(Scanner scanner) throws Exception {
        ConsoleUI.printHeader("Incident Analysis Report", "Historical breakdown by category");
        ConsoleUI.printPrompt("Select Format (CONSOLE / TXT / CSV)");
        String fmt = scanner.nextLine().trim().toUpperCase();
        String report = reportService.generateIncidentAnalysisReport(fmt);
        ConsoleUI.printSuccess("Report generated successfully.");
        System.out.println("\n" + report);
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

    private void manageEscalations(String managerName, Scanner scanner) throws Exception {
        ConsoleUI.printHeader("Escalated Incidents Queue", "Managerial Intervention Queue");
        List<TroubleTicket> escalations = managerService.getEscalationQueue();
        if (escalations.isEmpty()) {
            ConsoleUI.printSuccess("No escalated incidents pending intervention.");
            return;
        }
        
        System.out.printf("  %-5s %-16s %-18s %-12s %-16s\n", "ID", "TICKET NUMBER", "CATEGORY", "PRIORITY", "STATUS");
        ConsoleUI.printDivider();
        for (TroubleTicket t : escalations) {
            System.out.printf("  %-5d %-16s %-18s %-12s %s\n",
                    t.getTicketId(),
                    t.getTicketNumber(),
                    t.getCategory(),
                    ConsoleUI.getPriorityBadge(t.getPriority().name()),
                    ConsoleUI.getStatusBadge(t.getStatus().name()));
        }

        ConsoleUI.printPrompt("Enter Ticket Number or ID to reassign");
        int tktId = resolveTicketId(scanner.nextLine().trim());
        
        ConsoleUI.printPrompt("Enter Target Engineer ID");
        int engId = Integer.parseInt(scanner.nextLine().trim());

        ticketService.assignEngineerManual(tktId, engId, managerName);
        ConsoleUI.printSuccess("Escalated ticket reassigned to Engineer #" + engId + ".");
    }
}
