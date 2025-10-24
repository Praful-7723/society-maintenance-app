package com.example.myapp.gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class PaymentDialog extends JDialog {

    private String utr;

    public PaymentDialog(Frame owner) {
        super(owner, "Complete Your Payment", true);
        setSize(450, 550); // A fixed, reasonable size
        setResizable(false);
        setLocationRelativeTo(owner);
        
        // Use a BorderLayout for the main dialog content pane
        setLayout(new BorderLayout(10, 10));

        // 1. QR Code Panel (in the Center)
        // This panel will hold the image inside a scroll pane to contain it
        
        URL imageUrl = getClass().getResource("/images/qr-code.png");
        JLabel qrLabel;

        if (imageUrl == null) {
            // If the image is not found, display an error message instead
            qrLabel = new JLabel("<html><div style='text-align: center;'>QR Code image not found.<br>Please ensure 'qr-code.png' is in<br>src/main/resources/images/</div></html>");
            qrLabel.setHorizontalAlignment(SwingConstants.CENTER);
            qrLabel.setOpaque(true);
            qrLabel.setBackground(Color.PINK);
            qrLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        } else {
            ImageIcon qrIcon = new ImageIcon(imageUrl);
            // Scale the image to a fixed size to be safe
            Image image = qrIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
            qrLabel = new JLabel(new ImageIcon(image));
            qrLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }

        // Put the label in a scroll pane. This is the key to containing it.
        JScrollPane scrollPane = new JScrollPane(qrLabel);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Scan to Pay"));

        // 2. Input Panel (at the Bottom)
        // This panel holds the UTR field and button and will not be pushed around.
        
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JLabel utrLabel = new JLabel("Enter UTR/Transaction ID:");
        JTextField utrField = new JTextField(15);
        JButton submitButton = new JButton("Submit");

        inputPanel.add(utrLabel);
        inputPanel.add(utrField);
        inputPanel.add(submitButton);

        // Add the main components to the dialog's content pane
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        // Action Listener for the submit button
        submitButton.addActionListener(e -> {
            String enteredUtr = utrField.getText();
            if (enteredUtr == null || enteredUtr.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "UTR cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                this.utr = null; // Ensure UTR is null if input is invalid
            } else {
                this.utr = enteredUtr;
                dispose(); // Close the dialog
            }
        });
    }

    // Public method to get the UTR after the dialog is closed
    public String getUtr() {
        return utr;
    }
}
