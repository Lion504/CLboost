package com.clbooster.app.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout {

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        DrawerToggle toggle = new DrawerToggle();

        // Search bar
        TextField search = new TextField();
        search.setPlaceholder("Search resources...");
        search.setPrefixComponent(VaadinIcon.SEARCH.create());
        search.getStyle().set("width", "260px");

        // Right icons
        Button notif = new Button(VaadinIcon.BELL.create());
        notif.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        Button settings = new Button(VaadinIcon.COG.create());
        settings.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        Avatar avatar = new Avatar("Alex Rivera");
        avatar.setColorIndex(2);

        HorizontalLayout header = new HorizontalLayout(toggle, search, notif, settings, avatar);
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.expand(search);
        header.getStyle().set("padding", "0 16px");

        addToNavbar(true, header);
    }

    private void createDrawer() {
        // Logo
        Span logo = new Span("Cover Booster");
        logo.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.FontSize.LARGE);
        logo.getStyle().set("color", "white").set("padding", "16px 8px");

        // Navigation
        SideNav nav = new SideNav();
        nav.addItem(new SideNavItem("Dashboard",  DashboardView.class,  VaadinIcon.DASHBOARD.create()));
        nav.addItem(new SideNavItem("Generate",   GeneratorView.class,  VaadinIcon.MAGIC.create()));
        nav.addItem(new SideNavItem("Resumes",    HistoryView.class,    VaadinIcon.FILE_TEXT.create()));
        nav.addItem(new SideNavItem("History",    HistoryView.class,    VaadinIcon.CLOCK.create()));
        nav.getStyle().set("color", "white");

        addToDrawer(logo, nav);
        getDrawer().addClassName("sidebar");
    }
}
