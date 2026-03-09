package com.clbooster.app.views;

import com.clbooster.aiservice.AIService;
import com.clbooster.aiservice.Exporter;
import com.clbooster.aiservice.Parser;
import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.vaadin.flow.component.Component;
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
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

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
import java.util.stream.Collectors;

/**
 * 4-Step Wizard Generator View matching Figma Generator.tsx Step 1: Job Details
 * Step 2: Qualifications Step 3: AI Customization Step 4: Review
 */
@Route(value = "generator-wizard", layout = MainLayout.class)
@PageTitle("Generate Cover Letter | CL Booster")
public class GeneratorWizardView extends VerticalLayout {

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
    private String savedFilePath;

    // Captured on UI thread before background AI generation starts
    private String capturedUserName = "User";
    private int capturedUserPin = -1;

    // Step 2 skills grid for dynamic updates
    private Div skillsGrid;
    private final List<Button> skillButtons = new ArrayList<>();
    private static final String[] AVAILABLE_SKILLS = { "React", "TypeScript", "Node.js", "UI Design", "GraphQL", "AWS",
            "Agile", "Leadership" };

    private final AIService aiService;

    public GeneratorWizardView(AIService aiService) {
        this.aiService = aiService;
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", BG_WHITE);
        getStyle().set("overflow", "auto");

        // Clear any previous session data when starting a new wizard session
        clearWizardSessionData();

        // Main container with max width
        container = new VerticalLayout();
        container.setWidthFull();
        container.setMaxWidth("900px");
        container.setAlignItems(FlexComponent.Alignment.CENTER);
        container.getStyle().set("margin", "0 auto");
        container.getStyle().set("padding", "32px 24px 64px 24px");
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
        Button backBtn = new Button("Exit Generator", VaadinIcon.ARROW_LEFT.create());
        backBtn.getStyle().set("background", "transparent");
        backBtn.getStyle().set("color", TEXT_SECONDARY);
        backBtn.getStyle().set("border", "none");
        backBtn.getStyle().set("font-weight", "500");
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

        String[] stepTitles = { "Job Details", "Qualifications", "AI Customization", "Summary", "Editor" };
        VaadinIcon[] stepIcons = { VaadinIcon.BUILDING, VaadinIcon.LIST_SELECT, VaadinIcon.STAR,
                VaadinIcon.CHECK_CIRCLE, VaadinIcon.EDIT };

        for (int i = 0; i < totalSteps; i++) {
            final int stepNum = i + 1;

            HorizontalLayout stepItem = new HorizontalLayout();
            stepItem.setAlignItems(FlexComponent.Alignment.CENTER);
            stepItem.getStyle().set("gap", "6px");

            // Step badge
            Div badge = new Div();
            badge.getStyle().set("display", "flex");
            badge.getStyle().set("align-items", "center");
            badge.getStyle().set("gap", "6px");
            badge.getStyle().set("padding", "6px 12px");
            badge.getStyle().set("border-radius", "9999px");
            badge.getStyle().set("font-size", "12px");
            badge.getStyle().set("font-weight", "700");
            badge.getStyle().set("transition", "all 0.3s ease");

            // Set badge style based on current step
            if (stepNum == currentStep) {
                badge.getStyle().set("background", PRIMARY);
                badge.getStyle().set("color", "white");
                badge.getStyle().set("box-shadow", "0 4px 12px rgba(0,122,255,0.3)");
            } else if (stepNum < currentStep) {
                badge.getStyle().set("background", GREEN_LIGHT);
                badge.getStyle().set("color", GREEN);
            } else {
                badge.getStyle().set("background", "rgba(0,0,0,0.05)");
                badge.getStyle().set("color", TEXT_SECONDARY);
            }

            Icon icon = stepIcons[i].create();
            icon.getStyle().set("width", "14px");
            icon.getStyle().set("height", "14px");

            Span title = new Span(stepTitles[i]);
            badge.add(icon, title);

            stepItem.add(badge);

            // Connector line (except for last step)
            if (i < totalSteps - 1) {
                Div connector = new Div();
                connector.getStyle().set("width", "32px");
                connector.getStyle().set("height", "2px");
                connector.getStyle().set("margin", "0 4px");
                if (stepNum < currentStep) {
                    connector.getStyle().set("background", GREEN);
                } else {
                    connector.getStyle().set("background", "rgba(0,0,0,0.05)");
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
        H1 title = new H1("Tell us about the role");
        title.getStyle().set("font-size", "36px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("text-align", "center");
        title.getStyle().set("margin", "0");

        Paragraph subtitle = new Paragraph("We'll use this information to tailor your cover letter perfectly.");
        subtitle.getStyle().set("font-size", "18px");
        subtitle.getStyle().set("color", TEXT_SECONDARY);
        subtitle.getStyle().set("text-align", "center");
        subtitle.getStyle().set("margin", "0 0 16px 0");

        // Form card
        Div card = new Div();
        card.getStyle().set("background", BG_WHITE);
        card.getStyle().set("border", "1px solid rgba(0,0,0,0.08)");
        card.getStyle().set("border-radius", "24px");
        card.getStyle().set("padding", "32px");
        card.getStyle().set("width", "100%");
        card.getStyle().set("max-width", "600px");
        card.getStyle().set("box-shadow", "0 4px 20px rgba(0,0,0,0.04)");

        VerticalLayout form = new VerticalLayout();
        form.setSpacing(false);
        form.getStyle().set("gap", "20px");
        form.setPadding(false);

        // Job Title
        VerticalLayout jobTitleGroup = createFormField("Job Title", VaadinIcon.BRIEFCASE,
                "e.g., Senior Frontend Engineer");
        step1JobTitleField = (TextField) jobTitleGroup.getComponentAt(1);
        step1JobTitleField.setValue(jobTitle);
        step1JobTitleField.addValueChangeListener(e -> {
            jobTitle = e.getValue();
            step1JobTitleField.setInvalid(false); // Clear error on change
        });

        // Company Name
        VerticalLayout companyGroup = createFormField("Company Name", VaadinIcon.BUILDING, "e.g., Acme Corp");
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

        Span descLabel = new Span("Job Description");
        descLabel.getStyle().set("font-size", "12px");
        descLabel.getStyle().set("font-weight", "700");
        descLabel.getStyle().set("color", TEXT_SECONDARY);
        descLabel.getStyle().set("text-transform", "uppercase");
        descLabel.getStyle().set("letter-spacing", "0.1em");

        step1DescField = new TextArea();
        step1DescField.setPlaceholder("Paste the job description here...");
        step1DescField.setWidthFull();
        step1DescField.setMinHeight("160px");
        step1DescField.getStyle().set("background", BG_WHITE);
        step1DescField.getStyle().set("border", "1px solid rgba(0,0,0,0.1)");
        step1DescField.getStyle().set("border-radius", "12px");
        step1DescField.getStyle().set("padding", "12px 16px");
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

        // Title
        H1 title = new H1("Your Top Skills");
        title.getStyle().set("font-size", "36px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("text-align", "center");
        title.getStyle().set("margin", "0");

        Paragraph subtitle = new Paragraph("Pick the skills you want to highlight for this specific position.");
        subtitle.getStyle().set("font-size", "18px");
        subtitle.getStyle().set("color", TEXT_SECONDARY);
        subtitle.getStyle().set("text-align", "center");
        subtitle.getStyle().set("margin", "0 0 16px 0");

        // Skills grid
        skillsGrid = new Div();
        skillsGrid.getStyle().set("display", "grid");
        skillsGrid.getStyle().set("grid-template-columns", "repeat(auto-fill, minmax(140px, 1fr))");
        skillsGrid.getStyle().set("gap", "16px");
        skillsGrid.getStyle().set("width", "100%");
        skillsGrid.getStyle().set("max-width", "640px");

        // Clear previous buttons list
        skillButtons.clear();

        for (String skill : AVAILABLE_SKILLS) {
            Button skillBtn = createSkillButton(skill);
            skillButtons.add(skillBtn);
            skillsGrid.add(skillBtn);
        }

        // ── Use saved resume ───────────────────────────────────────────────
        // Collect all resume files for this user, newest first
        java.util.Map<String, File> resumeFileMap = new java.util.LinkedHashMap<>();
        try {
            AuthenticationService _authForResume = new AuthenticationService();
            com.clbooster.app.backend.service.profile.User _resumeUser = _authForResume.getCurrentUser();
            if (_resumeUser != null) {
                Path resumeDir = Paths.get("uploads", "resumes");
                if (Files.exists(resumeDir)) {
                    int _pin = _resumeUser.getPin();
                    File[] existing = resumeDir.toFile().listFiles((d, n) -> n.startsWith(_pin + "_"));
                    if (existing != null) {
                        java.util.Arrays.stream(existing)
                                .filter(File::isFile)
                                .sorted(java.util.Comparator.comparingLong(File::lastModified).reversed())
                                .forEach(f -> {
                                    // Strip pin_timestamp_ prefix for display
                                    String display = f.getName().replaceFirst("^\\d+_\\d+_", "");
                                    // Deduplicate display names by appending index if needed
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
            // If we can't list resumes, skip the saved-resume section
        }

        Div savedResumeCard = new Div();
        savedResumeCard.getStyle().set("background", GREEN_LIGHT).set("border", "1.5px solid " + GREEN)
                .set("border-radius", "16px").set("padding", "16px 24px").set("width", "100%")
                .set("max-width", "600px").set("margin-top", "16px");

        if (!resumeFileMap.isEmpty()) {
            HorizontalLayout savedRow = new HorizontalLayout();
            savedRow.setWidthFull();
            savedRow.setAlignItems(FlexComponent.Alignment.CENTER);
            savedRow.getStyle().set("gap", "12px");

            if (resumeFileMap.size() == 1) {
                // Single resume — show label + button
                File singleFile = resumeFileMap.values().iterator().next();
                String singleName = resumeFileMap.keySet().iterator().next();

                Span singleLabel = new Span("\uD83D\uDCCE " + singleName);
                singleLabel.getStyle().set("flex", "1").set("font-size", "14px").set("font-weight", "600")
                        .set("color", TEXT_PRIMARY).set("overflow", "hidden")
                        .set("text-overflow", "ellipsis").set("white-space", "nowrap");

                Button useBtn = new Button("Use this resume", VaadinIcon.CHECK_CIRCLE.create());
                useBtn.getStyle().set("background", GREEN).set("color", "white").set("font-weight", "600")
                        .set("border-radius", "9999px").set("border", "none").set("white-space", "nowrap");
                useBtn.addClickListener(e -> loadResumeSkills(singleFile));

                savedRow.add(singleLabel, useBtn);
                savedRow.expand(singleLabel);
            } else {
                // Multiple resumes — show dropdown + button
                Select<String> resumeSelect = new Select<>();
                resumeSelect.setItems(resumeFileMap.keySet());
                resumeSelect.setValue(resumeFileMap.keySet().iterator().next()); // preselect newest
                resumeSelect.setLabel(null);
                resumeSelect.getStyle().set("flex", "1").set("min-width", "0");
                resumeSelect.getElement().setAttribute("title", "Select a saved resume");

                Span selectHint = new Span("\uD83D\uDCCE Saved resumes:");
                selectHint.getStyle().set("font-size", "13px").set("font-weight", "600")
                        .set("color", TEXT_PRIMARY).set("white-space", "nowrap");

                Button useBtn = new Button("Use selected", VaadinIcon.CHECK_CIRCLE.create());
                useBtn.getStyle().set("background", GREEN).set("color", "white").set("font-weight", "600")
                        .set("border-radius", "9999px").set("border", "none").set("white-space", "nowrap");
                useBtn.addClickListener(e -> {
                    String selected = resumeSelect.getValue();
                    if (selected != null) {
                        File selectedFile = resumeFileMap.get(selected);
                        if (selectedFile != null)
                            loadResumeSkills(selectedFile);
                    }
                });

                savedRow.add(selectHint, resumeSelect, useBtn);
                savedRow.expand(resumeSelect);
            }

            savedResumeCard.add(savedRow);
        } else {
            savedResumeCard.setVisible(false);
        }

        // Import from Resume card
        Div importCard = new Div();
        importCard.getStyle().set("background", BG_GRAY);
        importCard.getStyle().set("border", "2px dashed rgba(0,0,0,0.1)");
        importCard.getStyle().set("border-radius", "24px");
        importCard.getStyle().set("padding", "32px");
        importCard.getStyle().set("width", "100%");
        importCard.getStyle().set("max-width", "600px");
        importCard.getStyle().set("text-align", "center");
        importCard.getStyle().set("margin-top", "16px");

        Icon fileIcon = VaadinIcon.FILE_SEARCH.create();
        fileIcon.getStyle().set("color", TEXT_SECONDARY);
        fileIcon.getStyle().set("width", "32px");
        fileIcon.getStyle().set("height", "32px");
        fileIcon.getStyle().set("margin-bottom", "12px");

        H3 importTitle = new H3("Import from Resume");
        importTitle.getStyle().set("font-size", "16px");
        importTitle.getStyle().set("font-weight", "700");
        importTitle.getStyle().set("color", TEXT_PRIMARY);
        importTitle.getStyle().set("margin", "0 0 4px 0");

        Paragraph importDesc = new Paragraph("Upload a new PDF or DOCX to auto-fill skills");
        importDesc.getStyle().set("font-size", "13px");
        importDesc.getStyle().set("color", TEXT_SECONDARY);
        importDesc.getStyle().set("margin", "0 0 16px 0");

        // Create Upload component with FileBuffer
        // FileBuffer writes directly to a file instead of memory, avoiding stream
        // issues
        FileBuffer buffer = new FileBuffer();
        Upload upload = new Upload();
        upload.setReceiver(buffer);

        upload.setDropAllowed(false);
        upload.setAcceptedFileTypes("application/pdf", ".pdf",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx",
                "application/msword", ".doc");
        upload.setMaxFiles(1);
        upload.setMaxFileSize(10 * 1024 * 1024); // 10MB max file size

        // Set up the upload button properly
        upload.setUploadButton(new Button("Upload File"));
        upload.setDropLabel(new Span(""));
        upload.setDropAllowed(false);

        // File name tag — hidden until a file is uploaded
        Div fileTag = new Div();
        fileTag.getStyle().set("display", "inline-flex").set("align-items", "center").set("gap", "6px")
                .set("background", "#e8f5e9").set("color", "#2e7d32").set("font-size", "13px").set("font-weight", "600")
                .set("padding", "6px 14px").set("border-radius", "9999px").set("margin-top", "12px")
                .set("max-width", "100%").set("overflow", "hidden").set("text-overflow", "ellipsis")
                .set("white-space", "nowrap");
        fileTag.setVisible(false);

        // Handle successful upload - FileBuffer provides direct file access
        upload.addSucceededListener(event -> {
            String fileName = event.getFileName();
            LOGGER.info("[UPLOAD] File upload succeeded: " + fileName);

            try {
                // Validate filename
                if (fileName == null || fileName.trim().isEmpty()) {
                    throw new IllegalArgumentException("Filename is empty or null");
                }

                // Extract and validate file extension
                String originalFileName = fileName.trim();
                String fileExtension = "";
                int lastDotIndex = originalFileName.lastIndexOf('.');
                if (lastDotIndex > 0 && lastDotIndex < originalFileName.length() - 1) {
                    fileExtension = originalFileName.substring(lastDotIndex).toLowerCase();
                }
                LOGGER.info("[UPLOAD] File extension detected: " + fileExtension);

                // Validate supported extensions
                Set<String> supportedExtensions = new HashSet<>(Arrays.asList(".pdf", ".docx", ".doc"));
                if (!supportedExtensions.contains(fileExtension)) {
                    throw new IllegalArgumentException(
                            "Unsupported file type: " + fileExtension + ". Only PDF and DOCX files are supported.");
                }

                // Get the temp file that FileBuffer already wrote to disk
                File tempFile = buffer.getFileData().getFile();
                LOGGER.info("[UPLOAD] FileBuffer temp file path: " + tempFile.getAbsolutePath());

                if (!tempFile.exists()) {
                    throw new IOException("Temp file does not exist: " + tempFile.getAbsolutePath());
                }
                if (tempFile.length() == 0) {
                    throw new IOException("Temp file is empty: " + tempFile.getAbsolutePath());
                }
                LOGGER.info("[UPLOAD] Temp file size: " + tempFile.length() + " bytes");

                // Parse the file using the Parser class directly using the temp file path
                Parser parser = new Parser();
                LOGGER.info("[UPLOAD] Starting file parsing...");
                String resumeText = parser.parseFileToJson(tempFile.getAbsolutePath());
                LOGGER.info("[UPLOAD] File parsing completed. Extracted "
                        + (resumeText != null ? resumeText.length() : 0) + " characters");

                // Extract skills from the resume text
                Set<String> extractedSkills = extractSkillsFromText(resumeText);
                LOGGER.info("[UPLOAD] Skills extracted: " + extractedSkills.size() + " skills found");

                // Persist the file to uploads/resumes/ so it appears in Resume Manager
                // and is picked up by loadUserResumeText during cover letter generation
                try {
                    AuthenticationService _authSvc2 = new AuthenticationService();
                    com.clbooster.app.backend.service.profile.User _u2 = _authSvc2.getCurrentUser();
                    if (_u2 != null) {
                        Path resumeDir = Paths.get("uploads", "resumes");
                        if (!Files.exists(resumeDir))
                            Files.createDirectories(resumeDir);
                        String safeOriginal = originalFileName.replaceAll("[^a-zA-Z0-9.\\-]", "_");
                        String persistedName = _u2.getPin() + "_" + System.currentTimeMillis() + "_" + safeOriginal;
                        Files.copy(tempFile.toPath(), resumeDir.resolve(persistedName),
                                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        LOGGER.info("[UPLOAD] Resume persisted to uploads/resumes/" + persistedName);
                    }
                } catch (Exception persistEx) {
                    LOGGER.warning("[UPLOAD] Could not persist resume: " + persistEx.getMessage());
                }

                // Show the uploaded filename in the card
                fileTag.setText("\uD83D\uDCCE " + originalFileName);
                fileTag.setVisible(true);

                if (!extractedSkills.isEmpty()) {
                    selectedSkills.addAll(extractedSkills);
                    updateSkillButtonsUI();

                    String skillsText = String.join(", ", extractedSkills);
                    Notification.show("Skills extracted from resume: " + skillsText, 5000,
                            Notification.Position.TOP_CENTER);
                } else {
                    Notification.show("No matching skills found in the resume. Please select skills manually.", 3000,
                            Notification.Position.TOP_CENTER);
                }

                upload.getElement().executeJs("this.files = []");

            } catch (IllegalArgumentException e) {
                LOGGER.warning("[UPLOAD] Validation error: " + e.getMessage());
                Notification.show("Upload error: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER);
                upload.getElement().executeJs("this.files = []");
            } catch (IOException e) {
                LOGGER.severe("[UPLOAD] File I/O error: " + e.getMessage());
                Notification.show("File error: " + e.getMessage() + ". Please try again.", 5000,
                        Notification.Position.TOP_CENTER);
                upload.getElement().executeJs("this.files = []");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE,
                        "[UPLOAD] Unexpected error processing file '" + fileName + "': " + e.getMessage(), e);
                Notification.show(
                        "Error processing file: " + e.getMessage() + ". Please check the file format and try again.",
                        5000, Notification.Position.TOP_CENTER);
                upload.getElement().executeJs("this.files = []");
            }
        });

        // Handle failed upload
        upload.addFailedListener(event -> {
            LOGGER.severe("[UPLOAD] Upload failed event: "
                    + (event.getReason() != null ? event.getReason().getMessage() : "Unknown reason"));
            Notification.show(
                    "File upload failed: "
                            + (event.getReason() != null ? event.getReason().getMessage() : "Unknown error"),
                    5000, Notification.Position.TOP_CENTER);
        });

        // Handle file rejection (wrong type, too large, etc.)
        upload.addFileRejectedListener(event -> {
            LOGGER.warning("[UPLOAD] File rejected: " + event.getErrorMessage());
            Notification.show("File rejected: " + event.getErrorMessage(), 5000, Notification.Position.TOP_CENTER);
        });

        importCard.add(fileIcon, importTitle, importDesc, upload, fileTag);

        layout.add(title, subtitle, skillsGrid, savedResumeCard, importCard);

        return layout;
    }

    private Button createSkillButton(String skill) {
        Button skillBtn = new Button(skill);
        skillBtn.setWidthFull();
        skillBtn.getStyle().set("padding", "16px");
        skillBtn.getStyle().set("border-radius", "16px");
        skillBtn.getStyle().set("font-weight", "700");
        skillBtn.getStyle().set("font-size", "14px");
        skillBtn.getStyle().set("border", "1px solid rgba(0,0,0,0.05)");
        skillBtn.getStyle().set("background", BG_WHITE);
        skillBtn.getStyle().set("color", TEXT_PRIMARY);
        skillBtn.getStyle().set("cursor", "pointer");
        skillBtn.getStyle().set("transition", "all 0.2s ease");

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
            skillBtn.getStyle().set("box-shadow", "0 4px 12px rgba(0,122,255,0.15)");
        } else {
            skillBtn.getStyle().set("border-color", "rgba(0,0,0,0.05)");
            skillBtn.getStyle().set("box-shadow", "none");
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
        H1 title = new H1("AI Personalization");
        title.getStyle().set("font-size", "36px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("text-align", "center");
        title.getStyle().set("margin", "0");

        Paragraph subtitle = new Paragraph(
                "How should the letter sound? Choose a voice that fits the company culture.");
        subtitle.getStyle().set("font-size", "18px");
        subtitle.getStyle().set("color", TEXT_SECONDARY);
        subtitle.getStyle().set("text-align", "center");
        subtitle.getStyle().set("margin", "0 0 16px 0");

        // Tone cards
        HorizontalLayout cards = new HorizontalLayout();
        cards.setWidthFull();
        cards.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        cards.getStyle().set("gap", "20px");
        cards.getStyle().set("flex-wrap", "wrap");
        cards.getStyle().set("max-width", "800px");

        // Professional card
        cards.add(createToneCard("Professional", "Formal, structured, and strictly business. Best for corporate roles.",
                VaadinIcon.BRIEFCASE, "#e3f2fd", PRIMARY, "Professional".equals(selectedTone)));

        // Creative card (selected by default)
        cards.add(createToneCard("Creative", "Enthusiastic, bold, and unique. Best for startups and agencies.",
                VaadinIcon.STAR, "#f3e5f5", PURPLE, "Creative".equals(selectedTone)));

        // Storyteller card
        cards.add(createToneCard("Storyteller", "Focuses on your journey and impact. Best for senior/lead roles.",
                VaadinIcon.MAGIC, "#fff3e0", ORANGE, "Storyteller".equals(selectedTone)));

        layout.add(title, subtitle, cards);

        return layout;
    }

    private Div createToneCard(String title, String description, VaadinIcon iconName, String bgColor, String iconColor,
            boolean isSelected) {
        Div card = new Div();
        card.getStyle().set("background", BG_WHITE);
        card.getStyle().set("border-radius", "20px");
        card.getStyle().set("padding", "28px");
        card.getStyle().set("width", "220px");
        card.getStyle().set("text-align", "center");
        card.getStyle().set("cursor", "pointer");
        card.getStyle().set("transition", "all 0.2s ease");

        if (isSelected) {
            card.getStyle().set("border", "2px solid " + PRIMARY);
            card.getStyle().set("box-shadow", "0 8px 24px rgba(0,122,255,0.15)");
        } else {
            card.getStyle().set("border", "2px solid transparent");
            card.getStyle().set("box-shadow", "0 2px 8px rgba(0,0,0,0.04)");
        }

        // Icon circle
        Div iconCircle = new Div();
        iconCircle.getStyle().set("width", "48px");
        iconCircle.getStyle().set("height", "48px");
        iconCircle.getStyle().set("border-radius", "50%");
        iconCircle.getStyle().set("background", bgColor);
        iconCircle.getStyle().set("display", "flex");
        iconCircle.getStyle().set("align-items", "center");
        iconCircle.getStyle().set("justify-content", "center");
        iconCircle.getStyle().set("margin", "0 auto 16px auto");

        Icon icon = iconName.create();
        icon.getStyle().set("color", iconColor);
        icon.getStyle().set("width", "24px");
        icon.getStyle().set("height", "24px");
        iconCircle.add(icon);

        H3 cardTitle = new H3(title);
        cardTitle.getStyle().set("font-size", "17px");
        cardTitle.getStyle().set("font-weight", "700");
        cardTitle.getStyle().set("color", TEXT_PRIMARY);
        cardTitle.getStyle().set("margin", "0 0 8px 0");

        Paragraph cardDesc = new Paragraph(description);
        cardDesc.getStyle().set("font-size", "13px");
        cardDesc.getStyle().set("color", TEXT_SECONDARY);
        cardDesc.getStyle().set("line-height", "1.5");
        cardDesc.getStyle().set("margin", "0");

        card.add(iconCircle, cardTitle, cardDesc);

        card.addClickListener(e -> {
            selectedTone = title;
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
        iconContainer.getStyle().set("width", "80px");
        iconContainer.getStyle().set("height", "80px");
        iconContainer.getStyle().set("border-radius", "50%");
        iconContainer.getStyle().set("background", GREEN_LIGHT);
        iconContainer.getStyle().set("display", "flex");
        iconContainer.getStyle().set("align-items", "center");
        iconContainer.getStyle().set("justify-content", "center");
        iconContainer.getStyle().set("margin-bottom", "8px");

        Icon checkIcon = VaadinIcon.CHECK_CIRCLE.create();
        checkIcon.getStyle().set("color", GREEN);
        checkIcon.getStyle().set("width", "40px");
        checkIcon.getStyle().set("height", "40px");
        iconContainer.add(checkIcon);

        // Title
        H1 title = new H1("Ready to Generate!");
        title.getStyle().set("font-size", "36px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("text-align", "center");
        title.getStyle().set("margin", "0");

        Paragraph subtitle = new Paragraph("We've gathered everything we need to build your perfect cover letter.");
        subtitle.getStyle().set("font-size", "18px");
        subtitle.getStyle().set("color", TEXT_SECONDARY);
        subtitle.getStyle().set("text-align", "center");
        subtitle.getStyle().set("margin", "0 0 16px 0");

        // Summary card
        Div card = new Div();
        card.getStyle().set("background", BG_WHITE);
        card.getStyle().set("border", "1px solid rgba(0,0,0,0.08)");
        card.getStyle().set("border-radius", "20px");
        card.getStyle().set("padding", "28px 32px");
        card.getStyle().set("width", "100%");
        card.getStyle().set("max-width", "420px");
        card.getStyle().set("box-shadow", "0 4px 20px rgba(0,0,0,0.04)");

        VerticalLayout summary = new VerticalLayout();
        summary.setPadding(false);
        summary.setSpacing(false);
        summary.getStyle().set("gap", "12px");

        summary.add(createSummaryRow("Role:", jobTitle.isEmpty() ? "Senior Product Designer" : jobTitle));
        summary.add(createSummaryRow("Company:", companyName.isEmpty() ? "Apple Inc." : companyName));
        summary.add(createSummaryRow("Tone:", selectedTone));

        // Divider
        Div divider = new Div();
        divider.getStyle().set("height", "1px");
        divider.getStyle().set("background", "rgba(0,0,0,0.06)");
        divider.getStyle().set("margin", "8px 0");
        summary.add(divider);

        summary.add(createSummaryRow("Access:", "Free Forever", GREEN));

        card.add(summary);

        layout.add(iconContainer, title, subtitle, card);

        return layout;
    }

    private HorizontalLayout createSummaryRow(String label, String value) {
        return createSummaryRow(label, value, TEXT_PRIMARY);
    }

    private HorizontalLayout createSummaryRow(String label, String value, String valueColor) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        row.setAlignItems(FlexComponent.Alignment.CENTER);

        Span labelSpan = new Span(label);
        labelSpan.getStyle().set("font-size", "14px");
        labelSpan.getStyle().set("color", TEXT_SECONDARY);

        Span valueSpan = new Span(value);
        valueSpan.getStyle().set("font-size", "14px");
        valueSpan.getStyle().set("font-weight", "700");
        valueSpan.getStyle().set("color", valueColor);

        row.add(labelSpan, valueSpan);

        return row;
    }

    private VerticalLayout createFormField(String label, VaadinIcon icon, String placeholder) {
        VerticalLayout group = new VerticalLayout();
        group.setPadding(false);
        group.setSpacing(false);
        group.getStyle().set("gap", "6px");

        HorizontalLayout labelRow = new HorizontalLayout();
        labelRow.setAlignItems(FlexComponent.Alignment.CENTER);
        labelRow.getStyle().set("gap", "6px");

        Icon iconComponent = icon.create();
        iconComponent.getStyle().set("width", "14px");
        iconComponent.getStyle().set("height", "14px");
        iconComponent.getStyle().set("color", TEXT_SECONDARY);

        Span labelText = new Span(label);
        labelText.getStyle().set("font-size", "12px");
        labelText.getStyle().set("font-weight", "700");
        labelText.getStyle().set("color", TEXT_SECONDARY);
        labelText.getStyle().set("text-transform", "uppercase");
        labelText.getStyle().set("letter-spacing", "0.1em");

        labelRow.add(iconComponent, labelText);
        group.add(labelRow);

        TextField field = new TextField();
        field.setPlaceholder(placeholder);
        field.setWidthFull();
        field.getStyle().set("background", BG_WHITE);
        field.getStyle().set("border", "1px solid rgba(0,0,0,0.1)");
        field.getStyle().set("border-radius", "12px");
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
        backButton = new Button("Back");
        backButton.getStyle().set("background", "rgba(0,0,0,0.05)");
        backButton.getStyle().set("color", TEXT_PRIMARY);
        backButton.getStyle().set("font-weight", "600");
        backButton.getStyle().set("border-radius", "9999px");
        backButton.getStyle().set("padding", "14px 32px");
        backButton.getStyle().set("font-size", "15px");
        backButton.setVisible(false);
        backButton.addClickListener(e -> {
            if (currentStep > 1) {
                showStep(currentStep - 1);
            }
        });

        // Next/Generate button
        nextButton = new Button("Next Step", VaadinIcon.ARROW_RIGHT.create());
        nextButton.getStyle().set("background", PRIMARY);
        nextButton.getStyle().set("color", "white");
        nextButton.getStyle().set("font-weight", "600");
        nextButton.getStyle().set("border-radius", "9999px");
        nextButton.getStyle().set("padding", "14px 40px");
        nextButton.getStyle().set("font-size", "15px");
        nextButton.getStyle().set("min-width", "180px");
        nextButton.getStyle().set("box-shadow", "0 10px 20px -4px rgba(0,122,255,0.3)");
        nextButton.getStyle().set("gap", "8px");
        nextButton.addClickListener(e -> handleNext());

        // Save button — visible only on step 5
        saveButton = new Button("Save", VaadinIcon.CHECK.create());
        saveButton.getStyle().set("background", GREEN);
        saveButton.getStyle().set("color", "white");
        saveButton.getStyle().set("font-weight", "600");
        saveButton.getStyle().set("border-radius", "9999px");
        saveButton.getStyle().set("padding", "14px 32px");
        saveButton.getStyle().set("font-size", "15px");
        saveButton.getStyle().set("box-shadow", "0 10px 20px -4px rgba(52,199,89,0.35)");
        saveButton.getStyle().set("gap", "8px");
        saveButton.setVisible(false);
        saveButton.addClickListener(e -> {
            if (editorTextArea == null || editorTextArea.getValue().isBlank()) {
                Notification.show("Nothing to save yet.", 2000, Notification.Position.TOP_CENTER);
                return;
            }
            saveButton.setEnabled(false);
            saveButton.setText("Saving...");
            String path = saveGeneratedCoverLetter(editorTextArea.getValue());
            if (path != null) {
                savedFilePath = path;
                saveButton.setText("Saved ✓");
                Notification.show("Cover letter saved!", 2500, Notification.Position.TOP_CENTER);
            } else {
                saveButton.setText("Save");
                Notification.show("Save failed — please try again.", 3000, Notification.Position.TOP_CENTER);
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
            saveButton.setText("Save");
            saveButton.setEnabled(true);
            nextButton.setVisible(false);
        } else if (currentStep == 4) {
            backButton.setVisible(true);
            saveButton.setVisible(false);
            nextButton.setVisible(true);
            nextButton.setText("Generate & Edit");
            nextButton.setIcon(VaadinIcon.MAGIC.create());
        } else {
            backButton.setVisible(currentStep > 1);
            saveButton.setVisible(false);
            nextButton.setVisible(true);
            nextButton.setText("Next Step");
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
                    Notification.show("Please select a tone to continue", 3000, Notification.Position.TOP_CENTER);
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
                step1JobTitleField.setErrorMessage("Job title is required");
                step1JobTitleField.setInvalid(true);
                valid = false;
            } else {
                step1JobTitleField.setInvalid(false);
            }
        }

        if (step1CompanyField != null) {
            if (companyName == null || companyName.trim().isEmpty()) {
                step1CompanyField.setErrorMessage("Company name is required");
                step1CompanyField.setInvalid(true);
                valid = false;
            } else {
                step1CompanyField.setInvalid(false);
            }
        }

        if (step1DescField != null) {
            if (jobDescription == null || jobDescription.trim().isEmpty()) {
                step1DescField.setErrorMessage("Job description is required");
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
        loadingOverlay.getStyle().set("background", "rgba(255,255,255,0.9)");
        loadingOverlay.getStyle().set("display", "flex");
        loadingOverlay.getStyle().set("align-items", "center");
        loadingOverlay.getStyle().set("justify-content", "center");
        loadingOverlay.getStyle().set("z-index", "100");
        loadingOverlay.getStyle().set("border-radius", "24px");

        Div spinner = new Div();
        spinner.getStyle().set("width", "40px");
        spinner.getStyle().set("height", "40px");
        spinner.getStyle().set("border", "3px solid rgba(0,122,255,0.2)");
        spinner.getStyle().set("border-top-color", PRIMARY);
        spinner.getStyle().set("border-radius", "50%");
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
        header.getStyle().set("padding", "0 0 8px 0");

        VerticalLayout titleGroup = new VerticalLayout();
        titleGroup.setPadding(false);
        titleGroup.setSpacing(false);
        H1 jobTitleLabel = new H1(jobTitle);
        jobTitleLabel.getStyle().set("font-size", "20px").set("font-weight", "700").set("color", TEXT_PRIMARY)
                .set("margin", "0");
        Paragraph subLabel = new Paragraph(companyName + " • " + selectedTone + " tone");
        subLabel.getStyle().set("font-size", "13px").set("color", TEXT_SECONDARY).set("margin", "0");
        titleGroup.add(jobTitleLabel, subLabel);

        HorizontalLayout actions = new HorizontalLayout();
        actions.setAlignItems(FlexComponent.Alignment.CENTER);
        actions.getStyle().set("gap", "12px");

        Button saveDocxBtn = new Button("Save DOCX", VaadinIcon.DOWNLOAD.create());
        saveDocxBtn.getStyle().set("background", "rgba(0,0,0,0.05)").set("color", TEXT_PRIMARY)
                .set("font-weight", "600").set("border-radius", "9999px").set("padding", "10px 20px");
        saveDocxBtn.addClickListener(e -> downloadEditorAsDocx());

        Button exportPdfBtn = new Button("Export PDF", VaadinIcon.FILE_TEXT.create());
        exportPdfBtn.getStyle().set("background", PRIMARY).set("color", "white").set("font-weight", "600")
                .set("border-radius", "9999px").set("padding", "10px 24px").set("border", "none")
                .set("box-shadow", "0 10px 15px -3px rgba(0,122,255,0.3)");
        exportPdfBtn.addClickListener(e -> downloadEditorAsPdf());

        actions.add(saveDocxBtn, exportPdfBtn);
        header.add(titleGroup, actions);
        header.expand(titleGroup);

        // ── Toolbar ────────────────────────────────────────────────────────────
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidthFull();
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.getStyle().set("gap", "8px").set("padding", "12px 16px").set("background", BG_GRAY).set("border-radius",
                "12px");

        Button boldBtn = createEditorToolbarButton(VaadinIcon.BOLD, "Bold");
        boldBtn.addClickListener(e -> wrapEditorContent("**", "**"));
        Button italicBtn = createEditorToolbarButton(VaadinIcon.ITALIC, "Italic");
        italicBtn.addClickListener(e -> wrapEditorContent("_", "_"));
        Button underlineBtn = createEditorToolbarButton(VaadinIcon.UNDERLINE, "Underline");
        underlineBtn.addClickListener(e -> wrapEditorContent("__", "__"));

        Button copyBtn = createEditorToolbarButton(VaadinIcon.COPY, "Copy all");
        copyBtn.addClickListener(e -> {
            if (editorTextArea != null) {
                UI.getCurrent().getPage().executeJs("navigator.clipboard.writeText($0)", editorTextArea.getValue());
                Notification.show("Copied to clipboard", 2000, Notification.Position.TOP_CENTER);
            }
        });

        Button clearBtn = createEditorToolbarButton(VaadinIcon.TRASH, "Clear");
        clearBtn.addClickListener(e -> {
            if (editorTextArea != null)
                editorTextArea.setValue("");
        });

        Div divider = new Div();
        divider.getStyle().set("width", "1px").set("height", "24px").set("background", "rgba(0,0,0,0.1)");

        Button regenBtn = new Button("Regenerate", VaadinIcon.MAGIC.create());
        regenBtn.getStyle().set("background", PRIMARY + "15").set("color", PRIMARY).set("font-weight", "600")
                .set("border-radius", "9999px").set("padding", "8px 16px").set("border", "none");
        regenBtn.addClickListener(e -> {
            if (editorTextArea == null)
                return;
            editorTextArea.setValue("⏳ Regenerating...");
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
        editorTextArea.getStyle().set("background", BG_WHITE).set("border", "1px solid rgba(0,0,0,0.1)")
                .set("border-radius", "16px").set("padding", "24px").set("font-size", "15px").set("line-height", "1.8")
                .set("color", TEXT_PRIMARY)
                .set("font-family", "-apple-system, BlinkMacSystemFont, 'SF Pro Text', sans-serif");
        editorTextArea.setValue("\u23f3 Generating your cover letter with AI, please wait...");
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
        btn.getStyle().set("background", "transparent").set("color", TEXT_SECONDARY).set("border", "none")
                .set("padding", "8px").set("border-radius", "8px");
        btn.getElement().addEventListener("mouseenter", e -> btn.getStyle().set("background", "rgba(0,0,0,0.05)"));
        btn.getElement().addEventListener("mouseleave", e -> btn.getStyle().set("background", "transparent"));
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
        StringBuilder ctx = new StringBuilder();

        try {
            // Use data captured on the UI thread – do NOT call VaadinSession from here
            if (capturedUserPin < 0) {
                LOGGER.warning("buildCandidateContext: no user captured before background thread");
                return fallbackSkillsSummary();
            }

            int pin = capturedUserPin;

            // 1 – Basic identity
            ctx.append("=== CANDIDATE PROFILE ===\n");
            ctx.append("Name: ").append(capturedUserName).append("\n");

            // 2 – Profile data from DB
            com.clbooster.app.backend.service.profile.ProfileDAO profileDAO = new com.clbooster.app.backend.service.profile.ProfileDAO();
            com.clbooster.app.backend.service.profile.Profile profile = profileDAO.getProfileByPin(pin);

            if (profile != null) {
                if (profile.getExperienceLevel() != null && !profile.getExperienceLevel().isBlank())
                    ctx.append("Experience Level: ").append(profile.getExperienceLevel()).append("\n");
                if (profile.getSkills() != null && !profile.getSkills().isBlank())
                    ctx.append("Profile Skills: ").append(profile.getSkills()).append("\n");
                if (profile.getTools() != null && !profile.getTools().isBlank())
                    ctx.append("Tools & Technologies: ").append(profile.getTools()).append("\n");
                if (profile.getLink() != null && !profile.getLink().isBlank())
                    ctx.append("Portfolio/LinkedIn: ").append(profile.getLink()).append("\n");
                if (profile.getProfileEmail() != null && !profile.getProfileEmail().isBlank())
                    ctx.append("Contact Email: ").append(profile.getProfileEmail()).append("\n");
            } else {
                LOGGER.info("buildCandidateContext: no profile row for PIN " + pin);
            }

            // 3 – Parsed resume text
            String resumeText = loadUserResumeText(pin);
            if (resumeText != null && !resumeText.isBlank()) {
                ctx.append("\n=== RESUME CONTENT ===\n").append(resumeText).append("\n");
            } else {
                // Fall back to wizard-selected skills if no resume uploaded yet
                ctx.append("\nSelected Skills for this application: ").append(String.join(", ", selectedSkills))
                        .append("\n");
                LOGGER.info("buildCandidateContext: no resume found, using wizard skills");
            }

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "buildCandidateContext error: " + e.getMessage(), e);
            return fallbackSkillsSummary();
        }

        return ctx.toString();
    }

    private String fallbackSkillsSummary() {
        return String.format("Experienced professional with skills in %s. Seeking a %s role at %s.",
                String.join(", ", selectedSkills), jobTitle, companyName);
    }

    /**
     * Parses {@code file}, extracts matching skills, selects them in the UI,
     * and shows a notification. Used by both single- and multi-resume selectors.
     */
    private void loadResumeSkills(File file) {
        try {
            String text = new Parser().parseFileToJson(file.getAbsolutePath());
            Set<String> skills = extractSkillsFromText(text);
            if (!skills.isEmpty()) {
                selectedSkills.addAll(skills);
                updateSkillButtonsUI();
                Notification.show("Skills loaded: " + String.join(", ", skills),
                        5000, Notification.Position.TOP_CENTER);
            } else {
                Notification.show("Resume loaded — no matching skills found.", 3000,
                        Notification.Position.TOP_CENTER);
            }
        } catch (Exception ex) {
            Notification.show("Could not read resume: " + ex.getMessage(), 3000,
                    Notification.Position.TOP_CENTER);
        }
    }

    /**
     * Finds the most recently uploaded resume for the given PIN in uploads/resumes/
     * and returns its parsed text content.
     */
    private String loadUserResumeText(int userPin) {
        try {
            Path resumeDir = Paths.get("uploads", "resumes");
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
            Path dir = Paths.get("uploads", "coverletters");
            if (!Files.exists(dir))
                Files.createDirectories(dir);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = userPin + "_" + timestamp + "_" + sanitizeEditorFilename(companyName) + "_"
                    + sanitizeEditorFilename(jobTitle) + ".docx";
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
            Notification.show("Nothing to download.", 2000, Notification.Position.TOP_CENTER);
            return;
        }
        try {
            Path dir = Paths.get("uploads", "coverletters");
            if (!Files.exists(dir))
                Files.createDirectories(dir);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = sanitizeEditorFilename(companyName) + "_" + sanitizeEditorFilename(jobTitle) + "_"
                    + timestamp + ".docx";
            Path outPath = dir.resolve(fileName);
            new Exporter().saveAsDoc(content, outPath.toString());
            byte[] bytes = Files.readAllBytes(outPath);
            serveEditorDownload(bytes, "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    fileName);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "DOCX export failed: " + ex.getMessage(), ex);
            Notification.show("Export failed: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER);
        }
    }

    private void downloadEditorAsPdf() {
        if (editorTextArea == null)
            return;
        String content = editorTextArea.getValue();
        if (content == null || content.isBlank()) {
            Notification.show("Nothing to export.", 2000, Notification.Position.TOP_CENTER);
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
        Notification.show("Downloading " + fileName + "\u2026", 2000, Notification.Position.TOP_CENTER);
    }

    private byte[] generateSimplePdf(String text) throws IOException {
        java.util.List<String> lines = new java.util.ArrayList<>();
        for (String rawLine : text.split("\n", -1)) {
            if (rawLine.length() <= 90) {
                lines.add(rawLine);
            } else {
                for (int i = 0; i < rawLine.length(); i += 90)
                    lines.add(rawLine.substring(i, Math.min(i + 90, rawLine.length())));
            }
        }
        final float TOP_MARGIN = 800f, LEFT_MARGIN = 50f, LINE_HEIGHT = 14f, BOTTOM_MARGIN = 50f, PAGE_HEIGHT = 841.89f;
        final int FONT_SIZE = 11;
        final int LINES_PER_PAGE = (int) ((TOP_MARGIN - BOTTOM_MARGIN) / LINE_HEIGHT);
        java.util.List<java.util.List<String>> pages = new java.util.ArrayList<>();
        for (int i = 0; i < lines.size(); i += LINES_PER_PAGE)
            pages.add(lines.subList(i, Math.min(i + LINES_PER_PAGE, lines.size())));
        if (pages.isEmpty())
            pages.add(new java.util.ArrayList<>());
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
            stream.append("BT /F1 ").append(FONT_SIZE).append(" Tf ").append(LEFT_MARGIN).append(" ").append(TOP_MARGIN)
                    .append(" Td ").append(LINE_HEIGHT).append(" TL\n");
            for (String line : pages.get(p)) {
                String safe = line.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
                stream.append("(").append(safe).append(") Tj T*\n");
            }
            stream.append("ET");
            String s = stream.toString();
            offsets.add(out.size());
            write.accept((4 + p * 2) + " 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595.28 " + PAGE_HEIGHT
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
                .replaceAll("^_|_$", "");
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
