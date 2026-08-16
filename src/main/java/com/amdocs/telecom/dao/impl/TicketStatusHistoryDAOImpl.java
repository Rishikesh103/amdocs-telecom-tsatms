package com.amdocs.telecom.dao.impl;

import com.amdocs.telecom.dao.TicketStatusHistoryDAO;
import com.amdocs.telecom.model.TicketStatusHistory;
import com.amdocs.telecom.exception.DAOException;
import com.amdocs.telecom.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketStatusHistoryDAOImpl implements TicketStatusHistoryDAO {

    @Override
    public Integer create(TicketStatusHistory entity) throws DAOException {
        String sql = "INSERT INTO ticket_status_history (ticket_id, old_status, new_status, changed_by, remarks) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, entity.getTicketId());
            stmt.setString(2, entity.getOldStatus());
            stmt.setString(3, entity.getNewStatus());
            stmt.setString(4, entity.getChangedBy());
            stmt.setString(5, entity.getRemarks());
            
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        entity.setHistoryId(id);
                        return id;
                    }
                }
            }
            throw new DAOException("Creating TicketStatusHistory failed, no ID obtained.");
        } catch (SQLException e) {
            throw new DAOException("Error creating TicketStatusHistory: " + e.getMessage(), e);
        }
    }

    @Override
    public TicketStatusHistory findById(Integer id) throws DAOException {
        String sql = "SELECT * FROM ticket_status_history WHERE history_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding TicketStatusHistory by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<TicketStatusHistory> findAll() throws DAOException {
        List<TicketStatusHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM ticket_status_history ORDER BY changed_date DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding all TicketStatusHistory: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public boolean update(TicketStatusHistory entity) throws DAOException {
        String sql = "UPDATE ticket_status_history SET ticket_id = ?, old_status = ?, new_status = ?, changed_by = ?, remarks = ? WHERE history_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, entity.getTicketId());
            stmt.setString(2, entity.getOldStatus());
            stmt.setString(3, entity.getNewStatus());
            stmt.setString(4, entity.getChangedBy());
            stmt.setString(5, entity.getRemarks());
            stmt.setInt(6, entity.getHistoryId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating TicketStatusHistory: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Integer id) throws DAOException {
        String sql = "DELETE FROM ticket_status_history WHERE history_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting TicketStatusHistory: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TicketStatusHistory> findByTicketId(Integer ticketId) throws DAOException {
        List<TicketStatusHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM ticket_status_history WHERE ticket_id = ? ORDER BY changed_date ASC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding TicketStatusHistory by ticket ID: " + e.getMessage(), e);
        }
        return list;
    }

    private TicketStatusHistory mapResultSetToEntity(ResultSet rs) throws SQLException {
        TicketStatusHistory history = new TicketStatusHistory();
        history.setHistoryId(rs.getInt("history_id"));
        history.setTicketId(rs.getInt("ticket_id"));
        history.setOldStatus(rs.getString("old_status"));
        history.setNewStatus(rs.getString("new_status"));
        history.setChangedBy(rs.getString("changed_by"));
        Timestamp date = rs.getTimestamp("changed_date");
        if (date != null) history.setChangedDate(date.toLocalDateTime());
        history.setRemarks(rs.getString("remarks"));
        return history;
    }
}
