package com.amdocs.telecom.dao.impl;

import com.amdocs.telecom.dao.AuditLogDAO;
import com.amdocs.telecom.model.AuditLog;
import com.amdocs.telecom.exception.DAOException;
import com.amdocs.telecom.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAOImpl implements AuditLogDAO {

    @Override
    public Integer create(AuditLog entity) throws DAOException {
        String sql = "INSERT INTO audit_log (user_id, action, entity_type, entity_id, old_value, new_value) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entity.getUserId());
            stmt.setString(2, entity.getAction());
            stmt.setString(3, entity.getEntityType());
            if (entity.getEntityId() != null) stmt.setInt(4, entity.getEntityId()); else stmt.setNull(4, Types.INTEGER);
            stmt.setString(5, entity.getOldValue());
            stmt.setString(6, entity.getNewValue());
            
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        entity.setAuditId(id);
                        return id;
                    }
                }
            }
            throw new DAOException("Creating AuditLog failed, no ID obtained.");
        } catch (SQLException e) {
            throw new DAOException("Error creating AuditLog: " + e.getMessage(), e);
        }
    }

    @Override
    public AuditLog findById(Integer id) throws DAOException {
        String sql = "SELECT * FROM audit_log WHERE audit_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding AuditLog by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<AuditLog> findAll() throws DAOException {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT * FROM audit_log ORDER BY action_date DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding all AuditLogs: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public boolean update(AuditLog entity) throws DAOException {
        String sql = "UPDATE audit_log SET user_id = ?, action = ?, entity_type = ?, entity_id = ?, old_value = ?, new_value = ? WHERE audit_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getUserId());
            stmt.setString(2, entity.getAction());
            stmt.setString(3, entity.getEntityType());
            if (entity.getEntityId() != null) stmt.setInt(4, entity.getEntityId()); else stmt.setNull(4, Types.INTEGER);
            stmt.setString(5, entity.getOldValue());
            stmt.setString(6, entity.getNewValue());
            stmt.setInt(7, entity.getAuditId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating AuditLog: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Integer id) throws DAOException {
        String sql = "DELETE FROM audit_log WHERE audit_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting AuditLog: " + e.getMessage(), e);
        }
    }

    @Override
    public List<AuditLog> findByUserId(String userId) throws DAOException {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT * FROM audit_log WHERE user_id = ? ORDER BY action_date DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding AuditLogs by user ID: " + e.getMessage(), e);
        }
        return list;
    }

    private AuditLog mapResultSetToEntity(ResultSet rs) throws SQLException {
        AuditLog audit = new AuditLog();
        audit.setAuditId(rs.getInt("audit_id"));
        audit.setUserId(rs.getString("user_id"));
        audit.setAction(rs.getString("action"));
        audit.setEntityType(rs.getString("entity_type"));
        int entityId = rs.getInt("entity_id");
        if (!rs.wasNull()) audit.setEntityId(entityId);
        audit.setOldValue(rs.getString("old_value"));
        audit.setNewValue(rs.getString("new_value"));
        Timestamp date = rs.getTimestamp("action_date");
        if (date != null) audit.setActionDate(date.toLocalDateTime());
        return audit;
    }
}
