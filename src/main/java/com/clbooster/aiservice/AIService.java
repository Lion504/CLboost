package com.clbooster.aiservice;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.PdfFileContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.output.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

/**
 * AI Service for resume processing and cover letter generation.
 * Provides resume scanning, job matching, and cover letter generation capabilities.
 */
@Service
public class AIService {

    private static final Logger logger = Logger.getLogger(AIService.class.getName());
    private final ChatLanguageModel languageModel;
    private final String apiKey;

    public AIService(@Value("${spring.ai.google.api-key:}") String apiKey) {
        this.apiKey = (apiKey != null && !apiKey.isEmpty()) ? 
            apiKey : System.getenv("GOOGLE_API_KEY");
        
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            logger.warning("No Google API Key provided. AI features may not work.");
            this.languageModel = null;
        } else {
            this.languageModel = GoogleAiGeminiChatModel.builder()
                .apiKey(this.apiKey)
                .modelName("gemini-2.5-flash-lite")
                .temperature(0.7)
                .build();
        }
    }

    /**
     * Extracts text from a file (PDF or TXT) using Gemini.
     * This can be used for both resumes and job descriptions.
     */
    /**
     * Extracts text from a file (PDF or TXT) using Gemini.
     */
    public String extractTextFromFile(String filePath, String prompt) {
        if (languageModel == null) return "Error: AI Model not initialized.";

        try {
            Path path = Path.of(filePath);
            String fileName = path.getFileName().toString().toLowerCase();

            if (fileName.endsWith(".pdf")) {
                // Read PDF and encode for multimodal processing
                byte[] fileBytes = Files.readAllBytes(path);
                String base64Data = Base64.getEncoder().encodeToString(fileBytes);

                UserMessage userMessage = UserMessage.from(
                    TextContent.from(prompt),
                    PdfFileContent.from(base64Data)
                );

                Response<AiMessage> response = languageModel.generate(userMessage);
                return response.content().text();
            } else if (fileName.endsWith(".txt")) {
                // Read text file as a standard string
                String fileContent = Files.readString(path);
                return languageModel.generate(prompt + "\n\n" + fileContent);
            } else {
                return "Error: Unsupported file type. Please use .pdf or .txt";
            }
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    // ============= Resume Scanning & Analysis Methods =============

    /**
     * Scans a resume and extracts structured information using AI.
     * 
     * @param resumeText The raw text extracted from the resume
     * @return Structured resume data as JSON string
     */
    public String scanResume(String resumeText) {
        if (languageModel == null) {
            logger.warning("ChatLanguageModel not initialized. Returning empty response.");
            return "{}";
        }

        try {
            logger.info("Starting resume scan with AI...");

            String extractionPrompt = buildExtractionPrompt(resumeText);
            String jsonResponse = languageModel.generate(extractionPrompt);

            logger.info("Resume scan completed successfully");
            return jsonResponse;

        } catch (Exception e) {
            logger.severe("Error during resume scanning: " + e.getMessage());
            e.printStackTrace();
            return "{}";
        }
    }

    /**
     * Analyzes match between resume and job description.
     * Extracts key achievements that are relevant to the position.
     * 
     * @param resumeText Resume content
     * @param jobDescription Job posting content
     * @return List of key selling points (up to 5)
     */
    public List<String> analyzeJobMatch(String resumeText, String jobDescription) {
        if (languageModel == null) {
            logger.warning("ChatLanguageModel not initialized. Returning empty list.");
            return new ArrayList<>();
        }

        try {
            logger.info("Analyzing resume-job match...");

            String matchPrompt = String.format("""
                    Analyze the following resume and job description. Extract the 5 most important \
                    qualifications, skills, and achievements from the resume that match the job requirements. \
                    Return ONLY a JSON array of strings with these key points, formatted like: \
                    ["point1", "point2", "point3", "point4", "point5"]
                    
                    RESUME:
                    %s
                    
                    JOB DESCRIPTION:
                    %s
                    """, resumeText, jobDescription);

            String response = languageModel.generate(matchPrompt);
            return parseJsonArray(response);

        } catch (Exception e) {
            logger.severe("Error during job match analysis: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Builds the prompt for extracting resume information.
     */
    private String buildExtractionPrompt(String resumeText) {
        return String.format("""
                Extract all relevant information from the following resume and return it as JSON. \
                The JSON should have the following structure (use null for missing fields):
                {
                  "fullName": "...",
                  "email": "...",
                  "phone": "...",
                  "summary": "...",
                  "skills": ["skill1", "skill2", ...],
                  "education": ["degree1", "degree2", ...],
                  "certifications": ["cert1", "cert2", ...],
                  "workExperience": [
                    {
                      "jobTitle": "...",
                      "company": "...",
                      "startDate": "...",
                      "endDate": "...",
                      "responsibilities": ["resp1", "resp2", ...]
                    }
                  ]
                }
                
                Important:
                1. Extract all skills mentioned in the resume as separate array items
                2. For work experience, extract all job positions with details
                3. Include every education entry
                4. List all certifications and licenses
                5. Return ONLY valid JSON, no additional text
                
                RESUME TEXT:
                %s
                """, resumeText);
    }

    /**
     * Parses a JSON array response.
     */
    private List<String> parseJsonArray(String response) {
        try {
            String cleanJson = response
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            // Simple JSON array parsing
            List<String> points = new ArrayList<>();
            String stripped = cleanJson.replaceAll("[\\[\\]]", "");
            for (String item : stripped.split(",")) {
                String cleaned = item.trim()
                        .replaceAll("\"", "")
                        .replaceAll("'", "");
                if (!cleaned.isEmpty()) {
                    points.add(cleaned);
                }
            }
            return points;

        } catch (Exception e) {
            logger.warning("Failed to parse match points: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ============= Cover Letter Generation Methods =============

    /**
     * Matches resume qualifications to job requirements.
     * 
     * @param resume Resume content
     * @param jobDetails Job description
     * @return Analysis of matching qualifications
     */
    private String matchQualification(String resume, String jobDetails) {
        if (languageModel == null) {
            logger.warning("ChatLanguageModel not initialized.");
            return "";
        }

        String matchPrompt = """
        You are a professional Job Recruiter, and you need to analyse a candidates RESUME and the required JOBDETAILS. Find the 3 most important qualifications on the resume related to the job position.
        --- RESUME ---
        {{RESUME}}

        --- JOBDETAILS ---
        {{JOBDETAILS}}
        """;

        String finalPrompt = matchPrompt
            .replace("{{RESUME}}", resume)
            .replace("{{JOBDETAILS}}", jobDetails);

        return languageModel.generate(finalPrompt);
    }

    /**
     * Generates a cover letter based on resume and job details.
     * 
     * @param matchAnalysis Analysis of matching qualifications
     * @param jobDetails Job description
     * @return Generated cover letter
     */
    private String writeCoverLetter(String matchAnalysis, String jobDetails) {
        if (languageModel == null) {
            logger.warning("ChatLanguageModel not initialized.");
            return "";
        }

        String coverLetterPrompt = """
        You are a professional resume writer. Take the ANALYSIS and the JOBDETAILS below and write a cover letter following the rules below:

        1. No use of generic phrases;
        2. Focus on the value the candidate brings to the company;
        3. Keep the following structure: The hook: establish why you want this specific job at this specific company; The pitch: in 1 to 2 paragraphs provide the evidence to why you would be the best option to solve the company's problems without repeating the resume's content; The close: reiterate the enthusiasm for the position and actively ask for an interview opportunity.
        4. Output only the body of the cover letter.

        --- ANALYSIS ---
        {{ANALYSIS}}

        --- JOBDETAILS ---
        {{JOB}}
        """;

        String finalPrompt = coverLetterPrompt
            .replace("{{ANALYSIS}}", matchAnalysis)
            .replace("{{JOB}}", jobDetails);

        return languageModel.generate(finalPrompt);
    }

    /**
     * Generates a complete cover letter from resume and job description.
     * 
     * @param resume Resume content
     * @param jobDetails Job description
     * @return Generated cover letter
     */
    public String generateCoverLetter(String resume, String jobDetails) {
        if (languageModel == null) {
            logger.warning("Cannot generate cover letter: ChatLanguageModel not initialized.");
            return "";
        }

        logger.info("Matching Resume and Job Details...");
        String analysis = matchQualification(resume, jobDetails);

        logger.info("Drafting Cover Letter...");
        String coverLetter = writeCoverLetter(analysis, jobDetails);

        return coverLetter;
    }
}
