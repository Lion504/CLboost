package com.clbooster.app.backend.service.authentication;

import com.clbooster.app.backend.service.profile.User;
import com.clbooster.app.backend.service.profile.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    private AuthenticationService service;
    private UserDAO userDAOMock;
    private Map<String, Object> sessionMap;

    @BeforeEach
    void setUp() throws Exception {
        sessionMap = new HashMap<>();
        service = new AuthenticationService(sessionMap);

        userDAOMock = mock(UserDAO.class);

        Field daoField = AuthenticationService.class.getDeclaredField("userDAO");
        daoField.setAccessible(true);
        daoField.set(service, userDAOMock);
    }

    // ---------------- REGISTER VALIDATION ----------------

    @Test
    void register_nullEmail() {
        boolean result = service.register(null, "user", "StrongPass1!", "John", "Doe");
        assertFalse(result);
        verify(userDAOMock, never()).registerUser(any());
    }

    @Test
    void register_emptyEmail() {
        boolean result = service.register("   ", "user", "StrongPass1!", "John", "Doe");
        assertFalse(result);
        verify(userDAOMock, never()).registerUser(any());
    }

    @Test
    void register_nullUsername() {
        boolean result = service.register("test@mail.com", null, "StrongPass1!", "John", "Doe");
        assertFalse(result);
        verify(userDAOMock, never()).registerUser(any());
    }

    @Test
    void register_emptyUsername() {
        boolean result = service.register("test@mail.com", "   ", "StrongPass1!", "John", "Doe");
        assertFalse(result);
        verify(userDAOMock, never()).registerUser(any());
    }

    @Test
    void register_nullFirstName() {
        boolean result = service.register("test@mail.com", "user", "StrongPass1!", null, "Doe");
        assertFalse(result);
        verify(userDAOMock, never()).registerUser(any());
    }

    @Test
    void register_nullLastName() {
        boolean result = service.register("test@mail.com", "user", "StrongPass1!", "John", null);
        assertFalse(result);
        verify(userDAOMock, never()).registerUser(any());
    }

    @Test
    void register_passwordTooShort() {
        boolean result = service.register("test@mail.com", "user", "Aa1!", "John", "Doe");
        assertFalse(result);
        verify(userDAOMock, never()).registerUser(any());
    }

    @Test
    void register_missingUppercase() {
        boolean result = service.register("test@mail.com", "user", "strongpass1!", "John", "Doe");
        assertFalse(result);
    }

    @Test
    void register_missingLowercase() {
        boolean result = service.register("test@mail.com", "user", "STRONGPASS1!", "John", "Doe");
        assertFalse(result);
    }

    @Test
    void register_missingDigit() {
        boolean result = service.register("test@mail.com", "user", "StrongPass!", "John", "Doe");
        assertFalse(result);
    }

    @Test
    void register_missingSpecialChar() {
        boolean result = service.register("test@mail.com", "user", "StrongPass1", "John", "Doe");
        assertFalse(result);
    }

    @Test
    void register_usernameExists() {
        when(userDAOMock.usernameExists("user")).thenReturn(true);

        boolean result = service.register("test@mail.com", "user", "StrongPass1!", "John", "Doe");

        assertFalse(result);
        verify(userDAOMock, never()).registerUser(any());
    }

    @Test
    void register_emailExists() {
        when(userDAOMock.usernameExists("user")).thenReturn(false);
        when(userDAOMock.emailExists("test@mail.com")).thenReturn(true);

        boolean result = service.register("test@mail.com", "user", "StrongPass1!", "John", "Doe");

        assertFalse(result);
        verify(userDAOMock, never()).registerUser(any());
    }

    @Test
    void register_success() {
        when(userDAOMock.usernameExists("user")).thenReturn(false);
        when(userDAOMock.emailExists("test@mail.com")).thenReturn(false);
        when(userDAOMock.registerUser(any(User.class))).thenReturn(true);

        boolean result = service.register("test@mail.com", "user", "StrongPass1!", "John", "Doe");

        assertTrue(result);
        verify(userDAOMock).registerUser(any(User.class));
    }

    // ---------------- LOGIN ----------------

    @Test
    void login_nullUsername() {
        boolean result = service.login(null, "pass");
        assertFalse(result);
    }

    @Test
    void login_emptyPassword() {
        boolean result = service.login("user", "   ");
        assertFalse(result);
    }

    @Test
    void login_invalidCredentials() {
        when(userDAOMock.loginUser("user", "pass")).thenReturn(null);

        boolean result = service.login("user", "pass");

        assertFalse(result);
        assertFalse(service.isLoggedIn());
    }

    @Test
    void login_success() {
        User user = new User("test@mail.com", "user", "StrongPass1!", "John", "Doe");
        user.setPin(42);

        when(userDAOMock.loginUser("user", "StrongPass1!")).thenReturn(user);

        boolean result = service.login("user", "StrongPass1!");

        assertTrue(result);
        assertTrue(service.isLoggedIn());
        assertEquals(42, service.getCurrentUserPin());
        assertEquals("user", service.getCurrentUser().getUsername());
    }

    // ---------------- LOGOUT ----------------

    @Test
    void logout_whenLoggedIn_clearsSession() {
        User user = new User("test@mail.com", "user", "StrongPass1!", "John", "Doe");
        user.setPin(1);

        when(userDAOMock.loginUser(any(), any())).thenReturn(user);

        service.login("user", "StrongPass1!");
        service.logout();

        assertFalse(service.isLoggedIn());
        assertNull(service.getCurrentUser());
    }

    @Test
    void logout_whenNotLoggedIn_doesNotCrash() {
        service.logout();
        assertFalse(service.isLoggedIn());
    }

    // ---------------- CHANGE PASSWORD ----------------

    @Test
    void changePassword_wrongCurrentPassword() {
        User user = new User("test@mail.com", "user", "StrongPass1!", "John", "Doe");
        user.setPin(1);

        service.login("user", "StrongPass1!");

        when(userDAOMock.loginUser("user", "wrongPass")).thenReturn(null);

        boolean result = service.changePassword("wrongPass", "newPass");

        assertFalse(result);
        verify(userDAOMock, never()).updatePassword(anyInt(), anyString());
    }

    @Test
    void changePassword_noUserLoggedIn() {
        boolean result = service.changePassword("old", "new");
        assertFalse(result);
    }
}