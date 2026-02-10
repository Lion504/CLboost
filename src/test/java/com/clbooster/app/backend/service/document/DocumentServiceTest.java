package com.clbooster.app.backend.service.document;

import com.clbooster.app.backend.service.ResumeData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("DocumentService Tests")
class DocumentServiceTest {

    @Autowired
    private DocumentService documentService;

    @BeforeEach
    void setUp() {
        // Setup before each test
    }

    @Test
    @DisplayName("Should store resume file")
    void testStoreResumeFile() throws IOException {
        byte[] content = "Resume content test".getBytes(StandardCharsets.UTF_8);
        MultipartFile file = new MockMultipartFile(
                "file",
                "resume.txt",
                "text/plain",
                content
        );

        String storagePath = documentService.storeResumeFile(file, "user123");

        assertNotNull(storagePath);
        assertFalse(storagePath.isEmpty());
        assertFalse(storagePath.contains("null"));
    }

    @Test
    @DisplayName("Should store resume text")
    void testStoreResumeText() throws IOException {
        String resumeText = "John Doe\njohn@example.com\n(555) 123-4567";

        String storagePath = documentService.storeResumeText(resumeText, "user123");

        assertNotNull(storagePath);
        assertTrue(Files.exists(Paths.get(storagePath)));
        
        // Verify content
        String stored = new String(Files.readAllBytes(Paths.get(storagePath)), StandardCharsets.UTF_8);
        assertEquals(resumeText, stored);
    }

    @Test
    @DisplayName("Should retrieve stored resume file")
    void testRetrieveResumeFile() throws IOException {
        byte[] originalContent = "Test resume content for retrieval".getBytes(StandardCharsets.UTF_8);
        MultipartFile file = new MockMultipartFile(
                "file",
                "resume.txt",
                "text/plain",
                originalContent
        );

        String storagePath = documentService.storeResumeFile(file, "user123");
        byte[] retrieved = documentService.retrieveResumeFile(storagePath);

        assertNotNull(retrieved);
        assertEquals(new String(originalContent), new String(retrieved));
    }

    @Test
    @DisplayName("Should export resume as document")
    void testExportResumeAsDocument() {
        ResumeData resumeData = new ResumeData();
        resumeData.setFullName("John Doe");
        resumeData.setEmail("john@example.com");
        resumeData.setPhone("(555) 123-4567");
        resumeData.setSummary("Experienced software engineer");

        // Add skills
        resumeData.getSkills().add("Java");
        resumeData.getSkills().add("Spring Boot");
        resumeData.getSkills().add("SQL");

        String outputPath = "test_output/resume_export_" + System.currentTimeMillis() + ".docx";
        boolean success = documentService.exportResumeAsDocument(resumeData, outputPath);

        assertTrue(success);
        assertTrue(Files.exists(Paths.get(outputPath)));
    }

    @Test
    @DisplayName("Should export resume with work experience")
    void testExportResumeWithExperience() {
        ResumeData resumeData = new ResumeData();
        resumeData.setFullName("Jane Smith");
        resumeData.setEmail("jane@example.com");

        ResumeData.WorkExperience exp = new ResumeData.WorkExperience();
        exp.setJobTitle("Senior Engineer");
        exp.setCompany("TechCorp");
        exp.setStartDate("2020");
        exp.setEndDate("Present");
        exp.getResponsibilities().add("Led team");
        exp.getResponsibilities().add("Improved performance");

        resumeData.getWorkExperience().add(exp);

        String outputPath = "test_output/resume_with_exp_" + System.currentTimeMillis() + ".docx";
        boolean success = documentService.exportResumeAsDocument(resumeData, outputPath);

        assertTrue(success);
    }

    @Test
    @DisplayName("Should delete resume file")
    void testDeleteResumeFile() throws IOException {
        byte[] content = "Resume to delete".getBytes(StandardCharsets.UTF_8);
        MultipartFile file = new MockMultipartFile(
                "file",
                "resume.txt",
                "text/plain",
                content
        );

        String storagePath = documentService.storeResumeFile(file, "user123");
        assertTrue(Files.exists(Paths.get(storagePath)));

        boolean deleted = documentService.deleteResumeFile(storagePath);

        assertTrue(deleted);
        assertFalse(Files.exists(Paths.get(storagePath)));
    }

    @Test
    @DisplayName("Should reject empty file upload")
    void testStoreResumeFile_EmptyFile() {
        MultipartFile emptyFile = new MockMultipartFile(
                "file",
                "resume.txt",
                "text/plain",
                new byte[0]
        );

        assertThrows(IllegalArgumentException.class, () -> {
            documentService.storeResumeFile(emptyFile, "user123");
        });
    }

    @Test
    @DisplayName("Should handle retrieve of non-existent file")
    void testRetrieveResumeFile_NotFound() {
        assertThrows(IOException.class, () -> {
            documentService.retrieveResumeFile("non_existent_path.txt");
        });
    }

    @Test
    @DisplayName("Should handle delete of non-existent file")
    void testDeleteResumeFile_NotFound() {
        boolean deleted = documentService.deleteResumeFile("non_existent_path.txt");

        assertFalse(deleted);
    }

    @Test
    @DisplayName("Should format resume with all sections")
    void testFormatResumeComplete() {
        ResumeData resumeData = new ResumeData();
        resumeData.setFullName("Complete Resume");
        resumeData.setEmail("complete@example.com");
        resumeData.setPhone("(555) 111-2222");
        resumeData.setSummary("Professional summary here");

        resumeData.getSkills().add("Skill 1");
        resumeData.getSkills().add("Skill 2");

        resumeData.getEducation().add("BS Computer Science");
        resumeData.getCertifications().add("AWS Certified");

        ResumeData.WorkExperience exp = new ResumeData.WorkExperience();
        exp.setJobTitle("Engineer");
        exp.setCompany("Company");
        exp.setStartDate("2020");
        exp.setEndDate("2023");
        resumeData.getWorkExperience().add(exp);

        String outputPath = "test_output/complete_" + System.currentTimeMillis() + ".docx";
        boolean success = documentService.exportResumeAsDocument(resumeData, outputPath);

        assertTrue(success);
    }
}
