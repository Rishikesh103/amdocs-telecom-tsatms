package com.amdocs.telecom.service.impl;

import com.amdocs.telecom.service.ManagerService;
import com.amdocs.telecom.dao.*;
import com.amdocs.telecom.dao.impl.*;
import com.amdocs.telecom.model.*;
import com.amdocs.telecom.dto.DashboardMetricsDTO;
import com.amdocs.telecom.dto.EngineerWorkloadDTO;
import com.amdocs.telecom.exception.DAOException;
import com.amdocs.telecom.util.DateUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
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
        
        // Case Study Section 9 Requirement: Critical tickets processed before lower-priority using PriorityQueue
        PriorityQueue<TroubleTicket> queue = new PriorityQueue<>(Comparator.comparing(TroubleTicket::getPriority, (p1, p2) -> {
            return Integer.compare(getPriorityRank(p2), getPriorityRank(p1));
        }));

        for (TroubleTicket t : all) {
            if (t.getStatus() == TicketStatus.ESCALATED) {
                queue.offer(t);
            }
        }

        List<TroubleTicket> prioritizedList = new ArrayList<>();
        while (!queue.isEmpty()) {
            prioritizedList.add(queue.poll());
        }
        return prioritizedList;
    }

    private int getPriorityRank(Priority p) {
        if (p == null) return 0;
        switch (p) {
            case CRITICAL: return 4;
            case HIGH: return 3;
            case MEDIUM: return 2;
            case LOW: return 1;
            default: return 0;
        }
    }
}
