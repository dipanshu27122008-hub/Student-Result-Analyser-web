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
 * Controller for deleting student records.
 */
@WebServlet(name = "DeleteStudentServlet", urlPatterns = {"/delete-student"})
public class DeleteStudentServlet extends HttpServlet {

    private final StudentService studentService = new StudentService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("studentId");
        try {
            int studentId = Integer.parseInt(idStr != null ? idStr.trim() : "0");
            OperationResult res = studentService.deleteStudent(studentId);
            if (res.isSuccess()) {
                resp.sendRedirect(req.getContextPath() + "/students?success=" + URLEncoder.encode(res.getMessage(), StandardCharsets.UTF_8));
            } else {
                resp.sendRedirect(req.getContextPath() + "/students?error=" + URLEncoder.encode(res.getMessage(), StandardCharsets.UTF_8));
            }
        } catch (NumberFormatException e) {
            String err = "Invalid student identifier.";
            resp.sendRedirect(req.getContextPath() + "/students?error=" + URLEncoder.encode(err, StandardCharsets.UTF_8));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}
