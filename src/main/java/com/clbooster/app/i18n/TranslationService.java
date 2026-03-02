package com.clbooster.app.i18n;

import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.clbooster.app.backend.service.settings.Settings;
import com.clbooster.app.backend.service.settings.SettingsService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.VaadinSession;

import java.text.MessageFormat;
import java.util.*;

public class TranslationService implements I18NProvider {
    
    private static final String BUNDLE_PREFIX = "messages";
    private static final Locale DEFAULT_LOCALE = new Locale("en");
    
    private final SettingsService settingsService;
    private final AuthenticationService authService;
    
    public TranslationService() {
        this.settingsService = new SettingsService();
        this.authService = new AuthenticationService();
    }
    
    @Override
    public List<Locale> getProvidedLocales() {
        return Arrays.asList(
            new Locale("en"),    // English
            new Locale("fi"),    // Finnish
            new Locale("sv"),    // Swedish
            new Locale("de"),    // German
            new Locale("fr")     // French
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
        Locale sessionLocale = VaadinSession.getCurrent() != null ? 
            (Locale) VaadinSession.getCurrent().getAttribute("locale") : null;
        
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
                    VaadinSession.getCurrent().setAttribute("locale", userLocale);
                    return userLocale;
                }
            }
        }
        
        // Default to English
        return DEFAULT_LOCALE;
    }
    
    public void setCurrentLocale(Locale locale) {
        if (VaadinSession.getCurrent() != null) {
            VaadinSession.getCurrent().setAttribute("locale", locale);
        }
        
        // Update UI locale
        UI currentUI = UI.getCurrent();
        if (currentUI != null) {
            currentUI.setLocale(locale);
        }
    }
    
    public void setLanguage(String languageName) {
        Locale locale = parseLanguage(languageName);
        if (locale != null) {
            setCurrentLocale(locale);
        }
    }
    
    private Locale parseLanguage(String languageName) {
        if (languageName == null) return DEFAULT_LOCALE;
        
        String lower = languageName.toLowerCase();
        
        if (lower.contains("finnish") || lower.equals("suomi") || lower.equals("fi")) {
            return new Locale("fi");
        } else if (lower.contains("swedish") || lower.equals("svenska") || lower.equals("sv")) {
            return new Locale("sv");
        } else if (lower.contains("german") || lower.equals("deutsch") || lower.equals("de")) {
            return new Locale("de");
        } else if (lower.contains("french") || lower.equals("français") || lower.equals("fr")) {
            return new Locale("fr");
        } else {
            return new Locale("en");
        }
    }
    
    public String translate(String key, Object... params) {
        return getTranslation(key, getCurrentLocale(), params);
    }
}
