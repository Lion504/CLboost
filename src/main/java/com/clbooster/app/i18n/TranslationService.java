package com.clbooster.app.i18n;

import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.clbooster.app.backend.service.settings.Settings;
import com.clbooster.app.backend.service.settings.SettingsService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.*;

@Component
public class TranslationService implements I18NProvider {
    private static final String LOCALE_KEY = "locale";

    private static final String BUNDLE_PREFIX = "messages";
    private static final Locale DEFAULT_LOCALE = new Locale("en");

    private final transient SettingsService settingsService;
    private final transient AuthenticationService authService;

    public TranslationService() {
        this.settingsService = new SettingsService();
        this.authService = new AuthenticationService();
    }

    @Override
    public List<Locale> getProvidedLocales() {
        return Arrays.asList(new Locale("en"), // English
                new Locale("fi"), // Finnish
                new Locale("pt"), // Portuguese
                new Locale("fa"), // Persian
                new Locale("zh"), // Chinese
                new Locale("ur") // Urdu
        );
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        // Use provided locale or get from user settings
        Locale effectiveLocale = locale != null ? locale : getCurrentLocale();

        ResourceBundle bundle = getBundle(effectiveLocale);
        if (bundle == null) {
            // Fallback to default locale
            bundle = getBundle(DEFAULT_LOCALE);
        }

        if (bundle != null && bundle.containsKey(key)) {
            String value = bundle.getString(key);
            if (params != null && params.length > 0) {
                return MessageFormat.format(value, params);
            }
            return value;
        }

        // Return key if not found
        return key;
    }

    private ResourceBundle getBundle(Locale locale) {
        try {
            return ResourceBundle.getBundle(BUNDLE_PREFIX, locale);
        } catch (MissingResourceException e) {
            return null;
        }
    }

    public Locale getCurrentLocale() {
        // First check session
        Locale sessionLocale = VaadinSession.getCurrent() != null
                ? (Locale) VaadinSession.getCurrent().getAttribute(LOCALE_KEY)
                : null;

        if (sessionLocale != null) {
            return sessionLocale;
        }

        // Then check user settings
        if (authService.getCurrentUser() != null) {
            int userPin = authService.getCurrentUserPin();
            Settings settings = settingsService.getSettings(userPin);
            if (settings.getLanguage() != null) {
                Locale userLocale = parseLanguage(settings.getLanguage());
                if (userLocale != null) {
                    // Store in session for next time
                    VaadinSession.getCurrent().setAttribute(LOCALE_KEY, userLocale);
                    return userLocale;
                }
            }
        }

        // Default to English
        return DEFAULT_LOCALE;
    }

    public void setCurrentLocale(Locale locale) {
        if (VaadinSession.getCurrent() != null) {
            VaadinSession.getCurrent().setAttribute(LOCALE_KEY, locale);
        }

        UI currentUI = UI.getCurrent();
        if (currentUI != null) {
            currentUI.setLocale(locale);
            currentUI.getPage().executeJs("document.documentElement.setAttribute('dir', $0)",
                    isRtl(locale) ? "rtl" : "ltr");
        }
    }

    public boolean isRtl(Locale locale) {
        if (locale == null)
            return false;
        String lang = locale.getLanguage();
        return "fa".equals(lang) || "ur".equals(lang) || "ar".equals(lang) || "he".equals(lang);
    }

    public void setLanguage(String languageName) {
        Locale locale = parseLanguage(languageName);
        if (locale != null) {
            setCurrentLocale(locale);
        }
    }

    private Locale parseLanguage(String languageName) {
        if (languageName == null)
            return DEFAULT_LOCALE;

        String lower = languageName.toLowerCase();

        if (lower.contains("finnish") || lower.equals("suomi") || lower.equals("fi")) {
            return new Locale("fi");
        } else if (lower.contains("portuguese") || lower.equals("português") || lower.equals("pt")) {
            return new Locale("pt");
        } else if (lower.contains("persian") || lower.equals("فارسی") || lower.equals("fa") || lower.equals("farsi")) {
            return new Locale("fa");
        } else if (lower.contains("chinese") || lower.equals("中文") || lower.equals("zh")) {
            return new Locale("zh");
        } else if (lower.contains("urdu") || lower.equals("اردو") || lower.equals("ur")) {
            return new Locale("ur");
        } else {
            return new Locale("en");
        }
    }

    public String translate(String key, Object... params) {
        return getTranslation(key, getCurrentLocale(), params);
    }
}
