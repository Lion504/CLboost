package com.clbooster.app.views;

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

import java.util.List;

/**
 * Help View - Help center with search, FAQs, and support cards
 * Following Apple Design System
 */
@Route(value = "help", layout = MainLayout.class)
@PageTitle("Help Center | CL Booster")
public class HelpView extends VerticalLayout {

    // Figma Design System Colors
    private static final String PRIMARY = "#007AFF";
    private static final String TEXT_PRIMARY = "#1d1d1f";
    private static final String TEXT_SECONDARY = "#86868b";
    private static final String BG_WHITE = "#ffffff";
    private static final String BG_GRAY = "#f5f5f7";

    private VerticalLayout faqContainer;

    public HelpView() {
        setSizeFull();
        setPadding(true);
        getStyle().set("gap", "40px");
        getStyle().set("background", BG_WHITE);
        getStyle().set("padding", "32px");
        getStyle().set("max-width", "900px");
        getStyle().set("margin", "0 auto");

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
        hero.getStyle().set("text-align", "center");
        hero.getStyle().set("padding", "40px 0");

        // Icon
        Div iconContainer = new Div();
        iconContainer.getStyle().set("width", "64px");
        iconContainer.getStyle().set("height", "64px");
        iconContainer.getStyle().set("background", BG_GRAY);
        iconContainer.getStyle().set("border-radius", "20px");
        iconContainer.getStyle().set("display", "flex");
        iconContainer.getStyle().set("align-items", "center");
        iconContainer.getStyle().set("justify-content", "center");
        iconContainer.getStyle().set("margin", "0 auto");

        Icon helpIcon = VaadinIcon.QUESTION_CIRCLE.create();
        helpIcon.getStyle().set("color", PRIMARY);
        helpIcon.getStyle().set("width", "32px");
        helpIcon.getStyle().set("height", "32px");
        iconContainer.add(helpIcon);

        // Title
        H1 title = new H1("How can we help?");
        title.getStyle().set("font-size", "40px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0");
        title.getStyle().set("letter-spacing", "-0.025em");

        // Subtitle
        Paragraph subtitle = new Paragraph("Search our knowledge base or browse categories below");
        subtitle.getStyle().set("font-size", "17px");
        subtitle.getStyle().set("color", TEXT_SECONDARY);
        subtitle.getStyle().set("margin", "0");

        // Search bar
        TextField searchField = new TextField();
        searchField.setPlaceholder("Search for answers...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setWidthFull();
        searchField.setMaxWidth("500px");
        searchField.getStyle().set("background", BG_GRAY);
        searchField.getStyle().set("border", "none");
        searchField.getStyle().set("border-radius", "9999px");
        searchField.getStyle().set("padding", "16px 24px");
        searchField.getStyle().set("font-size", "16px");

        searchField.addValueChangeListener(e -> filterFAQs(e.getValue()));

        hero.add(iconContainer, title, subtitle, searchField);

        return hero;
    }

    private HorizontalLayout createQuickLinks() {
        HorizontalLayout links = new HorizontalLayout();
        links.setWidthFull();
        links.getStyle().set("gap", "16px");
        links.getStyle().set("justify-content", "center");
        links.getStyle().set("flex-wrap", "wrap");

        String[][] linkData = {
            {"Getting Started", VaadinIcon.ROCKET.name()},
            {"Account & Billing", VaadinIcon.USER_CARD.name()},
            {"AI Features", VaadinIcon.MAGIC.name()},
            {"Export & Share", VaadinIcon.DOWNLOAD.name()}
        };

        for (String[] data : linkData) {
            Button linkBtn = createQuickLinkButton(data[0], VaadinIcon.valueOf(data[1]));
            links.add(linkBtn);
        }

        return links;
    }

    private Button createQuickLinkButton(String text, VaadinIcon icon) {
        Button btn = new Button(text, icon.create());
        btn.getStyle().set("background", BG_WHITE);
        btn.getStyle().set("color", TEXT_PRIMARY);
        btn.getStyle().set("font-weight", "600");
        btn.getStyle().set("border-radius", "9999px");
        btn.getStyle().set("border", "1px solid rgba(0,0,0,0.1)");
        btn.getStyle().set("padding", "12px 24px");
        btn.getStyle().set("cursor", "pointer");
        btn.getStyle().set("transition", "all 0.2s");

        btn.getElement().addEventListener("mouseenter", e -> {
            btn.getStyle().set("background", BG_GRAY);
            btn.getStyle().set("border-color", "rgba(0,0,0,0.15)");
        });

        btn.getElement().addEventListener("mouseleave", e -> {
            btn.getStyle().set("background", BG_WHITE);
            btn.getStyle().set("border-color", "rgba(0,0,0,0.1)");
        });

        btn.addClickListener(e -> Notification.show("Opening " + text, 3000, Notification.Position.TOP_CENTER));

        return btn;
    }

    private VerticalLayout createFAQSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(false);
        section.getStyle().set("gap", "20px");
        section.setWidthFull();

        H2 title = new H2("Frequently Asked Questions");
        title.getStyle().set("font-size", "24px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0 0 8px 0");

        faqContainer = new VerticalLayout();
        faqContainer.setPadding(false);
        faqContainer.setSpacing(false);
        faqContainer.getStyle().set("gap", "12px");

        // FAQ items
        List<String[]> faqs = List.of(
            new String[]{"Is CL Booster really free?", "Yes! CL Booster is free for all job seekers. You can generate unlimited cover letters with our AI. Premium features like advanced customization and priority support are available in our Pro plan."},
            new String[]{"How does the AI generate cover letters?", "Our AI analyzes your resume and the job description to create personalized cover letters. It identifies key skills, experiences, and company values to craft compelling narratives that match the role."},
            new String[]{"Can I edit the generated cover letter?", "Absolutely! After generation, you can edit your cover letter in our built-in editor. You also get AI-powered suggestions to improve your letter further."},
            new String[]{"What file formats can I export?", "You can export your cover letters as PDF, DOCX, or plain text. PDF is recommended for job applications as it preserves formatting across all devices."},
            new String[]{"Is my data secure?", "We take data security seriously. Your resume and personal information are encrypted and stored securely. We never share your data with third parties."},
            new String[]{"How do I improve my match score?", "To improve your match score, make sure to include relevant keywords from the job description, highlight quantifiable achievements, and tailor your skills to match the role requirements."}
        );

        for (String[] faq : faqs) {
            faqContainer.add(createFAQItem(faq[0], faq[1]));
        }

        section.add(title, faqContainer);

        return section;
    }

    private Div createFAQItem(String question, String answer) {
        Div item = new Div();
        item.getStyle().set("background", BG_GRAY);
        item.getStyle().set("border-radius", "16px");
        item.getStyle().set("padding", "24px");
        item.getStyle().set("cursor", "pointer");
        item.getStyle().set("transition", "all 0.3s");

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        H3 questionText = new H3(question);
        questionText.getStyle().set("font-size", "16px");
        questionText.getStyle().set("font-weight", "600");
        questionText.getStyle().set("color", TEXT_PRIMARY);
        questionText.getStyle().set("margin", "0");

        Icon chevron = VaadinIcon.CHEVRON_DOWN.create();
        chevron.getStyle().set("color", TEXT_SECONDARY);
        chevron.getStyle().set("transition", "transform 0.3s");

        header.add(questionText, chevron);
        header.expand(questionText);

        Paragraph answerText = new Paragraph(answer);
        answerText.getStyle().set("font-size", "14px");
        answerText.getStyle().set("color", TEXT_SECONDARY);
        answerText.getStyle().set("line-height", "1.6");
        answerText.getStyle().set("margin", "16px 0 0 0");
        answerText.getStyle().set("max-height", "0");
        answerText.getStyle().set("overflow", "hidden");
        answerText.getStyle().set("opacity", "0");
        answerText.getStyle().set("transition", "all 0.3s");

        item.add(header, answerText);

        // Toggle functionality
        final boolean[] isExpanded = {false};
        item.addClickListener(e -> {
            isExpanded[0] = !isExpanded[0];
            if (isExpanded[0]) {
                answerText.getStyle().set("max-height", "500px");
                answerText.getStyle().set("opacity", "1");
                chevron.getStyle().set("transform", "rotate(180deg)");
                item.getStyle().set("background", BG_WHITE);
                item.getStyle().set("box-shadow", "0 4px 20px rgba(0,0,0,0.08)");
            } else {
                answerText.getStyle().set("max-height", "0");
                answerText.getStyle().set("opacity", "0");
                chevron.getStyle().set("transform", "rotate(0deg)");
                item.getStyle().set("background", BG_GRAY);
                item.getStyle().set("box-shadow", "none");
            }
        });

        // Hover effect
        item.getElement().addEventListener("mouseenter", e -> {
            if (!isExpanded[0]) {
                item.getStyle().set("background", "rgba(0,0,0,0.04)");
            }
        });

        item.getElement().addEventListener("mouseleave", e -> {
            if (!isExpanded[0]) {
                item.getStyle().set("background", BG_GRAY);
            }
        });

        return item;
    }

    private HorizontalLayout createSupportCards() {
        HorizontalLayout cards = new HorizontalLayout();
        cards.setWidthFull();
        cards.getStyle().set("gap", "20px");
        cards.getStyle().set("margin-top", "20px");

        // Contact support card
        Div contactCard = createSupportCard(
            "Contact Support",
            "Get in touch with our team",
            VaadinIcon.ENVELOPE,
            PRIMARY,
            "Email Us"
        );

        // Community card
        Div communityCard = createSupportCard(
            "Community",
            "Join discussions with other users",
            VaadinIcon.USERS,
            "#AF52DE",
            "Join Community"
        );

        // Documentation card
        Div docsCard = createSupportCard(
            "Documentation",
            "Read our detailed guides",
            VaadinIcon.BOOK,
            "#34C759",
            "View Docs"
        );

        cards.add(contactCard, communityCard, docsCard);

        return cards;
    }

    private Div createSupportCard(String title, String description, VaadinIcon iconType,
                                   String iconColor, String actionText) {
        Div card = new Div();
        card.getStyle().set("background", BG_WHITE);
        card.getStyle().set("border", "1px solid rgba(0,0,0,0.05)");
        card.getStyle().set("border-radius", "24px");
        card.getStyle().set("padding", "24px");
        card.getStyle().set("flex", "1");
        card.getStyle().set("cursor", "pointer");
        card.getStyle().set("transition", "all 0.3s");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);
        content.getStyle().set("gap", "16px");
        content.setDefaultHorizontalComponentAlignment(Alignment.START);

        // Icon
        Div iconContainer = new Div();
        iconContainer.getStyle().set("width", "48px");
        iconContainer.getStyle().set("height", "48px");
        iconContainer.getStyle().set("border-radius", "14px");
        iconContainer.getStyle().set("background", iconColor + "15");
        iconContainer.getStyle().set("display", "flex");
        iconContainer.getStyle().set("align-items", "center");
        iconContainer.getStyle().set("justify-content", "center");

        Icon icon = iconType.create();
        icon.getStyle().set("color", iconColor);
        icon.getStyle().set("width", "24px");
        icon.getStyle().set("height", "24px");
        iconContainer.add(icon);

        // Text
        VerticalLayout textGroup = new VerticalLayout();
        textGroup.setPadding(false);
        textGroup.setSpacing(false);
        textGroup.getStyle().set("gap", "4px");

        H3 cardTitle = new H3(title);
        cardTitle.getStyle().set("font-size", "16px");
        cardTitle.getStyle().set("font-weight", "700");
        cardTitle.getStyle().set("color", TEXT_PRIMARY);
        cardTitle.getStyle().set("margin", "0");

        Paragraph cardDesc = new Paragraph(description);
        cardDesc.getStyle().set("font-size", "13px");
        cardDesc.getStyle().set("color", TEXT_SECONDARY);
        cardDesc.getStyle().set("margin", "0");

        textGroup.add(cardTitle, cardDesc);

        // Action link
        Span actionLink = new Span(actionText);
        actionLink.getStyle().set("font-size", "14px");
        actionLink.getStyle().set("font-weight", "600");
        actionLink.getStyle().set("color", iconColor);
        actionLink.getStyle().set("margin-top", "8px");

        content.add(iconContainer, textGroup, actionLink);
        card.add(content);

        // Hover effects
        card.getElement().addEventListener("mouseenter", e -> {
            card.getStyle().set("transform", "translateY(-4px)");
            card.getStyle().set("box-shadow", "0 20px 40px rgba(0,0,0,0.1)");
            iconContainer.getStyle().set("transform", "scale(1.1)");
        });

        card.getElement().addEventListener("mouseleave", e -> {
            card.getStyle().set("transform", "translateY(0)");
            card.getStyle().set("box-shadow", "none");
            iconContainer.getStyle().set("transform", "scale(1)");
        });

        card.addClickListener(e -> Notification.show("Opening " + title, 3000, Notification.Position.TOP_CENTER));

        return card;
    }

    private void filterFAQs(String searchTerm) {
        // Simple filter implementation - in production, this would search through actual data
        if (searchTerm == null || searchTerm.isEmpty()) {
            return;
        }
        // Notification to show search is working
        Notification.show("Searching for: " + searchTerm, 2000, Notification.Position.TOP_CENTER);
    }
}
