package com.clbooster.app.views;

import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.vaadin.flow.component.notification.Notification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class SignUpViewTest extends BaseVaadinViewTest {

    @AfterAll
    static void cleanup() {
        vaadinServletMock.close();
        vaadinSessionMock.close();
        vaadinServiceMock.close();
    }

    @Test
    void constructor_initializesView() {
        try (MockedConstruction<AuthenticationService> ignored = Mockito
                .mockConstruction(AuthenticationService.class)) {
            SignUpView view = new SignUpView();
            assertNotNull(view);
        }
    }

    @Test
    void calculatePasswordStrength_scoresWeakToStrong() throws Exception {
        try (MockedConstruction<AuthenticationService> ignored = Mockito
                .mockConstruction(AuthenticationService.class)) {
            SignUpView view = new SignUpView();
            Method method = SignUpView.class.getDeclaredMethod("calculatePasswordStrength", String.class);
            method.setAccessible(true);

            int weak = (int) method.invoke(view, "abc");
            int medium = (int) method.invoke(view, "abcdefghij12");
            int strong = (int) method.invoke(view, "Abcdefghijk12!");

            assertTrue(weak < medium);
            assertTrue(medium <= strong);
            assertEquals(4, strong);
        }
    }

    @Test
    void updatePasswordStrength_updatesIndicatorText() throws Exception {
        try (MockedConstruction<AuthenticationService> ignored = Mockito
                .mockConstruction(AuthenticationService.class)) {
            SignUpView view = new SignUpView();
            Method method = SignUpView.class.getDeclaredMethod("updatePasswordStrength", String.class);
            method.setAccessible(true);

            method.invoke(view, "");
            String emptyText = getSpanText(view, "strengthText");
            assertTrue(emptyText.contains("Password strength"));

            method.invoke(view, "Abcdefghijk12!");
            String strongText = getSpanText(view, "strengthText");
            assertTrue(strongText.contains("Strong"));
        }
    }

    @Test
    void handleRegistration_emptyFirstName_showsError() throws Exception {
        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class);
                MockedConstruction<Notification> notificationMock = Mockito.mockConstruction(Notification.class)) {
            SignUpView view = new SignUpView();

            setFieldValue(view, "firstNameField", "");
            setFieldValue(view, "lastNameField", "User");
            setFieldValue(view, "usernameField", "tester");
            setFieldValue(view, "emailField", "test@example.com");
            setFieldValue(view, "passwordField", "Abcdefghij12!");

            Method method = SignUpView.class.getDeclaredMethod("handleRegistration");
            method.setAccessible(true);
            method.invoke(view);

            assertTrue(notificationMock.constructed().size() >= 1);
        }
    }

    @Test
    void handleRegistration_success_callsAuthRegisterAndLogin() throws Exception {
        try (MockedConstruction<AuthenticationService> authMocked = Mockito
                .mockConstruction(AuthenticationService.class, (mock, context) -> {
                    when(mock.register(anyString(), anyString(), anyString(), anyString(), anyString()))
                            .thenReturn(true);
                    when(mock.login(anyString(), anyString())).thenReturn(true);
                });
                MockedConstruction<Notification> ignoredNotification = Mockito.mockConstruction(Notification.class)) {
            SignUpView view = new SignUpView();

            setFieldValue(view, "firstNameField", "Test");
            setFieldValue(view, "lastNameField", "User");
            setFieldValue(view, "usernameField", "tester");
            setFieldValue(view, "emailField", "test@example.com");
            setFieldValue(view, "passwordField", "Abcdefghij12!");

            Method method = SignUpView.class.getDeclaredMethod("handleRegistration");
            method.setAccessible(true);
            method.invoke(view);

            AuthenticationService auth = authMocked.constructed().get(0);
            Mockito.verify(auth).register("test@example.com", "tester", "Abcdefghij12!", "Test", "User");
            Mockito.verify(auth).login("tester", "Abcdefghij12!");
        }
    }

    @Test
    void handleRegistration_registrationFails_doesNotLogin() throws Exception {
        try (MockedConstruction<AuthenticationService> authMocked = Mockito.mockConstruction(
                AuthenticationService.class,
                (mock, context) -> when(mock.register(anyString(), anyString(), anyString(), anyString(), anyString()))
                        .thenReturn(false));
                MockedConstruction<Notification> ignoredNotification = Mockito.mockConstruction(Notification.class)) {
            SignUpView view = new SignUpView();

            setFieldValue(view, "firstNameField", "Test");
            setFieldValue(view, "lastNameField", "User");
            setFieldValue(view, "usernameField", "tester");
            setFieldValue(view, "emailField", "test@example.com");
            setFieldValue(view, "passwordField", "Abcdefghij12!");

            Method method = SignUpView.class.getDeclaredMethod("handleRegistration");
            method.setAccessible(true);
            method.invoke(view);

            AuthenticationService auth = authMocked.constructed().get(0);
            Mockito.verify(auth).register("test@example.com", "tester", "Abcdefghij12!", "Test", "User");
            Mockito.verify(auth, Mockito.never()).login(anyString(), anyString());
        }
    }

    @Test
    void handleRegistration_validationBranches_usernameEmailPassword() throws Exception {
        try (MockedConstruction<AuthenticationService> authMocked = Mockito
                .mockConstruction(AuthenticationService.class);
                MockedConstruction<Notification> notificationMock = Mockito.mockConstruction(Notification.class)) {
            SignUpView view = new SignUpView();

            Method method = SignUpView.class.getDeclaredMethod("handleRegistration");
            method.setAccessible(true);

            // Missing username
            setFieldValue(view, "firstNameField", "Test");
            setFieldValue(view, "lastNameField", "User");
            setFieldValue(view, "usernameField", " ");
            setFieldValue(view, "emailField", "test@example.com");
            setFieldValue(view, "passwordField", "Abcdefghij12!");
            method.invoke(view);

            // Missing email
            setFieldValue(view, "usernameField", "tester");
            setFieldValue(view, "emailField", " ");
            method.invoke(view);

            // Missing password
            setFieldValue(view, "emailField", "test@example.com");
            setFieldValue(view, "passwordField", " ");
            method.invoke(view);

            AuthenticationService auth = authMocked.constructed().get(0);
            Mockito.verify(auth, Mockito.never()).register(anyString(), anyString(), anyString(), anyString(),
                    anyString());
            assertTrue(notificationMock.constructed().size() >= 3);
        }
    }

    @Test
    void updatePasswordStrength_coversFairAndGoodLabels() throws Exception {
        try (MockedConstruction<AuthenticationService> ignored = Mockito
                .mockConstruction(AuthenticationService.class)) {
            SignUpView view = new SignUpView();
            Method method = SignUpView.class.getDeclaredMethod("updatePasswordStrength", String.class);
            method.setAccessible(true);

            method.invoke(view, "Abcdefghij");
            String fairText = getSpanText(view, "strengthText");

            method.invoke(view, "Abcdefghi1");
            String goodText = getSpanText(view, "strengthText");

            assertTrue(fairText.contains("Fair"));
            assertTrue(goodText.contains("Good"));
        }
    }

    @Test
    void showSuccessAndError_createNotifications() {
        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class);
                MockedConstruction<Notification> notificationMock = Mockito.mockConstruction(Notification.class)) {

            com.clbooster.app.views.util.AuthComponents.showSuccess("ok");
            com.clbooster.app.views.util.AuthComponents.showError("bad");

            assertTrue(notificationMock.constructed().size() >= 2);
        }
    }

    private void setFieldValue(SignUpView view, String fieldName, String value) throws Exception {
        Field field = SignUpView.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        Object component = field.get(view);
        Method setValue = component.getClass().getMethod("setValue", String.class);
        setValue.invoke(component, value);
    }

    private String getSpanText(SignUpView view, String fieldName) throws Exception {
        Field field = SignUpView.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        Object span = field.get(view);
        Method getText = span.getClass().getMethod("getText");
        return (String) getText.invoke(span);
    }
}
