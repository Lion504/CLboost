package com.clbooster.app.views;

import com.clbooster.app.backend.service.authentication.AuthenticationService;
import com.clbooster.app.backend.service.profile.*;
import com.clbooster.app.backend.service.CoverLetterService;
import com.clbooster.app.backend.service.ResumeService;
import com.clbooster.app.backend.service.document.DocumentService;
import com.clbooster.aiservice.AIService;

import java.util.Scanner;

public class CLgenerator_CLI {
    private static AuthenticationService authService;
    private static ProfileService profileService;
    private static UserDAO userDAO;
    private static CoverLetterService coverLetterService;
    private static Scanner scanner;

    public static void main(String[] args) {
        authService = new AuthenticationService();
        profileService = new ProfileService();
        userDAO = new UserDAO();
        
        // Initialize AI and backend services
        AIService aiService = new AIService(System.getenv("GOOGLE_API_KEY"));
        DocumentService documentService = new DocumentService(null);
        ResumeService resumeService = new ResumeService(aiService, null);
        coverLetterService = new CoverLetterService(aiService, documentService, profileService, resumeService);
        
        scanner = new Scanner(System.in);

        System.out.println("_______________________________________");
        System.out.println("   CL GENERATOR - COMMAND LINE");
        System.out.println("_______________________________________\n");

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
        System.out.println("\nThank you for using CL Generator. Goodbye!");
    }

    // Main menu (not logged in)
    private static boolean showMainMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");

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
                System.out.println("Invalid option. Please try again.");
        }
        return true;
    }

    // (logged in)
    private static boolean showUserMenu() {
        System.out.println("\n=== USER MENU ===");
        System.out.println("1. Profile");
        //System.out.println("2. Edit Profile");
        System.out.println("2. Generate Cover latter");
        System.out.println("3. Logout");
        System.out.println("4. Exit");
        System.out.print("Choose an option: ");

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
                handleGenerateCoverLetter();
                break;
            case "3":
                authService.logout();
                break;
            case "4":
                authService.logout();
                return false;
            default:
                System.out.println("Invalid option. Please try again.");
        }
        return true;
    }

    // Profile submenu
    private static boolean handleProfileMenu() {
        int pin = authService.getCurrentUserPin();
        if (pin == -1) {
            System.out.println("Error: Not logged in");
            return true;
        }

        boolean inProfileMenu = true;
        while (inProfileMenu) {

            profileService.displayProfile(pin);

            System.out.println("\n=== PROFILE MENU ===");
            System.out.println("1. Edit Profile");
            System.out.println("2. Delete Account");
            System.out.println("3. Back to Main Menu");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleEditProfile();
                    break;
                case "2":
                    boolean accountDeleted = handleDeleteAccount();
                    if (accountDeleted) {
                        // Account deleted, logout and return false
                        return false;
                    }
                    break;
                case "3":
                    // Go back to user menu
                    inProfileMenu = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        return true; // Continue in user menu
    }

    private static void handleLogin() {
        System.out.println("\n=== LOGIN ===");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        authService.login(username, password);
    }

    private static void handleRegistration() {
        System.out.println("\n=== REGISTRATION ===");

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        AuthenticationService.showPasswordRequirements();
        System.out.println();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        System.out.print("Confirm Password: ");
        String confirmPassword = scanner.nextLine().trim();

        if (!password.equals(confirmPassword)) {
            System.out.println("Error: Passwords do not match");
            return;
        }

        System.out.print("First Name: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Last Name: ");
        String lastName = scanner.nextLine().trim();

        authService.register(email, username, password, firstName, lastName);
    }

    private static void handleEditProfile() {
        int pin = authService.getCurrentUserPin();
        if (pin == -1) {
            System.out.println("Error: Not logged in");
            return;
        }

        System.out.println("\n=== EDIT PROFILE ===");
        System.out.println("(Press Enter to skip any field you don't want to change)");

        Profile currentProfile = profileService.getProfile(pin);
        if (currentProfile == null) {
            System.out.println("Error: Could not load current profile");
            return;
        }

        // Experience Level
        System.out.print("Experience Level (current: " +
                (currentProfile.getExperienceLevel() != null ? currentProfile.getExperienceLevel() : "Not set") + "): ");
        String experienceLevel = scanner.nextLine().trim();
        if (experienceLevel.isEmpty()) {
            experienceLevel = currentProfile.getExperienceLevel();
        }

        // Tools
        System.out.print("Tools (current: " +
                (currentProfile.getTools() != null ? currentProfile.getTools() : "Not set") + "): ");
        String tools = scanner.nextLine().trim();
        if (tools.isEmpty()) {
            tools = currentProfile.getTools();
        }

        // Skills
        System.out.print("Skills (current: " +
                (currentProfile.getSkills() != null ? currentProfile.getSkills() : "Not set") + "): ");
        String skills = scanner.nextLine().trim();
        if (skills.isEmpty()) {
            skills = currentProfile.getSkills();
        }

        // Link
        System.out.print("Link/Portfolio URL (current: " +
                (currentProfile.getLink() != null ? currentProfile.getLink() : "Not set") + "): ");
        String link = scanner.nextLine().trim();
        if (link.isEmpty()) {
            link = currentProfile.getLink();
        }

        // Profile Email
        System.out.print("Profile Email (current: " +
                (currentProfile.getProfileEmail() != null ? currentProfile.getProfileEmail() : "Not set") + "): ");
        String profileEmail = scanner.nextLine().trim();
        if (profileEmail.isEmpty()) {
            profileEmail = currentProfile.getProfileEmail();
        }

        // Confirm update
        System.out.print("\nSave changes? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("y") || confirm.equals("yes")) {
            profileService.updateProfile(pin, experienceLevel, tools, skills, link, profileEmail);
        } else {
            System.out.println("Changes discarded.");
        }
    }

    private static void handleGenerateCoverLetter() {
        int pin = authService.getCurrentUserPin();
        if (pin == -1) {
            System.out.println("Error: Not logged in");
            return;
        }

        System.out.println("\n=== COVER LETTER GENERATOR ===");

        // Get resume text
        System.out.println("Please provide your resume information.");
        System.out.println("(You can paste your resume text, formatted resume content, or key information)");
        System.out.print("Paste your resume here (press Enter twice when done): ");
        
        StringBuilder resumeText = new StringBuilder();
        String line;
        int emptyLines = 0;
        
        while (emptyLines < 1) {
            line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                emptyLines++;
            } else {
                emptyLines = 0;
                resumeText.append(line).append("\n");
            }
        }

        if (resumeText.length() == 0) {
            System.out.println("Error: Resume cannot be empty");
            return;
        }

        // Get job description
        System.out.println("\nNow paste the job description or job posting.");
        System.out.print("Paste job description here (press Enter twice when done): ");
        
        StringBuilder jobDescription = new StringBuilder();
        emptyLines = 0;
        
        while (emptyLines < 1) {
            line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                emptyLines++;
            } else {
                emptyLines = 0;
                jobDescription.append(line).append("\n");
            }
        }

        if (jobDescription.length() == 0) {
            System.out.println("Error: Job description cannot be empty");
            return;
        }

        // Generate cover letter
        try {
            System.out.println("\n⏳ Generating your cover letter... Please wait.");
            String generatedLetter = coverLetterService.generateCoverLetter(
                    resumeText.toString().trim(),
                    jobDescription.toString().trim()
            );

            if (generatedLetter == null || generatedLetter.isEmpty()) {
                System.out.println("Error: Failed to generate cover letter. Please check your API key and try again.");
                return;
            }

            // Display the generated letter
            System.out.println("\n=== GENERATED COVER LETTER ===");
            System.out.println(generatedLetter);
            System.out.println("\n==============================");

            // Ask if user wants to save it
            System.out.print("\nWould you like to save this cover letter? (y/n): ");
            String savechoice = scanner.nextLine().trim().toLowerCase();

            if (savechoice.equals("y") || savechoice.equals("yes")) {
                try {
                    String storagePath = coverLetterService.storeCoverLetter(generatedLetter, pin);
                    System.out.println("✓ Cover letter saved to: " + storagePath);
                } catch (Exception e) {
                    System.out.println("⚠ Warning: Could not save file, but cover letter was generated successfully.");
                    System.out.println("You can copy the text above manually.");
                }
            }

        } catch (Exception e) {
            System.out.println("Error: Failed to generate cover letter");
            System.out.println("Details: " + e.getMessage());
        }
    }

    private static boolean handleDeleteAccount() {
        int pin = authService.getCurrentUserPin();
        if (pin == -1) {
            System.out.println("Error: Not logged in");
            return false;
        }

        System.out.println("\n=== DELETE ACCOUNT ===");
        System.out.println("⚠ WARNING: This will permanently delete your account!");

        System.out.print("\nAre you sure you want to delete your account? (yes/no): ");
        String confirm1 = scanner.nextLine().trim().toLowerCase();

        if (!confirm1.equals("yes")) {
            System.out.println("Account deletion cancelled.");
            return false;
        }

        System.out.print("Type your username to confirm deletion: ");
        String usernameConfirm = scanner.nextLine().trim();

        if (!usernameConfirm.equals(authService.getCurrentUser().getUsername())) {
            System.out.println("Username does not match. Account deletion cancelled.");
            return false;
        }

        // Delete the user account (this will delete profile and user's data)
        if (userDAO.deleteUser(authService.getCurrentUser())) {
            System.out.println("\n✓ Account deleted successfully.");
            System.out.println("All your data has been permanently removed.");
            System.out.println("Thank you for using CL Generator. Goodbye!");

            // Logout the user
            authService.logout();
            return true;
        } else {
            System.out.println("Error: Failed to delete account. Please try again later.");
            return false;
        }
    }
}