package com.clbooster.app.backend.service.profile;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoverLetterTest {

    @Test
    void constructors_andAccessors_workAsExpected() {
        Timestamp ts = Timestamp.valueOf("2026-04-12 10:15:30");

        CoverLetter full = new CoverLetter(7, 42, ts, "/tmp/letter.docx");
        assertEquals(7, full.getId());
        assertEquals(42, full.getPin());
        assertEquals(ts, full.getTimestampEdited());
        assertEquals("/tmp/letter.docx", full.getFilePath());

        CoverLetter minimal = new CoverLetter(99, "uploads/coverletters/c1.txt");
        assertEquals(99, minimal.getPin());
        assertEquals("uploads/coverletters/c1.txt", minimal.getFilePath());

        CoverLetter empty = new CoverLetter();
        empty.setId(5);
        empty.setPin(11);
        empty.setTimestampEdited(ts);
        empty.setFilePath("/tmp/new.docx");

        assertEquals(5, empty.getId());
        assertEquals(11, empty.getPin());
        assertEquals(ts, empty.getTimestampEdited());
        assertEquals("/tmp/new.docx", empty.getFilePath());
    }

    @Test
    void toString_containsAllMainFields() {
        Timestamp ts = Timestamp.valueOf("2026-04-12 10:15:30");
        CoverLetter letter = new CoverLetter(3, 88, ts, "uploads/coverletters/file.docx");

        String rendered = letter.toString();

        assertNotNull(rendered);
        assertTrue(rendered.contains("id=3"));
        assertTrue(rendered.contains("pin=88"));
        assertTrue(rendered.contains("uploads/coverletters/file.docx"));
    }
}
