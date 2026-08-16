package com.amdocs.telecom.dao.impl;

import com.amdocs.telecom.dao.LoginHistoryDAO;
import com.amdocs.telecom.model.LoginHistory;
import com.amdocs.telecom.model.LoginStatus;
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
 * Implementation of LoginHistoryDAO using JDBC.
 */
public class LoginHistoryDAOImpl implements LoginHistoryDAO {
    
    @Override
    public Integer create(LoginHistory loginHistory) throws DAOException {
        String sql = "INSERT INTO login_history (user_id, login_time, ip_address, status) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, loginHistory.getUserId());
            pstmt.setObject(2, loginHistory.getLoginTime());
            pstmt.setString(3, loginHistory.getIpAddress());
            pstmt.setString(4, loginHistory.getStatus().name());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("Creating login history failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new DAOException("Creating login history failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error creating login history: " + e.getMessage(), e);
        }
    }

    @Override
    public LoginHistory findById(Integer id) throws DAOException {
        String sql = "SELECT * FROM login_history WHERE login_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLoginHistory(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding login history by ID: " + e.getMessage(), e);
        }
        
        return null;
    }

    @Override
    public List<LoginHistory> findAll() throws DAOException {
        String sql = "SELECT * FROM login_history ORDER BY login_id DESC";
        List<LoginHistory> histories = new ArrayList<>();
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                histories.add(mapResultSetToLoginHistory(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding all login histories: " + e.getMessage(), e);
        }
        
        return histories;
    }

    @Override
    public boolean update(LoginHistory loginHistory) throws DAOException {
        String sql = "UPDATE login_history SET user_id = ?, login_time = ?, logout_time = ?, ip_address = ?, status = ? WHERE login_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, loginHistory.getUserId());
            pstmt.setObject(2, loginHistory.getLoginTime());
            pstmt.setObject(3, loginHistory.getLogoutTime());
            pstmt.setString(4, loginHistory.getIpAddress());
            pstmt.setString(5, loginHistory.getStatus().name());
            pstmt.setInt(6, loginHistory.getLoginId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating login history: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Integer id) throws DAOException {
        String sql = "DELETE FROM login_history WHERE login_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting login history: " + e.getMessage(), e);
        }
    }

    @Override
    public List<LoginHistory> findByUserId(int userId) throws DAOException {
        String sql = "SELECT * FROM login_history WHERE user_id = ? ORDER BY login_time DESC LIMIT 10";
        List<LoginHistory> histories = new ArrayList<>();
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    histories.add(mapResultSetToLoginHistory(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding login history by user ID: " + e.getMessage(), e);
        }
        
        return histories;
    }

    @Override
    public boolean updateLogoutTime(int loginId, LocalDateTime logoutTime) throws DAOException {
        String sql = "UPDATE login_history SET logout_time = ? WHERE login_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setObject(1, logoutTime);
            pstmt.setInt(2, loginId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating logout time: " + e.getMessage(), e);
        }
    }

    /**
     * Maps a ResultSet row to a LoginHistory object.
     */
    private LoginHistory mapResultSetToLoginHistory(ResultSet rs) throws SQLException {
        LoginHistory history = new LoginHistory();
        history.setLoginId(rs.getInt("login_id"));
        history.setUserId(rs.getInt("user_id"));
        history.setLoginTime(rs.getObject("login_time", LocalDateTime.class));
        history.setLogoutTime(rs.getObject("logout_time", LocalDateTime.class));
        history.setIpAddress(rs.getString("ip_address"));
        history.setStatus(LoginStatus.fromString(rs.getString("status")));
        return history;
    }
}
