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
        // Left section - Logo (fixed width for balance)
        HorizontalLayout leftSection = new HorizontalLayout();
        leftSection.setWidth("200px");
        leftSection.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        leftSection.setAlignItems(FlexComponent.Alignment.CENTER);
        
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
        leftSection.add(logo);

        // Center section - Navigation links
        HorizontalLayout centerSection = new HorizontalLayout();
        centerSection.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        centerSection.setAlignItems(FlexComponent.Alignment.CENTER);
        centerSection.setSpacing(false);
        centerSection.getStyle().set("gap", "32px");

        Button howItWorks = createNavButton("How it works", () -> openHowItWorksModal());
        Button faq = createNavButton("FAQ", () -> openFaqModal());

        centerSection.add(howItWorks, faq);

        // Right section - Auth buttons (fixed width for balance)
        HorizontalLayout rightSection = new HorizontalLayout();
        rightSection.setWidth("200px");
        rightSection.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        rightSection.setAlignItems(FlexComponent.Alignment.CENTER);
        
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

        rightSection.add(loginBtn, signupBtn);

        // Full navbar with three equal sections
        HorizontalLayout navbar = new HorizontalLayout(leftSection, centerSection, rightSection);
        navbar.setWidthFull();
        navbar.setAlignItems(FlexComponent.Alignment.CENTER);
        navbar.setPadding(true);
        navbar.getStyle().set("padding", "0 48px");
        navbar.getStyle().set("height", "80px");
        navbar.getStyle().set("background", BG_WHITE + "cc");
        navbar.getStyle().set("backdrop-filter", "blur(12px)");
        navbar.getStyle().set("position", "sticky");
        navbar.getStyle().set("top", "0");
        navbar.getStyle().set("z-index", "40");
        navbar.getStyle().set("border-bottom", "1px solid rgba(0,0,0,0.05)");
        navbar.getStyle().set("box-sizing", "border-box");
        navbar.expand(centerSection);

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




        // Headline with gradient text - using HTML for proper gradient rendering
        Div headline = new Div();
        headline.getStyle().set("font-size", "clamp(40px, 5vw, 72px)");
        headline.getStyle().set("font-weight", "700");
        headline.getStyle().set("letter-spacing", "-0.025em");
        headline.getStyle().set("line-height", "1.1");
        headline.getStyle().set("margin", "0");
        headline.getStyle().set("max-width", "600px");
        
        // Set HTML content directly for gradient text support
        headline.getElement().setProperty("innerHTML",
            "<div style='color: " + TEXT_PRIMARY + ";'>Elevate your</div>" +
            "<span style='background: linear-gradient(135deg, " + PRIMARY + " 0%, " + PRIMARY_LIGHT + " 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; color: transparent; display: inline-block;'>job hunting</span>" +
            "<span style='color: " + TEXT_PRIMARY + ";'> with AI.</span>"
        );

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

        Button generateBtn = createPrimaryButton("Generate Now", () -> getUI().ifPresent(ui -> ui.navigate(LoginView.class)));
        generateBtn.getStyle().set("font-size", "16px");
        generateBtn.getStyle().set("padding", "16px 40px");

        Button samplesBtn = createSecondaryButton("See Samples", this::openSamplesModal);
        samplesBtn.getStyle().set("font-size", "16px");
        samplesBtn.getStyle().set("padding", "16px 40px");

        ctaRow.add(generateBtn, samplesBtn);
heroText.add(headline, description, ctaRow);

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
        hero.getStyle().set("padding", "80px 48px 128px"); // pt-20 (80px), pb-32 (128px) like Figma
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
            VaadinIcon.CHECK,
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
            getUI().ifPresent(ui -> ui.navigate(LoginView.class));
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

        // Samples Grid - 2 columns like Figma
        Div samplesGrid = new Div();
        samplesGrid.getStyle().set("display", "grid");
        samplesGrid.getStyle().set("grid-template-columns", "repeat(2, 1fr)");
        samplesGrid.getStyle().set("gap", "16px");
        samplesGrid.getStyle().set("padding", "32px");

        // Sample data with cover letter content
        record Sample(String title, String company, String match, String letter) {}
        
        List<Sample> samples = List.of(
            new Sample("Senior Product Manager", "Tech Giant", "98%",
                "Dear Hiring Manager," + System.lineSeparator() + System.lineSeparator() +
                "I am writing to express my strong interest in the Senior Product Manager position at Tech Giant. With over 8 years of experience driving product strategy and leading cross-functional teams, I am excited about the opportunity to contribute to your innovative platform." + System.lineSeparator() + System.lineSeparator() +
                "At my current role, I have successfully launched three major product features that increased user engagement by 45% and revenue by $2M annually. My experience in Agile methodologies, user research, and data-driven decision making aligns perfectly with Tech Giant's mission to create world-class products." + System.lineSeparator() + System.lineSeparator() +
                "I am particularly drawn to Tech Giant's commitment to innovation and user-centric design. I would welcome the opportunity to discuss how my background in product management and passion for technology can contribute to your team's continued success." + System.lineSeparator() + System.lineSeparator() +
                "Thank you for your time and consideration." + System.lineSeparator() + System.lineSeparator() +
                "Sincerely," + System.lineSeparator() +
                "[Your Name]"),
                
            new Sample("Junior React Developer", "Modern Startup", "95%",
                "Dear Hiring Team," + System.lineSeparator() + System.lineSeparator() +
                "I am thrilled to apply for the Junior React Developer position at Modern Startup. As a recent graduate with a Computer Science degree and hands-on experience building responsive web applications, I am eager to contribute to your cutting-edge projects." + System.lineSeparator() + System.lineSeparator() +
                "During my internship at XYZ Company, I developed React components that improved page load times by 30% and implemented Redux for state management in a production application. My portfolio includes several personal projects utilizing React, TypeScript, and modern CSS frameworks." + System.lineSeparator() + System.lineSeparator() +
                "Modern Startup's focus on innovation and growth mindset resonates with my career goals. I am excited about the opportunity to learn from experienced developers while contributing fresh perspectives and strong problem-solving skills." + System.lineSeparator() + System.lineSeparator() +
                "I would love to discuss how my technical skills and enthusiasm can add value to your development team." + System.lineSeparator() + System.lineSeparator() +
                "Best regards," + System.lineSeparator() +
                "[Your Name]"),
                
            new Sample("Creative Director", "Design Agency", "92%",
                "Dear Creative Team," + System.lineSeparator() + System.lineSeparator() +
                "I am excited to apply for the Creative Director position at Design Agency. With 10+ years of experience leading creative teams and delivering award-winning campaigns, I bring a unique blend of artistic vision and strategic thinking to every project." + System.lineSeparator() + System.lineSeparator() +
                "In my current role, I have led rebranding initiatives for Fortune 500 clients, resulting in a 60% increase in brand recognition. My expertise spans brand strategy, visual design, motion graphics, and team mentorship. I am proficient in Adobe Creative Suite, Figma, and emerging AI design tools." + System.lineSeparator() + System.lineSeparator() +
                "Design Agency's reputation for pushing creative boundaries and delivering exceptional work aligns perfectly with my professional values. I am eager to bring my leadership experience and creative expertise to elevate your client portfolio." + System.lineSeparator() + System.lineSeparator() +
                "Thank you for considering my application." + System.lineSeparator() + System.lineSeparator() +
                "Warm regards," + System.lineSeparator() +
                "[Your Name]"),
                
            new Sample("Customer Success Lead", "SaaS Corp", "96%",
                "Dear Hiring Manager," + System.lineSeparator() + System.lineSeparator() +
                "I am writing to express my interest in the Customer Success Lead position at SaaS Corp. With 6 years of experience in customer-facing roles and a proven track record of reducing churn by 25%, I am confident in my ability to drive customer satisfaction and retention." + System.lineSeparator() + System.lineSeparator() +
                "In my previous role, I managed a portfolio of 50+ enterprise accounts and implemented a proactive engagement strategy that increased NPS scores by 35 points. My experience with CRM platforms, data analysis, and team leadership has prepared me to excel in this role." + System.lineSeparator() + System.lineSeparator() +
                "SaaS Corp's commitment to customer-centric innovation is inspiring. I am excited about the opportunity to build and lead a high-performing customer success team that drives growth and loyalty." + System.lineSeparator() + System.lineSeparator() +
                "I look forward to discussing how my experience aligns with your team's goals." + System.lineSeparator() + System.lineSeparator() +
                "Best regards," + System.lineSeparator() +
                "[Your Name]")
        );

        for (Sample sample : samples) {
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

            H4 jobTitle = new H4(sample.title());
            jobTitle.getStyle().set("font-size", "14px");
            jobTitle.getStyle().set("font-weight", "700");
            jobTitle.getStyle().set("color", TEXT_PRIMARY);
            jobTitle.getStyle().set("margin", "0");

            Paragraph company = new Paragraph(sample.company());
            company.getStyle().set("font-size", "12px");
            company.getStyle().set("color", TEXT_SECONDARY);
            company.getStyle().set("margin", "0");

            textGroup.add(jobTitle, company);

            Span matchBadge = new Span(sample.match() + " MATCH");
            matchBadge.getStyle().set("font-size", "11px");
            matchBadge.getStyle().set("font-weight", "900");
            matchBadge.getStyle().set("color", PRIMARY);
            matchBadge.getStyle().set("white-space", "nowrap");

            cardContent.add(fileIcon, textGroup, matchBadge);
            cardContent.setWidthFull();
            cardContent.getStyle().set("gap", "12px");

            sampleCard.add(cardContent);

            // Click listener to open cover letter
            sampleCard.addClickListener(e -> openCoverLetterModal(sample.title(), sample.company(), sample.letter()));

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

            samplesGrid.add(sampleCard);
        }

        // Wrap in scrollable container
        Div scrollContainer = new Div();
        scrollContainer.getStyle().set("overflow-y", "auto");
        scrollContainer.getStyle().set("max-height", "50vh");
        scrollContainer.add(samplesGrid);

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
            getUI().ifPresent(ui -> ui.navigate(LoginView.class));
        });
        footer.add(ctaBtn);

        content.add(header, scrollContainer, footer);
        dialog.add(content);

        activeModal = dialog;
        dialog.open();
    }

    private void openHowItWorksModal() {
        if (activeModal != null && activeModal.isOpened()) {
            activeModal.close();
        }

        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setWidth("560px");
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
        iconContainer.add(VaadinIcon.LIGHTBULB.create());

        VerticalLayout titleGroup = new VerticalLayout();
        titleGroup.setPadding(false);
        titleGroup.setSpacing(false);
        H3 title = new H3("How It Works");
        title.getStyle().set("font-size", "20px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("margin", "0");
        Paragraph subtitle = new Paragraph("3 simple steps to your perfect cover letter");
        subtitle.getStyle().set("font-size", "13px");
        subtitle.getStyle().set("color", TEXT_SECONDARY);
        subtitle.getStyle().set("margin", "4px 0 0");
        titleGroup.add(title, subtitle);

        Button closeBtn = new Button(VaadinIcon.CLOSE.create(), e -> dialog.close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        closeBtn.getStyle().set("color", TEXT_SECONDARY);

        header.add(iconContainer, titleGroup, closeBtn);
        header.expand(titleGroup);

        // Steps Content
        VerticalLayout stepsContent = new VerticalLayout();
        stepsContent.setPadding(true);
        stepsContent.getStyle().set("padding", "32px");
        stepsContent.getStyle().set("gap", "24px");

        // Step 1
        HorizontalLayout step1 = createStepItem("1", "Upload Your Resume",
            "Upload your existing resume or create a new profile. Our AI will analyze your skills and experience.");
        
        // Step 2
        HorizontalLayout step2 = createStepItem("2", "Paste Job Description",
            "Copy and paste the job posting you're applying for. We'll extract key requirements and company details.");
        
        // Step 3
        HorizontalLayout step3 = createStepItem("3", "Generate & Download",
            "Our AI creates a tailored cover letter in seconds. Edit if needed, then download as PDF or copy to clipboard.");

        stepsContent.add(step1, step2, step3);

        // Footer
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        footer.setPadding(true);
        footer.getStyle().set("padding", "24px");
        footer.getStyle().set("border-top", "1px solid rgba(0,0,0,0.05)");
        footer.getStyle().set("background", BG_LIGHT);

        Button ctaBtn = createPrimaryButton("Get Started", () -> {
            dialog.close();
            getUI().ifPresent(ui -> ui.navigate(LoginView.class));
        });
        footer.add(ctaBtn);

        content.add(header, stepsContent, footer);
        dialog.add(content);

        activeModal = dialog;
        dialog.open();
    }

    private HorizontalLayout createStepItem(String number, String title, String description) {
        HorizontalLayout stepRow = new HorizontalLayout();
        stepRow.setWidthFull();
        stepRow.getStyle().set("gap", "16px");
        stepRow.setAlignItems(FlexComponent.Alignment.START);

        // Step number circle
        Div numberCircle = new Div();
        numberCircle.getStyle().set("width", "32px");
        numberCircle.getStyle().set("height", "32px");
        numberCircle.getStyle().set("border-radius", "50%");
        numberCircle.getStyle().set("background", PRIMARY);
        numberCircle.getStyle().set("color", "white");
        numberCircle.getStyle().set("display", "flex");
        numberCircle.getStyle().set("align-items", "center");
        numberCircle.getStyle().set("justify-content", "center");
        numberCircle.getStyle().set("font-weight", "700");
        numberCircle.getStyle().set("font-size", "14px");
        numberCircle.getStyle().set("flex-shrink", "0");
        numberCircle.add(new Span(number));

        // Content
        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);
        content.getStyle().set("gap", "4px");

        H4 stepTitle = new H4(title);
        stepTitle.getStyle().set("font-size", "16px");
        stepTitle.getStyle().set("font-weight", "700");
        stepTitle.getStyle().set("color", TEXT_PRIMARY);
        stepTitle.getStyle().set("margin", "0");

        Paragraph stepDesc = new Paragraph(description);
        stepDesc.getStyle().set("font-size", "14px");
        stepDesc.getStyle().set("color", TEXT_SECONDARY);
        stepDesc.getStyle().set("line-height", "1.5");
        stepDesc.getStyle().set("margin", "0");

        content.add(stepTitle, stepDesc);
        stepRow.add(numberCircle, content);
        stepRow.expand(content);

        return stepRow;
    }

    private void openCoverLetterModal(String jobTitle, String company, String letterContent) {
        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setWidth("720px");
        dialog.setMaxHeight("90vh");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);

        // Header
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setPadding(true);
        header.getStyle().set("padding", "20px 24px");
        header.getStyle().set("border-bottom", "1px solid rgba(0,0,0,0.05)");
        header.getStyle().set("background", BG_LIGHT);

        Div iconContainer = new Div();
        iconContainer.getStyle().set("width", "36px");
        iconContainer.getStyle().set("height", "36px");
        iconContainer.getStyle().set("border-radius", "10px");
        iconContainer.getStyle().set("background", "rgba(0,122,255,0.1)");
        iconContainer.getStyle().set("display", "flex");
        iconContainer.getStyle().set("align-items", "center");
        iconContainer.getStyle().set("justify-content", "center");
        iconContainer.add(VaadinIcon.FILE_TEXT_O.create());

        VerticalLayout titleGroup = new VerticalLayout();
        titleGroup.setPadding(false);
        titleGroup.setSpacing(false);
        H3 title = new H3(jobTitle);
        title.getStyle().set("font-size", "16px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("margin", "0");
        Paragraph subtitle = new Paragraph(company);
        subtitle.getStyle().set("font-size", "12px");
        subtitle.getStyle().set("color", TEXT_SECONDARY);
        subtitle.getStyle().set("margin", "2px 0 0");
        titleGroup.add(title, subtitle);

        Button closeBtn = new Button(VaadinIcon.CLOSE.create(), e -> dialog.close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        closeBtn.getStyle().set("color", TEXT_SECONDARY);

        header.add(iconContainer, titleGroup, closeBtn);
        header.expand(titleGroup);

        // Letter Content
        Div letterContainer = new Div();
        letterContainer.getStyle().set("padding", "32px");
        letterContainer.getStyle().set("background", BG_WHITE);
        letterContainer.getStyle().set("overflow-y", "auto");
        letterContainer.getStyle().set("max-height", "60vh");

        // Format letter with proper styling
        Div letterText = new Div();
        letterText.getStyle().set("font-family", "Georgia, 'Times New Roman', serif");
        letterText.getStyle().set("font-size", "15px");
        letterText.getStyle().set("line-height", "1.8");
        letterText.getStyle().set("color", TEXT_PRIMARY);
        letterText.getStyle().set("white-space", "pre-wrap");
        letterText.setText(letterContent);

        letterContainer.add(letterText);

        // Footer with copy button
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        footer.setPadding(true);
        footer.getStyle().set("padding", "16px 24px");
        footer.getStyle().set("border-top", "1px solid rgba(0,0,0,0.05)");
        footer.getStyle().set("background", BG_LIGHT);
        footer.getStyle().set("gap", "12px");

        Button copyBtn = new Button("Copy to Clipboard", VaadinIcon.COPY.create());
        copyBtn.getStyle().set("background", "rgba(0,0,0,0.05)");
        copyBtn.getStyle().set("color", TEXT_PRIMARY);
        copyBtn.getStyle().set("font-weight", "600");
        copyBtn.getStyle().set("border-radius", "9999px");
        copyBtn.getStyle().set("padding", "10px 20px");
        copyBtn.getStyle().set("cursor", "pointer");
        copyBtn.addClickListener(e -> {
            copyBtn.getElement().executeJs("navigator.clipboard.writeText($0)", letterContent);
            copyBtn.setText("Copied!");
            copyBtn.setIcon(VaadinIcon.CHECK.create());
        });

        Button useTemplateBtn = createPrimaryButton("Use This Template", () -> {
            dialog.close();
            getUI().ifPresent(ui -> ui.navigate(LoginView.class));
        });

        footer.add(copyBtn, useTemplateBtn);

        content.add(header, letterContainer, footer);
        dialog.add(content);
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
