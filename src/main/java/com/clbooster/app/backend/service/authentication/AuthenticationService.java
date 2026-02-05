package com.clbooster.app.backend.service.authentication;

import com.clbooster.app.backend.service.profile.User;
import com.clbooster.app.backend.service.profile.UserDAO;

public class AuthenticationService {
    private UserDAO userDAO;
    private User currentUser;

    public AuthenticationService() {
        this.userDAO = new UserDAO();
        this.currentUser = null;
    }

    public boolean register(String email, String username, String password, String firstName, String lastName) {
        if (email == null || email.trim().isEmpty()) {
            System.out.println("Error: Email is required");
            return false;
        }
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Error: Username is required");
            return false;
        }
        if (password == null || password.length() < 6) {
            System.out.println("Error: Password must be at least 6 characters");
            return false;
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            System.out.println("Error: First name is required");
            return false;
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            System.out.println("Error: Last name is required");
            return false;
        }

        // Check if username already exists
        if (userDAO.usernameExists(username)) {
            System.out.println("Error: Username already exists");
            return false;
        }

        if (userDAO.emailExists(email)) {
            System.out.println("Error: Email already registered");
            return false;
        }

        User user = new User(email, username, password, firstName, lastName);
        if (userDAO.registerUser(user)) {
            System.out.println("✓ Registration successful!");
            System.out.println("✓ Your PIN: " + user.getPin());
            System.out.println("✓ Username: " + username);
            return true;
        } else {
            System.out.println("Error: Registration failed. Please try again.");
            return false;
        }
    }

    public boolean login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Error: Username is required");
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            System.out.println("Error: Password is required");
            return false;
        }

        User user = userDAO.loginUser(username, password);
        if (user != null) {
            this.currentUser = user;
            System.out.println("✓ Login successful!");
            System.out.println("✓ Welcome, " + user.getFirstName() + " " + user.getLastName());
            System.out.println("✓ PIN: " + user.getPin());
            return true;
        } else {
            System.out.println("Error: Invalid username or password");
            return false;
        }
    }

    public void logout() {
        if (currentUser != null) {
            System.out.println("✓ Logged out successfully. Goodbye, " + currentUser.getFirstName() + "!");
            currentUser = null;
        }
    }

    // Check if user is logged in
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    // Get current logged-in user
    public User getCurrentUser() {
        return currentUser;
    }

    // Get current user's PIN
    public int getCurrentUserPin() {
        if (currentUser != null) {
            return currentUser.getPin();
        }
        return -1;
    }
}