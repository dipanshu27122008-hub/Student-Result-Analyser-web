package com.college.result.servlet;

import com.college.result.dao.CollegeSettingsDAO;
import com.college.result.model.CollegeSettings;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

/**
 * Controller for configuring institutional metadata, courses, academic year, and logo.
 */
@WebServlet(name = "SettingsServlet", urlPatterns = {"/settings"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 10
)
public class SettingsServlet extends HttpServlet {

    private final CollegeSettingsDAO settingsDAO = new CollegeSettingsDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CollegeSettings settings = settingsDAO.getSettings();
        req.setAttribute("collegeSettings", settings);
        req.setAttribute("dbStatus", com.college.result.util.DBConnectionUtil.getDatabaseStatus());
        req.setAttribute("dbUrl", com.college.result.util.DBConnectionUtil.getDbUrl());
        req.setAttribute("totalStudents", new com.college.result.dao.StudentDAO().getTotalCount());
        req.getRequestDispatcher("/settings.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String collegeName = req.getParameter("collegeName");
        String departmentName = req.getParameter("departmentName");
        String courseName = req.getParameter("courseName");
        String semester = req.getParameter("semester");
        String academicYear = req.getParameter("academicYear");
        String projectTitle = req.getParameter("projectTitle");

        CollegeSettings settings = settingsDAO.getSettings();
        if (collegeName != null && !collegeName.trim().isEmpty()) settings.setCollegeName(collegeName.trim());
        if (departmentName != null && !departmentName.trim().isEmpty()) settings.setDepartmentName(departmentName.trim());
        if (courseName != null && !courseName.trim().isEmpty()) settings.setCourseName(courseName.trim());
        if (semester != null && !semester.trim().isEmpty()) settings.setSemester(semester.trim());
        if (academicYear != null && !academicYear.trim().isEmpty()) settings.setAcademicYear(academicYear.trim());
        if (projectTitle != null && !projectTitle.trim().isEmpty()) settings.setProjectTitle(projectTitle.trim());

        // Handle Logo file upload if provided
        try {
            Part logoPart = req.getPart("logoFile");
            if (logoPart != null && logoPart.getSize() > 0) {
                String fileName = Paths.get(logoPart.getSubmittedFileName()).getFileName().toString();
                String ext = fileName.toLowerCase().endsWith(".png") ? ".png" :
                             (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg") ? ".jpg" : null);

                if (ext != null) {
                    String uploadDir = req.getServletContext().getRealPath("/images");
                    File dir = new File(uploadDir);
                    if (!dir.exists()) dir.mkdirs();

                    String targetLogoFileName = "college-logo" + ext;
                    File targetFile = new File(dir, targetLogoFileName);
                    try (InputStream in = logoPart.getInputStream();
                         FileOutputStream fos = new FileOutputStream(targetFile)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    }
                    settings.setLogoPath("images/" + targetLogoFileName);
                } else {
                    req.setAttribute("warningMessage", "Logo file must be PNG or JPG format. Metadata was updated.");
                }
            }
        } catch (Exception ignored) {}

        boolean updated = settingsDAO.updateSettings(settings);
        if (updated) {
            req.setAttribute("successMessage", "College Settings updated successfully!");
            // Refresh session settings
            req.getSession().setAttribute("collegeSettings", settings);
        } else {
            req.setAttribute("errorMessage", "Failed to update settings in database.");
        }

        req.setAttribute("collegeSettings", settings);
        req.setAttribute("dbStatus", com.college.result.util.DBConnectionUtil.getDatabaseStatus());
        req.setAttribute("dbUrl", com.college.result.util.DBConnectionUtil.getDbUrl());
        req.setAttribute("totalStudents", new com.college.result.dao.StudentDAO().getTotalCount());
        req.getRequestDispatcher("/settings.jsp").forward(req, resp);
    }
}
