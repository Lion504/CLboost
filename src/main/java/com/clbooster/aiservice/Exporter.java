package com.clbooster.aiservice;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class Exporter {

    private static final Logger log = LoggerFactory.getLogger(Exporter.class);

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
            log.info("File saved on path: {}", outputPath);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save document.", e);
        }
    }
}
