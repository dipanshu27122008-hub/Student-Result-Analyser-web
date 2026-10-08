package com.college.result.model;

import java.sql.Timestamp;

/**
 * Model representing institutional and project settings.
 */
public class CollegeSettings {
    private int id = 1;
    private String collegeName = "YOUR COLLEGE NAME";
    private String departmentName = "YOUR DEPARTMENT";
    private String courseName = "BCA";
    private String semester = "YOUR SEMESTER";
    private String academicYear = "2026-27";
    private String projectTitle = "Student Result Analysis System";
    private String logoPath = "images/college-logo.png";
    private Timestamp updatedAt;

    public CollegeSettings() {}

    public CollegeSettings(int id, String collegeName, String departmentName, String courseName,
                           String semester, String academicYear, String projectTitle, String logoPath) {
        this.id = id;
        this.collegeName = collegeName;
        this.departmentName = departmentName;
        this.courseName = courseName;
        this.semester = semester;
        this.academicYear = academicYear;
        this.projectTitle = projectTitle;
        this.logoPath = logoPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
