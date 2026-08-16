package com.amdocs.telecom.scheduler;

import com.amdocs.telecom.dao.NetworkEventDAO;
import com.amdocs.telecom.dao.impl.NetworkEventDAOImpl;
import com.amdocs.telecom.model.*;
import com.amdocs.telecom.service.TroubleTicketService;
import com.amdocs.telecom.service.impl.TroubleTicketServiceImpl;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class NetworkEventProcessor implements Runnable {

    private final BlockingQueue<NetworkEvent> eventQueue;
    private final NetworkEventDAO eventDAO;
    private final TroubleTicketService ticketService;
    private volatile boolean running = true;
    private Thread workerThread;

    public NetworkEventProcessor() {
        this.eventQueue = new ArrayBlockingQueue<>(100);
        this.eventDAO = new NetworkEventDAOImpl();
        this.ticketService = new TroubleTicketServiceImpl();
    }

    public void start() {
        workerThread = new Thread(this, "NetworkEventProcessorThread");
        workerThread.setDaemon(true);
        workerThread.start();
        System.out.println("✓ Network Event Processor Thread started.");
    }

    public void stop() {
        running = false;
        if (workerThread != null) {
            workerThread.interrupt();
        }
    }

    public void submitEvent(NetworkEvent event) {
        eventQueue.offer(event);
    }

    @Override
    public void run() {
        while (running) {
            try {
                // Poll from queue or query DB for unprocessed network events
                NetworkEvent event = eventQueue.poll(2, TimeUnit.SECONDS);
                if (event == null) {
                    List<NetworkEvent> dbEvents = eventDAO.findUnprocessedEvents();
                    for (NetworkEvent e : dbEvents) {
                        processSingleEvent(e);
                    }
                } else {
                    processSingleEvent(event);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                // Keep background thread alive
            }
        }
    }

    private void processSingleEvent(NetworkEvent event) {
        try {
            if (event.getSeverity() == Severity.CRITICAL && event.getTicketCreatedId() == null) {
                // Automatically raise trouble ticket for critical outage
                TroubleTicket created = ticketService.createTicket(
                        1, // Default system customer
                        1, // Default primary link service
                        "NETWORK_OUTAGE",
                        "Auto-generated ticket for network event on node " + event.getNetworkNode() + " (" + event.getEventType() + ")",
                        Priority.CRITICAL,
                        Severity.CRITICAL.name()
                );
                eventDAO.markAsProcessed(event.getEventId(), created.getTicketId());
                System.out.println("\n[NetworkEventProcessor] Auto-generated CRITICAL Ticket " + created.getTicketNumber() + " for node " + event.getNetworkNode());
            } else {
                eventDAO.markAsProcessed(event.getEventId(), null);
            }
        } catch (Exception e) {
            System.err.println("[NetworkEventProcessor] Error processing event: " + e.getMessage());
        }
    }
}
