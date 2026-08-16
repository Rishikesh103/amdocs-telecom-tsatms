package com.amdocs.telecom.dao.impl;

import com.amdocs.telecom.dao.EscalationHistoryDAO;
import com.amdocs.telecom.model.EscalationHistory;
import com.amdocs.telecom.exception.DAOException;
import com.amdocs.telecom.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EscalationHistoryDAOImpl implements EscalationHistoryDAO {

    @Override
    public Integer create(EscalationHistory entity) throws DAOException {
        String sql = "INSERT INTO escalation_history (ticket_id, from_level, to_level, reason, escalated_by) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, entity.getTicketId());
            stmt.setString(2, entity.getFromLevel());
            stmt.setString(3, entity.getToLevel());
            stmt.setString(4, entity.getReason());
            stmt.setString(5, entity.getEscalatedBy());
            
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        entity.setEscalationId(id);
                        return id;
                    }
                }
            }
            throw new DAOException("Creating EscalationHistory failed, no ID obtained.");
        } catch (SQLException e) {
            throw new DAOException("Error creating EscalationHistory: " + e.getMessage(), e);
        }
    }

    @Override
    public EscalationHistory findById(Integer id) throws DAOException {
        String sql = "SELECT * FROM escalation_history WHERE escalation_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding EscalationHistory by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<EscalationHistory> findAll() throws DAOException {
        List<EscalationHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM escalation_history ORDER BY escalation_date DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding all EscalationHistory: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public boolean update(EscalationHistory entity) throws DAOException {
        String sql = "UPDATE escalation_history SET ticket_id = ?, from_level = ?, to_level = ?, reason = ?, escalated_by = ? WHERE escalation_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, entity.getTicketId());
            stmt.setString(2, entity.getFromLevel());
            stmt.setString(3, entity.getToLevel());
            stmt.setString(4, entity.getReason());
            stmt.setString(5, entity.getEscalatedBy());
            stmt.setInt(6, entity.getEscalationId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating EscalationHistory: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Integer id) throws DAOException {
        String sql = "DELETE FROM escalation_history WHERE escalation_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting EscalationHistory: " + e.getMessage(), e);
        }
    }

    @Override
    public List<EscalationHistory> findByTicketId(Integer ticketId) throws DAOException {
        List<EscalationHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM escalation_history WHERE ticket_id = ? ORDER BY escalation_date ASC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding EscalationHistory by ticket ID: " + e.getMessage(), e);
        }
        return list;
    }

    private EscalationHistory mapResultSetToEntity(ResultSet rs) throws SQLException {
        EscalationHistory history = new EscalationHistory();
        history.setEscalationId(rs.getInt("escalation_id"));
        history.setTicketId(rs.getInt("ticket_id"));
        history.setFromLevel(rs.getString("from_level"));
        history.setToLevel(rs.getString("to_level"));
        history.setReason(rs.getString("reason"));
        Timestamp date = rs.getTimestamp("escalation_date");
        if (date != null) history.setEscalationDate(date.toLocalDateTime());
        history.setEscalatedBy(rs.getString("escalated_by"));
        return history;
    }
}
