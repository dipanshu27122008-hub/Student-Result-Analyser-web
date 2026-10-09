-- ====================================================================
-- STUDENT RESULT ANALYSIS SYSTEM — DATABASE INITIALIZATION SCRIPT
-- Compatible with MySQL 8.x / MariaDB 10.x / XAMPP / WampServer / Cloud
-- ====================================================================
-- HOW TO EXECUTE THIS SCRIPT:
-- 1. Using MySQL Command Line:
--    mysql -u root -p < student_result.sql
-- 2. Using MySQL Workbench / DBeaver / phpMyAdmin:
--    Open this file and click "Execute" (Lightning bolt icon).
-- ====================================================================

CREATE DATABASE IF NOT EXISTS student_result_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE student_result_db;

-- --------------------------------------------------------------------
-- 1. USERS TABLE (Authentication & Role Management)
-- --------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'ADMIN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed Default Administrator (Credentials: admin / admin123)
INSERT INTO users (username, password, role)
SELECT 'admin', 'admin123', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

-- --------------------------------------------------------------------
-- 2. STUDENTS TABLE (Master Registry)
-- --------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    roll_no VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_roll_no (roll_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------------------
-- 3. MARKS TABLE (Academic Subject Scores out of 100)
-- --------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS marks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    java_marks DECIMAL(5,2) NOT NULL,
    de_marks DECIMAL(5,2) NOT NULL,
    dsa_marks DECIMAL(5,2) NOT NULL,
    os_marks DECIMAL(5,2) NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    INDEX idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------------------
-- 4. IMPORT HISTORY TABLE (Audit Trail for File Uploads)
-- --------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS import_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(20) NOT NULL,
    total_records INT NOT NULL,
    successful_records INT NOT NULL,
    rejected_records INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    imported_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------------------
-- 5. COLLEGE SETTINGS TABLE (Institutional Branding for Reports)
-- --------------------------------------------------------------------
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert Institutional Settings Defaults
INSERT INTO college_settings (id, college_name, department_name, course_name, semester, academic_year, project_title, logo_path)
VALUES (1, 'NATIONAL INSTITUTE OF TECHNOLOGY', 'Computer Science & Engineering', 'BCA', 'Semester IV', '2026-27', 'Student Result Analysis System', 'images/college-logo.png')
ON DUPLICATE KEY UPDATE 
    college_name=VALUES(college_name),
    department_name=VALUES(department_name);

-- --------------------------------------------------------------------
-- 6. DEMO BENCHMARK DATASET (35 Realistic Student Records)
-- Contains: Toppers, 75%+, 65-74%, 40-64%, Below 40%, & Single-Subject Fails
-- --------------------------------------------------------------------

INSERT INTO students (id, roll_no, name) VALUES
(1, '101', 'Rahul Kumar'),
(2, '102', 'Priya Sharma'),
(3, '103', 'Amit Patel'),
(4, '104', 'Sneha Verma'),
(5, '105', 'Vikram Malhotra'),
(6, '106', 'Ananya Roy'),
(7, '107', 'Rajesh Gupta'),
(8, '108', 'Pooja Nair'),
(9, '109', 'Rohan Joshi'),
(10, '110', 'Meera Iyer'),
(11, '111', 'Arjun Reddy'),
(12, '112', 'Divya Pillai'),
(13, '113', 'Karan Kapoor'),
(14, '114', 'Ritu Deshmukh'),
(15, '115', 'Sandeep Chawla'),
(16, '116', 'Neha Saxena'),
(17, '117', 'Aditya Mehta'),
(18, '118', 'Swati Kulkarni'),
(19, '119', 'Manish Pandey'),
(20, '120', 'Kavita Bhat'),
(21, '121', 'Suresh Menon'),
(22, '122', 'Sunita Sen'),
(23, '123', 'Deepak Yadav'),
(24, '124', 'Pallavi Chatterjee'),
(25, '125', 'Harish Nambiar'),
(26, '126', 'Tanvi Hegde'),
(27, '127', 'Gaurav Bansal'),
(28, '128', 'Shreya Ghoshal'),
(29, '129', 'Alok Tiwari'),
(30, '130', 'Bhavna Agarwal'),
(31, '131', 'Nitin Gadkari'),
(32, '132', 'Varun Dhawan'),
(33, '133', 'Ishita Das'),
(34, '134', 'Sanjay Singhania'),
(35, '135', 'Madhuri Dixit')
ON DUPLICATE KEY UPDATE name=VALUES(name);

INSERT INTO marks (student_id, java_marks, de_marks, dsa_marks, os_marks) VALUES
(1, 85, 78, 88, 92),
(2, 95, 92, 94, 98),
(3, 68, 72, 70, 74),
(4, 75, 75, 75, 75),
(5, 65, 65, 65, 65),
(6, 40, 40, 40, 40),
(7, 92, 35, 90, 88),
(8, 88, 90, 38, 86),
(9, 52, 58, 60, 54),
(10, 62, 64, 60, 68),
(11, 78, 82, 80, 84),
(12, 45, 48, 50, 42),
(13, 32, 28, 35, 30),
(14, 80, 85, 82, 88),
(15, 58, 62, 64, 60),
(16, 70, 72, 68, 74),
(17, 90, 88, 86, 92),
(18, 42, 44, 46, 40),
(19, 38, 50, 55, 60),
(20, 74, 76, 72, 70),
(21, 66, 68, 70, 65),
(22, 84, 86, 80, 82),
(23, 25, 30, 28, 32),
(24, 96, 94, 98, 96),
(25, 50, 52, 48, 55),
(26, 69, 71, 73, 68),
(27, 88, 82, 85, 90),
(28, 72, 70, 74, 68),
(29, 41, 43, 45, 42),
(30, 85, 88, 92, 86),
(31, 100, 100, 100, 100),
(32, 0, 0, 0, 0),
(33, 82, 80, 84, 39),
(34, 60, 62, 58, 64),
(35, 77, 75, 79, 81)
ON DUPLICATE KEY UPDATE java_marks=VALUES(java_marks);

-- Initial Audit Record
INSERT INTO import_history (file_name, file_type, total_records, successful_records, rejected_records, status)
VALUES ('benchmark_dataset.sql', 'SQL/Demo', 35, 35, 0, 'Success');
