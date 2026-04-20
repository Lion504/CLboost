package com.clbooster.app.views;

import com.clbooster.app.views.util.StyleConstants;

import jakarta.annotation.security.PermitAll;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.clbooster.app.i18n.TranslationService;

import java.util.List;

/**
 * Help View - Help center with search, FAQs, and support cards Following Apple
 * Design System
 */
@Route(value = "help", layout = MainLayout.class)
@PageTitle("Help Center | CL Booster")
@PermitAll
public class HelpView extends VerticalLayout {

    // Figma Design System Colors
    private static final String PRIMARY = "#007AFF";
    private static final String TEXT_PRIMARY = "#1d1d1f";
    private static final String TEXT_SECONDARY = "#86868b";
    private static final String BG_WHITE = "#ffffff";
    private static final String BG_GRAY = "#f5f5f7";

    private final TranslationService translationService;
    private VerticalLayout faqContainer;

    public HelpView() {
        this.translationService = new TranslationService();
        setSizeFull();
        setPadding(true);
        getStyle().set("gap", "40px");
        getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        getStyle().set(StyleConstants.CSS_PADDING, "32px");
        getStyle().set(StyleConstants.CSS_MAX_WIDTH, "900px");
        getStyle().set(StyleConstants.CSS_MARGIN, "0 auto");

        // Hero section with search
        VerticalLayout heroSection = createHeroSection();

        // Quick links
        HorizontalLayout quickLinks = createQuickLinks();

        // FAQ section
        VerticalLayout faqSection = createFAQSection();

        // Support cards
        HorizontalLayout supportCards = createSupportCards();

        add(heroSection, quickLinks, faqSection, supportCards);
    }

    private VerticalLayout createHeroSection() {
        VerticalLayout hero = new VerticalLayout();
        hero.setPadding(false);
        hero.setSpacing(false);
        hero.getStyle().set("gap", "24px");
        hero.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        hero.getStyle().set(StyleConstants.CSS_TEXT_ALIGN, StyleConstants.VAL_CENTER);
        hero.getStyle().set(StyleConstants.CSS_PADDING, "40px 0");

        // Icon
        Div iconContainer = new Div();
        iconContainer.getStyle().set(StyleConstants.CSS_WIDTH, "64px");
        iconContainer.getStyle().set(StyleConstants.CSS_HEIGHT, "64px");
        iconContainer.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_GRAY);
        iconContainer.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "20px");
        iconContainer.getStyle().set(StyleConstants.CSS_DISPLAY, "flex");
        iconContainer.getStyle().set(StyleConstants.CSS_ALIGN_ITEMS, StyleConstants.VAL_CENTER);
        iconContainer.getStyle().set(StyleConstants.CSS_JUSTIFY_CONTENT, StyleConstants.VAL_CENTER);
        iconContainer.getStyle().set(StyleConstants.CSS_MARGIN, "0 auto");

        Icon helpIcon = VaadinIcon.QUESTION_CIRCLE.create();
        helpIcon.getStyle().set(StyleConstants.CSS_COLOR, PRIMARY);
        helpIcon.getStyle().set(StyleConstants.CSS_WIDTH, "32px");
        helpIcon.getStyle().set(StyleConstants.CSS_HEIGHT, "32px");
        iconContainer.add(helpIcon);

        // Title
        H1 title = new H1(translationService.translate("help.howCanWeHelp"));
        title.getStyle().set(StyleConstants.CSS_FONT_SIZE, "40px");
        title.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        title.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        title.getStyle().set(StyleConstants.CSS_MARGIN, "0");
        title.getStyle().set(StyleConstants.CSS_LETTER_SPACING, "-0.025em");

        // Subtitle
        Paragraph subtitle = new Paragraph(translationService.translate("help.searchKnowledgeBase"));
        subtitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "17px");
        subtitle.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        subtitle.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        // Search bar
        TextField searchField = new TextField();
        searchField.setPlaceholder(translationService.translate("help.searchPlaceholder"));
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setWidthFull();
        searchField.setMaxWidth("500px");
        searchField.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_GRAY);
        searchField.getStyle().set(StyleConstants.CSS_BORDER, "none");
        searchField.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX);
        searchField.getStyle().set(StyleConstants.CSS_PADDING, "16px 24px");
        searchField.getStyle().set(StyleConstants.CSS_FONT_SIZE, "16px");

        searchField.addValueChangeListener(e -> filterFAQs(e.getValue()));

        hero.add(iconContainer, title, subtitle, searchField);

        return hero;
    }

    private HorizontalLayout createQuickLinks() {
        HorizontalLayout links = new HorizontalLayout();
        links.setWidthFull();
        links.getStyle().set("gap", "16px");
        links.getStyle().set(StyleConstants.CSS_JUSTIFY_CONTENT, StyleConstants.VAL_CENTER);
        links.getStyle().set(StyleConstants.CSS_FLEX_WRAP, "wrap");

        String[][] linkData = { { translationService.translate("help.gettingStarted"), VaadinIcon.ROCKET.name() },
                { translationService.translate("help.accountBilling"), VaadinIcon.USER_CARD.name() },
                { translationService.translate("help.aiFeatures"), VaadinIcon.MAGIC.name() },
                { translationService.translate("help.exportShare"), VaadinIcon.DOWNLOAD.name() } };

        for (String[] data : linkData) {
            Button linkBtn = createQuickLinkButton(data[0], VaadinIcon.valueOf(data[1]));
            links.add(linkBtn);
        }

        return links;
    }

    private Button createQuickLinkButton(String text, VaadinIcon icon) {
        Button btn = new Button(text, icon.create());
        btn.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        btn.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        btn.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "600");
        btn.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX);
        btn.getStyle().set(StyleConstants.CSS_BORDER, "1px solid rgba(0,0,0,0.1)");
        btn.getStyle().set(StyleConstants.CSS_PADDING, "12px 24px");
        btn.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);
        btn.getStyle().set(StyleConstants.CSS_TRANSITION, StyleConstants.VAL_ALL_0_2S);

        btn.getElement().addEventListener(StyleConstants.VAL_MOUSEENTER, e -> {
            btn.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_GRAY);
            btn.getStyle().set(StyleConstants.CSS_BORDER_COLOR, "rgba(0,0,0,0.15)");
        });

        btn.getElement().addEventListener(StyleConstants.VAL_MOUSELEAVE, e -> {
            btn.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
            btn.getStyle().set(StyleConstants.CSS_BORDER_COLOR, "rgba(0,0,0,0.1)");
        });

        btn.addClickListener(e -> Notification.show(translationService.translate("help.opening", text), 3000,
                Notification.Position.TOP_CENTER));

        return btn;
    }

    private VerticalLayout createFAQSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(false);
        section.getStyle().set("gap", "20px");
        section.setWidthFull();

        H2 title = new H2(translationService.translate("help.faqTitle"));
        title.getStyle().set(StyleConstants.CSS_FONT_SIZE, "24px");
        title.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        title.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        title.getStyle().set(StyleConstants.CSS_MARGIN, "0 0 8px 0");

        faqContainer = new VerticalLayout();
        faqContainer.setPadding(false);
        faqContainer.setSpacing(false);
        faqContainer.getStyle().set("gap", "12px");

        // FAQ items
        List<String[]> faqs = List.of(
                new String[] { translationService.translate("help.isCLBoosterFree"),
                        translationService.translate("help.isCLBoosterFreeAnswer") },
                new String[] { translationService.translate("help.howAIGenerate"),
                        translationService.translate("help.howAIGenerateAnswer") },
                new String[] { translationService.translate("help.canIEdit"),
                        translationService.translate("help.canIEditAnswer") },
                new String[] { translationService.translate("help.whatFormats"),
                        translationService.translate("help.whatFormatsAnswer") },
                new String[] { translationService.translate("help.isDataSecure"),
                        translationService.translate("help.isDataSecurityAnswer") },
                new String[] { translationService.translate("help.howImproveMatch"),
                        translationService.translate("help.howImproveMatchAnswer") });

        for (String[] faq : faqs) {
            faqContainer.add(createFAQItem(faq[0], faq[1]));
        }

        section.add(title, faqContainer);

        return section;
    }

    private Div createFAQItem(String question, String answer) {
        Div item = new Div();
        item.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_GRAY);
        item.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "16px");
        item.getStyle().set(StyleConstants.CSS_PADDING, "24px");
        item.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);
        item.getStyle().set(StyleConstants.CSS_TRANSITION, StyleConstants.VAL_ALL_0_3S);

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        H3 questionText = new H3(question);
        questionText.getStyle().set(StyleConstants.CSS_FONT_SIZE, "16px");
        questionText.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "600");
        questionText.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        questionText.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        Icon chevron = VaadinIcon.CHEVRON_DOWN.create();
        chevron.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        chevron.getStyle().set(StyleConstants.CSS_TRANSITION, "transform 0.3s");

        header.add(questionText, chevron);
        header.expand(questionText);

        Paragraph answerText = new Paragraph(answer);
        answerText.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        answerText.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        answerText.getStyle().set("line-height", "1.6");
        answerText.getStyle().set(StyleConstants.CSS_MARGIN, "16px 0 0 0");
        answerText.getStyle().set(StyleConstants.CSS_MAX_HEIGHT, "0");
        answerText.getStyle().set(StyleConstants.CSS_OVERFLOW, "hidden");
        answerText.getStyle().set(StyleConstants.CSS_OPACITY, "0");
        answerText.getStyle().set(StyleConstants.CSS_TRANSITION, StyleConstants.VAL_ALL_0_3S);

        item.add(header, answerText);

        // Toggle functionality
        final boolean[] isExpanded = { false };
        item.addClickListener(e -> {
            isExpanded[0] = !isExpanded[0];
            if (isExpanded[0]) {
                answerText.getStyle().set(StyleConstants.CSS_MAX_HEIGHT, "500px");
                answerText.getStyle().set(StyleConstants.CSS_OPACITY, "1");
                chevron.getStyle().set(StyleConstants.CSS_TRANSFORM, "rotate(180deg)");
                item.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
                item.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 4px 20px rgba(0,0,0,0.08)");
            } else {
                answerText.getStyle().set(StyleConstants.CSS_MAX_HEIGHT, "0");
                answerText.getStyle().set(StyleConstants.CSS_OPACITY, "0");
                chevron.getStyle().set(StyleConstants.CSS_TRANSFORM, "rotate(0deg)");
                item.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_GRAY);
                item.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "none");
            }
        });

        // Hover effect
        item.getElement().addEventListener(StyleConstants.VAL_MOUSEENTER, e -> {
            if (!isExpanded[0]) {
                item.getStyle().set(StyleConstants.CSS_BACKGROUND, "rgba(0,0,0,0.04)");
            }
        });

        item.getElement().addEventListener(StyleConstants.VAL_MOUSELEAVE, e -> {
            if (!isExpanded[0]) {
                item.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_GRAY);
            }
        });

        return item;
    }

    private HorizontalLayout createSupportCards() {
        HorizontalLayout cards = new HorizontalLayout();
        cards.setWidthFull();
        cards.getStyle().set("gap", "20px");
        cards.getStyle().set(StyleConstants.CSS_MARGIN_TOP, "20px");

        // Contact support card
        Div contactCard = createSupportCard(translationService.translate("help.contactSupport"),
                translationService.translate("help.getInTouch"), VaadinIcon.ENVELOPE, PRIMARY,
                translationService.translate("help.emailUs"));

        // Community card
        Div communityCard = createSupportCard(translationService.translate("help.community"),
                translationService.translate("help.joinDiscussions"), VaadinIcon.USERS, "#AF52DE",
                translationService.translate("help.joinCommunity"));

        // Documentation card
        Div docsCard = createSupportCard(translationService.translate("help.documentation"),
                translationService.translate("help.readGuides"), VaadinIcon.BOOK, "#34C759",
                translationService.translate("help.viewDocs"));

        cards.add(contactCard, communityCard, docsCard);

        return cards;
    }

    private Div createSupportCard(String title, String description, VaadinIcon iconType, String iconColor,
            String actionText) {
        Div card = new Div();
        card.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        card.getStyle().set(StyleConstants.CSS_BORDER, "1px solid rgba(0,0,0,0.05)");
        card.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "24px");
        card.getStyle().set(StyleConstants.CSS_PADDING, "24px");
        card.getStyle().set("flex", "1");
        card.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);
        card.getStyle().set(StyleConstants.CSS_TRANSITION, StyleConstants.VAL_ALL_0_3S);

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);
        content.getStyle().set("gap", "16px");
        content.setDefaultHorizontalComponentAlignment(Alignment.START);

        // Icon
        Div iconContainer = new Div();
        iconContainer.getStyle().set(StyleConstants.CSS_WIDTH, "48px");
        iconContainer.getStyle().set(StyleConstants.CSS_HEIGHT, "48px");
        iconContainer.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "14px");
        iconContainer.getStyle().set(StyleConstants.CSS_BACKGROUND, iconColor + "15");
        iconContainer.getStyle().set(StyleConstants.CSS_DISPLAY, "flex");
        iconContainer.getStyle().set(StyleConstants.CSS_ALIGN_ITEMS, StyleConstants.VAL_CENTER);
        iconContainer.getStyle().set(StyleConstants.CSS_JUSTIFY_CONTENT, StyleConstants.VAL_CENTER);

        Icon icon = iconType.create();
        icon.getStyle().set(StyleConstants.CSS_COLOR, iconColor);
        icon.getStyle().set(StyleConstants.CSS_WIDTH, "24px");
        icon.getStyle().set(StyleConstants.CSS_HEIGHT, "24px");
        iconContainer.add(icon);

        // Text
        VerticalLayout textGroup = new VerticalLayout();
        textGroup.setPadding(false);
        textGroup.setSpacing(false);
        textGroup.getStyle().set("gap", "4px");

        H3 cardTitle = new H3(title);
        cardTitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "16px");
        cardTitle.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        cardTitle.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        cardTitle.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        Paragraph cardDesc = new Paragraph(description);
        cardDesc.getStyle().set(StyleConstants.CSS_FONT_SIZE, "13px");
        cardDesc.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        cardDesc.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        textGroup.add(cardTitle, cardDesc);

        // Action link
        Span actionLink = new Span(actionText);
        actionLink.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        actionLink.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "600");
        actionLink.getStyle().set(StyleConstants.CSS_COLOR, iconColor);
        actionLink.getStyle().set(StyleConstants.CSS_MARGIN_TOP, "8px");

        content.add(iconContainer, textGroup, actionLink);
        card.add(content);

        // Hover effects
        card.getElement().addEventListener(StyleConstants.VAL_MOUSEENTER, e -> {
            card.getStyle().set(StyleConstants.CSS_TRANSFORM, "translateY(-4px)");
            card.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 20px 40px rgba(0,0,0,0.1)");
            iconContainer.getStyle().set(StyleConstants.CSS_TRANSFORM, "scale(1.1)");
        });

        card.getElement().addEventListener(StyleConstants.VAL_MOUSELEAVE, e -> {
            card.getStyle().set(StyleConstants.CSS_TRANSFORM, "translateY(0)");
            card.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "none");
            iconContainer.getStyle().set(StyleConstants.CSS_TRANSFORM, "scale(1)");
        });

        card.addClickListener(e -> Notification.show(translationService.translate("help.opening", title), 3000,
                Notification.Position.TOP_CENTER));

        return card;
    }

    private void filterFAQs(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return;
        }
        Notification.show(translationService.translate("help.searching") + ": " + searchTerm, 2000,
                Notification.Position.TOP_CENTER);
    }
}
