package com.example.myapp.dao;

import com.example.myapp.db.DatabaseConnector;
import com.example.myapp.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDao {
    public boolean removeResidentByEmail(String email) {
        try (Connection conn = DatabaseConnector.connect()) {
            // First, get user id
            String getUserIdSql = "SELECT id FROM users WHERE email = ?";
            try (PreparedStatement getUserIdStmt = conn.prepareStatement(getUserIdSql)) {
                getUserIdStmt.setString(1, email);
                ResultSet rs = getUserIdStmt.executeQuery();
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    // Delete bills for user
                    String deleteBillsSql = "DELETE FROM bills WHERE user_id = ?";
                    try (PreparedStatement deleteBillsStmt = conn.prepareStatement(deleteBillsSql)) {
                        deleteBillsStmt.setInt(1, userId);
                        deleteBillsStmt.executeUpdate();
                    }
                    // Delete user
                    String deleteSql = "DELETE FROM users WHERE id = ?";
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                        deleteStmt.setInt(1, userId);
                        int affected = deleteStmt.executeUpdate();
                        return affected > 0;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error removing resident", e);
        }
        return false;
    }

    private static final Logger LOGGER = Logger.getLogger(UserDao.class.getName());

    public User login(String email, String password) {
        String sql = "SELECT id, name, email, flat_number, role FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("flat_number"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error executing login query", e);
        }
        return null;
    }

    public boolean signUp(String name, String email, String password, String flatNumber) {
        String dbType = System.getenv("DB_TYPE");
        boolean isMySql = dbType != null && dbType.equalsIgnoreCase("mysql");
        String sql;
        if (isMySql) {
            sql = "INSERT INTO users (name, email, password, flat_number, role) " +
                  "SELECT ?, ?, ?, ?, 'RESIDENT' FROM DUAL " +
                  "WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = '" + email + "' OR flat_number = '" + flatNumber + "') LIMIT 1;";
        } else {
            sql = "INSERT OR IGNORE INTO users(name, email, password, flat_number, role) VALUES(?,?,?,?, 'RESIDENT')";
        }
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, flatNumber);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating user", e);
            return false;
        }
    }

    public List<User> getAllResidents() {
        String sql = "SELECT id, name, email, flat_number, role FROM users WHERE role = 'RESIDENT'";
        List<User> residents = new ArrayList<>();
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                residents.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("flat_number"),
                        rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching residents", e);
        }
        return residents;
    }
}
