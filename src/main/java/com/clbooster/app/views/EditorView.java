package com.clbooster.app.views;

import com.vaadin.flow.theme.lumo.LumoUtility;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

/**
 * Editor View - Cover letter editor with split layout
 * Rich text editor on the left, AI suggestions sidebar on the right
 * Following Apple Design System
 */
@Route(value = "editor", layout = MainLayout.class)
@PageTitle("Editor | CL Booster")
public class EditorView extends HorizontalLayout {

    // Figma Design System Colors
    private static final String PRIMARY = "#007AFF";
    private static final String TEXT_PRIMARY = "#1d1d1f";
    private static final String TEXT_SECONDARY = "#86868b";
    private static final String BG_WHITE = "#ffffff";
    private static final String BG_GRAY = "#f5f5f7";
    private static final String BG_DARK = "#1d1d1f";

    private TextArea editorArea;

    public EditorView() {
        setSizeFull();
        setPadding(true);
        getStyle().set("gap", "24px");
        getStyle().set("background", BG_WHITE);
        getStyle().set("padding", "24px");

        // Left side - Editor
        VerticalLayout editorPanel = createEditorPanel();
        editorPanel.setWidth("65%");

        // Right side - AI Sidebar
        VerticalLayout sidebarPanel = createSidebarPanel();
        sidebarPanel.setWidth("35%");
        sidebarPanel.setMaxWidth("400px");

        add(editorPanel, sidebarPanel);
        expand(editorPanel);
    }

    private VerticalLayout createEditorPanel() {
        VerticalLayout panel = new VerticalLayout();
        panel.getStyle().set("gap", "16px");
        panel.setHeightFull();

        // Header
        HorizontalLayout header = createEditorHeader();

        // Toolbar
        HorizontalLayout toolbar = createToolbar();

        // Editor area
        editorArea = new TextArea();
        editorArea.setValue(getSampleCoverLetter());
        editorArea.setWidthFull();
        editorArea.setHeightFull();
        editorArea.getStyle().set("background", BG_WHITE);
        editorArea.getStyle().set("border", "1px solid rgba(0,0,0,0.1)");
        editorArea.getStyle().set("border-radius", "16px");
        editorArea.getStyle().set("padding", "32px");
        editorArea.getStyle().set("font-size", "15px");
        editorArea.getStyle().set("line-height", "1.8");
        editorArea.getStyle().set("color", TEXT_PRIMARY);
        editorArea.getStyle().set("font-family", "-apple-system, BlinkMacSystemFont, 'SF Pro Text', sans-serif");
        editorArea.getStyle().set("resize", "none");

        panel.add(header, toolbar, editorArea);
        panel.expand(editorArea);
panel.setSpacing(false);
panel.addClassNames(LumoUtility.Padding.MEDIUM);

        return panel;
    }

    private HorizontalLayout createEditorHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle().set("padding", "0 0 8px 0");

        // Back button and title
        HorizontalLayout leftGroup = new HorizontalLayout();
        leftGroup.setAlignItems(FlexComponent.Alignment.CENTER);
        leftGroup.getStyle().set("gap", "16px");

        Button backBtn = new Button(VaadinIcon.ARROW_LEFT.create());
        backBtn.getStyle().set("background", "transparent");
        backBtn.getStyle().set("color", TEXT_SECONDARY);
        backBtn.getStyle().set("border", "none");
        backBtn.getStyle().set("cursor", "pointer");
        backBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(DashboardView.class)));

        VerticalLayout titleGroup = new VerticalLayout();
        titleGroup.setPadding(false);
        titleGroup.setSpacing(false);
        titleGroup.getStyle().set("gap", "4px");

        H2 title = new H2("Senior Product Designer");
        title.getStyle().set("font-size", "20px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0");

        Paragraph subtitle = new Paragraph("Apple Inc. • Last edited 2 min ago");
        subtitle.getStyle().set("font-size", "13px");
        subtitle.getStyle().set("color", TEXT_SECONDARY);
        subtitle.getStyle().set("margin", "0");

        titleGroup.add(title, subtitle);
        leftGroup.add(backBtn, titleGroup);

        // Action buttons
        HorizontalLayout actions = new HorizontalLayout();
        actions.setAlignItems(FlexComponent.Alignment.CENTER);
        actions.getStyle().set("gap", "12px");

        Button saveBtn = new Button("Save", VaadinIcon.DOWNLOAD.create());
        saveBtn.getStyle().set("background", "rgba(0,0,0,0.05)");
        saveBtn.getStyle().set("color", TEXT_PRIMARY);
        saveBtn.getStyle().set("font-weight", "600");
        saveBtn.getStyle().set("border-radius", "9999px");
        saveBtn.getStyle().set("padding", "10px 20px");
        saveBtn.addClickListener(e -> Notification.show("Document saved!", 3000, Notification.Position.TOP_CENTER));

        Button exportBtn = createPrimaryButton("Export PDF", VaadinIcon.FILE_TEXT);
        exportBtn.addClickListener(e -> Notification.show("Exporting PDF...", 3000, Notification.Position.TOP_CENTER));

        actions.add(saveBtn, exportBtn);

        header.add(leftGroup, actions);
        header.expand(leftGroup);

        return header;
    }

    private HorizontalLayout createToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidthFull();
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.getStyle().set("gap", "8px");
        toolbar.getStyle().set("padding", "12px 16px");
        toolbar.getStyle().set("background", BG_GRAY);
        toolbar.getStyle().set("border-radius", "12px");
        toolbar.getStyle().set("margin-bottom", "8px");

        // Format buttons
        String[] tools = {"bold", "italic", "underline", "list"};
        VaadinIcon[] icons = {VaadinIcon.BOLD, VaadinIcon.ITALIC, VaadinIcon.UNDERLINE, VaadinIcon.LIST};

        for (int i = 0; i < tools.length; i++) {
            Button toolBtn = new Button(icons[i].create());
            toolBtn.getStyle().set("background", "transparent");
            toolBtn.getStyle().set("color", TEXT_SECONDARY);
            toolBtn.getStyle().set("border", "none");
            toolBtn.getStyle().set("padding", "8px");
            toolBtn.getStyle().set("border-radius", "8px");
            toolBtn.getStyle().set("cursor", "pointer");
            toolBtn.getElement().addEventListener("mouseenter", e -> toolBtn.getStyle().set("background", "rgba(0,0,0,0.05)"));
            toolBtn.getElement().addEventListener("mouseleave", e -> toolBtn.getStyle().set("background", "transparent"));
            toolbar.add(toolBtn);
        }

        // Divider
        Div divider = new Div();
        divider.getStyle().set("width", "1px");
        divider.getStyle().set("height", "24px");
        divider.getStyle().set("background", "rgba(0,0,0,0.1)");
        toolbar.add(divider);

        // AI Assist button
        Button aiBtn = new Button("AI Assist", VaadinIcon.MAGIC.create());
        aiBtn.getStyle().set("background", PRIMARY + "15");
        aiBtn.getStyle().set("color", PRIMARY);
        aiBtn.getStyle().set("font-weight", "600");
        aiBtn.getStyle().set("border-radius", "9999px");
        aiBtn.getStyle().set("padding", "8px 16px");
        aiBtn.getStyle().set("border", "none");
        aiBtn.getStyle().set("cursor", "pointer");
        toolbar.add(aiBtn);

        return toolbar;
    }

    private VerticalLayout createSidebarPanel() {
        VerticalLayout panel = new VerticalLayout();
panel.getStyle().set("gap", "20px").setWidth("");
        panel.setHeightFull();

        // AI Suggestions Card
        Div suggestionsCard = createSuggestionsCard();

        // Score Card
        Div scoreCard = createScoreCard();

        // Tips Card
        Div tipsCard = createTipsCard();

        panel.add(suggestionsCard, scoreCard, tipsCard);
panel.addClassNames(LumoUtility.Padding.Top.MEDIUM, LumoUtility.Padding.Right.MEDIUM, LumoUtility.Padding.Bottom.MEDIUM, LumoUtility.Padding.Left.MEDIUM);
panel.setSpacing(false);
panel.addClickListener(e -> {
});

        return panel;
    }

    private Div createSuggestionsCard() {
        Div card = new Div();
        card.getStyle().set("background", BG_DARK);
        card.getStyle().set("border-radius", "24px");
        card.getStyle().set("padding", "24px");
        card.getStyle().set("color", "white");

        // Header
        HorizontalLayout header = new HorizontalLayout();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle().set("gap", "12px");
        header.getStyle().set("margin-bottom", "20px");

        Div iconContainer = new Div();
        iconContainer.getStyle().set("width", "40px");
        iconContainer.getStyle().set("height", "40px");
        iconContainer.getStyle().set("background", "rgba(255,255,255,0.1)");
        iconContainer.getStyle().set("border-radius", "12px");
        iconContainer.getStyle().set("display", "flex");
        iconContainer.getStyle().set("align-items", "center");
        iconContainer.getStyle().set("justify-content", "center");

        Icon sparkIcon = VaadinIcon.SPARK_LINE.create();
        sparkIcon.getStyle().set("color", "#FFD700");
        iconContainer.add(sparkIcon);

        VerticalLayout titleGroup = new VerticalLayout();
        titleGroup.setPadding(false);
        titleGroup.setSpacing(false);

        H3 title = new H3("AI Suggestions");
        title.getStyle().set("font-size", "16px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", "white");
        title.getStyle().set("margin", "0");

        Paragraph subtitle = new Paragraph("3 improvements found");
        subtitle.getStyle().set("font-size", "13px");
        subtitle.getStyle().set("color", "rgba(255,255,255,0.6)");
        subtitle.getStyle().set("margin", "0");

        titleGroup.add(title, subtitle);
        header.add(iconContainer, titleGroup);

        card.add(header);

        // Suggestion items
        List<String[]> suggestions = List.of(
            new String[]{"Add specific metrics", "Include quantifiable achievements to strengthen impact"},
            new String[]{"Highlight leadership", "Mention team size or projects led"},
            new String[]{"Match keywords", "Add 'design system' from job description"}
        );

        for (String[] suggestion : suggestions) {
            Div item = createSuggestionItem(suggestion[0], suggestion[1]);
            card.add(item);
        }

        return card;
    }

    private Div createSuggestionItem(String title, String description) {
        Div item = new Div();
        item.getStyle().set("background", "rgba(255,255,255,0.05)");
        item.getStyle().set("border-radius", "12px");
        item.getStyle().set("padding", "16px");
        item.getStyle().set("margin-bottom", "12px");
        item.getStyle().set("cursor", "pointer");
        item.getStyle().set("transition", "all 0.2s");
        item.getElement().addEventListener("mouseenter", e -> item.getStyle().set("background", "rgba(255,255,255,0.1)"));
        item.getElement().addEventListener("mouseleave", e -> item.getStyle().set("background", "rgba(255,255,255,0.05)"));

        HorizontalLayout row = new HorizontalLayout();
        row.setAlignItems(FlexComponent.Alignment.START);
        row.getStyle().set("gap", "12px");

        Icon lightbulb = VaadinIcon.LIGHTBULB.create();
        lightbulb.getStyle().set("color", PRIMARY);
        lightbulb.getStyle().set("width", "18px");
        lightbulb.getStyle().set("margin-top", "2px");

        VerticalLayout textGroup = new VerticalLayout();
        textGroup.setPadding(false);
        textGroup.setSpacing(false);
        textGroup.getStyle().set("gap", "4px");

        Span titleSpan = new Span(title);
        titleSpan.getStyle().set("font-size", "14px");
        titleSpan.getStyle().set("font-weight", "600");
        titleSpan.getStyle().set("color", "white");

        Span descSpan = new Span(description);
        descSpan.getStyle().set("font-size", "12px");
        descSpan.getStyle().set("color", "rgba(255,255,255,0.6)");
        descSpan.getStyle().set("line-height", "1.4");

        textGroup.add(titleSpan, descSpan);
        row.add(lightbulb, textGroup);

        item.add(row);

        item.addClickListener(e -> Notification.show("Applying: " + title, 3000, Notification.Position.TOP_CENTER));

        return item;
    }

    private Div createScoreCard() {
        Div card = new Div();
        card.getStyle().set("background", BG_WHITE);
        card.getStyle().set("border", "1px solid rgba(0,0,0,0.05)");
        card.getStyle().set("border-radius", "24px");
        card.getStyle().set("padding", "24px");

        HorizontalLayout header = new HorizontalLayout();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle().set("gap", "12px");
        header.getStyle().set("margin-bottom", "16px");

        Icon chartIcon = VaadinIcon.CHART.create();
        chartIcon.getStyle().set("color", PRIMARY);

        H3 title = new H3("Match Score");
        title.getStyle().set("font-size", "16px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0");

        header.add(chartIcon, title);

        // Score display
        HorizontalLayout scoreRow = new HorizontalLayout();
        scoreRow.setAlignItems(FlexComponent.Alignment.END);
        scoreRow.getStyle().set("gap", "8px");
        scoreRow.getStyle().set("margin-bottom", "16px");

        Span scoreValue = new Span("94");
        scoreValue.getStyle().set("font-size", "48px");
        scoreValue.getStyle().set("font-weight", "800");
        scoreValue.getStyle().set("color", PRIMARY);
        scoreValue.getStyle().set("line-height", "1");

        Span scorePercent = new Span("%");
        scorePercent.getStyle().set("font-size", "24px");
        scorePercent.getStyle().set("font-weight", "600");
        scorePercent.getStyle().set("color", TEXT_SECONDARY);
        scorePercent.getStyle().set("margin-bottom", "8px");

        scoreRow.add(scoreValue, scorePercent);

        // Progress bar
        Div progressBg = new Div();
        progressBg.getStyle().set("width", "100%");
        progressBg.getStyle().set("height", "8px");
        progressBg.getStyle().set("background", "rgba(0,0,0,0.05)");
        progressBg.getStyle().set("border-radius", "4px");
        progressBg.getStyle().set("overflow", "hidden");

        Div progressFill = new Div();
        progressFill.getStyle().set("width", "94%");
        progressFill.getStyle().set("height", "100%");
        progressFill.getStyle().set("background", "linear-gradient(90deg, " + PRIMARY + " 0%, #5AC8FA 100%)");
        progressFill.getStyle().set("border-radius", "4px");
        progressBg.add(progressFill);

        card.add(header, scoreRow, progressBg);

        return card;
    }

    private Div createTipsCard() {
        Div card = new Div();
        card.getStyle().set("background", BG_GRAY);
        card.getStyle().set("border-radius", "24px");
        card.getStyle().set("padding", "24px");

        H3 title = new H3("Writing Tips");
        title.getStyle().set("font-size", "16px");
        title.getStyle().set("font-weight", "700");
        title.getStyle().set("color", TEXT_PRIMARY);
        title.getStyle().set("margin", "0 0 16px 0");

        card.add(title);

        String[] tips = {
            "Keep it under 400 words",
            "Use active voice",
            "Match the company's tone",
            "Proofread twice"
        };

        for (String tip : tips) {
            HorizontalLayout tipRow = new HorizontalLayout();
            tipRow.setAlignItems(FlexComponent.Alignment.CENTER);
            tipRow.getStyle().set("gap", "10px");
            tipRow.getStyle().set("margin-bottom", "10px");

            Icon check = VaadinIcon.CHECK.create();
            check.getStyle().set("color", "#34C759");
            check.getStyle().set("width", "16px");

            Span tipText = new Span(tip);
            tipText.getStyle().set("font-size", "13px");
            tipText.getStyle().set("color", TEXT_SECONDARY);

            tipRow.add(check, tipText);
            card.add(tipRow);
        }

        return card;
    }

    private Button createPrimaryButton(String text, VaadinIcon icon) {
        Button btn = new Button(text, icon.create());
        btn.getStyle().set("background", PRIMARY);
        btn.getStyle().set("color", "white");
        btn.getStyle().set("font-weight", "600");
        btn.getStyle().set("border-radius", "9999px");
        btn.getStyle().set("padding", "10px 24px");
        btn.getStyle().set("border", "none");
        btn.getStyle().set("box-shadow", "0 10px 15px -3px rgba(0,122,255,0.3)");
        btn.getStyle().set("cursor", "pointer");
        return btn;
    }

    private String getSampleCoverLetter() {
        return "Dear Hiring Manager,\n\n" +
               "I am writing to express my strong interest in the Senior Product Designer position at Apple Inc. " +
               "With over 8 years of experience crafting user-centered digital experiences and a proven track record " +
               "of leading design teams, I am excited about the opportunity to contribute to Apple's world-class design organization.\n\n" +
               "At my current role, I led the redesign of our core product that resulted in a 40% increase in user engagement " +
               "and a 25% reduction in support tickets. I specialize in building scalable design systems and fostering " +
               "collaboration between design, engineering, and product teams.\n\n" +
               "What draws me to Apple is your unwavering commitment to human-centered design and innovation. " +
               "I am particularly inspired by the seamless integration of hardware and software in your products, " +
               "and I would welcome the chance to contribute to that legacy.\n\n" +
               "Thank you for considering my application. I look forward to discussing how my skills and passion " +
               "align with Apple's design vision.\n\n" +
               "Best regards,\n" +
               "Alex Rivera";
    }
}
