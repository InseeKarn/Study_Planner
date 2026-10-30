package view.task;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;

import model.Task;

public class Task_Table extends JPanel {
    
    // Colors
    private final Color textPrimary = new Color(242, 242, 242);
    private final Color textSecondary = new Color(153, 153, 153);
    private final Color elementDark = new Color(32, 32, 32);

    public interface TaskTableListener {
        void onSubmitClicked(Task task);
        void onDeleteClicked(Task task);
        void onEditClicked(Task task);
        void onTaskSelected(Task task); // คอลแบ็กเมื่อมีการคลิกเลือกแถวในตาราง
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
                "..."
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        taskTable = new JTable(tableModel);
        taskTable.setForeground(textPrimary);
        taskTable.setBackground(cardBg);
        taskTable.setRowHeight(38);
        taskTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        taskTable.setShowGrid(false);

        taskTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        taskTable.getTableHeader().setBackground(cardBg);
        taskTable.getTableHeader().setForeground(textPrimary);

        // --- ตั้งค่า Cell Renderers ---

        // คอลัมน์ 0 (Subject)
        taskTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Task) {
                    value = ((Task) value).getSubject();
                }
                setHorizontalAlignment(JLabel.CENTER);
                setForeground(textPrimary);
                setBackground(isSelected ? new Color(45, 45, 45) : cardBg);
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });

        // คอลัมน์ 1 และ 2 (Assignment, Due Date)
        for (int i = 1; i <= 2; i++) {
            taskTable.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    setHorizontalAlignment(JLabel.CENTER);
                    setForeground(textPrimary);
                    setBackground(isSelected ? new Color(45, 45, 45) : cardBg);
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }
            });
        }

        // คอลัมน์ 3 (Status) - ใช้ StatusRenderer แสดงกล่องสี
        taskTable.getColumnModel().getColumn(3).setCellRenderer(new StatusRenderer());

        // คอลัมน์ 4: เมนู ... รวม Submit / Edit / Delete
        taskTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        taskTable.getColumnModel().getColumn(4).setCellEditor(new MenuButtonEditor(new JCheckBox()));

        // Listener สำหรับตรวจจับการเลือกแถวในตาราง
        taskTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && listener != null) {
                    int selectedRow = taskTable.getSelectedRow();
                    if (selectedRow >= 0 && selectedRow < tableModel.getRowCount()) {
                        Task selectedTask = (Task) tableModel.getValueAt(selectedRow, 0);
                        listener.onTaskSelected(selectedTask);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(taskTable);
        scrollPane.getViewport().setBackground(cardBg);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 220, 210), 1));

        add(scrollPane, BorderLayout.CENTER);
    }

    public void setTaskTableListener(TaskTableListener listener) {
        this.listener = listener;
    }

    public void updateTable(List<Task> tasks) {
        tableModel.setRowCount(0);

        for (Task task : tasks) {
            tableModel.addRow(new Object[] {
                    task,               // Column 0: Subject (ส่ง Task Object)
                    task.getTitle(),    // Column 1: Assignment
                    task.getDueDate(),  // Column 2: Due Date
                    task,               // Column 3: Status (ส่ง Task Object ให้ StatusRenderer นำไปคำนวณสี)
                    "..."               // Column 4: Actions
            });
        }
    }

    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setFocusPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            return this;
        }
    }

    private class MenuButtonEditor extends DefaultCellEditor {
        private final JButton button = new JButton("...");
        private int row;

        public MenuButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button.setFocusPainted(false);
            button.addActionListener(e -> {
                if (listener == null || row < 0 || row >= tableModel.getRowCount()) { fireEditingStopped(); return; }
                Task task = (Task) tableModel.getValueAt(row, 0);
                JPopupMenu menu = new JPopupMenu();
                JMenuItem submitItem = new JMenuItem("Submit");
                JMenuItem editItem = new JMenuItem("Edit");
                JMenuItem deleteItem = new JMenuItem("Delete");
                submitItem.addActionListener(ev -> listener.onSubmitClicked(task));
                editItem.addActionListener(ev -> listener.onEditClicked(task));
                deleteItem.addActionListener(ev -> listener.onDeleteClicked(task));
                menu.add(submitItem);
                menu.add(editItem);
                menu.add(deleteItem);
                menu.show(button, 0, button.getHeight());
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() { return "..."; }
    }
}
