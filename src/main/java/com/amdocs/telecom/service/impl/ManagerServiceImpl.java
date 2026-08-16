package com.amdocs.telecom.service.impl;

import com.amdocs.telecom.service.ManagerService;
import com.amdocs.telecom.dao.*;
import com.amdocs.telecom.dao.impl.*;
import com.amdocs.telecom.model.*;
import com.amdocs.telecom.dto.DashboardMetricsDTO;
import com.amdocs.telecom.dto.EngineerWorkloadDTO;
import com.amdocs.telecom.exception.DAOException;
import com.amdocs.telecom.util.DateUtil;

import java.util.List;
import java.util.stream.Collectors;

public class ManagerServiceImpl implements ManagerService {

    private final TroubleTicketDAO ticketDAO;
    private final NetworkEngineerDAO engineerDAO;

    public ManagerServiceImpl() {
        this.ticketDAO = new TroubleTicketDAOImpl();
        this.engineerDAO = new NetworkEngineerDAOImpl();
    }

    @Override
    public DashboardMetricsDTO getDashboardMetrics() throws DAOException {
        List<TroubleTicket> openTickets = ticketDAO.findAllOpen();
        List<NetworkEngineer> engineers = engineerDAO.findAll();

        DashboardMetricsDTO metrics = new DashboardMetricsDTO();
        metrics.setTotalOpenTickets(openTickets.size());

        // Java 8 Stream API counting
        long criticalCount = openTickets.stream()
                .filter(t -> t.getPriority() == Priority.CRITICAL)
                .count();
        metrics.setCriticalIncidents((int) criticalCount);

        long breachedCount = openTickets.stream()
                .filter(t -> t.getSlaDeadline() != null && DateUtil.isDeadlineExceeded(t.getSlaDeadline()))
                .count();
        metrics.setSlaBreached((int) breachedCount);

        long atRiskCount = openTickets.stream()
                .filter(t -> t.getSlaDeadline() != null && DateUtil.isAtRisk(t.getSlaDeadline()))
                .count();
        metrics.setSlaAtRisk((int) atRiskCount);

        metrics.setTotalEngineers(engineers.size());
        long availCount = engineers.stream()
                .filter(e -> e.getAvailability() == AvailabilityStatus.AVAILABLE)
                .count();
        metrics.setAvailableEngineers((int) availCount);

        return metrics;
    }

    @Override
    public List<EngineerWorkloadDTO> getEngineerPerformance() throws DAOException {
        List<NetworkEngineer> engineers = engineerDAO.findAll();
        List<TroubleTicket> openTickets = ticketDAO.findAllOpen();

        return engineers.stream().map(e -> {
            EngineerWorkloadDTO dto = new EngineerWorkloadDTO();
            dto.setEngineerId(e.getEngineerId());
            dto.setEmployeeCode(e.getEmployeeCode());
            dto.setEngineerName(e.getEngineerName());
            dto.setSpecialization(e.getSpecialization());
            dto.setRegion(e.getRegion());
            dto.setAvailability(e.getAvailability());

            long activeCount = openTickets.stream()
                    .filter(t -> t.getAssignedEngineerId() != null && t.getAssignedEngineerId().equals(e.getEngineerId()))
                    .count();
            dto.setActiveTicketCount((int) activeCount);

            long criticalCount = openTickets.stream()
                    .filter(t -> t.getAssignedEngineerId() != null && t.getAssignedEngineerId().equals(e.getEngineerId()) && t.getPriority() == Priority.CRITICAL)
                    .count();
            dto.setCriticalTicketCount((int) criticalCount);

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<TroubleTicket> getEscalationQueue() throws DAOException {
        List<TroubleTicket> all = ticketDAO.findAll();
        return all.stream()
                .filter(t -> t.getStatus() == TicketStatus.ESCALATED)
                .collect(Collectors.toList());
    }
}
