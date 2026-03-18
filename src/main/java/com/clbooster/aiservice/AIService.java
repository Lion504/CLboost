package com.clbooster.aiservice;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class AIService {

    private ChatLanguageModel languageModel;
    private final String apiKey;

    public AIService(@Value("${spring.ai.vertex.ai.gemini.api-key:}") String apiKey) {
        this.apiKey = apiKey;
        // Defer actual initialization to first use
    }

    private synchronized ChatLanguageModel getLanguageModel() {
        if (languageModel == null) {
            if (apiKey == null || apiKey.isBlank()) {
                throw new IllegalStateException("GEMINI_API_KEY environment variable is not set. "
                        + "Set it and restart the application before generating cover letters.");
            }
            languageModel = GoogleAiGeminiChatModel.builder().apiKey(apiKey).modelName("gemini-2.5-flash-lite")
                    .temperature(0.7).timeout(java.time.Duration.ofSeconds(30)).build();
        }
        return languageModel;
    }

    private String matchQualification(String resume, String jobDetails) {
        String matchPrompt = """
                You are a Strategic Talent Headhunter. Analyze the candidate's RESUME against the JOB DETAILS.
                Extract:
                1. The top 3 technical skills/keywords mentioned in the job post that appear on the resume.
                2. One specific achievement from the resume that proves the candidate can solve a pain point in the job post.

                Keep the analysis concise and data-driven.

                --- RESUME ---
                {{RESUME}}

                --- JOBDETAILS ---
                {{JOBDETAILS}}
                """;

        String finalPrompt = matchPrompt.replace("{{RESUME}}", resume).replace("{{JOBDETAILS}}", jobDetails);

        return getLanguageModel().generate(finalPrompt);
    }

    private String toneInstruction(String tone) {
        if (tone == null || tone.isBlank())
            return "";
        switch (tone.trim()) {
        case "Creative":
            return "TONE: Write in a creative, enthusiastic and bold voice. Show genuine personality, "
                    + "use vivid language, and let the candidate's passion stand out. Best for startups and agencies.";
        case "Storyteller":
            return "TONE: Write in a storytelling narrative voice. Focus on the candidate's journey, "
                    + "key moments of impact, and what drives them. Best for senior and lead roles.";
        default: // Professional
            return "TONE: Write in a formal, structured and strictly professional voice. "
                    + "Keep language concise, confident and business-appropriate. Best for corporate roles.";
        }
    }

    private String writeCoverLetter(String matchAnalysis, String jobDetails, String tone) {
        String coverLetterPrompt = """
                You are an Expert Career Copywriter. Write a high-conversion cover letter using the analysis and jobdetails.

                Rules:
                1. Forbidden phrases: "I am writing to apply," "To whom it may concern," "hardworking individual," "think outside the box," "perfect fit."
                2. Start with a 'Hook' that references a specific company goal or challenge.
                3. Instead of saying "I am a leader," describe a time they led.
                4. Identify a problem mentioned in the job details and explain how the candidate's specific experience is the solution.
                5. Output ONLY the body text. Do not include addresses or dates.

                {{TONE_INSTRUCTION}}

                --- STRATEGIC ANALYSIS ---
                {{ANALYSIS}}

                --- JOB DESCRIPTION ---
                {{JOB}}
                """;

        String finalPrompt = coverLetterPrompt.replace("{{TONE}}", toneInstruction(tone))
                .replace("{{ANALYSIS}}", matchAnalysis).replace("{{JOB}}", jobDetails);

        return getLanguageModel().generate(finalPrompt);
    }

    public String generateCoverLetter(String resume, String jobDetails) {
        return generateCoverLetter(resume, jobDetails, "Professional");
    }

    public String generateCoverLetter(String resume, String jobDetails, String tone) {
        System.out.println("Matching Resume and Job Details...");
        String analysis = matchQualification(resume, jobDetails);

        System.out.println("Drafting Cover Letter (tone: " + tone + ")...");
        return writeCoverLetter(analysis, jobDetails, tone);
    }
}
