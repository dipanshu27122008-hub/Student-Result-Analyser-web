package com.college.result.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Controller for downloading sample Excel (.xlsx) and TXT (.txt) templates.
 */
@WebServlet(name = "DownloadSampleServlet", urlPatterns = {"/download-sample"})
public class DownloadSampleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String type = req.getParameter("type");
        if (type == null) type = "xlsx";

        String fileName;
        String contentType;
        String resourcePath;

        if ("txt".equalsIgnoreCase(type)) {
            fileName = "students.txt";
            contentType = "text/plain; charset=UTF-8";
            resourcePath = "sample-data/students.txt";
        } else {
            fileName = "students.xlsx";
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            resourcePath = "sample-data/students.xlsx";
        }

        // Try getting from classpath first
        InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath);

        // Fallback to filesystem
        if (in == null) {
            File localFile = new File("sample-data", fileName);
            if (localFile.exists()) {
                in = new FileInputStream(localFile);
            }
        }

        if (in == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Sample file not found: " + fileName);
            return;
        }

        resp.setContentType(contentType);
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try (InputStream input = in; OutputStream out = resp.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        }
    }
}
