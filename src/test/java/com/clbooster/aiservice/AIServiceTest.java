package com.clbooster.aiservice;

import dev.langchain4j.model.chat.ChatLanguageModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AIServiceTest {

    private AIService aiService;
    private ChatLanguageModel mockModel;

    @BeforeEach
    void setUp() throws Exception {
        // 1. Initialize the service
        aiService = new AIService("fake-api-key");

        // 2. Create a mock for the Language Model
        mockModel = Mockito.mock(ChatLanguageModel.class);

        // 3. Use Reflection to inject the mock into the private final field
        // This ensures JaCoCo sees the code execution without hitting real APIs
        Field field = AIService.class.getDeclaredField("languageModel");
        field.setAccessible(true);
        field.set(aiService, mockModel);
    }

    @Test
    void testGenerateCoverLetter_Success() {
        // Arrange
        String resume = "Java Developer with 5 years experience.";
        String jobDetails = "Looking for a Senior Java Engineer.";
        String mockAnalysis = "Top 3: Java, Spring Boot, Unit Testing.";
        String mockFinalCL = "Dear Hiring Manager, I love Java...";

        // Define behavior for the two internal calls to languageModel.generate()
        when(mockModel.generate(anyString()))
            .thenReturn(mockAnalysis) // First call (matchQualification)
            .thenReturn(mockFinalCL); // Second call (writeCoverLetter)

        // Act
        String result = aiService.generateCoverLetter(resume, jobDetails);

        // Assert
        assertEquals(mockFinalCL, result);

        // Verify the model was called exactly twice
        verify(mockModel, times(2)).generate(anyString());
    }

    @Test
    void testGenerateCoverLetter_WithEmptyInputs() {
        // Testing empty strings to ensure the replace() logic doesn't crash
        // and covers the logic branches for string manipulation.
        when(mockModel.generate(anyString())).thenReturn("Result");

        String result = aiService.generateCoverLetter("", "");

        assertEquals("Result", result);
        verify(mockModel, times(2)).generate(anyString());
    }
}
