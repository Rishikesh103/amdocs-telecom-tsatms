package com.amdocs.telecom.dao.impl;

import com.amdocs.telecom.dao.NetworkEngineerDAO;
import com.amdocs.telecom.model.NetworkEngineer;
import com.amdocs.telecom.model.AvailabilityStatus;
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
 * Implementation of NetworkEngineerDAO using JDBC.
 */
public class NetworkEngineerDAOImpl implements NetworkEngineerDAO {
    
    @Override
    public Integer create(NetworkEngineer engineer) throws DAOException {
        String sql = "INSERT INTO network_engineer (employee_code, engineer_name, specialization, region, experience_years, availability, active_ticket_count, created_date, updated_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, engineer.getEmployeeCode());
            pstmt.setString(2, engineer.getFullName());
            pstmt.setString(3, engineer.getSpecialization());
            pstmt.setString(4, engineer.getRegion());
            pstmt.setInt(5, engineer.getExperienceYears());
            pstmt.setString(6, engineer.getAvailability().name());
            pstmt.setInt(7, engineer.getActiveTicketCount());
            pstmt.setObject(8, engineer.getCreatedDate());
            pstmt.setObject(9, engineer.getUpdatedDate());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("Creating engineer failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new DAOException("Creating engineer failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error creating engineer: " + e.getMessage(), e);
        }
    }

    @Override
    public NetworkEngineer findById(Integer id) throws DAOException {
        String sql = "SELECT * FROM network_engineer WHERE engineer_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEngineer(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding engineer by ID: " + e.getMessage(), e);
        }
        
        return null;
    }

    @Override
    public List<NetworkEngineer> findAll() throws DAOException {
        String sql = "SELECT * FROM network_engineer ORDER BY engineer_id";
        List<NetworkEngineer> engineers = new ArrayList<>();
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                engineers.add(mapResultSetToEngineer(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding all engineers: " + e.getMessage(), e);
        }
        
        return engineers;
    }

    @Override
    public boolean update(NetworkEngineer engineer) throws DAOException {
        String sql = "UPDATE network_engineer SET engineer_name = ?, specialization = ?, region = ?, experience_years = ?, availability = ?, active_ticket_count = ?, updated_date = ? WHERE engineer_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, engineer.getFullName());
            pstmt.setString(2, engineer.getSpecialization());
            pstmt.setString(3, engineer.getRegion());
            pstmt.setInt(4, engineer.getExperienceYears());
            pstmt.setString(5, engineer.getAvailability().name());
            pstmt.setInt(6, engineer.getActiveTicketCount());
            pstmt.setObject(7, LocalDateTime.now());
            pstmt.setInt(8, engineer.getEngineerId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating engineer: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Integer id) throws DAOException {
        String sql = "DELETE FROM network_engineer WHERE engineer_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting engineer: " + e.getMessage(), e);
        }
    }

    @Override
    public NetworkEngineer findByEmployeeCode(String employeeCode) throws DAOException {
        String sql = "SELECT * FROM network_engineer WHERE employee_code = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, employeeCode);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEngineer(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding engineer by code: " + e.getMessage(), e);
        }
        
        return null;
    }

    @Override
    public List<NetworkEngineer> findByRegion(String region) throws DAOException {
        String sql = "SELECT * FROM network_engineer WHERE region = ? ORDER BY engineer_id";
        List<NetworkEngineer> engineers = new ArrayList<>();
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, region);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    engineers.add(mapResultSetToEngineer(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding engineers by region: " + e.getMessage(), e);
        }
        
        return engineers;
    }

    @Override
    public List<NetworkEngineer> findBySpecialization(String specialization) throws DAOException {
        String sql = "SELECT * FROM network_engineer WHERE specialization = ? ORDER BY engineer_id";
        List<NetworkEngineer> engineers = new ArrayList<>();
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, specialization);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    engineers.add(mapResultSetToEngineer(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding engineers by specialization: " + e.getMessage(), e);
        }
        
        return engineers;
    }

    @Override
    public List<NetworkEngineer> findAllAvailable() throws DAOException {
        String sql = "SELECT * FROM network_engineer WHERE availability = 'AVAILABLE' ORDER BY active_ticket_count ASC, experience_years DESC";
        List<NetworkEngineer> engineers = new ArrayList<>();
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                engineers.add(mapResultSetToEngineer(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding available engineers: " + e.getMessage(), e);
        }
        
        return engineers;
    }

    @Override
    public List<NetworkEngineer> findByRegionAndSpecialization(String region, String specialization) throws DAOException {
        String sql = "SELECT * FROM network_engineer WHERE region = ? AND specialization = ? ORDER BY active_ticket_count ASC, experience_years DESC";
        List<NetworkEngineer> engineers = new ArrayList<>();
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, region);
            pstmt.setString(2, specialization);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    engineers.add(mapResultSetToEngineer(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding engineers by region and specialization: " + e.getMessage(), e);
        }
        
        return engineers;
    }

    /**
     * Maps a ResultSet row to a NetworkEngineer object.
     */
    private NetworkEngineer mapResultSetToEngineer(ResultSet rs) throws SQLException {
        NetworkEngineer engineer = new NetworkEngineer();
        engineer.setEngineerId(rs.getInt("engineer_id"));
        engineer.setEmployeeCode(rs.getString("employee_code"));
        engineer.setFullName(rs.getString("engineer_name"));
        engineer.setSpecialization(rs.getString("specialization"));
        engineer.setRegion(rs.getString("region"));
        engineer.setExperienceYears(rs.getInt("experience_years"));
        engineer.setAvailability(AvailabilityStatus.fromString(rs.getString("availability")));
        engineer.setActiveTicketCount(rs.getInt("active_ticket_count"));
        engineer.setCreatedDate(rs.getObject("created_date", LocalDateTime.class));
        engineer.setUpdatedDate(rs.getObject("updated_date", LocalDateTime.class));
        return engineer;
    }
}
