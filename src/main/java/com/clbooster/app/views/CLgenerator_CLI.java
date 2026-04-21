package com.clbooster.app.views;

import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.clbooster.app.backend.service.profile.*;
import com.clbooster.aiservice.AIService;
import com.clbooster.aiservice.Exporter;
import com.clbooster.aiservice.Parser;

import java.util.Scanner;
import java.util.logging.Logger;

public class CLgenerator_CLI {
    private static final String MSG_CHOOSE_OPTION = "Choose an option: ";
    private static final String MSG_INVALID_OPTION = "Invalid option. Please try again.";
    private static final String MSG_NOT_LOGGED_IN = "Error: Not logged in";
    private static final String MSG_NOT_SET = "Not set";

    private static final Logger LOGGER = Logger.getLogger(CLgenerator_CLI.class.getName());

    private static AuthenticationService authService;
    private static ProfileService profileService;
    private static UserDAO userDAO;
    private static Scanner scanner;
    private static AIService aiService;
    private static Parser parser;
    private static Exporter exporter;

    public static void main(String[] args) {
        authService = new AuthenticationService();
        profileService = new ProfileService();
        userDAO = new UserDAO();
        scanner = new Scanner(System.in);

        // Initialize AI services
        initializeAIServices();

        LOGGER.info("_______________________________________");
        LOGGER.info("   CL GENERATOR - COMMAND LINE");
        LOGGER.info("_______________________________________\n");

        boolean running = true;
        while (running) {
            if (!authService.isLoggedIn()) {
                // Show main menu when not logged in
                running = showMainMenu();
            } else {
                // Show user menu when logged in
                running = showUserMenu();
            }
        }

        scanner.close();
        LOGGER.info("\nThank you for using CL Generator. Goodbye!");
    }

    private static void initializeAIServices() {
        String apiKey = System.getenv("GEMINI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            LOGGER.info("⚠️ API Key not found in environment. Please paste your Google API Key: ");
            apiKey = scanner.nextLine();
        }

        parser = new Parser();
        aiService = new AIService(apiKey);
        exporter = new Exporter();
    }

    // Main menu (not logged in)
    private static boolean showMainMenu() {
        LOGGER.info("\n=== MAIN MENU ===");
        LOGGER.info("1. Login");
        LOGGER.info("2. Register");
        LOGGER.info("3. Exit");
        LOGGER.info(MSG_CHOOSE_OPTION);

        String choice = scanner.nextLine().trim();

        switch (choice) {
        case "1":
            handleLogin();
            break;
        case "2":
            handleRegistration();
            break;
        case "3":
            return false;
        default:
            LOGGER.info(MSG_INVALID_OPTION);
        }
        return true;
    }

    // (logged in)
    private static boolean showUserMenu() {
        LOGGER.info("\n=== USER MENU ===");
        LOGGER.info("1. Profile");
        LOGGER.info("2. Generate Cover Letter");
        LOGGER.info("3. Logout");
        LOGGER.info("4. Exit");
        LOGGER.info(MSG_CHOOSE_OPTION);

        String choice = scanner.nextLine().trim();

        switch (choice) {
        case "1":
            // Show profile submenu
            boolean continueAfterProfile = handleProfileMenu();
            if (!continueAfterProfile) {
                // User deleted account, return to main menu
                return true;
            }
            break;
        case "2":
            handleCoverLetterGeneration();
            break;
        case "3":
            authService.logout();
            break;
        case "4":
            authService.logout();
            return false;
        default:
            LOGGER.info(MSG_INVALID_OPTION);
        }
        return true;
    }

    private static void handleCoverLetterGeneration() {
        int pin = authService.getCurrentUserPin();
        if (pin == -1) {
            LOGGER.info(MSG_NOT_LOGGED_IN);
            return;
        }

        LOGGER.info("\n=== COVER LETTER GENERATION ===");

        // Check if profile exists
        Profile profile = profileService.getProfile(pin);
        if (profile == null) {
            LOGGER.info("⚠️ No profile found. Please create a profile first.");
            return;
        }

        // Ask for resume path
        LOGGER.info("Enter path to your resume (PDF format): ");
        String rawPath = scanner.nextLine();
        String resumePath = rawPath.replace("\"", "").replace("'", "").trim();

        // Ask for job description
        LOGGER.info("Paste the job description (press Enter twice when done):");
        StringBuilder jobDescription = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).isEmpty()) {
            jobDescription.append(line).append("\n");
        }

        // Ask for output filename
        LOGGER.info("Enter output filename (e.g., MyCoverLetter.docx): ");
        String outputPath = scanner.nextLine().trim();
        if (!outputPath.endsWith(".docx")) {
            outputPath += ".docx";
        }

        LOGGER.info("\n📄 Reading resume...");
        try {
            String resumeText = parser.parseFileToJson(resumePath);

            LOGGER.info("🤖 Analyzing resume and job description...");
            LOGGER.info("✍️ Generating cover letter...");

            String coverLetter = aiService.generateCoverLetter(resumeText, jobDescription.toString());

            LOGGER.info("💾 Saving cover letter...");
            exporter.saveAsDoc(coverLetter, outputPath);

            LOGGER.info("\n✅ Cover letter generated successfully!");
            LOGGER.info("📁 Saved to: " + outputPath);

            // Ask if user wants to preview the letter
            LOGGER.info("\nPreview the generated cover letter? (y/n): ");
            if (scanner.nextLine().trim().toLowerCase().startsWith("y")) {
                LOGGER.info("\n" + coverLetter);
            }

        } catch (Exception e) {
            LOGGER.info("❌ Error generating cover letter: " + e.getMessage());
        }
    }

    // Profile submenu
    private static boolean handleProfileMenu() {
        int pin = authService.getCurrentUserPin();
        if (pin == -1) {
            LOGGER.info(MSG_NOT_LOGGED_IN);
            return true;
        }

        boolean inProfileMenu = true;
        while (inProfileMenu) {

            profileService.displayProfile(pin);

            LOGGER.info("\n=== PROFILE MENU ===");
            LOGGER.info("1. Edit Profile");
            LOGGER.info("2. View Profile Details");
            LOGGER.info("3. Delete Account");
            LOGGER.info("4. Back to Main Menu");
            LOGGER.info(MSG_CHOOSE_OPTION);

            String choice = scanner.nextLine().trim();

            switch (choice) {
            case "1":
                handleEditProfile();
                break;
            case "2":
                viewProfileDetails(pin);
                break;
            case "3":
                boolean accountDeleted = handleDeleteAccount();
                if (accountDeleted) {
                    // Account deleted, logout and return false
                    return false;
                }
                break;
            case "4":
                // Go back to user menu
                inProfileMenu = false;
                break;
            default:
                LOGGER.info(MSG_INVALID_OPTION);
            }
        }
        return true; // Continue in user menu
    }

    private static void viewProfileDetails(int pin) {
        Profile profile = profileService.getProfile(pin);
        if (profile != null) {
            LOGGER.info("\n=== PROFILE DETAILS ===");
            LOGGER.info("Experience Level: " + profile.getExperienceLevel());
            LOGGER.info("Tools: " + profile.getTools());
            LOGGER.info("Skills: " + profile.getSkills());
            LOGGER.info("Portfolio/Link: " + profile.getLink());
            LOGGER.info("Profile Email: " + profile.getProfileEmail());

            // Ask if user wants to use profile data for cover letter generation
            LOGGER.info("\nUse this profile data to enhance cover letter generation? (y/n): ");
            if (scanner.nextLine().trim().toLowerCase().startsWith("y")) {
                LOGGER.info("✓ Profile data will be used when generating cover letters.");
            }
        } else {
            LOGGER.info("No profile data found.");
        }
    }

    private static void handleLogin() {
        LOGGER.info("\n=== LOGIN ===");
        LOGGER.info("Username: ");
        String username = scanner.nextLine().trim();

        LOGGER.info("Password: ");
        String password = scanner.nextLine().trim();

        authService.login(username, password);

        if (authService.isLoggedIn()) {
            LOGGER.info("✓ Login successful! Welcome, " + username);
        }
    }

    private static void handleRegistration() {
        LOGGER.info("\n=== REGISTRATION ===");

        LOGGER.info("Email: ");
        String email = scanner.nextLine().trim();

        LOGGER.info("Username: ");
        String username = scanner.nextLine().trim();

        AuthenticationService.showPasswordRequirements();
        LOGGER.info("");
        LOGGER.info("Password: ");
        String password = scanner.nextLine().trim();

        LOGGER.info("Confirm Password: ");
        String confirmPassword = scanner.nextLine().trim();

        if (!password.equals(confirmPassword)) {
            LOGGER.info("Error: Passwords do not match");
            return;
        }

        LOGGER.info("First Name: ");
        String firstName = scanner.nextLine().trim();

        LOGGER.info("Last Name: ");
        String lastName = scanner.nextLine().trim();

        boolean registered = authService.register(email, username, password, firstName, lastName);

        if (registered) {
            LOGGER.info("✓ Registration successful! You can now log in.");
        }
    }

    private static void handleEditProfile() {
        int pin = authService.getCurrentUserPin();
        if (pin == -1) {
            LOGGER.info(MSG_NOT_LOGGED_IN);
            return;
        }

        LOGGER.info("\n=== EDIT PROFILE ===");
        LOGGER.info("(Press Enter to skip any field you don't want to change)");

        Profile currentProfile = profileService.getProfile(pin);
        if (currentProfile == null) {
            LOGGER.info("Error: Could not load current profile");
            return;
        }

        // Experience Level
        LOGGER.info("Experience Level (current: "
                + (currentProfile.getExperienceLevel() != null ? currentProfile.getExperienceLevel() : MSG_NOT_SET)
                + "): ");
        String experienceLevel = scanner.nextLine().trim();
        if (experienceLevel.isEmpty()) {
            experienceLevel = currentProfile.getExperienceLevel();
        }

        // Tools
        LOGGER.info("Tools/Technologies (current: "
                + (currentProfile.getTools() != null ? currentProfile.getTools() : MSG_NOT_SET) + "): ");
        String tools = scanner.nextLine().trim();
        if (tools.isEmpty()) {
            tools = currentProfile.getTools();
        }

        // Skills
        LOGGER.info("Skills (current: "
                + (currentProfile.getSkills() != null ? currentProfile.getSkills() : MSG_NOT_SET) + "): ");
        String skills = scanner.nextLine().trim();
        if (skills.isEmpty()) {
            skills = currentProfile.getSkills();
        }

        // Link
        LOGGER.info("Link/Portfolio URL (current: "
                + (currentProfile.getLink() != null ? currentProfile.getLink() : MSG_NOT_SET) + "): ");
        String link = scanner.nextLine().trim();
        if (link.isEmpty()) {
            link = currentProfile.getLink();
        }

        // Profile Email
        LOGGER.info("Profile Email (current: "
                + (currentProfile.getProfileEmail() != null ? currentProfile.getProfileEmail() : MSG_NOT_SET) + "): ");
        String profileEmail = scanner.nextLine().trim();
        if (profileEmail.isEmpty()) {
            profileEmail = currentProfile.getProfileEmail();
        }

        // Confirm update
        LOGGER.info("\nSave changes? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("y") || confirm.equals("yes")) {
            profileService.updateProfile(pin, experienceLevel, tools, skills, link, profileEmail);
            LOGGER.info("✓ Profile updated successfully!");
        } else {
            LOGGER.info("Changes discarded.");
        }
    }

    private static boolean handleDeleteAccount() {
        int pin = authService.getCurrentUserPin();
        if (pin == -1) {
            LOGGER.info(MSG_NOT_LOGGED_IN);
            return false;
        }

        LOGGER.info("\n=== DELETE ACCOUNT ===");
        LOGGER.info("⚠ WARNING: This will permanently delete your account!");

        LOGGER.info("\nAre you sure you want to delete your account? (yes/no): ");
        String confirm1 = scanner.nextLine().trim().toLowerCase();

        if (!confirm1.equals("yes")) {
            LOGGER.info("Account deletion cancelled.");
            return false;
        }

        LOGGER.info("Type your username to confirm deletion: ");
        String usernameConfirm = scanner.nextLine().trim();

        if (!usernameConfirm.equals(authService.getCurrentUser().getUsername())) {
            LOGGER.info("Username does not match. Account deletion cancelled.");
            return false;
        }

        // Delete the user account (this will delete profile and user's data)
        if (userDAO.deleteUser(authService.getCurrentUser())) {
            LOGGER.info("\n✓ Account deleted successfully.");
            LOGGER.info("All your data has been permanently removed.");
            LOGGER.info("Thank you for using CL Generator. Goodbye!");

            // Logout the user
            authService.logout();
            return true;
        } else {
            LOGGER.info("Error: Failed to delete account. Please try again later.");
            return false;
        }
    }
}
