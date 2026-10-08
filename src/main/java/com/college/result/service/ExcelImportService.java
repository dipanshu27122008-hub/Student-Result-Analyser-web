package com.college.result.service;

import com.college.result.dao.ImportHistoryDAO;
import com.college.result.dao.MarksDAO;
import com.college.result.dao.StudentDAO;
import com.college.result.model.ImportHistory;
import com.college.result.model.ImportResult;
import com.college.result.model.Student;
import com.college.result.model.StudentMarks;
import com.college.result.util.DBConnectionUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for importing student marks from Microsoft Excel (.xlsx) files.
 * Uses Apache POI for streaming/DOM parsing with robust row-by-row validation.
 */
public class ExcelImportService {
    private static final Logger LOGGER = Logger.getLogger(ExcelImportService.class.getName());

    private final StudentDAO studentDAO = new StudentDAO();
    private final MarksDAO marksDAO = new MarksDAO();
    private final ImportHistoryDAO historyDAO = new ImportHistoryDAO();

    public ImportResult importExcel(InputStream inputStream, String fileName) {
        ImportResult result = new ImportResult(fileName, "Excel");
        Set<String> processedRollsInBatch = new HashSet<>();

        int totalCount = 0;
        int successCount = 0;
        int rejectCount = 0;

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            if (workbook.getNumberOfSheets() == 0) {
                result.setStatus("Failed");
                result.addErrorMessage("Excel workbook contains no sheets.");
                return result;
            }

            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            if (lastRowNum < 1) {
                result.setStatus("Failed");
                result.addErrorMessage("Excel sheet is empty or contains no data rows.");
                return result;
            }

            // Read and validate Header row
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                result.setStatus("Failed");
                result.addErrorMessage("Header row is missing in Excel file.");
                return result;
            }

            Map<String, Integer> colIndexMap = mapHeaderColumns(headerRow);
            String missingCol = validateRequiredColumns(colIndexMap);
            if (missingCol != null) {
                result.setStatus("Failed");
                result.addErrorMessage("Missing required header column: " + missingCol + ". Expected columns: RollNo, Name, Java, DE, DSA, OS");
                return result;
            }

            // Process data rows
            for (int r = 1; r <= lastRowNum; r++) {
                Row row = sheet.getRow(r);
                if (row == null || isRowEmpty(row)) {
                    continue; // Skip blank rows
                }

                totalCount++;
                int displayRowNumber = r + 1;

                String rollNo = getCellStringValue(row.getCell(colIndexMap.get("rollno")));
                String name = getCellStringValue(row.getCell(colIndexMap.get("name")));
                Double javaMark = getCellNumericValue(row.getCell(colIndexMap.get("java")));
                Double deMark = getCellNumericValue(row.getCell(colIndexMap.get("de")));
                Double dsaMark = getCellNumericValue(row.getCell(colIndexMap.get("dsa")));
                Double osMark = getCellNumericValue(row.getCell(colIndexMap.get("os")));

                // Validate Roll No
                if (rollNo == null || rollNo.trim().isEmpty()) {
                    rejectCount++;
                    result.addErrorMessage("Row " + displayRowNumber + ": Roll Number is missing or empty.");
                    continue;
                }
                rollNo = rollNo.trim();

                // Validate Name
                if (name == null || name.trim().isEmpty()) {
                    rejectCount++;
                    result.addErrorMessage("Row " + displayRowNumber + ": Student Name is missing.");
                    continue;
                }
                name = name.trim();
                if (name.length() > 100) {
                    name = name.substring(0, 100);
                }

                // Check Duplicates in current batch
                if (processedRollsInBatch.contains(rollNo.toLowerCase())) {
                    rejectCount++;
                    result.addErrorMessage("Row " + displayRowNumber + ": Duplicate Roll Number detected in batch: " + rollNo);
                    continue;
                }

                // Check Duplicates in Database
                if (studentDAO.studentExistsByRollNo(rollNo)) {
                    rejectCount++;
                    result.addErrorMessage("Row " + displayRowNumber + ": Duplicate Roll Number detected: " + rollNo + " (already exists in database).");
                    continue;
                }

                // Validate Marks
                String marksValidation = validateMarks(javaMark, deMark, dsaMark, osMark, displayRowNumber);
                if (marksValidation != null) {
                    rejectCount++;
                    result.addErrorMessage(marksValidation);
                    continue;
                }

                // Persist to Database with transactional safety
                boolean inserted = persistStudentWithMarks(rollNo, name, javaMark, deMark, dsaMark, osMark);
                if (inserted) {
                    successCount++;
                    processedRollsInBatch.add(rollNo.toLowerCase());
                } else {
                    rejectCount++;
                    result.addErrorMessage("Row " + displayRowNumber + ": Database error occurred while saving student " + rollNo);
                }
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error importing Excel file: " + fileName, e);
            result.addErrorMessage("Failed to parse Excel file: " + e.getMessage());
        }

        result.setTotalRows(totalCount);
        result.setImportedRows(successCount);
        result.setRejectedRows(rejectCount);

        String status = successCount > 0 ? (rejectCount == 0 ? "Success" : "Partial Success") : (totalCount == 0 ? "Empty File" : "Failed");
        result.setStatus(status);

        // Record history
        ImportHistory history = new ImportHistory(fileName, "Excel", totalCount, successCount, rejectCount, status);
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

    private Map<String, Integer> mapHeaderColumns(Row headerRow) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null) {
                String val = cell.getStringCellValue();
                if (val != null) {
                    val = val.trim().toLowerCase().replaceAll("[^a-z0-9]", "");
                    if (val.contains("roll")) map.put("rollno", i);
                    else if (val.contains("name")) map.put("name", i);
                    else if (val.equals("java")) map.put("java", i);
                    else if (val.equals("de") || val.contains("digitalelectronics")) map.put("de", i);
                    else if (val.equals("dsa") || val.contains("datastructure")) map.put("dsa", i);
                    else if (val.equals("os") || val.contains("operatingsystem")) map.put("os", i);
                }
            }
        }
        return map;
    }

    private String validateRequiredColumns(Map<String, Integer> map) {
        if (!map.containsKey("rollno")) return "RollNo";
        if (!map.containsKey("name")) return "Name";
        if (!map.containsKey("java")) return "Java";
        if (!map.containsKey("de")) return "DE";
        if (!map.containsKey("dsa")) return "DSA";
        if (!map.containsKey("os")) return "OS";
        return null;
    }

    private String validateMarks(Double java, Double de, Double dsa, Double os, int rowNum) {
        if (java == null || java < 0 || java > 100) return "Row " + rowNum + ": Java marks must be a valid number between 0 and 100.";
        if (de == null || de < 0 || de > 100) return "Row " + rowNum + ": DE marks must be a valid number between 0 and 100.";
        if (dsa == null || dsa < 0 || dsa > 100) return "Row " + rowNum + ": DSA marks must be a valid number between 0 and 100.";
        if (os == null || os < 0 || os > 100) return "Row " + rowNum + ": OS marks must be a valid number between 0 and 100.";
        return null;
    }

    private boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue();
        if (cell.getCellType() == CellType.NUMERIC) {
            double val = cell.getNumericCellValue();
            if (val == (long) val) return String.valueOf((long) val);
            return String.valueOf(val);
        }
        if (cell.getCellType() == CellType.BOOLEAN) return String.valueOf(cell.getBooleanCellValue());
        return null;
    }

    private Double getCellNumericValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) return cell.getNumericCellValue();
        if (cell.getCellType() == CellType.STRING) {
            try {
                return Double.parseDouble(cell.getStringCellValue().trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
