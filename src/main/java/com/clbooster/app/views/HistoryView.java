package com.clbooster.app.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
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

import java.util.List;

@Route(value = "history", layout = MainLayout.class)
@PageTitle("History | CL Booster")
public class HistoryView extends VerticalLayout {

    // Figma Design System Colors
    private static final String PRIMARY = "#007AFF";
    private static final String TEXT_PRIMARY = "#1d1d1f";
    private static final String TEXT_SECONDARY = "#86868b";
    private static final String BG_WHITE = "#ffffff";
    private static final String BG_GRAY = "#f5f5f7";
    private static final String SUCCESS = "#34C759";
    private static final String WARNING = "#FF9500";

    public HistoryView() {
        setPadding(true);
        setSpacing(true);
        getStyle().set("gap", "32px");
        getStyle().set("padding", "32px");
        getStyle().set("background", BG_WHITE);
        getStyle().set("font-family", "-apple-system, BlinkMacSystemFont, 'SF Pro Text', 'SF Pro Display', system-ui, sans-serif");
        setSizeFull();

        // Header Section
        HorizontalLayout header = createHeader();

        // Filters Section
        HorizontalLayout filters = createFilters();

        // History Cards Grid
        HorizontalLayout cardsGrid = createCardsGrid();

        // Load more button
        Button loadMore = createLoadMoreButton();

        add(header, filters, cardsGrid, loadMore);
    }

    private HorizontalLayout createHeader() {
        VerticalLayout titleGroup = new VerticalLayout();
        titleGroup.setPadding(false);
        titleGroup.setSpacing(false);
        titleGroup.getStyle().set("gap", "4px");

        H2 title = new H2("Generation History");
        title.getStyle().set("font-size", "30px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("letter-spacing", "-0.025em");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0");

        Paragraph subtitle = new Paragraph("Review, refine, or regenerate your previous successful applications.");
        subtitle.getStyle().set("font-size", "14px");
        subtitle.getStyle().set("color", TEXT_SECONDARY);
        subtitle.getStyle().set("margin", "0");

        titleGroup.add(title, subtitle);

        Button exportBtn = new Button("Export All", VaadinIcon.DOWNLOAD.create());
        exportBtn.getStyle().set("background", "rgba(0, 0, 0, 0.05)");
        exportBtn.getStyle().set("color", TEXT_PRIMARY);
        exportBtn.getStyle().set("font-weight", "600");
        exportBtn.getStyle().set("border-radius", "9999px");
        exportBtn.getStyle().set("padding", "10px 20px");
        exportBtn.getStyle().set("border", "none");
        exportBtn.getStyle().set("transition", "all 0.2s ease");
        exportBtn.getStyle().set("cursor", "pointer");

        exportBtn.getElement().addEventListener("mouseenter", e -> {
            exportBtn.getStyle().set("background", "rgba(0, 0, 0, 0.08)");
        });
        exportBtn.getElement().addEventListener("mouseleave", e -> {
            exportBtn.getStyle().set("background", "rgba(0, 0, 0, 0.05)");
        });

        HorizontalLayout header = new HorizontalLayout(titleGroup, exportBtn);
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.expand(titleGroup);

        return header;
    }

    private HorizontalLayout createFilters() {
        HorizontalLayout filters = new HorizontalLayout();
        filters.setAlignItems(FlexComponent.Alignment.CENTER);
        filters.getStyle().set("gap", "12px");

        // Search field
        TextField search = new TextField();
        search.setPlaceholder("Search by company or role...");
        search.setPrefixComponent(VaadinIcon.SEARCH.create());
        search.setWidth("320px");
        search.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        search.getStyle().set("--vaadin-input-field-border-radius", "12px");

        // Date filter button
        Button dateFilter = createFilterButton("Date Range", VaadinIcon.CALENDAR);

        // Status filter button
        Button statusFilter = createFilterButton("Status", VaadinIcon.FILTER);

        filters.add(search, dateFilter, statusFilter);

        return filters;
    }

    private Button createFilterButton(String text, VaadinIcon icon) {
        Button btn = new Button(text, icon.create());
        btn.getStyle().set("background", BG_GRAY);
        btn.getStyle().set("color", TEXT_PRIMARY);
        btn.getStyle().set("font-weight", "500");
        btn.getStyle().set("font-size", "14px");
        btn.getStyle().set("border-radius", "12px");
        btn.getStyle().set("border", "none");
        btn.getStyle().set("padding", "10px 16px");
        btn.getStyle().set("transition", "all 0.2s ease");
        btn.getStyle().set("cursor", "pointer");

        btn.getElement().addEventListener("mouseenter", e -> {
            btn.getStyle().set("background", "rgba(0, 0, 0, 0.08)");
        });
        btn.getElement().addEventListener("mouseleave", e -> {
            btn.getStyle().set("background", BG_GRAY);
        });

        return btn;
    }

    private HorizontalLayout createCardsGrid() {
        HorizontalLayout grid = new HorizontalLayout();
        grid.setWidthFull();
        grid.getStyle().set("gap", "24px");
        grid.getStyle().set("flex-wrap", "wrap");

        // Sample data
        List<HistoryItem> items = List.of(
            new HistoryItem("Growth Product Manager", "TechCorp Inc.", "Feb 7, 2024", "PM MAIN", "FINALIZED", "98%"),
            new HistoryItem("Senior UX Designer", "Design Studio", "Feb 5, 2024", "DESIGN 2025", "SENT", "95%"),
            new HistoryItem("Lead Frontend Engineer", "StartupXYZ", "Jan 26, 2026", "PM MAIN", "ARCHIVED", "92%"),
            new HistoryItem("Senior Product Manager", "Enterprise Co", "Jan 15, 2026", "GENERIC TECH", "ARCHIVED", "89%")
        );

        for (HistoryItem item : items) {
            grid.add(createHistoryCard(item));
        }

        return grid;
    }

    private Div createHistoryCard(HistoryItem item) {
        Div card = new Div();
        card.getStyle().set("width", "280px");
        card.getStyle().set("background", BG_WHITE);
        card.getStyle().set("border", "1px solid rgba(0, 0, 0, 0.05)");
        card.getStyle().set("border-radius", "24px");
        card.getStyle().set("padding", "24px");
        card.getStyle().set("cursor", "pointer");
        card.getStyle().set("transition", "all 0.3s ease");

        // Header with icon and status
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
        iconContainer.getStyle().set("transition", "transform 0.3s");

        Icon fileIcon = VaadinIcon.FILE_TEXT.create();
        fileIcon.getStyle().set("color", PRIMARY);
        fileIcon.getStyle().set("width", "24px");
        fileIcon.getStyle().set("height", "24px");
        iconContainer.add(fileIcon);

        // Status and match
        VerticalLayout statusGroup = new VerticalLayout();
        statusGroup.setPadding(false);
        statusGroup.setSpacing(false);
        statusGroup.getStyle().set("gap", "6px");
        statusGroup.setDefaultHorizontalComponentAlignment(Alignment.END);

        Span statusBadge = createStatusBadge(item.status);

        HorizontalLayout matchRow = new HorizontalLayout();
        matchRow.setAlignItems(FlexComponent.Alignment.CENTER);
        matchRow.getStyle().set("gap", "4px");
        matchRow.getStyle().set("color", WARNING);

        Icon star = VaadinIcon.STAR.create();
        star.getStyle().set("width", "12px");
        star.getStyle().set("height", "12px");

        Span matchText = new Span(item.match + " MATCH");
        matchText.getStyle().set("font-size", "12px");
        matchText.getStyle().set("font-weight", "700");

        matchRow.add(star, matchText);
        statusGroup.add(statusBadge, matchRow);

        header.add(iconContainer, statusGroup);
        header.expand(statusGroup);

        // Title and company
        H3 title = new H3(item.title);
        title.getStyle().set("font-size", "16px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "16px 0 4px 0");

        Paragraph company = new Paragraph(item.company);
        company.getStyle().set("font-size", "14px");
        company.getStyle().set("color", TEXT_SECONDARY);
        company.getStyle().set("margin", "0 0 8px 0");

        // Resume used
        HorizontalLayout resumeRow = new HorizontalLayout();
        resumeRow.setAlignItems(FlexComponent.Alignment.CENTER);
        resumeRow.getStyle().set("gap", "6px");
        resumeRow.getStyle().set("margin-bottom", "16px");

        Icon docIcon = VaadinIcon.FILE_TEXT.create();
        docIcon.getStyle().set("width", "14px");
        docIcon.getStyle().set("height", "14px");
        docIcon.getStyle().set("color", TEXT_SECONDARY);

        Span resumeText = new Span("Resume: " + item.resume);
        resumeText.getStyle().set("font-size", "12px");
        resumeText.getStyle().set("color", TEXT_SECONDARY);

        resumeRow.add(docIcon, resumeText);

        // Footer with date and actions
        HorizontalLayout footer = new HorizontalLayout();
        footer.setAlignItems(FlexComponent.Alignment.CENTER);
        footer.setWidthFull();
        footer.getStyle().set("padding-top", "16px");
        footer.getStyle().set("border-top", "1px solid rgba(0, 0, 0, 0.05)");

        HorizontalLayout dateRow = new HorizontalLayout();
        dateRow.setAlignItems(FlexComponent.Alignment.CENTER);
        dateRow.getStyle().set("gap", "4px");
        dateRow.getStyle().set("color", TEXT_SECONDARY);

        Icon clock = VaadinIcon.CLOCK.create();
        clock.getStyle().set("width", "14px");
        clock.getStyle().set("height", "14px");

        Span dateText = new Span(item.date);
        dateText.getStyle().set("font-size", "12px");

        dateRow.add(clock, dateText);

        // Action buttons
        HorizontalLayout actions = new HorizontalLayout();
        actions.getStyle().set("gap", "8px");

        Button viewBtn = createIconButton(VaadinIcon.EYE);
        Button downloadBtn = createIconButton(VaadinIcon.DOWNLOAD);
        Button moreBtn = createIconButton(VaadinIcon.ELLIPSIS_DOTS_V);

        actions.add(viewBtn, downloadBtn, moreBtn);

        footer.add(dateRow, actions);
        footer.expand(dateRow);

        card.add(header, title, company, resumeRow, footer);

        // Hover effects
        card.getElement().addEventListener("mouseenter", e -> {
            card.getStyle().set("box-shadow", "0 20px 25px -5px rgba(0, 0, 0, 0.1)");
            card.getStyle().set("border-color", "rgba(0, 0, 0, 0.1)");
            iconContainer.getStyle().set("transform", "scale(1.1)");
        });

        card.getElement().addEventListener("mouseleave", e -> {
            card.getStyle().set("box-shadow", "none");
            card.getStyle().set("border-color", "rgba(0, 0, 0, 0.05)");
            iconContainer.getStyle().set("transform", "scale(1)");
        });

        return card;
    }

    private Span createStatusBadge(String status) {
        Span badge = new Span(status);
        badge.getStyle().set("font-size", "11px");
        badge.getStyle().set("font-weight", "700");
        badge.getStyle().set("padding", "4px 10px");
        badge.getStyle().set("border-radius", "9999px");
        badge.getStyle().set("text-transform", "uppercase");
        badge.getStyle().set("letter-spacing", "0.05em");

        switch (status) {
            case "SENT":
                badge.getStyle().set("background", "rgba(52, 199, 89, 0.1)");
                badge.getStyle().set("color", SUCCESS);
                break;
            case "FINALIZED":
                badge.getStyle().set("background", "rgba(0, 122, 255, 0.1)");
                badge.getStyle().set("color", PRIMARY);
                break;
            default: // ARCHIVED
                badge.getStyle().set("background", "rgba(0, 0, 0, 0.05)");
                badge.getStyle().set("color", TEXT_SECONDARY);
                break;
        }

        return badge;
    }

    private Button createIconButton(VaadinIcon icon) {
        Button btn = new Button(icon.create());
        btn.getStyle().set("background", "transparent");
        btn.getStyle().set("color", TEXT_SECONDARY);
        btn.getStyle().set("padding", "6px");
        btn.getStyle().set("border-radius", "8px");
        btn.getStyle().set("border", "none");
        btn.getStyle().set("cursor", "pointer");
        btn.getStyle().set("transition", "all 0.2s");

        btn.getElement().addEventListener("mouseenter", e -> {
            btn.getStyle().set("background", "rgba(0, 0, 0, 0.05)");
            btn.getStyle().set("color", TEXT_PRIMARY);
        });
        btn.getElement().addEventListener("mouseleave", e -> {
            btn.getStyle().set("background", "transparent");
            btn.getStyle().set("color", TEXT_SECONDARY);
        });

        return btn;
    }

    private Button createLoadMoreButton() {
        Button btn = new Button("Load more generations");
        btn.getStyle().set("background", "transparent");
        btn.getStyle().set("color", TEXT_SECONDARY);
        btn.getStyle().set("font-weight", "500");
        btn.getStyle().set("font-size", "14px");
        btn.getStyle().set("border", "1px dashed rgba(0, 0, 0, 0.15)");
        btn.getStyle().set("border-radius", "12px");
        btn.getStyle().set("padding", "16px");
        btn.getStyle().set("width", "100%");
        btn.getStyle().set("cursor", "pointer");
        btn.getStyle().set("transition", "all 0.2s");
        btn.getStyle().set("margin-top", "8px");

        btn.getElement().addEventListener("mouseenter", e -> {
            btn.getStyle().set("background", "rgba(0, 0, 0, 0.02)");
            btn.getStyle().set("border-color", "rgba(0, 0, 0, 0.25)");
            btn.getStyle().set("color", TEXT_PRIMARY);
        });
        btn.getElement().addEventListener("mouseleave", e -> {
            btn.getStyle().set("background", "transparent");
            btn.getStyle().set("border-color", "rgba(0, 0, 0, 0.15)");
            btn.getStyle().set("color", TEXT_SECONDARY);
        });

        return btn;
    }

    // Helper class for history items
    private static class HistoryItem {
        final String title;
        final String company;
        final String date;
        final String resume;
        final String status;
        final String match;

        HistoryItem(String title, String company, String date, String resume, String status, String match) {
            this.title = title;
            this.company = company;
            this.date = date;
            this.resume = resume;
            this.status = status;
            this.match = match;
        }
    }
}
