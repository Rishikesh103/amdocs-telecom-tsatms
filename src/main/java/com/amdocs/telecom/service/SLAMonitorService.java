package com.amdocs.telecom.service;

import com.amdocs.telecom.exception.DAOException;

public interface SLAMonitorService {
    int checkAndProcessSLAs() throws DAOException;
}
