package com.clbooster.app.backend.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StoredFileTest {

    @Test
    void constructor_setsAllFieldsCorrectly() {
        byte[] data = new byte[]{1, 2, 3};

        StoredFile file = new StoredFile("test.txt", "text/plain", data);

        assertEquals("test.txt", file.getName());
        assertEquals("text/plain", file.getContentType());
        assertArrayEquals(data, file.getData());
    }

    @Test
    void allowsNullValues() {
        StoredFile file = new StoredFile(null, null, null);

        assertNull(file.getName());
        assertNull(file.getContentType());
        assertNull(file.getData());
    }

    @Test
    void dataArray_isNotCopied_referenceIsShared() {
        byte[] data = new byte[]{1, 2, 3};

        StoredFile file = new StoredFile("file", "type", data);

        // mutate original array
        data[0] = 99;

        // proves internal state is exposed
        assertEquals(99, file.getData()[0]);
    }

    @Test
    void modifyingReturnedArray_affectsInternalState() {
        byte[] data = new byte[]{1, 2, 3};

        StoredFile file = new StoredFile("file", "type", data);

        byte[] retrieved = file.getData();
        retrieved[1] = 42;

        // confirms lack of defensive copying
        assertEquals(42, file.getData()[1]);
    }
}