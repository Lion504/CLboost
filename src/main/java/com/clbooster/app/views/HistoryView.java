package com.clbooster.app.views;

import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Route(value = "history", layout = MainLayout.class)
@PageTitle("History | CL Booster")
public class HistoryView extends VerticalLayout {

    private static final String PRIMARY = "#007AFF";
    private static final String TEXT_PRIMARY = "#1d1d1f";
    private static final String TEXT_SECONDARY = "#86868b";
    private static final String BG_WHITE = "#ffffff";
    private static final String BG_GRAY = "#f5f5f7";
    private static final String SUCCESS = "#34C759";

    private static final Logger LOGGER = Logger.getLogger(HistoryView.class.getName());

    private List<HistoryItem> allItems = new ArrayList<>();
    private Div cardsGrid;
    private TextField searchField;
    private String currentStatusFilter = "ALL";
    private LocalDate dateFrom = null;
    private LocalDate dateTo = null;

    public HistoryView() {
        setPadding(true);
        setSpacing(true);
        getStyle().set("gap", "32px").set("padding", "32px")
            .set("background", BG_WHITE)
            .set("font-family", "-apple-system, BlinkMacSystemFont, 'SF Pro Text', system-ui, sans-serif");
        setSizeFull();

        allItems = loadCoverLetterHistory();

        add(createHeader(), createFilters());

        cardsGrid = new Div();
        cardsGrid.getStyle()
            .set("display", "flex")
            .set("flex-wrap", "wrap")
            .set("gap", "24px")
            .set("width", "100%");

        refreshCardsGrid(allItems);
        add(cardsGrid);
        add(createLoadMoreButton());
    }

    // ── Header ─────────────────────────────────────────────────────────────────

    private HorizontalLayout createHeader() {
        VerticalLayout titleGroup = new VerticalLayout();
        titleGroup.setPadding(false);
        titleGroup.setSpacing(false);

        H2 title = new H2("Generation History");
        title.getStyle().set("font-size", "30px").set("font-weight", "700")
            .set("color", TEXT_PRIMARY).set("margin", "0");

        Paragraph subtitle = new Paragraph("Review, refine, or regenerate your previous successful applications.");
        subtitle.getStyle().set("font-size", "14px").set("color", TEXT_SECONDARY).set("margin", "0");
        titleGroup.add(title, subtitle);

        Button exportBtn = new Button("Export All", VaadinIcon.DOWNLOAD.create());
        exportBtn.getStyle().set("background", "rgba(0,0,0,0.05)").set("color", TEXT_PRIMARY)
            .set("font-weight", "600").set("border-radius", "9999px")
            .set("padding", "10px 20px").set("border", "none");
        exportBtn.addClickListener(e -> exportAllFiles());

        HorizontalLayout header = new HorizontalLayout(titleGroup, exportBtn);
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.expand(titleGroup);
        return header;
    }

    // ── Filters ────────────────────────────────────────────────────────────────

    private HorizontalLayout createFilters() {
        HorizontalLayout filters = new HorizontalLayout();
        filters.setAlignItems(FlexComponent.Alignment.CENTER);
        filters.getStyle().set("gap", "12px");

        searchField = new TextField();
        searchField.setPlaceholder("Search by company or role...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setWidth("320px");
        searchField.getStyle().set("--vaadin-input-field-background", BG_GRAY)
            .set("--vaadin-input-field-border-radius", "12px");
        searchField.addValueChangeListener(e -> applyFilters());

        Button dateFilter = createFilterButton("Date Range", VaadinIcon.CALENDAR);
        dateFilter.addClickListener(e -> showDateFilterDialog());

        Button statusFilter = createFilterButton("Status", VaadinIcon.FILTER);
        statusFilter.addClickListener(e -> showStatusFilterDialog());

        filters.add(searchField, dateFilter, statusFilter);
        return filters;
    }

    private Button createFilterButton(String text, VaadinIcon icon) {
        Button btn = new Button(text, icon.create());
        btn.getStyle().set("background", BG_GRAY).set("color", TEXT_PRIMARY)
            .set("font-weight", "500").set("font-size", "14px")
            .set("border-radius", "12px").set("border", "none").set("padding", "10px 16px");
        return btn;
    }

    private void showDateFilterDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Filter by Date Range");
        VerticalLayout content = new VerticalLayout();

        DatePicker fromDate = new DatePicker("From");
        DatePicker toDate = new DatePicker("To");
        if (dateFrom != null) fromDate.setValue(dateFrom);
        if (dateTo != null) toDate.setValue(dateTo);
        content.add(fromDate, toDate);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttons.setWidthFull();

        Button clearBtn = new Button("Clear", e -> { dateFrom = null; dateTo = null; applyFilters(); dialog.close(); });
        clearBtn.getStyle().set("color", TEXT_SECONDARY);

        Button applyBtn = new Button("Apply", e -> { dateFrom = fromDate.getValue(); dateTo = toDate.getValue(); applyFilters(); dialog.close(); });
        applyBtn.getStyle().set("background", PRIMARY).set("color", "white");

        buttons.add(clearBtn, applyBtn);
        content.add(buttons);
        dialog.add(content);
        dialog.open();
    }

    private void showStatusFilterDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Filter by Status");
        VerticalLayout content = new VerticalLayout();

        Select<String> statusSelect = new Select<>();
        statusSelect.setLabel("Status");
        statusSelect.setItems("ALL", "SENT", "FINALIZED", "ARCHIVED");
        statusSelect.setValue(currentStatusFilter);
        statusSelect.setWidthFull();
        content.add(statusSelect);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttons.setWidthFull();

        Button clearBtn = new Button("Clear", e -> { currentStatusFilter = "ALL"; applyFilters(); dialog.close(); });
        clearBtn.getStyle().set("color", TEXT_SECONDARY);

        Button applyBtn = new Button("Apply", e -> { currentStatusFilter = statusSelect.getValue(); applyFilters(); dialog.close(); });
        applyBtn.getStyle().set("background", PRIMARY).set("color", "white");

        buttons.add(clearBtn, applyBtn);
        content.add(buttons);
        dialog.add(content);
        dialog.open();
    }

    private void applyFilters() {
        String searchText = searchField.getValue() != null ? searchField.getValue().toLowerCase().trim() : "";

        List<HistoryItem> filtered = allItems.stream().filter(item -> {
            if (!searchText.isEmpty()) {
                boolean matches = item.title.toLowerCase().contains(searchText)
                    || item.company.toLowerCase().contains(searchText);
                if (!matches) return false;
            }
            if (!"ALL".equals(currentStatusFilter) && !item.status.equals(currentStatusFilter)) return false;
            if (dateFrom != null && item.timestamp.toLocalDate().isBefore(dateFrom)) return false;
            if (dateTo != null && item.timestamp.toLocalDate().isAfter(dateTo)) return false;
            return true;
        }).collect(Collectors.toList());

        refreshCardsGrid(filtered);
    }

    // ── Cards grid ─────────────────────────────────────────────────────────────

    private void refreshCardsGrid(List<HistoryItem> items) {
        cardsGrid.removeAll();
        if (items.isEmpty()) {
            cardsGrid.add(createEmptyState());
        } else {
            for (HistoryItem item : items) {
                cardsGrid.add(createHistoryCard(item));
            }
        }
    }

    private VerticalLayout createEmptyState() {
        VerticalLayout emptyState = new VerticalLayout();
        emptyState.setWidthFull();
        emptyState.setAlignItems(FlexComponent.Alignment.CENTER);
        emptyState.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        emptyState.getStyle().set("padding", "60px 20px").set("gap", "16px");

        Icon emptyIcon = VaadinIcon.FILE_TEXT_O.create();
        emptyIcon.getStyle().set("color", TEXT_SECONDARY).set("width", "64px").set("height", "64px");

        H3 title = new H3("No cover letters yet");
        title.getStyle().set("font-size", "20px").set("font-weight", "600")
            .set("color", TEXT_PRIMARY).set("margin", "0");

        Paragraph description = new Paragraph("Generate your first cover letter to see it here.");
        description.getStyle().set("font-size", "14px").set("color", TEXT_SECONDARY).set("margin", "0");

        Button createBtn = new Button("Create Cover Letter", VaadinIcon.PLUS.create());
        createBtn.getStyle().set("background", PRIMARY).set("color", "white")
            .set("font-weight", "600").set("border-radius", "9999px")
            .set("padding", "12px 24px").set("border", "none").set("margin-top", "8px");
        createBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(GeneratorWizardView.class)));

        emptyState.add(emptyIcon, title, description, createBtn);
        return emptyState;
    }

    private Div createHistoryCard(HistoryItem item) {
        Div card = new Div();
        card.getStyle().set("width", "280px").set("background", BG_WHITE)
            .set("border", "1px solid rgba(0,0,0,0.05)").set("border-radius", "24px")
            .set("padding", "24px").set("cursor", "pointer").set("transition", "all 0.3s ease");

        // Header row: icon + status badge
        HorizontalLayout header = new HorizontalLayout();
        header.setAlignItems(FlexComponent.Alignment.START);
        header.setWidthFull();

        Div iconContainer = new Div();
        iconContainer.getStyle().set("width", "48px").set("height", "48px")
            .set("border-radius", "16px").set("background", BG_GRAY)
            .set("display", "flex").set("align-items", "center").set("justify-content", "center");
        Icon fileIcon = VaadinIcon.FILE_TEXT.create();
        fileIcon.getStyle().set("color", PRIMARY).set("width", "24px").set("height", "24px");
        iconContainer.add(fileIcon);

        Span statusBadge = createStatusBadge(item.status);

        header.add(iconContainer, statusBadge);
        header.expand(statusBadge);

        H3 title = new H3(item.title);
        title.getStyle().set("font-size", "16px").set("font-weight", "700")
            .set("color", TEXT_PRIMARY).set("margin", "16px 0 4px 0");

        Paragraph company = new Paragraph(item.company);
        company.getStyle().set("font-size", "14px").set("color", TEXT_SECONDARY)
            .set("margin", "0 0 16px 0");

        // Footer: date + action buttons
        HorizontalLayout footer = new HorizontalLayout();
        footer.setAlignItems(FlexComponent.Alignment.CENTER);
        footer.setWidthFull();
        footer.getStyle().set("padding-top", "16px").set("border-top", "1px solid rgba(0,0,0,0.05)");

        HorizontalLayout dateRow = new HorizontalLayout();
        dateRow.setAlignItems(FlexComponent.Alignment.CENTER);
        dateRow.getStyle().set("gap", "4px").set("color", TEXT_SECONDARY);
        Icon clock = VaadinIcon.CLOCK.create();
        clock.getStyle().set("width", "14px").set("height", "14px");
        Span dateText = new Span(item.date);
        dateText.getStyle().set("font-size", "12px");
        dateRow.add(clock, dateText);

        HorizontalLayout actions = new HorizontalLayout();
        actions.getStyle().set("gap", "4px");

        // View button
        Button viewBtn = createIconButton(VaadinIcon.EYE);
        viewBtn.getElement().setAttribute("title", "Preview");
        viewBtn.addClickListener(e -> openPreviewDialog(item));

        // Download button
        Button downloadBtn = createIconButton(VaadinIcon.DOWNLOAD);
        downloadBtn.getElement().setAttribute("title", "Download");
        downloadBtn.addClickListener(e -> downloadCoverLetter(item));

        // Edit button - navigates to editor
        Button editBtn = createIconButton(VaadinIcon.EDIT);
        editBtn.getElement().setAttribute("title", "Edit");
        editBtn.addClickListener(e -> {
            String encoded = java.net.URLEncoder.encode(item.filePath, java.nio.charset.StandardCharsets.UTF_8);
            getUI().ifPresent(ui -> ui.navigate("editor/" + encoded));
        });

        // Delete button
        Button deleteBtn = createIconButton(VaadinIcon.TRASH);
        deleteBtn.getElement().setAttribute("title", "Delete");
        deleteBtn.getStyle().set("color", "#FF3B30");
        deleteBtn.addClickListener(e -> confirmAndDelete(item, card));

        actions.add(viewBtn, downloadBtn, editBtn, deleteBtn);
        footer.add(dateRow, actions);
        footer.expand(dateRow);

        card.add(header, title, company, footer);

        card.getElement().addEventListener("mouseenter", e -> {
            card.getStyle().set("box-shadow", "0 20px 25px -5px rgba(0,0,0,0.1)")
                .set("border-color", "rgba(0,0,0,0.1)");
        });
        card.getElement().addEventListener("mouseleave", e -> {
            card.getStyle().set("box-shadow", "none")
                .set("border-color", "rgba(0,0,0,0.05)");
        });

        return card;
    }

    private void confirmAndDelete(HistoryItem item, Div card) {
        Dialog confirm = new Dialog();
        confirm.setHeaderTitle("Delete Cover Letter");

        VerticalLayout content = new VerticalLayout();
        content.add(new Paragraph("Are you sure you want to delete \"" + item.title + "\"? This cannot be undone."));

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttons.setWidthFull();

        Button cancelBtn = new Button("Cancel", e -> confirm.close());
        cancelBtn.getStyle().set("color", TEXT_SECONDARY);

        Button deleteBtn = new Button("Delete", e -> {
            File file = new File(item.filePath);
            if (file.exists() && file.delete()) {
                allItems.remove(item);
                applyFilters();
                Notification.show("Cover letter deleted", 2000, Notification.Position.TOP_CENTER);
            } else {
                Notification.show("Failed to delete file", 3000, Notification.Position.TOP_CENTER);
            }
            confirm.close();
        });
        deleteBtn.getStyle().set("background", "#FF3B30").set("color", "white");

        buttons.add(cancelBtn, deleteBtn);
        content.add(buttons);
        confirm.add(content);
        confirm.open();
    }

    // ── Card actions ───────────────────────────────────────────────────────────

    private void openPreviewDialog(HistoryItem item) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(item.title + " — " + item.company);
        dialog.setWidth("700px");
        dialog.setHeight("600px");

        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setSizeFull();
        wrapper.setPadding(false);
        wrapper.setSpacing(false);
        wrapper.getStyle().set("gap", "0");

        VerticalLayout content = new VerticalLayout();
        content.setWidthFull();
        content.setPadding(true);
        content.setSpacing(false);
        content.getStyle().set("overflow-y", "auto").set("flex", "1 1 auto");

        File file = new File(item.filePath);
        if (file.exists()) {
            String text = extractTextFromFile(file);
            Pre pre = new Pre(text);
            pre.getStyle()
                .set("white-space", "pre-wrap")
                .set("font-size", "13px")
                .set("line-height", "1.7")
                .set("margin", "0")
                .set("width", "100%");
            content.add(pre);
        } else {
            content.add(new Paragraph("File not found: " + item.filePath));
        }

        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        footer.setAlignItems(FlexComponent.Alignment.CENTER);
        footer.getStyle()
            .set("padding", "12px 16px")
            .set("border-top", "1px solid rgba(0,0,0,0.08)")
            .set("flex", "0 0 auto")
            .set("background", BG_WHITE);

        Button closeBtn = new Button("Close", e -> dialog.close());
        Button dlBtn = new Button("Download", VaadinIcon.DOWNLOAD.create(),
            e -> { downloadCoverLetter(item); dialog.close(); });
        dlBtn.getStyle().set("background", PRIMARY).set("color", "white");

        footer.add(closeBtn, dlBtn);
        wrapper.add(content, footer);
        wrapper.expand(content);

        dialog.add(wrapper);
        dialog.open();
    }

    /**
     * Extracts readable text from a file.
     * For .docx: parses word/document.xml inside the ZIP.
     * For .txt: reads as plain text.
     * For other binary: returns a fallback message.
     */
    private String extractTextFromFile(File file) {
        String name = file.getName().toLowerCase();
        try {
            if (name.endsWith(".docx") || name.endsWith(".doc")) {
                return extractTextFromDocx(file);
            } else if (name.endsWith(".txt")) {
                return new String(Files.readAllBytes(file.toPath()),
                    java.nio.charset.StandardCharsets.UTF_8);
            } else if (name.endsWith(".pdf")) {
                return "[PDF preview not supported – use the Download button to open it.]";
            } else {
                byte[] bytes = Files.readAllBytes(file.toPath());
                String text = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
                return isPrintable(text) ? text
                    : "[Binary file – use the Download button to open it.]";
            }
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Could not read file for preview: " + file.getName(), ex);
            return "[Could not read file: " + ex.getMessage() + "]";
        }
    }

    /**
     * Extracts plain text from a .docx file by reading word/document.xml
     * from the ZIP archive and stripping XML tags.
     */
    private String extractTextFromDocx(File file) throws IOException {
        try (java.util.zip.ZipFile zip = new java.util.zip.ZipFile(file)) {
            java.util.zip.ZipEntry entry = zip.getEntry("word/document.xml");
            if (entry == null) {
                return "[Could not find document content in DOCX file.]";
            }
            try (java.io.InputStream is = zip.getInputStream(entry)) {
                String xml = new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                // Extract text between <w:t> tags and preserve paragraph breaks
                StringBuilder sb = new StringBuilder();
                int i = 0;
                while (i < xml.length()) {
                    // New paragraph: <w:p[ >]
                    if (xml.startsWith("<w:p", i) && i + 4 < xml.length()
                            && (xml.charAt(i + 4) == ' ' || xml.charAt(i + 4) == '>'
                                || xml.charAt(i + 4) == '/')) {
                        if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '\n') {
                            sb.append('\n');
                        }
                    }
                    // Text run: <w:t ...>text</w:t>
                    if (xml.startsWith("<w:t", i)) {
                        int start = xml.indexOf('>', i);
                        if (start != -1) {
                            int end = xml.indexOf("</w:t>", start);
                            if (end != -1) {
                                sb.append(xml, start + 1, end);
                                i = end + 6;
                                continue;
                            }
                        }
                    }
                    i++;
                }
                String result = sb.toString().trim();
                return result.isEmpty() ? "[Document appears to be empty.]" : result;
            }
        }
    }

    private boolean isPrintable(String text) {
        for (int i = 0; i < Math.min(text.length(), 500); i++) {
            char c = text.charAt(i);
            if (c < 32 && c != '\n' && c != '\r' && c != '\t') return false;
        }
        return true;
    }

    private void downloadCoverLetter(HistoryItem item) {
        File file = new File(item.filePath);
        if (!file.exists()) {
            Notification.show("File not found: " + item.filePath, 3000, Notification.Position.TOP_CENTER);
            return;
        }
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            String mimeType = file.getName().endsWith(".pdf") ? "application/pdf"
                : "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            serveDownload(bytes, mimeType, file.getName());
        } catch (IOException ex) {
            Notification.show("Error reading file: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER);
            LOGGER.log(Level.SEVERE, "Download error", ex);
        }
    }

    private void exportAllFiles() {
        if (allItems.isEmpty()) {
            Notification.show("No files to export", 3000, Notification.Position.TOP_CENTER);
            return;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Set<String> added = new HashSet<>();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                for (HistoryItem item : allItems) {
                    File file = new File(item.filePath);
                    if (!file.exists() || added.contains(file.getName())) continue;
                    added.add(file.getName());
                    zos.putNextEntry(new ZipEntry(file.getName()));
                    zos.write(Files.readAllBytes(file.toPath()));
                    zos.closeEntry();
                }
            }
            String zipName = "cover_letters_export_" + System.currentTimeMillis() + ".zip";
            serveDownload(baos.toByteArray(), "application/zip", zipName);
            Notification.show("Exporting " + added.size() + " files…", 3000, Notification.Position.TOP_CENTER);
        } catch (Exception ex) {
            Notification.show("Export error: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER);
            LOGGER.log(Level.SEVERE, "Export all error", ex);
        }
    }

    /** Serves bytes as a browser download using a hidden anchor + JS click. */
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
    }

    // ── History loading ────────────────────────────────────────────────────────

    private List<HistoryItem> loadCoverLetterHistory() {
        List<HistoryItem> items = new ArrayList<>();
        try {
            AuthenticationService authService = new AuthenticationService();
            com.clbooster.app.backend.service.profile.User currentUser = authService.getCurrentUser();
            if (currentUser == null) {
                LOGGER.warning("No user logged in");
                return items;
            }
            int userPin = currentUser.getPin();

            // Only load from coverletters directory
            Path dir = Paths.get("uploads", "coverletters");
            if (!Files.exists(dir) || !Files.isDirectory(dir)) return items;

            File[] files = dir.toFile().listFiles((d, name) -> {
                String lower = name.toLowerCase();
                return lower.endsWith(".txt") || lower.endsWith(".docx") || lower.endsWith(".pdf");
            });
            if (files == null) return items;
            for (File file : files) {
                HistoryItem item = parseFilename(file.getName(), file.getAbsolutePath(), file.lastModified());
                if (item != null && item.pin == userPin) items.add(item);
            }
            items.sort((a, b) -> b.timestamp.compareTo(a.timestamp));
            LOGGER.info("Loaded " + items.size() + " cover letters for PIN: " + userPin);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading history: " + e.getMessage(), e);
        }
        return items;
    }

    private HistoryItem parseFilename(String filename, String filePath, long lastModified) {
        try {
            String baseName = filename;
            int dotIndex = baseName.lastIndexOf('.');
            if (dotIndex > 0) baseName = baseName.substring(0, dotIndex);

            String[] parts = baseName.split("_");
            if (parts.length < 2) return null;

            int pin;
            try {
                pin = Integer.parseInt(parts[0]);
            } catch (NumberFormatException e) {
                return null;
            }

            // Parse timestamp
            LocalDateTime timestamp;
            try {
                if (parts.length >= 3 && parts[1].length() == 8 && parts[2].length() == 6) {
                    timestamp = LocalDateTime.parse(parts[1] + "_" + parts[2],
                        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                } else if (parts[1].length() == 13) {
                    timestamp = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(Long.parseLong(parts[1])), ZoneId.systemDefault());
                } else {
                    timestamp = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(lastModified), ZoneId.systemDefault());
                }
            } catch (Exception e) {
                timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastModified), ZoneId.systemDefault());
            }

            String formattedDate = timestamp.format(DateTimeFormatter.ofPattern("MMM d, yyyy"));

            // Extract company and job title (parts after timestamp portion)
            int dataStart = (parts.length >= 3 && parts[1].length() == 8 && parts[2].length() == 6) ? 3 : 2;
            String company = "Unknown Company";
            String jobTitle = "Cover Letter";

            if (parts.length > dataStart) {
                int remaining = parts.length - dataStart;
                if (remaining >= 2) {
                    int split = dataStart + (remaining / 2);
                    company = String.join(" ", java.util.Arrays.copyOfRange(parts, dataStart, split));
                    jobTitle = String.join(" ", java.util.Arrays.copyOfRange(parts, split, parts.length));
                } else {
                    jobTitle = parts[dataStart];
                }
            }

            // Status based on age
            LocalDateTime now = LocalDateTime.now();
            String status = timestamp.plusDays(7).isAfter(now) ? "SENT"
                : timestamp.plusDays(30).isAfter(now) ? "FINALIZED" : "ARCHIVED";

            return new HistoryItem(jobTitle, company, formattedDate, status, pin, timestamp, filePath);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Parse error for: " + filename, e);
            return null;
        }
    }

    // ── UI helpers ─────────────────────────────────────────────────────────────

    private Span createStatusBadge(String status) {
        Span badge = new Span(status);
        badge.getStyle().set("font-size", "11px").set("font-weight", "700")
            .set("padding", "4px 10px").set("border-radius", "9999px")
            .set("text-transform", "uppercase").set("letter-spacing", "0.05em");
        switch (status) {
            case "SENT":
                badge.getStyle().set("background", "rgba(52,199,89,0.1)").set("color", SUCCESS);
                break;
            case "FINALIZED":
                badge.getStyle().set("background", "rgba(0,122,255,0.1)").set("color", PRIMARY);
                break;
            default:
                badge.getStyle().set("background", "rgba(0,0,0,0.05)").set("color", TEXT_SECONDARY);
        }
        return badge;
    }

    private Button createIconButton(VaadinIcon icon) {
        Button btn = new Button(icon.create());
        btn.getStyle().set("background", "transparent").set("color", TEXT_SECONDARY)
            .set("padding", "6px").set("border-radius", "8px").set("border", "none");
        btn.getElement().addEventListener("mouseenter",
            e -> btn.getStyle().set("background", "rgba(0,0,0,0.05)").set("color", TEXT_PRIMARY));
        btn.getElement().addEventListener("mouseleave",
            e -> btn.getStyle().set("background", "transparent").set("color", TEXT_SECONDARY));
        return btn;
    }

    private Button createLoadMoreButton() {
        Button btn = new Button("Load more generations");
        btn.getStyle().set("background", "transparent").set("color", TEXT_SECONDARY)
            .set("font-weight", "500").set("font-size", "14px")
            .set("border", "1px dashed rgba(0,0,0,0.15)").set("border-radius", "12px")
            .set("padding", "16px").set("width", "100%").set("margin-top", "8px");
        return btn;
    }

    // ── Data class ─────────────────────────────────────────────────────────────

    private static class HistoryItem implements Serializable {
        private static final long serialVersionUID = 1L;

        final String title;
        final String company;
        final String date;
        final String status;
        final int pin;
        final LocalDateTime timestamp;
        final String filePath;

        HistoryItem(String title, String company, String date, String status,
                    int pin, LocalDateTime timestamp, String filePath) {
            this.title = title;
            this.company = company;
            this.date = date;
            this.status = status;
            this.pin = pin;
            this.timestamp = timestamp;
            this.filePath = filePath;
        }
    }
}