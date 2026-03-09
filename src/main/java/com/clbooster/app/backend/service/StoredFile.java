package com.clbooster.app.backend.service;

public class StoredFile {

    private String name;
    private String contentType;
    private byte[] data;

    public StoredFile(String name, String contentType, byte[] data) {
        this.name = name;
        this.contentType = contentType;
        this.data = data;
    }

    // getters + setters
    public String getName() {
        return name;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getData() {
        return data;
    }
}