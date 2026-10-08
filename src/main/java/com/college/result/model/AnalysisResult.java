package com.college.result.model;

/**
 * Model representing aggregated analysis of results across all students.
 */
public class AnalysisResult {
    private int totalStudents;
    private int passedStudents;
    private int failedStudents;
    private double passPercentage;
    private double failPercentage;

    // Mutually exclusive percentage categories
    private int category75Plus;     // 75% and above
    private int category65To74;     // 65% to 74.99%
    private int category40To64;     // 40% to 64.99%
    private int categoryBelow40;    // Below 40%

    // Overall performance metrics
    private double overallAverage;
    private double highestPercentage;
    private double lowestPercentage;

    // Top and lowest performers
    private String topPerformerName;
    private String topPerformerRoll;
    private double topPerformerPercentage;

    private String lowestPerformerName;
    private String lowestPerformerRoll;
    private double lowestPerformerPercentage;

    public AnalysisResult() {}

    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
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

    public double getFailPercentage() {
        return failPercentage;
    }

    public void setFailPercentage(double failPercentage) {
        this.failPercentage = failPercentage;
    }

    public int getCategory75Plus() {
        return category75Plus;
    }

    public void setCategory75Plus(int category75Plus) {
        this.category75Plus = category75Plus;
    }

    public int getCategory65To74() {
        return category65To74;
    }

    public void setCategory65To74(int category65To74) {
        this.category65To74 = category65To74;
    }

    public int getCategory40To64() {
        return category40To64;
    }

    public void setCategory40To64(int category40To64) {
        this.category40To64 = category40To64;
    }

    public int getCategoryBelow40() {
        return categoryBelow40;
    }

    public void setCategoryBelow40(int categoryBelow40) {
        this.categoryBelow40 = categoryBelow40;
    }

    public double getOverallAverage() {
        return overallAverage;
    }

    public void setOverallAverage(double overallAverage) {
        this.overallAverage = overallAverage;
    }

    public double getHighestPercentage() {
        return highestPercentage;
    }

    public void setHighestPercentage(double highestPercentage) {
        this.highestPercentage = highestPercentage;
    }

    public double getLowestPercentage() {
        return lowestPercentage;
    }

    public void setLowestPercentage(double lowestPercentage) {
        this.lowestPercentage = lowestPercentage;
    }

    public String getTopPerformerName() {
        return topPerformerName;
    }

    public void setTopPerformerName(String topPerformerName) {
        this.topPerformerName = topPerformerName;
    }

    public String getTopPerformerRoll() {
        return topPerformerRoll;
    }

    public void setTopPerformerRoll(String topPerformerRoll) {
        this.topPerformerRoll = topPerformerRoll;
    }

    public double getTopPerformerPercentage() {
        return topPerformerPercentage;
    }

    public void setTopPerformerPercentage(double topPerformerPercentage) {
        this.topPerformerPercentage = topPerformerPercentage;
    }

    public String getLowestPerformerName() {
        return lowestPerformerName;
    }

    public void setLowestPerformerName(String lowestPerformerName) {
        this.lowestPerformerName = lowestPerformerName;
    }

    public String getLowestPerformerRoll() {
        return lowestPerformerRoll;
    }

    public void setLowestPerformerRoll(String lowestPerformerRoll) {
        this.lowestPerformerRoll = lowestPerformerRoll;
    }

    public double getLowestPerformerPercentage() {
        return lowestPerformerPercentage;
    }

    public void setLowestPerformerPercentage(double lowestPerformerPercentage) {
        this.lowestPerformerPercentage = lowestPerformerPercentage;
    }
}
