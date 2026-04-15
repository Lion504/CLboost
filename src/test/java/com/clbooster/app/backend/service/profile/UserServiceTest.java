package com.clbooster.app.backend.service.profile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserService service;
    private UserDAO dao;

    @BeforeEach
    void setUp() throws Exception {
        service = new UserService();
        dao = mock(UserDAO.class);

        Field field = UserService.class.getDeclaredField("userDAO");
        field.setAccessible(true);
        field.set(service, dao);
    }

    @Test
    void registerUser_delegatesToDao() {
        User user = new User("john", "pass");

        when(dao.registerUser(user)).thenReturn(true);

        boolean result = service.registerUser(user);

        assertTrue(result);
        verify(dao).registerUser(user);
    }

    @Test
    void loginUser_delegatesToDao() {
        User user = new User("john", "pass");

        when(dao.loginUser("john", "pass")).thenReturn(user);

        User result = service.loginUser("john", "pass");

        assertEquals(user, result);
        verify(dao).loginUser("john", "pass");
    }

    @Test
    void getUserByPin_delegatesToDao() {
        User user = new User("john", "pass");

        when(dao.getUserByPin(1)).thenReturn(user);

        User result = service.getUserByPin(1);

        assertEquals(user, result);
        verify(dao).getUserByPin(1);
    }

    @Test
    void deleteUser_delegatesToDao() {
        User user = new User("john", "pass");

        when(dao.deleteUser(user)).thenReturn(true);

        boolean result = service.deleteUser(user);

        assertTrue(result);
        verify(dao).deleteUser(user);
    }

    @Test
    void updatePassword_delegatesToDao() {
        when(dao.updatePassword(1, "new")).thenReturn(true);

        assertTrue(service.updatePassword(1, "new"));

        verify(dao).updatePassword(1, "new");
    }

    @Test
    void usernameExists_delegatesToDao() {
        when(dao.usernameExists("john")).thenReturn(true);

        assertTrue(service.usernameExists("john"));

        verify(dao).usernameExists("john");
    }

    @Test
    void emailExists_delegatesToDao() {
        when(dao.emailExists("a@b.com")).thenReturn(true);

        assertTrue(service.emailExists("a@b.com"));

        verify(dao).emailExists("a@b.com");
    }
}