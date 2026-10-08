package com.college.result.servlet;

import com.college.result.dao.CollegeSettingsDAO;
import com.college.result.model.AnalysisResult;
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
 * Controller for the main dashboard aggregating KPIs, performers, and chart data.
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {
    private final ResultAnalysisService analysisService = new ResultAnalysisService();
    private final CollegeSettingsDAO settingsDAO = new CollegeSettingsDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<StudentResult> results = analysisService.getAllResults();
        AnalysisResult analysis = analysisService.performOverallAnalysis(results);
        Map<String, SubjectAnalysis> subjectMap = analysisService.performSubjectAnalysis(results);
        CollegeSettings settings = settingsDAO.getSettings();

        req.setAttribute("analysis", analysis);
        req.setAttribute("subjectMap", subjectMap);
        req.setAttribute("resultsCount", results.size());
        req.setAttribute("collegeSettings", settings);

        // Chart Data (Pre-formatted values for Chart.js)
        // Chart 1: Pass vs Fail
        req.setAttribute("chartPassCount", analysis.getPassedStudents());
        req.setAttribute("chartFailCount", analysis.getFailedStudents());

        // Chart 2: Category Distribution
        req.setAttribute("cat75Plus", analysis.getCategory75Plus());
        req.setAttribute("cat65To74", analysis.getCategory65To74());
        req.setAttribute("cat40To64", analysis.getCategory40To64());
        req.setAttribute("catBelow40", analysis.getCategoryBelow40());

        // Chart 3 & 4: Subject Averages and Pass Percentages
        SubjectAnalysis javaSa = subjectMap.get("Java");
        SubjectAnalysis deSa = subjectMap.get("Digital Electronics");
        SubjectAnalysis dsaSa = subjectMap.get("Data Structures & Algorithms");
        SubjectAnalysis osSa = subjectMap.get("Operating System");

        req.setAttribute("javaAvg", javaSa != null ? javaSa.getAverageMarks() : 0.0);
        req.setAttribute("deAvg", deSa != null ? deSa.getAverageMarks() : 0.0);
        req.setAttribute("dsaAvg", dsaSa != null ? dsaSa.getAverageMarks() : 0.0);
        req.setAttribute("osAvg", osSa != null ? osSa.getAverageMarks() : 0.0);

        req.setAttribute("javaPassPct", javaSa != null ? javaSa.getPassPercentage() : 0.0);
        req.setAttribute("dePassPct", deSa != null ? deSa.getPassPercentage() : 0.0);
        req.setAttribute("dsaPassPct", dsaSa != null ? dsaSa.getPassPercentage() : 0.0);
        req.setAttribute("osPassPct", osSa != null ? osSa.getPassPercentage() : 0.0);

        req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
    }
}
