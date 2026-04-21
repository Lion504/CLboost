package com.clbooster.app.views;

import jakarta.annotation.security.PermitAll;
import com.clbooster.aiservice.AIService;
import com.clbooster.aiservice.Exporter;
import com.clbooster.aiservice.Parser;
import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.clbooster.app.i18n.TranslationService;
import com.clbooster.app.views.util.StyleConstants;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.FailedEvent;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 4-Step Wizard Generator View matching Figma Generator.tsx Step 1: Job Details
 * Step 2: Qualifications Step 3: AI Customization Step 4: Review
 */
@Route(value = "generator-wizard", layout = MainLayout.class)
@PageTitle("Generate Cover Letter | CL Booster")
@PermitAll
public class GeneratorWizardView extends VerticalLayout {
    private static final String VAL_NOWRAP = "nowrap";
    private static final String RESUMES_DIR = "resumes";
    private static final String UPLOADS_DIR = "uploads";
    private static final String ACTION_SAVE_KEY = "action.save";
    private static final String DOCX_EXTENSION = ".docx";
    private static final String MAX_WIDTH_600PX = "600px";
    private static final String BG_SOFT = "rgba(0,0,0,0.05)";
    private static final String BORDER_LIGHT = "1px solid rgba(0,0,0,0.1)";
    private static final String MIME_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    // Figma Design System Colors
    private static final String PRIMARY = "#007AFF";
    private static final String PRIMARY_LIGHT = "#5AC8FA";
    private static final String TEXT_PRIMARY = "#1d1d1f";
    private static final String TEXT_SECONDARY = "#86868b";
    private static final String BG_WHITE = "#ffffff";
    private static final String BG_GRAY = "#f5f5f7";
    private static final String GREEN = "#34C759";
    private static final String GREEN_LIGHT = "#e8f5e9";
    private static final String PURPLE = "#AF52DE";
    private static final String ORANGE = "#FF9500";

    private static final Logger LOGGER = Logger.getLogger(GeneratorWizardView.class.getName());

    private int currentStep = 1;
    private final int totalSteps = 5;
    private boolean loading = false;

    // Form data
    private String jobTitle = "";
    private String companyName = "";
    private String jobDescription = "";
    private Set<String> selectedSkills = new HashSet<>();
    private String selectedTone = ""; // No default selection - user must choose

    // Step content containers
    private Div stepContentContainer;
    private HorizontalLayout stepIndicator;
    private Button nextButton;
    private Button backButton;
    private Button saveButton;
    private Div loadingOverlay;
    private VerticalLayout container;

    // Step 1 form fields for inline validation
    private TextField step1JobTitleField;
    private TextField step1CompanyField;
    private TextArea step1DescField;

    // Step 5 editor
    private TextArea editorTextArea;

    private final TranslationService translationService;
    private String savedFilePath;

    // Captured on UI thread before background AI generation starts
    private String capturedUserName = "User";
    private int capturedUserPin = -1;

    // Step 2 skills grid for dynamic updates
    private Div skillsGrid;
    private final List<Button> skillButtons = new ArrayList<>();
    private static final String[] AVAILABLE_SKILLS = { "React", "TypeScript", "Node.js", "UI Design", "GraphQL", "AWS",
            "Agile", "Leadership" };

    private final transient AIService aiService;

    public GeneratorWizardView(AIService aiService) {
        this.aiService = aiService;
        this.translationService = new TranslationService();
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        getStyle().set("overflow", "auto");

        // Clear any previous session data when starting a new wizard session
        clearWizardSessionData();

        // Main container with max width
        container = new VerticalLayout();
        container.setWidthFull();
        container.setMaxWidth("900px");
        container.setAlignItems(FlexComponent.Alignment.CENTER);
        container.getStyle().set(StyleConstants.CSS_MARGIN, "0 auto");
        container.getStyle().set(StyleConstants.CSS_PADDING, "32px 24px 64px 24px");
        container.setSpacing(false);
        container.getStyle().set("gap", "24px");

        // Header with back button and step indicator
        HorizontalLayout header = createHeader();

        // Step indicator
        stepIndicator = createStepIndicator();

        // Step content
        stepContentContainer = new Div();
        stepContentContainer.setWidthFull();
        stepContentContainer.getStyle().set("min-height", "400px");

        // Navigation buttons
        HorizontalLayout navigation = createNavigation();

        container.add(header, stepIndicator, stepContentContainer, navigation);

        add(container);

        // Show initial step
        showStep(1);
    }

    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        // Back button
        Button backBtn = new Button(translationService.translate("generator.wizard.exit"),
                VaadinIcon.ARROW_LEFT.create());
        backBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, StyleConstants.VAL_TRANSPARENT);
        backBtn.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        backBtn.getStyle().set(StyleConstants.CSS_BORDER, "none");
        backBtn.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "500");
        backBtn.getStyle().set("gap", "8px");
        backBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(DashboardView.class)));

        header.add(backBtn);

        return header;
    }

    private HorizontalLayout createStepIndicator() {
        HorizontalLayout indicator = new HorizontalLayout();
        indicator.setWidthFull();
        indicator.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        indicator.getStyle().set("gap", "0");
        indicator.getStyle().set("margin-bottom", "24px");

        String[] stepTitles = { translationService.translate("generator.step1.title"),
                translationService.translate("generator.step2.title"),
                translationService.translate("generator.step3.title"),
                translationService.translate("generator.step4.title"),
                translationService.translate("generator.step5.title") };
        VaadinIcon[] stepIcons = { VaadinIcon.BUILDING, VaadinIcon.LIST_SELECT, VaadinIcon.STAR,
                VaadinIcon.CHECK_CIRCLE, VaadinIcon.EDIT };

        for (int i = 0; i < totalSteps; i++) {
            final int stepNum = i + 1;

            HorizontalLayout stepItem = new HorizontalLayout();
            stepItem.setAlignItems(FlexComponent.Alignment.CENTER);
            stepItem.getStyle().set("gap", "6px");

            // Step badge
            Div badge = new Div();
            badge.getStyle().set(StyleConstants.CSS_DISPLAY, "flex");
            badge.getStyle().set(StyleConstants.CSS_ALIGN_ITEMS, StyleConstants.VAL_CENTER);
            badge.getStyle().set("gap", "6px");
            badge.getStyle().set(StyleConstants.CSS_PADDING, "6px 12px");
            badge.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX);
            badge.getStyle().set(StyleConstants.CSS_FONT_SIZE, "12px");
            badge.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
            badge.getStyle().set(StyleConstants.CSS_TRANSITION, "all 0.3s ease");

            // Set badge style based on current step
            if (stepNum == currentStep) {
                badge.getStyle().set(StyleConstants.CSS_BACKGROUND, PRIMARY);
                badge.getStyle().set(StyleConstants.CSS_COLOR, StyleConstants.VAL_WHITE);
                badge.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 4px 12px rgba(0,122,255,0.3)");
            } else if (stepNum < currentStep) {
                badge.getStyle().set(StyleConstants.CSS_BACKGROUND, GREEN_LIGHT);
                badge.getStyle().set(StyleConstants.CSS_COLOR, GREEN);
            } else {
                badge.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_SOFT);
                badge.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
            }

            Icon icon = stepIcons[i].create();
            icon.getStyle().set(StyleConstants.CSS_WIDTH, "14px");
            icon.getStyle().set(StyleConstants.CSS_HEIGHT, "14px");

            Span title = new Span(stepTitles[i]);
            badge.add(icon, title);

            stepItem.add(badge);

            // Connector line (except for last step)
            if (i < totalSteps - 1) {
                Div connector = new Div();
                connector.getStyle().set(StyleConstants.CSS_WIDTH, "32px");
                connector.getStyle().set(StyleConstants.CSS_HEIGHT, "2px");
                connector.getStyle().set(StyleConstants.CSS_MARGIN, "0 4px");
                if (stepNum < currentStep) {
                    connector.getStyle().set(StyleConstants.CSS_BACKGROUND, GREEN);
                } else {
                    connector.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_SOFT);
                }
                stepItem.add(connector);
            }

            indicator.add(stepItem);
        }

        return indicator;
    }

    private void showStep(int step) {
        currentStep = step;
        stepContentContainer.removeAll();

        switch (step) {
        case 1:
            stepContentContainer.add(createStep1JobDetails());
            break;
        case 2:
            stepContentContainer.add(createStep2Qualifications());
            break;
        case 3:
            stepContentContainer.add(createStep3AICustomization());
            break;
        case 4:
            stepContentContainer.add(createStep4Review());
            break;
        case 5:
            stepContentContainer.add(createStep5Editor());
            break;
        default:
            // no action needed
            break;
        }

        // Update step indicator in container
        container.replace(stepIndicator, createStepIndicator());
        stepIndicator = (HorizontalLayout) container.getComponentAt(1);

        // Update buttons
        updateNavigationButtons();
    }

    private VerticalLayout createStep1JobDetails() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setSpacing(false);
        layout.getStyle().set("gap", "24px");

        // Title
        H1 title = new H1(translationService.translate("generator.step1.title"));
        title.getStyle().set(StyleConstants.CSS_FONT_SIZE, "36px");
        title.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        title.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        title.getStyle().set(StyleConstants.CSS_TEXT_ALIGN, StyleConstants.VAL_CENTER);
        title.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        Paragraph subtitle = new Paragraph(translationService.translate("generator.step1.description"));
        subtitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "18px");
        subtitle.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        subtitle.getStyle().set(StyleConstants.CSS_TEXT_ALIGN, StyleConstants.VAL_CENTER);
        subtitle.getStyle().set(StyleConstants.CSS_MARGIN, StyleConstants.VAL_0_0_16PX);

        // Form card
        Div card = new Div();
        card.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        card.getStyle().set(StyleConstants.CSS_BORDER, "1px solid rgba(0,0,0,0.08)");
        card.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "24px");
        card.getStyle().set(StyleConstants.CSS_PADDING, "32px");
        card.getStyle().set(StyleConstants.CSS_WIDTH, "100%");
        card.getStyle().set(StyleConstants.CSS_MAX_WIDTH, MAX_WIDTH_600PX);
        card.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 4px 20px rgba(0,0,0,0.04)");

        VerticalLayout form = new VerticalLayout();
        form.setSpacing(false);
        form.getStyle().set("gap", "20px");
        form.setPadding(false);

        // Job Title
        VerticalLayout jobTitleGroup = createFormField("generator.form.jobTitle", "generator.form.jobTitlePlaceholder",
                VaadinIcon.BRIEFCASE);
        step1JobTitleField = (TextField) jobTitleGroup.getComponentAt(1);
        step1JobTitleField.setValue(jobTitle);
        step1JobTitleField.addValueChangeListener(e -> {
            jobTitle = e.getValue();
            step1JobTitleField.setInvalid(false); // Clear error on change
        });

        // Company Name
        VerticalLayout companyGroup = createFormField("generator.form.companyName",
                "generator.form.companyNamePlaceholder", VaadinIcon.BUILDING);
        step1CompanyField = (TextField) companyGroup.getComponentAt(1);
        step1CompanyField.setValue(companyName);
        step1CompanyField.addValueChangeListener(e -> {
            companyName = e.getValue();
            step1CompanyField.setInvalid(false); // Clear error on change
        });

        // Job Description
        VerticalLayout descGroup = new VerticalLayout();
        descGroup.setPadding(false);
        descGroup.setSpacing(false);
        descGroup.getStyle().set("gap", "6px");

        Span descLabel = new Span(translationService.translate("generator.form.jobDescription"));
        descLabel.getStyle().set(StyleConstants.CSS_FONT_SIZE, "12px");
        descLabel.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        descLabel.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        descLabel.getStyle().set("text-transform", "uppercase");
        descLabel.getStyle().set("letter-spacing", "0.1em");

        step1DescField = new TextArea();
        step1DescField.setPlaceholder(translationService.translate("generator.form.jobDescriptionPlaceholder"));
        step1DescField.setWidthFull();
        step1DescField.setMinHeight("160px");
        step1DescField.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        step1DescField.getStyle().set(StyleConstants.CSS_BORDER, BORDER_LIGHT);
        step1DescField.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "12px");
        step1DescField.getStyle().set(StyleConstants.CSS_PADDING, "12px 16px");
        step1DescField.setValue(jobDescription);
        step1DescField.addValueChangeListener(e -> {
            jobDescription = e.getValue();
            step1DescField.setInvalid(false); // Clear error on change
        });

        descGroup.add(descLabel, step1DescField);

        form.add(jobTitleGroup, companyGroup, descGroup);
        card.add(form);

        layout.add(title, subtitle, card);

        return layout;
    }

    private VerticalLayout createStep2Qualifications() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setSpacing(false);
        layout.getStyle().set("gap", "24px");

        buildStep2Header(layout);
        buildStep2SkillsGrid(layout);
        buildSavedResumeSection(layout);
        buildImportResumeSection(layout);

        return layout;
    }

    private void buildStep2Header(VerticalLayout layout) {
        H1 title = new H1(translationService.translate("generator.step2.title"));
        title.getStyle().set(StyleConstants.CSS_FONT_SIZE, "36px").set(StyleConstants.CSS_FONT_WEIGHT, "700")
                .set(StyleConstants.CSS_COLOR, TEXT_PRIMARY)
                .set(StyleConstants.CSS_TEXT_ALIGN, StyleConstants.VAL_CENTER).set(StyleConstants.CSS_MARGIN, "0");

        Paragraph subtitle = new Paragraph(translationService.translate("generator.step2.description"));
        subtitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "18px").set(StyleConstants.CSS_COLOR, TEXT_SECONDARY)
                .set(StyleConstants.CSS_TEXT_ALIGN, StyleConstants.VAL_CENTER)
                .set(StyleConstants.CSS_MARGIN, StyleConstants.VAL_0_0_16PX);

        layout.add(title, subtitle);
    }

    private void buildStep2SkillsGrid(VerticalLayout layout) {
        skillsGrid = new Div();
        skillsGrid.getStyle().set(StyleConstants.CSS_DISPLAY, "grid")
                .set("grid-template-columns", "repeat(auto-fill, minmax(140px, 1fr))").set("gap", "16px")
                .set(StyleConstants.CSS_WIDTH, "100%").set(StyleConstants.CSS_MAX_WIDTH, "640px");

        skillButtons.clear();
        for (String skill : AVAILABLE_SKILLS) {
            Button skillBtn = createSkillButton(skill);
            skillButtons.add(skillBtn);
            skillsGrid.add(skillBtn);
        }
        layout.add(skillsGrid);
    }

    private void buildSavedResumeSection(VerticalLayout layout) {
        java.util.Map<String, java.io.File> resumeFileMap = fetchUserResumes();

        Div savedResumeCard = new Div();
        savedResumeCard.getStyle().set(StyleConstants.CSS_BACKGROUND, GREEN_LIGHT)
                .set(StyleConstants.CSS_BORDER, "1.5px solid " + GREEN).set(StyleConstants.CSS_BORDER_RADIUS, "16px")
                .set(StyleConstants.CSS_PADDING, "16px 24px").set(StyleConstants.CSS_WIDTH, "100%")
                .set(StyleConstants.CSS_MAX_WIDTH, MAX_WIDTH_600PX).set(StyleConstants.CSS_MARGIN_TOP, "16px");

        if (!resumeFileMap.isEmpty()) {
            HorizontalLayout savedRow = new HorizontalLayout();
            savedRow.setWidthFull();
            savedRow.setAlignItems(FlexComponent.Alignment.CENTER);
            savedRow.getStyle().set("gap", "12px");
            populateSavedRow(savedRow, resumeFileMap);
            savedResumeCard.add(savedRow);
        } else {
            savedResumeCard.setVisible(false);
        }
        layout.add(savedResumeCard);
    }

    private java.util.Map<String, java.io.File> fetchUserResumes() {
        java.util.Map<String, java.io.File> resumeFileMap = new java.util.LinkedHashMap<>();
        try {
            AuthenticationService _authForResume = new AuthenticationService();
            com.clbooster.app.backend.service.profile.User _resumeUser = _authForResume.getCurrentUser();
            if (_resumeUser != null) {
                java.nio.file.Path resumeDir = java.nio.file.Paths.get(UPLOADS_DIR, RESUMES_DIR);
                if (java.nio.file.Files.exists(resumeDir)) {
                    int _pin = _resumeUser.getPin();
                    java.io.File[] existing = resumeDir.toFile().listFiles((d, n) -> n.startsWith(_pin + "_"));
                    if (existing != null) {
                        java.util.Arrays.stream(existing).filter(java.io.File::isFile)
                                .sorted(java.util.Comparator.comparingLong(java.io.File::lastModified).reversed())
                                .forEach(f -> {
                                    String display = f.getName().replaceFirst("^\\d+_\\d+_", "");
                                    String key = display;
                                    int idx = 2;
                                    while (resumeFileMap.containsKey(key))
                                        key = display + " (" + idx++ + ")";
                                    resumeFileMap.put(key, f);
                                });
                    }
                }
            }
        } catch (Exception ignored) {
            /* Ignored */ }
        return resumeFileMap;
    }

    private void populateSavedRow(HorizontalLayout savedRow, java.util.Map<String, java.io.File> resumeFileMap) {
        if (resumeFileMap.size() == 1) {
            java.io.File singleFile = resumeFileMap.values().iterator().next();
            String singleName = resumeFileMap.keySet().iterator().next();
            Span singleLabel = new Span("\uD83D\uDCCE " + singleName);
            singleLabel.getStyle().set("flex", "1").set(StyleConstants.CSS_FONT_SIZE, "14px")
                    .set(StyleConstants.CSS_FONT_WEIGHT, "600").set(StyleConstants.CSS_COLOR, TEXT_PRIMARY)
                    .set(StyleConstants.CSS_OVERFLOW, "hidden").set("text-overflow", "ellipsis")
                    .set(StyleConstants.CSS_WHITE_SPACE, VAL_NOWRAP);
            Button useBtn = createUseResumeButton();
            useBtn.setText(translationService.translate("generator.step2.useResume"));
            useBtn.addClickListener(e -> loadResumeSkills(singleFile));
            savedRow.add(singleLabel, useBtn);
            savedRow.expand(singleLabel);
        } else {
            Select<String> resumeSelect = new Select<>();
            resumeSelect.setItems(resumeFileMap.keySet());
            resumeSelect.setValue(resumeFileMap.keySet().iterator().next());
            resumeSelect.setLabel(null);
            resumeSelect.getStyle().set("flex", "1").set(StyleConstants.CSS_MIN_WIDTH, "0");
            resumeSelect.getElement().setAttribute("title",
                    translationService.translate("generator.step2.selectSavedResume"));
            Span selectHint = new Span("\uD83D\uDCCE " + translationService.translate("generator.step2.savedResumes"));
            selectHint.getStyle().set(StyleConstants.CSS_FONT_SIZE, "13px").set(StyleConstants.CSS_FONT_WEIGHT, "600")
                    .set(StyleConstants.CSS_COLOR, TEXT_PRIMARY).set(StyleConstants.CSS_WHITE_SPACE, VAL_NOWRAP);
            Button useBtn = createUseResumeButton();
            useBtn.setText(translationService.translate("generator.step2.useSelected"));
            useBtn.addClickListener(e -> {
                String selected = resumeSelect.getValue();
                if (selected != null && resumeFileMap.get(selected) != null)
                    loadResumeSkills(resumeFileMap.get(selected));
            });
            savedRow.add(selectHint, resumeSelect, useBtn);
            savedRow.expand(resumeSelect);
        }
    }

    private Button createUseResumeButton() {
        Button useBtn = new Button(VaadinIcon.CHECK_CIRCLE.create());
        useBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, GREEN)
                .set(StyleConstants.CSS_COLOR, StyleConstants.VAL_WHITE).set(StyleConstants.CSS_FONT_WEIGHT, "600")
                .set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX).set(StyleConstants.CSS_BORDER, "none")
                .set(StyleConstants.CSS_WHITE_SPACE, VAL_NOWRAP);
        return useBtn;
    }

    private void buildImportResumeSection(VerticalLayout layout) {
        Div importCard = new Div();
        importCard.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_GRAY)
                .set(StyleConstants.CSS_BORDER, "2px dashed rgba(0,0,0,0.1)")
                .set(StyleConstants.CSS_BORDER_RADIUS, "24px").set(StyleConstants.CSS_PADDING, "32px")
                .set(StyleConstants.CSS_WIDTH, "100%").set(StyleConstants.CSS_MAX_WIDTH, MAX_WIDTH_600PX)
                .set(StyleConstants.CSS_TEXT_ALIGN, StyleConstants.VAL_CENTER)
                .set(StyleConstants.CSS_MARGIN_TOP, "16px");

        Icon fileIcon = VaadinIcon.FILE_SEARCH.create();
        fileIcon.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY).set(StyleConstants.CSS_WIDTH, "32px")
                .set(StyleConstants.CSS_HEIGHT, "32px").set(StyleConstants.CSS_MARGIN_BOTTOM, "12px");

        H3 importTitle = new H3(translationService.translate("generator.step2.importFromResume"));
        importTitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "16px").set(StyleConstants.CSS_FONT_WEIGHT, "700")
                .set(StyleConstants.CSS_COLOR, TEXT_PRIMARY).set(StyleConstants.CSS_MARGIN, "0 0 4px 0");

        Paragraph importDesc = new Paragraph(translationService.translate("generator.step2.uploadResume"));
        importDesc.getStyle().set(StyleConstants.CSS_FONT_SIZE, "13px").set(StyleConstants.CSS_COLOR, TEXT_SECONDARY)
                .set(StyleConstants.CSS_MARGIN, StyleConstants.VAL_0_0_16PX);

        com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer buffer = new com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer();
        com.vaadin.flow.component.upload.Upload upload = new com.vaadin.flow.component.upload.Upload();
        upload.setReceiver(buffer);
        upload.setDropAllowed(false);
        upload.setAcceptedFileTypes("application/pdf", ".pdf", MIME_DOCX, DOCX_EXTENSION, "application/msword", ".doc");
        upload.setMaxFiles(1);
        upload.setMaxFileSize(10 * 1024 * 1024);
        upload.setUploadButton(new Button(translationService.translate("generator.step2.uploadFile")));
        upload.setDropLabel(new Span(""));

        Div fileTag = new Div();
        fileTag.getStyle().set(StyleConstants.CSS_DISPLAY, "inline-flex")
                .set(StyleConstants.CSS_ALIGN_ITEMS, StyleConstants.VAL_CENTER).set("gap", "6px")
                .set(StyleConstants.CSS_BACKGROUND, GREEN_LIGHT).set(StyleConstants.CSS_COLOR, "#2e7d32")
                .set(StyleConstants.CSS_FONT_SIZE, "13px").set(StyleConstants.CSS_FONT_WEIGHT, "600")
                .set(StyleConstants.CSS_PADDING, "6px 14px")
                .set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX)
                .set(StyleConstants.CSS_MARGIN_TOP, "12px").set(StyleConstants.CSS_MAX_WIDTH, "100%")
                .set(StyleConstants.CSS_OVERFLOW, "hidden").set("text-overflow", "ellipsis")
                .set(StyleConstants.CSS_WHITE_SPACE, VAL_NOWRAP);
        fileTag.setVisible(false);

        upload.addSucceededListener((ComponentEventListener<SucceededEvent>) event -> handleUploadSucceeded(event,
                buffer, fileTag, importTitle, importDesc));
        upload.addFailedListener((ComponentEventListener<FailedEvent>) event -> handleUploadFailed());
        upload.addFileRejectedListener(event -> {
            LOGGER.warning("[UPLOAD REJECTED] Cannot upload file");
            showUploadFailedNotification();
        });

        importCard.add(fileIcon, importTitle, importDesc, upload, fileTag);
        layout.add(importCard);
    }

    private void handleUploadSucceeded(com.vaadin.flow.component.upload.SucceededEvent event,
            com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer buffer, Div fileTag, H3 importTitle,
            Paragraph importDesc) {
        String fileName = event.getFileName();
        LOGGER.log(Level.INFO, "[UPLOAD] File upload succeeded: {0}", fileName);

        try {
            if (fileName == null || fileName.trim().isEmpty()) {
                throw new IllegalArgumentException("Filename is empty or null");
            }
            String originalFileName = fileName.trim();
            String fileExtension = "";
            int lastDotIndex = originalFileName.lastIndexOf('.');
            if (lastDotIndex > 0 && lastDotIndex < originalFileName.length() - 1) {
                fileExtension = originalFileName.substring(lastDotIndex).toLowerCase();
            }
            if (!java.util.Arrays.asList(".pdf", DOCX_EXTENSION, ".doc").contains(fileExtension)) {
                throw new IllegalArgumentException("Unsupported file type: " + fileExtension);
            }
            java.io.File tempFile = buffer.getFileData(fileName).getFile();
            if (!tempFile.exists() || tempFile.length() == 0) {
                throw new java.io.IOException("Temp file does not exist or empty.");
            }
            Parser parser = new Parser();
            String resumeText = parser.parseFileToJson(tempFile.getAbsolutePath());

            try {
                AuthenticationService authServiceLoc = new AuthenticationService();
                com.clbooster.app.backend.service.profile.User u = authServiceLoc.getCurrentUser();
                if (u != null) {
                    java.nio.file.Path resumeDir = java.nio.file.Paths.get(UPLOADS_DIR, RESUMES_DIR);
                    if (!java.nio.file.Files.exists(resumeDir))
                        java.nio.file.Files.createDirectories(resumeDir);
                    String safeFileName = originalFileName.replaceAll("[^a-zA-Z0-9.-]", "_");
                    java.nio.file.Path destFile = resumeDir
                            .resolve(u.getPin() + "_" + System.currentTimeMillis() + "_" + safeFileName);
                    java.nio.file.Files.copy(tempFile.toPath(), destFile,
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (Exception persistenceEx) {
                LOGGER.warning("[UPLOAD] Failed to persist file, continuing: " + persistenceEx.getMessage());
            }

            fileTag.removeAll();
            Icon checkIcon = VaadinIcon.CHECK_CIRCLE.create();
            checkIcon.getStyle().set(StyleConstants.CSS_WIDTH, "14px").set(StyleConstants.CSS_HEIGHT, "14px");
            fileTag.add(checkIcon, new Span(originalFileName));
            fileTag.setVisible(true);

            importTitle.setText(translationService.translate("generator.step2.importSuccess"));
            importTitle.getStyle().set(StyleConstants.CSS_COLOR, "#2e7d32");
            importDesc.setVisible(false);

            Notification.show(translationService.translate("generator.step2.parsing"), 1500,
                    Notification.Position.TOP_CENTER);

            getUI().ifPresent(ui -> ui.access(() -> loadResumeSkills(tempFile)));

        } catch (Exception ex) {
            showUploadFailedNotification();
        }
    }

    private void showUploadFailedNotification() {
        Notification.show(translationService.translate("generator.step2.uploadFailed"), 3000,
                Notification.Position.TOP_CENTER);
    }

    private void handleUploadFailed() {
        LOGGER.severe("[UPLOAD FAILED] Upload failed entirely");
        showUploadFailedNotification();
    }

    private Button createSkillButton(String skill) {
        Button skillBtn = new Button(skill);
        skillBtn.setWidthFull();
        skillBtn.getStyle().set(StyleConstants.CSS_PADDING, "16px");
        skillBtn.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "16px");
        skillBtn.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        skillBtn.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        skillBtn.getStyle().set(StyleConstants.CSS_BORDER, "1px solid rgba(0,0,0,0.05)");
        skillBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        skillBtn.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        skillBtn.getStyle().set("cursor", "pointer");
        skillBtn.getStyle().set(StyleConstants.CSS_TRANSITION, "all 0.2s ease");

        // Check if already selected
        updateSkillButtonStyle(skillBtn, skill);

        skillBtn.addClickListener(e -> {
            if (selectedSkills.contains(skill)) {
                selectedSkills.remove(skill);
            } else {
                selectedSkills.add(skill);
            }
            updateSkillButtonStyle(skillBtn, skill);
        });

        return skillBtn;
    }

    private void updateSkillButtonStyle(Button skillBtn, String skill) {
        if (selectedSkills.contains(skill)) {
            skillBtn.getStyle().set("border-color", PRIMARY);
            skillBtn.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 4px 12px rgba(0,122,255,0.15)");
        } else {
            skillBtn.getStyle().set("border-color", BG_SOFT);
            skillBtn.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "none");
        }
    }

    private void updateSkillButtonsUI() {
        for (Button skillBtn : skillButtons) {
            String skill = skillBtn.getText();
            updateSkillButtonStyle(skillBtn, skill);
        }
    }

    private Set<String> extractSkillsFromText(String text) {
        Set<String> foundSkills = new HashSet<>();
        if (text == null || text.isEmpty()) {
            return foundSkills;
        }

        // Convert text to lowercase for case-insensitive matching
        String lowerText = text.toLowerCase();

        // Check for each skill in the text (case-insensitive)
        for (String skill : AVAILABLE_SKILLS) {
            // Create a regex pattern that matches the skill as a whole word
            // Handle special characters like . in "Node.js" and spaces in "UI Design"
            String skillPattern = skill.toLowerCase().replace(".", "\\.") // Escape dots
                    .replace(" ", "\\s+"); // Match spaces as one or more whitespace

            // Check if the skill appears as a word in the text
            if (lowerText.matches(".*\\b" + skillPattern + "\\b.*") || lowerText.contains(skill.toLowerCase())) {
                foundSkills.add(skill);
            }
        }

        return foundSkills;
    }

    private VerticalLayout createStep3AICustomization() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setSpacing(false);
        layout.getStyle().set("gap", "24px");

        // Title
        H1 title = new H1(translationService.translate("generator.step3.title"));
        title.getStyle().set(StyleConstants.CSS_FONT_SIZE, "36px");
        title.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        title.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        title.getStyle().set(StyleConstants.CSS_TEXT_ALIGN, StyleConstants.VAL_CENTER);
        title.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        Paragraph subtitle = new Paragraph(translationService.translate("generator.step3.description"));
        subtitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "18px");
        subtitle.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        subtitle.getStyle().set(StyleConstants.CSS_TEXT_ALIGN, StyleConstants.VAL_CENTER);
        subtitle.getStyle().set(StyleConstants.CSS_MARGIN, StyleConstants.VAL_0_0_16PX);

        // Tone cards
        HorizontalLayout cards = new HorizontalLayout();
        cards.setWidthFull();
        cards.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        cards.getStyle().set("gap", "20px");
        cards.getStyle().set("flex-wrap", "wrap");
        cards.getStyle().set(StyleConstants.CSS_MAX_WIDTH, "800px");

        // Professional card
        cards.add(createToneCard("professional", "generator.tone.professional", "generator.tone.professionalDesc",
                VaadinIcon.BRIEFCASE, "#e3f2fd", PRIMARY, "professional".equals(selectedTone)));

        // Creative card (selected by default)
        cards.add(createToneCard("creative", "generator.tone.creative", "generator.tone.creativeDesc", VaadinIcon.STAR,
                "#f3e5f5", PURPLE, "creative".equals(selectedTone)));

        // Storyteller card
        cards.add(createToneCard("storyteller", "generator.tone.storyteller", "generator.tone.storytellerDesc",
                VaadinIcon.MAGIC, "#fff3e0", ORANGE, "storyteller".equals(selectedTone)));

        layout.add(title, subtitle, cards);

        return layout;
    }

    private Div createToneCard(String toneId, String titleKey, String descKey, VaadinIcon iconName, String bgColor,
            String iconColor, boolean isSelected) {
        Div card = new Div();
        card.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        card.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "20px");
        card.getStyle().set(StyleConstants.CSS_PADDING, "28px");
        card.getStyle().set(StyleConstants.CSS_WIDTH, "220px");
        card.getStyle().set(StyleConstants.CSS_TEXT_ALIGN, StyleConstants.VAL_CENTER);
        card.getStyle().set("cursor", "pointer");
        card.getStyle().set(StyleConstants.CSS_TRANSITION, "all 0.2s ease");

        if (isSelected) {
            card.getStyle().set(StyleConstants.CSS_BORDER, "2px solid " + PRIMARY);
            card.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 8px 24px rgba(0,122,255,0.15)");
        } else {
            card.getStyle().set(StyleConstants.CSS_BORDER, "2px solid transparent");
            card.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 2px 8px rgba(0,0,0,0.04)");
        }

        // Icon circle
        Div iconCircle = new Div();
        iconCircle.getStyle().set(StyleConstants.CSS_WIDTH, "48px");
        iconCircle.getStyle().set(StyleConstants.CSS_HEIGHT, "48px");
        iconCircle.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "50%");
        iconCircle.getStyle().set(StyleConstants.CSS_BACKGROUND, bgColor);
        iconCircle.getStyle().set(StyleConstants.CSS_DISPLAY, "flex");
        iconCircle.getStyle().set(StyleConstants.CSS_ALIGN_ITEMS, StyleConstants.VAL_CENTER);
        iconCircle.getStyle().set(StyleConstants.CSS_JUSTIFY_CONTENT, StyleConstants.VAL_CENTER);
        iconCircle.getStyle().set(StyleConstants.CSS_MARGIN, "0 auto 16px auto");

        Icon icon = iconName.create();
        icon.getStyle().set(StyleConstants.CSS_COLOR, iconColor);
        icon.getStyle().set(StyleConstants.CSS_WIDTH, "24px");
        icon.getStyle().set(StyleConstants.CSS_HEIGHT, "24px");
        iconCircle.add(icon);

        H3 cardTitle = new H3(translationService.translate(titleKey));
        cardTitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "17px");
        cardTitle.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        cardTitle.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        cardTitle.getStyle().set(StyleConstants.CSS_MARGIN, "0 0 8px 0");

        Paragraph cardDesc = new Paragraph(translationService.translate(descKey));
        cardDesc.getStyle().set(StyleConstants.CSS_FONT_SIZE, "13px");
        cardDesc.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        cardDesc.getStyle().set("line-height", "1.5");
        cardDesc.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        card.add(iconCircle, cardTitle, cardDesc);

        card.addClickListener(e -> {
            selectedTone = toneId;
            showStep(3); // Refresh to update selection
        });

        return card;
    }

    private VerticalLayout createStep4Review() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setSpacing(false);
        layout.getStyle().set("gap", "24px");

        // Success icon
        Div iconContainer = new Div();
        iconContainer.getStyle().set(StyleConstants.CSS_WIDTH, "80px");
        iconContainer.getStyle().set(StyleConstants.CSS_HEIGHT, "80px");
        iconContainer.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "50%");
        iconContainer.getStyle().set(StyleConstants.CSS_BACKGROUND, GREEN_LIGHT);
        iconContainer.getStyle().set(StyleConstants.CSS_DISPLAY, "flex");
        iconContainer.getStyle().set(StyleConstants.CSS_ALIGN_ITEMS, StyleConstants.VAL_CENTER);
        iconContainer.getStyle().set(StyleConstants.CSS_JUSTIFY_CONTENT, StyleConstants.VAL_CENTER);
        iconContainer.getStyle().set("margin-bottom", "8px");

        Icon checkIcon = VaadinIcon.CHECK_CIRCLE.create();
        checkIcon.getStyle().set(StyleConstants.CSS_COLOR, GREEN);
        checkIcon.getStyle().set(StyleConstants.CSS_WIDTH, "40px");
        checkIcon.getStyle().set(StyleConstants.CSS_HEIGHT, "40px");
        iconContainer.add(checkIcon);

        // Title
        H1 title = new H1(translationService.translate("generator.step4.title"));
        title.getStyle().set(StyleConstants.CSS_FONT_SIZE, "36px");
        title.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        title.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        title.getStyle().set(StyleConstants.CSS_TEXT_ALIGN, StyleConstants.VAL_CENTER);
        title.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        Paragraph subtitle = new Paragraph(translationService.translate("generator.step4.description"));
        subtitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "18px");
        subtitle.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        subtitle.getStyle().set(StyleConstants.CSS_TEXT_ALIGN, StyleConstants.VAL_CENTER);
        subtitle.getStyle().set(StyleConstants.CSS_MARGIN, StyleConstants.VAL_0_0_16PX);

        // Summary card
        Div card = new Div();
        card.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        card.getStyle().set(StyleConstants.CSS_BORDER, "1px solid rgba(0,0,0,0.08)");
        card.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "20px");
        card.getStyle().set(StyleConstants.CSS_PADDING, "28px 32px");
        card.getStyle().set(StyleConstants.CSS_WIDTH, "100%");
        card.getStyle().set(StyleConstants.CSS_MAX_WIDTH, "420px");
        card.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 4px 20px rgba(0,0,0,0.04)");

        VerticalLayout summary = new VerticalLayout();
        summary.setPadding(false);
        summary.setSpacing(false);
        summary.getStyle().set("gap", "12px");

        summary.add(createSummaryRow("generator.step4.role", jobTitle.isEmpty() ? "Senior Product Designer" : jobTitle,
                TEXT_PRIMARY));
        summary.add(createSummaryRow("generator.step4.company", companyName.isEmpty() ? "Apple Inc." : companyName,
                TEXT_PRIMARY));
        summary.add(createSummaryRow("generator.step4.tone", selectedTone, TEXT_PRIMARY));

        // Divider
        Div divider = new Div();
        divider.getStyle().set(StyleConstants.CSS_HEIGHT, "1px");
        divider.getStyle().set(StyleConstants.CSS_BACKGROUND, "rgba(0,0,0,0.06)");
        divider.getStyle().set(StyleConstants.CSS_MARGIN, "8px 0");
        summary.add(divider);

        summary.add(createSummaryRow("generator.step4.access",
                translationService.translate("generator.step4.freeForever"), GREEN));

        card.add(summary);

        layout.add(iconContainer, title, subtitle, card);

        return layout;
    }

    private HorizontalLayout createSummaryRow(String label, String value) {
        return createSummaryRow(label, value, TEXT_PRIMARY);
    }

    private HorizontalLayout createSummaryRow(String labelKey, String value, String valueColor) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        row.setAlignItems(FlexComponent.Alignment.CENTER);

        Span labelSpan = new Span(translationService.translate(labelKey));
        labelSpan.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        labelSpan.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);

        Span valueSpan = new Span(value);
        valueSpan.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        valueSpan.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        valueSpan.getStyle().set(StyleConstants.CSS_COLOR, valueColor);

        row.add(labelSpan, valueSpan);

        return row;
    }

    private VerticalLayout createFormField(String labelKey, String placeholderKey, VaadinIcon icon) {
        VerticalLayout group = new VerticalLayout();
        group.setPadding(false);
        group.setSpacing(false);
        group.getStyle().set("gap", "6px");

        HorizontalLayout labelRow = new HorizontalLayout();
        labelRow.setAlignItems(FlexComponent.Alignment.CENTER);
        labelRow.getStyle().set("gap", "6px");

        Icon iconComponent = icon.create();
        iconComponent.getStyle().set(StyleConstants.CSS_WIDTH, "14px");
        iconComponent.getStyle().set(StyleConstants.CSS_HEIGHT, "14px");
        iconComponent.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);

        Span labelText = new Span(translationService.translate(labelKey));
        labelText.getStyle().set(StyleConstants.CSS_FONT_SIZE, "12px");
        labelText.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        labelText.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        labelText.getStyle().set("text-transform", "uppercase");
        labelText.getStyle().set("letter-spacing", "0.1em");

        labelRow.add(iconComponent, labelText);
        group.add(labelRow);

        TextField field = new TextField();
        field.setPlaceholder(translationService.translate(placeholderKey));
        field.setWidthFull();
        field.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        field.getStyle().set(StyleConstants.CSS_BORDER, BORDER_LIGHT);
        field.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "12px");
        field.getStyle().set("--vaadin-input-field-height", "48px");

        group.add(field);

        return group;
    }

    private HorizontalLayout createNavigation() {
        HorizontalLayout nav = new HorizontalLayout();
        nav.setWidthFull();
        nav.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        nav.getStyle().set("gap", "12px");
        nav.getStyle().set("margin-top", "16px");

        // Back button
        backButton = new Button(translationService.translate("generator.back"));
        backButton.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_SOFT);
        backButton.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        backButton.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "600");
        backButton.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX);
        backButton.getStyle().set(StyleConstants.CSS_PADDING, "14px 32px");
        backButton.getStyle().set(StyleConstants.CSS_FONT_SIZE, "15px");
        backButton.setVisible(false);
        backButton.addClickListener(e -> {
            if (currentStep > 1) {
                showStep(currentStep - 1);
            }
        });

        // Next/Generate button
        nextButton = new Button(translationService.translate("generator.next"), VaadinIcon.ARROW_RIGHT.create());
        nextButton.getStyle().set(StyleConstants.CSS_BACKGROUND, PRIMARY);
        nextButton.getStyle().set(StyleConstants.CSS_COLOR, StyleConstants.VAL_WHITE);
        nextButton.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "600");
        nextButton.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX);
        nextButton.getStyle().set(StyleConstants.CSS_PADDING, "14px 40px");
        nextButton.getStyle().set(StyleConstants.CSS_FONT_SIZE, "15px");
        nextButton.getStyle().set("min-width", "180px");
        nextButton.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 10px 20px -4px rgba(0,122,255,0.3)");
        nextButton.getStyle().set("gap", "8px");
        nextButton.addClickListener(e -> handleNext());

        // Save button — visible only on step 5
        saveButton = new Button(translationService.translate(ACTION_SAVE_KEY), VaadinIcon.CHECK.create());
        saveButton.getStyle().set(StyleConstants.CSS_BACKGROUND, GREEN);
        saveButton.getStyle().set(StyleConstants.CSS_COLOR, StyleConstants.VAL_WHITE);
        saveButton.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "600");
        saveButton.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX);
        saveButton.getStyle().set(StyleConstants.CSS_PADDING, "14px 32px");
        saveButton.getStyle().set(StyleConstants.CSS_FONT_SIZE, "15px");
        saveButton.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 10px 20px -4px rgba(52,199,89,0.35)");
        saveButton.getStyle().set("gap", "8px");
        saveButton.setVisible(false);
        saveButton.addClickListener(e -> {
            if (editorTextArea == null || editorTextArea.getValue().isBlank()) {
                Notification.show(translationService.translate("generator.notif.nothingToSave"), 2000,
                        Notification.Position.TOP_CENTER);
                return;
            }
            saveButton.setEnabled(false);
            saveButton.setText(translationService.translate("generator.notif.saving"));
            String path = saveGeneratedCoverLetter(editorTextArea.getValue());
            if (path != null) {
                savedFilePath = path;
                saveButton.setText(translationService.translate("generator.notif.saved"));
                Notification.show(translationService.translate("generator.notif.coverLetterSaved"), 2500,
                        Notification.Position.TOP_CENTER);
            } else {
                saveButton.setText(translationService.translate(ACTION_SAVE_KEY));
                Notification.show(translationService.translate("generator.notif.saveFailed"), 3000,
                        Notification.Position.TOP_CENTER);
            }
            saveButton.setEnabled(true);
        });

        nav.add(backButton, saveButton, nextButton);

        return nav;
    }

    private void updateNavigationButtons() {
        if (currentStep == totalSteps) {
            // Editor step: show Save, hide Next, keep Back
            backButton.setVisible(true);
            saveButton.setVisible(true);
            saveButton.setText(translationService.translate(ACTION_SAVE_KEY));
            saveButton.setEnabled(true);
            nextButton.setVisible(false);
        } else if (currentStep == 4) {
            backButton.setVisible(true);
            saveButton.setVisible(false);
            nextButton.setVisible(true);
            nextButton.setText(translationService.translate("generator.step4.generateEdit"));
            nextButton.setIcon(VaadinIcon.MAGIC.create());
        } else {
            backButton.setVisible(currentStep > 1);
            saveButton.setVisible(false);
            nextButton.setVisible(true);
            nextButton.setText(translationService.translate("generator.next"));
            nextButton.setIcon(VaadinIcon.ARROW_RIGHT.create());
        }
    }

    private void handleNext() {
        if (currentStep < totalSteps) {
            // Validate Step 1 fields before proceeding
            if (currentStep == 1) {
                if (!validateStep1Fields()) {
                    return; // Validation failed, don't proceed
                }
            }

            // Validate Step 3 tone selection before proceeding
            if (currentStep == 3) {
                if (selectedTone == null || selectedTone.isEmpty()) {
                    Notification.show(translationService.translate("generator.error.selectTone"), 3000,
                            Notification.Position.TOP_CENTER);
                    return; // Validation failed, don't proceed
                }
            }

            // Show loading animation
            showLoading();

            // Direct synchronous step transition on UI thread
            try {
                showStep(currentStep + 1);
            } finally {
                // Always hide loading overlay, even if an exception occurs
                hideLoading();
            }
        }
        // Step 5 (editor) hides the Next button, so no else branch needed
    }

    private boolean validateStep1Fields() {
        boolean valid = true;

        // Inline validation with field errors
        if (step1JobTitleField != null) {
            if (jobTitle == null || jobTitle.trim().isEmpty()) {
                step1JobTitleField.setErrorMessage(translationService.translate("generator.error.jobTitleRequired"));
                step1JobTitleField.setInvalid(true);
                valid = false;
            } else {
                step1JobTitleField.setInvalid(false);
            }
        }

        if (step1CompanyField != null) {
            if (companyName == null || companyName.trim().isEmpty()) {
                step1CompanyField.setErrorMessage(translationService.translate("generator.error.companyNameRequired"));
                step1CompanyField.setInvalid(true);
                valid = false;
            } else {
                step1CompanyField.setInvalid(false);
            }
        }

        if (step1DescField != null) {
            if (jobDescription == null || jobDescription.trim().isEmpty()) {
                step1DescField.setErrorMessage(translationService.translate("generator.error.jobDescriptionRequired"));
                step1DescField.setInvalid(true);
                valid = false;
            } else {
                step1DescField.setInvalid(false);
            }
        }

        return valid;
    }

    private void showLoading() {
        loading = true;
        nextButton.setEnabled(false);

        // Create loading overlay for the content area
        loadingOverlay = new Div();
        loadingOverlay.getStyle().set("position", "absolute");
        loadingOverlay.getStyle().set("inset", "0");
        loadingOverlay.getStyle().set(StyleConstants.CSS_BACKGROUND, "rgba(255,255,255,0.9)");
        loadingOverlay.getStyle().set(StyleConstants.CSS_DISPLAY, "flex");
        loadingOverlay.getStyle().set(StyleConstants.CSS_ALIGN_ITEMS, StyleConstants.VAL_CENTER);
        loadingOverlay.getStyle().set(StyleConstants.CSS_JUSTIFY_CONTENT, StyleConstants.VAL_CENTER);
        loadingOverlay.getStyle().set("z-index", "100");
        loadingOverlay.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "24px");

        Div spinner = new Div();
        spinner.getStyle().set(StyleConstants.CSS_WIDTH, "40px");
        spinner.getStyle().set(StyleConstants.CSS_HEIGHT, "40px");
        spinner.getStyle().set(StyleConstants.CSS_BORDER, "3px solid rgba(0,122,255,0.2)");
        spinner.getStyle().set("border-top-color", PRIMARY);
        spinner.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "50%");
        spinner.getStyle().set("animation", "spin 1s linear infinite");

        loadingOverlay.add(spinner);
        stepContentContainer.add(loadingOverlay);
    }

    private void hideLoading() {
        loading = false;
        nextButton.setEnabled(true);
        if (loadingOverlay != null) {
            loadingOverlay.removeFromParent();
        }
    }

    private VerticalLayout createStep5Editor() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setHeightFull();
        layout.setSpacing(false);
        layout.setPadding(false);
        layout.getStyle().set("gap", "12px");

        // ── Editor header ──────────────────────────────────────────────────────
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle().set(StyleConstants.CSS_PADDING, "0 0 8px 0");

        VerticalLayout titleGroup = new VerticalLayout();
        titleGroup.setPadding(false);
        titleGroup.setSpacing(false);
        H1 jobTitleLabel = new H1(jobTitle);
        jobTitleLabel.getStyle().set(StyleConstants.CSS_FONT_SIZE, "20px").set(StyleConstants.CSS_FONT_WEIGHT, "700")
                .set(StyleConstants.CSS_COLOR, TEXT_PRIMARY).set(StyleConstants.CSS_MARGIN, "0");
        Paragraph subLabel = new Paragraph(
                companyName + " \u2022 " + translationService.translate("history.tone") + ": " + selectedTone);
        subLabel.getStyle().set(StyleConstants.CSS_FONT_SIZE, "13px").set(StyleConstants.CSS_COLOR, TEXT_SECONDARY)
                .set(StyleConstants.CSS_MARGIN, "0");
        titleGroup.add(jobTitleLabel, subLabel);

        HorizontalLayout actions = new HorizontalLayout();
        actions.setAlignItems(FlexComponent.Alignment.CENTER);
        actions.getStyle().set("gap", "12px");

        Button saveDocxBtn = new Button(translationService.translate("generator.step5.saveDocx"),
                VaadinIcon.DOWNLOAD.create());
        saveDocxBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_SOFT).set(StyleConstants.CSS_COLOR, TEXT_PRIMARY)
                .set(StyleConstants.CSS_FONT_WEIGHT, "600")
                .set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX)
                .set(StyleConstants.CSS_PADDING, "10px 20px");
        saveDocxBtn.addClickListener(e -> downloadEditorAsDocx());

        Button exportPdfBtn = new Button(translationService.translate("generator.step5.exportPdf"),
                VaadinIcon.FILE_TEXT.create());
        exportPdfBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, PRIMARY)
                .set(StyleConstants.CSS_COLOR, StyleConstants.VAL_WHITE).set(StyleConstants.CSS_FONT_WEIGHT, "600")
                .set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX)
                .set(StyleConstants.CSS_PADDING, "10px 24px").set(StyleConstants.CSS_BORDER, "none")
                .set(StyleConstants.CSS_BOX_SHADOW, "0 10px 15px -3px rgba(0,122,255,0.3)");
        exportPdfBtn.addClickListener(e -> downloadEditorAsPdf());

        actions.add(saveDocxBtn, exportPdfBtn);
        header.add(titleGroup, actions);
        header.expand(titleGroup);

        // ── Toolbar ────────────────────────────────────────────────────────────
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidthFull();
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.getStyle().set("gap", "8px").set(StyleConstants.CSS_PADDING, "12px 16px")
                .set(StyleConstants.CSS_BACKGROUND, BG_GRAY).set(StyleConstants.CSS_BORDER_RADIUS, "12px");

        Button boldBtn = createEditorToolbarButton(VaadinIcon.BOLD,
                translationService.translate("editor.toolbar.bold"));
        boldBtn.addClickListener(e -> wrapEditorContent("**", "**"));
        Button italicBtn = createEditorToolbarButton(VaadinIcon.ITALIC,
                translationService.translate("editor.toolbar.italic"));
        italicBtn.addClickListener(e -> wrapEditorContent("_", "_"));
        Button underlineBtn = createEditorToolbarButton(VaadinIcon.UNDERLINE,
                translationService.translate("editor.toolbar.underline"));
        underlineBtn.addClickListener(e -> wrapEditorContent("__", "__"));

        Button copyBtn = createEditorToolbarButton(VaadinIcon.COPY,
                translationService.translate("editor.toolbar.copyAll"));
        copyBtn.addClickListener(e -> {
            if (editorTextArea != null) {
                UI.getCurrent().getPage().executeJs("navigator.clipboard.writeText($0)", editorTextArea.getValue());
                Notification.show(translationService.translate("editor.notif.copiedToClipboard"), 2000,
                        Notification.Position.TOP_CENTER);
            }
        });

        Button clearBtn = createEditorToolbarButton(VaadinIcon.TRASH,
                translationService.translate("editor.toolbar.clear"));
        clearBtn.addClickListener(e -> {
            if (editorTextArea != null)
                editorTextArea.setValue("");
        });

        Div divider = new Div();
        divider.getStyle().set(StyleConstants.CSS_WIDTH, "1px").set(StyleConstants.CSS_HEIGHT, "24px")
                .set(StyleConstants.CSS_BACKGROUND, "rgba(0,0,0,0.1)");

        Button regenBtn = new Button(translationService.translate("editor.toolbar.regenerate"),
                VaadinIcon.MAGIC.create());
        regenBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, PRIMARY + "15").set(StyleConstants.CSS_COLOR, PRIMARY)
                .set(StyleConstants.CSS_FONT_WEIGHT, "600")
                .set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX)
                .set(StyleConstants.CSS_PADDING, "8px 16px").set(StyleConstants.CSS_BORDER, "none");
        regenBtn.addClickListener(e -> {
            if (editorTextArea == null)
                return;
            editorTextArea.setValue("⏳ " + translationService.translate("generator.notif.regenerating"));
            editorTextArea.setEnabled(false);
            UI ui = UI.getCurrent();
            Thread t = new Thread(() -> {
                String result = generateCoverLetterText();
                ui.access(() -> {
                    editorTextArea.setValue(result);
                    editorTextArea.setEnabled(true);
                });
            });
            t.setDaemon(true);
            t.start();
        });

        toolbar.add(boldBtn, italicBtn, underlineBtn, copyBtn, clearBtn, divider, regenBtn);

        // ── Text Area ──────────────────────────────────────────────────────────
        editorTextArea = new TextArea();
        editorTextArea.setWidthFull();
        editorTextArea.setMinHeight("460px");
        editorTextArea.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE)
                .set(StyleConstants.CSS_BORDER, BORDER_LIGHT).set(StyleConstants.CSS_BORDER_RADIUS, "16px")
                .set(StyleConstants.CSS_PADDING, "24px").set(StyleConstants.CSS_FONT_SIZE, "15px")
                .set("line-height", "1.8").set(StyleConstants.CSS_COLOR, TEXT_PRIMARY)
                .set("font-family", "-apple-system, BlinkMacSystemFont, 'SF Pro Text', sans-serif");
        editorTextArea.setValue("\u23f3 " + translationService.translate("generator.notif.generating"));
        editorTextArea.setEnabled(false);

        layout.add(header, toolbar, editorTextArea);
        layout.expand(editorTextArea);

        // ── Capture session-bound data on UI thread before spawning background ──
        try {
            AuthenticationService _authSvc = new AuthenticationService();
            com.clbooster.app.backend.service.profile.User _u = _authSvc.getCurrentUser();
            if (_u != null) {
                capturedUserName = _u.getFirstName() + " " + _u.getLastName();
                capturedUserPin = _u.getPin();
            }
        } catch (Exception ignored) {
            LOGGER.warning("Could not capture user from session before generation");
        }

        // ── Trigger AI generation ──────────────────────────────────────────────
        UI ui = UI.getCurrent();
        Thread genThread = new Thread(() -> {
            String result = generateCoverLetterText();
            ui.access(() -> {
                editorTextArea.setValue(result);
                editorTextArea.setEnabled(true);
            });
        });
        genThread.setDaemon(true);
        genThread.start();

        return layout;
    }

    private Button createEditorToolbarButton(VaadinIcon icon, String tooltip) {
        Button btn = new Button(icon.create());
        btn.getElement().setAttribute("title", tooltip);
        btn.getStyle().set(StyleConstants.CSS_BACKGROUND, StyleConstants.VAL_TRANSPARENT)
                .set(StyleConstants.CSS_COLOR, TEXT_SECONDARY).set(StyleConstants.CSS_BORDER, "none")
                .set(StyleConstants.CSS_PADDING, "8px").set(StyleConstants.CSS_BORDER_RADIUS, "8px");
        btn.getElement().addEventListener("mouseenter",
                e -> btn.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_SOFT));
        btn.getElement().addEventListener("mouseleave",
                e -> btn.getStyle().set(StyleConstants.CSS_BACKGROUND, StyleConstants.VAL_TRANSPARENT));
        return btn;
    }

    private void wrapEditorContent(String prefix, String suffix) {
        if (editorTextArea == null)
            return;
        String current = editorTextArea.getValue();
        if (current.isEmpty())
            return;
        if (current.startsWith(prefix) && current.endsWith(suffix)) {
            editorTextArea.setValue(current.substring(prefix.length(), current.length() - suffix.length()));
        } else {
            editorTextArea.setValue(prefix + current + suffix);
        }
    }

    private String generateCoverLetterText() {
        StringBuilder jobDetails = new StringBuilder();
        jobDetails.append("Job Title: ").append(jobTitle).append("\n");
        jobDetails.append("Company: ").append(companyName).append("\n");
        jobDetails.append("Tone: ").append(selectedTone).append("\n");
        jobDetails.append("Selected Skills: ").append(String.join(", ", selectedSkills)).append("\n");
        jobDetails.append("Job Description: ").append(jobDescription).append("\n");

        // Build enriched candidate context from profile + resume
        String resumeContent = buildCandidateContext();

        try {
            String generated = aiService.generateCoverLetter(resumeContent, jobDetails.toString(), selectedTone);
            return (generated != null && !generated.isBlank()) ? generated : getFallbackCoverLetter();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "AI generation failed, using fallback: " + e.getMessage(), e);
            return getFallbackCoverLetter();
        }
    }

    /**
     * Builds a rich candidate context string by combining: 1. User identity (name,
     * email) from the User record 2. Profile data (experience level, skills, tools,
     * portfolio link) 3. Parsed text from the user's most recent uploaded resume
     * file
     *
     * This gives the AI full context to write a personalised cover letter.
     */
    private String buildCandidateContext() {
        if (!hasCapturedUser()) {
            return fallbackSkillsSummary();
        }

        StringBuilder ctx = new StringBuilder();
        int pin = capturedUserPin;

        try {
            appendCandidateHeader(ctx);
            appendProfileContext(ctx, pin);
            appendResumeOrSkillsContext(ctx, pin);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "buildCandidateContext error: " + e.getMessage(), e);
            return fallbackSkillsSummary();
        }

        return ctx.toString();
    }

    private boolean hasCapturedUser() {
        if (capturedUserPin < 0) {
            LOGGER.warning("buildCandidateContext: no user captured before background thread");
            return false;
        }
        return true;
    }

    private void appendCandidateHeader(StringBuilder ctx) {
        ctx.append("=== CANDIDATE PROFILE ===\n");
        ctx.append("Name: ").append(capturedUserName).append("\n");
    }

    private void appendProfileContext(StringBuilder ctx, int pin) {
        com.clbooster.app.backend.service.profile.ProfileDAO profileDAO = new com.clbooster.app.backend.service.profile.ProfileDAO();
        com.clbooster.app.backend.service.profile.Profile profile = profileDAO.getProfileByPin(pin);
        if (profile == null) {
            LOGGER.info("buildCandidateContext: no profile row for PIN " + pin);
            return;
        }

        appendIfNotBlank(ctx, "Experience Level: ", profile.getExperienceLevel());
        appendIfNotBlank(ctx, "Profile Skills: ", profile.getSkills());
        appendIfNotBlank(ctx, "Tools & Technologies: ", profile.getTools());
        appendIfNotBlank(ctx, "Portfolio/LinkedIn: ", profile.getLink());
        appendIfNotBlank(ctx, "Contact Email: ", profile.getProfileEmail());
    }

    private void appendResumeOrSkillsContext(StringBuilder ctx, int pin) {
        String resumeText = loadUserResumeText(pin);
        if (resumeText != null && !resumeText.isBlank()) {
            ctx.append("\n=== RESUME CONTENT ===\n").append(resumeText).append("\n");
            return;
        }

        ctx.append("\nSelected Skills for this application: ").append(String.join(", ", selectedSkills)).append("\n");
        LOGGER.info("buildCandidateContext: no resume found, using wizard skills");
    }

    private void appendIfNotBlank(StringBuilder ctx, String label, String value) {
        if (value != null && !value.isBlank()) {
            ctx.append(label).append(value).append("\n");
        }
    }

    private String fallbackSkillsSummary() {
        return String.format("Experienced professional with skills in %s. Seeking a %s role at %s.",
                String.join(", ", selectedSkills), jobTitle, companyName);
    }

    /**
     * Parses {@code file}, extracts matching skills, selects them in the UI, and
     * shows a notification. Used by both single- and multi-resume selectors.
     */
    private void loadResumeSkills(File file) {
        try {
            String text = new Parser().parseFileToJson(file.getAbsolutePath());
            Set<String> skills = extractSkillsFromText(text);
            if (!skills.isEmpty()) {
                selectedSkills.addAll(skills);
                updateSkillButtonsUI();
                Notification.show(
                        translationService.translate("generator.notif.skillsLoaded", String.join(", ", skills)), 5000,
                        Notification.Position.TOP_CENTER);
            } else {
                Notification.show(translationService.translate("generator.notif.resumeLoadedNoSkills"), 3000,
                        Notification.Position.TOP_CENTER);
            }
        } catch (Exception ex) {
            Notification.show(translationService.translate("generator.notif.couldNotReadResume", ex.getMessage()), 3000,
                    Notification.Position.TOP_CENTER);
        }
    }

    /**
     * Finds the most recently uploaded resume for the given PIN in uploads/resumes/
     * and returns its parsed text content.
     */
    private String loadUserResumeText(int userPin) {
        try {
            Path resumeDir = Paths.get(UPLOADS_DIR, RESUMES_DIR);
            if (!Files.exists(resumeDir))
                return null;

            File[] userFiles = resumeDir.toFile().listFiles((d, name) -> name.startsWith(userPin + "_"));
            if (userFiles == null || userFiles.length == 0)
                return null;

            File latest = java.util.Arrays.stream(userFiles).filter(File::isFile)
                    .max(java.util.Comparator.comparingLong(File::lastModified)).orElse(null);
            if (latest == null)
                return null;

            LOGGER.info("Using resume: " + latest.getName());
            String parsed = new Parser().parseFileToJson(latest.getAbsolutePath());
            return (parsed != null && !parsed.isBlank()) ? parsed : null;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "loadUserResumeText error: " + e.getMessage(), e);
            return null;
        }
    }

    private String getFallbackCoverLetter() {
        String userName = capturedUserName;
        return "Dear Hiring Manager,\n\n" + "I am writing to express my strong interest in the " + jobTitle
                + " position at " + companyName + ". " + "With relevant experience and skills in "
                + String.join(", ", selectedSkills) + ", "
                + "I am excited about the opportunity to contribute to your team.\n\n"
                + "My background aligns well with the requirements outlined in the job description. "
                + "I am particularly drawn to this role because of " + companyName + "'s reputation for excellence.\n\n"
                + "Thank you for considering my application. I look forward to discussing how my skills "
                + "align with your team's vision.\n\nBest regards,\n" + userName;
    }

    private String saveGeneratedCoverLetter(String content) {
        try {
            AuthenticationService authService = new AuthenticationService();
            com.clbooster.app.backend.service.profile.User currentUser = authService.getCurrentUser();
            if (currentUser == null)
                return null;
            int userPin = currentUser.getPin();
            Path dir = Paths.get(UPLOADS_DIR, "coverletters");
            if (!Files.exists(dir))
                Files.createDirectories(dir);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = userPin + "_" + timestamp + "_" + sanitizeEditorFilename(companyName) + "_"
                    + sanitizeEditorFilename(jobTitle) + DOCX_EXTENSION;
            Path filePath = dir.resolve(fileName);
            new Exporter().saveAsDoc(content, filePath.toString());
            LOGGER.info("Cover letter saved: " + filePath.toAbsolutePath());
            return filePath.toAbsolutePath().toString();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to save cover letter: " + e.getMessage(), e);
            return null;
        }
    }

    private void downloadEditorAsDocx() {
        if (editorTextArea == null)
            return;
        String content = editorTextArea.getValue();
        if (content == null || content.isBlank()) {
            Notification.show(translationService.translate("generator.notif.nothingToDownload"), 2000,
                    Notification.Position.TOP_CENTER);
            return;
        }
        try {
            Path dir = Paths.get(UPLOADS_DIR, "coverletters");
            if (!Files.exists(dir))
                Files.createDirectories(dir);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = sanitizeEditorFilename(companyName) + "_" + sanitizeEditorFilename(jobTitle) + "_"
                    + timestamp + DOCX_EXTENSION;
            Path outPath = dir.resolve(fileName);
            new Exporter().saveAsDoc(content, outPath.toString());
            byte[] bytes = Files.readAllBytes(outPath);
            serveEditorDownload(bytes, MIME_DOCX, fileName);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "DOCX export failed: " + ex.getMessage(), ex);
            Notification.show(translationService.translate("generator.notif.exportFailed", ex.getMessage()), 3000,
                    Notification.Position.TOP_CENTER);
        }
    }

    private void downloadEditorAsPdf() {
        if (editorTextArea == null)
            return;
        String content = editorTextArea.getValue();
        if (content == null || content.isBlank()) {
            Notification.show(translationService.translate("generator.notif.nothingToExport"), 2000,
                    Notification.Position.TOP_CENTER);
            return;
        }
        try {
            String fileName = sanitizeEditorFilename(companyName) + "_" + sanitizeEditorFilename(jobTitle) + ".pdf";
            byte[] bytes = generateSimplePdf(content);
            serveEditorDownload(bytes, "application/pdf", fileName);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "PDF export failed: " + ex.getMessage(), ex);
            Notification.show("Export failed: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER);
        }
    }

    private void serveEditorDownload(byte[] bytes, String mimeType, String fileName) {
        StreamResource resource = new StreamResource(fileName, () -> new ByteArrayInputStream(bytes));
        resource.setContentType(mimeType);
        Anchor anchor = new Anchor(resource, "");
        anchor.getElement().setAttribute("download", fileName);
        anchor.getElement().setAttribute("style", "display:none");
        add(anchor);
        anchor.getElement().executeJs(
                "var a=$0;setTimeout(function(){a.click();setTimeout(function(){a.remove();},1000);},100);",
                anchor.getElement());
        Notification.show(translationService.translate("generator.notif.downloading", fileName) + "\u2026", 2000,
                Notification.Position.TOP_CENTER);
    }

    private byte[] generateSimplePdf(String text) throws IOException {
        java.util.List<String> lines = wrapPdfLines(text, 90);
        final float TOP_MARGIN = 800f, LEFT_MARGIN = 50f, LINE_HEIGHT = 14f, BOTTOM_MARGIN = 50f, PAGE_HEIGHT = 841.89f;
        final int FONT_SIZE = 11;
        final int LINES_PER_PAGE = (int) ((TOP_MARGIN - BOTTOM_MARGIN) / LINE_HEIGHT);
        java.util.List<java.util.List<String>> pages = paginatePdfLines(lines, LINES_PER_PAGE);

        return renderPdfPages(pages, FONT_SIZE, LEFT_MARGIN, TOP_MARGIN, LINE_HEIGHT, PAGE_HEIGHT);
    }

    private java.util.List<String> wrapPdfLines(String text, int maxLineLength) {
        java.util.List<String> lines = new java.util.ArrayList<>();
        for (String rawLine : text.split("\n", -1)) {
            if (rawLine.length() <= maxLineLength) {
                lines.add(rawLine);
                continue;
            }
            for (int i = 0; i < rawLine.length(); i += maxLineLength) {
                lines.add(rawLine.substring(i, Math.min(i + maxLineLength, rawLine.length())));
            }
        }
        return lines;
    }

    private java.util.List<java.util.List<String>> paginatePdfLines(java.util.List<String> lines, int linesPerPage) {
        java.util.List<java.util.List<String>> pages = new java.util.ArrayList<>();
        for (int i = 0; i < lines.size(); i += linesPerPage) {
            pages.add(lines.subList(i, Math.min(i + linesPerPage, lines.size())));
        }
        if (pages.isEmpty()) {
            pages.add(new java.util.ArrayList<>());
        }
        return pages;
    }

    private byte[] renderPdfPages(java.util.List<java.util.List<String>> pages, int fontSize, float leftMargin,
            float topMargin, float lineHeight, float pageHeight) {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        java.util.List<Integer> offsets = new java.util.ArrayList<>();
        java.util.function.Consumer<String> write = s -> {
            try {
                out.write(s.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        };
        write.accept("%PDF-1.4\n");
        offsets.add(out.size());
        write.accept("1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");
        offsets.add(out.size());
        StringBuilder kids = new StringBuilder();
        for (int i = 0; i < pages.size(); i++)
            kids.append((4 + i * 2) + " 0 R ");
        write.accept("2 0 obj\n<< /Type /Pages /Kids [" + kids + "] /Count " + pages.size() + " >>\nendobj\n");
        offsets.add(out.size());
        write.accept(
                "3 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>\nendobj\n");
        for (int p = 0; p < pages.size(); p++) {
            StringBuilder stream = new StringBuilder();
            stream.append("BT /F1 ").append(fontSize).append(" Tf ").append(leftMargin).append(" ").append(topMargin)
                    .append(" Td ").append(lineHeight).append(" TL\n");
            for (String line : pages.get(p)) {
                String safe = line.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
                stream.append("(").append(safe).append(") Tj T*\n");
            }
            stream.append("ET");
            String s = stream.toString();
            offsets.add(out.size());
            write.accept((4 + p * 2) + " 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595.28 " + pageHeight
                    + "] /Contents " + (5 + p * 2) + " 0 R /Resources << /Font << /F1 3 0 R >> >> >>\nendobj\n");
            offsets.add(out.size());
            write.accept(
                    (5 + p * 2) + " 0 obj\n<< /Length " + s.length() + " >>\nstream\n" + s + "\nendstream\nendobj\n");
        }
        int xrefOffset = out.size();
        int totalObjs = 3 + pages.size() * 2;
        write.accept("xref\n0 " + (totalObjs + 1) + "\n");
        write.accept("0000000000 65535 f \n");
        for (int offset : offsets)
            write.accept(String.format("%010d 00000 n \n", offset));
        write.accept("trailer\n<< /Size " + (totalObjs + 1) + " /Root 1 0 R >>\n");
        write.accept("startxref\n" + xrefOffset + "\n%%EOF\n");
        return out.toByteArray();
    }

    private String sanitizeEditorFilename(String input) {
        if (input == null || input.isBlank())
            return "Unknown";
        return input.trim().replaceAll("\\s+", "_").replaceAll("[^a-zA-Z0-9_\\-]", "").replaceAll("_+", "_")
                .replaceAll("(^_)|(_$)", "");
    }

    /**
     * Clears wizard-related session attributes to prevent stale data when starting
     * a new cover letter generation.
     */
    private void clearWizardSessionData() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute("gen.jobTitle", null);
            session.setAttribute("gen.company", null);
            session.setAttribute("gen.tone", null);
            session.setAttribute("gen.skills", null);
            session.setAttribute("gen.jobDesc", null);
            LOGGER.info("Cleared wizard session data");
        }
    }

}
