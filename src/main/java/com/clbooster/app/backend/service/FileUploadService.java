package com.clbooster.app.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public abstract class FileUploadService {

    protected final FileRepository fileRepository;

    protected FileUploadService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    /**
     * Template method: validates and prepares the file, delegates actual
     * persistence to subclasses.
     */
    public void saveFile(MultipartFile file) throws Exception {
        validate(file);

        StoredFile storedFile = new StoredFile(file.getOriginalFilename(), file.getContentType(), file.getBytes());

        persist(storedFile);
    }

    protected void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }
    }

    /**
     * Hook for subclasses to define how the file is stored (DB, cloud, filesystem,
     * etc.)
     */

    protected abstract void persist(StoredFile file);
}
