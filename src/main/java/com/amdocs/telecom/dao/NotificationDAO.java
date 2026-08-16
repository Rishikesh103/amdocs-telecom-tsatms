package com.amdocs.telecom.dao;

import com.amdocs.telecom.model.Notification;
import com.amdocs.telecom.exception.DAOException;
import java.util.List;

public interface NotificationDAO extends BaseDAO<Notification, Integer> {
    List<Notification> findByRecipientId(String recipientId) throws DAOException;
    boolean markAsRead(Integer notificationId) throws DAOException;
}
