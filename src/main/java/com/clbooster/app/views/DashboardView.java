package com.clbooster.app.views;

import com.clbooster.app.backend.service.GreetService;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;

/**
 * Dashboard view showing application statistics and quick actions.
 */
@Route(value = "", layout = MainLayout.class)
@PageTitle("Dashboard | CL Booster")
public class DashboardView extends VerticalLayout {

}
