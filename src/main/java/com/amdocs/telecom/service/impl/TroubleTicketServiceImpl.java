package com.amdocs.telecom.service.impl;

import com.amdocs.telecom.service.TroubleTicketService;
import com.amdocs.telecom.dao.*;
import com.amdocs.telecom.dao.impl.*;
import com.amdocs.telecom.model.*;
import com.amdocs.telecom.dto.TicketSummaryDTO;
import com.amdocs.telecom.exception.BusinessException;
import com.amdocs.telecom.exception.DAOException;
import com.amdocs.telecom.util.DateUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class TroubleTicketServiceImpl implements TroubleTicketService {

    private final TroubleTicketDAO ticketDAO;
    private final CustomerDAO customerDAO;
    private final TelecomServiceDAO serviceDAO;
    private final NetworkEngineerDAO engineerDAO;
    private final SLAConfigurationDAO slaDAO;
    private final TicketStatusHistoryDAO historyDAO;
    private final EscalationHistoryDAO escalationDAO;
    private final NotificationDAO notificationDAO;
    private final AuditLogDAO auditDAO;

    public TroubleTicketServiceImpl() {
        this.ticketDAO = new TroubleTicketDAOImpl();
        this.customerDAO = new CustomerDAOImpl();
        this.serviceDAO = new TelecomServiceDAOImpl();
        this.engineerDAO = new NetworkEngineerDAOImpl();
        this.slaDAO = new SLAConfigurationDAOImpl();
        this.historyDAO = new TicketStatusHistoryDAOImpl();
        this.escalationDAO = new EscalationHistoryDAOImpl();
        this.notificationDAO = new NotificationDAOImpl();
        this.auditDAO = new AuditLogDAOImpl();
    }

    @Override
    public TroubleTicket createTicket(int customerId, int serviceId, String category, String description, Priority priority, String severity)
            throws BusinessException, DAOException {
        
        Customer customer = customerDAO.findById(customerId);
        if (customer == null) {
            throw new BusinessException("Customer not found with ID: " + customerId);
        }
        
        TelecomService service = serviceDAO.findById(serviceId);
        if (service == null) {
            throw new BusinessException("Telecom service not found with ID: " + serviceId);
        }

        TroubleTicket ticket = new TroubleTicket();
        String ticketNum = "TKT" + (100000 + new Random().nextInt(900000));
        ticket.setTicketNumber(ticketNum);
        ticket.setCustomerId(customerId);
        ticket.setServiceId(serviceId);
        ticket.setCategory(category);
        ticket.setDescription(description);
        ticket.setPriority(priority);
        ticket.setSeverity(severity);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreatedDate(LocalDateTime.now());
        ticket.setUpdatedDate(LocalDateTime.now());

        // Calculate SLA deadline from sla_configuration
        SLAConfiguration slaConfig = slaDAO.findByPriority(priority);
        if (slaConfig != null) {
            LocalDateTime deadline = DateUtil.addHours(LocalDateTime.now(), slaConfig.getResolutionSlaHours());
            ticket.setSlaDeadline(deadline);
        } else {
            ticket.setSlaDeadline(DateUtil.addHours(LocalDateTime.now(), 24));
        }

        Integer generatedId = ticketDAO.create(ticket);
        ticket.setTicketId(generatedId);

        // Record history
        TicketStatusHistory history = new TicketStatusHistory();
        history.setTicketId(ticket.getTicketId());
        history.setOldStatus("NONE");
        history.setNewStatus(TicketStatus.OPEN.name());
        history.setChangedBy("CUSTOMER_APP");
        history.setRemarks("Trouble ticket created");
        historyDAO.create(history);

        // Notify Admin & Customer
        Notification notif = new Notification();
        notif.setRecipientId("SERVICE_DESK");
        notif.setTicketId(ticket.getTicketId());
        notif.setMessage("New trouble ticket created: " + ticket.getTicketNumber() + " Priority: " + priority);
        notif.setNotificationType(NotificationType.TICKET_CREATION);
        notificationDAO.create(notif);

        return ticket;
    }

    @Override
    public List<TicketSummaryDTO> getAllOpenTickets() throws DAOException {
        List<TroubleTicket> tickets = ticketDAO.findAllOpen();
        List<TicketSummaryDTO> summaries = new ArrayList<>();

        for (TroubleTicket t : tickets) {
            TicketSummaryDTO dto = new TicketSummaryDTO();
            dto.setTicketId(t.getTicketId());
            dto.setTicketNumber(t.getTicketNumber());
            dto.setCategory(t.getCategory());
            dto.setPriority(t.getPriority());
            dto.setStatus(t.getStatus());
            dto.setCreatedDate(t.getCreatedDate());
            dto.setSlaDeadline(t.getSlaDeadline());

            // Check SLA status
            if (t.getSlaDeadline() != null) {
                if (DateUtil.isDeadlineExceeded(t.getSlaDeadline())) {
                    dto.setSlaStatus(SLAStatus.BREACHED);
                } else if (DateUtil.isAtRisk(t.getSlaDeadline())) {
                    dto.setSlaStatus(SLAStatus.AT_RISK);
                } else {
                    dto.setSlaStatus(SLAStatus.WITHIN_SLA);
                }
            } else {
                dto.setSlaStatus(SLAStatus.WITHIN_SLA);
            }

            Customer c = customerDAO.findById(t.getCustomerId());
            if (c != null) dto.setCustomerName(c.getCustomerName());

            TelecomService s = serviceDAO.findById(t.getServiceId());
            if (s != null) dto.setServiceName(s.getServiceName());

            if (t.getAssignedEngineerId() != null) {
                NetworkEngineer e = engineerDAO.findById(t.getAssignedEngineerId());
                if (e != null) dto.setAssignedEngineerName(e.getEngineerName());
            } else {
                dto.setAssignedEngineerName("Unassigned");
            }

            summaries.add(dto);
        }

        return summaries;
    }

    @Override
    public List<TroubleTicket> getTicketsByCustomerId(int customerId) throws DAOException {
        return ticketDAO.findByCustomerId(customerId);
    }

    @Override
    public List<TroubleTicket> getTicketsByEngineerId(int engineerId) throws DAOException {
        return ticketDAO.findByEngineerId(engineerId);
    }

    @Override
    public TroubleTicket getTicketByNumber(String ticketNumber) throws DAOException {
        return ticketDAO.findByTicketNumber(ticketNumber);
    }

    @Override
    public TroubleTicket getTicketById(int ticketId) throws DAOException {
        return ticketDAO.findById(ticketId);
    }

    @Override
    public NetworkEngineer assignEngineerAuto(int ticketId) throws BusinessException, DAOException {
        TroubleTicket ticket = getTicketById(ticketId);
        if (ticket == null) {
            throw new BusinessException("Ticket not found: " + ticketId);
        }

        List<NetworkEngineer> availableEngineers = engineerDAO.findAllAvailable();
        if (availableEngineers == null || availableEngineers.isEmpty()) {
            throw new BusinessException("No available engineers at this time.");
        }

        // Java 8 Stream API + Lambda: filter & sort engineers by workload and experience
        Optional<NetworkEngineer> bestEngineer = availableEngineers.stream()
                .filter(eng -> eng.getAvailability() == AvailabilityStatus.AVAILABLE)
                .min(Comparator.comparingInt(NetworkEngineer::getActiveTicketCount)
                        .thenComparing(Comparator.comparingInt(NetworkEngineer::getExperienceYears).reversed()));

        if (!bestEngineer.isPresent()) {
            throw new BusinessException("No matching available engineer found.");
        }

        NetworkEngineer selected = bestEngineer.get();

        // Assign to engineer
        ticket.setAssignedEngineerId(selected.getEngineerId());
        ticket.setStatus(TicketStatus.ASSIGNED);
        ticketDAO.update(ticket);

        // Update engineer active ticket count
        selected.setActiveTicketCount(selected.getActiveTicketCount() + 1);
        engineerDAO.update(selected);

        // Record History
        TicketStatusHistory history = new TicketStatusHistory();
        history.setTicketId(ticketId);
        history.setOldStatus(TicketStatus.OPEN.name());
        history.setNewStatus(TicketStatus.ASSIGNED.name());
        history.setChangedBy("AUTO_ASSIGNMENT_ENGINE");
        history.setRemarks("Assigned to engineer: " + selected.getEngineerName());
        historyDAO.create(history);

        // Notification to engineer
        Notification notif = new Notification();
        notif.setRecipientId(selected.getEmployeeCode());
        notif.setTicketId(ticketId);
        notif.setMessage("Ticket " + ticket.getTicketNumber() + " assigned to you.");
        notif.setNotificationType(NotificationType.ENGINEER_ASSIGNMENT);
        notificationDAO.create(notif);

        return selected;
    }

    @Override
    public boolean assignEngineerManual(int ticketId, int engineerId, String assignedBy) throws BusinessException, DAOException {
        TroubleTicket ticket = getTicketById(ticketId);
        if (ticket == null) throw new BusinessException("Ticket not found: " + ticketId);

        NetworkEngineer engineer = engineerDAO.findById(engineerId);
        if (engineer == null) throw new BusinessException("Engineer not found with ID: " + engineerId);

        String oldStatus = ticket.getStatus().name();

        ticket.setAssignedEngineerId(engineerId);
        ticket.setStatus(TicketStatus.ASSIGNED);
        ticketDAO.update(ticket);

        engineer.setActiveTicketCount(engineer.getActiveTicketCount() + 1);
        engineerDAO.update(engineer);

        TicketStatusHistory history = new TicketStatusHistory();
        history.setTicketId(ticketId);
        history.setOldStatus(oldStatus);
        history.setNewStatus(TicketStatus.ASSIGNED.name());
        history.setChangedBy(assignedBy);
        history.setRemarks("Manually assigned to: " + engineer.getEngineerName());
        historyDAO.create(history);

        return true;
    }

    @Override
    public boolean updateTicketStatus(int ticketId, TicketStatus newStatus, String changedBy, String remarks)
            throws BusinessException, DAOException {
        TroubleTicket ticket = getTicketById(ticketId);
        if (ticket == null) throw new BusinessException("Ticket not found: " + ticketId);

        String oldStatus = ticket.getStatus().name();
        ticket.setStatus(newStatus);
        ticketDAO.update(ticket);

        TicketStatusHistory history = new TicketStatusHistory();
        history.setTicketId(ticketId);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus.name());
        history.setChangedBy(changedBy);
        history.setRemarks(remarks);
        historyDAO.create(history);

        return true;
    }

    @Override
    public boolean updateTicketPriority(int ticketId, Priority newPriority, String updatedBy, String remarks)
            throws BusinessException, DAOException {
        TroubleTicket ticket = getTicketById(ticketId);
        if (ticket == null) throw new BusinessException("Ticket not found: " + ticketId);

        ticket.setPriority(newPriority);
        SLAConfiguration sla = slaDAO.findByPriority(newPriority);
        if (sla != null) {
            ticket.setSlaDeadline(DateUtil.addHours(LocalDateTime.now(), sla.getResolutionSlaHours()));
        }
        ticketDAO.update(ticket);

        TicketStatusHistory history = new TicketStatusHistory();
        history.setTicketId(ticketId);
        history.setOldStatus(ticket.getStatus().name());
        history.setNewStatus(ticket.getStatus().name());
        history.setChangedBy(updatedBy);
        history.setRemarks("Priority updated to " + newPriority + ". " + remarks);
        historyDAO.create(history);

        return true;
    }

    @Override
    public boolean addResolution(int ticketId, String resolutionText, String rootCause, ResolutionCode resolutionCode, int engineerId)
            throws BusinessException, DAOException {
        TroubleTicket ticket = getTicketById(ticketId);
        if (ticket == null) throw new BusinessException("Ticket not found: " + ticketId);

        String oldStatus = ticket.getStatus().name();
        ticket.setResolutionText(resolutionText);
        ticket.setRootCause(rootCause);
        ticket.setResolutionCode(resolutionCode != null ? resolutionCode.name() : ResolutionCode.HARDWARE_FAILURE.name());
        ticket.setResolutionDate(LocalDateTime.now());
        ticket.setStatus(TicketStatus.RESOLVED);
        ticketDAO.update(ticket);

        // Decrease engineer workload
        NetworkEngineer eng = engineerDAO.findById(engineerId);
        if (eng != null && eng.getActiveTicketCount() > 0) {
            eng.setActiveTicketCount(eng.getActiveTicketCount() - 1);
            engineerDAO.update(eng);
        }

        TicketStatusHistory history = new TicketStatusHistory();
        history.setTicketId(ticketId);
        history.setOldStatus(oldStatus);
        history.setNewStatus(TicketStatus.RESOLVED.name());
        history.setChangedBy("ENGINEER_" + engineerId);
        history.setRemarks("Resolution added: " + resolutionText);
        historyDAO.create(history);

        return true;
    }

    @Override
    public boolean escalateTicket(int ticketId, EscalationLevel toLevel, String reason, String escalatedBy)
            throws BusinessException, DAOException {
        TroubleTicket ticket = getTicketById(ticketId);
        if (ticket == null) throw new BusinessException("Ticket not found: " + ticketId);

        String oldStatus = ticket.getStatus().name();
        ticket.setStatus(TicketStatus.ESCALATED);
        ticketDAO.update(ticket);

        EscalationHistory escalation = new EscalationHistory();
        escalation.setTicketId(ticketId);
        escalation.setFromLevel(EscalationLevel.ENGINEER.name());
        escalation.setToLevel(toLevel.name());
        escalation.setReason(reason);
        escalation.setEscalatedBy(escalatedBy);
        escalationDAO.create(escalation);

        TicketStatusHistory history = new TicketStatusHistory();
        history.setTicketId(ticketId);
        history.setOldStatus(oldStatus);
        history.setNewStatus(TicketStatus.ESCALATED.name());
        history.setChangedBy(escalatedBy);
        history.setRemarks("Escalated to " + toLevel + ": " + reason);
        historyDAO.create(history);

        return true;
    }

    @Override
    public boolean closeTicket(int ticketId, String remarks, String closedBy) throws BusinessException, DAOException {
        TroubleTicket ticket = getTicketById(ticketId);
        if (ticket == null) throw new BusinessException("Ticket not found: " + ticketId);

        String oldStatus = ticket.getStatus().name();
        ticket.setStatus(TicketStatus.CLOSED);
        ticketDAO.update(ticket);

        TicketStatusHistory history = new TicketStatusHistory();
        history.setTicketId(ticketId);
        history.setOldStatus(oldStatus);
        history.setNewStatus(TicketStatus.CLOSED.name());
        history.setChangedBy(closedBy);
        history.setRemarks(remarks);
        historyDAO.create(history);

        return true;
    }
}
