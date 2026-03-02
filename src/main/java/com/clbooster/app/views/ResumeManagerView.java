package com.clbooster.app.views;

import com.vaadin.flow.component.button.Button;
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
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route(value = "resume", layout = MainLayout.class)
@PageTitle("Resume Manager | CL Booster")
public class ResumeManagerView extends VerticalLayout {

    // Figma Design System Colors
    private static final String PRIMARY = "#007AFF";
    private static final String PRIMARY_LIGHT = "#5AC8FA";
    private static final String TEXT_PRIMARY = "#1d1d1f";
    private static final String TEXT_SECONDARY = "#86868b";
    private static final String BG_WHITE = "#ffffff";
    private static final String BG_GRAY = "#f5f5f7";
    private static final String SUCCESS = "#34C759";
    private static final String WARNING = "#FF9500";
    private static final String ERROR = "#FF3B30";

    // Sample data for uploaded resumes
    private final List<ResumeData> resumes = new ArrayList<>();
    private VerticalLayout contentArea;
    private Div uploadZone;

    public ResumeManagerView() {
        // Initialize sample data
        resumes.add(new ResumeData("Alex_Rivera_Resume_2024.pdf", "PDF", "2.4 MB", "2 days ago", 94, true));
        resumes.add(new ResumeData("Alex_Rivera_CV_Tech.docx", "DOCX", "1.8 MB", "1 week ago", 87, false));

        setPadding(true);
        setSpacing(true);
        getStyle().set("gap", "32px");
        getStyle().set("padding", "32px");
        getStyle().set("background", BG_WHITE);
        getStyle().set("font-family", "-apple-system, BlinkMacSystemFont, 'SF Pro Text', 'SF Pro Display', system-ui, sans-serif");
        setSizeFull();

        buildUI();
    }

    private void buildUI() {
        // Header section
        HorizontalLayout header = createHeader();

        // Tabs navigation
        Tabs tabs = createTabs();

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

        add(header, tabs, mainContent);
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

        // Action buttons
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

        importBtn.getElement().addEventListener("mouseenter", e -> {
            importBtn.getStyle().set("background", "rgba(0,0,0,0.08)");
        });
        importBtn.getElement().addEventListener("mouseleave", e -> {
            importBtn.getStyle().set("background", BG_GRAY);
        });

        Button newBtn = createPrimaryButton("Upload New", () -> {
            Notification.show("Click on the upload zone to select a file", 3000, Notification.Position.BOTTOM_END);
        });
        newBtn.setIcon(VaadinIcon.UPLOAD.create());

        actions.add(importBtn, newBtn);

        header.add(titleGroup, actions);
        header.expand(titleGroup);

        return header;
    }

    private Tabs createTabs() {
        Tabs tabs = new Tabs();
        tabs.setWidthFull();
        tabs.getStyle().set("background", "transparent");

        Tab allResumes = new Tab("All Resumes");
        Tab aiOptimized = new Tab("AI Optimized");
        Tab starred = new Tab("Starred");

        // Style tabs
        for (Tab tab : new Tab[]{allResumes, aiOptimized, starred}) {
            tab.getStyle().set("font-weight", "600");
            tab.getStyle().set("font-size", "14px");
        }

        tabs.add(allResumes, aiOptimized, starred);
        tabs.setSelectedTab(allResumes);

        tabs.addSelectedChangeListener(event -> {
            Notification.show("Showing: " + event.getSelectedTab().getLabel(), 2000, Notification.Position.BOTTOM_CENTER);
        });

        return tabs;
    }

    private VerticalLayout createLeftPanel() {
        VerticalLayout panel = new VerticalLayout();
        panel.setPadding(false);
        panel.setSpacing(false);
        panel.getStyle().set("gap", "24px");

        // Upload zone
        uploadZone = createUploadZone();

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

        Span countBadge = new Span(String.valueOf(resumes.size()));
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

        Button sortBtn = new Button("Sort by: Recent", VaadinIcon.CHEVRON_DOWN.create());
        sortBtn.getStyle().set("background", "transparent");
        sortBtn.getStyle().set("color", TEXT_SECONDARY);
        sortBtn.getStyle().set("font-weight", "500");
        sortBtn.getStyle().set("border", "none");
        sortBtn.getStyle().set("padding", "8px 12px");

        listHeader.add(titleWithBadge, sortBtn);
        listHeader.expand(titleWithBadge);

        // Resume list
        VerticalLayout resumeList = new VerticalLayout();
        resumeList.setPadding(false);
        resumeList.setSpacing(false);
        resumeList.getStyle().set("gap", "16px");

        for (ResumeData resume : resumes) {
            resumeList.add(createResumeCard(resume));
        }

        panel.add(uploadZone, listHeader, resumeList);

        return panel;
    }

    private Div createUploadZone() {
        Div zone = new Div();
        zone.getStyle().set("width", "90%");
        zone.getStyle().set("background", "linear-gradient(135deg, rgba(0,122,255,0.05) 0%, rgba(90,200,250,0.05) 100%)");
        zone.getStyle().set("border", "2px dashed " + PRIMARY + "40");
        zone.getStyle().set("border-radius", "24px");
        zone.getStyle().set("padding", "48px");
        zone.getStyle().set("cursor", "pointer");
        zone.getStyle().set("transition", "all 0.3s");
        zone.getStyle().set("text-align", "center");

        // Upload icon container
        Div iconContainer = new Div();
        iconContainer.getStyle().set("width", "72px");
        iconContainer.getStyle().set("height", "72px");
        iconContainer.getStyle().set("background", "rgba(0,122,255,0.1)");
        iconContainer.getStyle().set("border-radius", "50%");
        iconContainer.getStyle().set("display", "flex");
        iconContainer.getStyle().set("align-items", "center");
        iconContainer.getStyle().set("justify-content", "center");
        iconContainer.getStyle().set("margin", "0 auto 24px");
        iconContainer.getStyle().set("transition", "all 0.3s");

        Icon uploadIcon = VaadinIcon.UPLOAD_ALT.create();
        uploadIcon.getStyle().set("color", PRIMARY);
        uploadIcon.getStyle().set("width", "32px");
        uploadIcon.getStyle().set("height", "32px");
        iconContainer.add(uploadIcon);

        // Title
        H3 title = new H3("Drop your resume here");
        title.getStyle().set("font-size", "20px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0 0 8px 0");

        // Subtitle
        Paragraph subtitle = new Paragraph("or click to browse files (PDF, DOCX, up to 10MB)");
        subtitle.getStyle().set("font-size", "14px");
        subtitle.getStyle().set("color", TEXT_SECONDARY);
        subtitle.getStyle().set("margin", "0 0 24px 0");

        // Format badges
        HorizontalLayout formats = new HorizontalLayout();
        formats.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        formats.getStyle().set("gap", "8px");

        formats.add(createFormatBadge("PDF"));
        formats.add(createFormatBadge("DOCX"));
        formats.add(createFormatBadge("TXT"));

        zone.add(iconContainer, title, subtitle, formats);

        // Hover effects
        zone.getElement().addEventListener("mouseenter", e -> {
            zone.getStyle().set("border-color", PRIMARY);
            zone.getStyle().set("background", "linear-gradient(135deg, rgba(0,122,255,0.08) 0%, rgba(90,200,250,0.08) 100%)");
            iconContainer.getStyle().set("transform", "scale(1.1)");
            iconContainer.getStyle().set("background", "rgba(0,122,255,0.15)");
        });

        zone.getElement().addEventListener("mouseleave", e -> {
            zone.getStyle().set("border-color", PRIMARY + "40");
            zone.getStyle().set("background", "linear-gradient(135deg, rgba(0,122,255,0.05) 0%, rgba(90,200,250,0.05) 100%)");
            iconContainer.getStyle().set("transform", "scale(1)");
            iconContainer.getStyle().set("background", "rgba(0,122,255,0.1)");
        });

        // Click handler
        zone.getElement().addEventListener("click", e -> {
            // In a real app, this would trigger file input
            Notification.show("File picker would open here", 2000, Notification.Position.BOTTOM_CENTER);
        });

        return zone;
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

        // AI Score
        VerticalLayout scoreGroup = new VerticalLayout();
        scoreGroup.setPadding(false);
        scoreGroup.setSpacing(false);
        scoreGroup.getStyle().set("gap", "6px");
        scoreGroup.setWidth("120px");
        scoreGroup.setDefaultHorizontalComponentAlignment(Alignment.END);

        HorizontalLayout scoreRow = new HorizontalLayout();
        scoreRow.setAlignItems(FlexComponent.Alignment.CENTER);
        scoreRow.getStyle().set("gap", "6px");
        scoreRow.getStyle().set("justify-content", "flex-end");

        Icon aiIcon = VaadinIcon.SPARK_LINE.create();
        aiIcon.getStyle().set("color", getScoreColor(resume.score));
        aiIcon.getStyle().set("width", "16px");
        aiIcon.getStyle().set("height", "16px");

        Span scoreText = new Span(resume.score + "%");
        scoreText.getStyle().set("font-size", "20px");
        scoreText.getStyle().set("font-weight", "700");
        scoreText.getStyle().set("color", getScoreColor(resume.score));

        scoreRow.add(aiIcon, scoreText);

        Span scoreLabel = new Span("AI Score");
        scoreLabel.getStyle().set("font-size", "11px");
        scoreLabel.getStyle().set("font-weight", "600");
        scoreLabel.getStyle().set("color", TEXT_SECONDARY);
        scoreLabel.getStyle().set("text-transform", "uppercase");
        scoreLabel.getStyle().set("letter-spacing", "0.05em");

        scoreGroup.add(scoreRow, scoreLabel);

        // Actions menu
        Button menuBtn = new Button(VaadinIcon.ELLIPSIS_DOTS_H.create());
        menuBtn.getStyle().set("background", "transparent");
        menuBtn.getStyle().set("color", TEXT_SECONDARY);
        menuBtn.getStyle().set("border", "none");
        menuBtn.getStyle().set("cursor", "pointer");

        content.add(fileIcon, fileInfo, scoreGroup, menuBtn);
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

    private String getFileColor(String format) {
        return switch (format.toUpperCase()) {
            case "PDF" -> ERROR;
            case "DOCX" -> PRIMARY;
            case "TXT" -> TEXT_SECONDARY;
            default -> PRIMARY;
        };
    }

    private String getScoreColor(int score) {
        if (score >= 90) return SUCCESS;
        if (score >= 70) return WARNING;
        return ERROR;
    }

    private Div createScoreBreakdown(ResumeData resume) {
        Div card = new Div();
        card.getStyle().set("background", BG_GRAY);
        card.getStyle().set("border-radius", "16px");
        card.getStyle().set("padding", "24px");

        H3 title = new H3("Score Breakdown");
        title.getStyle().set("font-size", "14px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0 0 20px 0");
        title.getStyle().set("text-transform", "uppercase");
        title.getStyle().set("letter-spacing", "0.05em");

        VerticalLayout bars = new VerticalLayout();
        bars.setPadding(false);
        bars.setSpacing(false);
        bars.getStyle().set("gap", "16px");

        bars.add(createScoreBar("Content Quality", 95, SUCCESS));
        bars.add(createScoreBar("ATS Compatibility", 88, SUCCESS));
        bars.add(createScoreBar("Keyword Match", 82, WARNING));
        bars.add(createScoreBar("Formatting", 90, SUCCESS));

        // Total score
        HorizontalLayout totalRow = new HorizontalLayout();
        totalRow.setWidthFull();
        totalRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        totalRow.setAlignItems(FlexComponent.Alignment.CENTER);
        totalRow.getStyle().set("margin-top", "20px");
        totalRow.getStyle().set("padding-top", "20px");
        totalRow.getStyle().set("border-top", "1px solid rgba(0,0,0,0.05)");

        Span totalLabel = new Span("Overall Score");
        totalLabel.getStyle().set("font-size", "14px");
        totalLabel.getStyle().set("font-weight", "600");
        totalLabel.getStyle().set("color", TEXT_PRIMARY);

        Span totalScore = new Span(resume.score + "%");
        totalScore.getStyle().set("font-size", "24px");
        totalScore.getStyle().set("font-weight", "700");
        totalScore.getStyle().set("color", getScoreColor(resume.score));

        totalRow.add(totalLabel, totalScore);

        card.add(title, bars, totalRow);

        return card;
    }

    private VerticalLayout createScoreBar(String label, int score, String color) {
        VerticalLayout barGroup = new VerticalLayout();
        barGroup.setPadding(false);
        barGroup.setSpacing(false);
        barGroup.getStyle().set("gap", "8px");
        barGroup.setWidthFull();

        HorizontalLayout labelRow = new HorizontalLayout();
        labelRow.setWidthFull();
        labelRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        Span labelSpan = new Span(label);
        labelSpan.getStyle().set("font-size", "13px");
        labelSpan.getStyle().set("font-weight", "500");
        labelSpan.getStyle().set("color", TEXT_PRIMARY);

        Span scoreSpan = new Span(score + "%");
        scoreSpan.getStyle().set("font-size", "13px");
        scoreSpan.getStyle().set("font-weight", "600");
        scoreSpan.getStyle().set("color", color);

        labelRow.add(labelSpan, scoreSpan);

        // Progress bar background
        Div barBg = new Div();
        barBg.getStyle().set("width", "100%");
        barBg.getStyle().set("height", "6px");
        barBg.getStyle().set("background", "rgba(0,0,0,0.05)");
        barBg.getStyle().set("border-radius", "3px");
        barBg.getStyle().set("overflow", "hidden");

        // Progress bar fill
        Div barFill = new Div();
        barFill.getStyle().set("width", score + "%");
        barFill.getStyle().set("height", "100%");
        barFill.getStyle().set("background", color);
        barFill.getStyle().set("border-radius", "3px");
        barFill.getStyle().set("transition", "width 0.5s ease");

        barBg.add(barFill);
        barGroup.add(labelRow, barBg);

        return barGroup;
    }

    private Div createStrengthsSection() {
        Div section = new Div();

        H3 title = new H3("Key Strengths");
        title.getStyle().set("font-size", "14px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0 0 16px 0");
        title.getStyle().set("text-transform", "uppercase");
        title.getStyle().set("letter-spacing", "0.05em");

        VerticalLayout items = new VerticalLayout();
        items.setPadding(false);
        items.setSpacing(false);
        items.getStyle().set("gap", "12px");

        items.add(createHighlightItem("Strong action verbs", "Your resume uses impactful language", SUCCESS, VaadinIcon.CHECK_CIRCLE));
        items.add(createHighlightItem("Clear structure", "Well-organized sections", SUCCESS, VaadinIcon.CHECK_CIRCLE));
        items.add(createHighlightItem("Relevant experience", "10+ years highlighted effectively", SUCCESS, VaadinIcon.CHECK_CIRCLE));

        section.add(title, items);

        return section;
    }

    private Div createSuggestionsSection() {
        Div section = new Div();

        H3 title = new H3("AI Suggestions");
        title.getStyle().set("font-size", "14px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0 0 16px 0");
        title.getStyle().set("text-transform", "uppercase");
        title.getStyle().set("letter-spacing", "0.05em");

        VerticalLayout items = new VerticalLayout();
        items.setPadding(false);
        items.setSpacing(false);
        items.getStyle().set("gap", "12px");

        items.add(createHighlightItem("Add more metrics", "Include quantifiable achievements", WARNING, VaadinIcon.LIGHTBULB));
        items.add(createHighlightItem("Update skills section", "Add trending technologies", WARNING, VaadinIcon.LIGHTBULB));

        section.add(title, items);

        return section;
    }

    private Div createKeywordsSection() {
        Div section = new Div();

        H3 title = new H3("Missing Keywords");
        title.getStyle().set("font-size", "14px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0 0 16px 0");
        title.getStyle().set("text-transform", "uppercase");
        title.getStyle().set("letter-spacing", "0.05em");

        HorizontalLayout keywords = new HorizontalLayout();
        keywords.getStyle().set("gap", "8px");
        keywords.getStyle().set("flex-wrap", "wrap");

        String[] missingKeywords = {"Agile", "Scrum", "React", "TypeScript", "AWS"};
        for (String keyword : missingKeywords) {
            keywords.add(createKeywordChip(keyword));
        }

        section.add(title, keywords);

        return section;
    }

    private HorizontalLayout createHighlightItem(String title, String description, String color, VaadinIcon iconType) {
        HorizontalLayout item = new HorizontalLayout();
        item.setWidthFull();
        item.setAlignItems(FlexComponent.Alignment.START);
        item.getStyle().set("gap", "12px");
        item.getStyle().set("padding", "12px");
        item.getStyle().set("background", color + "08");
        item.getStyle().set("border-radius", "12px");
        item.getStyle().set("border-left", "3px solid " + color);

        Div iconContainer = new Div();
        iconContainer.getStyle().set("width", "24px");
        iconContainer.getStyle().set("height", "24px");
        iconContainer.getStyle().set("border-radius", "50%");
        iconContainer.getStyle().set("background", color + "20");
        iconContainer.getStyle().set("display", "flex");
        iconContainer.getStyle().set("align-items", "center");
        iconContainer.getStyle().set("justify-content", "center");
        iconContainer.getStyle().set("flex-shrink", "0");

        Icon icon = iconType.create();
        icon.getStyle().set("color", color);
        icon.getStyle().set("width", "14px");
        icon.getStyle().set("height", "14px");
        iconContainer.add(icon);

        VerticalLayout text = new VerticalLayout();
        text.setPadding(false);
        text.setSpacing(false);
        text.getStyle().set("gap", "2px");

        Span titleSpan = new Span(title);
        titleSpan.getStyle().set("font-size", "14px");
        titleSpan.getStyle().set("font-weight", "600");
        titleSpan.getStyle().set("color", TEXT_PRIMARY);

        Span descSpan = new Span(description);
        descSpan.getStyle().set("font-size", "12px");
        descSpan.getStyle().set("color", TEXT_SECONDARY);

        text.add(titleSpan, descSpan);

        item.add(iconContainer, text);

        return item;
    }

    private Span createKeywordChip(String keyword) {
        Span chip = new Span(keyword);
        chip.getStyle().set("font-size", "12px");
        chip.getStyle().set("font-weight", "600");
        chip.getStyle().set("padding", "6px 12px");
        chip.getStyle().set("background", BG_GRAY);
        chip.getStyle().set("color", TEXT_SECONDARY);
        chip.getStyle().set("border-radius", "9999px");
        return chip;
    }

    private Button createPrimaryButton(String text, Runnable action) {
        Button btn = new Button(text, e -> action.run());
        btn.getStyle().set("background", "linear-gradient(135deg, " + PRIMARY + " 0%, " + PRIMARY_LIGHT + " 100%)");
        btn.getStyle().set("color", "white");
        btn.getStyle().set("font-weight", "600");
        btn.getStyle().set("font-size", "14px");
        btn.getStyle().set("border-radius", "9999px");
        btn.getStyle().set("border", "none");
        btn.getStyle().set("padding", "10px 24px");
        btn.getStyle().set("box-shadow", "0 10px 15px -3px rgba(0,122,255,0.3)");
        btn.getStyle().set("transition", "all 0.2s");
        btn.getStyle().set("cursor", "pointer");

        btn.getElement().addEventListener("mouseenter", e -> {
            btn.getStyle().set("filter", "brightness(1.1)");
            btn.getStyle().set("transform", "translateY(-1px)");
        });
        btn.getElement().addEventListener("mouseleave", e -> {
            btn.getStyle().set("filter", "brightness(1)");
            btn.getStyle().set("transform", "translateY(0)");
        });

        return btn;
    }

    // Inner class to represent resume data
    private static class ResumeData {
        String name;
        String format;
        String size;
        String date;
        int score;
        boolean starred;

        ResumeData(String name, String format, String size, String date, int score, boolean starred) {
            this.name = name;
            this.format = format;
            this.size = size;
            this.date = date;
            this.score = score;
            this.starred = starred;
        }
    }
}
