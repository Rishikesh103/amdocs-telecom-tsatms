package com.amdocs.telecom.dao;

import com.amdocs.telecom.model.Feedback;
import com.amdocs.telecom.exception.DAOException;
import java.util.List;

public interface FeedbackDAO extends BaseDAO<Feedback, Integer> {
    List<Feedback> findByCustomerId(Integer customerId) throws DAOException;
    Feedback findByTicketId(Integer ticketId) throws DAOException;
}
