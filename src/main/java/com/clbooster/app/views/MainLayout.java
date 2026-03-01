package com.clbooster.app.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.TextField;

public class MainLayout extends AppLayout {

    // Figma Design System Colors
    private static final String PRIMARY = "#007AFF";
    private static final String TEXT_PRIMARY = "#1d1d1f";
    private static final String TEXT_SECONDARY = "#86868b";
    private static final String BG_WHITE = "#ffffff";
    private static final String BG_GRAY = "#f5f5f7";
    private static final String SIDEBAR_BG = "#1d1d1f";
    private static final String SIDEBAR_HOVER = "#2d2d2f";

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        // Drawer toggle
        DrawerToggle toggle = new DrawerToggle();
        toggle.getStyle().set("color", TEXT_PRIMARY);

        // Search bar
        TextField search = new TextField();
        search.setPlaceholder("Search resources...");
        search.setPrefixComponent(VaadinIcon.SEARCH.create());
        search.setWidth("280px");
        search.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        search.getStyle().set("--vaadin-input-field-border-radius", "12px");
        search.getStyle().set("margin-left", "16px");

        // Right side icons
        HorizontalLayout rightSide = new HorizontalLayout();
        rightSide.setAlignItems(FlexComponent.Alignment.CENTER);
        rightSide.getStyle().set("gap", "8px");

        // Notification button
        Button notifBtn = createIconButton(VaadinIcon.BELL);
        notifBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(NotificationsView.class)));
        notifBtn.getElement().setAttribute("aria-label", "Notifications");

        // Settings button
        Button settingsBtn = createIconButton(VaadinIcon.COG);
        settingsBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(SettingsView.class)));
        settingsBtn.getElement().setAttribute("aria-label", "Settings");

        // User avatar (wrapped in clickable div)
        Div avatarWrapper = new Div();
        avatarWrapper.getStyle().set("cursor", "pointer");
        avatarWrapper.getStyle().set("transition", "transform 0.2s");
        
        Avatar avatar = new Avatar("Alex Rivera");
        avatar.setColorIndex(2);
        avatar.getStyle().set("border", "2px solid " + BG_WHITE);
        avatar.getStyle().set("box-shadow", "0 2px 8px rgba(0, 0, 0, 0.1)");
        
        avatarWrapper.add(avatar);
        avatarWrapper.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(ProfileView.class)));
        
        avatarWrapper.getElement().addEventListener("mouseenter", e -> {
            avatarWrapper.getStyle().set("transform", "scale(1.05)");
        });
        avatarWrapper.getElement().addEventListener("mouseleave", e -> {
            avatarWrapper.getStyle().set("transform", "scale(1)");
        });

        rightSide.add(notifBtn, settingsBtn, avatarWrapper);

        HorizontalLayout header = new HorizontalLayout(toggle, search, rightSide);
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle().set("padding", "12px 24px");
        header.getStyle().set("background", BG_WHITE);
        header.getStyle().set("border-bottom", "1px solid rgba(0, 0, 0, 0.05)");
        header.expand(search);

        addToNavbar(true, header);
    }

    private Button createIconButton(VaadinIcon icon) {
        Button btn = new Button(icon.create());
        btn.getStyle().set("background", "transparent");
        btn.getStyle().set("color", TEXT_SECONDARY);
        btn.getStyle().set("padding", "8px");
        btn.getStyle().set("border-radius", "10px");
        btn.getStyle().set("border", "none");
        btn.getStyle().set("cursor", "pointer");
        btn.getStyle().set("transition", "all 0.2s");

        btn.getElement().addEventListener("mouseenter", e -> {
            btn.getStyle().set("background", BG_GRAY);
            btn.getStyle().set("color", TEXT_PRIMARY);
        });
        btn.getElement().addEventListener("mouseleave", e -> {
            btn.getStyle().set("background", "transparent");
            btn.getStyle().set("color", TEXT_SECONDARY);
        });

        return btn;
    }

    private void createDrawer() {
        VerticalLayout drawer = new VerticalLayout();
        drawer.setPadding(false);
        drawer.setSpacing(false);
        drawer.getStyle().set("height", "100%");
        drawer.getStyle().set("background", "linear-gradient(180deg, " + SIDEBAR_BG + " 0%, " + SIDEBAR_HOVER + " 100%)");

        // Logo section
        HorizontalLayout logoSection = new HorizontalLayout();
        logoSection.setAlignItems(FlexComponent.Alignment.CENTER);
        logoSection.getStyle().set("gap", "12px");
        logoSection.getStyle().set("padding", "24px 20px");
        logoSection.getStyle().set("cursor", "pointer");

        Div logoIcon = new Div();
        logoIcon.getStyle().set("width", "36px");
        logoIcon.getStyle().set("height", "36px");
        logoIcon.getStyle().set("background", PRIMARY);
        logoIcon.getStyle().set("border-radius", "10px");
        logoIcon.getStyle().set("display", "flex");
        logoIcon.getStyle().set("align-items", "center");
        logoIcon.getStyle().set("justify-content", "center");
        logoIcon.getStyle().set("box-shadow", "0 4px 12px rgba(0, 122, 255, 0.3)");
        logoIcon.add(VaadinIcon.SPARK_LINE.create());

        Span logoText = new Span("CL Booster");
        logoText.getStyle().set("font-weight", "700");
        logoText.getStyle().set("font-size", "20px");
        logoText.getStyle().set("color", "white");
        logoText.getStyle().set("letter-spacing", "-0.025em");

        logoSection.add(logoIcon, logoText);
        logoSection.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("dashboard")));

        // Navigation
        SideNav nav = new SideNav();
        nav.getStyle().set("margin-top", "8px");

        // Main section
        Span mainLabel = new Span("MAIN");
        mainLabel.getStyle().set("font-size", "11px");
        mainLabel.getStyle().set("font-weight", "700");
        mainLabel.getStyle().set("color", "rgba(255, 255, 255, 0.4)");
        mainLabel.getStyle().set("padding", "0 20px");
        mainLabel.getStyle().set("letter-spacing", "0.1em");

        SideNavItem dashboard = createNavItem("Dashboard", "dashboard", VaadinIcon.DASHBOARD);
        SideNavItem generate = createNavItem("Generate", "generator-wizard", VaadinIcon.MAGIC);
        SideNavItem history = createNavItem("History", "history", VaadinIcon.CLOCK);

        // Account section
        Span accountLabel = new Span("ACCOUNT");
        accountLabel.getStyle().set("font-size", "11px");
        accountLabel.getStyle().set("font-weight", "700");
        accountLabel.getStyle().set("color", "rgba(255, 255, 255, 0.4)");
        accountLabel.getStyle().set("padding", "24px 20px 8px");
        accountLabel.getStyle().set("letter-spacing", "0.1em");

        SideNavItem profile = createNavItem("Profile", "profile", VaadinIcon.USER);
        SideNavItem settings = createNavItem("Settings", "settings", VaadinIcon.COG);

        // Add Resume section
        Span toolsLabel = new Span("TOOLS");
        toolsLabel.getStyle().set("font-size", "11px");
        toolsLabel.getStyle().set("font-weight", "700");
        toolsLabel.getStyle().set("color", "rgba(255, 255, 255, 0.4)");
        toolsLabel.getStyle().set("padding", "24px 20px 8px");
        toolsLabel.getStyle().set("letter-spacing", "0.1em");

        SideNavItem resume = createNavItem("Resume Manager", "resume", VaadinIcon.FILE_TEXT);
        SideNavItem editor = createNavItem("Editor", "editor", VaadinIcon.EDIT);

        // Support section
        Span supportLabel = new Span("SUPPORT");
        supportLabel.getStyle().set("font-size", "11px");
        supportLabel.getStyle().set("font-weight", "700");
        supportLabel.getStyle().set("color", "rgba(255, 255, 255, 0.4)");
        supportLabel.getStyle().set("padding", "24px 20px 8px");
        supportLabel.getStyle().set("letter-spacing", "0.1em");

        SideNavItem help = createNavItem("Help", "help", VaadinIcon.QUESTION_CIRCLE);

        nav.addItem(dashboard, generate, history);

        // Add items with custom styling
        drawer.add(logoSection, mainLabel, nav, toolsLabel);

        // Create separate nav for tools items
        SideNav toolsNav = new SideNav();
        toolsNav.addItem(resume, editor);
        drawer.add(toolsNav);

        drawer.add(supportLabel);
        SideNav supportNav = new SideNav();
        supportNav.addItem(help);
        drawer.add(supportNav);

        drawer.add(accountLabel);

        // Create separate nav for account items
        SideNav accountNav = new SideNav();
        accountNav.addItem(profile, settings);
        drawer.add(accountNav);

        // Add spacer and version info at bottom
        Div spacer = new Div();
        spacer.getStyle().set("flex", "1");

        HorizontalLayout versionInfo = new HorizontalLayout();
        versionInfo.setAlignItems(FlexComponent.Alignment.CENTER);
        versionInfo.getStyle().set("padding", "20px");
        versionInfo.getStyle().set("gap", "8px");

        Span versionLabel = new Span("v4.0.0");
        versionLabel.getStyle().set("font-size", "12px");
        versionLabel.getStyle().set("color", "rgba(255, 255, 255, 0.4)");

        Div dot = new Div();
        dot.getStyle().set("width", "6px");
        dot.getStyle().set("height", "6px");
        dot.getStyle().set("background", "#34C759");
        dot.getStyle().set("border-radius", "50%");

        Span statusLabel = new Span("Online");
        statusLabel.getStyle().set("font-size", "12px");
        statusLabel.getStyle().set("color", "rgba(255, 255, 255, 0.6)");

        versionInfo.add(versionLabel, dot, statusLabel);

        drawer.add(spacer, versionInfo);
        drawer.setFlexGrow(1, spacer);

        addToDrawer(drawer);
    }

    private SideNavItem createNavItem(String label, String route, VaadinIcon icon) {
        SideNavItem item = new SideNavItem(label, route, icon.create());
        item.getStyle().set("color", "rgba(255, 255, 255, 0.7)");
        item.getStyle().set("font-weight", "500");
        item.getStyle().set("font-size", "14px");
        item.getStyle().set("padding", "12px 20px");
        item.getStyle().set("border-radius", "12px");
        item.getStyle().set("margin", "2px 12px");
        item.getStyle().set("transition", "all 0.2s");

        // Hover effect
        item.getElement().addEventListener("mouseenter", e -> {
            item.getStyle().set("background", "rgba(255, 255, 255, 0.1)");
            item.getStyle().set("color", "white");
        });
        item.getElement().addEventListener("mouseleave", e -> {
            item.getStyle().set("background", "transparent");
            item.getStyle().set("color", "rgba(255, 255, 255, 0.7)");
        });

        return item;
    }
}
