package com.example.myapp.dao;

import com.example.myapp.db.DatabaseConnector;
import com.example.myapp.model.Bill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BillDao {

    private static final Logger LOGGER = Logger.getLogger(BillDao.class.getName());

    public List<Object[]> getBillsForAdminDashboard() {
        String sql = "SELECT u.name, u.flat_number, b.month, b.year, b.amount, b.status, b.approval_status, b.utr, b.id " +
                     "FROM bills b JOIN users u ON b.user_id = u.id";
        List<Object[]> bills = new ArrayList<>();
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                bills.add(new Object[]{
                    rs.getString("name"),
                    rs.getString("flat_number"),
                    rs.getInt("month"),
                    rs.getInt("year"),
                    rs.getDouble("amount"),
                    rs.getString("status"),
                    rs.getString("approval_status"),
                    rs.getString("utr"),
                    rs.getInt("id")
                });
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching bills for admin", e);
        }
        return bills;
    }

    public void createBill(int userId, double amount, int month, int year) {
        // Prevent duplicate bills for the same resident/month/year
        String checkSql = "SELECT COUNT(*) FROM bills WHERE user_id = ? AND month = ? AND year = ?";
        String insertSql = "INSERT INTO bills(user_id, amount, month, year, approval_status) VALUES(?,?,?,?,?)";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, month);
            checkStmt.setInt(3, year);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setDouble(2, amount);
                    insertStmt.setInt(3, month);
                    insertStmt.setInt(4, year);
                    insertStmt.setString(5, "PENDING");
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating bill", e);
        }
    }

    public void updateBillStatus(int billId, String status, String approvalStatus, String utr) {
        String sql = "UPDATE bills SET status = ?, approval_status = ?, utr = ? WHERE id = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setString(2, approvalStatus);
            pstmt.setString(3, utr);
            pstmt.setInt(4, billId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating bill status", e);
        }
    }
    
    public List<Bill> getBillsForUser(int userId) {
        String sql = "SELECT id, user_id, amount, month, year, status, approval_status, utr " +
                     "FROM bills WHERE user_id = ? ORDER BY year DESC, month DESC, id DESC";
        List<Bill> bills = new ArrayList<>();
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bills.add(new Bill(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getDouble("amount"),
                    rs.getInt("month"),
                    rs.getInt("year"),
                    rs.getString("status"),
                    rs.getString("approval_status"),
                    rs.getString("utr")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching bills for user", e);
        }
        return bills;
    }
}
