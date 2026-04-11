package com.clbooster.app.backend.service.profile;

import com.clbooster.app.backend.service.database.DatabaseConnection;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CoverLetterDAOTest {

    // ---------------- helpers ----------------

    private MockedStatic<DatabaseConnection> mockDB(Connection conn) {
        MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class);
        db.when(DatabaseConnection::getConnection).thenReturn(conn);
        return db;
    }

    // ---------------- addCoverLetter ----------------

    @Test
    void addCoverLetter_success() throws Exception {
        CoverLetterDAO dao = new CoverLetterDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(stmt);

        when(stmt.getGeneratedKeys()).thenReturn(rs);
        when(stmt.executeUpdate()).thenReturn(1);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(42);

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {

            int id = dao.addCoverLetter(123, "/file.pdf");

            assertEquals(42, id);

            verify(stmt).setInt(1, 123);
            verify(stmt).setString(2, "/file.pdf");
        }
    }

    @Test
    void addCoverLetter_sqlException_returnsMinusOne() throws Exception {
        CoverLetterDAO dao = new CoverLetterDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString(), anyInt()))
                .thenThrow(new SQLException());

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            assertEquals(-1, dao.addCoverLetter(1, "/x.pdf"));
        }
    }

    // ---------------- getCoverLetterById ----------------

    @Test
    void getCoverLetterById_found() throws Exception {
        CoverLetterDAO dao = new CoverLetterDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        Timestamp ts = new Timestamp(System.currentTimeMillis());

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        when(rs.getInt("id")).thenReturn(1);
        when(rs.getInt("Pin")).thenReturn(123);
        when(rs.getTimestamp("Timestamp_edited")).thenReturn(ts);
        when(rs.getString("FilePath")).thenReturn("/file.pdf");

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {

            CoverLetter cl = dao.getCoverLetterById(1);

            assertNotNull(cl);
            assertEquals(1, cl.getId());
            assertEquals(123, cl.getPin());
            assertEquals("/file.pdf", cl.getFilePath());
            assertEquals(ts, cl.getTimestampEdited());

            verify(stmt).setInt(1, 1);
        }
    }

    @Test
    void getCoverLetterById_sqlException_returnsNull() throws Exception {
        CoverLetterDAO dao = new CoverLetterDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenThrow(new SQLException());

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            assertNull(dao.getCoverLetterById(1));
        }
    }

    // ---------------- getCoverLettersByPin ----------------

    @Test
    void getCoverLettersByPin_success() throws Exception {
        CoverLetterDAO dao = new CoverLetterDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, true, false);
        when(rs.getInt("id")).thenReturn(1, 2);
        when(rs.getInt("Pin")).thenReturn(123, 123);
        when(rs.getTimestamp("Timestamp_edited")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(rs.getString("FilePath")).thenReturn("/a.pdf", "/b.pdf");

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {

            List<CoverLetter> list = dao.getCoverLettersByPin(123);

            assertEquals(2, list.size());
            verify(stmt).setInt(1, 123);
        }
    }

    @Test
    void getCoverLettersByPin_sqlException_returnsEmptyList() throws Exception {
        CoverLetterDAO dao = new CoverLetterDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenThrow(new SQLException());

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            List<CoverLetter> list = dao.getCoverLettersByPin(1);
            assertTrue(list.isEmpty());
        }
    }

    // ---------------- updateFilePath ----------------

    @Test
    void updateFilePath_sqlException_returnsFalse() throws Exception {
        CoverLetterDAO dao = new CoverLetterDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenThrow(new SQLException());

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            assertFalse(dao.updateFilePath(1, "/x.pdf"));
        }
    }

    // ---------------- deleteCoverLetter ----------------

    @Test
    void deleteCoverLetter_sqlException_returnsFalse() throws Exception {
        CoverLetterDAO dao = new CoverLetterDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenThrow(new SQLException());

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            assertFalse(dao.deleteCoverLetter(1));
        }
    }

    // ---------------- deleteAllByPin ----------------

    @Test
    void deleteAllByPin_sqlException_returnsFalse() throws Exception {
        CoverLetterDAO dao = new CoverLetterDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenThrow(new SQLException());

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            assertFalse(dao.deleteAllByPin(1));
        }
    }
}