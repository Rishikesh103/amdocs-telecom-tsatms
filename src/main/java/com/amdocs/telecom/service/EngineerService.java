package com.amdocs.telecom.service;

import com.amdocs.telecom.exception.DAOException;
import com.amdocs.telecom.model.AvailabilityStatus;
import com.amdocs.telecom.model.NetworkEngineer;
import com.amdocs.telecom.model.TroubleTicket;

import java.util.List;

public interface EngineerService {
    NetworkEngineer getEngineerByUserId(int userId) throws DAOException;
    NetworkEngineer getEngineerById(int engineerId) throws DAOException;
    List<NetworkEngineer> getAllEngineers() throws DAOException;
    List<TroubleTicket> getAssignedTickets(int engineerId) throws DAOException;
    boolean updateAvailability(int engineerId, AvailabilityStatus status) throws DAOException;
}
