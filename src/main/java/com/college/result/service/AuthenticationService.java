package com.college.result.service;

import com.college.result.dao.UserDAO;
import com.college.result.model.User;

/**
 * Service for handling user authentication and credential validation.
 */
public class AuthenticationService {
    private final UserDAO userDAO;

    public AuthenticationService() {
        this.userDAO = new UserDAO();
    }

    public AuthenticationService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User authenticate(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return null;
        }
        return userDAO.authenticate(username.trim(), password.trim());
    }
}
