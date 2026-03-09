package com.clbooster.aiservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ExporterTest {

    private final Exporter exporter = new Exporter();

    @Test
    void testSaveAsDoc_Success(@TempDir Path tempDir) {
        // Arrange
        String content = "Hello World\nThis is a test line.\n\nAnother line.";
        String outputPath = tempDir.resolve("test_output.docx").toString();

        // Act
        exporter.saveAsDoc(content, outputPath);

        // Assert
        File resultFile = new File(outputPath);
        assertTrue(resultFile.exists(), "The document should have been created.");
        assertTrue(resultFile.length() > 0, "The file should not be empty.");
    }

    @Test
    void testSaveAsDoc_IOException() {
        // Arrange
        String content = "Test content";
        // Using a path that is impossible to write to (like a directory as a file)
        // will trigger the IOException in the FileOutputStream
        String invalidPath = "/this/path/is/invalid/and/cannot/exist/document.docx";

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            exporter.saveAsDoc(content, invalidPath);
        });

        assertEquals("Failed to save document.", exception.getMessage());
    }

    @Test
    void testSaveAsDoc_EmptyContent(@TempDir Path tempDir) {
        // Testing empty content to ensure it doesn't crash
        // and covers the logic branches for the empty check
        String outputPath = tempDir.resolve("empty.docx").toString();

        exporter.saveAsDoc("", outputPath);

        File resultFile = new File(outputPath);
        assertTrue(resultFile.exists());
    }
}
