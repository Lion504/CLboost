package com.clbooster.app.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.router.*;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | CL Booster")
public class DashboardView extends VerticalLayout {

    public DashboardView() {
        setPadding(true);
        setSpacing(true);

        // Header row
        H2 title = new H2("Dashboard");
        Paragraph subtitle = new Paragraph("Ready to land your next dream role, Alex?");
        Button startNew = new Button("+ Start New Generation",
                e -> getUI().ifPresent(ui -> ui.navigate(GeneratorView.class)));
        startNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout headerRow = new HorizontalLayout(new VerticalLayout(title, subtitle), startNew);
        headerRow.setWidthFull();
        headerRow.setAlignItems(FlexComponent.Alignment.CENTER);
        headerRow.expand(new VerticalLayout(title, subtitle));

        // Performance card (dark)
        Div perfCard = new Div();
        perfCard.addClassName("performance-card");
        perfCard.setWidthFull();
        H3 perfTitle = new H3("Your applications are performing 24% better.");
        perfTitle.getStyle().set("color", "white").set("margin", "0");
        Paragraph perfSub = new Paragraph(
                "Based on profile views and interview callbacks from your last 5 generations.");
        perfSub.getStyle().set("color", "#94a3b8");
        Span sent = new Span("12 SENT");
        Span interviews = new Span("4 INTERVIEWS");
        Span trend = new Span("+12.5%");
        trend.getStyle().set("color", "#4ade80");
        HorizontalLayout stats = new HorizontalLayout(sent, interviews, trend);
        perfCard.add(perfTitle, perfSub, stats);

        // Recent generations grid
        H3 recentTitle = new H3("Recent Generations");
        Grid<String[]> grid = new Grid<>();
        grid.addColumn(r -> r[0]).setHeader("POSITION");
        grid.addColumn(r -> r[1]).setHeader("COMPANY");
        grid.addColumn(r -> r[2]).setHeader("MATCH");
        grid.addColumn(r -> r[3]).setHeader("STATUS");
        grid.setItems(new String[] { "Growth Product Manager", "NordicFin", "+98%", "DRAFT" },
                new String[] { "Senior UX Designer", "Spotify", "+92%", "SENT" },
                new String[] { "Lead Frontend Engineer", "Vercel", "+85%", "SENT" });
        grid.setWidthFull();

        add(headerRow, perfCard, recentTitle, grid);
    }
}
