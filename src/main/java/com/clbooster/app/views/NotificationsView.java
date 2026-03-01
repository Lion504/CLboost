package com.clbooster.app.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Notifications View - Notification center with animated cards
 * Following Apple Design System with modern animations
 */
@Route(value = "notifications", layout = MainLayout.class)
@PageTitle("Notifications | CL Booster")
public class NotificationsView extends VerticalLayout {

    // Figma Design System Colors
    private static final String PRIMARY = "#007AFF";
    private static final String TEXT_PRIMARY = "#1d1d1f";
    private static final String TEXT_SECONDARY = "#86868b";
    private static final String BG_WHITE = "#ffffff";
    private static final String BG_GRAY = "#f5f5f7";
    private static final String BG_DARK = "#1d1d1f";

    private VerticalLayout notificationsList;
    private String currentFilter = "All";

    public NotificationsView() {
        setSizeFull();
        setPadding(true);
        getStyle().set("gap", "24px");
        getStyle().set("background", BG_WHITE);
        getStyle().set("padding", "32px");
        getStyle().set("max-width", "800px");
        getStyle().set("margin", "0 auto");

        // Header
        HorizontalLayout header = createHeader();

        // Filter tabs
        Tabs filterTabs = createFilterTabs();

        // Notifications list
        notificationsList = new VerticalLayout();
        notificationsList.setPadding(false);
        notificationsList.setSpacing(false);
        notificationsList.getStyle().set("gap", "16px");
        notificationsList.setWidthFull();

        // Load notifications
        loadNotifications();

        add(header, filterTabs, notificationsList);
    }

    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        // Title group
        VerticalLayout titleGroup = new VerticalLayout();
        titleGroup.setPadding(false);
        titleGroup.setSpacing(false);
        titleGroup.getStyle().set("gap", "4px");

        H1 title = new H1("Notifications");
        title.getStyle().set("font-size", "30px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0");

        Paragraph subtitle = new Paragraph("Stay updated with your cover letter activity");
        subtitle.getStyle().set("font-size", "14px");
        subtitle.getStyle().set("color", TEXT_SECONDARY);
        subtitle.getStyle().set("margin", "0");

        titleGroup.add(title, subtitle);

        // Actions
        HorizontalLayout actions = new HorizontalLayout();
        actions.setAlignItems(FlexComponent.Alignment.CENTER);
        actions.getStyle().set("gap", "12px");

        Button markAllBtn = new Button("Mark all read", VaadinIcon.CHECK_CIRCLE.create());
        markAllBtn.getStyle().set("background", "transparent");
        markAllBtn.getStyle().set("color", TEXT_SECONDARY);
        markAllBtn.getStyle().set("font-weight", "500");
        markAllBtn.getStyle().set("border-radius", "9999px");
        markAllBtn.getStyle().set("border", "none");
        markAllBtn.getStyle().set("cursor", "pointer");
        markAllBtn.addClickListener(e -> {
            Notification.show("All notifications marked as read", 3000, Notification.Position.TOP_CENTER);
            markAllAsRead();
        });

        Button settingsBtn = new Button(VaadinIcon.COG.create());
        settingsBtn.getStyle().set("background", "transparent");
        settingsBtn.getStyle().set("color", TEXT_SECONDARY);
        settingsBtn.getStyle().set("border", "none");
        settingsBtn.getStyle().set("cursor", "pointer");
        settingsBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(SettingsView.class)));

        actions.add(markAllBtn, settingsBtn);

        header.add(titleGroup, actions);
        header.expand(titleGroup);

        return header;
    }

    private Tabs createFilterTabs() {
        Tabs tabs = new Tabs();
        tabs.getStyle().set("background", "transparent");

        Tab allTab = new Tab("All");
        Tab unreadTab = new Tab("Unread");
        Tab mentionsTab = new Tab("Mentions");

        tabs.add(allTab, unreadTab, mentionsTab);

        tabs.addSelectedChangeListener(e -> {
            currentFilter = e.getSelectedTab().getLabel();
            loadNotifications();
        });

        return tabs;
    }

    private void loadNotifications() {
        notificationsList.removeAll();

        // Today section
        Span todayLabel = new Span("Today");
        todayLabel.getStyle().set("font-size", "12px");
        todayLabel.getStyle().set("font-weight", "700");
        todayLabel.getStyle().set("color", TEXT_SECONDARY);
        todayLabel.getStyle().set("text-transform", "uppercase");
        todayLabel.getStyle().set("letter-spacing", "0.1em");
        todayLabel.getStyle().set("margin", "8px 0");

        notificationsList.add(todayLabel);

        // Today's notifications
        notificationsList.add(createNotificationCard(
            "AI Generated",
            "Your cover letter for Senior Product Designer is ready!",
            "2 hours ago",
            VaadinIcon.MAGIC,
            PRIMARY,
            true,
            true
        ));

        notificationsList.add(createNotificationCard(
            "Optimization Complete",
            "Your letter score improved from 82% to 94%",
            "4 hours ago",
            VaadinIcon.CHART,
            "#34C759",
            false,
            true
        ));

        // Yesterday section
        Span yesterdayLabel = new Span("Yesterday");
        yesterdayLabel.getStyle().set("font-size", "12px");
        yesterdayLabel.getStyle().set("font-weight", "700");
        yesterdayLabel.getStyle().set("color", TEXT_SECONDARY);
        yesterdayLabel.getStyle().set("text-transform", "uppercase");
        yesterdayLabel.getStyle().set("letter-spacing", "0.1em");
        yesterdayLabel.getStyle().set("margin", "24px 0 8px 0");

        notificationsList.add(yesterdayLabel);

        notificationsList.add(createNotificationCard(
            "Document Exported",
            "Your cover letter was exported as PDF",
            "Yesterday",
            VaadinIcon.FILE_TEXT,
            TEXT_SECONDARY,
            false,
            false
        ));

        notificationsList.add(createNotificationCard(
            "New Feature",
            "Try our new AI tone customization options",
            "Yesterday",
            VaadinIcon.SPARK_LINE,
            "#AF52DE",
            false,
            false
        ));

        // Earlier section
        Span earlierLabel = new Span("Earlier");
        earlierLabel.getStyle().set("font-size", "12px");
        earlierLabel.getStyle().set("font-weight", "700");
        earlierLabel.getStyle().set("color", TEXT_SECONDARY);
        earlierLabel.getStyle().set("text-transform", "uppercase");
        earlierLabel.getStyle().set("letter-spacing", "0.1em");
        earlierLabel.getStyle().set("margin", "24px 0 8px 0");

        notificationsList.add(earlierLabel);

        notificationsList.add(createNotificationCard(
            "Welcome to CL Booster",
            "Get started with your first cover letter",
            "3 days ago",
            VaadinIcon.HANDSHAKE,
            "#FF9500",
            false,
            false
        ));
    }

    private Div createNotificationCard(String title, String message, String time,
                                        VaadinIcon iconType, String iconColor,
                                        boolean isUnread, boolean isNew) {
        Div card = new Div();
        card.getStyle().set("background", isUnread ? BG_WHITE : BG_GRAY);
        card.getStyle().set("border", "1px solid rgba(0,0,0,0.05)");
        card.getStyle().set("border-radius", "16px");
        card.getStyle().set("padding", "20px");
        card.getStyle().set("cursor", "pointer");
        card.getStyle().set("transition", "all 0.3s cubic-bezier(0.4, 0, 0.2, 1)");
        card.getStyle().set("position", "relative");
        card.getStyle().set("overflow", "hidden");

        // Animation for new items
        if (isNew) {
            card.getStyle().set("animation", "slideInRight 0.4s ease forwards");
        }

        // Unread indicator
        if (isUnread) {
            Div unreadDot = new Div();
            unreadDot.getStyle().set("position", "absolute");
            unreadDot.getStyle().set("top", "20px");
            unreadDot.getStyle().set("right", "20px");
            unreadDot.getStyle().set("width", "8px");
            unreadDot.getStyle().set("height", "8px");
            unreadDot.getStyle().set("background", PRIMARY);
            unreadDot.getStyle().set("border-radius", "50%");
            card.add(unreadDot);
        }

        HorizontalLayout content = new HorizontalLayout();
        content.setAlignItems(FlexComponent.Alignment.START);
        content.getStyle().set("gap", "16px");

        // Icon
        Div iconContainer = new Div();
        iconContainer.getStyle().set("width", "44px");
        iconContainer.getStyle().set("height", "44px");
        iconContainer.getStyle().set("border-radius", "12px");
        iconContainer.getStyle().set("background", iconColor + "15");
        iconContainer.getStyle().set("display", "flex");
        iconContainer.getStyle().set("align-items", "center");
        iconContainer.getStyle().set("justify-content", "center");
        iconContainer.getStyle().set("flex-shrink", "0");
        iconContainer.getStyle().set("transition", "transform 0.3s");

        Icon icon = iconType.create();
        icon.getStyle().set("color", iconColor);
        icon.getStyle().set("width", "22px");
        icon.getStyle().set("height", "22px");
        iconContainer.add(icon);

        // Text content
        VerticalLayout textGroup = new VerticalLayout();
        textGroup.setPadding(false);
        textGroup.setSpacing(false);
        textGroup.getStyle().set("gap", "4px");

        H2 cardTitle = new H2(title);
        cardTitle.getStyle().set("font-size", "15px");
        cardTitle.getStyle().set("font-weight", isUnread ? "700" : "600");
        cardTitle.getStyle().set("color", TEXT_PRIMARY);
        cardTitle.getStyle().set("margin", "0");

        Paragraph cardMessage = new Paragraph(message);
        cardMessage.getStyle().set("font-size", "14px");
        cardMessage.getStyle().set("color", TEXT_SECONDARY);
        cardMessage.getStyle().set("line-height", "1.5");
        cardMessage.getStyle().set("margin", "0");

        Span timeSpan = new Span(time);
        timeSpan.getStyle().set("font-size", "12px");
        timeSpan.getStyle().set("color", TEXT_SECONDARY);
        timeSpan.getStyle().set("margin-top", "4px");

        textGroup.add(cardTitle, cardMessage, timeSpan);
        content.add(iconContainer, textGroup);
        card.add(content);

        // Hover effects
        card.getElement().addEventListener("mouseenter", e -> {
            card.getStyle().set("transform", "translateX(4px)");
            card.getStyle().set("box-shadow", "0 4px 20px rgba(0,0,0,0.08)");
            iconContainer.getStyle().set("transform", "scale(1.1) rotate(-5deg)");
        });

        card.getElement().addEventListener("mouseleave", e -> {
            card.getStyle().set("transform", "translateX(0)");
            card.getStyle().set("box-shadow", "none");
            iconContainer.getStyle().set("transform", "scale(1) rotate(0deg)");
        });

        card.addClickListener(e -> {
            Notification.show("Opening: " + title, 3000, Notification.Position.TOP_CENTER);
        });

        return card;
    }

    private void markAllAsRead() {
        // Reload with all notifications marked as read
        loadNotifications();
    }
}
