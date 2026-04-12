package com.clbooster.app.theme;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.server.VaadinSession;
import org.junit.jupiter.api.*;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ThemeServiceTest {

    @Mock
    private UI ui;

    @Mock
    private Page page;

    @Mock
    private VaadinSession session;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        when(ui.getPage()).thenReturn(page);

        // Mock static VaadinSession.getCurrent()
        VaadinSession.setCurrent(session);
    }

    @AfterEach
    void tearDown() {
        VaadinSession.setCurrent(null);
    }

    // --- applyTheme ---

    @Test
    void applyTheme_nullUi_doesNothing() {
        ThemeService.applyTheme("dark", null);

        verifyNoInteractions(session);
    }

    @Test
    void applyTheme_setsSessionAndExecutesJs() {
        ThemeService.applyTheme("dark", ui);

        verify(session).setAttribute("theme", "dark");
        verify(page).executeJs(contains("data-theme"), eq("dark"));
    }

    @Test
    void applyTheme_defaultsToLightOnInvalidInput() {
        ThemeService.applyTheme("invalid", ui);

        verify(session).setAttribute("theme", "light");
        verify(page).executeJs(contains("data-theme"), eq("light"));
    }

    @Test
    void applyTheme_nullTheme_defaultsToLight() {
        ThemeService.applyTheme(null, ui);

        verify(session).setAttribute("theme", "light");
        verify(page).executeJs(contains("data-theme"), eq("light"));
    }

    // --- getEffectiveTheme ---

    @Test
    void getEffectiveTheme_dark() {
        assertEquals("dark", ThemeService.getEffectiveTheme("dark"));
    }

    @Test
    void getEffectiveTheme_lightFallback() {
        assertEquals("light", ThemeService.getEffectiveTheme("light"));
    }

    @Test
    void getEffectiveTheme_null_returnsLight() {
        assertEquals("light", ThemeService.getEffectiveTheme(null));
    }

    @Test
    void getEffectiveTheme_system_usesSessionValue_ifAvailable() {
        when(session.getAttribute("theme")).thenReturn("dark");

        String result = ThemeService.getEffectiveTheme("system");

        assertEquals("dark", result);
    }

    @Test
    void getEffectiveTheme_system_ignoresSystemKeywordInSession() {
        when(session.getAttribute("theme")).thenReturn("system");

        String result = ThemeService.getEffectiveTheme("system");

        assertEquals("light", result);
    }

    @Test
    void getEffectiveTheme_system_defaultsToLight_whenNoSession() {
        VaadinSession.setCurrent(null);

        String result = ThemeService.getEffectiveTheme("system");

        assertEquals("light", result);
    }

    // --- getCurrentTheme ---

    @Test
    void getCurrentTheme_returnsSessionValue() {
        when(session.getAttribute("theme")).thenReturn("dark");

        String result = ThemeService.getCurrentTheme();

        assertEquals("dark", result);
    }

    @Test
    void getCurrentTheme_defaultsToLight_whenNull() {
        when(session.getAttribute("theme")).thenReturn(null);

        String result = ThemeService.getCurrentTheme();

        assertEquals("light", result);
    }

    @Test
    void getCurrentTheme_defaultsToLight_whenNoSession() {
        VaadinSession.setCurrent(null);

        String result = ThemeService.getCurrentTheme();

        assertEquals("light", result);
    }

    // --- initializeTheme ---

    @Test
    void initializeTheme_executesJavascript() {
        ThemeService.initializeTheme(ui);

        verify(page).executeJs(contains("matchMedia"));
    }

    @Test
    void initializeTheme_nullUi_doesNothing() {
        ThemeService.initializeTheme(null);

        verifyNoInteractions(page);
    }
}