package com.clbooster.app.backend.service.profile;
import java.sql.Timestamp;

public class Profile {
    private int pin;
    private String experienceLevel;
    private String tools;
    private String skills;
    private String link;
    private String profileEmail;
    private Timestamp cvLastUpdated;

    public Profile(int pin, String experienceLevel, String tools, String skills, String link, String profileEmail) {
        this.pin = pin;
        this.experienceLevel = experienceLevel;
        this.tools = tools;
        this.skills = skills;
        this.link = link;
        this.profileEmail = profileEmail;
    }

    // Empty constructor
    public Profile() {
    }

    // Getters & setters

    public int getPin() { return pin; }
    public String getExperienceLevel() { return experienceLevel; }
    public String getTools() { return tools; }
    public String getSkills() { return skills; }
    public String getLink() { return link; }
    public String getProfileEmail() { return profileEmail; }
    public Timestamp getCvLastUpdated() { return cvLastUpdated; }

    public void setPin(int pin) { this.pin = pin; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
    public void setTools(String tools) { this.tools = tools; }
    public void setSkills(String skills) { this.skills = skills; }
    public void setLink(String link) { this.link = link; }
    public void setProfileEmail(String profileEmail) { this.profileEmail = profileEmail; }
    public void setCvLastUpdated(Timestamp cvLastUpdated) { this.cvLastUpdated = cvLastUpdated; }

    @Override
    public String toString() {
        return "Profile{" +
                "pin=" + pin +
                ", experienceLevel='" + experienceLevel + '\'' +
                ", tools='" + tools + '\'' +
                ", skills='" + skills + '\'' +
                ", link='" + link + '\'' +
                ", profileEmail='" + profileEmail + '\'' +
                '}';
    }
}