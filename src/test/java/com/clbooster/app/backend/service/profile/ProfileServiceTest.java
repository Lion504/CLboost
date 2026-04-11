package com.clbooster.app.backend.service.profile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileServiceTest {

    private ProfileService service;
    private ProfileDAO dao;

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() throws Exception {
        service = new ProfileService();
        dao = mock(ProfileDAO.class);

        Field field = ProfileService.class.getDeclaredField("profileDAO");
        field.setAccessible(true);
        field.set(service, dao);

        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    // ---------------- getProfile ----------------

    @Test
    void getProfile_found() {
        Profile p = new Profile();
        p.setPin(1);

        when(dao.getProfileByPin(1)).thenReturn(p);

        Profile result = service.getProfile(1);

        assertNotNull(result);
        assertEquals(1, result.getPin());
    }

    @Test
    void getProfile_notFound() {
        when(dao.getProfileByPin(99)).thenReturn(null);

        Profile result = service.getProfile(99);

        assertNull(result);
        assertTrue(outContent.toString().contains("Profile not found for PIN"));
    }

    // ---------------- updateProfile ----------------

    @Test
    void updateProfile_success_validEmail() {
        when(dao.updateProfile(any(Profile.class))).thenReturn(true);

        boolean result = service.updateProfile(
                1, "Senior", "Java", "Backend", "link", "test@mail.com");

        assertTrue(result);
        verify(dao).updateProfile(any(Profile.class));
    }

    @Test
    void updateProfile_invalidEmail_blocksDaoCall() {
        boolean result = service.updateProfile(
                1, "Senior", "Java", "Backend", "link", "bad-email");

        assertFalse(result);
        verify(dao, never()).updateProfile(any());
    }

    @Test
    void updateProfile_emptyEmail_allowed() {
        when(dao.updateProfile(any(Profile.class))).thenReturn(true);

        boolean result = service.updateProfile(
                1, "Senior", "Java", "Backend", "link", "");

        assertTrue(result);
    }

    @Test
    void updateProfile_daoFails() {
        when(dao.updateProfile(any(Profile.class))).thenReturn(false);

        boolean result = service.updateProfile(
                1, "Senior", "Java", "Backend", "link", "test@mail.com");

        assertFalse(result);
    }

    // ---------------- updateCVTimestamp ----------------

    @Test
    void updateCVTimestamp_success() {
        when(dao.updateCVTimestamp(1)).thenReturn(true);

        assertTrue(service.updateCVTimestamp(1));
    }

    @Test
    void updateCVTimestamp_failure() {
        when(dao.updateCVTimestamp(1)).thenReturn(false);

        assertFalse(service.updateCVTimestamp(1));
    }

    // ---------------- profileExists ----------------

    @Test
    void profileExists_true() {
        when(dao.profileExists(1)).thenReturn(true);

        assertTrue(service.profileExists(1));
    }

    @Test
    void profileExists_false() {
        when(dao.profileExists(1)).thenReturn(false);

        assertFalse(service.profileExists(1));
    }

    // ---------------- displayProfile (IMPORTANT COVERAGE) ----------------

    @Test
    void displayProfile_nullProfile() {
        when(dao.getProfileByPin(1)).thenReturn(null);

        service.displayProfile(1);

        assertTrue(outContent.toString().contains("Profile not found"));
    }

    @Test
    void displayProfile_withNullFields() {
        Profile p = new Profile();
        p.setExperienceLevel(null);
        p.setTools(null);
        p.setSkills(null);
        p.setLink(null);
        p.setProfileEmail(null);

        when(dao.getProfileByPin(1)).thenReturn(p);

        service.displayProfile(1);

        String output = outContent.toString();

        assertTrue(output.contains("(Not set)"));
    }
}