package com.college.result.servlet;

import com.college.result.dao.CollegeSettingsDAO;
import com.college.result.model.AnalysisResult;
import com.college.result.model.CollegeSettings;
import com.college.result.model.StudentResult;
import com.college.result.model.SubjectAnalysis;
import com.college.result.service.PdfReportService;
import com.college.result.service.ResultAnalysisService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for generating and streaming downloadable PDF analytical reports.
 */
@WebServlet(name = "PdfReportServlet", urlPatterns = {"/pdf-report"})
public class PdfReportServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(PdfReportServlet.class.getName());

    private final ResultAnalysisService analysisService = new ResultAnalysisService();
    private final CollegeSettingsDAO settingsDAO = new CollegeSettingsDAO();
    private final PdfReportService pdfService = new PdfReportService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filter = req.getParameter("filter");
        if (filter == null || filter.trim().isEmpty()) {
            filter = "ALL";
        }

        // Fetch data
        List<StudentResult> filteredResults = analysisService.searchAndFilter("", filter);
        List<StudentResult> allResults = analysisService.getAllResults();
        AnalysisResult analysis = analysisService.performOverallAnalysis(allResults);
        Map<String, SubjectAnalysis> subjectMap = analysisService.performSubjectAnalysis(allResults);
        CollegeSettings settings = settingsDAO.getSettings();

        // Resolve Logo path on server
        String logoRelative = settings.getLogoPath();
        if (logoRelative == null || logoRelative.trim().isEmpty()) {
            logoRelative = "images/college-logo.png";
        }
        if (!logoRelative.startsWith("/")) {
            logoRelative = "/" + logoRelative;
        }
        String logoAbsolutePath = req.getServletContext().getRealPath(logoRelative);

        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=\"Student_Result_Analysis_Report.pdf\"");

        try (OutputStream out = resp.getOutputStream()) {
            pdfService.generateReport(out, settings, analysis, subjectMap, filteredResults, logoAbsolutePath, filter);
            out.flush();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to generate PDF report", e);
            resp.reset();
            resp.setContentType("text/html");
            resp.getWriter().println("<h3>Failed to generate PDF report: " + e.getMessage() + "</h3>");
        }
    }
}
