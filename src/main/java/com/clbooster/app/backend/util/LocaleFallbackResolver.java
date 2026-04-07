package com.clbooster.app.backend.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class LocaleFallbackResolver {

    private static final Locale DEFAULT_LOCALE = new Locale("en", "US");

    private LocaleFallbackResolver() {}

    public static List<Locale> getFallbackChain(Locale preferred) {
        List<Locale> chain = new ArrayList<>();

        if (preferred == null) {
            chain.add(DEFAULT_LOCALE);
            return chain;
        }

        chain.add(preferred);

        if (!preferred.getCountry().isEmpty()) {
            chain.add(new Locale(preferred.getLanguage()));
        }

        chain.add(DEFAULT_LOCALE);
        return chain;
    }

    public static Locale getDefault() {
        return DEFAULT_LOCALE;
    }
}
