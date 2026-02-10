package com.clbooster.aiservice;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Exporter service for saving content to DOCX documents using Apache POI.
 */
@Service
public class Exporter {

    private static final Logger logger = Logger.getLogger(Exporter.class.getName());

    /**
     * Saves content to a DOCX file.
     * 
     * @param content The content to save (lines separated by \n)
     * @param outputPath The path where the DOCX file should be saved
     * @throws RuntimeException if saving fails
     */
    public void saveAsDoc(String content, String outputPath) {
        try (XWPFDocument document = new XWPFDocument()) {
            String[] paragraphs = content.split("\n");
            for (String line : paragraphs) {
                if (!line.trim().isEmpty()) {
                    XWPFParagraph paragraph = document.createParagraph();
                    XWPFRun run = paragraph.createRun();
                    run.setText(line);
                }
            }
            try (FileOutputStream out = new FileOutputStream(outputPath)) {
                document.write(out);
            }
            logger.info("File saved successfully to: " + outputPath);
        }
        catch (IOException e) {
            logger.severe("Failed to save document: " + e.getMessage());
            throw new RuntimeException("Failed to save document to " + outputPath, e);
        }
    }
}
