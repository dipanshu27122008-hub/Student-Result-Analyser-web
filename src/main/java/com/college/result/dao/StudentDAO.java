package com.college.result.dao;

import com.college.result.model.Student;
import com.college.result.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Student entity.
 */
public class StudentDAO {
    private static final Logger LOGGER = Logger.getLogger(StudentDAO.class.getName());

    public boolean studentExistsByRollNo(String rollNo) {
        String sql = "SELECT 1 FROM students WHERE roll_no = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, rollNo);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking student existence: " + rollNo, e);
            return false;
        } finally {
            DBConnectionUtil.close(rs, ps, conn);
        }
    }

    public boolean studentExistsByRollNo(String rollNo, Connection conn) throws SQLException {
        String sql = "SELECT 1 FROM students WHERE roll_no = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int insertStudent(Student student) {
        Connection conn = null;
        try {
            conn = DBConnectionUtil.getConnection();
            return insertStudent(student, conn);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting student: " + student.getRollNo(), e);
            return -1;
        } finally {
            DBConnectionUtil.close(conn);
        }
    }

    public int insertStudent(Student student, Connection conn) throws SQLException {
        String sql = "INSERT INTO students (roll_no, name) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, student.getRollNo());
            ps.setString(2, student.getName());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        student.setId(id);
                        return id;
                    }
                }
            }
        }
        return -1;
    }

    public Student getStudentByRollNo(String rollNo) {
        String sql = "SELECT id, roll_no, name, created_at FROM students WHERE roll_no = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, rollNo);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new Student(
                        rs.getInt("id"),
                        rs.getString("roll_no"),
                        rs.getString("name"),
                        rs.getTimestamp("created_at")
                );
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching student by roll: " + rollNo, e);
        } finally {
            DBConnectionUtil.close(rs, ps, conn);
        }
        return null;
    }

    public Student getStudentById(int id) {
        String sql = "SELECT id, roll_no, name, created_at FROM students WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new Student(
                        rs.getInt("id"),
                        rs.getString("roll_no"),
                        rs.getString("name"),
                        rs.getTimestamp("created_at")
                );
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching student by id: " + id, e);
        } finally {
            DBConnectionUtil.close(rs, ps, conn);
        }
        return null;
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT id, roll_no, name, created_at FROM students ORDER BY roll_no ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Student(
                        rs.getInt("id"),
                        rs.getString("roll_no"),
                        rs.getString("name"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all students", e);
        } finally {
            DBConnectionUtil.close(rs, ps, conn);
        }
        return list;
    }

    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM students";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting student count", e);
        } finally {
            DBConnectionUtil.close(rs, ps, conn);
        }
        return 0;
    }

    public boolean updateStudent(int id, String name) {
        String sql = "UPDATE students SET name = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating student id: " + id, e);
            return false;
        } finally {
            DBConnectionUtil.close(ps, conn);
        }
    }

    public boolean deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting student id: " + id, e);
            return false;
        } finally {
            DBConnectionUtil.close(ps, conn);
        }
    }

    public boolean clearAllStudents() {
        Connection conn = null;
        Statement st = null;
        try {
            conn = DBConnectionUtil.getConnection();
            st = conn.createStatement();
            st.executeUpdate("DELETE FROM marks");
            st.executeUpdate("DELETE FROM students");
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error clearing student records", e);
            return false;
        } finally {
            DBConnectionUtil.close(st, conn);
        }
    }
}
