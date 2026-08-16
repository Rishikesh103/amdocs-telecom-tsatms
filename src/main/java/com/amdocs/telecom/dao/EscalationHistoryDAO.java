package com.amdocs.telecom.dao;

import com.amdocs.telecom.model.EscalationHistory;
import com.amdocs.telecom.exception.DAOException;
import java.util.List;

public interface EscalationHistoryDAO extends BaseDAO<EscalationHistory, Integer> {
    List<EscalationHistory> findByTicketId(Integer ticketId) throws DAOException;
}
