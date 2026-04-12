package com.clbooster.app.views;

import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.clbooster.app.backend.service.document.DocumentService;
import com.clbooster.app.backend.service.profile.User;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class ResumeManagerViewTest extends BaseVaadinViewTest {

    @AfterAll
    static void cleanup() {
        vaadinServletMock.close();
        vaadinSessionMock.close();
        vaadinServiceMock.close();
    }

    @Test
    void constructor_buildsMainUi() {
        DocumentService docService = Mockito.mock(DocumentService.class);

        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null))) {
            ResumeManagerView view = new ResumeManagerView(docService);
            assertNotNull(view);
            assertTrue(view.getComponentCount() >= 1);
        }
    }

    @Test
    void helperMethods_parseAndFormatValues() throws Exception {
        ResumeManagerView view = createViewWithNoUser();

        long kb = invoke(view, "parseSize", Long.class, new Class<?>[] { String.class }, new Object[] { "2.0 KB" });
        long mb = invoke(view, "parseSize", Long.class, new Class<?>[] { String.class }, new Object[] { "1.5 MB" });
        long bad = invoke(view, "parseSize", Long.class, new Class<?>[] { String.class }, new Object[] { "bad size" });

        String pdfColor = invoke(view, "getFileColor", String.class, new Class<?>[] { String.class },
                new Object[] { "PDF" });
        String txtColor = invoke(view, "getFileColor", String.class, new Class<?>[] { String.class },
                new Object[] { "TXT" });

        String bytes = invoke(view, "formatFileSize", String.class, new Class<?>[] { long.class },
                new Object[] { 512L });
        String kilobytes = invoke(view, "formatFileSize", String.class, new Class<?>[] { long.class },
                new Object[] { 2048L });

        String today = invoke(view, "formatDate", String.class, new Class<?>[] { LocalDateTime.class },
                new Object[] { LocalDateTime.now() });
        String yesterday = invoke(view, "formatDate", String.class, new Class<?>[] { LocalDateTime.class },
                new Object[] { LocalDateTime.now().minusDays(1) });

        assertEquals(2048L, kb);
        assertTrue(mb > 1024 * 1024);
        assertEquals(0L, bad);
        assertEquals("#FF3B30", pdfColor);
        assertEquals("#86868b", txtColor);
        assertTrue(bytes.contains("B"));
        assertTrue(kilobytes.contains("KB"));
        assertEquals("Today", today);
        assertEquals("Yesterday", yesterday);
    }

    @Test
    void helperMethods_coverAdditionalSizeAndDateBranches() throws Exception {
        ResumeManagerView view = createViewWithNoUser();

        long gb = invoke(view, "parseSize", Long.class, new Class<?>[] { String.class }, new Object[] { "1.0 GB" });
        long unknownUnit = invoke(view, "parseSize", Long.class, new Class<?>[] { String.class },
                new Object[] { "2.0 B" });

        String withinWeek = invoke(view, "formatDate", String.class, new Class<?>[] { LocalDateTime.class },
                new Object[] { LocalDateTime.now().minusDays(3) });
        String oldDate = invoke(view, "formatDate", String.class, new Class<?>[] { LocalDateTime.class },
                new Object[] { LocalDateTime.now().minusDays(20) });

        assertTrue(gb >= 1024L * 1024 * 1024);
        assertEquals(2L, unknownUnit);
        assertFalse(withinWeek.equals("Today") || withinWeek.equals("Yesterday"));
        assertTrue(oldDate.contains(","));
    }

    @Test
    void parseResumeFile_handlesValidAndInvalidNames() throws Exception {
        ResumeManagerView view = createViewWithNoUser();

        Path dir = Path.of("uploads", "resumes");
        Files.createDirectories(dir);

        Path valid = dir.resolve("0_1700000000000_MyResume.docx");
        Path invalid = dir.resolve("not-valid-name.pdf");
        Files.writeString(valid, "ok", StandardCharsets.UTF_8);
        Files.writeString(invalid, "bad", StandardCharsets.UTF_8);

        Object parsed = invoke(view, "parseResumeFile", Object.class, new Class<?>[] { File.class, int.class },
                new Object[] { valid.toFile(), 0 });
        Object rejected = invoke(view, "parseResumeFile", Object.class, new Class<?>[] { File.class, int.class },
                new Object[] { invalid.toFile(), 0 });

        assertNotNull(parsed);
        assertNull(rejected);

        Files.deleteIfExists(valid);
        Files.deleteIfExists(invalid);
    }

    @Test
    void loadResumesFromFilesystem_filtersByPin() throws Exception {
        ResumeManagerView view = createViewWithNoUser();

        Path dir = Path.of("uploads", "resumes");
        Files.createDirectories(dir);

        Path own = dir.resolve("0_1700000000001_OwnResume.pdf");
        Path other = dir.resolve("111_1700000000002_OtherResume.docx");
        Files.writeString(own, "own", StandardCharsets.UTF_8);
        Files.writeString(other, "other", StandardCharsets.UTF_8);

        @SuppressWarnings("unchecked")
        List<Object> loaded = (List<Object>) invoke(view, "loadResumesFromFilesystem", List.class);

        assertTrue(loaded.size() >= 1);
        assertTrue(loaded.stream().noneMatch(item -> {
            try {
                String name = readField(item, "name", String.class);
                return name.contains("OtherResume");
            } catch (Exception e) {
                return false;
            }
        }));

        Files.deleteIfExists(own);
        Files.deleteIfExists(other);
    }

    @Test
    void refreshAndSortResumes_updatesContainerAndBadge() throws Exception {
        ResumeManagerView view = createViewWithNoUser();

        List<Object> list = new ArrayList<>();
        list.add(newResumeData("B.docx", "DOCX", "1.0 KB", "Today", "/tmp/b", LocalDateTime.now().minusDays(1), false));
        list.add(newResumeData("A.pdf", "PDF", "2.0 KB", "Today", "/tmp/a", LocalDateTime.now(), false));

        setField(view, "resumes", list);
        invokeVoid(view, "refreshResumeList", new Class<?>[] {}, new Object[] {});

        VerticalLayout container = getField(view, "resumeListContainer", VerticalLayout.class);
        Span badge = getField(view, "countBadge", Span.class);
        assertEquals(2, container.getComponentCount());
        assertEquals("2", badge.getText());

        Object sortByNameAsc = enumConstant("com.clbooster.app.views.ResumeManagerView$ResumeSort", "NAME_ASC");
        invokeVoid(view, "sortResumes", new Class<?>[] { sortByNameAsc.getClass() }, new Object[] { sortByNameAsc });

        @SuppressWarnings("unchecked")
        List<Object> sorted = (List<Object>) getRawField(view, "resumes");
        String firstName = readField(sorted.get(0), "name", String.class);
        assertTrue(firstName.startsWith("A"));
    }

    @Test
    void createResumeCardAndToggleDelete_behaveAsExpected() throws Exception {
        DocumentService docService = Mockito.mock(DocumentService.class);
        when(docService.deleteResumeFile("/tmp/card")).thenReturn(true);

        ResumeManagerView view = createViewWithDocService(docService);

        Object resume = newResumeData("Card.docx", "DOCX", "1.0 KB", "Today", "/tmp/card", LocalDateTime.now(), false);
        Div card = invoke(view, "createResumeCard", Div.class, new Class<?>[] { resume.getClass() },
                new Object[] { resume });
        assertNotNull(card);

        List<Object> resumes = new ArrayList<>();
        resumes.add(resume);
        setField(view, "resumes", resumes);

        try (MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {
            notificationMock.when(() -> Notification.show(Mockito.anyString(), Mockito.anyInt(), Mockito.any()))
                    .thenReturn(Mockito.mock(Notification.class));

            invokeVoid(view, "toggleStar", new Class<?>[] { resume.getClass() }, new Object[] { resume });
            boolean starred = readField(resume, "starred", Boolean.class);
            assertTrue(starred);

            invokeVoid(view, "deleteResume", new Class<?>[] { resume.getClass() }, new Object[] { resume });
            @SuppressWarnings("unchecked")
            List<Object> remaining = (List<Object>) getRawField(view, "resumes");
            assertEquals(0, remaining.size());
        }
    }

    @Test
    void deleteResume_whenServiceFails_keepsResumeAndShowsFailureNotification() throws Exception {
        DocumentService docService = Mockito.mock(DocumentService.class);
        when(docService.deleteResumeFile("/tmp/fail-delete")).thenReturn(false);

        ResumeManagerView view = createViewWithDocService(docService);
        Object resume = newResumeData("FailDelete.pdf", "PDF", "1.0 KB", "Today", "/tmp/fail-delete",
                LocalDateTime.now(), false);

        List<Object> resumes = new ArrayList<>();
        resumes.add(resume);
        setField(view, "resumes", resumes);
        invokeVoid(view, "refreshResumeList", new Class<?>[] {}, new Object[] {});

        try (MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {
            notificationMock.when(() -> Notification.show(Mockito.anyString(), Mockito.anyInt(), Mockito.any()))
                    .thenReturn(Mockito.mock(Notification.class));

            invokeVoid(view, "deleteResume", new Class<?>[] { resume.getClass() }, new Object[] { resume });

            @SuppressWarnings("unchecked")
            List<Object> remaining = (List<Object>) getRawField(view, "resumes");
            assertEquals(1, remaining.size());
            notificationMock.verify(() -> Notification.show(Mockito.anyString(), Mockito.anyInt(), Mockito.any()));
        }
    }

    @Test
    void getCurrentUserPin_returnsAuthenticatedUsersPin() throws Exception {
        DocumentService docService = Mockito.mock(DocumentService.class);
        User user = new User("u@example.com", "user1", "secret", "First", "Last");
        user.setPin(4567);

        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(user))) {
            ResumeManagerView view = new ResumeManagerView(docService);
            int pin = invoke(view, "getCurrentUserPin", Integer.class);
            assertEquals(4567, pin);
        }
    }

    private ResumeManagerView createViewWithNoUser() {
        return createViewWithDocService(Mockito.mock(DocumentService.class));
    }

    private ResumeManagerView createViewWithDocService(DocumentService docService) {
        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null))) {
            return new ResumeManagerView(docService);
        }
    }

    private Object newResumeData(String name, String format, String size, String date, String filePath,
            LocalDateTime modified, boolean starred) throws Exception {
        Class<?> clazz = Class.forName("com.clbooster.app.views.ResumeManagerView$ResumeData");
        Constructor<?> c = clazz.getDeclaredConstructor(String.class, String.class, String.class, String.class,
                String.class, LocalDateTime.class, boolean.class);
        c.setAccessible(true);
        return c.newInstance(name, format, size, date, filePath, modified, starred);
    }

    private Object enumConstant(String className, String constant) throws Exception {
        Class<?> clazz = Class.forName(className);
        @SuppressWarnings("unchecked")
        Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) clazz;
        @SuppressWarnings({ "rawtypes", "unchecked" })
        Enum<?> value = Enum.valueOf((Class) enumClass, constant);
        return value;
    }

    @SuppressWarnings("unchecked")
    private <T> T invoke(Object target, String methodName, Class<T> type) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        return (T) method.invoke(target);
    }

    @SuppressWarnings("unchecked")
    private <T> T invoke(Object target, String methodName, Class<T> type, Class<?>[] parameterTypes, Object[] args)
            throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return (T) method.invoke(target, args);
    }

    private void invokeVoid(Object target, String methodName, Class<?>[] parameterTypes, Object[] args)
            throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        method.invoke(target, args);
    }

    @SuppressWarnings("unchecked")
    private <T> T getField(Object target, String fieldName, Class<T> type) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(target);
    }

    private Object getRawField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
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
