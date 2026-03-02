package com.clbooster.app.views;

import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.clbooster.app.backend.service.profile.User;
import com.clbooster.app.backend.service.settings.Settings;
import com.clbooster.app.backend.service.settings.SettingsService;
import com.clbooster.app.i18n.TranslationService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
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
    // Lighter sidebar colors for better visibility
    private static final String SIDEBAR_BG = "#f8f9fa";
    private static final String SIDEBAR_HOVER = "#e9ecef";
    private static final String SIDEBAR_TEXT = "#495057";
    private static final String SIDEBAR_LABEL = "#6c757d";

    private final AuthenticationService authService;
    private final TranslationService translationService;

    public MainLayout() {
        this.authService = new AuthenticationService();
        this.translationService = new TranslationService();
        setPrimarySection(Section.DRAWER);
        createHeader();
        createDrawer();
    }

    @Override
    protected void onAttach(com.vaadin.flow.component.AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        // Apply user's language preference if logged in
        UI ui = getUI().orElse(null);
        if (ui != null) {
            User currentUser = authService.getCurrentUser();
            if (currentUser != null) {
                SettingsService settingsService = new SettingsService();
                Settings settings = settingsService.getSettings(currentUser.getPin());
                if (settings != null && settings.getLanguage() != null) {
                    translationService.setLanguage(settings.getLanguage());
                    ui.setLocale(translationService.getCurrentLocale());
                }
            }
        }
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

        // Settings button removed - now accessible via avatar dropdown menu

        // User avatar with dropdown menu
        Div avatarWrapper = new Div();
        avatarWrapper.getStyle().set("cursor", "pointer");
        avatarWrapper.getStyle().set("transition", "transform 0.2s");
        
        // Get current user from auth service
        User currentUser = authService.getCurrentUser();
        String userName = currentUser != null ?
            currentUser.getFirstName() + " " + currentUser.getLastName() : "Guest";
        String userInitials = currentUser != null ?
            (currentUser.getFirstName().substring(0, 1) + currentUser.getLastName().substring(0, 1)).toUpperCase() : "G";
        
        Avatar avatar = new Avatar(userName);
        avatar.setColorIndex(2);
        avatar.getStyle().set("border", "2px solid " + BG_WHITE);
        avatar.getStyle().set("box-shadow", "0 2px 8px rgba(0, 0, 0, 0.1)");
        
        avatarWrapper.add(avatar);
        
        // Create context menu for avatar dropdown
        ContextMenu avatarMenu = new ContextMenu();
        avatarMenu.setTarget(avatarWrapper);
        avatarMenu.setOpenOnClick(true);
        
        // Add menu items
        avatarMenu.addItem("Profile", e -> getUI().ifPresent(ui -> ui.navigate(ProfileView.class)));
        avatarMenu.addItem("Settings", e -> getUI().ifPresent(ui -> ui.navigate(SettingsView.class)));
        avatarMenu.addSeparator();
        avatarMenu.addItem("Sign Out", e -> {
            authService.logout();
            getUI().ifPresent(ui -> ui.navigate(LoginView.class));
        });
        
        avatarWrapper.getElement().addEventListener("mouseenter", e -> {
            avatarWrapper.getStyle().set("transform", "scale(1.05)");
        });
        avatarWrapper.getElement().addEventListener("mouseleave", e -> {
            avatarWrapper.getStyle().set("transform", "scale(1)");
        });

        rightSide.add(notifBtn, avatarWrapper);

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
        logoText.getStyle().set("color", TEXT_PRIMARY);
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
        mainLabel.getStyle().set("color", SIDEBAR_LABEL);
        mainLabel.getStyle().set("padding", "0 20px");
        mainLabel.getStyle().set("letter-spacing", "0.1em");

        SideNavItem dashboard = createNavItem("Dashboard", "dashboard", VaadinIcon.DASHBOARD);
        SideNavItem generate = createNavItem("Generate", "generator-wizard", VaadinIcon.MAGIC);
        SideNavItem history = createNavItem("History", "history", VaadinIcon.CLOCK);

        // Add Resume section
        Span toolsLabel = new Span("TOOLS");
        toolsLabel.getStyle().set("font-size", "11px");
        toolsLabel.getStyle().set("font-weight", "700");
        toolsLabel.getStyle().set("color", SIDEBAR_LABEL);
        toolsLabel.getStyle().set("padding", "24px 20px 8px");
        toolsLabel.getStyle().set("letter-spacing", "0.1em");

        SideNavItem resume = createNavItem("Resume Manager", "resume", VaadinIcon.FILE_TEXT);
        SideNavItem editor = createNavItem("Editor", "editor", VaadinIcon.EDIT);

        // Support section
        Span supportLabel = new Span("SUPPORT");
        supportLabel.getStyle().set("font-size", "11px");
        supportLabel.getStyle().set("font-weight", "700");
        supportLabel.getStyle().set("color", SIDEBAR_LABEL);
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

        // Note: ACCOUNT section removed - now accessible via avatar dropdown in header

        // Add spacer and version info at bottom
        Div spacer = new Div();
        spacer.getStyle().set("flex", "1");

        HorizontalLayout versionInfo = new HorizontalLayout();
        versionInfo.setAlignItems(FlexComponent.Alignment.CENTER);
        versionInfo.getStyle().set("padding", "20px");
        versionInfo.getStyle().set("gap", "8px");

        Span versionLabel = new Span("v4.0.0");
        versionLabel.getStyle().set("font-size", "12px");
        versionLabel.getStyle().set("color", SIDEBAR_LABEL);

        Div dot = new Div();
        dot.getStyle().set("width", "6px");
        dot.getStyle().set("height", "6px");
        dot.getStyle().set("background", "#34C759");
        dot.getStyle().set("border-radius", "50%");

        Span statusLabel = new Span("Online");
        statusLabel.getStyle().set("font-size", "12px");
        statusLabel.getStyle().set("color", SIDEBAR_TEXT);

        versionInfo.add(versionLabel, dot, statusLabel);

        drawer.add(spacer, versionInfo);
        drawer.setFlexGrow(1, spacer);

        addToDrawer(drawer);
    }

    private SideNavItem createNavItem(String label, String route, VaadinIcon icon) {
        SideNavItem item = new SideNavItem(label, route, icon.create());
        item.getStyle().set("color", SIDEBAR_TEXT);
        item.getStyle().set("font-weight", "500");
        item.getStyle().set("font-size", "14px");
        item.getStyle().set("padding", "12px 20px");
        item.getStyle().set("border-radius", "12px");
        item.getStyle().set("margin", "2px 12px");
        item.getStyle().set("transition", "all 0.2s");

        // Hover effect
        item.getElement().addEventListener("mouseenter", e -> {
            item.getStyle().set("background", SIDEBAR_HOVER);
            item.getStyle().set("color", TEXT_PRIMARY);
        });
        item.getElement().addEventListener("mouseleave", e -> {
            item.getStyle().set("background", "transparent");
            item.getStyle().set("color", SIDEBAR_TEXT);
        });

        return item;
    }
}
