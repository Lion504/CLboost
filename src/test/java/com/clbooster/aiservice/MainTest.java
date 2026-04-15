package com.clbooster.aiservice;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MainTest {

    @Test
    void main_runsWorkflowAndNormalizesResumePath() {
        String envApiKey = System.getenv("API_KEY");
        String expectedNormalizedPath = "/tmp/resume.pdf";

        String input;
        if (envApiKey == null || envApiKey.isEmpty()) {
            input = "fallback-api-key\n  \"/tmp/resume.pdf\"  \nJob line\noutput.docx\n";
        } else {
            input = "  \"/tmp/resume.pdf\"  \nJob line\noutput.docx\n";
        }

        InputStream originalIn = System.in;
        try (MockedConstruction<Parser> parserConstruction = Mockito.mockConstruction(Parser.class,
                (mock, context) -> when(mock.parseFileToJson(anyString())).thenReturn("parsed-resume"));
                MockedConstruction<AIService> aiConstruction = Mockito.mockConstruction(AIService.class,
                        (mock, context) -> when(mock.generateCoverLetter(anyString(), anyString()))
                                .thenReturn("generated-cover-letter"));
                MockedConstruction<Exporter> exporterConstruction = Mockito.mockConstruction(Exporter.class)) {

            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

            Main.main(new String[0]);

            assertEquals(1, parserConstruction.constructed().size());
            assertEquals(1, aiConstruction.constructed().size());
            assertEquals(1, exporterConstruction.constructed().size());

            Parser parser = parserConstruction.constructed().get(0);
            AIService aiService = aiConstruction.constructed().get(0);
            Exporter exporter = exporterConstruction.constructed().get(0);

            verify(parser).parseFileToJson(expectedNormalizedPath);
            verify(aiService).generateCoverLetter("parsed-resume", "Job line");
            verify(exporter).saveAsDoc("generated-cover-letter", "output.docx");

            assertNotNull(parser);
            assertNotNull(aiService);
            assertNotNull(exporter);
        } finally {
            System.setIn(originalIn);
        }
    }
}
