package com.clbooster.app.theme;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;

/**
 * Service for managing theme switching between light, dark, and system modes.
 * Uses JavaScript to set a data-theme attribute on the document element.
 */
public class ThemeService {

    /**
     * Applies the specified theme to the current UI.
     * Theme is applied immediately via JavaScript.
     *
     * @param theme the theme preference: "light", "dark", or "system"
     * @param ui the UI instance to apply the theme to
     */
    public static void applyTheme(String theme, UI ui) {
        if (ui == null) {
            return;
        }

        String effectiveTheme = getEffectiveTheme(theme);

        // Store in session for persistence during the session
        VaadinSession.getCurrent().setAttribute("theme", effectiveTheme);

        // Apply theme via JavaScript
        ui.getPage().executeJs(
            "document.documentElement.setAttribute('data-theme', $0);" +
            "localStorage.setItem('cl-booster-theme', $0);",
            effectiveTheme
        );
    }

    /**
     * Applies theme to the current UI.
     */
    public static void applyTheme(String theme) {
        UI ui = UI.getCurrent();
        if (ui != null) {
            applyTheme(theme, ui);
        }
    }

    /**
     * Gets the effective theme based on user preference.
     * For "system", detects OS preference using matchMedia.
     *
     * @param themePreference the user's theme preference
     * @return the effective theme: "light" or "dark"
     */
    public static String getEffectiveTheme(String themePreference) {
        if (themePreference == null) {
            return "light";
        }

        return switch (themePreference.toLowerCase()) {
            case "dark" -> "dark";
            case "system" -> {
                // Check session first (if already determined)
                VaadinSession session = VaadinSession.getCurrent();
                if (session != null) {
                    String sessionTheme = (String) session.getAttribute("theme");
                    if (sessionTheme != null && !sessionTheme.equals("system")) {
                        yield sessionTheme;
                    }
                }
                // Default to light if system can't be determined server-side
                // Client-side JavaScript will handle the actual detection
                yield "light";
            }
            default -> "light";
        };
    }

    /**
     * Initializes theme handling on the client side.
     * Should be called once when the app loads.
     */
    public static void initializeTheme(UI ui) {
        if (ui == null) {
            return;
        }

        // Inject client-side theme detection and handling
        ui.getPage().executeJs(
            "// Check for saved theme preference or default to 'system'\n" +
            "var savedTheme = localStorage.getItem('cl-booster-theme') || 'system';\n" +
            "\n" +
            "// Function to get effective theme\n" +
            "function getEffectiveTheme(theme) {\n" +
            "    if (theme === 'dark') return 'dark';\n" +
            "    if (theme === 'light') return 'light';\n" +
            "    // System preference\n" +
            "    return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';\n" +
            "}\n" +
            "\n" +
            "// Apply the theme\n" +
            "var effectiveTheme = getEffectiveTheme(savedTheme);\n" +
            "document.documentElement.setAttribute('data-theme', effectiveTheme);\n" +
            "\n" +
            "// Listen for system theme changes\n" +
            "window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', function(e) {\n" +
            "    if (savedTheme === 'system') {\n" +
            "        document.documentElement.setAttribute('data-theme', e.matches ? 'dark' : 'light');\n" +
            "    }\n" +
            "});\n"
        );
    }

    /**
     * Gets the current theme from the session or localStorage.
     */
    public static String getCurrentTheme() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            String theme = (String) session.getAttribute("theme");
            if (theme != null) {
                return theme;
            }
        }
        return "light";
    }
}
