package com.clbooster.aiservice;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import java.io.FileOutputStream;
import java.io.IOException;

public class Exporter {

    public void saveAsDoc (String content, String outputPath) {
        try (XWPFDocument document = new XWPFDocument()) {
            String[] paragraphs = content.split("\n");
            for (String line : paragraphs) {
                if(!line.trim().isEmpty()) {
                    XWPFParagraph paragraph = document.createParagraph();
                    XWPFRun run = paragraph.createRun();
                    run.setText(line);
                }
            }
            try (FileOutputStream out = new FileOutputStream(outputPath)) {
                document.write(out);
            }
            System.out.println("File saved on path: " + outputPath);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to save document.");
        }
    }
}
