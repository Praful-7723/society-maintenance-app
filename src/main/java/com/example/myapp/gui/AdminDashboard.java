package com.example.myapp.gui;

import com.example.myapp.dao.BillDao;
import com.example.myapp.dao.MessageDao;
import com.example.myapp.dao.UserDao;
import com.example.myapp.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {

    private JTable billsTable;
    private DefaultTableModel tableModel;
    private JTable residentsTable;
    private DefaultTableModel residentsModel;
    private BillDao billDao = new BillDao();
    private MessageDao messageDao = new MessageDao();
    private UserDao userDao = new UserDao();

    public AdminDashboard() {
        JButton residentDetailsBtn = new JButton("Resident Details");
        UIUtils.styleButton(residentDetailsBtn);
        residentDetailsBtn.addActionListener(e -> {
            ResidentDetailsFrame detailsFrame = new ResidentDetailsFrame(this);
            detailsFrame.setVisible(true);
            this.setVisible(false);
        });
        setTitle("Admin Dashboard");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Month navigation controls
        JPanel topPanel = new JPanel();
        JComboBox<String> monthCombo = new JComboBox<>(new String[]{
            "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
        });
        int currentMonth = java.time.LocalDate.now().getMonthValue();
        monthCombo.setSelectedIndex(currentMonth - 1);
        JTextField yearField = new JTextField(String.valueOf(java.time.LocalDate.now().getYear()), 5);
        JButton goBtn = new JButton("Go");
        topPanel.add(new JLabel("Month:"));
        topPanel.add(monthCombo);
        topPanel.add(new JLabel("Year:"));
        topPanel.add(yearField);
        topPanel.add(goBtn);

        // Bills Table only
        String[] billColumns = {"Resident Name", "Flat", "Month", "Year", "Amount", "Status", "Approval Status", "UTR", "Bill ID"};
        tableModel = new DefaultTableModel(billColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        billsTable = new JTable(tableModel);
        // Hide the Bill ID column
        billsTable.getColumnModel().getColumn(8).setMinWidth(0);
        billsTable.getColumnModel().getColumn(8).setMaxWidth(0);
        billsTable.getColumnModel().getColumn(8).setWidth(0);
        loadBillsForMonth(currentMonth, Integer.parseInt(yearField.getText()));
        UIUtils.applyStripedTable(billsTable);
        UIUtils.installStatusRenderer(billsTable, 6);

        // Buttons
        JButton createBillBtn = new JButton("Create New Bill", UIUtils.createIcon(new Color(66,133,244), 12));
        createBillBtn.addActionListener(e -> {
            int selectedMonth = monthCombo.getSelectedIndex() + 1;
            int selectedYear = Integer.parseInt(yearField.getText());
            createNewBillForMonth(selectedMonth, selectedYear);
        });
        UIUtils.styleButton(createBillBtn);

        JButton approvePaymentBtn = new JButton("Approve Payment", UIUtils.createIcon(new Color(67,160,71), 12));
        approvePaymentBtn.addActionListener(e -> approvePayment());
        UIUtils.styleButton(approvePaymentBtn);

        JButton sendReminderBtn = new JButton("Send Reminder", UIUtils.createIcon(new Color(251,140,0), 12));
        sendReminderBtn.addActionListener(e -> sendReminder());
        UIUtils.styleButton(sendReminderBtn);

        JButton removeResidentBtn = new JButton("Remove Resident", UIUtils.createIcon(new Color(220,53,69), 12));
        removeResidentBtn.addActionListener(e -> removeResident());
        UIUtils.styleButton(removeResidentBtn);

        JButton rejectPaymentBtn = new JButton("Reject Payment", UIUtils.createIcon(new Color(255,0,0), 12));
        rejectPaymentBtn.addActionListener(e -> rejectPayment());
        UIUtils.styleButton(rejectPaymentBtn);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(createBillBtn);
        buttonPanel.add(approvePaymentBtn);
        buttonPanel.add(rejectPaymentBtn);
        buttonPanel.add(sendReminderBtn);
        buttonPanel.add(residentDetailsBtn);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(billsTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        goBtn.addActionListener(e -> {
            int selectedMonth = monthCombo.getSelectedIndex() + 1;
            int selectedYear = Integer.parseInt(yearField.getText());
            loadBillsForMonth(selectedMonth, selectedYear);
        });
    }

    private void loadBillsForMonth(int month, int year) {
        tableModel.setRowCount(0); // Clear existing data
        List<Object[]> bills = billDao.getBillsForAdminDashboard();
        for (Object[] bill : bills) {
            // bill contains [name, flat_number, month, year, amount, status, approval_status, utr, id]
            if ((int)bill[2] == month && (int)bill[3] == year) {
                Object[] row = new Object[9];
                row[0] = bill[0]; // name
                row[1] = bill[1]; // flat_number
                row[2] = bill[2]; // month
                row[3] = bill[3]; // year
                row[4] = bill[4]; // amount
                row[5] = bill[5]; // status
                row[6] = bill[6]; // approval_status
                row[7] = bill[7]; // UTR
                row[8] = bill[8]; // bill id
                tableModel.addRow(row);
            }
        }
    }

    private void rejectPayment() {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill to reject.", AppConstants.TITLE_WARNING, JOptionPane.WARNING_MESSAGE);
            return;
        }
        String status = (String) tableModel.getValueAt(selectedRow, 5);
        if (!"PENDING".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Only bills with 'PENDING' status can be rejected.", AppConstants.TITLE_WARNING, JOptionPane.WARNING_MESSAGE);
            return;
        }
            int billId = (int) tableModel.getValueAt(selectedRow, 8); // Get Bill ID from the hidden column
        billDao.updateBillStatus(billId, "UNPAID", "REJECTED", null); // Clear UTR, set approval status
    Component compMonth = ((JPanel) getContentPane().getComponent(0)).getComponent(1);
    int selectedMonth = compMonth instanceof JComboBox ? ((JComboBox<?>) compMonth).getSelectedIndex() + 1 : 1;
    Component compYear = ((JPanel) getContentPane().getComponent(0)).getComponent(3);
    int selectedYear = compYear instanceof JTextField ? Integer.parseInt(((JTextField) compYear).getText()) : java.time.Year.now().getValue();
    loadBillsForMonth(selectedMonth, selectedYear);
    // Mark row as red for rejection
    UIUtils.flashRow(billsTable, selectedRow, new Color(255, 204, 204), 3, 180);
    JOptionPane.showMessageDialog(this, "Payment rejected. Bill status set to UNPAID and approval status to REJECTED.");
    }


    private void removeResident() {
        // No-op: resident removal now in ResidentDetailsFrame
    }

    private void createNewBillForMonth(int month, int year) {
        String amountStr = JOptionPane.showInputDialog(this, "Enter maintenance amount for the selected month:");
        if (amountStr == null || amountStr.trim().isEmpty()) return;

        try {
            double amount = Double.parseDouble(amountStr);
            List<User> residents = userDao.getAllResidents();
            for (User resident : residents) {
                billDao.createBill(resident.getId(), amount, month, year);
            }
            // Always reload for the selected month after creation
            loadBillsForMonth(month, year);
            JOptionPane.showMessageDialog(this, "Bills created successfully for all residents for selected month.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void approvePayment() {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill to approve.", AppConstants.TITLE_WARNING, JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = (String) tableModel.getValueAt(selectedRow, 5);
        if (!"PENDING".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Only bills with 'PENDING' status can be approved.", AppConstants.TITLE_WARNING, JOptionPane.WARNING_MESSAGE);
            return;
        }

            int billId = (int) tableModel.getValueAt(selectedRow, 8); // Get Bill ID from the hidden column
        // Bill ID is now reliably fetched from the hidden column; no need for extra mapping or try-catch
        String utr = (String) tableModel.getValueAt(selectedRow, 7); // UTR column
        billDao.updateBillStatus(billId, "PAID", "APPROVED", utr);
    Component compMonth = ((JPanel) getContentPane().getComponent(0)).getComponent(1);
    int selectedMonth = compMonth instanceof JComboBox ? ((JComboBox<?>) compMonth).getSelectedIndex() + 1 : 1;
    Component compYear = ((JPanel) getContentPane().getComponent(0)).getComponent(3);
    int selectedYear = compYear instanceof JTextField ? Integer.parseInt(((JTextField) compYear).getText()) : java.time.Year.now().getValue();
    loadBillsForMonth(selectedMonth, selectedYear);
        // Mark row as green for approval
        UIUtils.flashRow(billsTable, selectedRow, new Color(198, 239, 206), 3, 180);
        JOptionPane.showMessageDialog(this, "Payment approved. Bill status set to PAID and approval status to APPROVED.");
    }
    
    private void sendReminder() {
        // Get selected month and year from UI
        JPanel topPanel = (JPanel) getContentPane().getComponent(0);
    Component compMonth = topPanel.getComponent(1);
    JComboBox<?> monthCombo = compMonth instanceof JComboBox ? (JComboBox<?>) compMonth : null;
        JTextField yearField = (JTextField) topPanel.getComponent(3);
        int selectedMonth = monthCombo.getSelectedIndex() + 1;
        int selectedYear = Integer.parseInt(yearField.getText());

        // Get all bills for the selected month and year
        List<Object[]> bills = billDao.getBillsForAdminDashboard();
        List<Integer> remindedUserIds = new java.util.ArrayList<>();
        List<User> allResidents = new UserDao().getAllResidents();
        for (Object[] bill : bills) {
            // bill: [name, flat_number, month, year, amount, status, approval_status, utr, id]
            if ((int)bill[2] == selectedMonth && (int)bill[3] == selectedYear && "UNPAID".equalsIgnoreCase((String)bill[5])) {
                String residentName = (String) bill[0];
                for (User u : allResidents) {
                    if (u.getName().equals(residentName)) {
                        if (!remindedUserIds.contains(u.getId())) {
                            String message = "Dear " + residentName + ", this is a friendly reminder that your maintenance bill is due. Please pay it at your earliest convenience.";
                            messageDao.createMessage(u.getId(), message);
                            remindedUserIds.add(u.getId());
                        }
                        break;
                    }
                }
            }
        }
    if (!remindedUserIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Reminder messages sent to all residents with unpaid bills for selected month!", "Reminders Sent", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No unpaid bills found for the selected month.", "No Reminders Sent", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
