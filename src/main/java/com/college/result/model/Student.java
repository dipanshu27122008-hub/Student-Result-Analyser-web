package com.college.result.model;

import java.sql.Timestamp;

/**
 * Model representing a Student entity.
 */
public class Student {
    private int id;
    private String rollNo;
    private String name;
    private Timestamp createdAt;

    public Student() {}

    public Student(String rollNo, String name) {
        this.rollNo = rollNo;
        this.name = name;
    }

    public Student(int id, String rollNo, String name, Timestamp createdAt) {
        this.id = id;
        this.rollNo = rollNo;
        this.name = name;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
