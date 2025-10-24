package com.example.myapp.model;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Improved Message model with:
 * - MessageType enum (INFO / REMINDER / URGENT)
 * - read/unread flag
 * - ISO-8601 timestamp handling using Instant
 * - validation and factory methods
 * - builder for fluent construction
 * - helpers to map to/from JDBC ResultSet / PreparedStatement
 */
public final class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int MAX_MESSAGE_LENGTH = 2000;
    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ISO_INSTANT;
    private static final DateTimeFormatter HUMAN_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    public enum MessageType { INFO, REMINDER, URGENT }

    private int id;               // -1 means not persisted yet
    private final int userId;
    private final String message;
    private final Instant timestamp;
    private final MessageType type;
    private boolean read;

    // Private constructor; use factory methods / builder
    private Message(int id, int userId, String message, Instant timestamp, MessageType type, boolean read) {
        validateUserId(userId);
        validateMessage(message);
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.timestamp = (timestamp == null) ? Instant.now() : timestamp;
        this.type = (type == null) ? MessageType.REMINDER : type;
        this.read = read;
    }

    // Validation helpers
    private static void validateUserId(int userId) {
        if (userId <= 0) throw new IllegalArgumentException("userId must be > 0");
    }

    private static void validateMessage(String msg) {
        if (msg == null || msg.trim().isEmpty()) throw new IllegalArgumentException("message cannot be empty");
        if (msg.length() > MAX_MESSAGE_LENGTH)
            throw new IllegalArgumentException("message too long (max " + MAX_MESSAGE_LENGTH + " chars)");
    }

    // Factory: new message for user (not persisted)
    public static Message createForUser(int userId, String message) {
        return new Message(-1, userId, message.trim(), Instant.now(), MessageType.REMINDER, false);
    }

    // Factory with type
    public static Message createForUser(int userId, String message, MessageType type) {
        return new Message(-1, userId, message.trim(), Instant.now(), (type == null ? MessageType.REMINDER : type), false);
    }

    // Build from DB ResultSet (expects columns: id,user_id,message,timestamp,type,read)
    public static Message fromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int userId = rs.getInt("user_id");
        String msg = rs.getString("message");
        String ts = rs.getString("timestamp");
        String typeStr = null;
        try { typeStr = rs.getString("type"); } catch (SQLException e) {
            // column 'type' may not exist on older DB schema; ignore and keep default
        }
        boolean read = false;
        try { read = rs.getBoolean("read"); } catch (SQLException e) {
            // column 'read' may not exist on older DB schema; treat as false
        }

        Instant instant;
        try {
            instant = (ts == null || ts.isEmpty()) ? Instant.now() : Instant.parse(ts);
        } catch (Exception e) {
            instant = Instant.now();
        }

        MessageType mtype = MessageType.REMINDER;
        if (typeStr != null) {
            try { mtype = MessageType.valueOf(typeStr); } catch (IllegalArgumentException e) {
                // Unknown type stored in DB; fall back to REMINDER
            }
        }

        return new Message(id, userId, msg, instant, mtype, read);
    }

    // Populate a PreparedStatement for insert: (user_id, message, timestamp, type, read)
    public int fillInsertStatement(PreparedStatement ps, int startIndex) throws SQLException {
        ps.setInt(startIndex++, userId);
        ps.setString(startIndex++, message);
        ps.setString(startIndex++, toIsoTimestamp());
        ps.setString(startIndex++, type.name());
        ps.setBoolean(startIndex++, read);
        return startIndex;
    }

    // Getters / Setters (id and read are mutable; others are immutable for safety)
    public int getId() { return id; }
    public void setId(int id) { if (this.id <= 0) this.id = id; } // only allow setting if new

    public int getUserId() { return userId; }
    public String getMessage() { return message; }
    public Instant getTimestamp() { return timestamp; }
    public MessageType getType() { return type; }

    public boolean isRead() { return read; }
    public Message markRead() { this.read = true; return this; }
    public Message markUnread() { this.read = false; return this; }

    // Timestamp helpers
    public String toIsoTimestamp() { return ISO_FMT.format(timestamp); }
    public String toHumanTimestamp() { return HUMAN_FMT.format(timestamp); }

    // Preview helper
    public String getPreview(int length) {
        if (length <= 0) return "";
        if (message.length() <= length) return message;
        return message.substring(0, length - 1) + "…";
    }

    // Builder for fluent creation
    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private int id = -1;
        private int userId;
        private String message;
        private Instant timestamp;
        private MessageType type = MessageType.REMINDER;
        private boolean read = false;

        private Builder() {}

        public Builder id(int id) { this.id = id; return this; }
        public Builder userId(int userId) { this.userId = userId; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }
        public Builder type(MessageType type) { this.type = type; return this; }
        public Builder read(boolean read) { this.read = read; return this; }

        public Message build() { return new Message(id, userId, message, timestamp, type, read); }
    }

    // equals/hashCode/toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message other = (Message) o;
        return id == other.id &&
                userId == other.userId &&
                read == other.read &&
                Objects.equals(message, other.message) &&
                Objects.equals(timestamp, other.timestamp) &&
                type == other.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, message, timestamp, type, read);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", userId=" + userId +
                ", type=" + type +
                ", read=" + read +
                ", timestamp=" + toIsoTimestamp() +
                ", message='" + getPreview(120) + '\'' +
                '}';
    }
}
