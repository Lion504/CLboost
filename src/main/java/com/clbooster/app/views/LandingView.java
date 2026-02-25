package com.clbooster.app.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("")               // public root — NO MainLayout
@PageTitle("CL Booster — AI Cover Letter Generator")
@AnonymousAllowed
public class LandingView extends VerticalLayout {

    public LandingView() {
        setPadding(false);
        setSpacing(false);
        setSizeFull();

        // === NAVBAR ===
        Span logo = new Span("CL Booster");
        logo.getStyle().set("font-weight","700").set("font-size","18px");

        Anchor howItWorks = new Anchor("#features", "How it works");
        Anchor faq        = new Anchor("faq",        "FAQ");
        Button loginBtn   = new Button("Log in",
            e -> getUI().ifPresent(ui -> ui.navigate(LoginView.class)));
        loginBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button signupBtn  = new Button("Sign up",
            e -> getUI().ifPresent(ui -> ui.navigate(SignUpView.class)));
        signupBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout navbar = new HorizontalLayout(
            logo, howItWorks, faq, loginBtn, signupBtn);
        navbar.setWidthFull();
        navbar.setAlignItems(FlexComponent.Alignment.CENTER);
        navbar.expand(logo);
        navbar.getStyle()
            .set("padding", "16px 48px")
            .set("border-bottom", "1px solid #e5e7eb")
            .set("background", "white");

        // === HERO ===
        Span newBadge = new Span("NEW  AI Version 4.0 is here");
        newBadge.getElement().getThemeList().add("badge primary small");

        H1 headline = new H1();
        headline.add("Elevate your ");
        Span highlight = new Span("job hunting");
        highlight.getStyle().set("color", "var(--lumo-primary-color)");
        headline.add(highlight);
        headline.add(" with AI.");
        headline.getStyle()
            .set("font-size", "clamp(2rem, 5vw, 3.5rem)")
            .set("line-height", "1.1")
            .set("margin", "0");

        Paragraph heroSub = new Paragraph(
            "Generate perfectly tailored cover letters in seconds by " +
            "analyzing your resume, the job ad, and company culture.");
        heroSub.getStyle().set("color","#6b7280").set("font-size","16px")
                          .set("max-width","480px");

        Button generateNow = new Button("Generate Now",
            e -> getUI().ifPresent(ui -> ui.navigate(SignUpView.class)));
        generateNow.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);

        Button seeSamples = new Button("See Samples");
        seeSamples.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_LARGE);

        HorizontalLayout ctaRow = new HorizontalLayout(generateNow, seeSamples);
        ctaRow.setSpacing(true);

        VerticalLayout heroText = new VerticalLayout(newBadge, headline, heroSub, ctaRow);
        heroText.setPadding(false);
        heroText.setSpacing(true);
        heroText.setWidth("50%");

        // Hero image placeholder
        Div heroImage = new Div();
        heroImage.getStyle()
            .set("width","50%")
            .set("min-height","380px")
            .set("background","#f1f5f9")
            .set("border-radius","16px");

        HorizontalLayout hero = new HorizontalLayout(heroText, heroImage);
        hero.setWidthFull();
        hero.setAlignItems(FlexComponent.Alignment.CENTER);
        hero.getStyle().set("padding","80px 80px 60px 80px");

        // === FEATURES ===
        H2 featTitle = new H2("Built for performance.");
        featTitle.getStyle().set("text-align","center");
        Paragraph featSub = new Paragraph("Stop wasting hours on repetitive writing.");
        featSub.getStyle().set("text-align","center").set("color","#6b7280");

        HorizontalLayout featCards = new HorizontalLayout(
            featureCard("Instant Generation",
                "Analyze complex job ads and extract key keywords automatically."),
            featureCard("Company Intel",
                "We research the company's mission to align your voice with their values."),
            featureCard("ATS Optimized",
                "Strategically place keywords to ensure you pass through modern filters.")
        );
        featCards.setWidthFull();
        featCards.setSpacing(true);
        featCards.getStyle().set("padding", "0 80px");

        VerticalLayout features = new VerticalLayout(featTitle, featSub, featCards);
        features.setAlignItems(FlexComponent.Alignment.CENTER);
        features.setWidthFull();
        features.getStyle()
            .set("background", "var(--lumo-contrast-5pct)")
            .set("padding", "60px 80px");

        add(navbar, hero, features);
    }

    private Div featureCard(String title, String description) {
        Div card = new Div();
        card.addClassName("cl-card");
        card.getStyle().set("flex", "1").set("min-width", "200px");

        H3 h = new H3(title);
        h.getStyle().set("margin-top", "8px");
        Paragraph p = new Paragraph(description);
        p.getStyle().set("color", "#6b7280").set("font-size", "14px");

        card.add(h, p);
        return card;
    }
}

