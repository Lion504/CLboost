package com.clbooster.app.views;

import com.clbooster.app.views.util.StyleConstants;

import jakarta.annotation.security.PermitAll;
import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.clbooster.app.backend.service.profile.Profile;
import com.clbooster.app.backend.service.profile.ProfileService;
import com.clbooster.app.backend.service.profile.User;
import com.clbooster.app.i18n.TranslationService;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
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
import java.util.regex.Pattern;

@Route(value = "profile", layout = MainLayout.class)
@PageTitle("Profile | CL Booster")
@PermitAll
public class ProfileView extends VerticalLayout {
    private static final String LETTER_SPACING = "0.05em";
    private static final String BORDER_BOTTOM = "1px solid rgba(0, 0, 0, 0.05)";

    // Figma Design System Colors
    private static final String PRIMARY = "#007AFF";
    private static final String TEXT_PRIMARY = "#1d1d1f";
    private static final String TEXT_SECONDARY = "#86868b";
    private static final String BG_WHITE = "#ffffff";
    private static final String BG_GRAY = "#f5f5f7";

    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]");

        private final transient AuthenticationService authService;
        private final transient ProfileService profileService;
    private final TranslationService translationService;
        private transient User currentUser;
        private transient Profile userProfile;

    // Form fields - stored as instance variables for save functionality
    private TextField firstNameField;
    private TextField lastNameField;
    private TextField usernameField;
    private EmailField emailField;
    private TextField experienceField;
    private TextArea skillsArea;
    private TextArea toolsArea;
    private TextField linkField;

    // Security tab fields
    private com.vaadin.flow.component.textfield.PasswordField currentPasswordField;
    private com.vaadin.flow.component.textfield.PasswordField newPasswordField;
    private com.vaadin.flow.component.textfield.PasswordField confirmPasswordField;

    private boolean editMode = false;
    private Button editBtn;
    private Button saveBtn;
    private Div generalContent;
    private Div securityContent;

    public ProfileView() {
        this.authService = new AuthenticationService();
        this.profileService = new ProfileService();
        this.translationService = new TranslationService();
        this.currentUser = authService.getCurrentUser();
        this.userProfile = currentUser != null
                ? profileService.getProfile(currentUser.getPin(), translationService.getCurrentLocale())
                : null;

        setPadding(true);
        setSpacing(true);
        getStyle().set("gap", "32px");
        getStyle().set(StyleConstants.CSS_PADDING, "32px");
        getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        getStyle().set("font-family",
                "-apple-system, BlinkMacSystemFont, 'SF Pro Text', 'SF Pro Display', system-ui, sans-serif");
        setSizeFull();

        // Profile Header Card
        Div profileCard = createProfileHeader();

        // Tabs Navigation
        Tabs tabs = createTabs();

        // Content containers
        generalContent = createGeneralContent();
        securityContent = createSecurityContent();
        securityContent.setVisible(false);

        add(profileCard, tabs, generalContent, securityContent);

        // Set initial edit mode
        setFieldsEditable(false);
    }

    private Div createProfileHeader() {
        Div card = new Div();
        card.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        card.getStyle().set(StyleConstants.CSS_BORDER, BORDER_BOTTOM);
        card.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "24px");
        card.getStyle().set(StyleConstants.CSS_PADDING, "32px");
        card.getStyle().set(StyleConstants.CSS_WIDTH, "100%");
        card.getStyle().set(StyleConstants.CSS_BOX_SHADOW, StyleConstants.VAL_0_2_12PX);

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        // Avatar and user info - using actual user data
        HorizontalLayout userInfo = new HorizontalLayout();
        userInfo.setAlignItems(FlexComponent.Alignment.CENTER);
        userInfo.getStyle().set("gap", "20px");

        String userFullName = currentUser != null ? currentUser.getFirstName() + " " + currentUser.getLastName()
                : "Guest User";
        String username = currentUser != null && currentUser.getUsername() != null
                && !currentUser.getUsername().isBlank() ? "@" + currentUser.getUsername() : "@guest";
        String userEmail = currentUser != null ? currentUser.getIdentityEmail() : "guest@example.com";

        Avatar avatar = new Avatar(userFullName);
        avatar.setColorIndex(2);
        avatar.setWidth("80px");
        avatar.setHeight("80px");
        avatar.getStyle().set(StyleConstants.CSS_BORDER, "4px solid " + BG_WHITE);
        avatar.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 4px 12px rgba(0, 0, 0, 0.1)");

        VerticalLayout nameGroup = new VerticalLayout();
        nameGroup.setPadding(false);
        nameGroup.setSpacing(false);
        nameGroup.getStyle().set("gap", "6px");

        H2 name = new H2(userFullName);
        name.getStyle().set(StyleConstants.CSS_FONT_SIZE, "24px");
        name.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        name.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        name.getStyle().set(StyleConstants.CSS_MARGIN, "0");
        name.getStyle().set(StyleConstants.CSS_LETTER_SPACING, "-0.025em");

        Paragraph usernameLine = new Paragraph(username);
        usernameLine.getStyle().set(StyleConstants.CSS_FONT_SIZE, "15px");
        usernameLine.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        usernameLine.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        Paragraph emailLine = new Paragraph(userEmail);
        emailLine.getStyle().set(StyleConstants.CSS_FONT_SIZE, "13px");
        emailLine.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        emailLine.getStyle().set(StyleConstants.CSS_OPACITY, "0.85");
        emailLine.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        // Plan badge
        HorizontalLayout badgeRow = new HorizontalLayout();
        badgeRow.getStyle().set("gap", "8px");
        badgeRow.getStyle().set(StyleConstants.CSS_MARGIN_TOP, "4px");

        Span planBadge = new Span(translationService.translate("profile.standardAccount"));
        planBadge.getStyle().set(StyleConstants.CSS_FONT_SIZE, "11px");
        planBadge.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        planBadge.getStyle().set(StyleConstants.CSS_PADDING, "4px 10px");
        planBadge.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_GRAY);
        planBadge.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        planBadge.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX);
        planBadge.getStyle().set(StyleConstants.CSS_LETTER_SPACING, LETTER_SPACING);

        Span versionBadge = new Span(translationService.translate("profile.v4pro"));
        versionBadge.getStyle().set(StyleConstants.CSS_FONT_SIZE, "11px");
        versionBadge.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        versionBadge.getStyle().set(StyleConstants.CSS_PADDING, "4px 10px");
        versionBadge.getStyle().set(StyleConstants.CSS_BACKGROUND, "rgba(0, 122, 255, 0.1)");
        versionBadge.getStyle().set(StyleConstants.CSS_COLOR, PRIMARY);
        versionBadge.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX);
        versionBadge.getStyle().set(StyleConstants.CSS_LETTER_SPACING, LETTER_SPACING);

        badgeRow.add(planBadge, versionBadge);
        nameGroup.add(name, usernameLine, emailLine, badgeRow);

        userInfo.add(avatar, nameGroup);

        // Action buttons
        HorizontalLayout actions = new HorizontalLayout();
        actions.getStyle().set("gap", "12px");

        editBtn = new Button(translationService.translate("profile.editProfile"), VaadinIcon.PENCIL.create());
        editBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_GRAY);
        editBtn.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        editBtn.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "600");
        editBtn.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX);
        editBtn.getStyle().set(StyleConstants.CSS_PADDING, "10px 20px");
        editBtn.getStyle().set(StyleConstants.CSS_BORDER, "none");
        editBtn.getStyle().set(StyleConstants.CSS_TRANSITION, StyleConstants.VAL_ALL_0_2S);
        editBtn.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);

        editBtn.getElement().addEventListener(StyleConstants.VAL_MOUSEENTER, e -> {
            editBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, "rgba(0, 0, 0, 0.08)");
        });
        editBtn.getElement().addEventListener(StyleConstants.VAL_MOUSELEAVE, e -> {
            editBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_GRAY);
        });

        editBtn.addClickListener(e -> toggleEditMode());

        saveBtn = createPrimaryButton(translationService.translate("profile.saveChanges"), this::saveProfile);
        saveBtn.setVisible(false); // Hidden initially

        actions.add(editBtn, saveBtn);

        header.add(userInfo, actions);
        header.expand(userInfo);

        card.add(header);

        return card;
    }

    private Tabs createTabs() {
        Tabs tabs = new Tabs();
        tabs.setWidthFull();
        tabs.getStyle().set(StyleConstants.CSS_BACKGROUND, StyleConstants.VAL_TRANSPARENT);

        Tab general = new Tab(translationService.translate("profile.general"));
        Tab security = new Tab(translationService.translate("profile.security"));
        // Notifications and Data & Privacy tabs removed - available via dedicated views

        general.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "600");
        general.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");

        tabs.add(general, security);
        tabs.setSelectedTab(general);

        // Add selection listener to handle tab switching
        tabs.addSelectedChangeListener(event -> {
            String selectedLabel = event.getSelectedTab().getLabel();
            if (selectedLabel.equals(translationService.translate("profile.general"))) {
                generalContent.setVisible(true);
                securityContent.setVisible(false);
            } else if (selectedLabel.equals(translationService.translate("profile.security"))) {
                generalContent.setVisible(false);
                securityContent.setVisible(true);
            }
        });

        return tabs;
    }

    private Div createGeneralContent() {
        Div card = new Div();
        card.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        card.getStyle().set(StyleConstants.CSS_BORDER, BORDER_BOTTOM);
        card.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "24px");
        card.getStyle().set(StyleConstants.CSS_PADDING, "32px");
        card.getStyle().set(StyleConstants.CSS_WIDTH, "100%");
        card.getStyle().set(StyleConstants.CSS_BOX_SHADOW, StyleConstants.VAL_0_2_12PX);

        // Section title
        H3 title = new H3(translationService.translate("profile.accountDetails"));
        title.getStyle().set(StyleConstants.CSS_FONT_SIZE, "20px");
        title.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        title.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        title.getStyle().set(StyleConstants.CSS_MARGIN, "0 0 8px 0");

        Paragraph subtitle = new Paragraph(translationService.translate("profile.personalInfo"));
        subtitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        subtitle.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        subtitle.getStyle().set(StyleConstants.CSS_MARGIN, "0 0 32px 0");

        // Form grid
        HorizontalLayout formGrid = new HorizontalLayout();
        formGrid.setWidthFull();
        formGrid.getStyle().set("gap", "24px");

        // Left column - User Info
        VerticalLayout leftCol = new VerticalLayout();
        leftCol.setPadding(false);
        leftCol.setSpacing(false);
        leftCol.getStyle().set("gap", "20px");
        leftCol.setWidth("50%");

        // First Name
        String firstName = currentUser != null ? currentUser.getFirstName() : "";
        firstNameField = createFormField(translationService.translate("profile.firstName"), firstName);
        leftCol.add(createFormGroup(translationService.translate("profile.firstName"), firstNameField));

        // Username
        String username = currentUser != null ? currentUser.getUsername() : "";
        usernameField = createFormField(translationService.translate("profile.username"), username);
        usernameField.setReadOnly(true); // Username cannot be changed
        leftCol.add(createFormGroup(translationService.translate("profile.username"), usernameField));

        // Experience Level (from Profile)
        String expLevel = userProfile != null && userProfile.getExperienceLevel() != null
                ? userProfile.getExperienceLevel()
                : "";
        experienceField = createFormField(translationService.translate("profile.experienceLevel"), expLevel);
        leftCol.add(createFormGroup(translationService.translate("profile.experienceLevel"), experienceField));

        // Right column - Profile Info
        VerticalLayout rightCol = new VerticalLayout();
        rightCol.setPadding(false);
        rightCol.setSpacing(false);
        rightCol.getStyle().set("gap", "20px");
        rightCol.setWidth("50%");

        // Last Name
        String lastName = currentUser != null ? currentUser.getLastName() : "";
        lastNameField = createFormField(translationService.translate("profile.lastName"), lastName);
        rightCol.add(createFormGroup(translationService.translate("profile.lastName"), lastNameField));

        // Email
        String email = currentUser != null ? currentUser.getIdentityEmail() : "";
        emailField = createEmailField(translationService.translate("profile.email"), email);
        rightCol.add(createFormGroup(translationService.translate("profile.email"), emailField));

        formGrid.add(leftCol, rightCol);

        // Skills Section (from Profile)
        String skills = userProfile != null && userProfile.getSkills() != null ? userProfile.getSkills() : "";
        skillsArea = createTextArea(translationService.translate("profile.skills"), skills,
                translationService.translate("profile.addSkills"));
        VerticalLayout skillsGroup = createFormGroup(translationService.translate("profile.skills"), skillsArea);
        skillsGroup.getStyle().set(StyleConstants.CSS_MARGIN_TOP, "24px");

        // Tools Section (from Profile)
        String tools = userProfile != null && userProfile.getTools() != null ? userProfile.getTools() : "";
        toolsArea = createTextArea(translationService.translate("profile.tools"), tools,
                translationService.translate("profile.addTools"));
        VerticalLayout toolsGroup = createFormGroup(translationService.translate("profile.tools"), toolsArea);
        toolsGroup.getStyle().set(StyleConstants.CSS_MARGIN_TOP, "16px");

        // Profile Link (from Profile)
        String profileLink = userProfile != null && userProfile.getLink() != null ? userProfile.getLink() : "";
        linkField = createFormField(translationService.translate("profile.portfolio"), profileLink);
        VerticalLayout linkGroup = createFormGroup(translationService.translate("profile.portfolio"), linkField);
        linkGroup.getStyle().set(StyleConstants.CSS_MARGIN_TOP, "16px");

        card.add(title, subtitle, formGrid, skillsGroup, toolsGroup, linkGroup);

        return card;
    }

    private Div createSecurityContent() {
        Div card = new Div();
        card.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        card.getStyle().set(StyleConstants.CSS_BORDER, BORDER_BOTTOM);
        card.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "24px");
        card.getStyle().set(StyleConstants.CSS_PADDING, "32px");
        card.getStyle().set(StyleConstants.CSS_WIDTH, "100%");
        card.getStyle().set(StyleConstants.CSS_BOX_SHADOW, StyleConstants.VAL_0_2_12PX);

        // Section title
        H3 title = new H3(translationService.translate("profile.securitySettings"));
        title.getStyle().set(StyleConstants.CSS_FONT_SIZE, "20px");
        title.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        title.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        title.getStyle().set(StyleConstants.CSS_MARGIN, "0 0 8px 0");

        Paragraph subtitle = new Paragraph(translationService.translate("profile.managePassword"));
        subtitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        subtitle.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        subtitle.getStyle().set(StyleConstants.CSS_MARGIN, "0 0 32px 0");

        // Change Password Section
        VerticalLayout passwordSection = new VerticalLayout();
        passwordSection.setPadding(false);
        passwordSection.setSpacing(false);
        passwordSection.getStyle().set("gap", "20px");
        passwordSection.getStyle().set(StyleConstants.CSS_MAX_WIDTH, "500px");

        // Current Password
        currentPasswordField = new com.vaadin.flow.component.textfield.PasswordField();
        currentPasswordField.setPlaceholder(translationService.translate("profile.currentPassword"));
        currentPasswordField.setWidthFull();
        currentPasswordField.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        currentPasswordField.getStyle().set("--vaadin-input-field-border-radius", "12px");
        passwordSection
                .add(createFormGroup(translationService.translate("profile.currentPassword"), currentPasswordField));

        // New Password
        newPasswordField = new com.vaadin.flow.component.textfield.PasswordField();
        newPasswordField.setPlaceholder(translationService.translate("profile.newPassword"));
        newPasswordField.setWidthFull();
        newPasswordField.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        newPasswordField.getStyle().set("--vaadin-input-field-border-radius", "12px");
        passwordSection.add(createFormGroup(translationService.translate("profile.newPassword"), newPasswordField));

        // Confirm Password
        confirmPasswordField = new com.vaadin.flow.component.textfield.PasswordField();
        confirmPasswordField.setPlaceholder(translationService.translate("profile.confirmPassword"));
        confirmPasswordField.setWidthFull();
        confirmPasswordField.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        confirmPasswordField.getStyle().set("--vaadin-input-field-border-radius", "12px");
        passwordSection
                .add(createFormGroup(translationService.translate("profile.confirmPassword"), confirmPasswordField));

        // Password requirements info
        Paragraph requirements = new Paragraph(translationService.translate("profile.passwordRequirements"));
        requirements.getStyle().set(StyleConstants.CSS_FONT_SIZE, "12px");
        requirements.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        requirements.getStyle().set(StyleConstants.CSS_MARGIN, "8px 0 0 0");
        passwordSection.add(requirements);

        // Change Password Button
        Button changePasswordBtn = createPrimaryButton(translationService.translate("profile.changePassword"),
                this::changePassword);
        changePasswordBtn.getStyle().set(StyleConstants.CSS_MARGIN_TOP, "16px");
        passwordSection.add(changePasswordBtn);

        // Divider
        Div divider = new Div();
        divider.getStyle().set(StyleConstants.CSS_WIDTH, "100%");
        divider.getStyle().set(StyleConstants.CSS_HEIGHT, "1px");
        divider.getStyle().set(StyleConstants.CSS_BACKGROUND, "rgba(0, 0, 0, 0.05)");
        divider.getStyle().set(StyleConstants.CSS_MARGIN, "32px 0");

        // Account Security Section
        H3 securityTitle = new H3(getTranslation("profile.securityTitle"));
        securityTitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "18px");
        securityTitle.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        securityTitle.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        securityTitle.getStyle().set(StyleConstants.CSS_MARGIN, StyleConstants.VAL_0_0_16PX);

        VerticalLayout securityOptions = new VerticalLayout();
        securityOptions.setPadding(false);
        securityOptions.setSpacing(false);
        securityOptions.getStyle().set("gap", "16px");

        // Two-Factor Authentication (placeholder)
        HorizontalLayout twoFactorRow = createSecurityOption(translationService.translate("profile.twoFactor"),
                translationService.translate("profile.addSecurity"), translationService.translate("profile.enable"),
                () -> Notification.show(translationService.translate("profile.twoFactorComingSoon"), 3000,
                        Notification.Position.TOP_CENTER));
        securityOptions.add(twoFactorRow);

        // Login Notifications (placeholder)
        HorizontalLayout loginNotifRow = createSecurityOption(
                translationService.translate("profile.loginNotifications"),
                translationService.translate("profile.getNotified"), translationService.translate("profile.enable"),
                () -> Notification.show(translationService.translate("profile.loginNotifComingSoon"), 3000,
                        Notification.Position.TOP_CENTER));
        securityOptions.add(loginNotifRow);

        card.add(title, subtitle, passwordSection, divider, securityTitle, securityOptions);

        return card;
    }

    private HorizontalLayout createSecurityOption(String title, String description, String buttonText,
            Runnable action) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.getStyle().set(StyleConstants.CSS_PADDING, "16px");
        row.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_GRAY);
        row.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "12px");

        VerticalLayout textGroup = new VerticalLayout();
        textGroup.setPadding(false);
        textGroup.setSpacing(false);
        textGroup.getStyle().set("gap", "4px");

        Span titleSpan = new Span(title);
        titleSpan.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "600");
        titleSpan.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        titleSpan.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);

        Span descSpan = new Span(description);
        descSpan.getStyle().set(StyleConstants.CSS_FONT_SIZE, "13px");
        descSpan.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);

        textGroup.add(titleSpan, descSpan);

        Button actionBtn = new Button(buttonText, e -> action.run());
        actionBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, StyleConstants.VAL_TRANSPARENT);
        actionBtn.getStyle().set(StyleConstants.CSS_COLOR, PRIMARY);
        actionBtn.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "600");
        actionBtn.getStyle().set(StyleConstants.CSS_BORDER, "1px solid " + PRIMARY);
        actionBtn.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX);
        actionBtn.getStyle().set(StyleConstants.CSS_PADDING, "8px 16px");
        actionBtn.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);

        row.add(textGroup, actionBtn);
        row.expand(textGroup);

        return row;
    }

    private void changePassword() {
        if (currentUser == null) {
            Notification.show(translationService.translate("profile.notLoggedIn"), 3000,
                    Notification.Position.TOP_CENTER);
            return;
        }

        String currentPwd = currentPasswordField.getValue();
        String newPwd = newPasswordField.getValue();
        String confirmPwd = confirmPasswordField.getValue();

        // Validation
        if (currentPwd == null || currentPwd.isEmpty()) {
            Notification.show(translationService.translate("profile.enterCurrentPassword"), 3000,
                    Notification.Position.TOP_CENTER);
            return;
        }

        if (newPwd == null || newPwd.isEmpty()) {
            Notification.show(translationService.translate("profile.enterNewPassword"), 3000,
                    Notification.Position.TOP_CENTER);
            return;
        }

        if (!newPwd.equals(confirmPwd)) {
            Notification.show(translationService.translate("profile.passwordsNotMatch"), 3000,
                    Notification.Position.TOP_CENTER);
            return;
        }

        // Check password requirements
        if (newPwd.length() < 10 || !UPPERCASE_PATTERN.matcher(newPwd).find() || !LOWERCASE_PATTERN.matcher(newPwd).find()
                || !DIGIT_PATTERN.matcher(newPwd).find() || !SPECIAL_CHAR_PATTERN.matcher(newPwd).find()) {
            Notification.show(translationService.translate("profile.passwordRequirementsError"), 5000,
                    Notification.Position.TOP_CENTER);
            return;
        }

        // Use AuthenticationService to change password
        boolean success = authService.changePassword(currentPwd, newPwd);
        if (success) {
            Notification.show(translationService.translate("profile.passwordChanged"), 3000,
                    Notification.Position.BOTTOM_END);
            // Clear fields
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
        } else {
            Notification.show(translationService.translate("profile.incorrectPassword"), 3000,
                    Notification.Position.TOP_CENTER);
        }
    }

    private VerticalLayout createFormGroup(String label, com.vaadin.flow.component.Component field) {
        VerticalLayout group = new VerticalLayout();
        group.setPadding(false);
        group.setSpacing(false);
        group.getStyle().set("gap", "8px");
        group.setWidthFull();

        Span labelSpan = new Span(label);
        labelSpan.getStyle().set(StyleConstants.CSS_FONT_SIZE, "12px");
        labelSpan.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        labelSpan.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        labelSpan.getStyle().set(StyleConstants.CSS_TEXT_TRANSFORM, "uppercase");
        labelSpan.getStyle().set(StyleConstants.CSS_LETTER_SPACING, LETTER_SPACING);

        group.add(labelSpan, field);
        return group;
    }

    private TextField createFormField(String label, String value) {
        TextField field = new TextField();
        field.setValue(value != null ? value : "");
        field.setWidthFull();
        field.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        field.getStyle().set("--vaadin-input-field-border-radius", "12px");
        return field;
    }

    private EmailField createEmailField(String label, String value) {
        EmailField field = new EmailField();
        field.setValue(value != null ? value : "");
        field.setWidthFull();
        field.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        field.getStyle().set("--vaadin-input-field-border-radius", "12px");
        return field;
    }

    private TextArea createTextArea(String label, String value, String placeholder) {
        TextArea area = new TextArea();
        area.setPlaceholder(placeholder);
        area.setValue(value != null ? value : "");
        area.setWidthFull();
        area.setMinHeight("80px");
        area.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        area.getStyle().set("--vaadin-input-field-border-radius", "12px");
        return area;
    }

    private void toggleEditMode() {
        editMode = !editMode;
        setFieldsEditable(editMode);

        if (editMode) {
            editBtn.setText(translationService.translate("action.cancel"));
            editBtn.setIcon(VaadinIcon.CLOSE.create());
            saveBtn.setVisible(true);
        } else {
            editBtn.setText(translationService.translate("profile.editProfile"));
            editBtn.setIcon(VaadinIcon.PENCIL.create());
            saveBtn.setVisible(false);
            // Reload original values
            reloadFieldValues();
        }
    }

    private void setFieldsEditable(boolean editable) {
        firstNameField.setReadOnly(!editable);
        lastNameField.setReadOnly(!editable);
        // usernameField always read-only
        emailField.setReadOnly(!editable);
        // pinField always read-only
        experienceField.setReadOnly(!editable);
        skillsArea.setReadOnly(!editable);
        toolsArea.setReadOnly(!editable);
        linkField.setReadOnly(!editable);
    }

    private void reloadFieldValues() {
        if (currentUser != null) {
            firstNameField.setValue(currentUser.getFirstName() != null ? currentUser.getFirstName() : "");
            lastNameField.setValue(currentUser.getLastName() != null ? currentUser.getLastName() : "");
            emailField.setValue(currentUser.getIdentityEmail() != null ? currentUser.getIdentityEmail() : "");
        }
        if (userProfile != null) {
            experienceField.setValue(userProfile.getExperienceLevel() != null ? userProfile.getExperienceLevel() : "");
            skillsArea.setValue(userProfile.getSkills() != null ? userProfile.getSkills() : "");
            toolsArea.setValue(userProfile.getTools() != null ? userProfile.getTools() : "");
            linkField.setValue(userProfile.getLink() != null ? userProfile.getLink() : "");
        }
    }

    private void saveProfile() {
        if (currentUser == null) {
            Notification.show(translationService.translate("profile.notLoggedIn"), 3000,
                    Notification.Position.TOP_CENTER);
            return;
        }

        // Validate email
        String email = emailField.getValue();
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            Notification.show(translationService.translate("profile.enterValidEmail"), 3000,
                    Notification.Position.TOP_CENTER);
            return;
        }

        // Update profile using ProfileService
        boolean success = profileService.updateProfile(currentUser.getPin(), firstNameField.getValue(),
                lastNameField.getValue(), experienceField.getValue(), toolsArea.getValue(), skillsArea.getValue(),
                linkField.getValue(), email, translationService.getCurrentLocale());

        if (success) {
            Notification.show(translationService.translate("profile.profileSaved"), 3000,
                    Notification.Position.BOTTOM_END);

            // Update user in session
            User updatedUser = profileService.getUpdatedUser(currentUser.getPin());
            if (updatedUser != null) {
                authService.setCurrentUser(updatedUser);
                this.currentUser = updatedUser;
            }

            // Reload profile data using the active locale
            this.userProfile = profileService.getProfile(currentUser.getPin(), translationService.getCurrentLocale());

            // Reload all field values in UI to reflect database state
            reloadFieldValues();

            // Exit edit mode (also reloads values but we do it twice to be safe)
            toggleEditMode();
        } else {
            Notification.show(translationService.translate("profile.saveFailed"), 3000,
                    Notification.Position.TOP_CENTER);
        }
    }

    private Button createPrimaryButton(String text, Runnable action) {
        Button btn = new Button(text, e -> action.run());
        btn.getStyle().set(StyleConstants.CSS_BACKGROUND, "linear-gradient(135deg, " + PRIMARY + " 0%, #5AC8FA 100%)");
        btn.getStyle().set(StyleConstants.CSS_COLOR, StyleConstants.VAL_WHITE);
        btn.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "600");
        btn.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        btn.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX);
        btn.getStyle().set(StyleConstants.CSS_BORDER, "none");
        btn.getStyle().set(StyleConstants.CSS_PADDING, "10px 24px");
        btn.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 10px 15px -3px rgba(0, 122, 255, 0.3)");
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

        return btn;
    }
}
