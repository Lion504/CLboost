package com.clbooster.app.backend.service.authentication;

import com.clbooster.app.backend.service.profile.User;
import com.clbooster.app.backend.service.profile.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    private AuthenticationService service;
    private UserDAO userDAOMock;

    @BeforeEach
    void setUp() throws Exception {
        service = new AuthenticationService();
        userDAOMock = mock(UserDAO.class);

        Field daoField = AuthenticationService.class.getDeclaredField("userDAO");
        daoField.setAccessible(true);
        daoField.set(service, userDAOMock);
    }

    // ---------- REGISTER ----------

    @Test
    void register_invalidPassword() {
        boolean result = service.register(
                "test@mail.com", "user", "weak",
                "John", "Doe"
        );

        assertFalse(result);
        verify(userDAOMock, never()).registerUser(any());
    }

    @Test
    void register_usernameExists() {
        when(userDAOMock.usernameExists("user")).thenReturn(true);

        boolean result = service.register(
                "test@mail.com", "user", "StrongPass1!",
                "John", "Doe"
        );

        assertFalse(result);
    }

    @Test
    void register_emailExists() {
        when(userDAOMock.usernameExists("user")).thenReturn(false);
        when(userDAOMock.emailExists("test@mail.com")).thenReturn(true);

        boolean result = service.register(
                "test@mail.com", "user", "StrongPass1!",
                "John", "Doe"
        );

        assertFalse(result);
    }

    @Test
    void register_success() {
        when(userDAOMock.usernameExists("user")).thenReturn(false);
        when(userDAOMock.emailExists("test@mail.com")).thenReturn(false);
        when(userDAOMock.registerUser(any(User.class))).thenReturn(true);

        boolean result = service.register(
                "test@mail.com", "user", "StrongPass1!",
                "John", "Doe"
        );

        assertTrue(result);
        verify(userDAOMock).registerUser(any(User.class));
    }

    // ---------- LOGIN ----------

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
    }

    // ---------- LOGOUT ----------

    @Test
    void logout_clearsCurrentUser() {
        User user = new User("test@mail.com", "user", "StrongPass1!", "John", "Doe");

        when(userDAOMock.loginUser(any(), any())).thenReturn(user);
        service.login("user", "StrongPass1!");

        service.logout();

        assertFalse(service.isLoggedIn());
        assertNull(service.getCurrentUser());
    }
}