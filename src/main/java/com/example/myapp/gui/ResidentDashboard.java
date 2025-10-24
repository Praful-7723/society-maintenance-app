package com.example.myapp.gui;

import com.example.myapp.dao.BillDao;
import com.example.myapp.model.Bill;
import com.example.myapp.dao.MessageDao;
import com.example.myapp.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ResidentDashboard extends JFrame {

    private User resident;
    private JTable billsTable;
    private DefaultTableModel tableModel;
    private BillDao billDao = new BillDao();
    private MessageDao messageDao = new MessageDao();

    public ResidentDashboard(User resident) {
        this.resident = resident;
        setTitle("Resident Dashboard - " + resident.getName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Table
        String[] columnNames = {"Month", "Year", "Amount", "Status", "UTR"};
        tableModel = new DefaultTableModel(columnNames, 0);
        billsTable = new JTable(tableModel);
        loadBills();
    UIUtils.applyStripedTable(billsTable);
    // status column index is 3 according to columnNames
    UIUtils.installStatusRenderer(billsTable, 3);

        // Buttons
    JButton payBillBtn = new JButton("Pay Selected Bill", UIUtils.createIcon(new Color(66,133,244), 12));
    UIUtils.styleButton(payBillBtn);
    payBillBtn.addActionListener(e -> payBill());

        JButton viewRemindersBtn = new JButton("View Reminders");
        int unread = messageDao.countUnreadForUser(resident.getId());
        if (unread > 0) viewRemindersBtn.setText("View Reminders (" + unread + ")");
        viewRemindersBtn.addActionListener(e -> {
            viewReminders();
            // refresh unread count label after dialog closes
            int newUnread = messageDao.countUnreadForUser(resident.getId());
            if (newUnread > 0) viewRemindersBtn.setText("View Reminders (" + newUnread + ")");
            else viewRemindersBtn.setText("View Reminders");
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(payBillBtn);
        buttonPanel.add(viewRemindersBtn);

        add(new JScrollPane(billsTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadBills() {
        tableModel.setRowCount(0); // Clear
        List<Bill> bills = billDao.getBillsForUser(resident.getId());
        for (Bill bill : bills) {
            tableModel.addRow(new Object[]{
                bill.getMonth(),
                bill.getYear(),
                bill.getAmount(),
                bill.getStatus(),
                bill.getUtr()
            });
        }
    }

    private void payBill() {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill to pay.", AppConstants.TITLE_WARNING, JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = (String) tableModel.getValueAt(selectedRow, 3);
        if (!"UNPAID".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "This bill is not eligible for payment.", AppConstants.TITLE_WARNING, JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Show QR Code and get UTR
        PaymentDialog paymentDialog = new PaymentDialog(this);
        paymentDialog.setVisible(true);
        String utr = paymentDialog.getUtr();

        if (utr != null && !utr.trim().isEmpty()) {
            Bill selectedBill = billDao.getBillsForUser(resident.getId()).get(selectedRow);
            billDao.updateBillStatus(selectedBill.getId(), "PENDING", "PENDING", utr);
            loadBills(); // Refresh
            // flash row to show status changed
            UIUtils.flashRow(billsTable, selectedRow, new Color(255, 243, 205), 3, 180);
            JOptionPane.showMessageDialog(this, "Payment submitted for approval with UTR: " + utr);
        }
    }

    private void viewReminders() {
        new MessagesDialog(this, resident.getId()).setVisible(true);
        // After dialog closes, refresh bills and UI
        loadBills();
    }
}
