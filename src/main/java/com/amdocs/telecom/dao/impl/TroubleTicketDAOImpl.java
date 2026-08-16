package com.amdocs.telecom.dao.impl;

import com.amdocs.telecom.dao.TroubleTicketDAO;
import com.amdocs.telecom.model.TroubleTicket;
import com.amdocs.telecom.model.Priority;
import com.amdocs.telecom.model.TicketStatus;
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
 * Implementation of TroubleTicketDAO using JDBC.
 * Handles all database operations for trouble tickets.
 */
public class TroubleTicketDAOImpl implements TroubleTicketDAO {
    
    @Override
    public Integer create(TroubleTicket ticket) throws DAOException {
        String sql = "INSERT INTO trouble_ticket (ticket_number, customer_id, service_id, category, description, priority, severity, status, assigned_engineer_id, created_date, updated_date, sla_deadline) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, ticket.getTicketNumber());
            pstmt.setInt(2, ticket.getCustomerId());
            pstmt.setInt(3, ticket.getServiceId());
            pstmt.setString(4, ticket.getCategory());
            pstmt.setString(5, ticket.getDescription());
            pstmt.setString(6, ticket.getPriority().name());
            pstmt.setString(7, ticket.getSeverity());
            pstmt.setString(8, ticket.getStatus().name());
            pstmt.setObject(9, ticket.getAssignedEngineerId());
            pstmt.setObject(10, ticket.getCreatedDate() != null ? ticket.getCreatedDate() : LocalDateTime.now());
            pstmt.setObject(11, ticket.getUpdatedDate() != null ? ticket.getUpdatedDate() : LocalDateTime.now());
            pstmt.setObject(12, ticket.getSlaDeadline());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("Creating ticket failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new DAOException("Creating ticket failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error creating ticket: " + e.getMessage(), e);
        }
    }

    @Override
    public TroubleTicket findById(Integer id) throws DAOException {
        String sql = "SELECT * FROM trouble_ticket WHERE ticket_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTicket(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding ticket by ID: " + e.getMessage(), e);
        }
        
        return null;
    }

    @Override
    public List<TroubleTicket> findAll() throws DAOException {
        String sql = "SELECT * FROM trouble_ticket ORDER BY created_date DESC";
        List<TroubleTicket> tickets = new ArrayList<>();
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                tickets.add(mapResultSetToTicket(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding all tickets: " + e.getMessage(), e);
        }
        
        return tickets;
    }

    @Override
    public boolean update(TroubleTicket ticket) throws DAOException {
        String sql = "UPDATE trouble_ticket SET customer_id = ?, service_id = ?, category = ?, description = ?, priority = ?, severity = ?, status = ?, assigned_engineer_id = ?, updated_date = ?, sla_deadline = ?, resolution_date = ?, root_cause = ?, resolution_text = ?, resolution_code = ? WHERE ticket_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, ticket.getCustomerId());
            pstmt.setInt(2, ticket.getServiceId());
            pstmt.setString(3, ticket.getCategory());
            pstmt.setString(4, ticket.getDescription());
            pstmt.setString(5, ticket.getPriority().name());
            pstmt.setString(6, ticket.getSeverity());
            pstmt.setString(7, ticket.getStatus().name());
            pstmt.setObject(8, ticket.getAssignedEngineerId());
            pstmt.setObject(9, LocalDateTime.now());
            pstmt.setObject(10, ticket.getSlaDeadline());
            pstmt.setObject(11, ticket.getResolutionDate());
            pstmt.setString(12, ticket.getRootCause());
            pstmt.setString(13, ticket.getResolutionText());
            pstmt.setString(14, ticket.getResolutionCode());
            pstmt.setInt(15, ticket.getTicketId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating ticket: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Integer id) throws DAOException {
        String sql = "DELETE FROM trouble_ticket WHERE ticket_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting ticket: " + e.getMessage(), e);
        }
    }

    @Override
    public TroubleTicket findByTicketNumber(String ticketNumber) throws DAOException {
        String sql = "SELECT * FROM trouble_ticket WHERE ticket_number = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, ticketNumber);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTicket(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding ticket by number: " + e.getMessage(), e);
        }
        
        return null;
    }

    @Override
    public List<TroubleTicket> findByCustomerId(int customerId) throws DAOException {
        String sql = "SELECT * FROM trouble_ticket WHERE customer_id = ? ORDER BY created_date DESC";
        List<TroubleTicket> tickets = new ArrayList<>();
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapResultSetToTicket(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding tickets by customer: " + e.getMessage(), e);
        }
        
        return tickets;
    }

    @Override
    public List<TroubleTicket> findByEngineerId(int engineerId) throws DAOException {
        String sql = "SELECT * FROM trouble_ticket WHERE assigned_engineer_id = ? AND status NOT IN ('RESOLVED', 'CLOSED', 'CANCELLED') ORDER BY priority, created_date";
        List<TroubleTicket> tickets = new ArrayList<>();
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, engineerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapResultSetToTicket(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding tickets by engineer: " + e.getMessage(), e);
        }
        
        return tickets;
    }

    @Override
    public List<TroubleTicket> findByStatus(String status) throws DAOException {
        String sql = "SELECT * FROM trouble_ticket WHERE status = ? ORDER BY priority, created_date DESC";
        List<TroubleTicket> tickets = new ArrayList<>();
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapResultSetToTicket(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding tickets by status: " + e.getMessage(), e);
        }
        
        return tickets;
    }

    @Override
    public List<TroubleTicket> findAllOpen() throws DAOException {
        String sql = "SELECT * FROM trouble_ticket WHERE status NOT IN ('RESOLVED', 'CLOSED', 'CANCELLED') ORDER BY priority, created_date";
        List<TroubleTicket> tickets = new ArrayList<>();
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                tickets.add(mapResultSetToTicket(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding all open tickets: " + e.getMessage(), e);
        }
        
        return tickets;
    }

    @Override
    public List<TroubleTicket> findByPriority(String priority) throws DAOException {
        String sql = "SELECT * FROM trouble_ticket WHERE priority = ? ORDER BY created_date DESC";
        List<TroubleTicket> tickets = new ArrayList<>();
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, priority);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapResultSetToTicket(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding tickets by priority: " + e.getMessage(), e);
        }
        
        return tickets;
    }

    @Override
    public List<TroubleTicket> findSLAAtRisk() throws DAOException {
        String sql = "SELECT * FROM trouble_ticket WHERE status NOT IN ('RESOLVED', 'CLOSED', 'CANCELLED') AND sla_deadline IS NOT NULL AND sla_deadline <= DATE_ADD(NOW(), INTERVAL 30 MINUTE) ORDER BY sla_deadline ASC";
        List<TroubleTicket> tickets = new ArrayList<>();
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                tickets.add(mapResultSetToTicket(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding SLA at-risk tickets: " + e.getMessage(), e);
        }
        
        return tickets;
    }

    /**
     * Maps a ResultSet row to a TroubleTicket object.
     */
    private TroubleTicket mapResultSetToTicket(ResultSet rs) throws SQLException {
        TroubleTicket ticket = new TroubleTicket();
        ticket.setTicketId(rs.getInt("ticket_id"));
        ticket.setTicketNumber(rs.getString("ticket_number"));
        ticket.setCustomerId(rs.getInt("customer_id"));
        ticket.setServiceId(rs.getInt("service_id"));
        ticket.setCategory(rs.getString("category"));
        ticket.setDescription(rs.getString("description"));
        ticket.setPriority(Priority.fromString(rs.getString("priority")));
        ticket.setSeverity(rs.getString("severity"));
        ticket.setStatus(TicketStatus.fromString(rs.getString("status")));
        ticket.setAssignedEngineerId((Integer) rs.getObject("assigned_engineer_id"));
        ticket.setCreatedDate(rs.getObject("created_date", LocalDateTime.class));
        ticket.setUpdatedDate(rs.getObject("updated_date", LocalDateTime.class));
        ticket.setSlaDeadline(rs.getObject("sla_deadline", LocalDateTime.class));
        ticket.setResolutionDate(rs.getObject("resolution_date", LocalDateTime.class));
        ticket.setRootCause(rs.getString("root_cause"));
        ticket.setResolutionText(rs.getString("resolution_text"));
        ticket.setResolutionCode(rs.getString("resolution_code"));
        return ticket;
    }
}
