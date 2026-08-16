package com.amdocs.telecom.dao.impl;

import com.amdocs.telecom.dao.NetworkEventDAO;
import com.amdocs.telecom.model.NetworkEvent;
import com.amdocs.telecom.model.Severity;
import com.amdocs.telecom.exception.DAOException;
import com.amdocs.telecom.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NetworkEventDAOImpl implements NetworkEventDAO {

    @Override
    public Integer create(NetworkEvent entity) throws DAOException {
        String sql = "INSERT INTO network_event (event_number, network_node, event_type, severity, event_time, ticket_created_id, processed) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entity.getEventNumber());
            stmt.setString(2, entity.getNetworkNode());
            stmt.setString(3, entity.getEventType());
            stmt.setString(4, entity.getSeverity() != null ? entity.getSeverity().name() : Severity.MEDIUM.name());
            stmt.setTimestamp(5, entity.getEventTime() != null ? Timestamp.valueOf(entity.getEventTime()) : new Timestamp(System.currentTimeMillis()));
            if (entity.getTicketCreatedId() != null) stmt.setInt(6, entity.getTicketCreatedId()); else stmt.setNull(6, Types.INTEGER);
            stmt.setInt(7, entity.getProcessed());
            
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        entity.setEventId(id);
                        return id;
                    }
                }
            }
            throw new DAOException("Creating NetworkEvent failed, no ID obtained.");
        } catch (SQLException e) {
            throw new DAOException("Error creating NetworkEvent: " + e.getMessage(), e);
        }
    }

    @Override
    public NetworkEvent findById(Integer id) throws DAOException {
        String sql = "SELECT * FROM network_event WHERE event_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding NetworkEvent by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<NetworkEvent> findAll() throws DAOException {
        List<NetworkEvent> list = new ArrayList<>();
        String sql = "SELECT * FROM network_event ORDER BY event_time DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding all NetworkEvents: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public boolean update(NetworkEvent entity) throws DAOException {
        String sql = "UPDATE network_event SET event_number = ?, network_node = ?, event_type = ?, severity = ?, event_time = ?, ticket_created_id = ?, processed = ? WHERE event_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getEventNumber());
            stmt.setString(2, entity.getNetworkNode());
            stmt.setString(3, entity.getEventType());
            stmt.setString(4, entity.getSeverity().name());
            stmt.setTimestamp(5, Timestamp.valueOf(entity.getEventTime()));
            if (entity.getTicketCreatedId() != null) stmt.setInt(6, entity.getTicketCreatedId()); else stmt.setNull(6, Types.INTEGER);
            stmt.setInt(7, entity.getProcessed());
            stmt.setInt(8, entity.getEventId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating NetworkEvent: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Integer id) throws DAOException {
        String sql = "DELETE FROM network_event WHERE event_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting NetworkEvent: " + e.getMessage(), e);
        }
    }

    @Override
    public List<NetworkEvent> findUnprocessedEvents() throws DAOException {
        List<NetworkEvent> list = new ArrayList<>();
        String sql = "SELECT * FROM network_event WHERE processed = 0 ORDER BY event_time ASC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding unprocessed NetworkEvents: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public boolean markAsProcessed(Integer eventId, Integer ticketId) throws DAOException {
        String sql = "UPDATE network_event SET processed = 1, ticket_created_id = ? WHERE event_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (ticketId != null) stmt.setInt(1, ticketId); else stmt.setNull(1, Types.INTEGER);
            stmt.setInt(2, eventId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error marking NetworkEvent as processed: " + e.getMessage(), e);
        }
    }

    private NetworkEvent mapResultSetToEntity(ResultSet rs) throws SQLException {
        NetworkEvent event = new NetworkEvent();
        event.setEventId(rs.getInt("event_id"));
        event.setEventNumber(rs.getString("event_number"));
        event.setNetworkNode(rs.getString("network_node"));
        event.setEventType(rs.getString("event_type"));
        String sevStr = rs.getString("severity");
        if (sevStr != null) event.setSeverity(Severity.valueOf(sevStr));
        Timestamp eventTime = rs.getTimestamp("event_time");
        if (eventTime != null) event.setEventTime(eventTime.toLocalDateTime());
        int ticketId = rs.getInt("ticket_created_id");
        if (!rs.wasNull()) event.setTicketCreatedId(ticketId);
        event.setProcessed(rs.getInt("processed"));
        Timestamp created = rs.getTimestamp("created_date");
        if (created != null) event.setCreatedDate(created.toLocalDateTime());
        return event;
    }
}
