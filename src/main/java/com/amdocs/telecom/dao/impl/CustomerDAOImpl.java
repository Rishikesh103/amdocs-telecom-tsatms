package com.amdocs.telecom.dao.impl;

import com.amdocs.telecom.dao.CustomerDAO;
import com.amdocs.telecom.model.Customer;
import com.amdocs.telecom.model.CustomerType;
import com.amdocs.telecom.model.EntityStatus;
import com.amdocs.telecom.exception.DAOException;
import com.amdocs.telecom.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of CustomerDAO using JDBC with PreparedStatements.
 * Demonstrates proper use of JDBC, transaction handling, and custom exceptions.
 * 
 * Design Pattern: DAO (Data Access Object)
 */
public class CustomerDAOImpl implements CustomerDAO {
    
    @Override
    public Integer create(Customer customer) throws DAOException {
        String sql = "INSERT INTO customer (customer_number, customer_name, email, mobile_number, customer_type, city, status, created_date, updated_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, customer.getCustomerNumber());
            pstmt.setString(2, customer.getFullName());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getMobileNumber());
            pstmt.setString(5, customer.getCustomerType().name());
            pstmt.setString(6, customer.getCity());
            pstmt.setString(7, customer.getStatus().name());
            pstmt.setObject(8, customer.getCreatedDate());
            pstmt.setObject(9, customer.getUpdatedDate());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("Creating customer failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new DAOException("Creating customer failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error creating customer: " + e.getMessage(), e);
        }
    }

    @Override
    public Customer findById(Integer id) throws DAOException {
        String sql = "SELECT * FROM customer WHERE customer_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding customer by ID: " + e.getMessage(), e);
        }
        
        return null;
    }

    @Override
    public List<Customer> findAll() throws DAOException {
        String sql = "SELECT * FROM customer ORDER BY customer_id";
        List<Customer> customers = new ArrayList<>();
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding all customers: " + e.getMessage(), e);
        }
        
        return customers;
    }

    @Override
    public boolean update(Customer customer) throws DAOException {
        String sql = "UPDATE customer SET customer_name = ?, email = ?, mobile_number = ?, customer_type = ?, city = ?, status = ?, updated_date = ? WHERE customer_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customer.getFullName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getMobileNumber());
            pstmt.setString(4, customer.getCustomerType().name());
            pstmt.setString(5, customer.getCity());
            pstmt.setString(6, customer.getStatus().name());
            pstmt.setObject(7, LocalDateTime.now());
            pstmt.setInt(8, customer.getCustomerId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating customer: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Integer id) throws DAOException {
        String sql = "DELETE FROM customer WHERE customer_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DAOException("Error deleting customer: " + e.getMessage(), e);
        }
    }

    @Override
    public Customer findByCustomerNumber(String customerNumber) throws DAOException {
        String sql = "SELECT * FROM customer WHERE customer_number = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customerNumber);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding customer by number: " + e.getMessage(), e);
        }
        
        return null;
    }

    @Override
    public Customer findByEmail(String email) throws DAOException {
        String sql = "SELECT * FROM customer WHERE email = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding customer by email: " + e.getMessage(), e);
        }
        
        return null;
    }

    @Override
    public Customer findByMobileNumber(String mobileNumber) throws DAOException {
        String sql = "SELECT * FROM customer WHERE mobile_number = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, mobileNumber);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding customer by mobile: " + e.getMessage(), e);
        }
        
        return null;
    }

    @Override
    public List<Customer> findByCustomerType(String customerType) throws DAOException {
        String sql = "SELECT * FROM customer WHERE customer_type = ? ORDER BY customer_id";
        List<Customer> customers = new ArrayList<>();
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customerType);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding customers by type: " + e.getMessage(), e);
        }
        
        return customers;
    }

    @Override
    public List<Customer> findAllActive() throws DAOException {
        String sql = "SELECT * FROM customer WHERE status = 'ACTIVE' ORDER BY customer_id";
        List<Customer> customers = new ArrayList<>();
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding active customers: " + e.getMessage(), e);
        }
        
        return customers;
    }

    /**
     * Maps a ResultSet row to a Customer object.
     * Helper method to reduce code duplication.
     */
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customer_id"));
        customer.setCustomerNumber(rs.getString("customer_number"));
        customer.setFullName(rs.getString("customer_name"));
        customer.setEmail(rs.getString("email"));
        customer.setMobileNumber(rs.getString("mobile_number"));
        customer.setCustomerType(CustomerType.fromString(rs.getString("customer_type")));
        customer.setCity(rs.getString("city"));
        customer.setStatus(EntityStatus.fromString(rs.getString("status")));
        customer.setCreatedDate(rs.getObject("created_date", LocalDateTime.class));
        customer.setUpdatedDate(rs.getObject("updated_date", LocalDateTime.class));
        return customer;
    }
}
