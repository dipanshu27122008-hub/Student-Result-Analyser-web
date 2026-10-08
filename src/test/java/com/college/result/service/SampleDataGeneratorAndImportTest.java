package com.college.result.service;

import com.college.result.dao.CollegeSettingsDAO;
import com.college.result.model.AnalysisResult;
import com.college.result.model.CollegeSettings;
import com.college.result.model.ImportResult;
import com.college.result.model.StudentResult;
import com.college.result.model.SubjectAnalysis;
import com.college.result.util.DBConnectionUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Generates sample-data/students.xlsx from sample-data/students.txt
 * and executes comprehensive integration tests on Excel/TXT parsing,
 * database persistence, duplicate detection, and PDF generation.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SampleDataGeneratorAndImportTest {

    @BeforeAll
    public static void setUp() {
        // Enforce H2 embedded mode for test executions
        DBConnectionUtil.setUsingFallback(true);
    }

    @Test
    @Order(1)
    @DisplayName("Generate sample-data/students.xlsx from students.txt and verify POI output")
    public void testGenerateSampleExcelFile() throws Exception {
        File txtFile = new File("sample-data/students.txt");
        assertTrue(txtFile.exists(), "sample-data/students.txt must exist");

        File excelFile = new File("sample-data/students.xlsx");

        try (BufferedReader reader = new BufferedReader(new FileReader(txtFile, StandardCharsets.UTF_8));
             Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Students");
            String line;
            int rowIdx = 0;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", -1);
                Row row = sheet.createRow(rowIdx++);

                for (int c = 0; c < parts.length; c++) {
                    String val = parts[c].trim();
                    if (rowIdx > 1 && c >= 2) {
                        try {
                            double num = Double.parseDouble(val);
                            row.createCell(c).setCellValue(num);
                        } catch (Exception e) {
                            row.createCell(c).setCellValue(val);
                        }
                    } else {
                        row.createCell(c).setCellValue(val);
                    }
                }
            }

            try (FileOutputStream fos = new FileOutputStream(excelFile)) {
                workbook.write(fos);
            }
        }

        assertTrue(excelFile.exists(), "students.xlsx should be generated");
        assertTrue(excelFile.length() > 0, "students.xlsx should not be empty");
    }

    @Test
    @Order(2)
    @DisplayName("Import valid TXT sample data and verify database insertion")
    public void testTxtImport() throws Exception {
        TxtImportService txtService = new TxtImportService();
        File txtFile = new File("sample-data/students.txt");

        try (InputStream is = new FileInputStream(txtFile)) {
            ImportResult result = txtService.importTxt(is, "students.txt");
            assertNotNull(result);
            assertTrue(result.getImportedRows() > 0, "Should import rows from students.txt");
            assertEquals("Success", result.getStatus());
        }
    }

    @Test
    @Order(3)
    @DisplayName("Import Excel file and verify duplicate rejection strategy")
    public void testExcelImportDuplicateRejection() throws Exception {
        File excelFile = new File("sample-data/students.xlsx");
        assertTrue(excelFile.exists());

        ExcelImportService excelService = new ExcelImportService();
        try (InputStream is = new FileInputStream(excelFile)) {
            // Since txtImport already imported roll numbers 101-135, Excel re-importing the same rolls
            // must detect and reject existing duplicate roll numbers
            ImportResult result = excelService.importExcel(is, "students.xlsx");
            assertNotNull(result);
            assertTrue(result.getRejectedRows() > 0, "Duplicate records must be rejected");
            assertTrue(result.getErrorMessages().stream().anyMatch(m -> m.contains("Duplicate Roll Number detected")));
        }
    }

    @Test
    @Order(4)
    @DisplayName("Reject malformed TXT rows with invalid marks (> 100 or < 0)")
    public void testInvalidMarksRejection() throws Exception {
        String badContent = "RollNo,Name,Java,DE,DSA,OS\n" +
                "999,Invalid Student,105,80,75,90\n" +
                "998,Negative Student,80,-5,75,90\n";

        TxtImportService txtService = new TxtImportService();
        try (InputStream is = new ByteArrayInputStream(badContent.getBytes(StandardCharsets.UTF_8))) {
            ImportResult res = txtService.importTxt(is, "invalid.txt");
            assertEquals(2, res.getTotalRows());
            assertEquals(0, res.getImportedRows());
            assertEquals(2, res.getRejectedRows());
            assertTrue(res.getErrorMessages().get(0).contains("between 0 and 100"));
        }
    }

    @Test
    @Order(5)
    @DisplayName("Generate complete PDF report with OpenPDF and verify output stream")
    public void testPdfReportGeneration() throws Exception {
        ResultAnalysisService analysisService = new ResultAnalysisService();
        CollegeSettingsDAO settingsDAO = new CollegeSettingsDAO();
        PdfReportService pdfService = new PdfReportService();

        List<StudentResult> results = analysisService.getAllResults();
        AnalysisResult analysis = analysisService.performOverallAnalysis(results);
        Map<String, SubjectAnalysis> subjectMap = analysisService.performSubjectAnalysis(results);
        CollegeSettings settings = settingsDAO.getSettings();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        File logoFile = new File("src/main/webapp/images/college-logo.png");

        assertDoesNotThrow(() -> {
            pdfService.generateReport(baos, settings, analysis, subjectMap, results, logoFile.getAbsolutePath(), "ALL");
        });

        byte[] pdfBytes = baos.toByteArray();
        assertTrue(pdfBytes.length > 5000, "PDF file size must be non-trivial (> 5KB)");

        // Verify PDF Magic Bytes %PDF-
        String header = new String(pdfBytes, 0, 5, StandardCharsets.US_ASCII);
        assertEquals("%PDF-", header, "Generated file must be valid PDF format");
    }
}
