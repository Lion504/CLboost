package com.clbooster.app.views;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.tabs.*;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.router.*;

@Route(value = "profile", layout = MainLayout.class)
@PageTitle("Profile | CL Booster")
public class ProfileView extends VerticalLayout {

    public ProfileView() {
        setPadding(true);
        setSpacing(true);

        // User header row
        Avatar avatar = new Avatar("Alex Rivera");
        avatar.setColorIndex(2);
        avatar.setWidth("64px");
        avatar.setHeight("64px");

        H2 name = new H2("Alex Rivera");
        name.getStyle().set("margin", "0");
        Paragraph role = new Paragraph("Senior Product Designer • Helsinki, Finland");
        role.getStyle().set("color", "#6b7280").set("margin", "4px 0 0 0");

        Span planBadge = new Span("STANDARD ACCOUNT  |  V4.0 PRO");
        planBadge.getElement().getThemeList().add("badge contrast");
        planBadge.getStyle().set("font-size", "11px").set("letter-spacing", "0.5px");

        Button saveChanges = new Button("Save Changes");
        saveChanges.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout userInfo = new HorizontalLayout(avatar,
            new VerticalLayout(name, role, planBadge));
        userInfo.setAlignItems(FlexComponent.Alignment.CENTER);
        userInfo.setSpacing(true);

        HorizontalLayout headerRow = new HorizontalLayout(userInfo, saveChanges);
        headerRow.setWidthFull();
        headerRow.setAlignItems(FlexComponent.Alignment.CENTER);
        headerRow.expand(userInfo);

        // Tab navigation
        Tab general     = new Tab("General");
        Tab security    = new Tab("Security");
        Tab notifs      = new Tab("Notifications");
        Tab privacy     = new Tab("Data & Privacy");
        Tabs profileTabs = new Tabs(general, security, notifs, privacy);
        profileTabs.setWidthFull();

        // Account Details Form (shown for General tab)
        Div formCard = new Div();
        formCard.addClassName("cl-card");
        formCard.setWidthFull();

        H3 formTitle = new H3("Account Details");
        Paragraph formSub = new Paragraph("Personal information and preferences");
        formSub.getStyle().set("color", "#6b7280").set("margin-top", "0");

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2));

        TextField displayName = new TextField("Display Name");
        displayName.setValue("Alex Rivera");
        displayName.setWidthFull();

        EmailField emailField = new EmailField("Email Address");
        emailField.setValue("alex.riviera@example.com");
        emailField.setWidthFull();

        TextArea bio = new TextArea("Professional Bio");
        bio.setPlaceholder("Tell us about yourself...");
        bio.setMinHeight("100px");
        bio.setWidthFull();

        TextField writingTone = new TextField("Preferred Writing Tone");
        writingTone.setPlaceholder("e.g. Confident, Professional");
        writingTone.setWidthFull();

        form.add(displayName, emailField, bio, writingTone);
        form.setColspan(bio, 2);
        form.setColspan(writingTone, 2);

        formCard.add(formTitle, formSub, form);

        // Tab switch behaviour
        profileTabs.addSelectedChangeListener(event -> {
            formCard.setVisible(event.getSelectedTab() == general);
        });

        add(headerRow, profileTabs, formCard, saveChanges);
    }
}
