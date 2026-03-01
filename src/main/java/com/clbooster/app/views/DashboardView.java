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
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | CL Booster")
public class DashboardView extends VerticalLayout {

    // Figma Design System Colors
    private static final String PRIMARY = "#007AFF";
    private static final String TEXT_PRIMARY = "#1d1d1f";
    private static final String TEXT_SECONDARY = "#86868b";
    private static final String BG_WHITE = "#ffffff";
    private static final String BG_GRAY = "#f5f5f7";

    public DashboardView() {
        setPadding(true);
        setSpacing(true);
        getStyle().set("gap", "32px");
        getStyle().set("padding", "32px");
        getStyle().set("background", BG_WHITE);
        setSizeFull();

        // Header Section
        HorizontalLayout header = createHeader();

        // Stats Cards
        HorizontalLayout statsCards = createStatsCards();

        // Recent Letters Section
        VerticalLayout recentSection = createRecentLettersSection();

        add(header, statsCards, recentSection);
    }

    private HorizontalLayout createHeader() {
        VerticalLayout titleGroup = new VerticalLayout();
        titleGroup.setPadding(false);
        titleGroup.setSpacing(false);
        titleGroup.getStyle().set("gap", "4px");

        H1 title = new H1("Welcome back, Alex");
        title.getStyle().set("font-size", "30px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("letter-spacing", "-0.025em");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0");

        Paragraph subtitle = new Paragraph("You have 4 active applications this week. Let's make them perfect.");
        subtitle.getStyle().set("font-size", "14px");
        subtitle.getStyle().set("color", TEXT_SECONDARY);
        subtitle.getStyle().set("margin", "0");

        titleGroup.add(title, subtitle);

        Button createBtn = new Button("Create New Letter", VaadinIcon.PLUS.create());
        createBtn.getStyle().set("background", PRIMARY);
        createBtn.getStyle().set("color", "white");
        createBtn.getStyle().set("font-weight", "600");
        createBtn.getStyle().set("border-radius", "9999px");
        createBtn.getStyle().set("padding", "12px 24px");
        createBtn.getStyle().set("box-shadow", "0 10px 15px -3px rgba(0,122,255,0.3)");
        createBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(GeneratorView.class)));

        HorizontalLayout header = new HorizontalLayout(titleGroup, createBtn);
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.expand(titleGroup);

        return header;
    }

    private HorizontalLayout createStatsCards() {
        // AI Score Card (Blue Gradient)
        Div scoreCard = new Div();
        scoreCard.getStyle().set("flex", "1");
        scoreCard.getStyle().set("background", "linear-gradient(135deg, #007AFF 0%, #0052AF 100%)");
        scoreCard.getStyle().set("border-radius", "24px");
        scoreCard.getStyle().set("padding", "32px");
        scoreCard.getStyle().set("position", "relative");
        scoreCard.getStyle().set("overflow", "hidden");

        Paragraph scoreLabel = new Paragraph("AI Score Average");
        scoreLabel.getStyle().set("font-size", "12px");
        scoreLabel.getStyle().set("font-weight", "700");
        scoreLabel.getStyle().set("text-transform", "uppercase");
        scoreLabel.getStyle().set("letter-spacing", "0.1em");
        scoreLabel.getStyle().set("color", "rgba(255,255,255,0.8)");
        scoreLabel.getStyle().set("margin", "0 0 16px 0");

        H2 scoreValue = new H2("94%");
        scoreValue.getStyle().set("font-size", "48px");
        scoreValue.getStyle().set("font-weight", "700");
        scoreValue.getStyle().set("color", "white");
        scoreValue.getStyle().set("margin", "0 0 8px 0");

        HorizontalLayout trendRow = new HorizontalLayout();
        trendRow.setAlignItems(FlexComponent.Alignment.CENTER);
        trendRow.getStyle().set("gap", "8px");
        trendRow.getStyle().set("color", "rgba(255,255,255,0.8)");

        Div trendIcon = new Div();
        trendIcon.getStyle().set("background", "rgba(255,255,255,0.2)");
        trendIcon.getStyle().set("padding", "4px");
        trendIcon.getStyle().set("border-radius", "4px");
        Icon arrow = VaadinIcon.ARROW_RIGHT.create();
        arrow.getStyle().set("color", "white");
        arrow.getStyle().set("width", "14px");
        trendIcon.add(arrow);

        Span trendText = new Span("+12% from last month");
        trendText.getStyle().set("font-size", "14px");
        trendRow.add(trendIcon, trendText);

        scoreCard.add(scoreLabel, scoreValue, trendRow);

        // Add sparkle decorations
        for (int i = 0; i < 6; i++) {
            Div sparkle = new Div();
            sparkle.getStyle().set("position", "absolute");
            sparkle.getStyle().set("color", "white");
            sparkle.getStyle().set("opacity", "0.2");
            sparkle.getStyle().set("top", (Math.random() * 100) + "%");
            sparkle.getStyle().set("left", (Math.random() * 100) + "%");
            sparkle.add(VaadinIcon.STAR.create());
            scoreCard.add(sparkle);
        }

        // Total Letters Card
        Div lettersCard = new Div();
        lettersCard.getStyle().set("flex", "1");
        lettersCard.getStyle().set("background", BG_WHITE);
        lettersCard.getStyle().set("border", "1px solid rgba(0,0,0,0.05)");
        lettersCard.getStyle().set("border-radius", "24px");
        lettersCard.getStyle().set("padding", "32px");

        Paragraph lettersLabel = new Paragraph("Total Letters");
        lettersLabel.getStyle().set("font-size", "12px");
        lettersLabel.getStyle().set("font-weight", "700");
        lettersLabel.getStyle().set("text-transform", "uppercase");
        lettersLabel.getStyle().set("letter-spacing", "0.1em");
        lettersLabel.getStyle().set("color", TEXT_SECONDARY);
        lettersLabel.getStyle().set("margin", "0 0 16px 0");

        H2 lettersValue = new H2("24");
        lettersValue.getStyle().set("font-size", "36px");
        lettersValue.getStyle().set("font-weight", "700");
        lettersValue.getStyle().set("color", TEXT_PRIMARY);
        lettersValue.getStyle().set("margin", "0 0 16px 0");

        // Progress bar
        Div progressBg = new Div();
        progressBg.getStyle().set("width", "100%");
        progressBg.getStyle().set("height", "8px");
        progressBg.getStyle().set("background", "rgba(0,0,0,0.05)");
        progressBg.getStyle().set("border-radius", "4px");
        progressBg.getStyle().set("overflow", "hidden");
        progressBg.getStyle().set("margin-bottom", "8px");

        Div progressFill = new Div();
        progressFill.getStyle().set("width", "100%");
        progressFill.getStyle().set("height", "100%");
        progressFill.getStyle().set("background", "#34C759");
        progressFill.getStyle().set("border-radius", "4px");
        progressBg.add(progressFill);

        Paragraph lettersSub = new Paragraph("Lifetime creations");
        lettersSub.getStyle().set("font-size", "12px");
        lettersSub.getStyle().set("color", TEXT_SECONDARY);
        lettersSub.getStyle().set("margin", "0");

        lettersCard.add(lettersLabel, lettersValue, progressBg, lettersSub);

        HorizontalLayout cards = new HorizontalLayout(scoreCard, lettersCard);
        cards.setWidthFull();
        cards.getStyle().set("gap", "24px");

        return cards;
    }

    private VerticalLayout createRecentLettersSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(true);
        section.getStyle().set("gap", "16px");
        section.setWidthFull();

        // Section header
        HorizontalLayout sectionHeader = new HorizontalLayout();
        sectionHeader.setWidthFull();
        sectionHeader.setAlignItems(FlexComponent.Alignment.CENTER);

        H2 sectionTitle = new H2("Recent Letters");
        sectionTitle.getStyle().set("font-size", "20px");
        sectionTitle.getStyle().set("font-weight", "700");
        sectionTitle.getStyle().set("color", TEXT_PRIMARY);
        sectionTitle.getStyle().set("margin", "0");

        HorizontalLayout filters = new HorizontalLayout();
        filters.setSpacing(false);
        filters.getStyle().set("gap", "8px");

        TextField searchField = new TextField();
        searchField.setPlaceholder("Filter...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.getStyle().set("max-width", "200px");

        Button filterBtn = new Button("Filter", VaadinIcon.FILTER.create());
        filterBtn.getStyle().set("background", "rgba(0,0,0,0.05)");
        filterBtn.getStyle().set("color", TEXT_PRIMARY);
        filterBtn.getStyle().set("border-radius", "9999px");
        filterBtn.getStyle().set("font-size", "13px");

        filters.add(searchField, filterBtn);

        sectionHeader.add(sectionTitle, filters);
        sectionHeader.expand(sectionTitle);

        // Letters grid
        HorizontalLayout lettersGrid = new HorizontalLayout();
        lettersGrid.setWidthFull();
        lettersGrid.getStyle().set("gap", "24px");
        lettersGrid.getStyle().set("flex-wrap", "wrap");

        // Sample letters data
        List<String[]> letters = List.of(
            new String[]{"Senior Product Designer", "Apple Inc.", "2 hours ago", "Draft", "98%"},
            new String[]{"React Developer", "Meta", "Yesterday", "Sent", "92%"},
            new String[]{"UX Engineer", "Airbnb", "3 days ago", "Optimized", "85%"}
        );

        for (String[] letter : letters) {
            lettersGrid.add(createLetterCard(letter[0], letter[1], letter[2], letter[3], letter[4]));
        }

        // Create new button card
        Div createCard = new Div();
        createCard.getStyle().set("width", "280px");
        createCard.getStyle().set("border", "2px dashed rgba(0,0,0,0.1)");
        createCard.getStyle().set("border-radius", "16px");
        createCard.getStyle().set("padding", "32px");
        createCard.getStyle().set("display", "flex");
        createCard.getStyle().set("flex-direction", "column");
        createCard.getStyle().set("align-items", "center");
        createCard.getStyle().set("justify-content", "center");
        createCard.getStyle().set("gap", "12px");
        createCard.getStyle().set("cursor", "pointer");
        createCard.getStyle().set("transition", "all 0.3s");
        createCard.getStyle().set("color", TEXT_SECONDARY);

        Div plusIcon = new Div();
        plusIcon.getStyle().set("width", "48px");
        plusIcon.getStyle().set("height", "48px");
        plusIcon.getStyle().set("border-radius", "50%");
        plusIcon.getStyle().set("background", "rgba(0,0,0,0.05)");
        plusIcon.getStyle().set("display", "flex");
        plusIcon.getStyle().set("align-items", "center");
        plusIcon.getStyle().set("justify-content", "center");
        plusIcon.add(VaadinIcon.PLUS.create());

        Span createText = new Span("Create from Template");
        createText.getStyle().set("font-weight", "600");
        createText.getStyle().set("font-size", "14px");

        createCard.add(plusIcon, createText);

        createCard.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(GeneratorView.class)));

        createCard.getElement().addEventListener("mouseenter", e -> {
            createCard.getStyle().set("background", "rgba(0,0,0,0.03)");
            createCard.getStyle().set("border-color", "rgba(0,122,255,0.3)");
            createCard.getStyle().set("color", PRIMARY);
        });

        createCard.getElement().addEventListener("mouseleave", e -> {
            createCard.getStyle().set("background", "transparent");
            createCard.getStyle().set("border-color", "rgba(0,0,0,0.1)");
            createCard.getStyle().set("color", TEXT_SECONDARY);
        });

        lettersGrid.add(createCard);

        section.add(sectionHeader, lettersGrid);

        return section;
    }

    private Div createLetterCard(String title, String company, String date, String status, String match) {
        Div card = new Div();
        card.getStyle().set("width", "280px");
        card.getStyle().set("background", BG_WHITE);
        card.getStyle().set("border", "1px solid rgba(0,0,0,0.05)");
        card.getStyle().set("border-radius", "16px");
        card.getStyle().set("padding", "24px");
        card.getStyle().set("cursor", "pointer");
        card.getStyle().set("transition", "all 0.3s");

        HorizontalLayout header = new HorizontalLayout();
        header.setAlignItems(FlexComponent.Alignment.START);
        header.setWidthFull();

        // File icon
        Div iconContainer = new Div();
        iconContainer.getStyle().set("width", "48px");
        iconContainer.getStyle().set("height", "48px");
        iconContainer.getStyle().set("border-radius", "16px");
        iconContainer.getStyle().set("background", BG_GRAY);
        iconContainer.getStyle().set("display", "flex");
        iconContainer.getStyle().set("align-items", "center");
        iconContainer.getStyle().set("justify-content", "center");
        Icon fileIcon = VaadinIcon.FILE_TEXT.create();
        fileIcon.getStyle().set("color", PRIMARY);
        fileIcon.getStyle().set("width", "24px");
        fileIcon.getStyle().set("height", "24px");
        iconContainer.add(fileIcon);

        // Status badge and match
        VerticalLayout statusGroup = new VerticalLayout();
        statusGroup.setPadding(false);
        statusGroup.setSpacing(false);
        statusGroup.setDefaultHorizontalComponentAlignment(Alignment.END);

        Span statusBadge = new Span(status);
        statusBadge.getStyle().set("font-size", "11px");
        statusBadge.getStyle().set("font-weight", "700");
        statusBadge.getStyle().set("padding", "4px 8px");
        statusBadge.getStyle().set("border-radius", "9999px");
        if (status.equals("Sent")) {
            statusBadge.getStyle().set("background", "rgba(52,199,89,0.1)");
            statusBadge.getStyle().set("color", "#34C759");
        } else {
            statusBadge.getStyle().set("background", "rgba(0,122,255,0.1)");
            statusBadge.getStyle().set("color", PRIMARY);
        }

        HorizontalLayout matchRow = new HorizontalLayout();
        matchRow.setAlignItems(FlexComponent.Alignment.CENTER);
        matchRow.getStyle().set("gap", "4px");
        matchRow.getStyle().set("color", "#FF9500");

        Icon star = VaadinIcon.STAR.create();
        star.getStyle().set("width", "12px");
        star.getStyle().set("height", "12px");
        Span matchText = new Span(match);
        matchText.getStyle().set("font-size", "12px");
        matchText.getStyle().set("font-weight", "700");
        matchRow.add(star, matchText);

        statusGroup.add(statusBadge, matchRow);

        header.add(iconContainer, statusGroup);
        header.expand(statusGroup);

        // Title and company
        H3 cardTitle = new H3(title);
        cardTitle.getStyle().set("font-size", "16px");
        cardTitle.getStyle().set("font-weight", "700");
        cardTitle.getStyle().set("color", TEXT_PRIMARY);
        cardTitle.getStyle().set("margin", "16px 0 4px 0");

        Paragraph cardCompany = new Paragraph(company);
        cardCompany.getStyle().set("font-size", "14px");
        cardCompany.getStyle().set("color", TEXT_SECONDARY);
        cardCompany.getStyle().set("margin", "0 0 16px 0");

        // Footer
        HorizontalLayout footer = new HorizontalLayout();
        footer.setAlignItems(FlexComponent.Alignment.CENTER);
        footer.setWidthFull();
        footer.getStyle().set("padding-top", "16px");
        footer.getStyle().set("border-top", "1px solid rgba(0,0,0,0.05)");

        HorizontalLayout dateRow = new HorizontalLayout();
        dateRow.setAlignItems(FlexComponent.Alignment.CENTER);
        dateRow.getStyle().set("gap", "4px");
        dateRow.getStyle().set("color", TEXT_SECONDARY);
        Icon clock = VaadinIcon.CLOCK.create();
        clock.getStyle().set("width", "14px");
        clock.getStyle().set("height", "14px");
        Span dateText = new Span(date);
        dateText.getStyle().set("font-size", "12px");
        dateRow.add(clock, dateText);

        Icon arrow = VaadinIcon.ARROW_RIGHT.create();
        arrow.getStyle().set("color", PRIMARY);
        arrow.getStyle().set("width", "18px");
        arrow.getStyle().set("transition", "transform 0.2s");

        footer.add(dateRow, arrow);
        footer.expand(dateRow);

        card.add(header, cardTitle, cardCompany, footer);

        // Hover effects
        card.getElement().addEventListener("mouseenter", e -> {
            card.getStyle().set("box-shadow", "0 20px 25px -5px rgba(0,0,0,0.1)");
            arrow.getStyle().set("transform", "translateX(4px)");
            iconContainer.getStyle().set("transform", "scale(1.1)");
        });

        card.getElement().addEventListener("mouseleave", e -> {
            card.getStyle().set("box-shadow", "none");
            arrow.getStyle().set("transform", "translateX(0)");
            iconContainer.getStyle().set("transform", "scale(1)");
        });

        card.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(GeneratorView.class)));

        return card;
    }
}
