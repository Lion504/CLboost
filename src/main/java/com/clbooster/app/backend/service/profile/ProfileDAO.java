package com.clbooster.app.backend.service.profile;

import com.clbooster.app.backend.service.database.DatabaseConnection;

import java.sql.*;

public class ProfileDAO {

    public Profile getProfileByPin(int pin) {
        String sql = "SELECT Pin, Experience_Level, Tools, Skills, Link, Profile_Email, CV_Last_Updated FROM profile WHERE Pin = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pin);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Profile profile = new Profile();
                profile.setPin(rs.getInt("Pin"));
                profile.setExperienceLevel(rs.getString("Experience_Level"));
                profile.setTools(rs.getString("Tools"));
                profile.setSkills(rs.getString("Skills"));
                profile.setLink(rs.getString("Link"));
                profile.setProfileEmail(rs.getString("Profile_Email"));
                profile.setCvLastUpdated(rs.getTimestamp("CV_Last_Updated"));
                return profile;
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateProfile(Profile profile) {
        String sql = "UPDATE profile SET Experience_Level = ?, Tools = ?, Skills = ?, Link = ?, Profile_Email = ? WHERE Pin = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, profile.getExperienceLevel());
            pstmt.setString(2, profile.getTools());
            pstmt.setString(3, profile.getSkills());
            pstmt.setString(4, profile.getLink());
            pstmt.setString(5, profile.getProfileEmail());
            pstmt.setInt(6, profile.getPin());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // CV last updated timestamp
    public boolean updateCVTimestamp(int pin) {
        String sql = "UPDATE profile SET CV_Last_Updated = CURRENT_TIMESTAMP WHERE Pin = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pin);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean profileExists(int pin) {
        String sql = "SELECT Pin FROM profile WHERE Pin = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pin);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}