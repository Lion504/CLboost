package com.clbooster.app.views;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextArea;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoverLetterEditorViewTest extends BaseVaadinViewTest {

    @AfterAll
    static void cleanup() {
        vaadinServletMock.close();
        vaadinSessionMock.close();
        vaadinServiceMock.close();
    }

    @Test
    void constructor_initializesView() {
        CoverLetterEditorView view = new CoverLetterEditorView();
        assertNotNull(view);
    }

    @Test
    void loadFileContent_readsTextFile(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("letter.txt");
        Files.writeString(file, "Hello CL Booster", StandardCharsets.UTF_8);

        CoverLetterEditorView view = new CoverLetterEditorView();
        invoke(view, "buildUI");
        setField(view, "currentFile", file.toFile());

        invoke(view, "loadFileContent");

        TextArea editor = getField(view, "editor", TextArea.class);
        assertEquals("Hello CL Booster", editor.getValue());
    }

    @Test
    void saveFile_writesEditorContent(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("save.txt");
        Files.writeString(file, "old", StandardCharsets.UTF_8);

        CoverLetterEditorView view = new CoverLetterEditorView();
        invoke(view, "buildUI");
        setField(view, "currentFile", file.toFile());

        TextArea editor = getField(view, "editor", TextArea.class);
        editor.setValue("new content");

        try (MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {
            notificationMock.when(() -> Notification.show(Mockito.anyString(), Mockito.anyInt(), Mockito.any()))
                    .thenReturn(Mockito.mock(Notification.class));
            invoke(view, "saveFile");
        }

        assertEquals("new content", Files.readString(file, StandardCharsets.UTF_8));
    }

    @Test
    void loadFileContent_handlesMissingFile() throws Exception {
        CoverLetterEditorView view = new CoverLetterEditorView();
        invoke(view, "buildUI");
        setField(view, "currentFile", new File("/tmp/definitely-missing-file-12345.txt"));

        try (MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {
            notificationMock.when(() -> Notification.show(Mockito.anyString(), Mockito.anyInt(), Mockito.any()))
                    .thenReturn(Mockito.mock(Notification.class));

            invoke(view, "loadFileContent");

            notificationMock.verify(
                    () -> Notification.show(Mockito.contains("File not found"), Mockito.anyInt(), Mockito.any()));
        }
    }

    @Test
    void loadFileContent_pdfSetsReadOnlyPlaceholder(@TempDir Path tempDir) throws Exception {
        Path pdf = tempDir.resolve("letter.pdf");
        Files.writeString(pdf, "binary-like", StandardCharsets.UTF_8);

        CoverLetterEditorView view = new CoverLetterEditorView();
        invoke(view, "buildUI");
        setField(view, "currentFile", pdf.toFile());

        invoke(view, "loadFileContent");

        TextArea editor = getField(view, "editor", TextArea.class);
        assertTrue(editor.isReadOnly());
        assertTrue(editor.getValue().contains("PDF editing is not supported"));
    }

    @Test
    void saveFile_handlesNullFileAndReadOnlyGuards(@TempDir Path tempDir) throws Exception {
        CoverLetterEditorView view = new CoverLetterEditorView();
        invoke(view, "buildUI");
        TextArea editor = getField(view, "editor", TextArea.class);

        try (MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {
            notificationMock.when(() -> Notification.show(Mockito.anyString(), Mockito.anyInt(), Mockito.any()))
                    .thenReturn(Mockito.mock(Notification.class));

            // Guard: currentFile == null
            invoke(view, "saveFile");
            notificationMock.verify(
                    () -> Notification.show(Mockito.contains("No file to save"), Mockito.anyInt(), Mockito.any()));

            // Guard: editor is read-only
            Path file = tempDir.resolve("readonly.txt");
            Files.writeString(file, "content", StandardCharsets.UTF_8);
            setField(view, "currentFile", file.toFile());
            editor.setReadOnly(true);

            invoke(view, "saveFile");
            notificationMock.verify(
                    () -> Notification.show(Mockito.contains("cannot be edited"), Mockito.anyInt(), Mockito.any()));
        }
    }

    @Test
    void extractTextFromDocx_parsesTextNodes(@TempDir Path tempDir) throws Exception {
        Path docx = tempDir.resolve("sample.docx");
        createDocxWithDocumentXml(docx, "<w:document><w:body><w:p><w:r><w:t>Hello</w:t></w:r></w:p>"
                + "<w:p><w:r><w:t>World</w:t></w:r></w:p></w:body></w:document>");

        CoverLetterEditorView view = new CoverLetterEditorView();
        Method method = CoverLetterEditorView.class.getDeclaredMethod("extractTextFromDocx", File.class);
        method.setAccessible(true);

        String text = (String) method.invoke(view, docx.toFile());

        assertTrue(text.contains("Hello"));
        assertTrue(text.contains("World"));
    }

    @Test
    void extractTextFromDocx_handlesMissingDocumentXml(@TempDir Path tempDir) throws Exception {
        Path docx = tempDir.resolve("missing-doc.xml.docx");
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(docx))) {
            zip.putNextEntry(new ZipEntry("word/other.xml"));
            zip.write("<x/>".getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
        }

        CoverLetterEditorView view = new CoverLetterEditorView();
        Method method = CoverLetterEditorView.class.getDeclaredMethod("extractTextFromDocx", File.class);
        method.setAccessible(true);

        String text = (String) method.invoke(view, docx.toFile());

        assertTrue(text.contains("Could not find document content"));
    }

    @Test
    void extractTextFromDocx_handlesEmptyTextContent(@TempDir Path tempDir) throws Exception {
        Path docx = tempDir.resolve("empty.docx");
        createDocxWithDocumentXml(docx, "<w:document><w:body><w:p><w:r><w:t></w:t></w:r></w:p></w:body></w:document>");

        CoverLetterEditorView view = new CoverLetterEditorView();
        Method method = CoverLetterEditorView.class.getDeclaredMethod("extractTextFromDocx", File.class);
        method.setAccessible(true);

        String text = (String) method.invoke(view, docx.toFile());

        assertFalse(text.isBlank());
        assertTrue(text.contains("Document appears to be empty"));
    }

    private void createDocxWithDocumentXml(Path target, String xmlContent) throws Exception {
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(target))) {
            zip.putNextEntry(new ZipEntry("word/document.xml"));
            zip.write(xmlContent.getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
        }
    }

    private void invoke(Object target, String methodName) throws Exception {
        Method m = target.getClass().getDeclaredMethod(methodName);
        m.setAccessible(true);
        m.invoke(target);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @SuppressWarnings("unchecked")
    private <T> T getField(Object target, String fieldName, Class<T> type) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(target);
    }
}
