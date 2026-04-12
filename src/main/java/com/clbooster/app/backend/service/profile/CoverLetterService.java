package com.clbooster.app.backend.service.profile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class CoverLetterService {
    private static final Logger log = LoggerFactory.getLogger(CoverLetterService.class);
    private static final Pattern EXTENSION_PATTERN = Pattern.compile("[a-zA-Z0-9]{1,10}");

    private static String BASE_PATH = System.getenv("STORAGE_PATH") != null ? System.getenv("STORAGE_PATH")
            : "storage/coverletters/";

    private CoverLetterDAO coverLetterDAO;

    public CoverLetterService() {
        this.coverLetterDAO = new CoverLetterDAO();
        createStorageDirectoryIfNeeded();
    }

    public int saveCoverLetter(int pin, byte[] fileContent, String extension) {
        try {
            String normalizedExtension = normalizeExtension(extension);
            Path filePath = buildFilePath(pin, normalizedExtension);

            Files.write(filePath, fileContent);

            return coverLetterDAO.addCoverLetter(pin, filePath.toString());

        } catch (IOException | IllegalArgumentException e) {
            log.error("Failed to save cover letter", e);
            return -1;
        }
    }

    public byte[] readCoverLetter(int id) {
        CoverLetter cl = coverLetterDAO.getCoverLetterById(id);
        if (cl == null) {
            return null;
        }

        try {
            Path filePath = resolveStoredFilePath(cl.getFilePath());
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("Failed to read cover letter", e);
            return null;
        }
    }

    public boolean updateCoverLetter(int id, byte[] newContent, String extension) {
        CoverLetter cl = coverLetterDAO.getCoverLetterById(id);
        if (cl == null) {
            return false;
        }

        try {
            String normalizedExtension = normalizeExtension(extension);
            Path newFilePath = buildFilePath(cl.getPin(), normalizedExtension);
            Path oldFilePath = resolveStoredFilePath(cl.getFilePath());

            Files.write(newFilePath, newContent);
            Files.deleteIfExists(oldFilePath);

            return coverLetterDAO.updateFilePath(id, newFilePath.toString());

        } catch (IOException | IllegalArgumentException e) {
            log.error("Failed to update cover letter", e);
            return false;
        }
    }

    public boolean deleteCoverLetter(int id) {
        CoverLetter cl = coverLetterDAO.getCoverLetterById(id);
        if (cl == null) {
            return false;
        }

        try {
            Path filePath = resolveStoredFilePath(cl.getFilePath());
            Files.deleteIfExists(filePath);
            return coverLetterDAO.deleteCoverLetter(id);
        } catch (IOException e) {
            log.error("Failed to delete cover letter", e);
            return false;
        }
    }

    public List<CoverLetter> getCoverLetters(int pin) {
        return coverLetterDAO.getCoverLettersByPin(pin);
    }

    private void createStorageDirectoryIfNeeded() {
        try {
            Files.createDirectories(getBasePathPath());
        } catch (IOException e) {
            log.error("Failed to create cover letter storage directory", e);
        }
    }

    private static String normalizeExtension(String extension) {
        if (extension == null) {
            throw new IllegalArgumentException("File extension is required");
        }
        String normalized = extension.trim();
        if (normalized.startsWith(".")) {
            normalized = normalized.substring(1);
        }
        if (!EXTENSION_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Invalid file extension");
        }
        return normalized.toLowerCase(Locale.ROOT);
    }

    private static Path getBasePathPath() {
        return Paths.get(BASE_PATH).toAbsolutePath().normalize();
    }

    private static Path buildFilePath(int pin, String extension) throws IOException {
        Path basePath = getBasePathPath();
        Files.createDirectories(basePath);
        Path targetPath = basePath.resolve(pin + "_" + System.currentTimeMillis() + "." + extension).normalize();
        if (!targetPath.startsWith(basePath)) {
            throw new IOException("Invalid cover letter storage path");
        }
        return targetPath;
    }

    private static Path resolveStoredFilePath(String filePath) throws IOException {
        if (filePath == null || filePath.isBlank()) {
            throw new IOException("Invalid file path");
        }
        Path basePath = getBasePathPath();
        Path resolved = Paths.get(filePath).toAbsolutePath().normalize();
        if (!resolved.startsWith(basePath)) {
            throw new IOException("Access denied to file path outside storage directory");
        }
        return resolved;
    }

    public String getBasePath() {
        return BASE_PATH;
    }
}