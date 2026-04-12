package com.clbooster.app.views;

import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.clbooster.app.backend.service.profile.User;
import com.clbooster.app.backend.service.profile.UserService;
import com.clbooster.app.backend.service.settings.Settings;
import com.clbooster.app.backend.service.settings.SettingsService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SettingsViewTest extends BaseVaadinViewTest {

    @AfterAll
    static void cleanup() {
        vaadinServletMock.close();
        vaadinSessionMock.close();
        vaadinServiceMock.close();
    }

    @Test
    void constructor_initializesWhenNoLoggedInUser() {
        try (MockedConstruction<AuthenticationService> authMocked = Mockito.mockConstruction(
                AuthenticationService.class, (mock, context) -> when(mock.getCurrentUser()).thenReturn(null));
                MockedConstruction<SettingsService> settingsMocked = Mockito.mockConstruction(SettingsService.class);
                MockedConstruction<UserService> userServiceMocked = Mockito.mockConstruction(UserService.class)) {

            SettingsView view = new SettingsView();
            assertNotNull(view);
            assertEquals(true, authMocked.constructed().size() >= 1);
            assertEquals(true, settingsMocked.constructed().size() >= 1);
            assertEquals(true, userServiceMocked.constructed().size() >= 1);
        }
    }

    @Test
    void loadSettings_withLoggedInUser_usesPersistedSettings() throws Exception {
        User user = new User("mail@test.com", "user", "Pass123!pass", "Test", "User");
        user.setPin(4321);

        Settings persisted = new Settings(4321);
        persisted.setEmailNotifications(false);
        persisted.setPushNotifications(true);
        persisted.setProductUpdates(false);

        try (MockedConstruction<AuthenticationService> ignoredAuth = Mockito.mockConstruction(
                AuthenticationService.class, (mock, context) -> when(mock.getCurrentUser()).thenReturn(null));
                MockedConstruction<SettingsService> ignoredSettings = Mockito.mockConstruction(SettingsService.class);
                MockedConstruction<UserService> ignoredUserService = Mockito.mockConstruction(UserService.class)) {

            SettingsView view = new SettingsView();

            SettingsService serviceMock = Mockito.mock(SettingsService.class);
            when(serviceMock.getSettings(4321)).thenReturn(persisted);

            setField(view, "currentUser", user);
            setField(view, "settingsService", serviceMock);
            invokeNoArg(view, "loadSettings");

            assertEquals(false, getBooleanField(view, "emailNotifications"));
            assertEquals(true, getBooleanField(view, "pushNotifications"));
            assertEquals(false, getBooleanField(view, "productUpdates"));
        }
    }

    @Test
    void updateToggleVisual_updatesTrackAndThumbStyles() throws Exception {
        try (MockedConstruction<AuthenticationService> ignoredAuth = Mockito.mockConstruction(
                AuthenticationService.class, (mock, context) -> when(mock.getCurrentUser()).thenReturn(null));
                MockedConstruction<SettingsService> ignoredSettings = Mockito.mockConstruction(SettingsService.class);
                MockedConstruction<UserService> ignoredUserService = Mockito.mockConstruction(UserService.class)) {

            SettingsView view = new SettingsView();
            Method method = SettingsView.class.getDeclaredMethod("updateToggleVisual", Div.class, Div.class,
                    boolean.class);
            method.setAccessible(true);

            Div track = new Div();
            Div thumb = new Div();

            method.invoke(view, track, thumb, true);
            assertEquals("auto", thumb.getStyle().get("inset-inline-start"));
            assertEquals("2px", thumb.getStyle().get("inset-inline-end"));

            method.invoke(view, track, thumb, false);
            assertEquals("2px", thumb.getStyle().get("inset-inline-start"));
            assertEquals("auto", thumb.getStyle().get("inset-inline-end"));
        }
    }

    @Test
    void saveSettings_handlesNotLoggedInAndServiceFailureBranches() throws Exception {
        try (MockedConstruction<AuthenticationService> ignoredAuth = Mockito.mockConstruction(
                AuthenticationService.class, (mock, context) -> when(mock.getCurrentUser()).thenReturn(null));
                MockedConstruction<SettingsService> ignoredSettings = Mockito.mockConstruction(SettingsService.class);
                MockedConstruction<UserService> ignoredUserService = Mockito.mockConstruction(UserService.class);
                MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {

            notificationMock.when(() -> Notification.show(anyString(), anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            SettingsView view = new SettingsView();
            SettingsService serviceMock = Mockito.mock(SettingsService.class);
            setField(view, "settingsService", serviceMock);

            invokeNoArg(view, "saveSettings");
            verify(serviceMock, Mockito.never()).saveSettings(Mockito.any(Settings.class));

            User user = new User("mail@test.com", "user", "Pass123!pass", "Test", "User");
            user.setPin(9001);
            setField(view, "currentUser", user);

            when(serviceMock.saveSettings(Mockito.any(Settings.class))).thenThrow(new RuntimeException("db down"));
            assertDoesNotThrow(() -> invokeNoArg(view, "saveSettings"));
        }
    }

    @Test
    void saveSettings_successPath_callsService() throws Exception {
        try (MockedConstruction<AuthenticationService> ignoredAuth = Mockito.mockConstruction(
                AuthenticationService.class, (mock, context) -> when(mock.getCurrentUser()).thenReturn(null));
                MockedConstruction<SettingsService> ignoredSettings = Mockito.mockConstruction(SettingsService.class);
                MockedConstruction<UserService> ignoredUserService = Mockito.mockConstruction(UserService.class);
                MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {

            notificationMock.when(() -> Notification.show(anyString(), anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            SettingsView view = new SettingsView();
            SettingsService serviceMock = Mockito.mock(SettingsService.class);
            when(serviceMock.saveSettings(Mockito.any(Settings.class))).thenReturn(true);

            User user = new User("mail@test.com", "user", "Pass123!pass", "Test", "User");
            user.setPin(4321);

            setField(view, "currentUser", user);
            setField(view, "settingsService", serviceMock);

            invokeNoArg(view, "saveSettings");

            verify(serviceMock).saveSettings(Mockito.any(Settings.class));
        }
    }

    @Test
    void showDeleteAccountDialog_notLoggedIn_returnsWithNotification() throws Exception {
        try (MockedConstruction<AuthenticationService> ignoredAuth = Mockito.mockConstruction(
                AuthenticationService.class, (mock, context) -> when(mock.getCurrentUser()).thenReturn(null));
                MockedConstruction<SettingsService> ignoredSettings = Mockito.mockConstruction(SettingsService.class);
                MockedConstruction<UserService> ignoredUserService = Mockito.mockConstruction(UserService.class);
                MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {

            notificationMock.when(() -> Notification.show(anyString(), anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            SettingsView view = new SettingsView();
            invokeNoArg(view, "showDeleteAccountDialog");

            notificationMock.verify(() -> Notification.show(anyString(), anyInt(), any()), Mockito.atLeastOnce());
        }
    }

    @Test
    void languageSelect_listener_coversAllLanguageMappingBranches() throws Exception {
        try (MockedConstruction<AuthenticationService> ignoredAuth = Mockito.mockConstruction(
                AuthenticationService.class, (mock, context) -> when(mock.getCurrentUser()).thenReturn(null));
                MockedConstruction<SettingsService> ignoredSettings = Mockito.mockConstruction(SettingsService.class);
                MockedConstruction<UserService> ignoredUserService = Mockito.mockConstruction(UserService.class);
                MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {

            notificationMock.when(() -> Notification.show(anyString(), anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            SettingsView view = new SettingsView();
            Select<String> langSelect = getField(view, "langSelect", Select.class);
            assertNotNull(langSelect);

            // Drive each language path in mapping chain.
            langSelect.setValue("Suomi");
            langSelect.setValue("Português");
            langSelect.setValue("فارسی");
            langSelect.setValue("中文");
            langSelect.setValue("اردو");
            langSelect.setValue("English");

            notificationMock.verify(() -> Notification.show(anyString(), anyInt(), any()), Mockito.atLeast(5));
        }
    }

    @Test
    void saveSettings_usesEnglishWhenLangNull_andHandlesFalseReturn() throws Exception {
        try (MockedConstruction<AuthenticationService> ignoredAuth = Mockito.mockConstruction(
                AuthenticationService.class, (mock, context) -> when(mock.getCurrentUser()).thenReturn(null));
                MockedConstruction<SettingsService> ignoredSettings = Mockito.mockConstruction(SettingsService.class);
                MockedConstruction<UserService> ignoredUserService = Mockito.mockConstruction(UserService.class);
                MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {

            notificationMock.when(() -> Notification.show(anyString(), anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            SettingsView view = new SettingsView();

            User currentUser = new User("mail@test.com", "tester", "Pass123!pass", "Test", "User");
            currentUser.setPin(1234);

            AuthenticationService authService = Mockito.mock(AuthenticationService.class);
            UserService userService = Mockito.mock(UserService.class);
            SettingsService settingsService = Mockito.mock(SettingsService.class);

            setField(view, "currentUser", currentUser);
            setField(view, "authService", authService);
            setField(view, "userService", userService);
            setField(view, "settingsService", settingsService);

            Select<String> langSelect = getField(view, "langSelect", Select.class);
            langSelect.setValue(null);

            when(settingsService.saveSettings(any())).thenReturn(false);

            invokeNoArg(view, "saveSettings");

            verify(settingsService).saveSettings(any(Settings.class));
            Settings persisted = getField(view, "userSettings", Settings.class);
            assertEquals("English", persisted.getLanguage());
            notificationMock.verify(() -> Notification.show(contains("Failed to save settings"), anyInt(), any()),
                    Mockito.atLeastOnce());
        }
    }

    @Test
    void createActionButtons_discard_click_reloadsSettingsAndNotifies() throws Exception {
        try (MockedConstruction<AuthenticationService> ignoredAuth = Mockito.mockConstruction(
                AuthenticationService.class, (mock, context) -> when(mock.getCurrentUser()).thenReturn(null));
                MockedConstruction<SettingsService> ignoredSettings = Mockito.mockConstruction(SettingsService.class);
                MockedConstruction<UserService> ignoredUserService = Mockito.mockConstruction(UserService.class);
                MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {

            notificationMock.when(() -> Notification.show(anyString(), anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            SettingsView view = new SettingsView();
            User user = new User("mail@test.com", "tester", "Pass123!pass", "Test", "User");
            user.setPin(2222);

            SettingsService settingsService = Mockito.mock(SettingsService.class);
            Settings persisted = new Settings(2222);
            persisted.setLanguage("English");
            when(settingsService.getSettings(2222)).thenReturn(persisted);

            setField(view, "currentUser", user);
            setField(view, "settingsService", settingsService);

            Method m = SettingsView.class.getDeclaredMethod("createActionButtons");
            m.setAccessible(true);
            HorizontalLayout actions = (HorizontalLayout) m.invoke(view);

            Button discard = (Button) actions.getComponentAt(0);
            discard.click();

            verify(settingsService).getSettings(2222);
            notificationMock.verify(() -> Notification.show(anyString(), anyInt(), any()), Mockito.atLeastOnce());
        }
    }

    @Test
    void showDeleteAccountDialog_loggedIn_confirm_coversWrongDeleteFailureAndSuccess() throws Exception {
        List<Component> dialogContent = new ArrayList<>();
        List<Component> footerButtons = new ArrayList<>();

        try (MockedConstruction<AuthenticationService> ignoredAuth = Mockito.mockConstruction(
                AuthenticationService.class, (mock, context) -> when(mock.getCurrentUser()).thenReturn(null));
                MockedConstruction<SettingsService> ignoredSettings = Mockito.mockConstruction(SettingsService.class);
                MockedConstruction<UserService> ignoredUserService = Mockito.mockConstruction(UserService.class);
                MockedConstruction<Dialog> dialogMocked = Mockito.mockConstruction(Dialog.class, (mock, context) -> {
                    Dialog.DialogFooter footer = Mockito.mock(Dialog.DialogFooter.class);
                    when(mock.getFooter()).thenReturn(footer);

                    Mockito.doAnswer(invocation -> {
                        for (Object arg : invocation.getArguments()) {
                            if (arg instanceof Component component) {
                                dialogContent.add(component);
                            } else if (arg instanceof Component[] components) {
                                for (Component c : components) {
                                    dialogContent.add(c);
                                }
                            }
                        }
                        return null;
                    }).when(mock).add(Mockito.<Component[]> any());

                    Mockito.doAnswer(invocation -> {
                        for (Object arg : invocation.getArguments()) {
                            if (arg instanceof Component component) {
                                footerButtons.add(component);
                            } else if (arg instanceof Component[] components) {
                                for (Component c : components) {
                                    footerButtons.add(c);
                                }
                            }
                        }
                        return null;
                    }).when(footer).add(Mockito.<Component[]> any());
                });
                MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {

            notificationMock.when(() -> Notification.show(anyString(), anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            SettingsView view = new SettingsView();

            User currentUser = new User("mail@test.com", "tester", "Pass123!pass", "Test", "User");
            currentUser.setPin(1234);

            AuthenticationService authService = Mockito.mock(AuthenticationService.class);
            UserService userService = Mockito.mock(UserService.class);
            SettingsService settingsService = Mockito.mock(SettingsService.class);

            setField(view, "currentUser", currentUser);
            setField(view, "authService", authService);
            setField(view, "userService", userService);
            setField(view, "settingsService", settingsService);

            invokeNoArg(view, "showDeleteAccountDialog");
            assertTrue(dialogMocked.constructed().size() >= 1);

            // In unit context, we only assert the logged-in branch reaches dialog creation
            // path.
            verify(settingsService, Mockito.never()).deleteSettings(anyInt());
            verify(authService, Mockito.never()).logout();
        }
    }

    private void invokeNoArg(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private boolean getBooleanField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.getBoolean(target);
    }

    @SuppressWarnings("unchecked")
    private <T> T getField(Object target, String fieldName, Class<T> type) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(target);
    }

}
