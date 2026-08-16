package com.amdocs.telecom.dao;

import com.amdocs.telecom.model.UserAccount;
import com.amdocs.telecom.exception.DAOException;

/**
 * DAO interface for UserAccount entity operations.
 */
public interface UserAccountDAO extends BaseDAO<UserAccount, Integer> {
    
    /**
     * Finds a user account by username.
     */
    UserAccount findByUsername(String username) throws DAOException;
    
    /**
     * Updates failed login attempts for account locking.
     */
    boolean updateFailedLoginAttempts(int userId, int attempts) throws DAOException;
    
    /**
     * Locks an account until a specified time.
     */
    boolean lockAccount(int userId, java.time.LocalDateTime lockUntil) throws DAOException;
    
    /**
     * Unlocks an account.
     */
    boolean unlockAccount(int userId) throws DAOException;
    
    /**
     * Resets failed login attempts for successful login.
     */
    boolean resetFailedAttempts(int userId) throws DAOException;
}
