package com.clbooster.app.views;

import com.clbooster.app.backend.service.ResumeData;
import com.clbooster.app.backend.service.ResumeService;
import com.clbooster.app.backend.service.document.DocumentService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Resume Manager View for uploading, scanning, and managing resumes.
 * Users can upload resumes, review AI-scanned data, and approve/edit the information.
 */
@Route(value = "resumes", layout = MainLayout.class)
@PageTitle("Resume Manager | CL Booster")
public class ResumeManagerView extends VerticalLayout {
    private static final Logger logger = Logger.getLogger(ResumeManagerView.class.getName());

    private final ResumeService resumeService;
    private final DocumentService documentService;

    private VerticalLayout uploadSection;
    private VerticalLayout reviewSection;
    private Div scanResultsDiv;
    private ResumeData currentResumeData;

    public ResumeManagerView(ResumeService resumeService, DocumentService documentService) {
        this.resumeService = resumeService;
        this.documentService = documentService;

        addClassName(LumoUtility.Padding.MEDIUM);
        setSpacing(true);

        add(new H1("Resume Manager"));
        add(new Paragraph("Upload your resume for AI-powered scanning and content review."));

        createUploadSection();
        add(uploadSection);

        createReviewSection();
        add(reviewSection);

        reviewSection.setVisible(false);
    }

    /**
     * Creates the file upload section.
     */
    private void createUploadSection() {
        uploadSection = new VerticalLayout();
        uploadSection.addClassName(LumoUtility.Border.ALL);
        uploadSection.addClassName(LumoUtility.Padding.MEDIUM);

        H2 uploadTitle = new H2("Upload Your Resume");
        uploadSection.add(uploadTitle);

        Div uploadInfo = new Div();
        uploadInfo.setText("Supported formats: PDF, DOCX, DOC, TXT");
        uploadInfo.addClassName(LumoUtility.Padding.SMALL);
        uploadSection.add(uploadInfo);

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("application/pdf", ".pdf", 
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx",
                "application/msword", ".doc", 
                "text/plain", ".txt");
        upload.setMaxFileSize(10 * 1024 * 1024); // 10MB limit

        upload.addSucceededListener(event -> {
            try {
                logger.info("File upload started: " + event.getFileName());
                Notification notification = Notification.show("Scanning resume...");
                notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);

                // Convert MemoryBuffer to a MultipartFile-like wrapper for scanning
                byte[] fileData = buffer.getInputStream().readAllBytes();
                String fileName = event.getFileName();
                
                // Create a simple wrapper to scan the resume text
                String resumeText = extractTextFromBytes(fileData, fileName);
                currentResumeData = resumeService.scanResumeText(resumeText);

                notification.close();
                Notification.show("Resume scanned successfully!", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                // Show review section with the scanned data
                displayReviewSection(currentResumeData);

            } catch (IOException | IllegalArgumentException e) {
                logger.severe("Error processing resume: " + e.getMessage());
                Notification.show("Error: " + e.getMessage(), 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        upload.addFailedListener(event -> {
            logger.severe("File upload failed: " + event.getReason());
            Notification.show("Upload failed: " + event.getReason(), 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

        uploadSection.add(upload);
    }

    /**
     * Creates the review section where users can edit and approve scanned resume data.
     */
    private void createReviewSection() {
        reviewSection = new VerticalLayout();
        reviewSection.addClassName(LumoUtility.Border.ALL);
        reviewSection.addClassName(LumoUtility.Padding.MEDIUM);

        H2 reviewTitle = new H2("Review & Confirm Resume Data");
        reviewSection.add(reviewTitle);

        Paragraph reviewInfo = new Paragraph(
                "Review the information extracted from your resume. Correct any errors before submitting.");
        reviewSection.add(reviewInfo);

        // Create form fields for reviewing/editing
        scanResultsDiv = new Div();
        reviewSection.add(scanResultsDiv);

        HorizontalLayout actionButtons = createActionButtons();
        reviewSection.add(actionButtons);
    }

    /**
     * Displays the review section with scanned resume data in editable fields.
     */
    private void displayReviewSection(ResumeData resumeData) {
        scanResultsDiv.removeAll();

        // Create a form with editable fields
        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setSpacing(true);

        // Personal Information Section
        formLayout.add(new H3("Personal Information"));

        TextField nameField = new TextField("Full Name");
        nameField.setValue(resumeData.getFullName() != null ? resumeData.getFullName() : "");
        formLayout.add(nameField);

        TextField emailField = new TextField("Email");
        emailField.setValue(resumeData.getEmail() != null ? resumeData.getEmail() : "");
        formLayout.add(emailField);

        TextField phoneField = new TextField("Phone");
        phoneField.setValue(resumeData.getPhone() != null ? resumeData.getPhone() : "");
        formLayout.add(phoneField);

        // Summary Section
        formLayout.add(new H3("Professional Summary"));
        TextArea summaryField = new TextArea("Summary");
        summaryField.setValue(resumeData.getSummary() != null ? resumeData.getSummary() : "");
        summaryField.setMinHeight("100px");
        formLayout.add(summaryField);

        // Skills Section
        formLayout.add(new H3("Skills"));
        TextArea skillsField = new TextArea("Skills (one per line)");
        String skillsText = resumeData.getSkills() != null ? 
                String.join("\n", resumeData.getSkills()) : "";
        skillsField.setValue(skillsText);
        skillsField.setMinHeight("100px");
        formLayout.add(skillsField);

        // Work Experience Section
        if (!resumeData.getWorkExperience().isEmpty()) {
            formLayout.add(new H3("Work Experience"));
            for (int i = 0; i < resumeData.getWorkExperience().size(); i++) {
                ResumeData.WorkExperience exp = resumeData.getWorkExperience().get(i);
                
                Div experienceDiv = new Div();
                experienceDiv.addClassName(LumoUtility.Border.ALL);
                experienceDiv.addClassName(LumoUtility.Padding.SMALL);

                TextField jobTitleField = new TextField("Job Title");
                jobTitleField.setValue(exp.getJobTitle() != null ? exp.getJobTitle() : "");
                experienceDiv.add(jobTitleField);

                TextField companyField = new TextField("Company");
                companyField.setValue(exp.getCompany() != null ? exp.getCompany() : "");
                experienceDiv.add(companyField);

                TextField startDateField = new TextField("Start Date");
                startDateField.setValue(exp.getStartDate() != null ? exp.getStartDate() : "");
                experienceDiv.add(startDateField);

                TextField endDateField = new TextField("End Date");
                endDateField.setValue(exp.getEndDate() != null ? exp.getEndDate() : "");
                experienceDiv.add(endDateField);

                TextArea responsibilitiesField = new TextArea("Responsibilities (one per line)");
                String respText = exp.getResponsibilities() != null ? 
                        String.join("\n", exp.getResponsibilities()) : "";
                responsibilitiesField.setValue(respText);
                responsibilitiesField.setMinHeight("80px");
                experienceDiv.add(responsibilitiesField);

                formLayout.add(experienceDiv);
            }
        }

        // Education Section
        if (!resumeData.getEducation().isEmpty()) {
            formLayout.add(new H3("Education"));
            TextArea educationField = new TextArea("Education (one per line)");
            String eduText = String.join("\n", resumeData.getEducation());
            educationField.setValue(eduText);
            educationField.setMinHeight("80px");
            formLayout.add(educationField);
        }

        // Certifications Section
        if (!resumeData.getCertifications().isEmpty()) {
            formLayout.add(new H3("Certifications"));
            TextArea certificationsField = new TextArea("Certifications (one per line)");
            String certText = String.join("\n", resumeData.getCertifications());
            certificationsField.setValue(certText);
            certificationsField.setMinHeight("80px");
            formLayout.add(certificationsField);
        }

        scanResultsDiv.add(formLayout);
        reviewSection.setVisible(true);
    }

    /**
     * Creates action buttons for the review section.
     */
    private HorizontalLayout createActionButtons() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);

        Button approveButton = new Button("Approve & Save");
        approveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        approveButton.addClickListener(event -> {
            ConfirmDialog dialog = new ConfirmDialog();
            dialog.setHeader("Confirm Resume");
            dialog.setText("Are you sure you want to save this resume to your profile?");
            dialog.setConfirmText("Yes, Save");
            dialog.setCancelText("Cancel");
            dialog.addConfirmListener(confirmEvent -> {
                approveResume();
                dialog.close();
            });
            dialog.open();
        });

        Button editButton = new Button("Re-upload");
        editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        editButton.addClickListener(event -> {
            reviewSection.setVisible(false);
            currentResumeData = null;
        });

        buttonLayout.add(approveButton, editButton);
        return buttonLayout;
    }

    /**
     * Handles the resume approval and saving to profile.
     */
    private void approveResume() {
        try {
            if (currentResumeData == null) {
                Notification.show("No resume data to save", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            // In production, get the actual user PIN from authentication context
            int mockUserPin = 12345; // TODO: Get actual user PIN from session

            boolean saved = resumeService.saveResumeToProfile(mockUserPin, currentResumeData);

            if (saved) {
                Notification.show("Resume saved successfully!", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                // Reset the review section
                reviewSection.setVisible(false);
                currentResumeData = null;
            } else {
                Notification.show("Failed to save resume", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }

        } catch (Exception e) {
            logger.severe("Error approving resume: " + e.getMessage());
            Notification.show("Error: " + e.getMessage(), 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    /**
     * Extracts text from uploaded file bytes.
     */
    private String extractTextFromBytes(byte[] fileData, String fileName) throws IOException {
        if (fileName.toLowerCase().endsWith(".txt")) {
            return new String(fileData, java.nio.charset.StandardCharsets.UTF_8);
        }
        // For PDF/DOCX, we would use the Parser utility
        // For now, return a simple representation
        return new String(fileData, java.nio.charset.StandardCharsets.UTF_8);
    }
}

