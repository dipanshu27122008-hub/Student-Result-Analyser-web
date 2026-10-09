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
 * Controller for manually adding a student and their marks.
 */
@WebServlet(name = "AddStudentServlet", urlPatterns = {"/add-student"})
public class AddStudentServlet extends HttpServlet {

    private final StudentService studentService = new StudentService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String rollNo = req.getParameter("rollNo");
        String name = req.getParameter("name");
        String javaStr = req.getParameter("java");
        String deStr = req.getParameter("de");
        String dsaStr = req.getParameter("dsa");
        String osStr = req.getParameter("os");

        try {
            double java = Double.parseDouble(javaStr != null ? javaStr.trim() : "0");
            double de = Double.parseDouble(deStr != null ? deStr.trim() : "0");
            double dsa = Double.parseDouble(dsaStr != null ? dsaStr.trim() : "0");
            double os = Double.parseDouble(osStr != null ? osStr.trim() : "0");

            OperationResult res = studentService.addStudent(rollNo, name, java, de, dsa, os);
            if (res.isSuccess()) {
                resp.sendRedirect(req.getContextPath() + "/students?success=" + URLEncoder.encode(res.getMessage(), StandardCharsets.UTF_8));
            } else {
                resp.sendRedirect(req.getContextPath() + "/students?error=" + URLEncoder.encode(res.getMessage(), StandardCharsets.UTF_8));
            }
        } catch (NumberFormatException e) {
            String err = "Subject marks must be valid numbers between 0 and 100.";
            resp.sendRedirect(req.getContextPath() + "/students?error=" + URLEncoder.encode(err, StandardCharsets.UTF_8));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/students");
    }
}
