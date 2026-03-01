package com.clbooster.app.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;

import java.util.List;

@Route("")
@PageTitle("CL Booster — AI Cover Letter Generator")
@AnonymousAllowed
public class LandingView extends VerticalLayout {

    // Figma Design System Colors
    private static final String PRIMARY = "#007AFF";
    private static final String PRIMARY_LIGHT = "#5AC8FA";
    private static final String TEXT_PRIMARY = "#1d1d1f";
    private static final String TEXT_SECONDARY = "#86868b";
    private static final String BG_WHITE = "#ffffff";
    private static final String BG_GRAY = "#f5f5f7";
    private static final String BG_LIGHT = "#fbfbfb";

    private Dialog activeModal = null;

    public LandingView() {
        setPadding(false);
        setSpacing(false);
        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("background", BG_WHITE);
        getStyle().set("font-family", "-apple-system, BlinkMacSystemFont, 'SF Pro Text', 'SF Pro Display', system-ui, sans-serif");

        // Main Container
        VerticalLayout page = new VerticalLayout();
        page.setPadding(false);
        page.setSpacing(false);
        page.setWidth("100%");
        page.setMaxWidth("1280px");
        page.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        // Build sections
        page.add(createNavbar());
        page.add(createHero());
        page.add(createFeatures());
        page.add(createFooter());

        add(page);
    }

    // ============ NAVBAR ============
    private HorizontalLayout createNavbar() {
        // Logo with sparkles
        HorizontalLayout logo = new HorizontalLayout();
        logo.setAlignItems(FlexComponent.Alignment.CENTER);
        logo.setSpacing(false);
        logo.getStyle().set("gap", "8px");
        logo.getStyle().set("cursor", "pointer");
        
        Div logoIcon = new Div();
        logoIcon.getStyle().set("width", "32px");
        logoIcon.getStyle().set("height", "32px");
        logoIcon.getStyle().set("background", PRIMARY);
        logoIcon.getStyle().set("border-radius", "8px");
        logoIcon.getStyle().set("display", "flex");
        logoIcon.getStyle().set("align-items", "center");
        logoIcon.getStyle().set("justify-content", "center");
        logoIcon.getStyle().set("color", "white");
        logoIcon.add(VaadinIcon.SPARK_LINE.create());
        
        Span logoText = new Span("CL Booster");
        logoText.getStyle().set("font-weight", "700");
        logoText.getStyle().set("font-size", "20px");
        logoText.getStyle().set("letter-spacing", "-0.025em");
        logoText.getStyle().set("color", TEXT_PRIMARY);

        logo.add(logoIcon, logoText);
        logo.addClickListener(e -> scrollToTop());

        // Navigation links (hidden on mobile)
        HorizontalLayout navLinks = new HorizontalLayout();
        navLinks.setVisible(true); // Will be hidden on mobile via CSS
        navLinks.setSpacing(false);
        navLinks.getStyle().set("gap", "32px");

        Button howItWorks = createNavButton("How it works", () -> scrollToSection("features"));
        Button faq = createNavButton("FAQ", () -> openFaqModal());

        navLinks.add(howItWorks, faq);

        // Auth buttons
        Button loginBtn = new Button("Log in", e -> getUI().ifPresent(ui -> ui.navigate(LoginView.class)));
        loginBtn.getStyle().set("font-size", "13px");
        loginBtn.getStyle().set("font-weight", "700");
        loginBtn.getStyle().set("color", TEXT_PRIMARY);
        loginBtn.getStyle().set("background", "transparent");
        loginBtn.getStyle().set("border-radius", "9999px");
        loginBtn.getStyle().set("padding", "8px 16px");
        loginBtn.getStyle().set("transition", "background 0.2s");

        Button signupBtn = createPrimaryButton("Sign up", () -> getUI().ifPresent(ui -> ui.navigate(SignUpView.class)));
        signupBtn.getStyle().set("font-size", "13px");
        signupBtn.getStyle().set("padding", "8px 20px");

        HorizontalLayout authButtons = new HorizontalLayout(loginBtn, signupBtn);
        authButtons.setSpacing(false);
        authButtons.getStyle().set("gap", "12px");
        authButtons.setAlignItems(FlexComponent.Alignment.CENTER);

        // Full navbar
        HorizontalLayout navbar = new HorizontalLayout(logo, navLinks, authButtons);
        navbar.setWidthFull();
        navbar.setAlignItems(FlexComponent.Alignment.CENTER);
        navbar.setPadding(true);
        navbar.getStyle().set("padding", "16px 48px");
        navbar.getStyle().set("background", BG_WHITE + "cc"); // 80% opacity
        navbar.getStyle().set("backdrop-filter", "blur(12px)");
        navbar.getStyle().set("position", "sticky");
        navbar.getStyle().set("top", "0");
        navbar.getStyle().set("z-index", "40");
        navbar.getStyle().set("border-bottom", "1px solid rgba(0,0,0,0.05)");
        navbar.expand(navLinks);

        return navbar;
    }

    private Button createNavButton(String text, Runnable action) {
        Button btn = new Button(text, e -> action.run());
        btn.getStyle().set("font-size", "13px");
        btn.getStyle().set("font-weight", "600");
        btn.getStyle().set("color", TEXT_SECONDARY);
        btn.getStyle().set("background", "transparent");
        btn.getStyle().set("padding", "8px 0");
        btn.getStyle().set("transition", "color 0.2s");
        btn.getElement().addEventListener("mouseenter", e -> btn.getStyle().set("color", TEXT_PRIMARY));
        btn.getElement().addEventListener("mouseleave", e -> btn.getStyle().set("color", TEXT_SECONDARY));
        return btn;
    }

    // ============ HERO SECTION ============
    private HorizontalLayout createHero() {
        // Left side - Text content
        VerticalLayout heroText = new VerticalLayout();
        heroText.setPadding(false);
        heroText.setSpacing(false);
        heroText.getStyle().set("gap", "32px");
        heroText.setWidth("50%");
        heroText.setDefaultHorizontalComponentAlignment(Alignment.START);

        // NEW Badge
        HorizontalLayout badge = new HorizontalLayout();
        badge.setAlignItems(FlexComponent.Alignment.CENTER);
        badge.setSpacing(false);
        badge.getStyle().set("gap", "8px");
        badge.getStyle().set("padding", "6px 16px");
        badge.getStyle().set("background", BG_GRAY);
        badge.getStyle().set("border", "1px solid rgba(0,0,0,0.05)");
        badge.getStyle().set("border-radius", "9999px");
        badge.getStyle().set("display", "inline-flex");
        badge.getStyle().set("width", "fit-content");

        Span newTag = new Span("NEW");
        newTag.getStyle().set("background", PRIMARY);
        newTag.getStyle().set("color", "white");
        newTag.getStyle().set("font-size", "11px");
        newTag.getStyle().set("font-weight", "900");
        newTag.getStyle().set("padding", "2px 6px");
        newTag.getStyle().set("border-radius", "4px");

        Span versionText = new Span("AI Version 4.0 is here");
        versionText.getStyle().set("font-size", "13px");
        versionText.getStyle().set("font-weight", "700");
        versionText.getStyle().set("color", TEXT_SECONDARY);

        badge.add(newTag, versionText);

        // Headline with gradient
        H1 headline = new H1();
        headline.setText("Elevate your job hunting with AI.");
        headline.getStyle().set("font-size", "clamp(40px, 5vw, 72px)");
        headline.getStyle().set("font-weight", "700");
        headline.getStyle().set("letter-spacing", "-0.025em");
        headline.getStyle().set("line-height", "1.1");
        headline.getStyle().set("color", TEXT_PRIMARY);
        headline.getStyle().set("margin", "0");
        headline.getStyle().set("max-width", "500px");

        // Description
        Paragraph description = new Paragraph(
            "Generate perfectly tailored cover letters in seconds by analyzing your resume, the job ad, and company culture."
        );
        description.getStyle().set("font-size", "20px");
        description.getStyle().set("color", TEXT_SECONDARY);
        description.getStyle().set("line-height", "1.6");
        description.getStyle().set("max-width", "520px");
        description.getStyle().set("margin", "0");

        // CTA Buttons
        HorizontalLayout ctaRow = new HorizontalLayout();
        ctaRow.setSpacing(false);
        ctaRow.getStyle().set("gap", "16px");
        ctaRow.getStyle().set("padding-top", "16px");

        Button generateBtn = createPrimaryButton("Generate Now", () -> getUI().ifPresent(ui -> ui.navigate(SignUpView.class)));
        generateBtn.getStyle().set("font-size", "16px");
        generateBtn.getStyle().set("padding", "16px 40px");

        Button samplesBtn = createSecondaryButton("See Samples", this::openSamplesModal);
        samplesBtn.getStyle().set("font-size", "16px");
        samplesBtn.getStyle().set("padding", "16px 40px");

        ctaRow.add(generateBtn, samplesBtn);

        heroText.add(badge, headline, description, ctaRow);
        heroText.getStyle().set("padding", "80px 0");

        // Right side - Hero Image
        Div imageContainer = new Div();
        imageContainer.setWidth("50%");
        imageContainer.getStyle().set("position", "relative");
        imageContainer.getStyle().set("display", "flex");
        imageContainer.getStyle().set("justify-content", "center");
        imageContainer.getStyle().set("align-items", "center");

        // Glow effect behind image
        Div glow = new Div();
        glow.getStyle().set("position", "absolute");
        glow.getStyle().set("inset", "-40px");
        glow.getStyle().set("background", "rgba(0,122,255,0.1)");
        glow.getStyle().set("filter", "blur(100px)");
        glow.getStyle().set("border-radius", "50%");
        glow.getStyle().set("z-index", "-1");
        glow.getStyle().set("animation", "pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite");

        Image heroImage = new Image("images/hero.jpg", "Hero");
        heroImage.getStyle().set("width", "100%");
        heroImage.getStyle().set("max-width", "560px");
        heroImage.getStyle().set("border-radius", "48px");
        heroImage.getStyle().set("box-shadow", "0 25px 50px -12px rgba(0,0,0,0.25)");
        heroImage.getStyle().set("border", "1px solid rgba(0,0,0,0.05)");
        heroImage.getStyle().set("transform", "rotate(2deg)");
        heroImage.getStyle().set("transition", "transform 0.7s");
        heroImage.getElement().addEventListener("mouseenter", e -> heroImage.getStyle().set("transform", "rotate(0deg)"));
        heroImage.getElement().addEventListener("mouseleave", e -> heroImage.getStyle().set("transform", "rotate(2deg)"));

        imageContainer.add(glow, heroImage);

        // Full hero layout
        HorizontalLayout hero = new HorizontalLayout(heroText, imageContainer);
        hero.setWidthFull();
        hero.setAlignItems(FlexComponent.Alignment.CENTER);
        hero.getStyle().set("padding", "40px 48px 80px");
        hero.getStyle().set("gap", "48px");

        return hero;
    }

    // ============ FEATURES SECTION ============
    private VerticalLayout createFeatures() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(false);
        section.getStyle().set("gap", "32px");
        section.getStyle().set("padding", "96px 48px");
        section.getStyle().set("background", BG_GRAY);
        section.setWidthFull();
        section.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        // Section header
        VerticalLayout header = new VerticalLayout();
        header.setPadding(false);
        header.setSpacing(false);
        header.getStyle().set("gap", "16px");
        header.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        H2 title = new H2("Built for performance.");
        title.getStyle().set("font-size", "36px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0");
        title.getStyle().set("text-align", "center");

        Paragraph subtitle = new Paragraph("Stop wasting hours on repetitive writing.");
        subtitle.getStyle().set("font-size", "15px");
        subtitle.getStyle().set("color", TEXT_SECONDARY);
        subtitle.getStyle().set("text-align", "center");
        subtitle.getStyle().set("margin", "0");

        header.add(title, subtitle);

        // Feature cards grid
        HorizontalLayout cards = new HorizontalLayout();
        cards.setWidthFull();
        cards.getStyle().set("gap", "32px");
        cards.getStyle().set("justify-content", "center");
        cards.getStyle().set("flex-wrap", "wrap");

        cards.add(createFeatureCard(
            VaadinIcon.BOLT,
            "#FF9500",
            "Instant Generation",
            "Analyze complex job ads and extract key keywords automatically."
        ));

        cards.add(createFeatureCard(
            VaadinIcon.GLOBE,
            "#007AFF",
            "Company Intel",
            "We research the company's mission to align your voice with their values."
        ));

        cards.add(createFeatureCard(
            VaadinIcon.FILE_TEXT,
            "#34C759",
            "ATS Optimized",
            "Strategically place keywords to ensure you pass through modern filters."
        ));

        section.add(header, cards);

        return section;
    }

    private VerticalLayout createFeatureCard(VaadinIcon iconType, String iconColor, String title, String description) {
        VerticalLayout card = new VerticalLayout();
        card.setPadding(true);
        card.getStyle().set("padding", "32px");
        card.getStyle().set("background", BG_WHITE);
        card.getStyle().set("border-radius", "24px");
        card.getStyle().set("border", "1px solid rgba(0,0,0,0.05)");
        card.getStyle().set("box-shadow", "0 2px 12px rgba(0,0,0,0.01)");
        card.getStyle().set("gap", "24px");
        card.getStyle().set("width", "320px");
        card.getStyle().set("transition", "all 0.5s");
        card.getStyle().set("cursor", "pointer");
        card.setDefaultHorizontalComponentAlignment(Alignment.START);

        // Icon container
        Div iconContainer = new Div();
        iconContainer.getStyle().set("width", "48px");
        iconContainer.getStyle().set("height", "48px");
        iconContainer.getStyle().set("border-radius", "16px");
        iconContainer.getStyle().set("background", "rgba(0,0,0,0.05)");
        iconContainer.getStyle().set("display", "flex");
        iconContainer.getStyle().set("align-items", "center");
        iconContainer.getStyle().set("justify-content", "center");
        iconContainer.getStyle().set("transition", "transform 0.3s");

        Icon icon = iconType.create();
        icon.getStyle().set("color", iconColor);
        icon.getStyle().set("width", "24px");
        icon.getStyle().set("height", "24px");
        iconContainer.add(icon);

        // Title
        H3 cardTitle = new H3(title);
        cardTitle.getStyle().set("font-size", "18px");
        cardTitle.getStyle().set("font-weight", "700");
        cardTitle.getStyle().set("color", TEXT_PRIMARY);
        cardTitle.getStyle().set("margin", "0");

        // Description
        Paragraph cardDesc = new Paragraph(description);
        cardDesc.getStyle().set("font-size", "14px");
        cardDesc.getStyle().set("color", TEXT_SECONDARY);
        cardDesc.getStyle().set("line-height", "1.6");
        cardDesc.getStyle().set("margin", "0");

        card.add(iconContainer, cardTitle, cardDesc);

        // Hover effects
        card.getElement().addEventListener("mouseenter", e -> {
            card.getStyle().set("box-shadow", "0 20px 25px -5px rgba(0,0,0,0.1)");
            card.getStyle().set("border-color", "rgba(0,0,0,0.1)");
            iconContainer.getStyle().set("transform", "scale(1.1)");
        });

        card.getElement().addEventListener("mouseleave", e -> {
            card.getStyle().set("box-shadow", "0 2px 12px rgba(0,0,0,0.01)");
            card.getStyle().set("border-color", "rgba(0,0,0,0.05)");
            iconContainer.getStyle().set("transform", "scale(1)");
        });

        return card;
    }

    // ============ MODALS ============
    private void openFaqModal() {
        if (activeModal != null && activeModal.isOpened()) {
            activeModal.close();
        }

        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setWidth("640px");
        dialog.setMaxHeight("80vh");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);

        // Header
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setPadding(true);
        header.getStyle().set("padding", "24px");
        header.getStyle().set("border-bottom", "1px solid rgba(0,0,0,0.05)");
        header.getStyle().set("background", BG_LIGHT);

        Div iconContainer = new Div();
        iconContainer.getStyle().set("width", "40px");
        iconContainer.getStyle().set("height", "40px");
        iconContainer.getStyle().set("border-radius", "12px");
        iconContainer.getStyle().set("background", "rgba(0,122,255,0.1)");
        iconContainer.getStyle().set("display", "flex");
        iconContainer.getStyle().set("align-items", "center");
        iconContainer.getStyle().set("justify-content", "center");
        iconContainer.add(VaadinIcon.QUESTION.create());

        VerticalLayout titleGroup = new VerticalLayout();
        titleGroup.setPadding(false);
        titleGroup.setSpacing(false);
        H3 title = new H3("Frequently Asked Questions");
        title.getStyle().set("font-size", "20px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("margin", "0");
        Paragraph subtitle = new Paragraph("Everything you need to know");
        subtitle.getStyle().set("font-size", "13px");
        subtitle.getStyle().set("color", TEXT_SECONDARY);
        subtitle.getStyle().set("margin", "4px 0 0");
        titleGroup.add(title, subtitle);

        Button closeBtn = new Button(VaadinIcon.CLOSE.create(), e -> dialog.close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        closeBtn.getStyle().set("color", TEXT_SECONDARY);

        header.add(iconContainer, titleGroup, closeBtn);
        header.expand(titleGroup);

        // FAQ Content
        VerticalLayout faqContent = new VerticalLayout();
        faqContent.setPadding(true);
        faqContent.getStyle().set("padding", "32px");
        faqContent.getStyle().set("gap", "32px");

        List<String[]> faqItems = List.of(
            new String[]{"Is it really free?", "Yes, Cover Booster is currently free for all job seekers. Our mission is to democratize high-quality job application tools."},
            new String[]{"How does the AI work?", "We use advanced Large Language Models specifically tuned for career coaching. We cross-reference your resume against the job description to find the most impactful overlaps."},
            new String[]{"Is my data safe?", "Absolutely. We encrypt all uploaded resumes and do not sell your personal data to third parties. Your privacy is our priority."},
            new String[]{"Can I use multiple resumes?", "Yes, you can upload up to 5 different versions of your resume to target various roles (e.g., Design vs. Management)."}
        );

        for (String[] item : faqItems) {
            VerticalLayout faqItem = new VerticalLayout();
            faqItem.setPadding(false);
            faqItem.setSpacing(false);
            faqItem.getStyle().set("gap", "8px");

            HorizontalLayout questionRow = new HorizontalLayout();
            questionRow.setAlignItems(FlexComponent.Alignment.CENTER);
            questionRow.setSpacing(false);
            questionRow.getStyle().set("gap", "8px");

            Icon check = VaadinIcon.CHECK.create();
            check.getStyle().set("color", PRIMARY);
            check.getStyle().set("width", "16px");
            check.getStyle().set("height", "16px");

            H4 question = new H4(item[0]);
            question.getStyle().set("font-size", "16px");
            question.getStyle().set("font-weight", "700");
            question.getStyle().set("color", TEXT_PRIMARY);
            question.getStyle().set("margin", "0");

            questionRow.add(check, question);

            Paragraph answer = new Paragraph(item[1]);
            answer.getStyle().set("font-size", "14px");
            answer.getStyle().set("color", TEXT_SECONDARY);
            answer.getStyle().set("line-height", "1.6");
            answer.getStyle().set("margin", "0 0 0 24px");

            faqItem.add(questionRow, answer);
            faqContent.add(faqItem);
        }

        // Footer
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        footer.setPadding(true);
        footer.getStyle().set("padding", "24px");
        footer.getStyle().set("border-top", "1px solid rgba(0,0,0,0.05)");
        footer.getStyle().set("background", BG_LIGHT);

        Button ctaBtn = createPrimaryButton("Try it for free", () -> {
            dialog.close();
            getUI().ifPresent(ui -> ui.navigate(SignUpView.class));
        });
        footer.add(ctaBtn);

        content.add(header, faqContent, footer);
        dialog.add(content);

        activeModal = dialog;
        dialog.open();
    }

    private void openSamplesModal() {
        if (activeModal != null && activeModal.isOpened()) {
            activeModal.close();
        }

        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setWidth("640px");
        dialog.setMaxHeight("80vh");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);

        // Header
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setPadding(true);
        header.getStyle().set("padding", "24px");
        header.getStyle().set("border-bottom", "1px solid rgba(0,0,0,0.05)");
        header.getStyle().set("background", BG_LIGHT);

        Div iconContainer = new Div();
        iconContainer.getStyle().set("width", "40px");
        iconContainer.getStyle().set("height", "40px");
        iconContainer.getStyle().set("border-radius", "12px");
        iconContainer.getStyle().set("background", "rgba(0,122,255,0.1)");
        iconContainer.getStyle().set("display", "flex");
        iconContainer.getStyle().set("align-items", "center");
        iconContainer.getStyle().set("justify-content", "center");
        iconContainer.add(VaadinIcon.FILE_TEXT.create());

        VerticalLayout titleGroup = new VerticalLayout();
        titleGroup.setPadding(false);
        titleGroup.setSpacing(false);
        H3 title = new H3("Cover Letter Samples");
        title.getStyle().set("font-size", "20px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("margin", "0");
        Paragraph subtitle = new Paragraph("See what our AI can generate");
        subtitle.getStyle().set("font-size", "13px");
        subtitle.getStyle().set("color", TEXT_SECONDARY);
        subtitle.getStyle().set("margin", "4px 0 0");
        titleGroup.add(title, subtitle);

        Button closeBtn = new Button(VaadinIcon.CLOSE.create(), e -> dialog.close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        closeBtn.getStyle().set("color", TEXT_SECONDARY);

        header.add(iconContainer, titleGroup, closeBtn);
        header.expand(titleGroup);

        // Samples Grid
        VerticalLayout samplesContent = new VerticalLayout();
        samplesContent.setPadding(true);
        samplesContent.getStyle().set("padding", "32px");
        samplesContent.getStyle().set("gap", "16px");

        List<String[]> samples = List.of(
            new String[]{"Senior Product Manager", "Tech Giant", "98%"},
            new String[]{"Junior React Developer", "Modern Startup", "95%"},
            new String[]{"Creative Director", "Design Agency", "92%"},
            new String[]{"Customer Success Lead", "SaaS Corp", "96%"}
        );;

        for (String[] sample : samples) {
            Div sampleCard = new Div();
            sampleCard.getStyle().set("padding", "16px");
            sampleCard.getStyle().set("border", "1px solid rgba(0,0,0,0.05)");
            sampleCard.getStyle().set("border-radius", "16px");
            sampleCard.getStyle().set("background", BG_WHITE);
            sampleCard.getStyle().set("cursor", "pointer");
            sampleCard.getStyle().set("transition", "all 0.3s");

            HorizontalLayout cardContent = new HorizontalLayout();
            cardContent.setAlignItems(FlexComponent.Alignment.START);

            Div fileIcon = new Div();
            fileIcon.getStyle().set("color", TEXT_SECONDARY);
            fileIcon.getStyle().set("transition", "color 0.3s");
            fileIcon.add(VaadinIcon.FILE_TEXT.create());

            VerticalLayout textGroup = new VerticalLayout();
            textGroup.setPadding(false);
            textGroup.setSpacing(false);
            textGroup.getStyle().set("gap", "4px");
            textGroup.setWidthFull();

            H4 jobTitle = new H4(sample[0]);
            jobTitle.getStyle().set("font-size", "14px");
            jobTitle.getStyle().set("font-weight", "700");
            jobTitle.getStyle().set("color", TEXT_PRIMARY);
            jobTitle.getStyle().set("margin", "0");

            Paragraph company = new Paragraph(sample[1]);
            company.getStyle().set("font-size", "12px");
            company.getStyle().set("color", TEXT_SECONDARY);
            company.getStyle().set("margin", "0");

            textGroup.add(jobTitle, company);

            Span matchBadge = new Span(sample[2] + " MATCH");
            matchBadge.getStyle().set("font-size", "11px");
            matchBadge.getStyle().set("font-weight", "900");
            matchBadge.getStyle().set("color", PRIMARY);
            matchBadge.getStyle().set("white-space", "nowrap");

            cardContent.add(fileIcon, textGroup, matchBadge);
            cardContent.setWidthFull();
            cardContent.getStyle().set("gap", "12px");

            sampleCard.add(cardContent);

            // Hover effects
            sampleCard.getElement().addEventListener("mouseenter", e -> {
                sampleCard.getStyle().set("background", "rgba(0,122,255,0.05)");
                sampleCard.getStyle().set("border-color", "rgba(0,122,255,0.2)");
                fileIcon.getStyle().set("color", PRIMARY);
            });

            sampleCard.getElement().addEventListener("mouseleave", e -> {
                sampleCard.getStyle().set("background", BG_WHITE);
                sampleCard.getStyle().set("border-color", "rgba(0,0,0,0.05)");
                fileIcon.getStyle().set("color", TEXT_SECONDARY);
            });

            samplesContent.add(sampleCard);
        }

        // Footer
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        footer.setPadding(true);
        footer.getStyle().set("padding", "24px");
        footer.getStyle().set("border-top", "1px solid rgba(0,0,0,0.05)");
        footer.getStyle().set("background", BG_LIGHT);

        Button ctaBtn = createPrimaryButton("Try it for free", () -> {
            dialog.close();
            getUI().ifPresent(ui -> ui.navigate(SignUpView.class));
        });
        footer.add(ctaBtn);

        content.add(header, samplesContent, footer);
        dialog.add(content);

        activeModal = dialog;
        dialog.open();
    }

    // ============ FOOTER ============
    private HorizontalLayout createFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        footer.setPadding(true);
        footer.getStyle().set("padding", "32px");
        footer.getStyle().set("border-top", "1px solid rgba(0,0,0,0.05)");

        Paragraph copyright = new Paragraph("© 2024 CL Booster. All rights reserved.");
        copyright.getStyle().set("font-size", "13px");
        copyright.getStyle().set("color", TEXT_SECONDARY);
        copyright.getStyle().set("text-align", "center");

        footer.add(copyright);
        return footer;
    }

    // ============ HELPER METHODS ============
    private Button createPrimaryButton(String text, Runnable action) {
        Button btn = new Button(text, e -> action.run());
        btn.getStyle().set("background", "linear-gradient(135deg, " + PRIMARY + " 0%, " + PRIMARY_LIGHT + " 100%)");
        btn.getStyle().set("color", "white");
        btn.getStyle().set("font-weight", "600");
        btn.getStyle().set("border-radius", "9999px");
        btn.getStyle().set("border", "none");
        btn.getStyle().set("box-shadow", "0 10px 15px -3px rgba(0,122,255,0.3)");
        btn.getStyle().set("transition", "all 0.2s");
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

    private Button createSecondaryButton(String text, Runnable action) {
        Button btn = new Button(text, e -> action.run());
        btn.getStyle().set("background", "rgba(0,0,0,0.05)");
        btn.getStyle().set("color", TEXT_PRIMARY);
        btn.getStyle().set("font-weight", "600");
        btn.getStyle().set("border-radius", "9999px");
        btn.getStyle().set("border", "1px solid rgba(0,0,0,0.05)");
        btn.getStyle().set("transition", "all 0.2s");
        btn.getStyle().set("cursor", "pointer");

        btn.getElement().addEventListener("mouseenter", e -> {
            btn.getStyle().set("background", "rgba(0,0,0,0.1)");
        });

        btn.getElement().addEventListener("mouseleave", e -> {
            btn.getStyle().set("background", "rgba(0,0,0,0.05)");
        });

        return btn;
    }

    private void scrollToTop() {
        getElement().executeJs("window.scrollTo({top: 0, behavior: 'smooth'})");
    }

    private void scrollToSection(String sectionId) {
        getElement().executeJs("document.getElementById('" + sectionId + "').scrollIntoView({behavior: 'smooth'})");
    }
}
