package com.clbooster.app.backend.service.database;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

class DatabaseConnectionTest {
    @Test
    void testConnection() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {
                assertFalse(conn.isClosed());
                conn.close();
            }
        } catch (SQLException e) {
            fail("Connection failed: " + e.getMessage());
        }
    }
}
