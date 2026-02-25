package com.clbooster.app.backend.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Data class representing extracted information from a scanned resume. This
 * class holds the structured data that will be displayed in the confirmation
 * form.
 */
public class ResumeData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fullName;
    private String email;
    private String phone;
    private String summary;
    private List<String> skills;
    private List<WorkExperience> workExperience;
    private List<String> education;
    private List<String> certifications;
    private String rawResumeText;

    public ResumeData() {
        this.skills = new ArrayList<>();
        this.workExperience = new ArrayList<>();
        this.education = new ArrayList<>();
        this.certifications = new ArrayList<>();
    }

    // Getters and Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<WorkExperience> getWorkExperience() {
        return workExperience;
    }

    public void setWorkExperience(List<WorkExperience> workExperience) {
        this.workExperience = workExperience;
    }

    public List<String> getEducation() {
        return education;
    }

    public void setEducation(List<String> education) {
        this.education = education;
    }

    public List<String> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<String> certifications) {
        this.certifications = certifications;
    }

    public String getRawResumeText() {
        return rawResumeText;
    }

    public void setRawResumeText(String rawResumeText) {
        this.rawResumeText = rawResumeText;
    }

    /**
     * Nested class representing a work experience entry
     */
    public static class WorkExperience implements Serializable {
        private static final long serialVersionUID = 1L;

        private String jobTitle;
        private String company;
        private String startDate;
        private String endDate;
        private List<String> responsibilities;

        public WorkExperience() {
            this.responsibilities = new ArrayList<>();
        }

        // Getters and Setters
        public String getJobTitle() {
            return jobTitle;
        }

        public void setJobTitle(String jobTitle) {
            this.jobTitle = jobTitle;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public List<String> getResponsibilities() {
            return responsibilities;
        }

        public void setResponsibilities(List<String> responsibilities) {
            this.responsibilities = responsibilities;
        }
    }

    @Override
    public String toString() {
        return "ResumeData{" + "fullName='" + fullName + '\'' + ", email='" + email + '\'' + ", phone='" + phone + '\''
                + ", skills=" + skills + ", education=" + education + ", certifications=" + certifications + '}';
    }
}
