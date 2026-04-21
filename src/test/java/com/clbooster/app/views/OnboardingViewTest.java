package com.clbooster.app.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OnboardingViewTest extends BaseVaadinViewTest {

    @AfterAll
    static void cleanup() {
        vaadinServletMock.close();
        vaadinSessionMock.close();
        vaadinServiceMock.close();
    }

    @Test
    void constructor_initializesLayoutAndFields() throws Exception {
        OnboardingView view = new OnboardingView();

        assertNotNull(view);
        assertEquals(1, getIntField(view, "currentStep"));
        assertNotNull(getField(view, "contentArea", VerticalLayout.class));
        assertNotNull(getField(view, "stepIndicator", HorizontalLayout.class));
        assertNotNull(getField(view, "nextButton", Button.class));
    }

    @Test
    void builders_createHeaderIndicatorAndNavigation() throws Exception {
        OnboardingView view = new OnboardingView();

        HorizontalLayout header = invoke(view, "createProgressHeader", HorizontalLayout.class);
        HorizontalLayout indicator = invoke(view, "createStepIndicator", HorizontalLayout.class);
        HorizontalLayout nav = invoke(view, "createNavigation", HorizontalLayout.class);

        assertNotNull(header);
        assertNotNull(indicator);
        assertNotNull(nav);
        assertTrue(header.getComponentCount() >= 2);
        assertTrue(indicator.getComponentCount() >= 5);
        assertEquals(3, nav.getComponentCount());
    }

    @Test
    void createStepBadge_handlesPastCurrentFutureStates() throws Exception {
        OnboardingView view = new OnboardingView();

        setIntField(view, "currentStep", 2);

        Div past = invoke(view, "createStepBadge", Div.class,
                new Class<?>[] { int.class, String.class, com.vaadin.flow.component.icon.VaadinIcon.class },
                new Object[] { 1, "Profile", com.vaadin.flow.component.icon.VaadinIcon.USER });

        Div current = invoke(view, "createStepBadge", Div.class,
                new Class<?>[] { int.class, String.class, com.vaadin.flow.component.icon.VaadinIcon.class },
                new Object[] { 2, "Career", com.vaadin.flow.component.icon.VaadinIcon.BRIEFCASE });

        Div future = invoke(view, "createStepBadge", Div.class,
                new Class<?>[] { int.class, String.class, com.vaadin.flow.component.icon.VaadinIcon.class },
                new Object[] { 3, "Preferences", com.vaadin.flow.component.icon.VaadinIcon.COG });

        assertNotNull(past);
        assertNotNull(current);
        assertNotNull(future);
        assertEquals(2, past.getComponentCount());
        assertEquals(2, current.getComponentCount());
        assertEquals(2, future.getComponentCount());
    }

    @Test
    void showStep_updatesStateAndRendersContent() throws Exception {
        OnboardingView view = new OnboardingView();

        invokeVoid(view, "showStep", new Class<?>[] { int.class }, new Object[] { 2 });
        assertEquals(2, getIntField(view, "currentStep"));
        VerticalLayout contentAt2 = getField(view, "contentArea", VerticalLayout.class);
        assertTrue(contentAt2.getComponentCount() >= 3);

        invokeVoid(view, "showStep", new Class<?>[] { int.class }, new Object[] { 3 });
        assertEquals(3, getIntField(view, "currentStep"));
        Button next = getField(view, "nextButton", Button.class);
        assertEquals("Complete Setup", next.getText());
    }

    @Test
    void explicitStepRenderers_populateContentArea() throws Exception {
        OnboardingView view = new OnboardingView();

        invokeNoArgs(view, "showProfileStep");
        VerticalLayout content = getField(view, "contentArea", VerticalLayout.class);
        assertTrue(content.getComponentCount() >= 2);

        invokeNoArgs(view, "showCareerStep");
        assertTrue(content.getComponentCount() >= 4);

        invokeNoArgs(view, "showPreferencesStep");
        assertTrue(content.getComponentCount() >= 4);
    }

    @Test
    void helperFactories_createInteractiveComponents() throws Exception {
        OnboardingView view = new OnboardingView();
        int[] clicks = new int[] { 0 };

        VerticalLayout formGroup = invoke(view, "createFormGroup", VerticalLayout.class,
                new Class<?>[] { String.class, com.vaadin.flow.component.Component.class,
                        com.vaadin.flow.component.icon.VaadinIcon.class },
                new Object[] { "Name", new TextField(), com.vaadin.flow.component.icon.VaadinIcon.USER });

        Button chip = invoke(view, "createChip", Button.class,
                new Class<?>[] { String.class, boolean.class, Runnable.class },
                new Object[] { "Tech", false, (Runnable) () -> clicks[0]++ });

        Div radio = invoke(view, "createRadioOption", Div.class,
                new Class<?>[] { String.class, boolean.class, Runnable.class },
                new Object[] { "Mid Level", true, (Runnable) () -> clicks[0]++ });

        Div tone = invoke(view, "createToneOption", Div.class,
                new Class<?>[] { String.class, String.class, String.class, String.class },
                new Object[] { "Professional", "Formal", "#007AFF", "BRIEFCASE" });

        VerticalLayout summaryStat = invoke(view, "createSummaryStat", VerticalLayout.class,
                new Class<?>[] { String.class, String.class }, new Object[] { "Tone", "Professional" });

        assertNotNull(formGroup);
        assertNotNull(chip);
        assertNotNull(radio);
        assertNotNull(tone);
        assertNotNull(summaryStat);

        chip.click();
        assertEquals(1, clicks[0]);
    }

    @Test
    void handleNext_advancesAndCompletesWithNotification() throws Exception {
        OnboardingView view = new OnboardingView();

        invokeNoArgs(view, "handleNext");
        assertEquals(2, getIntField(view, "currentStep"));

        invokeNoArgs(view, "handleNext");
        assertEquals(3, getIntField(view, "currentStep"));

        try (MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {
            notificationMock.when(() -> Notification.show(Mockito.anyString(), Mockito.anyInt(), Mockito.any()))
                    .thenReturn(Mockito.mock(Notification.class));

            invokeNoArgs(view, "handleNext");

            notificationMock.verify(() -> Notification.show(Mockito.contains("Welcome to CL Booster"), Mockito.eq(4000),
                    Mockito.eq(Notification.Position.BOTTOM_CENTER)));
        }
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

    private void invokeNoArgs(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }

    @SuppressWarnings("unchecked")
    private <T> T getField(Object target, String fieldName, Class<T> type) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(target);
    }

    private int getIntField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.getInt(target);
    }

    private void setIntField(Object target, String fieldName, int value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.setInt(target, value);
    }
}
