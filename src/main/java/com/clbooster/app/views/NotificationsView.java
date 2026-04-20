package com.clbooster.app.views;

import com.clbooster.app.views.util.StyleConstants;

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
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        // Title group
        VerticalLayout titleGroup = new VerticalLayout();
        titleGroup.setPadding(false);
        titleGroup.setSpacing(false);
        titleGroup.getStyle().set("gap", "4px");

        H1 title = new H1(translationService.translate("notifications.title"));
        title.getStyle().set(StyleConstants.CSS_FONT_SIZE, "30px");
        title.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        title.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        title.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        Paragraph subtitle = new Paragraph(translationService.translate("notifications.stayUpdated"));
        subtitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        subtitle.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        subtitle.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        titleGroup.add(title, subtitle);

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

        header.add(titleGroup, actions);
        header.expand(titleGroup);

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
        todayLabel.getStyle().set(StyleConstants.CSS_TEXT_TRANSFORM, "uppercase");
        todayLabel.getStyle().set(StyleConstants.CSS_LETTER_SPACING, "0.1em");
        todayLabel.getStyle().set(StyleConstants.CSS_MARGIN, "8px 0");

        notificationsList.add(todayLabel);

        // Today's notifications
        notificationsList.add(createNotificationCard(translationService.translate("notifications.aiGenerated"),
                translationService.translate("notifications.coverLetterReady"), "2 hours ago", VaadinIcon.MAGIC,
                PRIMARY, true, true));

        notificationsList.add(createNotificationCard(translationService.translate("notifications.optimizationComplete"),
                translationService.translate("notifications.scoreImproved"), "4 hours ago", VaadinIcon.CHART, "#34C759",
                false, true));

        // Yesterday section
        Span yesterdayLabel = new Span(translationService.translate("notifications.yesterday"));
        yesterdayLabel.getStyle().set(StyleConstants.CSS_FONT_SIZE, "12px");
        yesterdayLabel.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        yesterdayLabel.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        yesterdayLabel.getStyle().set(StyleConstants.CSS_TEXT_TRANSFORM, "uppercase");
        yesterdayLabel.getStyle().set(StyleConstants.CSS_LETTER_SPACING, "0.1em");
        yesterdayLabel.getStyle().set(StyleConstants.CSS_MARGIN, "24px 0 8px 0");

        notificationsList.add(yesterdayLabel);

        notificationsList.add(createNotificationCard(translationService.translate("notifications.documentExported"),
                translationService.translate("notifications.exportedAsPDF"), "Yesterday", VaadinIcon.FILE_TEXT,
                TEXT_SECONDARY, false, false));

        notificationsList.add(createNotificationCard(translationService.translate("notifications.newFeature"),
                translationService.translate("notifications.tryToneCustomization"), "Yesterday", VaadinIcon.SPARK_LINE,
                "#AF52DE", false, false));

        // Earlier section
        Span earlierLabel = new Span(translationService.translate("notifications.earlier"));
        earlierLabel.getStyle().set(StyleConstants.CSS_FONT_SIZE, "12px");
        earlierLabel.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        earlierLabel.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        earlierLabel.getStyle().set(StyleConstants.CSS_TEXT_TRANSFORM, "uppercase");
        earlierLabel.getStyle().set(StyleConstants.CSS_LETTER_SPACING, "0.1em");
        earlierLabel.getStyle().set(StyleConstants.CSS_MARGIN, "24px 0 8px 0");

        notificationsList.add(earlierLabel);

        notificationsList.add(createNotificationCard(translationService.translate("notifications.welcome"),
                translationService.translate("notifications.getStarted"), "3 days ago", VaadinIcon.HANDSHAKE, "#FF9500",
                false, false));
    }

    private Div createNotificationCard(String title, String message, String time, VaadinIcon iconType, String iconColor,
            boolean isUnread, boolean isNew) {
        Div card = new Div();
        card.getStyle().set(StyleConstants.CSS_BACKGROUND, isUnread ? BG_WHITE : BG_GRAY);
        card.getStyle().set(StyleConstants.CSS_BORDER, "1px solid rgba(0,0,0,0.05)");
        card.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "16px");
        card.getStyle().set(StyleConstants.CSS_PADDING, "20px");
        card.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);
        card.getStyle().set(StyleConstants.CSS_TRANSITION, "all 0.3s cubic-bezier(0.4, 0, 0.2, 1)");
        card.getStyle().set("position", "relative");
        card.getStyle().set(StyleConstants.CSS_OVERFLOW, "hidden");

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
            unreadDot.getStyle().set(StyleConstants.CSS_WIDTH, "8px");
            unreadDot.getStyle().set(StyleConstants.CSS_HEIGHT, "8px");
            unreadDot.getStyle().set(StyleConstants.CSS_BACKGROUND, PRIMARY);
            unreadDot.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "50%");
            card.add(unreadDot);
        }

        HorizontalLayout content = new HorizontalLayout();
        content.setAlignItems(FlexComponent.Alignment.START);
        content.getStyle().set("gap", "16px");

        // Icon
        Div iconContainer = new Div();
        iconContainer.getStyle().set(StyleConstants.CSS_WIDTH, "44px");
        iconContainer.getStyle().set(StyleConstants.CSS_HEIGHT, "44px");
        iconContainer.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "12px");
        iconContainer.getStyle().set(StyleConstants.CSS_BACKGROUND, iconColor + "15");
        iconContainer.getStyle().set(StyleConstants.CSS_DISPLAY, "flex");
        iconContainer.getStyle().set(StyleConstants.CSS_ALIGN_ITEMS, StyleConstants.VAL_CENTER);
        iconContainer.getStyle().set(StyleConstants.CSS_JUSTIFY_CONTENT, StyleConstants.VAL_CENTER);
        iconContainer.getStyle().set("flex-shrink", "0");
        iconContainer.getStyle().set(StyleConstants.CSS_TRANSITION, "transform 0.3s");

        Icon icon = iconType.create();
        icon.getStyle().set(StyleConstants.CSS_COLOR, iconColor);
        icon.getStyle().set(StyleConstants.CSS_WIDTH, "22px");
        icon.getStyle().set(StyleConstants.CSS_HEIGHT, "22px");
        iconContainer.add(icon);

        // Text content
        VerticalLayout textGroup = new VerticalLayout();
        textGroup.setPadding(false);
        textGroup.setSpacing(false);
        textGroup.getStyle().set("gap", "4px");

        H2 cardTitle = new H2(title);
        cardTitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "15px");
        cardTitle.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, isUnread ? "700" : "600");
        cardTitle.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        cardTitle.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        Paragraph cardMessage = new Paragraph(message);
        cardMessage.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        cardMessage.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        cardMessage.getStyle().set("line-height", "1.5");
        cardMessage.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        Span timeSpan = new Span(time);
        timeSpan.getStyle().set(StyleConstants.CSS_FONT_SIZE, "12px");
        timeSpan.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        timeSpan.getStyle().set(StyleConstants.CSS_MARGIN_TOP, "4px");

        textGroup.add(cardTitle, cardMessage, timeSpan);
        content.add(iconContainer, textGroup);
        card.add(content);

        // Hover effects
        card.getElement().addEventListener(StyleConstants.VAL_MOUSEENTER, e -> {
            card.getStyle().set(StyleConstants.CSS_TRANSFORM, "translateX(4px)");
            card.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 4px 20px rgba(0,0,0,0.08)");
            iconContainer.getStyle().set(StyleConstants.CSS_TRANSFORM, "scale(1.1) rotate(-5deg)");
        });

        card.getElement().addEventListener(StyleConstants.VAL_MOUSELEAVE, e -> {
            card.getStyle().set(StyleConstants.CSS_TRANSFORM, "translateX(0)");
            card.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "none");
            iconContainer.getStyle().set(StyleConstants.CSS_TRANSFORM, "scale(1) rotate(0deg)");
        });

        card.addClickListener(e -> {
            Notification.show(translationService.translate("notifications.opening", title), 3000,
                    Notification.Position.TOP_CENTER);
        });

        return card;
    }

    private void markAllAsRead() {
        // Reload with all notifications marked as read
        loadNotifications();
    }
}
