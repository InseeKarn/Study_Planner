package view.task;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.text.*;
import model.Task;
import service.Data;

public class AddTaskDialog {

    public static Task showDialog(Component parent, Data dataRepository) {
        return showDialog(parent, dataRepository, null);
    }

    public static Task showEditDialog(Component parent, Data dataRepository, Task task) {
        return showDialog(parent, dataRepository, task);
    }

    private static Task showDialog(Component parent, Data dataRepository, Task editTask) {

        JComboBox<String> cbSubject = new JComboBox<>();
        cbSubject.setEditable(true);

        // Get existing subjects
        List<String> existingSubjects = new ArrayList<>();

        for (Task t : dataRepository.getAllTasks()) {
            String sub = t.getSubject();

            if (sub != null
                    && !sub.trim().isEmpty()
                    && !existingSubjects.contains(sub.trim())) {

                existingSubjects.add(sub.trim());
            }
        }

        for (String sub : existingSubjects) {
            cbSubject.addItem(sub);
        }

        cbSubject.setSelectedItem(editTask == null ? "" : editTask.getSubject());

        // Work
        JTextField txtTitle = new JTextField(editTask == null ? "" : editTask.getTitle());

        // Date
        JTextField txtDay = new JTextField(2);
        JTextField txtMonth = new JTextField(2);
        JTextField txtYear = new JTextField(4);
        
        onlyNumbers(txtDay);
        onlyNumbers(txtMonth);
        onlyNumbers(txtYear);
        if (editTask != null && editTask.getDueDate() != null && !editTask.getDueDate().equals("Not specified")) {
            String[] dateParts = editTask.getDueDate().split("-");
            if (dateParts.length == 3) {
                txtDay.setText(dateParts[0]);
                txtMonth.setText(dateParts[1]);
                txtYear.setText(dateParts[2]);
            }
        }

        txtDay.setHorizontalAlignment(JTextField.CENTER);
        txtMonth.setHorizontalAlignment(JTextField.CENTER);
        txtYear.setHorizontalAlignment(JTextField.CENTER);

        JPanel datePanel = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 3, 0)
        );

        datePanel.setOpaque(false);

        datePanel.add(txtDay);
        datePanel.add(new JLabel("-"));
        datePanel.add(txtMonth);
        datePanel.add(new JLabel("-"));
        datePanel.add(txtYear);

        // Description
        JTextField txtDesc = new JTextField(editTask == null || "-".equals(editTask.getDescription()) ? "" : editTask.getDescription());

        // Auto move
        autoNextField(txtDay, txtMonth, 2);
        autoNextField(txtMonth, txtYear, 2);
        autoNextField(txtYear, txtDesc, 4);

        Object[] message = {
            "Subject:", cbSubject,
            "Work:", txtTitle,
            "Due date (DD-MM-YYYY):", datePanel,
            "Description:", txtDesc
        };

        int option = JOptionPane.showConfirmDialog(
            parent,
            message,
            editTask == null ? "Add new work" : "Edit assignment",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (option != JOptionPane.OK_OPTION) {
            return null;
        }

        // Get values
        Object selectedObj = cbSubject.getSelectedItem();

        String sub = selectedObj != null
                ? selectedObj.toString().trim()
                : "";

        String title = txtTitle.getText().trim();

        String due = "";

        if (!txtDay.getText().trim().isEmpty()
                || !txtMonth.getText().trim().isEmpty()
                || !txtYear.getText().trim().isEmpty()) {

        	due = txtDay.getText().trim()
        	        + "-" + txtMonth.getText().trim()
        	        + "-" + txtYear.getText().trim();
        }

        String desc = txtDesc.getText().trim();

        // Validate required fields
        if (sub.isEmpty() || title.isEmpty()) {

            JOptionPane.showMessageDialog(
                parent,
                "Please enter the subject and assignment name.",
                "Notification",
                JOptionPane.WARNING_MESSAGE
            );

            return null;
        }

        // Validate date
        if (!due.isEmpty() && Task.parseDueDate(due) == null) {

            JOptionPane.showMessageDialog(
                parent,
                "Invalid date! Please enter a valid date.",
                "Invalid Date",
                JOptionPane.WARNING_MESSAGE
            );

            return null;
        }

        return new Task(
            sub,
            title,
            due.isEmpty() ? "Not specified" : due,
            editTask == null ? "In Progress" : editTask.getStatus(),
            desc.isEmpty() ? "-" : desc
        );
    }

    private static void autoNextField(
            JTextField current,
            JTextField next,
            int maxLength) {

        current.getDocument().addDocumentListener(
            new javax.swing.event.DocumentListener() {

                private void checkLength() {
                    if (current.getText().length() >= maxLength) {
                        next.requestFocusInWindow();
                    }
                }

                @Override
                public void insertUpdate(
                        javax.swing.event.DocumentEvent e) {
                    checkLength();
                }

                @Override
                public void removeUpdate(
                        javax.swing.event.DocumentEvent e) {
                }

                @Override
                public void changedUpdate(
                        javax.swing.event.DocumentEvent e) {
                }
            }
        );

        current.addActionListener(e ->
            next.requestFocusInWindow()
        );
    }
    
    private static void onlyNumbers(JTextField textField) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(
            new DocumentFilter() {

                @Override
                public void insertString(
                        FilterBypass fb,
                        int offset,
                        String string,
                        AttributeSet attr)
                        throws BadLocationException {

                    if (string != null && string.matches("\\d+")) {
                        fb.insertString(offset, string, attr);
                    }
                }

                @Override
                public void replace(
                        FilterBypass fb,
                        int offset,
                        int length,
                        String text,
                        AttributeSet attrs)
                        throws BadLocationException {

                    if (text != null && text.matches("\\d*")) {
                        fb.replace(offset, length, text, attrs);
                    }
                }
            }
        );
    }
}