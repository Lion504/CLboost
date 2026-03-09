package com.clbooster.app.backend.service.profile;

import com.clbooster.app.backend.service.database.DatabaseConnection;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDAOTest {

    @Test
    void registerUser_success() throws Exception {
        UserDAO dao = new UserDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement insertUserStmt = mock(PreparedStatement.class);
        PreparedStatement insertProfileStmt = mock(PreparedStatement.class);
        ResultSet keys = mock(ResultSet.class);

        // FIRST INSERT (identification)
        when(conn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(insertUserStmt);

        when(insertUserStmt.executeUpdate()).thenReturn(1);
        when(insertUserStmt.getGeneratedKeys()).thenReturn(keys);
        when(keys.next()).thenReturn(true);
        when(keys.getInt(1)).thenReturn(42);

        // SECOND INSERT (profile)
        when(conn.prepareStatement(anyString())).thenReturn(insertProfileStmt);

        when(insertProfileStmt.executeUpdate()).thenReturn(1);

        try (MockedStatic<DatabaseConnection> mocked = mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(conn);

            User user = new User("john", "password");
            user.setIdentityEmail("john@test.com");
            user.setFirstName("John");
            user.setLastName("Doe");

            boolean result = dao.registerUser(user);

            assertTrue(result);
            assertEquals(42, user.getPin());
        }
    }

    @Test
    void loginUser_success() throws Exception {
        UserDAO dao = new UserDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getString("Password")).thenReturn("5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8"); // hash
                                                                                                                       // of
                                                                                                                       // "password"
        when(rs.getInt("Pin")).thenReturn(1);
        when(rs.getString("Identity_email")).thenReturn("john@test.com");
        when(rs.getString("Username")).thenReturn("john");
        when(rs.getString("First_Name")).thenReturn("John");
        when(rs.getString("Last_Name")).thenReturn("Doe");

        try (MockedStatic<DatabaseConnection> mocked = mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(conn);

            User user = dao.loginUser("john", "password");

            assertNotNull(user);
            assertEquals("john", user.getUsername());
        }
    }

    @Test
    void usernameExists_true() throws Exception {
        UserDAO dao = new UserDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        try (MockedStatic<DatabaseConnection> mocked = mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(conn);

            assertTrue(dao.usernameExists("john"));
        }
    }
}
