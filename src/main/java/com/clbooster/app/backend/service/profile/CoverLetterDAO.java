package com.clbooster.app.backend.service.profile;

import com.clbooster.app.backend.service.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoverLetterDAO {

    // Returns generated id, or -1 on failure
    public int addCoverLetter(int pin, String filePath) {
        String sql = "INSERT INTO coverletter (Pin, FilePath) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, pin);
            pstmt.setString(2, filePath);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next())
                return rs.getInt(1);
            return -1;

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public CoverLetter getCoverLetterById(int id) {
        String sql = "SELECT id, Pin, Timestamp_edited, FilePath FROM coverletter WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                return mapRow(rs);
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<CoverLetter> getCoverLettersByPin(int pin) {
        String sql = "SELECT id, Pin, Timestamp_edited, FilePath FROM coverletter WHERE Pin = ? ORDER BY Timestamp_edited DESC";
        List<CoverLetter> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pin);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
                list.add(mapRow(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Timestamp_edited auto-updates via ON UPDATE CURRENT_TIMESTAMP
    public boolean updateFilePath(int id, String newFilePath) {
        String sql = "UPDATE coverletter SET FilePath = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newFilePath);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCoverLetter(int id) {
        String sql = "DELETE FROM coverletter WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAllByPin(int pin) {
        String sql = "DELETE FROM coverletter WHERE Pin = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pin);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private CoverLetter mapRow(ResultSet rs) throws SQLException {
        CoverLetter cl = new CoverLetter();
        cl.setId(rs.getInt("id"));
        cl.setPin(rs.getInt("Pin"));
        cl.setTimestampEdited(rs.getTimestamp("Timestamp_edited"));
        cl.setFilePath(rs.getString("FilePath"));
        return cl;
    }
}