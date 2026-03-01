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
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route(value = "onboarding", layout = MainLayout.class)
@PageTitle("Onboarding | CL Booster")
public class OnboardingView extends VerticalLayout {

    // Figma Design System Colors
    private static final String PRIMARY = "#007AFF";
    private static final String PRIMARY_LIGHT = "#5AC8FA";
    private static final String TEXT_PRIMARY = "#1d1d1f";
    private static final String TEXT_SECONDARY = "#86868b";
    private static final String BG_WHITE = "#ffffff";
    private static final String BG_GRAY = "#f5f5f7";
    private static final String SUCCESS = "#34C759";
    private static final String WARNING = "#FF9500";

    private int currentStep = 1;
    private final int totalSteps = 3;

    // Form data
    private String fullName = "";
    private String email = "";
    private String jobTitle = "";
    private String location = "";
    private List<String> selectedIndustries = new ArrayList<>();
    private List<String> selectedExperience = new ArrayList<>();
    private String selectedTone = "Professional";
    private String careerGoal = "";

    // UI Components
    private VerticalLayout contentArea;
    private HorizontalLayout stepIndicator;
    private Button backButton;
    private Button nextButton;
    private ProgressBar progressBar;

    // Form fields
    private TextField nameField;
    private EmailField emailField;
    private TextField titleField;
    private TextField locationField;
    private TextArea bioField;
    private TextArea goalField;

    private final String[] industries = {"Technology", "Finance", "Healthcare", "Education", "Marketing", "Design", "Consulting", "Manufacturing"};
    private final String[] experienceLevels = {"Entry Level (0-2 years)", "Mid Level (3-5 years)", "Senior Level (6-10 years)", "Executive (10+ years)"};

    public OnboardingView() {
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", BG_WHITE);
        setSizeFull();

        initializeFields();
        buildUI();
        showStep(1);
    }

    private void initializeFields() {
        nameField = new TextField();
        nameField.setPlaceholder("e.g. Alex Rivera");
        nameField.setWidthFull();

        emailField = new EmailField();
        emailField.setPlaceholder("alex@example.com");
        emailField.setWidthFull();

        titleField = new TextField();
        titleField.setPlaceholder("e.g. Senior Product Designer");
        titleField.setWidthFull();

        locationField = new TextField();
        locationField.setPlaceholder("e.g. Helsinki, Finland");
        locationField.setWidthFull();

        bioField = new TextArea();
        bioField.setPlaceholder("Tell us about your professional background...");
        bioField.setMinHeight("120px");
        bioField.setWidthFull();

        goalField = new TextArea();
        goalField.setPlaceholder("What are your career goals? What kind of roles are you looking for?");
        goalField.setMinHeight("120px");
        goalField.setWidthFull();
    }

    private void buildUI() {
        // Main container with max width
        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.setPadding(true);
        mainContainer.setSpacing(false);
        mainContainer.getStyle().set("gap", "0");
        mainContainer.getStyle().set("padding", "48px 32px");
        mainContainer.getStyle().set("max-width", "800px");
        mainContainer.getStyle().set("margin", "0 auto");
        mainContainer.getStyle().set("min-height", "100vh");
        mainContainer.setSizeFull();

        // Progress header
        HorizontalLayout progressHeader = createProgressHeader();

        // Step indicator
        stepIndicator = createStepIndicator();

        // Progress bar
        progressBar = new ProgressBar();
        progressBar.setValue(0.33);
        progressBar.setWidthFull();
        progressBar.getStyle().set("height", "4px");
        progressBar.getStyle().set("background", "rgba(0,0,0,0.05)");
        progressBar.getStyle().set("margin", "32px 0");

        // Content area
        contentArea = new VerticalLayout();
        contentArea.setPadding(false);
        contentArea.setSpacing(false);
        contentArea.getStyle().set("gap", "32px");
        contentArea.getStyle().set("flex", "1");

        // Navigation
        HorizontalLayout navigation = createNavigation();

        mainContainer.add(progressHeader, stepIndicator, progressBar, contentArea, navigation);
        add(mainContainer);
    }

    private HorizontalLayout createProgressHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        // Logo
        HorizontalLayout logoGroup = new HorizontalLayout();
        logoGroup.setAlignItems(FlexComponent.Alignment.CENTER);
        logoGroup.getStyle().set("gap", "12px");

        Div logoIcon = new Div();
        logoIcon.getStyle().set("width", "40px");
        logoIcon.getStyle().set("height", "40px");
        logoIcon.getStyle().set("background", "linear-gradient(135deg, " + PRIMARY + " 0%, " + PRIMARY_LIGHT + " 100%)");
        logoIcon.getStyle().set("border-radius", "12px");
        logoIcon.getStyle().set("display", "flex");
        logoIcon.getStyle().set("align-items", "center");
        logoIcon.getStyle().set("justify-content", "center");

        Icon rocketIcon = VaadinIcon.ROCKET.create();
        rocketIcon.getStyle().set("color", "white");
        rocketIcon.getStyle().set("width", "20px");
        rocketIcon.getStyle().set("height", "20px");
        logoIcon.add(rocketIcon);

        Span logoText = new Span("CL Booster");
        logoText.getStyle().set("font-size", "20px");
        logoText.getStyle().set("font-weight", "700");
        logoText.getStyle().set("color", TEXT_PRIMARY);

        logoGroup.add(logoIcon, logoText);

        // Exit button
        Button exitBtn = new Button("Exit", VaadinIcon.CLOSE.create());
        exitBtn.getStyle().set("background", "transparent");
        exitBtn.getStyle().set("color", TEXT_SECONDARY);
        exitBtn.getStyle().set("font-weight", "500");
        exitBtn.getStyle().set("border", "none");
        exitBtn.getStyle().set("cursor", "pointer");
        exitBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(DashboardView.class)));

        header.add(logoGroup, exitBtn);
        header.expand(logoGroup);

        return header;
    }

    private HorizontalLayout createStepIndicator() {
        HorizontalLayout indicator = new HorizontalLayout();
        indicator.setWidthFull();
        indicator.getStyle().set("gap", "16px");
        indicator.getStyle().set("margin-top", "32px");

        // Step 1: Profile
        indicator.add(createStepBadge(1, "Profile", VaadinIcon.USER));
        indicator.add(createStepConnector());

        // Step 2: Career
        indicator.add(createStepBadge(2, "Career", VaadinIcon.BRIEFCASE));
        indicator.add(createStepConnector());

        // Step 3: Preferences
        indicator.add(createStepBadge(3, "Preferences", VaadinIcon.COG));

        return indicator;
    }

    private Div createStepBadge(int step, String label, VaadinIcon iconType) {
        Div badge = new Div();
        badge.getStyle().set("display", "flex");
        badge.getStyle().set("align-items", "center");
        badge.getStyle().set("gap", "12px");
        badge.getStyle().set("flex", "1");

        // Number/icon circle
        Div circle = new Div();
        circle.getStyle().set("width", "36px");
        circle.getStyle().set("height", "36px");
        circle.getStyle().set("border-radius", "50%");
        circle.getStyle().set("display", "flex");
        circle.getStyle().set("align-items", "center");
        circle.getStyle().set("justify-content", "center");
        circle.getStyle().set("font-size", "14px");
        circle.getStyle().set("font-weight", "700");
        circle.getStyle().set("transition", "all 0.3s");

        if (step < currentStep) {
            // Completed
            circle.getStyle().set("background", SUCCESS);
            Icon check = VaadinIcon.CHECK.create();
            check.getStyle().set("color", "white");
            check.getStyle().set("width", "18px");
            check.getStyle().set("height", "18px");
            circle.add(check);
        } else if (step == currentStep) {
            // Current
            circle.getStyle().set("background", PRIMARY);
            circle.getStyle().set("color", "white");
            circle.getStyle().set("box-shadow", "0 4px 12px rgba(0,122,255,0.3)");

            Icon icon = iconType.create();
            icon.getStyle().set("color", "white");
            icon.getStyle().set("width", "18px");
            icon.getStyle().set("height", "18px");
            circle.add(icon);
        } else {
            // Future
            circle.getStyle().set("background", BG_GRAY);
            circle.getStyle().set("color", TEXT_SECONDARY);
            circle.setText(String.valueOf(step));
        }

        // Label
        Span labelSpan = new Span(label);
        labelSpan.getStyle().set("font-size", "14px");
        labelSpan.getStyle().set("font-weight", step == currentStep ? "600" : "500");
        labelSpan.getStyle().set("color", step == currentStep ? TEXT_PRIMARY : TEXT_SECONDARY);

        badge.add(circle, labelSpan);

        return badge;
    }

    private Div createStepConnector() {
        Div connector = new Div();
        connector.getStyle().set("flex", "1");
        connector.getStyle().set("height", "2px");
        connector.getStyle().set("background", "rgba(0,0,0,0.05)");
        connector.getStyle().set("align-self", "center");
        connector.getStyle().set("max-width", "60px");
        return connector;
    }

    private HorizontalLayout createNavigation() {
        HorizontalLayout nav = new HorizontalLayout();
        nav.setWidthFull();
        nav.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        nav.setAlignItems(FlexComponent.Alignment.CENTER);
        nav.getStyle().set("padding-top", "32px");
        nav.getStyle().set("border-top", "1px solid rgba(0,0,0,0.05)");
        nav.getStyle().set("margin-top", "auto");

        // Back button
        backButton = new Button("Back", VaadinIcon.ARROW_LEFT.create());
        backButton.getStyle().set("background", "transparent");
        backButton.getStyle().set("color", TEXT_SECONDARY);
        backButton.getStyle().set("font-weight", "600");
        backButton.getStyle().set("border", "none");
        backButton.getStyle().set("padding", "12px 24px");
        backButton.getStyle().set("cursor", "pointer");
        backButton.getStyle().set("transition", "all 0.2s");
        backButton.addClickListener(e -> {
            if (currentStep > 1) {
                showStep(currentStep - 1);
            }
        });
        backButton.setVisible(false);

        backButton.getElement().addEventListener("mouseenter", e -> {
            backButton.getStyle().set("color", TEXT_PRIMARY);
        });
        backButton.getElement().addEventListener("mouseleave", e -> {
            backButton.getStyle().set("color", TEXT_SECONDARY);
        });

        // Step counter
        Span stepCounter = new Span("Step " + currentStep + " of " + totalSteps);
        stepCounter.getStyle().set("font-size", "14px");
        stepCounter.getStyle().set("color", TEXT_SECONDARY);

        // Next/Complete button
        nextButton = createPrimaryButton("Continue", this::handleNext);

        nav.add(backButton, stepCounter, nextButton);
        return nav;
    }

    private void showStep(int step) {
        currentStep = step;
        contentArea.removeAll();

        // Update progress bar
        progressBar.setValue((double) step / totalSteps);

        // Update step indicator
        updateStepIndicator();

        // Update navigation
        HorizontalLayout nav = (HorizontalLayout) getComponentAt(0).getChildren()
            .filter(c -> c instanceof HorizontalLayout)
            .reduce((first, second) -> second)
            .orElse(null);

        if (nav != null) {
            Button backBtn = (Button) nav.getComponentAt(0);
            backBtn.setVisible(step > 1);

            Span counter = (Span) nav.getComponentAt(1);
            counter.setText("Step " + step + " of " + totalSteps);

            Button nextBtn = (Button) nav.getComponentAt(2);
            nextBtn.setText(step == totalSteps ? "Complete Setup" : "Continue");
            if (step == totalSteps) {
                nextBtn.setIcon(VaadinIcon.CHECK_CIRCLE.create());
            } else {
                nextBtn.setIcon(VaadinIcon.ARROW_RIGHT.create());
            }
        }

        switch (step) {
            case 1:
                showProfileStep();
                break;
            case 2:
                showCareerStep();
                break;
            case 3:
                showPreferencesStep();
                break;
        }
    }

    private void updateStepIndicator() {
        stepIndicator.removeAll();

        // Step 1: Profile
        stepIndicator.add(createStepBadge(1, "Profile", VaadinIcon.USER));
        stepIndicator.add(createStepConnector());

        // Step 2: Career
        stepIndicator.add(createStepBadge(2, "Career", VaadinIcon.BRIEFCASE));
        stepIndicator.add(createStepConnector());

        // Step 3: Preferences
        stepIndicator.add(createStepBadge(3, "Preferences", VaadinIcon.COG));
    }

    private void showProfileStep() {
        // Header
        VerticalLayout header = new VerticalLayout();
        header.setPadding(false);
        header.setSpacing(false);
        header.getStyle().set("gap", "8px");
        header.getStyle().set("text-align", "center");

        H1 title = new H1("Let's get to know you");
        title.getStyle().set("font-size", "32px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0");
        title.getStyle().set("letter-spacing", "-0.025em");

        Paragraph subtitle = new Paragraph("Start with the basics. This helps us personalize your experience.");
        subtitle.getStyle().set("font-size", "16px");
        subtitle.getStyle().set("color", TEXT_SECONDARY);
        subtitle.getStyle().set("margin", "0");

        header.add(title, subtitle);

        // Form card
        Div formCard = new Div();
        formCard.getStyle().set("background", BG_WHITE);
        formCard.getStyle().set("border", "1px solid rgba(0,0,0,0.05)");
        formCard.getStyle().set("border-radius", "24px");
        formCard.getStyle().set("padding", "32px");
        formCard.getStyle().set("box-shadow", "0 2px 12px rgba(0,0,0,0.04)");

        VerticalLayout form = new VerticalLayout();
        form.setPadding(false);
        form.setSpacing(false);
        form.getStyle().set("gap", "24px");

        // Form fields
        form.add(createFormGroup("Full Name", nameField, VaadinIcon.USER));
        form.add(createFormGroup("Email Address", emailField, VaadinIcon.ENVELOPE));

        HorizontalLayout row2 = new HorizontalLayout();
        row2.setWidthFull();
        row2.getStyle().set("gap", "16px");
        row2.add(createFormGroup("Job Title", titleField, VaadinIcon.BRIEFCASE), createFormGroup("Location", locationField, VaadinIcon.MAP_MARKER));
        row2.expand(createFormGroup("Job Title", titleField, VaadinIcon.BRIEFCASE), createFormGroup("Location", locationField, VaadinIcon.MAP_MARKER));

        form.add(row2);
        form.add(createFormGroup("Professional Bio", bioField, null));

        formCard.add(form);

        contentArea.add(header, formCard);
    }

    private void showCareerStep() {
        // Header
        VerticalLayout header = new VerticalLayout();
        header.setPadding(false);
        header.setSpacing(false);
        header.getStyle().set("gap", "8px");
        header.getStyle().set("text-align", "center");

        H1 title = new H1("Your career background");
        title.getStyle().set("font-size", "32px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0");
        title.getStyle().set("letter-spacing", "-0.025em");

        Paragraph subtitle = new Paragraph("Help us understand your experience and career goals.");
        subtitle.getStyle().set("font-size", "16px");
        subtitle.getStyle().set("color", TEXT_SECONDARY);
        subtitle.getStyle().set("margin", "0");

        header.add(title, subtitle);

        // Industries card
        Div industriesCard = new Div();
        industriesCard.getStyle().set("background", BG_WHITE);
        industriesCard.getStyle().set("border", "1px solid rgba(0,0,0,0.05)");
        industriesCard.getStyle().set("border-radius", "24px");
        industriesCard.getStyle().set("padding", "32px");
        industriesCard.getStyle().set("box-shadow", "0 2px 12px rgba(0,0,0,0.04)");

        H3 industriesTitle = new H3("Industries you work in");
        industriesTitle.getStyle().set("font-size", "18px");
        industriesTitle.getStyle().set("font-weight", "700");
        industriesTitle.getStyle().set("color", TEXT_PRIMARY);
        industriesTitle.getStyle().set("margin", "0 0 16px 0");

        Paragraph industriesDesc = new Paragraph("Select all that apply to your experience");
        industriesDesc.getStyle().set("font-size", "14px");
        industriesDesc.getStyle().set("color", TEXT_SECONDARY);
        industriesDesc.getStyle().set("margin", "0 0 24px 0");

        // Industry chips
        HorizontalLayout industryChips = new HorizontalLayout();
        industryChips.getStyle().set("gap", "10px");
        industryChips.getStyle().set("flex-wrap", "wrap");

        for (String industry : industries) {
            Button chip = createChip(industry, selectedIndustries.contains(industry), () -> {
                if (selectedIndustries.contains(industry)) {
                    selectedIndustries.remove(industry);
                } else {
                    selectedIndustries.add(industry);
                }
                showStep(2); // Refresh to update styles
            });
            industryChips.add(chip);
        }

        industriesCard.add(industriesTitle, industriesDesc, industryChips);

        // Experience level card
        Div expCard = new Div();
        expCard.getStyle().set("background", BG_WHITE);
        expCard.getStyle().set("border", "1px solid rgba(0,0,0,0.05)");
        expCard.getStyle().set("border-radius", "24px");
        expCard.getStyle().set("padding", "32px");
        expCard.getStyle().set("box-shadow", "0 2px 12px rgba(0,0,0,0.04)");
        expCard.getStyle().set("margin-top", "24px");

        H3 expTitle = new H3("Experience level");
        expTitle.getStyle().set("font-size", "18px");
        expTitle.getStyle().set("font-weight", "700");
        expTitle.getStyle().set("color", TEXT_PRIMARY);
        expTitle.getStyle().set("margin", "0 0 16px 0");

        Paragraph expDesc = new Paragraph("Select your current career level");
        expDesc.getStyle().set("font-size", "14px");
        expDesc.getStyle().set("color", TEXT_SECONDARY);
        expDesc.getStyle().set("margin", "0 0 24px 0");

        VerticalLayout expOptions = new VerticalLayout();
        expOptions.setPadding(false);
        expOptions.setSpacing(false);
        expOptions.getStyle().set("gap", "12px");

        for (String level : experienceLevels) {
            expOptions.add(createRadioOption(level, selectedExperience.contains(level), () -> {
                selectedExperience.clear();
                selectedExperience.add(level);
                showStep(2); // Refresh
            }));
        }

        expCard.add(expTitle, expDesc, expOptions);

        // Career goals
        Div goalsCard = new Div();
        goalsCard.getStyle().set("background", BG_WHITE);
        goalsCard.getStyle().set("border", "1px solid rgba(0,0,0,0.05)");
        goalsCard.getStyle().set("border-radius", "24px");
        goalsCard.getStyle().set("padding", "32px");
        goalsCard.getStyle().set("box-shadow", "0 2px 12px rgba(0,0,0,0.04)");
        goalsCard.getStyle().set("margin-top", "24px");

        H3 goalsTitle = new H3("Career goals");
        goalsTitle.getStyle().set("font-size", "18px");
        goalsTitle.getStyle().set("font-weight", "700");
        goalsTitle.getStyle().set("color", TEXT_PRIMARY);
        goalsTitle.getStyle().set("margin", "0 0 16px 0");

        Paragraph goalsDesc = new Paragraph("What are you looking for in your next role?");
        goalsDesc.getStyle().set("font-size", "14px");
        goalsDesc.getStyle().set("color", TEXT_SECONDARY);
        goalsDesc.getStyle().set("margin", "0 0 24px 0");

        goalsCard.add(goalsTitle, goalsDesc, createFormGroup(null, goalField, null));

        contentArea.add(header, industriesCard, expCard, goalsCard);
    }

    private void showPreferencesStep() {
        // Header
        VerticalLayout header = new VerticalLayout();
        header.setPadding(false);
        header.setSpacing(false);
        header.getStyle().set("gap", "8px");
        header.getStyle().set("text-align", "center");

        H1 title = new H1("Personalize your experience");
        title.getStyle().set("font-size", "32px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0");
        title.getStyle().set("letter-spacing", "-0.025em");

        Paragraph subtitle = new Paragraph("Customize how CL Booster works for you.");
        subtitle.getStyle().set("font-size", "16px");
        subtitle.getStyle().set("color", TEXT_SECONDARY);
        subtitle.getStyle().set("margin", "0");

        header.add(title, subtitle);

        // Writing tone card
        Div toneCard = new Div();
        toneCard.getStyle().set("background", BG_WHITE);
        toneCard.getStyle().set("border", "1px solid rgba(0,0,0,0.05)");
        toneCard.getStyle().set("border-radius", "24px");
        toneCard.getStyle().set("padding", "32px");
        toneCard.getStyle().set("box-shadow", "0 2px 12px rgba(0,0,0,0.04)");

        H3 toneTitle = new H3("Preferred writing tone");
        toneTitle.getStyle().set("font-size", "18px");
        toneTitle.getStyle().set("font-weight", "700");
        toneTitle.getStyle().set("color", TEXT_PRIMARY);
        toneTitle.getStyle().set("margin", "0 0 8px 0");

        Paragraph toneDesc = new Paragraph("Choose the default tone for your cover letters");
        toneDesc.getStyle().set("font-size", "14px");
        toneDesc.getStyle().set("color", TEXT_SECONDARY);
        toneDesc.getStyle().set("margin", "0 0 24px 0");

        // Tone options
        HorizontalLayout toneOptions = new HorizontalLayout();
        toneOptions.setWidthFull();
        toneOptions.getStyle().set("gap", "16px");

        String[][] toneData = {
            {"Professional", "Formal and polished", PRIMARY, VaadinIcon.BRIEFCASE.name()},
            {"Creative", "Bold and unique", "#AF52DE", VaadinIcon.STAR.name()},
            {"Casual", "Friendly and approachable", WARNING, VaadinIcon.SMILEY_O.name()}
        };

        for (String[] tone : toneData) {
            toneOptions.add(createToneOption(tone[0], tone[1], tone[2], tone[3]));
        }

        toneCard.add(toneTitle, toneDesc, toneOptions);

        // Notifications card
        Div notifCard = new Div();
        notifCard.getStyle().set("background", BG_WHITE);
        notifCard.getStyle().set("border", "1px solid rgba(0,0,0,0.05)");
        notifCard.getStyle().set("border-radius", "24px");
        notifCard.getStyle().set("padding", "32px");
        notifCard.getStyle().set("box-shadow", "0 2px 12px rgba(0,0,0,0.04)");
        notifCard.getStyle().set("margin-top", "24px");

        H3 notifTitle = new H3("Notifications");
        notifTitle.getStyle().set("font-size", "18px");
        notifTitle.getStyle().set("font-weight", "700");
        notifTitle.getStyle().set("color", TEXT_PRIMARY);
        notifTitle.getStyle().set("margin", "0 0 8px 0");

        Paragraph notifDesc = new Paragraph("Stay updated on your application progress");
        notifDesc.getStyle().set("font-size", "14px");
        notifDesc.getStyle().set("color", TEXT_SECONDARY);
        notifDesc.getStyle().set("margin", "0 0 24px 0");

        VerticalLayout toggles = new VerticalLayout();
        toggles.setPadding(false);
        toggles.setSpacing(false);
        toggles.getStyle().set("gap", "16px");

        toggles.add(createToggleRow("Email notifications", "Weekly summaries and updates", true));
        toggles.add(createToggleRow("Match alerts", "Get notified when jobs match your profile", true));
        toggles.add(createToggleRow("Product updates", "New features and improvements", false));

        notifCard.add(notifTitle, notifDesc, toggles);

        // Summary card
        Div summaryCard = new Div();
        summaryCard.getStyle().set("background", "linear-gradient(135deg, " + PRIMARY + " 0%, " + PRIMARY_LIGHT + " 100%)");
        summaryCard.getStyle().set("border-radius", "24px");
        summaryCard.getStyle().set("padding", "32px");
        summaryCard.getStyle().set("margin-top", "24px");
        summaryCard.getStyle().set("color", "white");

        HorizontalLayout summaryHeader = new HorizontalLayout();
        summaryHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        summaryHeader.getStyle().set("gap", "16px");
        summaryHeader.getStyle().set("margin-bottom", "16px");

        Div checkCircle = new Div();
        checkCircle.getStyle().set("width", "48px");
        checkCircle.getStyle().set("height", "48px");
        checkCircle.getStyle().set("background", "rgba(255,255,255,0.2)");
        checkCircle.getStyle().set("border-radius", "50%");
        checkCircle.getStyle().set("display", "flex");
        checkCircle.getStyle().set("align-items", "center");
        checkCircle.getStyle().set("justify-content", "center");

        Icon checkIcon = VaadinIcon.CHECK.create();
        checkIcon.getStyle().set("color", "white");
        checkIcon.getStyle().set("width", "24px");
        checkIcon.getStyle().set("height", "24px");
        checkCircle.add(checkIcon);

        VerticalLayout summaryText = new VerticalLayout();
        summaryText.setPadding(false);
        summaryText.setSpacing(false);
        summaryText.getStyle().set("gap", "4px");

        H3 summaryTitle = new H3("You're all set!");
        summaryTitle.getStyle().set("font-size", "20px");
        summaryTitle.getStyle().set("font-weight", "700");
        summaryTitle.getStyle().set("color", "white");
        summaryTitle.getStyle().set("margin", "0");

        Paragraph summaryDesc = new Paragraph("Review your preferences and complete setup");
        summaryDesc.getStyle().set("font-size", "14px");
        summaryDesc.getStyle().set("color", "rgba(255,255,255,0.8)");
        summaryDesc.getStyle().set("margin", "0");

        summaryText.add(summaryTitle, summaryDesc);
        summaryHeader.add(checkCircle, summaryText);

        // Quick stats
        HorizontalLayout stats = new HorizontalLayout();
        stats.setWidthFull();
        stats.getStyle().set("gap", "24px");
        stats.getStyle().set("padding-top", "16px");
        stats.getStyle().set("border-top", "1px solid rgba(255,255,255,0.2)");

        stats.add(createSummaryStat("Profile", "Completed"));
        stats.add(createSummaryStat("Career", selectedExperience.isEmpty() ? "Pending" : selectedExperience.get(0).split(" ")[0]));
        stats.add(createSummaryStat("Tone", selectedTone));

        summaryCard.add(summaryHeader, stats);

        contentArea.add(header, toneCard, notifCard, summaryCard);
    }

    private VerticalLayout createFormGroup(String label, com.vaadin.flow.component.Component field, VaadinIcon icon) {
        VerticalLayout group = new VerticalLayout();
        group.setPadding(false);
        group.setSpacing(false);
        group.getStyle().set("gap", "8px");
        group.setWidthFull();

        if (label != null) {
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
            labelText.getStyle().set("text-transform", "uppercase");
            labelText.getStyle().set("letter-spacing", "0.05em");

            labelRow.add(labelText);
            group.add(labelRow);
        }

        field.getElement().getStyle().set("--vaadin-input-field-background", BG_GRAY);
        field.getElement().getStyle().set("--vaadin-input-field-border-radius", "12px");

        group.add(field);

        return group;
    }

    private Button createChip(String text, boolean selected, Runnable onClick) {
        Button chip = new Button(text, e -> onClick.run());
        chip.getStyle().set("padding", "10px 18px");
        chip.getStyle().set("border-radius", "9999px");
        chip.getStyle().set("font-weight", selected ? "600" : "500");
        chip.getStyle().set("font-size", "13px");
        chip.getStyle().set("border", selected ? "1px solid " + PRIMARY : "1px solid rgba(0,0,0,0.1)");
        chip.getStyle().set("background", selected ? "rgba(0,122,255,0.1)" : BG_WHITE);
        chip.getStyle().set("color", selected ? PRIMARY : TEXT_PRIMARY);
        chip.getStyle().set("cursor", "pointer");
        chip.getStyle().set("transition", "all 0.2s");

        chip.getElement().addEventListener("mouseenter", e -> {
            if (!selected) {
                chip.getStyle().set("background", "rgba(0,0,0,0.05)");
            }
        });
        chip.getElement().addEventListener("mouseleave", e -> {
            if (!selected) {
                chip.getStyle().set("background", BG_WHITE);
            }
        });

        return chip;
    }

    private Div createRadioOption(String label, boolean selected, Runnable onClick) {
        Div option = new Div();
        option.getStyle().set("display", "flex");
        option.getStyle().set("align-items", "center");
        option.getStyle().set("gap", "16px");
        option.getStyle().set("padding", "16px 20px");
        option.getStyle().set("border-radius", "12px");
        option.getStyle().set("background", selected ? "rgba(0,122,255,0.05)" : BG_GRAY);
        option.getStyle().set("border", selected ? "2px solid " + PRIMARY : "2px solid transparent");
        option.getStyle().set("cursor", "pointer");
        option.getStyle().set("transition", "all 0.2s");

        // Radio circle
        Div radio = new Div();
        radio.getStyle().set("width", "20px");
        radio.getStyle().set("height", "20px");
        radio.getStyle().set("border-radius", "50%");
        radio.getStyle().set("border", selected ? "6px solid " + PRIMARY : "2px solid " + TEXT_SECONDARY);
        radio.getStyle().set("transition", "all 0.2s");

        Span labelSpan = new Span(label);
        labelSpan.getStyle().set("font-size", "14px");
        labelSpan.getStyle().set("font-weight", selected ? "600" : "500");
        labelSpan.getStyle().set("color", selected ? TEXT_PRIMARY : TEXT_SECONDARY);

        option.add(radio, labelSpan);

        option.getElement().addEventListener("click", e -> onClick.run());

        option.getElement().addEventListener("mouseenter", e -> {
            if (!selected) {
                option.getStyle().set("background", "rgba(0,0,0,0.06)");
            }
        });
        option.getElement().addEventListener("mouseleave", e -> {
            if (!selected) {
                option.getStyle().set("background", BG_GRAY);
            }
        });

        return option;
    }

    private Div createToneOption(String title, String description, String color, String iconName) {
        boolean selected = selectedTone.equals(title);

        Div card = new Div();
        card.getStyle().set("flex", "1");
        card.getStyle().set("background", selected ? "rgba(0,122,255,0.05)" : BG_GRAY);
        card.getStyle().set("border", selected ? "2px solid " + color : "2px solid transparent");
        card.getStyle().set("border-radius", "16px");
        card.getStyle().set("padding", "24px");
        card.getStyle().set("cursor", "pointer");
        card.getStyle().set("transition", "all 0.3s");
        card.getStyle().set("text-align", "center");

        if (selected) {
            card.getStyle().set("box-shadow", "0 10px 20px rgba(0,122,255,0.1)");
        }

        Div iconContainer = new Div();
        iconContainer.getStyle().set("width", "48px");
        iconContainer.getStyle().set("height", "48px");
        iconContainer.getStyle().set("border-radius", "50%");
        iconContainer.getStyle().set("background", color + "20");
        iconContainer.getStyle().set("display", "flex");
        iconContainer.getStyle().set("align-items", "center");
        iconContainer.getStyle().set("justify-content", "center");
        iconContainer.getStyle().set("margin", "0 auto 16px");

        Icon icon = VaadinIcon.valueOf(iconName).create();
        icon.getStyle().set("color", color);
        icon.getStyle().set("width", "24px");
        icon.getStyle().set("height", "24px");
        iconContainer.add(icon);

        H3 cardTitle = new H3(title);
        cardTitle.getStyle().set("font-size", "16px");
        cardTitle.getStyle().set("font-weight", selected ? "700" : "600");
        cardTitle.getStyle().set("color", selected ? color : TEXT_PRIMARY);
        cardTitle.getStyle().set("margin", "0 0 4px 0");

        Paragraph cardDesc = new Paragraph(description);
        cardDesc.getStyle().set("font-size", "13px");
        cardDesc.getStyle().set("color", TEXT_SECONDARY);
        cardDesc.getStyle().set("margin", "0");

        card.add(iconContainer, cardTitle, cardDesc);

        card.getElement().addEventListener("click", e -> {
            selectedTone = title;
            showStep(3);
        });

        card.getElement().addEventListener("mouseenter", e -> {
            if (!selected) {
                card.getStyle().set("border-color", color + "50");
            }
        });
        card.getElement().addEventListener("mouseleave", e -> {
            if (!selected) {
                card.getStyle().set("border-color", "transparent");
            }
        });

        return card;
    }

    private HorizontalLayout createToggleRow(String title, String description, boolean enabled) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.getStyle().set("padding", "8px 0");

        VerticalLayout textGroup = new VerticalLayout();
        textGroup.setPadding(false);
        textGroup.setSpacing(false);
        textGroup.getStyle().set("gap", "4px");

        Span titleSpan = new Span(title);
        titleSpan.getStyle().set("font-size", "14px");
        titleSpan.getStyle().set("font-weight", "600");
        titleSpan.getStyle().set("color", TEXT_PRIMARY);

        Span descSpan = new Span(description);
        descSpan.getStyle().set("font-size", "13px");
        descSpan.getStyle().set("color", TEXT_SECONDARY);

        textGroup.add(titleSpan, descSpan);

        Div toggle = createToggleSwitch(enabled);

        row.add(textGroup, toggle);
        row.expand(textGroup);

        return row;
    }

    private Div createToggleSwitch(boolean enabled) {
        Div track = new Div();
        track.getStyle().set("width", "48px");
        track.getStyle().set("height", "28px");
        track.getStyle().set("background", enabled ? SUCCESS : "rgba(0,0,0,0.2)");
        track.getStyle().set("border-radius", "9999px");
        track.getStyle().set("position", "relative");
        track.getStyle().set("cursor", "pointer");
        track.getStyle().set("transition", "background 0.2s");

        Div thumb = new Div();
        thumb.getStyle().set("width", "24px");
        thumb.getStyle().set("height", "24px");
        thumb.getStyle().set("background", "white");
        thumb.getStyle().set("border-radius", "50%");
        thumb.getStyle().set("position", "absolute");
        thumb.getStyle().set("top", "2px");
        thumb.getStyle().set(enabled ? "right" : "left", "2px");
        thumb.getStyle().set("box-shadow", "0 2px 4px rgba(0,0,0,0.2)");
        thumb.getStyle().set("transition", "all 0.2s");

        track.add(thumb);

        return track;
    }

    private VerticalLayout createSummaryStat(String label, String value) {
        VerticalLayout stat = new VerticalLayout();
        stat.setPadding(false);
        stat.setSpacing(false);
        stat.getStyle().set("gap", "4px");

        Span valueSpan = new Span(value);
        valueSpan.getStyle().set("font-size", "16px");
        valueSpan.getStyle().set("font-weight", "700");
        valueSpan.getStyle().set("color", "white");

        Span labelSpan = new Span(label);
        labelSpan.getStyle().set("font-size", "12px");
        labelSpan.getStyle().set("color", "rgba(255,255,255,0.7)");

        stat.add(valueSpan, labelSpan);

        return stat;
    }

    private void handleNext() {
        if (currentStep < totalSteps) {
            showStep(currentStep + 1);
        } else {
            // Complete onboarding
            Notification.show("Welcome to CL Booster! Your profile is set up.", 4000, Notification.Position.BOTTOM_CENTER);
            getUI().ifPresent(ui -> ui.navigate(DashboardView.class));
        }
    }

    private Button createPrimaryButton(String text, Runnable action) {
        Button btn = new Button(text, e -> action.run());
        btn.getStyle().set("background", "linear-gradient(135deg, " + PRIMARY + " 0%, " + PRIMARY_LIGHT + " 100%)");
        btn.getStyle().set("color", "white");
        btn.getStyle().set("font-weight", "600");
        btn.getStyle().set("font-size", "14px");
        btn.getStyle().set("border-radius", "9999px");
        btn.getStyle().set("border", "none");
        btn.getStyle().set("padding", "12px 28px");
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
}
