package com.amdocs.telecom.service;

import com.amdocs.telecom.model.*;
import com.amdocs.telecom.dto.TicketSummaryDTO;
import com.amdocs.telecom.exception.BusinessException;
import com.amdocs.telecom.exception.DAOException;

import java.util.List;

public interface TroubleTicketService {
    TroubleTicket createTicket(int customerId, int serviceId, String category, String description, Priority priority, String severity) throws BusinessException, DAOException;
    
    List<TicketSummaryDTO> getAllOpenTickets() throws DAOException;
    List<TroubleTicket> getTicketsByCustomerId(int customerId) throws DAOException;
    List<TroubleTicket> getTicketsByEngineerId(int engineerId) throws DAOException;
    TroubleTicket getTicketByNumber(String ticketNumber) throws DAOException;
    TroubleTicket getTicketById(int ticketId) throws DAOException;
    
    NetworkEngineer assignEngineerAuto(int ticketId) throws BusinessException, DAOException;
    boolean assignEngineerManual(int ticketId, int engineerId, String assignedBy) throws BusinessException, DAOException;
    
    boolean updateTicketStatus(int ticketId, TicketStatus newStatus, String changedBy, String remarks) throws BusinessException, DAOException;
    boolean updateTicketPriority(int ticketId, Priority newPriority, String updatedBy, String remarks) throws BusinessException, DAOException;
    boolean addResolution(int ticketId, String resolutionText, String rootCause, ResolutionCode resolutionCode, int engineerId) throws BusinessException, DAOException;
    boolean escalateTicket(int ticketId, EscalationLevel toLevel, String reason, String escalatedBy) throws BusinessException, DAOException;
    boolean closeTicket(int ticketId, String remarks, String closedBy) throws BusinessException, DAOException;
}
