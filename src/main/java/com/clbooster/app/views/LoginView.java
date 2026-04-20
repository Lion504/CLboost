package com.clbooster.app.views;

import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.clbooster.app.i18n.TranslationService;
import com.clbooster.app.views.util.AuthComponents;
import com.clbooster.app.views.util.StyleConstants;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@PageTitle("Login | CL Booster")
@AnonymousAllowed
public class LoginView extends VerticalLayout {
    private static final String FONT_SIZE_PROP = StyleConstants.CSS_FONT_SIZE;
    private static final String FONT_WEIGHT_PROP = StyleConstants.CSS_FONT_WEIGHT;
    private static final String COLOR_PROP = StyleConstants.CSS_COLOR;

        private final transient AuthenticationService authService;
    private final TranslationService translationService;
    private TextField usernameField;
    private PasswordField passwordField;

    public LoginView() {
        this.authService = new AuthenticationService();
        this.translationService = new TranslationService();
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set(StyleConstants.CSS_BACKGROUND, StyleConstants.BG_GRAY);
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
        H2 title = AuthComponents.createTitle(translationService.translate("login.title"));

        Paragraph subtitle = AuthComponents.createSubtitle(translationService.translate("login.subtitle"));

        // Form fields
        VerticalLayout form = new VerticalLayout();
        form.setPadding(false);
        form.setSpacing(false);
        form.getStyle().set("gap", "20px");
        form.setWidthFull();

        // Username field
        usernameField = AuthComponents.createTextField(translationService.translate("label.username"), "alexsmith");

        // Password field
        passwordField = AuthComponents.createPasswordField(translationService.translate("label.password"));

        // Remember me and Forgot password row
        HorizontalLayout rememberRow = new HorizontalLayout();
        rememberRow.setWidthFull();
        rememberRow.setAlignItems(FlexComponent.Alignment.CENTER);
        rememberRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        Checkbox remember = new Checkbox(translationService.translate("action.rememberMe"));
        remember.getStyle().set(FONT_SIZE_PROP, "14px");
        remember.getStyle().set(COLOR_PROP, StyleConstants.TEXT_PRIMARY);
        remember.addValueChangeListener(e -> {
            if (e.getValue()) {
                AuthComponents.showInfo("Remember me is not yet implemented");
                remember.setValue(false);
            }
        });

        Span forgot = new Span(translationService.translate("login.forgotPassword"));
        forgot.getStyle().set(FONT_SIZE_PROP, "14px");
        forgot.getStyle().set(FONT_WEIGHT_PROP, "500");
        forgot.getStyle().set(COLOR_PROP, StyleConstants.PRIMARY);
        forgot.getStyle().set("text-decoration", "none");
        forgot.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);
        AuthComponents.applyLinkHoverEffect(forgot);
        forgot.addClickListener(e -> {
            AuthComponents.showInfo("Please contact admin@clbooster.com for password reset assistance");
        });

        rememberRow.add(remember, forgot);

        // Sign In button
        Button signIn = AuthComponents.createPrimaryButton(translationService.translate("action.login"), this::handleLogin);
        signIn.setWidthFull();
        signIn.getStyle().set(StyleConstants.CSS_MARGIN_TOP, "8px");

        // Divider
        HorizontalLayout divider = AuthComponents.createDivider(translationService.translate("login.orContinueWith"));

        // Social buttons
        HorizontalLayout socialButtons = AuthComponents.createSocialButtonsLayout();

        // Sign up link
        HorizontalLayout signupRow = new HorizontalLayout();
        signupRow.setWidthFull();
        signupRow.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        signupRow.getStyle().set(StyleConstants.CSS_MARGIN_TOP, "24px");
        signupRow.getStyle().set("gap", "4px");

        Span noAccount = new Span(translationService.translate("login.noAccount"));
        noAccount.getStyle().set(FONT_SIZE_PROP, "14px");
        noAccount.getStyle().set(COLOR_PROP, StyleConstants.TEXT_SECONDARY);

        Anchor createLink = new Anchor("signup", translationService.translate("signup.signup"));
        createLink.getStyle().set(FONT_SIZE_PROP, "14px");
        createLink.getStyle().set(FONT_WEIGHT_PROP, "600");
        createLink.getStyle().set(COLOR_PROP, StyleConstants.PRIMARY);
        createLink.getStyle().set("text-decoration", "none");
        AuthComponents.applyLinkHoverEffect(createLink);

        signupRow.add(noAccount, createLink);

        form.add(usernameField, passwordField, rememberRow, signIn);
        card.add(backLink, logoIcon, title, subtitle, form, divider, socialButtons, signupRow);

        add(card);
    }

    private void handleLogin() {
        String username = usernameField.getValue();
        String password = passwordField.getValue();

        if (username == null || username.trim().isEmpty()) {
            AuthComponents.showError("Please enter your username");
            return;
        }
        if (password == null || password.trim().isEmpty()) {
            AuthComponents.showError("Please enter your password");
            return;
        }

        boolean success = authService.login(username, password);
        if (success) {
            getUI().ifPresent(ui -> ui.navigate(DashboardView.class));
        } else {
            AuthComponents.showError("Invalid username or password");
            passwordField.clear();
        }
    }

    

    
}
