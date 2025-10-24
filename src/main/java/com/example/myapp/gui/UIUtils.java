package com.example.myapp.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public final class UIUtils {

    private UIUtils() {}

    public static void styleButton(JButton b) {
        b.setFocusPainted(false);
        b.setBackground(new Color(66,133,244));
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(6,12,6,12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void applyStripedTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setRowHeight(24);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0,0));
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250,250,250));
                return c;
            }
        });
    }

    public static void installStatusRenderer(JTable table, int statusColumnIndex) {
        table.getColumnModel().getColumn(statusColumnIndex).setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {
            JLabel label = new JLabel();
            String s = value == null ? "" : value.toString();
            String u = s.toUpperCase();
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBorder(BorderFactory.createEmptyBorder(4,8,4,8));

            switch (u) {
                case "PAID":
                    label.setText("PAID");
                    label.setBackground(new Color(198, 239, 206));
                    label.setForeground(new Color(0, 97, 0));
                    break;
                case "UNPAID":
                    label.setText("UNPAID");
                    label.setBackground(new Color(255, 235, 238));
                    label.setForeground(new Color(153, 0, 0));
                    break;
                case "PENDING":
                    label.setText("PENDING");
                    label.setBackground(new Color(255, 243, 205));
                    label.setForeground(new Color(102, 60, 0));
                    break;
                case "APPROVED":
                    label.setText("APPROVED");
                    label.setBackground(new Color(198, 239, 206));
                    label.setForeground(new Color(0, 97, 0));
                    break;
                case "REJECTED":
                    label.setText("REJECTED");
                    label.setBackground(new Color(255, 235, 238));
                    label.setForeground(new Color(153, 0, 0));
                    break;
                default:
                    label.setText(u);
                    label.setBackground(new Color(240, 240, 240));
                    label.setForeground(Color.DARK_GRAY);
                    break;
            }
            if (isSelected) label.setBorder(BorderFactory.createLineBorder(new Color(100,150,250)));
            return label;
        });
    }

    public static ImageIcon createIcon(Color color, int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.fillOval(1,1,size-2,size-2);
        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Flash a table row briefly to draw attention (simple animation using selection toggle).
     */
    public static void flashRow(final JTable table, final int row, final Color highlight, int flashes, int delayMs) {
        if (row < 0 || row >= table.getRowCount()) return;
        final Color origSelection = table.getSelectionBackground();
        final javax.swing.Timer timer = new javax.swing.Timer(delayMs, null);
        final int[] counter = {0};
        timer.addActionListener(e -> {
            boolean on = (counter[0] % 2 == 0);
            if (on) {
                table.setSelectionBackground(highlight);
                table.setRowSelectionInterval(row, row);
            } else {
                table.clearSelection();
                table.setSelectionBackground(origSelection);
            }
            counter[0]++;
            if (counter[0] >= flashes * 2) {
                timer.stop();
                table.setSelectionBackground(origSelection);
                table.clearSelection();
            }
        });
        timer.setInitialDelay(0);
        timer.start();
    }
}
