package com.amdocs.telecom.service;

import com.amdocs.telecom.dto.SLAAuditResultDTO;
import com.amdocs.telecom.exception.DAOException;

import java.util.List;

public interface SLAMonitorService {
    int checkAndProcessSLAs() throws DAOException;
    List<SLAAuditResultDTO> performDetailedAudit() throws DAOException;
}
