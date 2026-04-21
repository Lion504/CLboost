package com.clbooster.app.views;

import com.clbooster.app.views.util.StyleConstants;

import jakarta.annotation.security.PermitAll;
import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.clbooster.app.backend.service.profile.User;
import com.clbooster.app.backend.service.settings.Settings;
import com.clbooster.app.backend.service.settings.SettingsService;
import com.clbooster.app.i18n.TranslationService;
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

@PermitAll
public class MainLayout extends AppLayout {
    private static final String VAL_0_1EM = "0.1em";

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

    private final transient AuthenticationService authService;
    private final TranslationService translationService;

    public MainLayout() {
        this.authService = new AuthenticationService();
        this.translationService = new TranslationService();
        setPrimarySection(Section.DRAWER);

        // Apply user language preference BEFORE building the layout so all
        // getTranslation() calls in createDrawer() use the correct locale.
        User currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            SettingsService settingsService = new SettingsService();
            Settings settings = settingsService.getSettings(currentUser.getPin());
            if (settings != null && settings.getLanguage() != null) {
                translationService.setLanguage(settings.getLanguage());
            }
        }

        createHeader();
        createDrawer();
    }

    @Override
    protected void onAttach(com.vaadin.flow.component.AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        // Locale was already applied in the constructor.
        // Nothing extra needed here for language support.
    }

    private void createHeader() {
        // Drawer toggle
        DrawerToggle toggle = new DrawerToggle();
        toggle.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);

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
        avatarWrapper.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);
        avatarWrapper.getStyle().set(StyleConstants.CSS_TRANSITION, "transform 0.2s");

        // Get current user from auth service
        User currentUser = authService.getCurrentUser();
        String userName = currentUser != null ? currentUser.getFirstName() + " " + currentUser.getLastName() : "Guest";
        String userInitials = "G";
        if (currentUser != null) {
            String first = currentUser.getFirstName() != null && !currentUser.getFirstName().isBlank()
                    ? currentUser.getFirstName().substring(0, 1)
                    : "U";
            String last = currentUser.getLastName() != null && !currentUser.getLastName().isBlank()
                    ? currentUser.getLastName().substring(0, 1)
                    : "";
            userInitials = (first + last).toUpperCase();
        }

        Avatar avatar = new Avatar(userName);
        avatar.setColorIndex(2);
        avatar.getStyle().set(StyleConstants.CSS_BORDER, "2px solid " + BG_WHITE);
        avatar.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 2px 8px rgba(0, 0, 0, 0.1)");

        avatarWrapper.add(avatar);

        // Create context menu for avatar dropdown
        ContextMenu avatarMenu = new ContextMenu();
        avatarMenu.setTarget(avatarWrapper);
        avatarMenu.setOpenOnClick(true);

        // Add menu items
        avatarMenu.addItem(translationService.translate("mainlayout.profile"),
                e -> getUI().ifPresent(ui -> ui.navigate(ProfileView.class)));
        avatarMenu.addItem(translationService.translate("mainlayout.settings"),
                e -> getUI().ifPresent(ui -> ui.navigate(SettingsView.class)));
        avatarMenu.addSeparator();
        avatarMenu.addItem(translationService.translate("mainlayout.signOut"), e -> {
            authService.logout();
            getUI().ifPresent(ui -> ui.navigate(LoginView.class));
        });

        avatarWrapper.getElement().addEventListener(StyleConstants.VAL_MOUSEENTER, e -> {
            avatarWrapper.getStyle().set(StyleConstants.CSS_TRANSFORM, "scale(1.05)");
        });
        avatarWrapper.getElement().addEventListener(StyleConstants.VAL_MOUSELEAVE, e -> {
            avatarWrapper.getStyle().set(StyleConstants.CSS_TRANSFORM, "scale(1)");
        });

        rightSide.add(notifBtn, avatarWrapper);

        HorizontalLayout header = new HorizontalLayout(toggle, rightSide);
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle().set(StyleConstants.CSS_PADDING, "12px 24px");
        header.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        header.getStyle().set("border-bottom", "1px solid rgba(0, 0, 0, 0.05)");
        // No expand — toggle stays left, rightSide hugs the right naturally
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        addToNavbar(true, header);
    }

    private Button createIconButton(VaadinIcon icon) {
        Button btn = new Button(icon.create());
        btn.getStyle().set(StyleConstants.CSS_BACKGROUND, StyleConstants.VAL_TRANSPARENT);
        btn.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        btn.getStyle().set(StyleConstants.CSS_PADDING, "8px");
        btn.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "10px");
        btn.getStyle().set(StyleConstants.CSS_BORDER, "none");
        btn.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);
        btn.getStyle().set(StyleConstants.CSS_TRANSITION, StyleConstants.VAL_ALL_0_2S);

        btn.getElement().addEventListener(StyleConstants.VAL_MOUSEENTER, e -> {
            btn.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_GRAY);
            btn.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        });
        btn.getElement().addEventListener(StyleConstants.VAL_MOUSELEAVE, e -> {
            btn.getStyle().set(StyleConstants.CSS_BACKGROUND, StyleConstants.VAL_TRANSPARENT);
            btn.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        });

        return btn;
    }

    private void createDrawer() {
        VerticalLayout drawer = new VerticalLayout();
        drawer.setPadding(false);
        drawer.setSpacing(false);
        drawer.getStyle().set(StyleConstants.CSS_HEIGHT, "100%");
        drawer.getStyle().set(StyleConstants.CSS_BACKGROUND,
                "linear-gradient(180deg, " + SIDEBAR_BG + " 0%, " + SIDEBAR_HOVER + " 100%)");

        // Logo section
        HorizontalLayout logoSection = new HorizontalLayout();
        logoSection.setAlignItems(FlexComponent.Alignment.CENTER);
        logoSection.getStyle().set("gap", "12px");
        logoSection.getStyle().set(StyleConstants.CSS_PADDING, "24px 20px");
        logoSection.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);

        Div logoIcon = new Div();
        logoIcon.getStyle().set(StyleConstants.CSS_WIDTH, "36px");
        logoIcon.getStyle().set(StyleConstants.CSS_HEIGHT, "36px");
        logoIcon.getStyle().set(StyleConstants.CSS_BACKGROUND, PRIMARY);
        logoIcon.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "10px");
        logoIcon.getStyle().set(StyleConstants.CSS_DISPLAY, "flex");
        logoIcon.getStyle().set(StyleConstants.CSS_ALIGN_ITEMS, StyleConstants.VAL_CENTER);
        logoIcon.getStyle().set(StyleConstants.CSS_JUSTIFY_CONTENT, StyleConstants.VAL_CENTER);
        logoIcon.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 4px 12px rgba(0, 122, 255, 0.3)");
        logoIcon.add(VaadinIcon.SPARK_LINE.create());

        Span logoText = new Span("CL Booster");
        logoText.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        logoText.getStyle().set(StyleConstants.CSS_FONT_SIZE, "20px");
        logoText.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        logoText.getStyle().set(StyleConstants.CSS_LETTER_SPACING, "-0.025em");

        logoSection.add(logoIcon, logoText);
        logoSection.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("dashboard")));

        // Navigation
        SideNav nav = new SideNav();
        nav.getStyle().set(StyleConstants.CSS_MARGIN_TOP, "8px");

        // Main section
        Span mainLabel = new Span(translationService.translate("nav.section.main"));
        mainLabel.getStyle().set(StyleConstants.CSS_FONT_SIZE, "11px");
        mainLabel.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        mainLabel.getStyle().set(StyleConstants.CSS_COLOR, SIDEBAR_LABEL);
        mainLabel.getStyle().set(StyleConstants.CSS_PADDING, "0 20px");
        mainLabel.getStyle().set(StyleConstants.CSS_LETTER_SPACING, VAL_0_1EM);

        SideNavItem dashboard = createNavItem(translationService.translate("nav.dashboard"), "dashboard",
                VaadinIcon.DASHBOARD);
        SideNavItem generate = createNavItem(translationService.translate("nav.generator"), "generator-wizard",
                VaadinIcon.MAGIC);
        SideNavItem history = createNavItem(translationService.translate("nav.history"), "history", VaadinIcon.CLOCK);

        // Add Resume section
        Span toolsLabel = new Span(translationService.translate("nav.section.tools"));
        toolsLabel.getStyle().set(StyleConstants.CSS_FONT_SIZE, "11px");
        toolsLabel.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        toolsLabel.getStyle().set(StyleConstants.CSS_COLOR, SIDEBAR_LABEL);
        toolsLabel.getStyle().set(StyleConstants.CSS_PADDING, "24px 20px 8px");
        toolsLabel.getStyle().set(StyleConstants.CSS_LETTER_SPACING, VAL_0_1EM);

        SideNavItem resume = createNavItem(translationService.translate("nav.resume"), "resume", VaadinIcon.FILE_TEXT);

        // Support section
        Span supportLabel = new Span(translationService.translate("nav.section.support"));
        supportLabel.getStyle().set(StyleConstants.CSS_FONT_SIZE, "11px");
        supportLabel.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        supportLabel.getStyle().set(StyleConstants.CSS_COLOR, SIDEBAR_LABEL);
        supportLabel.getStyle().set(StyleConstants.CSS_PADDING, "24px 20px 8px");
        supportLabel.getStyle().set(StyleConstants.CSS_LETTER_SPACING, VAL_0_1EM);

        SideNavItem help = createNavItem(translationService.translate("nav.help"), "help", VaadinIcon.QUESTION_CIRCLE);

        nav.addItem(dashboard, generate, history);

        // Add items with custom styling
        drawer.add(logoSection, mainLabel, nav, toolsLabel);

        // Create separate nav for tools items
        SideNav toolsNav = new SideNav();
        toolsNav.addItem(resume);
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
        versionInfo.getStyle().set(StyleConstants.CSS_PADDING, "20px");
        versionInfo.getStyle().set("gap", "8px");

        Span versionLabel = new Span(translationService.translate("mainlayout.version") + ": 4.0.0");
        versionLabel.getStyle().set(StyleConstants.CSS_FONT_SIZE, "12px");
        versionLabel.getStyle().set(StyleConstants.CSS_COLOR, SIDEBAR_LABEL);

        Div dot = new Div();
        dot.getStyle().set(StyleConstants.CSS_WIDTH, "6px");
        dot.getStyle().set(StyleConstants.CSS_HEIGHT, "6px");
        dot.getStyle().set(StyleConstants.CSS_BACKGROUND, "#34C759");
        dot.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "50%");

        Span statusLabel = new Span(translationService.translate("mainlayout.online"));
        statusLabel.getStyle().set(StyleConstants.CSS_FONT_SIZE, "12px");
        statusLabel.getStyle().set(StyleConstants.CSS_COLOR, SIDEBAR_TEXT);

        versionInfo.add(versionLabel, dot, statusLabel);

        drawer.add(spacer, versionInfo);
        drawer.setFlexGrow(1, spacer);

        addToDrawer(drawer);
    }

    private SideNavItem createNavItem(String label, String route, VaadinIcon icon) {
        SideNavItem item = new SideNavItem(label, route, icon.create());
        item.getStyle().set(StyleConstants.CSS_COLOR, SIDEBAR_TEXT);
        item.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "500");
        item.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        item.getStyle().set(StyleConstants.CSS_PADDING, "12px 20px");
        item.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "12px");
        item.getStyle().set(StyleConstants.CSS_MARGIN, "2px 12px");
        item.getStyle().set(StyleConstants.CSS_TRANSITION, StyleConstants.VAL_ALL_0_2S);

        // Hover effect
        item.getElement().addEventListener(StyleConstants.VAL_MOUSEENTER, e -> {
            item.getStyle().set(StyleConstants.CSS_BACKGROUND, SIDEBAR_HOVER);
            item.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        });
        item.getElement().addEventListener(StyleConstants.VAL_MOUSELEAVE, e -> {
            item.getStyle().set(StyleConstants.CSS_BACKGROUND, StyleConstants.VAL_TRANSPARENT);
            item.getStyle().set(StyleConstants.CSS_COLOR, SIDEBAR_TEXT);
        });

        return item;
    }
}
