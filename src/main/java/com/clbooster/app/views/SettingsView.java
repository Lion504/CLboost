package com.clbooster.app.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "settings", layout = MainLayout.class)
@PageTitle("Settings | CL Booster")
public class SettingsView extends VerticalLayout {

    // Figma Design System Colors
    private static final String PRIMARY = "#007AFF";
    private static final String TEXT_PRIMARY = "#1d1d1f";
    private static final String TEXT_SECONDARY = "#86868b";
    private static final String BG_WHITE = "#ffffff";
    private static final String BG_GRAY = "#f5f5f7";
    private static final String SUCCESS = "#34C759";

    public SettingsView() {
        setPadding(true);
        setSpacing(true);
        getStyle().set("gap", "24px");
        getStyle().set("padding", "32px");
        getStyle().set("background", BG_WHITE);
        getStyle().set("font-family", "-apple-system, BlinkMacSystemFont, 'SF Pro Text', 'SF Pro Display', system-ui, sans-serif");
        setSizeFull();
        setMaxWidth("900px");

        // Page Header
        VerticalLayout pageHeader = new VerticalLayout();
        pageHeader.setPadding(false);
        pageHeader.setSpacing(false);
        pageHeader.getStyle().set("gap", "4px");
        pageHeader.getStyle().set("margin-bottom", "8px");

        H2 title = new H2("Preferences");
        title.getStyle().set("font-size", "30px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("letter-spacing", "-0.025em");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0");

        Paragraph subtitle = new Paragraph("Manage your application experience and accessibility.");
        subtitle.getStyle().set("font-size", "14px");
        subtitle.getStyle().set("color", TEXT_SECONDARY);
        subtitle.getStyle().set("margin", "0");

        pageHeader.add(title, subtitle);

        // Appearance Card
        Div appearanceCard = createAppearanceCard();

        // Language Card
        Div languageCard = createLanguageCard();

        // Notifications Card
        Div notificationsCard = createNotificationsCard();

        // Data & Privacy Card
        Div privacyCard = createPrivacyCard();

        // Action buttons
        HorizontalLayout actions = createActionButtons();

        add(pageHeader, appearanceCard, languageCard, notificationsCard, privacyCard, actions);
    }

    private Div createAppearanceCard() {
        Div card = createCard();

        H3 title = new H3("Appearance");
        title.getStyle().set("font-size", "18px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0 0 8px 0");

        Paragraph desc = new Paragraph("Customize how Cover Booster looks on your device.");
        desc.getStyle().set("font-size", "14px");
        desc.getStyle().set("color", TEXT_SECONDARY);
        desc.getStyle().set("margin", "0 0 24px 0");

        // Theme options
        HorizontalLayout themeOptions = new HorizontalLayout();
        themeOptions.getStyle().set("gap", "12px");

        themeOptions.add(createThemeOption("Light", VaadinIcon.SUN_O, false));
        themeOptions.add(createThemeOption("Dark", VaadinIcon.MOON_O, false));
        themeOptions.add(createThemeOption("System", VaadinIcon.DESKTOP, true));

        card.add(title, desc, themeOptions);

        return card;
    }

    private Div createThemeOption(String label, VaadinIcon iconType, boolean selected) {
        Div option = new Div();
        option.getStyle().set("display", "flex");
        option.getStyle().set("align-items", "center");
        option.getStyle().set("gap", "10px");
        option.getStyle().set("padding", "16px 20px");
        option.getStyle().set("background", selected ? "rgba(0, 122, 255, 0.1)" : BG_GRAY);
        option.getStyle().set("border", selected ? "2px solid " + PRIMARY : "2px solid transparent");
        option.getStyle().set("border-radius", "16px");
        option.getStyle().set("cursor", "pointer");
        option.getStyle().set("transition", "all 0.2s");
        option.getStyle().set("min-width", "120px");

        Icon icon = iconType.create();
        icon.getStyle().set("color", selected ? PRIMARY : TEXT_SECONDARY);
        icon.getStyle().set("width", "20px");
        icon.getStyle().set("height", "20px");

        Span labelSpan = new Span(label);
        labelSpan.getStyle().set("font-size", "14px");
        labelSpan.getStyle().set("font-weight", selected ? "600" : "500");
        labelSpan.getStyle().set("color", selected ? PRIMARY : TEXT_PRIMARY);

        option.add(icon, labelSpan);

        option.getElement().addEventListener("mouseenter", e -> {
            if (!selected) {
                option.getStyle().set("background", "rgba(0, 0, 0, 0.06)");
            }
        });
        option.getElement().addEventListener("mouseleave", e -> {
            if (!selected) {
                option.getStyle().set("background", BG_GRAY);
            }
        });

        return option;
    }

    private Div createLanguageCard() {
        Div card = createCard();

        H3 title = new H3("Language & Region");
        title.getStyle().set("font-size", "18px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0 0 8px 0");

        Paragraph desc = new Paragraph("Select your preferred language for the interface and AI generation.");
        desc.getStyle().set("font-size", "14px");
        desc.getStyle().set("color", TEXT_SECONDARY);
        desc.getStyle().set("margin", "0 0 24px 0");

        // Language select
        VerticalLayout selectGroup = new VerticalLayout();
        selectGroup.setPadding(false);
        selectGroup.setSpacing(false);
        selectGroup.getStyle().set("gap", "8px");
        selectGroup.setWidth("280px");

        Span label = new Span("Language");
        label.getStyle().set("font-size", "12px");
        label.getStyle().set("font-weight", "700");
        label.getStyle().set("color", TEXT_SECONDARY);
        label.getStyle().set("text-transform", "uppercase");
        label.getStyle().set("letter-spacing", "0.05em");

        Select<String> langSelect = new Select<>();
        langSelect.setItems("English", "Finnish (Suomi)", "Swedish (Svenska)", "German (Deutsch)", "French (Français)");
        langSelect.setValue("English");
        langSelect.setWidthFull();
        langSelect.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        langSelect.getStyle().set("--vaadin-input-field-border-radius", "12px");

        selectGroup.add(label, langSelect);
        card.add(title, desc, selectGroup);

        return card;
    }

    private Div createNotificationsCard() {
        Div card = createCard();

        H3 title = new H3("Notifications");
        title.getStyle().set("font-size", "18px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0 0 8px 0");

        Paragraph desc = new Paragraph("Manage how and when you receive updates from us.");
        desc.getStyle().set("font-size", "14px");
        desc.getStyle().set("color", TEXT_SECONDARY);
        desc.getStyle().set("margin", "0 0 24px 0");

        // Toggle items
        VerticalLayout toggles = new VerticalLayout();
        toggles.setPadding(false);
        toggles.setSpacing(false);
        toggles.getStyle().set("gap", "16px");

        toggles.add(createToggleRow("Email Notifications", "Weekly summaries and match alerts", true));
        toggles.add(createToggleRow("Push Notifications", "Real-time generation status", false));
        toggles.add(createToggleRow("Product Updates", "New features and tips", true));
        toggles.add(createToggleRow("Marketing", "Special offers and promotions", false));

        card.add(title, desc, toggles);

        return card;
    }

    private HorizontalLayout createToggleRow(String title, String description, boolean enabled) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.getStyle().set("padding", "12px 0");
        row.getStyle().set("border-bottom", "1px solid rgba(0, 0, 0, 0.05)");

        VerticalLayout textGroup = new VerticalLayout();
        textGroup.setPadding(false);
        textGroup.setSpacing(false);
        textGroup.getStyle().set("gap", "4px");

        Span titleSpan = new Span(title);
        titleSpan.getStyle().set("font-size", "15px");
        titleSpan.getStyle().set("font-weight", "600");
        titleSpan.getStyle().set("color", TEXT_PRIMARY);

        Span descSpan = new Span(description);
        descSpan.getStyle().set("font-size", "13px");
        descSpan.getStyle().set("color", TEXT_SECONDARY);

        textGroup.add(titleSpan, descSpan);

        // Toggle switch
        Div toggle = createToggleSwitch(enabled);

        row.add(textGroup, toggle);
        row.expand(textGroup);

        return row;
    }

    private Div createToggleSwitch(boolean enabled) {
        Div track = new Div();
        track.getStyle().set("width", "48px");
        track.getStyle().set("height", "28px");
        track.getStyle().set("background", enabled ? SUCCESS : "rgba(0, 0, 0, 0.2)");
        track.getStyle().set("border-radius", "9999px");
        track.getStyle().set("position", "relative");
        track.getStyle().set("cursor", "pointer");
        track.getStyle().set("transition", "background 0.2s");

        Div thumb = new Div();
        thumb.getStyle().set("width", "24px");
        thumb.getStyle().set("height", "24px");
        thumb.getStyle().set("background", "white");
        thumb.getStyle().set("border-radius", "50%");
        thumb.getStyle().set("position", "absolute");
        thumb.getStyle().set("top", "2px");
        thumb.getStyle().set(enabled ? "right" : "left", "2px");
        thumb.getStyle().set("box-shadow", "0 2px 4px rgba(0,0,0,0.2)");
        thumb.getStyle().set("transition", "all 0.2s");

        track.add(thumb);

        track.getElement().addEventListener("click", e -> {
            boolean newState = !enabled;
            track.getStyle().set("background", newState ? SUCCESS : "rgba(0, 0, 0, 0.2)");
            thumb.getStyle().set(newState ? "right" : "left", "2px");
            thumb.getStyle().remove(newState ? "left" : "right");
        });

        return track;
    }

    private Div createPrivacyCard() {
        Div card = createCard();

        H3 title = new H3("Data & Privacy");
        title.getStyle().set("font-size", "18px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0 0 8px 0");

        Paragraph desc = new Paragraph("Control your data and privacy settings.");
        desc.getStyle().set("font-size", "14px");
        desc.getStyle().set("color", TEXT_SECONDARY);
        desc.getStyle().set("margin", "0 0 24px 0");

        // Privacy options
        VerticalLayout options = new VerticalLayout();
        options.setPadding(false);
        options.setSpacing(false);
        options.getStyle().set("gap", "16px");

        options.add(createPrivacyRow("Store cover letters in cloud", true));
        options.add(createPrivacyRow("Allow AI improvement training", false));
        options.add(createPrivacyRow("Share anonymized usage data", true));

        // Danger zone
        Div dangerZone = new Div();
        dangerZone.getStyle().set("margin-top", "24px");
        dangerZone.getStyle().set("padding-top", "24px");
        dangerZone.getStyle().set("border-top", "1px solid rgba(255, 59, 48, 0.2)");

        HorizontalLayout dangerRow = new HorizontalLayout();
        dangerRow.setWidthFull();
        dangerRow.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout dangerText = new VerticalLayout();
        dangerText.setPadding(false);
        dangerText.setSpacing(false);
        dangerText.getStyle().set("gap", "4px");

        Span dangerTitle = new Span("Delete Account");
        dangerTitle.getStyle().set("font-size", "15px");
        dangerTitle.getStyle().set("font-weight", "600");
        dangerTitle.getStyle().set("color", "#FF3B30");

        Span dangerDesc = new Span("Permanently delete your account and all data");
        dangerDesc.getStyle().set("font-size", "13px");
        dangerDesc.getStyle().set("color", TEXT_SECONDARY);

        dangerText.add(dangerTitle, dangerDesc);

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyle().set("background", "rgba(255, 59, 48, 0.1)");
        deleteBtn.getStyle().set("color", "#FF3B30");
        deleteBtn.getStyle().set("font-weight", "600");
        deleteBtn.getStyle().set("border-radius", "9999px");
        deleteBtn.getStyle().set("padding", "8px 16px");
        deleteBtn.getStyle().set("border", "none");
        deleteBtn.getStyle().set("cursor", "pointer");
        deleteBtn.getStyle().set("transition", "all 0.2s");

        deleteBtn.getElement().addEventListener("mouseenter", e -> {
            deleteBtn.getStyle().set("background", "rgba(255, 59, 48, 0.2)");
        });
        deleteBtn.getElement().addEventListener("mouseleave", e -> {
            deleteBtn.getStyle().set("background", "rgba(255, 59, 48, 0.1)");
        });

        dangerRow.add(dangerText, deleteBtn);
        dangerRow.expand(dangerText);

        dangerZone.add(dangerRow);

        card.add(title, desc, options, dangerZone);

        return card;
    }

    private HorizontalLayout createPrivacyRow(String title, boolean enabled) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.getStyle().set("padding", "12px 0");
        row.getStyle().set("border-bottom", "1px solid rgba(0, 0, 0, 0.05)");

        Span titleSpan = new Span(title);
        titleSpan.getStyle().set("font-size", "15px");
        titleSpan.getStyle().set("font-weight", "500");
        titleSpan.getStyle().set("color", TEXT_PRIMARY);

        Div toggle = createToggleSwitch(enabled);

        row.add(titleSpan, toggle);
        row.expand(titleSpan);

        return row;
    }

    private HorizontalLayout createActionButtons() {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setWidthFull();
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        actions.getStyle().set("gap", "12px");
        actions.getStyle().set("margin-top", "8px");

        Button discardBtn = new Button("Discard Changes", e -> {
            Notification.show("Changes discarded", 2000, Notification.Position.BOTTOM_END);
        });
        discardBtn.getStyle().set("background", "transparent");
        discardBtn.getStyle().set("color", TEXT_SECONDARY);
        discardBtn.getStyle().set("font-weight", "600");
        discardBtn.getStyle().set("border-radius", "9999px");
        discardBtn.getStyle().set("padding", "12px 24px");
        discardBtn.getStyle().set("border", "none");
        discardBtn.getStyle().set("cursor", "pointer");
        discardBtn.getStyle().set("transition", "all 0.2s");

        discardBtn.getElement().addEventListener("mouseenter", e -> {
            discardBtn.getStyle().set("background", "rgba(0, 0, 0, 0.05)");
            discardBtn.getStyle().set("color", TEXT_PRIMARY);
        });
        discardBtn.getElement().addEventListener("mouseleave", e -> {
            discardBtn.getStyle().set("background", "transparent");
            discardBtn.getStyle().set("color", TEXT_SECONDARY);
        });

        Button saveBtn = createPrimaryButton("Save Changes", () -> {
            Notification.show("Settings saved successfully!", 3000, Notification.Position.BOTTOM_END);
        });

        actions.add(discardBtn, saveBtn);

        return actions;
    }

    private Div createCard() {
        Div card = new Div();
        card.getStyle().set("background", BG_WHITE);
        card.getStyle().set("border", "1px solid rgba(0, 0, 0, 0.05)");
        card.getStyle().set("border-radius", "24px");
        card.getStyle().set("padding", "28px");
        card.getStyle().set("box-shadow", "0 2px 12px rgba(0, 0, 0, 0.04)");
        card.getStyle().set("transition", "all 0.3s");

        card.getElement().addEventListener("mouseenter", e -> {
            card.getStyle().set("box-shadow", "0 8px 24px rgba(0, 0, 0, 0.06)");
        });
        card.getElement().addEventListener("mouseleave", e -> {
            card.getStyle().set("box-shadow", "0 2px 12px rgba(0, 0, 0, 0.04)");
        });

        return card;
    }

    private Button createPrimaryButton(String text, Runnable action) {
        Button btn = new Button(text, e -> action.run());
        btn.getStyle().set("background", "linear-gradient(135deg, " + PRIMARY + " 0%, #5AC8FA 100%)");
        btn.getStyle().set("color", "white");
        btn.getStyle().set("font-weight", "600");
        btn.getStyle().set("font-size", "14px");
        btn.getStyle().set("border-radius", "9999px");
        btn.getStyle().set("border", "none");
        btn.getStyle().set("padding", "12px 24px");
        btn.getStyle().set("box-shadow", "0 10px 15px -3px rgba(0, 122, 255, 0.3)");
        btn.getStyle().set("transition", "all 0.2s");
        btn.getStyle().set("cursor", "pointer");

        btn.getElement().addEventListener("mouseenter", e -> {
            btn.getStyle().set("filter", "brightness(1.1)");
            btn.getStyle().set("transform", "translateY(-1px)");
        });
        btn.getElement().addEventListener("mouseleave", e -> {
            btn.getStyle().set("filter", "brightness(1)");
            btn.getStyle().set("transform", "translateY(0)");
        });

        return btn;
    }
}
