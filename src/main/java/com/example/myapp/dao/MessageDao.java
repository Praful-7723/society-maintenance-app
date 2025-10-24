package com.example.myapp.dao;

import com.example.myapp.db.DatabaseConnector;
import com.example.myapp.model.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageDao {

    private static final Logger LOGGER = Logger.getLogger(MessageDao.class.getName());

    public void createMessage(int userId, String message) {
    String sql = "INSERT INTO messages(user_id, message, `type`, `read`) VALUES(?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, message);
            pstmt.setString(3, "REMINDER");
            pstmt.setBoolean(4, false);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating message", e);
        }
    }

    public List<Message> getMessagesForUser(int userId) {
    String sql = "SELECT id, user_id, message, timestamp, `type`, `read` FROM messages WHERE user_id = ? ORDER BY timestamp DESC";
        List<Message> messages = new ArrayList<>();
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                messages.add(Message.fromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching messages", e);
        }
        return messages;
    }

    public void markAllAsReadForUser(int userId) {
    String sql = "UPDATE messages SET `read` = 1 WHERE user_id = ? AND `read` = 0";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                String msg = "Error marking messages read for user " + userId;
                LOGGER.log(Level.WARNING, msg, e);
            }
        }
    }

    public int countUnreadForUser(int userId) {
    String sql = "SELECT COUNT(*) AS cnt FROM messages WHERE user_id = ? AND `read` = 0";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("cnt");
        } catch (SQLException e) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                String msg = "Error counting unread messages for user " + userId;
                LOGGER.log(Level.WARNING, msg, e);
            }
        }
        return 0;
    }
}
