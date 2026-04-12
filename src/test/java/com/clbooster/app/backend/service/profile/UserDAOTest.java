package com.clbooster.app.backend.service.profile;

import com.clbooster.app.backend.service.database.DatabaseConnection;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDAOTest {

    // ---------------- helpers ----------------

    private MockedStatic<DatabaseConnection> mockDB(Connection conn) {
        MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class);
        db.when(DatabaseConnection::getConnection).thenReturn(conn);
        return db;
    }

    // ---------------- registerUser ----------------

    @Test
    void registerUser_success() throws Exception {
        UserDAO dao = new UserDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement insertUser = mock(PreparedStatement.class);
        PreparedStatement insertProfile = mock(PreparedStatement.class);
        ResultSet keys = mock(ResultSet.class);

        when(conn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(insertUser);

        when(conn.prepareStatement(anyString())).thenReturn(insertProfile);

        when(insertUser.executeUpdate()).thenReturn(1);
        when(insertUser.getGeneratedKeys()).thenReturn(keys);
        when(keys.next()).thenReturn(true);
        when(keys.getInt(1)).thenReturn(42);

        when(insertProfile.executeUpdate()).thenReturn(1);

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {

            User user = new User("john", "password");
            user.setIdentityEmail("john@test.com");
            user.setFirstName("John");
            user.setLastName("Doe");

            boolean result = dao.registerUser(user);

            assertTrue(result);
            assertEquals(42, user.getPin());

            verify(insertUser).setString(eq(2), eq("john"));
            verify(insertUser).setString(eq(1), eq("john@test.com"));
        }
    }

    @Test
    void registerUser_sqlException_returnsFalse() throws Exception {
        UserDAO dao = new UserDAO();

        Connection conn = mock(Connection.class);

        when(conn.prepareStatement(anyString(), anyInt())).thenThrow(new SQLException());

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            assertFalse(dao.registerUser(new User("a", "b")));
        }
    }

    // ---------------- loginUser ----------------

    @Test
    void loginUser_success() throws Exception {
        UserDAO dao = new UserDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getString("Password")).thenReturn("5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8"); // "password"

        when(rs.getInt("Pin")).thenReturn(1);
        when(rs.getString("Username")).thenReturn("john");
        when(rs.getString("Identity_email")).thenReturn("john@test.com");
        when(rs.getString("First_Name")).thenReturn("John");
        when(rs.getString("Last_Name")).thenReturn("Doe");

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {

            User result = dao.loginUser("john", "password");

            assertNotNull(result);
            assertEquals("john", result.getUsername());
        }
    }

    @Test
    void loginUser_wrongPassword_returnsNull() throws Exception {
        UserDAO dao = new UserDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getString("Password")).thenReturn("WRONG_HASH");

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            assertNull(dao.loginUser("john", "password"));
        }
    }

    @Test
    void loginUser_notFound_returnsNull() throws Exception {
        UserDAO dao = new UserDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            assertNull(dao.loginUser("x", "y"));
        }
    }

    // ---------------- usernameExists ----------------

    @Test
    void usernameExists_true() throws Exception {
        UserDAO dao = new UserDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            assertTrue(dao.usernameExists("john"));
        }
    }

    @Test
    void usernameExists_false() throws Exception {
        UserDAO dao = new UserDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            assertFalse(dao.usernameExists("john"));
        }
    }

    // ---------------- emailExists ----------------

    @Test
    void emailExists_true() throws Exception {
        UserDAO dao = new UserDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            assertTrue(dao.emailExists("a@b.com"));
        }
    }

    // ---------------- getUserByPin ----------------

    @Test
    void getUserByPin_found() throws Exception {
        UserDAO dao = new UserDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getString("Username")).thenReturn("john");
        when(rs.getInt("Pin")).thenReturn(1);
        when(rs.getString("Identity_email")).thenReturn("a@b.com");
        when(rs.getString("First_Name")).thenReturn("John");
        when(rs.getString("Last_Name")).thenReturn("Doe");

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            User u = dao.getUserByPin(1);
            assertNotNull(u);
            assertEquals("john", u.getUsername());
        }
    }

    // ---------------- deleteUser ----------------

    @Test
    void deleteUser_success() throws Exception {
        UserDAO dao = new UserDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeUpdate()).thenReturn(1);

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            User u = new User("x", "y");
            u.setPin(1);

            assertTrue(dao.deleteUser(u));
        }
    }

    // ---------------- updatePassword ----------------

    @Test
    void updatePassword_success() throws Exception {
        UserDAO dao = new UserDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeUpdate()).thenReturn(1);

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            assertTrue(dao.updatePassword(1, "newpass"));
        }
    }
}