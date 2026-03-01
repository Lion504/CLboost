package com.clbooster.app.views;

import com.clbooster.aiservice.AIService;
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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

/**
 * Alternative Generator View with live preview panel
 * Real-time preview as user fills in details
 * Following Apple Design System
 */
@Route(value = "generator-alt", layout = MainLayout.class)
@PageTitle("Generator | CL Booster")
public class GeneratorView extends HorizontalLayout {

    // Figma Design System Colors
    private static final String PRIMARY = "#007AFF";
    private static final String TEXT_PRIMARY = "#1d1d1f";
    private static final String TEXT_SECONDARY = "#86868b";
    private static final String BG_WHITE = "#ffffff";
    private static final String BG_GRAY = "#f5f5f7";

    private final AIService aiService;

    // Form fields
    private TextField jobTitleField;
    private TextField companyNameField;
    private TextArea jobDescriptionField;
    private TextArea resumeSummaryField;

    // Preview components
    private Div previewContent;
    private Span previewCompany;
    private Span previewRole;
    private Paragraph previewBody;

    public GeneratorView(@org.springframework.context.annotation.Lazy AIService aiService) {
        this.aiService = aiService;

        setSizeFull();
        setPadding(true);
        getStyle().set("gap", "0");
        getStyle().set("background", BG_WHITE);

        // Left panel - Input form
        VerticalLayout inputPanel = createInputPanel();
        inputPanel.setWidth("45%");
        inputPanel.getStyle().set("padding", "32px");

        // Right panel - Live preview
        VerticalLayout previewPanel = createPreviewPanel();
        previewPanel.setWidth("55%");
        previewPanel.getStyle().set("background", BG_GRAY);
        previewPanel.getStyle().set("padding", "32px");

        add(inputPanel, previewPanel);
    }

    private VerticalLayout createInputPanel() {
        VerticalLayout panel = new VerticalLayout();
        panel.setPadding(false);
        panel.setSpacing(false);
        panel.getStyle().set("gap", "24px");
        panel.setHeightFull();

        // Header
        HorizontalLayout header = createPanelHeader();

        // Form
        VerticalLayout form = createInputForm();

        // Action buttons
        HorizontalLayout actions = createActionButtons();

        panel.add(header, form, actions);
        panel.expand(form);

        return panel;
    }

    private HorizontalLayout createPanelHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        Button backBtn = new Button(VaadinIcon.ARROW_LEFT.create());
        backBtn.getStyle().set("background", "transparent");
        backBtn.getStyle().set("color", TEXT_SECONDARY);
        backBtn.getStyle().set("border", "none");
        backBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(DashboardView.class)));

        H1 title = new H1("Create Cover Letter");
        title.getStyle().set("font-size", "24px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0");

        header.add(backBtn, title);
        header.expand(title);

        return header;
    }

    private VerticalLayout createInputForm() {
        VerticalLayout form = new VerticalLayout();
        form.setPadding(false);
        form.setSpacing(false);
        form.getStyle().set("gap", "20px");
        form.getStyle().set("overflow", "auto");

        // Job Details Section
        H2 section1 = new H2("Job Details");
        section1.getStyle().set("font-size", "14px");
        section1.getStyle().set("font-weight", "700");
        section1.getStyle().set("color", TEXT_SECONDARY);
        section1.getStyle().set("text-transform", "uppercase");
        section1.getStyle().set("letter-spacing", "0.1em");
        section1.getStyle().set("margin", "8px 0");

        // Job Title
        VerticalLayout jobTitleGroup = createFormField("Job Title *", VaadinIcon.BRIEFCASE);
        jobTitleField = new TextField();
        jobTitleField.setPlaceholder("e.g., Senior Product Designer");
        jobTitleField.setWidthFull();
        jobTitleField.getStyle().set("background", BG_GRAY);
        jobTitleField.getStyle().set("border", "1px solid rgba(0,0,0,0.1)");
        jobTitleField.getStyle().set("border-radius", "12px");
        jobTitleField.addValueChangeListener(e -> updatePreview());
        jobTitleGroup.add(jobTitleField);

        // Company Name
        VerticalLayout companyGroup = createFormField("Company Name *", VaadinIcon.BUILDING);
        companyNameField = new TextField();
        companyNameField.setPlaceholder("e.g., Apple Inc.");
        companyNameField.setWidthFull();
        companyNameField.getStyle().set("background", BG_GRAY);
        companyNameField.getStyle().set("border", "1px solid rgba(0,0,0,0.1)");
        companyNameField.getStyle().set("border-radius", "12px");
        companyNameField.addValueChangeListener(e -> updatePreview());
        companyGroup.add(companyNameField);

        // Job Description
        VerticalLayout descGroup = createFormField("Job Description *", VaadinIcon.FILE_TEXT);
        jobDescriptionField = new TextArea();
        jobDescriptionField.setPlaceholder("Paste the full job description here...");
        jobDescriptionField.setWidthFull();
        jobDescriptionField.setMinHeight("120px");
        jobDescriptionField.getStyle().set("background", BG_GRAY);
        jobDescriptionField.getStyle().set("border", "1px solid rgba(0,0,0,0.1)");
        jobDescriptionField.getStyle().set("border-radius", "12px");
        descGroup.add(jobDescriptionField);

        // Your Experience Section
        H2 section2 = new H2("Your Experience");
        section2.getStyle().set("font-size", "14px");
        section2.getStyle().set("font-weight", "700");
        section2.getStyle().set("color", TEXT_SECONDARY);
        section2.getStyle().set("text-transform", "uppercase");
        section2.getStyle().set("letter-spacing", "0.1em");
        section2.getStyle().set("margin", "16px 0 8px 0");

        // Resume Summary
        VerticalLayout resumeGroup = createFormField("Professional Summary", VaadinIcon.USER);
        resumeSummaryField = new TextArea();
        resumeSummaryField.setPlaceholder("Briefly describe your relevant experience and key achievements...");
        resumeSummaryField.setWidthFull();
        resumeSummaryField.setMinHeight("100px");
        resumeSummaryField.getStyle().set("background", BG_GRAY);
        resumeSummaryField.getStyle().set("border", "1px solid rgba(0,0,0,0.1)");
        resumeSummaryField.getStyle().set("border-radius", "12px");
        resumeSummaryField.addValueChangeListener(e -> updatePreview());
        resumeGroup.add(resumeSummaryField);

        // Skills selection
        VerticalLayout skillsGroup = createSkillsSelection();

        form.add(section1, jobTitleGroup, companyGroup, descGroup, section2, resumeGroup, skillsGroup);

        return form;
    }

    private VerticalLayout createFormField(String label, VaadinIcon icon) {
        VerticalLayout group = new VerticalLayout();
        group.setPadding(false);
        group.setSpacing(false);
        group.getStyle().set("gap", "6px");

        HorizontalLayout labelRow = new HorizontalLayout();
        labelRow.setAlignItems(FlexComponent.Alignment.CENTER);
        labelRow.getStyle().set("gap", "6px");

        if (icon != null) {
            Icon iconComponent = icon.create();
            iconComponent.getStyle().set("width", "14px");
            iconComponent.getStyle().set("height", "14px");
            iconComponent.getStyle().set("color", TEXT_SECONDARY);
            labelRow.add(iconComponent);
        }

        Span labelText = new Span(label);
        labelText.getStyle().set("font-size", "12px");
        labelText.getStyle().set("font-weight", "700");
        labelText.getStyle().set("color", TEXT_SECONDARY);

        labelRow.add(labelText);
        group.add(labelRow);

        return group;
    }

    private VerticalLayout createSkillsSelection() {
        VerticalLayout group = new VerticalLayout();
        group.setPadding(false);
        group.setSpacing(false);
        group.getStyle().set("gap", "10px");

        Span label = new Span("Key Skills to Highlight");
        label.getStyle().set("font-size", "12px");
        label.getStyle().set("font-weight", "700");
        label.getStyle().set("color", TEXT_SECONDARY);

        HorizontalLayout skillsRow = new HorizontalLayout();
        skillsRow.getStyle().set("gap", "8px");
        skillsRow.getStyle().set("flex-wrap", "wrap");

        String[] skills = {"React", "TypeScript", "UI Design", "Leadership", "Agile"};

        for (String skill : skills) {
            Button skillBtn = new Button(skill);
            skillBtn.getStyle().set("background", BG_GRAY);
            skillBtn.getStyle().set("color", TEXT_PRIMARY);
            skillBtn.getStyle().set("font-size", "13px");
            skillBtn.getStyle().set("font-weight", "500");
            skillBtn.getStyle().set("border-radius", "9999px");
            skillBtn.getStyle().set("padding", "6px 14px");
            skillBtn.getStyle().set("border", "1px solid rgba(0,0,0,0.1)");
            skillBtn.getStyle().set("cursor", "pointer");

            final boolean[] selected = {false};
            skillBtn.addClickListener(e -> {
                selected[0] = !selected[0];
                if (selected[0]) {
                    skillBtn.getStyle().set("background", PRIMARY);
                    skillBtn.getStyle().set("color", "white");
                    skillBtn.getStyle().set("border-color", PRIMARY);
                } else {
                    skillBtn.getStyle().set("background", BG_GRAY);
                    skillBtn.getStyle().set("color", TEXT_PRIMARY);
                    skillBtn.getStyle().set("border-color", "rgba(0,0,0,0.1)");
                }
                updatePreview();
            });

            skillsRow.add(skillBtn);
        }

        group.add(label, skillsRow);

        return group;
    }

    private HorizontalLayout createActionButtons() {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setWidthFull();
        actions.getStyle().set("gap", "12px");
        actions.getStyle().set("padding-top", "16px");

        Button saveBtn = new Button("Save Draft", VaadinIcon.DOWNLOAD.create());
        saveBtn.getStyle().set("background", "rgba(0,0,0,0.05)");
        saveBtn.getStyle().set("color", TEXT_PRIMARY);
        saveBtn.getStyle().set("font-weight", "600");
        saveBtn.getStyle().set("border-radius", "9999px");
        saveBtn.getStyle().set("padding", "12px 24px");
        saveBtn.addClickListener(e -> Notification.show("Draft saved!", 3000, Notification.Position.TOP_CENTER));

        Button generateBtn = new Button("Generate Letter", VaadinIcon.MAGIC.create());
        generateBtn.getStyle().set("background", PRIMARY);
        generateBtn.getStyle().set("color", "white");
        generateBtn.getStyle().set("font-weight", "600");
        generateBtn.getStyle().set("border-radius", "9999px");
        generateBtn.getStyle().set("padding", "12px 32px");
        generateBtn.getStyle().set("box-shadow", "0 10px 15px -3px rgba(0,122,255,0.3)");
        generateBtn.addClickListener(e -> generateFinalLetter());

        actions.add(saveBtn, generateBtn);
        actions.expand(saveBtn);

        return actions;
    }

    private VerticalLayout createPreviewPanel() {
        VerticalLayout panel = new VerticalLayout();
        panel.setPadding(false);
        panel.setSpacing(false);
        panel.getStyle().set("gap", "20px");
        panel.setHeightFull();

        // Header
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        H2 title = new H2("Live Preview");
        title.getStyle().set("font-size", "20px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0");

        HorizontalLayout badges = new HorizontalLayout();
        badges.getStyle().set("gap", "8px");

        Span liveBadge = new Span("LIVE");
        liveBadge.getStyle().set("font-size", "10px");
        liveBadge.getStyle().set("font-weight", "800");
        liveBadge.getStyle().set("color", "#34C759");
        liveBadge.getStyle().set("background", "rgba(52,199,89,0.1)");
        liveBadge.getStyle().set("padding", "4px 8px");
        liveBadge.getStyle().set("border-radius", "9999px");

        badges.add(liveBadge);

        header.add(title, badges);
        header.expand(title);

        // Preview card
        previewContent = new Div();
        previewContent.getStyle().set("background", BG_WHITE);
        previewContent.getStyle().set("border-radius", "24px");
        previewContent.getStyle().set("padding", "40px");
        previewContent.getStyle().set("box-shadow", "0 4px 20px rgba(0,0,0,0.06)");
        previewContent.getStyle().set("flex", "1");
        previewContent.getStyle().set("overflow", "auto");

        // Letter header
        HorizontalLayout letterHeader = new HorizontalLayout();
        letterHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        letterHeader.getStyle().set("gap", "12px");
        letterHeader.getStyle().set("margin-bottom", "24px");
        letterHeader.getStyle().set("padding-bottom", "20px");
        letterHeader.getStyle().set("border-bottom", "1px solid rgba(0,0,0,0.05)");

        Div avatar = new Div();
        avatar.getStyle().set("width", "48px");
        avatar.getStyle().set("height", "48px");
        avatar.getStyle().set("border-radius", "50%");
        avatar.getStyle().set("background", PRIMARY);
        avatar.getStyle().set("display", "flex");
        avatar.getStyle().set("align-items", "center");
        avatar.getStyle().set("justify-content", "center");
        avatar.getStyle().set("color", "white");
        avatar.getStyle().set("font-weight", "700");
        avatar.setText("AR");

        VerticalLayout headerText = new VerticalLayout();
        headerText.setPadding(false);
        headerText.setSpacing(false);

        previewRole = new Span("Cover Letter");
        previewRole.getStyle().set("font-size", "16px");
        previewRole.getStyle().set("font-weight", "700");
        previewRole.getStyle().set("color", TEXT_PRIMARY);

        previewCompany = new Span("for Company");
        previewCompany.getStyle().set("font-size", "14px");
        previewCompany.getStyle().set("color", TEXT_SECONDARY);

        headerText.add(previewRole, previewCompany);
        letterHeader.add(avatar, headerText);

        // Letter body
        previewBody = new Paragraph(getDefaultPreviewText());
        previewBody.getStyle().set("font-size", "15px");
        previewBody.getStyle().set("line-height", "1.8");
        previewBody.getStyle().set("color", TEXT_PRIMARY);
        previewBody.getStyle().set("margin", "0");

        previewContent.add(letterHeader, previewBody);

        // Score indicator
        HorizontalLayout scoreRow = new HorizontalLayout();
        scoreRow.setAlignItems(FlexComponent.Alignment.CENTER);
        scoreRow.getStyle().set("gap", "12px");
        scoreRow.getStyle().set("background", BG_WHITE);
        scoreRow.getStyle().set("border-radius", "16px");
        scoreRow.getStyle().set("padding", "16px 20px");

        Icon chartIcon = VaadinIcon.CHART.create();
        chartIcon.getStyle().set("color", PRIMARY);

        VerticalLayout scoreText = new VerticalLayout();
        scoreText.setPadding(false);
        scoreText.setSpacing(false);

        Span scoreLabel = new Span("Match Score");
        scoreLabel.getStyle().set("font-size", "12px");
        scoreLabel.getStyle().set("color", TEXT_SECONDARY);

        HorizontalLayout scoreValueRow = new HorizontalLayout();
        scoreValueRow.setAlignItems(FlexComponent.Alignment.BASELINE);
        scoreValueRow.getStyle().set("gap", "4px");

        Span scoreValue = new Span("--");
        scoreValue.getStyle().set("font-size", "24px");
        scoreValue.getStyle().set("font-weight", "800");
        scoreValue.getStyle().set("color", TEXT_PRIMARY);

        Span ofHundred = new Span("/ 100");
        ofHundred.getStyle().set("font-size", "14px");
        ofHundred.getStyle().set("color", TEXT_SECONDARY);

        scoreValueRow.add(scoreValue, ofHundred);
        scoreText.add(scoreLabel, scoreValueRow);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setValue(0);
        progressBar.setWidth("100px");
        progressBar.getStyle().set("--vaadin-progress-bar-height", "6px");

        scoreRow.add(chartIcon, scoreText, progressBar);
        scoreRow.expand(scoreText);

        panel.add(header, previewContent, scoreRow);
        panel.expand(previewContent);

        return panel;
    }

    private void updatePreview() {
        String company = companyNameField.getValue();
        String role = jobTitleField.getValue();

        if (company != null && !company.isEmpty()) {
            previewCompany.setText("for " + company);
        }

        if (role != null && !role.isEmpty()) {
            previewRole.setText(role);
        }

        // Update preview text with some dynamic content
        StringBuilder preview = new StringBuilder();
        preview.append("Dear Hiring Manager,\n\n");

        if (role != null && !role.isEmpty() && company != null && !company.isEmpty()) {
            preview.append("I am writing to express my strong interest in the ").append(role)
                   .append(" position at ").append(company).append(". ");
        } else {
            preview.append("I am writing to express my interest in this position. ");
        }

        String summary = resumeSummaryField.getValue();
        if (summary != null && !summary.isEmpty()) {
            preview.append(summary).append("\n\n");
        } else {
            preview.append("With my background and experience, I believe I would be a valuable addition to your team.\n\n");
        }

        preview.append("I look forward to the opportunity to discuss how my skills align with your needs.\n\n");
        preview.append("Best regards,\n");
        preview.append("Alex Rivera");

        previewBody.setText(preview.toString());
    }

    private void generateFinalLetter() {
        if (jobTitleField.getValue() == null || jobTitleField.getValue().isEmpty() ||
            companyNameField.getValue() == null || companyNameField.getValue().isEmpty()) {
            Notification.show("Please fill in all required fields", 4000, Notification.Position.TOP_CENTER);
            return;
        }

        // Show loading overlay
        Div loadingOverlay = new Div();
        loadingOverlay.getStyle().set("position", "fixed");
        loadingOverlay.getStyle().set("inset", "0");
        loadingOverlay.getStyle().set("background", "rgba(255,255,255,0.95)");
        loadingOverlay.getStyle().set("display", "flex");
        loadingOverlay.getStyle().set("flex-direction", "column");
        loadingOverlay.getStyle().set("align-items", "center");
        loadingOverlay.getStyle().set("justify-content", "center");
        loadingOverlay.getStyle().set("gap", "24px");
        loadingOverlay.getStyle().set("z-index", "1000");

        Div spinner = new Div();
        spinner.getStyle().set("width", "48px");
        spinner.getStyle().set("height", "48px");
        spinner.getStyle().set("border", "3px solid rgba(0,122,255,0.2)");
        spinner.getStyle().set("border-top-color", PRIMARY);
        spinner.getStyle().set("border-radius", "50%");
        spinner.getStyle().set("animation", "spin 1s linear infinite");

        Paragraph loadingText = new Paragraph("Crafting your perfect cover letter...");
        loadingText.getStyle().set("font-size", "16px");
        loadingText.getStyle().set("font-weight", "600");
        loadingText.getStyle().set("color", TEXT_PRIMARY);

        loadingOverlay.add(spinner, loadingText);
        getUI().ifPresent(ui -> ui.add(loadingOverlay));

        // Simulate generation delay
        getUI().ifPresent(ui -> ui.getPage().executeJs(
            "setTimeout(() => { $0.$server.finishGeneration(); }, 2000);",
            getElement()
        ));
    }

    // Called from JavaScript after "generation" completes
    private void finishGeneration() {
        Notification.show("Cover letter generated successfully!", 4000, Notification.Position.TOP_CENTER);
        getUI().ifPresent(ui -> ui.navigate(EditorView.class));
    }

    private String getDefaultPreviewText() {
        return "Dear Hiring Manager,\n\n" +
               "I am writing to express my strong interest in the position at your company. " +
               "With my background and experience, I believe I would be a valuable addition to your team.\n\n" +
               "I look forward to the opportunity to discuss how my skills align with your needs.\n\n" +
               "Best regards,\n" +
               "Alex Rivera";
    }
}
