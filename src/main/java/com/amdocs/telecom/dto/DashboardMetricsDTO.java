package com.amdocs.telecom.dto;

public class DashboardMetricsDTO {
    private int totalOpenTickets;
    private int criticalIncidents;
    private int slaAtRisk;
    private int slaBreached;
    private int totalEngineers;
    private int availableEngineers;

    public DashboardMetricsDTO() {}

    public int getTotalOpenTickets() { return totalOpenTickets; }
    public void setTotalOpenTickets(int totalOpenTickets) { this.totalOpenTickets = totalOpenTickets; }

    public int getCriticalIncidents() { return criticalIncidents; }
    public void setCriticalIncidents(int criticalIncidents) { this.criticalIncidents = criticalIncidents; }

    public int getSlaAtRisk() { return slaAtRisk; }
    public void setSlaAtRisk(int slaAtRisk) { this.slaAtRisk = slaAtRisk; }

    public int getSlaBreached() { return slaBreached; }
    public void setSlaBreached(int slaBreached) { this.slaBreached = slaBreached; }

    public int getTotalEngineers() { return totalEngineers; }
    public void setTotalEngineers(int totalEngineers) { this.totalEngineers = totalEngineers; }

    public int getAvailableEngineers() { return availableEngineers; }
    public void setAvailableEngineers(int availableEngineers) { this.availableEngineers = availableEngineers; }
}
