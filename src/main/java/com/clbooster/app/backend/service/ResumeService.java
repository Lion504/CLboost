package com.clbooster.app.backend.service;

import com.clbooster.aiservice.AIService;
import com.clbooster.aiservice.Parser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * ResumeService handles resume-related operations including:
 * - Resume upload and file management
 * - AI-powered resume scanning and parsing
 * - Resume data storage and retrieval
 * - Resume analysis for job matching
 */

@Service
public class ResumeService {
    private static final Logger logger = Logger.getLogger(ResumeService.class.getName());

    private final AIService aiService;
    private final Parser parser;
    private final ObjectMapper objectMapper;
    
    // In-memory storage for demo purposes. In production, use database
    private final Map<String, ResumeData> resumeCache = new HashMap<>();

    @Autowired
    public ResumeService(AIService aiService, Parser parser) {
        this.aiService = aiService;
        this.parser = parser;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Scans resume text directly (without file upload).
     * Uses AI to parse structured data from the provided text.
     * 
     * @param resumeText The raw resume text to scan
     * @return ResumeData containing parsed information
     */
    public ResumeData scanResumeText(String resumeText) {
        if (resumeText == null || resumeText.isEmpty()) {
            throw new IllegalArgumentException("Resume text cannot be empty");
        }

        logger.info("Scanning resume text (" + resumeText.length() + " characters)");

        // Use AI to scan and parse the resume
        String jsonResponse = aiService.scanResume(resumeText);
        ResumeData resumeData = parseResumeResponse(jsonResponse);
        resumeData.setRawResumeText(resumeText);

        logger.info("Resume scanned successfully");
        return resumeData;
    }

    /**
     * Uploads and scans a resume file.
     * Extracts text from the resume and uses AI to parse structured data.
     * 
     * @param file The resume file (PDF, DOCX, or TXT)
     * @return ResumeData containing parsed information
     * @throws IOException if file processing fails
     */
    public ResumeData uploadAndScanResume(MultipartFile file) throws IOException {
        logger.info("Processing resume upload: " + file.getOriginalFilename());

        // Validate file
        validateResumeFile(file);

        // Extract text from the file
        String resumeText = extractTextFromFile(file);

        // Use AI to scan and parse the resume
        return scanResumeText(resumeText);
    }

    /**
     * Analyzes the match between a resume and a job description.
     * Returns key selling points that align with the job requirements.
     * 
     * @param resumeData The parsed resume data
     * @param jobDescription The job posting content
     * @return List of key selling points
     */
    public List<String> analyzeJobMatch(ResumeData resumeData, String jobDescription) {
        if (resumeData == null || resumeData.getRawResumeText() == null) {
            logger.warning("Cannot analyze job match: resume data is empty");
            return List.of();
        }

        logger.info("Analyzing job match for resume...");
        return aiService.analyzeJobMatch(resumeData.getRawResumeText(), jobDescription);
    }

    /**
     * Saves the approved resume data to the user's profile.
     * In production, this would store to the database.
     * 
     * @param pin User's PIN
     * @param resumeData The approved resume data
     * @return true if save was successful
     */
    public boolean saveResumeToProfile(int pin, ResumeData resumeData) {
        try {
            logger.info("Saving resume data to profile for PIN: " + pin);

            // In production, save to database
            // For now, we store in memory
            String profileKey = "profile_" + pin;
            resumeCache.put(profileKey, resumeData);

            logger.info("Resume data saved successfully");
            return true;

        } catch (Exception e) {
            logger.severe("Failed to save resume data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves the resume data for a specific user.
     * 
     * @param pin User's PIN
     * @return ResumeData if found, null otherwise
     */
    public ResumeData getResumeForProfile(int pin) {
        String profileKey = "profile_" + pin;
        ResumeData data = resumeCache.get(profileKey);
        
        if (data != null) {
            logger.info("Retrieved resume data for PIN: " + pin);
        } else {
            logger.warning("No resume data found for PIN: " + pin);
        }
        
        return data;
    }

    /**
     * Validates that the uploaded file is a supported resume format.
     */
    private void validateResumeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Resume file cannot be empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("Invalid file");
        }

        String extension = originalFilename.toLowerCase();
        boolean validFormat = extension.endsWith(".pdf") 
                || extension.endsWith(".docx") 
                || extension.endsWith(".doc")
                || extension.endsWith(".txt");

        if (!validFormat) {
            throw new IllegalArgumentException(
                    "Unsupported file format. Please upload PDF, DOCX, or TXT files.");
        }

        logger.info("Resume file validated: " + originalFilename);
    }

    /**
     * Extracts text content from the resume file.
     * Uses Apache Tika parser for various file formats.
     */
    private String extractTextFromFile(MultipartFile file) throws IOException {
        try {
            String contentType = file.getContentType();
            String originalFilename = file.getOriginalFilename();

            if (originalFilename != null && originalFilename.toLowerCase().endsWith(".txt")) {
                // For text files, read directly
                return new String(file.getBytes(), StandardCharsets.UTF_8);
            }

            // For PDF and DOCX, create a temporary file and use parser
            // In production, use a more efficient implementation
            java.io.File tempFile = java.io.File.createTempFile("resume_", 
                    originalFilename != null ? originalFilename : ".tmp");
            file.transferTo(tempFile);

            // Use the Parser utility to extract text
            String extractedText = parser.parseFileToJson(tempFile.getAbsolutePath());
            
            // Clean up temp file
            if (!tempFile.delete()) {
                logger.warning("Failed to delete temporary file: " + tempFile.getAbsolutePath());
            }

            logger.info("Text extracted from resume file: " + extractedText.length() + " characters");
            return extractedText;

        } catch (IOException e) {
            logger.severe("Failed to extract text from resume: " + e.getMessage());
            throw new IOException("Failed to process resume file", e);
        }
    }

    /**
     * Generates a cache key from the filename.
     */
    private String generateFileKey(String originalFilename) {
        return "resume_" + System.currentTimeMillis() + "_" + originalFilename;
    }

    /**
     * Clears the in-memory cache (for testing or cleanup).
     */
    public void clearCache() {
        resumeCache.clear();
        logger.info("Resume cache cleared");
    }

    /**
     * Gets cache size (for testing and debugging).
     */
    public int getCacheSize() {
        return resumeCache.size();
    }

    /**
     * Parses the JSON response from AI into a ResumeData object.
     */
    private ResumeData parseResumeResponse(String jsonResponse) {
        try {
            // Remove markdown code blocks if present
            String cleanJson = jsonResponse
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            ResumeData resumeData = objectMapper.readValue(cleanJson, ResumeData.class);
            
            // Ensure lists are not null
            if (resumeData.getSkills() == null) {
                resumeData.setSkills(new ArrayList<>());
            }
            if (resumeData.getWorkExperience() == null) {
                resumeData.setWorkExperience(new ArrayList<>());
            }
            if (resumeData.getEducation() == null) {
                resumeData.setEducation(new ArrayList<>());
            }
            if (resumeData.getCertifications() == null) {
                resumeData.setCertifications(new ArrayList<>());
            }

            return resumeData;

        } catch (Exception e) {
            logger.warning("Failed to parse AI response as JSON: " + e.getMessage());
            // Return empty ResumeData on parse error
            return new ResumeData();
        }
    }
}

