package com.clbooster.app.backend.service.profile;

import java.io.*;
import java.nio.file.*;
import java.util.List;

public class CoverLetterService {

    // Use env variable for Docker, fallback to local path for dev
    private static final String BASE_PATH = System.getenv("STORAGE_PATH") != null ? System.getenv("STORAGE_PATH")
            : "storage/coverletters/";

    private CoverLetterDAO coverLetterDAO;

    public CoverLetterService() {
        this.coverLetterDAO = new CoverLetterDAO();
        createStorageDirectoryIfNeeded();
    }

    // Saves file to disk and records path in DB
    public int saveCoverLetter(int pin, byte[] fileContent, String extension) {
        try {
            // Build file name: pin_timestamp.pdf or pin_timestamp.docx
            String fileName = pin + "_" + System.currentTimeMillis() + "." + extension;
            String filePath = BASE_PATH + fileName;

            // Write file to disk
            Files.write(Paths.get(filePath), fileContent);

            // Save path to db
            int id = coverLetterDAO.addCoverLetter(pin, filePath);
            if (id != -1) {
                System.out.println("✓ Cover letter saved: " + filePath);
            } else {
                System.out.println("Error: Failed to save cover letter record to DB");
            }
            return id;

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Failed to write cover letter file to disk");
            return -1;
        }
    }

    // Reads file from disk using path stored in DB
    public byte[] readCoverLetter(int id) {
        CoverLetter cl = coverLetterDAO.getCoverLetterById(id);
        if (cl == null) {
            System.out.println("Error: Cover letter not found in DB for id: " + id);
            return null;
        }

        try {
            byte[] content = Files.readAllBytes(Paths.get(cl.getFilePath()));
            System.out.println("✓ Cover letter read: " + cl.getFilePath());
            return content;

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Failed to read file at path: " + cl.getFilePath());
            return null;
        }
    }

    // DB timestamp auto-updates and
    public boolean updateCoverLetter(int id, byte[] newContent, String extension) {
        CoverLetter cl = coverLetterDAO.getCoverLetterById(id);
        if (cl == null) {
            System.out.println("Error: Cover letter not found for id: " + id);
            return false;
        }

        try {
            // Build new file path with fresh timestamp
            String fileName = cl.getPin() + "_" + System.currentTimeMillis() + "." + extension;
            String newFilePath = BASE_PATH + fileName;
            Files.write(Paths.get(newFilePath), newContent);

            // Delete old file
            Files.deleteIfExists(Paths.get(cl.getFilePath()));

            // Update path in DB — this also triggers Timestamp_edited update
            boolean updated = coverLetterDAO.updateFilePath(id, newFilePath);
            if (updated) {
                System.out.println("✓ Cover letter updated: " + newFilePath);
            } else {
                System.out.println("Error: Failed to update cover letter path in DB");
            }
            return updated;

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Failed to update cover letter file on disk");
            return false;
        }
    }

    public boolean deleteCoverLetter(int id) {
        CoverLetter cl = coverLetterDAO.getCoverLetterById(id);
        if (cl == null) {
            System.out.println("Error: Cover letter not found for id: " + id);
            return false;
        }

        try {
            Files.deleteIfExists(Paths.get(cl.getFilePath()));
            boolean deleted = coverLetterDAO.deleteCoverLetter(id);
            if (deleted) {
                System.out.println("✓ Cover letter deleted: " + cl.getFilePath());
            }
            return deleted;

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Failed to delete cover letter file from disk");
            return false;
        }
    }

    // Get all cover letters simple data for a user
    public List<CoverLetter> getCoverLetters(int pin) {
        return coverLetterDAO.getCoverLettersByPin(pin);
    }

    // Creates storage directory if it doesn't exist
    private void createStorageDirectoryIfNeeded() {
        File dir = new File(BASE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("✓ Storage directory created: " + BASE_PATH);
        }
    }

    public String getBasePath() {
        return BASE_PATH;
    }
}