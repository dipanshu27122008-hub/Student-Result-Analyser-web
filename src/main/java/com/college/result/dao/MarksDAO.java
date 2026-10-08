package com.college.result.dao;

import com.college.result.model.Student;
import com.college.result.model.StudentMarks;
import com.college.result.model.StudentResult;
import com.college.result.service.ResultAnalysisService;
import com.college.result.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Marks entity and joining with Students.
 */
public class MarksDAO {
    private static final Logger LOGGER = Logger.getLogger(MarksDAO.class.getName());

    public boolean insertMarks(StudentMarks marks) {
        Connection conn = null;
        try {
            conn = DBConnectionUtil.getConnection();
            return insertMarks(marks, conn);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting marks for student id: " + marks.getStudentId(), e);
            return false;
        } finally {
            DBConnectionUtil.close(conn);
        }
    }

    public boolean insertMarks(StudentMarks marks, Connection conn) throws SQLException {
        String sql = "INSERT INTO marks (student_id, java_marks, de_marks, dsa_marks, os_marks) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, marks.getStudentId());
            ps.setDouble(2, marks.getJavaMarks());
            ps.setDouble(3, marks.getDeMarks());
            ps.setDouble(4, marks.getDsaMarks());
            ps.setDouble(5, marks.getOsMarks());
            return ps.executeUpdate() > 0;
        }
    }

    public StudentMarks getMarksByStudentId(int studentId) {
        String sql = "SELECT id, student_id, java_marks, de_marks, dsa_marks, os_marks FROM marks WHERE student_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new StudentMarks(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getDouble("java_marks"),
                        rs.getDouble("de_marks"),
                        rs.getDouble("dsa_marks"),
                        rs.getDouble("os_marks")
                );
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching marks for student id: " + studentId, e);
        } finally {
            DBConnectionUtil.close(rs, ps, conn);
        }
        return null;
    }

    public List<StudentResult> getAllStudentResults() {
        List<StudentResult> list = new ArrayList<>();
        String sql = "SELECT s.id, s.roll_no, s.name, m.java_marks, m.de_marks, m.dsa_marks, m.os_marks " +
                "FROM students s JOIN marks m ON s.id = m.student_id ORDER BY s.roll_no ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Student s = new Student(rs.getInt("id"), rs.getString("roll_no"), rs.getString("name"), null);
                StudentMarks m = new StudentMarks(
                        rs.getDouble("java_marks"),
                        rs.getDouble("de_marks"),
                        rs.getDouble("dsa_marks"),
                        rs.getDouble("os_marks")
                );
                m.setStudentId(s.getId());
                list.add(ResultAnalysisService.calculateResult(s, m));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching student results", e);
        } finally {
            DBConnectionUtil.close(rs, ps, conn);
        }
        return list;
    }

    public StudentResult getStudentResultByRollNo(String rollNo) {
        String sql = "SELECT s.id, s.roll_no, s.name, m.java_marks, m.de_marks, m.dsa_marks, m.os_marks " +
                "FROM students s JOIN marks m ON s.id = m.student_id WHERE LOWER(s.roll_no) = LOWER(?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, rollNo.trim());
            rs = ps.executeQuery();
            if (rs.next()) {
                Student s = new Student(rs.getInt("id"), rs.getString("roll_no"), rs.getString("name"), null);
                StudentMarks m = new StudentMarks(
                        rs.getDouble("java_marks"),
                        rs.getDouble("de_marks"),
                        rs.getDouble("dsa_marks"),
                        rs.getDouble("os_marks")
                );
                m.setStudentId(s.getId());
                return ResultAnalysisService.calculateResult(s, m);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching student result by roll: " + rollNo, e);
        } finally {
            DBConnectionUtil.close(rs, ps, conn);
        }
        return null;
    }

    public StudentResult getStudentResultById(int studentId) {
        String sql = "SELECT s.id, s.roll_no, s.name, m.java_marks, m.de_marks, m.dsa_marks, m.os_marks " +
                "FROM students s JOIN marks m ON s.id = m.student_id WHERE s.id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            rs = ps.executeQuery();
            if (rs.next()) {
                Student s = new Student(rs.getInt("id"), rs.getString("roll_no"), rs.getString("name"), null);
                StudentMarks m = new StudentMarks(
                        rs.getDouble("java_marks"),
                        rs.getDouble("de_marks"),
                        rs.getDouble("dsa_marks"),
                        rs.getDouble("os_marks")
                );
                m.setStudentId(s.getId());
                return ResultAnalysisService.calculateResult(s, m);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching student result by id: " + studentId, e);
        } finally {
            DBConnectionUtil.close(rs, ps, conn);
        }
        return null;
    }

    public List<StudentResult> searchAndFilter(String query, String filter) {
        List<StudentResult> all = getAllStudentResults();
        List<StudentResult> filtered = new ArrayList<>();

        String q = query != null ? query.trim().toLowerCase() : "";
        String f = filter != null ? filter.trim() : "ALL";

        for (StudentResult sr : all) {
            // Check search text (roll no or name)
            boolean matchesSearch = q.isEmpty() ||
                    sr.getRollNo().toLowerCase().contains(q) ||
                    sr.getName().toLowerCase().contains(q);

            if (!matchesSearch) {
                continue;
            }

            // Check category/result filter
            boolean matchesFilter = true;
            switch (f) {
                case "PASS":
                    matchesFilter = "PASS".equalsIgnoreCase(sr.getResult());
                    break;
                case "FAIL":
                    matchesFilter = "FAIL".equalsIgnoreCase(sr.getResult());
                    break;
                case "75%+":
                    matchesFilter = "75%+".equalsIgnoreCase(sr.getCategory());
                    break;
                case "65–74.99%":
                case "65-74.99%":
                    matchesFilter = "65–74.99%".equalsIgnoreCase(sr.getCategory()) || "65-74.99%".equalsIgnoreCase(sr.getCategory());
                    break;
                case "40–64.99%":
                case "40-64.99%":
                    matchesFilter = "40–64.99%".equalsIgnoreCase(sr.getCategory()) || "40-64.99%".equalsIgnoreCase(sr.getCategory());
                    break;
                case "Below 40%":
                    matchesFilter = "Below 40%".equalsIgnoreCase(sr.getCategory());
                    break;
                default:
                    matchesFilter = true; // ALL
            }

            if (matchesFilter) {
                filtered.add(sr);
            }
        }

        return filtered;
    }
}
