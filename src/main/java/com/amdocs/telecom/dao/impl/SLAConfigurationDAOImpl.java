package com.amdocs.telecom.dao.impl;

import com.amdocs.telecom.dao.SLAConfigurationDAO;
import com.amdocs.telecom.model.SLAConfiguration;
import com.amdocs.telecom.model.Priority;
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
 * Implementation of SLAConfigurationDAO using JDBC.
 * Manages SLA configuration for different priority levels.
 */
public class SLAConfigurationDAOImpl implements SLAConfigurationDAO {
    
    @Override
    public Integer create(SLAConfiguration slaConfig) throws DAOException {
        String sql = "INSERT INTO sla_configuration (priority, response_sla_minutes, resolution_sla_hours, created_date, updated_date) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, slaConfig.getPriority().name());
            pstmt.setInt(2, slaConfig.getResponseSlaMinutes());
            pstmt.setInt(3, slaConfig.getResolutionSlaHours());
            pstmt.setObject(4, slaConfig.getCreatedDate());
            pstmt.setObject(5, slaConfig.getUpdatedDate());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("Creating SLA configuration failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new DAOException("Creating SLA configuration failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error creating SLA configuration: " + e.getMessage(), e);
        }
    }

    @Override
    public SLAConfiguration findById(Integer id) throws DAOException {
        String sql = "SELECT * FROM sla_configuration WHERE sla_config_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSLAConfig(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding SLA configuration by ID: " + e.getMessage(), e);
        }
        
        return null;
    }

    @Override
    public List<SLAConfiguration> findAll() throws DAOException {
        String sql = "SELECT * FROM sla_configuration ORDER BY priority";
        List<SLAConfiguration> configs = new ArrayList<>();
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                configs.add(mapResultSetToSLAConfig(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding all SLA configurations: " + e.getMessage(), e);
        }
        
        return configs;
    }

    @Override
    public boolean update(SLAConfiguration slaConfig) throws DAOException {
        String sql = "UPDATE sla_configuration SET priority = ?, response_sla_minutes = ?, resolution_sla_hours = ?, updated_date = ? WHERE sla_config_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, slaConfig.getPriority().name());
            pstmt.setInt(2, slaConfig.getResponseSlaMinutes());
            pstmt.setInt(3, slaConfig.getResolutionSlaHours());
            pstmt.setObject(4, LocalDateTime.now());
            pstmt.setInt(5, slaConfig.getSlaConfigId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating SLA configuration: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Integer id) throws DAOException {
        String sql = "DELETE FROM sla_configuration WHERE sla_config_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting SLA configuration: " + e.getMessage(), e);
        }
    }

    @Override
    public SLAConfiguration findByPriority(Priority priority) throws DAOException {
        return findByPriorityString(priority.name());
    }

    @Override
    public SLAConfiguration findByPriorityString(String priority) throws DAOException {
        String sql = "SELECT * FROM sla_configuration WHERE priority = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, priority);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSLAConfig(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding SLA configuration by priority: " + e.getMessage(), e);
        }
        
        return null;
    }

    /**
     * Maps a ResultSet row to a SLAConfiguration object.
     */
    private SLAConfiguration mapResultSetToSLAConfig(ResultSet rs) throws SQLException {
        SLAConfiguration config = new SLAConfiguration();
        config.setSlaConfigId(rs.getInt("sla_config_id"));
        config.setPriority(Priority.fromString(rs.getString("priority")));
        config.setResponseSlaMinutes(rs.getInt("response_sla_minutes"));
        config.setResolutionSlaHours(rs.getInt("resolution_sla_hours"));
        config.setCreatedDate(rs.getObject("created_date", LocalDateTime.class));
        config.setUpdatedDate(rs.getObject("updated_date", LocalDateTime.class));
        return config;
    }
}
