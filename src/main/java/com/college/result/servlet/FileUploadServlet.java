package com.college.result.servlet;

import com.college.result.dao.CollegeSettingsDAO;
import com.college.result.model.CollegeSettings;
import com.college.result.model.ImportResult;
import com.college.result.service.ExcelImportService;
import com.college.result.service.TxtImportService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

/**
 * Controller handling file uploads for both Excel (.xlsx) and Text (.txt) formats.
 */
@WebServlet(name = "FileUploadServlet", urlPatterns = {"/upload"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,  // 2MB memory buffer
        maxFileSize = 1024 * 1024 * 15,       // 15MB max file size
        maxRequestSize = 1024 * 1024 * 25     // 25MB max request size
)
public class FileUploadServlet extends HttpServlet {

    private final ExcelImportService excelService = new ExcelImportService();
    private final TxtImportService txtService = new TxtImportService();
    private final CollegeSettingsDAO settingsDAO = new CollegeSettingsDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CollegeSettings settings = settingsDAO.getSettings();
        req.setAttribute("collegeSettings", settings);
        req.getRequestDispatcher("/upload.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CollegeSettings settings = settingsDAO.getSettings();
        req.setAttribute("collegeSettings", settings);

        Part filePart = null;
        try {
            filePart = req.getPart("file");
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error parsing upload request: " + e.getMessage());
            req.getRequestDispatcher("/upload.jsp").forward(req, resp);
            return;
        }

        if (filePart == null || filePart.getSize() == 0) {
            req.setAttribute("errorMessage", "Please select a valid file to upload.");
            req.getRequestDispatcher("/upload.jsp").forward(req, resp);
            return;
        }

        String submittedFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String lowerName = submittedFileName.toLowerCase();

        if (!lowerName.endsWith(".xlsx") && !lowerName.endsWith(".txt")) {
            req.setAttribute("errorMessage", "Invalid file format. Please upload .xlsx or .txt.");
            req.getRequestDispatcher("/upload.jsp").forward(req, resp);
            return;
        }

        ImportResult importResult;
        try (InputStream is = filePart.getInputStream()) {
            if (lowerName.endsWith(".xlsx")) {
                importResult = excelService.importExcel(is, submittedFileName);
            } else {
                importResult = txtService.importTxt(is, submittedFileName);
            }
        } catch (Exception e) {
            importResult = new ImportResult(submittedFileName, lowerName.endsWith(".xlsx") ? "Excel" : "TXT");
            importResult.setStatus("Failed");
            importResult.addErrorMessage("System error processing file: " + e.getMessage());
        }

        req.setAttribute("importResult", importResult);
        req.getRequestDispatcher("/upload.jsp").forward(req, resp);
    }
}
