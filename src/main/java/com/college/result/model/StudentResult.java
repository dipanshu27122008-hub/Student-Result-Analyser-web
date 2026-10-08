package com.college.result.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model representing calculated results for a student.
 */
public class StudentResult {
    private int studentId;
    private String rollNo;
    private String name;
    private double javaMarks;
    private double deMarks;
    private double dsaMarks;
    private double osMarks;

    private double totalMarks;
    private double maxMarks = 400.0;
    private double percentage;
    private String grade;
    private String result; // "PASS" or "FAIL"
    private String category; // "75%+", "65-74.99%", "40-64.99%", "Below 40%"

    private List<String> failedSubjects = new ArrayList<>();
    private String highestSubject;
    private double highestSubjectMarks;
    private String lowestSubject;
    private double lowestSubjectMarks;
    private double averageSubjectMarks;

    public StudentResult() {}

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getJavaMarks() {
        return javaMarks;
    }

    public void setJavaMarks(double javaMarks) {
        this.javaMarks = javaMarks;
    }

    public double getDeMarks() {
        return deMarks;
    }

    public void setDeMarks(double deMarks) {
        this.deMarks = deMarks;
    }

    public double getDsaMarks() {
        return dsaMarks;
    }

    public void setDsaMarks(double dsaMarks) {
        this.dsaMarks = dsaMarks;
    }

    public double getOsMarks() {
        return osMarks;
    }

    public void setOsMarks(double osMarks) {
        this.osMarks = osMarks;
    }

    public double getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(double totalMarks) {
        this.totalMarks = totalMarks;
    }

    public double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(double maxMarks) {
        this.maxMarks = maxMarks;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getFailedSubjects() {
        return failedSubjects;
    }

    public void setFailedSubjects(List<String> failedSubjects) {
        this.failedSubjects = failedSubjects;
    }

    public String getHighestSubject() {
        return highestSubject;
    }

    public void setHighestSubject(String highestSubject) {
        this.highestSubject = highestSubject;
    }

    public double getHighestSubjectMarks() {
        return highestSubjectMarks;
    }

    public void setHighestSubjectMarks(double highestSubjectMarks) {
        this.highestSubjectMarks = highestSubjectMarks;
    }

    public String getLowestSubject() {
        return lowestSubject;
    }

    public void setLowestSubject(String lowestSubject) {
        this.lowestSubject = lowestSubject;
    }

    public double getLowestSubjectMarks() {
        return lowestSubjectMarks;
    }

    public void setLowestSubjectMarks(double lowestSubjectMarks) {
        this.lowestSubjectMarks = lowestSubjectMarks;
    }

    public double getAverageSubjectMarks() {
        return averageSubjectMarks;
    }

    public void setAverageSubjectMarks(double averageSubjectMarks) {
        this.averageSubjectMarks = averageSubjectMarks;
    }
}
