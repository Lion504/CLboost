package com.clbooster.aiservice;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Lazy
public class AIService {

    private static final Logger log = LoggerFactory.getLogger(AIService.class);

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
                You are a professional Job Recruiter and Strategic Talent Headhunter.
                Extract structured insights from the RESUME based on JOBDETAILS.

                Return ONLY JSON:
                {
                  "key_requirements": ["..."],
                  "matching_skills": ["..."],
                  "relevant_achievements": [
                    "achievement with measurable impact",
                    "project or experience aligned with job"
                  ],
                  "value_summary": "why this candidate fits THIS job"
                }

                Rules:
                - Use only information provided
                - Prefer measurable results
                - No generic phrases
                - No hallucination, If information is missing, omit it
                - Highlight top 3 technical skills/keywords from job details present in resume
                - Identify one specific achievement that solves a job pain point

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
                You are an Expert Career Copywriter.
                Take the ANALYSIS and JOBDETAILS below and write a cover letter following the rules:

                {{TONE_INSTRUCTION}}

                RULES:
                1. Never use generic AI filler phrases like: "I am excited to apply",
                   "I believe I would be a great fit", "leverage my skills",
                   "passionate about", "dynamic team", "cutting-edge technologies",
                   "I am writing to apply", "To whom it may concern", "hardworking individual",
                   "think outside the box", "perfect fit".
                2. Do not follow a generic cover letter template; every sentence must feel written for this job and this company only.
                3. Output only the body of the cover letter; do not include addresses or dates.
                4. Cover letter length: Minimum 250 words, Maximum 400 words.
                5. Focus on the value the candidate brings to the company.
                6. Connect candidate's experience or projects directly to key requirements in the analysis.
                7. Cover letter should complement the matched analysis content, not duplicate it.
                8. Start with a hook referencing a company goal/problem, using "value_summary".
                9. In 1-2 paragraphs, provide evidence using "matching_skills" or "relevant_achievements" without repeating the resume.
                10. Close by reiterating enthusiasm and confidently requesting an interview opportunity.

                --- ANALYSIS ---
                {{ANALYSIS}}

                --- JOBDETAILS ---
                {{JOB}}
                """;

        String finalPrompt = coverLetterPrompt.replace("{{TONE_INSTRUCTION}}", toneInstruction(tone))
                .replace("{{ANALYSIS}}", matchAnalysis).replace("{{JOB}}", jobDetails);

        return getLanguageModel().generate(finalPrompt);
    }

    public String generateCoverLetter(String resume, String jobDetails) {
        return generateCoverLetter(resume, jobDetails, "Professional");
    }

    public String generateCoverLetter(String resume, String jobDetails, String tone) {
        log.info("Matching Resume and Job Details...");
        String rawAnalysis = matchQualification(resume, jobDetails);
        String analysis = rawAnalysis.replaceAll("(?s)```json\\s*", "").replaceAll("(?s)```\\s*", "").trim();

        log.info("Drafting Cover Letter (tone: {})...", tone);
        return writeCoverLetter(analysis, jobDetails, tone);
    }
}