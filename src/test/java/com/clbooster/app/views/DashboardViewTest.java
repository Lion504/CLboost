package com.clbooster.app.views;

import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.clbooster.app.backend.service.profile.User;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class DashboardViewTest extends BaseVaadinViewTest {

    private static final Path LETTERS_DIR = Path.of("uploads", "coverletters");

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
            DashboardView view = new DashboardView();
            assertNotNull(view);
        }
    }

    @Test
    void toTitleCase_formatsWords() throws Exception {
        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null))) {
            DashboardView view = new DashboardView();
            Method method = DashboardView.class.getDeclaredMethod("toTitleCase", String.class);
            method.setAccessible(true);

            String result = (String) method.invoke(view, "sENIOR   prOduct deSIGNer");

            assertEquals("Senior Product Designer", result);
        }
    }

    @Test
    void loadLetterData_filtersByCurrentUser() throws Exception {
        Files.createDirectories(LETTERS_DIR);

        User user = new User("mail@test.com", "user", "Pass123!pass", "Test", "User");
        user.setPin(987654);

        Path own = LETTERS_DIR.resolve("987654_20260412_120000_apple_product-designer.docx");
        Path ownSecond = LETTERS_DIR.resolve("987654_20260412_120001_meta_react-engineer.docx");
        Path foreign = LETTERS_DIR.resolve("111111_20260412_120002_other_company.docx");

        Files.writeString(own, "a");
        Files.writeString(ownSecond, "b");
        Files.writeString(foreign, "c");

        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(user))) {
            DashboardView view = new DashboardView();
            Method method = DashboardView.class.getDeclaredMethod("loadLetterData");
            method.setAccessible(true);

            @SuppressWarnings("unchecked")
            List<Object> letters = (List<Object>) method.invoke(view);

            assertEquals(2, letters.size());
            assertTrue(getField(letters.get(0), "company", String.class).length() > 0);
            assertTrue(getField(letters.get(1), "title", String.class).length() > 0);
        } finally {
            Files.deleteIfExists(own);
            Files.deleteIfExists(ownSecond);
            Files.deleteIfExists(foreign);

            try {
                if (Files.exists(LETTERS_DIR) && Files.list(LETTERS_DIR).findAny().isEmpty()) {
                    Files.delete(LETTERS_DIR);
                }
            } catch (Exception ignoredCleanup) {
                // No-op: folder may contain project files.
            }
        }
    }

    @Test
    void countHelpers_returnZeroWhenNoUser() throws Exception {
        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null))) {
            DashboardView view = new DashboardView();

            Method countFiles = DashboardView.class.getDeclaredMethod("countFiles", String.class);
            countFiles.setAccessible(true);
            int files = (int) countFiles.invoke(view, "uploads/coverletters/");

            Method countWeek = DashboardView.class.getDeclaredMethod("countLettersThisWeek");
            countWeek.setAccessible(true);
            int week = (int) countWeek.invoke(view);

            assertEquals(0, files);
            assertEquals(0, week);
        }
    }

    @Test
    void loadLetterData_returnsFallbackWhenNoFilesForLoggedUser() throws Exception {
        User user = new User("mail@test.com", "user", "Pass123!pass", "Test", "User");
        user.setPin(424242);

        Path own = LETTERS_DIR.resolve("424242_20260412_120000_temp-company_temp-role.docx");
        Files.deleteIfExists(own);

        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(user))) {
            DashboardView view = new DashboardView();
            Method method = DashboardView.class.getDeclaredMethod("loadLetterData");
            method.setAccessible(true);

            @SuppressWarnings("unchecked")
            List<Object> letters = (List<Object>) method.invoke(view);

            assertTrue(letters.size() >= 3);
        }
    }

    @Test
    void filterLetters_filtersAndRestoresCardList() throws Exception {
        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null))) {
            DashboardView view = new DashboardView();

            HorizontalLayout grid = new HorizontalLayout();
            Div newCard = new Div();
            newCard.add(new Span("New Cover Letter"));
            grid.add(newCard);

            setField(view, "lettersGrid", grid);
            setField(view, "allLetters", List.of(newLetter("Senior Product Designer", "Apple", "Today", "FINALIZED"),
                    newLetter("React Engineer", "Meta", "Yesterday", "ARCHIVED")));

            Method filter = DashboardView.class.getDeclaredMethod("filterLetters", String.class);
            filter.setAccessible(true);

            filter.invoke(view, "apple");
            assertEquals(2, grid.getComponentCount());

            filter.invoke(view, "");
            assertEquals(3, grid.getComponentCount());
        }
    }

    private Object newLetter(String title, String company, String date, String status) throws Exception {
        Class<?> clazz = Class.forName("com.clbooster.app.views.DashboardView$LetterCardData");
        Constructor<?> c = clazz.getDeclaredConstructor(String.class, String.class, String.class, String.class);
        c.setAccessible(true);
        return c.newInstance(title, company, date, status);
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
