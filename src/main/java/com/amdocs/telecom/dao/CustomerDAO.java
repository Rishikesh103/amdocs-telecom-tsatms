package com.amdocs.telecom.dao;

import com.amdocs.telecom.model.Customer;
import com.amdocs.telecom.exception.DAOException;
import java.util.List;

/**
 * DAO interface for Customer entity operations.
 */
public interface CustomerDAO extends BaseDAO<Customer, Integer> {
    
    /**
     * Finds a customer by customer number.
     * 
     * @param customerNumber The customer number
     * @return The customer if found, null otherwise
     * @throws DAOException if operation fails
     */
    Customer findByCustomerNumber(String customerNumber) throws DAOException;
    
    /**
     * Finds a customer by email address.
     * 
     * @param email The email address
     * @return The customer if found, null otherwise
     * @throws DAOException if operation fails
     */
    Customer findByEmail(String email) throws DAOException;
    
    /**
     * Finds a customer by mobile number.
     * 
     * @param mobileNumber The mobile number
     * @return The customer if found, null otherwise
     * @throws DAOException if operation fails
     */
    Customer findByMobileNumber(String mobileNumber) throws DAOException;
    
    /**
     * Finds all customers of a specific type.
     * 
     * @param customerType The customer type
     * @return A list of customers matching the type
     * @throws DAOException if operation fails
     */
    List<Customer> findByCustomerType(String customerType) throws DAOException;
    
    /**
     * Finds all active customers.
     * 
     * @return A list of active customers
     * @throws DAOException if operation fails
     */
    List<Customer> findAllActive() throws DAOException;
}
