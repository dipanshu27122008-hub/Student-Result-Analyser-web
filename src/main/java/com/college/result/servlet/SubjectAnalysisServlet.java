package com.college.result.servlet;

import com.college.result.dao.CollegeSettingsDAO;
import com.college.result.model.CollegeSettings;
import com.college.result.model.StudentResult;
import com.college.result.model.SubjectAnalysis;
import com.college.result.service.ResultAnalysisService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Controller for dedicated Subject-wise Performance Analysis.
 */
@WebServlet(name = "SubjectAnalysisServlet", urlPatterns = {"/subject-analysis"})
public class SubjectAnalysisServlet extends HttpServlet {

    private final ResultAnalysisService analysisService = new ResultAnalysisService();
    private final CollegeSettingsDAO settingsDAO = new CollegeSettingsDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<StudentResult> results = analysisService.getAllResults();
        Map<String, SubjectAnalysis> subjectMap = analysisService.performSubjectAnalysis(results);
        CollegeSettings settings = settingsDAO.getSettings();

        req.setAttribute("subjectMap", subjectMap);
        req.setAttribute("collegeSettings", settings);
        req.setAttribute("totalStudents", results.size());

        req.getRequestDispatcher("/subject-analysis.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
