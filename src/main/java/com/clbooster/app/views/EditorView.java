package com.clbooster.app.views;

import com.clbooster.aiservice.AIService;
import com.clbooster.aiservice.Exporter;
import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.vaadin.flow.theme.lumo.LumoUtility;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Editor View - Cover letter editor with split layout
 * Rich text editor on the left, AI suggestions sidebar on the right
 * Following Apple Design System
 */
@Route(value = "editor", layout = MainLayout.class)
@PageTitle("Editor | CL Booster")
public class EditorView extends HorizontalLayout {

    // Figma Design System Colors
    private static final String PRIMARY = "#007AFF";
    private static final String TEXT_PRIMARY = "#1d1d1f";
    private static final String TEXT_SECONDARY = "#86868b";
    private static final String BG_WHITE = "#ffffff";
    private static final String BG_GRAY = "#f5f5f7";

    private static final Logger LOGGER = Logger.getLogger(EditorView.class.getName());

    private TextArea editorArea;
    private String jobTitle;
    private String companyName;
    private String selectedTone;
    private Set<String> selectedSkills;
    private String jobDescription;
    private String userName;
    private String savedFilePath; // path of the saved .docx after generation

    // Loading indicator shown while AI generates
    private Div loadingOverlay;

    public EditorView() {
        setSizeFull();
        setPadding(true);
        getStyle().set("gap", "24px");
        getStyle().set("background", BG_WHITE);
        getStyle().set("padding", "24px");

        // Read wizard data from VaadinSession
        VaadinSession session = VaadinSession.getCurrent();
        this.jobTitle = getSessionAttribute(session, "gen.jobTitle", null);
        this.companyName = getSessionAttribute(session, "gen.company", null);
        this.selectedTone = getSessionAttribute(session, "gen.tone", "Professional");
        @SuppressWarnings("unchecked")
        Set<String> skills = (Set<String>) session.getAttribute("gen.skills");
        this.selectedSkills = skills != null ? skills : Set.of();
        this.jobDescription = getSessionAttribute(session, "gen.jobDesc", "");

        // Validate required session data
        if (this.jobTitle == null || this.companyName == null) {
            LOGGER.warning("Required session data missing. Redirecting to generator wizard.");
            Notification.show("Please complete the generator form first.", 3000, Notification.Position.TOP_CENTER);
            getUI().ifPresent(ui -> ui.navigate(GeneratorWizardView.class));
            
            // Set defaults to avoid NPE during construction
            this.jobTitle = this.jobTitle != null ? this.jobTitle : "Cover Letter";
            this.companyName = this.companyName != null ? this.companyName : "Company";
            this.userName = "User";
            return;
        }

        // Get user name from AuthenticationService
        AuthenticationService authService = new AuthenticationService();
        com.clbooster.app.backend.service.profile.User currentUser = authService.getCurrentUser();
        this.userName = currentUser != null ? currentUser.getFirstName() + " " + currentUser.getLastName() : "User";

        // Left side - Editor
        VerticalLayout editorPanel = createEditorPanel();
        editorPanel.setWidth("65%");

        // Right side - AI Sidebar
        VerticalLayout sidebarPanel = createSidebarPanel();
        sidebarPanel.setWidth("35%");
        sidebarPanel.setMaxWidth("400px");

        add(editorPanel, sidebarPanel);
        expand(editorPanel);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (editorArea == null) return; // redirected case

        // Show loading state
        editorArea.setValue("⏳ Generating your cover letter with AI, please wait...");
        editorArea.setEnabled(false);

        UI ui = attachEvent.getUI();

        // Run AI generation in background thread
        Thread generationThread = new Thread(() -> {
            String result = generateCoverLetter();
            ui.access(() -> {
                editorArea.setValue(result);
                editorArea.setEnabled(true);
                // Save after generation completes
                savedFilePath = saveGeneratedCoverLetter(result);
            });
        });
        generationThread.setDaemon(true);
        generationThread.start();
    }

    private String getSessionAttribute(VaadinSession session, String key, String defaultValue) {
        Object value = session.getAttribute(key);
        return value != null ? value.toString() : defaultValue;
    }

    // ── Editor Panel ───────────────────────────────────────────────────────────

    private VerticalLayout createEditorPanel() {
        VerticalLayout panel = new VerticalLayout();
        panel.getStyle().set("gap", "16px");
        panel.setHeightFull();
        panel.setSpacing(false);
        panel.addClassNames(LumoUtility.Padding.MEDIUM);

        editorArea = new TextArea();
        editorArea.setWidthFull();
        editorArea.setHeightFull();
        editorArea.getStyle().set("background", BG_WHITE);
        editorArea.getStyle().set("border", "1px solid rgba(0,0,0,0.1)");
        editorArea.getStyle().set("border-radius", "16px");
        editorArea.getStyle().set("padding", "32px");
        editorArea.getStyle().set("font-size", "15px");
        editorArea.getStyle().set("line-height", "1.8");
        editorArea.getStyle().set("color", TEXT_PRIMARY);
        editorArea.getStyle().set("font-family", "-apple-system, BlinkMacSystemFont, 'SF Pro Text', sans-serif");
        editorArea.getStyle().set("resize", "none");

        panel.add(createEditorHeader(), createToolbar(), editorArea);
        panel.expand(editorArea);

        return panel;
    }

    private HorizontalLayout createEditorHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle().set("padding", "0 0 8px 0");

        // Back button and title
        HorizontalLayout leftGroup = new HorizontalLayout();
        leftGroup.setAlignItems(FlexComponent.Alignment.CENTER);
        leftGroup.getStyle().set("gap", "16px");

        Button backBtn = new Button(VaadinIcon.ARROW_LEFT.create());
        backBtn.getStyle().set("background", "transparent");
        backBtn.getStyle().set("color", TEXT_SECONDARY);
        backBtn.getStyle().set("border", "none");
        backBtn.getStyle().set("cursor", "pointer");
        backBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(DashboardView.class)));

        VerticalLayout titleGroup = new VerticalLayout();
        titleGroup.setPadding(false);
        titleGroup.setSpacing(false);

        H2 title = new H2(jobTitle);
        title.getStyle().set("font-size", "20px").set("font-weight", "700")
            .set("color", TEXT_PRIMARY).set("margin", "0");

        Paragraph subtitle = new Paragraph(companyName + " • " + selectedTone + " tone");
        subtitle.getStyle().set("font-size", "13px").set("color", TEXT_SECONDARY).set("margin", "0");

        titleGroup.add(title, subtitle);
        leftGroup.add(backBtn, titleGroup);

        // Action buttons
        HorizontalLayout actions = new HorizontalLayout();
        actions.setAlignItems(FlexComponent.Alignment.CENTER);
        actions.getStyle().set("gap", "12px");

        // Save as DOCX button
        Button saveDocxBtn = new Button("Save DOCX", VaadinIcon.DOWNLOAD.create());
        saveDocxBtn.getStyle().set("background", "rgba(0,0,0,0.05)").set("color", TEXT_PRIMARY)
            .set("font-weight", "600").set("border-radius", "9999px").set("padding", "10px 20px");
        saveDocxBtn.addClickListener(e -> downloadAsDocx());

        // Export PDF button
        Button exportPdfBtn = createPrimaryButton("Export PDF", VaadinIcon.FILE_TEXT);
        exportPdfBtn.addClickListener(e -> downloadAsPdf());

        actions.add(saveDocxBtn, exportPdfBtn);
        header.add(leftGroup, actions);
        header.expand(leftGroup);

        return header;
    }

    private HorizontalLayout createToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidthFull();
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.getStyle().set("gap", "8px").set("padding", "12px 16px")
            .set("background", BG_GRAY).set("border-radius", "12px").set("margin-bottom", "8px");

        // Wrap selected text in markers (plain-text simulation)
        Button boldBtn = createToolbarButton(VaadinIcon.BOLD, "Bold");
        boldBtn.addClickListener(e -> wrapContent("**", "**"));

        Button italicBtn = createToolbarButton(VaadinIcon.ITALIC, "Italic");
        italicBtn.addClickListener(e -> wrapContent("_", "_"));

        Button underlineBtn = createToolbarButton(VaadinIcon.UNDERLINE, "Underline");
        underlineBtn.addClickListener(e -> wrapContent("__", "__"));

        Button listBtn = createToolbarButton(VaadinIcon.LIST, "Bullet list");
        listBtn.addClickListener(e -> {
            if (editorArea == null) return;
            String current = editorArea.getValue();
            editorArea.setValue(current + (current.endsWith("\n") ? "" : "\n") + "• ");
        });

        // Copy button
        Button copyBtn = createToolbarButton(VaadinIcon.COPY, "Copy all");
        copyBtn.addClickListener(e -> {
            if (editorArea != null) {
                editorArea.getElement().executeJs(
                    "navigator.clipboard.writeText($0)", editorArea.getValue());
                Notification.show("Copied to clipboard!", 2000, Notification.Position.TOP_CENTER);
            }
        });

        // Clear button
        Button clearBtn = createToolbarButton(VaadinIcon.TRASH, "Clear");
        clearBtn.addClickListener(e -> { if (editorArea != null) editorArea.clear(); });

        Div divider = new Div();
        divider.getStyle().set("width", "1px").set("height", "24px")
            .set("background", "rgba(0,0,0,0.1)");

        Button regenBtn = new Button("Regenerate", VaadinIcon.MAGIC.create());
        regenBtn.getStyle().set("background", PRIMARY + "15").set("color", PRIMARY)
            .set("font-weight", "600").set("border-radius", "9999px")
            .set("padding", "8px 16px").set("border", "none");
        regenBtn.addClickListener(e -> regenerateLetter());

        toolbar.add(boldBtn, italicBtn, underlineBtn, listBtn, copyBtn, clearBtn, divider, regenBtn);
        return toolbar;
    }

    private void wrapContent(String prefix, String suffix) {
        if (editorArea == null) return;
        String current = editorArea.getValue();
        if (current.isEmpty()) return;
        // Toggle: if already wrapped at start/end, remove; otherwise add
        if (current.startsWith(prefix) && current.endsWith(suffix)) {
            editorArea.setValue(current.substring(prefix.length(),
                current.length() - suffix.length()));
        } else {
            editorArea.setValue(prefix + current + suffix);
        }
    }

    private Button createToolbarButton(VaadinIcon icon, String tooltip) {
        Button btn = new Button(icon.create());
        btn.getElement().setAttribute("title", tooltip);
        btn.getStyle().set("background", "transparent").set("color", TEXT_SECONDARY)
            .set("border", "none").set("padding", "8px").set("border-radius", "8px");
        btn.getElement().addEventListener("mouseenter",
            e -> btn.getStyle().set("background", "rgba(0,0,0,0.05)"));
        btn.getElement().addEventListener("mouseleave",
            e -> btn.getStyle().set("background", "transparent"));
        return btn;
    }

    // ── Downloads ──────────────────────────────────────────────────────────────

    private void downloadAsDocx() {
        if (editorArea == null) return;
        String content = editorArea.getValue();
        if (content == null || content.isBlank()) {
            Notification.show("Nothing to export yet.", 2000, Notification.Position.TOP_CENTER);
            return;
        }
        try {
            String fileName = sanitizeForFilename(companyName) + "_" +
                sanitizeForFilename(jobTitle) + "_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".docx";
            Path outPath = Paths.get(System.getProperty("java.io.tmpdir"), fileName);

            new Exporter().saveAsDoc(content, outPath.toString());

            byte[] bytes = Files.readAllBytes(outPath);
            serveDownload(bytes,
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                fileName);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "DOCX export failed", ex);
            Notification.show("Export failed: " + ex.getMessage(), 4000, Notification.Position.TOP_CENTER);
        }
    }

    private void downloadAsPdf() {
        if (editorArea == null) return;
        String content = editorArea.getValue();
        if (content == null || content.isBlank()) {
            Notification.show("Nothing to export yet.", 2000, Notification.Position.TOP_CENTER);
            return;
        }
        try {
            String fileName = sanitizeForFilename(companyName) + "_" +
                sanitizeForFilename(jobTitle) + "_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";

            byte[] pdfBytes = generateSimplePdf(content);
            if (pdfBytes == null || pdfBytes.length == 0) {
                Notification.show("PDF generation produced no output.", 4000, Notification.Position.TOP_CENTER);
                return;
            }
            serveDownload(pdfBytes, "application/pdf", fileName);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "PDF export failed", ex);
            Notification.show("Export failed: " + ex.getMessage(), 4000, Notification.Position.TOP_CENTER);
        }
    }

    private void serveDownload(byte[] bytes, String mimeType, String fileName) {
        StreamResource resource = new StreamResource(fileName, () -> new ByteArrayInputStream(bytes));
        resource.setContentType(mimeType);

        Anchor anchor = new Anchor(resource, "");
        anchor.getElement().setAttribute("download", fileName);
        anchor.getElement().setAttribute("style", "display:none");
        add(anchor);
        anchor.getElement().executeJs(
            "var a=$0;setTimeout(function(){a.click();setTimeout(function(){a.remove();},1000);},100);",
            anchor.getElement());

        Notification.show("Downloading " + fileName + "…", 2000, Notification.Position.TOP_CENTER);
    }

    // ── Minimal PDF writer ─────────────────────────────────────────────────────

    private byte[] generateSimplePdf(String text) throws IOException {
        List<String> lines = new ArrayList<>();
        for (String rawLine : text.split("\n", -1)) {
            String escaped = rawLine
                .replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("\r", "");
            while (escaped.length() > 90) {
                int breakAt = escaped.lastIndexOf(' ', 90);
                if (breakAt <= 0) breakAt = 90;
                lines.add(escaped.substring(0, breakAt));
                escaped = escaped.substring(breakAt).replaceFirst("^ ", "");
            }
            lines.add(escaped);
        }

        final float TOP_MARGIN = 800f;
        final float LEFT_MARGIN = 50f;
        final float LINE_HEIGHT = 14f;
        final float BOTTOM_MARGIN = 50f;
        final float PAGE_HEIGHT = 841.89f;
        final int FONT_SIZE = 11;
        final int LINES_PER_PAGE = (int) ((TOP_MARGIN - BOTTOM_MARGIN) / LINE_HEIGHT);

        List<List<String>> pages = new ArrayList<>();
        for (int i = 0; i < lines.size(); i += LINES_PER_PAGE) {
            pages.add(lines.subList(i, Math.min(i + LINES_PER_PAGE, lines.size())));
        }
        if (pages.isEmpty()) pages.add(new ArrayList<>());

        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();

        java.util.function.Consumer<String> write = s -> {
            try { out.write(s.getBytes(java.nio.charset.StandardCharsets.US_ASCII)); }
            catch (IOException e) { throw new java.io.UncheckedIOException(e); }
        };

        write.accept("%PDF-1.4\n");

        // obj 1: Catalog
        offsets.add(out.size());
        write.accept("1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

        // obj 2: Pages
        offsets.add(out.size());
        StringBuilder kids = new StringBuilder();
        for (int i = 0; i < pages.size(); i++) kids.append(4 + i * 2).append(" 0 R ");
        write.accept("2 0 obj\n<< /Type /Pages /Kids [" + kids + "] /Count " + pages.size() + " >>\nendobj\n");

        // obj 3: Font
        offsets.add(out.size());
        write.accept("3 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>\nendobj\n");

        // Pages + content streams starting at obj 4
        for (int p = 0; p < pages.size(); p++) {
            int pageObj = 4 + p * 2;
            int contentObj = pageObj + 1;

            StringBuilder stream = new StringBuilder();
            stream.append("BT\n/F1 ").append(FONT_SIZE).append(" Tf\n");
            float y = TOP_MARGIN;
            for (String line : pages.get(p)) {
                stream.append(LEFT_MARGIN).append(" ").append(y).append(" Td\n");
                stream.append("(").append(line).append(") Tj\n");
                y -= LINE_HEIGHT;
                stream.append(-LEFT_MARGIN).append(" 0 Td\n");
            }
            stream.append("ET\n");
            byte[] streamBytes = stream.toString().getBytes(java.nio.charset.StandardCharsets.ISO_8859_1);

            offsets.add(out.size());
            write.accept(pageObj + " 0 obj\n<< /Type /Page /Parent 2 0 R"
                + " /MediaBox [0 0 595.28 " + PAGE_HEIGHT + "]"
                + " /Resources << /Font << /F1 3 0 R >> >>"
                + " /Contents " + contentObj + " 0 R >>\nendobj\n");

            offsets.add(out.size());
            write.accept(contentObj + " 0 obj\n<< /Length " + streamBytes.length + " >>\nstream\n");
            out.write(streamBytes);
            write.accept("\nendstream\nendobj\n");
        }

        int xrefOffset = out.size();
        int totalObjs = 3 + pages.size() * 2;
        write.accept("xref\n0 " + (totalObjs + 1) + "\n");
        write.accept("0000000000 65535 f \n");
        for (int offset : offsets) {
            write.accept(String.format("%010d 00000 n \n", offset));
        }
        write.accept("trailer\n<< /Size " + (totalObjs + 1) + " /Root 1 0 R >>\n");
        write.accept("startxref\n" + xrefOffset + "\n%%EOF\n");

        return out.toByteArray();
    }

    // ── Regenerate ─────────────────────────────────────────────────────────────

    private void regenerateLetter() {
        if (editorArea == null) return;
        editorArea.setValue("⏳ Regenerating...");
        editorArea.setEnabled(false);
        UI ui = UI.getCurrent();
        Thread t = new Thread(() -> {
            String result = generateCoverLetter();
            ui.access(() -> {
                editorArea.setValue(result);
                editorArea.setEnabled(true);
                savedFilePath = saveGeneratedCoverLetter(result);
            });
        });
        t.setDaemon(true);
        t.start();
    }

    // ── Sidebar ────────────────────────────────────────────────────────────────

    private VerticalLayout createSidebarPanel() {
        VerticalLayout panel = new VerticalLayout();
        panel.getStyle().set("gap", "20px");
        panel.setHeightFull();
        panel.setSpacing(false);
        panel.addClassNames(LumoUtility.Padding.Top.MEDIUM, LumoUtility.Padding.Right.MEDIUM,
            LumoUtility.Padding.Bottom.MEDIUM, LumoUtility.Padding.Left.MEDIUM);
        panel.add(createScoreCard(), createTipsCard());
        return panel;
    }

    private Div createScoreCard() {
        Div card = new Div();
        card.getStyle().set("background", BG_WHITE).set("border", "1px solid rgba(0,0,0,0.05)")
            .set("border-radius", "24px").set("padding", "24px");

        HorizontalLayout header = new HorizontalLayout();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle().set("gap", "12px").set("margin-bottom", "16px");

        Icon chartIcon = VaadinIcon.CHART.create();
        chartIcon.getStyle().set("color", PRIMARY);

        H3 title = new H3("Match Score");
        title.getStyle().set("font-size", "16px").set("font-weight", "700")
            .set("color", TEXT_PRIMARY).set("margin", "0");
        header.add(chartIcon, title);

        Paragraph placeholder = new Paragraph("Match score calculation coming soon");
        placeholder.getStyle().set("font-size", "14px").set("color", TEXT_SECONDARY).set("margin", "0");

        card.add(header, placeholder);
        return card;
    }

    private Div createTipsCard() {
        Div card = new Div();
        card.getStyle().set("background", BG_GRAY).set("border-radius", "24px").set("padding", "24px");

        H3 title = new H3("Writing Tips");
        title.getStyle().set("font-size", "16px").set("font-weight", "700")
            .set("color", TEXT_PRIMARY).set("margin", "0 0 16px 0");
        card.add(title);

        for (String tip : new String[]{"Keep it under 400 words", "Use active voice",
                "Match the company's tone", "Proofread twice"}) {
            HorizontalLayout row = new HorizontalLayout();
            row.setAlignItems(FlexComponent.Alignment.CENTER);
            row.getStyle().set("gap", "10px").set("margin-bottom", "10px");
            Icon check = VaadinIcon.CHECK.create();
            check.getStyle().set("color", "#34C759").set("width", "16px");
            Span text = new Span(tip);
            text.getStyle().set("font-size", "13px").set("color", TEXT_SECONDARY);
            row.add(check, text);
            card.add(row);
        }
        return card;
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private Button createPrimaryButton(String text, VaadinIcon icon) {
        Button btn = new Button(text, icon.create());
        btn.getStyle().set("background", PRIMARY).set("color", "white")
            .set("font-weight", "600").set("border-radius", "9999px")
            .set("padding", "10px 24px").set("border", "none")
            .set("box-shadow", "0 10px 15px -3px rgba(0,122,255,0.3)");
        return btn;
    }

    private String generateCoverLetter() {
        StringBuilder jobDetails = new StringBuilder();
        jobDetails.append("Job Title: ").append(jobTitle).append("\n");
        jobDetails.append("Company: ").append(companyName).append("\n");
        jobDetails.append("Tone: ").append(selectedTone).append("\n");
        jobDetails.append("Selected Skills: ").append(String.join(", ", selectedSkills)).append("\n");
        jobDetails.append("Job Description: ").append(jobDescription).append("\n");

        String resumeContent = String.format(
            "Experienced professional with skills in %s. Seeking to apply expertise in a %s role at %s.",
            String.join(", ", selectedSkills), jobTitle, companyName);

        try {
            AIService aiService = new AIService("");
            String generated = aiService.generateCoverLetter(resumeContent, jobDetails.toString());
            return (generated != null && !generated.isBlank()) ? generated : getFallbackCoverLetter();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "AI generation failed, using fallback: " + e.getMessage(), e);
            return getFallbackCoverLetter();
        }
    }

    private String getFallbackCoverLetter() {
        return "Dear Hiring Manager,\n\n" +
               "I am writing to express my strong interest in the " + jobTitle +
               " position at " + companyName + ". " +
               "With relevant experience and skills in " + String.join(", ", selectedSkills) + ", " +
               "I am excited about the opportunity to contribute to your team.\n\n" +
               "My background aligns well with the requirements outlined in the job description. " +
               "I am particularly drawn to this role because of " + companyName +
               "'s reputation for excellence.\n\n" +
               "Thank you for considering my application. I look forward to discussing how my skills " +
               "align with your team's vision.\n\n" +
               "Best regards,\n" + userName;
    }

    /**
     * Saves the cover letter as DOCX and returns the saved file path (or null on failure).
     */
    private String saveGeneratedCoverLetter(String content) {
        try {
            AuthenticationService authService = new AuthenticationService();
            com.clbooster.app.backend.service.profile.User currentUser = authService.getCurrentUser();
            if (currentUser == null) {
                LOGGER.warning("Cannot save cover letter – no user logged in");
                return null;
            }
            int userPin = currentUser.getPin();
            Path dir = Paths.get("uploads", "coverletters");
            if (!Files.exists(dir)) Files.createDirectories(dir);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = userPin + "_" + timestamp + "_" +
                sanitizeForFilename(companyName) + "_" + sanitizeForFilename(jobTitle) + ".docx";
            Path filePath = dir.resolve(fileName);

            new Exporter().saveAsDoc(content, filePath.toString());
            LOGGER.info("Cover letter saved: " + filePath.toAbsolutePath());
            return filePath.toAbsolutePath().toString();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to save cover letter: " + e.getMessage(), e);
            return null;
        }
    }

    private String sanitizeForFilename(String input) {
        if (input == null || input.isBlank()) return "Unknown";
        return input.trim()
            .replaceAll("\\s+", "_")
            .replaceAll("[^a-zA-Z0-9_\\-]", "")
            .replaceAll("_+", "_")
            .replaceAll("^_|_$", "");
    }
}
