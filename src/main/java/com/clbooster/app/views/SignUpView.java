package com.clbooster.app.views;

import com.clbooster.app.backend.service.authentication.AuthenticationService;
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
    private TextField firstNameField;
    private TextField lastNameField;
    private TextField usernameField;
    private EmailField emailField;
    private PasswordField passwordField;
    private Div[] strengthBars;
    private Span strengthText;

    public SignUpView() {
        this.authService = new AuthenticationService();
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background", BG_GRAY);
        getStyle().set("font-family", "-apple-system, BlinkMacSystemFont, 'SF Pro Text', 'SF Pro Display', system-ui, sans-serif");

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

        Span backText = new Span("Back to home");
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
        H2 title = new H2("Create account.");
        title.getStyle().set("font-size", "28px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0 0 8px 0");
        title.getStyle().set("letter-spacing", "-0.025em");

        Paragraph subtitle = new Paragraph("Join thousands of job seekers today.");
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

        firstNameField = new TextField("First Name");
        firstNameField.setPlaceholder("Alex");
        firstNameField.setWidthFull();
        firstNameField.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        firstNameField.getStyle().set("--vaadin-input-field-border-radius", "12px");

        lastNameField = new TextField("Last Name");
        lastNameField.setPlaceholder("Riviera");
        lastNameField.setWidthFull();
        lastNameField.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        lastNameField.getStyle().set("--vaadin-input-field-border-radius", "12px");

        nameRow.add(firstNameField, lastNameField);
        nameRow.expand(firstNameField, lastNameField);

        // Username field
        usernameField = new TextField("Username");
        usernameField.setPlaceholder("alexriviera");
        usernameField.setWidthFull();
        usernameField.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        usernameField.getStyle().set("--vaadin-input-field-border-radius", "12px");

        // Email field
        emailField = new EmailField("Email Address");
        emailField.setPlaceholder("alex@example.com");
        emailField.setWidthFull();
        emailField.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        emailField.getStyle().set("--vaadin-input-field-border-radius", "12px");

        // Password field
        passwordField = new PasswordField("Password");
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

        strengthText = new Span("Password strength: Enter password");
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

        Span agreeText = new Span("By signing up, you agree to our");
        agreeText.getStyle().set("font-size", "13px");
        agreeText.getStyle().set("color", TEXT_SECONDARY);

        Span termsLink = new Span("Terms of Service");
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

        Span andText = new Span("and");
        andText.getStyle().set("font-size", "13px");
        andText.getStyle().set("color", TEXT_SECONDARY);

        Span privacyLink = new Span("Privacy Policy");
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
        Button createBtn = createPrimaryButton("Create Account", this::handleRegistration);
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

        Span orText = new Span("or continue with");
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

        Span haveAccount = new Span("Already have an account?");
        haveAccount.getStyle().set("font-size", "14px");
        haveAccount.getStyle().set("color", TEXT_SECONDARY);

        Anchor loginLink = new Anchor("login", "Log in");
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
            showSuccess("Account created successfully! Welcome, " + firstName + "!");
            // Navigate to dashboard after successful registration
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

    private int calculatePasswordStrength(String password) {
        int strength = 0;

        // Length check
        if (password.length() >= 8) strength++;
        if (password.length() >= 12) strength++;

        // Character variety checks
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

        int varietyCount = 0;
        if (hasUpper) varietyCount++;
        if (hasLower) varietyCount++;
        if (hasNumber) varietyCount++;
        if (hasSpecial) varietyCount++;

        if (varietyCount >= 2) strength++;
        if (varietyCount >= 3) strength++;

        // Cap at 4
        return Math.min(strength, 4);
    }

    private void showTermsOfServiceDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Terms of Service");
        dialog.setWidth("500px");
        dialog.setHeight("600px");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(true);

        H3 title = new H3("CL Booster Terms of Service");
        title.getStyle().set("margin-top", "0");

        Paragraph intro = new Paragraph("Welcome to CL Booster! By using our service, you agree to these terms. Please read them carefully.");

        H4 section1 = new H4("1. Acceptance of Terms");
        Paragraph text1 = new Paragraph("By accessing or using CL Booster, you agree to be bound by these Terms of Service and all applicable laws and regulations. If you do not agree with any of these terms, you are prohibited from using or accessing this service.");

        H4 section2 = new H4("2. Use License");
        Paragraph text2 = new Paragraph("Permission is granted to temporarily use CL Booster for personal, non-commercial transitory viewing only. This is the grant of a license, not a transfer of title. Under this license you may not:");
        UnorderedList list2 = new UnorderedList(
            new ListItem("Modify or copy the materials"),
            new ListItem("Use the materials for any commercial purpose"),
            new ListItem("Attempt to decompile or reverse engineer any software"),
            new ListItem("Remove any copyright or proprietary notations")
        );

        H4 section3 = new H4("3. User Accounts");
        Paragraph text3 = new Paragraph("You are responsible for maintaining the confidentiality of your account and password. You agree to accept responsibility for all activities that occur under your account or password.");

        H4 section4 = new H4("4. Disclaimer");
        Paragraph text4 = new Paragraph("The materials on CL Booster are provided on an 'as is' basis. CL Booster makes no warranties, expressed or implied, and hereby disclaims and negates all other warranties including, without limitation, implied warranties or conditions of merchantability, fitness for a particular purpose, or non-infringement of intellectual property.");

        H4 section5 = new H4("5. Limitations");
        Paragraph text5 = new Paragraph("In no event shall CL Booster or its suppliers be liable for any damages arising out of the use or inability to use the materials on CL Booster.");

        content.add(title, intro, section1, text1, section2, text2, list2, section3, text3, section4, text4, section5, text5);
        content.getStyle().set("overflow", "auto");
        content.setHeight("400px");

        Button closeButton = new Button("I Understand", e -> dialog.close());
        closeButton.getStyle().set("background", PRIMARY);
        closeButton.getStyle().set("color", "white");

        dialog.add(content);
        dialog.getFooter().add(closeButton);
        dialog.open();
    }

    private void showPrivacyPolicyDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Privacy Policy");
        dialog.setWidth("500px");
        dialog.setHeight("600px");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(true);

        H3 title = new H3("CL Booster Privacy Policy");
        title.getStyle().set("margin-top", "0");

        Paragraph intro = new Paragraph("Your privacy is important to us. This Privacy Policy explains how CL Booster collects, uses, and protects your personal information.");

        H4 section1 = new H4("1. Information We Collect");
        Paragraph text1 = new Paragraph("We collect information you provide directly to us, including:");
        UnorderedList list1 = new UnorderedList(
            new ListItem("Personal information (name, email address, username)"),
            new ListItem("Resume and job application data"),
            new ListItem("Usage data and analytics"),
            new ListItem("Device and browser information")
        );

        H4 section2 = new H4("2. How We Use Your Information");
        Paragraph text2 = new Paragraph("We use the information we collect to:");
        UnorderedList list2 = new UnorderedList(
            new ListItem("Provide and maintain our services"),
            new ListItem("Generate personalized cover letters"),
            new ListItem("Improve and optimize our platform"),
            new ListItem("Communicate with you about updates and features"),
            new ListItem("Protect against fraud and abuse")
        );

        H4 section3 = new H4("3. Data Security");
        Paragraph text3 = new Paragraph("We implement appropriate technical and organizational measures to protect your personal data against unauthorized access, alteration, disclosure, or destruction. All passwords are securely hashed using industry-standard encryption.");

        H4 section4 = new H4("4. Data Retention");
        Paragraph text4 = new Paragraph("We retain your personal information for as long as your account is active or as needed to provide you services. You may request deletion of your account and associated data at any time by contacting us.");

        H4 section5 = new H4("5. Third-Party Services");
        Paragraph text5 = new Paragraph("We may use third-party services for hosting, analytics, and AI processing. These services have their own privacy policies and we encourage you to read them.");

        H4 section6 = new H4("6. Your Rights");
        Paragraph text6 = new Paragraph("You have the right to access, correct, or delete your personal information. You may also object to or restrict certain processing of your data. Contact us at privacy@clbooster.com for any privacy-related requests.");

        H4 section7 = new H4("7. Changes to This Policy");
        Paragraph text7 = new Paragraph("We may update this Privacy Policy from time to time. We will notify you of any changes by posting the new policy on this page and updating the effective date.");

        content.add(title, intro, section1, text1, list1, section2, text2, list2, section3, text3, section4, text4, section5, text5, section6, text6, section7, text7);
        content.getStyle().set("overflow", "auto");
        content.setHeight("400px");

        Button closeButton = new Button("I Understand", e -> dialog.close());
        closeButton.getStyle().set("background", PRIMARY);
        closeButton.getStyle().set("color", "white");

        dialog.add(content);
        dialog.getFooter().add(closeButton);
        dialog.open();
    }
}
