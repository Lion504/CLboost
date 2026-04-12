package com.clbooster.app.backend.service.authentication;

import com.clbooster.app.backend.service.profile.User;
import com.clbooster.app.backend.service.profile.UserDAO;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinSession;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class AuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private UserDAO userDAO;
    private Map<String, Object> sessionStore;
    private static final String USER_SESSION_ATTRIBUTE = "currentUser";

    // Production constructor - uses VaadinSession
    public AuthenticationService() {
        this.userDAO = new UserDAO();
        this.sessionStore = null;
    }

    // Test constructor - accepts HashMap for mocking session
    public AuthenticationService(Map<String, Object> sessionStore) {
        this.userDAO = new UserDAO();
        this.sessionStore = sessionStore;
    }

    // Store user in session (VaadinSession or HashMap for tests)
    private void storeUserInSession(User user) {
        if (sessionStore != null) {
            sessionStore.put(USER_SESSION_ATTRIBUTE, user);
        } else {
            VaadinSession.getCurrent().setAttribute(USER_SESSION_ATTRIBUTE, user);
        }
    }

    // Get user from session (VaadinSession or HashMap for tests)
    private User getUserFromSession() {
        if (sessionStore != null) {
            return (User) sessionStore.get(USER_SESSION_ATTRIBUTE);
        }
        return (User) VaadinSession.getCurrent().getAttribute(USER_SESSION_ATTRIBUTE);
    }

    // Remove user from session (VaadinSession or HashMap for tests)
    private void removeUserFromSession() {
        if (sessionStore != null) {
            sessionStore.remove(USER_SESSION_ATTRIBUTE);
        } else {
            VaadinSession.getCurrent().setAttribute(USER_SESSION_ATTRIBUTE, null);
        }
    }

    public void setCurrentUser(User user) {
        storeUserInSession(user);
        authenticateSpringSecurity(user);
    }

    private void authenticateSpringSecurity(User user) {
        if (sessionStore != null) {
            return;
        }
        try {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_USER")));

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            HttpSession httpSession = VaadinServletRequest.getCurrent().getHttpServletRequest().getSession(true);
            httpSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        } catch (Exception ex) {
            log.warn("Could not bind Spring Security context for logged-in user", ex);
        }
    }

    private void clearSpringSecurityAuthentication() {
        SecurityContextHolder.clearContext();
        if (sessionStore != null) {
            return;
        }
        try {
            HttpSession httpSession = VaadinServletRequest.getCurrent().getHttpServletRequest().getSession(false);
            if (httpSession != null) {
                httpSession.removeAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
            }
        } catch (Exception ex) {
            log.debug("Could not clear Spring Security context from session", ex);
        }
    }

    public static void showPasswordRequirements() {
        log.info("Password Requirements:");
        log.info("At least 10 characters long");
        log.info("At least one uppercase letter (A-Z)");
        log.info("At least one lowercase letter (a-z)");
        log.info("At least one number (0-9)");
        log.info("At least one special character (!@#$%^&*etc.)");
    }

    public boolean register(String email, String username, String password, String firstName, String lastName) {
        if (email == null || email.trim().isEmpty()) {
            log.warn("Registration rejected: email is required");
            return false;
        }
        if (username == null || username.trim().isEmpty()) {
            log.warn("Registration rejected: username is required");
            return false;
        }
        if (password == null || password.length() < 10 || !password.matches(".*[A-Z].*")
                || !password.matches(".*[a-z].*") || !password.matches(".*\\d.*")
                || !password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            log.warn("Registration rejected: password requirements not met");
            return false;
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            log.warn("Registration rejected: first name is required");
            return false;
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            log.warn("Registration rejected: last name is required");
            return false;
        }

        // Check if username already exists
        if (userDAO.usernameExists(username)) {
            log.warn("Registration rejected: user identifier already registered");
            return false;
        }

        if (userDAO.emailExists(email)) {
            log.warn("Registration rejected: user identifier already registered");
            return false;
        }

        User user = new User(email, username, password, firstName, lastName);
        if (userDAO.registerUser(user)) {
            log.info("Registration successful");
            return true;
        } else {
            log.warn("Registration failed");
            return false;
        }
    }

    public boolean login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            log.warn("Login rejected: username is required");
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            log.warn("Login rejected: password is required");
            return false;
        }

        User user = userDAO.loginUser(username, password);
        if (user != null) {
            // Store user in session for persistence
            storeUserInSession(user);
            authenticateSpringSecurity(user);
            log.info("Login successful");
            return true;
        } else {
            log.warn("Login rejected: invalid credentials");
            return false;
        }
    }

    public void logout() {
        User user = getUserFromSession();
        if (user != null) {
            log.info("Logout successful");
            removeUserFromSession();
            clearSpringSecurityAuthentication();
        }
    }

    // Check if user is logged in
    public boolean isLoggedIn() {
        return getUserFromSession() != null;
    }

    // Get current logged-in user
    public User getCurrentUser() {
        return getUserFromSession();
    }

    // Get current user's PIN
    public int getCurrentUserPin() {
        User user = getUserFromSession();
        if (user != null) {
            return user.getPin();
        }
        return -1;
    }

    // Change password for current user
    public boolean changePassword(String currentPassword, String newPassword) {
        User user = getUserFromSession();
        if (user == null) {
            log.warn("Password change rejected: no user logged in");
            return false;
        }

        // Verify current password
        User verifiedUser = userDAO.loginUser(user.getUsername(), currentPassword);
        if (verifiedUser == null) {
            log.warn("Password change rejected: current password invalid");
            return false;
        }

        // Update password in database
        boolean updated = userDAO.updatePassword(user.getPin(), newPassword);
        if (updated) {
            log.info("Password changed successfully");
        } else {
            log.warn("Password change failed");
        }
        return updated;
    }
}