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
 * Professional Enterprise Design with Clean Back/Exit Navigation
 */
public class ManagerController {

    private final ManagerService managerService;
    private final ReportService reportService;
    private final TroubleTicketService ticketService;
    private final EngineerService engineerService;

    public ManagerController() {
        this.managerService = new ManagerServiceImpl();
        this.reportService = new ReportServiceImpl();
        this.ticketService = new TroubleTicketServiceImpl();
        this.engineerService = new EngineerServiceImpl();
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
                case "0":
                    ConsoleUI.printSuccess("Logged out successfully. Returned to main gateway.");
                    return false;
                default:
                    ConsoleUI.printError("Invalid option. Please select 1-6 (or 0 to Sign Out).");
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
        
        System.out.printf("  %-4s %-10s %-20s %-24s %-12s %-14s %-12s\n",
                "ID", "EMP CODE", "ENGINEER NAME", "SPECIALIZATION", "ACTIVE TKT", "CRITICAL TKT", "STATUS");
        ConsoleUI.printDivider();
        for (EngineerWorkloadDTO dto : list) {
            String availStr = dto.getAvailability() != null ? dto.getAvailability().name() : "AVAILABLE";
            String status = "AVAILABLE".equalsIgnoreCase(availStr) ?
                    ConsoleUI.GREEN + "AVAILABLE" + ConsoleUI.RESET : 
                    ConsoleUI.YELLOW + "BUSY" + ConsoleUI.RESET;
            
            System.out.printf("  %-4d %-10s %-20s %-24s %-12d %-14d %s\n",
                    dto.getEngineerId() != null ? dto.getEngineerId() : 0,
                    dto.getEmployeeCode(),
                    dto.getEngineerName(),
                    dto.getSpecialization(),
                    dto.getActiveTicketCount(),
                    dto.getCriticalTicketCount(),
                    status);
        }
    }

    private String promptReportFormat(Scanner scanner) {
        ConsoleUI.printSection("Select Output Format");
        System.out.println("  [1] Console Display (View on Screen)");
        System.out.println("  [2] Plain Text File (.txt exported to reports/)");
        System.out.println("  [3] CSV Spreadsheet (.csv exported to reports/)");
        System.out.println("  [0] Cancel");
        ConsoleUI.printPrompt("Select format (1-3 or 0 to Cancel)");
        String rawFmt = scanner.nextLine().trim().toUpperCase();
        if ("0".equals(rawFmt) || "BACK".equalsIgnoreCase(rawFmt) || rawFmt.isEmpty()) {
            return null;
        }
        if ("2".equals(rawFmt) || "TXT".equalsIgnoreCase(rawFmt)) return "TXT";
        if ("3".equals(rawFmt) || "CSV".equalsIgnoreCase(rawFmt)) return "CSV";
        return "CONSOLE";
    }

    private void generateSLAReport(Scanner scanner) throws Exception {
        ConsoleUI.printHeader("SLA Compliance Report", "Audit resolution time compliance");
        String fmt = promptReportFormat(scanner);
        if (fmt == null) {
            ConsoleUI.printInfo("Action canceled.");
            return;
        }
        String report = reportService.generateSlaComplianceReport(fmt);
        ConsoleUI.printSuccess("Report generated successfully (" + fmt + " mode).");
        System.out.println("\n" + report);
    }

    private void generateIncidentReport(Scanner scanner) throws Exception {
        ConsoleUI.printHeader("Incident Analysis Report", "Historical breakdown by category");
        String fmt = promptReportFormat(scanner);
        if (fmt == null) {
            ConsoleUI.printInfo("Action canceled.");
            return;
        }
        String report = reportService.generateIncidentAnalysisReport(fmt);
        ConsoleUI.printSuccess("Report generated successfully (" + fmt + " mode).");
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

    private int resolveEngineerId(String input) throws Exception {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            List<NetworkEngineer> list = engineerService.getAllEngineers();
            for (NetworkEngineer eng : list) {
                if (eng.getEmployeeCode().equalsIgnoreCase(input)) {
                    return eng.getEngineerId();
                }
            }
            throw new Exception("Engineer not found for ID/Code: " + input);
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

        ConsoleUI.printPrompt("Enter Ticket Number or ID to reassign (or 0 to Cancel)");
        String rawTkt = scanner.nextLine().trim();
        if ("0".equals(rawTkt) || "back".equalsIgnoreCase(rawTkt) || rawTkt.isEmpty()) {
            ConsoleUI.printInfo("Action canceled.");
            return;
        }
        int tktId = resolveTicketId(rawTkt);
        
        List<NetworkEngineer> engineers = engineerService.getAllEngineers();
        ConsoleUI.printSection("Available Network Engineer Fleet");
        System.out.printf("  %-4s %-10s %-20s %-22s %-12s %-12s\n", "ID", "EMP CODE", "NAME", "SPECIALIZATION", "ACTIVE TKT", "STATUS");
        ConsoleUI.printDivider();
        for (NetworkEngineer e : engineers) {
            System.out.printf("  %-4d %-10s %-20s %-22s %-12d %-12s\n",
                    e.getEngineerId(), e.getEmployeeCode(), e.getEngineerName(), e.getSpecialization(), e.getActiveTicketCount(), e.getAvailability());
        }

        ConsoleUI.printPrompt("Enter Target Engineer ID or Employee Code (e.g., 1 or ENG1008) (or 0 to Cancel)");
        String rawEng = scanner.nextLine().trim();
        if ("0".equals(rawEng) || "back".equalsIgnoreCase(rawEng) || rawEng.isEmpty()) {
            ConsoleUI.printInfo("Action canceled.");
            return;
        }
        int engId = resolveEngineerId(rawEng);

        ticketService.assignEngineerManual(tktId, engId, managerName);
        ConsoleUI.printSuccess("Escalated ticket reassigned to Engineer #" + engId + ".");
    }
}
