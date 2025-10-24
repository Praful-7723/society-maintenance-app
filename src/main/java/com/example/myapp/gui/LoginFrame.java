package com.example.myapp.gui;

import com.example.myapp.dao.UserDao;
import com.example.myapp.model.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final String role;

    public LoginFrame(String role) {
        this.role = role;
        setTitle(role + " Login");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
    UIUtils.styleButton(loginButton);

        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            // Check DB connection first
            java.sql.Connection testConn = null;
            try {
                testConn = com.example.myapp.db.DatabaseConnector.connect();
                if (testConn == null) {
                    JOptionPane.showMessageDialog(this, "Cannot connect to the database. Please check your MySQL server and environment variables.", "Database Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                return;
            } finally {
                try { if (testConn != null) testConn.close(); } catch (Exception ignored) {}
            }

            UserDao userDao = new UserDao();
            User user = userDao.login(email, password);

            if (user != null && user.getRole().equals(role)) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                if ("ADMIN".equals(role)) {
                    new AdminDashboard().setVisible(true);
                } else {
                    new ResidentDashboard(user).setVisible(true);
                }
                this.dispose();
            } else if (user != null) {
                JOptionPane.showMessageDialog(this, "Your credentials are correct, but you are not registered as " + role + ". Your role is: " + user.getRole(), "Role Mismatch", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel()); // Empty cell
    panel.add(loginButton);

        add(panel);
    }
}
