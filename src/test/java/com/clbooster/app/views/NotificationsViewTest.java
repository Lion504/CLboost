package com.clbooster.app.views;

import com.clbooster.app.backend.service.authentication.AuthenticationService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class NotificationsViewTest extends BaseVaadinViewTest {

    @AfterAll
    static void cleanup() {
        vaadinServletMock.close();
        vaadinSessionMock.close();
        vaadinServiceMock.close();
    }

    @Test
    void constructor_initializesView() {
        try (MockedConstruction<AuthenticationService> mocked = Mockito.mockConstruction(AuthenticationService.class)) {
            NotificationsView view = new NotificationsView();
            assertNotNull(view);
            assertNotNull(mocked.constructed());
        }
    }

    @Test
    void markAllAsRead_canBeInvoked() throws Exception {
        try (MockedConstruction<AuthenticationService> ignored = Mockito
                .mockConstruction(AuthenticationService.class)) {
            NotificationsView view = new NotificationsView();
            Method method = NotificationsView.class.getDeclaredMethod("markAllAsRead");
            method.setAccessible(true);

            assertDoesNotThrow(() -> method.invoke(view));
        }
    }
}
