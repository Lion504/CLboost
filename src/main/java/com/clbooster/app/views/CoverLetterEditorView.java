package com.clbooster.app.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.WildcardParameter;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

@Route(value = "editor", layout = MainLayout.class)
@PageTitle("Edit Cover Letter | CL Booster")
public class CoverLetterEditorView extends VerticalLayout implements HasUrlParameter<String> {

    private static final String PRIMARY = "#007AFF";
    private static final String TEXT_PRIMARY = "#1d1d1f";
    private static final String TEXT_SECONDARY = "#86868b";
    private static final String BG_WHITE = "#ffffff";
    private static final String BG_GRAY = "#f5f5f7";

    private static final Logger LOGGER = Logger.getLogger(CoverLetterEditorView.class.getName());

    private TextArea editor;
    private File currentFile;
    private H2 titleLabel;

    public CoverLetterEditorView() {
        setPadding(true);
        setSpacing(false);
        getStyle()
            .set("gap", "24px").set("padding", "32px")
            .set("background", BG_WHITE)
            .set("font-family", "-apple-system, BlinkMacSystemFont, 'SF Pro Text', system-ui, sans-serif");
        setSizeFull();
    }

    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
        String filePath = URLDecoder.decode(parameter, StandardCharsets.UTF_8);
        currentFile = new File(filePath);
        buildUI();
        loadFileContent();
    }

    private void buildUI() {
        removeAll();

        // Header
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle().set("gap", "16px");

        Button backBtn = new Button("Back to History", VaadinIcon.ARROW_LEFT.create());
        backBtn.getStyle()
            .set("background", BG_GRAY).set("color", TEXT_PRIMARY)
            .set("font-weight", "600").set("border-radius", "9999px")
            .set("padding", "10px 20px").set("border", "none");
        backBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("history")));

        VerticalLayout titleGroup = new VerticalLayout();
        titleGroup.setPadding(false);
        titleGroup.setSpacing(false);
        titleGroup.getStyle().set("gap", "4px");

        titleLabel = new H2(currentFile != null ? currentFile.getName() : "Cover Letter");
        titleLabel.getStyle().set("font-size", "24px").set("font-weight", "700")
            .set("color", TEXT_PRIMARY).set("margin", "0");

        Paragraph subtitle = new Paragraph("Edit your cover letter below and save when done.");
        subtitle.getStyle().set("font-size", "14px").set("color", TEXT_SECONDARY).set("margin", "0");
        titleGroup.add(titleLabel, subtitle);

        Button saveBtn = new Button("Save Changes", VaadinIcon.CHECK.create());
        saveBtn.getStyle()
            .set("background", PRIMARY).set("color", "white")
            .set("font-weight", "600").set("border-radius", "9999px")
            .set("padding", "10px 20px").set("border", "none");
        saveBtn.addClickListener(e -> saveFile());

        header.add(backBtn, titleGroup, saveBtn);
        header.expand(titleGroup);

        // Editor
        editor = new TextArea();
        editor.setWidthFull();
        editor.setSizeFull();
        editor.getStyle()
            .set("font-family", "'SF Mono', 'Fira Code', 'Courier New', monospace")
            .set("font-size", "14px")
            .set("line-height", "1.7");
        // Make the textarea tall
        editor.getElement().getStyle().set("--vaadin-text-area-height", "600px");
        editor.setHeight("600px");

        add(header, editor);
        expand(editor);
    }

    private void loadFileContent() {
        if (currentFile == null || !currentFile.exists()) {
            Notification.show("File not found: " + (currentFile != null ? currentFile.getPath() : "null"),
                4000, Notification.Position.TOP_CENTER);
            return;
        }

        String fileName = currentFile.getName().toLowerCase();
        try {
            if (fileName.endsWith(".txt")) {
                editor.setValue(new String(Files.readAllBytes(currentFile.toPath()), StandardCharsets.UTF_8));
            } else if (fileName.endsWith(".docx") || fileName.endsWith(".doc")) {
                editor.setValue(extractTextFromDocx(currentFile));
                editor.setHelperText("Note: This is a DOCX file. Saving will write plain text back to the file.");
            } else if (fileName.endsWith(".pdf")) {
                editor.setValue("[PDF editing is not supported. Download the file to edit it externally.]");
                editor.setReadOnly(true);
            } else {
                editor.setValue(new String(Files.readAllBytes(currentFile.toPath()), StandardCharsets.UTF_8));
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error loading file", ex);
            Notification.show("Error loading file: " + ex.getMessage(), 4000, Notification.Position.TOP_CENTER);
        }
    }

    private void saveFile() {
        if (currentFile == null) {
            Notification.show("No file to save", 3000, Notification.Position.TOP_CENTER);
            return;
        }
        if (editor.isReadOnly()) {
            Notification.show("This file type cannot be edited here.", 3000, Notification.Position.TOP_CENTER);
            return;
        }
        try {
            Files.write(currentFile.toPath(), editor.getValue().getBytes(StandardCharsets.UTF_8));
            Notification.show("Saved successfully!", 2000, Notification.Position.TOP_CENTER);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error saving file", ex);
            Notification.show("Error saving file: " + ex.getMessage(), 4000, Notification.Position.TOP_CENTER);
        }
    }

    private String extractTextFromDocx(File file) throws IOException {
        try (java.util.zip.ZipFile zip = new java.util.zip.ZipFile(file)) {
            java.util.zip.ZipEntry entry = zip.getEntry("word/document.xml");
            if (entry == null) return "[Could not find document content in DOCX file.]";
            try (java.io.InputStream is = zip.getInputStream(entry)) {
                String xml = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                StringBuilder sb = new StringBuilder();
                int i = 0;
                while (i < xml.length()) {
                    if (xml.startsWith("<w:p", i) && i + 4 < xml.length()
                            && (xml.charAt(i + 4) == ' ' || xml.charAt(i + 4) == '>' || xml.charAt(i + 4) == '/')) {
                        if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '\n') sb.append('\n');
                    }
                    if (xml.startsWith("<w:t", i)) {
                        int start = xml.indexOf('>', i);
                        if (start != -1) {
                            int end = xml.indexOf("</w:t>", start);
                            if (end != -1) { sb.append(xml, start + 1, end); i = end + 6; continue; }
                        }
                    }
                    i++;
                }
                String result = sb.toString().trim();
                return result.isEmpty() ? "[Document appears to be empty.]" : result;
            }
        }
    }
}
