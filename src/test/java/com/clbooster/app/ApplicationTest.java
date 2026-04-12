package com.clbooster.app;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationTest {

    @Test
    void classCarriesExpectedVaadinShellAnnotations() {
        assertTrue(Application.class.isAnnotationPresent(PWA.class));
        assertTrue(Application.class.isAnnotationPresent(Theme.class));
        assertTrue(AppShellConfigurator.class.isAssignableFrom(Application.class));
    }

    @Test
    void main_delegatesToSpringApplicationRun() {
        String[] args = new String[] { "--server.port=9090" };

        try (MockedStatic<SpringApplication> springAppMock = Mockito.mockStatic(SpringApplication.class)) {
            Application.main(args);
            springAppMock.verify(() -> SpringApplication.run(Application.class, args));
        }
    }
}
