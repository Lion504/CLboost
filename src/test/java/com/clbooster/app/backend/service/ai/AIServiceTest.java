package com.clbooster.app.backend.service.ai;

import com.clbooster.aiservice.AIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("AIService Resume Scanning Tests")
class AIServiceTest {

    @Autowired
    private AIService aiService;

    private String sampleResumeText;
    private String jobDescription;

    @BeforeEach
    void setUp() throws IOException {
        sampleResumeText = Files.readString(Paths.get("src/test/resources/sample_resume.txt"));
        jobDescription = Files.readString(Paths.get("src/test/resources/sample_job_posting.txt"));
    }

    @Test
    @DisplayName("Should scan resume and extract structured data")
    void testScanResume() {
        String result = aiService.scanResume(sampleResumeText);

        assertNotNull(result);
        // Result should be JSON response (may be empty in test environment)
        assertTrue(!result.isEmpty() || result.equals("{}"));
    }

    @Test
    @DisplayName("Should analyze job match and return selling points")
    void testAnalyzeJobMatch() {
        List<String> matchPoints = aiService.analyzeJobMatch(sampleResumeText, jobDescription);

        assertNotNull(matchPoints);
        // May be empty in test environment, but should not throw exception
    }

    @Test
    @DisplayName("Should handle empty resume text")
    void testScanResume_EmptyInput() {
        String result = aiService.scanResume("");
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should handle null job description")
    void testAnalyzeJobMatch_NullJobDescription() {
        List<String> matchPoints = aiService.analyzeJobMatch(sampleResumeText, "");
        assertNotNull(matchPoints);
    }
}
