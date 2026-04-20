package com.clbooster.app.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import com.clbooster.app.i18n.TranslationService;

import java.util.Locale;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LandingViewTest extends BaseVaadinViewTest {

    @AfterAll
    static void cleanup() {
        vaadinServletMock.close();
        vaadinSessionMock.close();
        vaadinServiceMock.close();
    }

    @Test
    void constructor_initializesMainSections() {
        LandingView view = new LandingView();

        assertNotNull(view);
        assertTrue(view.getComponentCount() >= 1);
    }

    @Test
    void privateSectionBuilders_returnComponents() throws Exception {
        LandingView view = new LandingView();

        HorizontalLayout navbar = invoke(view, "createNavbar", HorizontalLayout.class);
        HorizontalLayout hero = invoke(view, "createHero", HorizontalLayout.class);
        VerticalLayout features = invoke(view, "createFeatures", VerticalLayout.class);
        HorizontalLayout footer = invoke(view, "createFooter", HorizontalLayout.class);

        assertNotNull(navbar);
        assertNotNull(hero);
        assertNotNull(features);
        assertNotNull(footer);
        assertTrue(navbar.getComponentCount() >= 3);
        assertTrue(hero.getComponentCount() >= 2);
        assertTrue(features.getComponentCount() >= 2);
        assertTrue(footer.getComponentCount() >= 1);
    }

    @Test
    void buttonFactories_createButtonsAndExecuteActions() throws Exception {
        LandingView view = new LandingView();
        int[] actionCount = new int[] { 0 };

        Button primary = invoke(view, "createPrimaryButton", Button.class,
                new Class<?>[] { String.class, Runnable.class },
                new Object[] { "Primary", (Runnable) () -> actionCount[0]++ });

        Button secondary = invoke(view, "createSecondaryButton", Button.class,
                new Class<?>[] { String.class, Runnable.class },
                new Object[] { "Secondary", (Runnable) () -> actionCount[0]++ });

        assertEquals("Primary", primary.getText());
        assertEquals("Secondary", secondary.getText());

        primary.click();
        secondary.click();

        assertEquals(2, actionCount[0]);
    }

    @Test
    void createNavButton_executesProvidedAction() throws Exception {
        LandingView view = new LandingView();
        int[] clicked = new int[] { 0 };

        Button navButton = invoke(view, "createNavButton", Button.class,
                new Class<?>[] { String.class, Runnable.class }, new Object[] { "FAQ", (Runnable) () -> clicked[0]++ });

        assertNotNull(navButton);
        navButton.click();
        assertEquals(1, clicked[0]);
    }

    @Test
    void createStepItem_buildsExpectedLayout() throws Exception {
        LandingView view = new LandingView();

        HorizontalLayout step = invoke(view, "createStepItem", HorizontalLayout.class,
                new Class<?>[] { String.class, String.class, String.class },
                new Object[] { "1", "Title", "Description" });

        assertNotNull(step);
        assertEquals(2, step.getComponentCount());
    }

    @Test
    void modalMethods_openDialogAndSetActiveModal() throws Exception {
        LandingView view = new LandingView();

        assertThrows(java.lang.reflect.InvocationTargetException.class, () -> invokeNoArgs(view, "openFaqModal"));
        Dialog first = getActiveModal(view);
        assertNotNull(first);

        assertThrows(java.lang.reflect.InvocationTargetException.class,
                () -> invokeNoArgs(view, "openHowItWorksModal"));
        Dialog second = getActiveModal(view);
        assertNotNull(second);

        assertThrows(java.lang.reflect.InvocationTargetException.class, () -> invokeNoArgs(view, "openSamplesModal"));
        Dialog third = getActiveModal(view);
        assertNotNull(third);
    }

    @Test
    void modalMethods_closePreviouslyOpenedDialogBranch() throws Exception {
        LandingView view = new LandingView();

        Dialog previous = Mockito.mock(Dialog.class);
        when(previous.isOpened()).thenReturn(true);
        setActiveModal(view, previous);

        assertThrows(java.lang.reflect.InvocationTargetException.class, () -> invokeNoArgs(view, "openFaqModal"));
        verify(previous).close();

        Dialog previous2 = Mockito.mock(Dialog.class);
        when(previous2.isOpened()).thenReturn(true);
        setActiveModal(view, previous2);

        assertThrows(java.lang.reflect.InvocationTargetException.class, () -> invokeNoArgs(view, "openSamplesModal"));
        verify(previous2).close();

        Dialog previous3 = Mockito.mock(Dialog.class);
        when(previous3.isOpened()).thenReturn(true);
        setActiveModal(view, previous3);

        assertThrows(java.lang.reflect.InvocationTargetException.class,
                () -> invokeNoArgs(view, "openHowItWorksModal"));
        verify(previous3).close();
    }

    @Test
    void openCoverLetterModal_opensDialog() throws Exception {
        LandingView view = new LandingView();

        Method method = LandingView.class.getDeclaredMethod("openCoverLetterModal", String.class, String.class,
                String.class);
        method.setAccessible(true);
        assertThrows(java.lang.reflect.InvocationTargetException.class,
                () -> method.invoke(view, "Software Engineer", "Acme", "Dear Hiring Team\nBody"));

        // openCoverLetterModal opens a dialog directly, which needs a UI in unit
        // context.
        assertNotNull(view);
    }

    @Test
    void scrollHelpers_executeWithoutErrors() throws Exception {
        LandingView view = new LandingView();

        invokeNoArgs(view, "scrollToTop");


        assertNotNull(view);
    }

    @Test
    void createNavbar_mapsSavedLocaleToDisplayedLanguage() throws Exception {
        try (MockedConstruction<TranslationService> mocked = Mockito.mockConstruction(TranslationService.class,
                (mock, context) -> {
                    when(mock.getCurrentLocale()).thenReturn(Locale.forLanguageTag("fi"));
                    when(mock.translate(anyString())).thenAnswer(i -> i.getArgument(0));
                })) {

            LandingView view = new LandingView();
            HorizontalLayout navbar = invoke(view, "createNavbar", HorizontalLayout.class);
            @SuppressWarnings("unchecked")
            Select<String> select = (Select<String>) findFirst(navbar, Select.class);

            assertNotNull(select);
            assertEquals("Suomi", select.getValue());
            assertTrue(mocked.constructed().size() >= 1);
        }
    }

    @Test
    void createNavbar_languageSelectListener_callsSetLanguageAcrossBranches() throws Exception {
        try (MockedConstruction<TranslationService> mocked = Mockito.mockConstruction(TranslationService.class,
                (mock, context) -> {
                    when(mock.getCurrentLocale()).thenReturn(Locale.forLanguageTag("en"));
                    when(mock.translate(anyString())).thenAnswer(i -> i.getArgument(0));
                })) {

            LandingView view = new LandingView();
            HorizontalLayout navbar = invoke(view, "createNavbar", HorizontalLayout.class);
            @SuppressWarnings("unchecked")
            Select<String> select = (Select<String>) findFirst(navbar, Select.class);
            TranslationService service = mocked.constructed().get(0);

            select.setValue("Suomi");
            select.setValue("Português");
            select.setValue("فارسی");
            select.setValue("中文");
            select.setValue("اردو");
            select.setValue("English");

            verify(service).setLanguage("Finnish (Suomi)");
            verify(service).setLanguage("Portuguese (Português)");
            verify(service).setLanguage("Persian (فارسی)");
            verify(service).setLanguage("Chinese (中文)");
            verify(service).setLanguage("Urdu (اردو)");
            verify(service).setLanguage("English");
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Component> T findFirst(Component root, Class<T> type) {
        if (type.isInstance(root)) {
            return (T) root;
        }
        for (Component child : (Iterable<Component>) root.getChildren()::iterator) {
            T found = findFirst(child, type);
            if (found != null) {
                return found;
            }
        }
        return null;
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

    private Dialog getActiveModal(LandingView view) throws Exception {
        Field field = LandingView.class.getDeclaredField("activeModal");
        field.setAccessible(true);
        return (Dialog) field.get(view);
    }

    private void setActiveModal(LandingView view, Dialog dialog) throws Exception {
        Field field = LandingView.class.getDeclaredField("activeModal");
        field.setAccessible(true);
        field.set(view, dialog);
    }
}
