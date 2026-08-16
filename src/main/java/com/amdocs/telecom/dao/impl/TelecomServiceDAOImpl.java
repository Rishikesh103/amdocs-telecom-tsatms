package com.amdocs.telecom.dao.impl;

import com.amdocs.telecom.dao.TelecomServiceDAO;
import com.amdocs.telecom.model.TelecomService;
import com.amdocs.telecom.model.ServiceType;
import com.amdocs.telecom.model.EntityStatus;
import com.amdocs.telecom.exception.DAOException;
import com.amdocs.telecom.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TelecomServiceDAOImpl implements TelecomServiceDAO {

    @Override
    public Integer create(TelecomService entity) throws DAOException {
        String sql = "INSERT INTO telecom_service (service_code, service_name, service_type, customer_id, activation_date, service_status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entity.getServiceCode());
            stmt.setString(2, entity.getServiceName());
            stmt.setString(3, entity.getServiceType().name());
            stmt.setInt(4, entity.getCustomerId());
            stmt.setDate(5, entity.getActivationDate() != null ? Date.valueOf(entity.getActivationDate()) : new Date(System.currentTimeMillis()));
            stmt.setString(6, entity.getServiceStatus() != null ? entity.getServiceStatus().name() : EntityStatus.ACTIVE.name());
            
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        entity.setServiceId(generatedId);
                        return generatedId;
                    }
                }
            }
            throw new DAOException("Creating TelecomService failed, no ID obtained.");
        } catch (SQLException e) {
            throw new DAOException("Error creating TelecomService: " + e.getMessage(), e);
        }
    }

    @Override
    public TelecomService findById(Integer id) throws DAOException {
        String sql = "SELECT * FROM telecom_service WHERE service_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding TelecomService by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<TelecomService> findAll() throws DAOException {
        List<TelecomService> list = new ArrayList<>();
        String sql = "SELECT * FROM telecom_service";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding all TelecomServices: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public boolean update(TelecomService entity) throws DAOException {
        String sql = "UPDATE telecom_service SET service_code = ?, service_name = ?, service_type = ?, customer_id = ?, activation_date = ?, service_status = ? WHERE service_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getServiceCode());
            stmt.setString(2, entity.getServiceName());
            stmt.setString(3, entity.getServiceType().name());
            stmt.setInt(4, entity.getCustomerId());
            stmt.setDate(5, entity.getActivationDate() != null ? Date.valueOf(entity.getActivationDate()) : null);
            stmt.setString(6, entity.getServiceStatus().name());
            stmt.setInt(7, entity.getServiceId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating TelecomService: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Integer id) throws DAOException {
        String sql = "DELETE FROM telecom_service WHERE service_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting TelecomService: " + e.getMessage(), e);
        }
    }

    @Override
    public TelecomService findByServiceCode(String serviceCode) throws DAOException {
        String sql = "SELECT * FROM telecom_service WHERE service_code = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, serviceCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding TelecomService by code: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<TelecomService> findByCustomerId(Integer customerId) throws DAOException {
        List<TelecomService> list = new ArrayList<>();
        String sql = "SELECT * FROM telecom_service WHERE customer_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding TelecomServices by customer ID: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<TelecomService> findByServiceType(String serviceType) throws DAOException {
        List<TelecomService> list = new ArrayList<>();
        String sql = "SELECT * FROM telecom_service WHERE service_type = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, serviceType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding TelecomServices by service type: " + e.getMessage(), e);
        }
        return list;
    }

    private TelecomService mapResultSetToEntity(ResultSet rs) throws SQLException {
        TelecomService service = new TelecomService();
        service.setServiceId(rs.getInt("service_id"));
        service.setServiceCode(rs.getString("service_code"));
        service.setServiceName(rs.getString("service_name"));
        String typeStr = rs.getString("service_type");
        if (typeStr != null) {
            service.setServiceType(ServiceType.fromString(typeStr));
        }
        service.setCustomerId(rs.getInt("customer_id"));
        Date actDate = rs.getDate("activation_date");
        if (actDate != null) {
            service.setActivationDate(actDate.toLocalDate());
        }
        String statusStr = rs.getString("service_status");
        if (statusStr != null) {
            service.setServiceStatus(EntityStatus.fromString(statusStr));
        }
        Timestamp created = rs.getTimestamp("created_date");
        if (created != null) service.setCreatedDate(created.toLocalDateTime());
        Timestamp updated = rs.getTimestamp("updated_date");
        if (updated != null) service.setUpdatedDate(updated.toLocalDateTime());
        return service;
    }
}
