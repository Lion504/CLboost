package com.clbooster.app.views;

import com.clbooster.app.views.util.StyleConstants;
import com.clbooster.app.views.util.ViewComponents;

import jakarta.annotation.security.PermitAll;
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
import com.clbooster.app.i18n.TranslationService;

/**
 * Notifications View - Notification center with animated cards Following Apple
 * Design System with modern animations
 */
@Route(value = "notifications", layout = MainLayout.class)
@PageTitle("Notifications | CL Booster")
@PermitAll
public class NotificationsView extends VerticalLayout {
    private static final String VAL_0_1EM = "0.1em";
    private static final String UPPERCASE = "uppercase";

    // Figma Design System Colors
    private static final String PRIMARY = "#007AFF";
    private static final String TEXT_PRIMARY = "#1d1d1f";
    private static final String TEXT_SECONDARY = "#86868b";
    private static final String BG_WHITE = "#ffffff";
    private static final String BG_GRAY = "#f5f5f7";
    private static final String BG_DARK = "#1d1d1f";

    private VerticalLayout notificationsList;
    private String currentFilter = "All";
    private final TranslationService translationService;

    public NotificationsView() {
        this.translationService = new TranslationService();
        setSizeFull();
        setPadding(true);
        getStyle().set("gap", "24px");
        getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        getStyle().set(StyleConstants.CSS_PADDING, "32px");
        getStyle().set(StyleConstants.CSS_MAX_WIDTH, "800px");
        getStyle().set(StyleConstants.CSS_MARGIN, "0 auto");

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
        // Header with title and actions
        HorizontalLayout header = ViewComponents.createPageHeader("notifications.title", "notifications.stayUpdated",
                translationService);

        // Actions
        HorizontalLayout actions = new HorizontalLayout();
        actions.setAlignItems(FlexComponent.Alignment.CENTER);
        actions.getStyle().set("gap", "12px");

        Button markAllBtn = new Button(translationService.translate("notifications.markAllRead"),
                VaadinIcon.CHECK_CIRCLE.create());
        markAllBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, StyleConstants.VAL_TRANSPARENT);
        markAllBtn.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        markAllBtn.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "500");
        markAllBtn.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX);
        markAllBtn.getStyle().set(StyleConstants.CSS_BORDER, "none");
        markAllBtn.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);
        markAllBtn.addClickListener(e -> {
            Notification.show(translationService.translate("notifications.allRead"), 3000,
                    Notification.Position.TOP_CENTER);
            markAllAsRead();
        });

        Button settingsBtn = new Button(VaadinIcon.COG.create());
        settingsBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, StyleConstants.VAL_TRANSPARENT);
        settingsBtn.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        settingsBtn.getStyle().set(StyleConstants.CSS_BORDER, "none");
        settingsBtn.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);
        settingsBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(SettingsView.class)));

        actions.add(markAllBtn, settingsBtn);

        header.add(actions);
        // titleGroup (first child) already expanded by createPageHeader

        return header;
    }

    private Tabs createFilterTabs() {
        Tabs tabs = new Tabs();
        tabs.getStyle().set(StyleConstants.CSS_BACKGROUND, StyleConstants.VAL_TRANSPARENT);

        Tab allTab = new Tab(translationService.translate("notifications.all"));
        Tab unreadTab = new Tab(translationService.translate("notifications.unread"));
        Tab mentionsTab = new Tab(translationService.translate("notifications.mentions"));

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
        Span todayLabel = new Span(translationService.translate("notifications.today"));
        todayLabel.getStyle().set(StyleConstants.CSS_FONT_SIZE, "12px");
        todayLabel.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        todayLabel.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        todayLabel.getStyle().set(StyleConstants.CSS_TEXT_TRANSFORM, UPPERCASE);
        todayLabel.getStyle().set(StyleConstants.CSS_LETTER_SPACING, VAL_0_1EM);
        todayLabel.getStyle().set(StyleConstants.CSS_MARGIN, "8px 0");

        notificationsList.add(todayLabel);

        // Today's notifications
        notificationsList
                .add(ViewComponents.createNotificationCard(translationService.translate("notifications.aiGenerated"),
                        translationService.translate("notifications.coverLetterReady"), "2 hours ago", VaadinIcon.MAGIC,
                        PRIMARY, true, true, translationService));

        notificationsList.add(ViewComponents.createNotificationCard(
                translationService.translate("notifications.optimizationComplete"),
                translationService.translate("notifications.scoreImproved"), "4 hours ago", VaadinIcon.CHART, "#34C759",
                false, true, translationService));

        // Yesterday section
        Span yesterdayLabel = new Span(translationService.translate("notifications.yesterday"));
        yesterdayLabel.getStyle().set(StyleConstants.CSS_FONT_SIZE, "12px");
        yesterdayLabel.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        yesterdayLabel.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        yesterdayLabel.getStyle().set(StyleConstants.CSS_TEXT_TRANSFORM, UPPERCASE);
        yesterdayLabel.getStyle().set(StyleConstants.CSS_LETTER_SPACING, VAL_0_1EM);
        yesterdayLabel.getStyle().set(StyleConstants.CSS_MARGIN, "24px 0 8px 0");

        notificationsList.add(yesterdayLabel);

        notificationsList.add(
                ViewComponents.createNotificationCard(translationService.translate("notifications.documentExported"),
                        translationService.translate("notifications.exportedAsPDF"), "Yesterday", VaadinIcon.FILE_TEXT,
                        TEXT_SECONDARY, false, false, translationService));

        notificationsList
                .add(ViewComponents.createNotificationCard(translationService.translate("notifications.newFeature"),
                        translationService.translate("notifications.tryToneCustomization"), "Yesterday",
                        VaadinIcon.SPARK_LINE, "#AF52DE", false, false, translationService));

        // Earlier section
        Span earlierLabel = new Span(translationService.translate("notifications.earlier"));
        earlierLabel.getStyle().set(StyleConstants.CSS_FONT_SIZE, "12px");
        earlierLabel.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        earlierLabel.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        earlierLabel.getStyle().set(StyleConstants.CSS_TEXT_TRANSFORM, UPPERCASE);
        earlierLabel.getStyle().set(StyleConstants.CSS_LETTER_SPACING, VAL_0_1EM);
        earlierLabel.getStyle().set(StyleConstants.CSS_MARGIN, "24px 0 8px 0");

        notificationsList.add(earlierLabel);

        notificationsList
                .add(ViewComponents.createNotificationCard(translationService.translate("notifications.welcome"),
                        translationService.translate("notifications.getStarted"), "3 days ago", VaadinIcon.HANDSHAKE,
                        "#FF9500", false, false, translationService));
    }

    private void markAllAsRead() {
        // Reload with all notifications marked as read
        loadNotifications();
    }
}
