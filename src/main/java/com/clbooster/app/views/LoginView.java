package com.clbooster.app.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login") // NO layout = MainLayout — this is a standalone page
@PageTitle("Login | CL Booster")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background", "var(--lumo-contrast-5pct)");

        Div card = new Div();
        card.getStyle().set("background", "white").set("border-radius", "16px").set("padding", "48px 40px")
                .set("width", "420px").set("box-shadow", "var(--lumo-box-shadow-s)");

        Anchor back = new Anchor("/", "← Back to home");
        back.getStyle().set("font-size", "13px").set("color", "#6b7280");

        H2 title = new H2("Welcome back.");
        title.getStyle().set("margin", "16px 0 4px 0");
        Paragraph sub = new Paragraph("Please enter your details to sign in.");
        sub.getStyle().set("color", "#6b7280").set("margin-top", "0");

        EmailField email = new EmailField("Email Address");
        email.setPlaceholder("alex@example.com");
        email.setWidthFull();

        PasswordField password = new PasswordField("Password");
        password.setWidthFull();

        Checkbox remember = new Checkbox("Remember me");
        Anchor forgot = new Anchor("#", "Forgot password?");
        forgot.getStyle().set("margin-left", "auto");
        HorizontalLayout rememberRow = new HorizontalLayout(remember, forgot);
        rememberRow.setWidthFull();
        rememberRow.setAlignItems(FlexComponent.Alignment.CENTER);

        Button signIn = new Button("Sign In", e -> getUI().ifPresent(ui -> ui.navigate(DashboardView.class)));
        signIn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        signIn.setWidthFull();

        Paragraph noAccount = new Paragraph("Don't have an account? ");
        Anchor createLink = new Anchor("signup", "Create an account");
        noAccount.add(createLink);
        noAccount.getStyle().set("text-align", "center").set("margin-top", "16px");

        card.add(back, title, sub, email, password, rememberRow, signIn, noAccount);
        add(card);
    }
}
