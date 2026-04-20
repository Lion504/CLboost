package com.clbooster.app.views;

import jakarta.annotation.security.PermitAll;
import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.clbooster.app.backend.service.profile.User;
import com.clbooster.app.backend.service.profile.UserService;
import com.clbooster.app.backend.service.settings.Settings;
import com.clbooster.app.backend.service.settings.SettingsService;
import com.clbooster.app.i18n.TranslationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "settings", layout = MainLayout.class)
@PageTitle("Settings | CL Booster")
@PermitAll
public class SettingsView extends VerticalLayout {
    private static final Logger log = LoggerFactory.getLogger(SettingsView.class);

    // Figma Design System Colors
    private static final String PRIMARY = "#007AFF";
    private static final String TEXT_PRIMARY = "#1d1d1f";
    private static final String TEXT_SECONDARY = "#86868b";
    private static final String BG_WHITE = "#ffffff";
    private static final String BG_GRAY = "#f5f5f7";
    private static final String SUCCESS = "#34C759";

        private final transient SettingsService settingsService;
        private final transient AuthenticationService authService;
        private final transient UserService userService;
    private final TranslationService translationService;
        private transient Settings userSettings;
        private transient User currentUser;

    // Language select
    private Select<String> langSelect;

    // Toggle track elements for updating styles
    private Div emailTrack;
    private Div pushTrack;
    private Div productTrack;
    private Div marketingTrack;
    private Div cloudTrack;
    private Div aiTrack;
    private Div usageTrack;

    // Toggle thumbs for animation
    private Div emailThumb;
    private Div pushThumb;
    private Div productThumb;
    private Div marketingThumb;
    private Div cloudThumb;
    private Div aiThumb;
    private Div usageThumb;

    // Toggle states
    private boolean emailNotifications;
    private boolean pushNotifications;
    private boolean productUpdates;
    private boolean marketing;
    private boolean storeInCloud;
    private boolean allowAiTraining;
    private boolean shareUsageData;

    public SettingsView() {
        this.settingsService = new SettingsService();
        this.authService = new AuthenticationService();
        this.userService = new UserService();
        this.translationService = new TranslationService();
        this.currentUser = authService.getCurrentUser();

        setPadding(true);
        setSpacing(true);
        getStyle().set("gap", "24px");
        getStyle().set("padding", "32px");
        getStyle().set("background", BG_WHITE);
        getStyle().set("font-family",
                "-apple-system, BlinkMacSystemFont, 'SF Pro Text', 'SF Pro Display', system-ui, sans-serif");
        setSizeFull();
        setMaxWidth("900px");

        // Load user settings first
        loadSettings();

        // Page Header
        VerticalLayout pageHeader = new VerticalLayout();
        pageHeader.setPadding(false);
        pageHeader.setSpacing(false);
        pageHeader.getStyle().set("gap", "4px");
        pageHeader.getStyle().set("margin-bottom", "8px");

        H2 title = new H2(translationService.translate("settings.preferences"));
        title.getStyle().set("font-size", "30px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("letter-spacing", "-0.025em");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0");

        Paragraph subtitle = new Paragraph(translationService.translate("settings.preferencesDesc"));
        subtitle.getStyle().set("font-size", "14px");
        subtitle.getStyle().set("color", TEXT_SECONDARY);
        subtitle.getStyle().set("margin", "0");

        pageHeader.add(title, subtitle);

        // Settings Cards
        Div appearanceCard = createAppearanceCard();
        Div languageCard = createLanguageCard();
        Div notificationsCard = createNotificationsCard();
        Div privacyCard = createPrivacyCard();

        // Action buttons
        HorizontalLayout actions = createActionButtons();

        add(pageHeader, appearanceCard, languageCard, notificationsCard, privacyCard, actions);
    }

    private void loadSettings() {
        if (currentUser != null) {
            userSettings = settingsService.getSettings(currentUser.getPin());
            emailNotifications = userSettings.isEmailNotifications();
            pushNotifications = userSettings.isPushNotifications();
            productUpdates = userSettings.isProductUpdates();
            marketing = userSettings.isMarketing();
            storeInCloud = userSettings.isStoreInCloud();
            allowAiTraining = userSettings.isAllowAiTraining();
            shareUsageData = userSettings.isShareUsageData();
        } else {
            userSettings = new Settings();
            emailNotifications = true;
            pushNotifications = false;
            productUpdates = true;
            marketing = false;
            storeInCloud = true;
            allowAiTraining = false;
            shareUsageData = true;
        }
    }

    private Div createAppearanceCard() {
        Div card = createCard();

        H3 title = new H3(translationService.translate("settings.appearance"));
        title.getStyle().set("font-size", "18px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0 0 8px 0");

        Paragraph desc = new Paragraph(translationService.translate("settings.appearance.desc"));
        desc.getStyle().set("font-size", "14px");
        desc.getStyle().set("color", TEXT_SECONDARY);
        desc.getStyle().set("margin", "0");

        card.add(title, desc);

        return card;
    }

    private Div createLanguageCard() {
        Div card = createCard();

        H3 title = new H3(translationService.translate("settings.language"));
        title.getStyle().set("font-size", "18px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0 0 8px 0");

        Paragraph desc = new Paragraph(translationService.translate("settings.languageSelect"));
        desc.getStyle().set("font-size", "14px");
        desc.getStyle().set("color", TEXT_SECONDARY);
        desc.getStyle().set("margin", "0 0 24px 0");

        // Language select
        VerticalLayout selectGroup = new VerticalLayout();
        selectGroup.setPadding(false);
        selectGroup.setSpacing(false);
        selectGroup.getStyle().set("gap", "8px");
        selectGroup.setWidth("280px");

        Span label = new Span(translationService.translate("label.language"));
        label.getStyle().set("font-size", "12px");
        label.getStyle().set("font-weight", "700");
        label.getStyle().set("color", TEXT_SECONDARY);
        label.getStyle().set("text-transform", "uppercase");
        label.getStyle().set("letter-spacing", "0.05em");

        langSelect = new Select<>();
        langSelect.setItems(translationService.translate("settings.langEnglish"),
                translationService.translate("settings.langFinnish"),
                translationService.translate("settings.langPortuguese"),
                translationService.translate("settings.langPersian"),
                translationService.translate("settings.langChinese"),
                translationService.translate("settings.langUrdu"));
        // Convert stored language code to display name
        String savedLanguage = userSettings.getLanguage();
        String displayValue = "English";
        if (savedLanguage != null) {
            if (savedLanguage.contains("Finnish"))
                displayValue = "Suomi";
            else if (savedLanguage.contains("Portuguese"))
                displayValue = "Português";
            else if (savedLanguage.contains("Persian"))
                displayValue = "فارسی";
            else if (savedLanguage.contains("Chinese"))
                displayValue = "中文";
            else if (savedLanguage.contains("Urdu"))
                displayValue = "اردو";
        }
        langSelect.setValue(displayValue);
        langSelect.setWidthFull();
        langSelect.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        langSelect.getStyle().set("--vaadin-input-field-border-radius", "12px");

        // Apply language change immediately when selected
        langSelect.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                String langCode;
                String selectedDisplay = e.getValue();
                if (selectedDisplay.equals(translationService.translate("settings.langFinnish"))) {
                    langCode = "Finnish (Suomi)";
                } else if (selectedDisplay.equals(translationService.translate("settings.langPortuguese"))) {
                    langCode = "Portuguese (Português)";
                } else if (selectedDisplay.equals(translationService.translate("settings.langPersian"))) {
                    langCode = "Persian (فارسی)";
                } else if (selectedDisplay.equals(translationService.translate("settings.langChinese"))) {
                    langCode = "Chinese (中文)";
                } else if (selectedDisplay.equals("اردو")) {
                    langCode = "Urdu (اردو)";
                } else {
                    langCode = "English";
                }
                translationService.setLanguage(langCode);
                Notification.show(
                        translationService.translate("settings.languageChanged") + " " + selectedDisplay + ". "
                                + translationService.translate("settings.saveToApply"),
                        3000, Notification.Position.BOTTOM_END);
            }
        });

        selectGroup.add(label, langSelect);
        card.add(title, desc, selectGroup);

        return card;
    }

    private Div createNotificationsCard() {
        Div card = createCard();

        H3 title = new H3(translationService.translate("settings.notifications"));
        title.getStyle().set("font-size", "18px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0 0 8px 0");

        Paragraph desc = new Paragraph(translationService.translate("settings.notificationsDesc"));
        desc.getStyle().set("font-size", "14px");
        desc.getStyle().set("color", TEXT_SECONDARY);
        desc.getStyle().set("margin", "0 0 24px 0");

        // Toggle items
        VerticalLayout toggles = new VerticalLayout();
        toggles.setPadding(false);
        toggles.setSpacing(false);
        toggles.getStyle().set("gap", "0");

        ToggleResult emailResult = createToggleRow(translationService.translate("settings.emailNotifications"),
                translationService.translate("settings.emailNotifications.desc"), emailNotifications);
        emailTrack = emailResult.track;
        emailThumb = emailResult.thumb;
        emailTrack.addClickListener(e -> {
            emailNotifications = !emailNotifications;
            updateToggleVisual(emailTrack, emailThumb, emailNotifications);
        });

        ToggleResult pushResult = createToggleRow(translationService.translate("settings.pushNotifications"),
                translationService.translate("settings.pushNotifications.desc"), pushNotifications);
        pushTrack = pushResult.track;
        pushThumb = pushResult.thumb;
        pushTrack.addClickListener(e -> {
            pushNotifications = !pushNotifications;
            updateToggleVisual(pushTrack, pushThumb, pushNotifications);
        });

        ToggleResult productResult = createToggleRow(translationService.translate("settings.productUpdates"),
                translationService.translate("settings.productUpdates.desc"), productUpdates);
        productTrack = productResult.track;
        productThumb = productResult.thumb;
        productTrack.addClickListener(e -> {
            productUpdates = !productUpdates;
            updateToggleVisual(productTrack, productThumb, productUpdates);
        });

        ToggleResult marketingResult = createToggleRow(translationService.translate("settings.marketing"),
                translationService.translate("settings.marketing.desc"), marketing);
        marketingTrack = marketingResult.track;
        marketingThumb = marketingResult.thumb;
        marketingTrack.addClickListener(e -> {
            marketing = !marketing;
            updateToggleVisual(marketingTrack, marketingThumb, marketing);
        });

        toggles.add(emailResult.row, pushResult.row, productResult.row, marketingResult.row);

        card.add(title, desc, toggles);

        return card;
    }

    private static class ToggleResult {
        final HorizontalLayout row;
        final Div track;
        final Div thumb;

        ToggleResult(HorizontalLayout row, Div track, Div thumb) {
            this.row = row;
            this.track = track;
            this.thumb = thumb;
        }
    }

    private ToggleResult createToggleRow(String title, String description, boolean enabled) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.getStyle().set("padding", "16px 0");
        row.getStyle().set("border-bottom", "1px solid rgba(0, 0, 0, 0.05)");

        VerticalLayout textGroup = new VerticalLayout();
        textGroup.setPadding(false);
        textGroup.setSpacing(false);
        textGroup.getStyle().set("gap", "4px");

        Span titleSpan = new Span(title);
        titleSpan.getStyle().set("font-size", "15px");
        titleSpan.getStyle().set("font-weight", "600");
        titleSpan.getStyle().set("color", TEXT_PRIMARY);

        Span descSpan = new Span(description);
        descSpan.getStyle().set("font-size", "13px");
        descSpan.getStyle().set("color", TEXT_SECONDARY);

        textGroup.add(titleSpan, descSpan);

        // Create toggle
        Div track = new Div();
        track.getStyle().set("width", "48px");
        track.getStyle().set("height", "28px");
        track.getStyle().set("border-radius", "9999px");
        track.getStyle().set("position", "relative");
        track.getStyle().set("cursor", "pointer");
        track.getStyle().set("transition", "background 0.2s");
        track.getStyle().set("flex-shrink", "0");

        Div thumb = new Div();
        thumb.getStyle().set("width", "24px");
        thumb.getStyle().set("height", "24px");
        thumb.getStyle().set("background", "white");
        thumb.getStyle().set("border-radius", "50%");
        thumb.getStyle().set("position", "absolute");
        thumb.getStyle().set("top", "2px");
        thumb.getStyle().set("box-shadow", "0 2px 4px rgba(0,0,0,0.2)");
        thumb.getStyle().set("transition", "all 0.2s");

        track.add(thumb);

        // Set initial state
        updateToggleVisual(track, thumb, enabled);

        row.add(textGroup, track);
        row.expand(textGroup);

        return new ToggleResult(row, track, thumb);
    }

    private void updateToggleVisual(Div track, Div thumb, boolean enabled) {
        track.getStyle().set("background", enabled ? SUCCESS : "rgba(0, 0, 0, 0.2)");
        thumb.getStyle().set("inset-inline-start", enabled ? "auto" : "2px");
        thumb.getStyle().set("inset-inline-end", enabled ? "2px" : "auto");
        thumb.getStyle().remove(enabled ? "left" : "right");
    }

    private Div createPrivacyCard() {
        Div card = createCard();

        H3 title = new H3(translationService.translate("settings.privacy"));
        title.getStyle().set("font-size", "18px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0 0 8px 0");

        Paragraph desc = new Paragraph(translationService.translate("settings.privacyDesc"));
        desc.getStyle().set("font-size", "14px");
        desc.getStyle().set("color", TEXT_SECONDARY);
        desc.getStyle().set("margin", "0 0 24px 0");

        // Privacy options
        VerticalLayout options = new VerticalLayout();
        options.setPadding(false);
        options.setSpacing(false);
        options.getStyle().set("gap", "0");

        ToggleResult cloudResult = createToggleRow(translationService.translate("settings.cloudStorage"),
                translationService.translate("settings.cloudStorage.desc"), storeInCloud);
        cloudTrack = cloudResult.track;
        cloudThumb = cloudResult.thumb;
        cloudTrack.addClickListener(e -> {
            storeInCloud = !storeInCloud;
            updateToggleVisual(cloudTrack, cloudThumb, storeInCloud);
        });

        ToggleResult aiResult = createToggleRow(translationService.translate("settings.aiTraining"),
                translationService.translate("settings.aiTraining.desc"), allowAiTraining);
        aiTrack = aiResult.track;
        aiThumb = aiResult.thumb;
        aiTrack.addClickListener(e -> {
            allowAiTraining = !allowAiTraining;
            updateToggleVisual(aiTrack, aiThumb, allowAiTraining);
        });

        ToggleResult usageResult = createToggleRow(translationService.translate("settings.usageData"),
                translationService.translate("settings.usageData.desc"), shareUsageData);
        usageTrack = usageResult.track;
        usageThumb = usageResult.thumb;
        usageTrack.addClickListener(e -> {
            shareUsageData = !shareUsageData;
            updateToggleVisual(usageTrack, usageThumb, shareUsageData);
        });

        options.add(cloudResult.row, aiResult.row, usageResult.row);

        // Danger zone
        Div dangerZone = new Div();
        dangerZone.getStyle().set("margin-top", "24px");
        dangerZone.getStyle().set("padding-top", "24px");
        dangerZone.getStyle().set("border-top", "1px solid rgba(255, 59, 48, 0.2)");

        HorizontalLayout dangerRow = new HorizontalLayout();
        dangerRow.setWidthFull();
        dangerRow.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout dangerText = new VerticalLayout();
        dangerText.setPadding(false);
        dangerText.setSpacing(false);
        dangerText.getStyle().set("gap", "4px");

        Span dangerTitle = new Span(translationService.translate("settings.deleteAccountTitle"));
        dangerTitle.getStyle().set("font-size", "15px");
        dangerTitle.getStyle().set("font-weight", "600");
        dangerTitle.getStyle().set("color", "#FF3B30");

        Span dangerDesc = new Span(translationService.translate("settings.deleteAccountDesc2"));
        dangerDesc.getStyle().set("font-size", "13px");
        dangerDesc.getStyle().set("color", TEXT_SECONDARY);

        dangerText.add(dangerTitle, dangerDesc);

        Button deleteBtn = new Button(translationService.translate("action.delete"));
        deleteBtn.getStyle().set("background", "rgba(255, 59, 48, 0.1)");
        deleteBtn.getStyle().set("color", "#FF3B30");
        deleteBtn.getStyle().set("font-weight", "600");
        deleteBtn.getStyle().set("border-radius", "9999px");
        deleteBtn.getStyle().set("padding", "8px 16px");
        deleteBtn.getStyle().set("border", "none");
        deleteBtn.getStyle().set("cursor", "pointer");
        deleteBtn.getStyle().set("transition", "all 0.2s");

        deleteBtn.getElement().addEventListener("mouseenter", e -> {
            deleteBtn.getStyle().set("background", "rgba(255, 59, 48, 0.2)");
        });
        deleteBtn.getElement().addEventListener("mouseleave", e -> {
            deleteBtn.getStyle().set("background", "rgba(255, 59, 48, 0.1)");
        });

        deleteBtn.addClickListener(e -> showDeleteAccountDialog());

        dangerRow.add(dangerText, deleteBtn);
        dangerRow.expand(dangerText);

        dangerZone.add(dangerRow);

        card.add(title, desc, options, dangerZone);

        return card;
    }

    private void showDeleteAccountDialog() {
        if (currentUser == null) {
            Notification.show("Error: You must be logged in to delete your account", 3000,
                    Notification.Position.TOP_CENTER);
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(translationService.translate("settings.deleteAccountTitle"));

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(true);

        Paragraph warning = new Paragraph(translationService.translate("settings.deleteConfirm"));
        warning.getStyle().set("color", TEXT_SECONDARY);

        PasswordField passwordField = new PasswordField(translationService.translate("label.password"));
        passwordField.setWidthFull();
        passwordField.getStyle().set("--vaadin-input-field-background", BG_GRAY);

        content.add(warning, passwordField);

        Button cancelBtn = new Button(translationService.translate("action.cancel"), e -> dialog.close());
        cancelBtn.getStyle().set("background", "transparent");
        cancelBtn.getStyle().set("color", TEXT_SECONDARY);

        Button confirmBtn = new Button(translationService.translate("settings.deleteAccountTitle"), e -> {
            String password = passwordField.getValue();
            if (password == null || password.isEmpty()) {
                Notification.show(translationService.translate("settings.enterPassword"), 3000,
                        Notification.Position.TOP_CENTER);
                return;
            }

            if (userService.loginUser(currentUser.getUsername(), password) != null) {
                settingsService.deleteSettings(currentUser.getPin());
                if (userService.deleteUser(currentUser)) {
                    Notification.show("Account deleted successfully", 3000, Notification.Position.BOTTOM_END);
                    authService.logout();
                    dialog.close();
                    getUI().ifPresent(ui -> ui.navigate(LoginView.class));
                } else {
                    Notification.show("Error deleting account. Please try again.", 3000,
                            Notification.Position.TOP_CENTER);
                }
            } else {
                Notification.show("Incorrect password", 3000, Notification.Position.TOP_CENTER);
            }
        });
        confirmBtn.getStyle().set("background", "#FF3B30");
        confirmBtn.getStyle().set("color", "white");

        dialog.add(content);
        dialog.getFooter().add(cancelBtn, confirmBtn);
        dialog.open();
    }

    private HorizontalLayout createActionButtons() {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setWidthFull();
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        actions.getStyle().set("gap", "12px");
        actions.getStyle().set("margin-top", "8px");

        Button discardBtn = new Button(translationService.translate("action.discard"), e -> {
            loadSettings();
            getUI().ifPresent(ui -> ui.getPage().reload());
            Notification.show(translationService.translate("settings.changesDiscarded"), 2000,
                    Notification.Position.BOTTOM_END);
        });
        discardBtn.getStyle().set("background", "transparent");
        discardBtn.getStyle().set("color", TEXT_SECONDARY);
        discardBtn.getStyle().set("font-weight", "600");
        discardBtn.getStyle().set("border-radius", "9999px");
        discardBtn.getStyle().set("padding", "12px 24px");
        discardBtn.getStyle().set("border", "none");
        discardBtn.getStyle().set("cursor", "pointer");
        discardBtn.getStyle().set("transition", "all 0.2s");

        discardBtn.getElement().addEventListener("mouseenter", e -> {
            discardBtn.getStyle().set("background", "rgba(0, 0, 0, 0.05)");
            discardBtn.getStyle().set("color", TEXT_PRIMARY);
        });
        discardBtn.getElement().addEventListener("mouseleave", e -> {
            discardBtn.getStyle().set("background", "transparent");
            discardBtn.getStyle().set("color", TEXT_SECONDARY);
        });

        Button saveBtn = createPrimaryButton(translationService.translate("action.save"), () -> saveSettings());

        actions.add(discardBtn, saveBtn);

        return actions;
    }

    private void saveSettings() {
        if (currentUser == null) {
            Notification.show("Error: You must be logged in to save settings", 3000, Notification.Position.TOP_CENTER);
            return;
        }

        String languageValue = langSelect.getValue();
        if (languageValue == null) {
            languageValue = "English";
        }

        userSettings.setUserPin(currentUser.getPin());
        userSettings.setTheme("system");
        userSettings.setLanguage(languageValue);
        userSettings.setEmailNotifications(emailNotifications);
        userSettings.setPushNotifications(pushNotifications);
        userSettings.setProductUpdates(productUpdates);
        userSettings.setMarketing(marketing);
        userSettings.setStoreInCloud(storeInCloud);
        userSettings.setAllowAiTraining(allowAiTraining);
        userSettings.setShareUsageData(shareUsageData);

        log.debug("Saving settings from SettingsView");

        try {
            boolean success = settingsService.saveSettings(userSettings);
            if (success) {
                Notification.show("Settings saved!", 2000, Notification.Position.BOTTOM_END);
                // Reload so MainLayout and all components rebuild with the new locale
                getUI().ifPresent(ui -> ui.getPage().reload());
            } else {
                Notification.show("Error: Failed to save settings. The database may be unavailable.", 5000,
                        Notification.Position.TOP_CENTER);
                log.warn("Failed to save settings in SettingsView");
            }
        } catch (Exception e) {
            Notification.show("Database error. Please try again later.", 5000, Notification.Position.TOP_CENTER);
            log.error("Unexpected exception while saving settings", e);
        }
    }

    private Div createCard() {
        Div card = new Div();
        card.getStyle().set("background", BG_WHITE);
        card.getStyle().set("border", "1px solid rgba(0, 0, 0, 0.05)");
        card.getStyle().set("border-radius", "24px");
        card.getStyle().set("padding", "28px");
        card.getStyle().set("box-shadow", "0 2px 12px rgba(0, 0, 0, 0.04)");
        card.getStyle().set("transition", "all 0.3s");

        card.getElement().addEventListener("mouseenter", e -> {
            card.getStyle().set("box-shadow", "0 8px 24px rgba(0, 0, 0, 0.06)");
        });
        card.getElement().addEventListener("mouseleave", e -> {
            card.getStyle().set("box-shadow", "0 2px 12px rgba(0, 0, 0, 0.04)");
        });

        return card;
    }

    private Button createPrimaryButton(String text, Runnable action) {
        Button btn = new Button(text, e -> action.run());
        btn.getStyle().set("background", "linear-gradient(135deg, " + PRIMARY + " 0%, #5AC8FA 100%)");
        btn.getStyle().set("color", "white");
        btn.getStyle().set("font-weight", "600");
        btn.getStyle().set("font-size", "14px");
        btn.getStyle().set("border-radius", "9999px");
        btn.getStyle().set("border", "none");
        btn.getStyle().set("padding", "12px 24px");
        btn.getStyle().set("box-shadow", "0 10px 15px -3px rgba(0, 122, 255, 0.3)");
        btn.getStyle().set("transition", "all 0.2s");
        btn.getStyle().set("cursor", "pointer");

        btn.getElement().addEventListener("mouseenter", e -> {
            btn.getStyle().set("filter", "brightness(1.1)");
            btn.getStyle().set("transform", "translateY(-1px)");
        });
        btn.getElement().addEventListener("mouseleave", e -> {
            btn.getStyle().set("filter", "brightness(1)");
            btn.getStyle().set("transform", "translateY(0)");
        });

        return btn;
    }
}
