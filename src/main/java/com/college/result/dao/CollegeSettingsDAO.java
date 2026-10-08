package com.college.result.dao;

import com.college.result.model.CollegeSettings;
import com.college.result.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for institutional College Settings.
 */
public class CollegeSettingsDAO {
    private static final Logger LOGGER = Logger.getLogger(CollegeSettingsDAO.class.getName());

    public CollegeSettings getSettings() {
        String sql = "SELECT id, college_name, department_name, course_name, semester, academic_year, project_title, logo_path, updated_at " +
                "FROM college_settings WHERE id = 1";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                CollegeSettings s = new CollegeSettings();
                s.setId(rs.getInt("id"));
                s.setCollegeName(rs.getString("college_name"));
                s.setDepartmentName(rs.getString("department_name"));
                s.setCourseName(rs.getString("course_name"));
                s.setSemester(rs.getString("semester"));
                s.setAcademicYear(rs.getString("academic_year"));
                s.setProjectTitle(rs.getString("project_title"));
                s.setLogoPath(rs.getString("logo_path"));
                s.setUpdatedAt(rs.getTimestamp("updated_at"));
                return s;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching college settings", e);
        } finally {
            DBConnectionUtil.close(rs, ps, conn);
        }
        return new CollegeSettings(); // Return default placeholder settings
    }

    public boolean updateSettings(CollegeSettings s) {
        String sql = "UPDATE college_settings SET college_name = ?, department_name = ?, course_name = ?, " +
                "semester = ?, academic_year = ?, project_title = ?, logo_path = ? WHERE id = 1";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, s.getCollegeName());
            ps.setString(2, s.getDepartmentName());
            ps.setString(3, s.getCourseName());
            ps.setString(4, s.getSemester());
            ps.setString(5, s.getAcademicYear());
            ps.setString(6, s.getProjectTitle());
            ps.setString(7, s.getLogoPath());

            int updated = ps.executeUpdate();
            if (updated == 0) {
                // Insert if not yet exists
                String insertSql = "INSERT INTO college_settings (id, college_name, department_name, course_name, semester, academic_year, project_title, logo_path) " +
                        "VALUES (1, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                    insertPs.setString(1, s.getCollegeName());
                    insertPs.setString(2, s.getDepartmentName());
                    insertPs.setString(3, s.getCourseName());
                    insertPs.setString(4, s.getSemester());
                    insertPs.setString(5, s.getAcademicYear());
                    insertPs.setString(6, s.getProjectTitle());
                    insertPs.setString(7, s.getLogoPath());
                    return insertPs.executeUpdate() > 0;
                }
            }
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating college settings", e);
            return false;
        } finally {
            DBConnectionUtil.close(ps, conn);
        }
    }
}
