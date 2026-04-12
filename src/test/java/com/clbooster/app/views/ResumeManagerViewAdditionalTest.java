package com.clbooster.app.views;

import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.clbooster.app.backend.service.document.DocumentService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ResumeManagerViewAdditionalTest extends BaseVaadinViewTest {

    @AfterAll
    static void cleanup() {
        vaadinServletMock.close();
        vaadinSessionMock.close();
        vaadinServiceMock.close();
    }

    @Test
    void styleActiveTab_andCreateFormatBadge_applyExpectedStyles() throws Exception {
        ResumeManagerView view = createView(Mockito.mock(DocumentService.class));

        Method styleActiveTab = ResumeManagerView.class.getDeclaredMethod("styleActiveTab", Button.class,
                boolean.class);
        styleActiveTab.setAccessible(true);

        Button button = new Button("Tab");
        styleActiveTab.invoke(view, button, true);
        assertEquals("700", button.getStyle().get("font-weight"));

        styleActiveTab.invoke(view, button, false);
        assertEquals("500", button.getStyle().get("font-weight"));

        Method createFormatBadge = ResumeManagerView.class.getDeclaredMethod("createFormatBadge", String.class);
        createFormatBadge.setAccessible(true);

        Span badge = (Span) createFormatBadge.invoke(view, "PDF");
        assertEquals("PDF", badge.getText());
        assertNotNull(badge.getStyle().get("background"));
    }

    @Test
    void downloadAndViewResume_coverErrorBranches() throws Exception {
        DocumentService docService = Mockito.mock(DocumentService.class);
        when(docService.retrieveResumeFile("/tmp/resume-error.docx")).thenThrow(new IOException("boom"));

        ResumeManagerView view = createView(docService);
        Method download = ResumeManagerView.class.getDeclaredMethod("downloadResume",
                Class.forName("com.clbooster.app.views.ResumeManagerView$ResumeData"));
        Method viewResume = ResumeManagerView.class.getDeclaredMethod("viewResume",
                Class.forName("com.clbooster.app.views.ResumeManagerView$ResumeData"));
        download.setAccessible(true);
        viewResume.setAccessible(true);

        Object failing = newResumeData("resume-error.docx", "DOCX", "1.0 KB", "Today", "/tmp/resume-error.docx",
                LocalDateTime.now(), false);

        try (MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {
            notificationMock.when(() -> Notification.show(anyString(), anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            download.invoke(view, failing);
            viewResume.invoke(view, failing);

            verify(docService, Mockito.atLeastOnce()).retrieveResumeFile("/tmp/resume-error.docx");
            notificationMock.verify(() -> Notification.show(anyString(), anyInt(), any()), Mockito.atLeastOnce());
        }
    }

    @Test
    void createPasteTextPanel_saveButtonCoversValidationAndSuccessPath() throws Exception {
        DocumentService docService = Mockito.mock(DocumentService.class);
        when(docService.storeResumeText(anyString(), anyString(), anyString())).thenReturn("uploads/resumes/stored.txt");

        ResumeManagerView view = createView(docService);
        Method createPastePanel = ResumeManagerView.class.getDeclaredMethod("createPasteTextPanel");
        createPastePanel.setAccessible(true);

        Div panel = (Div) createPastePanel.invoke(view);
        Component inner = panel.getComponentAt(0);
        TextArea textArea = (TextArea) ((com.vaadin.flow.component.orderedlayout.VerticalLayout) inner).getComponentAt(1);
        TextField filenameField = (TextField) ((com.vaadin.flow.component.orderedlayout.VerticalLayout) inner)
                .getComponentAt(2);
        Button saveButton = (Button) ((com.vaadin.flow.component.orderedlayout.VerticalLayout) inner).getComponentAt(3);

        try (MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {
            notificationMock.when(() -> Notification.show(anyString(), anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            textArea.setValue("   ");
            saveButton.click();

            textArea.setValue("resume as text");
            filenameField.setValue("my_resume");
            saveButton.click();

            verify(docService).storeResumeText("resume as text", "my_resume.txt", "0");
            assertTrue(textArea.getValue().isEmpty());
            assertTrue(filenameField.getValue().isEmpty());
            notificationMock.verify(() -> Notification.show(anyString(), anyInt(), any()), Mockito.atLeastOnce());
        }
    }

    @Test
    void createUploadZone_returnsContainer() throws Exception {
        ResumeManagerView view = createView(Mockito.mock(DocumentService.class));
        Method createUploadZone = ResumeManagerView.class.getDeclaredMethod("createUploadZone");
        createUploadZone.setAccessible(true);

        Object container = createUploadZone.invoke(view);
        assertNotNull(container);
    }

    private ResumeManagerView createView(DocumentService documentService) {
        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null))) {
            return new ResumeManagerView(documentService);
        }
    }

    private Object newResumeData(String name, String format, String size, String date, String filePath,
            LocalDateTime modified, boolean starred) throws Exception {
        Class<?> clazz = Class.forName("com.clbooster.app.views.ResumeManagerView$ResumeData");
        Constructor<?> constructor = clazz.getDeclaredConstructor(String.class, String.class, String.class,
                String.class, String.class, LocalDateTime.class, boolean.class);
        constructor.setAccessible(true);
        return constructor.newInstance(name, format, size, date, filePath, modified, starred);
    }
}
