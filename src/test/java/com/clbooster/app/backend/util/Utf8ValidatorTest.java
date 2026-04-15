package com.clbooster.app.backend.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Utf8ValidatorTest {

    @Test
    void isValidUtf8_returnsTrueForNull() {
        assertTrue(Utf8Validator.isValidUtf8(null));
    }

    @Test
    void isValidUtf8_returnsTrueForNormalText() {
        assertTrue(Utf8Validator.isValidUtf8("hello äö å"));
    }

    @Test
    void sanitize_returnsNullForNullInput() {
        assertNull(Utf8Validator.sanitize(null));
    }

    @Test
    void sanitize_removesControlCharactersAndTrims() {
        String input = "  A\u0000B\n\tC\r  ";
        assertEquals("ABC", Utf8Validator.sanitize(input));
    }
}
