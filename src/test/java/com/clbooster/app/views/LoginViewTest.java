package com.clbooster.app.views;

import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoginViewTest extends BaseVaadinViewTest {

    @AfterAll
    static void cleanup() {
        vaadinServletMock.close();
        vaadinSessionMock.close();
        vaadinServiceMock.close();
    }

    @Test
    void constructor_initializesView() {
        try (MockedConstruction<AuthenticationService> mocked = Mockito.mockConstruction(AuthenticationService.class)) {
            LoginView view = new LoginView();
            assertNotNull(view);
            assertTrue(mocked.constructed().size() >= 1);
        }
    }

    @Test
    void handleLogin_emptyUsername_returnsEarly() throws Exception {
        try (MockedConstruction<AuthenticationService> mocked = Mockito.mockConstruction(AuthenticationService.class);
             MockedConstruction<Notification> ignored = Mockito.mockConstruction(Notification.class)) {
            LoginView view = new LoginView();
            AuthenticationService authMock = mocked.constructed().get(0);

            TextField username = getPrivateField(view, "usernameField", TextField.class);
            PasswordField password = getPrivateField(view, "passwordField", PasswordField.class);
            username.setValue("");
            password.setValue("Secret123!");

            invokeHandleLogin(view);

            verify(authMock, never()).login(anyString(), anyString());
        }
    }

    @Test
    void handleLogin_emptyPassword_returnsEarly() throws Exception {
        try (MockedConstruction<AuthenticationService> mocked = Mockito.mockConstruction(AuthenticationService.class);
             MockedConstruction<Notification> ignored = Mockito.mockConstruction(Notification.class)) {
            LoginView view = new LoginView();
            AuthenticationService authMock = mocked.constructed().get(0);

            TextField username = getPrivateField(view, "usernameField", TextField.class);
            PasswordField password = getPrivateField(view, "passwordField", PasswordField.class);
            username.setValue("user");
            password.setValue("   ");

            invokeHandleLogin(view);

            verify(authMock, never()).login(anyString(), anyString());
        }
    }

    @Test
    void handleLogin_failedLogin_clearsPassword() throws Exception {
        try (MockedConstruction<AuthenticationService> mocked = Mockito.mockConstruction(AuthenticationService.class);
             MockedConstruction<Notification> ignored = Mockito.mockConstruction(Notification.class)) {
            LoginView view = new LoginView();
            AuthenticationService authMock = mocked.constructed().get(0);
            when(authMock.login("user", "Secret123!")).thenReturn(false);

            TextField username = getPrivateField(view, "usernameField", TextField.class);
            PasswordField password = getPrivateField(view, "passwordField", PasswordField.class);
            username.setValue("user");
            password.setValue("Secret123!");

            invokeHandleLogin(view);

            verify(authMock).login("user", "Secret123!");
            assertTrue(password.getValue().isEmpty());
        }
    }

    @Test
    void handleLogin_success_callsAuthenticationService() throws Exception {
        try (MockedConstruction<AuthenticationService> mocked = Mockito.mockConstruction(AuthenticationService.class)) {
            LoginView view = new LoginView();
            AuthenticationService authMock = mocked.constructed().get(0);
            when(authMock.login("user", "Secret123!")).thenReturn(true);

            TextField username = getPrivateField(view, "usernameField", TextField.class);
            PasswordField password = getPrivateField(view, "passwordField", PasswordField.class);
            username.setValue("user");
            password.setValue("Secret123!");

            invokeHandleLogin(view);

            verify(authMock).login("user", "Secret123!");
        }
    }

    private void invokeHandleLogin(LoginView view) throws Exception {
        Method method = LoginView.class.getDeclaredMethod("handleLogin");
        method.setAccessible(true);
        method.invoke(view);
    }

    @SuppressWarnings("unchecked")
    private <T> T getPrivateField(Object target, String fieldName, Class<T> type) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(target);
    }
}
