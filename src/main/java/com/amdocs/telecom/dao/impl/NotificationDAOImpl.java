package com.amdocs.telecom.dao.impl;

import com.amdocs.telecom.dao.NotificationDAO;
import com.amdocs.telecom.model.Notification;
import com.amdocs.telecom.model.NotificationType;
import com.amdocs.telecom.exception.DAOException;
import com.amdocs.telecom.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAOImpl implements NotificationDAO {

    @Override
    public Integer create(Notification entity) throws DAOException {
        String sql = "INSERT INTO notification (recipient_id, ticket_id, message, notification_type, read_status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entity.getRecipientId());
            if (entity.getTicketId() != null) {
                stmt.setInt(2, entity.getTicketId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setString(3, entity.getMessage());
            stmt.setString(4, entity.getNotificationType() != null ? entity.getNotificationType().name() : NotificationType.TICKET_CREATION.name());
            stmt.setInt(5, entity.getReadStatus());
            
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        entity.setNotificationId(id);
                        return id;
                    }
                }
            }
            throw new DAOException("Creating Notification failed, no ID obtained.");
        } catch (SQLException e) {
            throw new DAOException("Error creating Notification: " + e.getMessage(), e);
        }
    }

    @Override
    public Notification findById(Integer id) throws DAOException {
        String sql = "SELECT * FROM notification WHERE notification_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding Notification by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Notification> findAll() throws DAOException {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notification ORDER BY created_date DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding all Notifications: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public boolean update(Notification entity) throws DAOException {
        String sql = "UPDATE notification SET recipient_id = ?, ticket_id = ?, message = ?, notification_type = ?, read_status = ? WHERE notification_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getRecipientId());
            if (entity.getTicketId() != null) stmt.setInt(2, entity.getTicketId()); else stmt.setNull(2, Types.INTEGER);
            stmt.setString(3, entity.getMessage());
            stmt.setString(4, entity.getNotificationType() != null ? entity.getNotificationType().name() : NotificationType.TICKET_CREATION.name());
            stmt.setInt(5, entity.getReadStatus());
            stmt.setInt(6, entity.getNotificationId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating Notification: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Integer id) throws DAOException {
        String sql = "DELETE FROM notification WHERE notification_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting Notification: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Notification> findByRecipientId(String recipientId) throws DAOException {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notification WHERE recipient_id = ? ORDER BY created_date DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, recipientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding Notifications by recipient ID: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public boolean markAsRead(Integer notificationId) throws DAOException {
        String sql = "UPDATE notification SET read_status = 1 WHERE notification_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, notificationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error marking Notification as read: " + e.getMessage(), e);
        }
    }

    private Notification mapResultSetToEntity(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setNotificationId(rs.getInt("notification_id"));
        notification.setRecipientId(rs.getString("recipient_id"));
        int ticketId = rs.getInt("ticket_id");
        if (!rs.wasNull()) {
            notification.setTicketId(ticketId);
        }
        notification.setMessage(rs.getString("message"));
        String typeStr = rs.getString("notification_type");
        if (typeStr != null) {
            try {
                notification.setNotificationType(NotificationType.valueOf(typeStr));
            } catch (Exception e) {
                notification.setNotificationType(NotificationType.TICKET_CREATION);
            }
        }
        Timestamp date = rs.getTimestamp("created_date");
        if (date != null) notification.setCreatedDate(date.toLocalDateTime());
        notification.setReadStatus(rs.getInt("read_status"));
        return notification;
    }
}
