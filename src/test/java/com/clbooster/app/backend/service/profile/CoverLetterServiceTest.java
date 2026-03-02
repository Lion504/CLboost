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

        // inject DAO
        Field daoField = CoverLetterService.class.getDeclaredField("coverLetterDAO");
        daoField.setAccessible(true);
        daoField.set(service, dao);

        // override BASE_PATH (now works because it's NOT final)
        Field basePathField = CoverLetterService.class.getDeclaredField("BASE_PATH");
        basePathField.setAccessible(true);
        basePathField.set(null, tempDir.toString() + "/");
    }

    @Test
    void saveCoverLetter_success() throws Exception {
        when(dao.addCoverLetter(eq(123), anyString())).thenReturn(1);

        byte[] content = "test".getBytes();
        int id = service.saveCoverLetter(123, content, "pdf");

        assertEquals(1, id);
        assertEquals(1, Files.list(tempDir).count());
    }

    @Test
    void saveCoverLetter_dbFailure_returnsMinusOne() {
        when(dao.addCoverLetter(anyInt(), anyString())).thenReturn(-1);

        int id = service.saveCoverLetter(123, "x".getBytes(), "pdf");

        assertEquals(-1, id);
    }

    @Test
    void readCoverLetter_success() throws Exception {
        Path file = tempDir.resolve("file.pdf");
        byte[] content = "hello".getBytes();
        Files.write(file, content);

        CoverLetter cl = new CoverLetter();
        cl.setId(1);
        cl.setFilePath(file.toString());

        when(dao.getCoverLetterById(1)).thenReturn(cl);

        byte[] result = service.readCoverLetter(1);

        assertArrayEquals(content, result);
    }

    @Test
    void readCoverLetter_notFound_returnsNull() {
        when(dao.getCoverLetterById(1)).thenReturn(null);

        assertNull(service.readCoverLetter(1));
    }

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
    }

    @Test
    void updateCoverLetter_notFound_returnsFalse() {
        when(dao.getCoverLetterById(1)).thenReturn(null);

        assertFalse(service.updateCoverLetter(1, "x".getBytes(), "pdf"));
    }

    @Test
    void deleteCoverLetter_success() throws Exception {
        Path file = tempDir.resolve("delete.pdf");
        Files.write(file, "x".getBytes());

        CoverLetter cl = new CoverLetter();
        cl.setId(1);
        cl.setFilePath(file.toString());

        when(dao.getCoverLetterById(1)).thenReturn(cl);
        when(dao.deleteCoverLetter(1)).thenReturn(true);

        boolean result = service.deleteCoverLetter(1);

        assertTrue(result);
        assertFalse(Files.exists(file));
    }

    @Test
    void deleteCoverLetter_notFound_returnsFalse() {
        when(dao.getCoverLetterById(1)).thenReturn(null);

        assertFalse(service.deleteCoverLetter(1));
    }

    @Test
    void getCoverLetters_delegatesToDao() {
        when(dao.getCoverLettersByPin(123)).thenReturn(List.of());

        List<CoverLetter> result = service.getCoverLetters(123);

        assertNotNull(result);
        verify(dao).getCoverLettersByPin(123);
    }
}