package com.clbooster.app.views;

import com.clbooster.app.views.util.StyleConstants;

import jakarta.annotation.security.PermitAll;
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
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route(value = "onboarding", layout = MainLayout.class)
@PageTitle("Onboarding | CL Booster")
@PermitAll
public class OnboardingView extends VerticalLayout {
    private static final String BORDER_2PX = "2px solid ";
    private static final String MARGIN_24 = "0 0 24px 0";
    private static final String LETTER_SPACING = "-0.025em";
    private static final String BG_HOVER = "rgba(0,0,0,0.05)";

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

    private final String[] industries = { "Technology", "Finance", "Healthcare", "Education", "Marketing", "Design",
            "Consulting", "Manufacturing" };
    private final String[] experienceLevels = { "Entry Level (0-2 years)", "Mid Level (3-5 years)",
            "Senior Level (6-10 years)", "Executive (10+ years)" };

    public OnboardingView() {
        setPadding(false);
        setSpacing(false);
        getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
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
        mainContainer.getStyle().set(StyleConstants.CSS_PADDING, "48px 32px");
        mainContainer.getStyle().set(StyleConstants.CSS_MAX_WIDTH, "800px");
        mainContainer.getStyle().set(StyleConstants.CSS_MARGIN, "0 auto");
        mainContainer.getStyle().set(StyleConstants.CSS_MIN_HEIGHT, "100vh");
        mainContainer.setSizeFull();

        // Progress header
        HorizontalLayout progressHeader = createProgressHeader();

        // Step indicator
        stepIndicator = createStepIndicator();

        // Progress bar
        progressBar = new ProgressBar();
        progressBar.setValue(0.33);
        progressBar.setWidthFull();
        progressBar.getStyle().set(StyleConstants.CSS_HEIGHT, "4px");
        progressBar.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_HOVER);
        progressBar.getStyle().set(StyleConstants.CSS_MARGIN, "32px 0");

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
        logoIcon.getStyle().set(StyleConstants.CSS_WIDTH, "40px");
        logoIcon.getStyle().set(StyleConstants.CSS_HEIGHT, "40px");
        logoIcon.getStyle().set(StyleConstants.CSS_BACKGROUND,
                "linear-gradient(135deg, " + PRIMARY + " 0%, " + PRIMARY_LIGHT + " 100%)");
        logoIcon.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "12px");
        logoIcon.getStyle().set(StyleConstants.CSS_DISPLAY, "flex");
        logoIcon.getStyle().set(StyleConstants.CSS_ALIGN_ITEMS, StyleConstants.VAL_CENTER);
        logoIcon.getStyle().set(StyleConstants.CSS_JUSTIFY_CONTENT, StyleConstants.VAL_CENTER);

        Icon rocketIcon = VaadinIcon.ROCKET.create();
        rocketIcon.getStyle().set(StyleConstants.CSS_COLOR, StyleConstants.VAL_WHITE);
        rocketIcon.getStyle().set(StyleConstants.CSS_WIDTH, "20px");
        rocketIcon.getStyle().set(StyleConstants.CSS_HEIGHT, "20px");
        logoIcon.add(rocketIcon);

        Span logoText = new Span("CL Booster");
        logoText.getStyle().set(StyleConstants.CSS_FONT_SIZE, "20px");
        logoText.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        logoText.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);

        logoGroup.add(logoIcon, logoText);

        // Exit button
        Button exitBtn = new Button("Exit", VaadinIcon.CLOSE.create());
        exitBtn.getStyle().set(StyleConstants.CSS_BACKGROUND, StyleConstants.VAL_TRANSPARENT);
        exitBtn.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        exitBtn.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "500");
        exitBtn.getStyle().set(StyleConstants.CSS_BORDER, "none");
        exitBtn.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);
        exitBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(DashboardView.class)));

        header.add(logoGroup, exitBtn);
        header.expand(logoGroup);

        return header;
    }

    private HorizontalLayout createStepIndicator() {
        HorizontalLayout indicator = new HorizontalLayout();
        indicator.setWidthFull();
        indicator.getStyle().set("gap", "16px");
        indicator.getStyle().set(StyleConstants.CSS_MARGIN_TOP, "32px");

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
        badge.getStyle().set(StyleConstants.CSS_DISPLAY, "flex");
        badge.getStyle().set(StyleConstants.CSS_ALIGN_ITEMS, StyleConstants.VAL_CENTER);
        badge.getStyle().set("gap", "12px");
        badge.getStyle().set("flex", "1");

        // Number/icon circle
        Div circle = new Div();
        circle.getStyle().set(StyleConstants.CSS_WIDTH, "36px");
        circle.getStyle().set(StyleConstants.CSS_HEIGHT, "36px");
        circle.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "50%");
        circle.getStyle().set(StyleConstants.CSS_DISPLAY, "flex");
        circle.getStyle().set(StyleConstants.CSS_ALIGN_ITEMS, StyleConstants.VAL_CENTER);
        circle.getStyle().set(StyleConstants.CSS_JUSTIFY_CONTENT, StyleConstants.VAL_CENTER);
        circle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        circle.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        circle.getStyle().set(StyleConstants.CSS_TRANSITION, StyleConstants.VAL_ALL_0_3S);

        if (step < currentStep) {
            // Completed
            circle.getStyle().set(StyleConstants.CSS_BACKGROUND, SUCCESS);
            Icon check = VaadinIcon.CHECK.create();
            check.getStyle().set(StyleConstants.CSS_COLOR, StyleConstants.VAL_WHITE);
            check.getStyle().set(StyleConstants.CSS_WIDTH, "18px");
            check.getStyle().set(StyleConstants.CSS_HEIGHT, "18px");
            circle.add(check);
        } else if (step == currentStep) {
            // Current
            circle.getStyle().set(StyleConstants.CSS_BACKGROUND, PRIMARY);
            circle.getStyle().set(StyleConstants.CSS_COLOR, StyleConstants.VAL_WHITE);
            circle.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 4px 12px rgba(0,122,255,0.3)");

            Icon icon = iconType.create();
            icon.getStyle().set(StyleConstants.CSS_COLOR, StyleConstants.VAL_WHITE);
            icon.getStyle().set(StyleConstants.CSS_WIDTH, "18px");
            icon.getStyle().set(StyleConstants.CSS_HEIGHT, "18px");
            circle.add(icon);
        } else {
            // Future
            circle.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_GRAY);
            circle.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
            circle.setText(String.valueOf(step));
        }

        // Label
        Span labelSpan = new Span(label);
        labelSpan.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        labelSpan.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, step == currentStep ? "600" : "500");
        labelSpan.getStyle().set(StyleConstants.CSS_COLOR, step == currentStep ? TEXT_PRIMARY : TEXT_SECONDARY);

        badge.add(circle, labelSpan);

        return badge;
    }

    private Div createStepConnector() {
        Div connector = new Div();
        connector.getStyle().set("flex", "1");
        connector.getStyle().set(StyleConstants.CSS_HEIGHT, "2px");
        connector.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_HOVER);
        connector.getStyle().set("align-self", StyleConstants.VAL_CENTER);
        connector.getStyle().set(StyleConstants.CSS_MAX_WIDTH, "60px");
        return connector;
    }

    private HorizontalLayout createNavigation() {
        HorizontalLayout nav = new HorizontalLayout();
        nav.setWidthFull();
        nav.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        nav.setAlignItems(FlexComponent.Alignment.CENTER);
        nav.getStyle().set("padding-top", "32px");
        nav.getStyle().set("border-top", "1px solid rgba(0,0,0,0.05)");
        nav.getStyle().set(StyleConstants.CSS_MARGIN_TOP, "auto");

        // Back button
        backButton = new Button("Back", VaadinIcon.ARROW_LEFT.create());
        backButton.getStyle().set(StyleConstants.CSS_BACKGROUND, StyleConstants.VAL_TRANSPARENT);
        backButton.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        backButton.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "600");
        backButton.getStyle().set(StyleConstants.CSS_BORDER, "none");
        backButton.getStyle().set(StyleConstants.CSS_PADDING, "12px 24px");
        backButton.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);
        backButton.getStyle().set(StyleConstants.CSS_TRANSITION, StyleConstants.VAL_ALL_0_2S);
        backButton.addClickListener(e -> {
            if (currentStep > 1) {
                showStep(currentStep - 1);
            }
        });
        backButton.setVisible(false);

        backButton.getElement().addEventListener(StyleConstants.VAL_MOUSEENTER, e -> {
            backButton.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        });
        backButton.getElement().addEventListener(StyleConstants.VAL_MOUSELEAVE, e -> {
            backButton.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        });

        // Step counter
        Span stepCounter = new Span("Step " + currentStep + " of " + totalSteps);
        stepCounter.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        stepCounter.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);

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
                .filter(c -> c instanceof HorizontalLayout).reduce((first, second) -> second).orElse(null);

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
        default:
            // no action needed
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
        header.getStyle().set(StyleConstants.CSS_TEXT_ALIGN, StyleConstants.VAL_CENTER);

        H1 title = new H1("Let's get to know you");
        title.getStyle().set(StyleConstants.CSS_FONT_SIZE, "32px");
        title.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        title.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        title.getStyle().set(StyleConstants.CSS_MARGIN, "0");
        title.getStyle().set(StyleConstants.CSS_LETTER_SPACING, LETTER_SPACING);

        Paragraph subtitle = new Paragraph("Start with the basics. This helps us personalize your experience.");
        subtitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "16px");
        subtitle.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        subtitle.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        header.add(title, subtitle);

        // Form card
        Div formCard = new Div();
        formCard.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        formCard.getStyle().set(StyleConstants.CSS_BORDER, "1px solid rgba(0,0,0,0.05)");
        formCard.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "24px");
        formCard.getStyle().set(StyleConstants.CSS_PADDING, "32px");
        formCard.getStyle().set(StyleConstants.CSS_BOX_SHADOW, StyleConstants.VAL_0_2_12PX);

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
        row2.add(createFormGroup("Job Title", titleField, VaadinIcon.BRIEFCASE),
                createFormGroup("Location", locationField, VaadinIcon.MAP_MARKER));
        row2.expand(createFormGroup("Job Title", titleField, VaadinIcon.BRIEFCASE),
                createFormGroup("Location", locationField, VaadinIcon.MAP_MARKER));

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
        header.getStyle().set(StyleConstants.CSS_TEXT_ALIGN, StyleConstants.VAL_CENTER);

        H1 title = new H1("Your career background");
        title.getStyle().set(StyleConstants.CSS_FONT_SIZE, "32px");
        title.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        title.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        title.getStyle().set(StyleConstants.CSS_MARGIN, "0");
        title.getStyle().set(StyleConstants.CSS_LETTER_SPACING, LETTER_SPACING);

        Paragraph subtitle = new Paragraph("Help us understand your experience and career goals.");
        subtitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "16px");
        subtitle.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        subtitle.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        header.add(title, subtitle);

        // Industries card
        Div industriesCard = new Div();
        industriesCard.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        industriesCard.getStyle().set(StyleConstants.CSS_BORDER, "1px solid rgba(0,0,0,0.05)");
        industriesCard.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "24px");
        industriesCard.getStyle().set(StyleConstants.CSS_PADDING, "32px");
        industriesCard.getStyle().set(StyleConstants.CSS_BOX_SHADOW, StyleConstants.VAL_0_2_12PX);

        H3 industriesTitle = new H3("Industries you work in");
        industriesTitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "18px");
        industriesTitle.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        industriesTitle.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        industriesTitle.getStyle().set(StyleConstants.CSS_MARGIN, StyleConstants.VAL_0_0_16PX);

        Paragraph industriesDesc = new Paragraph("Select all that apply to your experience");
        industriesDesc.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        industriesDesc.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        industriesDesc.getStyle().set(StyleConstants.CSS_MARGIN, MARGIN_24);

        // Industry chips
        HorizontalLayout industryChips = new HorizontalLayout();
        industryChips.getStyle().set("gap", "10px");
        industryChips.getStyle().set(StyleConstants.CSS_FLEX_WRAP, "wrap");

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
        expCard.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        expCard.getStyle().set(StyleConstants.CSS_BORDER, "1px solid rgba(0,0,0,0.05)");
        expCard.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "24px");
        expCard.getStyle().set(StyleConstants.CSS_PADDING, "32px");
        expCard.getStyle().set(StyleConstants.CSS_BOX_SHADOW, StyleConstants.VAL_0_2_12PX);
        expCard.getStyle().set(StyleConstants.CSS_MARGIN_TOP, "24px");

        H3 expTitle = new H3("Experience level");
        expTitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "18px");
        expTitle.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        expTitle.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        expTitle.getStyle().set(StyleConstants.CSS_MARGIN, StyleConstants.VAL_0_0_16PX);

        Paragraph expDesc = new Paragraph("Select your current career level");
        expDesc.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        expDesc.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        expDesc.getStyle().set(StyleConstants.CSS_MARGIN, MARGIN_24);

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
        goalsCard.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        goalsCard.getStyle().set(StyleConstants.CSS_BORDER, "1px solid rgba(0,0,0,0.05)");
        goalsCard.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "24px");
        goalsCard.getStyle().set(StyleConstants.CSS_PADDING, "32px");
        goalsCard.getStyle().set(StyleConstants.CSS_BOX_SHADOW, StyleConstants.VAL_0_2_12PX);
        goalsCard.getStyle().set(StyleConstants.CSS_MARGIN_TOP, "24px");

        H3 goalsTitle = new H3("Career goals");
        goalsTitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "18px");
        goalsTitle.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        goalsTitle.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        goalsTitle.getStyle().set(StyleConstants.CSS_MARGIN, StyleConstants.VAL_0_0_16PX);

        Paragraph goalsDesc = new Paragraph("What are you looking for in your next role?");
        goalsDesc.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        goalsDesc.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        goalsDesc.getStyle().set(StyleConstants.CSS_MARGIN, MARGIN_24);

        goalsCard.add(goalsTitle, goalsDesc, createFormGroup(null, goalField, null));

        contentArea.add(header, industriesCard, expCard, goalsCard);
    }

    private void showPreferencesStep() {
        // Header
        VerticalLayout header = new VerticalLayout();
        header.setPadding(false);
        header.setSpacing(false);
        header.getStyle().set("gap", "8px");
        header.getStyle().set(StyleConstants.CSS_TEXT_ALIGN, StyleConstants.VAL_CENTER);

        H1 title = new H1("Personalize your experience");
        title.getStyle().set(StyleConstants.CSS_FONT_SIZE, "32px");
        title.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        title.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        title.getStyle().set(StyleConstants.CSS_MARGIN, "0");
        title.getStyle().set(StyleConstants.CSS_LETTER_SPACING, LETTER_SPACING);

        Paragraph subtitle = new Paragraph("Customize how CL Booster works for you.");
        subtitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "16px");
        subtitle.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        subtitle.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        header.add(title, subtitle);

        // Writing tone card
        Div toneCard = new Div();
        toneCard.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        toneCard.getStyle().set(StyleConstants.CSS_BORDER, "1px solid rgba(0,0,0,0.05)");
        toneCard.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "24px");
        toneCard.getStyle().set(StyleConstants.CSS_PADDING, "32px");
        toneCard.getStyle().set(StyleConstants.CSS_BOX_SHADOW, StyleConstants.VAL_0_2_12PX);

        H3 toneTitle = new H3("Preferred writing tone");
        toneTitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "18px");
        toneTitle.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        toneTitle.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        toneTitle.getStyle().set(StyleConstants.CSS_MARGIN, "0 0 8px 0");

        Paragraph toneDesc = new Paragraph("Choose the default tone for your cover letters");
        toneDesc.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        toneDesc.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        toneDesc.getStyle().set(StyleConstants.CSS_MARGIN, MARGIN_24);

        // Tone options
        HorizontalLayout toneOptions = new HorizontalLayout();
        toneOptions.setWidthFull();
        toneOptions.getStyle().set("gap", "16px");

        String[][] toneData = { { "Professional", "Formal and polished", PRIMARY, VaadinIcon.BRIEFCASE.name() },
                { "Creative", "Bold and unique", "#AF52DE", VaadinIcon.STAR.name() },
                { "Casual", "Friendly and approachable", WARNING, VaadinIcon.SMILEY_O.name() } };

        for (String[] tone : toneData) {
            toneOptions.add(createToneOption(tone[0], tone[1], tone[2], tone[3]));
        }

        toneCard.add(toneTitle, toneDesc, toneOptions);

        // Notifications card
        Div notifCard = new Div();
        notifCard.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
        notifCard.getStyle().set(StyleConstants.CSS_BORDER, "1px solid rgba(0,0,0,0.05)");
        notifCard.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "24px");
        notifCard.getStyle().set(StyleConstants.CSS_PADDING, "32px");
        notifCard.getStyle().set(StyleConstants.CSS_BOX_SHADOW, StyleConstants.VAL_0_2_12PX);
        notifCard.getStyle().set(StyleConstants.CSS_MARGIN_TOP, "24px");

        H3 notifTitle = new H3("Notifications");
        notifTitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "18px");
        notifTitle.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        notifTitle.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);
        notifTitle.getStyle().set(StyleConstants.CSS_MARGIN, "0 0 8px 0");

        Paragraph notifDesc = new Paragraph("Stay updated on your application progress");
        notifDesc.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        notifDesc.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        notifDesc.getStyle().set(StyleConstants.CSS_MARGIN, MARGIN_24);

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
        summaryCard.getStyle().set(StyleConstants.CSS_BACKGROUND,
                "linear-gradient(135deg, " + PRIMARY + " 0%, " + PRIMARY_LIGHT + " 100%)");
        summaryCard.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "24px");
        summaryCard.getStyle().set(StyleConstants.CSS_PADDING, "32px");
        summaryCard.getStyle().set(StyleConstants.CSS_MARGIN_TOP, "24px");
        summaryCard.getStyle().set(StyleConstants.CSS_COLOR, StyleConstants.VAL_WHITE);

        HorizontalLayout summaryHeader = new HorizontalLayout();
        summaryHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        summaryHeader.getStyle().set("gap", "16px");
        summaryHeader.getStyle().set(StyleConstants.CSS_MARGIN_BOTTOM, "16px");

        Div checkCircle = new Div();
        checkCircle.getStyle().set(StyleConstants.CSS_WIDTH, "48px");
        checkCircle.getStyle().set(StyleConstants.CSS_HEIGHT, "48px");
        checkCircle.getStyle().set(StyleConstants.CSS_BACKGROUND, "rgba(255,255,255,0.2)");
        checkCircle.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "50%");
        checkCircle.getStyle().set(StyleConstants.CSS_DISPLAY, "flex");
        checkCircle.getStyle().set(StyleConstants.CSS_ALIGN_ITEMS, StyleConstants.VAL_CENTER);
        checkCircle.getStyle().set(StyleConstants.CSS_JUSTIFY_CONTENT, StyleConstants.VAL_CENTER);

        Icon checkIcon = VaadinIcon.CHECK.create();
        checkIcon.getStyle().set(StyleConstants.CSS_COLOR, StyleConstants.VAL_WHITE);
        checkIcon.getStyle().set(StyleConstants.CSS_WIDTH, "24px");
        checkIcon.getStyle().set(StyleConstants.CSS_HEIGHT, "24px");
        checkCircle.add(checkIcon);

        VerticalLayout summaryText = new VerticalLayout();
        summaryText.setPadding(false);
        summaryText.setSpacing(false);
        summaryText.getStyle().set("gap", "4px");

        H3 summaryTitle = new H3("You're all set!");
        summaryTitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "20px");
        summaryTitle.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        summaryTitle.getStyle().set(StyleConstants.CSS_COLOR, StyleConstants.VAL_WHITE);
        summaryTitle.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        Paragraph summaryDesc = new Paragraph("Review your preferences and complete setup");
        summaryDesc.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        summaryDesc.getStyle().set(StyleConstants.CSS_COLOR, "rgba(255,255,255,0.8)");
        summaryDesc.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        summaryText.add(summaryTitle, summaryDesc);
        summaryHeader.add(checkCircle, summaryText);

        // Quick stats
        HorizontalLayout stats = new HorizontalLayout();
        stats.setWidthFull();
        stats.getStyle().set("gap", "24px");
        stats.getStyle().set("padding-top", "16px");
        stats.getStyle().set("border-top", "1px solid rgba(255,255,255,0.2)");

        stats.add(createSummaryStat("Profile", "Completed"));
        stats.add(createSummaryStat("Career",
                selectedExperience.isEmpty() ? "Pending" : selectedExperience.get(0).split(" ")[0]));
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
                iconComponent.getStyle().set(StyleConstants.CSS_WIDTH, "14px");
                iconComponent.getStyle().set(StyleConstants.CSS_HEIGHT, "14px");
                iconComponent.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
                labelRow.add(iconComponent);
            }

            Span labelText = new Span(label);
            labelText.getStyle().set(StyleConstants.CSS_FONT_SIZE, "12px");
            labelText.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
            labelText.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
            labelText.getStyle().set(StyleConstants.CSS_TEXT_TRANSFORM, "uppercase");
            labelText.getStyle().set(StyleConstants.CSS_LETTER_SPACING, "0.05em");

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
        chip.getStyle().set(StyleConstants.CSS_PADDING, "10px 18px");
        chip.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX);
        chip.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, selected ? "600" : "500");
        chip.getStyle().set(StyleConstants.CSS_FONT_SIZE, "13px");
        chip.getStyle().set(StyleConstants.CSS_BORDER, selected ? "1px solid " + PRIMARY : "1px solid rgba(0,0,0,0.1)");
        chip.getStyle().set(StyleConstants.CSS_BACKGROUND, selected ? "rgba(0,122,255,0.1)" : BG_WHITE);
        chip.getStyle().set(StyleConstants.CSS_COLOR, selected ? PRIMARY : TEXT_PRIMARY);
        chip.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);
        chip.getStyle().set(StyleConstants.CSS_TRANSITION, StyleConstants.VAL_ALL_0_2S);

        chip.getElement().addEventListener(StyleConstants.VAL_MOUSEENTER, e -> {
            if (!selected) {
                chip.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_HOVER);
            }
        });
        chip.getElement().addEventListener(StyleConstants.VAL_MOUSELEAVE, e -> {
            if (!selected) {
                chip.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_WHITE);
            }
        });

        return chip;
    }

    private Div createRadioOption(String label, boolean selected, Runnable onClick) {
        Div option = new Div();
        option.getStyle().set(StyleConstants.CSS_DISPLAY, "flex");
        option.getStyle().set(StyleConstants.CSS_ALIGN_ITEMS, StyleConstants.VAL_CENTER);
        option.getStyle().set("gap", "16px");
        option.getStyle().set(StyleConstants.CSS_PADDING, "16px 20px");
        option.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "12px");
        option.getStyle().set(StyleConstants.CSS_BACKGROUND, selected ? "rgba(0,122,255,0.05)" : BG_GRAY);
        option.getStyle().set(StyleConstants.CSS_BORDER, selected ? BORDER_2PX + PRIMARY : "2px solid transparent");
        option.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);
        option.getStyle().set(StyleConstants.CSS_TRANSITION, StyleConstants.VAL_ALL_0_2S);

        // Radio circle
        Div radio = new Div();
        radio.getStyle().set(StyleConstants.CSS_WIDTH, "20px");
        radio.getStyle().set(StyleConstants.CSS_HEIGHT, "20px");
        radio.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "50%");
        radio.getStyle().set(StyleConstants.CSS_BORDER, selected ? "6px solid " + PRIMARY : BORDER_2PX + TEXT_SECONDARY);
        radio.getStyle().set(StyleConstants.CSS_TRANSITION, StyleConstants.VAL_ALL_0_2S);

        Span labelSpan = new Span(label);
        labelSpan.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        labelSpan.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, selected ? "600" : "500");
        labelSpan.getStyle().set(StyleConstants.CSS_COLOR, selected ? TEXT_PRIMARY : TEXT_SECONDARY);

        option.add(radio, labelSpan);

        option.getElement().addEventListener("click", e -> onClick.run());

        option.getElement().addEventListener(StyleConstants.VAL_MOUSEENTER, e -> {
            if (!selected) {
                option.getStyle().set(StyleConstants.CSS_BACKGROUND, "rgba(0,0,0,0.06)");
            }
        });
        option.getElement().addEventListener(StyleConstants.VAL_MOUSELEAVE, e -> {
            if (!selected) {
                option.getStyle().set(StyleConstants.CSS_BACKGROUND, BG_GRAY);
            }
        });

        return option;
    }

    private Div createToneOption(String title, String description, String color, String iconName) {
        boolean selected = selectedTone.equals(title);

        Div card = new Div();
        card.getStyle().set("flex", "1");
        card.getStyle().set(StyleConstants.CSS_BACKGROUND, selected ? "rgba(0,122,255,0.05)" : BG_GRAY);
        card.getStyle().set(StyleConstants.CSS_BORDER, selected ? BORDER_2PX + color : "2px solid transparent");
        card.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "16px");
        card.getStyle().set(StyleConstants.CSS_PADDING, "24px");
        card.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);
        card.getStyle().set(StyleConstants.CSS_TRANSITION, StyleConstants.VAL_ALL_0_3S);
        card.getStyle().set(StyleConstants.CSS_TEXT_ALIGN, StyleConstants.VAL_CENTER);

        if (selected) {
            card.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 10px 20px rgba(0,122,255,0.1)");
        }

        Div iconContainer = new Div();
        iconContainer.getStyle().set(StyleConstants.CSS_WIDTH, "48px");
        iconContainer.getStyle().set(StyleConstants.CSS_HEIGHT, "48px");
        iconContainer.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "50%");
        iconContainer.getStyle().set(StyleConstants.CSS_BACKGROUND, color + "20");
        iconContainer.getStyle().set(StyleConstants.CSS_DISPLAY, "flex");
        iconContainer.getStyle().set(StyleConstants.CSS_ALIGN_ITEMS, StyleConstants.VAL_CENTER);
        iconContainer.getStyle().set(StyleConstants.CSS_JUSTIFY_CONTENT, StyleConstants.VAL_CENTER);
        iconContainer.getStyle().set(StyleConstants.CSS_MARGIN, "0 auto 16px");

        Icon icon = VaadinIcon.valueOf(iconName).create();
        icon.getStyle().set(StyleConstants.CSS_COLOR, color);
        icon.getStyle().set(StyleConstants.CSS_WIDTH, "24px");
        icon.getStyle().set(StyleConstants.CSS_HEIGHT, "24px");
        iconContainer.add(icon);

        H3 cardTitle = new H3(title);
        cardTitle.getStyle().set(StyleConstants.CSS_FONT_SIZE, "16px");
        cardTitle.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, selected ? "700" : "600");
        cardTitle.getStyle().set(StyleConstants.CSS_COLOR, selected ? color : TEXT_PRIMARY);
        cardTitle.getStyle().set(StyleConstants.CSS_MARGIN, "0 0 4px 0");

        Paragraph cardDesc = new Paragraph(description);
        cardDesc.getStyle().set(StyleConstants.CSS_FONT_SIZE, "13px");
        cardDesc.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);
        cardDesc.getStyle().set(StyleConstants.CSS_MARGIN, "0");

        card.add(iconContainer, cardTitle, cardDesc);

        card.getElement().addEventListener("click", e -> {
            selectedTone = title;
            showStep(3);
        });

        card.getElement().addEventListener(StyleConstants.VAL_MOUSEENTER, e -> {
            if (!selected) {
                card.getStyle().set(StyleConstants.CSS_BORDER_COLOR, color + "50");
            }
        });
        card.getElement().addEventListener(StyleConstants.VAL_MOUSELEAVE, e -> {
            if (!selected) {
                card.getStyle().set(StyleConstants.CSS_BORDER_COLOR, StyleConstants.VAL_TRANSPARENT);
            }
        });

        return card;
    }

    private HorizontalLayout createToggleRow(String title, String description, boolean enabled) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.getStyle().set(StyleConstants.CSS_PADDING, "8px 0");

        VerticalLayout textGroup = new VerticalLayout();
        textGroup.setPadding(false);
        textGroup.setSpacing(false);
        textGroup.getStyle().set("gap", "4px");

        Span titleSpan = new Span(title);
        titleSpan.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        titleSpan.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "600");
        titleSpan.getStyle().set(StyleConstants.CSS_COLOR, TEXT_PRIMARY);

        Span descSpan = new Span(description);
        descSpan.getStyle().set(StyleConstants.CSS_FONT_SIZE, "13px");
        descSpan.getStyle().set(StyleConstants.CSS_COLOR, TEXT_SECONDARY);

        textGroup.add(titleSpan, descSpan);

        Div toggle = createToggleSwitch(enabled);

        row.add(textGroup, toggle);
        row.expand(textGroup);

        return row;
    }

    private Div createToggleSwitch(boolean enabled) {
        Div track = new Div();
        track.getStyle().set(StyleConstants.CSS_WIDTH, "48px");
        track.getStyle().set(StyleConstants.CSS_HEIGHT, "28px");
        track.getStyle().set(StyleConstants.CSS_BACKGROUND, enabled ? SUCCESS : "rgba(0,0,0,0.2)");
        track.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX);
        track.getStyle().set("position", "relative");
        track.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);
        track.getStyle().set(StyleConstants.CSS_TRANSITION, "background 0.2s");

        Div thumb = new Div();
        thumb.getStyle().set(StyleConstants.CSS_WIDTH, "24px");
        thumb.getStyle().set(StyleConstants.CSS_HEIGHT, "24px");
        thumb.getStyle().set(StyleConstants.CSS_BACKGROUND, StyleConstants.VAL_WHITE);
        thumb.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, "50%");
        thumb.getStyle().set("position", "absolute");
        thumb.getStyle().set("top", "2px");
        thumb.getStyle().set(enabled ? "right" : "left", "2px");
        thumb.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 2px 4px rgba(0,0,0,0.2)");
        thumb.getStyle().set(StyleConstants.CSS_TRANSITION, StyleConstants.VAL_ALL_0_2S);

        track.add(thumb);

        return track;
    }

    private VerticalLayout createSummaryStat(String label, String value) {
        VerticalLayout stat = new VerticalLayout();
        stat.setPadding(false);
        stat.setSpacing(false);
        stat.getStyle().set("gap", "4px");

        Span valueSpan = new Span(value);
        valueSpan.getStyle().set(StyleConstants.CSS_FONT_SIZE, "16px");
        valueSpan.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "700");
        valueSpan.getStyle().set(StyleConstants.CSS_COLOR, StyleConstants.VAL_WHITE);

        Span labelSpan = new Span(label);
        labelSpan.getStyle().set(StyleConstants.CSS_FONT_SIZE, "12px");
        labelSpan.getStyle().set(StyleConstants.CSS_COLOR, "rgba(255,255,255,0.7)");

        stat.add(valueSpan, labelSpan);

        return stat;
    }

    private void handleNext() {
        if (currentStep < totalSteps) {
            showStep(currentStep + 1);
        } else {
            // Complete onboarding
            Notification.show("Welcome to CL Booster! Your profile is set up.", 4000,
                    Notification.Position.BOTTOM_CENTER);
            getUI().ifPresent(ui -> ui.navigate(DashboardView.class));
        }
    }

    private Button createPrimaryButton(String text, Runnable action) {
        Button btn = new Button(text, e -> action.run());
        btn.getStyle().set(StyleConstants.CSS_BACKGROUND, "linear-gradient(135deg, " + PRIMARY + " 0%, " + PRIMARY_LIGHT + " 100%)");
        btn.getStyle().set(StyleConstants.CSS_COLOR, StyleConstants.VAL_WHITE);
        btn.getStyle().set(StyleConstants.CSS_FONT_WEIGHT, "600");
        btn.getStyle().set(StyleConstants.CSS_FONT_SIZE, "14px");
        btn.getStyle().set(StyleConstants.CSS_BORDER_RADIUS, StyleConstants.VAL_9999PX);
        btn.getStyle().set(StyleConstants.CSS_BORDER, "none");
        btn.getStyle().set(StyleConstants.CSS_PADDING, "12px 28px");
        btn.getStyle().set(StyleConstants.CSS_BOX_SHADOW, "0 10px 15px -3px rgba(0,122,255,0.3)");
        btn.getStyle().set(StyleConstants.CSS_TRANSITION, StyleConstants.VAL_ALL_0_2S);
        btn.getStyle().set(StyleConstants.CSS_CURSOR, StyleConstants.VAL_POINTER);

        btn.getElement().addEventListener(StyleConstants.VAL_MOUSEENTER, e -> {
            btn.getStyle().set("filter", "brightness(1.1)");
            btn.getStyle().set(StyleConstants.CSS_TRANSFORM, "translateY(-1px)");
        });
        btn.getElement().addEventListener(StyleConstants.VAL_MOUSELEAVE, e -> {
            btn.getStyle().set("filter", "brightness(1)");
            btn.getStyle().set(StyleConstants.CSS_TRANSFORM, "translateY(0)");
        });

        return btn;
    }
}
