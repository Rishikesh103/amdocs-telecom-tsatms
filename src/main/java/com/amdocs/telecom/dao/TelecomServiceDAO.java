package com.amdocs.telecom.dao;

import com.amdocs.telecom.model.TelecomService;
import com.amdocs.telecom.exception.DAOException;
import java.util.List;

/**
 * Data Access Object interface for TelecomService entity.
 */
public interface TelecomServiceDAO extends BaseDAO<TelecomService, Integer> {
    TelecomService findByServiceCode(String serviceCode) throws DAOException;
    List<TelecomService> findByCustomerId(Integer customerId) throws DAOException;
    List<TelecomService> findByServiceType(String serviceType) throws DAOException;
}
