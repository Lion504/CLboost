@Route(value = "generator", layout = MainLayout.class)
@PageTitle("Generator | CL Booster")
public class GeneratorView extends VerticalLayout {

    private final AIService aiService;
    private final DocumentService documentService;
    private int step = 1;

    // Step 1 fields
    private TextField jobTitle = new TextField("Job Title");
    private TextField companyName = new TextField("Company Name");
    private TextField jobUrl = new TextField("Job URL (Optional)");
    private TextField location = new TextField("Location");
    private TextArea jobDesc = new TextArea("Job Description / Ad Content");

    public GeneratorView(AIService aiService, DocumentService documentService) {
        this.aiService = aiService;
        this.documentService = documentService;
        setPadding(true);
        setSpacing(true);
        showStep1();
    }

    private Component createStepBar(int currentStep) {
        HorizontalLayout bar = new HorizontalLayout();
        bar.setSpacing(true);
        bar.setAlignItems(FlexComponent.Alignment.CENTER);
        for (int i = 1; i <= 3; i++) {
            String[] labels = {"", "1. Input", "2. Review", "3. Result"};
            Span badge = new Span(labels[i]);
            badge.getElement().getThemeList().add(
                i == currentStep ? "badge primary" : "badge contrast");
            bar.add(badge);
            if (i < 3) bar.add(new Span("→"));
        }
        return bar;
    }

    private void showStep1() {
        removeAll();
        add(createStepBar(1));

        H2 title = new H2("New Cover Letter");
        Paragraph sub = new Paragraph("Paste the job details below and let AI handle the heavy lifting.");

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("600px", 2));
        jobTitle.setPlaceholder("e.g. Growth Product Manager");
        companyName.setPlaceholder("e.g. NordicFin");
        jobUrl.setPlaceholder("job-site.com/jobs/...");
        location.setPlaceholder("Helsinki (Hybrid)");
        jobDesc.setPlaceholder("Paste the full job description here...");
        jobDesc.setMinHeight("130px");
        form.add(jobTitle, companyName, jobUrl, location, jobDesc);
        form.setColspan(jobDesc, 2);

        Button next = new Button("Continue →", e -> showStep2());
        next.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);

        add(title, sub, form, next);
    }

    private void showStep2() {
        removeAll();
        add(createStepBar(2));
        add(new H2("Review Your Input"));
        // Show summary of entered data
        Div summary = new Div();
        summary.addClassName("cl-card");
        summary.add(new Paragraph("Position: " + jobTitle.getValue()));
        summary.add(new Paragraph("Company: " + companyName.getValue()));
        summary.add(new Paragraph("Location: " + location.getValue()));
        add(summary);

        Button back = new Button("← Back", e -> showStep1());
        back.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button generate = new Button("Generate Cover Letter", e -> showStep3());
        generate.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        add(new HorizontalLayout(back, generate));
    }

    private void showStep3() {
        removeAll();
        add(createStepBar(3));
        add(new H2("Your Cover Letter"));

        // Call existing AIService
        Div resultCard = new Div();
        resultCard.addClassName("cl-card");
        resultCard.setWidthFull();

        Paragraph loadingMsg = new Paragraph("Generating your cover letter...");
        resultCard.add(loadingMsg);
        add(resultCard);

        // Trigger AI generation using the already-injected aiService
        // (AIService already exists in com.clbooster.aiservice[screenshot:1])
        try {
            String prompt = buildPrompt();
            String result = aiService.generateCoverLetter(prompt);

            resultCard.removeAll();
            TextArea output = new TextArea();
            output.setValue(result);
            output.setWidthFull();
            output.setMinHeight("400px");
            output.setReadOnly(false); // allow user to edit

            Button copyBtn = new Button("Copy to Clipboard",
                e -> output.getElement().executeJs(
                    "navigator.clipboard.writeText($0)", output.getValue()));
            copyBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            Button exportBtn = new Button("Export as .docx",
                e -> Notification.show("Export coming soon"));
            exportBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            Button startOver = new Button("Start New",
                e -> { step = 1; showStep1(); });
            startOver.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            resultCard.add(output);
            add(resultCard, new HorizontalLayout(startOver, copyBtn, exportBtn));

        } catch (Exception ex) {
            resultCard.removeAll();
            resultCard.add(new Paragraph("Error: " + ex.getMessage()));
            Button retry = new Button("← Back", e -> showStep2());
            retry.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            add(retry);
        }
    }

    private String buildPrompt() {
        return String.format(
            "Write a professional cover letter for the position of %s at %s. " +
            "Location: %s. " +
            "Job description: %s. " +
            "Keep it concise, ATS-friendly, and use a confident tone.",
            jobTitle.getValue(),
            companyName.getValue(),
            location.getValue(),
            jobDesc.getValue()
        );
    }
}
