package com.clbooster.app.backend.service.profile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileServiceTest {

    private ProfileService service;
    private ProfileDAO profileDAOMock;

    @BeforeEach
    void setUp() throws Exception {
        service = new ProfileService();
        profileDAOMock = mock(ProfileDAO.class);

        // 🔴 Forced dependency injection (because constructor is bad)
        Field daoField = ProfileService.class.getDeclaredField("profileDAO");
        daoField.setAccessible(true);
        daoField.set(service, profileDAOMock);
    }

    @Test
    void getProfile_found() {
        Profile profile = new Profile();
        profile.setPin(123);

        when(profileDAOMock.getProfileByPin(123)).thenReturn(profile);

        Profile result = service.getProfile(123);

        assertNotNull(result);
        assertEquals(123, result.getPin());
        verify(profileDAOMock).getProfileByPin(123);
    }

    @Test
    void getProfile_notFound() {
        when(profileDAOMock.getProfileByPin(999)).thenReturn(null);

        Profile result = service.getProfile(999);

        assertNull(result);
        verify(profileDAOMock).getProfileByPin(999);
    }

    @Test
    void updateProfile_success() {
        when(profileDAOMock.updateProfile(any(Profile.class))).thenReturn(true);

        boolean result = service.updateProfile(1, "Senior", "Java", "Backend", "link", "test@mail.com");

        assertTrue(result);
        verify(profileDAOMock).updateProfile(any(Profile.class));
    }

    @Test
    void updateProfile_invalidEmail() {
        boolean result = service.updateProfile(1, "Senior", "Java", "Backend", "link", "invalid-email");

        assertFalse(result);
        verify(profileDAOMock, never()).updateProfile(any());
    }

    @Test
    void updateProfile_daoFailure() {
        when(profileDAOMock.updateProfile(any(Profile.class))).thenReturn(false);

        boolean result = service.updateProfile(1, "Senior", "Java", "Backend", "link", "test@mail.com");

        assertFalse(result);
    }

    @Test
    void updateCVTimestamp_success() {
        when(profileDAOMock.updateCVTimestamp(1)).thenReturn(true);

        assertTrue(service.updateCVTimestamp(1));
        verify(profileDAOMock).updateCVTimestamp(1);
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
}