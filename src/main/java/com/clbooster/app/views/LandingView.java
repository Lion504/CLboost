package com.clbooster.app.views;

import com.clbooster.app.i18n.TranslationService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
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

import java.util.List;
import java.util.Locale;

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

    private static final String COLOR_PROP = "color";
    private static final String FONT_SIZE_PROP = "font-size";
    private static final String FONT_WEIGHT_PROP = "font-weight";
    private static final String BACKGROUND_PROP = "background";
    private static final String MARGIN_PROP = "margin";
    private static final String PADDING_PROP = "padding";
    private static final String BORDER_RADIUS_PROP = "border-radius";
    private static final String GAP_PROP = "gap";
    private static final String WIDTH_PROP = "width";
    private static final String HEIGHT_PROP = "height";
    private static final String DISPLAY_PROP = "display";
    private static final String ALIGN_ITEMS_PROP = "align-items";
    private static final String JUSTIFY_CONTENT_PROP = "justify-content";
    private static final String CURSOR_PROP = "cursor";
    private static final String TRANSITION_PROP = "transition";

    private static final String POINTER_CURSOR = "pointer";
    private static final String CENTER_ALIGN = "center";
    private static final String WHITE_COLOR = "white";
    private static final String FULLY_ROUNDED = "9999px";
    private static final String TRANSITION_ALL_FAST = "all 0.2s";
    private static final String EVENT_MOUSELEAVE = "mouseleave";
    private static final String BG_PRIMARY_ALPHA_10 = "rgba(0,122,255,0.1)";
    private static final String BG_BLACK_ALPHA_05 = "rgba(0,0,0,0.05)";
    private static final String MARGIN_TOP_SMALL = "4px 0 0";

    private static final String BORDER_PROP = "border";
    private static final String BORDER_TOP_PROP = "border-top";
    private static final String BORDER_BOTTOM_PROP = "border-bottom";
    private static final String BORDER_COLOR_PROP = "border-color";
    private static final String BOX_SHADOW_PROP = "box-shadow";
    private static final String TRANSFORM_PROP = "transform";
    private static final String LINE_HEIGHT_PROP = "line-height";
    private static final String TEXT_ALIGN_PROP = "text-align";
    private static final String ZERO_VALUE = "0";
    private static final String BORDER_SUBTLE = "1px solid rgba(0,0,0,0.05)";
    private static final String FONT_WEIGHT_BOLD = "700";
    private static final String FONT_WEIGHT_SEMIBOLD = "600";
    private static final String DISPLAY_FLEX = "flex";
    private static final String TRANSPARENT_COLOR = "transparent";
    private static final String EVENT_MOUSEENTER = "mouseenter";

    private static final String LANG_ENGLISH = "English";
    private static final String LANG_SUOMI = "Suomi";
    private static final String LANG_PORTUGUESE = "Português";
    private static final String LANG_PERSIAN = "فارسی";
    private static final String LANG_CHINESE = "中文";
    private static final String LANG_URDU = "اردو";
    private static final String POSITION_PROP = "position";
    private static final String MAX_WIDTH_PROP = "max-width";
    private static final String FILTER_PROP = "filter";
    private static final String YOUR_NAME_PLACEHOLDER = "[Your Name]";


    private Dialog activeModal = null;
    private final TranslationService translationService;

    public LandingView() {
        this.translationService = new TranslationService();

        // Apply saved locale from session on page load
        try {
            Locale savedLocale = translationService.getCurrentLocale();
            if (savedLocale != null) {
                translationService.setCurrentLocale(savedLocale);
            }
        } catch (Exception e) {
            // VaadinSession not available yet, use default
        }

        setPadding(false);
        setSpacing(false);
        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set(BACKGROUND_PROP, BG_WHITE);
        getStyle().set("font-family",
                "-apple-system, BlinkMacSystemFont, 'SF Pro Text', 'SF Pro Display', system-ui, sans-serif");

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
        logo.getStyle().set(GAP_PROP, "8px");
        logo.getStyle().set(CURSOR_PROP, POINTER_CURSOR);

        Div logoIcon = new Div();
        logoIcon.getStyle().set(WIDTH_PROP, "32px");
        logoIcon.getStyle().set(HEIGHT_PROP, "32px");
        logoIcon.getStyle().set(BACKGROUND_PROP, PRIMARY);
        logoIcon.getStyle().set(BORDER_RADIUS_PROP, "8px");
        logoIcon.getStyle().set(DISPLAY_PROP, DISPLAY_FLEX);
        logoIcon.getStyle().set(ALIGN_ITEMS_PROP, CENTER_ALIGN);
        logoIcon.getStyle().set(JUSTIFY_CONTENT_PROP, CENTER_ALIGN);
        logoIcon.getStyle().set(COLOR_PROP, WHITE_COLOR);
        logoIcon.add(VaadinIcon.SPARK_LINE.create());

        Span logoText = new Span(translationService.translate("landing.clBooster"));
        logoText.getStyle().set(FONT_WEIGHT_PROP, FONT_WEIGHT_BOLD);
        logoText.getStyle().set(FONT_SIZE_PROP, "20px");
        logoText.getStyle().set("letter-spacing", "-0.025em");
        logoText.getStyle().set(COLOR_PROP, TEXT_PRIMARY);

        logo.add(logoIcon, logoText);
        logo.addClickListener(e -> scrollToTop());
        leftSection.add(logo);

        // Center section - Navigation links
        HorizontalLayout centerSection = new HorizontalLayout();
        centerSection.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        centerSection.setAlignItems(FlexComponent.Alignment.CENTER);
        centerSection.setSpacing(false);
        centerSection.getStyle().set(GAP_PROP, "32px");

        Button howItWorks = createNavButton(translationService.translate("landing.howItWorks"),
                this::openHowItWorksModal);
        Button faq = createNavButton(translationService.translate("landing.faq"), this::openFaqModal);

        centerSection.add(howItWorks, faq);

        // Right section - Language switcher + Auth buttons (fixed width for balance)
        HorizontalLayout rightSection = new HorizontalLayout();
        rightSection.setWidth("280px");
        rightSection.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        rightSection.setAlignItems(FlexComponent.Alignment.CENTER);
        rightSection.getStyle().set(GAP_PROP, "12px");

        // Language switcher - set current language based on saved locale
        String currentLang = LANG_ENGLISH;
        try {
            Locale currentLocale = translationService.getCurrentLocale();
            if (currentLocale != null) {
                String lang = currentLocale.getLanguage();
                if ("fi".equals(lang))
                    currentLang = LANG_SUOMI;
                else if ("pt".equals(lang))
                    currentLang = LANG_PORTUGUESE;
                else if ("fa".equals(lang))
                    currentLang = LANG_PERSIAN;
                else if ("zh".equals(lang))
                    currentLang = LANG_CHINESE;
                else if ("ur".equals(lang))
                    currentLang = LANG_URDU;
            }
        } catch (Exception e) {
            // VaadinSession not available, use default
        }

        Select<String> langSelect = new Select<>();
        langSelect.setItems(LANG_ENGLISH, LANG_SUOMI, LANG_PORTUGUESE, LANG_PERSIAN, LANG_CHINESE, LANG_URDU);
        langSelect.setValue(currentLang);
        langSelect.setWidth("100px");
        langSelect.getStyle().set("--vaadin-input-field-background", TRANSPARENT_COLOR);
        langSelect.getStyle().set(FONT_SIZE_PROP, "13px");
        langSelect.getStyle().set(FONT_WEIGHT_PROP, FONT_WEIGHT_SEMIBOLD);
        langSelect.addValueChangeListener(e -> {
            String lang = e.getValue();
            if (lang != null) {
                String langCode;
                switch (lang) {
                case LANG_SUOMI:
                    langCode = "Finnish (Suomi)";
                    break;
                case LANG_PORTUGUESE:
                    langCode = "Portuguese (Português)";
                    break;
                case LANG_PERSIAN:
                    langCode = "Persian (فارسی)";
                    break;
                case LANG_CHINESE:
                    langCode = "Chinese (中文)";
                    break;
                case LANG_URDU:
                    langCode = "Urdu (اردو)";
                    break;
                default:
                    langCode = LANG_ENGLISH;
                }
                translationService.setLanguage(langCode);
                getUI().ifPresent(ui -> ui.getPage().reload());
            }
        });

        Button loginBtn = new Button(translationService.translate("landing.logIn"),
                e -> getUI().ifPresent(ui -> ui.navigate(LoginView.class)));
        loginBtn.getStyle().set(FONT_SIZE_PROP, "13px");
        loginBtn.getStyle().set(FONT_WEIGHT_PROP, FONT_WEIGHT_BOLD);
        loginBtn.getStyle().set(COLOR_PROP, TEXT_PRIMARY);
        loginBtn.getStyle().set(BACKGROUND_PROP, TRANSPARENT_COLOR);
        loginBtn.getStyle().set(BORDER_RADIUS_PROP, FULLY_ROUNDED);
        loginBtn.getStyle().set(PADDING_PROP, "8px 16px");
        loginBtn.getStyle().set(TRANSITION_PROP, TRANSITION_ALL_FAST);
        loginBtn.getElement().addEventListener(EVENT_MOUSEENTER,
                e -> loginBtn.getStyle().set(BACKGROUND_PROP, "rgba(0, 0, 0, 0.08)"));
        loginBtn.getElement().addEventListener(EVENT_MOUSELEAVE, e -> loginBtn.getStyle().set(BACKGROUND_PROP, TRANSPARENT_COLOR));

        Button signupBtn = createPrimaryButton(translationService.translate("signup.signup"),
                () -> getUI().ifPresent(ui -> ui.navigate(SignUpView.class)));
        signupBtn.getStyle().set(FONT_SIZE_PROP, "13px");
        signupBtn.getStyle().set(PADDING_PROP, "8px 20px");

        rightSection.add(langSelect, loginBtn, signupBtn);

        // Full navbar with three equal sections
        HorizontalLayout navbar = new HorizontalLayout(leftSection, centerSection, rightSection);
        navbar.setWidthFull();
        navbar.setAlignItems(FlexComponent.Alignment.CENTER);
        navbar.setPadding(true);
        navbar.getStyle().set(PADDING_PROP, "0 48px");
        navbar.getStyle().set(HEIGHT_PROP, "80px");
        navbar.getStyle().set(BACKGROUND_PROP, BG_WHITE + "cc");
        navbar.getStyle().set("backdrop-filter", "blur(12px)");
        navbar.getStyle().set(POSITION_PROP, "sticky");
        navbar.getStyle().set("top", ZERO_VALUE);
        navbar.getStyle().set("z-index", "40");
        navbar.getStyle().set(BORDER_BOTTOM_PROP, BORDER_SUBTLE);
        navbar.getStyle().set("box-sizing", "border-box");
        navbar.expand(centerSection);

        return navbar;
    }

    private Button createNavButton(String text, Runnable action) {
        Button btn = new Button(text, e -> action.run());
        btn.getStyle().set(FONT_SIZE_PROP, "13px");
        btn.getStyle().set(FONT_WEIGHT_PROP, FONT_WEIGHT_SEMIBOLD);
        btn.getStyle().set(COLOR_PROP, TEXT_SECONDARY);
        btn.getStyle().set(BACKGROUND_PROP, TRANSPARENT_COLOR);
        btn.getStyle().set(PADDING_PROP, "8px 0");
        btn.getStyle().set(TRANSITION_PROP, "color 0.2s");
        btn.getElement().addEventListener(EVENT_MOUSEENTER, e -> btn.getStyle().set(COLOR_PROP, TEXT_PRIMARY));
        btn.getElement().addEventListener(EVENT_MOUSELEAVE, e -> btn.getStyle().set(COLOR_PROP, TEXT_SECONDARY));
        return btn;
    }

    // ============ HERO SECTION ============
    private HorizontalLayout createHero() {
        // Left side - Text content
        VerticalLayout heroText = new VerticalLayout();
        heroText.setPadding(false);
        heroText.setSpacing(false);
        heroText.getStyle().set(GAP_PROP, "32px");
        heroText.setWidth("50%");
        heroText.setDefaultHorizontalComponentAlignment(Alignment.START);

        // NEW Badge

        // Headline with gradient text - using HTML for proper gradient rendering
        Div headline = new Div();
        headline.getStyle().set(FONT_SIZE_PROP, "clamp(40px, 5vw, 72px)");
        headline.getStyle().set(FONT_WEIGHT_PROP, FONT_WEIGHT_BOLD);
        headline.getStyle().set("letter-spacing", "-0.025em");
        headline.getStyle().set(LINE_HEIGHT_PROP, "1.1");
        headline.getStyle().set(MARGIN_PROP, ZERO_VALUE);
        headline.getStyle().set(MAX_WIDTH_PROP, "600px");

        // Set HTML content directly for gradient text support
        String elevateYour = translationService.translate("landing.elevateYour");
        String jobHunting = translationService.translate("landing.jobHunting");
        String withAI = translationService.translate("landing.withAI");
        headline.getElement().setProperty("innerHTML", "<div style='color: " + TEXT_PRIMARY + ";'>" + elevateYour
                + "</div>" + "<span style='background: linear-gradient(135deg, " + PRIMARY + " 0%, " + PRIMARY_LIGHT
                + " 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; color: transparent; display: inline-block;'>"
                + jobHunting + "</span>" + "<span style='color: " + TEXT_PRIMARY + ";'> " + withAI + "</span>");

        // Description
        Paragraph description = new Paragraph(translationService.translate("landing.heroDescription"));
        description.getStyle().set(FONT_SIZE_PROP, "20px");
        description.getStyle().set(COLOR_PROP, TEXT_SECONDARY);
        description.getStyle().set(LINE_HEIGHT_PROP, "1.6");
        description.getStyle().set(MAX_WIDTH_PROP, "520px");
        description.getStyle().set(MARGIN_PROP, ZERO_VALUE);

        // CTA Buttons
        HorizontalLayout ctaRow = new HorizontalLayout();
        ctaRow.setSpacing(false);
        ctaRow.getStyle().set(GAP_PROP, "16px");
        ctaRow.getStyle().set("padding-top", "16px");

        Button generateBtn = createPrimaryButton(translationService.translate("landing.generateNow"),
                () -> getUI().ifPresent(ui -> ui.navigate(LoginView.class)));
        generateBtn.getStyle().set(FONT_SIZE_PROP, "16px");
        generateBtn.getStyle().set(PADDING_PROP, "16px 40px");

        Button samplesBtn = createSecondaryButton(translationService.translate("landing.seeSamples"),
                this::openSamplesModal);
        samplesBtn.getStyle().set(FONT_SIZE_PROP, "16px");
        samplesBtn.getStyle().set(PADDING_PROP, "16px 40px");

        ctaRow.add(generateBtn, samplesBtn);
        heroText.add(headline, description, ctaRow);

        heroText.getStyle().set(PADDING_PROP, "80px 0");

        // Right side - Hero Image
        Div imageContainer = new Div();
        imageContainer.setWidth("50%");
        imageContainer.getStyle().set(POSITION_PROP, "relative");
        imageContainer.getStyle().set(DISPLAY_PROP, DISPLAY_FLEX);
        imageContainer.getStyle().set(JUSTIFY_CONTENT_PROP, CENTER_ALIGN);
        imageContainer.getStyle().set(ALIGN_ITEMS_PROP, CENTER_ALIGN);

        // Glow effect behind image
        Div glow = new Div();
        glow.getStyle().set(POSITION_PROP, "absolute");
        glow.getStyle().set("inset", "-40px");
        glow.getStyle().set(BACKGROUND_PROP, BG_PRIMARY_ALPHA_10);
        glow.getStyle().set(FILTER_PROP, "blur(100px)");
        glow.getStyle().set(BORDER_RADIUS_PROP, "50%");
        glow.getStyle().set("z-index", "-1");
        glow.getStyle().set("animation", "pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite");

        Image heroImage = new Image("images/hero.jpg", translationService.translate("landing.heroImage"));
        heroImage.getStyle().set(WIDTH_PROP, "100%");
        heroImage.getStyle().set(MAX_WIDTH_PROP, "560px");
        heroImage.getStyle().set(BORDER_RADIUS_PROP, "48px");
        heroImage.getStyle().set(BOX_SHADOW_PROP, "0 25px 50px -12px rgba(0,0,0,0.25)");
        heroImage.getStyle().set(BORDER_PROP, BORDER_SUBTLE);
        heroImage.getStyle().set(TRANSFORM_PROP, "rotate(2deg)");
        heroImage.getStyle().set(TRANSITION_PROP, "transform 0.7s");
        heroImage.getElement().addEventListener(EVENT_MOUSEENTER,
                e -> heroImage.getStyle().set(TRANSFORM_PROP, "rotate(0deg)"));
        heroImage.getElement().addEventListener(EVENT_MOUSELEAVE,
                e -> heroImage.getStyle().set(TRANSFORM_PROP, "rotate(2deg)"));

        imageContainer.add(glow, heroImage);

        // Full hero layout
        HorizontalLayout hero = new HorizontalLayout(heroText, imageContainer);
        hero.setWidthFull();
        hero.setAlignItems(FlexComponent.Alignment.CENTER);
        hero.getStyle().set(PADDING_PROP, "80px 48px 128px"); // pt-20 (80px), pb-32 (128px) like Figma
        hero.getStyle().set(GAP_PROP, "48px");

        return hero;
    }

    // ============ FEATURES SECTION ============
    private VerticalLayout createFeatures() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(false);
        section.getStyle().set(GAP_PROP, "32px");
        section.getStyle().set(PADDING_PROP, "96px 48px");
        section.getStyle().set(BACKGROUND_PROP, BG_GRAY);
        section.setWidthFull();
        section.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        // Section header
        VerticalLayout header = new VerticalLayout();
        header.setPadding(false);
        header.setSpacing(false);
        header.getStyle().set(GAP_PROP, "16px");
        header.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        H2 title = new H2(translationService.translate("landing.builtForPerformance"));
        title.getStyle().set(FONT_SIZE_PROP, "36px");
        title.getStyle().set(FONT_WEIGHT_PROP, FONT_WEIGHT_BOLD);
        title.getStyle().set(COLOR_PROP, TEXT_PRIMARY);
        title.getStyle().set(MARGIN_PROP, ZERO_VALUE);
        title.getStyle().set(TEXT_ALIGN_PROP, CENTER_ALIGN);

        Paragraph subtitle = new Paragraph(translationService.translate("landing.stopWastingHours"));
        subtitle.getStyle().set(FONT_SIZE_PROP, "15px");
        subtitle.getStyle().set(COLOR_PROP, TEXT_SECONDARY);
        subtitle.getStyle().set(TEXT_ALIGN_PROP, CENTER_ALIGN);
        subtitle.getStyle().set(MARGIN_PROP, ZERO_VALUE);

        header.add(title, subtitle);

        // Feature cards grid
        HorizontalLayout cards = new HorizontalLayout();
        cards.setWidthFull();
        cards.getStyle().set(GAP_PROP, "32px");
        cards.getStyle().set(JUSTIFY_CONTENT_PROP, CENTER_ALIGN);
        cards.getStyle().set("flex-wrap", "wrap");

        cards.add(
                createFeatureCard(VaadinIcon.BOLT, "#FF9500", translationService.translate("landing.instantGeneration"),
                        translationService.translate("landing.instantGenerationDesc")));

        cards.add(createFeatureCard(VaadinIcon.GLOBE, PRIMARY, translationService.translate("landing.companyIntel"),
                translationService.translate("landing.companyIntelDesc")));

        cards.add(createFeatureCard(VaadinIcon.CHECK, "#34C759", translationService.translate("landing.atsOptimized"),
                translationService.translate("landing.atsOptimizedDesc")));

        section.add(header, cards);

        return section;
    }

    private VerticalLayout createFeatureCard(VaadinIcon iconType, String iconColor, String title, String description) {
        VerticalLayout card = new VerticalLayout();
        card.setPadding(true);
        card.getStyle().set(PADDING_PROP, "32px");
        card.getStyle().set(BACKGROUND_PROP, BG_WHITE);
        card.getStyle().set(BORDER_RADIUS_PROP, "24px");
        card.getStyle().set(BORDER_PROP, BORDER_SUBTLE);
        card.getStyle().set(BOX_SHADOW_PROP, "0 2px 12px rgba(0,0,0,0.01)");
        card.getStyle().set(GAP_PROP, "24px");
        card.getStyle().set(WIDTH_PROP, "320px");
        card.getStyle().set(TRANSITION_PROP, "all 0.5s");
        card.getStyle().set(CURSOR_PROP, POINTER_CURSOR);
        card.setDefaultHorizontalComponentAlignment(Alignment.START);

        // Icon container
        Div iconContainer = new Div();
        iconContainer.getStyle().set(WIDTH_PROP, "48px");
        iconContainer.getStyle().set(HEIGHT_PROP, "48px");
        iconContainer.getStyle().set(BORDER_RADIUS_PROP, "16px");
        iconContainer.getStyle().set(BACKGROUND_PROP, BG_BLACK_ALPHA_05);
        iconContainer.getStyle().set(DISPLAY_PROP, DISPLAY_FLEX);
        iconContainer.getStyle().set(ALIGN_ITEMS_PROP, CENTER_ALIGN);
        iconContainer.getStyle().set(JUSTIFY_CONTENT_PROP, CENTER_ALIGN);
        iconContainer.getStyle().set(TRANSITION_PROP, "transform 0.3s");

        Icon icon = iconType.create();
        icon.getStyle().set(COLOR_PROP, iconColor);
        icon.getStyle().set(WIDTH_PROP, "24px");
        icon.getStyle().set(HEIGHT_PROP, "24px");
        iconContainer.add(icon);

        // Title
        H3 cardTitle = new H3(title);
        cardTitle.getStyle().set(FONT_SIZE_PROP, "18px");
        cardTitle.getStyle().set(FONT_WEIGHT_PROP, FONT_WEIGHT_BOLD);
        cardTitle.getStyle().set(COLOR_PROP, TEXT_PRIMARY);
        cardTitle.getStyle().set(MARGIN_PROP, ZERO_VALUE);

        // Description
        Paragraph cardDesc = new Paragraph(description);
        cardDesc.getStyle().set(FONT_SIZE_PROP, "14px");
        cardDesc.getStyle().set(COLOR_PROP, TEXT_SECONDARY);
        cardDesc.getStyle().set(LINE_HEIGHT_PROP, "1.6");
        cardDesc.getStyle().set(MARGIN_PROP, ZERO_VALUE);

        card.add(iconContainer, cardTitle, cardDesc);

        // Hover effects
        card.getElement().addEventListener(EVENT_MOUSEENTER, e -> {
            card.getStyle().set(BOX_SHADOW_PROP, "0 20px 25px -5px rgba(0,0,0,0.1)");
            card.getStyle().set(BORDER_COLOR_PROP, "rgba(0,0,0,0.1)");
            iconContainer.getStyle().set(TRANSFORM_PROP, "scale(1.1)");
        });

        card.getElement().addEventListener(EVENT_MOUSELEAVE, e -> {
            card.getStyle().set(BOX_SHADOW_PROP, "0 2px 12px rgba(0,0,0,0.01)");
            card.getStyle().set(BORDER_COLOR_PROP, BG_BLACK_ALPHA_05);
            iconContainer.getStyle().set(TRANSFORM_PROP, "scale(1)");
        });

        return card;
    }

    // ============ MODALS ============
    private void openFaqModal() {
        if (activeModal != null && activeModal.isOpened()) {
            activeModal.close();
        }

        Dialog dialog = setupDialog("640px", "80vh");
        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);

        HorizontalLayout header = createModalHeader(dialog, VaadinIcon.QUESTION, 
            translationService.translate("help.faqTitle"), 
            translationService.translate("landing.everythingYouNeed"), STANDARD_HEADER_STYLE);

        // FAQ Content
        VerticalLayout faqContent = new VerticalLayout();
        faqContent.setPadding(true);
        faqContent.getStyle().set(PADDING_PROP, "32px");
        faqContent.getStyle().set(GAP_PROP, "32px");

        List<String[]> faqItems = List.of(
                new String[] { translationService.translate("landing.faqIsFree"),
                        translationService.translate("landing.faqIsFreeAnswer") },
                new String[] { translationService.translate("landing.faqHowAi"),
                        translationService.translate("landing.faqHowAiAnswer") },
                new String[] { translationService.translate("landing.faqDataSafe"),
                        translationService.translate("landing.faqDataSafeAnswer") },
                new String[] { translationService.translate("landing.faqMultipleResumes"),
                        translationService.translate("landing.faqMultipleResumesAnswer") });

        for (String[] item : faqItems) {
            VerticalLayout faqItem = new VerticalLayout();
            faqItem.setPadding(false);
            faqItem.setSpacing(false);
            faqItem.getStyle().set(GAP_PROP, "8px");

            HorizontalLayout questionRow = new HorizontalLayout();
            questionRow.setAlignItems(FlexComponent.Alignment.CENTER);
            questionRow.setSpacing(false);
            questionRow.getStyle().set(GAP_PROP, "8px");

            Icon check = VaadinIcon.CHECK.create();
            check.getStyle().set(COLOR_PROP, PRIMARY);
            check.getStyle().set(WIDTH_PROP, "16px");
            check.getStyle().set(HEIGHT_PROP, "16px");

            H4 question = new H4(item[0]);
            question.getStyle().set(FONT_SIZE_PROP, "16px");
            question.getStyle().set(FONT_WEIGHT_PROP, FONT_WEIGHT_BOLD);
            question.getStyle().set(COLOR_PROP, TEXT_PRIMARY);
            question.getStyle().set(MARGIN_PROP, ZERO_VALUE);

            questionRow.add(check, question);

            Paragraph answer = new Paragraph(item[1]);
            answer.getStyle().set(FONT_SIZE_PROP, "14px");
            answer.getStyle().set(COLOR_PROP, TEXT_SECONDARY);
            answer.getStyle().set(LINE_HEIGHT_PROP, "1.6");
            answer.getStyle().set(MARGIN_PROP, "0 0 0 24px");

            faqItem.add(questionRow, answer);
            faqContent.add(faqItem);
        }

        // Footer
        HorizontalLayout footer = createStandardModalFooter(dialog);

        content.add(header, faqContent, footer);
        dialog.add(content);

        activeModal = dialog;
        dialog.open();
    }

    private void openSamplesModal() {
        if (activeModal != null && activeModal.isOpened()) {
            activeModal.close();
        }

        Dialog dialog = setupDialog("640px", "80vh");
        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);

        HorizontalLayout header = createModalHeader(dialog, VaadinIcon.FILE_TEXT, 
            translationService.translate("generator.title"), 
            translationService.translate("landing.seeWhatAiCanGenerate"), STANDARD_HEADER_STYLE);

        // Samples Grid - 2 columns like Figma
        Div samplesGrid = new Div();
        samplesGrid.getStyle().set(DISPLAY_PROP, "grid");
        samplesGrid.getStyle().set("grid-template-columns", "repeat(2, 1fr)");
        samplesGrid.getStyle().set(GAP_PROP, "16px");
        samplesGrid.getStyle().set(PADDING_PROP, "32px");

        // Sample data with cover letter content
        record Sample(String title, String company, String match, String letter) {
        }

        List<Sample> samples = List.of(new Sample("Senior Product Manager", "Tech Giant", "98%", "Dear Hiring Manager,"
                + System.lineSeparator() + System.lineSeparator()
                + "I am writing to express my strong interest in the Senior Product Manager position at Tech Giant. With over 8 years of experience driving product strategy and leading cross-functional teams, I am excited about the opportunity to contribute to your innovative platform."
                + System.lineSeparator() + System.lineSeparator()
                + "At my current role, I have successfully launched three major product features that increased user engagement by 45% and revenue by $2M annually. My experience in Agile methodologies, user research, and data-driven decision making aligns perfectly with Tech Giant's mission to create world-class products."
                + System.lineSeparator() + System.lineSeparator()
                + "I am particularly drawn to Tech Giant's commitment to innovation and user-centric design. I would welcome the opportunity to discuss how my background in product management and passion for technology can contribute to your team's continued success."
                + System.lineSeparator() + System.lineSeparator() + "Thank you for your time and consideration."
                + System.lineSeparator() + System.lineSeparator() + "Sincerely," + System.lineSeparator()
                + YOUR_NAME_PLACEHOLDER),

                new Sample("Junior React Developer", "Modern Startup", "95%", "Dear Hiring Team,"
                        + System.lineSeparator() + System.lineSeparator()
                        + "I am thrilled to apply for the Junior React Developer position at Modern Startup. As a recent graduate with a Computer Science degree and hands-on experience building responsive web applications, I am eager to contribute to your cutting-edge projects."
                        + System.lineSeparator() + System.lineSeparator()
                        + "During my internship at XYZ Company, I developed React components that improved page load times by 30% and implemented Redux for state management in a production application. My portfolio includes several personal projects utilizing React, TypeScript, and modern CSS frameworks."
                        + System.lineSeparator() + System.lineSeparator()
                        + "Modern Startup's focus on innovation and growth mindset resonates with my career goals. I am excited about the opportunity to learn from experienced developers while contributing fresh perspectives and strong problem-solving skills."
                        + System.lineSeparator() + System.lineSeparator()
                        + "I would love to discuss how my technical skills and enthusiasm can add value to your development team."
                        + System.lineSeparator() + System.lineSeparator() + "Best regards," + System.lineSeparator()
                        + YOUR_NAME_PLACEHOLDER),

                new Sample("Creative Director", "Design Agency", "92%", "Dear Creative Team," + System.lineSeparator()
                        + System.lineSeparator()
                        + "I am excited to apply for the Creative Director position at Design Agency. With 10+ years of experience leading creative teams and delivering award-winning campaigns, I bring a unique blend of artistic vision and strategic thinking to every project."
                        + System.lineSeparator() + System.lineSeparator()
                        + "In my current role, I have led rebranding initiatives for Fortune 500 clients, resulting in a 60% increase in brand recognition. My expertise spans brand strategy, visual design, motion graphics, and team mentorship. I am proficient in Adobe Creative Suite, Figma, and emerging AI design tools."
                        + System.lineSeparator() + System.lineSeparator()
                        + "Design Agency's reputation for pushing creative boundaries and delivering exceptional work aligns perfectly with my professional values. I am eager to bring my leadership experience and creative expertise to elevate your client portfolio."
                        + System.lineSeparator() + System.lineSeparator() + "Thank you for considering my application."
                        + System.lineSeparator() + System.lineSeparator() + "Warm regards," + System.lineSeparator()
                        + YOUR_NAME_PLACEHOLDER),

                new Sample("Customer Success Lead", "SaaS Corp", "96%", "Dear Hiring Manager," + System.lineSeparator()
                        + System.lineSeparator()
                        + "I am writing to express my interest in the Customer Success Lead position at SaaS Corp. With 6 years of experience in customer-facing roles and a proven track record of reducing churn by 25%, I am confident in my ability to drive customer satisfaction and retention."
                        + System.lineSeparator() + System.lineSeparator()
                        + "In my previous role, I managed a portfolio of 50+ enterprise accounts and implemented a proactive engagement strategy that increased NPS scores by 35 points. My experience with CRM platforms, data analysis, and team leadership has prepared me to excel in this role."
                        + System.lineSeparator() + System.lineSeparator()
                        + "SaaS Corp's commitment to customer-centric innovation is inspiring. I am excited about the opportunity to build and lead a high-performing customer success team that drives growth and loyalty."
                        + System.lineSeparator() + System.lineSeparator()
                        + "I look forward to discussing how my experience aligns with your team's goals."
                        + System.lineSeparator() + System.lineSeparator() + "Best regards," + System.lineSeparator()
                        + YOUR_NAME_PLACEHOLDER));

        for (Sample sample : samples) {
            Div sampleCard = new Div();
            sampleCard.getStyle().set(PADDING_PROP, "16px");
            sampleCard.getStyle().set(BORDER_PROP, BORDER_SUBTLE);
            sampleCard.getStyle().set(BORDER_RADIUS_PROP, "16px");
            sampleCard.getStyle().set(BACKGROUND_PROP, BG_WHITE);
            sampleCard.getStyle().set(CURSOR_PROP, POINTER_CURSOR);
            sampleCard.getStyle().set(TRANSITION_PROP, "all 0.3s");

            HorizontalLayout cardContent = new HorizontalLayout();
            cardContent.setAlignItems(FlexComponent.Alignment.START);

            Div fileIcon = new Div();
            fileIcon.getStyle().set(COLOR_PROP, TEXT_SECONDARY);
            fileIcon.getStyle().set(TRANSITION_PROP, "color 0.3s");
            fileIcon.add(VaadinIcon.FILE_TEXT.create());

            VerticalLayout textGroup = new VerticalLayout();
            textGroup.setPadding(false);
            textGroup.setSpacing(false);
            textGroup.getStyle().set(GAP_PROP, "4px");
            textGroup.setWidthFull();

            H4 jobTitle = new H4(sample.title());
            jobTitle.getStyle().set(FONT_SIZE_PROP, "14px");
            jobTitle.getStyle().set(FONT_WEIGHT_PROP, FONT_WEIGHT_BOLD);
            jobTitle.getStyle().set(COLOR_PROP, TEXT_PRIMARY);
            jobTitle.getStyle().set(MARGIN_PROP, ZERO_VALUE);

            Paragraph company = new Paragraph(sample.company());
            company.getStyle().set(FONT_SIZE_PROP, "12px");
            company.getStyle().set(COLOR_PROP, TEXT_SECONDARY);
            company.getStyle().set(MARGIN_PROP, ZERO_VALUE);

            textGroup.add(jobTitle, company);

            Span matchBadge = new Span(sample.match() + " " + translationService.translate("landing.match"));
            matchBadge.getStyle().set(FONT_SIZE_PROP, "11px");
            matchBadge.getStyle().set(FONT_WEIGHT_PROP, "900");
            matchBadge.getStyle().set(COLOR_PROP, PRIMARY);
            matchBadge.getStyle().set("white-space", "nowrap");

            cardContent.add(fileIcon, textGroup, matchBadge);
            cardContent.setWidthFull();
            cardContent.getStyle().set(GAP_PROP, "12px");

            sampleCard.add(cardContent);

            // Click listener to open cover letter
            sampleCard.addClickListener(e -> openCoverLetterModal(sample.title(), sample.company(), sample.letter()));

            // Hover effects
            sampleCard.getElement().addEventListener(EVENT_MOUSEENTER, e -> {
                sampleCard.getStyle().set(BACKGROUND_PROP, "rgba(0,122,255,0.05)");
                sampleCard.getStyle().set(BORDER_COLOR_PROP, "rgba(0,122,255,0.2)");
                fileIcon.getStyle().set(COLOR_PROP, PRIMARY);
            });

            sampleCard.getElement().addEventListener(EVENT_MOUSELEAVE, e -> {
                sampleCard.getStyle().set(BACKGROUND_PROP, BG_WHITE);
                sampleCard.getStyle().set(BORDER_COLOR_PROP, BG_BLACK_ALPHA_05);
                fileIcon.getStyle().set(COLOR_PROP, TEXT_SECONDARY);
            });

            samplesGrid.add(sampleCard);
        }

        // Wrap in scrollable container
        Div scrollContainer = new Div();
        scrollContainer.getStyle().set("overflow-y", "auto");
        scrollContainer.getStyle().set("max-height", "50vh");
        scrollContainer.add(samplesGrid);

        // Footer
        HorizontalLayout footer = createStandardModalFooter(dialog);

        content.add(header, scrollContainer, footer);
        dialog.add(content);

        activeModal = dialog;
        dialog.open();
    }

    private void openHowItWorksModal() {
        if (activeModal != null && activeModal.isOpened()) {
            activeModal.close();
        }

        Dialog dialog = setupDialog("560px", "80vh");
        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);

        HorizontalLayout header = createModalHeader(dialog, VaadinIcon.LIGHTBULB, 
            translationService.translate("help.howItWorks"), 
            translationService.translate("landing.simpleSteps"), STANDARD_HEADER_STYLE);

        // Steps Content
        VerticalLayout stepsContent = new VerticalLayout();
        stepsContent.setPadding(true);
        stepsContent.getStyle().set(PADDING_PROP, "32px");
        stepsContent.getStyle().set(GAP_PROP, "24px");

        // Step 1
        HorizontalLayout step1 = createStepItem("1", translationService.translate("landing.uploadYourResume"),
                translationService.translate("landing.uploadYourResumeDesc"));

        // Step 2
        HorizontalLayout step2 = createStepItem("2", translationService.translate("landing.pasteJobDescription"),
                translationService.translate("landing.pasteJobDescriptionDesc"));

        // Step 3
        HorizontalLayout step3 = createStepItem("3", translationService.translate("landing.generateDownload"),
                translationService.translate("landing.generateDownloadDesc"));

        stepsContent.add(step1, step2, step3);

        // Footer
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        footer.setPadding(true);
        footer.getStyle().set(PADDING_PROP, "24px");
        footer.getStyle().set(BORDER_TOP_PROP, BORDER_SUBTLE);
        footer.getStyle().set(BACKGROUND_PROP, BG_LIGHT);

        Button ctaBtn = createPrimaryButton(translationService.translate("landing.getStarted"), () -> {
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
        stepRow.getStyle().set(GAP_PROP, "16px");
        stepRow.setAlignItems(FlexComponent.Alignment.START);

        // Step number circle
        Div numberCircle = new Div();
        numberCircle.getStyle().set(WIDTH_PROP, "32px");
        numberCircle.getStyle().set(HEIGHT_PROP, "32px");
        numberCircle.getStyle().set(BORDER_RADIUS_PROP, "50%");
        numberCircle.getStyle().set(BACKGROUND_PROP, PRIMARY);
        numberCircle.getStyle().set(COLOR_PROP, WHITE_COLOR);
        numberCircle.getStyle().set(DISPLAY_PROP, DISPLAY_FLEX);
        numberCircle.getStyle().set(ALIGN_ITEMS_PROP, CENTER_ALIGN);
        numberCircle.getStyle().set(JUSTIFY_CONTENT_PROP, CENTER_ALIGN);
        numberCircle.getStyle().set(FONT_WEIGHT_PROP, FONT_WEIGHT_BOLD);
        numberCircle.getStyle().set(FONT_SIZE_PROP, "14px");
        numberCircle.getStyle().set("flex-shrink", ZERO_VALUE);
        numberCircle.add(new Span(number));

        // Content
        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);
        content.getStyle().set(GAP_PROP, "4px");

        H4 stepTitle = new H4(title);
        stepTitle.getStyle().set(FONT_SIZE_PROP, "16px");
        stepTitle.getStyle().set(FONT_WEIGHT_PROP, FONT_WEIGHT_BOLD);
        stepTitle.getStyle().set(COLOR_PROP, TEXT_PRIMARY);
        stepTitle.getStyle().set(MARGIN_PROP, ZERO_VALUE);

        Paragraph stepDesc = new Paragraph(description);
        stepDesc.getStyle().set(FONT_SIZE_PROP, "14px");
        stepDesc.getStyle().set(COLOR_PROP, TEXT_SECONDARY);
        stepDesc.getStyle().set(LINE_HEIGHT_PROP, "1.5");
        stepDesc.getStyle().set(MARGIN_PROP, ZERO_VALUE);

        content.add(stepTitle, stepDesc);
        stepRow.add(numberCircle, content);
        stepRow.expand(content);

        return stepRow;
    }

    private void openCoverLetterModal(String jobTitle, String company, String letterContent) {
        Dialog dialog = setupDialog("720px", "90vh");
        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);

        HorizontalLayout header = createModalHeader(dialog, VaadinIcon.FILE_TEXT_O, 
            jobTitle, company, CV_HEADER_STYLE);

        // Letter Content
        Div letterContainer = new Div();
        letterContainer.getStyle().set(PADDING_PROP, "32px");
        letterContainer.getStyle().set(BACKGROUND_PROP, BG_WHITE);
        letterContainer.getStyle().set("overflow-y", "auto");
        letterContainer.getStyle().set("max-height", "60vh");

        // Format letter with proper styling
        Div letterText = new Div();
        letterText.getStyle().set("font-family", "Georgia, 'Times New Roman', serif");
        letterText.getStyle().set(FONT_SIZE_PROP, "15px");
        letterText.getStyle().set(LINE_HEIGHT_PROP, "1.8");
        letterText.getStyle().set(COLOR_PROP, TEXT_PRIMARY);
        letterText.getStyle().set("white-space", "pre-wrap");
        letterText.setText(letterContent);

        letterContainer.add(letterText);

        // Footer with copy button
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        footer.setPadding(true);
        footer.getStyle().set(PADDING_PROP, "16px 24px");
        footer.getStyle().set(BORDER_TOP_PROP, BORDER_SUBTLE);
        footer.getStyle().set(BACKGROUND_PROP, BG_LIGHT);
        footer.getStyle().set(GAP_PROP, "12px");

        Button copyBtn = new Button(translationService.translate("landing.copyToClipboard"), VaadinIcon.COPY.create());
        copyBtn.getStyle().set(BACKGROUND_PROP, BG_BLACK_ALPHA_05);
        copyBtn.getStyle().set(COLOR_PROP, TEXT_PRIMARY);
        copyBtn.getStyle().set(FONT_WEIGHT_PROP, FONT_WEIGHT_SEMIBOLD);
        copyBtn.getStyle().set(BORDER_RADIUS_PROP, FULLY_ROUNDED);
        copyBtn.getStyle().set(PADDING_PROP, "10px 20px");
        copyBtn.getStyle().set(CURSOR_PROP, POINTER_CURSOR);
        copyBtn.addClickListener(e -> {
            copyBtn.getElement().executeJs("navigator.clipboard.writeText($0)", letterContent);
            copyBtn.setText(translationService.translate("landing.copied"));
            copyBtn.setIcon(VaadinIcon.CHECK.create());
        });

        Button useTemplateBtn = createPrimaryButton(translationService.translate("landing.useTemplate"), () -> {
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
        footer.getStyle().set(PADDING_PROP, "32px");
        footer.getStyle().set(BORDER_TOP_PROP, BORDER_SUBTLE);

        Paragraph copyright = new Paragraph(translationService.translate("landing.copyright"));
        copyright.getStyle().set(FONT_SIZE_PROP, "13px");
        copyright.getStyle().set(COLOR_PROP, TEXT_SECONDARY);
        copyright.getStyle().set(TEXT_ALIGN_PROP, CENTER_ALIGN);

        footer.add(copyright);
        return footer;
    }

    // ============ HELPER METHODS ============
    private Button createPrimaryButton(String text, Runnable action) {
        Button btn = new Button(text, e -> action.run());
        btn.getStyle().set(BACKGROUND_PROP, "linear-gradient(135deg, " + PRIMARY + " 0%, " + PRIMARY_LIGHT + " 100%)");
        btn.getStyle().set(COLOR_PROP, WHITE_COLOR);
        btn.getStyle().set(FONT_WEIGHT_PROP, FONT_WEIGHT_SEMIBOLD);
        btn.getStyle().set(BORDER_RADIUS_PROP, FULLY_ROUNDED);
        btn.getStyle().set(BORDER_PROP, "none");
        btn.getStyle().set(BOX_SHADOW_PROP, "0 10px 15px -3px rgba(0,122,255,0.3)");
        btn.getStyle().set(TRANSITION_PROP, TRANSITION_ALL_FAST);
        btn.getStyle().set(CURSOR_PROP, POINTER_CURSOR);

        btn.getElement().addEventListener(EVENT_MOUSEENTER, e -> {
            btn.getStyle().set(FILTER_PROP, "brightness(1.1)");
            btn.getStyle().set(TRANSFORM_PROP, "translateY(-1px)");
        });

        btn.getElement().addEventListener(EVENT_MOUSELEAVE, e -> {
            btn.getStyle().set(FILTER_PROP, "brightness(1)");
            btn.getStyle().set(TRANSFORM_PROP, "translateY(0)");
        });

        return btn;
    }

    private Button createSecondaryButton(String text, Runnable action) {
        Button btn = new Button(text, e -> action.run());
        btn.getStyle().set(BACKGROUND_PROP, BG_BLACK_ALPHA_05);
        btn.getStyle().set(COLOR_PROP, TEXT_PRIMARY);
        btn.getStyle().set(FONT_WEIGHT_PROP, FONT_WEIGHT_SEMIBOLD);
        btn.getStyle().set(BORDER_RADIUS_PROP, FULLY_ROUNDED);
        btn.getStyle().set(BORDER_PROP, BORDER_SUBTLE);
        btn.getStyle().set(TRANSITION_PROP, TRANSITION_ALL_FAST);
        btn.getStyle().set(CURSOR_PROP, POINTER_CURSOR);

        btn.getElement().addEventListener(EVENT_MOUSEENTER, e -> btn.getStyle().set(BACKGROUND_PROP, "rgba(0,0,0,0.1)"));

        btn.getElement().addEventListener(EVENT_MOUSELEAVE, e -> btn.getStyle().set(BACKGROUND_PROP, BG_BLACK_ALPHA_05));

        return btn;
    }


    private record HeaderStyle(String padding, String iconSize, String iconRadius, String titleSize, String subtitleSize, String subtitleMargin) {}
    private static final HeaderStyle STANDARD_HEADER_STYLE = new HeaderStyle("24px", "40px", "12px", "20px", "13px", MARGIN_TOP_SMALL);
    private static final HeaderStyle CV_HEADER_STYLE = new HeaderStyle("20px 24px", "36px", "10px", "16px", "12px", "2px 0 0");

private Dialog setupDialog(String width, String maxHeight) {
        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setWidth(width);
        dialog.setMaxHeight(maxHeight);
        return dialog;
    }

    private HorizontalLayout createModalHeader(Dialog dialog, VaadinIcon iconType, String titleText, String subtitleText, HeaderStyle style) {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setPadding(true);
        header.getStyle().set(PADDING_PROP, style.padding());
        header.getStyle().set(BORDER_BOTTOM_PROP, BORDER_SUBTLE);
        header.getStyle().set(BACKGROUND_PROP, BG_LIGHT);

        Div iconContainer = new Div();
        iconContainer.getStyle().set(WIDTH_PROP, style.iconSize());
        iconContainer.getStyle().set(HEIGHT_PROP, style.iconSize());
        iconContainer.getStyle().set(BORDER_RADIUS_PROP, style.iconRadius());
        iconContainer.getStyle().set(BACKGROUND_PROP, BG_PRIMARY_ALPHA_10);
        iconContainer.getStyle().set(DISPLAY_PROP, DISPLAY_FLEX);
        iconContainer.getStyle().set(ALIGN_ITEMS_PROP, CENTER_ALIGN);
        iconContainer.getStyle().set(JUSTIFY_CONTENT_PROP, CENTER_ALIGN);
        iconContainer.add(iconType.create());

        VerticalLayout titleGroup = new VerticalLayout();
        titleGroup.setPadding(false);
        titleGroup.setSpacing(false);
        H3 title = new H3(titleText);
        title.getStyle().set(FONT_SIZE_PROP, style.titleSize());
        title.getStyle().set(FONT_WEIGHT_PROP, FONT_WEIGHT_BOLD);
        title.getStyle().set(MARGIN_PROP, ZERO_VALUE);
        Paragraph subtitle = new Paragraph(subtitleText);
        subtitle.getStyle().set(FONT_SIZE_PROP, style.subtitleSize());
        subtitle.getStyle().set(COLOR_PROP, TEXT_SECONDARY);
        subtitle.getStyle().set(MARGIN_PROP, style.subtitleMargin());
        titleGroup.add(title, subtitle);

        Button closeBtn = new Button(VaadinIcon.CLOSE.create(), e -> dialog.close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        closeBtn.getStyle().set(COLOR_PROP, TEXT_SECONDARY);

        header.add(iconContainer, titleGroup, closeBtn);
        header.expand(titleGroup);

        return header;
    }

    private HorizontalLayout createStandardModalFooter(Dialog dialog) {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        footer.setPadding(true);
        footer.getStyle().set(PADDING_PROP, "24px");
        footer.getStyle().set(BORDER_TOP_PROP, BORDER_SUBTLE);
        footer.getStyle().set(BACKGROUND_PROP, BG_LIGHT);

        Button ctaBtn = createPrimaryButton(translationService.translate("landing.tryItForFree"), () -> {
            dialog.close();
            getUI().ifPresent(ui -> ui.navigate(LoginView.class));
        });
        footer.add(ctaBtn);
        return footer;
    }    private void scrollToTop() {
        getElement().executeJs("window.scrollTo({top: 0, behavior: 'smooth'})");
    }
}
