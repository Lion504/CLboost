package com.clbooster.app.backend.service.document;

import com.clbooster.app.backend.service.ResumeData;
import com.clbooster.aiservice.Exporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

/**
 * DocumentService handles document storage, retrieval, and export operations.
 * Manages resume files and generates formatted documents.
 */
@Service
public class DocumentService {
    private static final Logger logger = Logger.getLogger(DocumentService.class.getName());

    // Directory for storing uploaded documents
    private static final String DOCUMENT_STORAGE_DIR = "uploads/resumes/";

    private final Exporter exporter;

    @Autowired
    public DocumentService(Exporter exporter) {
        this.exporter = exporter;
        // Ensure storage directory exists
        try {
            Files.createDirectories(Paths.get(DOCUMENT_STORAGE_DIR));
            logger.info("Document storage directory ensured: " + DOCUMENT_STORAGE_DIR);
        } catch (IOException e) {
            logger.warning("Could not create storage directory: " + e.getMessage());
        }
    }

    /**
     * Stores an uploaded resume file.
     * 
     * @param file
     *            The resume file to store
     * @param userId
     *            User identifier for organizing files
     * @return The storage path of the file
     * @throws IOException
     *             if storage fails
     */
    public String storeResumeFile(MultipartFile file, String userId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file");
        }

        try {
            String filename = generateStorageFilename(file.getOriginalFilename(), userId);
            Path targetPath = Paths.get(DOCUMENT_STORAGE_DIR).resolve(filename);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            logger.info("File stored successfully: " + targetPath);
            return targetPath.toString();

        } catch (IOException e) {
            logger.severe("Failed to store resume file: " + e.getMessage());
            throw new IOException("Failed to store resume file", e);
        }
    }

    /**
     * Stores raw resume text to a file.
     * 
     * @param resumeText
     *            The resume text content
     * @param userId
     *            User identifier
     * @return The storage path of the file
     * @throws IOException
     *             if storage fails
     */
    public String storeResumeText(String resumeText, String userId) throws IOException {
        try {
            String filename = generateStorageFilename("resume_text.txt", userId);
            Path targetPath = Paths.get(DOCUMENT_STORAGE_DIR).resolve(filename);

            Files.write(targetPath, resumeText.getBytes());

            logger.info("Resume text stored: " + targetPath);
            return targetPath.toString();

        } catch (IOException e) {
            logger.severe("Failed to store resume text: " + e.getMessage());
            throw new IOException("Failed to store resume text", e);
        }
    }

    /**
     * Exports resume data as a formatted document.
     * 
     * @param resumeData
     *            The parsed resume data
     * @param outputPath
     *            Path where the document should be saved
     * @return true if export was successful
     */
    public boolean exportResumeAsDocument(ResumeData resumeData, String outputPath) {
        try {
            logger.info("Exporting resume as document: " + outputPath);

            String formattedContent = formatResumeContent(resumeData);

            // Use the injected Exporter to save as DOCX
            exporter.saveAsDoc(formattedContent, outputPath);

            logger.info("Resume exported successfully");
            return true;

        } catch (Exception e) {
            logger.severe("Failed to export resume: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves a stored resume file.
     * 
     * @param storagePath
     *            The path of the stored file
     * @return Byte array of the file content
     * @throws IOException
     *             if retrieval fails
     */
    public byte[] retrieveResumeFile(String storagePath) throws IOException {
        try {
            Path path = Paths.get(storagePath);
            if (!Files.exists(path)) {
                throw new IOException("File not found: " + storagePath);
            }

            byte[] content = Files.readAllBytes(path);
            logger.info("Retrieved resume file: " + storagePath + " (" + content.length + " bytes)");
            return content;

        } catch (IOException e) {
            logger.severe("Failed to retrieve resume file: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Deletes a stored resume file.
     * 
     * @param storagePath
     *            The path of the file to delete
     * @return true if deletion was successful
     */
    public boolean deleteResumeFile(String storagePath) {
        try {
            Path path = Paths.get(storagePath);
            if (Files.exists(path)) {
                Files.delete(path);
                logger.info("Deleted resume file: " + storagePath);
                return true;
            } else {
                logger.warning("File not found for deletion: " + storagePath);
                return false;
            }

        } catch (IOException e) {
            logger.severe("Failed to delete resume file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Generates a unique filename for storage.
     */
    private String generateStorageFilename(String originalFilename, String userId) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String extension = getFileExtension(originalFilename);
        return userId + "_" + timestamp + extension;
    }

    /**
     * Extracts file extension from filename.
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot) : "";
    }

    /**
     * Formats ResumeData into readable document content.
     */
    private String formatResumeContent(ResumeData resumeData) {
        StringBuilder content = new StringBuilder();

        // Header
        if (resumeData.getFullName() != null) {
            content.append(resumeData.getFullName()).append("\n");
        }
        if (resumeData.getEmail() != null || resumeData.getPhone() != null) {
            if (resumeData.getEmail() != null) {
                content.append(resumeData.getEmail()).append(" ");
            }
            if (resumeData.getPhone() != null) {
                content.append(resumeData.getPhone());
            }
            content.append("\n\n");
        }

        // Summary
        if (resumeData.getSummary() != null && !resumeData.getSummary().isEmpty()) {
            content.append("PROFESSIONAL SUMMARY\n");
            content.append(resumeData.getSummary()).append("\n\n");
        }

        // Skills
        if (!resumeData.getSkills().isEmpty()) {
            content.append("SKILLS\n");
            for (String skill : resumeData.getSkills()) {
                content.append("• ").append(skill).append("\n");
            }
            content.append("\n");
        }

        // Work Experience
        if (!resumeData.getWorkExperience().isEmpty()) {
            content.append("WORK EXPERIENCE\n");
            for (ResumeData.WorkExperience exp : resumeData.getWorkExperience()) {
                if (exp.getJobTitle() != null) {
                    content.append(exp.getJobTitle());
                }
                if (exp.getCompany() != null) {
                    content.append(" at ").append(exp.getCompany());
                }
                content.append("\n");

                if (exp.getStartDate() != null || exp.getEndDate() != null) {
                    if (exp.getStartDate() != null) {
                        content.append(exp.getStartDate());
                    }
                    content.append(" - ");
                    if (exp.getEndDate() != null) {
                        content.append(exp.getEndDate());
                    }
                    content.append("\n");
                }

                for (String resp : exp.getResponsibilities()) {
                    content.append("• ").append(resp).append("\n");
                }
                content.append("\n");
            }
        }

        // Education
        if (!resumeData.getEducation().isEmpty()) {
            content.append("EDUCATION\n");
            for (String edu : resumeData.getEducation()) {
                content.append("• ").append(edu).append("\n");
            }
            content.append("\n");
        }

        // Certifications
        if (!resumeData.getCertifications().isEmpty()) {
            content.append("CERTIFICATIONS\n");
            for (String cert : resumeData.getCertifications()) {
                content.append("• ").append(cert).append("\n");
            }
        }

        return content.toString();
    }
}
