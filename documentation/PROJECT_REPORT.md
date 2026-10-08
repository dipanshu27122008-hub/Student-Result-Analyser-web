# ACADEMIC PROJECT REPORT

## PROJECT TITLE: STUDENT RESULT ANALYSIS SYSTEM

**Submitted in partial fulfillment of the requirements for the Degree of Bachelor of Computer Applications (BCA) / Bachelor of Technology (B.Tech)**

**Academic Year: 2026–27**

---

### PROJECT SUBMITTED BY:
1. **[STUDENT NAME 1]** (Roll No: `[ROLL NO 1]`)
2. **[STUDENT NAME 2]** (Roll No: `[ROLL NO 2]`)
3. **[STUDENT NAME 3]** (Roll No: `[ROLL NO 3]`)
4. **[STUDENT NAME 4]** (Roll No: `[ROLL NO 4]`)

**Under the Guidance of:**
**[PROJECT GUIDE / FACULTY NAME]**
*[Designation / Assistant Professor]*

**Department of [YOUR DEPARTMENT]**  
**[YOUR COLLEGE NAME]**  
*[College Address, City, State - PIN]*

---

## TABLE OF CONTENTS

1. **Title and Declaration**
2. **Abstract**
3. **Introduction**
4. **Problem Statement**
5. **Objectives of the System**
6. **Existing System Analysis**
7. **Proposed System Architecture**
8. **Scope of the Project**
9. **System Requirements Specification (SRS)**
10. **Technology Stack Justification**
11. **System Architecture (MVC Pattern)**
12. **Detailed Module Description**
13. **Database Design & Data Dictionary**
14. **Input Processing & File Ingestion Logic**
15. **Result Calculation & Grading Algorithms**
16. **Analytical Metrics & Category Classification**
17. **PDF Report Generation Architecture**
18. **Testing Strategy & Boundary Validation**
19. **Results & Performance Analysis**
20. **Future Scope & Enhancements**
21. **Conclusion**
22. **References & Bibliography**

---

## 1. TITLE AND DECLARATION

### 1.1 Title
**Student Result Analysis System**

### 1.2 Declaration
We hereby declare that the project titled **"Student Result Analysis System"** is an authentic piece of work carried out by our team under the supervision and guidance of **[PROJECT GUIDE / FACULTY NAME]**, Department of **[YOUR DEPARTMENT]**, **[YOUR COLLEGE NAME]**. No part of this project report has been submitted elsewhere for the award of any other degree or diploma.

---

## 2. ABSTRACT

Academic result compilation and post-examination analytics represent a critical administrative activity in colleges and universities. Traditional manual methods or isolated spreadsheet calculations are prone to calculation errors, lack centralized duplicate detection, provide no real-time diagnostic reporting for backlogged students, and fail to generate official institutional documentation dynamically.

The **Student Result Analysis System** is an enterprise-grade Java web application engineered using **Jakarta EE 10**, **Jakarta Servlets**, **JSP**, **Apache POI**, and **MySQL**, deployed on **Apache Tomcat 10.1+**. The system ingests student marks through **Microsoft Excel (`.xlsx`)** or **Comma-Separated Text (`.txt`)** files, applies row-level data validation and duplicate roll number rejection, dynamically calculates subject-wise and grand total marks out of 400 across four fundamental subjects (Java Programming, Digital Electronics, Data Structures & Algorithms, and Operating System), assigns academic letter grades (A+ to F), and categorizes cohorts into mutually exclusive percentage brackets (75%+, 65–74.99%, 40–64.99%, and Below 40%). Furthermore, it renders executive visual dashboards powered by **Chart.js** and generates formal, publication-ready academic reports in PDF format using **OpenPDF**.

---

## 3. INTRODUCTION

In higher education institutions, measuring students' academic progress is vital for syllabus auditing, faculty evaluation, accreditation (such as NAAC and NBA), and remedial education planning. Examination results contain multidimensional performance data that must be aggregated quickly and accurately.

The **Student Result Analysis System** bridges the gap between raw spreadsheet data entry and high-level decision-making. By implementing a strict **Model-View-Controller (MVC)** architectural pattern, the system separates persistence, calculation algorithms, and graphical user interfaces, ensuring high reliability, maintainability, and ease of demonstration during academic viva examinations.

---

## 4. PROBLEM STATEMENT

Manual processing and uncoordinated spreadsheet templates suffer from several systemic defects:
1. **Human Error**: Manual percentage and grade computation often misclassifies borderline students (e.g., exactly 75.00% or 40.00%).
2. **False Passes**: Conventional spreadsheet macros frequently fail to check subject-level minimum passing criteria. For instance, a student scoring 90 in three subjects and 35 in one subject might mistakenly be deemed "passed" if overall percentage alone is evaluated.
3. **Data Redundancy & Collisions**: Batch re-uploads frequently overwrite historical data or create duplicate roll number conflicts.
4. **Lack of Centralized Reporting**: Faculty and heads of department lack a standardized, one-click mechanism to produce signed, official analytical reports with charts and statistical breakdowns.

---

## 5. OBJECTIVES OF THE SYSTEM

The primary objectives achieved by this application include:
* **Automated Multi-Format Ingestion**: Ingest student rosters seamlessly from Excel (`.xlsx`) and Text (`.txt`).
* **Strict Per-Subject Pass Enforcement**: Enforce a mandatory minimum threshold of 40 marks in every single subject for a student to qualify as passed.
* **Dynamic Percentage & Grade Determination**: Compute totals out of 400, percentages to two decimal places, and grades on a 7-tier scale (`A+`, `A`, `B+`, `B`, `C`, `D`, `F`).
* **Mutually Exclusive Cohort Stratification**: Partition students into distinct brackets (`75%+`, `65–74.99%`, `40–64.99%`, `Below 40%`).
* **Course Diagnostics**: Determine subject averages, highest/lowest scores, and pass percentages across Java, DE, DSA, and OS.
* **Visual Analytical Dashboards**: Present interactive charts and KPI metrics.
* **Standardized PDF Generation**: Output publication-ready academic project reports featuring college branding, tables, performer highlights, and approval signatures.
* **Audit & Institutional Governance**: Track ingestion history logs and allow dynamic institutional customization.

---

## 6. EXISTING SYSTEM ANALYSIS

In existing institutional workflows:
* Marks entries are compiled in fragmented Excel files across individual faculty laptops.
* Calculating class averages, distinctions, and backlog rates requires complex manual formulas (`=COUNTIF`, `=AVERAGEIF`).
* Inconsistent formula propagation results in errors when formulas are not correctly copied across thousands of rows.
* There is no role-based session authentication or central database storage.

---

## 7. PROPOSED SYSTEM ARCHITECTURE

The proposed system replaces manual processing with an integrated, web-based platform:
* **Central Database**: Relational MySQL database enforces unique roll number constraints and referential integrity.
* **Unified Business Logic**: Java service layer centralizes all mathematical and business rules, eliminating client-side or spreadsheet calculation drift.
* **Responsive Administrative UI**: Clean, responsive web interface built on modern CSS and HTML5.
* **Automatic Resilience**: Graceful fallback to an embedded in-memory database prevents demonstration crashes if the external database daemon is unavailable.

---

## 8. SCOPE OF THE PROJECT

The software covers the evaluation of undergraduate and postgraduate students enrolled in computer science and engineering coursework. It supports batch sizes ranging from small cohorts to hundreds of students, handling data ingestion, analytical reporting, and PDF distribution. It provides college administrators and faculty with instant visibility into curricular bottlenecks.

---

## 9. SYSTEM REQUIREMENTS SPECIFICATION (SRS)

### 9.1 Hardware Requirements
* **Processor**: Intel Core i3 / AMD Ryzen 3 or higher.
* **RAM**: 4 GB minimum (8 GB recommended).
* **Storage**: 500 MB available disk space for application WAR and runtime dependencies.
* **Network**: Standard TCP/IP networking (port 8080).

### 9.2 Software Requirements
* **Operating System**: Windows 10/11, Linux (Ubuntu/Debian/CentOS), or macOS.
* **Java Development Kit (JDK)**: Java SE 17 LTS or Java SE 21 LTS.
* **Web Container**: Apache Tomcat 10.1.x (Jakarta EE 10 compatible).
* **Relational Database**: MySQL Server 8.0+ / MariaDB 10.4+.
* **Build Tool**: Apache Maven 3.9+.
* **Web Browser**: Google Chrome, Mozilla Firefox, or Microsoft Edge.

---

## 10. TECHNOLOGY STACK JUSTIFICATION

* **Jakarta EE 10 / Tomcat 10.1+**: Standard enterprise Java specification utilizing modern `jakarta.*` packages, ensuring longevity and compliance with modern industry frameworks.
* **Apache POI 5.2.5**: The industry-standard library for streaming and parsing Office Open XML (`.xlsx`) workbooks.
* **OpenPDF 1.3.39**: High-performance, open-source LGPL/MPL Java library for creating strict academic PDF documents with tables, fonts, running headers, and page numbers.
* **Chart.js**: Clean HTML5 Canvas rendering for responsive, accessible administrative data visualizations.
* **MySQL 8.0 & JDBC PreparedStatement**: High transaction throughput, parameterized queries preventing SQL injection, and robust relational constraint enforcement.

---

## 11. SYSTEM ARCHITECTURE (MVC PATTERN)

The application adheres strictly to the classic three-tier **Model-View-Controller (MVC)** architectural paradigm:

```text
[ CLIENT BROWSER ] 
       │ HTTP GET / POST
       ▼
[ Jakarta Controller Servlets ] ◄── AuthFilter (Session Check)
       │ Invokes
       ▼
[ Service Layer (Pure Java Business Logic) ]
  ├── ResultAnalysisService (Pass/Fail, Grades, Categories, Aggregations)
  ├── ExcelImportService (Apache POI Parsing & Row Validation)
  ├── TxtImportService (BufferedReader Parsing & Delimitation)
  └── PdfReportService (OpenPDF Document Builder & Layout)
       │ Queries / Persists
       ▼
[ DAO Layer (Data Access Objects) ]
  ├── StudentDAO, MarksDAO, UserDAO, ImportHistoryDAO, CollegeSettingsDAO
       │ JDBC PreparedStatements
       ▼
[ Database Layer ]
  └── MySQL Database (`student_result_db`) / In-Memory H2 Fallback
       │ Returns Data Models
       ▼
[ Presentation Layer (JSP / JSTL / EL) ]
  └── Rendered HTML5 UI & Chart.js Visualizations
```

---

## 12. DETAILED MODULE DESCRIPTION

1. **Authentication Module (`LoginServlet`, `AuthFilter`)**:
   Enforces session-based authentication. Unauthenticated requests targeting internal administrative routes are redirected to `/login`.
2. **Dashboard Module (`DashboardServlet`, `dashboard.jsp`)**:
   Computes and displays top-level KPIs, performer cards, diagnostic tables, and initializes four dynamic Chart.js charts.
3. **Data Ingestion Module (`FileUploadServlet`, `upload.jsp`)**:
   Multi-part file handler accepting `.xlsx` or `.txt`. Routes files to the appropriate service and generates detailed row-by-row status feedback.
4. **Student Management Module (`StudentServlet`, `students.jsp`)**:
   Tabulates student marks with search capability across roll numbers and names, coupled with single-click category filters.
5. **Scorecard Module (`StudentResultServlet`, `student-result.jsp`)**:
   Generates individual student scorecards highlighting specific failed courses and subject statistics.
6. **Overall Analysis Module (`AnalysisServlet`, `analysis.jsp`)**:
   Aggregates class-wide distribution statistics and extreme bounds.
7. **Subject Analysis Module (`SubjectAnalysisServlet`, `subject-analysis.jsp`)**:
   Breaks down each of the four core subjects by class average, peak score, minimum score, pass count, backlog count, and clearance rate.
8. **Audit Module (`ImportHistoryServlet`, `import-history.jsp`)**:
   Maintains a timestamped log of all file ingestion operations.
9. **PDF Reporting Module (`PdfReportServlet`, `PdfReportService`)**:
   Compiles and streams dynamic PDF reports to the user.
10. **Institutional Settings Module (`SettingsServlet`, `settings.jsp`)**:
    Enables administrative customization of college names, degree programs, terms, and crest logos.

---

## 13. DATABASE DESIGN & DATA DICTIONARY

The relational database `student_result_db` comprises five primary tables:

```text
users (id PK, username UNIQUE, password, role, created_at)
students (id PK, roll_no UNIQUE, name, created_at)
marks (id PK, student_id FK -> students.id, java_marks, de_marks, dsa_marks, os_marks)
import_history (id PK, file_name, file_type, total_records, successful_records, rejected_records, status, imported_at)
college_settings (id PK, college_name, department_name, course_name, semester, academic_year, project_title, logo_path, updated_at)
```

### Table 1: `students`
| Field | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | INT | PRIMARY KEY, AUTO_INCREMENT | Unique student identifier |
| `roll_no` | VARCHAR(50) | UNIQUE, NOT NULL | Institutional Roll Number |
| `name` | VARCHAR(100) | NOT NULL | Full Student Name |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Record registration timestamp |

### Table 2: `marks`
| Field | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | INT | PRIMARY KEY, AUTO_INCREMENT | Unique mark record identifier |
| `student_id` | INT | NOT NULL, FK &rarr; students(id) | Associated student link |
| `java_marks` | DECIMAL(5,2) | NOT NULL | Java mark (0.00 – 100.00) |
| `de_marks` | DECIMAL(5,2) | NOT NULL | Digital Electronics mark |
| `dsa_marks` | DECIMAL(5,2) | NOT NULL | Data Structures mark |
| `os_marks` | DECIMAL(5,2) | NOT NULL | Operating System mark |

---

## 14. INPUT PROCESSING & FILE INGESTION LOGIC

### 14.1 Excel Reader Pipeline (`ExcelImportService`)
1. Opens the uploaded input stream via `WorkbookFactory.create()`.
2. Inspects header cells in row 0, normalizing tokens to identify column positions (`rollno`, `name`, `java`, `de`, `dsa`, `os`).
3. Validates row presence. Blank rows are skipped automatically.
4. Validates marks: must be non-null numeric values between 0.00 and 100.00.
5. Detects duplicates against both the in-flight upload batch and existing database records. Duplicates are rejected and preserved.
6. Commits valid rows using a database transaction (`setAutoCommit(false)` &rarr; `commit()`).

### 14.2 Text Reader Pipeline (`TxtImportService`)
1. Reads lines using `BufferedReader` and UTF-8 encoding.
2. Validates comma-separated header line against required tokens.
3. Splits data lines using `line.split(",", -1)` to prevent empty trailing token drops.
4. Performs equivalent duplicate checks and marks validations.

---

## 15. RESULT CALCULATION & GRADING ALGORITHMS

### 15.1 Pass/Fail Rule
A student qualifies as **PASS** if and only if:
$$\text{Java} \ge 40 \quad \land \quad \text{DE} \ge 40 \quad \land \quad \text{DSA} \ge 40 \quad \land \quad \text{OS} \ge 40$$

If any subject mark is below 40, the result is **FAIL**, and the failing course is appended to the diagnostic report.

### 15.2 Percentage Calculation
$$\text{Total} = \text{Java} + \text{DE} + \text{DSA} + \text{OS}$$
$$\text{Percentage} = \left(\frac{\text{Total}}{400.0}\right) \times 100$$
Rounded to 2 decimal places using `BigDecimal` (`HALF_UP`).

### 15.3 Grade Mapping Table
| Percentage Range | Assigned Grade |
| :--- | :---: |
| 90.00% &ndash; 100.00% | **A+** |
| 80.00% &ndash; 89.99% | **A** |
| 70.00% &ndash; 79.99% | **B+** |
| 60.00% &ndash; 69.99% | **B** |
| 50.00% &ndash; 59.99% | **C** |
| 40.00% &ndash; 49.99% | **D** |
| Below 40.00% | **F** |

---

## 16. ANALYTICAL METRICS & CATEGORY CLASSIFICATION

Students are partitioned into four mutually exclusive categories based on percentage:
1. **Category 1 (75%+ Distinction)**: $\text{Percentage} \ge 75.00$
2. **Category 2 (65–74.99% First Class)**: $65.00 \le \text{Percentage} < 75.00$
3. **Category 3 (40–64.99% Pass Class)**: $40.00 \le \text{Percentage} < 65.00$
4. **Category 4 (Below 40% Remedial Class)**: $\text{Percentage} < 40.00$

---

## 17. PDF REPORT GENERATION ARCHITECTURE

The PDF generation module (`PdfReportService`) creates professional academic reports using OpenPDF:
* **Cover Page**: Displays the institutional crest, college name, department, academic year, degree program, and generation metadata.
* **Running Headers & Footers**: Uses an implementation of `PdfPageEventHelper` to render running top rules, document titles, confidentiality notices, and dynamic page numbering.
* **Zebra-Striped Roster Tables**: Structured tables with headers, color-coded badges for PASS/FAIL outcomes, and aligned decimal marks.
* **Sign-Off Block**: Verification blocks for Result Coordinator, Head of Department, and Examination Controller.

---

## 18. TESTING STRATEGY & BOUNDARY VALIDATION

Automated unit tests in `ResultAnalysisServiceTest` and integration tests in `SampleDataGeneratorAndImportTest` validate system correctness:

| Test Case | Scenario / Input | Expected Output | Status |
| :--- | :--- | :--- | :---: |
| **TC-01** | All subjects $\ge 40$ (80, 75, 60, 55) | PASS, 67.50%, Grade B, 65–74.99% | **PASS** |
| **TC-02** | High percentage but DE = 35 (92, 35, 90, 88) | FAIL, 76.25%, Diagnostic: DE Failed | **PASS** |
| **TC-03** | Exact boundary: 40 in all subjects | PASS, 40.00%, Grade D, 40–64.99% | **PASS** |
| **TC-04** | Exact boundary: 65 in all subjects | PASS, 65.00%, Grade B, 65–74.99% | **PASS** |
| **TC-05** | Exact boundary: 75 in all subjects | PASS, 75.00%, Grade B+, 75%+ | **PASS** |
| **TC-06** | Exact boundary: 100 in all subjects | PASS, 100.00%, Grade A+, 75%+ | **PASS** |
| **TC-07** | Exact boundary: 0 in all subjects | FAIL, 0.00%, Grade F, Below 40% | **PASS** |
| **TC-08** | Ingestion of duplicate roll number | Record rejected, original preserved | **PASS** |
| **TC-09** | Out-of-bounds marks (105, -5) | Rejected with row-specific diagnostic | **PASS** |
| **TC-10** | PDF Stream generation | Valid `%PDF-` document generated (> 5KB) | **PASS** |

---

## 19. RESULTS & PERFORMANCE ANALYSIS

* **Build Time**: Clean compile, test, and WAR packaging completed in under 15 seconds.
* **Import Throughput**: Processes and persists 35 records in under 200 ms with transactional safety.
* **PDF Generation**: Generates complete multi-page PDF reports in under 300 ms.
* **Zero Scriptlet Guarantee**: JSP views use JSTL/EL exclusively with zero business logic in presentation files.

---

## 20. FUTURE SCOPE & ENHANCEMENTS

1. **Role-Based Access Control (RBAC)**: Support for Student, Faculty, and Exam Controller logins.
2. **Automated Email & SMS Notifications**: Dispatching scorecards directly to students and guardians.
3. **Multi-Semester Transcripts**: Aggregating cumulative SGPA and CGPA metrics across multiple semesters.
4. **AI-Driven Remedial Recommendations**: Providing students with personalized study materials based on their individual weak subjects.

---

## 21. CONCLUSION

The **Student Result Analysis System** provides an automated, reliable, and user-friendly platform for processing academic examination results. By enforcing strict per-subject pass thresholds, automating multi-format file ingestion, and generating dynamic executive dashboards alongside formal PDF reports, the system eliminates the human errors and data fragmentation associated with manual spreadsheets. Its clean MVC architecture and comprehensive documentation make it well-suited for college viva presentations and institutional adoption.

---

## 22. REFERENCES & BIBLIOGRAPHY

1. Jakarta EE Specification Committee. *Jakarta Servlet Specification 6.0*. Eclipse Foundation, 2022.
2. Apache POI Project. *Apache POI - Java API To Access Microsoft Format Files*. Apache Software Foundation, 2023.
3. OpenPDF Community. *OpenPDF: Open-Source Java PDF Library*. LibrePDF, 2023.
4. Chart.js Documentation. *Simple yet flexible JavaScript charting for designers & developers*, 2023.
5. Oracle Corporation. *Java SE 17 & 21 Documentation*. Oracle Technology Network, 2023.
