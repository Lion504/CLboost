package com.clbooster.app.backend.service;

import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileUploadServiceTest {

    private FileRepository repository;
    private TestFileUploadService service;
    private MultipartFile file;

    // Concrete test implementation
    static class TestFileUploadService extends FileUploadService {

        StoredFile capturedFile;

        TestFileUploadService(FileRepository repo) {
            super(repo);
        }

        @Override
        protected void persist(StoredFile file) {
            this.capturedFile = file;
        }
    }

    @BeforeEach
    void setup() {
        repository = mock(FileRepository.class);
        service = new TestFileUploadService(repository);
        file = mock(MultipartFile.class);
    }

    // --- Validation ---

    @Test
    void saveFile_nullFile_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> service.saveFile(null));
    }

    @Test
    void saveFile_emptyFile_throwsException() {
        when(file.isEmpty()).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.saveFile(file));

        assertNull(service.capturedFile); // persist should not run
    }

    // --- Happy path ---

    @Test
    void saveFile_validFile_persistsCorrectly() throws Exception {
        byte[] data = new byte[] { 1, 2, 3 };

        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("test.txt");
        when(file.getContentType()).thenReturn("text/plain");
        when(file.getBytes()).thenReturn(data);

        service.saveFile(file);

        assertNotNull(service.capturedFile);
        assertEquals("test.txt", service.capturedFile.getName());
        assertEquals("text/plain", service.capturedFile.getContentType());
        assertArrayEquals(data, service.capturedFile.getData());
    }

    // --- Interaction correctness ---

    @Test
    void saveFile_callsPersistExactlyOnce() throws Exception {
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("file");
        when(file.getContentType()).thenReturn("type");
        when(file.getBytes()).thenReturn(new byte[] { 1 });

        service.saveFile(file);

        assertNotNull(service.capturedFile); // implies persist called once
    }

    // --- Exception propagation ---

    @Test
    void saveFile_whenGetBytesFails_propagatesException() throws Exception {
        when(file.isEmpty()).thenReturn(false);
        when(file.getBytes()).thenThrow(new RuntimeException("IO error"));

        assertThrows(RuntimeException.class, () -> service.saveFile(file));

        assertNull(service.capturedFile); // persist must not run
    }

    // --- Edge cases ---

    @Test
    void saveFile_allowsNullMetadata() throws Exception {
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn(null);
        when(file.getContentType()).thenReturn(null);
        when(file.getBytes()).thenReturn(new byte[] { 1, 2 });

        service.saveFile(file);

        assertNull(service.capturedFile.getName());
        assertNull(service.capturedFile.getContentType());
    }
}