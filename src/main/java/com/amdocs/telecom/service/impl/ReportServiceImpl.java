package com.amdocs.telecom.service.impl;

import com.amdocs.telecom.dao.NetworkEngineerDAO;
import com.amdocs.telecom.dao.TroubleTicketDAO;
import com.amdocs.telecom.dao.impl.NetworkEngineerDAOImpl;
import com.amdocs.telecom.dao.impl.TroubleTicketDAOImpl;
import com.amdocs.telecom.exception.DAOException;
import com.amdocs.telecom.model.NetworkEngineer;
import com.amdocs.telecom.model.Priority;
import com.amdocs.telecom.model.TicketStatus;
import com.amdocs.telecom.model.TroubleTicket;
import com.amdocs.telecom.service.ReportService;
import com.amdocs.telecom.util.ConsoleUI;
import com.amdocs.telecom.util.DateUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportServiceImpl implements ReportService {

    private final TroubleTicketDAO ticketDAO;
    private final NetworkEngineerDAO engineerDAO;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ReportServiceImpl() {
        this.ticketDAO = new TroubleTicketDAOImpl();
        this.engineerDAO = new NetworkEngineerDAOImpl();
    }

    private void ensureReportsDirectory() {
        File dir = new File("reports");
        if (!dir.exists() && !dir.mkdirs()) {
            ConsoleUI.printError("Could not create reports directory. File export may fail.");
        }
    }

    @Override
    public String generateSlaComplianceReport(String format) throws DAOException {
        List<TroubleTicket> tickets = ticketDAO.findAll();
        boolean isCsv = "CSV".equalsIgnoreCase(format);
        StringBuilder sb = new StringBuilder();

        if (isCsv) {
            sb.append("TICKET_NUMBER,PRIORITY,STATUS,DEADLINE,SLA_STATUS\n");
            for (TroubleTicket t : tickets) {
                String slaStatusStr = calculateSlaStatus(t);
                String deadlineStr = t.getSlaDeadline() != null ? t.getSlaDeadline().format(DATE_FMT) : "N/A";
                sb.append(String.format("%s,%s,%s,%s,%s\n",
                        t.getTicketNumber(), t.getPriority(), t.getStatus(), deadlineStr, slaStatusStr));
            }
            exportReportToFile("sla_compliance_report", "csv", sb.toString());
            return sb.toString();
        } else {
            sb.append("=================================================================================\n");
            sb.append("                          SLA COMPLIANCE REPORT                                  \n");
            sb.append("=================================================================================\n");
            sb.append(String.format("%-15s %-12s %-18s %-18s %-15s\n", "TICKET #", "PRIORITY", "STATUS", "DEADLINE", "SLA STATUS"));
            sb.append("---------------------------------------------------------------------------------\n");

            for (TroubleTicket t : tickets) {
                String slaStatusStr = calculateSlaStatus(t);
                String deadlineStr = t.getSlaDeadline() != null ? t.getSlaDeadline().format(DATE_FMT) : "N/A";
                sb.append(String.format("%-15s %-12s %-18s %-18s %-15s\n",
                        t.getTicketNumber(), t.getPriority(), t.getStatus(), deadlineStr, slaStatusStr));
            }

            if ("TXT".equalsIgnoreCase(format)) {
                exportReportToFile("sla_compliance_report", "txt", sb.toString());
            }
            return sb.toString();
        }
    }

    @Override
    public String generateEngineerPerformanceReport(String format) throws DAOException {
        List<NetworkEngineer> engineers = engineerDAO.findAll();
        List<TroubleTicket> tickets = ticketDAO.findAll();
        boolean isCsv = "CSV".equalsIgnoreCase(format);
        StringBuilder sb = new StringBuilder();

        if (isCsv) {
            sb.append("EMPLOYEE_CODE,ENGINEER_NAME,SPECIALIZATION,ACTIVE_TICKETS,RESOLVED_TICKETS\n");
            for (NetworkEngineer engineer : engineers) {
                long active = tickets.stream()
                        .filter(t -> t.getAssignedEngineerId() != null && t.getAssignedEngineerId().equals(engineer.getEngineerId()) &&
                                t.getStatus() != TicketStatus.RESOLVED && t.getStatus() != TicketStatus.CLOSED)
                        .count();

                long resolved = tickets.stream()
                        .filter(t -> t.getAssignedEngineerId() != null && t.getAssignedEngineerId().equals(engineer.getEngineerId()) &&
                                (t.getStatus() == TicketStatus.RESOLVED || t.getStatus() == TicketStatus.CLOSED))
                        .count();

                sb.append(String.format("%s,\"%s\",%s,%d,%d\n",
                        engineer.getEmployeeCode(), engineer.getFullName(), engineer.getSpecialization(), active, resolved));
            }
            exportReportToFile("engineer_performance_report", "csv", sb.toString());
            return sb.toString();
        } else {
            sb.append("=================================================================================\n");
            sb.append("                       ENGINEER PERFORMANCE REPORT                               \n");
            sb.append("=================================================================================\n");
            sb.append(String.format("%-10s %-20s %-20s %-12s %-12s\n", "EMP CODE", "NAME", "SPECIALIZATION", "ACTIVE TKT", "RESOLVED TKT"));
            sb.append("---------------------------------------------------------------------------------\n");

            for (NetworkEngineer engineer : engineers) {
                long active = tickets.stream()
                        .filter(t -> t.getAssignedEngineerId() != null && t.getAssignedEngineerId().equals(engineer.getEngineerId()) &&
                                t.getStatus() != TicketStatus.RESOLVED && t.getStatus() != TicketStatus.CLOSED)
                        .count();

                long resolved = tickets.stream()
                        .filter(t -> t.getAssignedEngineerId() != null && t.getAssignedEngineerId().equals(engineer.getEngineerId()) &&
                                (t.getStatus() == TicketStatus.RESOLVED || t.getStatus() == TicketStatus.CLOSED))
                        .count();

                sb.append(String.format("%-10s %-20s %-20s %-12d %-12d\n",
                        engineer.getEmployeeCode(), engineer.getFullName(), engineer.getSpecialization(), active, resolved));
            }

            if ("TXT".equalsIgnoreCase(format)) {
                exportReportToFile("engineer_performance_report", "txt", sb.toString());
            }
            return sb.toString();
        }
    }

    @Override
    public String generateIncidentAnalysisReport(String format) throws DAOException {
        List<TroubleTicket> tickets = ticketDAO.findAll();
        boolean isCsv = "CSV".equalsIgnoreCase(format);
        StringBuilder sb = new StringBuilder();

        Map<String, List<TroubleTicket>> categoryMap = tickets.stream()
                .collect(Collectors.groupingBy(TroubleTicket::getCategory));

        if (isCsv) {
            sb.append("CATEGORY,TOTAL_INCIDENTS,CRITICAL_INCIDENTS,RESOLVED_INCIDENTS\n");
            categoryMap.forEach((category, list) -> {
                long total = list.size();
                long critical = list.stream().filter(t -> t.getPriority() == Priority.CRITICAL).count();
                long resolved = list.stream().filter(t -> t.getStatus() == TicketStatus.RESOLVED || t.getStatus() == TicketStatus.CLOSED).count();
                sb.append(String.format("%s,%d,%d,%d\n", category, total, critical, resolved));
            });
            exportReportToFile("incident_analysis_report", "csv", sb.toString());
            return sb.toString();
        } else {
            sb.append("=================================================================================\n");
            sb.append("                        INCIDENT ANALYSIS REPORT                                 \n");
            sb.append("=================================================================================\n");
            sb.append(String.format("%-25s %-12s %-15s %-15s\n", "CATEGORY", "TOTAL", "CRITICAL", "RESOLVED"));
            sb.append("---------------------------------------------------------------------------------\n");

            categoryMap.forEach((category, list) -> {
                long total = list.size();
                long critical = list.stream().filter(t -> t.getPriority() == Priority.CRITICAL).count();
                long resolved = list.stream().filter(t -> t.getStatus() == TicketStatus.RESOLVED || t.getStatus() == TicketStatus.CLOSED).count();
                sb.append(String.format("%-25s %-12d %-15d %-15d\n", category, total, critical, resolved));
            });

            if ("TXT".equalsIgnoreCase(format)) {
                exportReportToFile("incident_analysis_report", "txt", sb.toString());
            }
            return sb.toString();
        }
    }

    private String calculateSlaStatus(TroubleTicket t) {
        if (t.getStatus() == TicketStatus.RESOLVED || t.getStatus() == TicketStatus.CLOSED) {
            if (t.getResolutionDate() != null && t.getSlaDeadline() != null && t.getResolutionDate().isAfter(t.getSlaDeadline())) {
                return "BREACHED";
            }
            return "COMPLIANT";
        }
        if (t.getSlaDeadline() != null) {
            if (DateUtil.isDeadlineExceeded(t.getSlaDeadline())) {
                return "BREACHED";
            }
            if (DateUtil.isAtRisk(t.getSlaDeadline())) {
                return "AT_RISK";
            }
            return "ON_TRACK";
        }
        return "N/A";
    }

    private void exportReportToFile(String baseName, String format, String content) {
        ensureReportsDirectory();
        String filename = "reports/" + baseName + "_" + System.currentTimeMillis() + "." + format.toLowerCase();
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(content);
            ConsoleUI.printSuccess("Exported file: " + new File(filename).getAbsolutePath());
        } catch (IOException e) {
            ConsoleUI.printError("Failed to write report file: " + e.getMessage());
        }
    }
}
