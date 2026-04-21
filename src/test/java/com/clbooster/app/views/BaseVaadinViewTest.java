package com.clbooster.app.views;

import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinSession;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class BaseVaadinViewTest {

    protected static MockedStatic<VaadinServlet> vaadinServletMock;
    protected static MockedStatic<VaadinSession> vaadinSessionMock;
    protected static MockedStatic<VaadinService> vaadinServiceMock;

    @BeforeAll
    static void setupVaadinMockEnvironment() {
        vaadinServletMock = mockStatic(VaadinServlet.class);
        vaadinSessionMock = mockStatic(VaadinSession.class);
        vaadinServiceMock = mockStatic(VaadinService.class);

        VaadinServlet mockServlet = mock(VaadinServlet.class);
        VaadinService mockService = mock(VaadinService.class);
        VaadinSession mockSession = mock(VaadinSession.class);

        vaadinServletMock.when(VaadinServlet::getCurrent).thenReturn(mockServlet);
        vaadinSessionMock.when(VaadinSession::getCurrent).thenReturn(mockSession);
        vaadinServiceMock.when(VaadinService::getCurrent).thenReturn(mockService);
    }

    @org.junit.jupiter.api.Test
    void testMocksInitialized() {
        org.junit.jupiter.api.Assertions.assertNotNull(VaadinServlet.getCurrent());
        org.junit.jupiter.api.Assertions.assertNotNull(VaadinSession.getCurrent());
        org.junit.jupiter.api.Assertions.assertNotNull(VaadinService.getCurrent());
    }

    @org.junit.jupiter.api.AfterAll
    static void tearDownVaadinMockEnvironment() {
        if (vaadinServletMock != null) {
            vaadinServletMock.close();
        }
        if (vaadinSessionMock != null) {
            vaadinSessionMock.close();
        }
        if (vaadinServiceMock != null) {
            vaadinServiceMock.close();
        }
    }
}
