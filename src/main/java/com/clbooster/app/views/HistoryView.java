package com.clbooster.app.views;

import com.clbooster.app.views.util.StyleConstants;

import jakarta.annotation.security.PermitAll;
import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.clbooster.app.i18n.TranslationService;
import com.clbooster.aiservice.Parser;
import com.vaadin.flow.server.VaadinSession;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Route(value = "history", layout = MainLayout.class)
@PageTitle("History | CL Booster")
@PermitAll
public class HistoryView extends VerticalLayout {
    private static final String BG_HOVER = "rgba(0,0,0,0.05)";
    private static final String ATTR_TITLE = "title";
    private static final String HISTORY_FILE_NOT_FOUND_KEY = "history.fileNotFound";

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
    private final TranslationService translationService;

    public HistoryView() {
        this.translationService = new TranslationService();
        setPadding(true);
        setSpacing(true);
        getStyle().set("gap", "32px").set(StyleConstants.CSS_PADDING, "32px")
                .set(StyleConstants.CSS_BACKGROUND, BG_WHITE)
                .set("font-family", "-apple-system, BlinkMacSystemFont, 'SF Pro Text', system-ui, sans-serif");
        setSizeFull();

        allItems = loadCoverLetterHistory();

        add(createHeader(), createFilters());

        cardsGrid = new Div();
        cardsGrid.getStyle().set(StyleConstants.CSS_DISPLAY, "flex").set(StyleConstants.CSS_FLEX_WRAP, "wrap")
                .set("gap", "24px").set(StyleConstants.CSS_WIDTH, "100%");

        refreshCardsGrid(allItems);
        add(cardsGrid);
        add(createLoadMoreButton());
    }

    // ── Header ─────────────────────────────────────────────────────────────────

    private HorizontalLayout createHeader() {
        VerticalLayout titleGroup = new VerticalLayout();
        titleGroup.setPadding(false);
        titleGroup.setSpacing(false);

        H2 title = new H2(translationService.translate("history.title"));
        title.getStyle().set(StyleConstants.CSS_FONT_SIZE, "30px").set(StyleConstants.CSS_FONT_WEIGHT, "700")
                .set(StyleConstants.CSS_COLOR, TEXT_PRIMARY).set(StyleConstants.CSS_MARGIN, "0");

        Paragraph subtitle = new Paragraph(translationService.translate("history.reviewRefine"));
        subtitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px").set(StyleConstants.CSS_COLOR, TEXT_SECONDARY)
                .set(StyleConstants.CSS_MARGIN, "0");
        titleGroup.add(title, subtitle);

        Button exportBtn = new Button(translationService.translate("history.exportAll"), VaadinIcon.DOWNLOAD.create());
        exportBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_HOVER).set(StyleConstants.CSS_COLOR, TEXT_PRIMARY)
                .set(StyleConstants.CSS_FONT_WEIGHT, "600")
                .set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX)
                .set(StyleConstants.CSS_PADDING, "10px 20px").set(StyleConstants.CSS_BORDER, "none");
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
        searchField.setPlaceholder(translationService.translate("history.searchByCompany"));
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setWidth("320px");
        searchField.getStyle().set("--vaadin-input-field-background", BG_GRAY).set("--vaadin-input-field-border-radius",
                "12px");
        searchField.addValueChangeListener(e -> applyFilters());

        Button dateFilter = createFilterButton(translationService.translate("history.dateRange"), VaadinIcon.CALENDAR);
        dateFilter.addClickListener(e -> showDateFilterDialog());

        Button statusFilter = createFilterButton(translationService.translate("history.status"), VaadinIcon.FILTER);
        statusFilter.addClickListener(e -> showStatusFilterDialog());

        filters.add(searchField, dateFilter, statusFilter);
        return filters;
    }

    private Button createFilterButton(String text, VaadinIcon icon) {
        Button btn = new Button(text, icon.create());
        btn.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_GRAY).set(StyleConstants.CSS_COLOR, TEXT_PRIMARY)
                .set(StyleConstants.CSS_FONT_WEIGHT, "500").set(StyleConstants.CSS_FONT_SIZE, "14px")
                .set(StyleConstants.CSS_BORDER_RADIUS, "12px").set(StyleConstants.CSS_BORDER, "none")
                .set(StyleConstants.CSS_PADDING, "10px 16px");
        return btn;
    }

    private void showDateFilterDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(translationService.translate("history.filterByDate"));
        VerticalLayout content = new VerticalLayout();

        DatePicker fromDate = new DatePicker(translationService.translate("history.from"));
        DatePicker toDate = new DatePicker(translationService.translate("history.to"));
        if (dateFrom != null)
            fromDate.setValue(dateFrom);
        if (dateTo != null)
            toDate.setValue(dateTo);
        content.add(fromDate, toDate);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttons.setWidthFull();

        Button clearBtn = new Button(translationService.translate("history.clear"), e -> {
            dateFrom = null;
            dateTo = null;
            applyFilters();
            dialog.close();
        });
        clearBtn.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);

        Button applyBtn = new Button(translationService.translate("history.apply"), e -> {
            dateFrom = fromDate.getValue();
            dateTo = toDate.getValue();
            applyFilters();
            dialog.close();
        });
        applyBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, PRIMARY).set(StyleConstants.CSS_COLOR,
                StyleConstants.VAL_WHITE);

        buttons.add(clearBtn, applyBtn);
        content.add(buttons);
        dialog.add(content);
        dialog.open();
    }

    private void showStatusFilterDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(translationService.translate("history.filterByStatus"));
        VerticalLayout content = new VerticalLayout();

        Select<String> statusSelect = new Select<>();
        statusSelect.setLabel(translationService.translate("history.status"));
        statusSelect.setItems("ALL", "SENT", StyleConstants.VAL_FINALIZED, "ARCHIVED");
        statusSelect.setValue(currentStatusFilter);
        statusSelect.setWidthFull();
        content.add(statusSelect);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttons.setWidthFull();

        Button clearBtn = new Button(translationService.translate("history.clear"), e -> {
            currentStatusFilter = "ALL";
            applyFilters();
            dialog.close();
        });
        clearBtn.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);

        Button applyBtn = new Button(translationService.translate("history.apply"), e -> {
            currentStatusFilter = statusSelect.getValue();
            applyFilters();
            dialog.close();
        });
        applyBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, PRIMARY).set(StyleConstants.CSS_COLOR,
                StyleConstants.VAL_WHITE);

        buttons.add(clearBtn, applyBtn);
        content.add(buttons);
        dialog.add(content);
        dialog.open();
    }

    private void applyFilters() {
        String searchText = searchField.getValue() != null ? searchField.getValue().toLowerCase().trim() : "";

        List<HistoryItem> filtered = allItems.stream().filter(item -> matchesSearch(item, searchText))
                .filter(this::matchesStatus).filter(this::matchesDateRange).toList();

        refreshCardsGrid(filtered);
    }

    private boolean matchesSearch(HistoryItem item, String searchText) {
        return searchText.isEmpty() || item.title.toLowerCase().contains(searchText)
                || item.company.toLowerCase().contains(searchText);
    }

    private boolean matchesStatus(HistoryItem item) {
        return "ALL".equals(currentStatusFilter) || item.status.equals(currentStatusFilter);
    }

    private boolean matchesDateRange(HistoryItem item) {
        LocalDate itemDate = item.timestamp.toLocalDate();
        if (dateFrom != null && itemDate.isBefore(dateFrom)) {
            return false;
        }
        return dateTo == null || !itemDate.isAfter(dateTo);
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
        emptyState.getStyle().set(StyleConstants.CSS_PADDING, "60px 20px").set("gap", "16px");

        Icon emptyIcon = VaadinIcon.FILE_TEXT_O.create();
        emptyIcon.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY).set(StyleConstants.CSS_WIDTH, "64px")
                .set(StyleConstants.CSS_HEIGHT, "64px");

        H3 title = new H3(translationService.translate("history.noCoverLetters"));
        title.getStyle().set(StyleConstants.CSS_FONT_SIZE, "20px").set(StyleConstants.CSS_FONT_WEIGHT, "600")
                .set(StyleConstants.CSS_COLOR, TEXT_PRIMARY).set(StyleConstants.CSS_MARGIN, "0");

        Paragraph description = new Paragraph(translationService.translate("history.generateFirst"));
        description.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px").set(StyleConstants.CSS_COLOR, TEXT_SECONDARY)
                .set(StyleConstants.CSS_MARGIN, "0");

        Button createBtn = new Button(translationService.translate("history.createCoverLetter"),
                VaadinIcon.PLUS.create());
        createBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, PRIMARY)
                .set(StyleConstants.CSS_COLOR, StyleConstants.VAL_WHITE).set(StyleConstants.CSS_FONT_WEIGHT, "600")
                .set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX)
                .set(StyleConstants.CSS_PADDING, "12px 24px").set(StyleConstants.CSS_BORDER, "none")
                .set(StyleConstants.CSS_MARGIN_TOP, "8px");
        createBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(GeneratorWizardView.class)));

        emptyState.add(emptyIcon, title, description, createBtn);
        return emptyState;
    }

    private Div createHistoryCard(HistoryItem item) {
        Div card = new Div();
        card.getStyle().set(StyleConstants.CSS_WIDTH, "280px").set(StyleConstants.CSS_BACKGROUND, BG_WHITE)
                .set(StyleConstants.CSS_BORDER, "1px solid rgba(0,0,0,0.05)")
                .set(StyleConstants.CSS_BORDER_RADIUS, "24px").set(StyleConstants.CSS_PADDING, "24px")
                .set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER)
                .set(StyleConstants.CSS_TRANSITION, "all 0.3s ease");

        // Header row: icon + status badge
        HorizontalLayout header = new HorizontalLayout();
        header.setAlignItems(FlexComponent.Alignment.START);
        header.setWidthFull();

        Div iconContainer = new Div();
        iconContainer.getStyle().set(StyleConstants.CSS_WIDTH, "48px").set(StyleConstants.CSS_HEIGHT, "48px")
                .set(StyleConstants.CSS_BORDER_RADIUS, "16px").set(StyleConstants.CSS_BACKGROUND, BG_GRAY)
                .set(StyleConstants.CSS_DISPLAY, "flex").set(StyleConstants.CSS_ALIGN_ITEMS, StyleConstants.VAL_CENTER)
                .set(StyleConstants.CSS_JUSTIFY_CONTENT, StyleConstants.VAL_CENTER);
        Icon fileIcon = VaadinIcon.FILE_TEXT.create();
        fileIcon.getStyle().set(StyleConstants.CSS_COLOR, PRIMARY).set(StyleConstants.CSS_WIDTH, "24px")
                .set(StyleConstants.CSS_HEIGHT, "24px");
        iconContainer.add(fileIcon);

        Span statusBadge = createStatusBadge(item.status);

        header.add(iconContainer, statusBadge);
        header.expand(statusBadge);

        H3 title = new H3(item.title);
        title.getStyle().set(StyleConstants.CSS_FONT_SIZE, "16px").set(StyleConstants.CSS_FONT_WEIGHT, "700")
                .set(StyleConstants.CSS_COLOR, TEXT_PRIMARY).set(StyleConstants.CSS_MARGIN, "16px 0 4px 0");

        Paragraph company = new Paragraph(item.company);
        company.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px").set(StyleConstants.CSS_COLOR, TEXT_SECONDARY)
                .set(StyleConstants.CSS_MARGIN, StyleConstants.VAL_0_0_16PX);

        // Footer: date + action buttons
        HorizontalLayout footer = new HorizontalLayout();
        footer.setAlignItems(FlexComponent.Alignment.CENTER);
        footer.setWidthFull();
        footer.getStyle().set("padding-top", "16px").set("border-top", "1px solid rgba(0,0,0,0.05)");

        HorizontalLayout dateRow = new HorizontalLayout();
        dateRow.setAlignItems(FlexComponent.Alignment.CENTER);
        dateRow.getStyle().set("gap", "4px").set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        Icon clock = VaadinIcon.CLOCK.create();
        clock.getStyle().set(StyleConstants.CSS_WIDTH, "14px").set(StyleConstants.CSS_HEIGHT, "14px");
        Span dateText = new Span(item.date);
        dateText.getStyle().set(StyleConstants.CSS_FONT_SIZE, "12px");
        dateRow.add(clock, dateText);

        HorizontalLayout actions = new HorizontalLayout();
        actions.getStyle().set("gap", "4px");

        // View button
        Button viewBtn = createIconButton(VaadinIcon.EYE);
        viewBtn.getElement().setAttribute(ATTR_TITLE, translationService.translate("history.preview"));
        viewBtn.addClickListener(e -> openPreviewDialog(item));

        // Download button
        Button downloadBtn = createIconButton(VaadinIcon.DOWNLOAD);
        downloadBtn.getElement().setAttribute(ATTR_TITLE, translationService.translate("history.download"));
        downloadBtn.addClickListener(e -> downloadCoverLetter(item));

        // Edit button - navigates to editor with item metadata in session
        Button editBtn = createIconButton(VaadinIcon.EDIT);
        editBtn.getElement().setAttribute(ATTR_TITLE, translationService.translate("history.edit"));
        editBtn.addClickListener(e -> {
            VaadinSession session = VaadinSession.getCurrent();
            session.setAttribute("gen.jobTitle", item.title);
            session.setAttribute("gen.company", item.company);
            session.setAttribute("gen.tone", "Professional");
            session.setAttribute("gen.skills", new java.util.HashSet<>());
            session.setAttribute("gen.jobDesc", "");
            // Load existing content so the editor skips re-generation
            File coverFile = new File(item.filePath);
            if (coverFile.exists()) {
                session.setAttribute("gen.existingContent", extractTextFromFile(coverFile));
            } else {
                session.setAttribute("gen.existingContent", null);
            }
            getUI().ifPresent(ui -> ui.navigate("editor"));
        });

        // Delete button
        Button deleteBtn = createIconButton(VaadinIcon.TRASH);
        deleteBtn.getElement().setAttribute(ATTR_TITLE, translationService.translate("history.delete"));
        deleteBtn.getStyle().set(StyleConstants.CSS_COLOR, "#FF3B30");
        deleteBtn.addClickListener(e -> confirmAndDelete(item, card));

        actions.add(viewBtn, downloadBtn, editBtn, deleteBtn);
        footer.add(dateRow, actions);
        footer.expand(dateRow);

        card.add(header, title, company, footer);

        card.getElement().addEventListener(StyleConstants.VAL_MOUSEENTER, e -> 
            card.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 20px 25px -5px rgba(0,0,0,0.1)")
                    .set(StyleConstants.CSS_BORDER_COLOR, "rgba(0,0,0,0.1)")
        );
        card.getElement().addEventListener(StyleConstants.VAL_MOUSELEAVE, e -> 
            card.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "none").set(StyleConstants.CSS_BORDER_COLOR, BG_HOVER)
        );

        return card;
    }

    private void confirmAndDelete(HistoryItem item, Div card) {
        Dialog confirm = new Dialog();
        confirm.setHeaderTitle(translationService.translate("history.deleteConfirm"));

        VerticalLayout content = new VerticalLayout();
        content.add(new Paragraph(translationService.translate("history.deleteConfirmText", item.title)));

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttons.setWidthFull();

        Button cancelBtn = new Button(translationService.translate("history.cancel"), e -> confirm.close());
        cancelBtn.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);

        Button deleteBtn = new Button(translationService.translate("history.delete"), e -> {
            File file = new File(item.filePath);
            if (file.exists() && file.delete()) {
                allItems.remove(item);
                applyFilters();
                Notification.show(translationService.translate("history.deleted"), 2000,
                        Notification.Position.TOP_CENTER);
            } else {
                Notification.show(translationService.translate("history.deleteFailed"), 3000,
                        Notification.Position.TOP_CENTER);
            }
            confirm.close();
        });
        deleteBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, "#FF3B30").set(StyleConstants.CSS_COLOR,
                StyleConstants.VAL_WHITE);

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
            pre.getStyle().set(StyleConstants.CSS_WHITE_SPACE, "pre-wrap").set(StyleConstants.CSS_FONT_SIZE, "13px")
                    .set("line-height", "1.7").set(StyleConstants.CSS_MARGIN, "0")
                    .set(StyleConstants.CSS_WIDTH, "100%");
            content.add(pre);
        } else {
            content.add(new Paragraph(translationService.translate(HISTORY_FILE_NOT_FOUND_KEY, item.filePath)));
        }

        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        footer.setAlignItems(FlexComponent.Alignment.CENTER);
        footer.getStyle().set(StyleConstants.CSS_PADDING, "12px 16px").set("border-top", "1px solid rgba(0,0,0,0.08)")
                .set("flex", "0 0 auto").set(StyleConstants.CSS_BACKGROUND, BG_WHITE);

        Button closeBtn = new Button(translationService.translate("history.cancel"), e -> dialog.close());
        Button dlBtn = new Button(translationService.translate("history.download"), VaadinIcon.DOWNLOAD.create(), e -> {
            downloadCoverLetter(item);
            dialog.close();
        });
        dlBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, PRIMARY).set(StyleConstants.CSS_COLOR,
                StyleConstants.VAL_WHITE);

        footer.add(closeBtn, dlBtn);
        wrapper.add(content, footer);
        wrapper.expand(content);

        dialog.add(wrapper);
        dialog.open();
    }

    /**
     * Extracts readable text from a file. For .docx: parses word/document.xml
     * inside the ZIP. For .txt: reads as plain text. For other binary: returns a
     * fallback message.
     */
    private String extractTextFromFile(File file) {
        String name = file.getName().toLowerCase();
        try {
            if (name.endsWith(".docx") || name.endsWith(".doc")) {
                return extractTextFromDocx(file);
            } else if (name.endsWith(".txt")) {
                return new String(Files.readAllBytes(file.toPath()), java.nio.charset.StandardCharsets.UTF_8);
            } else if (name.endsWith(".pdf")) {
                return "[PDF preview not supported – use the Download button to open it.]";
            } else {
                byte[] bytes = Files.readAllBytes(file.toPath());
                String text = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
                return isPrintable(text) ? text : "[Binary file – use the Download button to open it.]";
            }
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Could not read file for preview: " + file.getName(), ex);
            return "[Could not read file: " + ex.getMessage() + "]";
        }
    }

    /**
     * Extracts plain text from a .docx file.
     */
    private String extractTextFromDocx(File file) {
        Parser parser = new Parser();
        return parser.parseFileToJson(file.getAbsolutePath());
    }

    private boolean isPrintable(String text) {
        for (int i = 0; i < Math.min(text.length(), 500); i++) {
            char c = text.charAt(i);
            if (c < 32 && c != '\n' && c != '\r' && c != '\t')
                return false;
        }
        return true;
    }

    private void downloadCoverLetter(HistoryItem item) {
        File file = new File(item.filePath);
        if (!file.exists()) {
            Notification.show(translationService.translate(HISTORY_FILE_NOT_FOUND_KEY, item.filePath), 3000,
                    Notification.Position.TOP_CENTER);
            return;
        }
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            String mimeType = file.getName().endsWith(".pdf") ? "application/pdf"
                    : "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            serveDownload(bytes, mimeType, file.getName());
        } catch (IOException ex) {
            Notification.show(translationService.translate(HISTORY_FILE_NOT_FOUND_KEY, ex.getMessage()), 3000,
                    Notification.Position.TOP_CENTER);
            LOGGER.log(Level.SEVERE, "Download error", ex);
        }
    }

    private void exportAllFiles() {
        if (allItems.isEmpty()) {
            Notification.show(translationService.translate("history.noFilesToExport"), 3000,
                    Notification.Position.TOP_CENTER);
            return;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Set<String> added = new HashSet<>();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                for (HistoryItem item : allItems) {
                    File file = new File(item.filePath);
                    if (!file.exists() || added.contains(file.getName()))
                        continue;
                    added.add(file.getName());
                    zos.putNextEntry(new ZipEntry(file.getName()));
                    zos.write(Files.readAllBytes(file.toPath()));
                    zos.closeEntry();
                }
            }
            String zipName = "cover_letters_export_" + System.currentTimeMillis() + ".zip";
            serveDownload(baos.toByteArray(), "application/zip", zipName);
            Notification.show(translationService.translate("history.exporting", added.size()), 3000,
                    Notification.Position.TOP_CENTER);
        } catch (Exception ex) {
            Notification.show(translationService.translate("history.exportError", ex.getMessage()), 3000,
                    Notification.Position.TOP_CENTER);
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
            if (!Files.exists(dir) || !Files.isDirectory(dir))
                return items;

            File[] files = dir.toFile().listFiles((d, name) -> {
                String lower = name.toLowerCase();
                return lower.endsWith(".txt") || lower.endsWith(".docx") || lower.endsWith(".pdf");
            });
            if (files == null)
                return items;
            for (File file : files) {
                HistoryItem item = parseFilename(file.getName(), file.getAbsolutePath(), file.lastModified());
                if (item != null && item.pin == userPin)
                    items.add(item);
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
            if (dotIndex > 0)
                baseName = baseName.substring(0, dotIndex);

            String[] parts = baseName.split("_");
            if (parts.length < 2)
                return null;

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
                    timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(parts[1])),
                            ZoneId.systemDefault());
                } else {
                    timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastModified), ZoneId.systemDefault());
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
            String status = timestamp.plusDays(30).isAfter(now) ? StyleConstants.VAL_FINALIZED : "ARCHIVED";

            return new HistoryItem(jobTitle, company, formattedDate, status, pin, timestamp, filePath);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Parse error for: " + filename, e);
            return null;
        }
    }

    // ── UI helpers ─────────────────────────────────────────────────────────────

    private Span createStatusBadge(String status) {
        Span badge = new Span(status);
        badge.getStyle().set(StyleConstants.CSS_FONT_SIZE, "11px").set(StyleConstants.CSS_FONT_WEIGHT, "700")
                .set(StyleConstants.CSS_PADDING, "4px 10px")
                .set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX)
                .set(StyleConstants.CSS_TEXT_TRANSFORM, "uppercase").set(StyleConstants.CSS_LETTER_SPACING, "0.05em");
        switch (status) {
        case "SENT":
            badge.getStyle().set(StyleConstants.CSS_BACKGROUND, "rgba(52,199,89,0.1)").set(StyleConstants.CSS_COLOR,
                    SUCCESS);
            break;
        case StyleConstants.VAL_FINALIZED:
            badge.getStyle().set(StyleConstants.CSS_BACKGROUND, "rgba(0,122,255,0.1)").set(StyleConstants.CSS_COLOR,
                    PRIMARY);
            break;
        default:
            badge.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_HOVER).set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        }
        return badge;
    }

    private Button createIconButton(VaadinIcon icon) {
        Button btn = new Button(icon.create());
        btn.getStyle().set(StyleConstants.CSS_BACKGROUND, StyleConstants.VAL_TRANSPARENT)
                .set(StyleConstants.CSS_COLOR, TEXT_SECONDARY).set(StyleConstants.CSS_PADDING, "6px")
                .set(StyleConstants.CSS_BORDER_RADIUS, "8px").set(StyleConstants.CSS_BORDER, "none");
        btn.getElement().addEventListener(StyleConstants.VAL_MOUSEENTER, e -> btn.getStyle()
                .set(StyleConstants.CSS_BACKGROUND, BG_HOVER).set(StyleConstants.CSS_COLOR, TEXT_PRIMARY));
        btn.getElement().addEventListener(StyleConstants.VAL_MOUSELEAVE,
                e -> btn.getStyle().set(StyleConstants.CSS_BACKGROUND, StyleConstants.VAL_TRANSPARENT)
                        .set(StyleConstants.CSS_COLOR, TEXT_SECONDARY));
        return btn;
    }

    private Button createLoadMoreButton() {
        Button btn = new Button(translationService.translate("history.loadMore"));
        btn.getStyle().set(StyleConstants.CSS_BACKGROUND, StyleConstants.VAL_TRANSPARENT)
                .set(StyleConstants.CSS_COLOR, TEXT_SECONDARY).set(StyleConstants.CSS_FONT_WEIGHT, "500")
                .set(StyleConstants.CSS_FONT_SIZE, "14px").set(StyleConstants.CSS_BORDER, "1px dashed rgba(0,0,0,0.15)")
                .set(StyleConstants.CSS_BORDER_RADIUS, "12px").set(StyleConstants.CSS_PADDING, "16px")
                .set(StyleConstants.CSS_WIDTH, "100%").set(StyleConstants.CSS_MARGIN_TOP, "8px");
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

        HistoryItem(String title, String company, String date, String status, int pin, LocalDateTime timestamp,
                String filePath) {
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