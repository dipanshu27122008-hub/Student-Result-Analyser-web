-- ========================================================
-- Database Schema for Student Result Analysis System
-- Compatible with MySQL 8.x / MariaDB
-- ========================================================

CREATE DATABASE IF NOT EXISTS student_result_db;
USE student_result_db;

-- 1. Users Table (Authentication)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'ADMIN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert Default Admin (Password: admin123)
-- Stored as plain text for clear demonstration / viva explanation,
-- with support for hashed verification in production
INSERT INTO users (username, password, role)
SELECT 'admin', 'admin123', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

-- 2. Students Table
CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    roll_no VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_roll_no (roll_no)
);

-- 3. Marks Table
CREATE TABLE IF NOT EXISTS marks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    java_marks DECIMAL(5,2) NOT NULL,
    de_marks DECIMAL(5,2) NOT NULL,
    dsa_marks DECIMAL(5,2) NOT NULL,
    os_marks DECIMAL(5,2) NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    INDEX idx_student_id (student_id)
);

-- 4. Import History Table
CREATE TABLE IF NOT EXISTS import_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(20) NOT NULL,
    total_records INT NOT NULL,
    successful_records INT NOT NULL,
    rejected_records INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    imported_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. College Settings Table
CREATE TABLE IF NOT EXISTS college_settings (
    id INT PRIMARY KEY,
    college_name VARCHAR(255) NOT NULL,
    department_name VARCHAR(255) NOT NULL,
    course_name VARCHAR(100) NOT NULL,
    semester VARCHAR(50) NOT NULL,
    academic_year VARCHAR(50) NOT NULL,
    project_title VARCHAR(255) NOT NULL,
    logo_path VARCHAR(255) DEFAULT 'images/college-logo.png',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert Default Settings
INSERT INTO college_settings (id, college_name, department_name, course_name, semester, academic_year, project_title, logo_path)
VALUES (1, 'YOUR COLLEGE NAME', 'YOUR DEPARTMENT', 'BCA', 'YOUR SEMESTER', '2026-27', 'Student Result Analysis System', 'images/college-logo.png')
ON DUPLICATE KEY UPDATE id=1;
