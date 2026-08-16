package com.amdocs.telecom.controller;

import com.amdocs.telecom.dto.DashboardMetricsDTO;
import com.amdocs.telecom.dto.EngineerWorkloadDTO;
import com.amdocs.telecom.model.*;
import com.amdocs.telecom.service.*;
import com.amdocs.telecom.service.impl.*;

import java.util.List;
import java.util.Scanner;

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
                    System.out.println("✓ Logged out successfully.");
                    return false;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } catch (Exception e) {
            System.err.println("Error processing Manager request: " + e.getMessage());
        }
        return true;
    }

    private void viewDashboardMetrics() throws Exception {
        DashboardMetricsDTO m = managerService.getDashboardMetrics();
        System.out.println("\n========================================================");
        System.out.println("            NETWORK MANAGER DASHBOARD METRICS           ");
        System.out.println("========================================================");
        System.out.println("Total Open Trouble Tickets : " + m.getTotalOpenTickets());
        System.out.println("Critical Incidents          : " + m.getCriticalIncidents());
        System.out.println("SLA At Risk (within 30m)   : " + m.getSlaAtRisk());
        System.out.println("SLA Breached                : " + m.getSlaBreached());
        System.out.println("--------------------------------------------------------");
        System.out.println("Total Engineers             : " + m.getTotalEngineers());
        System.out.println("Available Engineers         : " + m.getAvailableEngineers());
        System.out.println("========================================================");
    }

    private void viewEngineerPerformance() throws Exception {
        System.out.println("\n================================ ENGINEER WORKLOAD & PERFORMANCE ================================");
        List<EngineerWorkloadDTO> list = managerService.getEngineerPerformance();
        System.out.printf("%-10s %-20s %-20s %-12s %-12s %-12s\n", "EMP CODE", "NAME", "SPECIALIZATION", "ACTIVE TKT", "CRITICAL TKT", "STATUS");
        System.out.println("------------------------------------------------------------------------------------------------");
        for (EngineerWorkloadDTO dto : list) {
            System.out.printf("%-10s %-20s %-20s %-12d %-12d %-12s\n",
                    dto.getEmployeeCode(), dto.getEngineerName(), dto.getSpecialization(),
                    dto.getActiveTicketCount(), dto.getCriticalTicketCount(), dto.getAvailability());
        }
    }

    private void generateSLAReport(Scanner scanner) throws Exception {
        System.out.print("\nSelect Format (CONSOLE / TXT / CSV): ");
        String fmt = scanner.nextLine().trim().toUpperCase();
        String report = reportService.generateSlaComplianceReport(fmt);
        System.out.println("\n" + report);
    }

    private void generateIncidentReport(Scanner scanner) throws Exception {
        System.out.print("\nSelect Format (CONSOLE / TXT / CSV): ");
        String fmt = scanner.nextLine().trim().toUpperCase();
        String report = reportService.generateIncidentAnalysisReport(fmt);
        System.out.println("\n" + report);
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

    private void manageEscalations(String managerName, Scanner scanner) throws Exception {
        System.out.println("\n========== ESCALATED TICKETS QUEUE ==========");
        List<TroubleTicket> escalations = managerService.getEscalationQueue();
        if (escalations.isEmpty()) {
            System.out.println("No tickets currently escalated.");
            return;
        }
        for (TroubleTicket t : escalations) {
            System.out.printf("ID: %d | Ticket #: %s | Category: %s | Priority: %s\n",
                    t.getTicketId(), t.getTicketNumber(), t.getCategory(), t.getPriority());
        }

        System.out.print("\nEnter Ticket Number or ID to assign directly: ");
        int tktId = resolveTicketId(scanner.nextLine().trim());
        System.out.print("Enter Engineer ID to assign: ");
        int engId = Integer.parseInt(scanner.nextLine().trim());

        ticketService.assignEngineerManual(tktId, engId, managerName);
        System.out.println("✓ Escalated ticket reassigned successfully.");
    }
}
