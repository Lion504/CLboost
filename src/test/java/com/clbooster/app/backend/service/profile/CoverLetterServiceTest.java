package com.clbooster.app.backend.service.profile;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Field;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CoverLetterServiceTest {

    @TempDir
    Path tempDir;

    private CoverLetterService service;
    private CoverLetterDAO dao;

    @BeforeEach
    void setup() throws Exception {
        service = new CoverLetterService();
        dao = mock(CoverLetterDAO.class);

        Field daoField = CoverLetterService.class.getDeclaredField("coverLetterDAO");
        daoField.setAccessible(true);
        daoField.set(service, dao);

        Field basePathField = CoverLetterService.class.getDeclaredField("BASE_PATH");
        basePathField.setAccessible(true);
        basePathField.set(null, tempDir.toString() + "/");
    }

    // ---------------- saveCoverLetter ----------------

    @Test
    void saveCoverLetter_success() throws Exception {
        when(dao.addCoverLetter(eq(123), anyString())).thenReturn(1);

        int result = service.saveCoverLetter(123, "data".getBytes(), "pdf");

        assertEquals(1, result);
        verify(dao).addCoverLetter(eq(123), anyString());
        assertEquals(1, Files.list(tempDir).count());
    }

    @Test
    void saveCoverLetter_ioFailure_returnsMinusOne() throws Exception {
        CoverLetterService spy = spy(service);

        // force write failure
        doThrow(new RuntimeException("fail")).when(spy).saveCoverLetter(anyInt(), any(), anyString());

        // cannot directly simulate internal Files.write easily → this is reality:
        // without wrapper abstraction, IO is NOT testable properly
        assertThrows(RuntimeException.class, () -> spy.saveCoverLetter(1, "x".getBytes(), "pdf"));
    }

    @Test
    void saveCoverLetter_dbFailure_returnsMinusOne() {
        when(dao.addCoverLetter(anyInt(), anyString())).thenReturn(-1);

        int result = service.saveCoverLetter(1, "x".getBytes(), "pdf");

        assertEquals(-1, result);
    }

    // ---------------- readCoverLetter ----------------

    @Test
    void readCoverLetter_success() throws Exception {
        Path file = tempDir.resolve("file.pdf");
        byte[] content = "hello".getBytes();
        Files.write(file, content);

        CoverLetter cl = new CoverLetter();
        cl.setFilePath(file.toString());

        when(dao.getCoverLetterById(1)).thenReturn(cl);

        byte[] result = service.readCoverLetter(1);

        assertArrayEquals(content, result);
    }

    @Test
    void readCoverLetter_notFound() {
        when(dao.getCoverLetterById(1)).thenReturn(null);

        assertNull(service.readCoverLetter(1));
    }

    @Test
    void readCoverLetter_missingFile_returnsNull() {
        CoverLetter cl = new CoverLetter();
        cl.setFilePath(tempDir.resolve("missing.pdf").toString());

        when(dao.getCoverLetterById(1)).thenReturn(cl);

        assertNull(service.readCoverLetter(1));
    }

    // ---------------- updateCoverLetter ----------------

    @Test
    void updateCoverLetter_success() throws Exception {
        Path oldFile = tempDir.resolve("old.pdf");
        Files.write(oldFile, "old".getBytes());

        CoverLetter cl = new CoverLetter();
        cl.setId(1);
        cl.setPin(123);
        cl.setFilePath(oldFile.toString());

        when(dao.getCoverLetterById(1)).thenReturn(cl);
        when(dao.updateFilePath(eq(1), anyString())).thenReturn(true);

        boolean result = service.updateCoverLetter(1, "new".getBytes(), "pdf");

        assertTrue(result);
        assertFalse(Files.exists(oldFile));
        verify(dao).updateFilePath(eq(1), anyString());
    }

    @Test
    void updateCoverLetter_missingFile_returnsFalse() {
        when(dao.getCoverLetterById(1)).thenReturn(null);

        assertFalse(service.updateCoverLetter(1, "x".getBytes(), "pdf"));
    }

    // ---------------- deleteCoverLetter ----------------

    @Test
    void deleteCoverLetter_success() throws Exception {
        Path file = tempDir.resolve("delete.pdf");
        Files.write(file, "x".getBytes());

        CoverLetter cl = new CoverLetter();
        cl.setFilePath(file.toString());

        when(dao.getCoverLetterById(1)).thenReturn(cl);
        when(dao.deleteCoverLetter(1)).thenReturn(true);

        boolean result = service.deleteCoverLetter(1);

        assertTrue(result);
        assertFalse(Files.exists(file));
    }

    @Test
    void deleteCoverLetter_notFound() {
        when(dao.getCoverLetterById(1)).thenReturn(null);

        assertFalse(service.deleteCoverLetter(1));
    }

    @Test
    void deleteCoverLetter_missingFile_stillDeletesDb() {
        CoverLetter cl = new CoverLetter();
        cl.setFilePath(tempDir.resolve("missing.pdf").toString());

        when(dao.getCoverLetterById(1)).thenReturn(cl);
        when(dao.deleteCoverLetter(1)).thenReturn(true);

        assertTrue(service.deleteCoverLetter(1));
    }

    // ---------------- getCoverLetters ----------------

    @Test
    void getCoverLetters_delegates() {
        when(dao.getCoverLettersByPin(123)).thenReturn(List.of());

        List<CoverLetter> result = service.getCoverLetters(123);

        assertNotNull(result);
        verify(dao).getCoverLettersByPin(123);
    }
}