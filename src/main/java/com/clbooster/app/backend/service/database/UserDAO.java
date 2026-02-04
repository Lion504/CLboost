package com.clbooster.app.backend.service.database;

import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserDAO {

    // Hash password (SHA-256)
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean registerUser(User user) {
        String sql = "INSERT INTO identification (Identity_email, Username, Password, First_Name, Last_Name) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getIdentityEmail());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, hashPassword(user.getPassword()));
            pstmt.setString(4, user.getFirstName());
            pstmt.setString(5, user.getLastName());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // Get the generated Pin
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int pin = rs.getInt(1);
                    user.setPin(pin);

                    // Create empty profile entry for the user
                    createEmptyProfile(pin, user.getIdentityEmail());
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Create empty profile entry when user registers
    // Profile_Email is initially set to Identity_email
    private void createEmptyProfile(int pin, String identityEmail) {
        String sql = "INSERT INTO profile (Pin, Profile_Email) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pin);
            pstmt.setString(2, identityEmail);  // Initially same as identity email
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User loginUser(String username, String password) {
        String sql = "SELECT Pin, Identity_email, Username, Password, First_Name, Last_Name FROM identification WHERE Username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("Password");
                if (storedHash.equals(hashPassword(password))) {
                    User user = new User(username, password);
                    user.setPin(rs.getInt("Pin"));
                    user.setIdentityEmail(rs.getString("Identity_email"));
                    user.setUsername(rs.getString("Username"));
                    user.setPassword(password);
                    user.setFirstName(rs.getString("First_Name"));
                    user.setLastName(rs.getString("Last_Name"));
                    return user;
                }
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean usernameExists(String username) {
        String sql = "SELECT Pin FROM identification WHERE Username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean emailExists(String email) {
        String sql = "SELECT Pin FROM identification WHERE Identity_email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserByPin(int pin) {
        String sql = "SELECT Pin, Identity_email, Username, First_Name, Last_Name FROM identification WHERE Pin = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pin);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User(rs.getString("Username"), "");
                user.setPin(rs.getInt("Pin"));
                user.setIdentityEmail(rs.getString("Identity_email"));
                user.setFirstName(rs.getString("First_Name"));
                user.setLastName(rs.getString("Last_Name"));
                return user;
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean deleteUser(User user) {
        String sql = "DELETE FROM identification WHERE Pin = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, user.getPin());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}