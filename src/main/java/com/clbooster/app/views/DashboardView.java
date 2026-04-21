package com.clbooster.app.views;

import com.clbooster.app.views.util.StyleConstants;

import jakarta.annotation.security.PermitAll;
import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.clbooster.app.backend.service.profile.User;
import com.clbooster.app.i18n.TranslationService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | CL Booster")
@PermitAll
class DashboardView extends VerticalLayout {
    private static final String FILE_EXTENSION_REGEX = "\\.[^.]+$";

    private static final String PRIMARY = "#007AFF";
    private static final String TEXT_PRIMARY = "#1d1d1f";
    private static final String TEXT_SECONDARY = "#86868b";
    private static final String BG_WHITE = "#ffffff";
    private static final String BG_GRAY = "#f5f5f7";
    private static final String GREEN = "#34C759";
    private static final String ORANGE = "#FF9500";

    private static final String COVER_LETTERS_DIR = "uploads/coverletters/";
    private static final String RESUMES_DIR = "uploads/resumes/";

    // Class-level field — must be assigned, not shadowed by a local variable
    private HorizontalLayout lettersGrid;
        private transient List<LetterCardData> allLetters;

        private final transient AuthenticationService authService;
    private final TranslationService translationService;
        private final transient User currentUser;

    public DashboardView() {
        this.authService = new AuthenticationService();
        this.translationService = new TranslationService();
        this.currentUser = authService.getCurrentUser();

        setPadding(true);
        setSpacing(false);
        getStyle().set("gap", "28px").set(StyleConstants.CSS_PADDING, "32px").set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        setSizeFull();

        add(createHeader());
        add(createStatsRow());
        add(createQuickActions());
        add(createRecentLettersSection());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Header
    // ─────────────────────────────────────────────────────────────────────────
    private HorizontalLayout createHeader() {
        VerticalLayout text = new VerticalLayout();
        text.setPadding(false);
        text.setSpacing(false);
        text.getStyle().set("gap", "4px");

        String firstName = currentUser != null ? currentUser.getFirstName() : "Guest";
        H1 title = new H1(translationService.translate("dashboard.welcome", firstName) + " 👋");
        title.getStyle().set(StyleConstants.CSS_FONT_SIZE, "28px").set(StyleConstants.CSS_FONT_WEIGHT, "700").set(StyleConstants.CSS_LETTER_SPACING, "-0.02em")
                .set(StyleConstants.CSS_COLOR, TEXT_PRIMARY).set(StyleConstants.CSS_MARGIN, "0");

        int weekCount = countLettersThisWeek();
        String weekMsg = weekCount == 0 ? translationService.translate("dashboard.noLettersYet")
                : weekCount + " " + translationService.translate("dashboard.lettersGenerated");
        Paragraph sub = new Paragraph(weekMsg);
        sub.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px").set(StyleConstants.CSS_COLOR, TEXT_SECONDARY).set(StyleConstants.CSS_MARGIN, "0");

        text.add(title, sub);

        Button createBtn = new Button(translationService.translate("dashboard.newCoverLetter"),
                VaadinIcon.PLUS.create());
        createBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, PRIMARY).set(StyleConstants.CSS_COLOR, StyleConstants.VAL_WHITE).set(StyleConstants.CSS_FONT_WEIGHT, "600")
                .set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX).set(StyleConstants.CSS_PADDING, "12px 24px")
                .set(StyleConstants.CSS_BOX_SHADOW, "0 10px 15px -3px rgba(0,122,255,0.3)");
        createBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(GeneratorWizardView.class)));

        HorizontalLayout header = new HorizontalLayout(text, createBtn);
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.expand(text);
        return header;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Stats row — 4 real-data tiles
    // ─────────────────────────────────────────────────────────────────────────
    private HorizontalLayout createStatsRow() {
        int totalLetters = countFiles(COVER_LETTERS_DIR);
        int totalResumes = countFiles(RESUMES_DIR);
        int thisWeek = countLettersThisWeek();

        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.getStyle().set("gap", "20px").set(StyleConstants.CSS_FLEX_WRAP, "wrap");

        row.add(createStatTile(translationService.translate("dashboard.coverLetters"), String.valueOf(totalLetters),
                translationService.translate("dashboard.allTimeGenerated"), VaadinIcon.FILE_TEXT, PRIMARY,
                "rgba(0,122,255,0.08)"),
                createStatTile(translationService.translate("dashboard.resumesUploaded"), String.valueOf(totalResumes),
                        translationService.translate("dashboard.storedOnAccount"), VaadinIcon.UPLOAD, "#5856D6",
                        "rgba(88,86,214,0.08)"),
                createStatTile(translationService.translate("dashboard.thisWeek"), String.valueOf(thisWeek),
                        translationService.translate("dashboard.lettersGenerated2"), VaadinIcon.CALENDAR, GREEN,
                        "rgba(52,199,89,0.08)"),
                createStatTile(translationService.translate("dashboard.tonesAvailable"), "3",
                        translationService.translate("dashboard.professional") + " · "
                                + translationService.translate("dashboard.creative") + " · "
                                + translationService.translate("dashboard.storyteller"),
                        VaadinIcon.MAGIC, ORANGE, "rgba(255,149,0,0.08)"));

        return row;
    }

    private Div createStatTile(String label, String value, String subtext, VaadinIcon icon, String accentColor,
            String bgColor) {
        Div tile = new Div();
        tile.getStyle().set("flex", "1").set(StyleConstants.CSS_MIN_WIDTH, "180px").set(StyleConstants.CSS_BACKGROUND, BG_WHITE)
                .set(StyleConstants.CSS_BORDER, "1px solid rgba(0,0,0,0.07)").set(StyleConstants.CSS_BORDER_RADIUS, "20px").set(StyleConstants.CSS_PADDING, "24px")
                .set(StyleConstants.CSS_BOX_SHADOW, "0 2px 8px rgba(0,0,0,0.04)");

        // Icon badge
        Div iconBadge = new Div();
        iconBadge.getStyle().set(StyleConstants.CSS_WIDTH, "44px").set(StyleConstants.CSS_HEIGHT, "44px").set(StyleConstants.CSS_BORDER_RADIUS, "14px")
                .set(StyleConstants.CSS_BACKGROUND, bgColor).set(StyleConstants.CSS_DISPLAY, "flex").set(StyleConstants.CSS_ALIGN_ITEMS, StyleConstants.VAL_CENTER)
                .set(StyleConstants.CSS_JUSTIFY_CONTENT, StyleConstants.VAL_CENTER).set(StyleConstants.CSS_MARGIN_BOTTOM, "16px");
        Icon ic = icon.create();
        ic.getStyle().set(StyleConstants.CSS_COLOR, accentColor).set(StyleConstants.CSS_WIDTH, "22px").set(StyleConstants.CSS_HEIGHT, "22px");
        iconBadge.add(ic);

        H2 val = new H2(value);
        val.getStyle().set(StyleConstants.CSS_FONT_SIZE, "32px").set(StyleConstants.CSS_FONT_WEIGHT, "700").set(StyleConstants.CSS_COLOR, TEXT_PRIMARY)
                .set(StyleConstants.CSS_MARGIN, "0 0 4px 0").set("line-height", "1");

        Paragraph lbl = new Paragraph(label);
        lbl.getStyle().set(StyleConstants.CSS_FONT_SIZE, "13px").set(StyleConstants.CSS_FONT_WEIGHT, "700").set(StyleConstants.CSS_COLOR, TEXT_PRIMARY).set(StyleConstants.CSS_MARGIN,
                "0 0 2px 0");

        Paragraph sub = new Paragraph(subtext);
        sub.getStyle().set(StyleConstants.CSS_FONT_SIZE, "11px").set(StyleConstants.CSS_COLOR, TEXT_SECONDARY).set(StyleConstants.CSS_MARGIN, "0");

        tile.add(iconBadge, val, lbl, sub);
        return tile;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Quick Actions bar
    // ─────────────────────────────────────────────────────────────────────────
    private Div createQuickActions() {
        Div card = new Div();
        card.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_GRAY).set(StyleConstants.CSS_BORDER_RADIUS, "20px").set(StyleConstants.CSS_PADDING, "20px 24px").set(StyleConstants.CSS_WIDTH,
                "100%");

        Span heading = new Span(translationService.translate("dashboard.quickActions"));
        heading.getStyle().set(StyleConstants.CSS_FONT_SIZE, "12px").set(StyleConstants.CSS_FONT_WEIGHT, "700").set(StyleConstants.CSS_TEXT_TRANSFORM, "uppercase")
                .set(StyleConstants.CSS_LETTER_SPACING, "0.08em").set(StyleConstants.CSS_COLOR, TEXT_SECONDARY).set(StyleConstants.CSS_DISPLAY, "block")
                .set(StyleConstants.CSS_MARGIN_BOTTOM, "14px");

        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        actions.getStyle().set("gap", "12px").set(StyleConstants.CSS_FLEX_WRAP, "wrap");

        actions.add(
                createActionBtn(translationService.translate("dashboard.generateLetter"), VaadinIcon.MAGIC, PRIMARY,
                        StyleConstants.VAL_WHITE, () -> getUI().ifPresent(ui -> ui.navigate(GeneratorWizardView.class))),
                createActionBtn(translationService.translate("dashboard.myHistory"), VaadinIcon.CLOCK, StyleConstants.VAL_WHITE,
                        TEXT_PRIMARY, () -> getUI().ifPresent(ui -> ui.navigate(HistoryView.class))),
                createActionBtn(translationService.translate("dashboard.manageResumes"), VaadinIcon.FILE_SEARCH,
                        StyleConstants.VAL_WHITE, TEXT_PRIMARY, () -> getUI().ifPresent(ui -> ui.navigate(ResumeManagerView.class))),
                createActionBtn(translationService.translate("dashboard.profileSettings"), VaadinIcon.USER, StyleConstants.VAL_WHITE,
                        TEXT_PRIMARY, () -> getUI().ifPresent(ui -> ui.navigate(ProfileView.class))));

        card.add(heading, actions);
        return card;
    }

    private Button createActionBtn(String label, VaadinIcon icon, String bg, String fg, Runnable onClick) {
        Button btn = new Button(label, icon.create());
        btn.getStyle().set(StyleConstants.CSS_BACKGROUND, bg.equals(PRIMARY) ? PRIMARY : "rgba(0,0,0,0.06)")
                .set(StyleConstants.CSS_COLOR, bg.equals(PRIMARY) ? StyleConstants.VAL_WHITE : TEXT_PRIMARY).set(StyleConstants.CSS_FONT_WEIGHT, "600")
                .set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX).set(StyleConstants.CSS_PADDING, "10px 20px").set(StyleConstants.CSS_FONT_SIZE, "13px")
                .set(StyleConstants.CSS_BORDER, "none");
        if (bg.equals(PRIMARY)) {
            btn.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 4px 12px rgba(0,122,255,0.25)");
        }
        btn.addClickListener(e -> onClick.run());
        return btn;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Recent Letters section — fixed field assignment + real filenames
    // ─────────────────────────────────────────────────────────────────────────
    private VerticalLayout createRecentLettersSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(false);
        section.getStyle().set("gap", "16px");
        section.setWidthFull();

        // Section header
        H2 sectionTitle = new H2(translationService.translate("dashboard.recentLetters"));
        sectionTitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "18px").set(StyleConstants.CSS_FONT_WEIGHT, "700").set(StyleConstants.CSS_COLOR, TEXT_PRIMARY)
                .set(StyleConstants.CSS_MARGIN, "0");

        TextField searchField = new TextField();
        searchField.setPlaceholder(translationService.translate("dashboard.searchLetters"));
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.getStyle().set(StyleConstants.CSS_MAX_WIDTH, "220px");
        searchField.addValueChangeListener(e -> filterLetters(e.getValue()));

        HorizontalLayout sectionHeader = new HorizontalLayout(sectionTitle, searchField);
        sectionHeader.setWidthFull();
        sectionHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        sectionHeader.expand(sectionTitle);

        // Letters grid — assign to class field, NOT a local variable
        lettersGrid = new HorizontalLayout();
        lettersGrid.setWidthFull();
        lettersGrid.getStyle().set("gap", "20px").set(StyleConstants.CSS_FLEX_WRAP, "wrap");

        allLetters = loadLetterData();

        // "Create new" card
        Div createCard = createNewLetterCard();
        lettersGrid.add(createCard);

        // Render letters before the create card
        for (LetterCardData letter : allLetters) {
            lettersGrid.addComponentAsFirst(createLetterCard(letter.title, letter.company, letter.date, letter.status));
        }

        section.add(sectionHeader, lettersGrid);
        return section;
    }

    private Div createNewLetterCard() {
        Div card = new Div();
        card.getStyle().set(StyleConstants.CSS_WIDTH, "260px").set(StyleConstants.CSS_MIN_HEIGHT, "180px").set(StyleConstants.CSS_BORDER, "2px dashed rgba(0,0,0,0.1)")
                .set(StyleConstants.CSS_BORDER_RADIUS, "16px").set(StyleConstants.CSS_PADDING, "28px").set(StyleConstants.CSS_DISPLAY, "flex")
                .set(StyleConstants.CSS_FLEX_DIRECTION, "column").set(StyleConstants.CSS_ALIGN_ITEMS, StyleConstants.VAL_CENTER).set(StyleConstants.CSS_JUSTIFY_CONTENT, StyleConstants.VAL_CENTER)
                .set("gap", "10px").set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER).set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);

        Div plus = new Div();
        plus.getStyle().set(StyleConstants.CSS_WIDTH, "44px").set(StyleConstants.CSS_HEIGHT, "44px").set(StyleConstants.CSS_BORDER_RADIUS, "50%")
                .set(StyleConstants.CSS_BACKGROUND, "rgba(0,0,0,0.05)").set(StyleConstants.CSS_DISPLAY, "flex").set(StyleConstants.CSS_ALIGN_ITEMS, StyleConstants.VAL_CENTER)
                .set(StyleConstants.CSS_JUSTIFY_CONTENT, StyleConstants.VAL_CENTER);
        plus.add(VaadinIcon.PLUS.create());

        Span txt = new Span(translationService.translate("dashboard.newCoverLetter"));
        txt.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "600").set(StyleConstants.CSS_FONT_SIZE, "14px");

        card.add(plus, txt);
        card.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(GeneratorWizardView.class)));
        card.getElement().addEventListener(StyleConstants.VAL_MOUSEENTER, e -> card.getStyle().set(StyleConstants.CSS_BACKGROUND, "rgba(0,122,255,0.03)")
                .set(StyleConstants.CSS_BORDER_COLOR, "rgba(0,122,255,0.3)").set(StyleConstants.CSS_COLOR, PRIMARY));
        card.getElement().addEventListener(StyleConstants.VAL_MOUSELEAVE, e -> card.getStyle().set(StyleConstants.CSS_BACKGROUND, StyleConstants.VAL_TRANSPARENT)
                .set(StyleConstants.CSS_BORDER_COLOR, "rgba(0,0,0,0.1)").set(StyleConstants.CSS_COLOR, TEXT_SECONDARY));
        return card;
    }

    private Div createLetterCard(String title, String company, String date, String status) {
        Div card = new Div();
        card.getStyle().set(StyleConstants.CSS_WIDTH, "260px").set(StyleConstants.CSS_BACKGROUND, BG_WHITE).set(StyleConstants.CSS_BORDER, "1px solid rgba(0,0,0,0.07)")
                .set(StyleConstants.CSS_BORDER_RADIUS, "16px").set(StyleConstants.CSS_PADDING, "20px").set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER)
                .set(StyleConstants.CSS_TRANSITION, StyleConstants.VAL_ALL_0_2S);

        // Header row: icon + status badge
        Div iconBox = new Div();
        iconBox.getStyle().set(StyleConstants.CSS_WIDTH, "44px").set(StyleConstants.CSS_HEIGHT, "44px").set(StyleConstants.CSS_BORDER_RADIUS, "14px")
                .set(StyleConstants.CSS_BACKGROUND, BG_GRAY).set(StyleConstants.CSS_DISPLAY, "flex").set(StyleConstants.CSS_ALIGN_ITEMS, StyleConstants.VAL_CENTER)
                .set(StyleConstants.CSS_JUSTIFY_CONTENT, StyleConstants.VAL_CENTER);
        Icon fileIc = VaadinIcon.FILE_TEXT.create();
        fileIc.getStyle().set(StyleConstants.CSS_COLOR, PRIMARY).set(StyleConstants.CSS_WIDTH, "22px").set(StyleConstants.CSS_HEIGHT, "22px");
        iconBox.add(fileIc);

        Span badge = new Span(translationService.translate("dashboard.status." + status.toLowerCase()));
        badge.getStyle().set(StyleConstants.CSS_FONT_SIZE, "10px").set(StyleConstants.CSS_FONT_WEIGHT, "700").set(StyleConstants.CSS_PADDING, "3px 8px")
                .set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX)
                .set(StyleConstants.CSS_BACKGROUND, StyleConstants.VAL_FINALIZED.equals(status) ? "rgba(52,199,89,0.12)" : "rgba(142,142,147,0.12)")
                .set(StyleConstants.CSS_COLOR, StyleConstants.VAL_FINALIZED.equals(status) ? GREEN : TEXT_SECONDARY);

        HorizontalLayout top = new HorizontalLayout(iconBox, badge);
        top.setWidthFull();
        top.setAlignItems(FlexComponent.Alignment.CENTER);
        top.expand(iconBox);

        // Title + company
        H3 t = new H3(title);
        t.getStyle().set(StyleConstants.CSS_FONT_SIZE, "15px").set(StyleConstants.CSS_FONT_WEIGHT, "700").set(StyleConstants.CSS_COLOR, TEXT_PRIMARY).set(StyleConstants.CSS_MARGIN,
                "14px 0 2px 0");

        Paragraph c = new Paragraph(company);
        c.getStyle().set(StyleConstants.CSS_FONT_SIZE, "13px").set(StyleConstants.CSS_COLOR, TEXT_SECONDARY).set(StyleConstants.CSS_MARGIN, "0 0 14px 0");

        // Footer: date + arrow
        Div divider = new Div();
        divider.getStyle().set(StyleConstants.CSS_HEIGHT, "1px").set(StyleConstants.CSS_BACKGROUND, "rgba(0,0,0,0.06)").set(StyleConstants.CSS_MARGIN_BOTTOM, "12px");

        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setAlignItems(FlexComponent.Alignment.CENTER);

        Icon clock = VaadinIcon.CLOCK.create();
        clock.getStyle().set(StyleConstants.CSS_WIDTH, "13px").set(StyleConstants.CSS_HEIGHT, "13px").set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        Span dateSpan = new Span(date);
        dateSpan.getStyle().set(StyleConstants.CSS_FONT_SIZE, "12px").set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);

        HorizontalLayout dateRow = new HorizontalLayout(clock, dateSpan);
        dateRow.setAlignItems(FlexComponent.Alignment.CENTER);
        dateRow.getStyle().set("gap", "4px");

        Icon arrow = VaadinIcon.ARROW_RIGHT.create();
        arrow.getStyle().set(StyleConstants.CSS_COLOR, PRIMARY).set(StyleConstants.CSS_WIDTH, "16px");

        footer.add(dateRow, arrow);
        footer.expand(dateRow);

        card.add(top, t, c, divider, footer);

        card.getElement().addEventListener(StyleConstants.VAL_MOUSEENTER, e -> card.getStyle()
                .set(StyleConstants.CSS_BOX_SHADOW, "0 8px 24px rgba(0,0,0,0.08)").set(StyleConstants.CSS_TRANSFORM, "translateY(-2px)"));
        card.getElement().addEventListener(StyleConstants.VAL_MOUSELEAVE,
                e -> card.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "none").set(StyleConstants.CSS_TRANSFORM, "none"));
        card.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(HistoryView.class)));

        return card;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Data helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Count files in a directory for the current user (returns 0 if it doesn't
     * exist or no user logged in).
     */
    private int countFiles(String dir) {
        int userPin = currentUser != null ? currentUser.getPin() : -1;
        if (userPin == -1) {
            return 0;
        }
        try {
            Path p = Paths.get(dir);
            if (!Files.exists(p))
                return 0;
            try (Stream<Path> s = Files.list(p)) {
                return (int) s.filter(Files::isRegularFile).filter(f -> {
                    String name = f.getFileName().toString();
                    String[] parts = name.split("_");
                    if (parts.length < 1)
                        return false;
                    try {
                        int filePin = Integer.parseInt(parts[0].replaceAll(FILE_EXTENSION_REGEX, ""));
                        return filePin == userPin;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }).count();
            }
        } catch (IOException e) {
            return 0;
        }
    }

    /** Count cover letters created in the last 7 days for current user. */
    private int countLettersThisWeek() {
        int userPin = currentUser != null ? currentUser.getPin() : -1;
        if (userPin == -1) {
            return 0;
        }
        try {
            Path p = Paths.get(COVER_LETTERS_DIR);
            if (!Files.exists(p))
                return 0;
            Instant weekAgo = Instant.now().minus(7, ChronoUnit.DAYS);
            try (Stream<Path> s = Files.list(p)) {
                return (int) s.filter(Files::isRegularFile).filter(f -> {
                    // Filter by user PIN
                    String name = f.getFileName().toString();
                    String[] parts = name.split("_");
                    if (parts.length < 1)
                        return false;
                    try {
                        int filePin = Integer.parseInt(parts[0].replaceAll(FILE_EXTENSION_REGEX, ""));
                        if (filePin != userPin)
                            return false;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                    // Filter by date (last 7 days)
                    try {
                        return Files.getLastModifiedTime(f).toInstant().isAfter(weekAgo);
                    } catch (IOException ex) {
                        return false;
                    }
                }).count();
            }
        } catch (IOException e) {
            return 0;
        }
    }

    /**
     * Load up to 6 most recent cover letters from uploads/coverletters/. Filename
     * format: {pin}_{yyyyMMdd}_{HHmmss}_{company}_{jobtitle}.docx NOW FIXED: Only
     * loads files belonging to the current user.
     */
    private List<LetterCardData> loadLetterData() {
        List<LetterCardData> letters = new ArrayList<>();
        int userPin = currentUser != null ? currentUser.getPin() : -1;
        if (userPin == -1) return letters;

        Path dir = Paths.get(COVER_LETTERS_DIR);
        if (Files.exists(dir)) {
            try (Stream<Path> stream = Files.list(dir)) {
                stream.filter(Files::isRegularFile)
                      .sorted(this::compareByLastModifiedDesc)
                      .limit(20)
                      .forEach(file -> processLetterFile(file, userPin, letters));
            } catch (IOException e) {
                // Document access failed silently
            }
        }

        if (letters.isEmpty()) {
            letters.add(new LetterCardData("Senior Product Designer", "Apple", "2 hours ago", StyleConstants.VAL_FINALIZED));
            letters.add(new LetterCardData("React Developer", "Meta", "Yesterday", "ARCHIVED"));
            letters.add(new LetterCardData("UX Engineer", "Airbnb", "3 days ago", "ARCHIVED"));
        }
        
        return letters.stream().limit(6).toList();
    }

    private int compareByLastModifiedDesc(Path a, Path b) {
        try {
            return Files.getLastModifiedTime(b).compareTo(Files.getLastModifiedTime(a));
        } catch (IOException e) {
            return 0;
        }
    }

    private void processLetterFile(Path file, int userPin, List<LetterCardData> letters) {
        String name = file.getFileName().toString().replaceAll(FILE_EXTENSION_REGEX, "");
        String[] parts = name.split("_");
        if (parts.length < 1 || !isUserFile(parts[0], userPin)) return;

        String company = parts.length > 3 ? parts[3].replace("-", " ") : "Company";
        String jobTitle = "Cover Letter";
        if (parts.length > 4) {
            String[] titleParts = java.util.Arrays.copyOfRange(parts, 4, parts.length);
            jobTitle = String.join(" ", titleParts).replace("-", " ");
        }

        letters.add(new LetterCardData(toTitleCase(jobTitle), toTitleCase(company),
                getRelativeDate(file), StyleConstants.VAL_FINALIZED));
    }

    private boolean isUserFile(String pinPart, int userPin) {
        try {
            return Integer.parseInt(pinPart) == userPin;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String toTitleCase(String s) {
        if (s == null || s.isBlank())
            return s;
        String[] words = s.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) {
                sb.append(Character.toUpperCase(w.charAt(0)));
                sb.append(w.substring(1).toLowerCase());
                sb.append(' ');
            }
        }
        return sb.toString().trim();
    }

    private String getRelativeDate(Path file) {
        try {
            long diff = System.currentTimeMillis() - Files.getLastModifiedTime(file).toMillis();
            long hours = diff / 3_600_000L;
            if (hours < 1)
                return translationService.translate("dashboard.justNow");
            if (hours < 24)
                return hours + translationService.translate("dashboard.hoursAgo");
            long days = hours / 24;
            if (days == 1)
                return translationService.translate("dashboard.yesterday");
            if (days < 7)
                return days + translationService.translate("dashboard.daysAgo");
            if (days < 30)
                return (days / 7) + translationService.translate("dashboard.weeksAgo");
            return (days / 30) + translationService.translate("dashboard.monthsAgo");
        } catch (IOException e) {
            return translationService.translate("dashboard.recently");
        }
    }

    private void filterLetters(String query) {
        // Remove all letter cards (everything except the "new" card at the end)
        List<com.vaadin.flow.component.Component> toRemove = lettersGrid.getChildren()
                .filter(c -> c instanceof Div && !((Div) c).getChildren().anyMatch(
                        child -> child instanceof Span && "New Cover Letter".equals(((Span) child).getText())))
                .toList();
        toRemove.forEach(lettersGrid::remove);

        List<LetterCardData> source = (query == null
                || query.isBlank())
                ? allLetters
                : allLetters.stream()
                        .filter(l -> l.title.toLowerCase().contains(query.toLowerCase())
                                || l.company.toLowerCase().contains(query.toLowerCase()))
                        .toList();

        // Insert letter cards before the "new" card
        source.forEach(l -> lettersGrid.addComponentAtIndex(Math.max(0, (int) lettersGrid.getChildren().count() - 1),
                createLetterCard(l.title, l.company, l.date, l.status)));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Data model
    // ─────────────────────────────────────────────────────────────────────────
    private static class LetterCardData {
        final String title, company, date, status;

        LetterCardData(String title, String company, String date, String status) {
            this.title = title;
            this.company = company;
            this.date = date;
            this.status = status;
        }
    }
}
