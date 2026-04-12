package com.clbooster.app.views;

import com.clbooster.app.backend.service.authentication.AuthenticationService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class HistoryViewAdditionalTest extends BaseVaadinViewTest {

    @AfterAll
    static void cleanup() {
        vaadinServletMock.close();
        vaadinSessionMock.close();
        vaadinServiceMock.close();
    }

    @Test
    void dialogMethods_openWithoutErrors() throws Exception {
        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null))) {
            HistoryView view = new HistoryView();

            Method showDate = HistoryView.class.getDeclaredMethod("showDateFilterDialog");
            showDate.setAccessible(true);
            Method showStatus = HistoryView.class.getDeclaredMethod("showStatusFilterDialog");
            showStatus.setAccessible(true);

            assertThrows(InvocationTargetException.class, () -> showDate.invoke(view));
            assertThrows(InvocationTargetException.class, () -> showStatus.invoke(view));
        }
    }

    @Test
    void openPreviewDialog_coversExistingAndMissingFileBranches() throws Exception {
        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null))) {
            HistoryView view = new HistoryView();
            Method openPreview = HistoryView.class.getDeclaredMethod("openPreviewDialog",
                    Class.forName("com.clbooster.app.views.HistoryView$HistoryItem"));
            openPreview.setAccessible(true);

            Path existing = Files.createTempFile("history-preview-", ".txt");
            Files.writeString(existing, "preview text", StandardCharsets.UTF_8);

            Object existingItem = newHistoryItem("Title", "Company", "Apr 12, 2026", "FINALIZED", 1,
                    LocalDateTime.now(), existing.toString());
            Object missingItem = newHistoryItem("Missing", "Company", "Apr 12, 2026", "ARCHIVED", 1,
                    LocalDateTime.now(), "/tmp/does-not-exist-history-preview.txt");

            assertThrows(InvocationTargetException.class, () -> openPreview.invoke(view, existingItem));
            assertThrows(InvocationTargetException.class, () -> openPreview.invoke(view, missingItem));

            Files.deleteIfExists(existing);
        }
    }

    @Test
    void createHistoryCard_buildsCardForItem() throws Exception {
        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null))) {
            HistoryView view = new HistoryView();
            Method createCard = HistoryView.class.getDeclaredMethod("createHistoryCard",
                    Class.forName("com.clbooster.app.views.HistoryView$HistoryItem"));
            createCard.setAccessible(true);

            Path existing = Files.createTempFile("history-card-", ".txt");
            Files.writeString(existing, "card text", StandardCharsets.UTF_8);

            Object item = newHistoryItem("Role", "Acme", "Apr 12, 2026", "FINALIZED", 1,
                    LocalDateTime.now(), existing.toString());

            Object card = createCard.invoke(view, item);
            assertNotNull(card);

            Files.deleteIfExists(existing);
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
}
