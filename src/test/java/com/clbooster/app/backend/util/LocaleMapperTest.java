package com.clbooster.app.backend.util;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocaleMapperTest {

    @Test
    void getDbCode_returnsDefaultForNull() {
        assertEquals("en_US", LocaleMapper.getDbCode(null));
    }

    @Test
    void getDbCode_mapsKnownLocales() {
        assertEquals("fi_FI", LocaleMapper.getDbCode(new Locale("fi")));
        assertEquals("pt_BR", LocaleMapper.getDbCode(new Locale("pt")));
        assertEquals("zh_CN", LocaleMapper.getDbCode(new Locale("zh")));
    }

    @Test
    void getDbCode_returnsDefaultForUnknownLocale() {
        assertEquals("en_US", LocaleMapper.getDbCode(new Locale("de")));
    }
}
