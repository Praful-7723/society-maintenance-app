package com.example.myapp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnector {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnector.class.getName());

    // Default to SQLite file-based DB in project working directory. To use MySQL,
    // set the following environment variables before running the app:
    //   DB_TYPE=mysql
    //   DB_HOST=localhost
    //   DB_PORT=3306
    //   DB_NAME=society
    //   DB_USER=root
    //   DB_PASS=secret
    private static final String DEFAULT_SQLITE_URL = "jdbc:sqlite:society.db";

    private static String getJdbcUrl() {
        String dbType = System.getenv("DB_TYPE");
        if (dbType != null && dbType.equalsIgnoreCase("mysql")) {
            String host = System.getenv().getOrDefault("DB_HOST", "localhost");
            String port = System.getenv().getOrDefault("DB_PORT", "3306");
            String dbName = System.getenv().getOrDefault("DB_NAME", "society");
            return String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", host, port, dbName);
        }
        return DEFAULT_SQLITE_URL;
    }

    private static String getDbUser() {
        return System.getenv().getOrDefault("DB_USER", "");
    }

    private static String getDbPass() {
        return System.getenv().getOrDefault("DB_PASS", "");
    }

    public static Connection connect() {
        Connection conn = null;
        String url = getJdbcUrl();
        try {
            String dbType = System.getenv("DB_TYPE");
            if (dbType != null && dbType.equalsIgnoreCase("mysql")) {
                conn = DriverManager.getConnection(url, getDbUser(), getDbPass());
            } else {
                conn = DriverManager.getConnection(url);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize database", e);
        }
        return conn;
    }

    public static void initializeDatabase() {
        Connection conn = connect();
        if (conn == null) {
            LOGGER.severe("Cannot initialize database because connection is null");
            return;
        }
        String dbType = System.getenv("DB_TYPE");
        boolean isMySql = dbType != null && dbType.equalsIgnoreCase("mysql");

        try (Statement stmt = conn.createStatement()) {
            // Table for Residents/Users
            String sqlUser;
            if (isMySql) {
                sqlUser = "CREATE TABLE IF NOT EXISTS users ("
                        + " id INT PRIMARY KEY AUTO_INCREMENT,"
                        + " name VARCHAR(255) NOT NULL,"
                        + " email VARCHAR(255) NOT NULL UNIQUE,"
                        + " password VARCHAR(255) NOT NULL,"
                        + " flat_number VARCHAR(50) NOT NULL UNIQUE,"
                        + " role VARCHAR(50) NOT NULL DEFAULT 'RESIDENT'"
                        + ") ENGINE=InnoDB;";
            } else {
                sqlUser = "CREATE TABLE IF NOT EXISTS users ("
                        + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + " name TEXT NOT NULL,"
                        + " email TEXT NOT NULL UNIQUE,"
                        + " password TEXT NOT NULL,"
                        + " flat_number TEXT NOT NULL UNIQUE,"
                        + " role TEXT NOT NULL DEFAULT 'RESIDENT'"
                        + ");";
            }

            // Table for Maintenance Bills
        String sqlBill;
        if (isMySql) {
        sqlBill = "CREATE TABLE IF NOT EXISTS bills ("
            + " id INT PRIMARY KEY AUTO_INCREMENT,"
            + " user_id INT NOT NULL,"
            + " amount DOUBLE NOT NULL,"
            + " month INT NOT NULL,"
            + " year INT NOT NULL,"
            + " status VARCHAR(50) NOT NULL DEFAULT 'UNPAID',"
            + " approval_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',"
            + " utr VARCHAR(255),"
            + " FOREIGN KEY (user_id) REFERENCES users (id)"
            + ") ENGINE=InnoDB;";
        } else {
        sqlBill = "CREATE TABLE IF NOT EXISTS bills ("
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " user_id INTEGER NOT NULL,"
            + " amount REAL NOT NULL,"
            + " month INTEGER NOT NULL,"
            + " year INTEGER NOT NULL,"
            + " status TEXT NOT NULL DEFAULT 'UNPAID'," // UNPAID, PENDING, PAID
            + " approval_status TEXT NOT NULL DEFAULT 'PENDING',"
            + " utr TEXT,"
            + " FOREIGN KEY (user_id) REFERENCES users (id)"
            + ");";
        }

            // Table for Messages
        String sqlMessage;
        if (isMySql) {
        sqlMessage = "CREATE TABLE IF NOT EXISTS messages ("
            + " id INT PRIMARY KEY AUTO_INCREMENT,"
            + " user_id INT NOT NULL,"
            + " message TEXT NOT NULL,"
            + " timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + " FOREIGN KEY (user_id) REFERENCES users (id)"
            + ") ENGINE=InnoDB;";
        } else {
        sqlMessage = "CREATE TABLE IF NOT EXISTS messages ("
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " user_id INTEGER NOT NULL,"
            + " message TEXT NOT NULL,"
            + " timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + " FOREIGN KEY (user_id) REFERENCES users (id)"
            + ");";
        }
            
            // Create a default admin user if it doesn't exist
            String sqlAdmin;
            if (isMySql) {
                // Use explicit aliases for derived table columns to avoid MySQL 'Duplicate column name' errors
                sqlAdmin = "INSERT INTO users (name, email, password, flat_number, role) " +
                        "SELECT t.name, t.email, t.password, t.flat_number, t.role FROM (" +
                        "SELECT 'Admin' AS name, 'admin@society.com' AS email, 'admin123' AS password, 'A-000' AS flat_number, 'ADMIN' AS role" +
                        ") AS t WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@society.com') LIMIT 1;";
            } else {
                sqlAdmin = "INSERT OR IGNORE INTO users (name, email, password, flat_number, role) " +
                        "VALUES ('Admin', 'admin@society.com', 'admin123', 'A-000', 'ADMIN');";
            }

            stmt.execute(sqlUser);
            stmt.execute(sqlBill);
            stmt.execute(sqlMessage);
            stmt.execute(sqlAdmin);

            // Migrate messages table to include 'type' and 'read' columns if they don't exist.
            // SQLite doesn't support IF NOT EXISTS for ALTER, so attempt and ignore failures.
            try {
                stmt.execute("ALTER TABLE messages ADD COLUMN type TEXT DEFAULT 'REMINDER'");
            } catch (SQLException ignored) {
                // column may already exist; ignore
            }
            try {
                stmt.execute("ALTER TABLE messages ADD COLUMN read BOOLEAN DEFAULT 0");
            } catch (SQLException ignored) {
                // column may already exist; ignore
            }

        } catch (SQLException e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Database initialization failed", e);
        } finally {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
