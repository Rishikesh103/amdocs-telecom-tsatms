package com.amdocs.telecom.dao;

import com.amdocs.telecom.model.TroubleTicket;
import com.amdocs.telecom.exception.DAOException;
import java.util.List;

/**
 * DAO interface for TroubleTicket entity operations.
 */
public interface TroubleTicketDAO extends BaseDAO<TroubleTicket, Integer> {
    
    /**
     * Finds a ticket by ticket number.
     */
    TroubleTicket findByTicketNumber(String ticketNumber) throws DAOException;
    
    /**
     * Finds all tickets for a customer.
     */
    List<TroubleTicket> findByCustomerId(int customerId) throws DAOException;
    
    /**
     * Finds all tickets assigned to an engineer.
     */
    List<TroubleTicket> findByEngineerId(int engineerId) throws DAOException;
    
    /**
     * Finds all tickets by status.
     */
    List<TroubleTicket> findByStatus(String status) throws DAOException;
    
    /**
     * Finds all open tickets.
     */
    List<TroubleTicket> findAllOpen() throws DAOException;
    
    /**
     * Finds all tickets by priority.
     */
    List<TroubleTicket> findByPriority(String priority) throws DAOException;
    
    /**
     * Finds tickets approaching or breached SLA.
     */
    List<TroubleTicket> findSLAAtRisk() throws DAOException;
}
