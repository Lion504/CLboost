package com.clbooster.app.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * History view showing grid of past generated cover letters.
 */
@Route(value = "history", layout = MainLayout.class)
@PageTitle("History | CL Booster")
public class HistoryView extends VerticalLayout {

    public HistoryView() {
        add(new H1("Cover Letter History"));
        // TODO: Implement history grid showing past cover letters
    }
}
