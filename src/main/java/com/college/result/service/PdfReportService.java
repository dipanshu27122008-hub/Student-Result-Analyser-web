package com.college.result.service;

import com.college.result.model.AnalysisResult;
import com.college.result.model.CollegeSettings;
import com.college.result.model.StudentResult;
import com.college.result.model.SubjectAnalysis;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.File;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for generating comprehensive, publication-quality academic PDF reports
 * using OpenPDF with cover page, institutional branding, statistics, and tables.
 */
public class PdfReportService {
    private static final Logger LOGGER = Logger.getLogger(PdfReportService.class.getName());

    // Academic Color Palette
    private static final Color PRIMARY_COLOR = new Color(26, 54, 93);       // Navy #1A365D
    private static final Color SECONDARY_COLOR = new Color(43, 108, 176);   // Slate Blue #2B6CB0
    private static final Color ACCENT_GREEN = new Color(34, 139, 34);       // Pass Green
    private static final Color ACCENT_RED = new Color(178, 34, 34);         // Fail Red
    private static final Color BG_LIGHT_GRAY = new Color(245, 247, 250);   // Table zebra
    private static final Color BORDER_GRAY = new Color(226, 232, 240);

    // Fonts
    private static final Font FONT_TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, PRIMARY_COLOR);
    private static final Font FONT_SUBTITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, SECONDARY_COLOR);
    private static final Font FONT_HEADING = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, PRIMARY_COLOR);
    private static final Font FONT_SUBHEADING = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
    private static final Font FONT_BODY = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.DARK_GRAY);
    private static final Font FONT_BODY_BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);
    private static final Font FONT_TH = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
    private static final Font FONT_PASS = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, ACCENT_GREEN);
    private static final Font FONT_FAIL = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, ACCENT_RED);

    /**
     * Generates the complete PDF report and writes it to the provided output stream.
     */
    public void generateReport(OutputStream out,
                               CollegeSettings settings,
                               AnalysisResult analysis,
                               Map<String, SubjectAnalysis> subjectAnalysis,
                               List<StudentResult> studentResults,
                               String logoAbsolutePath,
                               String reportFilter) throws DocumentException {

        Document document = new Document(PageSize.A4, 36, 36, 46, 46);
        PdfWriter writer = PdfWriter.getInstance(document, out);

        // Header and Footer Page Event
        HeaderFooterPageEvent event = new HeaderFooterPageEvent(settings);
        writer.setPageEvent(event);

        document.open();

        // 1. Cover Page
        buildCoverPage(document, settings, logoAbsolutePath);
        document.newPage();

        // Enable running header & footer for subsequent pages
        event.setHeaderFooterEnabled(true);

        // 2. Executive Summary & Batch Statistics
        buildExecutiveSummary(document, analysis, reportFilter);

        // 3. Overall Statistics & Category Distribution
        buildCategoryDistributionSection(document, analysis);

        // 4. Subject-wise Analysis Table
        buildSubjectAnalysisSection(document, subjectAnalysis);

        // 5. Performer Highlights (Top and Lowest)
        buildPerformerHighlightsSection(document, analysis);

        // 6. Detailed Student Results Table
        buildStudentResultsTable(document, studentResults);

        // 7. Conclusion / Sign-off
        buildConclusionSection(document);

        document.close();
    }

    private void buildCoverPage(Document doc, CollegeSettings settings, String logoAbsolutePath) throws DocumentException {
        Paragraph spacer = new Paragraph();
        spacer.setSpacingBefore(30);
        doc.add(spacer);

        // Try to add Logo
        if (logoAbsolutePath != null && new File(logoAbsolutePath).exists()) {
            try {
                Image logo = Image.getInstance(logoAbsolutePath);
                logo.setAlignment(Image.ALIGN_CENTER);
                logo.scaleToFit(110, 110);
                doc.add(logo);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Could not load logo for PDF cover: " + e.getMessage());
            }
        }

        Paragraph pCollege = new Paragraph(settings.getCollegeName().toUpperCase(), FONT_TITLE);
        pCollege.setAlignment(Element.ALIGN_CENTER);
        pCollege.setSpacingBefore(20);
        doc.add(pCollege);

        Paragraph pDept = new Paragraph("DEPARTMENT OF " + settings.getDepartmentName().toUpperCase(), FONT_SUBTITLE);
        pDept.setAlignment(Element.ALIGN_CENTER);
        pDept.setSpacingBefore(10);
        doc.add(pDept);

        Paragraph divider = new Paragraph("____________________________________________________",
                FontFactory.getFont(FontFactory.HELVETICA, 10, Color.LIGHT_GRAY));
        divider.setAlignment(Element.ALIGN_CENTER);
        divider.setSpacingBefore(15);
        divider.setSpacingAfter(30);
        doc.add(divider);

        Paragraph pProject = new Paragraph(settings.getProjectTitle().toUpperCase(),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, PRIMARY_COLOR));
        pProject.setAlignment(Element.ALIGN_CENTER);
        doc.add(pProject);

        Paragraph pSub = new Paragraph("COMPREHENSIVE BATCH PERFORMANCE & RESULT ANALYSIS REPORT",
                FontFactory.getFont(FontFactory.HELVETICA, 11, Color.GRAY));
        pSub.setAlignment(Element.ALIGN_CENTER);
        pSub.setSpacingBefore(8);
        doc.add(pSub);

        // Metadata box
        PdfPTable metaTable = new PdfPTable(2);
        metaTable.setWidthPercentage(75);
        metaTable.setSpacingBefore(60);

        addMetaRow(metaTable, "Program / Course:", settings.getCourseName());
        addMetaRow(metaTable, "Semester:", settings.getSemester());
        addMetaRow(metaTable, "Academic Year:", settings.getAcademicYear());
        addMetaRow(metaTable, "Report Date:", new SimpleDateFormat("dd MMMM yyyy, hh:mm a").format(new Date()));
        addMetaRow(metaTable, "Generated By:", "Student Result Analysis System (Admin)");

        doc.add(metaTable);
    }

    private void addMetaRow(PdfPTable table, String label, String value) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, FONT_BODY_BOLD));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setPadding(6);
        c1.setHorizontalAlignment(Element.ALIGN_RIGHT);

        PdfPCell c2 = new PdfPCell(new Phrase(value, FONT_BODY));
        c2.setBorder(Rectangle.NO_BORDER);
        c2.setPadding(6);
        c2.setHorizontalAlignment(Element.ALIGN_LEFT);

        table.addCell(c1);
        table.addCell(c2);
    }

    private void buildExecutiveSummary(Document doc, AnalysisResult analysis, String reportFilter) throws DocumentException {
        Paragraph heading = new Paragraph("1. Executive Summary & Batch Overview", FONT_HEADING);
        heading.setSpacingBefore(10);
        heading.setSpacingAfter(8);
        doc.add(heading);

        String filterNote = (reportFilter != null && !reportFilter.equalsIgnoreCase("ALL"))
                ? " (Filtered view: " + reportFilter + ")" : "";

        Paragraph intro = new Paragraph(
                "This report provides an analytical evaluation of student performance across all primary courses. " +
                        "A student is declared PASS only if every individual subject score equals or exceeds the mandatory passing threshold of 40 marks out of 100." + filterNote,
                FONT_BODY);
        intro.setSpacingAfter(12);
        doc.add(intro);

        // 4 KPI Summary Cards in a 4-column Table
        PdfPTable kpiTable = new PdfPTable(4);
        kpiTable.setWidthPercentage(100);
        kpiTable.setSpacingAfter(15);

        addKpiCell(kpiTable, "Total Students", String.valueOf(analysis.getTotalStudents()), PRIMARY_COLOR);
        addKpiCell(kpiTable, "Passed Students", String.valueOf(analysis.getPassedStudents()), ACCENT_GREEN);
        addKpiCell(kpiTable, "Failed Students", String.valueOf(analysis.getFailedStudents()), ACCENT_RED);
        addKpiCell(kpiTable, "Pass Percentage", analysis.getPassPercentage() + "%", SECONDARY_COLOR);

        doc.add(kpiTable);
    }

    private void addKpiCell(PdfPTable table, String label, String value, Color color) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(10);
        cell.setBackgroundColor(BG_LIGHT_GRAY);
        cell.setBorderColor(BORDER_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        Paragraph pVal = new Paragraph(value, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, color));
        pVal.setAlignment(Element.ALIGN_CENTER);
        Paragraph pLbl = new Paragraph(label, FONT_BODY);
        pLbl.setAlignment(Element.ALIGN_CENTER);

        cell.addElement(pVal);
        cell.addElement(pLbl);
        table.addCell(cell);
    }

    private void buildCategoryDistributionSection(Document doc, AnalysisResult analysis) throws DocumentException {
        Paragraph heading = new Paragraph("2. Percentage Category Distribution", FONT_HEADING);
        heading.setSpacingBefore(10);
        heading.setSpacingAfter(8);
        doc.add(heading);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{30, 25, 25, 20});
        table.setSpacingAfter(15);

        addTableHeader(table, "Category Range", "Student Count", "Category Share (%)", "Performance Grade");

        int total = Math.max(1, analysis.getTotalStudents());
        addTableRow(table, "75% and above (Distinction)", String.valueOf(analysis.getCategory75Plus()),
                String.format("%.2f%%", (analysis.getCategory75Plus() * 100.0) / total), "Excellent (A/A+)");
        addTableRow(table, "65% to 74.99% (First Class)", String.valueOf(analysis.getCategory65To74()),
                String.format("%.2f%%", (analysis.getCategory65To74() * 100.0) / total), "Very Good (B+)");
        addTableRow(table, "40% to 64.99% (Pass Class)", String.valueOf(analysis.getCategory40To64()),
                String.format("%.2f%%", (analysis.getCategory40To64() * 100.0) / total), "Average (B/C/D)");
        addTableRow(table, "Below 40% (Fail Class)", String.valueOf(analysis.getCategoryBelow40()),
                String.format("%.2f%%", (analysis.getCategoryBelow40() * 100.0) / total), "Needs Improvement (F)");

        doc.add(table);
    }

    private void buildSubjectAnalysisSection(Document doc, Map<String, SubjectAnalysis> map) throws DocumentException {
        Paragraph heading = new Paragraph("3. Subject-Wise Performance Analysis", FONT_HEADING);
        heading.setSpacingBefore(10);
        heading.setSpacingAfter(8);
        doc.add(heading);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{28, 12, 12, 12, 12, 12, 12});
        table.setSpacingAfter(15);

        addTableHeader(table, "Subject", "Average", "Highest", "Lowest", "Passed", "Failed", "Pass %");

        if (map != null) {
            for (SubjectAnalysis sa : map.values()) {
                table.addCell(createBodyCell(sa.getSubjectName(), Element.ALIGN_LEFT, false));
                table.addCell(createBodyCell(String.valueOf(sa.getAverageMarks()), Element.ALIGN_CENTER, false));
                table.addCell(createBodyCell(String.valueOf(sa.getHighestMarks()), Element.ALIGN_CENTER, false));
                table.addCell(createBodyCell(String.valueOf(sa.getLowestMarks()), Element.ALIGN_CENTER, false));
                table.addCell(createBodyCell(String.valueOf(sa.getPassedStudents()), Element.ALIGN_CENTER, false));
                table.addCell(createBodyCell(String.valueOf(sa.getFailedStudents()), Element.ALIGN_CENTER, false));
                table.addCell(createBodyCell(sa.getPassPercentage() + "%", Element.ALIGN_CENTER, true));
            }
        }

        doc.add(table);
    }

    private void buildPerformerHighlightsSection(Document doc, AnalysisResult analysis) throws DocumentException {
        Paragraph heading = new Paragraph("4. Performer Highlights", FONT_HEADING);
        heading.setSpacingBefore(10);
        heading.setSpacingAfter(8);
        doc.add(heading);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(15);

        // Top Performer
        PdfPCell topCell = new PdfPCell();
        topCell.setPadding(8);
        topCell.setBorderColor(BORDER_GRAY);
        Paragraph topTitle = new Paragraph("Top Performer", FONT_SUBHEADING);
        Paragraph topDetails = new Paragraph(
                (analysis.getTopPerformerName() != null ? analysis.getTopPerformerName() : "N/A") +
                        " (Roll No: " + (analysis.getTopPerformerRoll() != null ? analysis.getTopPerformerRoll() : "N/A") + ")\n" +
                        "Percentage: " + analysis.getTopPerformerPercentage() + "%",
                FONT_BODY_BOLD);
        topCell.addElement(topTitle);
        topCell.addElement(topDetails);

        // Lowest Performer
        PdfPCell lowCell = new PdfPCell();
        lowCell.setPadding(8);
        lowCell.setBorderColor(BORDER_GRAY);
        Paragraph lowTitle = new Paragraph("Lowest Performer", FONT_SUBHEADING);
        Paragraph lowDetails = new Paragraph(
                (analysis.getLowestPerformerName() != null ? analysis.getLowestPerformerName() : "N/A") +
                        " (Roll No: " + (analysis.getLowestPerformerRoll() != null ? analysis.getLowestPerformerRoll() : "N/A") + ")\n" +
                        "Percentage: " + analysis.getLowestPerformerPercentage() + "%",
                FONT_BODY_BOLD);
        lowCell.addElement(lowTitle);
        lowCell.addElement(lowDetails);

        table.addCell(topCell);
        table.addCell(lowCell);
        doc.add(table);
    }

    private void buildStudentResultsTable(Document doc, List<StudentResult> studentResults) throws DocumentException {
        Paragraph heading = new Paragraph("5. Detailed Student Roster & Marks Record", FONT_HEADING);
        heading.setSpacingBefore(10);
        heading.setSpacingAfter(8);
        doc.add(heading);

        PdfPTable table = new PdfPTable(10);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{10, 22, 8, 8, 8, 8, 10, 10, 8, 8});
        table.setSpacingAfter(15);

        addTableHeader(table, "Roll No", "Student Name", "Java", "DE", "DSA", "OS", "Total", "%", "Grade", "Result");

        if (studentResults != null && !studentResults.isEmpty()) {
            boolean zebra = false;
            for (StudentResult sr : studentResults) {
                Color rowBg = zebra ? BG_LIGHT_GRAY : Color.WHITE;
                zebra = !zebra;

                table.addCell(createCell(sr.getRollNo(), Element.ALIGN_CENTER, FONT_BODY, rowBg));
                table.addCell(createCell(sr.getName(), Element.ALIGN_LEFT, FONT_BODY, rowBg));
                table.addCell(createCell(format(sr.getJavaMarks()), Element.ALIGN_CENTER, FONT_BODY, rowBg));
                table.addCell(createCell(format(sr.getDeMarks()), Element.ALIGN_CENTER, FONT_BODY, rowBg));
                table.addCell(createCell(format(sr.getDsaMarks()), Element.ALIGN_CENTER, FONT_BODY, rowBg));
                table.addCell(createCell(format(sr.getOsMarks()), Element.ALIGN_CENTER, FONT_BODY, rowBg));
                table.addCell(createCell(format(sr.getTotalMarks()), Element.ALIGN_CENTER, FONT_BODY_BOLD, rowBg));
                table.addCell(createCell(sr.getPercentage() + "%", Element.ALIGN_CENTER, FONT_BODY_BOLD, rowBg));
                table.addCell(createCell(sr.getGrade(), Element.ALIGN_CENTER, FONT_BODY_BOLD, rowBg));

                Font resFont = "PASS".equalsIgnoreCase(sr.getResult()) ? FONT_PASS : FONT_FAIL;
                table.addCell(createCell(sr.getResult(), Element.ALIGN_CENTER, resFont, rowBg));
            }
        } else {
            PdfPCell empty = new PdfPCell(new Phrase("No student records available.", FONT_BODY));
            empty.setColspan(10);
            empty.setHorizontalAlignment(Element.ALIGN_CENTER);
            empty.setPadding(10);
            table.addCell(empty);
        }

        doc.add(table);
    }

    private void buildConclusionSection(Document doc) throws DocumentException {
        Paragraph heading = new Paragraph("6. Verification & Signatures", FONT_HEADING);
        heading.setSpacingBefore(15);
        heading.setSpacingAfter(25);
        doc.add(heading);

        PdfPTable sigTable = new PdfPTable(3);
        sigTable.setWidthPercentage(100);

        PdfPCell c1 = new PdfPCell(new Phrase("Prepared By:\nResult Coordinator", FONT_BODY_BOLD));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell c2 = new PdfPCell(new Phrase("Verified By:\nHead of Department", FONT_BODY_BOLD));
        c2.setBorder(Rectangle.NO_BORDER);
        c2.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell c3 = new PdfPCell(new Phrase("Approved By:\nPrincipal / Examination Controller", FONT_BODY_BOLD));
        c3.setBorder(Rectangle.NO_BORDER);
        c3.setHorizontalAlignment(Element.ALIGN_CENTER);

        sigTable.addCell(c1);
        sigTable.addCell(c2);
        sigTable.addCell(c3);

        doc.add(sigTable);
    }

    private void addTableHeader(PdfPTable table, String... headers) {
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, FONT_TH));
            cell.setBackgroundColor(PRIMARY_COLOR);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            cell.setBorderColor(Color.GRAY);
            table.addCell(cell);
        }
    }

    private void addTableRow(PdfPTable table, String c1, String c2, String c3, String c4) {
        table.addCell(createBodyCell(c1, Element.ALIGN_LEFT, false));
        table.addCell(createBodyCell(c2, Element.ALIGN_CENTER, false));
        table.addCell(createBodyCell(c3, Element.ALIGN_CENTER, false));
        table.addCell(createBodyCell(c4, Element.ALIGN_LEFT, false));
    }

    private PdfPCell createBodyCell(String text, int align, boolean bold) {
        PdfPCell cell = new PdfPCell(new Phrase(text, bold ? FONT_BODY_BOLD : FONT_BODY));
        cell.setHorizontalAlignment(align);
        cell.setPadding(5);
        cell.setBorderColor(BORDER_GRAY);
        return cell;
    }

    private PdfPCell createCell(String text, int align, Font font, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(align);
        cell.setPadding(5);
        cell.setBackgroundColor(bg);
        cell.setBorderColor(BORDER_GRAY);
        return cell;
    }

    private String format(double d) {
        if (d == (long) d) return String.format("%d", (long) d);
        return String.format("%.1f", d);
    }

    /**
     * Page Event to draw Running Header and Footer on pages after cover page.
     */
    private static class HeaderFooterPageEvent extends PdfPageEventHelper {
        private final CollegeSettings settings;
        private boolean headerFooterEnabled = false;

        public HeaderFooterPageEvent(CollegeSettings settings) {
            this.settings = settings;
        }

        public void setHeaderFooterEnabled(boolean enabled) {
            this.headerFooterEnabled = enabled;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            if (!headerFooterEnabled) {
                return;
            }

            Rectangle rect = document.getPageSize();

            // Running Header
            Paragraph header = new Paragraph(
                    settings.getCollegeName() + " | " + settings.getCourseName() + " Result Analysis (" + settings.getAcademicYear() + ")",
                    FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY));
            header.setAlignment(Element.ALIGN_RIGHT);

            // Running Footer
            Paragraph footer = new Paragraph(
                    "Page " + writer.getPageNumber() + " | Confidential Academic Record",
                    FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY));
            footer.setAlignment(Element.ALIGN_CENTER);

            try {
                PdfPTable hTable = new PdfPTable(1);
                hTable.setTotalWidth(rect.getWidth() - 72);
                PdfPCell hCell = new PdfPCell(header);
                hCell.setBorder(Rectangle.BOTTOM);
                hCell.setBorderColor(BORDER_GRAY);
                hCell.setPaddingBottom(4);
                hTable.addCell(hCell);
                hTable.writeSelectedRows(0, -1, 36, rect.getHeight() - 20, writer.getDirectContent());

                PdfPTable fTable = new PdfPTable(1);
                fTable.setTotalWidth(rect.getWidth() - 72);
                PdfPCell fCell = new PdfPCell(footer);
                fCell.setBorder(Rectangle.TOP);
                fCell.setBorderColor(BORDER_GRAY);
                fCell.setPaddingTop(4);
                fTable.addCell(fCell);
                fTable.writeSelectedRows(0, -1, 36, 32, writer.getDirectContent());
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error drawing page header/footer: " + e.getMessage());
            }
        }
    }
}
