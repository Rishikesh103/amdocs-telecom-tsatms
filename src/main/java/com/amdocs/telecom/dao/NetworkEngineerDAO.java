package com.amdocs.telecom.dao;

import com.amdocs.telecom.model.NetworkEngineer;
import com.amdocs.telecom.exception.DAOException;
import java.util.List;

/**
 * DAO interface for NetworkEngineer entity operations.
 */
public interface NetworkEngineerDAO extends BaseDAO<NetworkEngineer, Integer> {
    
    /**
     * Finds an engineer by employee code.
     */
    NetworkEngineer findByEmployeeCode(String employeeCode) throws DAOException;
    
    /**
     * Finds all engineers by region.
     */
    List<NetworkEngineer> findByRegion(String region) throws DAOException;
    
    /**
     * Finds all engineers by specialization.
     */
    List<NetworkEngineer> findBySpecialization(String specialization) throws DAOException;
    
    /**
     * Finds all available engineers.
     */
    List<NetworkEngineer> findAllAvailable() throws DAOException;
    
    /**
     * Finds all engineers by region and specialization.
     */
    List<NetworkEngineer> findByRegionAndSpecialization(String region, String specialization) throws DAOException;
}
