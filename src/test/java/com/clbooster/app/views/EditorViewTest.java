package com.clbooster.app.views;

import com.clbooster.aiservice.AIService;
import com.clbooster.aiservice.Exporter;
import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.clbooster.app.backend.service.document.DocumentService;
import com.clbooster.app.backend.service.profile.User;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EditorViewTest extends BaseVaadinViewTest {

    @AfterAll
    static void cleanup() {
        vaadinServletMock.close();
        vaadinSessionMock.close();
        vaadinServiceMock.close();
    }

    @Test
    void constructor_withSessionData_initializesEditor() throws Exception {
        VaadinSession session = mockSession("Backend Developer", "Acme", "Professional", "Build APIs");
        vaadinSessionMock.when(VaadinSession::getCurrent).thenReturn(session);

        User user = new User("a@b.com", "tester", "Password1!x", "Test", "User");
        user.setPin(1001);

        DocumentService documentService = Mockito.mock(DocumentService.class);
        AIService aiService = Mockito.mock(AIService.class);

        try (MockedConstruction<AuthenticationService> authMock = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(user))) {

            EditorView view = new EditorView(documentService, aiService);

            assertNotNull(view);
            assertEquals("Backend Developer", getField(view, "jobTitle", String.class));
            assertEquals("Acme", getField(view, "companyName", String.class));
            assertNotNull(getField(view, "editorArea", TextArea.class));
            assertEquals(1, authMock.constructed().size());
        }
    }

    @Test
    void constructor_missingRequiredSessionData_setsDefaultsAndStops() throws Exception {
        VaadinSession session = Mockito.mock(VaadinSession.class);
        when(session.getAttribute(anyString())).thenReturn(null);
        vaadinSessionMock.when(VaadinSession::getCurrent).thenReturn(session);

        DocumentService documentService = Mockito.mock(DocumentService.class);
        AIService aiService = Mockito.mock(AIService.class);

        EditorView view;
        try (MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {
            notificationMock.when(() -> Notification.show(anyString(), Mockito.anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));
            view = new EditorView(documentService, aiService);
        }

        assertEquals("Cover Letter", getField(view, "jobTitle", String.class));
        assertEquals("Company", getField(view, "companyName", String.class));
        assertNull(getField(view, "editorArea", TextArea.class));
    }

    @Test
    void uiBuilders_createHeaderToolbarAndPanel() throws Exception {
        EditorView view = createViewWithDefaults();

        VerticalLayout panel = invoke(view, "createEditorPanel", VerticalLayout.class);
        HorizontalLayout header = invoke(view, "createEditorHeader", HorizontalLayout.class);
        HorizontalLayout toolbar = invoke(view, "createToolbar", HorizontalLayout.class);
        Button primary = invoke(view, "createPrimaryButton", Button.class,
                new Class<?>[] { String.class, com.vaadin.flow.component.icon.VaadinIcon.class },
                new Object[] { "Export", com.vaadin.flow.component.icon.VaadinIcon.FILE_TEXT });

        assertNotNull(panel);
        assertNotNull(header);
        assertNotNull(toolbar);
        assertNotNull(primary);
        assertTrue(toolbar.getComponentCount() >= 7);
    }

    @Test
    void wrapContent_andFilenameSanitizer_workAsExpected() throws Exception {
        EditorView view = createViewWithDefaults();

        TextArea editor = getField(view, "editorArea", TextArea.class);
        editor.setValue("content");

        invokeVoid(view, "wrapContent", new Class<?>[] { String.class, String.class }, new Object[] { "**", "**" });
        assertEquals("**content**", editor.getValue());

        invokeVoid(view, "wrapContent", new Class<?>[] { String.class, String.class }, new Object[] { "**", "**" });
        assertEquals("content", editor.getValue());

        String sanitized = invoke(view, "sanitizeForFilename", String.class,
                new Class<?>[] { String.class }, new Object[] { "  ACME / Senior Dev!!!  " });
        assertEquals("ACME_Senior_Dev", sanitized);
    }

    @Test
    void generateCoverLetter_usesAiWhenAvailable_andFallbackOtherwise() throws Exception {
        EditorView view = createViewWithDefaults();
        AIService aiService = getField(view, "aiService", AIService.class);

        when(aiService.generateCoverLetter(anyString(), anyString(), anyString())).thenReturn("Generated body");
        String generated = invoke(view, "generateCoverLetter", String.class);
        assertEquals("Generated body", generated);

        when(aiService.generateCoverLetter(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("ai down"));
        String fallback = invoke(view, "generateCoverLetter", String.class);
        assertTrue(fallback.contains("Dear Hiring Manager"));
    }

    @Test
    void saveGeneratedCoverLetter_handlesUserAndExporterCases() throws Exception {
        EditorView view = createViewWithDefaults();
        User user = new User("a@b.com", "tester", "Password1!x", "Test", "User");
        user.setPin(4321);

        try (MockedConstruction<AuthenticationService> authMock = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(user));
             MockedConstruction<Exporter> exporterMock = Mockito.mockConstruction(Exporter.class,
                     (mock, context) -> Mockito.doNothing().when(mock).saveAsDoc(anyString(), anyString()))) {

            String path = invoke(view, "saveGeneratedCoverLetter", String.class,
                    new Class<?>[] { String.class }, new Object[] { "Generated text" });

            assertNotNull(path);
            assertTrue(path.contains("uploads"));
            verify(exporterMock.constructed().get(0)).saveAsDoc(eq("Generated text"), anyString());
            assertEquals(1, authMock.constructed().size());
        }

        try (MockedConstruction<AuthenticationService> authMock = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null))) {
            String path = invoke(view, "saveGeneratedCoverLetter", String.class,
                    new Class<?>[] { String.class }, new Object[] { "Generated text" });
            assertNull(path);
            assertEquals(1, authMock.constructed().size());
        }
    }

    @Test
    void generateSimplePdf_returnsValidHeader() throws Exception {
        EditorView view = createViewWithDefaults();

        byte[] pdf = invoke(view, "generateSimplePdf", byte[].class,
                new Class<?>[] { String.class }, new Object[] { "Line 1\nLine 2" });

        assertNotNull(pdf);
        assertTrue(pdf.length > 20);
        assertEquals("%PDF-1.4", new String(pdf, 0, 8));
    }

    @Test
    void downloadMethods_showNotificationWhenEditorEmpty() throws Exception {
        EditorView view = createViewWithDefaults();

        TextArea editor = getField(view, "editorArea", TextArea.class);
        editor.setValue("");

        try (MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {
            notificationMock.when(() -> Notification.show(anyString(), Mockito.anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            invokeNoArgs(view, "downloadAsDocx");
            invokeNoArgs(view, "downloadAsPdf");

            notificationMock.verify(() -> Notification.show(Mockito.contains("Nothing to export"), Mockito.anyInt(), any()),
                    Mockito.atLeastOnce());
        }
    }

    @Test
    void downloadAsDocx_coversExportFailureAndExceptionBranches() throws Exception {
        EditorView view = createViewWithDefaults();
        TextArea editor = getField(view, "editorArea", TextArea.class);
        editor.setValue("Generated cover letter body");

        DocumentService documentService = getField(view, "documentService", DocumentService.class);

        try (MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {
            notificationMock.when(() -> Notification.show(anyString(), anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            when(documentService.exportResumeAsDocument(any(), anyString())).thenReturn(false);
            invokeNoArgs(view, "downloadAsDocx");
            notificationMock.verify(() -> Notification.show(Mockito.contains("Export failed"), anyInt(), any()),
                    Mockito.atLeastOnce());
        }

        try (MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {
            notificationMock.when(() -> Notification.show(anyString(), anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            when(documentService.exportResumeAsDocument(any(), anyString()))
                    .thenThrow(new RuntimeException("doc service down"));
            invokeNoArgs(view, "downloadAsDocx");
            notificationMock.verify(() -> Notification.show(Mockito.contains("Export failed:"), anyInt(), any()),
                    Mockito.atLeastOnce());
        }
    }

    @Test
    void toolbarListButton_appendsBulletLine() throws Exception {
        EditorView view = createViewWithDefaults();
        HorizontalLayout toolbar = invoke(view, "createToolbar", HorizontalLayout.class);
        TextArea editor = getField(view, "editorArea", TextArea.class);

        editor.setValue("First line");
        Button listBtn = (Button) toolbar.getComponentAt(3);
        listBtn.click();

        assertTrue(editor.getValue().contains("• "));
    }

    @Test
    void onAttach_handlesNullEditorAndExistingContentBranches() throws Exception {
        EditorView redirected = createViewWithMissingSessionData();
        Method onAttach = EditorView.class.getDeclaredMethod("onAttach", AttachEvent.class);
        onAttach.setAccessible(true);

        AttachEvent event = Mockito.mock(AttachEvent.class);
        when(event.getUI()).thenReturn(Mockito.mock(UI.class));

        // editorArea is null in redirected case: should return immediately.
        invokeVoid(redirected, "onAttach", new Class<?>[] { AttachEvent.class }, new Object[] { event });

        EditorView view = createViewWithDefaults();
        TextArea editor = getField(view, "editorArea", TextArea.class);
        setField(view, "existingContent", "Existing letter content");

        invokeVoid(view, "onAttach", new Class<?>[] { AttachEvent.class }, new Object[] { event });

        assertEquals("Existing letter content", editor.getValue());
        assertTrue(editor.isEnabled());
    }

    @Test
    void onAttach_generationBranch_setsLoadingState() throws Exception {
        EditorView view = createViewWithDefaults();
        setField(view, "existingContent", null);

        Method onAttach = EditorView.class.getDeclaredMethod("onAttach", AttachEvent.class);
        onAttach.setAccessible(true);

        AttachEvent event = Mockito.mock(AttachEvent.class);
        UI ui = Mockito.mock(UI.class);
        when(event.getUI()).thenReturn(ui);

        TextArea editor = getField(view, "editorArea", TextArea.class);
        invokeVoid(view, "onAttach", new Class<?>[] { AttachEvent.class }, new Object[] { event });

        assertTrue(editor.getValue().contains("Generating your cover letter"));
        assertFalse(editor.isEnabled());
    }

    @Test
    void downloadAsDocx_successPath_coversRetrieveAndServe() throws Exception {
        EditorView view = createViewWithDefaults();
        TextArea editor = getField(view, "editorArea", TextArea.class);
        editor.setValue("Generated cover letter body");

        DocumentService documentService = getField(view, "documentService", DocumentService.class);

        try (MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {
            notificationMock.when(() -> Notification.show(anyString(), anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            when(documentService.exportResumeAsDocument(any(), anyString())).thenReturn(true);
            when(documentService.retrieveResumeFile(anyString())).thenReturn(new byte[] { 1, 2, 3, 4 });

            invokeNoArgs(view, "downloadAsDocx");

            verify(documentService).retrieveResumeFile(anyString());
            notificationMock.verify(() -> Notification.show(Mockito.contains("Export failed:"), anyInt(), any()),
                    Mockito.atLeastOnce());
        }
    }

    @Test
    void downloadAsPdf_executesGenerationPathAndHandlesUnitContextFailure() throws Exception {
        EditorView view = createViewWithDefaults();
        TextArea editor = getField(view, "editorArea", TextArea.class);
        editor.setValue("Generated cover letter body\nWith multiple lines.");

        try (MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {
            notificationMock.when(() -> Notification.show(anyString(), anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            invokeNoArgs(view, "downloadAsPdf");

            // In unit context, stream registry is absent; method should fail gracefully.
            notificationMock.verify(() -> Notification.show(Mockito.contains("Export failed:"), anyInt(), any()),
                    Mockito.atLeastOnce());
        }
    }

    private EditorView createViewWithMissingSessionData() {
        VaadinSession session = Mockito.mock(VaadinSession.class);
        when(session.getAttribute(anyString())).thenReturn(null);
        vaadinSessionMock.when(VaadinSession::getCurrent).thenReturn(session);

        DocumentService documentService = Mockito.mock(DocumentService.class);
        AIService aiService = Mockito.mock(AIService.class);

        try (MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {
            notificationMock.when(() -> Notification.show(anyString(), Mockito.anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));
            return new EditorView(documentService, aiService);
        }
    }

    private EditorView createViewWithDefaults() {
        VaadinSession session = mockSession("Backend Developer", "Acme", "Professional", "Build APIs");
        vaadinSessionMock.when(VaadinSession::getCurrent).thenReturn(session);

        User user = new User("a@b.com", "tester", "Password1!x", "Test", "User");
        user.setPin(1001);

        DocumentService documentService = Mockito.mock(DocumentService.class);
        AIService aiService = Mockito.mock(AIService.class);

        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(user))) {
            EditorView view = new EditorView(documentService, aiService);
            setField(view, "selectedSkills", Set.of("Java", "Spring"));
            return view;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private VaadinSession mockSession(String title, String company, String tone, String description) {
        VaadinSession session = Mockito.mock(VaadinSession.class);
        when(session.getAttribute("gen.jobTitle")).thenReturn(title);
        when(session.getAttribute("gen.company")).thenReturn(company);
        when(session.getAttribute("gen.tone")).thenReturn(tone);
        when(session.getAttribute("gen.jobDesc")).thenReturn(description);
        when(session.getAttribute("gen.existingContent")).thenReturn(null);
        when(session.getAttribute("gen.skills")).thenReturn(Set.of("Java", "Spring"));
        return session;
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

    private void invokeNoArgs(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }

    private void invokeVoid(Object target, String methodName, Class<?>[] parameterTypes, Object[] args) throws Exception {
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

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
