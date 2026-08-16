package com.amdocs.telecom.dao;

import com.amdocs.telecom.model.TicketStatusHistory;
import com.amdocs.telecom.exception.DAOException;
import java.util.List;

public interface TicketStatusHistoryDAO extends BaseDAO<TicketStatusHistory, Integer> {
    List<TicketStatusHistory> findByTicketId(Integer ticketId) throws DAOException;
}
