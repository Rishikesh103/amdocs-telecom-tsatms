package com.amdocs.telecom.dao.impl;

import com.amdocs.telecom.dao.FeedbackDAO;
import com.amdocs.telecom.model.Feedback;
import com.amdocs.telecom.exception.DAOException;
import com.amdocs.telecom.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAOImpl implements FeedbackDAO {

    @Override
    public Integer create(Feedback entity) throws DAOException {
        String sql = "INSERT INTO feedback (ticket_id, customer_id, rating, comments) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, entity.getTicketId());
            stmt.setInt(2, entity.getCustomerId());
            stmt.setInt(3, entity.getRating());
            stmt.setString(4, entity.getComments());
            
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        entity.setFeedbackId(id);
                        return id;
                    }
                }
            }
            throw new DAOException("Creating Feedback failed, no ID obtained.");
        } catch (SQLException e) {
            throw new DAOException("Error creating Feedback: " + e.getMessage(), e);
        }
    }

    @Override
    public Feedback findById(Integer id) throws DAOException {
        String sql = "SELECT * FROM feedback WHERE feedback_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding Feedback by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Feedback> findAll() throws DAOException {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT * FROM feedback ORDER BY created_date DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding all Feedback: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public boolean update(Feedback entity) throws DAOException {
        String sql = "UPDATE feedback SET ticket_id = ?, customer_id = ?, rating = ?, comments = ? WHERE feedback_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, entity.getTicketId());
            stmt.setInt(2, entity.getCustomerId());
            stmt.setInt(3, entity.getRating());
            stmt.setString(4, entity.getComments());
            stmt.setInt(5, entity.getFeedbackId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating Feedback: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Integer id) throws DAOException {
        String sql = "DELETE FROM feedback WHERE feedback_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting Feedback: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Feedback> findByCustomerId(Integer customerId) throws DAOException {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT * FROM feedback WHERE customer_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding Feedback by customer ID: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public Feedback findByTicketId(Integer ticketId) throws DAOException {
        String sql = "SELECT * FROM feedback WHERE ticket_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding Feedback by ticket ID: " + e.getMessage(), e);
        }
        return null;
    }

    private Feedback mapResultSetToEntity(ResultSet rs) throws SQLException {
        Feedback feedback = new Feedback();
        feedback.setFeedbackId(rs.getInt("feedback_id"));
        feedback.setTicketId(rs.getInt("ticket_id"));
        feedback.setCustomerId(rs.getInt("customer_id"));
        feedback.setRating(rs.getInt("rating"));
        feedback.setComments(rs.getString("comments"));
        Timestamp date = rs.getTimestamp("created_date");
        if (date != null) feedback.setCreatedDate(date.toLocalDateTime());
        return feedback;
    }
}
