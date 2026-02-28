package com.clbooster.app.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.*;

@Route(value = "settings", layout = MainLayout.class)
@PageTitle("Settings | CL Booster")
public class SettingsView extends VerticalLayout {

    public SettingsView() {
        setPadding(true);
        setSpacing(true);
        setMaxWidth("800px");

        H2 title = new H2("Preferences");
        Paragraph sub = new Paragraph("Manage your application experience and accessibility.");
        sub.getStyle().set("color", "#6b7280").set("margin-top", "0");

        // --- Appearance Card ---
        Div appearanceCard = buildCard();
        H3 appearanceTitle = new H3("Appearance");
        Paragraph appearanceSub = new Paragraph("Customize how Cover Booster looks on your device.");
        appearanceSub.getStyle().set("color", "#6b7280").set("margin-top", "0");

        RadioButtonGroup<String> themeGroup = new RadioButtonGroup<>();
        themeGroup.setItems("Light", "Dark", "System");
        themeGroup.setValue("System");

        appearanceCard.add(appearanceTitle, appearanceSub, themeGroup);

        // --- Language Card ---
        Div langCard = buildCard();
        H3 langTitle = new H3("Language & Region");
        Paragraph langSub = new Paragraph("Select your preferred language for the interface and AI generation.");
        langSub.getStyle().set("color", "#6b7280").set("margin-top", "0");

        Select<String> langSelect = new Select<>();
        langSelect.setLabel("Language");
        langSelect.setItems("English", "Finnish (Suomi)", "Swedish (Svenska)", "German (Deutsch)", "French (Français)");
        langSelect.setValue("English");
        langSelect.setWidth("280px");

        langCard.add(langTitle, langSub, langSelect);

        // --- Notifications Card ---
        Div notifCard = buildCard();
        H3 notifTitle = new H3("Notifications");
        Paragraph notifSub = new Paragraph("Manage how and when you receive updates from us.");
        notifSub.getStyle().set("color", "#6b7280").set("margin-top", "0");

        Checkbox emailNotifs = toggleRow("Email Notifications", "Weekly summaries and match alerts", true);
        Checkbox pushNotifs = toggleRow("Push Notifications", "Real-time generation status", false);
        Checkbox productUpdates = toggleRow("Product Updates", "New features and tips", true);

        notifCard.add(notifTitle, notifSub, emailNotifs, pushNotifs, productUpdates);

        // --- Feedback Card ---
        Div feedbackCard = buildCard();
        H3 feedbackTitle = new H3("Feedback & Support");
        Paragraph feedbackSub = new Paragraph("Help us improve Cover Booster.");
        feedbackSub.getStyle().set("color", "#6b7280").set("margin-top", "0");
        Button sendFeedback = new Button("Send Feedback");
        sendFeedback.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        feedbackCard.add(feedbackTitle, feedbackSub, sendFeedback);

        // --- Action Buttons ---
        Button discard = new Button("Discard Changes", e -> Notification.show("Changes discarded"));
        discard.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button apply = new Button("Apply Settings",
                e -> Notification.show("Settings saved!", 3000, Notification.Position.BOTTOM_END));
        apply.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout actions = new HorizontalLayout(discard, apply);
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        actions.setWidthFull();

        add(title, sub, appearanceCard, langCard, notifCard, feedbackCard, actions);
    }

    private Div buildCard() {
        Div card = new Div();
        card.addClassName("cl-card");
        card.setWidthFull();
        card.getStyle().set("margin-bottom", "16px");
        return card;
    }

    private Checkbox toggleRow(String label, String description, boolean defaultValue) {
        Checkbox cb = new Checkbox(label);
        cb.setValue(defaultValue);
        // Add description as helper text via element
        cb.getStyle().set("margin-bottom", "8px");
        return cb;
    }
}
