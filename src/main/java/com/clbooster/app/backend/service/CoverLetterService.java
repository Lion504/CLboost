package com.clbooster.app.backend.service;

import com.clbooster.aiservice.AIService;
import com.clbooster.app.backend.service.document.DocumentService;
import com.clbooster.app.backend.service.profile.Profile;
import com.clbooster.app.backend.service.profile.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Map;

/**
 * CoverLetterService handles the complete cover letter generation workflow.
 * Integrates AI analysis with resume and profile data.
 */
@Service
public class CoverLetterService {
    private static final Logger logger = Logger.getLogger(CoverLetterService.class.getName());

    private final AIService aiService;
    private final DocumentService documentService;
    private final ProfileService profileService;
    private final ResumeService resumeService;

    @Autowired
    public CoverLetterService(AIService aiService, DocumentService documentService,
                            ProfileService profileService, ResumeService resumeService) {
        this.aiService = aiService;
        this.documentService = documentService;
        this.profileService = profileService;
        this.resumeService = resumeService;
    }

    /**
     * Generates a cover letter from resume text and job description.
     * 
     * @param resumeText The raw resume text
     * @param jobDescription The job posting content
     * @return Generated cover letter
     */
    public String generateCoverLetter(String resumeText, String jobDescription) {
        if (resumeText == null || resumeText.isEmpty()) {
            throw new IllegalArgumentException("Resume text cannot be empty");
        }
        if (jobDescription == null || jobDescription.isEmpty()) {
            throw new IllegalArgumentException("Job description cannot be empty");
        }

        logger.info("Starting cover letter generation...");
        return aiService.generateCoverLetter(resumeText, jobDescription);
    }

    /**
     * Generates a cover letter using user profile and stored resume.
     * 
     * @param userPin The user's PIN
     * @param jobDescription The job posting content
     * @return Generated cover letter
     */
    public String generateCoverLetterForUser(int userPin, String jobDescription) {
        if (jobDescription == null || jobDescription.isEmpty()) {
            throw new IllegalArgumentException("Job description cannot be empty");
        }

        logger.info("Generating cover letter for user PIN: " + userPin);

        // Get user profile for context
        Profile profile = profileService.getProfile(userPin);
        if (profile == null) {
            throw new IllegalArgumentException("User profile not found");
        }

        // Build enhanced resume context from profile and job description
        String resumeContext = buildResumeContext(profile);
        
        return generateCoverLetter(resumeContext, jobDescription);
    }

    /**
     * Builds resume context from user profile data.
     * 
     * @param profile User profile
     * @return Formatted resume context
     */
    private String buildResumeContext(Profile profile) {
        StringBuilder context = new StringBuilder();

        context.append("PROFILE INFORMATION:\n");
        
        if (profile.getTools() != null && !profile.getTools().isEmpty()) {
            context.append("Tools/Technologies: ").append(profile.getTools()).append("\n");
        }
        
        if (profile.getSkills() != null && !profile.getSkills().isEmpty()) {
            context.append("Skills: ").append(profile.getSkills()).append("\n");
        }
        
        if (profile.getExperienceLevel() != null && !profile.getExperienceLevel().isEmpty()) {
            context.append("Experience Level: ").append(profile.getExperienceLevel()).append("\n");
        }
        
        if (profile.getLink() != null && !profile.getLink().isEmpty()) {
            context.append("Portfolio/Link: ").append(profile.getLink()).append("\n");
        }

        return context.toString();
    }

    /**
     * Stores a generated cover letter to file.
     * 
     * @param coverLetterContent The cover letter text
     * @param userPin User identifier
     * @return The storage path
     * @throws IOException if storage fails
     */
    public String storeCoverLetter(String coverLetterContent, int userPin) throws IOException {
        if (coverLetterContent == null || coverLetterContent.isEmpty()) {
            throw new IllegalArgumentException("Cover letter content cannot be empty");
        }

        logger.info("Storing cover letter for user PIN: " + userPin);

        String filename = "coverLetter_" + userPin + "_" + System.currentTimeMillis() + ".txt";
        return documentService.storeResumeText(coverLetterContent, String.valueOf(userPin));
    }

    /**
     * Gets generation history metadata for a user.
     * 
     * @param userPin User identifier
     * @return Metadata about recent generations
     */
    public Map<String, String> getGenerationMetadata(int userPin) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("userPin", String.valueOf(userPin));
        metadata.put("generatedAt", String.valueOf(System.currentTimeMillis()));
        return metadata;
    }
}
