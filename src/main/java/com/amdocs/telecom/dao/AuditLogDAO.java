package com.amdocs.telecom.dao;

import com.amdocs.telecom.model.AuditLog;
import com.amdocs.telecom.exception.DAOException;
import java.util.List;

public interface AuditLogDAO extends BaseDAO<AuditLog, Integer> {
    List<AuditLog> findByUserId(String userId) throws DAOException;
}
