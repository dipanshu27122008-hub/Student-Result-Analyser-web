package com.college.result.servlet;

import com.college.result.model.OperationResult;
import com.college.result.service.StudentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Controller for editing student information and subject marks.
 */
@WebServlet(name = "EditStudentServlet", urlPatterns = {"/edit-student"})
public class EditStudentServlet extends HttpServlet {

    private final StudentService studentService = new StudentService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("studentId");
        String name = req.getParameter("name");
        String javaStr = req.getParameter("java");
        String deStr = req.getParameter("de");
        String dsaStr = req.getParameter("dsa");
        String osStr = req.getParameter("os");

        try {
            int studentId = Integer.parseInt(idStr != null ? idStr.trim() : "0");
            double java = Double.parseDouble(javaStr != null ? javaStr.trim() : "0");
            double de = Double.parseDouble(deStr != null ? deStr.trim() : "0");
            double dsa = Double.parseDouble(dsaStr != null ? dsaStr.trim() : "0");
            double os = Double.parseDouble(osStr != null ? osStr.trim() : "0");

            OperationResult res = studentService.updateStudent(studentId, name, java, de, dsa, os);
            if (res.isSuccess()) {
                resp.sendRedirect(req.getContextPath() + "/students?success=" + URLEncoder.encode(res.getMessage(), StandardCharsets.UTF_8));
            } else {
                resp.sendRedirect(req.getContextPath() + "/students?error=" + URLEncoder.encode(res.getMessage(), StandardCharsets.UTF_8));
            }
        } catch (NumberFormatException e) {
            String err = "Invalid input. Please provide valid numbers for student ID and marks (0-100).";
            resp.sendRedirect(req.getContextPath() + "/students?error=" + URLEncoder.encode(err, StandardCharsets.UTF_8));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/students");
    }
}
