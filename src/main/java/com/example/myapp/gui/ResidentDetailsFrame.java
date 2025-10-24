package com.example.myapp.gui;

import com.example.myapp.dao.UserDao;
import com.example.myapp.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ResidentDetailsFrame extends JFrame {
    private JTable residentsTable;
    private DefaultTableModel residentsModel;
    private UserDao userDao = new UserDao();

    public ResidentDetailsFrame(AdminDashboard parent) {
        setTitle("Resident Details");
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] residentColumns = {"Name", "Email", "Flat Number"};
        residentsModel = new DefaultTableModel(residentColumns, 0);
        residentsTable = new JTable(residentsModel);
        loadResidents();
        UIUtils.applyStripedTable(residentsTable);

        JButton removeResidentBtn = new JButton("Remove Resident", UIUtils.createIcon(new Color(220,53,69), 12));
        UIUtils.styleButton(removeResidentBtn);
        removeResidentBtn.addActionListener(e -> removeResident());

        JButton backBtn = new JButton("Back to Dashboard");
        UIUtils.styleButton(backBtn);
        backBtn.addActionListener(e -> {
            this.dispose();
            parent.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(removeResidentBtn);
        buttonPanel.add(backBtn);

        add(new JScrollPane(residentsTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadResidents() {
        residentsModel.setRowCount(0);
        List<User> residents = userDao.getAllResidents();
        for (User u : residents) {
            residentsModel.addRow(new Object[]{u.getName(), u.getEmail(), u.getFlatNumber()});
        }
    }

    private void removeResident() {
        int selectedRow = residentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a resident to remove.", AppConstants.TITLE_WARNING, JOptionPane.WARNING_MESSAGE);
            return;
        }
        String email = (String) residentsModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove resident: " + email + "?", "Confirm Remove", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
                boolean success = userDao.removeResidentByEmail(email);
                if (success) {
                    loadResidents(); // Refresh list
                    JOptionPane.showMessageDialog(this, "Resident removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to remove resident. Make sure all bills and related data are deleted.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
