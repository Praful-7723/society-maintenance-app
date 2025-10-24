package com.example.myapp.gui;

import com.example.myapp.dao.UserDao;

import javax.swing.*;
import java.awt.*;

public class SignUpFrame extends JFrame {

    public SignUpFrame() {
        setTitle("Resident Sign Up");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Full Name:");
        JTextField nameField = new JTextField();
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JLabel flatLabel = new JLabel("Flat Number (e.g., A-101):");
        JTextField flatField = new JTextField();
        JButton signUpButton = new JButton("Sign Up");
    UIUtils.styleButton(signUpButton);

        signUpButton.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String flatNumber = flatField.getText();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || flatNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            UserDao userDao = new UserDao();
            if (userDao.signUp(name, email, password, flatNumber)) {
                JOptionPane.showMessageDialog(this, "Sign Up Successful! Please log in.");
                new LoginFrame("RESIDENT").setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Sign Up Failed. Email or Flat Number may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(flatLabel);
        panel.add(flatField);
        panel.add(new JLabel()); // Empty cell
        panel.add(signUpButton);

        add(panel);
    }
}
