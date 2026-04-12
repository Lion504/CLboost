package com.clbooster.app.backend.service.settings;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SettingsTest {

    @Test
    void defaultConstructor_setsExpectedDefaults() {
        Settings settings = new Settings();

        assertEquals("system", settings.getTheme());
        assertEquals("English", settings.getLanguage());
        assertTrue(settings.isEmailNotifications());
        assertFalse(settings.isPushNotifications());
        assertTrue(settings.isProductUpdates());
        assertFalse(settings.isMarketing());
        assertTrue(settings.isStoreInCloud());
        assertFalse(settings.isAllowAiTraining());
        assertTrue(settings.isShareUsageData());
    }

    @Test
    void constructorWithUserPin_setsPinAndDefaults() {
        Settings settings = new Settings(321);

        assertEquals(321, settings.getUserPin());
        assertEquals("system", settings.getTheme());
    }

    @Test
    void settersAndGetters_roundTripValues() {
        Settings settings = new Settings();

        settings.setUserPin(111);
        settings.setTheme("dark");
        settings.setLanguage("Finnish");
        settings.setEmailNotifications(false);
        settings.setPushNotifications(true);
        settings.setProductUpdates(false);
        settings.setMarketing(true);
        settings.setStoreInCloud(false);
        settings.setAllowAiTraining(true);
        settings.setShareUsageData(false);

        assertEquals(111, settings.getUserPin());
        assertEquals("dark", settings.getTheme());
        assertEquals("Finnish", settings.getLanguage());
        assertFalse(settings.isEmailNotifications());
        assertTrue(settings.isPushNotifications());
        assertFalse(settings.isProductUpdates());
        assertTrue(settings.isMarketing());
        assertFalse(settings.isStoreInCloud());
        assertTrue(settings.isAllowAiTraining());
        assertFalse(settings.isShareUsageData());
    }
}
