package com.college.result.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility for managing database connectivity using JDBC.
 * Reads configuration from db.properties or environment variables.
 * Provides resilient fallback to embedded H2 if MySQL is unavailable.
 */
public class DBConnectionUtil {
    private static final Logger LOGGER = Logger.getLogger(DBConnectionUtil.class.getName());

    private static String dbDriver = "com.mysql.cj.jdbc.Driver";
    private static String dbUrl = "jdbc:mysql://localhost:3306/student_result_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
    private static String dbUsername = "root";
    private static String dbPassword = "root";
    private static boolean fallbackH2 = true;

    private static volatile boolean usingFallback = false;
    private static volatile boolean h2Initialized = false;

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
     * Obtains a JDBC Connection.
     * Attempts primary MySQL connection first. If unreachable and fallback is enabled,
     * connects to embedded H2 in MySQL-compatibility mode.
     */
    public static Connection getConnection() throws SQLException {
        if (!usingFallback) {
            try {
                Class.forName(dbDriver);
                return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            } catch (ClassNotFoundException e) {
                LOGGER.log(Level.WARNING, "MySQL Driver not found: " + e.getMessage());
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Unable to connect to MySQL database (" + dbUrl + "): " + e.getMessage());
                if (!fallbackH2) {
                    throw new SQLException("Database connection failed. Please check MySQL configuration: " + e.getMessage(), e);
                }
                LOGGER.info("Switching to resilient embedded H2 database (MySQL compatibility mode)...");
                usingFallback = true;
            }
        }

        // Resilient Fallback Mode (H2 in-memory)
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:h2:mem:student_result_db;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_LOWER=TRUE",
                    "sa",
                    ""
            );
            if (!h2Initialized) {
                initFallbackSchema(conn);
                h2Initialized = true;
            }
            return conn;
        } catch (Exception ex) {
            throw new SQLException("Failed to initialize database connection: " + ex.getMessage(), ex);
        }
    }

    private static synchronized void initFallbackSchema(Connection conn) {
        try (Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) NOT NULL UNIQUE, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "role VARCHAR(20) DEFAULT 'ADMIN', " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            st.execute("INSERT INTO users (username, password, role) " +
                    "SELECT 'admin', 'admin123', 'ADMIN' " +
                    "WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin')");

            st.execute("CREATE TABLE IF NOT EXISTS students (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "roll_no VARCHAR(50) NOT NULL UNIQUE, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            st.execute("CREATE TABLE IF NOT EXISTS marks (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "student_id INT NOT NULL, " +
                    "java_marks DECIMAL(5,2) NOT NULL, " +
                    "de_marks DECIMAL(5,2) NOT NULL, " +
                    "dsa_marks DECIMAL(5,2) NOT NULL, " +
                    "os_marks DECIMAL(5,2) NOT NULL, " +
                    "FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE)");

            st.execute("CREATE TABLE IF NOT EXISTS import_history (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "file_name VARCHAR(255) NOT NULL, " +
                    "file_type VARCHAR(20) NOT NULL, " +
                    "total_records INT NOT NULL, " +
                    "successful_records INT NOT NULL, " +
                    "rejected_records INT NOT NULL, " +
                    "status VARCHAR(50) NOT NULL, " +
                    "imported_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

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

            st.execute("MERGE INTO college_settings KEY(id) " +
                    "VALUES (1, 'YOUR COLLEGE NAME', 'YOUR DEPARTMENT', 'BCA', 'YOUR SEMESTER', '2026-27', 'Student Result Analysis System', 'images/college-logo.png', CURRENT_TIMESTAMP)");

            LOGGER.info("Fallback H2 schema initialized successfully.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error initializing fallback schema: " + e.getMessage(), e);
        }
    }

    public static boolean isUsingFallback() {
        return usingFallback;
    }

    public static void setUsingFallback(boolean fallback) {
        usingFallback = fallback;
    }

    public static String getDbUrl() {
        return usingFallback ? "jdbc:h2:mem:student_result_db (Embedded)" : dbUrl;
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
