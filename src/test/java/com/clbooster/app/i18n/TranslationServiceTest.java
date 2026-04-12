package com.clbooster.app.i18n;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.server.VaadinSession;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TranslationServiceTest {

    private TranslationService service;

    @Mock
    private VaadinSession session;

    @Mock
    private UI ui;

    @Mock
    private Page page;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        service = new TranslationService();

        VaadinSession.setCurrent(session);
        when(ui.getPage()).thenReturn(page);
        UI.setCurrent(ui);
    }

    @AfterEach
    void tearDown() {
        VaadinSession.setCurrent(null);
        UI.setCurrent(null);
    }

    // --- getProvidedLocales ---

    @Test
    void getProvidedLocales_containsExpectedLanguages() {
        var locales = service.getProvidedLocales();

        assertTrue(locales.contains(new Locale("en")));
        assertTrue(locales.contains(new Locale("fi")));
        assertTrue(locales.contains(new Locale("pt")));
        assertTrue(locales.contains(new Locale("fa")));
        assertTrue(locales.contains(new Locale("zh")));
        assertTrue(locales.contains(new Locale("ur")));
    }

    // --- parseLanguage (via public API) ---

    @Test
    void setLanguage_parsesFinnishVariants() {
        service.setLanguage("finnish");
        verify(session).setAttribute(eq("locale"), eq(new Locale("fi")));
    }

    @Test
    void setLanguage_parsesPortugueseVariants() {
        service.setLanguage("pt");
        verify(session).setAttribute(eq("locale"), eq(new Locale("pt")));
    }

    @Test
    void setLanguage_defaultsToEnglishOnUnknown() {
        service.setLanguage("nonsense");
        verify(session).setAttribute(eq("locale"), eq(new Locale("en")));
    }

    @Test
    void setLanguage_handlesNull() {
        service.setLanguage(null);
        verify(session).setAttribute(eq("locale"), eq(new Locale("en")));
    }

    // --- isRtl ---

    @Test
    void isRtl_trueForRtlLanguages() {
        assertTrue(service.isRtl(new Locale("fa")));
        assertTrue(service.isRtl(new Locale("ur")));
        assertTrue(service.isRtl(new Locale("ar")));
        assertTrue(service.isRtl(new Locale("he")));
    }

    @Test
    void isRtl_falseForNonRtlLanguages() {
        assertFalse(service.isRtl(new Locale("en")));
        assertFalse(service.isRtl(new Locale("fi")));
    }

    @Test
    void isRtl_nullReturnsFalse() {
        assertFalse(service.isRtl(null));
    }

    // --- setCurrentLocale ---

    @Test
    void setCurrentLocale_setsSessionAndUpdatesUI() {
        Locale locale = new Locale("fa");

        service.setCurrentLocale(locale);

        verify(session).setAttribute("locale", locale);
        verify(ui).setLocale(locale);
        verify(page).executeJs(contains("dir"), eq("rtl"));
    }

    @Test
    void setCurrentLocale_ltrForNonRtl() {
        Locale locale = new Locale("en");

        service.setCurrentLocale(locale);

        verify(page).executeJs(contains("dir"), eq("ltr"));
    }

    // --- getCurrentLocale ---

    @Test
    void getCurrentLocale_returnsSessionLocale_ifPresent() {
        Locale locale = new Locale("fi");
        when(session.getAttribute("locale")).thenReturn(locale);

        Locale result = service.getCurrentLocale();

        assertEquals(locale, result);
    }

    @Test
    void getCurrentLocale_defaultsToEnglish_whenNoSessionAndNoUser() {
        when(session.getAttribute("locale")).thenReturn(null);

        Locale result = service.getCurrentLocale();

        assertEquals(new Locale("en"), result);
    }

    // --- getTranslation ---

    @Test
    void getTranslation_returnsKey_whenMissing() {
        String result = service.getTranslation("non.existent.key", new Locale("en"));

        assertEquals("non.existent.key", result);
    }

    @Test
    void getTranslation_fallsBackToDefaultLocale() {
        String result = service.getTranslation("some.key", new Locale("xx"));

        assertNotNull(result); // should not crash
    }

    // --- translate() wrapper ---

    @Test
    void translate_usesCurrentLocale() {
        when(session.getAttribute("locale")).thenReturn(new Locale("en"));

        String result = service.translate("non.existent");

        assertEquals("non.existent", result);
    }
}