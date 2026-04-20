package com.clbooster.app.backend.service.profile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class ProfileService {
    private static final String NOT_SET = "(Not set)";

    private static final Logger log = LoggerFactory.getLogger(ProfileService.class);

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
                log.error("Invalid email format");
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
            log.info("Profile updated successfully!");
            return true;
        } catch (Exception e) {
            log.error("Failed to update profile for PIN {}", pin, e);
            return false;
        }
    }

    public User getUpdatedUser(int pin) {
        return userDAO.getUserByPin(pin);
    }

    public Profile getProfile(int pin, Locale locale) {
        Profile profile = profileDAO.getByIdWithFallback(pin, locale, Locale.US);
        if (profile == null) {
            log.error("Profile not found for PIN: {}", pin);
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
     * @deprecated Use
     *             {@link #updateProfile(int, String, String, String, String, String, String, String, Locale)}
     *             instead.
     */
    @Deprecated
    public boolean updateProfile(int pin, String experienceLevel, String tools, String skills, String link,
            String profileEmail, Locale locale) {
        return updateProfile(pin, "", "", experienceLevel, tools, skills, link, profileEmail, locale);
    }

    /**
     * @deprecated Use
     *             {@link #updateProfile(int, String, String, String, String, String, String, String, Locale)}
     *             instead.
     */
    @Deprecated
    public boolean updateProfile(int pin, String experienceLevel, String tools, String skills, String link,
            String profileEmail) {
        return updateProfile(pin, "", "", experienceLevel, tools, skills, link, profileEmail, Locale.getDefault());
    }

    public void displayProfile(int pin) {
        Profile profile = getProfile(pin);

        if (profile == null) {
            log.error("Profile not found");
            return;
        }

        log.info("\n_____________________________________");
        log.info("           YOUR PROFILE");
        log.info("______________________________________");
        log.info("Experience Level: {}", (profile.getExperienceLevel() != null ? profile.getExperienceLevel() : NOT_SET));
        log.info("Tools:            {}", (profile.getTools() != null ? profile.getTools() : NOT_SET));
        log.info("Skills:           {}", (profile.getSkills() != null ? profile.getSkills() : NOT_SET));
        log.info("Link:             {}", (profile.getLink() != null ? profile.getLink() : NOT_SET));
        log.info("Profile Email:    {}", (profile.getProfileEmail() != null ? profile.getProfileEmail() : NOT_SET));
        log.info("_____________________________________\n");
    }

    public boolean updateCVTimestamp(int pin) {
        if (profileDAO.updateCVTimestamp(pin)) {
            log.info("CV timestamp updated");
            return true;
        } else {
            log.error("Failed to update CV timestamp");
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