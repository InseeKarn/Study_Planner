package main;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Task_Table extends JPanel {

    public interface TaskTableListener {
        void onSubmitClicked(Task task);
        void onDetailsClicked(Task task);
    }

    private final DefaultTableModel tableModel;
    private final JTable taskTable;
    private TaskTableListener listener;

    public Task_Table(Color cardBg, Font boldFont, Font plainFont) {
        setLayout(new BorderLayout());
        setOpaque(false);

        String[] columnNames = {"วิชา", "ชื่องาน", "วันกำหนดส่ง", "สถานะ", "Submit", "Details"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        taskTable = new JTable(tableModel);
        taskTable.setRowHeight(38);
        taskTable.setFont(plainFont);
        taskTable.setBackground(cardBg);
        taskTable.setShowGrid(false);
        taskTable.getTableHeader().setFont(boldFont);
        taskTable.getTableHeader().setBackground(cardBg);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < taskTable.getColumnCount(); i++) {
            taskTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        taskTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = taskTable.getSelectedRow();
                int col = taskTable.getSelectedColumn();
                if (row != -1 && listener != null) {
                    Task task = (Task) tableModel.getValueAt(row, 0);
                    if (col == 4) {
                        listener.onSubmitClicked(task);
                    } else if (col == 5) {
                        listener.onDetailsClicked(task);
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
            String actionText = task.isCompleted() ? "✓ Submit (Edit)" : "Submit";
            tableModel.addRow(new Object[]{
                task,
                task.getTitle(),
                task.getDueDate(),
                task.getStatus(),
                actionText,
                "🔍 Details"
            });
        }

        taskTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Task) {
                    value = ((Task) value).getSubject();
                }
                setHorizontalAlignment(JLabel.CENTER);
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });
    }
}