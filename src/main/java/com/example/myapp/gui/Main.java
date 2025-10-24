package com.example.myapp.gui;

import com.example.myapp.db.DatabaseConnector;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("[DEBUG] Society Maintenance App starting...");
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            System.err.println("[ERROR] Uncaught exception in thread " + t.getName() + ": " + e);
            e.printStackTrace();
        });

        // Initialize the database and tables
        DatabaseConnector.initializeDatabase();

        // Run the GUI on the Event Dispatch Thread
        // Load logging configuration from resources (if available)
        try {
            java.net.URL cfg = Main.class.getResource("/logging.properties");
            if (cfg != null) java.util.logging.LogManager.getLogManager().readConfiguration(cfg.openStream());
        } catch (Exception ex) {
            System.err.println("Could not load logging configuration: " + ex.getMessage());
        }

        // Apply a modern look-and-feel (Nimbus) and some defaults
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            // tweak some UI defaults for a cleaner modern look
            UIManager.put("control", new javax.swing.plaf.ColorUIResource(245,245,245));
            UIManager.put("nimbusBase", new javax.swing.plaf.ColorUIResource(50,50,50));
            UIManager.put("nimbusFocus", new javax.swing.plaf.ColorUIResource(100,150,250));
            UIManager.put("TabbedPane.contentAreaColor", new javax.swing.plaf.ColorUIResource(250,250,250));
            UIManager.put("defaultFont", new javax.swing.plaf.FontUIResource("Segoe UI", java.awt.Font.PLAIN, 13));
        } catch (Exception ex) {
            // If Nimbus is not available, fall back to default LAF
            System.err.println("Could not set Nimbus L&F: " + ex.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            System.out.println("[DEBUG] Showing WelcomeFrame...");
            WelcomeFrame welcomeFrame = new WelcomeFrame();
            welcomeFrame.setVisible(true);
        });
    }
}
