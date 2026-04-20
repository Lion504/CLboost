package com.clbooster.app.views;

import com.clbooster.app.views.util.StyleConstants;

import jakarta.annotation.security.PermitAll;
import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.clbooster.app.backend.service.document.DocumentService;
import com.clbooster.app.backend.service.profile.ProfileService;
import com.clbooster.app.i18n.TranslationService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Route(value = "resume", layout = MainLayout.class)
@PageTitle("Resume Manager | CL Booster")
@PermitAll
public class ResumeManagerView extends VerticalLayout {

    private static final String PRIMARY = "#007AFF";
    private static final String PRIMARY_LIGHT = "#5AC8FA";
    private static final String TEXT_PRIMARY = "#1d1d1f";
    private static final String TEXT_SECONDARY = "#86868b";
    private static final String BG_WHITE = "#ffffff";
    private static final String BG_GRAY = "#f5f5f7";
    private static final String SUCCESS = "#34C759";
    private static final String WARNING = "#FF9500";
    private static final String ERROR = "#FF3B30";

    private static final Logger LOGGER = Logger.getLogger(ResumeManagerView.class.getName());

        private final transient DocumentService documentService;
    private final TranslationService translationService;
    private List<ResumeData> resumes = new ArrayList<>();
    private VerticalLayout resumeListContainer;
    private Span countBadge;

    public ResumeManagerView(DocumentService documentService) {
        this.documentService = documentService;
        this.translationService = new TranslationService();
        setPadding(true);
        setSpacing(true);
        getStyle().set("gap", "32px");
        getStyle().set(StyleConstants.CSS_PADDING, "32px");
        getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        getStyle().set("font-family",
                "-apple-system, BlinkMacSystemFont, 'SF Pro Text', 'SF Pro Display', system-ui, sans-serif");
        setSizeFull();

        // Load actual resumes from filesystem
        resumes = loadResumesFromFilesystem();

        buildUI();
    }

    private void buildUI() {
        // Header section
        HorizontalLayout header = createHeader();

        // Main content area
        HorizontalLayout mainContent = new HorizontalLayout();
        mainContent.setWidthFull();
        mainContent.getStyle().set("gap", "32px");
        mainContent.getStyle().set(StyleConstants.CSS_ALIGN_ITEMS, "flex-start");

        // Left panel - Upload and Resume List
        VerticalLayout leftPanel = createLeftPanel();
        leftPanel.setWidth("100%");

        mainContent.add(leftPanel);
        mainContent.expand(leftPanel);

        add(header, mainContent);
    }

    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        // Title group
        VerticalLayout titleGroup = new VerticalLayout();
        titleGroup.setPadding(false);
        titleGroup.setSpacing(false);
        titleGroup.getStyle().set("gap", "4px");

        H1 title = new H1(translationService.translate("resume.title"));
        title.getStyle().set(StyleConstants.CSS_FONT_SIZE, "30px");
        title.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        title.getStyle().set(StyleConstants.CSS_LETTER_SPACING, "-0.025em");
        title.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        title.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        Paragraph subtitle = new Paragraph(translationService.translate("resume.uploadManage"));
        subtitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        subtitle.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        subtitle.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        titleGroup.add(title, subtitle);

        // Action buttons - Only Import from LinkedIn (shows future feature message)
        HorizontalLayout actions = new HorizontalLayout();
        actions.getStyle().set("gap", "12px");

        Button importBtn = new Button(translationService.translate("resume.importLinkedIn"), VaadinIcon.LINK.create());
        importBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_GRAY);
        importBtn.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        importBtn.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "600");
        importBtn.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX);
        importBtn.getStyle().set(StyleConstants.CSS_PADDING, "10px 20px");
        importBtn.getStyle().set(StyleConstants.CSS_BORDER, "none");
        importBtn.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);

        importBtn.addClickListener(e -> {
            Notification.show(translationService.translate("resume.linkedInComing"), 5000,
                    Notification.Position.TOP_CENTER);
        });

        importBtn.getElement().addEventListener(StyleConstants.VAL_MOUSEENTER, e -> {
            importBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, "rgba(0,0,0,0.08)");
        });
        importBtn.getElement().addEventListener(StyleConstants.VAL_MOUSELEAVE, e -> {
            importBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_GRAY);
        });

        actions.add(importBtn);

        header.add(titleGroup, actions);
        header.expand(titleGroup);

        return header;
    }

    private VerticalLayout createLeftPanel() {
        VerticalLayout panel = new VerticalLayout();
        panel.setPadding(false);
        panel.setSpacing(false);
        panel.getStyle().set("gap", "24px");

        // ---- Tab toggle: Upload File / Paste Text ----
        HorizontalLayout tabToggle = new HorizontalLayout();
        tabToggle.setPadding(false);
        tabToggle.setSpacing(false);
        tabToggle.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_GRAY).set(StyleConstants.CSS_BORDER_RADIUS, "12px").set(StyleConstants.CSS_PADDING, "4px")
                .set("gap", "4px").set(StyleConstants.CSS_WIDTH, "fit-content");

        Button uploadTabBtn = new Button(translationService.translate("resume.uploadFile"));
        Button pasteTabBtn = new Button(translationService.translate("resume.pasteText"));

        styleActiveTab(uploadTabBtn, true);
        styleActiveTab(pasteTabBtn, false);

        // Upload zone and paste panel — only one is visible at a time
        Div uploadZone = createUploadZone();
        Div pastePanel = createPasteTextPanel();
        pastePanel.setVisible(false);

        uploadTabBtn.addClickListener(e -> {
            styleActiveTab(uploadTabBtn, true);
            styleActiveTab(pasteTabBtn, false);
            uploadZone.setVisible(true);
            pastePanel.setVisible(false);
        });
        pasteTabBtn.addClickListener(e -> {
            styleActiveTab(uploadTabBtn, false);
            styleActiveTab(pasteTabBtn, true);
            uploadZone.setVisible(false);
            pastePanel.setVisible(true);
        });

        tabToggle.add(uploadTabBtn, pasteTabBtn);

        // Resume list header
        HorizontalLayout listHeader = new HorizontalLayout();
        listHeader.setWidthFull();
        listHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        listHeader.getStyle().set(StyleConstants.CSS_MARGIN_TOP, "8px");

        H2 listTitle = new H2(translationService.translate("resume.yourResumes"));
        listTitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "18px");
        listTitle.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        listTitle.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        listTitle.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        countBadge = new Span(String.valueOf(resumes.size()));
        countBadge.getStyle().set(StyleConstants.CSS_FONT_SIZE, "12px");
        countBadge.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        countBadge.getStyle().set(StyleConstants.CSS_PADDING, "4px 10px");
        countBadge.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_GRAY);
        countBadge.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        countBadge.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX);

        HorizontalLayout titleWithBadge = new HorizontalLayout();
        titleWithBadge.setAlignItems(FlexComponent.Alignment.CENTER);
        titleWithBadge.getStyle().set("gap", "8px");
        titleWithBadge.add(listTitle, countBadge);

        // Sort dropdown button
        Button sortBtn = new Button(translationService.translate("resume.sortByRecent"),
                VaadinIcon.CHEVRON_DOWN.create());
        sortBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, StyleConstants.VAL_TRANSPARENT);
        sortBtn.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        sortBtn.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "500");
        sortBtn.getStyle().set(StyleConstants.CSS_BORDER, "none");
        sortBtn.getStyle().set(StyleConstants.CSS_PADDING, "8px 12px");

        // Create sort menu - open on left click
        ContextMenu sortMenu = new ContextMenu();
        sortMenu.setTarget(sortBtn);
        sortMenu.setOpenOnClick(true);
        sortMenu.addItem(translationService.translate("resume.sortByRecent"), e -> {
            sortResumes(ResumeSort.RECENT);
            sortBtn.setText(translationService.translate("resume.sortByRecent"));
        });
        sortMenu.addItem(translationService.translate("resume.sortByName"), e -> {
            sortResumes(ResumeSort.NAME_ASC);
            sortBtn.setText(translationService.translate("resume.sortByName"));
        });
        sortMenu.addItem(translationService.translate("resume.sortByNameZA"), e -> {
            sortResumes(ResumeSort.NAME_DESC);
            sortBtn.setText(translationService.translate("resume.sortByNameZA"));
        });
        sortMenu.addItem(translationService.translate("resume.sortBySize"), e -> {
            sortResumes(ResumeSort.SIZE);
            sortBtn.setText(translationService.translate("resume.sortBySize"));
        });

        listHeader.add(titleWithBadge, sortBtn);
        listHeader.expand(titleWithBadge);

        // Resume list container
        resumeListContainer = new VerticalLayout();
        resumeListContainer.setPadding(false);
        resumeListContainer.setSpacing(false);
        resumeListContainer.getStyle().set("gap", "16px");

        refreshResumeList();

        panel.add(tabToggle, uploadZone, pastePanel, listHeader, resumeListContainer);

        return panel;
    }

    private Div createUploadZone() {
        FileBuffer buffer = new FileBuffer();
        Upload upload = new Upload(buffer);

        // Accept all three formats - use extensions only for broader browser
        // compatibility
        upload.setAcceptedFileTypes("application/pdf", ".pdf",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx",
                "application/msword", ".doc", "text/plain", "text/x-plain", "application/text", ".txt");
        upload.setMaxFileSize(10 * 1024 * 1024);
        upload.setMaxFiles(1);
        upload.setDropAllowed(true);

        // Style upload component to fill the zone visually — don't hide it
        upload.getStyle().set("position", "absolute").set("inset", "0").set(StyleConstants.CSS_OPACITY, "0").set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER)
                .set(StyleConstants.CSS_WIDTH, "100%").set(StyleConstants.CSS_HEIGHT, "100%");

        // Wrapper is position:relative so absolute upload overlays it
        Div uploadContainer = new Div();
        uploadContainer.getStyle().set("position", "relative").set(StyleConstants.CSS_WIDTH, "90%")
                .set(StyleConstants.CSS_BACKGROUND, "linear-gradient(135deg, rgba(0,122,255,0.05) 0%, rgba(90,200,250,0.05) 100%)")
                .set(StyleConstants.CSS_BORDER, "2px dashed " + PRIMARY + "40").set(StyleConstants.CSS_BORDER_RADIUS, "24px").set(StyleConstants.CSS_PADDING, "48px")
                .set(StyleConstants.CSS_TRANSITION, StyleConstants.VAL_ALL_0_3S).set(StyleConstants.CSS_TEXT_ALIGN, StyleConstants.VAL_CENTER).set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);

        Div iconContainer = new Div();
        iconContainer.getStyle().set(StyleConstants.CSS_WIDTH, "72px").set(StyleConstants.CSS_HEIGHT, "72px").set(StyleConstants.CSS_BACKGROUND, "rgba(0,122,255,0.1)")
                .set(StyleConstants.CSS_BORDER_RADIUS, "50%").set(StyleConstants.CSS_DISPLAY, "flex").set(StyleConstants.CSS_ALIGN_ITEMS, StyleConstants.VAL_CENTER)
                .set(StyleConstants.CSS_JUSTIFY_CONTENT, StyleConstants.VAL_CENTER).set(StyleConstants.CSS_MARGIN, "0 auto 24px");

        Icon uploadIcon = VaadinIcon.UPLOAD_ALT.create();
        uploadIcon.getStyle().set(StyleConstants.CSS_COLOR, PRIMARY).set(StyleConstants.CSS_WIDTH, "32px").set(StyleConstants.CSS_HEIGHT, "32px");
        iconContainer.add(uploadIcon);

        H3 title = new H3(translationService.translate("resume.dropResume"));
        title.getStyle().set(StyleConstants.CSS_FONT_SIZE, "20px").set(StyleConstants.CSS_FONT_WEIGHT, "700").set(StyleConstants.CSS_COLOR, TEXT_PRIMARY).set(StyleConstants.CSS_MARGIN,
                "0 0 8px 0");

        Paragraph subtitle = new Paragraph(translationService.translate("resume.orClickBrowse"));
        subtitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px").set(StyleConstants.CSS_COLOR, TEXT_SECONDARY).set(StyleConstants.CSS_MARGIN, "0 0 24px 0");

        HorizontalLayout formats = new HorizontalLayout();
        formats.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        formats.getStyle().set("gap", "8px");
        formats.add(createFormatBadge("PDF"));
        formats.add(createFormatBadge("DOCX"));
        formats.add(createFormatBadge("TXT"));

        // Visual content layer (pointer-events: none so clicks pass through to upload)
        Div visualLayer = new Div();
        visualLayer.getStyle().set("pointer-events", "none");
        visualLayer.add(iconContainer, title, subtitle, formats);

        uploadContainer.add(visualLayer, upload);

        // Drag-over styling
        uploadContainer.getElement().addEventListener("dragover",
                e -> uploadContainer.getStyle().set(StyleConstants.CSS_BORDER_COLOR, PRIMARY).set(StyleConstants.CSS_BACKGROUND,
                        "linear-gradient(135deg, rgba(0,122,255,0.1) 0%, rgba(90,200,250,0.1) 100%)"));
        uploadContainer.getElement().addEventListener("dragleave",
                e -> uploadContainer.getStyle().set(StyleConstants.CSS_BORDER_COLOR, PRIMARY + "40").set(StyleConstants.CSS_BACKGROUND,
                        "linear-gradient(135deg, rgba(0,122,255,0.05) 0%, rgba(90,200,250,0.05) 100%)"));
        uploadContainer.getElement().addEventListener("drop",
                e -> uploadContainer.getStyle().set(StyleConstants.CSS_BORDER_COLOR, PRIMARY + "40").set(StyleConstants.CSS_BACKGROUND,
                        "linear-gradient(135deg, rgba(0,122,255,0.05) 0%, rgba(90,200,250,0.05) 100%)"));

        // File upload success handler
        upload.addSucceededListener(event -> {
            String fileName = event.getFileName();
            int userPin = getCurrentUserPin();
            try {
                String storedPath = documentService.storeResumeFile(buffer.getInputStream(), fileName,
                        String.valueOf(userPin));
                new ProfileService().updateCVTimestamp(userPin);

                String safeOriginalName = fileName.replaceAll("[^a-zA-Z0-9._\\-]", "_");
                LOGGER.info("Resume uploaded via DocumentService: " + storedPath);
                Notification.show(translationService.translate("resume.uploadedSuccessfully", safeOriginalName), 3000,
                        Notification.Position.TOP_CENTER);

                resumes = loadResumesFromFilesystem();
                refreshResumeList();

            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Upload failed", ex);
                Notification.show(translationService.translate("resume.uploadFailed", ex.getMessage()), 5000,
                        Notification.Position.TOP_CENTER);
            }
        });

        upload.addFailedListener(event -> Notification.show(
                translationService.translate("resume.uploadFailed",
                        event.getReason() != null ? event.getReason().getMessage() : "Unknown error"),
                5000, Notification.Position.TOP_CENTER));

        upload.addFileRejectedListener(
                event -> Notification.show(translationService.translate("resume.fileRejected", event.getErrorMessage()),
                        5000, Notification.Position.TOP_CENTER));

        return uploadContainer;
    }

    private void styleActiveTab(Button btn, boolean active) {
        if (active) {
            btn.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE).set(StyleConstants.CSS_COLOR, TEXT_PRIMARY).set(StyleConstants.CSS_FONT_WEIGHT, "700")
                    .set(StyleConstants.CSS_BORDER_RADIUS, "10px").set(StyleConstants.CSS_PADDING, "8px 20px").set(StyleConstants.CSS_BORDER, "none")
                    .set(StyleConstants.CSS_BOX_SHADOW, "0 1px 4px rgba(0,0,0,0.1)").set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);
        } else {
            btn.getStyle().set(StyleConstants.CSS_BACKGROUND, StyleConstants.VAL_TRANSPARENT).set(StyleConstants.CSS_COLOR, TEXT_SECONDARY).set(StyleConstants.CSS_FONT_WEIGHT, "500")
                    .set(StyleConstants.CSS_BORDER_RADIUS, "10px").set(StyleConstants.CSS_PADDING, "8px 20px").set(StyleConstants.CSS_BORDER, "none")
                    .set(StyleConstants.CSS_BOX_SHADOW, "none").set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);
        }
    }

    private Div createPasteTextPanel() {
        Div panel = new Div();
        panel.getStyle().set(StyleConstants.CSS_WIDTH, "90%").set(StyleConstants.CSS_BACKGROUND, BG_WHITE).set(StyleConstants.CSS_BORDER, "1px solid rgba(0,0,0,0.08)")
                .set(StyleConstants.CSS_BORDER_RADIUS, "24px").set(StyleConstants.CSS_PADDING, "32px");

        VerticalLayout inner = new VerticalLayout();
        inner.setPadding(false);
        inner.setSpacing(false);
        inner.getStyle().set("gap", "16px");

        H3 heading = new H3(translationService.translate("resume.pasteResumeText"));
        heading.getStyle().set(StyleConstants.CSS_FONT_SIZE, "18px").set(StyleConstants.CSS_FONT_WEIGHT, "700").set(StyleConstants.CSS_COLOR, TEXT_PRIMARY).set(StyleConstants.CSS_MARGIN,
                "0");

        TextArea resumeTextArea = new TextArea();
        resumeTextArea.setPlaceholder(translationService.translate("resume.pastePlainText"));
        resumeTextArea.setWidthFull();
        resumeTextArea.setMinHeight("200px");
        resumeTextArea.getStyle().set("--vaadin-input-field-background", BG_GRAY)
                .set("--vaadin-input-field-border-radius", "12px");

        TextField filenameField = new TextField();
        filenameField.setPlaceholder(translationService.translate("resume.filename"));
        filenameField.setWidthFull();
        filenameField.getStyle().set("--vaadin-input-field-background", BG_GRAY)
                .set("--vaadin-input-field-border-radius", "12px");

        Button saveBtn = new Button(translationService.translate("resume.saveAsText"));
        saveBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, "linear-gradient(135deg, " + PRIMARY + " 0%, " + PRIMARY_LIGHT + " 100%)")
                .set(StyleConstants.CSS_COLOR, StyleConstants.VAL_WHITE).set(StyleConstants.CSS_FONT_WEIGHT, "600").set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX).set(StyleConstants.CSS_BORDER, "none")
                .set(StyleConstants.CSS_PADDING, "12px 24px").set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER)
                .set(StyleConstants.CSS_BOX_SHADOW, "0 10px 15px -3px rgba(0,122,255,0.3)");

        saveBtn.addClickListener(e -> {
            String text = resumeTextArea.getValue();
            if (text == null || text.trim().isEmpty()) {
                Notification.show(translationService.translate("resume.pastePlainText"), 3000,
                        Notification.Position.TOP_CENTER);
                return;
            }
            String filename = filenameField.getValue();
            if (filename == null || filename.trim().isEmpty()) {
                filename = "resume_text.txt";
            } else if (!filename.contains(".")) {
                filename = filename + ".txt";
            }
            int userPin = getCurrentUserPin();
            try {
                String storedPath = documentService.storeResumeText(text, filename, String.valueOf(userPin));
                LOGGER.info("Resume text saved via DocumentService: " + storedPath);
                Notification.show(translationService.translate("resume.textSaved", filename), 3000,
                        Notification.Position.TOP_CENTER);
                resumeTextArea.clear();
                filenameField.clear();
                resumes = loadResumesFromFilesystem();
                refreshResumeList();
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Save resume text failed", ex);
                Notification.show(translationService.translate("resume.failedToSave", ex.getMessage()), 5000,
                        Notification.Position.TOP_CENTER);
            }
        });

        inner.add(heading, resumeTextArea, filenameField, saveBtn);
        panel.add(inner);
        return panel;
    }

    private Span createFormatBadge(String format) {
        Span badge = new Span(format);
        badge.getStyle().set(StyleConstants.CSS_FONT_SIZE, "11px");
        badge.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        badge.getStyle().set(StyleConstants.CSS_PADDING, "6px 12px");
        badge.getStyle().set(StyleConstants.CSS_BACKGROUND, "rgba(0,0,0,0.05)");
        badge.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        badge.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX);
        return badge;
    }

    private void refreshResumeList() {
        resumeListContainer.removeAll();

        if (resumes.isEmpty()) {
            resumeListContainer.add(createEmptyState());
        } else {
            for (ResumeData resume : resumes) {
                resumeListContainer.add(createResumeCard(resume));
            }
        }

        // Update count badge
        countBadge.setText(String.valueOf(resumes.size()));
    }

    private VerticalLayout createEmptyState() {
        VerticalLayout emptyState = new VerticalLayout();
        emptyState.setWidthFull();
        emptyState.setAlignItems(FlexComponent.Alignment.CENTER);
        emptyState.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        emptyState.getStyle().set(StyleConstants.CSS_PADDING, "60px 20px").set("gap", "16px");

        Icon emptyIcon = VaadinIcon.FILE_TEXT_O.create();
        emptyIcon.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY).set(StyleConstants.CSS_WIDTH, "64px").set(StyleConstants.CSS_HEIGHT, "64px");

        H3 title = new H3(translationService.translate("resume.noResumes"));
        title.getStyle().set(StyleConstants.CSS_FONT_SIZE, "20px").set(StyleConstants.CSS_FONT_WEIGHT, "600").set(StyleConstants.CSS_COLOR, TEXT_PRIMARY).set(StyleConstants.CSS_MARGIN,
                "0");

        Paragraph description = new Paragraph(translationService.translate("resume.uploadFirst"));
        description.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px").set(StyleConstants.CSS_COLOR, TEXT_SECONDARY).set(StyleConstants.CSS_MARGIN, "0");

        emptyState.add(emptyIcon, title, description);
        return emptyState;
    }

    private Div createResumeCard(ResumeData resume) {
        Div card = new Div();
        card.getStyle().set(StyleConstants.CSS_WIDTH, "95%");
        card.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        card.getStyle().set(StyleConstants.CSS_BORDER, "1px solid rgba(0,0,0,0.05)");
        card.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "20px");
        card.getStyle().set(StyleConstants.CSS_PADDING, "24px");
        card.getStyle().set(StyleConstants.CSS_BOX_SHADOW, StyleConstants.VAL_0_2_12PX);
        card.getStyle().set(StyleConstants.CSS_TRANSITION, StyleConstants.VAL_ALL_0_3S);
        card.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);

        HorizontalLayout content = new HorizontalLayout();
        content.setWidthFull();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.getStyle().set("gap", "20px");

        // File icon
        Div fileIcon = new Div();
        fileIcon.getStyle().set(StyleConstants.CSS_WIDTH, "56px");
        fileIcon.getStyle().set(StyleConstants.CSS_HEIGHT, "56px");
        fileIcon.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "16px");
        fileIcon.getStyle().set(StyleConstants.CSS_BACKGROUND, getFileColor(resume.format) + "15");
        fileIcon.getStyle().set(StyleConstants.CSS_DISPLAY, "flex");
        fileIcon.getStyle().set(StyleConstants.CSS_ALIGN_ITEMS, StyleConstants.VAL_CENTER);
        fileIcon.getStyle().set(StyleConstants.CSS_JUSTIFY_CONTENT, StyleConstants.VAL_CENTER);
        fileIcon.getStyle().set("flex-shrink", "0");

        Icon icon = VaadinIcon.FILE_TEXT.create();
        icon.getStyle().set(StyleConstants.CSS_COLOR, getFileColor(resume.format));
        icon.getStyle().set(StyleConstants.CSS_WIDTH, "28px");
        icon.getStyle().set(StyleConstants.CSS_HEIGHT, "28px");
        fileIcon.add(icon);

        // File info
        VerticalLayout fileInfo = new VerticalLayout();
        fileInfo.setPadding(false);
        fileInfo.setSpacing(false);
        fileInfo.getStyle().set("gap", "4px");
        fileInfo.getStyle().set("flex", "1");

        HorizontalLayout nameRow = new HorizontalLayout();
        nameRow.setAlignItems(FlexComponent.Alignment.CENTER);
        nameRow.getStyle().set("gap", "8px");

        H3 fileName = new H3(resume.name);
        fileName.getStyle().set(StyleConstants.CSS_FONT_SIZE, "16px");
        fileName.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        fileName.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        fileName.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        if (resume.starred) {
            Icon starIcon = VaadinIcon.STAR.create();
            starIcon.getStyle().set(StyleConstants.CSS_COLOR, WARNING);
            starIcon.getStyle().set(StyleConstants.CSS_WIDTH, "16px");
            starIcon.getStyle().set(StyleConstants.CSS_HEIGHT, "16px");
            nameRow.add(fileName, starIcon);
        } else {
            nameRow.add(fileName);
        }

        HorizontalLayout metaRow = new HorizontalLayout();
        metaRow.setAlignItems(FlexComponent.Alignment.CENTER);
        metaRow.getStyle().set("gap", "12px");

        Span formatBadge = new Span(resume.format);
        formatBadge.getStyle().set(StyleConstants.CSS_FONT_SIZE, "11px");
        formatBadge.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "600");
        formatBadge.getStyle().set(StyleConstants.CSS_PADDING, "2px 8px");
        formatBadge.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_GRAY);
        formatBadge.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        formatBadge.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "4px");

        Span sizeText = new Span(resume.size);
        sizeText.getStyle().set(StyleConstants.CSS_FONT_SIZE, "13px");
        sizeText.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);

        Span dot = new Span("•");
        dot.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);

        Span dateText = new Span(resume.date);
        dateText.getStyle().set(StyleConstants.CSS_FONT_SIZE, "13px");
        dateText.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);

        metaRow.add(formatBadge, sizeText, dot, dateText);

        fileInfo.add(nameRow, metaRow);

        // Actions menu (3 dots) - open on left click
        Button menuBtn = new Button(VaadinIcon.ELLIPSIS_DOTS_H.create());
        menuBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, StyleConstants.VAL_TRANSPARENT);
        menuBtn.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        menuBtn.getStyle().set(StyleConstants.CSS_BORDER, "none");
        menuBtn.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);

        // Create context menu that opens on left click
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setTarget(menuBtn);
        contextMenu.setOpenOnClick(true);
        contextMenu.addItem(translationService.translate("resume.download"), e -> downloadResume(resume));
        contextMenu.addItem(translationService.translate("resume.view"), e -> viewResume(resume));
        contextMenu.addItem(translationService.translate("profile.edit"), e -> toggleStar(resume));
        contextMenu.addItem(translationService.translate("resume.delete"), e -> deleteResume(resume));

        content.add(fileIcon, fileInfo, menuBtn);
        content.expand(fileInfo);

        card.add(content);

        // Hover effects
        card.getElement().addEventListener(StyleConstants.VAL_MOUSEENTER, e -> {
            card.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 8px 24px rgba(0,0,0,0.08)");
            card.getStyle().set(StyleConstants.CSS_TRANSFORM, "translateY(-2px)");
            card.getStyle().set(StyleConstants.CSS_BORDER_COLOR, PRIMARY + "30");
        });

        card.getElement().addEventListener(StyleConstants.VAL_MOUSELEAVE, e -> {
            card.getStyle().set(StyleConstants.CSS_BOX_SHADOW, StyleConstants.VAL_0_2_12PX);
            card.getStyle().set(StyleConstants.CSS_TRANSFORM, "translateY(0)");
            card.getStyle().set(StyleConstants.CSS_BORDER_COLOR, "rgba(0,0,0,0.05)");
        });

        return card;
    }

    private void downloadResume(ResumeData resume) {
        try {
            byte[] bytes = documentService.retrieveResumeFile(resume.filePath);
            String mimeType = resume.format.equalsIgnoreCase("PDF") ? "application/pdf"
                    : "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            serveDownload(bytes, mimeType, resume.name);
            Notification.show(translationService.translate("resume.downloading", resume.name), 2000,
                    Notification.Position.TOP_CENTER);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Download error", ex);
            Notification.show(translationService.translate("resume.downloadError", ex.getMessage()), 3000,
                    Notification.Position.TOP_CENTER);
        }
    }

    private void viewResume(ResumeData resume) {
        try {
            byte[] bytes = documentService.retrieveResumeFile(resume.filePath);
            String mimeType = switch (resume.format.toUpperCase()) {
            case "PDF" -> "application/pdf";
            case "TXT" -> "text/plain";
            default -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            };

            StreamResource resource = new StreamResource(resume.name, () -> new ByteArrayInputStream(bytes));
            resource.setContentType(mimeType);

            com.vaadin.flow.component.html.Anchor anchor = new com.vaadin.flow.component.html.Anchor(resource, "");
            anchor.getElement().setAttribute("target", "_blank");
            anchor.getElement().setAttribute("style", "display:none");
            add(anchor);
            anchor.getElement().executeJs(
                    "var a=$0;setTimeout(function(){a.click();setTimeout(function(){a.remove();},1000);},100);",
                    anchor.getElement());

        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "View error", ex);
            Notification.show(translationService.translate("resume.openError", ex.getMessage()), 3000,
                    Notification.Position.TOP_CENTER);
        }
    }

    private void toggleStar(ResumeData resume) {
        resume.starred = !resume.starred;
        // In a real app, you would persist this to the database
        refreshResumeList();
        Notification.show(
                resume.starred ? translationService.translate("resume.resumeStarred")
                        : translationService.translate("resume.resumeUnstarred"),
                2000, Notification.Position.TOP_CENTER);
    }

    private void deleteResume(ResumeData resume) {
        if (documentService.deleteResumeFile(resume.filePath)) {
            resumes.remove(resume);
            refreshResumeList();
            Notification.show(translationService.translate("resume.resumeDeleted"), 2000,
                    Notification.Position.TOP_CENTER);
        } else {
            Notification.show(translationService.translate("resume.failedDelete"), 3000,
                    Notification.Position.TOP_CENTER);
        }
    }

    private void sortResumes(ResumeSort sort) {
        switch (sort) {
        case RECENT -> resumes.sort((a, b) -> b.lastModified.compareTo(a.lastModified));
        case NAME_ASC -> resumes.sort(Comparator.comparing(a -> a.name.toLowerCase()));
        case NAME_DESC -> resumes.sort((a, b) -> b.name.compareToIgnoreCase(a.name));
        case SIZE -> resumes.sort((a, b) -> Long.compare(parseSize(b.size), parseSize(a.size)));
        }
        refreshResumeList();
    }

    private long parseSize(String sizeStr) {
        try {
            String[] parts = sizeStr.split(" ");
            double value = Double.parseDouble(parts[0]);
            String unit = parts[1].toUpperCase();
            return (long) (value * switch (unit) {
            case "KB" -> 1024;
            case "MB" -> 1024 * 1024;
            case "GB" -> 1024 * 1024 * 1024;
            default -> 1;
            });
        } catch (Exception e) {
            return 0;
        }
    }

    private String getFileColor(String format) {
        return switch (format.toUpperCase()) {
        case "PDF" -> ERROR;
        case "DOCX" -> PRIMARY;
        case "TXT" -> TEXT_SECONDARY;
        default -> PRIMARY;
        };
    }

    private List<ResumeData> loadResumesFromFilesystem() {
        List<ResumeData> items = new ArrayList<>();
        try {
            int userPin = getCurrentUserPin();
            Path resumesDir = Paths.get("uploads", "resumes");
            if (!Files.exists(resumesDir) || !Files.isDirectory(resumesDir)) {
                return items;
            }
            File[] files = resumesDir.toFile().listFiles((d, name) -> {
                String lower = name.toLowerCase();
                // Added .txt support
                return lower.endsWith(".pdf") || lower.endsWith(".docx") || lower.endsWith(".doc")
                        || lower.endsWith(".txt");
            });
            if (files == null)
                return items;
            for (File file : files) {
                ResumeData item = parseResumeFile(file, userPin);
                if (item != null)
                    items.add(item);
            }
            items.sort((a, b) -> b.lastModified.compareTo(a.lastModified));
            LOGGER.info("Loaded " + items.size() + " resumes for PIN: " + userPin);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading resumes: " + e.getMessage(), e);
        }
        return items;
    }

    private ResumeData parseResumeFile(File file, int currentUserPin) {
        try {
            String fileName = file.getName();

            // Parse filename format: PIN_timestamp_originalname.ext
            String[] parts = fileName.split("_", 3);
            if (parts.length < 3) {
                return null;
            }

            int pin;
            try {
                pin = Integer.parseInt(parts[0]);
            } catch (NumberFormatException e) {
                return null;
            }

            // Only show resumes for the current user
            if (pin != currentUserPin) {
                return null;
            }

            // Get file extension/format
            String extension = "";
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0) {
                extension = fileName.substring(dotIndex + 1).toUpperCase();
            }

            // Get original file name (after PIN_timestamp_ prefix)
            String originalName = parts[2];

            // Format file size
            long sizeBytes = file.length();
            String sizeStr = formatFileSize(sizeBytes);

            // Format date
            LocalDateTime modifiedTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()),
                    ZoneId.systemDefault());
            String dateStr = formatDate(modifiedTime);

            return new ResumeData(originalName, extension, sizeStr, dateStr, file.getAbsolutePath(), modifiedTime,
                    false);

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Parse error for: " + file.getName(), e);
            return null;
        }
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024)
            return bytes + " B";
        if (bytes < 1024 * 1024)
            return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024));
    }

    private String formatDate(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        if (dateTime.toLocalDate().equals(now.toLocalDate())) {
            return "Today";
        } else if (dateTime.toLocalDate().equals(now.toLocalDate().minusDays(1))) {
            return "Yesterday";
        } else if (dateTime.isAfter(now.minusDays(7))) {
            return dateTime.format(DateTimeFormatter.ofPattern("EEEE")); // Day name
        } else {
            return dateTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy"));
        }
    }

    private int getCurrentUserPin() {
        try {
            AuthenticationService authService = new AuthenticationService();
            com.clbooster.app.backend.service.profile.User currentUser = authService.getCurrentUser();
            if (currentUser == null) {
                LOGGER.warning("No user logged in, returning default PIN 0");
                return 0;
            }
            return currentUser.getPin();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error getting current user PIN", e);
            return 0;
        }
    }

    /** Serves bytes as a browser download using a hidden anchor + JS click. */
    private void serveDownload(byte[] bytes, String mimeType, String fileName) {
        StreamResource resource = new StreamResource(fileName, () -> new ByteArrayInputStream(bytes));
        resource.setContentType(mimeType);

        com.vaadin.flow.component.html.Anchor anchor = new com.vaadin.flow.component.html.Anchor(resource, "");
        anchor.getElement().setAttribute("download", fileName);
        anchor.getElement().setAttribute("style", "display:none");
        add(anchor);
        anchor.getElement().executeJs(
                "var a=$0;setTimeout(function(){a.click();setTimeout(function(){a.remove();},1000);},100);",
                anchor.getElement());
    }

    private enum ResumeSort {
        RECENT, NAME_ASC, NAME_DESC, SIZE
    }

    // Inner class to represent resume data
    private static class ResumeData implements Serializable {
        private static final long serialVersionUID = 1L;

        String name;
        String format;
        String size;
        String date;
        String filePath;
        LocalDateTime lastModified;
        boolean starred;

        ResumeData(String name, String format, String size, String date, String filePath, LocalDateTime lastModified,
                boolean starred) {
            this.name = name;
            this.format = format;
            this.size = size;
            this.date = date;
            this.filePath = filePath;
            this.lastModified = lastModified;
            this.starred = starred;
        }
    }
}
