package com.college.result.servlet;

import com.college.result.dao.CollegeSettingsDAO;
import com.college.result.model.AnalysisResult;
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
 * Controller for dedicated Overall Batch Performance Analysis.
 */
@WebServlet(name = "AnalysisServlet", urlPatterns = {"/analysis"})
public class AnalysisServlet extends HttpServlet {

    private final ResultAnalysisService analysisService = new ResultAnalysisService();
    private final CollegeSettingsDAO settingsDAO = new CollegeSettingsDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<StudentResult> results = analysisService.getAllResults();
        AnalysisResult analysis = analysisService.performOverallAnalysis(results);
        CollegeSettings settings = settingsDAO.getSettings();

        req.setAttribute("analysis", analysis);
        req.setAttribute("collegeSettings", settings);
        req.setAttribute("totalCount", results.size());

        req.getRequestDispatcher("/analysis.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
