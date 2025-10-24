package com.example.myapp.gui;

import com.example.myapp.dao.MessageDao;
import com.example.myapp.model.Message;
import com.example.myapp.model.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MessagesDialog extends JDialog {

    public MessagesDialog(Frame owner, int residentId) {
        super(owner, "Your Reminders", true);
        setSize(500, 400);
        setLocationRelativeTo(owner);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> messageList = new JList<>(listModel);
        messageList.setCellRenderer(new WordWrapCellRenderer());

        MessageDao messageDao = new MessageDao();
        // Mark all as read first (so unread count drops when dialog is shown)
        messageDao.markAllAsReadForUser(residentId);

        List<Message> messages = messageDao.getMessagesForUser(residentId);

        if (messages.isEmpty()) {
            listModel.addElement("You have no new messages.");
        } else {
            for (Message msg : messages) {
                listModel.addElement("<html><p style='width: 300px;'>" +
                        "<b>On:</b> " + msg.toHumanTimestamp() + "<br>" +
                        "<b>Message:</b> " + msg.getMessage() +
                        "</p></html>");
            }
        }

        add(new JScrollPane(messageList));
    }
}

// A custom cell renderer to handle word wrapping in JList
class WordWrapCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setVerticalAlignment(SwingConstants.TOP);
        return label;
    }
}
