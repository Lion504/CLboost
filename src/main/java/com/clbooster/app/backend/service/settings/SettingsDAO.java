package com.clbooster.app.backend.service.settings;

import com.clbooster.app.backend.service.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SettingsDAO {

    public SettingsDAO() {
        // Ensure table exists when DAO is created
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS user_settings (" +
                "user_pin INT PRIMARY KEY," +
                "theme VARCHAR(20) DEFAULT 'system'," +
                "language VARCHAR(50) DEFAULT 'English'," +
                "email_notifications BOOLEAN DEFAULT TRUE," +
                "push_notifications BOOLEAN DEFAULT FALSE," +
                "product_updates BOOLEAN DEFAULT TRUE," +
                "marketing BOOLEAN DEFAULT FALSE," +
                "store_in_cloud BOOLEAN DEFAULT TRUE," +
                "allow_ai_training BOOLEAN DEFAULT FALSE," +
                "share_usage_data BOOLEAN DEFAULT TRUE," +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_pin) REFERENCES identification(Pin) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("DEBUG: user_settings table checked/created successfully");
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to create user_settings table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Settings getSettings(int userPin) {
        String sql = "SELECT * FROM user_settings WHERE user_pin = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userPin);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Settings settings = new Settings();
                settings.setUserPin(userPin);
                settings.setTheme(rs.getString("theme"));
                settings.setLanguage(rs.getString("language"));
                settings.setEmailNotifications(rs.getBoolean("email_notifications"));
                settings.setPushNotifications(rs.getBoolean("push_notifications"));
                settings.setProductUpdates(rs.getBoolean("product_updates"));
                settings.setMarketing(rs.getBoolean("marketing"));
                settings.setStoreInCloud(rs.getBoolean("store_in_cloud"));
                settings.setAllowAiTraining(rs.getBoolean("allow_ai_training"));
                settings.setShareUsageData(rs.getBoolean("share_usage_data"));
                return settings;
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to get settings for user " + userPin + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        // Return default settings if none found
        return new Settings(userPin);
    }

    public boolean saveSettings(Settings settings) {
        System.out.println("DEBUG: Saving settings for user " + settings.getUserPin());
        
        // Check if settings exist
        boolean exists = settingsExist(settings.getUserPin());
        System.out.println("DEBUG: Settings exist = " + exists);
        
        if (exists) {
            return updateSettings(settings);
        } else {
            return insertSettings(settings);
        }
    }

    private boolean settingsExist(int userPin) {
        String sql = "SELECT 1 FROM user_settings WHERE user_pin = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userPin);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to check if settings exist for user " + userPin + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean insertSettings(Settings settings) {
        String sql = "INSERT INTO user_settings (user_pin, theme, language, email_notifications, " +
                     "push_notifications, product_updates, marketing, store_in_cloud, " +
                     "allow_ai_training, share_usage_data) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, settings.getUserPin());
            pstmt.setString(2, settings.getTheme());
            pstmt.setString(3, settings.getLanguage());
            pstmt.setBoolean(4, settings.isEmailNotifications());
            pstmt.setBoolean(5, settings.isPushNotifications());
            pstmt.setBoolean(6, settings.isProductUpdates());
            pstmt.setBoolean(7, settings.isMarketing());
            pstmt.setBoolean(8, settings.isStoreInCloud());
            pstmt.setBoolean(9, settings.isAllowAiTraining());
            pstmt.setBoolean(10, settings.isShareUsageData());

            int result = pstmt.executeUpdate();
            System.out.println("DEBUG: Insert settings result = " + result);
            return result > 0;

        } catch (SQLException e) {
            System.err.println("ERROR: Failed to insert settings for user " + settings.getUserPin());
            System.err.println("SQL Error: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            e.printStackTrace();
            return false;
        }
    }

    private boolean updateSettings(Settings settings) {
        String sql = "UPDATE user_settings SET theme = ?, language = ?, email_notifications = ?, " +
                     "push_notifications = ?, product_updates = ?, marketing = ?, store_in_cloud = ?, " +
                     "allow_ai_training = ?, share_usage_data = ? WHERE user_pin = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, settings.getTheme());
            pstmt.setString(2, settings.getLanguage());
            pstmt.setBoolean(3, settings.isEmailNotifications());
            pstmt.setBoolean(4, settings.isPushNotifications());
            pstmt.setBoolean(5, settings.isProductUpdates());
            pstmt.setBoolean(6, settings.isMarketing());
            pstmt.setBoolean(7, settings.isStoreInCloud());
            pstmt.setBoolean(8, settings.isAllowAiTraining());
            pstmt.setBoolean(9, settings.isShareUsageData());
            pstmt.setInt(10, settings.getUserPin());
            
            int result = pstmt.executeUpdate();
            System.out.println("DEBUG: Update settings result = " + result);
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to update settings for user " + settings.getUserPin());
            System.err.println("SQL Error: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSettings(int userPin) {
        String sql = "DELETE FROM user_settings WHERE user_pin = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userPin);
            int result = pstmt.executeUpdate();
            System.out.println("DEBUG: Delete settings result = " + result);
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to delete settings for user " + userPin + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
