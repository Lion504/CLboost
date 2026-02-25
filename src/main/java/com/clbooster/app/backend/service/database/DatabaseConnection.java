package com.clbooster.app.backend.service.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = System.getenv().getOrDefault("DB_URL", 
            "jdbc:mariadb://" + 
            System.getenv().getOrDefault("DB_HOST", "localhost") + ":" +
            System.getenv().getOrDefault("DB_PORT", "3306") + "/" +
            System.getenv().getOrDefault("DB_NAME", "CL_generator"));
    private static final String USER = System.getenv().getOrDefault("DB_USERNAME", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "password");

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
    }
}
