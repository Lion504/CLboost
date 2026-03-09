package com.clbooster.aiservice;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;

class ParserTest {

    private final Parser parser = new Parser();

    @Test
    void testParseFileToJson_Success() {
        String fakePath = "resume.pdf";
        String expectedText = "John Doe - Java Developer";

        // 1. Create a mock Document to return
        Document mockDocument = mock(Document.class);
        Mockito.when(mockDocument.text()).thenReturn(expectedText);

        // 2. Mock the static call to FileSystemDocumentLoader
        try (MockedStatic<FileSystemDocumentLoader> loaderMock = Mockito.mockStatic(FileSystemDocumentLoader.class)) {
            loaderMock.when(
                    () -> FileSystemDocumentLoader.loadDocument(any(Path.class), any(ApacheTikaDocumentParser.class)))
                    .thenReturn(mockDocument);

            // Act
            String result = parser.parseFileToJson(fakePath);

            // Assert
            assertEquals(expectedText, result);
        }
    }

    @Test
    void testParseFileToJson_Exception() {
        String fakePath = "non_existent.pdf";

        // Mock the static call to throw an exception
        try (MockedStatic<FileSystemDocumentLoader> loaderMock = Mockito.mockStatic(FileSystemDocumentLoader.class)) {
            loaderMock.when(
                    () -> FileSystemDocumentLoader.loadDocument(any(Path.class), any(ApacheTikaDocumentParser.class)))
                    .thenThrow(new RuntimeException("File not found"));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                parser.parseFileToJson(fakePath);
            });

            assertTrue(exception.getMessage().contains("Unable to parse file"));
            assertEquals("File not found", exception.getCause().getMessage());
        }
    }
}
