package com.clbooster.app.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
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

    public SignUpView() {
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
        backLink.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(LandingView.class)));
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

        // Full Name field
        TextField fullName = new TextField("Full Name");
        fullName.setPlaceholder("Alex Riviera");
        fullName.setWidthFull();
        fullName.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        fullName.getStyle().set("--vaadin-input-field-border-radius", "12px");

        // Email field
        EmailField email = new EmailField("Email Address");
        email.setPlaceholder("alex@example.com");
        email.setWidthFull();
        email.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        email.getStyle().set("--vaadin-input-field-border-radius", "12px");

        // Password field
        PasswordField password = new PasswordField("Password");
        password.setWidthFull();
        password.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        password.getStyle().set("--vaadin-input-field-border-radius", "12px");

        // Password strength indicator
        VerticalLayout strengthIndicator = new VerticalLayout();
        strengthIndicator.setPadding(false);
        strengthIndicator.setSpacing(false);
        strengthIndicator.getStyle().set("gap", "8px");
        strengthIndicator.getStyle().set("margin-top", "-12px");

        HorizontalLayout strengthBars = new HorizontalLayout();
        strengthBars.getStyle().set("gap", "4px");
        strengthBars.setWidthFull();

        for (int i = 0; i < 4; i++) {
            Div bar = new Div();
            bar.getStyle().set("flex", "1");
            bar.getStyle().set("height", "4px");
            bar.getStyle().set("background", i < 2 ? SUCCESS : "rgba(0, 0, 0, 0.1)");
            bar.getStyle().set("border-radius", "2px");
            bar.getStyle().set("transition", "background 0.3s");
            strengthBars.add(bar);
        }

        Span strengthText = new Span("Password strength: Good");
        strengthText.getStyle().set("font-size", "12px");
        strengthText.getStyle().set("color", SUCCESS);
        strengthText.getStyle().set("font-weight", "500");

        strengthIndicator.add(strengthBars, strengthText);

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

        Paragraph termsText = new Paragraph("By signing up, you agree to our ");
        termsText.getStyle().set("font-size", "13px");
        termsText.getStyle().set("color", TEXT_SECONDARY);
        termsText.getStyle().set("margin", "0");

        Anchor termsLink = new Anchor("#", "Terms of Service");
        termsLink.getStyle().set("font-size", "13px");
        termsLink.getStyle().set("font-weight", "500");
        termsLink.getStyle().set("color", PRIMARY);
        termsLink.getStyle().set("text-decoration", "none");

        Span andText = new Span(" and ");
        andText.getStyle().set("font-size", "13px");
        andText.getStyle().set("color", TEXT_SECONDARY);

        Anchor privacyLink = new Anchor("#", "Privacy Policy");
        privacyLink.getStyle().set("font-size", "13px");
        privacyLink.getStyle().set("font-weight", "500");
        privacyLink.getStyle().set("color", PRIMARY);
        privacyLink.getStyle().set("text-decoration", "none");

        termsRow.add(checkIcon, termsText, termsLink, andText, privacyLink);

        // Create Account button
        Button createBtn = createPrimaryButton("Create Account", () -> getUI().ifPresent(ui -> ui.navigate(DashboardView.class)));
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
        Button appleBtn = createSocialButton("Apple");

        socialButtons.add(googleBtn, appleBtn);
        socialButtons.expand(googleBtn, appleBtn);

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

        form.add(fullName, email, password, strengthIndicator, termsRow, createBtn);
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
}
