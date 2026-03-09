package com.clbooster.aiservice;

import java.util.Scanner;

import com.clbooster.aiservice.AIService;
import com.clbooster.aiservice.Exporter;
import com.clbooster.aiservice.Parser;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String apiKey = System.getenv("API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.out.print("⚠️ API Key not found in environment. Please paste your Google API Key: ");
            apiKey = scanner.nextLine();
        }
        Parser parser = new Parser();
        AIService aiService = new AIService(apiKey);
        Exporter exporter = new Exporter();

        System.out.println("Resume Cover Letter Generator");

        System.out.print("Please type the path to your resume PDF: ");
        String rawPath = scanner.nextLine();
        String resumePath = rawPath.replace("\"", "").replace("'", "").trim();

        System.out.println("Paste the Job Description here (one line): ");
        String jobDescription = scanner.nextLine();

        System.out.print("Name your output file (e.g. Letter.docx): ");
        String outputPath = scanner.nextLine();

        System.out.println("Reading...");
        String resumeText = parser.parseFileToJson(resumePath);

        System.out.println("Thinking...");
        String coverLetter = aiService.generateCoverLetter(resumeText, jobDescription);

        exporter.saveAsDoc(coverLetter, outputPath);

        System.out.println("Done! Saved to " + outputPath);

        scanner.close();
    }
}
