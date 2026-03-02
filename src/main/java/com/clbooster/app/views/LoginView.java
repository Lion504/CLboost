package com.clbooster.app.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@PageTitle("Login | CL Booster")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    // Figma Design System Colors
    private static final String PRIMARY = "#007AFF";
    private static final String TEXT_PRIMARY = "#1d1d1f";
    private static final String TEXT_SECONDARY = "#86868b";
    private static final String BG_GRAY = "#f5f5f7";
    private static final String BG_WHITE = "#ffffff";

    public LoginView() {
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
        H2 title = new H2("Welcome back.");
        title.getStyle().set("font-size", "28px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0 0 8px 0");
        title.getStyle().set("letter-spacing", "-0.025em");

        Paragraph subtitle = new Paragraph("Please enter your details to sign in.");
        subtitle.getStyle().set("font-size", "15px");
        subtitle.getStyle().set("color", TEXT_SECONDARY);
        subtitle.getStyle().set("margin", "0 0 32px 0");

        // Form fields
        VerticalLayout form = new VerticalLayout();
        form.setPadding(false);
        form.setSpacing(false);
        form.getStyle().set("gap", "20px");
        form.setWidthFull();

        // Email field
        EmailField email = createEmailField("Email Address", "alex@example.com");

        // Password field
        PasswordField password = createPasswordField("Password");

        // Remember me and Forgot password row
        HorizontalLayout rememberRow = new HorizontalLayout();
        rememberRow.setWidthFull();
        rememberRow.setAlignItems(FlexComponent.Alignment.CENTER);
        rememberRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        Checkbox remember = new Checkbox("Remember me");
        remember.getStyle().set("font-size", "14px");
        remember.getStyle().set("color", TEXT_PRIMARY);

        Anchor forgot = new Anchor("#", "Forgot password?");
        forgot.getStyle().set("font-size", "14px");
        forgot.getStyle().set("font-weight", "500");
        forgot.getStyle().set("color", PRIMARY);
        forgot.getStyle().set("text-decoration", "none");
        forgot.getStyle().set("transition", "opacity 0.2s");
        forgot.getElement().addEventListener("mouseenter", e -> forgot.getStyle().set("opacity", "0.7"));
        forgot.getElement().addEventListener("mouseleave", e -> forgot.getStyle().set("opacity", "1"));

        rememberRow.add(remember, forgot);

        // Sign In button
        Button signIn = createPrimaryButton("Sign In", () -> getUI().ifPresent(ui -> ui.navigate(DashboardView.class)));
        signIn.setWidthFull();
        signIn.getStyle().set("margin-top", "8px");

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

        // Sign up link
        HorizontalLayout signupRow = new HorizontalLayout();
        signupRow.setWidthFull();
        signupRow.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        signupRow.getStyle().set("margin-top", "24px");
        signupRow.getStyle().set("gap", "4px");

        Span noAccount = new Span("Don't have an account?");
        noAccount.getStyle().set("font-size", "14px");
        noAccount.getStyle().set("color", TEXT_SECONDARY);

        Anchor createLink = new Anchor("signup", "Sign up");
        createLink.getStyle().set("font-size", "14px");
        createLink.getStyle().set("font-weight", "600");
        createLink.getStyle().set("color", PRIMARY);
        createLink.getStyle().set("text-decoration", "none");
        createLink.getStyle().set("transition", "opacity 0.2s");
        createLink.getElement().addEventListener("mouseenter", e -> createLink.getStyle().set("opacity", "0.7"));
        createLink.getElement().addEventListener("mouseleave", e -> createLink.getStyle().set("opacity", "1"));

        signupRow.add(noAccount, createLink);

        form.add(email, password, rememberRow, signIn);
        card.add(backLink, logoIcon, title, subtitle, form, divider, socialButtons, signupRow);

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

    private EmailField createEmailField(String label, String placeholder) {
        EmailField field = new EmailField(label);
        field.setPlaceholder(placeholder);
        field.setWidthFull();
        field.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        field.getStyle().set("--vaadin-input-field-border-radius", "12px");
        return field;
    }

    private PasswordField createPasswordField(String label) {
        PasswordField field = new PasswordField(label);
        field.setWidthFull();
        field.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        field.getStyle().set("--vaadin-input-field-border-radius", "12px");
        return field;
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
