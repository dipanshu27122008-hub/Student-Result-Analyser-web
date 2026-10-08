package com.college.result.servlet;

import com.college.result.dao.CollegeSettingsDAO;
import com.college.result.model.CollegeSettings;
import com.college.result.model.User;
import com.college.result.service.AuthenticationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Controller handling user authentication and login sessions.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    private final AuthenticationService authService = new AuthenticationService();
    private final CollegeSettingsDAO settingsDAO = new CollegeSettingsDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        // Attach institutional settings for branding on login page
        CollegeSettings settings = settingsDAO.getSettings();
        req.setAttribute("collegeSettings", settings);

        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        User user = authService.authenticate(username, password);
        if (user != null) {
            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole());

            // Cache college settings in session
            CollegeSettings settings = settingsDAO.getSettings();
            session.setAttribute("collegeSettings", settings);

            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } else {
            req.setAttribute("errorMessage", "Invalid username or password. Demo credentials: admin / admin123");
            req.setAttribute("usernameVal", username);
            CollegeSettings settings = settingsDAO.getSettings();
            req.setAttribute("collegeSettings", settings);
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}
