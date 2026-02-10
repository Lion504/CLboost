package com.clbooster.aiservice;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * Parser service for extracting text from various file formats using Apache Tika.
 */
@Service
public class Parser {

    private static final Logger logger = Logger.getLogger(Parser.class.getName());

    /**
     * Parses a file and extracts its text content.
     * 
     * @param filePath The absolute path to the file
     * @return The extracted text content
     * @throws RuntimeException if parsing fails
     */
    public String parseFileToJson(String filePath) {
        try {
            logger.info("Parsing file: " + filePath);
            Path path = Paths.get(filePath);
            ApacheTikaDocumentParser documentToParse = new ApacheTikaDocumentParser();
            Document document = FileSystemDocumentLoader.loadDocument(path, documentToParse);
            logger.info("File parsed successfully");
            return document.text();
        }
        catch (Exception e) {
            logger.severe("Unable to parse file " + filePath + ": " + e.getMessage());
            throw new RuntimeException("Unable to parse file " + filePath, e);
        }
    }
}
