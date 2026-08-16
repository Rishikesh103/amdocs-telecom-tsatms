package com.amdocs.telecom.dao;

import com.amdocs.telecom.model.LoginHistory;
import com.amdocs.telecom.exception.DAOException;
import java.util.List;

/**
 * DAO interface for LoginHistory entity operations.
 */
public interface LoginHistoryDAO extends BaseDAO<LoginHistory, Integer> {
    
    /**
     * Finds all login records for a user.
     */
    List<LoginHistory> findByUserId(int userId) throws DAOException;
    
    /**
     * Updates logout time for a login record.
     */
    boolean updateLogoutTime(int loginId, java.time.LocalDateTime logoutTime) throws DAOException;
}
