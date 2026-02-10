package com.clbooster.app.backend.service;

import com.clbooster.app.backend.service.document.DocumentService;
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
import java.util.List;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Resume Workflow Integration Tests")
class ResumeWorkflowIntegrationTest {

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private DocumentService documentService;

    private String sampleResumeText;
    private String jobDescription;

    @BeforeEach
    void setUp() throws IOException {
        resumeService.clearCache();

        // Read sample resume from file
        URL resumeResource = getClass().getClassLoader().getResource("sample_resume.txt");
        assert resumeResource != null : "sample_resume.txt not found in test resources";
        sampleResumeText = new String(Files.readAllBytes(Paths.get(resumeResource.getPath())), 
                StandardCharsets.UTF_8);

        // Read sample job posting from file
        URL jobResource = getClass().getClassLoader().getResource("sample_job_posting.txt");
        assert jobResource != null : "sample_job_posting.txt not found in test resources";
        jobDescription = new String(Files.readAllBytes(Paths.get(jobResource.getPath())), 
                StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("Complete workflow: Upload -> Scan -> Review -> Save")
    void testCompleteResumeScanWorkflow() throws IOException {
        // Step 1: Create and upload resume file
        byte[] resumeBytes = sampleResumeText.getBytes(StandardCharsets.UTF_8);
        MultipartFile resumeFile = new MockMultipartFile(
                "file",
                "resume.txt",
                "text/plain",
                resumeBytes
        );

        // Step 2: Upload and scan
        ResumeData scannedData = resumeService.uploadAndScanResume(resumeFile);

        assertNotNull(scannedData);
        assertTrue(scannedData.getRawResumeText().contains("John"));

        // Step 3: Verify extraction
        assertNotNull(scannedData.getRawResumeText(), "Raw text should be extracted");

        // Step 4: Save to profile
        int userPin = 12345;
        boolean saved = resumeService.saveResumeToProfile(userPin, scannedData);
        assertTrue(saved, "Resume should be saved to profile");

        // Step 5: Retrieve and verify
        ResumeData retrieved = resumeService.getResumeForProfile(userPin);
        assertNotNull(retrieved);
        assertEquals(scannedData.getRawResumeText(), retrieved.getRawResumeText());
    }

    @Test
    @DisplayName("Resume scanning and job matching workflow")
    void testResumeScanAndJobMatch() throws IOException {
        // Upload and scan resume
        byte[] resumeBytes = sampleResumeText.getBytes(StandardCharsets.UTF_8);
        MultipartFile resumeFile = new MockMultipartFile(
                "file",
                "resume.txt",
                "text/plain",
                resumeBytes
        );

        ResumeData scannedData = resumeService.uploadAndScanResume(resumeFile);

        // Analyze job match
        List<String> matchPoints = resumeService.analyzeJobMatch(scannedData, jobDescription);

        assertNotNull(matchPoints);
        // Match points may be empty in test environment, but shouldn't throw exception
    }

    @Test
    @DisplayName("Resume scanning and document export workflow")
    void testResumeScanAndExport() throws IOException {
        // Upload and scan
        byte[] resumeBytes = sampleResumeText.getBytes(StandardCharsets.UTF_8);
        MultipartFile resumeFile = new MockMultipartFile(
                "file",
                "resume.txt",
                "text/plain",
                resumeBytes
        );

        ResumeData scannedData = resumeService.uploadAndScanResume(resumeFile);

        // Create complete resume data for export
        scannedData.setFullName("John Edward Doe");
        scannedData.setEmail("john.doe@example.com");
        scannedData.setPhone("(555) 123-4567");
        scannedData.setSummary("Experienced Senior Software Engineer");

        scannedData.getSkills().add("Java");
        scannedData.getSkills().add("Spring Boot");
        scannedData.getSkills().add("Microservices");

        scannedData.getEducation().add("BS Computer Science - State University");

        scannedData.getCertifications().add("AWS Solutions Architect");

        ResumeData.WorkExperience exp = new ResumeData.WorkExperience();
        exp.setJobTitle("Senior Software Engineer");
        exp.setCompany("TechCorp Inc.");
        exp.setStartDate("January 2020");
        exp.setEndDate("Present");
        exp.getResponsibilities().add("Architected microservices platform");
        exp.getResponsibilities().add("Led team of 5 developers");
        scannedData.getWorkExperience().add(exp);

        // Export as document
        String exportPath = "test_output/resume_export_" + System.currentTimeMillis() + ".docx";
        boolean exported = documentService.exportResumeAsDocument(scannedData, exportPath);

        assertTrue(exported, "Resume should be exported successfully");
        assertTrue(Files.exists(Paths.get(exportPath)), "Exported file should exist");
    }

    @Test
    @DisplayName("Multiple file uploads with caching")
    void testMultipleResumeHandling() throws IOException {
        // Upload first resume
        byte[] resume1 = """
                Alice Johnson
                alice@example.com
                Senior Java Developer
                """.getBytes(StandardCharsets.UTF_8);
        MultipartFile file1 = new MockMultipartFile(
                "file",
                "resume1.txt",
                "text/plain",
                resume1
        );
        ResumeData data1 = resumeService.uploadAndScanResume(file1);

        // Upload second resume
        byte[] resume2 = """
                Bob Smith
                bob@example.com
                Backend Engineer
                """.getBytes(StandardCharsets.UTF_8);
        MultipartFile file2 = new MockMultipartFile(
                "file",
                "resume2.txt",
                "text/plain",
                resume2
        );
        ResumeData data2 = resumeService.uploadAndScanResume(file2);

        // Save both
        resumeService.saveResumeToProfile(111, data1);
        resumeService.saveResumeToProfile(222, data2);

        // Verify both are retrievable
        ResumeData retrieved1 = resumeService.getResumeForProfile(111);
        ResumeData retrieved2 = resumeService.getResumeForProfile(222);

        assertNotNull(retrieved1);
        assertNotNull(retrieved2);
        assertTrue(retrieved1.getRawResumeText().contains("Alice"));
        assertTrue(retrieved2.getRawResumeText().contains("Bob"));
    }

    @Test
    @DisplayName("Complete workflow with file storage and retrieval")
    void testCompleteWorkflowWithStorage() throws IOException {
        // Upload resume
        byte[] resumeBytes = sampleResumeText.getBytes(StandardCharsets.UTF_8);
        MultipartFile resumeFile = new MockMultipartFile(
                "file",
                "resume.txt",
                "text/plain",
                resumeBytes
        );

        // Scan
        ResumeData scannedData = resumeService.uploadAndScanResume(resumeFile);
        scannedData.setFullName("John Doe");
        scannedData.setEmail("john@example.com");

        // Store file
        String storagePath = documentService.storeResumeFile(resumeFile, "user_123");
        assertNotNull(storagePath);

        // Store resume text
        String textPath = documentService.storeResumeText(scannedData.getRawResumeText(), "user_123");
        assertTrue(Files.exists(Paths.get(textPath)));

        // Save to profile
        resumeService.saveResumeToProfile(12345, scannedData);

        // Verify everything
        ResumeData retrieved = resumeService.getResumeForProfile(12345);
        assertNotNull(retrieved);

        byte[] storedFile = documentService.retrieveResumeFile(storagePath);
        assertNotNull(storedFile);

        // Export
        String exportPath = "test_output/final_" + System.currentTimeMillis() + ".docx";
        boolean exported = documentService.exportResumeAsDocument(retrieved, exportPath);
        assertTrue(exported);
    }

    @Test
    @DisplayName("Error handling - invalid formats throughout workflow")
    void testErrorHandlingWorkflow() {
        // Try invalid file format
        byte[] content = "test".getBytes();
        MultipartFile invalidFile = new MockMultipartFile(
                "file",
                "test.invalid",
                "application/invalid",
                content
        );

        assertThrows(IllegalArgumentException.class, () -> {
            resumeService.uploadAndScanResume(invalidFile);
        });

        // Try empty resume scan
        assertThrows(IllegalArgumentException.class, () -> {
            resumeService.scanResumeText("");
        });

        // Try null resume for job match
        ResumeData nullData = new ResumeData();
        List<String> matchPoints = resumeService.analyzeJobMatch(nullData, "job");
        assertTrue(matchPoints.isEmpty());
    }

    @Test
    @DisplayName("Workflow with complex resume data")
    void testComplexResumeWorkflow() throws IOException {
        byte[] resumeBytes = sampleResumeText.getBytes(StandardCharsets.UTF_8);
        MultipartFile resumeFile = new MockMultipartFile(
                "file",
                "complex_resume.txt",
                "text/plain",
                resumeBytes
        );

        ResumeData scannedData = resumeService.uploadAndScanResume(resumeFile);

        // Populate with detailed data
        scannedData.setFullName("John Edward Doe");
        scannedData.setEmail("john.doe@example.com");
        scannedData.setPhone("(555) 123-4567");
        scannedData.setSummary("Experienced Senior Software Engineer with 8+ years");

        scannedData.getSkills().add("Java");
        scannedData.getSkills().add("Spring Boot");
        scannedData.getSkills().add("Microservices");
        scannedData.getSkills().add("Kubernetes");
        scannedData.getSkills().add("AWS");

        scannedData.getEducation().add("BS Computer Science - State University");
        scannedData.getCertifications().add("AWS Solutions Architect");
        scannedData.getCertifications().add("Kubernetes CKAD");

        // Add multiple work experiences
        ResumeData.WorkExperience exp1 = new ResumeData.WorkExperience();
        exp1.setJobTitle("Senior Software Engineer");
        exp1.setCompany("TechCorp");
        exp1.setStartDate("2020");
        exp1.setEndDate("Present");
        exp1.getResponsibilities().add("Architected microservices");
        scannedData.getWorkExperience().add(exp1);

        ResumeData.WorkExperience exp2 = new ResumeData.WorkExperience();
        exp2.setJobTitle("Software Engineer");
        exp2.setCompany("StartupXYZ");
        exp2.setStartDate("2017");
        exp2.setEndDate("2019");
        exp2.getResponsibilities().add("REST API development");
        scannedData.getWorkExperience().add(exp2);

        // Save and retrieve
        resumeService.saveResumeToProfile(99999, scannedData);
        ResumeData retrieved = resumeService.getResumeForProfile(99999);

        assertNotNull(retrieved);
        assertEquals("John Edward Doe", retrieved.getFullName());
        assertEquals(5, retrieved.getSkills().size());
        assertEquals(2, retrieved.getWorkExperience().size());

        // Export complex data
        String exportPath = "test_output/complex_" + System.currentTimeMillis() + ".docx";
        boolean exported = documentService.exportResumeAsDocument(retrieved, exportPath);
        assertTrue(exported);
    }
}
