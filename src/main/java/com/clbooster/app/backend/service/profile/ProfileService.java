package com.clbooster.app.backend.service.profile;

public class ProfileService {
    private ProfileDAO profileDAO;

    public ProfileService() {
        this.profileDAO = new ProfileDAO();
    }

    public Profile getProfile(int pin) {
        Profile profile = profileDAO.getProfileByPin(pin);
        if (profile == null) {
            System.out.println("Error: Profile not found for PIN: " + pin);
        }
        return profile;
    }

    public boolean updateProfile(int pin, String experienceLevel, String tools, String skills, String link,
            String profileEmail) {
        if (profileEmail != null && !profileEmail.trim().isEmpty()) {
            if (!isValidEmail(profileEmail)) {
                System.out.println("Error: Invalid email format");
                return false;
            }
        }
        // Create profile object
        Profile profile = new Profile(pin, experienceLevel, tools, skills, link, profileEmail);

        if (profileDAO.updateProfile(profile)) {
            System.out.println("✓ Profile updated successfully!");
            return true;
        } else {
            System.out.println("Error: Failed to update profile");
            return false;
        }
    }

    public void displayProfile(int pin) {
        Profile profile = profileDAO.getProfileByPin(pin);

        if (profile == null) {
            System.out.println("Error: Profile not found");
            return;
        }

        System.out.println("\n_____________________________________");
        System.out.println("           YOUR PROFILE");
        System.out.println("______________________________________");
        // System.out.println("PIN: " + profile.getPin());
        System.out.println("Experience Level: "
                + (profile.getExperienceLevel() != null ? profile.getExperienceLevel() : "(Not set)"));
        System.out.println("Tools:            " + (profile.getTools() != null ? profile.getTools() : "(Not set)"));
        System.out.println("Skills:           " + (profile.getSkills() != null ? profile.getSkills() : "(Not set)"));
        System.out.println("Link:             " + (profile.getLink() != null ? profile.getLink() : "(Not set)"));
        System.out.println(
                "Profile Email:    " + (profile.getProfileEmail() != null ? profile.getProfileEmail() : "(Not set)"));
        System.out.println("_____________________________________\n");
    }

    public boolean updateCVTimestamp(int pin) {
        if (profileDAO.updateCVTimestamp(pin)) {
            System.out.println("✓ CV timestamp updated");
            return true;
        } else {
            System.out.println("Error: Failed to update CV timestamp");
            return false;
        }
    }

    public boolean profileExists(int pin) {
        return profileDAO.profileExists(pin);
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}