package com.college.result.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility for managing database connectivity using JDBC.
 * Reads configuration from db.properties or environment variables.
 * Provides resilient fallback to persistent file-based H2 if MySQL is unavailable.
 */
public class DBConnectionUtil {
    private static final Logger LOGGER = Logger.getLogger(DBConnectionUtil.class.getName());

    private static String dbDriver = "com.mysql.cj.jdbc.Driver";
    private static String dbUrl = "jdbc:mysql://localhost:3306/student_result_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
    private static String dbUsername = "root";
    private static String dbPassword = "root";
    private static boolean fallbackH2 = true;

    private static volatile boolean usingFallback = false;
    private static volatile boolean schemaInitialized = false;

    static {
        loadProperties();
    }

    private static synchronized void loadProperties() {
        try (InputStream is = DBConnectionUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                if (props.getProperty("db.driver") != null) dbDriver = props.getProperty("db.driver").trim();
                if (props.getProperty("db.url") != null) dbUrl = props.getProperty("db.url").trim();
                if (props.getProperty("db.username") != null) dbUsername = props.getProperty("db.username").trim();
                if (props.getProperty("db.password") != null) dbPassword = props.getProperty("db.password").trim();
                if (props.getProperty("db.fallback.h2") != null) {
                    fallbackH2 = Boolean.parseBoolean(props.getProperty("db.fallback.h2").trim());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to load db.properties, using defaults: " + e.getMessage());
        }

        // Support environment variable overrides
        String envUrl = System.getenv("DB_URL");
        String envUser = System.getenv("DB_USERNAME");
        String envPass = System.getenv("DB_PASSWORD");
        if (envUrl != null && !envUrl.isEmpty()) dbUrl = envUrl;
        if (envUser != null && !envUser.isEmpty()) dbUsername = envUser;
        if (envPass != null) dbPassword = envPass;
    }

    /**
     * Resolves safe persistent database path for embedded H2 mode.
     * Stored in <catalina.base>/database_data or ./database_data.
     */
    public static String getPersistentH2Path() {
        String baseDir = System.getProperty("catalina.base");
        if (baseDir == null || baseDir.trim().isEmpty()) {
            baseDir = System.getProperty("user.dir");
        }
        File folder = new File(baseDir, "database_data");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return new File(folder, "student_result_db").getAbsolutePath().replace('\\', '/');
    }

    public static String getPersistentH2Url() {
        boolean isTestRunner = System.getProperty("surefire.test.class.path") != null;
        if (isTestRunner) {
            return "jdbc:h2:mem:student_result_test_db;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_LOWER=TRUE";
        }
        return "jdbc:h2:file:" + getPersistentH2Path() + ";DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_LOWER=TRUE;AUTO_SERVER=TRUE";
    }

    /**
     * Obtains a JDBC Connection.
     * Attempts primary MySQL connection first. If unreachable and fallback is enabled,
     * connects to persistent embedded H2 in MySQL-compatibility mode.
     */
    public static Connection getConnection() throws SQLException {
        if (!usingFallback) {
            try {
                Class.forName(dbDriver);
                Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                if (!schemaInitialized) {
                    initSchemaIfNeeded(conn);
                    schemaInitialized = true;
                }
                return conn;
            } catch (ClassNotFoundException e) {
                LOGGER.log(Level.WARNING, "MySQL Driver not found: " + e.getMessage());
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Unable to connect to MySQL database (" + dbUrl + "): " + e.getMessage());
                if (!fallbackH2) {
                    throw new SQLException("Database connection failed. Please check MySQL configuration: " + e.getMessage(), e);
                }
                LOGGER.info("Switching to persistent embedded H2 database (MySQL compatibility mode)...");
                usingFallback = true;
            }
        }

        // Resilient Fallback Mode (Persistent H2 on disk)
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection(getPersistentH2Url(), "sa", "");
            if (!schemaInitialized) {
                initSchemaIfNeeded(conn);
                schemaInitialized = true;
            }
            return conn;
        } catch (Exception ex) {
            throw new SQLException("Failed to initialize database connection: " + ex.getMessage(), ex);
        }
    }

    private static synchronized void initSchemaIfNeeded(Connection conn) {
        try (Statement st = conn.createStatement()) {
            // 1. Users Table
            st.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) NOT NULL UNIQUE, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "role VARCHAR(20) DEFAULT 'ADMIN', " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            st.execute("INSERT INTO users (username, password, role) " +
                    "SELECT 'admin', 'admin123', 'ADMIN' " +
                    "WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin')");

            // 2. Students Table
            st.execute("CREATE TABLE IF NOT EXISTS students (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "roll_no VARCHAR(50) NOT NULL UNIQUE, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            // 3. Marks Table
            st.execute("CREATE TABLE IF NOT EXISTS marks (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "student_id INT NOT NULL, " +
                    "java_marks DECIMAL(5,2) NOT NULL, " +
                    "de_marks DECIMAL(5,2) NOT NULL, " +
                    "dsa_marks DECIMAL(5,2) NOT NULL, " +
                    "os_marks DECIMAL(5,2) NOT NULL, " +
                    "FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE)");

            // 4. Import History Table
            st.execute("CREATE TABLE IF NOT EXISTS import_history (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "file_name VARCHAR(255) NOT NULL, " +
                    "file_type VARCHAR(20) NOT NULL, " +
                    "total_records INT NOT NULL, " +
                    "successful_records INT NOT NULL, " +
                    "rejected_records INT NOT NULL, " +
                    "status VARCHAR(50) NOT NULL, " +
                    "imported_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            // 5. College Settings Table
            st.execute("CREATE TABLE IF NOT EXISTS college_settings (" +
                    "id INT PRIMARY KEY, " +
                    "college_name VARCHAR(255) NOT NULL, " +
                    "department_name VARCHAR(255) NOT NULL, " +
                    "course_name VARCHAR(100) NOT NULL, " +
                    "semester VARCHAR(50) NOT NULL, " +
                    "academic_year VARCHAR(50) NOT NULL, " +
                    "project_title VARCHAR(255) NOT NULL, " +
                    "logo_path VARCHAR(255) DEFAULT 'images/college-logo.png', " +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            if (usingFallback) {
                st.execute("MERGE INTO college_settings KEY(id) " +
                        "VALUES (1, 'NATIONAL INSTITUTE OF TECHNOLOGY', 'Computer Science & Engineering', 'BCA', 'Semester IV', '2026-27', 'Student Result Analysis System', 'images/college-logo.png', CURRENT_TIMESTAMP)");
            } else {
                st.execute("INSERT INTO college_settings (id, college_name, department_name, course_name, semester, academic_year, project_title, logo_path) " +
                        "VALUES (1, 'NATIONAL INSTITUTE OF TECHNOLOGY', 'Computer Science & Engineering', 'BCA', 'Semester IV', '2026-27', 'Student Result Analysis System', 'images/college-logo.png') " +
                        "ON DUPLICATE KEY UPDATE id=1");
            }

            // Check if students table is empty; if so and not in test runner, seed demo dataset
            boolean isTestRunner = System.getProperty("surefire.test.class.path") != null;
            if (!isTestRunner) {
                try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM students")) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        LOGGER.info("Database is empty. Automatically seeding 35 benchmark demo students...");
                        seedDemoDataInternal(conn);
                    }
                }
            }

            LOGGER.info("Database schema verified and active.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error initializing database schema: " + e.getMessage(), e);
        }
    }

    /**
     * Seeds the complete 35 benchmark student records with diverse marks and categories.
     */
    public static synchronized boolean seedDemoDataInternal(Connection conn) {
        String[][] benchmarkData = {
                {"101", "Rahul Kumar", "85", "78", "88", "92"},
                {"102", "Priya Sharma", "95", "92", "94", "98"},
                {"103", "Amit Patel", "68", "72", "70", "74"},
                {"104", "Sneha Verma", "75", "75", "75", "75"},
                {"105", "Vikram Malhotra", "65", "65", "65", "65"},
                {"106", "Ananya Roy", "40", "40", "40", "40"},
                {"107", "Rajesh Gupta", "92", "35", "90", "88"},
                {"108", "Pooja Nair", "88", "90", "38", "86"},
                {"109", "Rohan Joshi", "52", "58", "60", "54"},
                {"110", "Meera Iyer", "62", "64", "60", "68"},
                {"111", "Arjun Reddy", "78", "82", "80", "84"},
                {"112", "Divya Pillai", "45", "48", "50", "42"},
                {"113", "Karan Kapoor", "32", "28", "35", "30"},
                {"114", "Ritu Deshmukh", "80", "85", "82", "88"},
                {"115", "Sandeep Chawla", "58", "62", "64", "60"},
                {"116", "Neha Saxena", "70", "72", "68", "74"},
                {"117", "Aditya Mehta", "90", "88", "86", "92"},
                {"118", "Swati Kulkarni", "42", "44", "46", "40"},
                {"119", "Manish Pandey", "38", "50", "55", "60"},
                {"120", "Kavita Bhat", "74", "76", "72", "70"},
                {"121", "Suresh Menon", "66", "68", "70", "65"},
                {"122", "Sunita Sen", "84", "86", "80", "82"},
                {"123", "Deepak Yadav", "25", "30", "28", "32"},
                {"124", "Pallavi Chatterjee", "96", "94", "98", "96"},
                {"125", "Harish Nambiar", "50", "52", "48", "55"},
                {"126", "Tanvi Hegde", "69", "71", "73", "68"},
                {"127", "Gaurav Bansal", "88", "82", "85", "90"},
                {"128", "Shreya Ghoshal", "72", "70", "74", "68"},
                {"129", "Alok Tiwari", "41", "43", "45", "42"},
                {"130", "Bhavna Agarwal", "85", "88", "92", "86"},
                {"131", "Nitin Gadkari", "100", "100", "100", "100"},
                {"132", "Varun Dhawan", "0", "0", "0", "0"},
                {"133", "Ishita Das", "82", "80", "84", "39"},
                {"134", "Sanjay Singhania", "60", "62", "58", "64"},
                {"135", "Madhuri Dixit", "77", "75", "79", "81"}
        };

        boolean originalAutoCommit = true;
        try {
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            String studentSql = "INSERT INTO students (roll_no, name) VALUES (?, ?)";
            String marksSql = "INSERT INTO marks (student_id, java_marks, de_marks, dsa_marks, os_marks) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement sPs = conn.prepareStatement(studentSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement mPs = conn.prepareStatement(marksSql)) {

                for (String[] row : benchmarkData) {
                    String rollNo = row[0];
                    String name = row[1];
                    double java = Double.parseDouble(row[2]);
                    double de = Double.parseDouble(row[3]);
                    double dsa = Double.parseDouble(row[4]);
                    double os = Double.parseDouble(row[5]);

                    // Check if student already exists
                    try (PreparedStatement checkPs = conn.prepareStatement("SELECT id FROM students WHERE roll_no = ?")) {
                        checkPs.setString(1, rollNo);
                        try (ResultSet rs = checkPs.executeQuery()) {
                            if (rs.next()) {
                                continue; // Skip existing
                            }
                        }
                    }

                    sPs.setString(1, rollNo);
                    sPs.setString(2, name);
                    sPs.executeUpdate();

                    int studentId = -1;
                    try (ResultSet rs = sPs.getGeneratedKeys()) {
                        if (rs.next()) {
                            studentId = rs.getInt(1);
                        }
                    }

                    if (studentId > 0) {
                        mPs.setInt(1, studentId);
                        mPs.setDouble(2, java);
                        mPs.setDouble(3, de);
                        mPs.setDouble(4, dsa);
                        mPs.setDouble(5, os);
                        mPs.executeUpdate();
                    }
                }
            }

            // Insert audit history
            String histSql = "INSERT INTO import_history (file_name, file_type, total_records, successful_records, rejected_records, status) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement hPs = conn.prepareStatement(histSql)) {
                hPs.setString(1, "Benchmark_Dataset_Seed.txt");
                hPs.setString(2, "DEMO_SEED");
                hPs.setInt(3, benchmarkData.length);
                hPs.setInt(4, benchmarkData.length);
                hPs.setInt(5, 0);
                hPs.setString(6, "Success");
                hPs.executeUpdate();
            }

            conn.commit();
            LOGGER.info("Demo benchmark dataset seeded successfully with " + benchmarkData.length + " students.");
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to seed demo dataset: " + e.getMessage(), e);
            try { conn.rollback(); } catch (Exception ignored) {}
            return false;
        } finally {
            try { conn.setAutoCommit(originalAutoCommit); } catch (Exception ignored) {}
        }
    }

    public static boolean isUsingFallback() {
        return usingFallback;
    }

    public static void setUsingFallback(boolean fallback) {
        usingFallback = fallback;
    }

    public static String getDatabaseStatus() {
        if (!usingFallback) {
            return "MySQL (Active: localhost:3306)";
        } else {
            return "Persistent Embedded DB (Saved: database_data)";
        }
    }

    public static String getDbUrl() {
        return usingFallback ? getPersistentH2Url() : dbUrl;
    }

    public static void close(AutoCloseable... closeables) {
        for (AutoCloseable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception ignored) {}
            }
        }
    }
}
