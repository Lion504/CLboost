package com.clbooster.app.backend.service.profile;

import com.clbooster.app.backend.service.database.DatabaseConnection;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CoverLetterDAOTest {

    @Test
    void addCoverLetter_success_returnsGeneratedId() throws Exception {
        CoverLetterDAO dao = new CoverLetterDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        try (MockedStatic<DatabaseConnection> dbMock = mockStatic(DatabaseConnection.class)) {

            dbMock.when(DatabaseConnection::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(stmt);
            when(stmt.executeUpdate()).thenReturn(1);
            when(stmt.getGeneratedKeys()).thenReturn(rs);
            when(rs.next()).thenReturn(true);
            when(rs.getInt(1)).thenReturn(42);

            int id = dao.addCoverLetter(123, "/file.pdf");

            assertEquals(42, id);
        }
    }

    @Test
    void addCoverLetter_failure_returnsMinusOne() throws Exception {
        CoverLetterDAO dao = new CoverLetterDAO();

        Connection conn = mock(Connection.class);

        try (MockedStatic<DatabaseConnection> dbMock = mockStatic(DatabaseConnection.class)) {

            dbMock.when(DatabaseConnection::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString(), anyInt())).thenThrow(new SQLException());

            int id = dao.addCoverLetter(123, "/file.pdf");

            assertEquals(-1, id);
        }
    }

    @Test
    void getCoverLetterById_found() throws Exception {
        CoverLetterDAO dao = new CoverLetterDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        Timestamp ts = new Timestamp(System.currentTimeMillis());

        try (MockedStatic<DatabaseConnection> dbMock = mockStatic(DatabaseConnection.class)) {

            dbMock.when(DatabaseConnection::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(true);

            when(rs.getInt("id")).thenReturn(1);
            when(rs.getInt("Pin")).thenReturn(123);
            when(rs.getTimestamp("Timestamp_edited")).thenReturn(ts);
            when(rs.getString("FilePath")).thenReturn("/file.pdf");

            CoverLetter cl = dao.getCoverLetterById(1);

            assertNotNull(cl);
            assertEquals(1, cl.getId());
            assertEquals(123, cl.getPin());
            assertEquals("/file.pdf", cl.getFilePath());
            assertEquals(ts, cl.getTimestampEdited());
        }
    }

    @Test
    void getCoverLetterById_notFound() throws Exception {
        CoverLetterDAO dao = new CoverLetterDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        try (MockedStatic<DatabaseConnection> dbMock = mockStatic(DatabaseConnection.class)) {

            dbMock.when(DatabaseConnection::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(false);

            CoverLetter cl = dao.getCoverLetterById(999);

            assertNull(cl);
        }
    }

    @Test
    void getCoverLettersByPin_returnsList() throws Exception {
        CoverLetterDAO dao = new CoverLetterDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        try (MockedStatic<DatabaseConnection> dbMock = mockStatic(DatabaseConnection.class)) {

            dbMock.when(DatabaseConnection::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeQuery()).thenReturn(rs);

            when(rs.next()).thenReturn(true, true, false);
            when(rs.getInt("id")).thenReturn(1, 2);
            when(rs.getInt("Pin")).thenReturn(123, 123);
            when(rs.getTimestamp("Timestamp_edited")).thenReturn(new Timestamp(System.currentTimeMillis()));
            when(rs.getString("FilePath")).thenReturn("/a.pdf", "/b.pdf");

            List<CoverLetter> list = dao.getCoverLettersByPin(123);

            assertEquals(2, list.size());
        }
    }

    @Test
    void updateFilePath_success() throws Exception {
        CoverLetterDAO dao = new CoverLetterDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);

        try (MockedStatic<DatabaseConnection> dbMock = mockStatic(DatabaseConnection.class)) {

            dbMock.when(DatabaseConnection::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeUpdate()).thenReturn(1);

            assertTrue(dao.updateFilePath(1, "/new.pdf"));
        }
    }

    @Test
    void updateFilePath_failure() throws Exception {
        CoverLetterDAO dao = new CoverLetterDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);

        try (MockedStatic<DatabaseConnection> dbMock = mockStatic(DatabaseConnection.class)) {

            dbMock.when(DatabaseConnection::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeUpdate()).thenReturn(0);

            assertFalse(dao.updateFilePath(1, "/new.pdf"));
        }
    }

    @Test
    void deleteCoverLetter_success() throws Exception {
        CoverLetterDAO dao = new CoverLetterDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);

        try (MockedStatic<DatabaseConnection> dbMock = mockStatic(DatabaseConnection.class)) {

            dbMock.when(DatabaseConnection::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeUpdate()).thenReturn(1);

            assertTrue(dao.deleteCoverLetter(1));
        }
    }

    @Test
    void deleteAllByPin_success() throws Exception {
        CoverLetterDAO dao = new CoverLetterDAO();

        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);

        try (MockedStatic<DatabaseConnection> dbMock = mockStatic(DatabaseConnection.class)) {

            dbMock.when(DatabaseConnection::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeUpdate()).thenReturn(2);

            assertTrue(dao.deleteAllByPin(123));
        }
    }
}