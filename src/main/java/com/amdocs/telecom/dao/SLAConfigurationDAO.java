package com.amdocs.telecom.dao;

import com.amdocs.telecom.model.SLAConfiguration;
import com.amdocs.telecom.model.Priority;
import com.amdocs.telecom.exception.DAOException;

/**
 * DAO interface for SLAConfiguration entity operations.
 */
public interface SLAConfigurationDAO extends BaseDAO<SLAConfiguration, Integer> {
    
    /**
     * Finds SLA configuration by priority.
     */
    SLAConfiguration findByPriority(Priority priority) throws DAOException;
    
    /**
     * Finds SLA configuration by priority string.
     */
    SLAConfiguration findByPriorityString(String priority) throws DAOException;
}
