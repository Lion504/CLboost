package com.clbooster.aiservice;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;

public class AIService {

    private final ChatLanguageModel languageModel;

    public AIService(String apiKey) {
        this.languageModel = GoogleAiGeminiChatModel.builder()
        .apiKey(apiKey)
        .modelName("gemini-2.5-flash-lite")
        .temperature(0.7)
        .build();
    }

    private String matchQualification(String resume, String jobDetails) {
        String matchPrompt = """
        "You are a professional Job Recruiter, and you need to analyse a candidates RESUME and the required JOBDETAILS. Find the 3 most important qualifications on the resume related to the job position."
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

    private String writeCoverLetter(String matchAnalysis, String jobDetails) {
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

    public String generateCoverLetter(String resume, String jobDetails) {
        System.out.println("Matching Resume and Job Details...");
        String analysis = matchQualification(resume, jobDetails);

        System.out.println("Drafting Cover Letter...");
        String buildCL = writeCoverLetter(analysis, jobDetails);

        return buildCL;

    }
}
