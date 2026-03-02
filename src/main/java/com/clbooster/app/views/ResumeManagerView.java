package com.clbooster.app.views;

import com.clbooster.app.backend.service.authentication.AuthenticationService;
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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    private List<ResumeData> resumes = new ArrayList<>();
    private VerticalLayout resumeListContainer;
    private Span countBadge;

    public ResumeManagerView() {
        setPadding(true);
        setSpacing(true);
        getStyle().set("gap", "32px");
        getStyle().set("padding", "32px");
        getStyle().set("background", BG_WHITE);
        getStyle().set("font-family", "-apple-system, BlinkMacSystemFont, 'SF Pro Text', 'SF Pro Display', system-ui, sans-serif");
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
        mainContent.getStyle().set("align-items", "flex-start");

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

        H1 title = new H1("Resume Manager");
        title.getStyle().set("font-size", "30px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("letter-spacing", "-0.025em");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0");

        Paragraph subtitle = new Paragraph("Upload, manage, and optimize your resumes with AI.");
        subtitle.getStyle().set("font-size", "14px");
        subtitle.getStyle().set("color", TEXT_SECONDARY);
        subtitle.getStyle().set("margin", "0");

        titleGroup.add(title, subtitle);

        // Action buttons - Only Import from LinkedIn (shows future feature message)
        HorizontalLayout actions = new HorizontalLayout();
        actions.getStyle().set("gap", "12px");

        Button importBtn = new Button("Import from LinkedIn", VaadinIcon.LINK.create());
        importBtn.getStyle().set("background", BG_GRAY);
        importBtn.getStyle().set("color", TEXT_PRIMARY);
        importBtn.getStyle().set("font-weight", "600");
        importBtn.getStyle().set("border-radius", "9999px");
        importBtn.getStyle().set("padding", "10px 20px");
        importBtn.getStyle().set("border", "none");
        importBtn.getStyle().set("cursor", "pointer");

        importBtn.addClickListener(e -> {
            Notification.show("LinkedIn import is coming soon! This feature will be available in a future update.",
                5000, Notification.Position.TOP_CENTER);
        });

        importBtn.getElement().addEventListener("mouseenter", e -> {
            importBtn.getStyle().set("background", "rgba(0,0,0,0.08)");
        });
        importBtn.getElement().addEventListener("mouseleave", e -> {
            importBtn.getStyle().set("background", BG_GRAY);
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

        // Upload zone with actual file upload functionality
        Div uploadZone = createUploadZone();

        // Resume list header
        HorizontalLayout listHeader = new HorizontalLayout();
        listHeader.setWidthFull();
        listHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        listHeader.getStyle().set("margin-top", "8px");

        H2 listTitle = new H2("Your Resumes");
        listTitle.getStyle().set("font-size", "18px");
        listTitle.getStyle().set("font-weight", "700");
        listTitle.getStyle().set("color", TEXT_PRIMARY);
        listTitle.getStyle().set("margin", "0");

        countBadge = new Span(String.valueOf(resumes.size()));
        countBadge.getStyle().set("font-size", "12px");
        countBadge.getStyle().set("font-weight", "700");
        countBadge.getStyle().set("padding", "4px 10px");
        countBadge.getStyle().set("background", BG_GRAY);
        countBadge.getStyle().set("color", TEXT_SECONDARY);
        countBadge.getStyle().set("border-radius", "9999px");

        HorizontalLayout titleWithBadge = new HorizontalLayout();
        titleWithBadge.setAlignItems(FlexComponent.Alignment.CENTER);
        titleWithBadge.getStyle().set("gap", "8px");
        titleWithBadge.add(listTitle, countBadge);

        // Sort dropdown button
        Button sortBtn = new Button("Sort by: Recent", VaadinIcon.CHEVRON_DOWN.create());
        sortBtn.getStyle().set("background", "transparent");
        sortBtn.getStyle().set("color", TEXT_SECONDARY);
        sortBtn.getStyle().set("font-weight", "500");
        sortBtn.getStyle().set("border", "none");
        sortBtn.getStyle().set("padding", "8px 12px");

        // Create sort menu - open on left click
        ContextMenu sortMenu = new ContextMenu();
        sortMenu.setTarget(sortBtn);
        sortMenu.setOpenOnClick(true);
        sortMenu.addItem("Recent", e -> {
            sortResumes(ResumeSort.RECENT);
            sortBtn.setText("Sort by: Recent");
        });
        sortMenu.addItem("Name (A-Z)", e -> {
            sortResumes(ResumeSort.NAME_ASC);
            sortBtn.setText("Sort by: Name");
        });
        sortMenu.addItem("Name (Z-A)", e -> {
            sortResumes(ResumeSort.NAME_DESC);
            sortBtn.setText("Sort by: Name (Z-A)");
        });
        sortMenu.addItem("Size", e -> {
            sortResumes(ResumeSort.SIZE);
            sortBtn.setText("Sort by: Size");
        });

        listHeader.add(titleWithBadge, sortBtn);
        listHeader.expand(titleWithBadge);

        // Resume list container
        resumeListContainer = new VerticalLayout();
        resumeListContainer.setPadding(false);
        resumeListContainer.setSpacing(false);
        resumeListContainer.getStyle().set("gap", "16px");

        refreshResumeList();

        panel.add(uploadZone, listHeader, resumeListContainer);

        return panel;
    }

    private Div createUploadZone() {
        FileBuffer buffer = new FileBuffer();
        Upload upload = new Upload(buffer);

        // Accept all three formats - use extensions only for broader browser compatibility
        upload.setAcceptedFileTypes(
            "application/pdf", ".pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx",
            "application/msword", ".doc",
            "text/plain", "text/x-plain", "application/text", ".txt"
        );
        upload.setMaxFileSize(10 * 1024 * 1024);
        upload.setMaxFiles(1);
        upload.setDropAllowed(true);

        // Style upload component to fill the zone visually — don't hide it
        upload.getStyle()
            .set("position", "absolute")
            .set("inset", "0")
            .set("opacity", "0")
            .set("cursor", "pointer")
            .set("width", "100%")
            .set("height", "100%");

        // Wrapper is position:relative so absolute upload overlays it
        Div uploadContainer = new Div();
        uploadContainer.getStyle()
            .set("position", "relative")
            .set("width", "90%")
            .set("background", "linear-gradient(135deg, rgba(0,122,255,0.05) 0%, rgba(90,200,250,0.05) 100%)")
            .set("border", "2px dashed " + PRIMARY + "40")
            .set("border-radius", "24px")
            .set("padding", "48px")
            .set("transition", "all 0.3s")
            .set("text-align", "center")
            .set("cursor", "pointer");

        Div iconContainer = new Div();
        iconContainer.getStyle()
            .set("width", "72px").set("height", "72px")
            .set("background", "rgba(0,122,255,0.1)")
            .set("border-radius", "50%")
            .set("display", "flex").set("align-items", "center").set("justify-content", "center")
            .set("margin", "0 auto 24px");

        Icon uploadIcon = VaadinIcon.UPLOAD_ALT.create();
        uploadIcon.getStyle().set("color", PRIMARY).set("width", "32px").set("height", "32px");
        iconContainer.add(uploadIcon);

        H3 title = new H3("Drop your resume here");
        title.getStyle().set("font-size", "20px").set("font-weight", "700")
            .set("color", TEXT_PRIMARY).set("margin", "0 0 8px 0");

        Paragraph subtitle = new Paragraph("or click to browse files (PDF, DOCX, TXT, up to 10MB)");
        subtitle.getStyle().set("font-size", "14px").set("color", TEXT_SECONDARY).set("margin", "0 0 24px 0");

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
        uploadContainer.getElement().addEventListener("dragover", e ->
            uploadContainer.getStyle().set("border-color", PRIMARY).set("background",
                "linear-gradient(135deg, rgba(0,122,255,0.1) 0%, rgba(90,200,250,0.1) 100%)"));
        uploadContainer.getElement().addEventListener("dragleave", e ->
            uploadContainer.getStyle().set("border-color", PRIMARY + "40").set("background",
                "linear-gradient(135deg, rgba(0,122,255,0.05) 0%, rgba(90,200,250,0.05) 100%)"));
        uploadContainer.getElement().addEventListener("drop", e ->
            uploadContainer.getStyle().set("border-color", PRIMARY + "40").set("background",
                "linear-gradient(135deg, rgba(0,122,255,0.05) 0%, rgba(90,200,250,0.05) 100%)"));

        // File upload success handler
        upload.addSucceededListener(event -> {
            String fileName = event.getFileName();
            try {
                Path uploadsDir = Paths.get("uploads", "resumes");
                if (!Files.exists(uploadsDir)) {
                    Files.createDirectories(uploadsDir);
                }

                int userPin = getCurrentUserPin();
                String timestamp = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDateTime.now());
                String safeOriginalName = fileName.replaceAll("[^a-zA-Z0-9._\\-]", "_");
                String newFileName = userPin + "_" + timestamp + "_" + safeOriginalName;
                Path targetPath = uploadsDir.resolve(newFileName);

                File tempFile = buffer.getFileData().getFile();
                if (!tempFile.exists()) {
                    throw new IOException("Uploaded file is missing");
                }
                // Allow zero-byte TXT files; only reject if file doesn't exist
                Files.copy(tempFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                LOGGER.info("Resume uploaded: " + newFileName + " (" + targetPath.toFile().length() + " bytes)");
                Notification.show("\"" + safeOriginalName + "\" uploaded successfully!",
                    3000, Notification.Position.TOP_CENTER);

                resumes = loadResumesFromFilesystem();
                refreshResumeList();

            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Upload failed", ex);
                Notification.show("Upload failed: " + ex.getMessage(), 5000, Notification.Position.TOP_CENTER);
            }
        });

        upload.addFailedListener(event ->
            Notification.show("Upload failed: " +
                (event.getReason() != null ? event.getReason().getMessage() : "Unknown error"),
                5000, Notification.Position.TOP_CENTER));

        upload.addFileRejectedListener(event ->
            Notification.show("File rejected: " + event.getErrorMessage(),
                5000, Notification.Position.TOP_CENTER));

        return uploadContainer;
    }

    private Span createFormatBadge(String format) {
        Span badge = new Span(format);
        badge.getStyle().set("font-size", "11px");
        badge.getStyle().set("font-weight", "700");
        badge.getStyle().set("padding", "6px 12px");
        badge.getStyle().set("background", "rgba(0,0,0,0.05)");
        badge.getStyle().set("color", TEXT_SECONDARY);
        badge.getStyle().set("border-radius", "9999px");
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
        emptyState.getStyle().set("padding", "60px 20px").set("gap", "16px");

        Icon emptyIcon = VaadinIcon.FILE_TEXT_O.create();
        emptyIcon.getStyle().set("color", TEXT_SECONDARY).set("width", "64px").set("height", "64px");

        H3 title = new H3("No resumes yet");
        title.getStyle().set("font-size", "20px").set("font-weight", "600")
            .set("color", TEXT_PRIMARY).set("margin", "0");

        Paragraph description = new Paragraph("Upload your first resume to get started.");
        description.getStyle().set("font-size", "14px").set("color", TEXT_SECONDARY).set("margin", "0");

        emptyState.add(emptyIcon, title, description);
        return emptyState;
    }

    private Div createResumeCard(ResumeData resume) {
        Div card = new Div();
        card.getStyle().set("width", "95%");
        card.getStyle().set("background", BG_WHITE);
        card.getStyle().set("border", "1px solid rgba(0,0,0,0.05)");
        card.getStyle().set("border-radius", "20px");
        card.getStyle().set("padding", "24px");
        card.getStyle().set("box-shadow", "0 2px 12px rgba(0,0,0,0.04)");
        card.getStyle().set("transition", "all 0.3s");
        card.getStyle().set("cursor", "pointer");

        HorizontalLayout content = new HorizontalLayout();
        content.setWidthFull();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.getStyle().set("gap", "20px");

        // File icon
        Div fileIcon = new Div();
        fileIcon.getStyle().set("width", "56px");
        fileIcon.getStyle().set("height", "56px");
        fileIcon.getStyle().set("border-radius", "16px");
        fileIcon.getStyle().set("background", getFileColor(resume.format) + "15");
        fileIcon.getStyle().set("display", "flex");
        fileIcon.getStyle().set("align-items", "center");
        fileIcon.getStyle().set("justify-content", "center");
        fileIcon.getStyle().set("flex-shrink", "0");

        Icon icon = VaadinIcon.FILE_TEXT.create();
        icon.getStyle().set("color", getFileColor(resume.format));
        icon.getStyle().set("width", "28px");
        icon.getStyle().set("height", "28px");
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
        fileName.getStyle().set("font-size", "16px");
        fileName.getStyle().set("font-weight", "700");
        fileName.getStyle().set("color", TEXT_PRIMARY);
        fileName.getStyle().set("margin", "0");

        if (resume.starred) {
            Icon starIcon = VaadinIcon.STAR.create();
            starIcon.getStyle().set("color", WARNING);
            starIcon.getStyle().set("width", "16px");
            starIcon.getStyle().set("height", "16px");
            nameRow.add(fileName, starIcon);
        } else {
            nameRow.add(fileName);
        }

        HorizontalLayout metaRow = new HorizontalLayout();
        metaRow.setAlignItems(FlexComponent.Alignment.CENTER);
        metaRow.getStyle().set("gap", "12px");

        Span formatBadge = new Span(resume.format);
        formatBadge.getStyle().set("font-size", "11px");
        formatBadge.getStyle().set("font-weight", "600");
        formatBadge.getStyle().set("padding", "2px 8px");
        formatBadge.getStyle().set("background", BG_GRAY);
        formatBadge.getStyle().set("color", TEXT_SECONDARY);
        formatBadge.getStyle().set("border-radius", "4px");

        Span sizeText = new Span(resume.size);
        sizeText.getStyle().set("font-size", "13px");
        sizeText.getStyle().set("color", TEXT_SECONDARY);

        Span dot = new Span("•");
        dot.getStyle().set("color", TEXT_SECONDARY);

        Span dateText = new Span(resume.date);
        dateText.getStyle().set("font-size", "13px");
        dateText.getStyle().set("color", TEXT_SECONDARY);

        metaRow.add(formatBadge, sizeText, dot, dateText);

        fileInfo.add(nameRow, metaRow);

        // Actions menu (3 dots) - open on left click
        Button menuBtn = new Button(VaadinIcon.ELLIPSIS_DOTS_H.create());
        menuBtn.getStyle().set("background", "transparent");
        menuBtn.getStyle().set("color", TEXT_SECONDARY);
        menuBtn.getStyle().set("border", "none");
        menuBtn.getStyle().set("cursor", "pointer");

        // Create context menu that opens on left click
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setTarget(menuBtn);
        contextMenu.setOpenOnClick(true);
        contextMenu.addItem("Download", e -> downloadResume(resume));
        contextMenu.addItem("View", e -> viewResume(resume));
        contextMenu.addItem("Toggle Star", e -> toggleStar(resume));
        contextMenu.addItem("Delete", e -> deleteResume(resume));

        content.add(fileIcon, fileInfo, menuBtn);
        content.expand(fileInfo);

        card.add(content);

        // Hover effects
        card.getElement().addEventListener("mouseenter", e -> {
            card.getStyle().set("box-shadow", "0 8px 24px rgba(0,0,0,0.08)");
            card.getStyle().set("transform", "translateY(-2px)");
            card.getStyle().set("border-color", PRIMARY + "30");
        });

        card.getElement().addEventListener("mouseleave", e -> {
            card.getStyle().set("box-shadow", "0 2px 12px rgba(0,0,0,0.04)");
            card.getStyle().set("transform", "translateY(0)");
            card.getStyle().set("border-color", "rgba(0,0,0,0.05)");
        });

        return card;
    }

    private void downloadResume(ResumeData resume) {
        File file = new File(resume.filePath);
        if (!file.exists()) {
            Notification.show("File not found: " + resume.filePath, 3000, Notification.Position.TOP_CENTER);
            return;
        }
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            String mimeType = resume.format.equalsIgnoreCase("PDF") 
                ? "application/pdf" 
                : "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            serveDownload(bytes, mimeType, resume.name);
            Notification.show("Downloading " + resume.name, 2000, Notification.Position.TOP_CENTER);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Download error", ex);
            Notification.show("Error downloading file: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER);
        }
    }

    private void viewResume(ResumeData resume) {
        File file = new File(resume.filePath);
        if (!file.exists()) {
            Notification.show("File not found: " + resume.filePath, 3000, Notification.Position.TOP_CENTER);
            return;
        }
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            String mimeType = switch (resume.format.toUpperCase()) {
                case "PDF"  -> "application/pdf";
                case "TXT"  -> "text/plain";
                default     -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
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
            Notification.show("Error opening file: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER);
        }
    }

    private void toggleStar(ResumeData resume) {
        resume.starred = !resume.starred;
        // In a real app, you would persist this to the database
        refreshResumeList();
        Notification.show(resume.starred ? "Resume starred" : "Resume unstarred", 2000, Notification.Position.TOP_CENTER);
    }

    private void deleteResume(ResumeData resume) {
        File file = new File(resume.filePath);
        if (file.exists()) {
            if (file.delete()) {
                resumes.remove(resume);
                refreshResumeList();
                Notification.show("Resume deleted", 2000, Notification.Position.TOP_CENTER);
            } else {
                Notification.show("Failed to delete resume", 3000, Notification.Position.TOP_CENTER);
            }
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
                return lower.endsWith(".pdf") || lower.endsWith(".docx")
                    || lower.endsWith(".doc") || lower.endsWith(".txt");
            });
            if (files == null) return items;
            for (File file : files) {
                ResumeData item = parseResumeFile(file, userPin);
                if (item != null) items.add(item);
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
            LocalDateTime modifiedTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault());
            String dateStr = formatDate(modifiedTime);

            return new ResumeData(originalName, extension, sizeStr, dateStr, 
                file.getAbsolutePath(), modifiedTime, false);

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Parse error for: " + file.getName(), e);
            return null;
        }
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
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

        ResumeData(String name, String format, String size, String date, 
                   String filePath, LocalDateTime lastModified, boolean starred) {
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
