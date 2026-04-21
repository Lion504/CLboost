package com.clbooster.app.views;

import com.clbooster.app.views.util.StyleConstants;
import com.clbooster.app.views.util.ViewComponents;

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
    private static final String COLOR_DANGER = "#FF3B30";
    private static final String MARGIN_24 = "0 0 24px 0";
    private static final String MARGIN_8 = "0 0 8px 0";
    private static final Logger log = LoggerFactory.getLogger(SettingsView.class);

    // Figma Design System Colors
    private static final String PRIMARY = "#007AFF";
    private static final String TEXT_PRIMARY = "#1d1d1f";
    private static final String TEXT_SECONDARY = "#86868b";
    private static final String BG_WHITE = "#ffffff";
    private static final String BG_GRAY = "#f5f5f7";
    private static final String SUCCESS = "#34C759";
    private static final String LANG_ENGLISH = "English";
    private static final String SETTINGS_DELETE_ACCOUNT_TITLE = "settings.deleteAccountTitle";

    private final transient SettingsService settingsService;
    private final transient AuthenticationService authService;
    private final transient UserService userService;
    private final TranslationService translationService;
    private transient Settings userSettings;
    private transient User currentUser;

    // Language select
    private Select<String> langSelect;

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
        getStyle().set(StyleConstants.CSS_PADDING, "32px");
        getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
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
        pageHeader.getStyle().set(StyleConstants.CSS_MARGIN_BOTTOM, "8px");

        H2 title = new H2(translationService.translate("settings.preferences"));
        title.getStyle().set(StyleConstants.CSS_FONT_SIZE, "30px");
        title.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        title.getStyle().set(StyleConstants.CSS_LETTER_SPACING, "-0.025em");
        title.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        title.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        Paragraph subtitle = new Paragraph(translationService.translate("settings.preferencesDesc"));
        subtitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        subtitle.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        subtitle.getStyle().set(StyleConstants.CSS_MARGIN, "0");

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
        styleCardTitle(title);

        Paragraph desc = new Paragraph(translationService.translate("settings.appearance.desc"));
        styleCardDescription(desc, "0");

        card.add(title, desc);

        return card;
    }

    private Div createLanguageCard() {
        Div card = createCard();

        H3 title = new H3(translationService.translate("settings.language"));
        styleCardTitle(title);

        Paragraph desc = new Paragraph(translationService.translate("settings.languageSelect"));
        styleCardDescription(desc, MARGIN_24);

        // Language select
        VerticalLayout selectGroup = new VerticalLayout();
        selectGroup.setPadding(false);
        selectGroup.setSpacing(false);
        selectGroup.getStyle().set("gap", "8px");
        selectGroup.setWidth("280px");

        Span label = new Span(translationService.translate("label.language"));
        label.getStyle().set(StyleConstants.CSS_FONT_SIZE, "12px");
        label.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        label.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        label.getStyle().set(StyleConstants.CSS_TEXT_TRANSFORM, "uppercase");
        label.getStyle().set(StyleConstants.CSS_LETTER_SPACING, "0.05em");

        langSelect = new Select<>();
        langSelect.setItems(translationService.translate("settings.langEnglish"),
                translationService.translate("settings.langFinnish"),
                translationService.translate("settings.langPortuguese"),
                translationService.translate("settings.langPersian"),
                translationService.translate("settings.langChinese"),
                translationService.translate("settings.langUrdu"));
        // Convert stored language code to display name
        String savedLanguage = userSettings.getLanguage();
        String displayValue = getDisplayLanguage(savedLanguage);
        langSelect.setValue(displayValue);
        langSelect.setWidthFull();
        langSelect.getStyle().set("--vaadin-input-field-background", BG_GRAY);
        langSelect.getStyle().set("--vaadin-input-field-border-radius", "12px");

        // Apply language change immediately when selected
        langSelect.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                String selectedDisplay = e.getValue();
                String langCode = getLanguageCode(selectedDisplay);
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

    private String getDisplayLanguage(String savedLanguage) {
        String displayValue = LANG_ENGLISH;
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
        return displayValue;
    }

    private String getLanguageCode(String selectedDisplay) {
        String langCode;
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
            langCode = LANG_ENGLISH;
        }
        return langCode;
    }

    private Div createNotificationsCard() {
        Div card = createCard();

        H3 title = new H3(translationService.translate("settings.notifications"));
        styleCardTitle(title);

        Paragraph desc = new Paragraph(translationService.translate("settings.notificationsDesc"));
        styleCardDescription(desc, MARGIN_24);

        // Toggle items
        VerticalLayout toggles = new VerticalLayout();
        toggles.setPadding(false);
        toggles.setSpacing(false);
        toggles.getStyle().set("gap", "0");

        ToggleResult emailResult = createToggleRow(translationService.translate("settings.emailNotifications"),
                translationService.translate("settings.emailNotifications.desc"), emailNotifications);
        Div emailTrack = emailResult.track;
        Div emailThumb = emailResult.thumb;
        emailTrack.addClickListener(e -> {
            emailNotifications = !emailNotifications;
            updateToggleVisual(emailTrack, emailThumb, emailNotifications);
        });

        ToggleResult pushResult = createToggleRow(translationService.translate("settings.pushNotifications"),
                translationService.translate("settings.pushNotifications.desc"), pushNotifications);
        Div pushTrack = pushResult.track;
        Div pushThumb = pushResult.thumb;
        pushTrack.addClickListener(e -> {
            pushNotifications = !pushNotifications;
            updateToggleVisual(pushTrack, pushThumb, pushNotifications);
        });

        ToggleResult productResult = createToggleRow(translationService.translate("settings.productUpdates"),
                translationService.translate("settings.productUpdates.desc"), productUpdates);
        Div productTrack = productResult.track;
        Div productThumb = productResult.thumb;
        productTrack.addClickListener(e -> {
            productUpdates = !productUpdates;
            updateToggleVisual(productTrack, productThumb, productUpdates);
        });

        ToggleResult marketingResult = createToggleRow(translationService.translate("settings.marketing"),
                translationService.translate("settings.marketing.desc"), marketing);
        Div marketingTrack = marketingResult.track;
        Div marketingThumb = marketingResult.thumb;
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
        row.getStyle().set(StyleConstants.CSS_PADDING, "16px 0");
        row.getStyle().set("border-bottom", "1px solid rgba(0, 0, 0, 0.05)");

        VerticalLayout textGroup = new VerticalLayout();
        textGroup.setPadding(false);
        textGroup.setSpacing(false);
        textGroup.getStyle().set("gap", "4px");

        Span titleSpan = new Span(title);
        titleSpan.getStyle().set(StyleConstants.CSS_FONT_SIZE, "15px");
        titleSpan.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "600");
        titleSpan.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);

        Span descSpan = new Span(description);
        descSpan.getStyle().set(StyleConstants.CSS_FONT_SIZE, "13px");
        descSpan.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);

        textGroup.add(titleSpan, descSpan);

        // Create toggle
        Div track = new Div();
        track.getStyle().set(StyleConstants.CSS_WIDTH, "48px");
        track.getStyle().set(StyleConstants.CSS_HEIGHT, "28px");
        track.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX);
        track.getStyle().set("position", "relative");
        track.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);
        track.getStyle().set(StyleConstants.CSS_TRANSITION, "background 0.2s");
        track.getStyle().set("flex-shrink", "0");

        Div thumb = new Div();
        thumb.getStyle().set(StyleConstants.CSS_WIDTH, "24px");
        thumb.getStyle().set(StyleConstants.CSS_HEIGHT, "24px");
        thumb.getStyle().set(StyleConstants.CSS_BACKGROUND, StyleConstants.VAL_WHITE);
        thumb.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "50%");
        thumb.getStyle().set("position", "absolute");
        thumb.getStyle().set("top", "2px");
        thumb.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 2px 4px rgba(0,0,0,0.2)");
        thumb.getStyle().set(StyleConstants.CSS_TRANSITION, StyleConstants.VAL_ALL_0_2S);

        track.add(thumb);

        // Set initial state
        updateToggleVisual(track, thumb, enabled);

        row.add(textGroup, track);
        row.expand(textGroup);

        return new ToggleResult(row, track, thumb);
    }

    private void updateToggleVisual(Div track, Div thumb, boolean enabled) {
        track.getStyle().set(StyleConstants.CSS_BACKGROUND, enabled ? SUCCESS : "rgba(0, 0, 0, 0.2)");
        thumb.getStyle().set("inset-inline-start", enabled ? "auto" : "2px");
        thumb.getStyle().set("inset-inline-end", enabled ? "2px" : "auto");
        thumb.getStyle().remove(enabled ? "left" : "right");
    }

    private Div createPrivacyCard() {
        Div card = createCard();

        H3 title = new H3(translationService.translate("settings.privacy"));
        styleCardTitle(title);

        Paragraph desc = new Paragraph(translationService.translate("settings.privacyDesc"));
        styleCardDescription(desc, MARGIN_24);

        // Privacy options
        VerticalLayout options = new VerticalLayout();
        options.setPadding(false);
        options.setSpacing(false);
        options.getStyle().set("gap", "0");

        ToggleResult cloudResult = createToggleRow(translationService.translate("settings.cloudStorage"),
                translationService.translate("settings.cloudStorage.desc"), storeInCloud);
        Div cloudTrack = cloudResult.track;
        Div cloudThumb = cloudResult.thumb;
        cloudTrack.addClickListener(e -> {
            storeInCloud = !storeInCloud;
            updateToggleVisual(cloudTrack, cloudThumb, storeInCloud);
        });

        ToggleResult aiResult = createToggleRow(translationService.translate("settings.aiTraining"),
                translationService.translate("settings.aiTraining.desc"), allowAiTraining);
        Div aiTrack = aiResult.track;
        Div aiThumb = aiResult.thumb;
        aiTrack.addClickListener(e -> {
            allowAiTraining = !allowAiTraining;
            updateToggleVisual(aiTrack, aiThumb, allowAiTraining);
        });

        ToggleResult usageResult = createToggleRow(translationService.translate("settings.usageData"),
                translationService.translate("settings.usageData.desc"), shareUsageData);
        Div usageTrack = usageResult.track;
        Div usageThumb = usageResult.thumb;
        usageTrack.addClickListener(e -> {
            shareUsageData = !shareUsageData;
            updateToggleVisual(usageTrack, usageThumb, shareUsageData);
        });

        options.add(cloudResult.row, aiResult.row, usageResult.row);

        // Danger zone
        Div dangerZone = new Div();
        dangerZone.getStyle().set(StyleConstants.CSS_MARGIN_TOP, "24px");
        dangerZone.getStyle().set("padding-top", "24px");
        dangerZone.getStyle().set("border-top", "1px solid rgba(255, 59, 48, 0.2)");

        HorizontalLayout dangerRow = new HorizontalLayout();
        dangerRow.setWidthFull();
        dangerRow.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout dangerText = new VerticalLayout();
        dangerText.setPadding(false);
        dangerText.setSpacing(false);
        dangerText.getStyle().set("gap", "4px");

        Span dangerTitle = new Span(translationService.translate(SETTINGS_DELETE_ACCOUNT_TITLE));
        dangerTitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "15px");
        dangerTitle.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "600");
        dangerTitle.getStyle().set(StyleConstants.CSS_COLOR, COLOR_DANGER);

        Span dangerDesc = new Span(translationService.translate("settings.deleteAccountDesc2"));
        dangerDesc.getStyle().set(StyleConstants.CSS_FONT_SIZE, "13px");
        dangerDesc.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);

        dangerText.add(dangerTitle, dangerDesc);

        Button deleteBtn = new Button(translationService.translate("action.delete"));
        applyBaseButtonStyles(deleteBtn);
        deleteBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, "rgba(255, 59, 48, 0.1)");
        deleteBtn.getStyle().set(StyleConstants.CSS_COLOR, COLOR_DANGER);
        deleteBtn.getStyle().set(StyleConstants.CSS_PADDING, "8px 16px");

        deleteBtn.getElement().addEventListener(StyleConstants.VAL_MOUSEENTER,
                e -> deleteBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, "rgba(255, 59, 48, 0.2)"));
        deleteBtn.getElement().addEventListener(StyleConstants.VAL_MOUSELEAVE,
                e -> deleteBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, "rgba(255, 59, 48, 0.1)"));

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
        dialog.setHeaderTitle(translationService.translate(SETTINGS_DELETE_ACCOUNT_TITLE));

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(true);

        Paragraph warning = new Paragraph(translationService.translate("settings.deleteConfirm"));
        warning.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);

        PasswordField passwordField = new PasswordField(translationService.translate("label.password"));
        passwordField.setWidthFull();
        passwordField.getStyle().set("--vaadin-input-field-background", BG_GRAY);

        content.add(warning, passwordField);

        Button cancelBtn = new Button(translationService.translate("action.cancel"), e -> dialog.close());
        cancelBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, StyleConstants.VAL_TRANSPARENT);
        cancelBtn.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);

        Button confirmBtn = new Button(translationService.translate(SETTINGS_DELETE_ACCOUNT_TITLE), e -> {
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
        confirmBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, COLOR_DANGER);
        confirmBtn.getStyle().set(StyleConstants.CSS_COLOR, StyleConstants.VAL_WHITE);

        dialog.add(content);
        dialog.getFooter().add(cancelBtn, confirmBtn);
        dialog.open();
    }

    private HorizontalLayout createActionButtons() {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setWidthFull();
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        actions.getStyle().set("gap", "12px");
        actions.getStyle().set(StyleConstants.CSS_MARGIN_TOP, "8px");

        Button discardBtn = new Button(translationService.translate("action.discard"), e -> {
            loadSettings();
            getUI().ifPresent(ui -> ui.getPage().reload());
            Notification.show(translationService.translate("settings.changesDiscarded"), 2000,
                    Notification.Position.BOTTOM_END);
        });
        applyBaseButtonStyles(discardBtn);
        discardBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, StyleConstants.VAL_TRANSPARENT);
        discardBtn.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        discardBtn.getStyle().set(StyleConstants.CSS_PADDING, "12px 24px");

        discardBtn.getElement().addEventListener(StyleConstants.VAL_MOUSEENTER, e -> {
            discardBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, "rgba(0, 0, 0, 0.05)");
            discardBtn.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        });
        discardBtn.getElement().addEventListener(StyleConstants.VAL_MOUSELEAVE, e -> {
            discardBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, StyleConstants.VAL_TRANSPARENT);
            discardBtn.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        });

        Button saveBtn = ViewComponents.createPrimaryButton(translationService.translate("action.save"),
                this::saveSettings);

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
            languageValue = LANG_ENGLISH;
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
        card.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        card.getStyle().set(StyleConstants.CSS_BORDER, "1px solid rgba(0, 0, 0, 0.05)");
        card.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "24px");
        card.getStyle().set(StyleConstants.CSS_PADDING, "28px");
        card.getStyle().set(StyleConstants.CSS_BOX_SHADOW, StyleConstants.VAL_0_2_12PX);
        card.getStyle().set(StyleConstants.CSS_TRANSITION, StyleConstants.VAL_ALL_0_3S);

        card.getElement().addEventListener(StyleConstants.VAL_MOUSEENTER,
                e -> card.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 8px 24px rgba(0, 0, 0, 0.06)"));
        card.getElement().addEventListener(StyleConstants.VAL_MOUSELEAVE,
                e -> card.getStyle().set(StyleConstants.CSS_BOX_SHADOW, StyleConstants.VAL_0_2_12PX));

        return card;
    }

    private void styleCardTitle(H3 title) {
        title.getStyle().set(StyleConstants.CSS_FONT_SIZE, "18px");
        title.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        title.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        title.getStyle().set(StyleConstants.CSS_MARGIN, MARGIN_8);
    }

    private void styleCardDescription(Paragraph desc, String margin) {
        desc.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        desc.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        desc.getStyle().set(StyleConstants.CSS_MARGIN, margin);
    }

    private void applyBaseButtonStyles(Button btn) {
        btn.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "600");
        btn.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX);
        btn.getStyle().set(StyleConstants.CSS_BORDER, "none");
        btn.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);
        btn.getStyle().set(StyleConstants.CSS_TRANSITION, StyleConstants.VAL_ALL_0_2S);
    }
}