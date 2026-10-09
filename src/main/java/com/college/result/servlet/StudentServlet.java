package com.college.result.servlet;

import com.college.result.dao.CollegeSettingsDAO;
import com.college.result.model.CollegeSettings;
import com.college.result.model.StudentResult;
import com.college.result.service.ResultAnalysisService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Controller for student records management, search, and filtering.
 */
@WebServlet(name = "StudentServlet", urlPatterns = {"/students"})
public class StudentServlet extends HttpServlet {

    private final ResultAnalysisService analysisService = new ResultAnalysisService();
    private final CollegeSettingsDAO settingsDAO = new CollegeSettingsDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query = req.getParameter("q");
        String filter = req.getParameter("filter");

        if (filter == null || filter.trim().isEmpty()) {
            filter = "ALL";
        }

        List<StudentResult> studentResults = analysisService.searchAndFilter(query, filter);
        CollegeSettings settings = settingsDAO.getSettings();

        String success = req.getParameter("success");
        String error = req.getParameter("error");

        req.setAttribute("studentsList", studentResults);
        req.setAttribute("currentQuery", query != null ? query : "");
        req.setAttribute("currentFilter", filter);
        req.setAttribute("totalFound", studentResults.size());
        req.setAttribute("collegeSettings", settings);
        req.setAttribute("successMessage", success);
        req.setAttribute("errorMessage", error);
        req.setAttribute("dbStatus", com.college.result.util.DBConnectionUtil.getDatabaseStatus());

        req.getRequestDispatcher("/students.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
