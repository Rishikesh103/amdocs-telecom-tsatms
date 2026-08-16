package com.amdocs.telecom.service;

import com.amdocs.telecom.model.*;
import com.amdocs.telecom.exception.BusinessException;
import com.amdocs.telecom.exception.DAOException;

import java.util.List;

public interface CustomerService {
    Customer getCustomerByUserId(int userId) throws DAOException;
    Customer getCustomerById(int customerId) throws DAOException;
    List<TelecomService> getServicesForCustomer(int customerId) throws DAOException;
    List<Notification> getNotificationsForUser(String recipientId) throws DAOException;
    boolean submitFeedback(int ticketId, int customerId, int rating, String comments) throws BusinessException, DAOException;
}
