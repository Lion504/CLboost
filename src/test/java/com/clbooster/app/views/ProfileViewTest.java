package com.clbooster.app.views;

import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.clbooster.app.backend.service.profile.Profile;
import com.clbooster.app.backend.service.profile.ProfileService;
import com.clbooster.app.backend.service.profile.User;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProfileViewTest extends BaseVaadinViewTest {

    @AfterAll
    static void cleanup() {
        vaadinServletMock.close();
        vaadinSessionMock.close();
        vaadinServiceMock.close();
    }

    @Test
    void constructor_initializesWithUserData() {
        User user = new User("test@mail.com", "tester", "Pass123!pass", "Test", "User");
        user.setPin(1234);
        Profile profile = new Profile(1234, "Senior", "Java", "Spring", "https://me.dev", "test@mail.com");

        try (MockedConstruction<AuthenticationService> authMocked = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(user));
             MockedConstruction<ProfileService> profileMocked = Mockito.mockConstruction(ProfileService.class,
                     (mock, context) -> when(mock.getProfile(1234, Locale.getDefault())).thenReturn(profile))) {

            ProfileView view = new ProfileView();
            assertNotNull(view);
            assertTrue(authMocked.constructed().size() >= 1);
            assertTrue(profileMocked.constructed().size() >= 1);
        }
    }

    @Test
    void createFormHelpers_returnStyledFields() throws Exception {
        ProfileView view = createViewWithUser();

        TextField tf = invoke(view, "createFormField", TextField.class,
                new Class<?>[] { String.class, String.class }, new Object[] { "Label", "Value" });
        EmailField ef = invoke(view, "createEmailField", EmailField.class,
                new Class<?>[] { String.class, String.class }, new Object[] { "Email", "a@b.com" });
        TextArea ta = invoke(view, "createTextArea", TextArea.class,
                new Class<?>[] { String.class, String.class, String.class },
                new Object[] { "Skills", "Java", "Add skills" });

        assertEquals("Value", tf.getValue());
        assertEquals("a@b.com", ef.getValue());
        assertEquals("Java", ta.getValue());
        assertNotNull(tf.getStyle().get("--vaadin-input-field-background"));
    }

    @Test
    void toggleEditMode_enablesAndDisablesFields() throws Exception {
        ProfileView view = createViewWithUser();

        Method toggle = ProfileView.class.getDeclaredMethod("toggleEditMode");
        toggle.setAccessible(true);

        TextField firstName = getField(view, "firstNameField", TextField.class);
        Button saveBtn = getField(view, "saveBtn", Button.class);

        assertTrue(firstName.isReadOnly());
        assertFalse(saveBtn.isVisible());

        toggle.invoke(view);
        assertFalse(firstName.isReadOnly());
        assertTrue(saveBtn.isVisible());

        toggle.invoke(view);
        assertTrue(firstName.isReadOnly());
        assertFalse(saveBtn.isVisible());
    }

    @Test
    void saveProfile_invalidEmail_doesNotCallUpdate() throws Exception {
        User user = new User("test@mail.com", "tester", "Pass123!pass", "Test", "User");
        user.setPin(1234);

        try (MockedConstruction<AuthenticationService> authMocked = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(user));
             MockedConstruction<ProfileService> profileMocked = Mockito.mockConstruction(ProfileService.class);
             MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {

            notificationMock.when(() -> Notification.show(anyString(), Mockito.anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            ProfileView view = new ProfileView();
            EmailField emailField = getField(view, "emailField", EmailField.class);
            emailField.setValue("invalid-email");

            invokeNoArgs(view, "saveProfile");

            ProfileService ps = profileMocked.constructed().get(0);
            verify(ps, never()).updateProfile(anyInt(), anyString(), anyString(), anyString(), anyString(), anyString(),
                    anyString(), anyString(), any());
        }
    }

    @Test
    void saveProfile_success_updatesSessionAndExitsEditMode() throws Exception {
        User user = new User("test@mail.com", "tester", "Pass123!pass", "Test", "User");
        user.setPin(1234);

        User updatedUser = new User("new@mail.com", "tester", "Pass123!pass", "Updated", "User");
        updatedUser.setPin(1234);

        Profile initialProfile = new Profile(1234, "Senior", "Java", "Spring", "https://me.dev", "test@mail.com");
        Profile updatedProfile = new Profile(1234, "Lead", "Java,SQL", "Spring,Cloud", "https://new.dev", "new@mail.com");

        try (MockedConstruction<AuthenticationService> authMocked = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(user));
             MockedConstruction<ProfileService> profileMocked = Mockito.mockConstruction(ProfileService.class,
                     (mock, context) -> {
                         when(mock.getProfile(1234, Locale.getDefault())).thenReturn(initialProfile, updatedProfile);
                         when(mock.updateProfile(anyInt(), anyString(), anyString(), anyString(), anyString(), anyString(),
                                 anyString(), anyString(), any())).thenReturn(true);
                         when(mock.getUpdatedUser(1234)).thenReturn(updatedUser);
                     });
             MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {

            notificationMock.when(() -> Notification.show(anyString(), Mockito.anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            ProfileView view = new ProfileView();
            invokeNoArgs(view, "toggleEditMode");

            TextField firstName = getField(view, "firstNameField", TextField.class);
            TextField lastName = getField(view, "lastNameField", TextField.class);
            EmailField email = getField(view, "emailField", EmailField.class);
            TextField exp = getField(view, "experienceField", TextField.class);
            TextArea tools = getField(view, "toolsArea", TextArea.class);
            TextArea skills = getField(view, "skillsArea", TextArea.class);
            TextField link = getField(view, "linkField", TextField.class);

            firstName.setValue("Updated");
            lastName.setValue("User");
            email.setValue("new@mail.com");
            exp.setValue("Lead");
            tools.setValue("Java,SQL");
            skills.setValue("Spring,Cloud");
            link.setValue("https://new.dev");

            invokeNoArgs(view, "saveProfile");

            ProfileService ps = profileMocked.constructed().get(0);
            AuthenticationService auth = authMocked.constructed().get(0);

            verify(ps).updateProfile(anyInt(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                    anyString(), any());
            verify(ps).getUpdatedUser(1234);
            verify(auth).setCurrentUser(updatedUser);

            Button saveBtn = getField(view, "saveBtn", Button.class);
            assertFalse(saveBtn.isVisible());
        }
    }

    @Test
    void saveProfile_notLoggedIn_doesNotAttemptUpdate() throws Exception {
        try (MockedConstruction<AuthenticationService> authMocked = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null));
             MockedConstruction<ProfileService> profileMocked = Mockito.mockConstruction(ProfileService.class);
             MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {

            notificationMock.when(() -> Notification.show(anyString(), Mockito.anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            ProfileView view = new ProfileView();
            invokeNoArgs(view, "saveProfile");

            ProfileService ps = profileMocked.constructed().get(0);
            verify(ps, never()).updateProfile(anyInt(), anyString(), anyString(), anyString(), anyString(), anyString(),
                    anyString(), anyString(), any());
            verify(authMocked.constructed().get(0), never()).setCurrentUser(any());
        }
    }

    @Test
    void saveProfile_whenUpdateFails_staysInEditMode() throws Exception {
        User user = new User("test@mail.com", "tester", "Pass123!pass", "Test", "User");
        user.setPin(1234);
        Profile profile = new Profile(1234, "Senior", "Java", "Spring", "https://me.dev", "test@mail.com");

        try (MockedConstruction<AuthenticationService> ignoredAuth = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(user));
             MockedConstruction<ProfileService> profileMocked = Mockito.mockConstruction(ProfileService.class,
                     (mock, context) -> {
                         when(mock.getProfile(1234, Locale.getDefault())).thenReturn(profile);
                         when(mock.updateProfile(anyInt(), anyString(), anyString(), anyString(), anyString(), anyString(),
                                 anyString(), anyString(), any())).thenReturn(false);
                     });
             MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {

            notificationMock.when(() -> Notification.show(anyString(), Mockito.anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            ProfileView view = new ProfileView();
            invokeNoArgs(view, "toggleEditMode");

            invokeNoArgs(view, "saveProfile");

            Button saveBtn = getField(view, "saveBtn", Button.class);
            assertTrue(saveBtn.isVisible());
            verify(profileMocked.constructed().get(0)).updateProfile(anyInt(), anyString(), anyString(), anyString(),
                    anyString(), anyString(), anyString(), anyString(), any());
        }
    }

    @Test
    void changePassword_validationAndSuccessFlows() throws Exception {
        User user = new User("test@mail.com", "tester", "Pass123!pass", "Test", "User");
        user.setPin(1234);

        try (MockedConstruction<AuthenticationService> authMocked = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(user));
             MockedConstruction<ProfileService> ignoredProfile = Mockito.mockConstruction(ProfileService.class);
             MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {

            notificationMock.when(() -> Notification.show(anyString(), Mockito.anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            ProfileView view = new ProfileView();
            AuthenticationService auth = authMocked.constructed().get(0);

            // Empty current password
            invokeNoArgs(view, "changePassword");
            verify(auth, never()).changePassword(anyString(), anyString());

            // Valid current, mismatching new/confirm
            setPasswordFields(view, "oldPass1!A", "NewPass1!A", "Different1!A");
            invokeNoArgs(view, "changePassword");
            verify(auth, never()).changePassword(anyString(), anyString());

            // Weak new password
            setPasswordFields(view, "oldPass1!A", "weak", "weak");
            invokeNoArgs(view, "changePassword");
            verify(auth, never()).changePassword(anyString(), anyString());

            // Success
            when(auth.changePassword("oldPass1!A", "NewStrong1!A")).thenReturn(true);
            setPasswordFields(view, "oldPass1!A", "NewStrong1!A", "NewStrong1!A");
            invokeNoArgs(view, "changePassword");
            verify(auth).changePassword("oldPass1!A", "NewStrong1!A");

            com.vaadin.flow.component.textfield.PasswordField current = getField(view, "currentPasswordField",
                    com.vaadin.flow.component.textfield.PasswordField.class);
            com.vaadin.flow.component.textfield.PasswordField newer = getField(view, "newPasswordField",
                    com.vaadin.flow.component.textfield.PasswordField.class);
            com.vaadin.flow.component.textfield.PasswordField confirm = getField(view, "confirmPasswordField",
                    com.vaadin.flow.component.textfield.PasswordField.class);

            assertEquals("", current.getValue());
            assertEquals("", newer.getValue());
            assertEquals("", confirm.getValue());
        }
    }

    @Test
    void changePassword_notLoggedIn_andOtherValidationBranches() throws Exception {
        try (MockedConstruction<AuthenticationService> authMocked = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null));
             MockedConstruction<ProfileService> ignoredProfile = Mockito.mockConstruction(ProfileService.class);
             MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {

            notificationMock.when(() -> Notification.show(anyString(), Mockito.anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            ProfileView view = new ProfileView();
            invokeNoArgs(view, "changePassword");

            verify(authMocked.constructed().get(0), never()).changePassword(anyString(), anyString());
        }

        User user = new User("test@mail.com", "tester", "Pass123!pass", "Test", "User");
        user.setPin(1234);
        try (MockedConstruction<AuthenticationService> authMocked = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(user));
             MockedConstruction<ProfileService> ignoredProfile = Mockito.mockConstruction(ProfileService.class);
             MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {

            notificationMock.when(() -> Notification.show(anyString(), Mockito.anyInt(), any()))
                    .thenReturn(Mockito.mock(Notification.class));

            ProfileView view = new ProfileView();
            AuthenticationService auth = authMocked.constructed().get(0);

            // Empty new password branch
            setPasswordFields(view, "oldPass1!A", "", "");
            invokeNoArgs(view, "changePassword");
            verify(auth, never()).changePassword(anyString(), anyString());

            // AuthService returns false branch
            when(auth.changePassword("oldPass1!A", "NewStrong1!A")).thenReturn(false);
            setPasswordFields(view, "oldPass1!A", "NewStrong1!A", "NewStrong1!A");
            invokeNoArgs(view, "changePassword");
            verify(auth).changePassword("oldPass1!A", "NewStrong1!A");

            com.vaadin.flow.component.textfield.PasswordField current = getField(view, "currentPasswordField",
                    com.vaadin.flow.component.textfield.PasswordField.class);
            assertEquals("oldPass1!A", current.getValue());
        }
    }

    private ProfileView createViewWithUser() {
        User user = new User("test@mail.com", "tester", "Pass123!pass", "Test", "User");
        user.setPin(1234);
        Profile profile = new Profile(1234, "Senior", "Java", "Spring", "https://me.dev", "test@mail.com");

        try (MockedConstruction<AuthenticationService> ignoredAuth = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(user));
             MockedConstruction<ProfileService> ignoredProfile = Mockito.mockConstruction(ProfileService.class,
                     (mock, context) -> when(mock.getProfile(1234, Locale.getDefault())).thenReturn(profile))) {
            return new ProfileView();
        }
    }

    private void setPasswordFields(ProfileView view, String current, String newer, String confirm) throws Exception {
        com.vaadin.flow.component.textfield.PasswordField currentField = getField(view, "currentPasswordField",
                com.vaadin.flow.component.textfield.PasswordField.class);
        com.vaadin.flow.component.textfield.PasswordField newField = getField(view, "newPasswordField",
                com.vaadin.flow.component.textfield.PasswordField.class);
        com.vaadin.flow.component.textfield.PasswordField confirmField = getField(view, "confirmPasswordField",
                com.vaadin.flow.component.textfield.PasswordField.class);

        currentField.setValue(current);
        newField.setValue(newer);
        confirmField.setValue(confirm);
    }

    @SuppressWarnings("unchecked")
    private <T> T invoke(ProfileView view, String methodName, Class<T> type, Class<?>[] parameterTypes, Object[] args)
            throws Exception {
        Method m = ProfileView.class.getDeclaredMethod(methodName, parameterTypes);
        m.setAccessible(true);
        return (T) m.invoke(view, args);
    }

    private void invokeNoArgs(ProfileView view, String methodName) throws Exception {
        Method m = ProfileView.class.getDeclaredMethod(methodName);
        m.setAccessible(true);
        m.invoke(view);
    }

    @SuppressWarnings("unchecked")
    private <T> T getField(Object target, String fieldName, Class<T> type) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(target);
    }
}
