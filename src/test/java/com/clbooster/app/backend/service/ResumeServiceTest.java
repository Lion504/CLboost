package com.clbooster.app.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("ResumeService Tests")
class ResumeServiceTest {

    @Autowired
    private ResumeService resumeService;

    private String sampleResumeText;

    @BeforeEach
    void setUp() {
        resumeService.clearCache();
        sampleResumeText = "John Doe\njohn@example.com\n(555) 123-4567";
    }

    @Test
    @DisplayName("Should scan resume text successfully")
    void testScanResumeText() {
        ResumeData result = resumeService.scanResumeText(sampleResumeText);

        assertNotNull(result);
        assertEquals(sampleResumeText, result.getRawResumeText());
    }

    @Test
    @DisplayName("Should throw exception for empty resume text")
    void testScanResumeText_EmptyInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            resumeService.scanResumeText("");
        });
    }

    @Test
    @DisplayName("Should throw exception for null resume text")
    void testScanResumeText_NullInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            resumeService.scanResumeText(null);
        });
    }

    @Test
    @DisplayName("Should validate file format - reject invalid")
    void testUploadAndScanResume_InvalidFormat() {
        byte[] content = "resume content".getBytes(StandardCharsets.UTF_8);
        MultipartFile invalidFile = new MockMultipartFile(
                "file",
                "resume.xyz",
                "application/xyz",
                content
        );

        assertThrows(IllegalArgumentException.class, () -> {
            resumeService.uploadAndScanResume(invalidFile);
        });
    }

    @Test
    @DisplayName("Should accept TXT file format")
    void testUploadAndScanResume_TxtFile() throws IOException {
        byte[] content = sampleResumeText.getBytes(StandardCharsets.UTF_8);
        MultipartFile txtFile = new MockMultipartFile(
                "file",
                "resume.txt",
                "text/plain",
                content
        );

        ResumeData result = resumeService.uploadAndScanResume(txtFile);

        assertNotNull(result);
        assertNotNull(result.getRawResumeText());
    }

    @Test
    @DisplayName("Should reject empty files")
    void testUploadAndScanResume_EmptyFile() {
        MultipartFile emptyFile = new MockMultipartFile(
                "file",
                "resume.txt",
                "text/plain",
                new byte[0]
        );

        assertThrows(IllegalArgumentException.class, () -> {
            resumeService.uploadAndScanResume(emptyFile);
        });
    }

    @Test
    @DisplayName("Should reject files with null name")
    void testUploadAndScanResume_NullFilename() {
        byte[] content = "resume".getBytes(StandardCharsets.UTF_8);
        MultipartFile invalidFile = new MockMultipartFile(
                "file",
                null,
                "text/plain",
                content
        );

        assertThrows(IllegalArgumentException.class, () -> {
            resumeService.uploadAndScanResume(invalidFile);
        });
    }

    @Test
    @DisplayName("Should save resume to profile")
    void testSaveResumeToProfile() {
        ResumeData resumeData = new ResumeData();
        resumeData.setFullName("John Doe");
        resumeData.setEmail("john@example.com");

        boolean saved = resumeService.saveResumeToProfile(12345, resumeData);

        assertTrue(saved);
    }

    @Test
    @DisplayName("Should retrieve saved resume from profile")
    void testGetResumeForProfile() {
        int pin = 12345;
        ResumeData resumeData = new ResumeData();
        resumeData.setFullName("Jane Smith");
        resumeData.setEmail("jane@example.com");

        resumeService.saveResumeToProfile(pin, resumeData);
        ResumeData retrieved = resumeService.getResumeForProfile(pin);

        assertNotNull(retrieved);
        assertEquals("Jane Smith", retrieved.getFullName());
        assertEquals("jane@example.com", retrieved.getEmail());
    }

    @Test
    @DisplayName("Should return null for non-existent profile")
    void testGetResumeForProfile_NotFound() {
        ResumeData retrieved = resumeService.getResumeForProfile(99999);

        assertNull(retrieved);
    }

    @Test
    @DisplayName("Should analyze job match")
    void testAnalyzeJobMatch() {
        ResumeData resumeData = new ResumeData();
        resumeData.setRawResumeText(sampleResumeText);

        String jobDescription = "Looking for Java developer";
        List<String> matchPoints = resumeService.analyzeJobMatch(resumeData, jobDescription);

        assertNotNull(matchPoints);
    }

    @Test
    @DisplayName("Should handle null resume in job match")
    void testAnalyzeJobMatch_NullResume() {
        ResumeData resumeData = new ResumeData();

        List<String> matchPoints = resumeService.analyzeJobMatch(resumeData, "job desc");

        assertNotNull(matchPoints);
        assertTrue(matchPoints.isEmpty());
    }

    @Test
    @DisplayName("Should clear cache")
    void testClearCache() {
        ResumeData data = new ResumeData();
        data.setFullName("Test");
        resumeService.saveResumeToProfile(111, data);

        int sizeBefore = resumeService.getCacheSize();
        assertTrue(sizeBefore > 0);

        resumeService.clearCache();

        int sizeAfter = resumeService.getCacheSize();
        assertEquals(0, sizeAfter);
    }

    @Test
    @DisplayName("Should handle multiple resumes")
    void testMultipleResumes() {
        ResumeData data1 = new ResumeData();
        data1.setFullName("Person 1");

        ResumeData data2 = new ResumeData();
        data2.setFullName("Person 2");

        resumeService.saveResumeToProfile(111, data1);
        resumeService.saveResumeToProfile(222, data2);

        assertEquals("Person 1", resumeService.getResumeForProfile(111).getFullName());
        assertEquals("Person 2", resumeService.getResumeForProfile(222).getFullName());
    }
}
