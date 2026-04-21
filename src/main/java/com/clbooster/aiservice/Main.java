package com.clbooster.aiservice;

import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String apiKey = System.getenv("API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            log.info("⚠️ API Key not found in environment. Please paste your Google API Key: ");
            apiKey = scanner.nextLine();
        }
        Parser parser = new Parser();
        AIService aiService = new AIService(apiKey);
        Exporter exporter = new Exporter();

        log.info("Resume Cover Letter Generator");

        log.info("Please type the path to your resume PDF: ");
        String rawPath = scanner.nextLine();
        String resumePath = rawPath.replace("\"", "").replace("'", "").trim();

        log.info("Paste the Job Description here (one line): ");
        String jobDescription = scanner.nextLine();

        log.info("Name your output file (e.g. Letter.docx): ");
        String outputPath = scanner.nextLine();

        log.info("Reading...");
        String resumeText = parser.parseFileToJson(resumePath);

        log.info("Thinking...");
        String coverLetter = aiService.generateCoverLetter(resumeText, jobDescription);

        exporter.saveAsDoc(coverLetter, outputPath);

        log.info("Done! Saved to {}", outputPath);

        scanner.close();
    }
}
