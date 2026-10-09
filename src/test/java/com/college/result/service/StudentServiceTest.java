package com.college.result.service;

import com.college.result.model.OperationResult;
import com.college.result.model.StudentResult;
import com.college.result.util.DBConnectionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StudentServiceTest {

    private final StudentService studentService = new StudentService();
    private final ResultAnalysisService analysisService = new ResultAnalysisService();

    @BeforeEach
    public void setup() {
        DBConnectionUtil.setUsingFallback(true);
        studentService.clearAllStudents();
    }

    @Test
    @DisplayName("Successfully add a student and verify dynamic result calculation")
    public void testAddStudentSuccess() {
        OperationResult res = studentService.addStudent("201", "Kavya Verma", 85, 78, 92, 88);
        assertTrue(res.isSuccess(), "Student should be added successfully");

        StudentResult sr = analysisService.getStudentResultByRollNo("201");
        assertNotNull(sr);
        assertEquals("Kavya Verma", sr.getName());
        assertEquals(343.0, sr.getTotalMarks(), 0.01);
        assertEquals(85.75, sr.getPercentage(), 0.01);
        assertEquals("A", sr.getGrade());
        assertEquals("PASS", sr.getResult());
        assertEquals("75%+", sr.getCategory());
    }

    @Test
    @DisplayName("Reject adding student with duplicate roll number")
    public void testDuplicateRollRejection() {
        OperationResult res1 = studentService.addStudent("202", "Amit Kumar", 70, 70, 70, 70);
        assertTrue(res1.isSuccess());

        OperationResult res2 = studentService.addStudent("202", "Duplicate Amit", 80, 80, 80, 80);
        assertFalse(res2.isSuccess(), "Duplicate roll number must be rejected");
        assertTrue(res2.getMessage().contains("already exists"));
    }

    @Test
    @DisplayName("Reject marks outside 0 to 100 bounds")
    public void testInvalidMarksRejection() {
        OperationResult resHigh = studentService.addStudent("203", "Overachiever", 105, 80, 80, 80);
        assertFalse(resHigh.isSuccess());
        assertTrue(resHigh.getMessage().contains("between 0 and 100"));

        OperationResult resLow = studentService.addStudent("204", "Underachiever", 80, -10, 80, 80);
        assertFalse(resLow.isSuccess());
        assertTrue(resLow.getMessage().contains("between 0 and 100"));
    }

    @Test
    @DisplayName("Update student marks and verify recalculated totals")
    public void testUpdateStudent() {
        studentService.addStudent("205", "Rohan Joshi", 35, 60, 60, 60); // Initially fails DE (Wait, 35 in Java)
        StudentResult initial = analysisService.getStudentResultByRollNo("205");
        assertEquals("FAIL", initial.getResult());

        // Update Java to 75
        OperationResult updateRes = studentService.updateStudent(initial.getStudentId(), "Rohan Joshi", 75, 60, 60, 60);
        assertTrue(updateRes.isSuccess());

        StudentResult updated = analysisService.getStudentResultByRollNo("205");
        assertEquals("PASS", updated.getResult());
        assertEquals(255.0, updated.getTotalMarks(), 0.01);
    }

    @Test
    @DisplayName("Delete student and verify removal")
    public void testDeleteStudent() {
        studentService.addStudent("206", "Student To Delete", 50, 50, 50, 50);
        StudentResult sr = analysisService.getStudentResultByRollNo("206");
        assertNotNull(sr);

        OperationResult delRes = studentService.deleteStudent(sr.getStudentId());
        assertTrue(delRes.isSuccess());

        assertNull(analysisService.getStudentResultByRollNo("206"));
    }

    @Test
    @DisplayName("Seed benchmark demo dataset and verify 35 students")
    public void testSeedDemoData() {
        OperationResult seedRes = studentService.seedDemoData();
        assertTrue(seedRes.isSuccess());

        List<StudentResult> list = analysisService.getAllResults();
        assertEquals(35, list.size(), "Should have exactly 35 benchmark students");
    }
}
