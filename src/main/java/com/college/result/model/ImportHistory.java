package com.college.result.model;

import java.sql.Timestamp;

/**
 * Model representing persistent import log history record.
 */
public class ImportHistory {
    private int id;
    private String fileName;
    private String fileType;
    private int totalRecords;
    private int successfulRecords;
    private int rejectedRecords;
    private String status;
    private Timestamp importedAt;

    public ImportHistory() {}

    public ImportHistory(String fileName, String fileType, int totalRecords, int successfulRecords,
                         int rejectedRecords, String status) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.totalRecords = totalRecords;
        this.successfulRecords = successfulRecords;
        this.rejectedRecords = rejectedRecords;
        this.status = status;
    }

    public ImportHistory(int id, String fileName, String fileType, int totalRecords, int successfulRecords,
                         int rejectedRecords, String status, Timestamp importedAt) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.totalRecords = totalRecords;
        this.successfulRecords = successfulRecords;
        this.rejectedRecords = rejectedRecords;
        this.status = status;
        this.importedAt = importedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getSuccessfulRecords() {
        return successfulRecords;
    }

    public void setSuccessfulRecords(int successfulRecords) {
        this.successfulRecords = successfulRecords;
    }

    public int getRejectedRecords() {
        return rejectedRecords;
    }

    public void setRejectedRecords(int rejectedRecords) {
        this.rejectedRecords = rejectedRecords;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getImportedAt() {
        return importedAt;
    }

    public void setImportedAt(Timestamp importedAt) {
        this.importedAt = importedAt;
    }
}
