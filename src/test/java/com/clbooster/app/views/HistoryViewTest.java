package com.clbooster.app.views;

import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.clbooster.app.backend.service.profile.User;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class HistoryViewTest extends BaseVaadinViewTest {

    @AfterAll
    static void cleanup() {
        vaadinServletMock.close();
        vaadinSessionMock.close();
        vaadinServiceMock.close();
    }

    @Test
    void constructor_initializesView() {
        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null))) {
            HistoryView view = new HistoryView();
            assertNotNull(view);
        }
    }

    @Test
    void parseFilename_validPattern_returnsHistoryItem() throws Exception {
        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null))) {
            HistoryView view = new HistoryView();
            Method method = HistoryView.class.getDeclaredMethod("parseFilename", String.class, String.class, long.class);
            method.setAccessible(true);

            Object item = method.invoke(view, "12345_20260412_113000_Acme_Senior_Engineer.docx", "path", System.currentTimeMillis());

            assertNotNull(item);
            assertEquals(12345, readField(item, "pin", Integer.class));
            assertNotNull(readField(item, "title", String.class));
            assertNotNull(readField(item, "timestamp", LocalDateTime.class));
        }
    }

    @Test
    void parseFilename_invalidPattern_returnsNull() throws Exception {
        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null))) {
            HistoryView view = new HistoryView();
            Method method = HistoryView.class.getDeclaredMethod("parseFilename", String.class, String.class, long.class);
            method.setAccessible(true);

            Object item = method.invoke(view, "invalidname.docx", "path", System.currentTimeMillis());
            assertNull(item);
        }
    }

    @Test
    void parseFilename_epochAndFallbackTimestamp_branchesCovered() throws Exception {
        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null))) {
            HistoryView view = new HistoryView();
            Method method = HistoryView.class.getDeclaredMethod("parseFilename", String.class, String.class, long.class);
            method.setAccessible(true);

            Object epochItem = method.invoke(view, "12345_1700000000000_Acme_Engineer.docx", "path", 1700000000000L);
            assertNotNull(epochItem);
            assertEquals(12345, readField(epochItem, "pin", Integer.class));

            long lastModified = 1704067200000L;
            Object fallbackItem = method.invoke(view, "12345_bad_Acme_Engineer.docx", "path", lastModified);
            assertNotNull(fallbackItem);

            LocalDateTime expected = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastModified), ZoneId.systemDefault());
            LocalDateTime actual = readField(fallbackItem, "timestamp", LocalDateTime.class);
            assertEquals(expected, actual);
        }
    }

    @Test
    void extractTextFromDocx_readsWordDocumentXml() throws Exception {
        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null))) {
            Path tmp = Files.createTempFile("history-view-", ".docx");
            createDocxWithDocumentXml(tmp,
                    "<w:document><w:body><w:p><w:r><w:t>Hello</w:t></w:r></w:p><w:p><w:r><w:t>World</w:t></w:r></w:p></w:body></w:document>");

            HistoryView view = new HistoryView();
            Method method = HistoryView.class.getDeclaredMethod("extractTextFromDocx", File.class);
            method.setAccessible(true);
            String text = (String) method.invoke(view, tmp.toFile());

            assertTrue(text.contains("Hello"));
            assertTrue(text.contains("World"));
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    void extractTextFromFile_handlesTxtPdfBinaryAndBrokenDocx() throws Exception {
        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null))) {
            HistoryView view = new HistoryView();
            Method method = HistoryView.class.getDeclaredMethod("extractTextFromFile", File.class);
            method.setAccessible(true);

            Path txt = Files.createTempFile("history-view-", ".txt");
            Files.writeString(txt, "Plain text", StandardCharsets.UTF_8);
            String txtResult = (String) method.invoke(view, txt.toFile());
            assertEquals("Plain text", txtResult);

            Path pdf = Files.createTempFile("history-view-", ".pdf");
            Files.writeString(pdf, "ignored", StandardCharsets.UTF_8);
            String pdfResult = (String) method.invoke(view, pdf.toFile());
            assertTrue(pdfResult.contains("PDF preview not supported"));

            Path binary = Files.createTempFile("history-view-", ".bin");
            Files.write(binary, new byte[] {0, 1, 2, 3, 4});
            String binaryResult = (String) method.invoke(view, binary.toFile());
            assertTrue(binaryResult.contains("Binary file"));

            Path brokenDocx = Files.createTempFile("history-view-broken-", ".docx");
            try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(brokenDocx))) {
                zip.putNextEntry(new ZipEntry("word/other.xml"));
                zip.write("<root/>".getBytes(StandardCharsets.UTF_8));
                zip.closeEntry();
            }
            String brokenDocxResult = (String) method.invoke(view, brokenDocx.toFile());
            assertTrue(brokenDocxResult.contains("Could not find document content"));

            Files.deleteIfExists(txt);
            Files.deleteIfExists(pdf);
            Files.deleteIfExists(binary);
            Files.deleteIfExists(brokenDocx);
        }
    }

    @Test
    void loadCoverLetterHistory_filtersByUserPin() throws Exception {
        Path uploadsDir = Path.of("uploads", "coverletters");
        Files.createDirectories(uploadsDir);

        Path own = uploadsDir.resolve("998877_20260412_120000_Acme_Engineer.docx");
        Path foreign = uploadsDir.resolve("111111_20260412_120001_Other_Analyst.docx");
        Files.writeString(own, "own", StandardCharsets.UTF_8);
        Files.writeString(foreign, "foreign", StandardCharsets.UTF_8);

        User user = new User("u@test.com", "u", "Pass123!pass", "U", "Test");
        user.setPin(998877);

        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(user))) {
            HistoryView view = new HistoryView();
            Method method = HistoryView.class.getDeclaredMethod("loadCoverLetterHistory");
            method.setAccessible(true);

            @SuppressWarnings("unchecked")
            List<Object> items = (List<Object>) method.invoke(view);

            assertTrue(items.stream().allMatch(i -> {
                try {
                    return (int) readField(i, "pin", Integer.class) == 998877;
                } catch (Exception e) {
                    return false;
                }
            }));
            assertTrue(items.size() >= 1);
        } finally {
            Files.deleteIfExists(own);
            Files.deleteIfExists(foreign);
        }
    }

    @Test
    void applyFilters_searchStatusAndDate_filtersItems() throws Exception {
        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null))) {
            HistoryView view = new HistoryView();

            List<Object> items = new ArrayList<>();
            items.add(newHistoryItem("Backend Engineer", "Acme", "Apr 12, 2026", "FINALIZED", 1,
                    LocalDateTime.of(2026, 4, 12, 12, 0), "/tmp/a.docx"));
            items.add(newHistoryItem("Analyst", "OtherCorp", "Mar 10, 2025", "ARCHIVED", 1,
                    LocalDateTime.of(2025, 3, 10, 9, 0), "/tmp/b.docx"));

            setField(view, "allItems", items);
            setField(view, "cardsGrid", new Div());

            TextField search = new TextField();
            search.setValue("acme");
            setField(view, "searchField", search);
            setField(view, "currentStatusFilter", "FINALIZED");
            setField(view, "dateFrom", LocalDate.of(2026, 1, 1));
            setField(view, "dateTo", LocalDate.of(2026, 12, 31));

            Method applyFilters = HistoryView.class.getDeclaredMethod("applyFilters");
            applyFilters.setAccessible(true);
            applyFilters.invoke(view);

            Div cardsGrid = getField(view, "cardsGrid", Div.class);
            assertEquals(1, cardsGrid.getComponentCount());
        }
    }

    @Test
    void statusBadgeAndUiHelpers_coverRemainingBranches() throws Exception {
        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null))) {
            HistoryView view = new HistoryView();

            Method createStatusBadge = HistoryView.class.getDeclaredMethod("createStatusBadge", String.class);
            createStatusBadge.setAccessible(true);
            Span sent = (Span) createStatusBadge.invoke(view, "SENT");
            Span finalized = (Span) createStatusBadge.invoke(view, "FINALIZED");
            Span archived = (Span) createStatusBadge.invoke(view, "ARCHIVED");

            assertNotNull(sent.getStyle().get("background"));
            assertNotNull(finalized.getStyle().get("color"));
            assertNotNull(archived.getStyle().get("color"));

            Method createIconButton = HistoryView.class.getDeclaredMethod("createIconButton", VaadinIcon.class);
            createIconButton.setAccessible(true);
            Button iconButton = (Button) createIconButton.invoke(view, VaadinIcon.EYE);
            assertNotNull(iconButton);

            Method createLoadMore = HistoryView.class.getDeclaredMethod("createLoadMoreButton");
            createLoadMore.setAccessible(true);
            Button loadMore = (Button) createLoadMore.invoke(view);
            assertNotNull(loadMore);
        }
    }

    @Test
    void extractTextFromFile_returnsReadableErrorWhenFileMissing() throws Exception {
        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null))) {
            HistoryView view = new HistoryView();
            Method method = HistoryView.class.getDeclaredMethod("extractTextFromFile", File.class);
            method.setAccessible(true);

            File missing = new File("/tmp/history-missing-preview-123456789.txt");
            String result = (String) method.invoke(view, missing);

            assertTrue(result.contains("Could not read file"));
        }
    }

    @Test
    void parseFilename_minimalParts_usesDefaultsAndArchivedStatus() throws Exception {
        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null))) {
            HistoryView view = new HistoryView();
            Method method = HistoryView.class.getDeclaredMethod("parseFilename", String.class, String.class, long.class);
            method.setAccessible(true);

            long old = Instant.now().minus(90, java.time.temporal.ChronoUnit.DAYS).toEpochMilli();
            Object item = method.invoke(view, "12345_1700000000000.docx", "/tmp/f.docx", old);

            assertNotNull(item);
            assertEquals("Unknown Company", readField(item, "company", String.class));
            assertEquals("Cover Letter", readField(item, "title", String.class));
            assertEquals("ARCHIVED", readField(item, "status", String.class));
        }
    }

    @Test
    void isPrintable_falseForControlChars() throws Exception {
        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null))) {
            HistoryView view = new HistoryView();
            Method method = HistoryView.class.getDeclaredMethod("isPrintable", String.class);
            method.setAccessible(true);

            boolean printable = (boolean) method.invoke(view, "Normal text");
            boolean notPrintable = (boolean) method.invoke(view, "bad\u0001text");

            assertTrue(printable);
            assertTrue(!notPrintable);
        }
    }

    @Test
    void exportAllFiles_whenEmpty_showsNoFilesNotification() throws Exception {
        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null));
             MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {

            notificationMock.when(() -> Notification.show(anyString(), anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            HistoryView view = new HistoryView();
            setField(view, "allItems", new ArrayList<>());

            Method export = HistoryView.class.getDeclaredMethod("exportAllFiles");
            export.setAccessible(true);
            export.invoke(view);

            notificationMock.verify(() -> Notification.show(anyString(), anyInt(), any()));
        }
    }

            @Test
            void extractTextFromFile_unknownPrintableExtension_returnsText() throws Exception {
            try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null))) {
                HistoryView view = new HistoryView();
                Method method = HistoryView.class.getDeclaredMethod("extractTextFromFile", File.class);
                method.setAccessible(true);

                Path data = Files.createTempFile("history-printable-", ".dat");
                Files.writeString(data, "readable printable text", StandardCharsets.UTF_8);

                String result = (String) method.invoke(view, data.toFile());
                assertEquals("readable printable text", result);

                Files.deleteIfExists(data);
            }
            }

            @Test
            void extractTextFromDocx_emptyText_returnsEmptyDocumentMessage() throws Exception {
            try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null))) {
                Path tmp = Files.createTempFile("history-empty-docx-", ".docx");
                createDocxWithDocumentXml(tmp,
                    "<w:document><w:body><w:p><w:r><w:t></w:t></w:r></w:p></w:body></w:document>");

                HistoryView view = new HistoryView();
                Method method = HistoryView.class.getDeclaredMethod("extractTextFromDocx", File.class);
                method.setAccessible(true);

                String result = (String) method.invoke(view, tmp.toFile());
                assertTrue(result.contains("Document appears to be empty"));

                Files.deleteIfExists(tmp);
            }
            }

            @Test
            void downloadCoverLetter_missingAndReadErrorPaths_showNotification() throws Exception {
            try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null));
                 MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {

                notificationMock.when(() -> Notification.show(anyString(), anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

                HistoryView view = new HistoryView();
                Method method = HistoryView.class.getDeclaredMethod("downloadCoverLetter",
                    Class.forName("com.clbooster.app.views.HistoryView$HistoryItem"));
                method.setAccessible(true);

                Object missing = newHistoryItem("T", "C", "D", "FINALIZED", 1, LocalDateTime.now(),
                    "/tmp/not-there-history-download.docx");
                method.invoke(view, missing);

                Path dir = Files.createTempDirectory("history-download-dir-");
                Object unreadable = newHistoryItem("T2", "C2", "D2", "FINALIZED", 1, LocalDateTime.now(),
                    dir.toString());
                method.invoke(view, unreadable);

                notificationMock.verify(() -> Notification.show(anyString(), anyInt(), any()), Mockito.atLeast(2));
                Files.deleteIfExists(dir);
            }
            }

            @Test
            void exportAllFiles_successWithExistingFiles() throws Exception {
            try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null));
                 MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {

                notificationMock.when(() -> Notification.show(anyString(), anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

                HistoryView view = new HistoryView();

                Path file = Files.createTempFile("history-export-", ".txt");
                Files.writeString(file, "export", StandardCharsets.UTF_8);

                List<Object> items = new ArrayList<>();
                items.add(newHistoryItem("Title", "Comp", "Date", "FINALIZED", 1, LocalDateTime.now(),
                    file.toString()));
                setField(view, "allItems", items);

                Method export = HistoryView.class.getDeclaredMethod("exportAllFiles");
                export.setAccessible(true);
                export.invoke(view);

                notificationMock.verify(() -> Notification.show(anyString(), anyInt(), any()), Mockito.atLeastOnce());
                Files.deleteIfExists(file);
            }
            }

    private void createDocxWithDocumentXml(Path target, String xmlContent) throws Exception {
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(target))) {
            zip.putNextEntry(new ZipEntry("word/document.xml"));
            zip.write(xmlContent.getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
        }
    }

    private Object newHistoryItem(String title, String company, String date, String status, int pin,
            LocalDateTime timestamp, String filePath) throws Exception {
        Class<?> clazz = Class.forName("com.clbooster.app.views.HistoryView$HistoryItem");
        Constructor<?> c = clazz.getDeclaredConstructor(String.class, String.class, String.class, String.class,
                int.class, LocalDateTime.class, String.class);
        c.setAccessible(true);
        return c.newInstance(title, company, date, status, pin, timestamp, filePath);
    }

    @SuppressWarnings("unchecked")
    private <T> T getField(Object target, String fieldName, Class<T> type) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(target);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @SuppressWarnings("unchecked")
    private <T> T readField(Object target, String fieldName, Class<T> type) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        return (T) f.get(target);
    }
}
