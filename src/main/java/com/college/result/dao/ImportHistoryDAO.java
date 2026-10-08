package com.college.result.dao;

import com.college.result.model.ImportHistory;
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
 * Data Access Object for Import History logging.
 */
public class ImportHistoryDAO {
    private static final Logger LOGGER = Logger.getLogger(ImportHistoryDAO.class.getName());

    public int insertImportHistory(ImportHistory history) {
        String sql = "INSERT INTO import_history (file_name, file_type, total_records, successful_records, rejected_records, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnectionUtil.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, history.getFileName());
            ps.setString(2, history.getFileType());
            ps.setInt(3, history.getTotalRecords());
            ps.setInt(4, history.getSuccessfulRecords());
            ps.setInt(5, history.getRejectedRecords());
            ps.setString(6, history.getStatus());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        history.setId(id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting import history for file: " + history.getFileName(), e);
        } finally {
            DBConnectionUtil.close(ps, conn);
        }
        return -1;
    }

    public List<ImportHistory> getAllHistory() {
        List<ImportHistory> list = new ArrayList<>();
        String sql = "SELECT id, file_name, file_type, total_records, successful_records, rejected_records, status, imported_at " +
                "FROM import_history ORDER BY imported_at DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnectionUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ImportHistory(
                        rs.getInt("id"),
                        rs.getString("file_name"),
                        rs.getString("file_type"),
                        rs.getInt("total_records"),
                        rs.getInt("successful_records"),
                        rs.getInt("rejected_records"),
                        rs.getString("status"),
                        rs.getTimestamp("imported_at")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching import history", e);
        } finally {
            DBConnectionUtil.close(rs, ps, conn);
        }
        return list;
    }
}
