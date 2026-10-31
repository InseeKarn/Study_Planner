
package view.task;

import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.*;

import model.Task;
import service.Data;

public class AddTaskDialog {

    // Theme Colors
    private static final Color BG = new Color(17, 17, 17);
    private static final Color CARD = new Color(24, 24, 24);
    private static final Color FIELD = new Color(32, 32, 32);
    private static final Color BORDER = new Color(60, 60, 60);

    private static final Color TEXT = new Color(242, 242, 242);
    private static final Color MUTED = new Color(153, 153, 153);

    private static final Color GREEN = new Color(143, 214, 148);
    private static final Color RED = new Color(230, 100, 100);

    public static Task showDialog(Component parent, Data dataRepository) {
        return showDialog(parent, dataRepository, null);
    }

    public static Task showEditDialog(
            Component parent,
            Data dataRepository,
            Task task) {

        return showDialog(parent, dataRepository, task);
    }

    private static Task showDialog(
            Component parent,
            Data dataRepository,
            Task editTask) {

        // Dialog
        String windowTitle = editTask == null
                ? "Add New Work"
                : "Edit Assignment";

        Window owner = SwingUtilities.getWindowAncestor(parent);

        JDialog dialog = new JDialog(
                owner,
                windowTitle,
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);

        // Main panel
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.setBorder(new LineBorder(BORDER, 1));

        // =========================
        // HEADER
        // =========================

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(CARD);
        header.setBorder(new EmptyBorder(22, 25, 18, 25));

        JLabel titleLabel = new JLabel(windowTitle);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 21));
        titleLabel.setForeground(TEXT);

        JLabel subtitleLabel = new JLabel(
                editTask == null
                        ? "Create a new assignment and keep track of your work."
                        : "Update your assignment details."
        );

        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitleLabel.setForeground(MUTED);

        header.add(titleLabel);
        header.add(Box.createVerticalStrut(7));
        header.add(subtitleLabel);

        root.add(header, BorderLayout.NORTH);

        // =========================
        // INPUT FIELDS
        // =========================

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(BG);
        form.setBorder(new EmptyBorder(22, 25, 15, 25));

        // Subject
        JComboBox<String> cbSubject = new JComboBox<>();
        cbSubject.setEditable(true);

        List<String> existingSubjects = new ArrayList<>();

        for (Task task : dataRepository.getAllTasks()) {

            String subject = task.getSubject();

            if (subject != null
                    && !subject.trim().isEmpty()
                    && !existingSubjects.contains(subject.trim())) {

                existingSubjects.add(subject.trim());
            }
        }

        for (String subject : existingSubjects) {
            cbSubject.addItem(subject);
        }

        cbSubject.setSelectedItem(
                editTask == null ? "" : editTask.getSubject()
        );

        styleComboBox(cbSubject);

        Component editor = cbSubject.getEditor().getEditorComponent();

        if (editor instanceof JTextField) {
            styleTextField((JTextField) editor);
        }

        addFormField(form, "SUBJECT", cbSubject);

        // Assignment Name
        JTextField txtTitle = new JTextField(
                editTask == null ? "" : editTask.getTitle()
        );

        styleTextField(txtTitle);

        addFormField(form, "ASSIGNMENT NAME", txtTitle);

        // =========================
        // DATE INPUT
        // =========================

        JTextField txtDay = new JTextField(2);
        JTextField txtMonth = new JTextField(2);
        JTextField txtYear = new JTextField(4);

        onlyNumbers(txtDay);
        onlyNumbers(txtMonth);
        onlyNumbers(txtYear);

        if (editTask != null
                && editTask.getDueDate() != null
                && !editTask.getDueDate().equals("Not specified")) {

            String[] dateParts = editTask.getDueDate().split("-");

            if (dateParts.length == 3) {
                txtDay.setText(dateParts[0]);
                txtMonth.setText(dateParts[1]);
                txtYear.setText(dateParts[2]);
            }
        }

        styleDateField(txtDay, 55);
        styleDateField(txtMonth, 55);
        styleDateField(txtYear, 85);

        JPanel datePanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 0)
        );

        datePanel.setBackground(BG);

        datePanel.add(txtDay);
        datePanel.add(createDateSeparator());
        datePanel.add(txtMonth);
        datePanel.add(createDateSeparator());
        datePanel.add(txtYear);

        datePanel.setPreferredSize(new Dimension(440, 40));
        datePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        datePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        addFormField(form, "DUE DATE", datePanel);

        // Auto move between date fields
        autoNextField(txtDay, txtMonth, 2);
        autoNextField(txtMonth, txtYear, 2);

        txtYear.addActionListener(e ->
                txtDescFocus(form)
        );

        // =========================
        // DESCRIPTION
        // =========================

        JTextArea txtDesc = new JTextArea(
                editTask == null
                        || "-".equals(editTask.getDescription())
                        ? ""
                        : editTask.getDescription()
        );

        txtDesc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtDesc.setForeground(TEXT);
        txtDesc.setBackground(FIELD);
        txtDesc.setCaretColor(TEXT);
        txtDesc.setSelectionColor(new Color(65, 105, 160));
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setBorder(new EmptyBorder(10, 12, 10, 12));

        JScrollPane descScroll = new JScrollPane(txtDesc);
        descScroll.setPreferredSize(new Dimension(440, 85));
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));
        descScroll.setBorder(new LineBorder(BORDER, 1));
        descScroll.getViewport().setBackground(FIELD);

        addFormField(form, "DESCRIPTION (OPTIONAL)", descScroll);

        root.add(form, BorderLayout.CENTER);

        // =========================
        // FOOTER
        // =========================

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        errorLabel.setForeground(RED);

        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton(
                editTask == null ? "Add Assignment" : "Save Changes"
        );

        styleButton(
                cancelButton,
                FIELD,
                TEXT,
                BORDER
        );

        styleButton(
                saveButton,
                GREEN,
                BG,
                GREEN
        );

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );

        buttons.setBackground(BG);
        buttons.add(cancelButton);
        buttons.add(saveButton);

        JPanel footer = new JPanel(new BorderLayout(0, 12));
        footer.setBackground(BG);
        footer.setBorder(new EmptyBorder(0, 25, 22, 25));

        footer.add(errorLabel, BorderLayout.NORTH);
        footer.add(buttons, BorderLayout.SOUTH);

        root.add(footer, BorderLayout.SOUTH);

        // =========================
        // SAVE ACTION
        // =========================

        Task[] result = new Task[1];

        saveButton.addActionListener(e -> {

            Object selected = cbSubject.getSelectedItem();

            String subject = selected == null
                    ? ""
                    : selected.toString().trim();

            String title = txtTitle.getText().trim();

            String day = txtDay.getText().trim();
            String month = txtMonth.getText().trim();
            String year = txtYear.getText().trim();

            String due = "";
            String description = txtDesc.getText().trim();

            if (subject.isEmpty() || title.isEmpty()) {
                errorLabel.setText(
                        "Please enter the subject and assignment name."
                );
                return;
            }

            boolean hasDate = !day.isEmpty()
                    || !month.isEmpty()
                    || !year.isEmpty();

            if (hasDate) {

                if (day.isEmpty()
                        || month.isEmpty()
                        || year.isEmpty()) {

                    errorLabel.setText(
                            "Please complete all date fields."
                    );
                    return;
                }

                due = day + "-" + month + "-" + year;

                if (Task.parseDueDate(due) == null) {
                    errorLabel.setText(
                            "Invalid date. Please enter a valid date."
                    );
                    return;
                }
            }

            Task newTask = new Task(
                    subject,
                    title,
                    due.isEmpty() ? "Not specified" : due,
                    editTask == null
                            ? "In Progress"
                            : editTask.getStatus(),
                    description.isEmpty() ? "-" : description
            );

            if (editTask != null) {
                newTask.setImportant(editTask.isImportant());
            }

            result[0] = newTask;
            dialog.dispose();
        });

        dialog.setContentPane(root);
        dialog.getRootPane().setDefaultButton(saveButton);

        dialog.setSize(520, 620);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);

        return result[0];
    }

    // =========================
    // HELPER METHODS
    // =========================

    private static void addFormField(
            JPanel form,
            String labelText,
            JComponent component) {

        JLabel label = new JLabel(labelText);

        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        component.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(label);
        form.add(Box.createVerticalStrut(7));
        form.add(component);
        form.add(Box.createVerticalStrut(17));
    }

    private static void styleTextField(JTextField field) {

        field.setFont(new Font("SansSerif", Font.PLAIN, 12));
        field.setForeground(TEXT);
        field.setBackground(FIELD);
        field.setCaretColor(TEXT);
        field.setSelectionColor(new Color(65, 105, 160));

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        new LineBorder(BORDER, 1),
                        new EmptyBorder(8, 11, 8, 11)
                )
        );

        field.setPreferredSize(new Dimension(440, 38));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
    }

    private static void styleDateField(
            JTextField field,
            int width) {

        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setForeground(TEXT);
        field.setBackground(FIELD);
        field.setCaretColor(TEXT);
        field.setHorizontalAlignment(JTextField.CENTER);

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        new LineBorder(BORDER, 1),
                        new EmptyBorder(5, 5, 5, 5)
                )
        );

        field.setPreferredSize(new Dimension(width, 38));
    }

    private static void styleComboBox(JComboBox<String> combo) {

        combo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        combo.setForeground(TEXT);
        combo.setBackground(FIELD);
        combo.setBorder(new LineBorder(BORDER, 1));
        combo.setPreferredSize(new Dimension(440, 38));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        combo.setMaximumRowCount(6);

        combo.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean selected,
                    boolean focus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, selected, focus
                );

                label.setBackground(
                        selected ? new Color(55, 75, 105) : FIELD
                );

                label.setForeground(TEXT);
                label.setBorder(new EmptyBorder(7, 10, 7, 10));

                return label;
            }
        });
    }

    private static void styleButton(
            JButton button,
            Color background,
            Color foreground,
            Color border) {

        button.setFont(new Font("SansSerif", Font.BOLD, 11));
        button.setForeground(foreground);
        button.setBackground(background);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(125, 38));

        button.setBorder(
                new LineBorder(border, 1, true)
        );
    }

    private static JLabel createDateSeparator() {

        JLabel separator = new JLabel("-");

        separator.setFont(new Font("SansSerif", Font.BOLD, 15));
        separator.setForeground(MUTED);

        return separator;
    }

    private static void txtDescFocus(JPanel form) {
        // Focus is moved through the normal Tab navigation.
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
