package com.amdocs.telecom.dao;

import com.amdocs.telecom.model.NetworkEvent;
import com.amdocs.telecom.exception.DAOException;
import java.util.List;

public interface NetworkEventDAO extends BaseDAO<NetworkEvent, Integer> {
    List<NetworkEvent> findUnprocessedEvents() throws DAOException;
    boolean markAsProcessed(Integer eventId, Integer ticketId) throws DAOException;
}
