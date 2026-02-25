package com.clbooster.aiservice;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Parser {

    public String parseFileToJson(String filePath) {
        try {
            Path path = Paths.get(filePath);
            ApacheTikaDocumentParser documentToParse = new ApacheTikaDocumentParser();
            Document document = FileSystemDocumentLoader.loadDocument(path, documentToParse);
            return document.text();
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse file" + filePath, e);
        }
    }
}
