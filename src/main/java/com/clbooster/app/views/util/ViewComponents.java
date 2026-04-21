package com.clbooster.app.views.util;

import com.clbooster.app.i18n.TranslationService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * UI component factory methods to avoid duplication across views.
 */
public final class ViewComponents {

    private static final String PRIMARY = "#007AFF";
    private static final String PRIMARY_LIGHT = "#5AC8FA";
    private static final String TEXT_PRIMARY = "#1d1d1f";
    private static final String TEXT_SECONDARY = "#86868b";
    private static final String BG_WHITE = "#ffffff";
    private static final String BG_GRAY = "#f5f5f7";
    private static final String LETTER_SPACING = "-0.025em";
    private static final String BORDER_SUBTLE = "1px solid rgba(0,0,0,0.05)";
    private static final String CSS_POSITION = "position";

    private ViewComponents() {
        // Utility class — prevent instantiation
    }

    /**
     * Creates a primary gradient background string.
     */
    public static String primaryGradient() {
        return "linear-gradient(135deg, " + PRIMARY + " 0%, " + PRIMARY_LIGHT + " 100%)";
    }

    /**
     * Creates a styled primary button with hover effects.
     */
    public static Button createPrimaryButton(String text, Runnable action) {
        Button btn = new Button(text, e -> action.run());
        stylePrimaryButtonGradient(btn);
        return btn;
    }

    /**
     * Creates a styled primary button with an icon (no action listener). Used in
     * EditorView.
     */
    public static Button createPrimaryButton(String text, VaadinIcon icon) {
        Button btn = new Button(text, icon.create());
        stylePrimaryButtonSolid(btn);
        return btn;
    }

    private static void stylePrimaryButtonGradient(Button btn) {
        btn.getStyle().set(StyleConstants.CSS_BACKGROUND, primaryGradient());
        btn.getStyle().set(StyleConstants.CSS_COLOR, StyleConstants.VAL_WHITE);
        btn.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "600");
        btn.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        btn.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX);
        btn.getStyle().set(StyleConstants.CSS_BORDER, "none");
        btn.getStyle().set(StyleConstants.CSS_PADDING, "12px 28px");
        btn.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 10px 15px -3px rgba(0,122,255,0.3)");
        btn.getStyle().set(StyleConstants.CSS_TRANSITION, StyleConstants.VAL_ALL_0_2S);
        btn.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);

        btn.getElement().addEventListener(StyleConstants.VAL_MOUSEENTER, e -> {
            btn.getStyle().set("filter", "brightness(1.1)");
            btn.getStyle().set(StyleConstants.CSS_TRANSFORM, "translateY(-1px)");
        });
        btn.getElement().addEventListener(StyleConstants.VAL_MOUSELEAVE, e -> {
            btn.getStyle().set("filter", "brightness(1)");
            btn.getStyle().set(StyleConstants.CSS_TRANSFORM, "translateY(0)");
        });
    }

    private static void stylePrimaryButtonSolid(Button btn) {
        btn.getStyle().set(StyleConstants.CSS_BACKGROUND, PRIMARY);
        btn.getStyle().set(StyleConstants.CSS_COLOR, StyleConstants.VAL_WHITE);
        btn.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "600");
        btn.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        btn.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX);
        btn.getStyle().set(StyleConstants.CSS_BORDER, "none");
        btn.getStyle().set(StyleConstants.CSS_PADDING, "10px 24px");
        btn.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 10px 15px -3px rgba(0, 122, 255, 0.3)");
    }

    /**
     * Creates a section card container with title and subtitle (raw strings).
     * Common pattern used in OnboardingView.
     */
    public static Div createSectionCard(String title, String subtitle) {
        Div card = new Div();
        card.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        card.getStyle().set(StyleConstants.CSS_BORDER, BORDER_SUBTLE);
        card.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "24px");
        card.getStyle().set(StyleConstants.CSS_PADDING, "32px");
        card.getStyle().set(StyleConstants.CSS_BOX_SHADOW, StyleConstants.VAL_0_2_12PX);

        H3 titleH3 = new H3(title);
        titleH3.getStyle().set(StyleConstants.CSS_FONT_SIZE, "18px");
        titleH3.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        titleH3.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        titleH3.getStyle().set(StyleConstants.CSS_MARGIN, "0 0 8px 0");

        Paragraph subtitlePara = new Paragraph(subtitle);
        subtitlePara.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        subtitlePara.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        subtitlePara.getStyle().set(StyleConstants.CSS_MARGIN, MARGIN_BOTTOM_24);

        card.add(titleH3, subtitlePara);
        return card;
    }

    /**
     * Creates a section card container with title and subtitle using translation
     * keys. Common pattern used in ProfileView.
     */
    public static Div createSectionCard(String titleKey, String subtitleKey,
            com.clbooster.app.i18n.TranslationService translationService) {
        Div card = new Div();
        card.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        card.getStyle().set(StyleConstants.CSS_BORDER, BORDER_SUBTLE);
        card.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "24px");
        card.getStyle().set(StyleConstants.CSS_PADDING, "32px");
        card.getStyle().set(StyleConstants.CSS_BOX_SHADOW, StyleConstants.VAL_0_2_12PX);

        H3 title = new H3(translationService.translate(titleKey));
        title.getStyle().set(StyleConstants.CSS_FONT_SIZE, "18px");
        title.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        title.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        title.getStyle().set(StyleConstants.CSS_MARGIN, "0 0 8px 0");

        Paragraph subtitle = new Paragraph(translationService.translate(subtitleKey));
        subtitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        subtitle.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        subtitle.getStyle().set(StyleConstants.CSS_MARGIN, MARGIN_BOTTOM_24);

        card.add(title, subtitle);
        return card;
    }

    /**
     * Creates a notification card with hover animation. Common pattern used in
     * NotificationsView.
     */
    @SuppressWarnings("java:S107")
    public static Div createNotificationCard(String title, String message, String time, VaadinIcon iconType,
            String iconColor, boolean isUnread, boolean isNew,
            com.clbooster.app.i18n.TranslationService translationService) {
        Div card = new Div();
        card.getStyle().set(StyleConstants.CSS_BACKGROUND, isUnread ? BG_WHITE : BG_GRAY);
        card.getStyle().set(StyleConstants.CSS_BORDER, BORDER_SUBTLE);
        card.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "16px");
        card.getStyle().set(StyleConstants.CSS_PADDING, "20px");
        card.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);
        card.getStyle().set(StyleConstants.CSS_TRANSITION, "all 0.3s cubic-bezier(0.4, 0, 0.2, 1)");
        card.getStyle().set(CSS_POSITION, "relative");
        card.getStyle().set(StyleConstants.CSS_OVERFLOW, "hidden");

        if (isNew) {
            card.getStyle().set("animation", "slideInRight 0.4s ease forwards");
        }

        if (isUnread) {
            Div unreadDot = new Div();
            unreadDot.getStyle().set(CSS_POSITION, "absolute");
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

        card.addClickListener(e -> Notification.show(translationService.translate("notifications.opening", title), 3000,
                    Notification.Position.TOP_CENTER));

        return card;
    }

    /**
     * Creates an onboarding step header (H1 + subtitle) with raw strings. Used in
     * OnboardingView.
     */
    public static VerticalLayout createStepHeader(String title, String subtitle) {
        VerticalLayout header = new VerticalLayout();
        header.setPadding(false);
        header.setSpacing(false);
        header.getStyle().set("gap", "8px");
        header.getStyle().set(StyleConstants.CSS_TEXT_ALIGN, StyleConstants.VAL_CENTER);

        H1 titleH1 = new H1(title);
        titleH1.getStyle().set(StyleConstants.CSS_FONT_SIZE, "32px");
        titleH1.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        titleH1.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        titleH1.getStyle().set(StyleConstants.CSS_MARGIN, "0");
        titleH1.getStyle().set(StyleConstants.CSS_LETTER_SPACING, LETTER_SPACING);

        Paragraph subtitlePara = new Paragraph(subtitle);
        subtitlePara.getStyle().set(StyleConstants.CSS_FONT_SIZE, "16px");
        subtitlePara.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        subtitlePara.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        header.add(titleH1, subtitlePara);
        return header;
    }

    /**
     * Creates a page header with title and subtitle using translation keys. Used in
     * NotificationsView and other pages.
     */
    public static HorizontalLayout createPageHeader(String titleKey, String subtitleKey,
            TranslationService translationService) {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout titleGroup = new VerticalLayout();
        titleGroup.setPadding(false);
        titleGroup.setSpacing(false);
        titleGroup.getStyle().set("gap", "4px");

        H1 title = new H1(translationService.translate(titleKey));
        title.getStyle().set(StyleConstants.CSS_FONT_SIZE, "30px");
        title.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        title.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        title.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        Paragraph subtitle = new Paragraph(translationService.translate(subtitleKey));
        subtitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        subtitle.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        subtitle.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        titleGroup.add(title, subtitle);
        header.add(titleGroup);
        header.expand(titleGroup);

        return header;
    }

    /**
     * Creates a notification card with hover animation (raw strings, no
     * translation). Used in NotificationsView.
     */
    public static Div createNotificationCard(String title, String message, String time, VaadinIcon iconType,
            String iconColor, boolean isUnread, boolean isNew) {
        return createNotificationCard(title, message, time, iconType, iconColor, isUnread, isNew, null);
    }

    /**
     * Creates a toggle row (title + description + toggle switch). Common pattern
     * used in OnboardingView.
     */
    public static HorizontalLayout createToggleRow(String title, String description, boolean enabled) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.getStyle().set(StyleConstants.CSS_PADDING, "8px 0");

        VerticalLayout textGroup = new VerticalLayout();
        textGroup.setPadding(false);
        textGroup.setSpacing(false);
        textGroup.getStyle().set("gap", "4px");

        Span titleSpan = new Span(title);
        titleSpan.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        titleSpan.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "600");
        titleSpan.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);

        Span descSpan = new Span(description);
        descSpan.getStyle().set(StyleConstants.CSS_FONT_SIZE, "13px");
        descSpan.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);

        textGroup.add(titleSpan, descSpan);

        Div toggle = createToggleSwitch(enabled);

        row.add(textGroup, toggle);
        row.expand(textGroup);

        return row;
    }

    private static Div createToggleSwitch(boolean enabled) {
        Div track = new Div();
        track.getStyle().set(StyleConstants.CSS_WIDTH, "48px");
        track.getStyle().set(StyleConstants.CSS_HEIGHT, "28px");
        track.getStyle().set(StyleConstants.CSS_BACKGROUND, enabled ? SUCCESS : "rgba(0,0,0,0.2)");
        track.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX);
        track.getStyle().set(CSS_POSITION, "relative");
        track.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);
        track.getStyle().set(StyleConstants.CSS_TRANSITION, "background 0.2s");

        Div thumb = new Div();
        thumb.getStyle().set(StyleConstants.CSS_WIDTH, "24px");
        thumb.getStyle().set(StyleConstants.CSS_HEIGHT, "24px");
        thumb.getStyle().set(StyleConstants.CSS_BACKGROUND, StyleConstants.VAL_WHITE);
        thumb.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "50%");
        thumb.getStyle().set(CSS_POSITION, "absolute");
        thumb.getStyle().set("top", "2px");
        thumb.getStyle().set(enabled ? "right" : "left", "2px");
        thumb.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 2px 4px rgba(0,0,0,0.2)");
        thumb.getStyle().set(StyleConstants.CSS_TRANSITION, StyleConstants.VAL_ALL_0_2S);

        track.add(thumb);

        return track;
    }

    private static final String MARGIN_BOTTOM_24 = "0 0 24px 0";
    private static final String SUCCESS = "#34C759";
}
