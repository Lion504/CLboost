package com.clbooster.app.backend.util;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

public final class Utf8Validator {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private Utf8Validator() {}

    public static boolean isValidUtf8(String input) {
        if (input == null) return true;
        CharsetDecoder decoder = UTF8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);
        try {
            decoder.decode(UTF8.encode(input));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String sanitize(String input) {
        if (input == null) return null;
        return input
                .replaceAll("\\p{Cntrl}", "")
                .replaceAll("\u0000", "")
                .trim();
    }
}
