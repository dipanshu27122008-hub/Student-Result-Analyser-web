package com.college.result.service;

import com.college.result.model.AnalysisResult;
import com.college.result.model.Student;
import com.college.result.model.StudentMarks;
import com.college.result.model.StudentResult;
import com.college.result.model.SubjectAnalysis;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Rigorous Unit Tests for result calculation, pass/fail rules,
 * percentage classification categories, grading, and statistical metrics.
 */
public class ResultAnalysisServiceTest {

    @Test
    @DisplayName("Case 1: All subjects >= 40 results in PASS")
    public void testPassAllSubjects() {
        Student s = new Student("101", "Rahul Kumar");
        StudentMarks m = new StudentMarks(80, 75, 60, 55);
        StudentResult res = ResultAnalysisService.calculateResult(s, m);

        assertEquals("PASS", res.getResult());
        assertEquals(270.0, res.getTotalMarks());
        assertEquals(67.50, res.getPercentage());
        assertEquals("B", res.getGrade());
        assertEquals("65–74.99%", res.getCategory());
        assertTrue(res.getFailedSubjects().isEmpty());
    }

    @Test
    @DisplayName("Case 2: One subject < 40 results in FAIL even with high overall percentage")
    public void testFailOneSubjectHighPercentage() {
        Student s = new Student("107", "Rajesh Gupta");
        StudentMarks m = new StudentMarks(92, 35, 90, 88); // DE is 35 (< 40)
        StudentResult res = ResultAnalysisService.calculateResult(s, m);

        assertEquals("FAIL", res.getResult(), "Must be FAIL because Digital Electronics is 35 (< 40)");
        assertEquals(305.0, res.getTotalMarks());
        assertEquals(76.25, res.getPercentage());
        assertEquals("75%+", res.getCategory());
        assertEquals(1, res.getFailedSubjects().size());
        assertTrue(res.getFailedSubjects().get(0).contains("Digital Electronics"));
    }

    @Test
    @DisplayName("Case 3: Exactly 40% boundary belongs to 40-64.99% category and grade D")
    public void testExact40Percent() {
        Student s = new Student("106", "Ananya Roy");
        StudentMarks m = new StudentMarks(40, 40, 40, 40);
        StudentResult res = ResultAnalysisService.calculateResult(s, m);

        assertEquals("PASS", res.getResult());
        assertEquals(160.0, res.getTotalMarks());
        assertEquals(40.0, res.getPercentage());
        assertEquals("D", res.getGrade());
        assertEquals("40–64.99%", res.getCategory());
    }

    @Test
    @DisplayName("Case 4: Exactly 65% boundary belongs to 65-74.99% category and grade B")
    public void testExact65Percent() {
        Student s = new Student("105", "Vikram Malhotra");
        StudentMarks m = new StudentMarks(65, 65, 65, 65);
        StudentResult res = ResultAnalysisService.calculateResult(s, m);

        assertEquals("PASS", res.getResult());
        assertEquals(260.0, res.getTotalMarks());
        assertEquals(65.0, res.getPercentage());
        assertEquals("B", res.getGrade());
        assertEquals("65–74.99%", res.getCategory());
    }

    @Test
    @DisplayName("Case 5: Exactly 75% boundary belongs to 75%+ category and grade B+")
    public void testExact75Percent() {
        Student s = new Student("104", "Sneha Verma");
        StudentMarks m = new StudentMarks(75, 75, 75, 75);
        StudentResult res = ResultAnalysisService.calculateResult(s, m);

        assertEquals("PASS", res.getResult());
        assertEquals(300.0, res.getTotalMarks());
        assertEquals(75.0, res.getPercentage());
        assertEquals("B+", res.getGrade());
        assertEquals("75%+", res.getCategory());
    }

    @Test
    @DisplayName("Case 6: Exactly 100% boundary belongs to 75%+ category and grade A+")
    public void testExact100Percent() {
        Student s = new Student("131", "Nitin Gadkari");
        StudentMarks m = new StudentMarks(100, 100, 100, 100);
        StudentResult res = ResultAnalysisService.calculateResult(s, m);

        assertEquals("PASS", res.getResult());
        assertEquals(400.0, res.getTotalMarks());
        assertEquals(100.0, res.getPercentage());
        assertEquals("A+", res.getGrade());
        assertEquals("75%+", res.getCategory());
    }

    @Test
    @DisplayName("Case 7: 0 marks results in 0.00%, grade F, Below 40% category, and FAIL")
    public void testExact0Marks() {
        Student s = new Student("132", "Varun Dhawan");
        StudentMarks m = new StudentMarks(0, 0, 0, 0);
        StudentResult res = ResultAnalysisService.calculateResult(s, m);

        assertEquals("FAIL", res.getResult());
        assertEquals(0.0, res.getTotalMarks());
        assertEquals(0.0, res.getPercentage());
        assertEquals("F", res.getGrade());
        assertEquals("Below 40%", res.getCategory());
        assertEquals(4, res.getFailedSubjects().size());
    }

    @Test
    @DisplayName("Overall and Subject Analysis aggregations correctly compute metrics")
    public void testOverallAndSubjectAnalysis() {
        List<StudentResult> results = new ArrayList<>();

        Student s1 = new Student("101", "Priya");
        results.add(ResultAnalysisService.calculateResult(s1, new StudentMarks(90, 90, 90, 90))); // 90%, PASS

        Student s2 = new Student("102", "Amit");
        results.add(ResultAnalysisService.calculateResult(s2, new StudentMarks(80, 70, 70, 70))); // 72.5%, PASS

        Student s3 = new Student("103", "Karan");
        results.add(ResultAnalysisService.calculateResult(s3, new StudentMarks(30, 80, 80, 80))); // 67.5%, FAIL (Java 30)

        ResultAnalysisService service = new ResultAnalysisService();
        AnalysisResult overall = service.performOverallAnalysis(results);

        assertEquals(3, overall.getTotalStudents());
        assertEquals(2, overall.getPassedStudents());
        assertEquals(1, overall.getFailedStudents());
        assertEquals(66.67, overall.getPassPercentage());
        assertEquals(33.33, overall.getFailPercentage());
        assertEquals("Priya", overall.getTopPerformerName());
        assertEquals(90.0, overall.getTopPerformerPercentage());
        assertEquals("Karan", overall.getLowestPerformerName());
        assertEquals(67.5, overall.getLowestPerformerPercentage());

        Map<String, SubjectAnalysis> subMap = service.performSubjectAnalysis(results);
        SubjectAnalysis javaSa = subMap.get("Java");
        assertNotNull(javaSa);
        assertEquals(66.67, javaSa.getAverageMarks()); // (90 + 80 + 30) / 3 = 66.67
        assertEquals(90.0, javaSa.getHighestMarks());
        assertEquals(30.0, javaSa.getLowestMarks());
        assertEquals(2, javaSa.getPassedStudents());
        assertEquals(1, javaSa.getFailedStudents());
        assertEquals(66.67, javaSa.getPassPercentage());
    }
}
