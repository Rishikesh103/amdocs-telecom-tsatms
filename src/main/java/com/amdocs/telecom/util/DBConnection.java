package com.amdocs.telecom.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton pattern implementation for managing database connections.
 * This class provides a single point of access to the database connection
 * for all DAO operations throughout the system.
 * 
 * Design Pattern: SINGLETON
 */
public class DBConnection {
    private static DBConnection instance;
    private Connection connection;

    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/tsatms_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "RevNor";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    /**
     * Private constructor to prevent instantiation from outside the class.
     * Attempts to establish connection to the database.
     */
    private DBConnection() {
        connect();
    }

    private void connect() {
        try {
            Class.forName(DB_DRIVER);
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Failed to establish database connection: " + e.getMessage());
        }
    }

    /**
     * Static method to get the singleton instance.
     * Thread-safe implementation using synchronized block.
     * 
     * @return The single instance of DBConnection
     */
    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    /**
     * Returns the active database connection.
     * 
     * @return Connection object for database operations
     * @throws SQLException if connection is null or closed
     */
    public synchronized Connection getConnection() throws SQLException {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Closes the database connection.
     * Should be called during application shutdown.
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if the connection is active.
     * 
     * @return true if connection is active and not closed, false otherwise
     */
    public boolean isConnectionActive() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
