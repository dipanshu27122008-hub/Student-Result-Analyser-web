package com.college.result.servlet;

import com.college.result.model.OperationResult;
import com.college.result.service.StudentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Controller for loading or resetting the 35 benchmark demo students.
 */
@WebServlet(name = "DemoDataServlet", urlPatterns = {"/demo-data"})
public class DemoDataServlet extends HttpServlet {

    private final StudentService studentService = new StudentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String redirect = req.getParameter("redirect");
        if (redirect == null || redirect.trim().isEmpty()) {
            redirect = req.getContextPath() + "/dashboard";
        }

        if ("clear".equalsIgnoreCase(action)) {
            OperationResult res = studentService.clearAllStudents();
            String msg = URLEncoder.encode(res.getMessage(), StandardCharsets.UTF_8);
            resp.sendRedirect(redirect + (redirect.contains("?") ? "&" : "?") + (res.isSuccess() ? "success=" : "error=") + msg);
            return;
        }

        // Default action: Seed demo data
        OperationResult res = studentService.seedDemoData();
        String msg = URLEncoder.encode(res.getMessage(), StandardCharsets.UTF_8);
        resp.sendRedirect(redirect + (redirect.contains("?") ? "&" : "?") + (res.isSuccess() ? "success=" : "error=") + msg);
    }
}
