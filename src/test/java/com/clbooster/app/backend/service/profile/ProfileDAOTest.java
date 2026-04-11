package com.clbooster.app.backend.service.profile;

import com.clbooster.app.backend.service.database.DatabaseConnection;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileDAOTest {

    // ---------------- helpers ----------------

    private Connection mockConnection(PreparedStatement stmt) throws Exception {
        Connection conn = mock(Connection.class);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        return conn;
    }

    private MockedStatic<DatabaseConnection> mockDB(Connection conn) {
        MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class);
        db.when(DatabaseConnection::getConnection).thenReturn(conn);
        return db;
    }

    // ---------------- getProfileByPin ----------------

    @Test
    void getProfileByPin_found() throws Exception {
        ProfileDAO dao = new ProfileDAO();

        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        Connection conn = mockConnection(stmt);

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {

            when(stmt.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(true);

            when(rs.getInt("Pin")).thenReturn(123);
            when(rs.getString(anyString())).thenReturn("value");
            when(rs.getTimestamp("CV_Last_Updated")).thenReturn(new Timestamp(System.currentTimeMillis()));

            Profile result = dao.getProfileByPin(123);

            assertNotNull(result);
            assertEquals(123, result.getPin());

            verify(stmt).setInt(1, 123);
        }
    }

    @Test
    void getProfileByPin_notFound() throws Exception {
        ProfileDAO dao = new ProfileDAO();

        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        Connection conn = mockConnection(stmt);

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {

            when(stmt.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(false);

            assertNull(dao.getProfileByPin(999));
        }
    }

    @Test
    void getProfileByPin_sqlException_returnsNull() throws Exception {
        ProfileDAO dao = new ProfileDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenThrow(new SQLException("DB error"));

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            assertNull(dao.getProfileByPin(1));
        }
    }

    // ---------------- profileExists ----------------

    @Test
    void profileExists_true() throws Exception {
        ProfileDAO dao = new ProfileDAO();

        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        Connection conn = mockConnection(stmt);

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {

            when(stmt.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(true);

            assertTrue(dao.profileExists(1));
            verify(stmt).setInt(1, 1);
        }
    }

    @Test
    void profileExists_sqlException_false() throws Exception {
        ProfileDAO dao = new ProfileDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenThrow(new SQLException("fail"));

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            assertFalse(dao.profileExists(1));
        }
    }

    // ---------------- updateProfile ----------------

    @Test
    void updateProfile_success() throws Exception {
        ProfileDAO dao = new ProfileDAO();

        Profile p = new Profile();
        p.setPin(1);
        p.setExperienceLevel("Senior");
        p.setTools("Java");
        p.setSkills("Backend");
        p.setLink("link");
        p.setProfileEmail("test@mail.com");

        PreparedStatement stmt = mock(PreparedStatement.class);
        Connection conn = mockConnection(stmt);

        when(stmt.executeUpdate()).thenReturn(1);

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {

            assertTrue(dao.updateProfile(p));

            verify(stmt).setString(1, "Senior");
            verify(stmt).setString(2, "Java");
            verify(stmt).setString(3, "Backend");
            verify(stmt).setString(4, "link");
            verify(stmt).setString(5, "test@mail.com");
            verify(stmt).setInt(6, 1);
        }
    }

    @Test
    void updateProfile_failure() throws Exception {
        ProfileDAO dao = new ProfileDAO();

        Profile p = new Profile();
        p.setPin(1);

        PreparedStatement stmt = mock(PreparedStatement.class);
        Connection conn = mockConnection(stmt);

        when(stmt.executeUpdate()).thenReturn(0);

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            assertFalse(dao.updateProfile(p));
        }
    }

    @Test
    void updateProfile_sqlException() throws Exception {
        ProfileDAO dao = new ProfileDAO();

        Profile p = new Profile();
        p.setPin(1);

        PreparedStatement stmt = mock(PreparedStatement.class);
        Connection conn = mockConnection(stmt);

        when(stmt.executeUpdate()).thenThrow(new SQLException());

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            assertFalse(dao.updateProfile(p));
        }
    }

    // ---------------- updateCVTimestamp ----------------

    @Test
    void updateCVTimestamp_success() throws Exception {
        ProfileDAO dao = new ProfileDAO();

        PreparedStatement stmt = mock(PreparedStatement.class);
        Connection conn = mockConnection(stmt);

        when(stmt.executeUpdate()).thenReturn(1);

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            assertTrue(dao.updateCVTimestamp(1));
            verify(stmt).setInt(1, 1);
        }
    }

    @Test
    void updateCVTimestamp_sqlException() throws Exception {
        ProfileDAO dao = new ProfileDAO();

        PreparedStatement stmt = mock(PreparedStatement.class);
        Connection conn = mockConnection(stmt);

        when(stmt.executeUpdate()).thenThrow(new SQLException());

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            assertFalse(dao.updateCVTimestamp(1));
        }
    }
}