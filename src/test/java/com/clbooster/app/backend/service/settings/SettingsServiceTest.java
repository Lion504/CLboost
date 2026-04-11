package com.clbooster.app.backend.service.settings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SettingsServiceTest {

    private SettingsService service;
    private SettingsDAO dao;

    @BeforeEach
    void setUp() throws Exception {
        service = new SettingsService();
        dao = mock(SettingsDAO.class);

        Field field = SettingsService.class.getDeclaredField("settingsDAO");
        field.setAccessible(true);
        field.set(service, dao);
    }

    @Test
    void getSettings_delegatesToDAO() {
        Settings settings = new Settings(1);

        when(dao.getSettings(1)).thenReturn(settings);

        Settings result = service.getSettings(1);

        assertEquals(settings, result);
        verify(dao).getSettings(1);
    }

    @Test
    void saveSettings_delegatesToDAO() {
        Settings settings = new Settings(1);

        when(dao.saveSettings(settings)).thenReturn(true);

        boolean result = service.saveSettings(settings);

        assertTrue(result);
        verify(dao).saveSettings(settings);
    }

    @Test
    void deleteSettings_delegatesToDAO() {
        when(dao.deleteSettings(1)).thenReturn(true);

        boolean result = service.deleteSettings(1);

        assertTrue(result);
        verify(dao).deleteSettings(1);
    }
}