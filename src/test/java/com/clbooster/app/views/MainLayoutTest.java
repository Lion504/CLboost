package com.clbooster.app.views;

import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.clbooster.app.backend.service.profile.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class MainLayoutTest extends BaseVaadinViewTest {

    @AfterAll
    static void cleanup() {
        vaadinServletMock.close();
        vaadinSessionMock.close();
        vaadinServiceMock.close();
    }

    @Test
    void constructorInitializesWithoutLoggedInUser() {
        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(null))) {

            MainLayout layout = new MainLayout();
            org.junit.jupiter.api.Assertions.assertNotNull(layout);
        }
    }

    @Test
    void constructorInitializesWithLoggedInUser() {
        User testUser = new User("test@test.com", "testuser", "testpass", "Test", "User");
        testUser.setPin(1);

        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(testUser))) {

            MainLayout layout = new MainLayout();
            org.junit.jupiter.api.Assertions.assertNotNull(layout);
        }
    }

    @Test
    void constructorHandlesEmptyUserNames() {
        User testUser = new User("test@test.com", "testuser", "testpass", "", "");
        testUser.setPin(1);

        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class,
                (mock, context) -> when(mock.getCurrentUser()).thenReturn(testUser))) {

            MainLayout layout = new MainLayout();
            org.junit.jupiter.api.Assertions.assertNotNull(layout);
        }
    }
}
