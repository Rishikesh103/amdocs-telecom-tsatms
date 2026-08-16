package com.amdocs.telecom.dao.impl;

import com.amdocs.telecom.dao.UserAccountDAO;
import com.amdocs.telecom.model.UserAccount;
import com.amdocs.telecom.model.UserRole;
import com.amdocs.telecom.model.AccountStatus;
import com.amdocs.telecom.exception.DAOException;
import com.amdocs.telecom.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of UserAccountDAO using JDBC.
 */
public class UserAccountDAOImpl implements UserAccountDAO {
    
    @Override
    public Integer create(UserAccount account) throws DAOException {
        String sql = "INSERT INTO user_account (username, password_hash, role, linked_id, status, failed_login_attempts, created_date, updated_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, account.getUsername());
            pstmt.setString(2, account.getPasswordHash());
            pstmt.setString(3, account.getRole().name());
            pstmt.setObject(4, account.getLinkedId());
            pstmt.setString(5, account.getStatus().name());
            pstmt.setInt(6, account.getFailedLoginAttempts());
            pstmt.setObject(7, account.getCreatedDate());
            pstmt.setObject(8, account.getUpdatedDate());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("Creating user account failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new DAOException("Creating user account failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error creating user account: " + e.getMessage(), e);
        }
    }

    @Override
    public UserAccount findById(Integer id) throws DAOException {
        String sql = "SELECT * FROM user_account WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAccount(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding user account by ID: " + e.getMessage(), e);
        }
        
        return null;
    }

    @Override
    public List<UserAccount> findAll() throws DAOException {
        String sql = "SELECT * FROM user_account ORDER BY user_id";
        List<UserAccount> accounts = new ArrayList<>();
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding all user accounts: " + e.getMessage(), e);
        }
        
        return accounts;
    }

    @Override
    public boolean update(UserAccount account) throws DAOException {
        String sql = "UPDATE user_account SET username = ?, password_hash = ?, role = ?, linked_id = ?, status = ?, failed_login_attempts = ?, lock_until = ?, updated_date = ? WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, account.getUsername());
            pstmt.setString(2, account.getPasswordHash());
            pstmt.setString(3, account.getRole().name());
            pstmt.setObject(4, account.getLinkedId());
            pstmt.setString(5, account.getStatus().name());
            pstmt.setInt(6, account.getFailedLoginAttempts());
            pstmt.setObject(7, account.getLockUntil());
            pstmt.setObject(8, LocalDateTime.now());
            pstmt.setInt(9, account.getUserId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating user account: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Integer id) throws DAOException {
        String sql = "DELETE FROM user_account WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting user account: " + e.getMessage(), e);
        }
    }

    @Override
    public UserAccount findByUsername(String username) throws DAOException {
        String sql = "SELECT * FROM user_account WHERE username = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAccount(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding user account by username: " + e.getMessage(), e);
        }
        
        return null;
    }

    @Override
    public boolean updateFailedLoginAttempts(int userId, int attempts) throws DAOException {
        String sql = "UPDATE user_account SET failed_login_attempts = ?, updated_date = ? WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, attempts);
            pstmt.setObject(2, LocalDateTime.now());
            pstmt.setInt(3, userId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating failed login attempts: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean lockAccount(int userId, LocalDateTime lockUntil) throws DAOException {
        String sql = "UPDATE user_account SET status = 'LOCKED', lock_until = ?, updated_date = ? WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setObject(1, lockUntil);
            pstmt.setObject(2, LocalDateTime.now());
            pstmt.setInt(3, userId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DAOException("Error locking user account: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean unlockAccount(int userId) throws DAOException {
        String sql = "UPDATE user_account SET status = 'ACTIVE', lock_until = NULL, failed_login_attempts = 0, updated_date = ? WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setObject(1, LocalDateTime.now());
            pstmt.setInt(2, userId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DAOException("Error unlocking user account: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean resetFailedAttempts(int userId) throws DAOException {
        String sql = "UPDATE user_account SET failed_login_attempts = 0, lock_until = NULL, updated_date = ? WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setObject(1, LocalDateTime.now());
            pstmt.setInt(2, userId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DAOException("Error resetting failed attempts: " + e.getMessage(), e);
        }
    }

    /**
     * Maps a ResultSet row to a UserAccount object.
     */
    private UserAccount mapResultSetToAccount(ResultSet rs) throws SQLException {
        UserAccount account = new UserAccount();
        account.setUserId(rs.getInt("user_id"));
        account.setUsername(rs.getString("username"));
        account.setPasswordHash(rs.getString("password_hash"));
        account.setRole(UserRole.fromString(rs.getString("role")));
        account.setLinkedId((Integer) rs.getObject("linked_id"));
        account.setStatus(AccountStatus.fromString(rs.getString("status")));
        account.setFailedLoginAttempts(rs.getInt("failed_login_attempts"));
        account.setLockUntil(rs.getObject("lock_until", LocalDateTime.class));
        account.setCreatedDate(rs.getObject("created_date", LocalDateTime.class));
        account.setUpdatedDate(rs.getObject("updated_date", LocalDateTime.class));
        return account;
    }
}
