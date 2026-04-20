package com.clbooster.app.views;

import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.clbooster.app.i18n.TranslationService;
import com.clbooster.app.views.util.AuthComponents;
import com.clbooster.app.views.util.StyleConstants;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import java.util.regex.Pattern;

@Route("signup")
@PageTitle("Create Account | CL Booster")
@AnonymousAllowed
public class SignUpView extends VerticalLayout {
    private static final String FONT_SIZE_PROP = StyleConstants.CSS_FONT_SIZE;
    private static final String FONT_WEIGHT_PROP = StyleConstants.CSS_FONT_WEIGHT;
    private static final String COLOR_PROP = StyleConstants.CSS_COLOR;
    private static final String BACKGROUND_PROP = StyleConstants.CSS_BACKGROUND;
    private static final String HEIGHT_PROP = StyleConstants.CSS_HEIGHT;
    private static final String VAADIN_INPUT_FIELD_BACKGROUND_PROP = "--vaadin-input-field-background";
    private static final String GAP_PROP = "gap";
    private static final String MARGIN_PROP = StyleConstants.CSS_MARGIN;
    private static final String WIDTH_PROP = StyleConstants.CSS_WIDTH;
    private static final String MARGIN_TOP_PROP = StyleConstants.CSS_MARGIN_TOP;
    private static final String FLEX_PROP = "flex";
    private static final String BORDER_RADIUS_PROP = StyleConstants.CSS_BORDER_RADIUS;
    private static final String WHITE_VAL = StyleConstants.VAL_WHITE;
    private static final String TEXT_DECORATION_PROP = "text-decoration";
    private static final String VAADIN_INPUT_FIELD_BORDER_RADIUS_PROP = "--vaadin-input-field-border-radius";

    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]");

    private final transient AuthenticationService authService;
    private final transient TranslationService translationService;
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
        getStyle().set(BACKGROUND_PROP, StyleConstants.BG_GRAY);
        getStyle().set("font-family",
                "-apple-system, BlinkMacSystemFont, 'SF Pro Text', 'SF Pro Display', system-ui, sans-serif");

        // Main card container
        Div card = AuthComponents.createCard();

        // Back link
        HorizontalLayout backLink = AuthComponents.createBackLink(
            translationService.translate("landing.backToHome"), 
            () -> getUI().ifPresent(ui -> ui.navigate(""))
        );

        // Logo icon
        Div logoIcon = AuthComponents.createLogoIcon();

        // Title section
        H2 title = AuthComponents.createTitle(translationService.translate("signup.createAccount"));

        Paragraph subtitle = AuthComponents.createSubtitle(translationService.translate("signup.joinThousands"));

        // Form fields
        VerticalLayout form = new VerticalLayout();
        form.setPadding(false);
        form.setSpacing(false);
        form.getStyle().set(GAP_PROP, "20px");
        form.setWidthFull();

        // Name fields (First and Last)
        HorizontalLayout nameRow = new HorizontalLayout();
        nameRow.setWidthFull();
        nameRow.getStyle().set(GAP_PROP, "12px");

        firstNameField = new TextField(translationService.translate("label.firstName"));
        firstNameField.setPlaceholder("Alex");
        firstNameField.setWidthFull();
        firstNameField.getStyle().set(VAADIN_INPUT_FIELD_BACKGROUND_PROP, StyleConstants.BG_GRAY);
        firstNameField.getStyle().set(VAADIN_INPUT_FIELD_BORDER_RADIUS_PROP, "12px");

        lastNameField = new TextField(translationService.translate("label.lastName"));
        lastNameField.setPlaceholder("Riviera");
        lastNameField.setWidthFull();
        lastNameField.getStyle().set(VAADIN_INPUT_FIELD_BACKGROUND_PROP, StyleConstants.BG_GRAY);
        lastNameField.getStyle().set(VAADIN_INPUT_FIELD_BORDER_RADIUS_PROP, "12px");

        nameRow.add(firstNameField, lastNameField);
        nameRow.expand(firstNameField, lastNameField);

        // Username field
        usernameField = new TextField(translationService.translate("label.username"));
        usernameField.setPlaceholder("alexriviera");
        usernameField.setWidthFull();
        usernameField.getStyle().set(VAADIN_INPUT_FIELD_BACKGROUND_PROP, StyleConstants.BG_GRAY);
        usernameField.getStyle().set(VAADIN_INPUT_FIELD_BORDER_RADIUS_PROP, "12px");

        // Email field
        emailField = new EmailField(translationService.translate("label.email"));
        emailField.setPlaceholder("alex@example.com");
        emailField.setWidthFull();
        emailField.getStyle().set(VAADIN_INPUT_FIELD_BACKGROUND_PROP, StyleConstants.BG_GRAY);
        emailField.getStyle().set(VAADIN_INPUT_FIELD_BORDER_RADIUS_PROP, "12px");

        // Password field
        passwordField = new PasswordField(translationService.translate("label.password"));
        passwordField.setWidthFull();
        passwordField.getStyle().set(VAADIN_INPUT_FIELD_BACKGROUND_PROP, StyleConstants.BG_GRAY);
        passwordField.getStyle().set(VAADIN_INPUT_FIELD_BORDER_RADIUS_PROP, "12px");

        // Password strength indicator
        VerticalLayout strengthIndicator = new VerticalLayout();
        strengthIndicator.setPadding(false);
        strengthIndicator.setSpacing(false);
        strengthIndicator.getStyle().set(GAP_PROP, "8px");
        strengthIndicator.getStyle().set(MARGIN_TOP_PROP, "-12px");

        HorizontalLayout strengthBarsLayout = new HorizontalLayout();
        strengthBarsLayout.getStyle().set(GAP_PROP, "4px");
        strengthBarsLayout.setWidthFull();

        strengthBars = new Div[4];
        for (int i = 0; i < 4; i++) {
            Div bar = new Div();
            bar.getStyle().set(FLEX_PROP, "1");
            bar.getStyle().set(HEIGHT_PROP, "4px");
            bar.getStyle().set(BACKGROUND_PROP, StyleConstants.GRAY);
            bar.getStyle().set(BORDER_RADIUS_PROP, "2px");
            bar.getStyle().set(StyleConstants.CSS_TRANSITION, "background 0.3s");
            strengthBars[i] = bar;
            strengthBarsLayout.add(bar);
        }

        strengthText = new Span(translationService.translate("signup.passwordStrength"));
        strengthText.getStyle().set(FONT_SIZE_PROP, "12px");
        strengthText.getStyle().set(COLOR_PROP, StyleConstants.TEXT_SECONDARY);
        strengthText.getStyle().set(FONT_WEIGHT_PROP, "500");

        strengthIndicator.add(strengthBarsLayout, strengthText);

        // Add real-time password strength listener
        passwordField.addValueChangeListener(e -> updatePasswordStrength(e.getValue()));

        // Terms checkbox
        HorizontalLayout termsRow = new HorizontalLayout();
        termsRow.setAlignItems(FlexComponent.Alignment.START);
        termsRow.getStyle().set(GAP_PROP, "12px");
        termsRow.getStyle().set(MARGIN_PROP, "8px 0");

        Div checkIcon = new Div();
        checkIcon.getStyle().set(WIDTH_PROP, "20px");
        checkIcon.getStyle().set(HEIGHT_PROP, "20px");
        checkIcon.getStyle().set(BACKGROUND_PROP, StyleConstants.SUCCESS);
        checkIcon.getStyle().set(BORDER_RADIUS_PROP, "6px");
        checkIcon.getStyle().set(StyleConstants.CSS_DISPLAY, FLEX_PROP);
        checkIcon.getStyle().set(StyleConstants.CSS_ALIGN_ITEMS, StyleConstants.VAL_CENTER);
        checkIcon.getStyle().set(StyleConstants.CSS_JUSTIFY_CONTENT, StyleConstants.VAL_CENTER);
        checkIcon.getStyle().set("flex-shrink", "0");
        checkIcon.getStyle().set(MARGIN_TOP_PROP, "2px");

        Icon check = VaadinIcon.CHECK.create();
        check.getStyle().set(WIDTH_PROP, "12px");
        check.getStyle().set(HEIGHT_PROP, "12px");
        check.getStyle().set(COLOR_PROP, WHITE_VAL);
        checkIcon.add(check);

        // Terms text - two lines
        VerticalLayout termsTextLayout = new VerticalLayout();
        termsTextLayout.setPadding(false);
        termsTextLayout.setSpacing(false);
        termsTextLayout.getStyle().set(GAP_PROP, "2px");

        // Line 1: Terms of Service
        HorizontalLayout termsLine1 = new HorizontalLayout();
        termsLine1.setAlignItems(FlexComponent.Alignment.CENTER);
        termsLine1.getStyle().set(GAP_PROP, "4px");
        termsLine1.getStyle().set(StyleConstants.CSS_FLEX_WRAP, "nowrap");

        Span agreeText = new Span(translationService.translate("signup.bySigningUp"));
        agreeText.getStyle().set(FONT_SIZE_PROP, "13px");
        agreeText.getStyle().set(COLOR_PROP, StyleConstants.TEXT_SECONDARY);

        Span termsLink = new Span(translationService.translate("signup.termsOfService"));
        termsLink.getStyle().set(FONT_SIZE_PROP, "13px");
        termsLink.getStyle().set(FONT_WEIGHT_PROP, "500");
        termsLink.getStyle().set(COLOR_PROP, StyleConstants.PRIMARY);
        termsLink.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);
        termsLink.getStyle().set(TEXT_DECORATION_PROP, "underline");
        termsLink.addClickListener(e -> showTermsOfServiceDialog());

        termsLine1.add(agreeText, termsLink);

        // Line 2: Privacy Policy
        HorizontalLayout termsLine2 = new HorizontalLayout();
        termsLine2.setAlignItems(FlexComponent.Alignment.CENTER);
        termsLine2.getStyle().set(GAP_PROP, "4px");
        termsLine2.getStyle().set(StyleConstants.CSS_FLEX_WRAP, "nowrap");

        Span andText = new Span(translationService.translate("signup.and"));
        andText.getStyle().set(FONT_SIZE_PROP, "13px");
        andText.getStyle().set(COLOR_PROP, StyleConstants.TEXT_SECONDARY);

        Span privacyLink = new Span(translationService.translate("signup.privacyPolicy"));
        privacyLink.getStyle().set(FONT_SIZE_PROP, "13px");
        privacyLink.getStyle().set(FONT_WEIGHT_PROP, "500");
        privacyLink.getStyle().set(COLOR_PROP, StyleConstants.PRIMARY);
        privacyLink.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);
        privacyLink.getStyle().set(TEXT_DECORATION_PROP, "underline");
        privacyLink.addClickListener(e -> showPrivacyPolicyDialog());

        termsLine2.add(andText, privacyLink);

        termsTextLayout.add(termsLine1, termsLine2);
        termsRow.add(checkIcon, termsTextLayout);

        // Create Account button
        Button createBtn = AuthComponents.createPrimaryButton(translationService.translate("signup.createAccount"),
                this::handleRegistration);
        createBtn.setWidthFull();
        createBtn.getStyle().set(MARGIN_TOP_PROP, "8px");

        // Divider
        HorizontalLayout divider = AuthComponents.createDivider(translationService.translate("login.orContinueWith"));

        // Social buttons
        HorizontalLayout socialButtons = AuthComponents.createSocialButtonsLayout();

        // Login link
        HorizontalLayout loginRow = new HorizontalLayout();
        loginRow.setWidthFull();
        loginRow.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        loginRow.getStyle().set(MARGIN_TOP_PROP, "24px");
        loginRow.getStyle().set(GAP_PROP, "4px");

        Span haveAccount = new Span(translationService.translate("signup.haveAccount"));
        haveAccount.getStyle().set(FONT_SIZE_PROP, "14px");
        haveAccount.getStyle().set(COLOR_PROP, StyleConstants.TEXT_SECONDARY);

        Anchor loginLink = new Anchor("login", translationService.translate("landing.logIn"));
        loginLink.getStyle().set(FONT_SIZE_PROP, "14px");
        loginLink.getStyle().set(FONT_WEIGHT_PROP, "600");
        loginLink.getStyle().set(COLOR_PROP, StyleConstants.PRIMARY);
        loginLink.getStyle().set(TEXT_DECORATION_PROP, "none");
        AuthComponents.applyLinkHoverEffect(loginLink);

        loginRow.add(haveAccount, loginLink);

        form.add(nameRow, usernameField, emailField, passwordField, strengthIndicator, termsRow, createBtn);
        card.add(backLink, logoIcon, title, subtitle, form, divider, socialButtons, loginRow);

        // Hover effect for card
        card.getElement().addEventListener(StyleConstants.VAL_MOUSEENTER, e -> {
            card.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 24px 48px rgba(0, 0, 0, 0.06)");
            card.getStyle().set(StyleConstants.CSS_TRANSFORM, "translateY(-2px)");
        });
        card.getElement().addEventListener(StyleConstants.VAL_MOUSELEAVE, e -> {
            card.getStyle().set(StyleConstants.CSS_BOX_SHADOW, StyleConstants.VAL_0_2_12PX);
            card.getStyle().set(StyleConstants.CSS_TRANSFORM, "translateY(0)");
        });

        add(card);
    }

    

    

    private void handleRegistration() {
        String firstName = firstNameField.getValue();
        String lastName = lastNameField.getValue();
        String username = usernameField.getValue();
        String email = emailField.getValue();
        String password = passwordField.getValue();

        // Validation
        if (firstName == null || firstName.trim().isEmpty()) {
            AuthComponents.showError("Please enter your first name");
            return;
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            AuthComponents.showError("Please enter your last name");
            return;
        }
        if (username == null || username.trim().isEmpty()) {
            AuthComponents.showError("Please enter a username");
            return;
        }
        if (email == null || email.trim().isEmpty()) {
            AuthComponents.showError("Please enter your email address");
            return;
        }
        if (password == null || password.trim().isEmpty()) {
            AuthComponents.showError("Please enter a password");
            return;
        }

        // Attempt registration
        boolean success = authService.register(email, username, password, firstName, lastName);
        if (success) {
            // Log the new user in immediately so the session is populated
            authService.login(username, password);
            AuthComponents.showSuccess("Account created successfully! Welcome, " + firstName + "!");
            // Navigate to dashboard as the newly created user
            getUI().ifPresent(ui -> ui.navigate(DashboardView.class));
        } else {
            AuthComponents.showError("Registration failed. Username or email may already be registered.");
        }
    }

    

    

    private void updatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            // Reset to gray
            for (int i = 0; i < 4; i++) {
                strengthBars[i].getStyle().set(BACKGROUND_PROP, StyleConstants.GRAY);
            }
            strengthText.setText("Password strength: Enter password");
            strengthText.getStyle().set(COLOR_PROP, StyleConstants.TEXT_SECONDARY);
            return;
        }

        int strength = calculatePasswordStrength(password);

        // Update bars based on strength
        String barColor;
        String strengthLabel;
        String textColor;

        if (strength <= 1) {
            barColor = StyleConstants.WEAK;
            strengthLabel = "Weak";
            textColor = StyleConstants.WEAK;
        } else if (strength == 2) {
            barColor = StyleConstants.MEDIUM;
            strengthLabel = "Fair";
            textColor = StyleConstants.MEDIUM;
        } else if (strength == 3) {
            barColor = StyleConstants.SUCCESS;
            strengthLabel = "Good";
            textColor = StyleConstants.SUCCESS;
        } else {
            barColor = StyleConstants.SUCCESS;
            strengthLabel = "Strong";
            textColor = StyleConstants.SUCCESS;
        }

        // Fill bars
        for (int i = 0; i < 4; i++) {
            if (i < strength) {
                strengthBars[i].getStyle().set(BACKGROUND_PROP, barColor);
            } else {
                strengthBars[i].getStyle().set(BACKGROUND_PROP, StyleConstants.GRAY);
            }
        }

        strengthText.setText("Password strength: " + strengthLabel);
        strengthText.getStyle().set(COLOR_PROP, textColor);
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
        boolean hasUpper = UPPERCASE_PATTERN.matcher(password).find();
        boolean hasLower = LOWERCASE_PATTERN.matcher(password).find();
        boolean hasNumber = DIGIT_PATTERN.matcher(password).find();
        boolean hasSpecial = SPECIAL_CHAR_PATTERN.matcher(password).find();

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

        createInfoDialog("terms.title", "terms.intro", 
                section1, text1, section2, text2, list2, section3, text3, section4, text4, section5, text5).open();
    }

    private void showPrivacyPolicyDialog() {
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

        createInfoDialog("privacy.title", "privacy.intro", 
                section1, text1, list1, section2, text2, list2, section3, text3, section4, text4,
                section5, text5, section6, text6, section7, text7).open();
    }

    private Dialog createInfoDialog(String titleKey, String introKey, com.vaadin.flow.component.Component... sections) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(translationService.translate(titleKey));
        dialog.setWidth("500px");
        dialog.setHeight("600px");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(true);

        H3 title = new H3(translationService.translate(titleKey));
        title.getStyle().set(MARGIN_TOP_PROP, "0");

        Paragraph intro = new Paragraph(translationService.translate(introKey));

        content.add(title, intro);
        content.add(sections);
        
        content.getStyle().set(StyleConstants.CSS_OVERFLOW, "auto");
        content.setHeight("400px");

        Button closeButton = new Button(translationService.translate("landing.iUnderstand"), e -> dialog.close());
        closeButton.getStyle().set(BACKGROUND_PROP, StyleConstants.PRIMARY);
        closeButton.getStyle().set(COLOR_PROP, WHITE_VAL);

        dialog.add(content);
        dialog.getFooter().add(closeButton);
        
        return dialog;
    }
}
