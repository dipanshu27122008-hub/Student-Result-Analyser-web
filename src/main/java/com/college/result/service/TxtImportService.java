package com.college.result.service;

import com.college.result.dao.ImportHistoryDAO;
import com.college.result.dao.MarksDAO;
import com.college.result.dao.StudentDAO;
import com.college.result.model.ImportHistory;
import com.college.result.model.ImportResult;
import com.college.result.model.Student;
import com.college.result.model.StudentMarks;
import com.college.result.util.DBConnectionUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for importing student marks from delimited text (.txt) files.
 * Uses BufferedReader with line-by-line validation and duplicate detection.
 */
public class TxtImportService {
    private static final Logger LOGGER = Logger.getLogger(TxtImportService.class.getName());

    private final StudentDAO studentDAO = new StudentDAO();
    private final MarksDAO marksDAO = new MarksDAO();
    private final ImportHistoryDAO historyDAO = new ImportHistoryDAO();

    public ImportResult importTxt(InputStream inputStream, String fileName) {
        ImportResult result = new ImportResult(fileName, "TXT");
        Set<String> processedRollsInBatch = new HashSet<>();

        int totalCount = 0;
        int successCount = 0;
        int rejectCount = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.trim().isEmpty()) {
                result.setStatus("Failed");
                result.addErrorMessage("TXT file is empty.");
                return result;
            }

            // Parse header columns
            String[] headers = headerLine.split(",");
            if (headers.length < 6) {
                result.setStatus("Failed");
                result.addErrorMessage("Invalid TXT header. Expected at least 6 columns: RollNo,Name,Java,DE,DSA,OS");
                return result;
            }

            Map<String, Integer> colIndexMap = mapHeaders(headers);
            String missingCol = validateRequiredHeaders(colIndexMap);
            if (missingCol != null) {
                result.setStatus("Failed");
                result.addErrorMessage("Missing header column: " + missingCol + ". Required columns: RollNo, Name, Java, DE, DSA, OS");
                return result;
            }

            String line;
            int lineNumber = 1; // header was line 1

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) {
                    continue; // Skip empty lines
                }

                totalCount++;
                String[] tokens = line.split(",", -1);
                if (tokens.length < 6) {
                    rejectCount++;
                    result.addErrorMessage("Row " + lineNumber + ": Insufficient columns. Expected 6 comma-separated values.");
                    continue;
                }

                String rollNo = tokens[colIndexMap.get("rollno")].trim();
                String name = tokens[colIndexMap.get("name")].trim();
                String javaStr = tokens[colIndexMap.get("java")].trim();
                String deStr = tokens[colIndexMap.get("de")].trim();
                String dsaStr = tokens[colIndexMap.get("dsa")].trim();
                String osStr = tokens[colIndexMap.get("os")].trim();

                // Validate Roll No
                if (rollNo.isEmpty()) {
                    rejectCount++;
                    result.addErrorMessage("Row " + lineNumber + ": Roll Number is missing or empty.");
                    continue;
                }

                // Validate Name
                if (name.isEmpty()) {
                    rejectCount++;
                    result.addErrorMessage("Row " + lineNumber + ": Student Name is missing.");
                    continue;
                }
                if (name.length() > 100) {
                    name = name.substring(0, 100);
                }

                // Check Duplicates in current batch
                if (processedRollsInBatch.contains(rollNo.toLowerCase())) {
                    rejectCount++;
                    result.addErrorMessage("Row " + lineNumber + ": Duplicate Roll Number detected in batch: " + rollNo);
                    continue;
                }

                // Check Duplicates in Database
                if (studentDAO.studentExistsByRollNo(rollNo)) {
                    rejectCount++;
                    result.addErrorMessage("Row " + lineNumber + ": Duplicate Roll Number detected: " + rollNo + " (already exists in database).");
                    continue;
                }

                // Parse and validate marks
                Double javaMark = parseMark(javaStr);
                Double deMark = parseMark(deStr);
                Double dsaMark = parseMark(dsaStr);
                Double osMark = parseMark(osStr);

                String markErr = validateMarks(javaMark, deMark, dsaMark, osMark, lineNumber);
                if (markErr != null) {
                    rejectCount++;
                    result.addErrorMessage(markErr);
                    continue;
                }

                // Persist to Database with transaction
                boolean inserted = persistStudentWithMarks(rollNo, name, javaMark, deMark, dsaMark, osMark);
                if (inserted) {
                    successCount++;
                    processedRollsInBatch.add(rollNo.toLowerCase());
                } else {
                    rejectCount++;
                    result.addErrorMessage("Row " + lineNumber + ": Database error occurred while saving student " + rollNo);
                }
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error importing TXT file: " + fileName, e);
            result.addErrorMessage("Failed to read TXT file: " + e.getMessage());
        }

        result.setTotalRows(totalCount);
        result.setImportedRows(successCount);
        result.setRejectedRows(rejectCount);

        String status = successCount > 0 ? (rejectCount == 0 ? "Success" : "Partial Success") : (totalCount == 0 ? "Empty File" : "Failed");
        result.setStatus(status);

        // Record history
        ImportHistory history = new ImportHistory(fileName, "TXT", totalCount, successCount, rejectCount, status);
        historyDAO.insertImportHistory(history);

        return result;
    }

    private boolean persistStudentWithMarks(String rollNo, String name, double java, double de, double dsa, double os) {
        Connection conn = null;
        try {
            conn = DBConnectionUtil.getConnection();
            conn.setAutoCommit(false);

            Student s = new Student(rollNo, name);
            int studentId = studentDAO.insertStudent(s, conn);
            if (studentId <= 0) {
                conn.rollback();
                return false;
            }

            StudentMarks marks = new StudentMarks(java, de, dsa, os);
            marks.setStudentId(studentId);
            boolean marksInserted = marksDAO.insertMarks(marks, conn);
            if (!marksInserted) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Transaction rollback for student " + rollNo, e);
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ignored) {}
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (Exception ignored) {}
            }
        }
    }

    private Map<String, Integer> mapHeaders(String[] headers) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            String h = headers[i].trim().toLowerCase().replaceAll("[^a-z0-9]", "");
            if (h.contains("roll")) map.put("rollno", i);
            else if (h.contains("name")) map.put("name", i);
            else if (h.equals("java")) map.put("java", i);
            else if (h.equals("de") || h.contains("digitalelectronics")) map.put("de", i);
            else if (h.equals("dsa") || h.contains("datastructure")) map.put("dsa", i);
            else if (h.equals("os") || h.contains("operatingsystem")) map.put("os", i);
        }
        return map;
    }

    private String validateRequiredHeaders(Map<String, Integer> map) {
        if (!map.containsKey("rollno")) return "RollNo";
        if (!map.containsKey("name")) return "Name";
        if (!map.containsKey("java")) return "Java";
        if (!map.containsKey("de")) return "DE";
        if (!map.containsKey("dsa")) return "DSA";
        if (!map.containsKey("os")) return "OS";
        return null;
    }

    private Double parseMark(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return null;
        }
    }

    private String validateMarks(Double java, Double de, Double dsa, Double os, int rowNum) {
        if (java == null || java < 0 || java > 100) return "Row " + rowNum + ": Java marks must be between 0 and 100.";
        if (de == null || de < 0 || de > 100) return "Row " + rowNum + ": DE marks must be between 0 and 100.";
        if (dsa == null || dsa < 0 || dsa > 100) return "Row " + rowNum + ": DSA marks must be between 0 and 100.";
        if (os == null || os < 0 || os > 100) return "Row " + rowNum + ": OS marks must be between 0 and 100.";
        return null;
    }
}
