package com.example.myapp.gui;

import javax.swing.*;
import java.awt.*;

public class WelcomeFrame extends JFrame {

    public WelcomeFrame() {
        setTitle("Welcome to Society Maintenance");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel welcomeLabel = new JLabel("Welcome to the Society!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 20));

        JButton adminLoginBtn = new JButton("Admin Login");
        UIUtils.styleButton(adminLoginBtn);
        adminLoginBtn.addActionListener(e -> {
            new LoginFrame("ADMIN").setVisible(true);
            this.dispose();
        });

        JButton residentLoginBtn = new JButton("Resident Login");
        UIUtils.styleButton(residentLoginBtn);
        residentLoginBtn.addActionListener(e -> {
            new LoginFrame("RESIDENT").setVisible(true);
            this.dispose();
        });

        JButton residentSignUpBtn = new JButton("Resident Sign Up");
        UIUtils.styleButton(residentSignUpBtn);
        residentSignUpBtn.addActionListener(e -> {
            new SignUpFrame().setVisible(true);
            this.dispose();
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        panel.add(adminLoginBtn);
        panel.add(residentLoginBtn);
        panel.add(residentSignUpBtn);

        getContentPane().add(BorderLayout.CENTER, panel);
        getContentPane().add(BorderLayout.NORTH, welcomeLabel);
    }
}
