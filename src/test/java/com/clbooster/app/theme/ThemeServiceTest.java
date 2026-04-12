package com.clbooster.app.theme;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.server.VaadinSession;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ThemeServiceTest {

    @Test
    void applyTheme_withNullUi_doesNothing() {
        ThemeService.applyTheme("dark", null);
    }

    @Test
    void applyTheme_withUi_setsSessionAndExecutesJs() {
        UI ui = Mockito.mock(UI.class);
        Page page = Mockito.mock(Page.class);
        VaadinSession session = Mockito.mock(VaadinSession.class);

        when(ui.getPage()).thenReturn(page);

        try (MockedStatic<VaadinSession> sessionMock = Mockito.mockStatic(VaadinSession.class)) {
            sessionMock.when(VaadinSession::getCurrent).thenReturn(session);

            ThemeService.applyTheme("dark", ui);

            verify(session).setAttribute("theme", "dark");
            verify(page).executeJs(contains("document.documentElement.setAttribute"), eq("dark"));
        }
    }

    @Test
    void applyTheme_withoutUiArgument_usesCurrentUi() {
        UI ui = Mockito.mock(UI.class);
        Page page = Mockito.mock(Page.class);
        VaadinSession session = Mockito.mock(VaadinSession.class);

        when(ui.getPage()).thenReturn(page);

        try (MockedStatic<UI> uiMock = Mockito.mockStatic(UI.class);
                MockedStatic<VaadinSession> sessionMock = Mockito.mockStatic(VaadinSession.class)) {
            uiMock.when(UI::getCurrent).thenReturn(ui);
            sessionMock.when(VaadinSession::getCurrent).thenReturn(session);

            ThemeService.applyTheme("light");

            verify(session).setAttribute("theme", "light");
            verify(page).executeJs(contains("localStorage.setItem"), eq("light"));
        }
    }

    @Test
    void getEffectiveTheme_handlesDarkSystemAndDefaults() {
        VaadinSession session = Mockito.mock(VaadinSession.class);

        assertEquals("dark", ThemeService.getEffectiveTheme("dark"));
        assertEquals("light", ThemeService.getEffectiveTheme("invalid"));
        assertEquals("light", ThemeService.getEffectiveTheme(null));

        try (MockedStatic<VaadinSession> sessionMock = Mockito.mockStatic(VaadinSession.class)) {
            sessionMock.when(VaadinSession::getCurrent).thenReturn(session);
            when(session.getAttribute("theme")).thenReturn("dark");
            assertEquals("dark", ThemeService.getEffectiveTheme("system"));

            when(session.getAttribute("theme")).thenReturn("system");
            assertEquals("light", ThemeService.getEffectiveTheme("system"));
        }
    }

    @Test
    void initializeAndGetCurrentTheme_coverSessionBranches() {
        UI ui = Mockito.mock(UI.class);
        Page page = Mockito.mock(Page.class);
        VaadinSession session = Mockito.mock(VaadinSession.class);

        when(ui.getPage()).thenReturn(page);

        ThemeService.initializeTheme(null);

        ThemeService.initializeTheme(ui);
        verify(page).executeJs(contains("prefers-color-scheme"));

        try (MockedStatic<VaadinSession> sessionMock = Mockito.mockStatic(VaadinSession.class)) {
            sessionMock.when(VaadinSession::getCurrent).thenReturn(session);
            when(session.getAttribute("theme")).thenReturn("dark");
            assertEquals("dark", ThemeService.getCurrentTheme());

            when(session.getAttribute("theme")).thenReturn(null);
            assertEquals("light", ThemeService.getCurrentTheme());
        }
    }
}
