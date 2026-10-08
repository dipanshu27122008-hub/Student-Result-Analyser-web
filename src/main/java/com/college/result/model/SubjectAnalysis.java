package com.college.result.model;

/**
 * Model representing statistical analysis for a single subject.
 */
public class SubjectAnalysis {
    private String subjectName;
    private double averageMarks;
    private double highestMarks;
    private double lowestMarks;
    private int passedStudents;
    private int failedStudents;
    private double passPercentage;

    public SubjectAnalysis() {}

    public SubjectAnalysis(String subjectName, double averageMarks, double highestMarks, double lowestMarks,
                           int passedStudents, int failedStudents, double passPercentage) {
        this.subjectName = subjectName;
        this.averageMarks = averageMarks;
        this.highestMarks = highestMarks;
        this.lowestMarks = lowestMarks;
        this.passedStudents = passedStudents;
        this.failedStudents = failedStudents;
        this.passPercentage = passPercentage;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public double getAverageMarks() {
        return averageMarks;
    }

    public void setAverageMarks(double averageMarks) {
        this.averageMarks = averageMarks;
    }

    public double getHighestMarks() {
        return highestMarks;
    }

    public void setHighestMarks(double highestMarks) {
        this.highestMarks = highestMarks;
    }

    public double getLowestMarks() {
        return lowestMarks;
    }

    public void setLowestMarks(double lowestMarks) {
        this.lowestMarks = lowestMarks;
    }

    public int getPassedStudents() {
        return passedStudents;
    }

    public void setPassedStudents(int passedStudents) {
        this.passedStudents = passedStudents;
    }

    public int getFailedStudents() {
        return failedStudents;
    }

    public void setFailedStudents(int failedStudents) {
        this.failedStudents = failedStudents;
    }

    public double getPassPercentage() {
        return passPercentage;
    }

    public void setPassPercentage(double passPercentage) {
        this.passPercentage = passPercentage;
    }
}
