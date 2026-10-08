# Student Result Analysis System

A comprehensive, production-grade, college-level Java Web Application designed for academic institutions to ingest, validate, analyze, and report student examination performances from Microsoft Excel (`.xlsx`) or delimited text (`.txt`) files.

---

## 📌 Project Overview

The **Student Result Analysis System** provides automated calculation of academic results across four primary core subjects:
1. **Java Programming**
2. **Digital Electronics (DE)**
3. **Data Structures & Algorithms (DSA)**
4. **Operating System (OS)**

Each subject is evaluated out of **100 marks**, with a maximum grand total of **400 marks**. The application dynamically computes pass/fail status based on strict per-subject passing thresholds, determines academic grades, categorizes students into mutually exclusive percentage brackets, generates interactive statistical charts on an executive dashboard, and produces downloadable, publication-grade academic PDF reports.

---

## 🚀 Key Features

* **Dual-Format Data Ingestion**:
  * Microsoft Excel 2007+ (`.xlsx`) via **Apache POI**.
  * Comma-Separated Text (`.txt`) via Java streaming `BufferedReader`.
* **Rigorous Data Validation**:
  * Validates alphanumeric roll numbers, non-empty student names, and numeric marks between 0 and 100.
  * Rejects duplicate roll numbers automatically while preserving existing records.
  * Comprehensive error reporting detailing the exact row and field failure.
* **Strict Academic Pass/Fail Rule**:
  * A student qualifies as **PASS** *only* when every single subject mark is at least **40**.
  * Even if a student has an overall percentage of 75% or 90%, failing any individual subject (< 40) marks them as **FAIL** with clear diagnostic feedback (e.g., `Digital Electronics - 35/100`).
* **Mutually Exclusive Percentage Brackets**:
  * **Category 1**: 75% and above (Distinction)
  * **Category 2**: 65% to 74.99% (First Class)
  * **Category 3**: 40% to 64.99% (Pass Class)
  * **Category 4**: Below 40% (Fail Class)
* **Standard 7-Tier Grading Scale**:
  * `90.00 – 100.00%`: **A+**
  * `80.00 – 89.99%`: **A**
  * `70.00 – 79.99%`: **B+**
  * `60.00 – 69.99%`: **B**
  * `50.00 – 59.99%`: **C**
  * `40.00 – 49.99%`: **D**
  * `Below 40.00%`: **F**
* **Executive Administrative Dashboard**:
  * Dynamic KPI stat cards (Total, Passed, Failed, Pass %, and Category counts).
  * Highlighting **Top Performer** and **Lowest Performer**.
  * 4 Dynamic **Chart.js** charts:
    1. Pass vs. Fail Doughnut Chart
    2. Percentage Category Distribution Bar Chart
    3. Course Average Marks Bar Chart
    4. Course-wise Pass Percentage Chart
* **Dedicated Analytics Modules**:
  * **Overall Analysis**: Batch population stats, extreme percentage bounds, class averages.
  * **Subject-Wise Analysis**: For each subject: Average, Highest, Lowest, Passed, Failed, and Pass Rate.
* **Individual Scorecard & Diagnostics**:
  * Student lookup by roll number or dropdown roster.
  * Diagnostic breakdown pinpointing subjects causing backlogs.
* **Publication-Quality PDF Reports**:
  * Generated with **OpenPDF**.
  * Complete with college cover page, institutional crest, metadata, KPI summaries, category distributions, subject analysis tables, full student rosters, and signature approval blocks.
* **Audit & Institutional Settings**:
  * Import history audit logs with timestamped success and rejection counts.
  * Configurable college name, department, course, semester, academic year, and custom logo upload.
* **Resilient Dual-Mode Database Architecture**:
  * Connects to MySQL by default via JDBC `PreparedStatement`.
  * Automatic graceful fallback to embedded in-memory H2 if MySQL is offline during viva/demonstration.

---

## 🛠️ Technology Stack

* **Platform**: Java 17 / 21
* **Web Framework**: Jakarta EE 10 (Jakarta Servlet 6.0, Jakarta JSP 3.1, JSTL 3.0)
* **Application Server**: Apache Tomcat 10.1+
* **Build System**: Apache Maven 3.9+
* **Database**: MySQL 8.0+ / MariaDB (with embedded H2 compatibility fallback)
* **Excel Processing**: Apache POI 5.2.5 (`poi`, `poi-ooxml`)
* **PDF Engine**: OpenPDF 1.3.39
* **Frontend**: Semantic HTML5, Custom CSS3, Responsive Layout, Chart.js

---

## 🏛️ MVC Architecture

```text
[ JSP Presentation Layer ]
  (dashboard.jsp, upload.jsp, students.jsp, student-result.jsp, analysis.jsp, settings.jsp)
          │  ▲
          ▼  │
[ Jakarta Servlets / Controllers ]
  (LoginServlet, FileUploadServlet, StudentServlet, DashboardServlet, PdfReportServlet)
          │  ▲
          ▼  │
[ Business Service Layer ]
  (ResultAnalysisService, ExcelImportService, TxtImportService, PdfReportService, AuthenticationService)
          │  ▲
          ▼  │
[ DAO (Data Access Object) Layer ]
  (StudentDAO, MarksDAO, UserDAO, ImportHistoryDAO, CollegeSettingsDAO)
          │  ▲
          ▼  │
[ Database / JDBC Layer ]
  (DBConnectionUtil -> MySQL student_result_db / Embedded H2 fallback)
```

---

## 📂 Project Directory Structure

```text
StudentResultAnalysis/
│
├── pom.xml                               # Maven project descriptor
├── README.md                             # Comprehensive technical guide
├── mvnw.cmd                              # Maven wrapper execution script
│
├── database/
│   └── student_result.sql                # Complete MySQL DDL & DML schema script
│
├── sample-data/
│   ├── students.xlsx                     # Realistic Excel test dataset (35 students)
│   └── students.txt                      # Comma-separated test dataset (35 students)
│
├── documentation/
│   ├── PROJECT_REPORT.md                 # 22-section academic project report
│   ├── PPT_CONTENT.md                    # 12-slide structured presentation guide
│   └── TEAM_CONTRIBUTION.md              # 4-member project division & viva roles
│
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/college/result/
    │   │       ├── model/               # Model entities (Student, StudentMarks, StudentResult, etc.)
    │   │       ├── dao/                 # JDBC Data Access Objects with PreparedStatements
    │   │       ├── service/             # Pure Java business logic, calculations, POI, OpenPDF
    │   │       ├── servlet/             # Jakarta HTTP Controllers and AuthFilter
    │   │       └── util/                # DBConnectionUtil with resilient fallback
    │   │
    │   ├── resources/
    │   │   └── db.properties            # Centralized database configuration
    │   │
    │   └── webapp/
    │       ├── index.jsp                # Entry point router
    │       ├── login.jsp                # Professional administrative login
    │       ├── dashboard.jsp            # KPI cards & Chart.js visualizations
    │       ├── upload.jsp               # Dual Excel & TXT file upload
    │       ├── students.jsp             # Searchable, filterable student roster
    │       ├── student-result.jsp       # Individual scorecard with diagnostic fail notice
    │       ├── analysis.jsp             # Dedicated overall batch performance metrics
    │       ├── subject-analysis.jsp     # Course-wise averages, bounds, and pass rates
    │       ├── import-history.jsp       # File ingestion audit history log
    │       ├── settings.jsp             # College branding and logo configuration
    │       ├── error.jsp                # Friendly error page
    │       ├── css/style.css            # Academic navy/slate custom stylesheet
    │       ├── js/script.js             # Chart.js initialization & UI interactions
    │       ├── images/college-logo.png  # Academic seal placeholder logo
    │       └── WEB-INF/
    │           ├── web.xml              # Jakarta EE 10 deployment descriptor
    │           └── includes/            # Reusable sidebar.jsp and topbar.jsp
    │
    └── test/
        └── java/com/college/result/service/
            ├── ResultAnalysisServiceTest.java          # Exhaustive boundary & grading unit tests
            └── SampleDataGeneratorAndImportTest.java   # Integration test for POI, TXT, & OpenPDF
```

---

## 🗄️ Database Setup (MySQL)

### 1. Execute SQL Script
Open MySQL Workbench, phpMyAdmin, or MySQL CLI and run:

```sql
source database/student_result.sql;
```

This creates the database `student_result_db` and tables:
* `users`: Stores administrator credentials.
* `students`: Stores student roll numbers and names.
* `marks`: Stores marks for Java, DE, DSA, and OS (foreign key to `students`).
* `import_history`: Stores audit records of all ingested files.
* `college_settings`: Stores institution title, department, course, and logo path.

### 2. Configure Credentials
Edit `src/main/resources/db.properties`:

```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/student_result_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
db.username=root
db.password=root
db.fallback.h2=true
```

> **Viva / Offline Demonstration Note**: If MySQL is not running on the demonstration computer, `db.fallback.h2=true` automatically launches an in-memory embedded database with full MySQL compatibility. The application will never crash due to a missing MySQL daemon.

---

## 💻 How to Run the Project

### Option A: Running in Eclipse IDE (Recommended for College Viva)

1. **Import the Project**:
   * Open Eclipse IDE for Enterprise Java and Web Developers.
   * Go to **File** &rarr; **Import...**
   * Select **Maven** &rarr; **Existing Maven Projects** &rarr; click **Next**.
   * Browse to the project root directory (`Marks analyizer`) &rarr; click **Finish**.
2. **Verify JDK and Tomcat Configuration**:
   * Right-click the project in Project Explorer &rarr; **Properties** &rarr; **Java Build Path**.
   * Verify that **JavaSE-17** or **JavaSE-21** is selected.
   * In **Targeted Runtimes**, check **Apache Tomcat v10.1** (or add a new Tomcat 10.1+ runtime).
3. **Maven Update**:
   * Right-click the project &rarr; **Maven** &rarr; **Update Project...** &rarr; check **Force Update of Snapshots/Releases** &rarr; click **OK**.
4. **Deploy and Run**:
   * Right-click the project &rarr; **Run As** &rarr; **Run on Server**.
   * Select your configured Apache Tomcat 10.1+ server &rarr; click **Finish**.
   * Open your browser at:
     ```text
     http://localhost:8080/student-result-analysis/
     ```

---

### Option B: Command Line (Maven + Standalone Tomcat)

1. **Build and Test**:
   ```cmd
   mvnw.cmd clean test package
   ```
2. **Deploy WAR**:
   * Copy `target/student-result-analysis.war` to your Apache Tomcat `webapps/` folder:
     ```cmd
     copy target\student-result-analysis.war "C:\apache-tomcat-10.1.x\webapps\"
     ```
3. **Start Tomcat**:
   * Run `bin/startup.bat`.
   * Access `http://localhost:8080/student-result-analysis/`.

---

## 🔑 Demo Login Credentials

* **URL**: `http://localhost:8080/student-result-analysis/login`
* **Username**: `admin`
* **Password**: `admin123`

---

## 📊 Sample Data Testing

Two equivalent sample datasets containing **35 realistic Indian student records** are provided in `sample-data/`:
1. `sample-data/students.xlsx` (Excel workbook)
2. `sample-data/students.txt` (Comma-separated text)

### Edge Cases Covered in the Sample Files:
* **All Subjects Passed (&ge; 40)**: e.g., Roll 101, 102, 103
* **High Percentage but Failed Due to One Subject**: e.g., Roll 107 (Rajesh Gupta: Java 92, DE 35, DSA 90, OS 88 &rarr; Overall 76.25%, but **FAIL** because Digital Electronics is 35)
* **Exact 75% Boundary**: Roll 104 (Sneha Verma: 75 in all &rarr; 75.00%, **75%+ Distinction**)
* **Exact 65% Boundary**: Roll 105 (Vikram Malhotra: 65 in all &rarr; 65.00%, **65–74.99% First Class**)
* **Exact 40% Boundary**: Roll 106 (Ananya Roy: 40 in all &rarr; 40.00%, **40–64.99% Pass Class**)
* **Exact 100% Top Performer**: Roll 131 (Nitin Gadkari: 100 in all &rarr; 100.00%, **A+**)
* **Exact 0 Marks Floor**: Roll 132 (Varun Dhawan: 0 in all &rarr; 0.00%, **F, Below 40%**)
* **Multi-Subject Failures**: e.g., Roll 113, Roll 123

---

## 📑 How to Import Data & Generate PDF

1. **Importing Marks**:
   * Navigate to **Import Data** in the sidebar.
   * Click the file picker and select `sample-data/students.xlsx` or `sample-data/students.txt`.
   * Click **Import Data**.
   * View the processing summary detailing successfully inserted rows and any rejected duplicate/malformed entries.
2. **Generating the PDF Report**:
   * Click **Generate PDF** in the sidebar or **Download Report** in the top bar.
   * The server dynamically compiles database statistics, category distributions, subject metrics, and student rosters into a downloadable PDF: `Student_Result_Analysis_Report.pdf`.

---

## 🏫 Customizing College Information

Navigate to **College Settings** in the administrative sidebar to configure:
* **College Name**: e.g., `XYZ Institute of Technology`
* **Department**: e.g., `Computer Science & Engineering`
* **Course & Semester**: e.g., `BCA - Semester IV`
* **Academic Year**: e.g., `2026-27`
* **Custom Logo**: Upload a PNG or JPG emblem which automatically replaces the default logo on all views and PDF headers.

---

## 🧪 Testing Checklist & Verification

* [x] **Maven build succeeds** without compilation warnings.
* [x] **Tomcat 10.1+ Jakarta compatibility** verified (`jakarta.servlet.*`).
* [x] **Authentication**: Valid login (`admin`/`admin123`) and protected pages verified.
* [x] **Excel (.xlsx) import** via Apache POI verified.
* [x] **TXT import** with comma separation verified.
* [x] **Duplicate roll number detection** correctly rejects and preserves existing records.
* [x] **Invalid marks rejection** (< 0 or > 100) tested and verified.
* [x] **Pass/Fail calculation** correctly fails students with any subject < 40.
* [x] **Percentage classifications** tested for 75%+, 65–74.99%, 40–64.99%, and < 40%.
* [x] **Subject-wise analysis** (Average, Highest, Lowest, Pass %) verified.
* [x] **Interactive search and category filtering** verified on student roster.
* [x] **Dynamic dashboard metrics & Chart.js** verified.
* [x] **Downloadable PDF report** generation with OpenPDF verified with %PDF- header.
