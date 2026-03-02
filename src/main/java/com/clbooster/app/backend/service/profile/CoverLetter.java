package com.clbooster.app.backend.service.profile;

import java.sql.Timestamp;

public class CoverLetter {
    private int id;
    private int pin;
    private Timestamp timestampEdited;
    private String filePath;

    public CoverLetter(int id, int pin, Timestamp timestampEdited, String filePath) {
        this.id = id;
        this.pin = pin;
        this.timestampEdited = timestampEdited;
        this.filePath = filePath;
    }

    public CoverLetter(int pin, String filePath) {
        this.pin = pin;
        this.filePath = filePath;
    }

    public CoverLetter() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPin() { return pin; }
    public void setPin(int pin) { this.pin = pin; }
    public Timestamp getTimestampEdited() { return timestampEdited; }
    public void setTimestampEdited(Timestamp timestampEdited) { this.timestampEdited = timestampEdited; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    @Override
    public String toString() {
        return "CoverLetter{id=" + id + ", pin=" + pin +
                ", timestampEdited=" + timestampEdited +
                ", filePath='" + filePath + "'}";
    }
}
