package com.amdocs.telecom.scheduler;

import com.amdocs.telecom.service.SLAMonitorService;
import com.amdocs.telecom.service.impl.SLAMonitorServiceImpl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SLAMonitorScheduler {

    private final ScheduledExecutorService scheduler;
    private final SLAMonitorService slaMonitorService;
    private volatile boolean running = false;

    public SLAMonitorScheduler() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "SLA-Monitor-Thread");
            t.setDaemon(true);
            return t;
        });
        this.slaMonitorService = new SLAMonitorServiceImpl();
    }

    public synchronized void start() {
        if (!running) {
            running = true;
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    slaMonitorService.checkAndProcessSLAs();
                } catch (Exception e) {
                    System.err.println("[SLAMonitorScheduler] Error checking SLAs: " + e.getMessage());
                }
            }, 5, 30, TimeUnit.SECONDS);
            System.out.println("[INFO ] [SLAMonitorScheduler] Started — scanning every 30 seconds.");
        }
    }

    public synchronized void stop() {
        if (running) {
            running = false;
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(3, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
            System.out.println("[INFO ] [SLAMonitorScheduler] Stopped.");
        }
    }
}
