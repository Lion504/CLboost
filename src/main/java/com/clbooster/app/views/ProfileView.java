package com.clbooster.app.views;

import com.vaadin.flow.component.avatar.Avatar;
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
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "profile", layout = MainLayout.class)
@PageTitle("Profile | CL Booster")
public class ProfileView extends VerticalLayout {

    // Figma Design System Colors
    private static final String PRIMARY = "#007AFF";
    private static final String TEXT_PRIMARY = "#1d1d1f";
    private static final String TEXT_SECONDARY = "#86868b";
    private static final String BG_WHITE = "#ffffff";
    private static final String BG_GRAY = "#f5f5f7";
    private static final String SUCCESS = "#34C759";
    private static final String WARNING = "#FF9500";

    public ProfileView() {
        setPadding(true);
        setSpacing(true);
        getStyle().set("gap", "32px");
        getStyle().set("padding", "32px");
        getStyle().set("background", BG_WHITE);
        getStyle().set("font-family", "-apple-system, BlinkMacSystemFont, 'SF Pro Text', 'SF Pro Display', system-ui, sans-serif");
        setSizeFull();

        // Profile Header Card
        Div profileCard = createProfileHeader();

        // Tabs Navigation
        Tabs tabs = createTabs();

        // Content Card - General Tab
        Div contentCard = createGeneralContent();

        add(profileCard, tabs, contentCard);
    }

    private Div createProfileHeader() {
        Div card = new Div();
        card.getStyle().set("background", BG_WHITE);
        card.getStyle().set("border", "1px solid rgba(0, 0, 0, 0.05)");
        card.getStyle().set("border-radius", "24px");
        card.getStyle().set("padding", "32px");
        card.getStyle().set("width", "100%");
        card.getStyle().set("box-shadow", "0 2px 12px rgba(0, 0, 0, 0.04)");

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        // Avatar and user info
        HorizontalLayout userInfo = new HorizontalLayout();
        userInfo.setAlignItems(FlexComponent.Alignment.CENTER);
        userInfo.getStyle().set("gap", "20px");

        Avatar avatar = new Avatar("Alex Rivera");
        avatar.setColorIndex(2);
        avatar.setWidth("80px");
        avatar.setHeight("80px");
        avatar.getStyle().set("border", "4px solid " + BG_WHITE);
        avatar.getStyle().set("box-shadow", "0 4px 12px rgba(0, 0, 0, 0.1)");

        VerticalLayout nameGroup = new VerticalLayout();
        nameGroup.setPadding(false);
        nameGroup.setSpacing(false);
        nameGroup.getStyle().set("gap", "6px");

        H2 name = new H2("Alex Rivera");
        name.getStyle().set("font-size", "24px");
        name.getStyle().set("font-weight", "700");
        name.getStyle().set("color", TEXT_PRIMARY);
        name.getStyle().set("margin", "0");
        name.getStyle().set("letter-spacing", "-0.025em");

        Paragraph role = new Paragraph("Senior Product Designer • Helsinki, Finland");
        role.getStyle().set("font-size", "15px");
        role.getStyle().set("color", TEXT_SECONDARY);
        role.getStyle().set("margin", "0");

        // Plan badge
        HorizontalLayout badgeRow = new HorizontalLayout();
        badgeRow.getStyle().set("gap", "8px");
        badgeRow.getStyle().set("margin-top", "4px");

        Span planBadge = new Span("STANDARD ACCOUNT");
        planBadge.getStyle().set("font-size", "11px");
        planBadge.getStyle().set("font-weight", "700");
        planBadge.getStyle().set("padding", "4px 10px");
        planBadge.getStyle().set("background", BG_GRAY);
        planBadge.getStyle().set("color", TEXT_SECONDARY);
        planBadge.getStyle().set("border-radius", "9999px");
        planBadge.getStyle().set("letter-spacing", "0.05em");

        Span versionBadge = new Span("V4.0 PRO");
        versionBadge.getStyle().set("font-size", "11px");
        versionBadge.getStyle().set("font-weight", "700");
        versionBadge.getStyle().set("padding", "4px 10px");
        versionBadge.getStyle().set("background", "rgba(0, 122, 255, 0.1)");
        versionBadge.getStyle().set("color", PRIMARY);
        versionBadge.getStyle().set("border-radius", "9999px");
        versionBadge.getStyle().set("letter-spacing", "0.05em");

        badgeRow.add(planBadge, versionBadge);
        nameGroup.add(name, role, badgeRow);

        userInfo.add(avatar, nameGroup);

        // Action buttons
        HorizontalLayout actions = new HorizontalLayout();
        actions.getStyle().set("gap", "12px");

        Button editBtn = new Button("Edit Profile", VaadinIcon.PENCIL.create());
        editBtn.getStyle().set("background", BG_GRAY);
        editBtn.getStyle().set("color", TEXT_PRIMARY);
        editBtn.getStyle().set("font-weight", "600");
        editBtn.getStyle().set("border-radius", "9999px");
        editBtn.getStyle().set("padding", "10px 20px");
        editBtn.getStyle().set("border", "none");
        editBtn.getStyle().set("transition", "all 0.2s");
        editBtn.getStyle().set("cursor", "pointer");

        editBtn.getElement().addEventListener("mouseenter", e -> {
            editBtn.getStyle().set("background", "rgba(0, 0, 0, 0.08)");
        });
        editBtn.getElement().addEventListener("mouseleave", e -> {
            editBtn.getStyle().set("background", BG_GRAY);
        });

        Button saveBtn = createPrimaryButton("Save Changes", () -> {
            Notification.show("Profile saved successfully!", 3000, Notification.Position.BOTTOM_END);
        });

        actions.add(editBtn, saveBtn);

        header.add(userInfo, actions);
        header.expand(userInfo);

        card.add(header);

        return card;
    }

    private Tabs createTabs() {
        Tabs tabs = new Tabs();
        tabs.setWidthFull();
        tabs.getStyle().set("background", "transparent");

        Tab general = new Tab("General");
        Tab security = new Tab("Security");
        Tab notifications = new Tab("Notifications");
        Tab privacy = new Tab("Data & Privacy");

        general.getStyle().set("font-weight", "600");
        general.getStyle().set("font-size", "14px");

        tabs.add(general, security, notifications, privacy);
        tabs.setSelectedTab(general);

        // Add selection listener to handle tab switching
        tabs.addSelectedChangeListener(event -> {
            // In a real app, this would switch the content
            Notification.show("Switched to " + event.getSelectedTab().getLabel(), 2000, Notification.Position.BOTTOM_CENTER);
        });

        return tabs;
    }

    private Div createGeneralContent() {
        Div card = new Div();
        card.getStyle().set("background", BG_WHITE);
        card.getStyle().set("border", "1px solid rgba(0, 0, 0, 0.05)");
        card.getStyle().set("border-radius", "24px");
        card.getStyle().set("padding", "32px");
        card.getStyle().set("width", "100%");
        card.getStyle().set("box-shadow", "0 2px 12px rgba(0, 0, 0, 0.04)");

        // Section title
        H3 title = new H3("Account Details");
        title.getStyle().set("font-size", "20px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0 0 8px 0");

        Paragraph subtitle = new Paragraph("Personal information and preferences");
        subtitle.getStyle().set("font-size", "14px");
        subtitle.getStyle().set("color", TEXT_SECONDARY);
        subtitle.getStyle().set("margin", "0 0 32px 0");

        // Form grid
        HorizontalLayout formGrid = new HorizontalLayout();
        formGrid.setWidthFull();
        formGrid.getStyle().set("gap", "24px");

        // Left column
        VerticalLayout leftCol = new VerticalLayout();
        leftCol.setPadding(false);
        leftCol.setSpacing(false);
        leftCol.getStyle().set("gap", "20px");
        leftCol.setWidth("50%");

        // Display Name
        TextField displayName = createFormField("Display Name", "Alex Rivera");
        leftCol.add(displayName);

        // Preferred Writing Tone
        TextField writingTone = createFormField("Preferred Writing Tone", "Confident, Professional");
        leftCol.add(writingTone);

        // Right column
        VerticalLayout rightCol = new VerticalLayout();
        rightCol.setPadding(false);
        rightCol.setSpacing(false);
        rightCol.getStyle().set("gap", "20px");
        rightCol.setWidth("50%");

        // Email
        EmailField email = createEmailField("Email Address", "alex.riviera@example.com");
        rightCol.add(email);

        // Job Title
        TextField jobTitle = createFormField("Job Title", "Senior Product Designer");
        rightCol.add(jobTitle);

        formGrid.add(leftCol, rightCol);

        // Professional Bio (full width)
        VerticalLayout bioGroup = new VerticalLayout();
        bioGroup.setPadding(false);
        bioGroup.setSpacing(false);
        bioGroup.getStyle().set("gap", "8px");
        bioGroup.getStyle().set("margin-top", "24px");

        Span bioLabel = new Span("Professional Bio");
        bioLabel.getStyle().set("font-size", "12px");
        bioLabel.getStyle().set("font-weight", "700");
        bioLabel.getStyle().set("color", TEXT_SECONDARY);
        bioLabel.getStyle().set("text-transform", "uppercase");
        bioLabel.getStyle().set("letter-spacing", "0.05em");

        TextArea bio = new TextArea();
        bio.setPlaceholder("Tell us about yourself...");
        bio.setValue("Passionate product designer with 8+ years of experience creating user-centered digital experiences. Specialized in design systems and cross-functional collaboration.");
        bio.setWidthFull();
        bio.setMinHeight("120px");
        bio.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        bio.getStyle().set("--vaadin-input-field-border-radius", "12px");

        bioGroup.add(bioLabel, bio);

        // Stats section
        HorizontalLayout statsRow = new HorizontalLayout();
        statsRow.setWidthFull();
        statsRow.getStyle().set("gap", "16px");
        statsRow.getStyle().set("margin-top", "32px");
        statsRow.getStyle().set("padding-top", "32px");
        statsRow.getStyle().set("border-top", "1px solid rgba(0, 0, 0, 0.05)");

        statsRow.add(createStatCard("24", "Cover Letters", VaadinIcon.FILE_TEXT, PRIMARY));
        statsRow.add(createStatCard("98%", "Avg. Match", VaadinIcon.STAR, WARNING));
        statsRow.add(createStatCard("12", "Resumes", VaadinIcon.CLIPBOARD, SUCCESS));

        card.add(title, subtitle, formGrid, bioGroup, statsRow);

        return card;
    }

    private TextField createFormField(String label, String value) {
        VerticalLayout group = new VerticalLayout();
        group.setPadding(false);
        group.setSpacing(false);
        group.getStyle().set("gap", "8px");
        group.setWidthFull();

        Span labelSpan = new Span(label);
        labelSpan.getStyle().set("font-size", "12px");
        labelSpan.getStyle().set("font-weight", "700");
        labelSpan.getStyle().set("color", TEXT_SECONDARY);
        labelSpan.getStyle().set("text-transform", "uppercase");
        labelSpan.getStyle().set("letter-spacing", "0.05em");

        TextField field = new TextField();
        field.setValue(value);
        field.setWidthFull();
        field.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        field.getStyle().set("--vaadin-input-field-border-radius", "12px");

        group.add(labelSpan, field);

        // Return the field but we need the group for layout
        // For simplicity, we'll just return the field and add label separately
        return field;
    }

    private EmailField createEmailField(String label, String value) {
        VerticalLayout group = new VerticalLayout();
        group.setPadding(false);
        group.setSpacing(false);
        group.getStyle().set("gap", "8px");
        group.setWidthFull();

        Span labelSpan = new Span(label);
        labelSpan.getStyle().set("font-size", "12px");
        labelSpan.getStyle().set("font-weight", "700");
        labelSpan.getStyle().set("color", TEXT_SECONDARY);
        labelSpan.getStyle().set("text-transform", "uppercase");
        labelSpan.getStyle().set("letter-spacing", "0.05em");

        EmailField field = new EmailField();
        field.setValue(value);
        field.setWidthFull();
        field.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        field.getStyle().set("--vaadin-input-field-border-radius", "12px");

        group.add(labelSpan, field);

        return field;
    }

    private Div createStatCard(String value, String label, VaadinIcon iconType, String color) {
        Div card = new Div();
        card.getStyle().set("flex", "1");
        card.getStyle().set("background", BG_GRAY);
        card.getStyle().set("border-radius", "16px");
        card.getStyle().set("padding", "20px");
        card.getStyle().set("transition", "all 0.2s");

        HorizontalLayout content = new HorizontalLayout();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.getStyle().set("gap", "16px");

        Div iconContainer = new Div();
        iconContainer.getStyle().set("width", "44px");
        iconContainer.getStyle().set("height", "44px");
        iconContainer.getStyle().set("border-radius", "12px");
        iconContainer.getStyle().set("background", color + "20");
        iconContainer.getStyle().set("display", "flex");
        iconContainer.getStyle().set("align-items", "center");
        iconContainer.getStyle().set("justify-content", "center");

        Icon icon = iconType.create();
        icon.getStyle().set("color", color);
        icon.getStyle().set("width", "22px");
        icon.getStyle().set("height", "22px");
        iconContainer.add(icon);

        VerticalLayout textGroup = new VerticalLayout();
        textGroup.setPadding(false);
        textGroup.setSpacing(false);
        textGroup.getStyle().set("gap", "2px");

        Span valueSpan = new Span(value);
        valueSpan.getStyle().set("font-size", "24px");
        valueSpan.getStyle().set("font-weight", "700");
        valueSpan.getStyle().set("color", TEXT_PRIMARY);

        Span labelSpan = new Span(label);
        labelSpan.getStyle().set("font-size", "13px");
        labelSpan.getStyle().set("color", TEXT_SECONDARY);

        textGroup.add(valueSpan, labelSpan);
        content.add(iconContainer, textGroup);

        card.add(content);

        card.getElement().addEventListener("mouseenter", e -> {
            card.getStyle().set("background", "rgba(0, 0, 0, 0.06)");
        });
        card.getElement().addEventListener("mouseleave", e -> {
            card.getStyle().set("background", BG_GRAY);
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
        btn.getStyle().set("padding", "10px 24px");
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
