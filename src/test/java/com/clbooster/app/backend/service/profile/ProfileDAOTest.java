package com.clbooster.app.backend.service.profile;

import static org.junit.jupiter.api.Assertions.*;

import com.clbooster.app.backend.service.database.DatabaseConnection;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileDAOTest {

    @Test
    void getProfileByPin_found() throws Exception {
        ProfileDAO dao = new ProfileDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        try (MockedStatic<DatabaseConnection> dbMock = mockStatic(DatabaseConnection.class)) {

            dbMock.when(DatabaseConnection::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(true);

            when(rs.getInt("Pin")).thenReturn(123);
            when(rs.getString("Experience_Level")).thenReturn("Senior");
            when(rs.getString("Tools")).thenReturn("Java");
            when(rs.getString("Skills")).thenReturn("Backend");
            when(rs.getString("Link")).thenReturn("linkedin");
            when(rs.getString("Profile_Email")).thenReturn("test@mail.com");
            when(rs.getTimestamp("CV_Last_Updated")).thenReturn(new Timestamp(System.currentTimeMillis()));

            Profile profile = dao.getProfileByPin(123);

            assertNotNull(profile);
            assertEquals(123, profile.getPin());
            assertEquals("Senior", profile.getExperienceLevel());
        }
    }

    @Test
    void getProfileByPin_notFound() throws Exception {
        ProfileDAO dao = new ProfileDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        try (MockedStatic<DatabaseConnection> dbMock = mockStatic(DatabaseConnection.class)) {

            dbMock.when(DatabaseConnection::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(false);

            Profile profile = dao.getProfileByPin(999);

            assertNull(profile);
        }
    }

    @Test
    void profileExists_true() throws Exception {
        ProfileDAO dao = new ProfileDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        try (MockedStatic<DatabaseConnection> dbMock = mockStatic(DatabaseConnection.class)) {

            dbMock.when(DatabaseConnection::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(true);

            assertTrue(dao.profileExists(1));
        }
    }

    @Test
    void updateProfile_success() throws Exception {
        ProfileDAO dao = new ProfileDAO();
        Profile profile = new Profile();
        profile.setPin(1);

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);

        try (MockedStatic<DatabaseConnection> dbMock = mockStatic(DatabaseConnection.class)) {

            dbMock.when(DatabaseConnection::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeUpdate()).thenReturn(1);

            assertTrue(dao.updateProfile(profile));
        }
    }

    @Test
    void updateCVTimestamp_success() throws Exception {
        ProfileDAO dao = new ProfileDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);

        try (MockedStatic<DatabaseConnection> dbMock = mockStatic(DatabaseConnection.class)) {

            dbMock.when(DatabaseConnection::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeUpdate()).thenReturn(1);

            assertTrue(dao.updateCVTimestamp(1));
        }
    }
}