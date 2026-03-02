package com.clbooster.app.backend.service.profile;

import java.io.*;
import java.nio.file.*;
import java.util.List;

public class CoverLetterService {

    private static String BASE_PATH = System.getenv("STORAGE_PATH") != null ? System.getenv("STORAGE_PATH")
            : "storage/coverletters/";

    private CoverLetterDAO coverLetterDAO;

    public CoverLetterService() {
        this.coverLetterDAO = new CoverLetterDAO();
        createStorageDirectoryIfNeeded();
    }

    public int saveCoverLetter(int pin, byte[] fileContent, String extension) {
        try {
            String fileName = pin + "_" + System.currentTimeMillis() + "." + extension;
            String filePath = BASE_PATH + fileName;

            Files.write(Paths.get(filePath), fileContent);

            return coverLetterDAO.addCoverLetter(pin, filePath);

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public byte[] readCoverLetter(int id) {
        CoverLetter cl = coverLetterDAO.getCoverLetterById(id);
        if (cl == null) {
            return null;
        }

        try {
            return Files.readAllBytes(Paths.get(cl.getFilePath()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateCoverLetter(int id, byte[] newContent, String extension) {
        CoverLetter cl = coverLetterDAO.getCoverLetterById(id);
        if (cl == null) {
            return false;
        }

        try {
            String fileName = cl.getPin() + "_" + System.currentTimeMillis() + "." + extension;
            String newFilePath = BASE_PATH + fileName;

            Files.write(Paths.get(newFilePath), newContent);
            Files.deleteIfExists(Paths.get(cl.getFilePath()));

            return coverLetterDAO.updateFilePath(id, newFilePath);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCoverLetter(int id) {
        CoverLetter cl = coverLetterDAO.getCoverLetterById(id);
        if (cl == null) {
            return false;
        }

        try {
            Files.deleteIfExists(Paths.get(cl.getFilePath()));
            return coverLetterDAO.deleteCoverLetter(id);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<CoverLetter> getCoverLetters(int pin) {
        return coverLetterDAO.getCoverLettersByPin(pin);
    }

    private void createStorageDirectoryIfNeeded() {
        File dir = new File(BASE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public String getBasePath() {
        return BASE_PATH;
    }
}