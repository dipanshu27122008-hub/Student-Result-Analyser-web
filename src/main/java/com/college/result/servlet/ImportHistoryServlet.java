package com.college.result.servlet;

import com.college.result.dao.CollegeSettingsDAO;
import com.college.result.dao.ImportHistoryDAO;
import com.college.result.model.CollegeSettings;
import com.college.result.model.ImportHistory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Controller for viewing system data ingestion logs and import history.
 */
@WebServlet(name = "ImportHistoryServlet", urlPatterns = {"/import-history"})
public class ImportHistoryServlet extends HttpServlet {

    private final ImportHistoryDAO historyDAO = new ImportHistoryDAO();
    private final CollegeSettingsDAO settingsDAO = new CollegeSettingsDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ImportHistory> historyList = historyDAO.getAllHistory();
        CollegeSettings settings = settingsDAO.getSettings();

        req.setAttribute("historyList", historyList);
        req.setAttribute("collegeSettings", settings);

        req.getRequestDispatcher("/import-history.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
