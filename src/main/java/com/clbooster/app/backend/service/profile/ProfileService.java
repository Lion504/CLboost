package com.clbooster.app.backend.service.profile;

import java.util.Locale;

public class ProfileService {
    private ProfileDAO profileDAO;
    private UserDAO userDAO;

    public ProfileService() {
        this.profileDAO = new ProfileDAO();
        this.userDAO = new UserDAO();
    }

    public boolean updateProfile(int pin, String firstName, String lastName, String experienceLevel, String tools,
            String skills, String link, String profileEmail, Locale locale) {
        if (profileEmail != null && !profileEmail.trim().isEmpty()) {
            if (!isValidEmail(profileEmail)) {
                System.out.println("Error: Invalid email format");
                return false;
            }
        }

        // Update identity info (First Name, Last Name, Identity Email)
        userDAO.updateUser(pin, firstName, lastName, profileEmail);

        // Create profile object
        Profile profile = new Profile(pin, experienceLevel, tools, skills, link, profileEmail);
        
        // Save translation and base profile in one go via ProfileDAO
        try {
            profileDAO.saveTranslation(profile, locale);
            System.out.println("✓ Profile updated successfully!");
            return true;
        } catch (Exception e) {
            System.out.println("Error: Failed to update profile - " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public User getUpdatedUser(int pin) {
        return userDAO.getUserByPin(pin);
    }

    public Profile getProfile(int pin, Locale locale) {
        Profile profile = profileDAO.getByIdWithFallback(pin, locale, Locale.US);
        if (profile == null) {
            System.out.println("Error: Profile not found for PIN: " + pin);
        }
        return profile;
    }

    /**
     * @deprecated Use {@link #getProfile(int, Locale)} instead.
     */
    @Deprecated
    public Profile getProfile(int pin) {
        return getProfile(pin, Locale.getDefault());
    }

    /**
     * @deprecated Use {@link #updateProfile(int, String, String, String, String, String, String, String, Locale)} instead.
     */
    @Deprecated
    public boolean updateProfile(int pin, String experienceLevel, String tools, String skills, String link,
            String profileEmail, Locale locale) {
        return updateProfile(pin, "", "", experienceLevel, tools, skills, link, profileEmail, locale);
    }

    /**
     * @deprecated Use {@link #updateProfile(int, String, String, String, String, String, String, String, Locale)} instead.
     */
    @Deprecated
    public boolean updateProfile(int pin, String experienceLevel, String tools, String skills, String link,
            String profileEmail) {
        return updateProfile(pin, "", "", experienceLevel, tools, skills, link, profileEmail, Locale.getDefault());
    }

    public void displayProfile(int pin) {
        Profile profile = getProfile(pin);

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