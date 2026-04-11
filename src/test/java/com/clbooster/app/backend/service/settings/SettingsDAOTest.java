package com.clbooster.app.backend.service.settings;

import com.clbooster.app.backend.service.database.DatabaseConnection;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SettingsDAOTest {

    private MockedStatic<DatabaseConnection> mockDB(Connection conn) {
        MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class);
        db.when(DatabaseConnection::getConnection).thenReturn(conn);
        return db;
    }

    // ---------------- getSettings ----------------

    @Test
    void getSettings_found() throws Exception {
        SettingsDAO dao = new SettingsDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getString("theme")).thenReturn("dark");
        when(rs.getString("language")).thenReturn("EN");
        when(rs.getBoolean(anyString())).thenReturn(true);

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {

            Settings s = dao.getSettings(1);

            assertNotNull(s);
            assertEquals(1, s.getUserPin());
            assertEquals("dark", s.getTheme());

            verify(stmt).setInt(1, 1);
        }
    }

    @Test
    void getSettings_notFound_returnsDefault() throws Exception {
        SettingsDAO dao = new SettingsDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            Settings s = dao.getSettings(1);
            assertNotNull(s);
            assertEquals(1, s.getUserPin());
        }
    }

    // ---------------- saveSettings INSERT branch ----------------

    @Test
    void saveSettings_insertBranch() throws Exception {
        SettingsDAO dao = new SettingsDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement existsStmt = mock(PreparedStatement.class);
        PreparedStatement insertStmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(contains("SELECT 1"))).thenReturn(existsStmt);
        when(conn.prepareStatement(contains("INSERT"))).thenReturn(insertStmt);

        when(existsStmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false); // forces INSERT branch

        when(insertStmt.executeUpdate()).thenReturn(1);

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {

            Settings s = new Settings(1);
            s.setTheme("dark");

            boolean result = dao.saveSettings(s);

            assertTrue(result);
            verify(insertStmt).setInt(eq(1), eq(1));
        }
    }

    // ---------------- saveSettings UPDATE branch ----------------

    @Test
    void saveSettings_updateBranch() throws Exception {
        SettingsDAO dao = new SettingsDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement existsStmt = mock(PreparedStatement.class);
        PreparedStatement updateStmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(contains("SELECT 1"))).thenReturn(existsStmt);
        when(conn.prepareStatement(contains("UPDATE"))).thenReturn(updateStmt);

        when(existsStmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true); // forces UPDATE branch

        when(updateStmt.executeUpdate()).thenReturn(1);

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {

            Settings s = new Settings(1);
            s.setTheme("light");

            boolean result = dao.saveSettings(s);

            assertTrue(result);
            verify(updateStmt).setInt(eq(10), eq(1));
        }
    }

    // ---------------- settingsExist failure ----------------

    @Test
    void settingsExist_sqlFailure_returnsFalse() throws Exception {
        SettingsDAO dao = new SettingsDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenThrow(new SQLException());

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            Settings s = new Settings(1);
            assertFalse(dao.saveSettings(s)); // falls into INSERT path but still safe
        }
    }

    // ---------------- deleteSettings ----------------

    @Test
    void deleteSettings_success() throws Exception {
        SettingsDAO dao = new SettingsDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeUpdate()).thenReturn(1);

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            assertTrue(dao.deleteSettings(1));
            verify(stmt).setInt(1, 1);
        }
    }

    @Test
    void deleteSettings_failure() throws Exception {
        SettingsDAO dao = new SettingsDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeUpdate()).thenReturn(0);

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            assertFalse(dao.deleteSettings(1));
        }
    }
}