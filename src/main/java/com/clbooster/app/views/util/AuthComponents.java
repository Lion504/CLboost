package com.clbooster.app.views.util;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import java.util.Optional;

public final class AuthComponents {
    private static final String BACKGROUND = StyleConstants.CSS_BACKGROUND;
    private static final String BORDER_RADIUS = StyleConstants.CSS_BORDER_RADIUS;
    private static final String PADDING = StyleConstants.CSS_PADDING;
    private static final String WIDTH = StyleConstants.CSS_WIDTH;
    private static final String BOX_SHADOW = StyleConstants.CSS_BOX_SHADOW;
    private static final String BORDER = StyleConstants.CSS_BORDER;
    private static final String TRANSITION = StyleConstants.CSS_TRANSITION;
    private static final String MOUSEENTER = StyleConstants.VAL_MOUSEENTER;
    private static final String TRANSFORM = StyleConstants.CSS_TRANSFORM;
    private static final String MOUSELEAVE = StyleConstants.VAL_MOUSELEAVE;
    private static final String POINTER = StyleConstants.VAL_POINTER;
    private static final String CURSOR = StyleConstants.CSS_CURSOR;
    private static final String HEIGHT = StyleConstants.CSS_HEIGHT;
    private static final String COLOR = StyleConstants.CSS_COLOR;
    private static final String FONT_SIZE = StyleConstants.CSS_FONT_SIZE;
    private static final String FONT_WEIGHT = StyleConstants.CSS_FONT_WEIGHT;
    private static final String OPACITY = StyleConstants.CSS_OPACITY;
    private static final String MARGIN = StyleConstants.CSS_MARGIN;
    private static final String RGBA_BLACK_10 = "rgba(0, 0, 0, 0.1)";

    private AuthComponents() {
    }

    public static Div createCard() {
        Div card = new Div();
        card.getStyle().set(BACKGROUND, StyleConstants.BG_WHITE);
        card.getStyle().set(BORDER_RADIUS, "24px");
        card.getStyle().set(PADDING, "48px");
        card.getStyle().set(WIDTH, "100%");
        card.getStyle().set(StyleConstants.CSS_MAX_WIDTH, "420px");
        card.getStyle().set(BOX_SHADOW, StyleConstants.VAL_0_2_12PX);
        card.getStyle().set(BORDER, "1px solid rgba(0, 0, 0, 0.05)");
        card.getStyle().set(TRANSITION, "all 0.5s ease");

        // Hover effect for card
        card.getElement().addEventListener(MOUSEENTER, e -> {
            card.getStyle().set(BOX_SHADOW, "0 24px 48px rgba(0, 0, 0, 0.06)");
            card.getStyle().set(TRANSFORM, "translateY(-2px)");
        });
        card.getElement().addEventListener(MOUSELEAVE, e -> {
            card.getStyle().set(BOX_SHADOW, StyleConstants.VAL_0_2_12PX);
            card.getStyle().set(TRANSFORM, "translateY(0)");
        });

        return card;
    }

    public static HorizontalLayout createBackLink(String text, Runnable onClick) {
        HorizontalLayout backLink = new HorizontalLayout();
        backLink.setAlignItems(FlexComponent.Alignment.CENTER);
        backLink.getStyle().set("gap", "6px");
        backLink.getStyle().set(CURSOR, POINTER);
        backLink.getStyle().set(StyleConstants.CSS_MARGIN_BOTTOM, "24px");
        backLink.getStyle().set(WIDTH, "fit-content");
        backLink.getStyle().set(TRANSITION, "opacity 0.2s");

        Icon arrowLeft = VaadinIcon.ARROW_LEFT.create();
        arrowLeft.getStyle().set(WIDTH, "14px");
        arrowLeft.getStyle().set(HEIGHT, "14px");
        arrowLeft.getStyle().set(COLOR, StyleConstants.TEXT_SECONDARY);

        Span backText = new Span(text);
        backText.getStyle().set(FONT_SIZE, "13px");
        backText.getStyle().set(FONT_WEIGHT, "500");
        backText.getStyle().set(COLOR, StyleConstants.TEXT_SECONDARY);

        backLink.add(arrowLeft, backText);
        backLink.addClickListener(e -> onClick.run());
        backLink.getElement().addEventListener(MOUSEENTER, e -> backLink.getStyle().set(OPACITY, "0.7"));
        backLink.getElement().addEventListener(MOUSELEAVE, e -> backLink.getStyle().set(OPACITY, "1"));

        return backLink;
    }

    public static Div createLogoIcon() {
        Div logoIcon = new Div();
        logoIcon.getStyle().set(WIDTH, "48px");
        logoIcon.getStyle().set(HEIGHT, "48px");
        logoIcon.getStyle().set(BACKGROUND, StyleConstants.PRIMARY);
        logoIcon.getStyle().set(BORDER_RADIUS, "12px");
        logoIcon.getStyle().set(StyleConstants.CSS_DISPLAY, "flex");
        logoIcon.getStyle().set(StyleConstants.CSS_ALIGN_ITEMS, StyleConstants.VAL_CENTER);
        logoIcon.getStyle().set(StyleConstants.CSS_JUSTIFY_CONTENT, StyleConstants.VAL_CENTER);
        logoIcon.getStyle().set(StyleConstants.CSS_MARGIN_BOTTOM, "24px");
        logoIcon.getStyle().set(BOX_SHADOW, "0 10px 15px -3px rgba(0, 122, 255, 0.3)");
        logoIcon.add(VaadinIcon.SPARK_LINE.create());
        return logoIcon;
    }

    public static TextField createTextField(String label, String placeholder) {
        TextField field = new TextField(label);
        Optional.ofNullable(placeholder).ifPresent(field::setPlaceholder);
        field.setWidthFull();
        field.getStyle().set("--vaadin-input-field-background", StyleConstants.BG_GRAY);
        field.getStyle().set("--vaadin-input-field-border-radius", "12px");
        return field;
    }

    public static PasswordField createPasswordField(String label) {
        PasswordField field = new PasswordField(label);
        field.setWidthFull();
        field.getStyle().set("--vaadin-input-field-background", StyleConstants.BG_GRAY);
        field.getStyle().set("--vaadin-input-field-border-radius", "12px");
        return field;
    }

    public static Button createPrimaryButton(String text, Runnable action) {
        Button btn = new Button(text, e -> action.run());
        btn.getStyle().set(BACKGROUND, "linear-gradient(135deg, " + StyleConstants.PRIMARY + " 0%, #5AC8FA 100%)");
        btn.getStyle().set(COLOR, StyleConstants.VAL_WHITE);
        btn.getStyle().set(FONT_WEIGHT, "600");
        btn.getStyle().set(FONT_SIZE, "15px");
        btn.getStyle().set(BORDER_RADIUS, StyleConstants.VAL_9999PX);
        btn.getStyle().set(BORDER, "none");
        btn.getStyle().set(PADDING, "14px 24px");
        btn.getStyle().set(BOX_SHADOW, "0 10px 15px -3px rgba(0, 122, 255, 0.3)");
        btn.getStyle().set(TRANSITION, "all 0.2s ease");
        btn.getStyle().set(CURSOR, POINTER);

        btn.getElement().addEventListener(MOUSEENTER, e -> {
            btn.getStyle().set("filter", "brightness(1.1)");
            btn.getStyle().set(TRANSFORM, "translateY(-1px)");
        });
        btn.getElement().addEventListener(MOUSELEAVE, e -> {
            btn.getStyle().set("filter", "brightness(1)");
            btn.getStyle().set(TRANSFORM, "translateY(0)");
        });

        return btn;
    }

    public static Button createSocialButton(String provider) {
        Button btn = new Button(provider);
        btn.getStyle().set(BACKGROUND, StyleConstants.BG_GRAY);
        btn.getStyle().set(COLOR, StyleConstants.TEXT_PRIMARY);
        btn.getStyle().set(FONT_WEIGHT, "600");
        btn.getStyle().set(FONT_SIZE, "14px");
        btn.getStyle().set(BORDER_RADIUS, "12px");
        btn.getStyle().set(BORDER, "1px solid rgba(0, 0, 0, 0.05)");
        btn.getStyle().set(PADDING, "12px 24px");
        btn.getStyle().set(TRANSITION, "all 0.2s ease");
        btn.getStyle().set(CURSOR, POINTER);

        btn.getElement().addEventListener(MOUSEENTER, e -> {
            btn.getStyle().set(BACKGROUND, "rgba(0, 0, 0, 0.08)");
            btn.getStyle().set(StyleConstants.CSS_BORDER_COLOR, RGBA_BLACK_10);
        });
        btn.getElement().addEventListener(MOUSELEAVE, e -> {
            btn.getStyle().set(BACKGROUND, StyleConstants.BG_GRAY);
            btn.getStyle().set(StyleConstants.CSS_BORDER_COLOR, "rgba(0, 0, 0, 0.05)");
        });

        return btn;
    }

    public static HorizontalLayout createDivider(String text) {
        HorizontalLayout divider = new HorizontalLayout();
        divider.setWidthFull();
        divider.setAlignItems(FlexComponent.Alignment.CENTER);
        divider.getStyle().set("gap", "16px");
        divider.getStyle().set(MARGIN, "24px 0");

        Div line1 = new Div();
        line1.getStyle().set("flex", "1");
        line1.getStyle().set(HEIGHT, "1px");
        line1.getStyle().set(BACKGROUND, RGBA_BLACK_10);

        Span textSpan = new Span(text);
        textSpan.getStyle().set(FONT_SIZE, "13px");
        textSpan.getStyle().set(COLOR, StyleConstants.TEXT_SECONDARY);
        textSpan.getStyle().set(StyleConstants.CSS_WHITE_SPACE, "nowrap");

        Div line2 = new Div();
        line2.getStyle().set("flex", "1");
        line2.getStyle().set(HEIGHT, "1px");
        line2.getStyle().set(BACKGROUND, RGBA_BLACK_10);

        divider.add(line1, textSpan, line2);
        return divider;
    }

    public static H2 createTitle(String text) {
        H2 title = new H2(text);
        title.getStyle().set(FONT_SIZE, "28px");
        title.getStyle().set(FONT_WEIGHT, "700");
        title.getStyle().set(COLOR, StyleConstants.TEXT_PRIMARY);
        title.getStyle().set(MARGIN, "0 0 8px 0");
        title.getStyle().set(StyleConstants.CSS_LETTER_SPACING, "-0.025em");
        return title;
    }

    public static Paragraph createSubtitle(String text) {
        Paragraph subtitle = new Paragraph(text);
        subtitle.getStyle().set(FONT_SIZE, "15px");
        subtitle.getStyle().set(COLOR, StyleConstants.TEXT_SECONDARY);
        subtitle.getStyle().set(MARGIN, "0 0 32px 0");
        return subtitle;
    }

    public static HorizontalLayout createSocialButtonsLayout() {
        HorizontalLayout socialButtons = new HorizontalLayout();
        socialButtons.setWidthFull();
        socialButtons.getStyle().set("gap", "12px");

        Button googleBtn = createSocialButton("Google");
        Button linkedinBtn = createSocialButton("LinkedIn");

        socialButtons.add(googleBtn, linkedinBtn);
        socialButtons.expand(googleBtn, linkedinBtn);
        return socialButtons;
    }

    public static void showError(String message) {
        com.vaadin.flow.component.notification.Notification notification = new com.vaadin.flow.component.notification.Notification(
                message, 3000, com.vaadin.flow.component.notification.Notification.Position.TOP_CENTER);
        notification.addThemeVariants(com.vaadin.flow.component.notification.NotificationVariant.LUMO_ERROR);
        notification.open();
    }

    public static void showSuccess(String message) {
        com.vaadin.flow.component.notification.Notification notification = new com.vaadin.flow.component.notification.Notification(
                message, 3000, com.vaadin.flow.component.notification.Notification.Position.TOP_CENTER);
        notification.addThemeVariants(com.vaadin.flow.component.notification.NotificationVariant.LUMO_SUCCESS);
        notification.open();
    }

    public static void showInfo(String message) {
        com.vaadin.flow.component.notification.Notification notification = new com.vaadin.flow.component.notification.Notification(
                message, 4000, com.vaadin.flow.component.notification.Notification.Position.TOP_CENTER);
        notification.open();
    }

    public static void applyLinkHoverEffect(com.vaadin.flow.component.Component link) {
        link.getStyle().set(TRANSITION, "opacity 0.2s");
        link.getElement().addEventListener(MOUSEENTER, e -> link.getStyle().set(OPACITY, "0.7"));
        link.getElement().addEventListener(MOUSELEAVE, e -> link.getStyle().set(OPACITY, "1"));
    }
}