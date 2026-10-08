package com.college.result.service;

import com.college.result.dao.MarksDAO;
import com.college.result.dao.StudentDAO;
import com.college.result.model.AnalysisResult;
import com.college.result.model.Student;
import com.college.result.model.StudentMarks;
import com.college.result.model.StudentResult;
import com.college.result.model.SubjectAnalysis;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service handling all result calculations, grading, categorization,
 * overall batch analytics, and subject-wise performance metrics.
 */
public class ResultAnalysisService {

    public static final double PASS_MARK_THRESHOLD = 40.0;
    public static final double MAX_SUBJECT_MARKS = 100.0;
    public static final double MAX_TOTAL_MARKS = 400.0;

    private final StudentDAO studentDAO;
    private final MarksDAO marksDAO;

    public ResultAnalysisService() {
        this.studentDAO = new StudentDAO();
        this.marksDAO = new MarksDAO();
    }

    public ResultAnalysisService(StudentDAO studentDAO, MarksDAO marksDAO) {
        this.studentDAO = studentDAO;
        this.marksDAO = marksDAO;
    }

    /**
     * Calculates full result for a student given their marks.
     */
    public static StudentResult calculateResult(Student student, StudentMarks marks) {
        StudentResult res = new StudentResult();
        if (student != null) {
            res.setStudentId(student.getId());
            res.setRollNo(student.getRollNo());
            res.setName(student.getName());
        } else if (marks != null) {
            res.setStudentId(marks.getStudentId());
        }

        double java = marks != null ? marks.getJavaMarks() : 0.0;
        double de = marks != null ? marks.getDeMarks() : 0.0;
        double dsa = marks != null ? marks.getDsaMarks() : 0.0;
        double os = marks != null ? marks.getOsMarks() : 0.0;

        res.setJavaMarks(java);
        res.setDeMarks(de);
        res.setDsaMarks(dsa);
        res.setOsMarks(os);

        double total = roundToTwoDecimals(java + de + dsa + os);
        res.setTotalMarks(total);
        res.setMaxMarks(MAX_TOTAL_MARKS);

        double percentage = roundToTwoDecimals((total / MAX_TOTAL_MARKS) * 100.0);
        res.setPercentage(percentage);

        // Grade based on overall percentage
        res.setGrade(calculateGrade(percentage));

        // Mutually exclusive percentage category
        res.setCategory(calculateCategory(percentage));

        // Pass/Fail rule: Every subject mark must be >= 40
        List<String> failed = new ArrayList<>();
        if (java < PASS_MARK_THRESHOLD) {
            failed.add("Java (" + formatMarks(java) + "/100)");
        }
        if (de < PASS_MARK_THRESHOLD) {
            failed.add("Digital Electronics (" + formatMarks(de) + "/100)");
        }
        if (dsa < PASS_MARK_THRESHOLD) {
            failed.add("Data Structures & Algorithms (" + formatMarks(dsa) + "/100)");
        }
        if (os < PASS_MARK_THRESHOLD) {
            failed.add("Operating System (" + formatMarks(os) + "/100)");
        }

        res.setFailedSubjects(failed);
        res.setResult(failed.isEmpty() ? "PASS" : "FAIL");

        // Subject stats: Highest, Lowest, Average
        determineSubjectStats(res, java, de, dsa, os);

        return res;
    }

    private static void determineSubjectStats(StudentResult res, double java, double de, double dsa, double os) {
        double maxMark = java;
        String maxSub = "Java";
        double minMark = java;
        String minSub = "Java";

        if (de > maxMark) { maxMark = de; maxSub = "Digital Electronics"; }
        if (de < minMark) { minMark = de; minSub = "Digital Electronics"; }

        if (dsa > maxMark) { maxMark = dsa; maxSub = "DSA"; }
        if (dsa < minMark) { minMark = dsa; minSub = "DSA"; }

        if (os > maxMark) { maxMark = os; maxSub = "Operating System"; }
        if (os < minMark) { minMark = os; minSub = "Operating System"; }

        res.setHighestSubject(maxSub + " (" + formatMarks(maxMark) + ")");
        res.setHighestSubjectMarks(maxMark);
        res.setLowestSubject(minSub + " (" + formatMarks(minMark) + ")");
        res.setLowestSubjectMarks(minMark);
        res.setAverageSubjectMarks(roundToTwoDecimals((java + de + dsa + os) / 4.0));
    }

    /**
     * Grade System:
     * 90–100       A+
     * 80–89.99     A
     * 70–79.99     B+
     * 60–69.99     B
     * 50–59.99     C
     * 40–49.99     D
     * Below 40     F
     */
    public static String calculateGrade(double percentage) {
        if (percentage >= 90.0) return "A+";
        if (percentage >= 80.0) return "A";
        if (percentage >= 70.0) return "B+";
        if (percentage >= 60.0) return "B";
        if (percentage >= 50.0) return "C";
        if (percentage >= 40.0) return "D";
        return "F";
    }

    /**
     * Mutually exclusive percentage categories:
     * Category 1: 75% and above
     * Category 2: 65% to 74.99%
     * Category 3: 40% to 64.99%
     * Category 4: Below 40%
     */
    public static String calculateCategory(double percentage) {
        if (percentage >= 75.0) return "75%+";
        if (percentage >= 65.0) return "65–74.99%";
        if (percentage >= 40.0) return "40–64.99%";
        return "Below 40%";
    }

    /**
     * Aggregates overall batch statistics.
     */
    public AnalysisResult performOverallAnalysis(List<StudentResult> results) {
        AnalysisResult analysis = new AnalysisResult();
        if (results == null || results.isEmpty()) {
            return analysis;
        }

        int total = results.size();
        int passed = 0;
        int failed = 0;
        int cat75 = 0;
        int cat65 = 0;
        int cat40 = 0;
        int catBelow40 = 0;

        double sumPercentage = 0.0;
        double maxPct = Double.MIN_VALUE;
        double minPct = Double.MAX_VALUE;

        StudentResult topStudent = null;
        StudentResult lowestStudent = null;

        for (StudentResult sr : results) {
            double pct = sr.getPercentage();
            sumPercentage += pct;

            if ("PASS".equalsIgnoreCase(sr.getResult())) {
                passed++;
            } else {
                failed++;
            }

            // Categories
            if (pct >= 75.0) {
                cat75++;
            } else if (pct >= 65.0) {
                cat65++;
            } else if (pct >= 40.0) {
                cat40++;
            } else {
                catBelow40++;
            }

            if (pct > maxPct) {
                maxPct = pct;
                topStudent = sr;
            }
            if (pct < minPct) {
                minPct = pct;
                lowestStudent = sr;
            }
        }

        analysis.setTotalStudents(total);
        analysis.setPassedStudents(passed);
        analysis.setFailedStudents(failed);
        analysis.setPassPercentage(roundToTwoDecimals((double) passed / total * 100.0));
        analysis.setFailPercentage(roundToTwoDecimals((double) failed / total * 100.0));

        analysis.setCategory75Plus(cat75);
        analysis.setCategory65To74(cat65);
        analysis.setCategory40To64(cat40);
        analysis.setCategoryBelow40(catBelow40);

        analysis.setOverallAverage(roundToTwoDecimals(sumPercentage / total));
        analysis.setHighestPercentage(maxPct != Double.MIN_VALUE ? maxPct : 0.0);
        analysis.setLowestPercentage(minPct != Double.MAX_VALUE ? minPct : 0.0);

        if (topStudent != null) {
            analysis.setTopPerformerName(topStudent.getName());
            analysis.setTopPerformerRoll(topStudent.getRollNo());
            analysis.setTopPerformerPercentage(topStudent.getPercentage());
        }
        if (lowestStudent != null) {
            analysis.setLowestPerformerName(lowestStudent.getName());
            analysis.setLowestPerformerRoll(lowestStudent.getRollNo());
            analysis.setLowestPerformerPercentage(lowestStudent.getPercentage());
        }

        return analysis;
    }

    /**
     * Performs subject-wise statistical analysis for all 4 primary subjects:
     * Java, Digital Electronics (DE), Data Structures & Algorithms (DSA), Operating System (OS).
     */
    public Map<String, SubjectAnalysis> performSubjectAnalysis(List<StudentResult> results) {
        Map<String, SubjectAnalysis> map = new LinkedHashMap<>();
        String[] subjects = {"Java", "Digital Electronics", "Data Structures & Algorithms", "Operating System"};

        for (String sub : subjects) {
            map.put(sub, computeSingleSubjectAnalysis(sub, results));
        }
        return map;
    }

    private SubjectAnalysis computeSingleSubjectAnalysis(String subjectName, List<StudentResult> results) {
        SubjectAnalysis sa = new SubjectAnalysis();
        sa.setSubjectName(subjectName);

        if (results == null || results.isEmpty()) {
            return sa;
        }

        int total = results.size();
        int passed = 0;
        int failed = 0;
        double sum = 0.0;
        double maxMark = Double.MIN_VALUE;
        double minMark = Double.MAX_VALUE;

        for (StudentResult sr : results) {
            double mark;
            switch (subjectName) {
                case "Java":
                    mark = sr.getJavaMarks();
                    break;
                case "Digital Electronics":
                    mark = sr.getDeMarks();
                    break;
                case "Data Structures & Algorithms":
                    mark = sr.getDsaMarks();
                    break;
                case "Operating System":
                    mark = sr.getOsMarks();
                    break;
                default:
                    mark = 0.0;
            }

            sum += mark;
            if (mark >= PASS_MARK_THRESHOLD) {
                passed++;
            } else {
                failed++;
            }

            if (mark > maxMark) maxMark = mark;
            if (mark < minMark) minMark = mark;
        }

        sa.setAverageMarks(roundToTwoDecimals(sum / total));
        sa.setHighestMarks(maxMark != Double.MIN_VALUE ? maxMark : 0.0);
        sa.setLowestMarks(minMark != Double.MAX_VALUE ? minMark : 0.0);
        sa.setPassedStudents(passed);
        sa.setFailedStudents(failed);
        sa.setPassPercentage(roundToTwoDecimals((double) passed / total * 100.0));

        return sa;
    }

    public List<StudentResult> getAllResults() {
        return marksDAO.getAllStudentResults();
    }

    public StudentResult getStudentResultByRollNo(String rollNo) {
        return marksDAO.getStudentResultByRollNo(rollNo);
    }

    public List<StudentResult> searchAndFilter(String query, String filter) {
        return marksDAO.searchAndFilter(query, filter);
    }

    public static double roundToTwoDecimals(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public static String formatMarks(double value) {
        if (value == (long) value) {
            return String.format("%d", (long) value);
        } else {
            return String.format("%.2f", value);
        }
    }
}
