package com.clbooster.app.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.HashSet;
import java.util.Set;

/**
 * 4-Step Wizard Generator View matching Figma Generator.tsx
 * Step 1: Job Details
 * Step 2: Qualifications  
 * Step 3: AI Customization
 * Step 4: Review
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

    private int currentStep = 1;
    private final int totalSteps = 4;
    private boolean loading = false;

    // Form data
    private String jobTitle = "";
    private String companyName = "";
    private String jobDescription = "";
    private Set<String> selectedSkills = new HashSet<>();
    private String selectedTone = "Creative";

    // Step content containers
    private Div stepContentContainer;
    private HorizontalLayout stepIndicator;
    private Button nextButton;
    private Button backButton;
    private Div loadingOverlay;
    private VerticalLayout container;

    public GeneratorWizardView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", BG_WHITE);
        getStyle().set("overflow", "auto");

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

        String[] stepTitles = {"Job Details", "Qualifications", "AI Customization", "Review"};
        VaadinIcon[] stepIcons = {VaadinIcon.BUILDING, VaadinIcon.LIST_SELECT, VaadinIcon.STAR, VaadinIcon.CHECK_CIRCLE};

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
        VerticalLayout jobTitleGroup = createFormField("Job Title", VaadinIcon.BRIEFCASE, "e.g., Senior Frontend Engineer");
        TextField jobTitleField = (TextField) jobTitleGroup.getComponentAt(1);
        jobTitleField.setValue(jobTitle);
        jobTitleField.addValueChangeListener(e -> jobTitle = e.getValue());

        // Company Name
        VerticalLayout companyGroup = createFormField("Company Name", VaadinIcon.BUILDING, "e.g., Acme Corp");
        TextField companyField = (TextField) companyGroup.getComponentAt(1);
        companyField.setValue(companyName);
        companyField.addValueChangeListener(e -> companyName = e.getValue());

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

        TextArea descField = new TextArea();
        descField.setPlaceholder("Paste the job description here...");
        descField.setWidthFull();
        descField.setMinHeight("160px");
        descField.getStyle().set("background", BG_WHITE);
        descField.getStyle().set("border", "1px solid rgba(0,0,0,0.1)");
        descField.getStyle().set("border-radius", "12px");
        descField.getStyle().set("padding", "12px 16px");
        descField.setValue(jobDescription);
        descField.addValueChangeListener(e -> jobDescription = e.getValue());

        descGroup.add(descLabel, descField);

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
        Div skillsGrid = new Div();
        skillsGrid.getStyle().set("display", "grid");
        skillsGrid.getStyle().set("grid-template-columns", "repeat(auto-fill, minmax(140px, 1fr))");
        skillsGrid.getStyle().set("gap", "16px");
        skillsGrid.getStyle().set("width", "100%");
        skillsGrid.getStyle().set("max-width", "640px");

        String[] skills = {"React", "TypeScript", "Node.js", "UI Design", "GraphQL", "AWS", "Agile", "Leadership"};
        
        for (String skill : skills) {
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
            if (selectedSkills.contains(skill)) {
                skillBtn.getStyle().set("border-color", PRIMARY);
                skillBtn.getStyle().set("box-shadow", "0 4px 12px rgba(0,122,255,0.15)");
            }

            skillBtn.addClickListener(e -> {
                if (selectedSkills.contains(skill)) {
                    selectedSkills.remove(skill);
                    skillBtn.getStyle().set("border-color", "rgba(0,0,0,0.05)");
                    skillBtn.getStyle().set("box-shadow", "none");
                } else {
                    selectedSkills.add(skill);
                    skillBtn.getStyle().set("border-color", PRIMARY);
                    skillBtn.getStyle().set("box-shadow", "0 4px 12px rgba(0,122,255,0.15)");
                }
            });

            skillsGrid.add(skillBtn);
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

        Paragraph importDesc = new Paragraph("Upload PDF or DOCX to auto-fill skills");
        importDesc.getStyle().set("font-size", "13px");
        importDesc.getStyle().set("color", TEXT_SECONDARY);
        importDesc.getStyle().set("margin", "0 0 16px 0");

        Button uploadBtn = new Button("Upload File");
        uploadBtn.getStyle().set("background", "rgba(0,0,0,0.05)");
        uploadBtn.getStyle().set("color", TEXT_PRIMARY);
        uploadBtn.getStyle().set("font-weight", "600");
        uploadBtn.getStyle().set("border-radius", "9999px");
        uploadBtn.getStyle().set("padding", "8px 20px");
        uploadBtn.addClickListener(e -> Notification.show("Resume upload coming soon!", 3000, Notification.Position.TOP_CENTER));

        importCard.add(fileIcon, importTitle, importDesc, uploadBtn);

        layout.add(title, subtitle, skillsGrid, importCard);

        return layout;
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

        Paragraph subtitle = new Paragraph("How should the letter sound? Choose a voice that fits the company culture.");
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
        cards.add(createToneCard("Professional", 
            "Formal, structured, and strictly business. Best for corporate roles.",
            VaadinIcon.BRIEFCASE, "#e3f2fd", PRIMARY, "Professional".equals(selectedTone)));

        // Creative card (selected by default)
        cards.add(createToneCard("Creative",
            "Enthusiastic, bold, and unique. Best for startups and agencies.",
            VaadinIcon.STAR, "#f3e5f5", PURPLE, "Creative".equals(selectedTone)));

        // Storyteller card
        cards.add(createToneCard("Storyteller",
            "Focuses on your journey and impact. Best for senior/lead roles.",
            VaadinIcon.MAGIC, "#fff3e0", ORANGE, "Storyteller".equals(selectedTone)));

        layout.add(title, subtitle, cards);

        return layout;
    }

    private Div createToneCard(String title, String description, VaadinIcon iconName, String bgColor, String iconColor, boolean isSelected) {
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

        nav.add(backButton, nextButton);

        return nav;
    }

    private void updateNavigationButtons() {
        backButton.setVisible(currentStep > 1);
        
        if (currentStep == totalSteps) {
            nextButton.setText("Generate Letter");
            nextButton.setIcon(VaadinIcon.MAGIC.create());
        } else {
            nextButton.setText("Next Step");
            nextButton.setIcon(VaadinIcon.ARROW_RIGHT.create());
        }
    }

    private void handleNext() {
        if (currentStep < totalSteps) {
            // Show loading animation
            showLoading();
            
            // Simulate loading delay like Figma
            getUI().ifPresent(ui -> ui.getPage().executeJs(
                "setTimeout(() => { $0.$server.advanceStep(); }, 800);",
                getElement()
            ));
        } else {
            // Generate the letter
            generateLetter();
        }
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

    // Called from JavaScript
    private void advanceStep() {
        getUI().ifPresent(ui -> ui.access(() -> {
            hideLoading();
            if (currentStep < totalSteps) {
                showStep(currentStep + 1);
            }
        }));
    }

    private void generateLetter() {
        // Show full-screen loading
        Div fullScreenLoading = new Div();
        fullScreenLoading.getStyle().set("position", "fixed");
        fullScreenLoading.getStyle().set("inset", "0");
        fullScreenLoading.getStyle().set("background", "rgba(255,255,255,0.98)");
        fullScreenLoading.getStyle().set("display", "flex");
        fullScreenLoading.getStyle().set("flex-direction", "column");
        fullScreenLoading.getStyle().set("align-items", "center");
        fullScreenLoading.getStyle().set("justify-content", "center");
        fullScreenLoading.getStyle().set("gap", "24px");
        fullScreenLoading.getStyle().set("z-index", "1000");

        Div spinner = new Div();
        spinner.getStyle().set("width", "56px");
        spinner.getStyle().set("height", "56px");
        spinner.getStyle().set("border", "4px solid rgba(0,122,255,0.2)");
        spinner.getStyle().set("border-top-color", PRIMARY);
        spinner.getStyle().set("border-radius", "50%");
        spinner.getStyle().set("animation", "spin 1s linear infinite");

        Paragraph loadingText = new Paragraph("Crafting your perfect cover letter...");
        loadingText.getStyle().set("font-size", "18px");
        loadingText.getStyle().set("font-weight", "600");
        loadingText.getStyle().set("color", TEXT_PRIMARY);

        fullScreenLoading.add(spinner, loadingText);
        add(fullScreenLoading);

        // Navigate to editor after delay
        getUI().ifPresent(ui -> ui.getPage().executeJs(
            "setTimeout(() => { window.location.href = '/editor'; }, 2500);"
        ));
    }
}
