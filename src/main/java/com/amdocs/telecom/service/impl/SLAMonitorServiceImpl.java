package com.amdocs.telecom.service.impl;

import com.amdocs.telecom.service.SLAMonitorService;
import com.amdocs.telecom.dao.*;
import com.amdocs.telecom.dao.impl.*;
import com.amdocs.telecom.model.*;
import com.amdocs.telecom.exception.DAOException;
import com.amdocs.telecom.util.DateUtil;

import java.util.List;

public class SLAMonitorServiceImpl implements SLAMonitorService {

    private final TroubleTicketDAO ticketDAO;
    private final NotificationDAO notificationDAO;

    public SLAMonitorServiceImpl() {
        this.ticketDAO = new TroubleTicketDAOImpl();
        this.notificationDAO = new NotificationDAOImpl();
    }

    @Override
    public int checkAndProcessSLAs() throws DAOException {
        List<TroubleTicket> openTickets = ticketDAO.findAllOpen();
        int alertedCount = 0;

        for (TroubleTicket t : openTickets) {
            if (t.getSlaDeadline() == null) continue;

            if (DateUtil.isDeadlineExceeded(t.getSlaDeadline())) {
                // SLA Breached
                Notification notif = new Notification();
                notif.setRecipientId("SERVICE_DESK");
                notif.setTicketId(t.getTicketId());
                notif.setMessage("SLA BREACH ALERT: Ticket " + t.getTicketNumber() + " has breached SLA deadline!");
                notif.setNotificationType(NotificationType.SLA_BREACH);
                notificationDAO.create(notif);
                alertedCount++;
            } else if (DateUtil.isAtRisk(t.getSlaDeadline())) {
                // SLA At Risk
                Notification notif = new Notification();
                notif.setRecipientId("SERVICE_DESK");
                notif.setTicketId(t.getTicketId());
                notif.setMessage("SLA WARNING: Ticket " + t.getTicketNumber() + " is at risk of breaching SLA within 30 minutes.");
                notif.setNotificationType(NotificationType.SLA_WARNING);
                notificationDAO.create(notif);
                alertedCount++;
            }
        }

        return alertedCount;
    }
}
