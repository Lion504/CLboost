package com.clbooster.app.views;

import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.ComponentUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HelpViewTest extends BaseVaadinViewTest {

    private static ClickEvent<Div> divClickEvent(Div div) {
        return new ClickEvent<>(div, false, 0, 0, 0, 0, 0, 1, false, false, false, false);
    }

    @AfterAll
    static void cleanup() {
        vaadinServletMock.close();
        vaadinSessionMock.close();
        vaadinServiceMock.close();
    }

    @Test
    void constructor_initializesView() {
        try (MockedConstruction<AuthenticationService> mocked = Mockito.mockConstruction(AuthenticationService.class)) {
            HelpView view = new HelpView();
            assertNotNull(view);
            assertNotNull(mocked.constructed());
        }
    }

    @Test
    void filterFaqs_handlesNullAndEmptyAndSearchTerm() throws Exception {
        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class)) {
            HelpView view = new HelpView();
            Method method = HelpView.class.getDeclaredMethod("filterFAQs", String.class);
            method.setAccessible(true);

            assertDoesNotThrow(() -> method.invoke(view, new Object[] {null}));
            assertDoesNotThrow(() -> method.invoke(view, ""));
            try (MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {
                notificationMock.when(() -> Notification.show(Mockito.anyString(), Mockito.anyInt(), Mockito.any()))
                        .thenReturn(Mockito.mock(Notification.class));
                assertDoesNotThrow(() -> method.invoke(view, "account"));
            }
        }
    }

    @Test
    void createFAQItem_togglesExpandedAndCollapsedStyles() throws Exception {
        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class)) {
            HelpView view = new HelpView();
            Method method = HelpView.class.getDeclaredMethod("createFAQItem", String.class, String.class);
            method.setAccessible(true);

            Div item = (Div) method.invoke(view, "Q", "A");
            assertNotNull(item);

            com.vaadin.flow.component.html.Paragraph answer =
                    (com.vaadin.flow.component.html.Paragraph) item.getChildren().skip(1).findFirst().orElseThrow();

            ComponentUtil.fireEvent(item, divClickEvent(item));
            assertEquals("1", answer.getStyle().get("opacity"));

            ComponentUtil.fireEvent(item, divClickEvent(item));
            assertEquals("0", answer.getStyle().get("opacity"));
        }
    }

    @Test
    void quickLinkAndSupportCardClicks_showNotification() throws Exception {
        try (MockedConstruction<AuthenticationService> ignored = Mockito.mockConstruction(AuthenticationService.class);
             MockedStatic<Notification> notificationMock = Mockito.mockStatic(Notification.class)) {

            notificationMock.when(() -> Notification.show(Mockito.anyString(), Mockito.anyInt(), Mockito.any()))
                    .thenReturn(Mockito.mock(Notification.class));

            HelpView view = new HelpView();

            Method quickLinkMethod = HelpView.class.getDeclaredMethod("createQuickLinkButton", String.class,
                    VaadinIcon.class);
            quickLinkMethod.setAccessible(true);
            Button quickLink = (Button) quickLinkMethod.invoke(view, "Getting Started", VaadinIcon.ROCKET);
            quickLink.click();

            Method supportMethod = HelpView.class.getDeclaredMethod("createSupportCard", String.class, String.class,
                    VaadinIcon.class, String.class, String.class);
            supportMethod.setAccessible(true);
            Div supportCard = (Div) supportMethod.invoke(view, "Support", "Desc", VaadinIcon.ENVELOPE,
                    "#007AFF", "Email us");
                ComponentUtil.fireEvent(supportCard, divClickEvent(supportCard));

            notificationMock.verify(() -> Notification.show(Mockito.anyString(), Mockito.anyInt(), Mockito.any()),
                    Mockito.atLeast(2));
        }
    }
}
