package com.amdocs.telecom.service.impl;

import com.amdocs.telecom.service.EngineerService;
import com.amdocs.telecom.dao.*;
import com.amdocs.telecom.dao.impl.*;
import com.amdocs.telecom.model.*;
import com.amdocs.telecom.exception.DAOException;

import java.util.List;

public class EngineerServiceImpl implements EngineerService {

    private final NetworkEngineerDAO engineerDAO;
    private final TroubleTicketDAO ticketDAO;
    private final UserAccountDAO userAccountDAO;

    public EngineerServiceImpl() {
        this.engineerDAO = new NetworkEngineerDAOImpl();
        this.ticketDAO = new TroubleTicketDAOImpl();
        this.userAccountDAO = new UserAccountDAOImpl();
    }

    @Override
    public NetworkEngineer getEngineerByUserId(int userId) throws DAOException {
        UserAccount user = userAccountDAO.findById(userId);
        if (user != null && user.getLinkedId() != null) {
            return engineerDAO.findById(user.getLinkedId());
        }
        return null;
    }

    @Override
    public NetworkEngineer getEngineerById(int engineerId) throws DAOException {
        return engineerDAO.findById(engineerId);
    }

    @Override
    public List<NetworkEngineer> getAllEngineers() throws DAOException {
        return engineerDAO.findAll();
    }

    @Override
    public List<TroubleTicket> getAssignedTickets(int engineerId) throws DAOException {
        return ticketDAO.findByEngineerId(engineerId);
    }

    @Override
    public boolean updateAvailability(int engineerId, AvailabilityStatus status) throws DAOException {
        NetworkEngineer engineer = engineerDAO.findById(engineerId);
        if (engineer != null) {
            engineer.setAvailability(status);
            return engineerDAO.update(engineer);
        }
        return false;
    }
}
