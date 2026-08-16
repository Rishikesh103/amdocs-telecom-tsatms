package com.amdocs.telecom.controller;

import com.amdocs.telecom.dto.TicketSummaryDTO;
import com.amdocs.telecom.model.*;
import com.amdocs.telecom.service.*;
import com.amdocs.telecom.service.impl.*;
import com.amdocs.telecom.util.ConsoleUI;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Service Desk Operations Console Controller
 * Professional Enterprise Design with Interactive Selection & Explicit SLA Scales
 */
public class ServiceDeskController {

    private final TroubleTicketService ticketService;
    private final EngineerService engineerService;
    private final ReportService reportService;
    private final SLAMonitorService slaMonitorService;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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
                case "0":
                    ConsoleUI.printSuccess("Logged out successfully. Returned to main gateway.");
                    return false;
                default:
                    ConsoleUI.printError("Invalid option. Please select 1-9 (or 0 to Sign Out).");
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

    private TicketSummaryDTO selectOpenTicket(Scanner scanner, String headerTitle) throws Exception {
        List<TicketSummaryDTO> list = ticketService.getAllOpenTickets();
        if (list.isEmpty()) {
            ConsoleUI.printInfo("No open trouble tickets available.");
            return null;
        }

        ConsoleUI.printHeader(headerTitle, "Select a ticket from the active queue or enter ticket number/ID");
        for (int i = 0; i < list.size(); i++) {
            TicketSummaryDTO t = list.get(i);
            String eng = t.getAssignedEngineerName() != null ? t.getAssignedEngineerName() : "Unassigned";
            System.out.printf("  [%d] %-15s | %-16s | %-8s | %-12s | Eng: %s\n",
                    (i + 1),
                    t.getTicketNumber(),
                    t.getCategory(),
                    ConsoleUI.getPriorityBadge(t.getPriority().name()),
                    ConsoleUI.getStatusBadge(t.getStatus().name()),
                    eng);
        }
        System.out.println("  [0] Cancel / Go Back");

        ConsoleUI.printPrompt("Select option (1-" + list.size() + ", Ticket #, or 0 to Cancel)");
        String input = scanner.nextLine().trim();
        if ("0".equals(input) || "back".equalsIgnoreCase(input) || input.isEmpty()) {
            ConsoleUI.printInfo("Action canceled.");
            return null;
        }

        // Numeric choice from listed items
        try {
            int idx = Integer.parseInt(input);
            if (idx >= 1 && idx <= list.size()) {
                return list.get(idx - 1);
            }
        } catch (NumberFormatException ignored) {}

        // Fallback search by ticket number
        for (TicketSummaryDTO dto : list) {
            if (dto.getTicketNumber().equalsIgnoreCase(input)) {
                return dto;
            }
        }

        // Fallback search by ID
        try {
            int id = Integer.parseInt(input);
            for (TicketSummaryDTO dto : list) {
                if (dto.getTicketId() == id) return dto;
            }
        } catch (NumberFormatException ignored) {}

        ConsoleUI.printError("Ticket not found in open queue: " + input);
        return null;
    }

    private void assignEngineer(Scanner scanner) throws Exception {
        TicketSummaryDTO selectedTicket = selectOpenTicket(scanner, "Engineer Assignment");
        if (selectedTicket == null) return;
        int tktId = selectedTicket.getTicketId();
        
        ConsoleUI.printSection("Select Assignment Strategy");
        System.out.println("  [1] Automatic Recommendation (Stream API: Least Workload + Experience)");
        System.out.println("  [2] Manual Selection from Available Fleet");
        System.out.println("  [0] Cancel");
        ConsoleUI.printPrompt("Select mode (1, 2, or 0 to Cancel)");
        String mode = scanner.nextLine().trim();
        if ("0".equals(mode) || "back".equalsIgnoreCase(mode)) {
            ConsoleUI.printInfo("Action canceled.");
            return;
        }

        if ("1".equals(mode)) {
            NetworkEngineer assigned = ticketService.assignEngineerAuto(tktId);
            ConsoleUI.printSuccess("Auto-assignment completed successfully.");
            ConsoleUI.printCard("Assignment Confirmation", Arrays.asList(
                    "Ticket Number   : " + selectedTicket.getTicketNumber(),
                    "Assigned To     : " + assigned.getEngineerName(),
                    "Specialization  : " + assigned.getSpecialization(),
                    "Region          : " + assigned.getRegion(),
                    "Seniority       : " + assigned.getExperienceYears() + " Years",
                    "Active Queue    : " + assigned.getActiveTicketCount() + " Tickets"
            ));
        } else {
            List<NetworkEngineer> engineers = engineerService.getAllEngineers();
            ConsoleUI.printSection("Available Engineer Fleet");
            System.out.printf("  %-4s %-20s %-22s %-12s %-12s\n", "ID", "NAME", "SPECIALIZATION", "ACTIVE TKT", "STATUS");
            ConsoleUI.printDivider();
            for (NetworkEngineer e : engineers) {
                System.out.printf("  %-4d %-20s %-22s %-12d %-12s\n",
                        e.getEngineerId(), e.getEngineerName(), e.getSpecialization(), e.getActiveTicketCount(), e.getAvailability());
            }
            ConsoleUI.printPrompt("Enter Engineer ID (or 0 to Cancel)");
            String rawEng = scanner.nextLine().trim();
            if ("0".equals(rawEng) || "back".equalsIgnoreCase(rawEng)) {
                ConsoleUI.printInfo("Action canceled.");
                return;
            }
            int engId = Integer.parseInt(rawEng);
            ticketService.assignEngineerManual(tktId, engId, "SERVICE_DESK_ADMIN");
            ConsoleUI.printSuccess("Engineer manually assigned to ticket " + selectedTicket.getTicketNumber() + ".");
        }
    }

    private void reassignTicket(Scanner scanner) throws Exception {
        assignEngineer(scanner);
    }

    private void escalateTicket(String adminName, Scanner scanner) throws Exception {
        TicketSummaryDTO selectedTicket = selectOpenTicket(scanner, "Escalate Trouble Ticket");
        if (selectedTicket == null) return;
        int tktId = selectedTicket.getTicketId();
        
        ConsoleUI.printSection("Select Escalation Hierarchy Level");
        System.out.println("  [1] TEAM_LEAD         (Shift Supervisor Intervention)");
        System.out.println("  [2] NETWORK_MANAGER   (Regional Operational Management)");
        System.out.println("  [3] OPERATIONS_MANAGER(Executive Priority Triage)");
        System.out.println("  [0] Cancel");
        ConsoleUI.printPrompt("Select escalation level (1-3 or 0 to Cancel)");
        String lvlChoice = scanner.nextLine().trim();
        if ("0".equals(lvlChoice) || "back".equalsIgnoreCase(lvlChoice)) {
            ConsoleUI.printInfo("Action canceled.");
            return;
        }

        EscalationLevel level = EscalationLevel.TEAM_LEAD;
        if ("2".equals(lvlChoice)) level = EscalationLevel.NETWORK_MANAGER;
        else if ("3".equals(lvlChoice)) level = EscalationLevel.OPERATIONS_MANAGER;

        ConsoleUI.printPrompt("Enter justification / reason for escalation");
        String reason = scanner.nextLine().trim();

        ticketService.escalateTicket(tktId, level, reason, adminName);
        ConsoleUI.printSuccess("Ticket " + selectedTicket.getTicketNumber() + " escalated to " + level + ".");
    }

    private void updatePriority(String adminName, Scanner scanner) throws Exception {
        TicketSummaryDTO selectedTicket = selectOpenTicket(scanner, "Update Ticket Priority & Recalculate SLA");
        if (selectedTicket == null) return;
        int tktId = selectedTicket.getTicketId();
        
        ConsoleUI.printSection("Select New Priority Level (Case Study Section 8 SLA Window)");
        System.out.println("  [1] LOW      - 48 Hours SLA Target (Minor non-critical requests)");
        System.out.println("  [2] MEDIUM   - 12 Hours SLA Target (Standard service issues / call drops)");
        System.out.println("  [3] HIGH     -  4 Hours SLA Target (Major business disruption / slow speeds)");
        System.out.println("  [4] CRITICAL -  2 Hours SLA Target (Complete network outage / emergencies)");
        System.out.println("  [0] Cancel");
        ConsoleUI.printPrompt("Select new priority (1-4 or 0 to Cancel)");
        String pChoice = scanner.nextLine().trim();
        if ("0".equals(pChoice) || "back".equalsIgnoreCase(pChoice)) {
            ConsoleUI.printInfo("Action canceled.");
            return;
        }

        Priority p = Priority.MEDIUM;
        String slaWindow = "12 Hours";
        if ("1".equals(pChoice)) { p = Priority.LOW; slaWindow = "48 Hours"; }
        else if ("3".equals(pChoice)) { p = Priority.HIGH; slaWindow = "4 Hours"; }
        else if ("4".equals(pChoice)) { p = Priority.CRITICAL; slaWindow = "2 Hours"; }

        ticketService.updateTicketPriority(tktId, p, adminName, "Priority modified via Service Desk Console");
        TroubleTicket updated = ticketService.getTicketById(tktId);

        ConsoleUI.printSuccess("Priority updated to " + p + ". SLA target recalculated.");
        ConsoleUI.printCard("Priority & SLA Telemetry", Arrays.asList(
                "Ticket Number : " + updated.getTicketNumber(),
                "New Priority  : " + ConsoleUI.getPriorityBadge(p.name()) + " (" + slaWindow + " Target)",
                "New SLA Limit : " + (updated.getSlaDeadline() != null ? updated.getSlaDeadline().format(DATE_FMT) : "N/A"),
                "SLA Status    : " + ConsoleUI.getSLABadge("ON_TRACK")
        ));
    }

    private void monitorSLA() throws Exception {
        ConsoleUI.printHeader("Real-Time SLA Audit Telemetry", "Comprehensive scan of active tickets vs SLA deadlines");
        List<com.amdocs.telecom.dto.SLAAuditResultDTO> auditList = slaMonitorService.performDetailedAudit();
        if (auditList.isEmpty()) {
            ConsoleUI.printSuccess("No open tickets found. All SLA targets 100% compliant.");
            return;
        }

        System.out.printf("  %-16s %-16s %-10s %-16s %-16s %-12s\n",
                "TICKET NUMBER", "CATEGORY", "PRIORITY", "SLA DEADLINE", "REMAINING TIME", "SLA HEALTH");
        ConsoleUI.printDivider();
        
        int breachedCount = 0;
        int atRiskCount = 0;
        int onTrackCount = 0;

        for (com.amdocs.telecom.dto.SLAAuditResultDTO r : auditList) {
            String timeStr = r.getSlaDeadline() != null ? r.getSlaDeadline().format(DATE_FMT) : "N/A";
            String remTime;
            if (r.getRemainingMinutes() < 0) {
                remTime = ConsoleUI.RED + (r.getRemainingMinutes() * -1) + "m OVERDUE" + ConsoleUI.RESET;
                breachedCount++;
            } else if (r.getRemainingMinutes() <= 30) {
                remTime = ConsoleUI.YELLOW + r.getRemainingMinutes() + "m (Expires Soon)" + ConsoleUI.RESET;
                atRiskCount++;
            } else {
                long hrs = r.getRemainingMinutes() / 60;
                long mins = r.getRemainingMinutes() % 60;
                remTime = (hrs > 0 ? hrs + "h " : "") + mins + "m";
                onTrackCount++;
            }

            System.out.printf("  %-16s %-16s %-10s %-16s %-16s %-12s\n",
                    r.getTicketNumber(),
                    r.getCategory(),
                    ConsoleUI.getPriorityBadge(r.getPriority().name()),
                    timeStr,
                    remTime,
                    ConsoleUI.getSLABadge(r.getSlaHealth()));
        }

        ConsoleUI.printDivider();
        ConsoleUI.printCard("Audit Summary Findings", Arrays.asList(
                "Total Scanned Tickets : " + auditList.size(),
                "Breached Incidents    : " + (breachedCount > 0 ? ConsoleUI.RED + breachedCount + " (CRITICAL ALERTS DISPATCHED)" + ConsoleUI.RESET : "0"),
                "At-Risk Incidents     : " + (atRiskCount > 0 ? ConsoleUI.YELLOW + atRiskCount + " (WARNINGS SENT)" + ConsoleUI.RESET : "0"),
                "Compliant On-Track    : " + ConsoleUI.GREEN + onTrackCount + ConsoleUI.RESET
        ));
    }

    private void closeTicket(String adminName, Scanner scanner) throws Exception {
        TicketSummaryDTO selectedTicket = selectOpenTicket(scanner, "Close Trouble Ticket");
        if (selectedTicket == null) return;
        int tktId = selectedTicket.getTicketId();

        ConsoleUI.printPrompt("Enter closure verification remarks");
        String remarks = scanner.nextLine().trim();
        ticketService.closeTicket(tktId, remarks, adminName);
        ConsoleUI.printSuccess("Ticket " + selectedTicket.getTicketNumber() + " closed successfully.");
    }

    private void generateReports(Scanner scanner) throws Exception {
        ConsoleUI.printHeader("Generate Operational Reports", "Export analytics and compliance data");
        System.out.println("  [1] SLA Compliance Report");
        System.out.println("  [2] Engineer Performance Report");
        System.out.println("  [3] Incident Analysis Report");
        System.out.println("  [0] Cancel and return to menu");
        ConsoleUI.printPrompt("Select report (1-3 or 0 to Cancel)");
        String rChoice = scanner.nextLine().trim();
        if ("0".equals(rChoice) || "back".equalsIgnoreCase(rChoice)) {
            ConsoleUI.printInfo("Action canceled.");
            return;
        }

        ConsoleUI.printSection("Select Output Format");
        System.out.println("  [1] Console Display (View on Screen)");
        System.out.println("  [2] Plain Text File (.txt exported to reports/)");
        System.out.println("  [3] CSV Spreadsheet (.csv exported to reports/)");
        System.out.println("  [0] Cancel");
        ConsoleUI.printPrompt("Select format (1-3 or 0 to Cancel)");
        String rawFmt = scanner.nextLine().trim().toUpperCase();
        if ("0".equals(rawFmt) || "BACK".equalsIgnoreCase(rawFmt)) {
            ConsoleUI.printInfo("Action canceled.");
            return;
        }

        String format = "CONSOLE";
        if ("2".equals(rawFmt) || "TXT".equalsIgnoreCase(rawFmt)) format = "TXT";
        else if ("3".equals(rawFmt) || "CSV".equalsIgnoreCase(rawFmt)) format = "CSV";

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

        ConsoleUI.printSuccess("Report generated successfully (" + format + " mode).");
        System.out.println("\n" + report);
    }
}
