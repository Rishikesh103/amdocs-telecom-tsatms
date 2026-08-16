package com.amdocs.telecom.service.impl;

import com.amdocs.telecom.dao.NotificationDAO;
import com.amdocs.telecom.dao.TroubleTicketDAO;
import com.amdocs.telecom.dao.impl.NotificationDAOImpl;
import com.amdocs.telecom.dao.impl.TroubleTicketDAOImpl;
import com.amdocs.telecom.dto.SLAAuditResultDTO;
import com.amdocs.telecom.exception.DAOException;
import com.amdocs.telecom.model.Notification;
import com.amdocs.telecom.model.NotificationType;
import com.amdocs.telecom.model.TroubleTicket;
import com.amdocs.telecom.service.SLAMonitorService;
import com.amdocs.telecom.util.DateUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        List<SLAAuditResultDTO> audit = performDetailedAudit();
        int alerted = 0;
        for (SLAAuditResultDTO r : audit) {
            if ("BREACHED".equals(r.getSlaHealth()) || "AT_RISK".equals(r.getSlaHealth())) {
                alerted++;
            }
        }
        return alerted;
    }

    @Override
    public List<SLAAuditResultDTO> performDetailedAudit() throws DAOException {
        List<TroubleTicket> openTickets = ticketDAO.findAllOpen();
        List<SLAAuditResultDTO> results = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (TroubleTicket t : openTickets) {
            if (t.getSlaDeadline() == null) continue;

            SLAAuditResultDTO dto = new SLAAuditResultDTO();
            dto.setTicketId(t.getTicketId());
            dto.setTicketNumber(t.getTicketNumber());
            dto.setCategory(t.getCategory() != null ? t.getCategory() : "UNKNOWN");
            dto.setPriority(t.getPriority());
            dto.setStatus(t.getStatus());
            dto.setSlaDeadline(t.getSlaDeadline());

            long diffMinutes = Duration.between(now, t.getSlaDeadline()).toMinutes();
            dto.setRemainingMinutes(diffMinutes);

            if (DateUtil.isDeadlineExceeded(t.getSlaDeadline())) {
                dto.setSlaHealth("BREACHED");
                dto.setActionTaken("SLA Breach Alert Dispatched to Manager & Service Desk");

                // Create alert notification
                Notification notif = new Notification();
                notif.setRecipientId("SERVICE_DESK");
                notif.setTicketId(t.getTicketId());
                notif.setMessage("SLA BREACH ALERT: Ticket " + t.getTicketNumber() + " has breached SLA deadline!");
                notif.setNotificationType(NotificationType.SLA_BREACH);
                notificationDAO.create(notif);

            } else if (DateUtil.isAtRisk(t.getSlaDeadline())) {
                dto.setSlaHealth("AT_RISK");
                dto.setActionTaken("Urgent Warning Dispatched (Expires in <30m)");

                // Create warning notification
                Notification notif = new Notification();
                notif.setRecipientId("SERVICE_DESK");
                notif.setTicketId(t.getTicketId());
                notif.setMessage("SLA WARNING: Ticket " + t.getTicketNumber() + " is at risk of breaching SLA within 30 minutes.");
                notif.setNotificationType(NotificationType.SLA_WARNING);
                notificationDAO.create(notif);

            } else {
                dto.setSlaHealth("ON_TRACK");
                dto.setActionTaken("Compliant - Countdown Active");
            }

            results.add(dto);
        }

        return results;
    }
}
