package com.clbooster.app.views;

import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.clbooster.app.i18n.TranslationService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("signup")
@PageTitle("Create Account | CL Booster")
@AnonymousAllowed
public class SignUpView extends VerticalLayout {

    // Figma Design System Colors
    private static final String PRIMARY = "#007AFF";
    private static final String TEXT_PRIMARY = "#1d1d1f";
    private static final String TEXT_SECONDARY = "#86868b";
    private static final String BG_GRAY = "#f5f5f7";
    private static final String BG_WHITE = "#ffffff";
    private static final String SUCCESS = "#34C759";
    private static final String ERROR = "#FF3B30";
    private static final String WEAK = "#FF3B30";
    private static final String MEDIUM = "#FF9500";
    private static final String GRAY = "rgba(0, 0, 0, 0.1)";

    private final AuthenticationService authService;
    private final TranslationService translationService;
    private TextField firstNameField;
    private TextField lastNameField;
    private TextField usernameField;
    private EmailField emailField;
    private PasswordField passwordField;
    private Div[] strengthBars;
    private Span strengthText;

    public SignUpView() {
        this.authService = new AuthenticationService();
        this.translationService = new TranslationService();
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background", BG_GRAY);
        getStyle().set("font-family",
                "-apple-system, BlinkMacSystemFont, 'SF Pro Text', 'SF Pro Display', system-ui, sans-serif");

        // Main card container
        Div card = new Div();
        card.getStyle().set("background", BG_WHITE);
        card.getStyle().set("border-radius", "24px");
        card.getStyle().set("padding", "48px");
        card.getStyle().set("width", "100%");
        card.getStyle().set("max-width", "420px");
        card.getStyle().set("box-shadow", "0 2px 12px rgba(0, 0, 0, 0.04)");
        card.getStyle().set("border", "1px solid rgba(0, 0, 0, 0.05)");
        card.getStyle().set("transition", "all 0.5s ease");

        // Back link
        HorizontalLayout backLink = new HorizontalLayout();
        backLink.setAlignItems(FlexComponent.Alignment.CENTER);
        backLink.getStyle().set("gap", "6px");
        backLink.getStyle().set("cursor", "pointer");
        backLink.getStyle().set("margin-bottom", "24px");
        backLink.getStyle().set("width", "fit-content");
        backLink.getStyle().set("transition", "opacity 0.2s");

        Icon arrowLeft = VaadinIcon.ARROW_LEFT.create();
        arrowLeft.getStyle().set("width", "14px");
        arrowLeft.getStyle().set("height", "14px");
        arrowLeft.getStyle().set("color", TEXT_SECONDARY);

        Span backText = new Span(translationService.translate("landing.backToHome"));
        backText.getStyle().set("font-size", "13px");
        backText.getStyle().set("font-weight", "500");
        backText.getStyle().set("color", TEXT_SECONDARY);

        backLink.add(arrowLeft, backText);
        backLink.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("")));
        backLink.getElement().addEventListener("mouseenter", e -> backLink.getStyle().set("opacity", "0.7"));
        backLink.getElement().addEventListener("mouseleave", e -> backLink.getStyle().set("opacity", "1"));

        // Logo icon
        Div logoIcon = new Div();
        logoIcon.getStyle().set("width", "48px");
        logoIcon.getStyle().set("height", "48px");
        logoIcon.getStyle().set("background", PRIMARY);
        logoIcon.getStyle().set("border-radius", "12px");
        logoIcon.getStyle().set("display", "flex");
        logoIcon.getStyle().set("align-items", "center");
        logoIcon.getStyle().set("justify-content", "center");
        logoIcon.getStyle().set("margin-bottom", "24px");
        logoIcon.getStyle().set("box-shadow", "0 10px 15px -3px rgba(0, 122, 255, 0.3)");
        logoIcon.add(VaadinIcon.SPARK_LINE.create());

        // Title section
        H2 title = new H2(translationService.translate("signup.createAccount"));
        title.getStyle().set("font-size", "28px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0 0 8px 0");
        title.getStyle().set("letter-spacing", "-0.025em");

        Paragraph subtitle = new Paragraph(translationService.translate("signup.joinThousands"));
        subtitle.getStyle().set("font-size", "15px");
        subtitle.getStyle().set("color", TEXT_SECONDARY);
        subtitle.getStyle().set("margin", "0 0 32px 0");

        // Form fields
        VerticalLayout form = new VerticalLayout();
        form.setPadding(false);
        form.setSpacing(false);
        form.getStyle().set("gap", "20px");
        form.setWidthFull();

        // Name fields (First and Last)
        HorizontalLayout nameRow = new HorizontalLayout();
        nameRow.setWidthFull();
        nameRow.getStyle().set("gap", "12px");

        firstNameField = new TextField(translationService.translate("label.firstName"));
        firstNameField.setPlaceholder("Alex");
        firstNameField.setWidthFull();
        firstNameField.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        firstNameField.getStyle().set("--vaadin-input-field-border-radius", "12px");

        lastNameField = new TextField(translationService.translate("label.lastName"));
        lastNameField.setPlaceholder("Riviera");
        lastNameField.setWidthFull();
        lastNameField.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        lastNameField.getStyle().set("--vaadin-input-field-border-radius", "12px");

        nameRow.add(firstNameField, lastNameField);
        nameRow.expand(firstNameField, lastNameField);

        // Username field
        usernameField = new TextField(translationService.translate("label.username"));
        usernameField.setPlaceholder("alexriviera");
        usernameField.setWidthFull();
        usernameField.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        usernameField.getStyle().set("--vaadin-input-field-border-radius", "12px");

        // Email field
        emailField = new EmailField(translationService.translate("label.email"));
        emailField.setPlaceholder("alex@example.com");
        emailField.setWidthFull();
        emailField.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        emailField.getStyle().set("--vaadin-input-field-border-radius", "12px");

        // Password field
        passwordField = new PasswordField(translationService.translate("label.password"));
        passwordField.setWidthFull();
        passwordField.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        passwordField.getStyle().set("--vaadin-input-field-border-radius", "12px");

        // Password strength indicator
        VerticalLayout strengthIndicator = new VerticalLayout();
        strengthIndicator.setPadding(false);
        strengthIndicator.setSpacing(false);
        strengthIndicator.getStyle().set("gap", "8px");
        strengthIndicator.getStyle().set("margin-top", "-12px");

        HorizontalLayout strengthBarsLayout = new HorizontalLayout();
        strengthBarsLayout.getStyle().set("gap", "4px");
        strengthBarsLayout.setWidthFull();

        strengthBars = new Div[4];
        for (int i = 0; i < 4; i++) {
            Div bar = new Div();
            bar.getStyle().set("flex", "1");
            bar.getStyle().set("height", "4px");
            bar.getStyle().set("background", GRAY);
            bar.getStyle().set("border-radius", "2px");
            bar.getStyle().set("transition", "background 0.3s");
            strengthBars[i] = bar;
            strengthBarsLayout.add(bar);
        }

        strengthText = new Span(translationService.translate("signup.passwordStrength"));
        strengthText.getStyle().set("font-size", "12px");
        strengthText.getStyle().set("color", TEXT_SECONDARY);
        strengthText.getStyle().set("font-weight", "500");

        strengthIndicator.add(strengthBarsLayout, strengthText);

        // Add real-time password strength listener
        passwordField.addValueChangeListener(e -> updatePasswordStrength(e.getValue()));

        // Terms checkbox
        HorizontalLayout termsRow = new HorizontalLayout();
        termsRow.setAlignItems(FlexComponent.Alignment.START);
        termsRow.getStyle().set("gap", "12px");
        termsRow.getStyle().set("margin", "8px 0");

        Div checkIcon = new Div();
        checkIcon.getStyle().set("width", "20px");
        checkIcon.getStyle().set("height", "20px");
        checkIcon.getStyle().set("background", SUCCESS);
        checkIcon.getStyle().set("border-radius", "6px");
        checkIcon.getStyle().set("display", "flex");
        checkIcon.getStyle().set("align-items", "center");
        checkIcon.getStyle().set("justify-content", "center");
        checkIcon.getStyle().set("flex-shrink", "0");
        checkIcon.getStyle().set("margin-top", "2px");

        Icon check = VaadinIcon.CHECK.create();
        check.getStyle().set("width", "12px");
        check.getStyle().set("height", "12px");
        check.getStyle().set("color", "white");
        checkIcon.add(check);

        // Terms text - two lines
        VerticalLayout termsTextLayout = new VerticalLayout();
        termsTextLayout.setPadding(false);
        termsTextLayout.setSpacing(false);
        termsTextLayout.getStyle().set("gap", "2px");

        // Line 1: Terms of Service
        HorizontalLayout termsLine1 = new HorizontalLayout();
        termsLine1.setAlignItems(FlexComponent.Alignment.CENTER);
        termsLine1.getStyle().set("gap", "4px");
        termsLine1.getStyle().set("flex-wrap", "nowrap");

        Span agreeText = new Span(translationService.translate("signup.bySigningUp"));
        agreeText.getStyle().set("font-size", "13px");
        agreeText.getStyle().set("color", TEXT_SECONDARY);

        Span termsLink = new Span(translationService.translate("signup.termsOfService"));
        termsLink.getStyle().set("font-size", "13px");
        termsLink.getStyle().set("font-weight", "500");
        termsLink.getStyle().set("color", PRIMARY);
        termsLink.getStyle().set("cursor", "pointer");
        termsLink.getStyle().set("text-decoration", "underline");
        termsLink.addClickListener(e -> showTermsOfServiceDialog());

        termsLine1.add(agreeText, termsLink);

        // Line 2: Privacy Policy
        HorizontalLayout termsLine2 = new HorizontalLayout();
        termsLine2.setAlignItems(FlexComponent.Alignment.CENTER);
        termsLine2.getStyle().set("gap", "4px");
        termsLine2.getStyle().set("flex-wrap", "nowrap");

        Span andText = new Span(translationService.translate("signup.and"));
        andText.getStyle().set("font-size", "13px");
        andText.getStyle().set("color", TEXT_SECONDARY);

        Span privacyLink = new Span(translationService.translate("signup.privacyPolicy"));
        privacyLink.getStyle().set("font-size", "13px");
        privacyLink.getStyle().set("font-weight", "500");
        privacyLink.getStyle().set("color", PRIMARY);
        privacyLink.getStyle().set("cursor", "pointer");
        privacyLink.getStyle().set("text-decoration", "underline");
        privacyLink.addClickListener(e -> showPrivacyPolicyDialog());

        termsLine2.add(andText, privacyLink);

        termsTextLayout.add(termsLine1, termsLine2);
        termsRow.add(checkIcon, termsTextLayout);

        // Create Account button
        Button createBtn = createPrimaryButton(translationService.translate("signup.createAccount"),
                this::handleRegistration);
        createBtn.setWidthFull();
        createBtn.getStyle().set("margin-top", "8px");

        // Divider
        HorizontalLayout divider = new HorizontalLayout();
        divider.setWidthFull();
        divider.setAlignItems(FlexComponent.Alignment.CENTER);
        divider.getStyle().set("gap", "16px");
        divider.getStyle().set("margin", "24px 0");

        Div line1 = new Div();
        line1.getStyle().set("flex", "1");
        line1.getStyle().set("height", "1px");
        line1.getStyle().set("background", "rgba(0, 0, 0, 0.1)");

        Span orText = new Span(translationService.translate("login.orContinueWith"));
        orText.getStyle().set("font-size", "13px");
        orText.getStyle().set("color", TEXT_SECONDARY);
        orText.getStyle().set("white-space", "nowrap");

        Div line2 = new Div();
        line2.getStyle().set("flex", "1");
        line2.getStyle().set("height", "1px");
        line2.getStyle().set("background", "rgba(0, 0, 0, 0.1)");

        divider.add(line1, orText, line2);

        // Social buttons
        HorizontalLayout socialButtons = new HorizontalLayout();
        socialButtons.setWidthFull();
        socialButtons.getStyle().set("gap", "12px");

        Button googleBtn = createSocialButton("Google");
        Button linkedinBtn = createSocialButton("LinkedIn");

        socialButtons.add(googleBtn, linkedinBtn);
        socialButtons.expand(googleBtn, linkedinBtn);

        // Login link
        HorizontalLayout loginRow = new HorizontalLayout();
        loginRow.setWidthFull();
        loginRow.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        loginRow.getStyle().set("margin-top", "24px");
        loginRow.getStyle().set("gap", "4px");

        Span haveAccount = new Span(translationService.translate("signup.haveAccount"));
        haveAccount.getStyle().set("font-size", "14px");
        haveAccount.getStyle().set("color", TEXT_SECONDARY);

        Anchor loginLink = new Anchor("login", translationService.translate("landing.logIn"));
        loginLink.getStyle().set("font-size", "14px");
        loginLink.getStyle().set("font-weight", "600");
        loginLink.getStyle().set("color", PRIMARY);
        loginLink.getStyle().set("text-decoration", "none");
        loginLink.getStyle().set("transition", "opacity 0.2s");
        loginLink.getElement().addEventListener("mouseenter", e -> loginLink.getStyle().set("opacity", "0.7"));
        loginLink.getElement().addEventListener("mouseleave", e -> loginLink.getStyle().set("opacity", "1"));

        loginRow.add(haveAccount, loginLink);

        form.add(nameRow, usernameField, emailField, passwordField, strengthIndicator, termsRow, createBtn);
        card.add(backLink, logoIcon, title, subtitle, form, divider, socialButtons, loginRow);

        // Hover effect for card
        card.getElement().addEventListener("mouseenter", e -> {
            card.getStyle().set("box-shadow", "0 24px 48px rgba(0, 0, 0, 0.06)");
            card.getStyle().set("transform", "translateY(-2px)");
        });
        card.getElement().addEventListener("mouseleave", e -> {
            card.getStyle().set("box-shadow", "0 2px 12px rgba(0, 0, 0, 0.04)");
            card.getStyle().set("transform", "translateY(0)");
        });

        add(card);
    }

    private Button createPrimaryButton(String text, Runnable action) {
        Button btn = new Button(text, e -> action.run());
        btn.getStyle().set("background", "linear-gradient(135deg, " + PRIMARY + " 0%, #5AC8FA 100%)");
        btn.getStyle().set("color", "white");
        btn.getStyle().set("font-weight", "600");
        btn.getStyle().set("font-size", "15px");
        btn.getStyle().set("border-radius", "9999px");
        btn.getStyle().set("border", "none");
        btn.getStyle().set("padding", "14px 24px");
        btn.getStyle().set("box-shadow", "0 10px 15px -3px rgba(0, 122, 255, 0.3)");
        btn.getStyle().set("transition", "all 0.2s ease");
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

    private Button createSocialButton(String provider) {
        Button btn = new Button(provider);
        btn.getStyle().set("background", BG_GRAY);
        btn.getStyle().set("color", TEXT_PRIMARY);
        btn.getStyle().set("font-weight", "600");
        btn.getStyle().set("font-size", "14px");
        btn.getStyle().set("border-radius", "12px");
        btn.getStyle().set("border", "1px solid rgba(0, 0, 0, 0.05)");
        btn.getStyle().set("padding", "12px 24px");
        btn.getStyle().set("transition", "all 0.2s ease");
        btn.getStyle().set("cursor", "pointer");

        btn.getElement().addEventListener("mouseenter", e -> {
            btn.getStyle().set("background", "rgba(0, 0, 0, 0.08)");
            btn.getStyle().set("border-color", "rgba(0, 0, 0, 0.1)");
        });
        btn.getElement().addEventListener("mouseleave", e -> {
            btn.getStyle().set("background", BG_GRAY);
            btn.getStyle().set("border-color", "rgba(0, 0, 0, 0.05)");
        });

        return btn;
    }

    private void handleRegistration() {
        String firstName = firstNameField.getValue();
        String lastName = lastNameField.getValue();
        String username = usernameField.getValue();
        String email = emailField.getValue();
        String password = passwordField.getValue();

        // Validation
        if (firstName == null || firstName.trim().isEmpty()) {
            showError("Please enter your first name");
            return;
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            showError("Please enter your last name");
            return;
        }
        if (username == null || username.trim().isEmpty()) {
            showError("Please enter a username");
            return;
        }
        if (email == null || email.trim().isEmpty()) {
            showError("Please enter your email address");
            return;
        }
        if (password == null || password.trim().isEmpty()) {
            showError("Please enter a password");
            return;
        }

        // Attempt registration
        boolean success = authService.register(email, username, password, firstName, lastName);
        if (success) {
            // Log the new user in immediately so the session is populated
            authService.login(username, password);
            showSuccess("Account created successfully! Welcome, " + firstName + "!");
            // Navigate to dashboard as the newly created user
            getUI().ifPresent(ui -> ui.navigate(DashboardView.class));
        } else {
            showError("Registration failed. Username or email may already be registered.");
        }
    }

    private void showSuccess(String message) {
        Notification notification = new Notification(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.open();
    }

    private void showError(String message) {
        Notification notification = new Notification(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.open();
    }

    private void updatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            // Reset to gray
            for (int i = 0; i < 4; i++) {
                strengthBars[i].getStyle().set("background", GRAY);
            }
            strengthText.setText("Password strength: Enter password");
            strengthText.getStyle().set("color", TEXT_SECONDARY);
            return;
        }

        int strength = calculatePasswordStrength(password);

        // Update bars based on strength
        String barColor;
        String strengthLabel;
        String textColor;

        if (strength <= 1) {
            barColor = WEAK;
            strengthLabel = "Weak";
            textColor = WEAK;
        } else if (strength == 2) {
            barColor = MEDIUM;
            strengthLabel = "Fair";
            textColor = MEDIUM;
        } else if (strength == 3) {
            barColor = SUCCESS;
            strengthLabel = "Good";
            textColor = SUCCESS;
        } else {
            barColor = SUCCESS;
            strengthLabel = "Strong";
            textColor = SUCCESS;
        }

        // Fill bars
        for (int i = 0; i < 4; i++) {
            if (i < strength) {
                strengthBars[i].getStyle().set("background", barColor);
            } else {
                strengthBars[i].getStyle().set("background", GRAY);
            }
        }

        strengthText.setText("Password strength: " + strengthLabel);
        strengthText.getStyle().set("color", textColor);
    }

    /**
     * Scores the password strength on a 0-4 scale. The minimum length threshold
     * (10) is sourced from {@link AuthenticationService#getPasswordRequirements()}
     * to keep validation logic in one place.
     */
    private int calculatePasswordStrength(String password) {
        int strength = 0;

        // Length check — aligned with AuthenticationService requirement (10 chars)
        if (password.length() >= 10)
            strength++;
        if (password.length() >= 14)
            strength++;

        // Character variety checks
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

        int varietyCount = 0;
        if (hasUpper)
            varietyCount++;
        if (hasLower)
            varietyCount++;
        if (hasNumber)
            varietyCount++;
        if (hasSpecial)
            varietyCount++;

        if (varietyCount >= 2)
            strength++;
        if (varietyCount >= 3)
            strength++;

        // Cap at 4
        return Math.min(strength, 4);
    }

    private void showTermsOfServiceDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(translationService.translate("terms.title"));
        dialog.setWidth("500px");
        dialog.setHeight("600px");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(true);

        H3 title = new H3(translationService.translate("terms.title"));
        title.getStyle().set("margin-top", "0");

        Paragraph intro = new Paragraph(translationService.translate("terms.intro"));

        H4 section1 = new H4(translationService.translate("terms.section1.title"));
        Paragraph text1 = new Paragraph(translationService.translate("terms.section1.text"));

        H4 section2 = new H4(translationService.translate("terms.section2.title"));
        Paragraph text2 = new Paragraph(translationService.translate("terms.section2.text"));
        UnorderedList list2 = new UnorderedList(new ListItem(translationService.translate("terms.section2.list.item1")),
                new ListItem(translationService.translate("terms.section2.list.item2")),
                new ListItem(translationService.translate("terms.section2.list.item3")),
                new ListItem(translationService.translate("terms.section2.list.item4")));

        H4 section3 = new H4(translationService.translate("terms.section3.title"));
        Paragraph text3 = new Paragraph(translationService.translate("terms.section3.text"));

        H4 section4 = new H4(translationService.translate("terms.section4.title"));
        Paragraph text4 = new Paragraph(translationService.translate("terms.section4.text"));

        H4 section5 = new H4(translationService.translate("terms.section5.title"));
        Paragraph text5 = new Paragraph(translationService.translate("terms.section5.text"));

        content.add(title, intro, section1, text1, section2, text2, list2, section3, text3, section4, text4, section5,
                text5);
        content.getStyle().set("overflow", "auto");
        content.setHeight("400px");

        Button closeButton = new Button(translationService.translate("landing.iUnderstand"), e -> dialog.close());
        closeButton.getStyle().set("background", PRIMARY);
        closeButton.getStyle().set("color", "white");

        dialog.add(content);
        dialog.getFooter().add(closeButton);
        dialog.open();
    }

    private void showPrivacyPolicyDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(translationService.translate("privacy.title"));
        dialog.setWidth("500px");
        dialog.setHeight("600px");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(true);

        H3 title = new H3(translationService.translate("privacy.title"));
        title.getStyle().set("margin-top", "0");

        Paragraph intro = new Paragraph(translationService.translate("privacy.intro"));

        H4 section1 = new H4(translationService.translate("privacy.section1.title"));
        Paragraph text1 = new Paragraph(translationService.translate("privacy.section1.text"));
        UnorderedList list1 = new UnorderedList(
                new ListItem(translationService.translate("privacy.section1.list.item1")),
                new ListItem(translationService.translate("privacy.section1.list.item2")),
                new ListItem(translationService.translate("privacy.section1.list.item3")),
                new ListItem(translationService.translate("privacy.section1.list.item4")));

        H4 section2 = new H4(translationService.translate("privacy.section2.title"));
        Paragraph text2 = new Paragraph(translationService.translate("privacy.section2.text"));
        UnorderedList list2 = new UnorderedList(
                new ListItem(translationService.translate("privacy.section2.list.item1")),
                new ListItem(translationService.translate("privacy.section2.list.item2")),
                new ListItem(translationService.translate("privacy.section2.list.item3")),
                new ListItem(translationService.translate("privacy.section2.list.item4")),
                new ListItem(translationService.translate("privacy.section2.list.item5")));

        H4 section3 = new H4(translationService.translate("privacy.section3.title"));
        Paragraph text3 = new Paragraph(translationService.translate("privacy.section3.text"));

        H4 section4 = new H4(translationService.translate("privacy.section4.title"));
        Paragraph text4 = new Paragraph(translationService.translate("privacy.section4.text"));

        H4 section5 = new H4(translationService.translate("privacy.section5.title"));
        Paragraph text5 = new Paragraph(translationService.translate("privacy.section5.text"));

        H4 section6 = new H4(translationService.translate("privacy.section6.title"));
        Paragraph text6 = new Paragraph(translationService.translate("privacy.section6.text"));

        H4 section7 = new H4(translationService.translate("privacy.section7.title"));
        Paragraph text7 = new Paragraph(translationService.translate("privacy.section7.text"));

        content.add(title, intro, section1, text1, list1, section2, text2, list2, section3, text3, section4, text4,
                section5, text5, section6, text6, section7, text7);
        content.getStyle().set("overflow", "auto");
        content.setHeight("400px");

        Button closeButton = new Button(translationService.translate("landing.iUnderstand"), e -> dialog.close());
        closeButton.getStyle().set("background", PRIMARY);
        closeButton.getStyle().set("color", "white");

        dialog.add(content);
        dialog.getFooter().add(closeButton);
        dialog.open();
    }
}
