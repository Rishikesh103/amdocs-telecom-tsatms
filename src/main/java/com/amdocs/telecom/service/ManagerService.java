package com.amdocs.telecom.service;

import com.amdocs.telecom.dto.DashboardMetricsDTO;
import com.amdocs.telecom.dto.EngineerWorkloadDTO;
import com.amdocs.telecom.model.TroubleTicket;
import com.amdocs.telecom.exception.DAOException;

import java.util.List;

public interface ManagerService {
    DashboardMetricsDTO getDashboardMetrics() throws DAOException;
    List<EngineerWorkloadDTO> getEngineerPerformance() throws DAOException;
    List<TroubleTicket> getEscalationQueue() throws DAOException;
}
