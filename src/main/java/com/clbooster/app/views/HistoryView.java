package com.clbooster.app.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;

@Route(value = "history", layout = MainLayout.class)
@PageTitle("History | CL Booster")
public class HistoryView extends VerticalLayout {

    public HistoryView() {
        setPadding(true);
        setSpacing(true);

        // Header
        H2 title = new H2("Generation History");
        Paragraph sub = new Paragraph("Review, refine, or regenerate your previous successful applications.");
        Button exportAll = new Button("Export All", VaadinIcon.DOWNLOAD.create());
        exportAll.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        HorizontalLayout header = new HorizontalLayout(
            new VerticalLayout(title, sub), exportAll);
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        // Search + filters
        TextField search = new TextField();
        search.setPlaceholder("Search by company or role...");
        search.setPrefixComponent(VaadinIcon.SEARCH.create());
        search.setWidth("320px");
        Button dateFilter   = new Button("Date Range", VaadinIcon.CALENDAR.create());
        Button statusFilter = new Button("Status", VaadinIcon.FILTER.create());
        dateFilter.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        statusFilter.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        HorizontalLayout filters = new HorizontalLayout(search, dateFilter, statusFilter);
        filters.setAlignItems(FlexComponent.Alignment.CENTER);

        // Grid
        Grid<String[]> grid = new Grid<>();
        grid.addColumn(r -> r[0]).setHeader("APPLICATION").setAutoWidth(true);
        grid.addColumn(r -> r[1]).setHeader("DETAILS").setAutoWidth(true);
        grid.addColumn(r -> r[2]).setHeader("RESUME USED").setAutoWidth(true);
        grid.addComponentColumn(r -> {
            Span badge = new Span(r[3]);
            String cls = switch (r[3]) {
                case "SENT"      -> "badge-sent";
                case "FINALIZED" -> "badge-draft";
                default          -> "badge-archived";
            };
            badge.addClassName(cls);
            badge.getStyle().set("padding", "2px 10px")
                            .set("border-radius", "999px")
                            .set("font-size", "12px")
                            .set("font-weight", "600");
            return badge;
        }).setHeader("STATUS").setAutoWidth(true);
        grid.addComponentColumn(r -> {
            Button view     = new Button(VaadinIcon.EYE.create());
            Button download = new Button(VaadinIcon.DOWNLOAD.create());
            Button more     = new Button(VaadinIcon.ELLIPSIS_DOTS_V.create());
            view.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
            download.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
            more.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
            return new HorizontalLayout(view, download, more);
        }).setHeader("ACTIONS");

        grid.setItems(
            new String[]{"Growth Product Manager",  "Feb 7, 2024", "PM MAIN",      "FINALIZED"},
            new String[]{"Senior UX Designer",      "Feb 5, 2024", "DESIGN 2025",  "SENT"},
            new String[]{"Lead Frontend Engineer",  "Jan 26, 2026","PM MAIN",      "ARCHIVED"},
            new String[]{"Senior Product Manager",  "Jan 15, 2026","GENERIC TECH", "ARCHIVED"}
        );
        grid.setWidthFull();

        Button loadMore = new Button("Load more generations");
        loadMore.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        loadMore.setWidthFull();

        add(header, filters, grid, loadMore);
    }
}
