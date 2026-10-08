package com.college.result.model;

/**
 * Model representing marks scored by a student in the 4 primary subjects.
 */
public class StudentMarks {
    private int id;
    private int studentId;
    private double javaMarks;
    private double deMarks;
    private double dsaMarks;
    private double osMarks;

    public StudentMarks() {}

    public StudentMarks(double javaMarks, double deMarks, double dsaMarks, double osMarks) {
        this.javaMarks = javaMarks;
        this.deMarks = deMarks;
        this.dsaMarks = dsaMarks;
        this.osMarks = osMarks;
    }

    public StudentMarks(int id, int studentId, double javaMarks, double deMarks, double dsaMarks, double osMarks) {
        this.id = id;
        this.studentId = studentId;
        this.javaMarks = javaMarks;
        this.deMarks = deMarks;
        this.dsaMarks = dsaMarks;
        this.osMarks = osMarks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
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
}
