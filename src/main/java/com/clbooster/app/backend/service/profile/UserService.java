package com.clbooster.app.backend.service.profile;

import org.springframework.stereotype.Service;

/**
 * Spring service wrapping {@link UserDAO}.
 * Views should inject this bean instead of instantiating UserDAO directly.
 */
@Service
public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public boolean registerUser(User user) {
        return userDAO.registerUser(user);
    }

    public User loginUser(String username, String password) {
        return userDAO.loginUser(username, password);
    }

    public User getUserByPin(int pin) {
        return userDAO.getUserByPin(pin);
    }

    public boolean deleteUser(User user) {
        return userDAO.deleteUser(user);
    }

    public boolean updatePassword(int pin, String newPassword) {
        return userDAO.updatePassword(pin, newPassword);
    }

    public boolean usernameExists(String username) {
        return userDAO.usernameExists(username);
    }

    public boolean emailExists(String email) {
        return userDAO.emailExists(email);
    }
}
