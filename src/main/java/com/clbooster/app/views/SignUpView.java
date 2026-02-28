package com.clbooster.app.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("signup")
@PageTitle("Create Account | CL Booster")
@AnonymousAllowed
public class SignUpView extends VerticalLayout {

    public SignUpView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background", "var(--lumo-contrast-5pct)");

        Div card = new Div();
        card.getStyle().set("background", "white").set("border-radius", "16px").set("padding", "48px 40px")
                .set("width", "420px").set("box-shadow", "var(--lumo-box-shadow-s)");

        Anchor back = new Anchor("/", "← Back to home");
        back.getStyle().set("font-size", "13px").set("color", "#6b7280");

        H2 title = new H2("Create account.");
        Paragraph sub = new Paragraph("Join thousands of job seekers today.");
        sub.getStyle().set("color", "#6b7280");

        TextField fullName = new TextField("Full Name");
        fullName.setPlaceholder("Alex Riviera");
        fullName.setWidthFull();

        EmailField email = new EmailField("Email Address");
        email.setPlaceholder("alex@example.com");
        email.setWidthFull();

        PasswordField password = new PasswordField("Password");
        password.setWidthFull();

        Paragraph terms = new Paragraph("By signing up, you agree to our ");
        terms.add(new Anchor("#", "Terms of Service"));
        terms.add(" and ");
        terms.add(new Anchor("#", "Privacy Policy"));
        terms.getStyle().set("font-size", "13px").set("color", "#6b7280");

        Button create = new Button("Create Account", e -> getUI().ifPresent(ui -> ui.navigate(DashboardView.class)));
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        create.setWidthFull();

        Paragraph login = new Paragraph("Already have an account? ");
        login.add(new Anchor("login", "Log in here"));
        login.getStyle().set("text-align", "center");

        card.add(back, title, sub, fullName, email, password, terms, create, login);
        add(card);
    }
}
