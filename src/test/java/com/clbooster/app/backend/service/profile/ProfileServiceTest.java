package com.clbooster.app.backend.service.profile;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileServiceTest {

    private ProfileService service;
    private ProfileDAO profileDAOMock;
    private UserDAO userDAOMock;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() throws Exception {
        service = new ProfileService();
        profileDAOMock = mock(ProfileDAO.class);
        userDAOMock = mock(UserDAO.class);

        Field daoField = ProfileService.class.getDeclaredField("profileDAO");
        daoField.setAccessible(true);
        daoField.set(service, profileDAOMock);

        Field userDaoField = ProfileService.class.getDeclaredField("userDAO");
        userDaoField.setAccessible(true);
        userDaoField.set(service, userDAOMock);

        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void getProfile_found() {
        Profile profile = new Profile();
        profile.setPin(1);

        when(profileDAOMock.getByIdWithFallback(1, Locale.getDefault(), Locale.US)).thenReturn(profile);

        Profile result = service.getProfile(1, Locale.getDefault());

        assertNotNull(result);
        assertEquals(1, result.getPin());
        verify(profileDAOMock).getByIdWithFallback(1, Locale.getDefault(), Locale.US);
    }

    @Test
    void getProfile_notFound() {
        when(profileDAOMock.getByIdWithFallback(99, Locale.getDefault(), Locale.US)).thenReturn(null);

        Profile result = service.getProfile(99, Locale.getDefault());

        assertNull(result);
        assertTrue(outContent.toString().contains("Profile not found for PIN"));
        verify(profileDAOMock).getByIdWithFallback(99, Locale.getDefault(), Locale.US);
    }

    @Test
    void updateProfile_success_validEmail() {
        boolean result = service.updateProfile(1, "", "", "Senior", "Java", "Backend", "link", "test@mail.com", Locale.getDefault());

        assertTrue(result);
        verify(userDAOMock).updateUser(1, "", "", "test@mail.com");
        verify(profileDAOMock).saveTranslation(any(Profile.class), eq(Locale.getDefault()));
    }

    @Test
    void updateProfile_invalidEmail_blocksDaoCall() {
        boolean result = service.updateProfile(1, "", "", "Senior", "Java", "Backend", "link", "bad-email", Locale.getDefault());

        assertFalse(result);
        verifyNoInteractions(userDAOMock, profileDAOMock);
    }

    @Test
    void updateProfile_emptyEmail_allowed() {
        boolean result = service.updateProfile(1, "", "", "Senior", "Java", "Backend", "link", "", Locale.getDefault());

        assertTrue(result);
        verify(userDAOMock).updateUser(1, "", "", "");
        verify(profileDAOMock).saveTranslation(any(Profile.class), eq(Locale.getDefault()));
    }

    @Test
    void updateProfile_daoFailure() {
        doThrow(new RuntimeException("DB failure")).when(profileDAOMock).saveTranslation(any(Profile.class),
                any(Locale.class));

        boolean result = service.updateProfile(1, "", "", "Senior", "Java", "Backend", "link", "test@mail.com", Locale.getDefault());

        assertFalse(result);
    }

    @Test
    void updateCVTimestamp_success() {
        when(profileDAOMock.updateCVTimestamp(1)).thenReturn(true);

        assertTrue(service.updateCVTimestamp(1));
    }

    @Test
    void updateCVTimestamp_failure() {
        when(profileDAOMock.updateCVTimestamp(1)).thenReturn(false);

        assertFalse(service.updateCVTimestamp(1));
    }

    @Test
    void profileExists_true() {
        when(profileDAOMock.profileExists(1)).thenReturn(true);

        assertTrue(service.profileExists(1));
    }

    @Test
    void profileExists_false() {
        when(profileDAOMock.profileExists(1)).thenReturn(false);

        assertFalse(service.profileExists(1));
    }

    @Test
    void displayProfile_nullProfile() {
        when(profileDAOMock.getByIdWithFallback(1, Locale.getDefault(), Locale.US)).thenReturn(null);

        service.displayProfile(1);

        assertTrue(outContent.toString().contains("Profile not found"));
    }

    @Test
    void displayProfile_withNullFields() {
        Profile profile = new Profile();
        profile.setExperienceLevel(null);
        profile.setTools(null);
        profile.setSkills(null);
        profile.setLink(null);
        profile.setProfileEmail(null);

        when(profileDAOMock.getByIdWithFallback(1, Locale.getDefault(), Locale.US)).thenReturn(profile);

        service.displayProfile(1);

        String output = outContent.toString();
        assertTrue(output.contains("(Not set)"));
    }
}
