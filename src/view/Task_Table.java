package view;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

import model.Task;

public class Task_Table extends JPanel {
	
	//Colors
	Color textPrimary = new Color(242, 242, 242);
	Color textSecondary = new Color(153, 153, 153);
	Color elementDark = new Color(32, 32, 32);

    public interface TaskTableListener {
        void onSubmitClicked(Task task);
        void onDeleteClicked(Task task);
    }

    private final DefaultTableModel tableModel;
    private final JTable taskTable;

    private TaskTableListener listener;

    public Task_Table(Color cardBg) {
        setLayout(new BorderLayout());
        setOpaque(false);

        String[] columnNames = {
                "Subject",
                "Assignment",
                "Due Date",
                "Status",
                "Submit",
                "Delete"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 5;
            }
        };

        taskTable = new JTable(tableModel);
        taskTable.setForeground(textPrimary);
        taskTable.setBackground(cardBg);
        taskTable.setRowHeight(38);
        taskTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        taskTable.setBackground(cardBg);
        taskTable.setShowGrid(false);

        taskTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );

        taskTable.getTableHeader().setBackground(cardBg);
        taskTable.getTableHeader().setForeground(textPrimary);

        DefaultTableCellRenderer centerRenderer =
                new DefaultTableCellRenderer();

        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setForeground(textPrimary);
        centerRenderer.setBackground(cardBg);

        for (int i = 0; i < taskTable.getColumnCount(); i++) {
        	taskTable.getColumnModel()
            .getColumn(0)
            .setCellRenderer(new DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(
                        JTable table,
                        Object value,
                        boolean isSelected,
                        boolean hasFocus,
                        int row,
                        int column
                ) {

                    if (value instanceof Task) {
                        value = ((Task) value).getSubject();
                    }

                    setHorizontalAlignment(JLabel.CENTER);
                    setForeground(textPrimary);
                    setBackground(cardBg);

                    return super.getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column
                    );
                }
            });
            
        }

        taskTable.getColumnModel()
                .getColumn(4)
                .setCellRenderer(new ButtonRenderer());

        taskTable.getColumnModel()
                .getColumn(4)
                .setCellEditor(new ButtonEditor(
                        new JCheckBox(),
                        false
                ));

        taskTable.getColumnModel()
                .getColumn(5)
                .setCellRenderer(new ButtonRenderer());

        taskTable.getColumnModel()
                .getColumn(5)
                .setCellEditor(new ButtonEditor(
                        new JCheckBox(),
                        true
                ));

        JScrollPane scrollPane = new JScrollPane(taskTable);

        scrollPane.getViewport().setBackground(cardBg);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        new Color(230, 220, 210), 1
                )
        );

        add(scrollPane, BorderLayout.CENTER);
    }

    public void setTaskTableListener(TaskTableListener listener) {
        this.listener = listener;
    }

    public void updateTable(List<Task> tasks) {
        tableModel.setRowCount(0);

        for (Task task : tasks) {

            String submitText = task.isCompleted()
                    ? "✓ Submit (Edit)"
                    : "Submit";

            tableModel.addRow(new Object[] {
                    task,
                    task.getTitle(),
                    task.getDueDate(),
                    task.getStatus(),
                    submitText,
                    "Delete"
            });
        }

        taskTable.getColumnModel()
                .getColumn(0)
                .setCellRenderer(new DefaultTableCellRenderer() {

                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table,
                            Object value,
                            boolean isSelected,
                            boolean hasFocus,
                            int row,
                            int column
                    ) {

                        if (value instanceof Task) {
                            value = ((Task) value).getSubject();
                        }

                        setHorizontalAlignment(JLabel.CENTER);

                        return super.getTableCellRendererComponent(
                                table,
                                value,
                                isSelected,
                                hasFocus,
                                row,
                                column
                        );
                    }
                });

        taskTable.getColumnModel()
                .getColumn(1)
                .setCellRenderer(centerRenderer());

        taskTable.getColumnModel()
                .getColumn(2)
                .setCellRenderer(centerRenderer());

        taskTable.getColumnModel()
                .getColumn(3)
                .setCellRenderer(centerRenderer());

        taskTable.revalidate();
        taskTable.repaint();
    }

    private DefaultTableCellRenderer centerRenderer() {
        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer();

        renderer.setHorizontalAlignment(JLabel.CENTER);

        return renderer;
    }

    private class ButtonRenderer extends JButton
            implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
            setFocusPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            setText(value == null ? "" : value.toString());

            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {

        private final JButton button;
        private final boolean deleteButton;
        private int row;

        public ButtonEditor(
                JCheckBox checkBox,
                boolean deleteButton
        ) {
            super(checkBox);

            this.deleteButton = deleteButton;

            button = new JButton();
            button.setFocusPainted(false);

            button.addActionListener(e -> {
                fireEditingStopped();

                if (listener == null) {
                    return;
                }

                if (row < 0 || row >= tableModel.getRowCount()) {
                    return;
                }

                Task task = (Task) tableModel.getValueAt(row, 0);

                if (deleteButton) {
                    listener.onDeleteClicked(task);
                } else {
                    listener.onSubmitClicked(task);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column
        ) {
            this.row = row;

            button.setText(
                    value == null ? "" : value.toString()
            );

            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }
    }
}