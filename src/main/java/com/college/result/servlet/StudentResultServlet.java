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
 * Controller for viewing comprehensive individual student scorecards and diagnostic breakdowns.
 */
@WebServlet(name = "StudentResultServlet", urlPatterns = {"/student-result"})
public class StudentResultServlet extends HttpServlet {

    private final ResultAnalysisService analysisService = new ResultAnalysisService();
    private final CollegeSettingsDAO settingsDAO = new CollegeSettingsDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String rollNo = req.getParameter("rollNo");
        CollegeSettings settings = settingsDAO.getSettings();
        req.setAttribute("collegeSettings", settings);

        // Load all students for quick navigation dropdown
        List<StudentResult> allStudents = analysisService.getAllResults();
        req.setAttribute("allStudents", allStudents);

        if (rollNo != null && !rollNo.trim().isEmpty()) {
            StudentResult result = analysisService.getStudentResultByRollNo(rollNo.trim());
            if (result != null) {
                req.setAttribute("studentResult", result);
            } else {
                req.setAttribute("errorMessage", "Student not found with Roll Number: " + rollNo);
            }
        } else if (!allStudents.isEmpty()) {
            // Default to first student if available
            req.setAttribute("studentResult", allStudents.get(0));
        }

        req.getRequestDispatcher("/student-result.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
