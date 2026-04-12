package com.clbooster.app.backend.service.profile;

import com.clbooster.app.backend.service.database.DatabaseConnection;
import com.clbooster.app.backend.util.LocaleFallbackResolver;
import com.clbooster.app.backend.util.Utf8Validator;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    // ---------------- getById ----------------

    @Test
    void getById_baseNull_returnsNull() {
        ProfileDAO dao = spy(new ProfileDAO());
        doReturn(null).when(dao).getProfileByPin(10);

        assertNull(dao.getById(10, Locale.US));
    }

    @Test
    void getById_nullLocale_returnsBaseWithoutTranslationLookup() {
        ProfileDAO dao = spy(new ProfileDAO());
        Profile base = new Profile();
        base.setPin(11);
        doReturn(base).when(dao).getProfileByPin(11);

        Profile result = dao.getById(11, null);

        assertSame(base, result);
    }

    @Test
    void getById_translationFound_overridesLocalizedFields() throws Exception {
        ProfileDAO dao = spy(new ProfileDAO());
        Profile base = new Profile();
        base.setPin(12);
        base.setExperienceLevel("BaseExp");
        base.setTools("BaseTools");
        base.setSkills("BaseSkills");
        doReturn(base).when(dao).getProfileByPin(12);

        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        Connection conn = mockConnection(stmt);

        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString("experience_level")).thenReturn("LocalizedExp");
        when(rs.getString("tools")).thenReturn("LocalizedTools");
        when(rs.getString("skills")).thenReturn("LocalizedSkills");

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            Profile result = dao.getById(12, Locale.US);

            assertEquals("LocalizedExp", result.getExperienceLevel());
            assertEquals("LocalizedTools", result.getTools());
            assertEquals("LocalizedSkills", result.getSkills());
        }
    }

    @Test
    void getById_translationMissing_keepsBaseFields() throws Exception {
        ProfileDAO dao = spy(new ProfileDAO());
        Profile base = new Profile();
        base.setPin(13);
        base.setExperienceLevel("BaseExp");
        base.setTools("BaseTools");
        base.setSkills("BaseSkills");
        doReturn(base).when(dao).getProfileByPin(13);

        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        Connection conn = mockConnection(stmt);

        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            Profile result = dao.getById(13, Locale.US);

            assertEquals("BaseExp", result.getExperienceLevel());
            assertEquals("BaseTools", result.getTools());
            assertEquals("BaseSkills", result.getSkills());
        }
    }

    @Test
    void getById_sqlException_returnsBaseProfile() throws Exception {
        ProfileDAO dao = spy(new ProfileDAO());
        Profile base = new Profile();
        base.setPin(14);
        doReturn(base).when(dao).getProfileByPin(14);

        PreparedStatement stmt = mock(PreparedStatement.class);
        Connection conn = mockConnection(stmt);
        when(stmt.executeQuery()).thenThrow(new SQLException("translation query failed"));

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            Profile result = dao.getById(14, Locale.US);
            assertSame(base, result);
        }
    }

    // ---------------- getByIdWithFallback ----------------

    @Test
    void getByIdWithFallback_usesFirstMatchingLocale() {
        ProfileDAO dao = spy(new ProfileDAO());
        Profile translated = new Profile();
        translated.setPin(15);

        doReturn(true).when(dao).hasTranslation(15, Locale.US);
        doReturn(translated).when(dao).getById(15, Locale.US);

        Profile result = dao.getByIdWithFallback(15, Locale.US, Locale.US);
        assertSame(translated, result);
    }

    @Test
    void getByIdWithFallback_noTranslations_returnsBaseProfile() {
        ProfileDAO dao = spy(new ProfileDAO());
        Profile base = new Profile();
        base.setPin(16);

        doReturn(false).when(dao).hasTranslation(eq(16), any(Locale.class));
        doReturn(base).when(dao).getProfileByPin(16);

        Profile result = dao.getByIdWithFallback(16, Locale.US, Locale.US);
        assertSame(base, result);
    }

    @Test
    void getByIdWithFallback_nullPreferred_usesDefaultChain() {
        ProfileDAO dao = spy(new ProfileDAO());
        Profile translated = new Profile();
        translated.setPin(17);

        Locale defaultLocale = LocaleFallbackResolver.getDefault();
        doReturn(true).when(dao).hasTranslation(17, defaultLocale);
        doReturn(translated).when(dao).getById(17, defaultLocale);

        Profile result = dao.getByIdWithFallback(17, null, Locale.US);
        assertSame(translated, result);
    }

    // ---------------- saveTranslation ----------------

    @Test
    void saveTranslation_invalidUtf8_throwsIllegalArgumentException() {
        ProfileDAO dao = new ProfileDAO();
        Profile profile = new Profile(1, "exp", "tools", "skills", "link", "mail@test.com");

        try (MockedStatic<Utf8Validator> utf8 = mockStatic(Utf8Validator.class)) {
            utf8.when(() -> Utf8Validator.isValidUtf8(anyString())).thenReturn(false);

            assertThrows(IllegalArgumentException.class, () -> dao.saveTranslation(profile, Locale.US));
        }
    }

    @Test
    void saveTranslation_success_updatesBaseProfileToo() throws Exception {
        ProfileDAO dao = spy(new ProfileDAO());
        Profile profile = new Profile(18, "Senior", "Java", "Testing", "link", "mail@test.com");

        doReturn(true).when(dao).updateProfile(any(Profile.class));

        PreparedStatement stmt = mock(PreparedStatement.class);
        Connection conn = mockConnection(stmt);
        when(stmt.executeUpdate()).thenReturn(1);

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            dao.saveTranslation(profile, Locale.US);

            verify(stmt).setInt(1, 18);
            verify(stmt).executeUpdate();
            verify(dao).updateProfile(profile);
        }
    }

    @Test
    void saveTranslation_sqlException_doesNotThrow() throws Exception {
        ProfileDAO dao = new ProfileDAO();
        Profile profile = new Profile(19, "Senior", "Java", "Testing", "link", "mail@test.com");

        PreparedStatement stmt = mock(PreparedStatement.class);
        Connection conn = mockConnection(stmt);
        when(stmt.executeUpdate()).thenThrow(new SQLException("write failed"));

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            assertDoesNotThrow(() -> dao.saveTranslation(profile, Locale.US));
        }
    }

    // ---------------- getAvailableLocales ----------------

    @Test
    void getAvailableLocales_success_parsesLocaleCodes() throws Exception {
        ProfileDAO dao = new ProfileDAO();

        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        Connection conn = mockConnection(stmt);

        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, true, false);
        when(rs.getString("locale_code")).thenReturn("en_US", "fi_FI");

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            List<Locale> locales = dao.getAvailableLocales(20);
            assertEquals(2, locales.size());
            assertEquals(Locale.forLanguageTag("en-US"), locales.get(0));
            assertEquals(Locale.forLanguageTag("fi-FI"), locales.get(1));
        }
    }

    @Test
    void getAvailableLocales_sqlException_returnsEmptyList() throws Exception {
        ProfileDAO dao = new ProfileDAO();

        PreparedStatement stmt = mock(PreparedStatement.class);
        Connection conn = mockConnection(stmt);
        when(stmt.executeQuery()).thenThrow(new SQLException("locale query failed"));

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            List<Locale> locales = dao.getAvailableLocales(21);
            assertTrue(locales.isEmpty());
        }
    }

    // ---------------- hasTranslation ----------------

    @Test
    void hasTranslation_trueWhenRowExists() throws Exception {
        ProfileDAO dao = new ProfileDAO();

        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        Connection conn = mockConnection(stmt);

        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            assertTrue(dao.hasTranslation(22, Locale.US));
        }
    }

    @Test
    void hasTranslation_falseWhenSqlException() throws Exception {
        ProfileDAO dao = new ProfileDAO();

        PreparedStatement stmt = mock(PreparedStatement.class);
        Connection conn = mockConnection(stmt);
        when(stmt.executeQuery()).thenThrow(new SQLException("hasTranslation failed"));

        try (MockedStatic<DatabaseConnection> db = mockDB(conn)) {
            assertFalse(dao.hasTranslation(23, Locale.US));
        }
    }
}