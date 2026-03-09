package com.clbooster.app.views;

import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.clbooster.app.backend.service.profile.User;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | CL Booster")
public class DashboardView extends VerticalLayout {

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
    private List<LetterCardData> allLetters;

    private final AuthenticationService authService;
    private final User currentUser;

    public DashboardView() {
        this.authService = new AuthenticationService();
        this.currentUser = authService.getCurrentUser();

        setPadding(true);
        setSpacing(false);
        getStyle().set("gap", "28px").set("padding", "32px").set("background", BG_WHITE);
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
        H1 title = new H1("Welcome back, " + firstName + " 👋");
        title.getStyle().set("font-size", "28px").set("font-weight", "700").set("letter-spacing", "-0.02em")
                .set("color", TEXT_PRIMARY).set("margin", "0");

        int weekCount = countLettersThisWeek();
        String weekMsg = weekCount == 0 ? "No letters generated this week yet — let's change that."
                : weekCount + " cover letter" + (weekCount > 1 ? "s" : "") + " generated this week. Keep it up!";
        Paragraph sub = new Paragraph(weekMsg);
        sub.getStyle().set("font-size", "14px").set("color", TEXT_SECONDARY).set("margin", "0");

        text.add(title, sub);

        Button createBtn = new Button("New Cover Letter", VaadinIcon.PLUS.create());
        createBtn.getStyle().set("background", PRIMARY).set("color", "white").set("font-weight", "600")
                .set("border-radius", "9999px").set("padding", "12px 24px")
                .set("box-shadow", "0 10px 15px -3px rgba(0,122,255,0.3)");
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
        row.getStyle().set("gap", "20px").set("flex-wrap", "wrap");

        row.add(createStatTile("Cover Letters", String.valueOf(totalLetters), "All time generated",
                VaadinIcon.FILE_TEXT, PRIMARY, "rgba(0,122,255,0.08)"),
                createStatTile("Resumes Uploaded", String.valueOf(totalResumes), "Stored on account", VaadinIcon.UPLOAD,
                        "#5856D6", "rgba(88,86,214,0.08)"),
                createStatTile("This Week", String.valueOf(thisWeek), "Letters generated", VaadinIcon.CALENDAR, GREEN,
                        "rgba(52,199,89,0.08)"),
                createStatTile("Tones Available", "3", "Professional · Creative · Storyteller", VaadinIcon.MAGIC,
                        ORANGE, "rgba(255,149,0,0.08)"));

        return row;
    }

    private Div createStatTile(String label, String value, String subtext, VaadinIcon icon, String accentColor,
            String bgColor) {
        Div tile = new Div();
        tile.getStyle().set("flex", "1").set("min-width", "180px").set("background", BG_WHITE)
                .set("border", "1px solid rgba(0,0,0,0.07)").set("border-radius", "20px").set("padding", "24px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.04)");

        // Icon badge
        Div iconBadge = new Div();
        iconBadge.getStyle().set("width", "44px").set("height", "44px").set("border-radius", "14px")
                .set("background", bgColor).set("display", "flex").set("align-items", "center")
                .set("justify-content", "center").set("margin-bottom", "16px");
        Icon ic = icon.create();
        ic.getStyle().set("color", accentColor).set("width", "22px").set("height", "22px");
        iconBadge.add(ic);

        H2 val = new H2(value);
        val.getStyle().set("font-size", "32px").set("font-weight", "700").set("color", TEXT_PRIMARY)
                .set("margin", "0 0 4px 0").set("line-height", "1");

        Paragraph lbl = new Paragraph(label);
        lbl.getStyle().set("font-size", "13px").set("font-weight", "700").set("color", TEXT_PRIMARY).set("margin",
                "0 0 2px 0");

        Paragraph sub = new Paragraph(subtext);
        sub.getStyle().set("font-size", "11px").set("color", TEXT_SECONDARY).set("margin", "0");

        tile.add(iconBadge, val, lbl, sub);
        return tile;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Quick Actions bar
    // ─────────────────────────────────────────────────────────────────────────
    private Div createQuickActions() {
        Div card = new Div();
        card.getStyle().set("background", BG_GRAY).set("border-radius", "20px").set("padding", "20px 24px").set("width",
                "100%");

        Span heading = new Span("Quick Actions");
        heading.getStyle().set("font-size", "12px").set("font-weight", "700").set("text-transform", "uppercase")
                .set("letter-spacing", "0.08em").set("color", TEXT_SECONDARY).set("display", "block")
                .set("margin-bottom", "14px");

        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        actions.getStyle().set("gap", "12px").set("flex-wrap", "wrap");

        actions.add(
                createActionBtn("Generate Letter", VaadinIcon.MAGIC, PRIMARY, "white",
                        () -> getUI().ifPresent(ui -> ui.navigate(GeneratorWizardView.class))),
                createActionBtn("My History", VaadinIcon.CLOCK, "white", TEXT_PRIMARY,
                        () -> getUI().ifPresent(ui -> ui.navigate(HistoryView.class))),
                createActionBtn("Manage Resumes", VaadinIcon.FILE_SEARCH, "white", TEXT_PRIMARY,
                        () -> getUI().ifPresent(ui -> ui.navigate(ResumeManagerView.class))),
                createActionBtn("Profile Settings", VaadinIcon.USER, "white", TEXT_PRIMARY,
                        () -> getUI().ifPresent(ui -> ui.navigate(ProfileView.class))));

        card.add(heading, actions);
        return card;
    }

    private Button createActionBtn(String label, VaadinIcon icon, String bg, String fg, Runnable onClick) {
        Button btn = new Button(label, icon.create());
        btn.getStyle().set("background", bg.equals(PRIMARY) ? PRIMARY : "rgba(0,0,0,0.06)")
                .set("color", bg.equals(PRIMARY) ? "white" : TEXT_PRIMARY).set("font-weight", "600")
                .set("border-radius", "9999px").set("padding", "10px 20px").set("font-size", "13px")
                .set("border", "none");
        if (bg.equals(PRIMARY)) {
            btn.getStyle().set("box-shadow", "0 4px 12px rgba(0,122,255,0.25)");
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
        H2 sectionTitle = new H2("Recent Letters");
        sectionTitle.getStyle().set("font-size", "18px").set("font-weight", "700").set("color", TEXT_PRIMARY)
                .set("margin", "0");

        TextField searchField = new TextField();
        searchField.setPlaceholder("Search letters…");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.getStyle().set("max-width", "220px");
        searchField.addValueChangeListener(e -> filterLetters(e.getValue()));

        HorizontalLayout sectionHeader = new HorizontalLayout(sectionTitle, searchField);
        sectionHeader.setWidthFull();
        sectionHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        sectionHeader.expand(sectionTitle);

        // Letters grid — assign to class field, NOT a local variable
        lettersGrid = new HorizontalLayout();
        lettersGrid.setWidthFull();
        lettersGrid.getStyle().set("gap", "20px").set("flex-wrap", "wrap");

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
        card.getStyle().set("width", "260px").set("min-height", "180px").set("border", "2px dashed rgba(0,0,0,0.1)")
                .set("border-radius", "16px").set("padding", "28px").set("display", "flex")
                .set("flex-direction", "column").set("align-items", "center").set("justify-content", "center")
                .set("gap", "10px").set("cursor", "pointer").set("color", TEXT_SECONDARY);

        Div plus = new Div();
        plus.getStyle().set("width", "44px").set("height", "44px").set("border-radius", "50%")
                .set("background", "rgba(0,0,0,0.05)").set("display", "flex").set("align-items", "center")
                .set("justify-content", "center");
        plus.add(VaadinIcon.PLUS.create());

        Span txt = new Span("New Cover Letter");
        txt.getStyle().set("font-weight", "600").set("font-size", "14px");

        card.add(plus, txt);
        card.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(GeneratorWizardView.class)));
        card.getElement().addEventListener("mouseenter", e -> card.getStyle().set("background", "rgba(0,122,255,0.03)")
                .set("border-color", "rgba(0,122,255,0.3)").set("color", PRIMARY));
        card.getElement().addEventListener("mouseleave", e -> card.getStyle().set("background", "transparent")
                .set("border-color", "rgba(0,0,0,0.1)").set("color", TEXT_SECONDARY));
        return card;
    }

    private Div createLetterCard(String title, String company, String date, String status) {
        Div card = new Div();
        card.getStyle().set("width", "260px").set("background", BG_WHITE).set("border", "1px solid rgba(0,0,0,0.07)")
                .set("border-radius", "16px").set("padding", "20px").set("cursor", "pointer")
                .set("transition", "all 0.2s");

        // Header row: icon + status badge
        Div iconBox = new Div();
        iconBox.getStyle().set("width", "44px").set("height", "44px").set("border-radius", "14px")
                .set("background", BG_GRAY).set("display", "flex").set("align-items", "center")
                .set("justify-content", "center");
        Icon fileIc = VaadinIcon.FILE_TEXT.create();
        fileIc.getStyle().set("color", PRIMARY).set("width", "22px").set("height", "22px");
        iconBox.add(fileIc);

        Span badge = new Span(status);
        badge.getStyle().set("font-size", "10px").set("font-weight", "700").set("padding", "3px 8px")
                .set("border-radius", "9999px")
                .set("background", "FINALIZED".equals(status) ? "rgba(52,199,89,0.12)" : "rgba(142,142,147,0.12)")
                .set("color", "FINALIZED".equals(status) ? GREEN : TEXT_SECONDARY);

        HorizontalLayout top = new HorizontalLayout(iconBox, badge);
        top.setWidthFull();
        top.setAlignItems(FlexComponent.Alignment.CENTER);
        top.expand(iconBox);

        // Title + company
        H3 t = new H3(title);
        t.getStyle().set("font-size", "15px").set("font-weight", "700").set("color", TEXT_PRIMARY).set("margin",
                "14px 0 2px 0");

        Paragraph c = new Paragraph(company);
        c.getStyle().set("font-size", "13px").set("color", TEXT_SECONDARY).set("margin", "0 0 14px 0");

        // Footer: date + arrow
        Div divider = new Div();
        divider.getStyle().set("height", "1px").set("background", "rgba(0,0,0,0.06)").set("margin-bottom", "12px");

        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setAlignItems(FlexComponent.Alignment.CENTER);

        Icon clock = VaadinIcon.CLOCK.create();
        clock.getStyle().set("width", "13px").set("height", "13px").set("color", TEXT_SECONDARY);
        Span dateSpan = new Span(date);
        dateSpan.getStyle().set("font-size", "12px").set("color", TEXT_SECONDARY);

        HorizontalLayout dateRow = new HorizontalLayout(clock, dateSpan);
        dateRow.setAlignItems(FlexComponent.Alignment.CENTER);
        dateRow.getStyle().set("gap", "4px");

        Icon arrow = VaadinIcon.ARROW_RIGHT.create();
        arrow.getStyle().set("color", PRIMARY).set("width", "16px");

        footer.add(dateRow, arrow);
        footer.expand(dateRow);

        card.add(top, t, c, divider, footer);

        card.getElement().addEventListener("mouseenter", e -> card.getStyle()
                .set("box-shadow", "0 8px 24px rgba(0,0,0,0.08)").set("transform", "translateY(-2px)"));
        card.getElement().addEventListener("mouseleave",
                e -> card.getStyle().set("box-shadow", "none").set("transform", "none"));
        card.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(HistoryView.class)));

        return card;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Data helpers
    // ─────────────────────────────────────────────────────────────────────────

    /** Count files in a directory (returns 0 if it doesn't exist). */
    private int countFiles(String dir) {
        try {
            Path p = Paths.get(dir);
            if (!Files.exists(p))
                return 0;
            try (Stream<Path> s = Files.list(p)) {
                return (int) s.filter(Files::isRegularFile).count();
            }
        } catch (IOException e) {
            return 0;
        }
    }

    /** Count cover letters created in the last 7 days. */
    private int countLettersThisWeek() {
        try {
            Path p = Paths.get(COVER_LETTERS_DIR);
            if (!Files.exists(p))
                return 0;
            Instant weekAgo = Instant.now().minus(7, ChronoUnit.DAYS);
            try (Stream<Path> s = Files.list(p)) {
                return (int) s.filter(Files::isRegularFile).filter(f -> {
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
     * format: {pin}_{yyyyMMdd}_{HHmmss}_{company}_{jobtitle}.docx
     */
    private List<LetterCardData> loadLetterData() {
        List<LetterCardData> letters = new ArrayList<>();
        try {
            Path dir = Paths.get(COVER_LETTERS_DIR);
            if (Files.exists(dir)) {
                try (Stream<Path> stream = Files.list(dir).filter(Files::isRegularFile).sorted((a, b) -> {
                    try {
                        return Files.getLastModifiedTime(b).compareTo(Files.getLastModifiedTime(a));
                    } catch (IOException e) {
                        return 0;
                    }
                }).limit(6)) {
                    stream.forEach(file -> {
                        String name = file.getFileName().toString().replaceAll("\\.[^.]+$", ""); // strip extension
                        String[] parts = name.split("_");
                        // parts[0]=pin, parts[1]=date, parts[2]=time,
                        // parts[3]=company(first word), parts[4..]=jobtitle words
                        String company = parts.length > 3 ? parts[3].replace("-", " ") : "Company";
                        String jobTitle = parts.length > 4 ? String
                                .join(" ", java.util.Arrays.copyOfRange(parts, 4, parts.length)).replace("-", " ")
                                : "Cover Letter";
                        letters.add(new LetterCardData(toTitleCase(jobTitle), toTitleCase(company),
                                getRelativeDate(file), "FINALIZED"));
                    });
                }
            }
        } catch (IOException e) {
            // fall through
        }
        if (letters.isEmpty()) {
            letters.add(new LetterCardData("Senior Product Designer", "Apple", "2 hours ago", "FINALIZED"));
            letters.add(new LetterCardData("React Developer", "Meta", "Yesterday", "ARCHIVED"));
            letters.add(new LetterCardData("UX Engineer", "Airbnb", "3 days ago", "ARCHIVED"));
        }
        return letters;
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
                return "Just now";
            if (hours < 24)
                return hours + "h ago";
            long days = hours / 24;
            if (days == 1)
                return "Yesterday";
            if (days < 7)
                return days + " days ago";
            if (days < 30)
                return (days / 7) + " weeks ago";
            return (days / 30) + " months ago";
        } catch (IOException e) {
            return "Recently";
        }
    }

    private void filterLetters(String query) {
        // Remove all letter cards (everything except the "new" card at the end)
        List<com.vaadin.flow.component.Component> toRemove = lettersGrid.getChildren()
                .filter(c -> c instanceof Div && !((Div) c).getChildren().anyMatch(
                        child -> child instanceof Span && "New Cover Letter".equals(((Span) child).getText())))
                .collect(Collectors.toList());
        toRemove.forEach(lettersGrid::remove);

        List<LetterCardData> source = (query == null
                || query.isBlank())
                        ? allLetters
                        : allLetters.stream()
                                .filter(l -> l.title.toLowerCase().contains(query.toLowerCase())
                                        || l.company.toLowerCase().contains(query.toLowerCase()))
                                .collect(Collectors.toList());

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
