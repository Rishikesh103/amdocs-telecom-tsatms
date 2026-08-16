package com.amdocs.telecom.service.impl;

import com.amdocs.telecom.dao.CustomerDAO;
import com.amdocs.telecom.dao.FeedbackDAO;
import com.amdocs.telecom.dao.NotificationDAO;
import com.amdocs.telecom.dao.TelecomServiceDAO;
import com.amdocs.telecom.dao.UserAccountDAO;
import com.amdocs.telecom.dao.impl.CustomerDAOImpl;
import com.amdocs.telecom.dao.impl.FeedbackDAOImpl;
import com.amdocs.telecom.dao.impl.NotificationDAOImpl;
import com.amdocs.telecom.dao.impl.TelecomServiceDAOImpl;
import com.amdocs.telecom.dao.impl.UserAccountDAOImpl;
import com.amdocs.telecom.exception.BusinessException;
import com.amdocs.telecom.exception.DAOException;
import com.amdocs.telecom.model.Customer;
import com.amdocs.telecom.model.Feedback;
import com.amdocs.telecom.model.Notification;
import com.amdocs.telecom.model.TelecomService;
import com.amdocs.telecom.model.UserAccount;
import com.amdocs.telecom.service.CustomerService;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {

    private final CustomerDAO customerDAO;
    private final TelecomServiceDAO serviceDAO;
    private final NotificationDAO notificationDAO;
    private final FeedbackDAO feedbackDAO;
    private final UserAccountDAO userAccountDAO;

    public CustomerServiceImpl() {
        this.customerDAO = new CustomerDAOImpl();
        this.serviceDAO = new TelecomServiceDAOImpl();
        this.notificationDAO = new NotificationDAOImpl();
        this.feedbackDAO = new FeedbackDAOImpl();
        this.userAccountDAO = new UserAccountDAOImpl();
    }

    @Override
    public Customer getCustomerByUserId(int userId) throws DAOException {
        UserAccount user = userAccountDAO.findById(userId);
        if (user != null && user.getLinkedId() != null) {
            return customerDAO.findById(user.getLinkedId());
        }
        return null;
    }

    @Override
    public Customer getCustomerById(int customerId) throws DAOException {
        return customerDAO.findById(customerId);
    }

    @Override
    public List<TelecomService> getServicesForCustomer(int customerId) throws DAOException {
        return serviceDAO.findByCustomerId(customerId);
    }

    @Override
    public List<Notification> getNotificationsForUser(String recipientId) throws DAOException {
        return notificationDAO.findByRecipientId(recipientId);
    }

    @Override
    public boolean submitFeedback(int ticketId, int customerId, int rating, String comments) throws BusinessException, DAOException {
        if (rating < 1 || rating > 5) {
            throw new BusinessException("Rating must be between 1 and 5.");
        }
        Feedback feedback = new Feedback();
        feedback.setTicketId(ticketId);
        feedback.setCustomerId(customerId);
        feedback.setRating(rating);
        feedback.setComments(comments);
        feedbackDAO.create(feedback);
        return true;
    }
}
