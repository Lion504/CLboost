package com.clbooster.app.backend.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocaleFallbackResolverTest {

    @Test
    void getFallbackChain_forNullPreferred_returnsDefaultOnly() {
        List<Locale> chain = LocaleFallbackResolver.getFallbackChain(null);

        assertEquals(1, chain.size());
        assertEquals(new Locale("en", "US"), chain.get(0));
    }

    @Test
    void getFallbackChain_withCountry_addsLanguageAndDefault() {
        List<Locale> chain = LocaleFallbackResolver.getFallbackChain(new Locale("pt", "BR"));

        assertEquals(3, chain.size());
        assertEquals(new Locale("pt", "BR"), chain.get(0));
        assertEquals(new Locale("pt"), chain.get(1));
        assertEquals(new Locale("en", "US"), chain.get(2));
    }

    @Test
    void getFallbackChain_withoutCountry_addsDefaultOnly() {
        List<Locale> chain = LocaleFallbackResolver.getFallbackChain(new Locale("fi"));

        assertEquals(2, chain.size());
        assertEquals(new Locale("fi"), chain.get(0));
        assertEquals(new Locale("en", "US"), chain.get(1));
    }

    @Test
    void getDefault_returnsEnglishUs() {
        assertEquals(new Locale("en", "US"), LocaleFallbackResolver.getDefault());
    }
}
