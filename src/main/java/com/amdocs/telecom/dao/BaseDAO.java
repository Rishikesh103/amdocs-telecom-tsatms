package com.amdocs.telecom.dao;

import com.amdocs.telecom.exception.DAOException;
import java.util.List;

/**
 * Generic DAO interface providing CRUD operations for entities.
 * All DAO implementations should extend this interface.
 * 
 * Design Pattern: DAO (Data Access Object)
 * 
 * @param <T> The entity type
 * @param <ID> The primary key type
 */
public interface BaseDAO<T, ID> {
    
    /**
     * Creates a new record in the database.
     * 
     * @param entity The entity to create
     * @return The ID of the created entity
     * @throws DAOException if creation fails
     */
    ID create(T entity) throws DAOException;
    
    /**
     * Retrieves an entity by its primary key.
     * 
     * @param id The primary key
     * @return The entity if found, null otherwise
     * @throws DAOException if retrieval fails
     */
    T findById(ID id) throws DAOException;
    
    /**
     * Retrieves all entities.
     * 
     * @return A list of all entities
     * @throws DAOException if retrieval fails
     */
    List<T> findAll() throws DAOException;
    
    /**
     * Updates an existing entity.
     * 
     * @param entity The entity with updated values
     * @return true if update was successful, false otherwise
     * @throws DAOException if update fails
     */
    boolean update(T entity) throws DAOException;
    
    /**
     * Deletes an entity by its primary key.
     * 
     * @param id The primary key
     * @return true if deletion was successful, false otherwise
     * @throws DAOException if deletion fails
     */
    boolean delete(ID id) throws DAOException;
}
