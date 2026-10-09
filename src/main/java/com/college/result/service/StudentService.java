package com.college.result.service;

import com.college.result.dao.MarksDAO;
import com.college.result.dao.StudentDAO;
import com.college.result.model.OperationResult;
import com.college.result.model.Student;
import com.college.result.model.StudentMarks;
import com.college.result.util.DBConnectionUtil;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for managing manual student records (Create, Update, Delete)
 * and loading benchmark demo datasets.
 */
public class StudentService {
    private static final Logger LOGGER = Logger.getLogger(StudentService.class.getName());

    private final StudentDAO studentDAO = new StudentDAO();
    private final MarksDAO marksDAO = new MarksDAO();

    /**
     * Adds a new student and their marks with strict academic validation.
     */
    public OperationResult addStudent(String rollNo, String name, double java, double de, double dsa, double os) {
        if (rollNo == null || rollNo.trim().isEmpty()) {
            return new OperationResult(false, "Roll Number is required and cannot be blank.");
        }
        rollNo = rollNo.trim();

        if (name == null || name.trim().isEmpty()) {
            return new OperationResult(false, "Student Name is required and cannot be blank.");
        }
        name = name.trim();
        if (name.length() > 100) {
            name = name.substring(0, 100);
        }

        // Validate Marks range [0, 100]
        String marksValidation = validateMarks(java, de, dsa, os);
        if (marksValidation != null) {
            return new OperationResult(false, marksValidation);
        }

        // Check for duplicate Roll Number
        if (studentDAO.studentExistsByRollNo(rollNo)) {
            return new OperationResult(false, "Roll Number '" + rollNo + "' already exists! Duplicate records are not permitted.");
        }

        // Transactional insert
        Connection conn = null;
        try {
            conn = DBConnectionUtil.getConnection();
            conn.setAutoCommit(false);

            Student student = new Student(rollNo, name);
            int studentId = studentDAO.insertStudent(student, conn);
            if (studentId <= 0) {
                conn.rollback();
                return new OperationResult(false, "Failed to create student record in database.");
            }

            StudentMarks marks = new StudentMarks(java, de, dsa, os);
            marks.setStudentId(studentId);
            boolean marksInserted = marksDAO.insertMarks(marks, conn);
            if (!marksInserted) {
                conn.rollback();
                return new OperationResult(false, "Failed to store student marks in database.");
            }

            conn.commit();
            LOGGER.info("Student added successfully: " + rollNo + " (" + name + ")");
            return new OperationResult(true, "Student " + name + " (Roll No: " + rollNo + ") added successfully!", student);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding student " + rollNo, e);
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ignored) {}
            }
            return new OperationResult(false, "Database error: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (Exception ignored) {}
            }
        }
    }

    /**
     * Updates an existing student's name and marks.
     */
    public OperationResult updateStudent(int studentId, String name, double java, double de, double dsa, double os) {
        if (studentId <= 0) {
            return new OperationResult(false, "Invalid student identifier.");
        }
        if (name == null || name.trim().isEmpty()) {
            return new OperationResult(false, "Student Name is required.");
        }
        name = name.trim();

        String marksValidation = validateMarks(java, de, dsa, os);
        if (marksValidation != null) {
            return new OperationResult(false, marksValidation);
        }

        Student existing = studentDAO.getStudentById(studentId);
        if (existing == null) {
            return new OperationResult(false, "Student with ID " + studentId + " was not found.");
        }

        boolean studentUpdated = studentDAO.updateStudent(studentId, name);
        if (!studentUpdated) {
            return new OperationResult(false, "Failed to update student profile.");
        }

        StudentMarks marks = new StudentMarks(java, de, dsa, os);
        marks.setStudentId(studentId);
        boolean marksUpdated = marksDAO.updateMarks(marks);
        if (!marksUpdated) {
            return new OperationResult(false, "Failed to update student subject marks.");
        }

        LOGGER.info("Student updated successfully: ID " + studentId);
        return new OperationResult(true, "Student record for " + existing.getRollNo() + " updated successfully!");
    }

    /**
     * Deletes a student and cascaded marks.
     */
    public OperationResult deleteStudent(int studentId) {
        if (studentId <= 0) {
            return new OperationResult(false, "Invalid student identifier.");
        }
        Student existing = studentDAO.getStudentById(studentId);
        if (existing == null) {
            return new OperationResult(false, "Student with ID " + studentId + " not found.");
        }

        boolean deleted = studentDAO.deleteStudent(studentId);
        if (deleted) {
            return new OperationResult(true, "Student " + existing.getName() + " (Roll: " + existing.getRollNo() + ") was deleted successfully.");
        } else {
            return new OperationResult(false, "Failed to delete student from database.");
        }
    }

    /**
     * Seeds the complete 35 benchmark students.
     */
    public OperationResult seedDemoData() {
        Connection conn = null;
        try {
            conn = DBConnectionUtil.getConnection();
            boolean success = DBConnectionUtil.seedDemoDataInternal(conn);
            if (success) {
                return new OperationResult(true, "Demo benchmark dataset (35 students) loaded successfully! Dashboard and analytics are now active.");
            } else {
                return new OperationResult(false, "Failed to seed demo dataset.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error seeding demo data", e);
            return new OperationResult(false, "Error loading demo data: " + e.getMessage());
        } finally {
            DBConnectionUtil.close(conn);
        }
    }

    /**
     * Clears all student records and marks from database.
     */
    public OperationResult clearAllStudents() {
        boolean cleared = studentDAO.clearAllStudents();
        if (cleared) {
            return new OperationResult(true, "All student records and marks have been cleared.");
        } else {
            return new OperationResult(false, "Failed to clear student records.");
        }
    }

    private String validateMarks(double java, double de, double dsa, double os) {
        if (java < 0 || java > 100) return "Java marks must be between 0 and 100. Entered: " + java;
        if (de < 0 || de > 100) return "Digital Electronics (DE) marks must be between 0 and 100. Entered: " + de;
        if (dsa < 0 || dsa > 100) return "DSA marks must be between 0 and 100. Entered: " + dsa;
        if (os < 0 || os > 100) return "Operating System (OS) marks must be between 0 and 100. Entered: " + os;
        return null;
    }
}
