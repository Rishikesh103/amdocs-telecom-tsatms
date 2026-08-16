package com.amdocs.telecom.scheduler;

import com.amdocs.telecom.model.Notification;
import com.amdocs.telecom.model.NotificationType;
import com.amdocs.telecom.dao.NotificationDAO;
import com.amdocs.telecom.dao.impl.NotificationDAOImpl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationProcessor {

    private final ExecutorService executor;
    private final NotificationDAO notificationDAO;

    public NotificationProcessor() {
        this.executor = Executors.newFixedThreadPool(2, r -> {
            Thread t = new Thread(r, "NotificationProcessorThread");
            t.setDaemon(true);
            return t;
        });
        this.notificationDAO = new NotificationDAOImpl();
    }

    public void sendNotification(String recipientId, Integer ticketId, String message, NotificationType notificationType) {
        executor.submit(() -> {
            try {
                Notification notification = new Notification();
                notification.setRecipientId(recipientId);
                notification.setTicketId(ticketId);
                notification.setMessage(message);
                notification.setNotificationType(notificationType);
                notification.setReadStatus(0);
                notificationDAO.create(notification);
            } catch (Exception e) {
                System.err.println("[NotificationProcessor] Error saving notification: " + e.getMessage());
            }
        });
    }

    public void shutdown() {
        executor.shutdown();
    }
}
