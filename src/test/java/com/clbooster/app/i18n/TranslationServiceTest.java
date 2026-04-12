package com.clbooster.app.i18n;

import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.clbooster.app.backend.service.profile.User;
import com.clbooster.app.backend.service.settings.Settings;
import com.clbooster.app.backend.service.settings.SettingsService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.server.VaadinSession;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TranslationServiceTest {

    @Test
    void providedLocales_andMissingKeyFallback_areCovered() {
        TranslationService service = new TranslationService();

        List<Locale> locales = service.getProvidedLocales();
        assertTrue(locales.contains(new Locale("en")));
        assertTrue(locales.contains(new Locale("fi")));

        String missing = service.getTranslation("non.existent.translation.key", Locale.ENGLISH);
        assertEquals("non.existent.translation.key", missing);
    }

    @Test
    void getCurrentLocale_prefersSessionLocale() {
        VaadinSession session = mock(VaadinSession.class);
        when(session.getAttribute("locale")).thenReturn(new Locale("pt"));

        try (MockedStatic<VaadinSession> sessionMock = Mockito.mockStatic(VaadinSession.class)) {
            sessionMock.when(VaadinSession::getCurrent).thenReturn(session);

            TranslationService service = new TranslationService();
            Locale locale = service.getCurrentLocale();

            assertEquals("pt", locale.getLanguage());
        }
    }

    @Test
    void getCurrentLocale_usesUserSettingsAndCachesInSession() {
        VaadinSession session = mock(VaadinSession.class);
        when(session.getAttribute("locale")).thenReturn(null);

        User currentUser = new User("mail@test.com", "tester", "Password1!x", "Test", "User");
        currentUser.setPin(1234);

        Settings settings = new Settings(1234);
        settings.setLanguage("Finnish (Suomi)");

        try (MockedStatic<VaadinSession> sessionMock = Mockito.mockStatic(VaadinSession.class);
                MockedConstruction<SettingsService> settingsMocked = Mockito.mockConstruction(SettingsService.class);
                MockedConstruction<AuthenticationService> authMocked = Mockito
                        .mockConstruction(AuthenticationService.class)) {

            sessionMock.when(VaadinSession::getCurrent).thenReturn(session);

            TranslationService service = new TranslationService();

            AuthenticationService auth = authMocked.constructed().get(0);
            when(auth.getCurrentUser()).thenReturn(currentUser);
            when(auth.getCurrentUserPin()).thenReturn(1234);

            SettingsService settingsService = settingsMocked.constructed().get(0);
            when(settingsService.getSettings(1234)).thenReturn(settings);

            Locale locale = service.getCurrentLocale();

            assertEquals("fi", locale.getLanguage());
            verify(session).setAttribute(eq("locale"), eq(new Locale("fi")));
        }
    }

    @Test
    void setCurrentLocale_andSetLanguage_updateUiAndDirection() {
        VaadinSession session = mock(VaadinSession.class);
        UI ui = mock(UI.class);
        Page page = mock(Page.class);
        when(ui.getPage()).thenReturn(page);

        try (MockedStatic<VaadinSession> sessionMock = Mockito.mockStatic(VaadinSession.class);
                MockedStatic<UI> uiMock = Mockito.mockStatic(UI.class)) {
            sessionMock.when(VaadinSession::getCurrent).thenReturn(session);
            uiMock.when(UI::getCurrent).thenReturn(ui);

            TranslationService service = new TranslationService();

            service.setCurrentLocale(new Locale("fa"));
            verify(session).setAttribute("locale", new Locale("fa"));
            verify(ui).setLocale(new Locale("fa"));
            verify(page).executeJs(anyString(), eq("rtl"));

            service.setLanguage("English");
            verify(session).setAttribute("locale", new Locale("en"));
        }
    }

    @Test
    void isRtl_handlesRtlAndNonRtlLocales() {
        TranslationService service = new TranslationService();

        assertTrue(service.isRtl(new Locale("fa")));
        assertTrue(service.isRtl(new Locale("ur")));
        assertFalse(service.isRtl(new Locale("en")));
        assertFalse(service.isRtl(null));
    }

    @Test
    void getTranslation_formatsParams_andFallsBackToDefaultBundleForUnknownLocale() {
        TranslationService service = new TranslationService();

        String formatted = service.getTranslation("history.exporting", Locale.ENGLISH, 3);
        assertTrue(formatted.contains("3"));

        String fallback = service.getTranslation("history.title", new Locale("xx"));
        assertTrue(fallback != null && !fallback.isBlank());
    }

    @Test
    void getCurrentLocale_defaultsWhenNoUserOrLanguage_and_setLanguageMapsMultipleAliases() {
        VaadinSession session = mock(VaadinSession.class);
        when(session.getAttribute("locale")).thenReturn(null);

        User currentUser = new User("mail@test.com", "tester", "Password1!x", "Test", "User");
        currentUser.setPin(5678);

        Settings settings = new Settings(5678);
        settings.setLanguage(null);

        try (MockedStatic<VaadinSession> sessionMock = Mockito.mockStatic(VaadinSession.class);
                MockedConstruction<SettingsService> settingsMocked = Mockito.mockConstruction(SettingsService.class);
                MockedConstruction<AuthenticationService> authMocked = Mockito
                        .mockConstruction(AuthenticationService.class)) {

            sessionMock.when(VaadinSession::getCurrent).thenReturn(session);
            TranslationService service = new TranslationService();

            AuthenticationService auth = authMocked.constructed().get(0);
            SettingsService settingsService = settingsMocked.constructed().get(0);

            when(auth.getCurrentUser()).thenReturn(null);
            assertEquals("en", service.getCurrentLocale().getLanguage());

            when(auth.getCurrentUser()).thenReturn(currentUser);
            when(auth.getCurrentUserPin()).thenReturn(5678);
            when(settingsService.getSettings(5678)).thenReturn(settings);
            assertEquals("en", service.getCurrentLocale().getLanguage());

            service.setLanguage("Português");
            verify(session).setAttribute("locale", new Locale("pt"));

            service.setLanguage("فارسی");
            verify(session).setAttribute("locale", new Locale("fa"));

            service.setLanguage("中文");
            verify(session).setAttribute("locale", new Locale("zh"));

            service.setLanguage("اردو");
            verify(session).setAttribute("locale", new Locale("ur"));
        }
    }
}
